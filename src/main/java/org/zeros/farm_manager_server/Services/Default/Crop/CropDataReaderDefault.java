package org.zeros.farm_manager_server.Services.Default.Crop;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Domain.DTO.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.CropSummary.CropSummary;
import org.zeros.farm_manager_server.Domain.DTO.CropSummary.ResourcesSummary;
import org.zeros.farm_manager_server.Domain.Entities.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.Data.Fertilizer;
import org.zeros.farm_manager_server.Domain.Entities.Data.Spray;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.InterCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.GrainParameters;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.RapeSeedParameters;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.SugarBeetParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropSale;
import org.zeros.farm_manager_server.Domain.Entities.Data.Subside;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropDataReader;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class CropDataReaderDefault implements CropDataReader {
    private final CropManager cropManager;



    @Override
    public CropSummary getCropSummary(UUID cropId) {
        Crop crop = getCropIfExist(cropId);
        return CropSummary.builder()
                .cropId(crop.getId())
                .area(crop.getFieldPart().getArea().floatValue())
                .yieldPerAreaUnit(getYieldPerAreaUnit(crop))
                .meanSellPrice(getMeanSellPrice(crop))
                .estimatedAmountNotSoldPerAreaUnit(getEstimatedAmountNotSoldPerAreaUnit(crop))
                .totalFuelCostPerAreaUnit(getFuelCostPerAreaUnit(crop))
                .totalSprayCostPerAreaUnit(getSprayCostPerAreaUnit(crop))
                .totalFertilizerCostPerAreaUnit(getFertilizerCostPerAreaUnit(crop))
                .totalSubsidesValuePerAreaUnit(getSubsidesPerAreaUnit(crop))
                .estimatedValue(getIsValueDetermined(crop))
                .build();
    }

    private Crop getCropIfExist(UUID cropId) {
        Crop crop = cropManager.getCropById(cropId);
        if (crop == MainCrop.NONE) {
            throw new IllegalArgumentExceptionCustom(Crop.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return crop;
    }

    private boolean getIsValueDetermined(Crop crop) {
        if (crop instanceof MainCrop) {
            return ((MainCrop) crop).getIsFullySold() && crop.getWorkFinished();
        }
        return (!((InterCrop) crop).getDateDestroyed().equals(ApplicationDefaults.UNDEFINED_DATE_MAX)) && crop.getWorkFinished();
    }

    private float getSubsidesPerAreaUnit(Crop crop) {
        BigDecimal subsideSum = BigDecimal.ZERO;
        for (Subside subside : crop.getSubsides()) {
            subsideSum = subsideSum.add(subside.getSubsideValuePerAreaUnit());
        }
        return subsideSum.setScale(2, RoundingMode.HALF_UP).floatValue();

    }

    private float getFertilizerCostPerAreaUnit(Crop crop) {
        BigDecimal fertilizerCostSum = BigDecimal.ZERO;
        for (FertilizerApplication fertilizerApplication : crop.getFertilizerApplications()) {
            fertilizerCostSum = fertilizerCostSum.add(fertilizerApplication.getQuantityPerAreaUnit().multiply(fertilizerApplication.getPricePerUnit()));
        }
        for (SprayApplication sprayApplication : crop.getSprayApplications()) {
            fertilizerCostSum = fertilizerCostSum.add(sprayApplication.getFertilizerQuantityPerAreaUnit().multiply(sprayApplication.getFertilizerPricePerUnit()));
        }
        return fertilizerCostSum.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    private float getSprayCostPerAreaUnit(Crop crop) {

        BigDecimal sprayCostSum = BigDecimal.ZERO;

        for (SprayApplication sprayApplication : crop.getSprayApplications()) {
            sprayCostSum = sprayCostSum.add(sprayApplication.getQuantityPerAreaUnit().multiply(sprayApplication.getPricePerUnit()));
        }
        return sprayCostSum.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    private float getFuelCostPerAreaUnit(Crop crop) {
        BigDecimal fuelCostSum = BigDecimal.ZERO;
        Set<AgriculturalOperation> operations = new java.util.HashSet<>();
        operations.addAll(crop.getSeeding());
        operations.addAll(crop.getCultivations());
        operations.addAll(crop.getFertilizerApplications());
        operations.addAll(crop.getSprayApplications());
        if (crop instanceof MainCrop) {
            operations.addAll(((MainCrop) crop).getHarvest());
        }
        for (AgriculturalOperation operation : operations) {
            if (!operation.getIsPlannedOperation()) {
                if (operation.getIsExternalService()) {
                    fuelCostSum = fuelCostSum.add(operation.getExternalServicePrice());
                } else {
                    fuelCostSum = fuelCostSum.add(operation.getFuelConsumptionPerUnit().multiply(operation.getFuelPrice()));
                }
            }
        }
        return fuelCostSum.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    private Map<ResourceType, BigDecimal> getEstimatedAmountNotSoldPerAreaUnit(Crop crop) {
        if (crop instanceof InterCrop) {
            return new HashMap<>();
        }
        if (((MainCrop) crop).getIsFullySold()) {
            return new HashMap<>();
        }
        Set<Harvest> harvests = ((MainCrop) crop).getHarvest();
        Set<CropSale> cropSales = ((MainCrop) crop).getCropSales();
        Map<ResourceType, BigDecimal> estimatedAmount = new HashMap<>();
        for (Harvest harvest : harvests) {
            if (estimatedAmount.containsKey(harvest.getResourceType())) {
                BigDecimal amount = estimatedAmount.get(harvest.getResourceType());
                estimatedAmount.remove(harvest.getResourceType());
                estimatedAmount.put(harvest.getResourceType(), harvest.getQuantityPerAreaUnit()
                        .add(amount));
            } else {
                estimatedAmount.put(harvest.getResourceType(), harvest.getQuantityPerAreaUnit());
            }
        }
        for (CropSale cropSale : cropSales) {
            if (estimatedAmount.containsKey(cropSale.getResourceType())) {
                BigDecimal amount = estimatedAmount.get(cropSale.getResourceType());
                estimatedAmount.remove(cropSale.getResourceType());
                estimatedAmount.put(cropSale.getResourceType(), amount.subtract(cropSale.getAmountSold().divide(crop.getFieldPart().getArea(), RoundingMode.HALF_DOWN)));
            } else {
                throw new IllegalArgumentExceptionCustom(CropSale.class, IllegalArgumentExceptionCause.INVALID_OBJECT_PRESENT);
            }
        }
        return estimatedAmount;
    }

    private Map<ResourceType, BigDecimal> getMeanSellPrice(Crop crop) {
        if (crop instanceof InterCrop) {
            return new HashMap<>();
        }
        Set<CropSale> cropSales = ((MainCrop) crop).getCropSales();
        Map<ResourceType, BigDecimal> totalIncome = new HashMap<>();
        Map<ResourceType, BigDecimal> amountSold = new HashMap<>();
        for (CropSale cropSale : cropSales) {
            if (totalIncome.containsKey(cropSale.getResourceType())) {
                BigDecimal income = totalIncome.get(cropSale.getResourceType());
                BigDecimal amount = amountSold.get(cropSale.getResourceType());
                totalIncome.remove(cropSale.getResourceType());
                amountSold.remove(cropSale.getResourceType());
                totalIncome.put(cropSale.getResourceType(), income.add(cropSale.getAmountSold().multiply(cropSale.getPricePerUnit())));
                amountSold.put(cropSale.getResourceType(), amount.add(cropSale.getAmountSold()));
            } else {
                totalIncome.put(cropSale.getResourceType(), cropSale.getAmountSold().multiply(cropSale.getPricePerUnit()));
                amountSold.put(cropSale.getResourceType(), cropSale.getAmountSold());
            }
        }
        Map<ResourceType, BigDecimal> meanPrice = new HashMap<>();
        totalIncome.forEach((resourceType, income) -> meanPrice.put(resourceType, income.divide(amountSold.get(resourceType), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP)));
        return meanPrice;

    }

    private Map<ResourceType, BigDecimal> getYieldPerAreaUnit(Crop crop) {
        if (crop instanceof InterCrop) {
            return new HashMap<>();
        }
        if (((MainCrop) crop).getIsFullySold()) {
            return getMeanYieldByCropSales(((MainCrop) crop).getCropSales(), crop.getFieldPart().getArea());
        }
        return getMeanYieldByHarvests(((MainCrop) crop).getHarvest());

    }

    private Map<ResourceType, BigDecimal> getMeanYieldByHarvests(Set<Harvest> harvests) {
        Map<ResourceType, BigDecimal> meanYield = new HashMap<>();
        for (Harvest harvest : harvests) {
            if (meanYield.containsKey(harvest.getResourceType())) {
                throw new IllegalArgumentExceptionCustom(Harvest.class, IllegalArgumentExceptionCause.INVALID_OBJECT_PRESENT);
            } else {
                meanYield.put(harvest.getResourceType(), harvest.getQuantityPerAreaUnit());
            }
        }
        return meanYield;
    }

    private Map<ResourceType, BigDecimal> getMeanYieldByCropSales(Set<CropSale> cropSales, BigDecimal area) {
        Map<ResourceType, BigDecimal> amountSold = new HashMap<>();
        Map<ResourceType, BigDecimal> meanYield = new HashMap<>();
        for (CropSale cropSale : cropSales) {
            if (amountSold.containsKey(cropSale.getResourceType())) {
                BigDecimal amount = amountSold.get(cropSale.getResourceType());
                amountSold.remove(cropSale.getResourceType());
                amountSold.put(cropSale.getResourceType(), amount.add(cropSale.getAmountSold()));
            } else {
                amountSold.put(cropSale.getResourceType(), cropSale.getAmountSold());
            }
        }
        amountSold.forEach((resourceType, amount) -> meanYield.put(resourceType,
                amount.divide(area, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP)));
        return meanYield;
    }

    @Override
    public ResourcesSummary getCropResourcesSummary(UUID cropId) {
        Crop crop = getCropIfExist(cropId);
        return ResourcesSummary.builder()
                .cropId(crop.getId())
                .area(crop.getFieldPart().getArea().floatValue())
                .seedingMaterialPerAreaUnit(getSeedingMaterialPerAreaUnit(crop, true))
                .sprayPerAreaUnit(getSprayQuantityPerAreaUnit(crop, true))
                .fertilizerPerAreaUnit(getFertilizerQuantityPerAreaUnit(crop, true))
                .build();
    }

    @Override
    public ResourcesSummary getPlannedResourcesSummary(UUID cropId) {
        Crop crop = getCropIfExist(cropId);
        return ResourcesSummary.builder()
                .cropId(crop.getId())
                .area(crop.getFieldPart().getArea().floatValue())
                .seedingMaterialPerAreaUnit(getSeedingMaterialPerAreaUnit(crop, false))
                .sprayPerAreaUnit(getSprayQuantityPerAreaUnit(crop, false))
                .fertilizerPerAreaUnit(getFertilizerQuantityPerAreaUnit(crop, false))
                .build();
    }

    private Map<Set<UUID>, BigDecimal> getSeedingMaterialPerAreaUnit(Crop crop, boolean includeNotPlanned) {
        Map<Set<UUID>, BigDecimal> seedingMaterialQuantity = new HashMap<>();
        for (Seeding seeding : crop.getSeeding()) {
            if (seeding.getIsPlannedOperation() || includeNotPlanned) {
                if (seedingMaterialQuantity.containsKey(getSownPlantsIds(seeding))) {
                    BigDecimal quantity = seedingMaterialQuantity.get(getSownPlantsIds(seeding));
                    seedingMaterialQuantity.remove(getSownPlantsIds(seeding));
                    seedingMaterialQuantity.put(getSownPlantsIds(seeding), seeding.getQuantityPerAreaUnit().add(quantity));
                } else {
                    seedingMaterialQuantity.put(getSownPlantsIds(seeding), seeding.getQuantityPerAreaUnit());
                }
            }
        }
        return seedingMaterialQuantity;

    }

    private Set<UUID> getSownPlantsIds(Seeding seeding) {
        return seeding.getSownPlants().stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }

    private Map<UUID, BigDecimal> getFertilizerQuantityPerAreaUnit(Crop crop, boolean includeNotPlanned) {
        Map<UUID, BigDecimal> fertilizerQuantity = new HashMap<>();
        for (SprayApplication sprayApplication : crop.getSprayApplications()) {
            if (!sprayApplication.getSpray().equals(Spray.UNDEFINED)) {
                if (sprayApplication.getIsPlannedOperation() || includeNotPlanned) {
                    if (fertilizerQuantity.containsKey(getFertilizerId(sprayApplication))) {
                        BigDecimal quantity = fertilizerQuantity.get(getFertilizerId(sprayApplication));
                        fertilizerQuantity.remove(getFertilizerId(sprayApplication));
                        fertilizerQuantity.put(getFertilizerId(sprayApplication), sprayApplication.getFertilizerQuantityPerAreaUnit()
                                .add(quantity));
                    } else {
                        fertilizerQuantity.put(getFertilizerId(sprayApplication), sprayApplication.getFertilizerQuantityPerAreaUnit());
                    }
                }
            }
        }
        for (FertilizerApplication fertilizerApplication : crop.getFertilizerApplications()) {
            if (fertilizerApplication.getFertilizer().equals(Fertilizer.UNDEFINED)) {
                if (fertilizerApplication.getIsPlannedOperation() || includeNotPlanned) {
                    if (fertilizerQuantity.containsKey(getFertilizerId(fertilizerApplication))) {
                        BigDecimal quantity = fertilizerQuantity.get(getFertilizerId(fertilizerApplication));
                        fertilizerQuantity.remove(getFertilizerId(fertilizerApplication));
                        fertilizerQuantity.put(getFertilizerId(fertilizerApplication), fertilizerApplication.getQuantityPerAreaUnit()
                                .add(quantity));
                    } else {
                        fertilizerQuantity.put(getFertilizerId(fertilizerApplication),
                                fertilizerApplication.getQuantityPerAreaUnit());
                    }
                }
            }
        }
        return fertilizerQuantity;
    }

    private UUID getFertilizerId(SprayApplication sprayApplication) {
        return sprayApplication.getFertilizer().getId();
    }

    private UUID getFertilizerId(FertilizerApplication fertilizerApplication) {
        return fertilizerApplication.getFertilizer().getId();
    }

    private Map<UUID, BigDecimal> getSprayQuantityPerAreaUnit(Crop crop, boolean includeNotPlanned) {
        Map<UUID, BigDecimal> sprayQuantity = new HashMap<>();
        for (SprayApplication sprayApplication : crop.getSprayApplications()) {
            if (!sprayApplication.getSpray().equals(Spray.UNDEFINED)) {
                if (sprayApplication.getIsPlannedOperation() || includeNotPlanned) {
                    if (sprayQuantity.containsKey(getSprayId(sprayApplication))) {
                        BigDecimal quantity = sprayQuantity.get(getSprayId(sprayApplication));
                        sprayQuantity.remove(getSprayId(sprayApplication));
                        sprayQuantity.put(getSprayId(sprayApplication), sprayApplication.getQuantityPerAreaUnit()
                                .add(quantity));
                    } else {
                        sprayQuantity.put(getSprayId(sprayApplication), sprayApplication.getQuantityPerAreaUnit());
                    }
                }
            }
        }
        return sprayQuantity;
    }
    private  UUID getSprayId(SprayApplication sprayApplication) {
        return sprayApplication.getSpray().getId();
    }

    @Override
    public Map<ResourceType, CropParametersDTO> getMeanCropParameters(UUID cropId) {
        Crop crop = getCropIfExist(cropId);
        if (crop instanceof InterCrop) {
            return new HashMap<>();
        }
        if (((MainCrop) crop).getIsFullySold()) {
            return getMeanCropParametersByCropSales(((MainCrop) crop).getCropSales());
        }
        return getMeanCropParametersByHarvests(((MainCrop) crop).getHarvest());
    }

    private Map<ResourceType, CropParametersDTO> getMeanCropParametersByHarvests(Set<Harvest> harvests) {
        Map<ResourceType, CropParametersDTO> meanParameters = new HashMap<>();
        for (Harvest harvest : harvests) {
            if (meanParameters.containsKey(harvest.getResourceType())) {
                throw new IllegalArgumentExceptionCustom(Harvest.class, IllegalArgumentExceptionCause.INVALID_OBJECT_PRESENT);
            } else {
                if (!harvest.getCropParameters().equals(CropParameters.UNDEFINED)) {
                    meanParameters.put(harvest.getResourceType(),
                            DefaultMappers.cropParametersMapper.entityToDto(harvest.getCropParameters()));
                }
            }
        }
        return meanParameters;

    }

    private Map<ResourceType, CropParametersDTO> getMeanCropParametersByCropSales(Set<CropSale> cropSales) {
        Map<ResourceType, BigDecimal> amountSold = new HashMap<>();
        Map<ResourceType, CropParameters> sumParameters = new HashMap<>();
        for (CropSale cropSale : cropSales) {
            if (!cropSale.getCropParameters().equals(CropParameters.UNDEFINED)) {
                if (amountSold.containsKey(cropSale.getResourceType())) {
                    BigDecimal amount = amountSold.get(cropSale.getResourceType());
                    amountSold.remove(cropSale.getResourceType());
                    amountSold.put(cropSale.getResourceType(), amount.add(cropSale.getAmountSold()));
                    CropParameters cropParameters = sumParameters.get(cropSale.getResourceType());
                    sumParameters.remove(cropSale.getResourceType());
                    sumParameters.put(cropSale.getResourceType(), sumParameters(cropParameters, multiplyParameters(cropSale.getCropParameters(), cropSale.getAmountSold())));
                } else {
                    amountSold.put(cropSale.getResourceType(), cropSale.getAmountSold());
                    sumParameters.put(cropSale.getResourceType(), multiplyParameters(cropSale.getCropParameters(), cropSale.getAmountSold()));
                }
            }
        }
        Map<ResourceType, CropParametersDTO> meanParameters = new HashMap<>();
        amountSold.forEach((resourceType, amount) -> meanParameters.put(resourceType,
                DefaultMappers.cropParametersMapper.entityToDto(
                        multiplyParameters(sumParameters.get(resourceType), BigDecimal.valueOf(1 / amount.doubleValue())))));
        return meanParameters;
    }

    private CropParameters sumParameters(CropParameters cropParameters, CropParameters cropParameters1) {
        if (cropParameters instanceof GrainParameters && cropParameters1 instanceof GrainParameters) {
            return GrainParameters.builder().density(((GrainParameters) cropParameters).getDensity().add(((GrainParameters) cropParameters1).getDensity())).humidity(((GrainParameters) cropParameters).getHumidity().add(((GrainParameters) cropParameters1).getHumidity())).proteinContent(((GrainParameters) cropParameters).getProteinContent().add(((GrainParameters) cropParameters1).getProteinContent())).glutenContent(((GrainParameters) cropParameters).getGlutenContent().add(((GrainParameters) cropParameters1).getGlutenContent())).fallingNumber(((GrainParameters) cropParameters).getHumidity().add(((GrainParameters) cropParameters1).getFallingNumber())).pollution(cropParameters.getPollution().add(cropParameters1.getPollution())).build();
        }
        if (cropParameters instanceof RapeSeedParameters && cropParameters1 instanceof RapeSeedParameters) {
            return RapeSeedParameters.builder().density(((RapeSeedParameters) cropParameters).getDensity().add(((RapeSeedParameters) cropParameters1).getDensity())).humidity(((RapeSeedParameters) cropParameters).getHumidity().add(((RapeSeedParameters) cropParameters).getHumidity())).oilContent(((RapeSeedParameters) cropParameters).getOilContent().add(((RapeSeedParameters) cropParameters1).getOilContent())).pollution(cropParameters.getPollution().add(cropParameters1.getPollution())).build();
        }

        if (cropParameters instanceof SugarBeetParameters && cropParameters1 instanceof SugarBeetParameters) {
            return SugarBeetParameters.builder().sugarContent(((SugarBeetParameters) cropParameters1).getSugarContent().add(((SugarBeetParameters) cropParameters).getSugarContent())).pollution(cropParameters1.getPollution().add(cropParameters.getPollution())).build();
        }
        return CropParameters.builder().pollution(cropParameters1.getPollution().add(cropParameters.getPollution())).build();
    }

    private CropParameters multiplyParameters(CropParameters cropParameters, BigDecimal multiplier) {
        if (cropParameters instanceof GrainParameters) {
            return GrainParameters.builder().density(((GrainParameters) cropParameters).getDensity().multiply(multiplier)).humidity(((GrainParameters) cropParameters).getHumidity().multiply(multiplier)).proteinContent(((GrainParameters) cropParameters).getProteinContent().multiply(multiplier)).glutenContent(((GrainParameters) cropParameters).getGlutenContent().multiply(multiplier)).fallingNumber(((GrainParameters) cropParameters).getHumidity().multiply(multiplier)).pollution(cropParameters.getPollution().multiply(multiplier)).build();
        }
        if (cropParameters instanceof RapeSeedParameters) {
            return RapeSeedParameters.builder().density(((RapeSeedParameters) cropParameters).getDensity().multiply(multiplier)).humidity(((RapeSeedParameters) cropParameters).getHumidity().multiply(multiplier)).oilContent(((RapeSeedParameters) cropParameters).getOilContent().multiply(multiplier)).pollution(cropParameters.getPollution().multiply(multiplier)).build();
        }

        if (cropParameters instanceof SugarBeetParameters) {
            return SugarBeetParameters.builder().sugarContent(((SugarBeetParameters) cropParameters).getSugarContent().multiply(multiplier)).pollution(cropParameters.getPollution().multiply(multiplier)).build();
        }
        return CropParameters.builder().pollution(cropParameters.getPollution().multiply(multiplier)).build();

    }
}
