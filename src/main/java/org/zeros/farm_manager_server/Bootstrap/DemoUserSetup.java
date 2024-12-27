package org.zeros.farm_manager_server.Bootstrap;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.Services.Interface.CropOperationsManager;
import org.zeros.farm_manager_server.Services.Interface.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Data.*;
import org.zeros.farm_manager_server.Services.Interface.UserFieldsManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Enum.CultivationType;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.Entities.Crop.Crop.MainCrop;
import org.zeros.farm_manager_server.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Entities.Crop.CropParameters.GrainParameters;
import org.zeros.farm_manager_server.Entities.Crop.CropParameters.RapeSeedParameters;
import org.zeros.farm_manager_server.Entities.Crop.CropSale;
import org.zeros.farm_manager_server.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Entities.Crop.Subside;
import org.zeros.farm_manager_server.Entities.Fields.Field;
import org.zeros.farm_manager_server.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Entities.User.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DemoUserSetup {
    private final UserManager userManager;
    private final UserFieldsManager userFieldsManager;
    private final SubsideManager subsideManager;
    private final SprayManager sprayManager;
    private final PlantManager plantManager;
    private final FertilizerManager fertilizerManager;
    private final FarmingMachineManager farmingMachineManager;
    private final CropParametersManager cropParametersManager;
    private final CropOperationsManager cropOperationsManager;
    private Field field1;
    private Field field2;
    private Field field3;
    private Field field4;
    private Field field5;
    private ArrayList<Plant> plants;
    private ArrayList<Spray> sprays;
    private ArrayList<FarmingMachine> farmingMachines;
    private ArrayList<Subside> subsides;
    private ArrayList<Fertilizer> fertilizers;
    private final Random random = new Random();
    public DemoUserSetup(UserManager userManager, UserFieldsManager userFieldsManager, SubsideManager subsideManager, SprayManager sprayManager, SpeciesManager speciesManager, PlantManager plantManager, FertilizerManager fertilizerManager, FarmingMachineManager farmingMachineManager, CropParametersManager cropParametersManager, CropOperationsManager cropOperationsManager) {
        this.userManager = userManager;
        this.userFieldsManager = userFieldsManager;
        this.subsideManager = subsideManager;
        this.sprayManager = sprayManager;
        this.plantManager = plantManager;
        this.fertilizerManager = fertilizerManager;
        this.farmingMachineManager = farmingMachineManager;
        this.cropParametersManager = cropParametersManager;
        this.cropOperationsManager = cropOperationsManager;
    }

    @Transactional
    public void createDemoUser() {
        if (userManager.getUserByUsername("DEMO_USER").equals(User.NONE)) {
            createNewDemoUser();
            userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
            addDemoUserFields();
            loadDefaultData();
            addDemoUserCrops();
            userManager.logOutUser();
        }
    }

    @Transactional
    protected void loadDefaultData() {
        plants = plantManager.getAllPlants(0).stream().collect(Collectors.toCollection(ArrayList::new));
        sprays = sprayManager.getAllSprays(0).stream().filter(spray -> !spray.equals(sprayManager.getUndefinedSpray())).collect(Collectors.toCollection(ArrayList::new));
        farmingMachines = farmingMachineManager.getDefaultFarmingMachines(0).stream().collect(Collectors.toCollection(ArrayList::new));
        subsides = subsideManager.getAllSubsides(0).stream().collect(Collectors.toCollection(ArrayList::new));
        fertilizers = fertilizerManager.getDefaultFertilizers(0).stream().collect(Collectors.toCollection(ArrayList::new));
    }


    @Transactional
    protected void createNewDemoUser() {
        User user = User.builder()
                .firstName("Demo")
                .lastName("User")
                .username("DEMO_USER")
                .password("DEMO_PASSWORD")
                .email("demo@zeros.org")
                .phoneNumber("999999999")
                .build();
        userManager.createNewUser(user);

    }

    @Transactional
    protected void addDemoUserFields() {
        FieldGroup group1 = userFieldsManager.createEmptyFieldGroup("Kowary wschód", "");
        FieldGroup group2 = userFieldsManager.createEmptyFieldGroup("Kowary zachód", "");
        field1 = userFieldsManager.createFieldInGroup(Field.builder()
                        .fieldName("Koło stawu")
                        .isOwnField(true)
                        .area(BigDecimal.valueOf(12.21))
                        .propertyTax(BigDecimal.valueOf(132.11))
                        .surveyingPlots(Set.of("11/2", "11/3", "11/8"))
                        .build()
                , group1);
        field2 = userFieldsManager.createFieldInGroup(Field.builder()
                        .fieldName("Kowalski za domem")
                        .isOwnField(false)
                        .area(BigDecimal.valueOf(1.21))
                        .rent(BigDecimal.valueOf(1321.11))
                        .surveyingPlots(Set.of("112/2", "112/8"))
                        .build()
                , group1);
        field3 = userFieldsManager.createFieldInGroup(Field.builder()
                        .fieldName("Kowalski przed domem")
                        .isOwnField(false)
                        .area(BigDecimal.valueOf(15.11))
                        .rent(BigDecimal.valueOf(1321.11))
                        .surveyingPlots(Set.of("112", "115/1"))
                        .build()
                , group1);

        field4 = userFieldsManager.createFieldInGroup(Field.builder()
                        .fieldName("Za domem")
                        .isOwnField(true)
                        .area(BigDecimal.valueOf(45.11))
                        .propertyTax(BigDecimal.valueOf(132.11))
                        .surveyingPlots(Set.of("1121", "1151/1"))
                        .build()
                , group2);
        field5 = userFieldsManager.createFieldInGroup(Field.builder()
                        .fieldName("Przed domem")
                        .isOwnField(true)
                        .area(BigDecimal.valueOf(145.11))
                        .propertyTax(BigDecimal.valueOf(132.11))
                        .surveyingPlots(Set.of("1/1", "1/11", "1/12", "1/13", "1/21", "1/22", "1/23", "1/24", "1/25", "1/26"))
                        .build()
                , group2);
        userFieldsManager.divideFieldPart(field3.getFieldParts().stream().findFirst().orElse(FieldPart.NONE),
                FieldPart.builder()
                        .fieldPartName("Część wschodnia")
                        .area(BigDecimal.valueOf(0.5))
                        .build(),
                FieldPart.builder()
                        .fieldPartName("Część zachodnia")
                        .build()
        );
        userFieldsManager.divideFieldPart(field5.getFieldParts().stream().findFirst().orElse(FieldPart.NONE),
                FieldPart.builder()
                        .fieldPartName("Część wschodnia")
                        .area(BigDecimal.valueOf(15.11))
                        .build(),
                FieldPart.builder()
                        .fieldPartName("Część zachodnia")
                        .build()
        );
    }

    @Transactional
    protected void addDemoUserCrops() {
        Set<Field> fields = Set.of(userFieldsManager.getFieldById(field1.getId()),
                userFieldsManager.getFieldById(field2.getId()),
                userFieldsManager.getFieldById(field3.getId()),
                userFieldsManager.getFieldById(field4.getId()),
                userFieldsManager.getFieldById(field5.getId()));
        createActiveCrops(fields);
        createNotActiveCrops(fields);
    }

    private void createActiveCrops(Set<Field> fields) {
        for (Field f : fields) {
            for (FieldPart fp : f.getFieldParts()) {
                if (!fp.getIsArchived()) {
                    createRandomCropWithOperations(fp, random.nextBoolean(), 0);
                }
            }
        }
    }

    private void createNotActiveCrops(Set<Field> fields) {
        for (int i = 1; i < 5; i++) {
            for (Field f : fields) {
                for (FieldPart fp : f.getFieldParts()) {
                    if (!fp.getIsArchived()) {
                        MainCrop crop = createRandomCropWithOperations(fp, true, i);
                        cropOperationsManager.setWorkFinished(crop);
                        crop = addCropSales(crop);
                        if (crop.getCropSales().size() > 1) {
                            cropOperationsManager.setFullySold(crop);
                        }
                    }
                }
            }
        }
    }

    private MainCrop addCropSales(MainCrop crop) {
        Harvest harvest = crop.getHarvest().stream().findFirst().orElse(Harvest.NONE);
        double estimatedGrainQuantity = harvest.getQuantityPerAreaUnit().doubleValue() *
                crop.getFieldPart().getArea().doubleValue();
        double amountSoldSum = 0;
        while (amountSoldSum < 0.85 * estimatedGrainQuantity) {
            double amountSold = estimatedGrainQuantity * random.nextDouble();
            cropOperationsManager.addCropSale(crop, CropSale.builder()
                    .amountSold(BigDecimal.valueOf(amountSold))
                    .soldTo("CEFETRA")
                    .dateSold(harvest.getDateFinished().plusDays(random.nextInt(3, 100)))
                    .resourceType(harvest.getResourceType())
                    .pricePerUnit(BigDecimal.valueOf(550 * (1 + random.nextDouble())))
                    .unit("t")
                    .cropParameters(getRandomCropParameters(crop))
                    .build());
            amountSoldSum += amountSold;
        }
        return (MainCrop) cropOperationsManager.getCropById(crop.getId());
    }


    private MainCrop createRandomCropWithOperations(FieldPart fieldPart, boolean withHarvest, int yearsOffset) {
        MainCrop mainCrop = cropOperationsManager.createNewMainCrop(fieldPart, Set.of(plants.get(random.nextInt(0, plants.size() - 1))));
        LocalDate seedingDate = LocalDate.now().minusDays(random.nextInt(260, 270)).minusYears(yearsOffset);

        if (yearsOffset == 0 && random.nextBoolean()) {
            cropOperationsManager.planSeeding(mainCrop, getRandomSeeding(seedingDate, mainCrop));
        } else {
            cropOperationsManager.addSeeding(mainCrop, getRandomSeeding(seedingDate, mainCrop));
        }

        for (int i = 0; i < random.nextInt(7); i++) {
            LocalDate cultivationDate = LocalDate.now().minusDays(random.nextInt(270, 300)).minusYears(yearsOffset);
            double depth = random.nextDouble() * 40;
            if (yearsOffset == 0 && random.nextBoolean()) {
                cropOperationsManager.planCultivation(mainCrop, getRandomCultivation(depth, cultivationDate));
            } else {
                cropOperationsManager.addCultivation(mainCrop, getRandomCultivation(depth, cultivationDate));
            }
        }
        for (int i = 0; i < random.nextInt(7); i++) {
            LocalDate fertilizerApplicationDate = LocalDate.now().minusDays(random.nextInt(100, 300)).minusYears(yearsOffset);

            if (yearsOffset == 0 && random.nextBoolean()) {
                cropOperationsManager.planFertilizerApplication(mainCrop, getRandomFertilizerApplication(fertilizerApplicationDate));
            } else {
                cropOperationsManager.addFertilizerApplication(mainCrop, getRandomFertilizerApplication(fertilizerApplicationDate));
            }
        }
        for (int i = 0; i < random.nextInt(7); i++) {
            LocalDate sprayApplicationDate = LocalDate.now().minusDays(random.nextInt(100, 250)).minusYears(yearsOffset);
            if (yearsOffset == 0 && random.nextBoolean()) {
                cropOperationsManager.planSprayApplication(mainCrop, getRandomSprayApplication(sprayApplicationDate));
            } else {
                cropOperationsManager.addSprayApplication(mainCrop, getRandomSprayApplication(sprayApplicationDate));
            }
        }
        if (withHarvest) {
            LocalDate harvestDate = LocalDate.now().minusDays(random.nextInt(5, 50)).minusYears(yearsOffset);
            if (yearsOffset == 0 && random.nextBoolean()) {
                cropOperationsManager.planHarvest(mainCrop, getRandomHarvest(mainCrop, harvestDate));
            } else {
                cropOperationsManager.addHarvest(mainCrop, getRandomHarvest(mainCrop, harvestDate));
            }
        }
        return (MainCrop) cropOperationsManager.getCropById(mainCrop.getId());
    }

    private Harvest getRandomHarvest(MainCrop mainCrop, LocalDate harvestDate) {
        return Harvest.builder()
                .farmingMachine(farmingMachines.stream()
                        .filter(machine -> machine.getSupportedOperationTypes().contains(OperationType.HARVEST))
                        .findFirst().orElse(FarmingMachine.UNDEFINED))
                .quantityPerAreaUnit(BigDecimal.valueOf(random.nextDouble() * 15))
                .cropParameters(getRandomCropParameters(mainCrop))
                .fuelConsumptionPerUnit(BigDecimal.valueOf(random.nextDouble() * 21))
                .fuelPrice(BigDecimal.valueOf(5.17))
                .dateStarted(harvestDate)
                .dateFinished(harvestDate.plusDays(1))
                .build();
    }

    private SprayApplication getRandomSprayApplication(LocalDate sprayApplicationDate) {
        return SprayApplication.builder()
                .farmingMachine(farmingMachines.stream()
                        .filter(machine -> machine.getSupportedOperationTypes().contains(OperationType.SPRAY_APPLICATION))
                        .findFirst().orElse(FarmingMachine.UNDEFINED))
                .fertilizer(fertilizers.get(random.nextInt(fertilizers.size() - 1)))
                .spray(sprays.get(random.nextInt(sprays.size() - 1)))
                .quantityPerAreaUnit(BigDecimal.valueOf(random.nextDouble()))
                .pricePerUnit(BigDecimal.valueOf(random.nextDouble() * 2000))
                .fertilizerQuantityPerAreaUnit(BigDecimal.valueOf(random.nextDouble()))
                .fertilizerPricePerUnit(BigDecimal.valueOf(random.nextDouble() * 2000))
                .fuelConsumptionPerUnit(BigDecimal.valueOf(random.nextDouble() * 4))
                .fuelPrice(BigDecimal.valueOf(5.17))
                .dateStarted(sprayApplicationDate)
                .dateFinished(sprayApplicationDate.plusDays(1))
                .build();
    }

    private FertilizerApplication getRandomFertilizerApplication(LocalDate fertilizerApplicationDate) {
        return FertilizerApplication.builder()
                .farmingMachine(farmingMachines.stream()
                        .filter(machine -> machine.getSupportedOperationTypes().contains(OperationType.FERTILIZER_APPLICATION))
                        .findFirst().orElse(FarmingMachine.UNDEFINED))
                .fertilizer(fertilizers.get(random.nextInt(fertilizers.size() - 1)))
                .quantityPerAreaUnit(BigDecimal.valueOf(random.nextDouble()))
                .pricePerUnit(BigDecimal.valueOf(random.nextInt(100, 2000)))
                .fuelConsumptionPerUnit(BigDecimal.valueOf(random.nextDouble() * 5))
                .fuelPrice(BigDecimal.valueOf(5.17))
                .dateStarted(fertilizerApplicationDate)
                .dateFinished(fertilizerApplicationDate.plusDays(1))
                .build();
    }

    private Cultivation getRandomCultivation(double depth, LocalDate cultivationDate) {
        return Cultivation.builder()
                .farmingMachine(farmingMachines.stream()
                        .filter(machine -> machine.getSupportedOperationTypes().contains(OperationType.CULTIVATION))
                        .findFirst().orElse(FarmingMachine.UNDEFINED))
                .depth(BigDecimal.valueOf(depth))
                .cultivationType(cultivationTypeAccordingToDepth(depth))
                .fuelConsumptionPerUnit(BigDecimal.valueOf(random.nextDouble() * 30))
                .fuelPrice(BigDecimal.valueOf(5.17))
                .dateStarted(cultivationDate)
                .dateFinished(cultivationDate.plusDays(1))
                .build();
    }

    private Seeding getRandomSeeding(LocalDate seedingDate, Crop crop) {
        return Seeding.builder()
                .sownPlants(Set.copyOf(crop.getCultivatedPlants()))
                .farmingMachine(farmingMachines.stream()
                        .filter(machine -> machine.getSupportedOperationTypes().contains(OperationType.SEEDING))
                        .findFirst().orElse(FarmingMachine.UNDEFINED))
                .quantityPerAreaUnit(BigDecimal.valueOf(random.nextDouble() * 100))
                .thousandSeedsMass(BigDecimal.valueOf(random.nextDouble() * 50))
                .depth(BigDecimal.valueOf(random.nextDouble() * 10))
                .seedsCostPerUnit(BigDecimal.valueOf(random.nextDouble() * 2000))
                .fuelConsumptionPerUnit(BigDecimal.valueOf(random.nextDouble() * 30))
                .fuelPrice(BigDecimal.valueOf(5.17))
                .dateStarted(seedingDate)
                .dateFinished(seedingDate.plusDays(1))
                .build();
    }

    private CultivationType cultivationTypeAccordingToDepth(double depth) {
        if (depth < 1) return CultivationType.MULCHING;
        if (depth < 3) return CultivationType.VERY_SHALLOW;
        if (depth < 7) return CultivationType.SHALLOW;
        if (depth < 23) return CultivationType.PLOWING;
        if (depth < 30) return CultivationType.DEEP_NO_TILL;
        return CultivationType.DEEP_LOOSENING;
    }

    private CropParameters getRandomCropParameters(MainCrop mainCrop) {
        if (mainCrop.getCultivatedPlants().stream().findFirst().orElse(Plant.NONE).getSpecies().getName().equals("Rape seed")) {
            return cropParametersManager.createCropParameters(RapeSeedParameters.builder()
                    .name("Parameters" + String.valueOf(random.nextDouble()).substring(0, 10))
                    .density(BigDecimal.valueOf(600 + random.nextInt(100)))
                    .humidity(BigDecimal.valueOf(5 + random.nextDouble() * 5))
                    .resourceType(ResourceType.GRAIN)
                    .build());
        }
        return cropParametersManager.createCropParameters(GrainParameters.builder()
                .name("Parameters" + String.valueOf(random.nextDouble()).substring(0, 10))
                .density(BigDecimal.valueOf(700 + random.nextInt(100)))
                .humidity(BigDecimal.valueOf(14 + random.nextDouble() * 5))
                .glutenContent(BigDecimal.valueOf(20 + random.nextDouble() * 20))
                .proteinContent(BigDecimal.valueOf(20 + random.nextDouble() * 15))
                .resourceType(ResourceType.GRAIN)
                .build());
    }


}
