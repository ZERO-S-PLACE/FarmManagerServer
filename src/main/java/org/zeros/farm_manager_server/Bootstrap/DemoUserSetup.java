package org.zeros.farm_manager_server.Bootstrap;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.GrainParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.RapeSeedParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.DTO.Data.SubsideDTO;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldDTO;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldPartDTO;
import org.zeros.farm_manager_server.Domain.DTO.Operations.*;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Data.*;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.Operations.Harvest;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Enum.CultivationType;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Services.Default.Crop.CropSaleManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Operations.AgriculturalOperationsManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Data.*;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldGroupManager;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldManager;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local")
public class DemoUserSetup {
    private final UserManager userManager;
    private final FieldPartManager fieldPartManager;
    private final FieldManager fieldManager;
    private final FieldGroupManager fieldGroupManager;
    private final SubsideManager subsideManager;
    private final SprayManager sprayManager;
    private final PlantManager plantManager;
    private final FertilizerManager fertilizerManager;
    private final FarmingMachineManager farmingMachineManager;
    private final CropParametersManager cropParametersManager;
    private final CropManager cropManager;
    private final Random random = new Random();
    private final CropSaleManagerDefault cropSaleManagerDefault;
    private final AgriculturalOperationsManagerDefault agriculturalOperationsManagerDefault;
    private final LoggedUserConfiguration loggedUserConfiguration;
    private Field field1;
    private Field field2;
    private Field field3;
    private Field field4;
    private Field field5;
    private ArrayList<Plant> plants;
    private ArrayList<Spray> sprays;
    private ArrayList<FarmingMachine> farmingMachines;
    private ArrayList<Fertilizer> fertilizers;

    @Transactional
    public void createDemoUser() {
        if (userManager.getUserByUsername("DEMO_USER").equals(User.NONE)) {
            log.atInfo().log("Creating Demo User");
            createNewDemoUser();
            loggedUserConfiguration.replaceUser(userManager.getUserByUsername("DEMO_USER"));
            addDemoUserFields();
            log.atInfo().log("Created Demo User fields");
            loadDefaultData();
            addDemoUserCrops();
            loggedUserConfiguration.replaceUser(User.NONE);
            log.atInfo().log("Created Demo User operations");
        }
    }

    @Transactional
    protected void loadDefaultData() {
        plants = plantManager.getAllPlants(0).stream().collect(Collectors.toCollection(ArrayList::new));
        sprays = sprayManager.getAllSprays(0).stream().filter(spray -> !spray.equals(sprayManager.getUndefinedSpray())).collect(Collectors.toCollection(ArrayList::new));
        farmingMachines = farmingMachineManager.getDefaultFarmingMachines(0).stream().collect(Collectors.toCollection(ArrayList::new));
        fertilizers = fertilizerManager.getDefaultFertilizers(0).stream().collect(Collectors.toCollection(ArrayList::new));
    }


    @Transactional
    protected void createNewDemoUser() {
        UserDTO userDTO = UserDTO.builder()
                .firstName("Demo")
                .lastName("User")
                .username("DEMO_USER")
                .password("DEMO_PASSWORD")
                .email("demo@zeros.org")
                .phoneNumber("999999999")
                .build();
        userManager.registerNewUser(userDTO);

    }

