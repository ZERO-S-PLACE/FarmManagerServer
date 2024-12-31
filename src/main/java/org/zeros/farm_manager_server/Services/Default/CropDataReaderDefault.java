package org.zeros.farm_manager_server.Services.Default;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Services.Interface.CropDataReader;
import org.zeros.farm_manager_server.Services.Interface.CropOperationsManager;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.Entities.Crop.Crop.InterCrop;
import org.zeros.farm_manager_server.Entities.Crop.Crop.MainCrop;
import org.zeros.farm_manager_server.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Entities.Crop.CropParameters.GrainParameters;
import org.zeros.farm_manager_server.Entities.Crop.CropParameters.RapeSeedParameters;
import org.zeros.farm_manager_server.Entities.Crop.CropParameters.SugarBeetParameters;
import org.zeros.farm_manager_server.Entities.Crop.CropSale;
import org.zeros.farm_manager_server.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Entities.Crop.Subside;
import org.zeros.farm_manager_server.DTO.DataTransfer.ResourcesSummary;
import org.zeros.farm_manager_server.DTO.DataTransfer.CropSummary;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Primary
public class CropDataReaderDefault implements CropDataReader {
    private final CropOperationsManager cropOperationsManager;

    public CropDataReaderDefault(CropOperationsManager cropOperationsManager) {
        this.cropOperationsManager = cropOperationsManager;
    }

    private static Map<ResourceType, BigDecimal> getMeanYieldByCropSales(Set<CropSale> cropSales, BigDecimal area) {
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
        amountSold.forEach((resourceType, amount) -> meanYield.put(resourceType, amount.divide(area, RoundingMode.HALF_DOWN)));
        return meanYield;
    }

    private static Map<ResourceType, BigDecimal> getMeanYieldByHarvests(Set<Harvest> harvests) {
        Map<ResourceType, BigDecimal> meanYield = new HashMap<>();
        for (Harvest harvest : harvests) {
            if (meanYield.containsKey(harvest.getResourceType())) {
                throw new IllegalArgumentException("Many harvests of the same resource");
            } else {
                meanYield.put(harvest.getResourceType(), harvest.getQuantityPerAreaUnit());
            }
        }
        return meanYield;
    }

