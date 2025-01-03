package org.zeros.farm_manager_server.Bootstrap;

import com.fasterxml.jackson.databind.deser.UnresolvedId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.GrainParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.RapeSeedParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.SubsideDTO;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldDTO;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldPartDTO;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.CultivationType;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Operations.Harvest;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Subside;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Services.Interface.CropOperationsManager;
import org.zeros.farm_manager_server.Services.Interface.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Data.*;
import org.zeros.farm_manager_server.Services.Interface.UserFieldsManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
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
    private final Random random = new Random();
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
        UserDTO userDTO = UserDTO.builder()
                .firstName("Demo")
                .lastName("User")
                .username("DEMO_USER")
                .password("DEMO_PASSWORD")
                .email("demo@zeros.org")
                .phoneNumber("999999999")
                .build();
        userManager.createNewUser(userDTO);

    }

    @Transactional
    protected void addDemoUserFields() {
        FieldGroup group1 = userFieldsManager.createEmptyFieldGroup("Kowary wschód", "");
        FieldGroup group2 = userFieldsManager.createEmptyFieldGroup("Kowary zachód", "");
        field1 = userFieldsManager.createFieldInGroup(FieldDTO.builder()
                        .fieldName("Koło stawu")
                        .isOwnField(true)
                        .area(12.21f)
                        .propertyTax(132.11f)
                        .surveyingPlots(Set.of("11/2", "11/3", "11/8"))
                        .build()
                , group1.getId());
        field2 = userFieldsManager.createFieldInGroup(FieldDTO.builder()
                        .fieldName("Kowalski za domem")
                        .isOwnField(false)
                        .area(1.21f)
                        .rent(1321.11f)
                        .surveyingPlots(Set.of("112/2", "112/8"))
                        .build()
                , group1.getId());
        field3 = userFieldsManager.createFieldInGroup(FieldDTO.builder()
                        .fieldName("Kowalski przed domem")
                        .isOwnField(false)
                        .area(15.11f)
                        .rent(1321.11f)
                        .surveyingPlots(Set.of("112", "115/1"))
                        .build()
                , group1.getId());

        field4 = userFieldsManager.createFieldInGroup(FieldDTO.builder()
                        .fieldName("Za domem")
                        .isOwnField(true)
                        .area(45.11f)
                        .propertyTax(132.11f)
                        .surveyingPlots(Set.of("1121", "1151/1"))
                        .build()
                , group2.getId());
        field5 = userFieldsManager.createFieldInGroup(FieldDTO.builder()
                        .fieldName("Przed domem")
                        .isOwnField(true)
                        .area(145.11f)
                        .propertyTax(132.11f)
                        .surveyingPlots(Set.of("1/1", "1/11", "1/12", "1/13", "1/21", "1/22", "1/23", "1/24", "1/25", "1/26"))
                        .build()
                , group2.getId());
        userFieldsManager.divideFieldPart(field3.getFieldParts().stream().findFirst().orElse(FieldPart.NONE).getId(),
                FieldPartDTO.builder()
                        .fieldPartName("Część wschodnia")
                        .area(0.5f)
                        .build(),
                FieldPartDTO.builder()
                        .fieldPartName("Część zachodnia")
                        .build()
        );
        userFieldsManager.divideFieldPart(field5.getFieldParts().stream().findFirst().orElse(FieldPart.NONE).getId(),
                FieldPartDTO.builder()
                        .fieldPartName("Część wschodnia")
                        .area(15.11f)
                        .build(),
                FieldPartDTO.builder()
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
                        cropOperationsManager.setWorkFinished(crop.getId());
                        crop = addCropSales(crop);
                        if (crop.getCropSales().size() > 1) {
                            cropOperationsManager.setFullySold(crop.getId());
                        }
                    }
                }
            }
        }
    }

    private MainCrop addCropSales(MainCrop crop) {
        Harvest harvest = crop.getHarvest().stream().findFirst().orElse(Harvest.NONE);
        float estimatedGrainQuantity = harvest.getQuantityPerAreaUnit().floatValue() *
                crop.getFieldPart().getArea().floatValue();
        float amountSoldSum = 0;
        while (amountSoldSum < 0.85 * estimatedGrainQuantity) {
            float amountSold = estimatedGrainQuantity * random.nextFloat();
            cropOperationsManager.addCropSale(crop.getId(), CropSaleDTO.builder()
                    .amountSold(amountSold)
                    .soldTo("CEFETRA")
                    .dateSold(harvest.getDateFinished().plusDays(random.nextInt(3, 100)))
                    .resourceType(harvest.getResourceType())
                    .pricePerUnit(550 * (1 + random.nextFloat()))
                    .unit("t")
                    .cropParameters(getRandomCropParameters(crop).getId())
                    .build());
            amountSoldSum += amountSold;
        }
        return (MainCrop) cropOperationsManager.getCropById(crop.getId());
    }


    private MainCrop createRandomCropWithOperations(FieldPart fieldPart, boolean withHarvest, int yearsOffset) {
        MainCrop mainCrop = cropOperationsManager.createNewMainCrop(fieldPart.getId(), Set.of(plants.get(random.nextInt(0, plants.size() - 1)).getId()));
        LocalDate seedingDate = LocalDate.now().minusDays(random.nextInt(260, 270)).minusYears(yearsOffset);

        if (yearsOffset == 0 && random.nextBoolean()) {
            cropOperationsManager.planSeeding(mainCrop.getId(), getRandomSeeding(seedingDate, mainCrop));
        } else {
            cropOperationsManager.addSeeding(mainCrop.getId(), getRandomSeeding(seedingDate, mainCrop));
        }

        for (int i = 0; i < random.nextInt(7); i++) {
            LocalDate cultivationDate = LocalDate.now().minusDays(random.nextInt(270, 300)).minusYears(yearsOffset);
            float depth = random.nextFloat() * 40;
            if (yearsOffset == 0 && random.nextBoolean()) {
                cropOperationsManager.planCultivation(mainCrop.getId(), getRandomCultivation(depth, cultivationDate));
            } else {
                cropOperationsManager.addCultivation(mainCrop.getId(), getRandomCultivation(depth, cultivationDate));
            }
        }
        for (int i = 0; i < random.nextInt(7); i++) {
            LocalDate fertilizerApplicationDate = LocalDate.now().minusDays(random.nextInt(100, 300)).minusYears(yearsOffset);

            if (yearsOffset == 0 && random.nextBoolean()) {
                cropOperationsManager.planFertilizerApplication(mainCrop.getId(), getRandomFertilizerApplication(fertilizerApplicationDate));
            } else {
                cropOperationsManager.addFertilizerApplication(mainCrop.getId(), getRandomFertilizerApplication(fertilizerApplicationDate));
            }
        }
        for (int i = 0; i < random.nextInt(7); i++) {
            LocalDate sprayApplicationDate = LocalDate.now().minusDays(random.nextInt(100, 250)).minusYears(yearsOffset);
            if (yearsOffset == 0 && random.nextBoolean()) {
                cropOperationsManager.planSprayApplication(mainCrop.getId(), getRandomSprayApplication(sprayApplicationDate));
            } else {
                cropOperationsManager.addSprayApplication(mainCrop.getId(), getRandomSprayApplication(sprayApplicationDate));
            }
        }
        if (withHarvest) {
            LocalDate harvestDate = LocalDate.now().minusDays(random.nextInt(5, 50)).minusYears(yearsOffset);
            if (yearsOffset == 0 && random.nextBoolean()) {
                cropOperationsManager.planHarvest(mainCrop.getId(), getRandomHarvest(mainCrop, harvestDate));
            } else {
                cropOperationsManager.addHarvest(mainCrop.getId(), getRandomHarvest(mainCrop, harvestDate));
            }
        }
        for (int i = 0; i < random.nextInt(7); i++) {
            LocalDate subsideYear = LocalDate.now().minusYears(yearsOffset);
            cropOperationsManager.addSubside(mainCrop.getId(),createRandomSubside(mainCrop.getCultivatedPlants(),subsideYear).getId());
        }
        return (MainCrop) cropOperationsManager.getCropById(mainCrop.getId());
    }

    private Subside createRandomSubside(Set<Plant> plants, LocalDate subsideYear) {
        return subsideManager.addSubside(SubsideDTO.builder()
                        .name("Subside"+Math.round(random.nextFloat()*100000))
                        .subsideValuePerAreaUnit(500*random.nextFloat())
                        .yearOfSubside(subsideYear)
                        .speciesAllowed(plants.stream().map(plant->plant.getSpecies().getId()).collect(Collectors.toSet()))
                .build());
    }

    private HarvestDTO getRandomHarvest(MainCrop mainCrop, LocalDate harvestDate) {
        return HarvestDTO.builder()
                .farmingMachine(farmingMachines.stream()
                        .filter(machine -> machine.getSupportedOperationTypes().contains(OperationType.HARVEST))
                        .findFirst().orElse(FarmingMachine.UNDEFINED).getId())
                .quantityPerAreaUnit(random.nextFloat() * 15)
                .cropParameters(getRandomCropParameters(mainCrop).getId())
                .fuelConsumptionPerUnit(random.nextFloat() * 21)
                .fuelPrice(5.17f)
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
                .quantityPerAreaUnit(random.nextFloat())
                .pricePerUnit(random.nextFloat() * 2000)
                .fertilizerQuantityPerAreaUnit(random.nextFloat())
                .fertilizerPricePerUnit(random.nextFloat() * 2000)
                .fuelConsumptionPerUnit(random.nextFloat() * 4)
                .fuelPrice(5.17f)
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
                .quantityPerAreaUnit(random.nextFloat())
                .pricePerUnit(random.nextInt(100, 2000))
                .fuelConsumptionPerUnit(random.nextFloat() * 5)
                .fuelPrice(5.17f)
                .dateStarted(fertilizerApplicationDate)
                .dateFinished(fertilizerApplicationDate.plusDays(1))
                .build();
    }

    private CultivationDTO getRandomCultivation(float depth, LocalDate cultivationDate) {
        return CultivationDTO.builder()
                .farmingMachine(farmingMachines.stream()
                        .filter(machine -> machine.getSupportedOperationTypes().contains(OperationType.CULTIVATION))
                        .findFirst().orElse(FarmingMachine.UNDEFINED).getId())
                .depth(depth)
                .cultivationType(cultivationTypeAccordingToDepth(depth))
                .fuelConsumptionPerUnit(random.nextFloat() * 30)
                .fuelPrice(5.17f)
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
                .quantityPerAreaUnit(random.nextFloat() * 100)
                .thousandSeedsMass(random.nextFloat() * 50)
                .depth(random.nextFloat() * 10)
                .seedsCostPerUnit(random.nextFloat() * 2000)
                .fuelConsumptionPerUnit(random.nextFloat() * 30)
                .fuelPrice(5.17f)
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

    private CropParameters getRandomCropParameters(MainCrop mainCrop) {
        if (mainCrop.getCultivatedPlants().stream().findFirst().orElse(Plant.NONE).getSpecies().getName().equals("Rape seed")) {
            return cropParametersManager.createCropParameters(RapeSeedParametersDTO.builder()
                    .name("Parameters" + String.valueOf(Math.round(random.nextFloat()*10000)))
                    .density(600 + random.nextInt(100))
                    .humidity(5 + random.nextFloat() * 5)
                    .resourceType(ResourceType.GRAIN)
                    .build());
        }
        return cropParametersManager.createCropParameters(GrainParametersDTO.builder()
                .name("Parameters" +  String.valueOf(Math.round(random.nextFloat()*10000)))
                .density(700 + random.nextInt(100))
                .humidity(14 + random.nextFloat() * 5)
                .glutenContent(20 + random.nextFloat() * 20)
                .proteinContent(20 + random.nextFloat() * 15)
                .resourceType(ResourceType.GRAIN)
                .build());
    }


}