    @Transactional
    protected void addDemoUserFields() {
        FieldGroup group1 = fieldGroupManager.createEmptyFieldGroup("Kowary wschód", "");
        FieldGroup group2 = fieldGroupManager.createEmptyFieldGroup("Kowary zachód", "");
        field1 = fieldManager.createFieldInGroup(FieldDTO.builder()
                        .fieldName("Koło stawu")
                        .isOwnField(true)
                        .area(BigDecimal.valueOf(12.21))
                        .propertyTax(BigDecimal.valueOf(132.11))
                        .surveyingPlots(Set.of("11/2", "11/3", "11/8"))
                        .build()
                , group1.getId());
        field2 = fieldManager.createFieldInGroup(FieldDTO.builder()
                        .fieldName("Kowalski za domem")
                        .isOwnField(false)
                        .area(BigDecimal.valueOf(1.21))
                        .rent(BigDecimal.valueOf(1321.11))
                        .surveyingPlots(Set.of("112/2", "112/8"))
                        .build()
                , group1.getId());
        field3 = fieldManager.createFieldInGroup(FieldDTO.builder()
                        .fieldName("Kowalski przed domem")
                        .isOwnField(false)
                        .area(BigDecimal.valueOf(15.11))
                        .rent(BigDecimal.valueOf(1321.11))
                        .surveyingPlots(Set.of("112", "115/1"))
                        .build()
                , group1.getId());

        field4 = fieldManager.createFieldInGroup(FieldDTO.builder()
                        .fieldName("Za domem")
                        .isOwnField(true)
                        .area(BigDecimal.valueOf(45.11))
                        .propertyTax(BigDecimal.valueOf(132.11))
                        .surveyingPlots(Set.of("1121", "1151/1"))
                        .build()
                , group2.getId());
        field5 = fieldManager.createFieldInGroup(FieldDTO.builder()
                        .fieldName("Przed domem")
                        .isOwnField(true)
                        .area(BigDecimal.valueOf(145.11))
                        .propertyTax(BigDecimal.valueOf(132.11))
                        .surveyingPlots(Set.of("1/1", "1/11", "1/12", "1/13", "1/21", "1/22", "1/23", "1/24", "1/25", "1/26"))
                        .build()
                , group2.getId());

        divideFields();
    }

    @Transactional
    protected void divideFields() {
        fieldPartManager.divideFieldPart(fieldManager.getFieldById(field3.getId()).getFieldParts().stream().findFirst().orElse(FieldPart.NONE).getId(),
                FieldPartDTO.builder()
                        .fieldPartName("Część wschodnia")
                        .area(BigDecimal.valueOf(0.5))
                        .build(),
                FieldPartDTO.builder()
                        .fieldPartName("Część zachodnia")
                        .build()
        );
        fieldPartManager.divideFieldPart(fieldManager.getFieldById(field5.getId()).getFieldParts().stream().findFirst().orElse(FieldPart.NONE).getId(),
                FieldPartDTO.builder()
                        .fieldPartName("Część wschodnia")
                        .area(BigDecimal.valueOf(15.11))
                        .build(),
                FieldPartDTO.builder()
                        .fieldPartName("Część zachodnia")
                        .build()
        );
    }

    @Transactional
    protected void addDemoUserCrops() {
        Set<Field> fields = Set.of(fieldManager.getFieldById(field1.getId()),
                fieldManager.getFieldById(field2.getId()),
                fieldManager.getFieldById(field3.getId()),
                fieldManager.getFieldById(field4.getId()),
                fieldManager.getFieldById(field5.getId()));
        createActiveCrops(fields);
        createNotActiveCrops(fields);
    }

    @Transactional
    protected void createActiveCrops(Set<Field> fields) {
        for (Field f : fields) {
            for (FieldPart fp : fieldManager.getFieldById(f.getId()).getFieldParts()) {
                if (!fp.getIsArchived()) {
                    createRandomCropWithOperations(fp, random.nextBoolean(), 0);
                }
            }
        }
    }

    @Transactional
    protected void createNotActiveCrops(Set<Field> fields) {
        for (int i = 1; i < 10; i++) {
            for (Field f : fields) {
                for (FieldPart fp : fieldManager.getFieldById(f.getId()).getFieldParts()) {
                    if (!fp.getIsArchived()) {
                        MainCrop crop = createRandomCropWithOperations(fp, true, i);
                        cropManager.setWorkFinished(crop.getId());
                        crop = addCropSales(crop);
                        if (crop.getCropSales().size() > 2) {
                            cropManager.setFullySold(crop.getId());
                        }
                    }
                }
            }
        }
    }