    @Override
    public CropSummary getCropSummary(Crop crop) {
        crop = cropOperationsManager.getCropById(crop.getId());
        if (crop == MainCrop.NONE) {
            return CropSummary.NONE;
        }
        return CropSummary.builder()
                .cropId(crop.getId())
                .area(crop.getFieldPart().getArea())
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

    private boolean getIsValueDetermined(Crop crop) {
        if (crop instanceof MainCrop) {
            return ((MainCrop) crop).getIsFullySold() && crop.getWorkFinished();
        }
        return (!((InterCrop) crop).getDateDestroyed().equals(ApplicationDefaults.UNDEFINED_DATE_MAX)) && crop.getWorkFinished();
    }

    private BigDecimal getSubsidesPerAreaUnit(Crop crop) {
        BigDecimal subsideSum = BigDecimal.ZERO;
        for (Subside subside : crop.getSubsides()) {
            subsideSum = subsideSum.add(subside.getSubsideValuePerAreaUnit());
        }
        return subsideSum;

    }

    private BigDecimal getFertilizerCostPerAreaUnit(Crop crop) {
        BigDecimal fertilizerCostSum = BigDecimal.ZERO;
        for (FertilizerApplication fertilizerApplication : crop.getFertilizerApplications()) {
            fertilizerCostSum = fertilizerCostSum.add(fertilizerApplication.getQuantityPerAreaUnit().multiply(fertilizerApplication.getPricePerUnit()));
        }
        for (SprayApplication sprayApplication : crop.getSprayApplications()) {
            fertilizerCostSum = fertilizerCostSum.add(sprayApplication.getFertilizerQuantityPerAreaUnit().multiply(sprayApplication.getFertilizerPricePerUnit()));
        }
        return fertilizerCostSum;
    }

    private BigDecimal getSprayCostPerAreaUnit(Crop crop) {

        BigDecimal sprayCostSum = BigDecimal.ZERO;

        for (SprayApplication sprayApplication : crop.getSprayApplications()) {
            sprayCostSum = sprayCostSum.add(sprayApplication.getQuantityPerAreaUnit().multiply(sprayApplication.getPricePerUnit()));
        }
        return sprayCostSum;
    }

    private BigDecimal getFuelCostPerAreaUnit(Crop crop) {
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
        return fuelCostSum;
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
                estimatedAmount.put(harvest.getResourceType(), amount.add(harvest.getQuantityPerAreaUnit()));
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
                throw new IllegalArgumentException("Non harvested crop was sold");
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
        totalIncome.forEach((resourceType, income) -> meanPrice.put(resourceType, income.divide(amountSold.get(resourceType), RoundingMode.HALF_DOWN)));
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

    @Override
    public ResourcesSummary getCropResourcesSummary(Crop crop) {
        crop = cropOperationsManager.getCropById(crop.getId());
        if (crop == MainCrop.NONE) {
            return ResourcesSummary.NONE;
        }
        return ResourcesSummary.builder()
                .cropId(crop.getId())
                .area(crop.getFieldPart().getArea())
                .seedingMaterialPerAreaUnit(getSeedingMaterialPerAreaUnit(crop,true))
                .sprayPerAreaUnit(getSprayQuantityPerAreaUnit(crop,true))
                .fertilizerPerAreaUnit(getFertilizerQuantityPerAreaUnit(crop,true))
                .build();
    }
    @Override
    public ResourcesSummary getPlannedResourcesSummary(Crop crop) {
        crop = cropOperationsManager.getCropById(crop.getId());
        if (crop == MainCrop.NONE) {
            return ResourcesSummary.NONE;
        }
        return ResourcesSummary.builder()
                .cropId(crop.getId())
                .area(crop.getFieldPart().getArea())
                .seedingMaterialPerAreaUnit(getSeedingMaterialPerAreaUnit(crop,false))
                .sprayPerAreaUnit(getSprayQuantityPerAreaUnit(crop,false))
                .fertilizerPerAreaUnit(getFertilizerQuantityPerAreaUnit(crop,false))
                .build();
    }

    private Map<Set<Plant>, BigDecimal> getSeedingMaterialPerAreaUnit(Crop crop,boolean includeNotPlanned) {
        Map<Set<Plant>, BigDecimal> seedingMaterialQuantity = new HashMap<>();
        for (Seeding seeding : crop.getSeeding()) {
            if(seeding.getIsPlannedOperation()||includeNotPlanned) {
                if (seedingMaterialQuantity.containsKey(seeding.getSownPlants())) {
                    BigDecimal quantity = seedingMaterialQuantity.get(seeding.getSownPlants());
                    seedingMaterialQuantity.remove(seeding.getSownPlants());
                    seedingMaterialQuantity.put(seeding.getSownPlants(), seeding.getQuantityPerAreaUnit().add(quantity));
                } else {
                    seedingMaterialQuantity.put(seeding.getSownPlants(), seeding.getQuantityPerAreaUnit());
                }
            }
        }
        return seedingMaterialQuantity;

    }

    private Map<Fertilizer, BigDecimal> getFertilizerQuantityPerAreaUnit(Crop crop,boolean includeNotPlanned) {
        Map<Fertilizer, BigDecimal> fertilizerQuantity = new HashMap<>();
        for (SprayApplication sprayApplication : crop.getSprayApplications()) {
            if (!sprayApplication.getSpray().equals(Spray.UNDEFINED)) {
                if(sprayApplication.getIsPlannedOperation()||includeNotPlanned) {
                    if (fertilizerQuantity.containsKey(sprayApplication.getFertilizer())) {
                        BigDecimal quantity = fertilizerQuantity.get(sprayApplication.getFertilizer());
                        fertilizerQuantity.remove(sprayApplication.getFertilizer());
                        fertilizerQuantity.put(sprayApplication.getFertilizer(), sprayApplication.getFertilizerQuantityPerAreaUnit().add(quantity));
                    } else {
                        fertilizerQuantity.put(sprayApplication.getFertilizer(), sprayApplication.getFertilizerQuantityPerAreaUnit());
                    }
                }
            }
        }
        for (FertilizerApplication fertilizerApplication : crop.getFertilizerApplications()) {
            if (!fertilizerApplication.getFertilizer().equals(Fertilizer.UNDEFINED)) {
                if(fertilizerApplication.getIsPlannedOperation()||includeNotPlanned) {
                    if (fertilizerQuantity.containsKey(fertilizerApplication.getFertilizer())) {
                        BigDecimal quantity = fertilizerQuantity.get(fertilizerApplication.getFertilizer());
                        fertilizerQuantity.remove(fertilizerApplication.getFertilizer());
                        fertilizerQuantity.put(fertilizerApplication.getFertilizer(), fertilizerApplication.getQuantityPerAreaUnit().add(quantity));
                    } else {
                        fertilizerQuantity.put(fertilizerApplication.getFertilizer(), fertilizerApplication.getQuantityPerAreaUnit());
                    }
                }
            }
        }
        return fertilizerQuantity;
    }

    private Map<Spray, BigDecimal> getSprayQuantityPerAreaUnit(Crop crop,boolean includeNotPlanned) {
        Map<Spray, BigDecimal> sprayQuantity = new HashMap<>();
        for (SprayApplication sprayApplication : crop.getSprayApplications()) {
            if (!sprayApplication.getSpray().equals(Spray.UNDEFINED)) {
                if(sprayApplication.getIsPlannedOperation()||includeNotPlanned) {
                    if (sprayQuantity.containsKey(sprayApplication.getSpray())) {
                        BigDecimal quantity = sprayQuantity.get(sprayApplication.getSpray());
                        sprayQuantity.remove(sprayApplication.getSpray());
                        sprayQuantity.put(sprayApplication.getSpray(), sprayApplication.getQuantityPerAreaUnit().add(quantity));
                    } else {
                        sprayQuantity.put(sprayApplication.getSpray(), sprayApplication.getQuantityPerAreaUnit());
                    }
                }
            }
        }
        return sprayQuantity;
    }

    @Override
    public Map<ResourceType, CropParameters> getMeanCropParameters(Crop crop) {
        if (crop instanceof InterCrop) {
            return new HashMap<>();
        }
        if (((MainCrop) crop).getIsFullySold()) {
            return getMeanCropParametersByCropSales(((MainCrop) crop).getCropSales());
        }
        return getMeanCropParametersByHarvests(((MainCrop) crop).getHarvest());
    }

    private Map<ResourceType, CropParameters> getMeanCropParametersByHarvests(Set<Harvest> harvests) {
        Map<ResourceType, CropParameters> meanParameters = new HashMap<>();
        for (Harvest harvest : harvests) {
            if (meanParameters.containsKey(harvest.getResourceType())) {
                throw new IllegalArgumentException("Many harvests of the same resource");
            } else {
                if (!harvest.getCropParameters().equals(CropParameters.UNDEFINED)) {
                    meanParameters.put(harvest.getResourceType(), harvest.getCropParameters());
                }
            }
        }
        return meanParameters;

    }

    private Map<ResourceType, CropParameters> getMeanCropParametersByCropSales(Set<CropSale> cropSales) {
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
        Map<ResourceType, CropParameters> meanParameters = new HashMap<>();
        amountSold.forEach((resourceType, amount) -> meanParameters.put(resourceType, multiplyParameters(sumParameters.get(resourceType), BigDecimal.valueOf(1 / amount.doubleValue()))));
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
