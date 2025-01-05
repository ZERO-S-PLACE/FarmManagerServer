package org.zeros.farm_manager_server.IntegrationTests;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.DTO.CropParameters.GrainParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Data.Plant;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Services.Default.Crop.CropSaleManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.CropParameters.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Data.*;
import org.zeros.farm_manager_server.Services.Interface.Operations.AgriculturalOperationsManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Profile("local")
class FarmManagerServerApplicationTests {
    @Autowired
    UserManager userManager;
    @Autowired
    CropManager cropManager;
    @Autowired
    PlantManager plantManager;
    @Autowired
    CropParametersManager cropParametersManager;
    @Autowired
    AgriculturalOperationsManager agriculturalOperationsManager;
    @Autowired
    FertilizerManager fertilizerManager;
    @Autowired
    SprayManager sprayManager;
    @Autowired
    SubsideManager subsideManager;

    @Autowired
    private FarmingMachineManager farmingMachineManager;
    private FieldPart fieldPart;
    private Plant plant1;
    private Plant plant2;
    @Autowired
    private CropSaleManagerDefault cropSaleManagerDefault;

    @BeforeEach
    public void setUp() {

        User user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
        Field field = user.getFields().stream().findFirst().orElse(Field.NONE);
        fieldPart = field.getFieldParts().stream().findFirst().orElse(FieldPart.NONE);
        ArrayList<Plant> plants = plantManager.getDefaultPlants(0).stream().collect(Collectors.toCollection(ArrayList::new));
        plant1 = plants.getFirst();
        plant2 = plants.getLast();
    }

    @Test
    @Transactional
    void testCreationOfCropWithProperties() {
        Crop crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));

        agriculturalOperationsManager.addOperation(crop.getId(),
                CultivationDTO.builder()
                        .dateStarted(LocalDate.now())
                        .dateFinished(LocalDate.now().plusDays(1))
                        .depth(10)
                        .farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(
                                OperationType.CULTIVATION, 0).getContent().getFirst().getId()).build());

        agriculturalOperationsManager.addOperation(crop.getId(), SeedingDTO.builder()
                .sownPlants(cropManager.getCropById(crop.getId()).getCultivatedPlants().stream().map(BaseEntity::getId).collect(Collectors.toSet()))
                .dateStarted(LocalDate.now())
                .dateFinished(LocalDate.now().plusDays(1))
                .depth(10)
                .quantityPerAreaUnit(40)
                .farmingMachine(farmingMachineManager
                        .getFarmingMachineBySupportedOperation(
                                OperationType.SEEDING, 0).getContent().getFirst().getId())
                .build());

        agriculturalOperationsManager.addOperation(crop.getId(),
                FertilizerApplicationDTO.builder()
                        .dateStarted(LocalDate.now())
                        .dateFinished(LocalDate.now().plusDays(1))
                        .quantityPerAreaUnit(100)
                        .fertilizer(fertilizerManager.getDefaultFertilizers(0).getContent().getFirst().getId())
                        .farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(
                                OperationType.FERTILIZER_APPLICATION, 0).getContent().getFirst().getId())
                        .build());


        agriculturalOperationsManager.addOperation(crop.getId(),
                SprayApplicationDTO.builder()
                        .dateStarted(LocalDate.now())
                        .dateFinished(LocalDate.now().plusDays(1))
                        .quantityPerAreaUnit(0.5f)
                        .spray(sprayManager.getAllSprays(0).getContent().getFirst().getId())
                        .fertilizer(fertilizerManager.getUndefinedFertilizer().getId())
                        .farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(
                                OperationType.SPRAY_APPLICATION, 0).getContent().getFirst().getId())
                        .build());


        agriculturalOperationsManager.addOperation(crop.getId(),
                HarvestDTO.builder()
                        .dateStarted(LocalDate.now())
                        .resourceType(ResourceType.GRAIN)
                        .dateFinished(LocalDate.now().plusDays(1))
                        .quantityPerAreaUnit(10)
                        .farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(
                                OperationType.HARVEST, 0).getContent().getFirst().getId())
                        .cropParameters(cropParametersManager.getUndefinedCropParameters().getId())
                        .build());


        cropManager.addSubside(crop.getId(),
                subsideManager.getDefaultSubsides(0).getContent().getFirst().getId());

        cropSaleManagerDefault.addCropSale(crop.getId(),
                CropSaleDTO.builder()
                        .resourceType(ResourceType.GRAIN)
                        .amountSold(100)
                        .dateSold(LocalDate.now()
                                .minusDays(111))
                        .soldTo("CEFETRA")
                        .cropParameters(cropParametersManager
                                .addCropParameters(GrainParametersDTO.builder()
                                        .density(800)
                                        .humidity(10)
                                        .name("cefetra 11.01.2024")
                                        .resourceType(ResourceType.GRAIN)
                                        .build()).getId())
                        .build());

        crop = cropManager.getCropById(crop.getId());

        assertThat(crop).isNotNull();
        assertThat(crop.getSeeding().size()).isEqualTo(1);
        assertThat(crop.getCultivations().size()).isEqualTo(1);
        assertThat(crop.getSprayApplications().size()).isEqualTo(1);
        assertThat(crop.getFertilizerApplications().size()).isEqualTo(1);
        assertThat(((MainCrop) crop).getHarvest().size()).isEqualTo(1);
        assertThat(((MainCrop) crop).getCropSales().size()).isEqualTo(1);
        assertThat(crop.getSubsides().size()).isEqualTo(1);
    }


}