    @Transactional
    protected MainCrop addCropSales(MainCrop crop) {
        Harvest harvest = crop.getHarvest().stream().findFirst().orElse(Harvest.NONE);
        float estimatedGrainQuantity = harvest.getQuantityPerAreaUnit().floatValue() *
                crop.getFieldPart().getArea().floatValue();
        float amountSoldSum = 0;
        while (amountSoldSum < 0.85 * estimatedGrainQuantity) {
            float amountSold = estimatedGrainQuantity * random.nextFloat();
            cropSaleManagerDefault.addCropSale(crop.getId(), CropSaleDTO.builder()
                    .amountSold(BigDecimal.valueOf(amountSold))
                    .soldTo("CEFETRA")
                    .dateSold(harvest.getDateFinished().plusDays(random.nextInt(3, 100)))
                    .resourceType(harvest.getResourceType())
                    .pricePerUnit(BigDecimal.valueOf(550 * (1 + random.nextFloat())))
                    .unit("t")
                    .cropParameters(getRandomCropParameters(crop).getId())
                    .build());
            amountSoldSum += amountSold;
        }
        return (MainCrop) cropManager.getCropById(crop.getId());
    }

    @Transactional
    protected MainCrop createRandomCropWithOperations(FieldPart fieldPart, boolean withHarvest, int yearsOffset) {
        MainCrop mainCrop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plants.get(random.nextInt(0, plants.size() - 1)).getId()));
        for (int i = 0; i < random.nextInt(7) + 1; i++) {
            LocalDate subsideYear = LocalDate.now().minusYears(yearsOffset);
            cropManager.addSubside(mainCrop.getId(), createRandomSubside(mainCrop.getCultivatedPlants(), subsideYear).getId());
        }

        LocalDate seedingDate = LocalDate.now().minusDays(random.nextInt(260, 270)).minusYears(yearsOffset);
        if (yearsOffset == 0 && random.nextBoolean()) {
            agriculturalOperationsManagerDefault.planOperation(mainCrop.getId(), getRandomSeeding(seedingDate, mainCrop));
        } else {
            agriculturalOperationsManagerDefault.addOperation(mainCrop.getId(), getRandomSeeding(seedingDate, mainCrop));
        }

        for (int i = 0; i < random.nextInt(7) + 1; i++) {
            LocalDate cultivationDate = LocalDate.now().minusDays(random.nextInt(270, 300)).minusYears(yearsOffset);
            float depth = random.nextFloat() * 40;
            if (yearsOffset == 0 && random.nextBoolean()) {
                agriculturalOperationsManagerDefault.planOperation(mainCrop.getId(), getRandomCultivation(depth, cultivationDate));
            } else {
                agriculturalOperationsManagerDefault.addOperation(mainCrop.getId(), getRandomCultivation(depth, cultivationDate));
            }
        }
        for (int i = 0; i < random.nextInt(10) + 2; i++) {
            LocalDate fertilizerApplicationDate = LocalDate.now().minusDays(random.nextInt(100, 300)).minusYears(yearsOffset);

            if (yearsOffset == 0 && random.nextBoolean()) {
                agriculturalOperationsManagerDefault.planOperation(mainCrop.getId(), getRandomFertilizerApplication(fertilizerApplicationDate));
            } else {
                agriculturalOperationsManagerDefault.addOperation(mainCrop.getId(), getRandomFertilizerApplication(fertilizerApplicationDate));
            }
        }
        for (int i = 0; i < random.nextInt(10) + 2; i++) {
            LocalDate sprayApplicationDate = LocalDate.now().minusDays(random.nextInt(100, 250)).minusYears(yearsOffset);
            if (yearsOffset == 0 && random.nextBoolean()) {
                agriculturalOperationsManagerDefault.planOperation(mainCrop.getId(), getRandomSprayApplication(sprayApplicationDate));
            } else {
                agriculturalOperationsManagerDefault.addOperation(mainCrop.getId(), getRandomSprayApplication(sprayApplicationDate));
            }
        }
        if (withHarvest) {
            LocalDate harvestDate = LocalDate.now().minusDays(random.nextInt(5, 50)).minusYears(yearsOffset);
            if (yearsOffset == 0 && random.nextBoolean()) {
                agriculturalOperationsManagerDefault.planOperation(mainCrop.getId(), getRandomHarvest(mainCrop, harvestDate));
            } else {
                agriculturalOperationsManagerDefault.addOperation(mainCrop.getId(), getRandomHarvest(mainCrop, harvestDate));
            }
        }

        return (MainCrop) cropManager.getCropById(mainCrop.getId());
    }

    private Subside createRandomSubside(Set<Plant> plants, LocalDate subsideYear) {
        return subsideManager.addSubside(SubsideDTO.builder()
                .name("Subside" + Math.round(random.nextFloat() * 100000))
                .subsideValuePerAreaUnit(BigDecimal.valueOf(500 * random.nextFloat()))
                .yearOfSubside(subsideYear)
                .speciesAllowed(plants.stream().map(plant -> plant.getSpecies().getId()).collect(Collectors.toSet()))
                .build());
    }

    private HarvestDTO getRandomHarvest(MainCrop mainCrop, LocalDate harvestDate) {
        return HarvestDTO.builder()
                .farmingMachine(farmingMachines.stream()
                        .filter(machine -> machine.getSupportedOperationTypes().contains(OperationType.HARVEST))
                        .findFirst().orElse(FarmingMachine.UNDEFINED).getId())
                .quantityPerAreaUnit(BigDecimal.valueOf(random.nextFloat() * 15))
                .cropParameters(getRandomCropParameters(mainCrop).getId())
                .fuelConsumptionPerUnit(BigDecimal.valueOf(random.nextFloat() * 21))
                .fuelPrice(BigDecimal.valueOf(5.17f))
                .dateStarted(harvestDate)
                .dateFinished(harvestDate.plusDays(1))
                .build();
    }

    private SprayApplicationDTO getRandomSprayApplication(LocalDate sprayApplicationDate) {
        return SprayApplicationDTO.builder()
                .farmingMachine(farmingMachines.stream()
                        .filter(machine -> machine.getSupportedOperationTypes().contains(OperationType.SPRAY_APPLICATION))
                        .findFirst().orElse(FarmingMachine.UNDEFINED).getId())
                .fertilizer(fertilizers.get(random.nextInt(fertilizers.size() - 1)).getId())
                .spray(sprays.get(random.nextInt(sprays.size() - 1)).getId())
                .quantityPerAreaUnit(BigDecimal.valueOf(random.nextFloat()))
                .pricePerUnit(BigDecimal.valueOf(random.nextFloat() * 2000))
                .fertilizerQuantityPerAreaUnit(BigDecimal.valueOf(random.nextFloat()))
                .fertilizerPricePerUnit(BigDecimal.valueOf(random.nextFloat() * 2000))
                .fuelConsumptionPerUnit(BigDecimal.valueOf(random.nextFloat() * 4))
                .fuelPrice(BigDecimal.valueOf(5.17))
                .dateStarted(sprayApplicationDate)
                .dateFinished(sprayApplicationDate.plusDays(1))
                .build();
    }

    private FertilizerApplicationDTO getRandomFertilizerApplication(LocalDate fertilizerApplicationDate) {
        return FertilizerApplicationDTO.builder()
                .farmingMachine(farmingMachines.stream()
                        .filter(machine -> machine.getSupportedOperationTypes().contains(OperationType.FERTILIZER_APPLICATION))
                        .findFirst().orElse(FarmingMachine.UNDEFINED).getId())
                .fertilizer(fertilizers.get(random.nextInt(fertilizers.size() - 1)).getId())
                .quantityPerAreaUnit(BigDecimal.valueOf(random.nextFloat()))
                .pricePerUnit(BigDecimal.valueOf(random.nextInt(100, 2000)))
                .fuelConsumptionPerUnit(BigDecimal.valueOf(random.nextFloat() * 5))
                .fuelPrice(BigDecimal.valueOf(5.17f))
                .dateStarted(fertilizerApplicationDate)
                .dateFinished(fertilizerApplicationDate.plusDays(1))
                .build();
    }

    private CultivationDTO getRandomCultivation(float depth, LocalDate cultivationDate) {
        return CultivationDTO.builder()
                .farmingMachine(farmingMachines.stream()
                        .filter(machine -> machine.getSupportedOperationTypes().contains(OperationType.CULTIVATION))
                        .findFirst().orElse(FarmingMachine.UNDEFINED).getId())
                .depth(BigDecimal.valueOf(depth))
                .cultivationType(cultivationTypeAccordingToDepth(depth))
                .fuelConsumptionPerUnit(BigDecimal.valueOf(random.nextFloat() * 30))
                .fuelPrice(BigDecimal.valueOf(5.17))
                .dateStarted(cultivationDate)
                .dateFinished(cultivationDate.plusDays(1))
                .build();
    }

    private SeedingDTO getRandomSeeding(LocalDate seedingDate, Crop crop) {
        return SeedingDTO.builder()
                .sownPlants(crop.getCultivatedPlants().stream().map(BaseEntity::getId).collect(Collectors.toSet()))
                .farmingMachine(farmingMachines.stream()
                        .filter(machine -> machine.getSupportedOperationTypes().contains(OperationType.SEEDING))
                        .findFirst().orElse(FarmingMachine.UNDEFINED).getId())
                .quantityPerAreaUnit(BigDecimal.valueOf(random.nextFloat() * 100))
                .thousandSeedsMass(BigDecimal.valueOf(random.nextFloat() * 50))
                .depth(BigDecimal.valueOf(random.nextFloat() * 10))
                .seedsCostPerUnit(BigDecimal.valueOf(random.nextFloat() * 2000))
                .fuelConsumptionPerUnit(BigDecimal.valueOf(random.nextFloat() * 30))
                .fuelPrice(BigDecimal.valueOf(5.17f))
                .dateStarted(seedingDate)
                .dateFinished(seedingDate.plusDays(1))
                .build();
    }

    private CultivationType cultivationTypeAccordingToDepth(float depth) {
        if (depth < 1) return CultivationType.MULCHING;
        if (depth < 3) return CultivationType.VERY_SHALLOW;
        if (depth < 7) return CultivationType.SHALLOW;
        if (depth < 23) return CultivationType.PLOWING;
        if (depth < 30) return CultivationType.DEEP_NO_TILL;
        return CultivationType.DEEP_LOOSENING;
    }

    protected CropParameters getRandomCropParameters(MainCrop mainCrop) {
        if (cropManager.getCropById(mainCrop.getId()).getCultivatedPlants().stream().findFirst().orElse(Plant.NONE).getSpecies().getName().equals("Rape seed")) {
            return cropParametersManager.addCropParameters(RapeSeedParametersDTO.builder()
                    .name("Parameters" + Math.round(random.nextFloat() * 10000))
                    .density(BigDecimal.valueOf(600 + random.nextInt(100)))
                    .humidity(BigDecimal.valueOf(5 + random.nextFloat() * 5))
                    .resourceType(ResourceType.GRAIN)
                    .build());

        }
        return cropParametersManager.addCropParameters(GrainParametersDTO.builder()
                .name("Parameters" + Math.round(random.nextFloat() * 1000000))
                .density(BigDecimal.valueOf(700 + random.nextInt(100)))
                .humidity(BigDecimal.valueOf(14 + random.nextFloat() * 5))
                .glutenContent(BigDecimal.valueOf(20 + random.nextFloat() * 20))
                .proteinContent(BigDecimal.valueOf(20 + random.nextFloat() * 15))
                .resourceType(ResourceType.GRAIN)
                .build());
    }
}
