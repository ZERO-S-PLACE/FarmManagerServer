package org.zeros.farm_manager_server.UnitTests.Service.Crop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfigurationForServiceTest;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.GrainParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.MainCropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Data.PlantDTO;
import org.zeros.farm_manager_server.Domain.DTO.Data.SubsideDTO;
import org.zeros.farm_manager_server.Domain.DTO.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.Data.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropSaleManager;
import org.zeros.farm_manager_server.Services.Interface.Data.*;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;
import org.zeros.farm_manager_server.Services.Interface.Operations.AgriculturalOperationsManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan("org.zeros.farm_manager_server.Services")
@Import(LoggedUserConfigurationForServiceTest.class)
public class CropManagerTest {

    @Autowired
    FieldPartManager fieldPartManager;
    @Autowired
    AgriculturalOperationsManager agriculturalOperationsManager;
    @Autowired
    FarmingMachineManager farmingMachineManager;
    @Autowired
    FertilizerManager fertilizerManager;
    @Autowired
    SprayManager sprayManager;
    @Autowired
    CropParametersManager cropParametersManager;
    @Autowired
    CropSaleManager cropSaleManager;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private CropManager cropManager;
    @Autowired
    private PlantManager plantManager;
    @Autowired
    private SubsideManager subsideManager;
    private FieldPart fieldPart;
    private Plant plant1;
    private Plant plant2;
    private Species plant1Species;

    @BeforeEach
    public void setUp() {
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        loggedUserConfiguration.replaceUser(user);
        Field field = user.getFields().stream().findFirst().orElse(Field.NONE);
        fieldPart = field.getFieldParts().stream().findFirst().orElse(FieldPart.NONE);
        ArrayList<PlantDTO> plants = plantManager.getDefaultPlants(0).stream().collect(Collectors.toCollection(ArrayList::new));
        plant1 = plantManager.getPlantIfExists(plants.getFirst().getId());
        plant2 = plantManager.getPlantIfExists(plants.getLast().getId());
        plant1Species = plant1.getSpecies();
    }


    @Test
    void testCreateMainCrop() {
        CropDTO crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        assertThat(crop).isNotNull();
        assertThat(crop.getId()).isNotNull();
        assertThat(crop.getCultivatedPlants()).isEqualTo(Set.of(plant1.getId(), plant2.getId()));
        assertThat(crop.getFieldPart()).isEqualTo(fieldPart.getId());

    }

    @Test
    void testCreateInterCrop() {
        CropDTO crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        assertThat(crop).isNotNull();
        assertThat(crop.getId()).isNotNull();
        assertThat(crop.getCultivatedPlants()).isEqualTo(Set.of(plant1.getId(), plant2.getId()));
        assertThat(fieldPart.getId()).isEqualTo(crop.getFieldPart());
    }

    @Test
    void testAddSubside() {
        CropDTO crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId()));
        List<SubsideDTO> subsides = subsideManager.getAllSubsides(0).stream()
                .filter(subside -> subside.getSpeciesAllowed().contains(plant1Species.getId())).toList();
        cropManager.addSubside(crop.getId(), subsides.get(0).getId());
        cropManager.addSubside(crop.getId(), subsides.get(1).getId());
        cropManager.addSubside(crop.getId(), subsides.get(1).getId());
        cropManager.addSubside(crop.getId(), subsides.get(1).getId());
        crop = cropManager.getCropById(crop.getId());
        assertThat(crop.getSubsides()).contains(subsides.get(0).getId());
        assertThat(crop.getSubsides()).contains(subsides.get(1).getId());
        assertThat(crop.getSubsides().size()).isEqualTo(2);
        assertThrows(IllegalAccessError.class, () -> subsideManager.deleteSubsideSafe(subsides.getFirst().getId()));

    }

    @Test
    void testRemoveSubside() {
        CropDTO crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        List<SubsideDTO> subsides = subsideManager.getAllSubsides(0).stream()
                .filter(subside -> subside.getSpeciesAllowed().contains(plant1Species.getId())).toList();
        SubsideDTO subside = subsides.getFirst();
        cropManager.addSubside(crop.getId(), subside.getId());
        crop = cropManager.getCropById(crop.getId());
        assertThat(crop.getSubsides()).contains(subside.getId());
        cropManager.removeSubside(crop.getId(), subside.getId());
        crop = cropManager.getCropById(crop.getId());
        assertThat(crop.getSubsides().contains(subside.getId())).isFalse();
    }

    @Test
    void testCreationOfCropWithProperties() {
        MainCropDTO crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));

        agriculturalOperationsManager.addOperation(crop.getId(),
                CultivationDTO.builder()
                        .dateStarted(LocalDate.now())
                        .dateFinished(LocalDate.now().plusDays(1))
                        .depth(BigDecimal.valueOf(10))
                        .farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(
                                OperationType.CULTIVATION, 0).getContent().getFirst().getId()).build());

        agriculturalOperationsManager.addOperation(crop.getId(), SeedingDTO.builder()
                .sownPlants(cropManager.getCropById(crop.getId()).getCultivatedPlants())
                .dateStarted(LocalDate.now())
                .dateFinished(LocalDate.now().plusDays(1))
                .depth(BigDecimal.valueOf(10))
                .quantityPerAreaUnit(BigDecimal.valueOf(40))
                .farmingMachine(farmingMachineManager
                        .getFarmingMachineBySupportedOperation(
                                OperationType.SEEDING, 0).getContent().getFirst().getId())
                .build());

        agriculturalOperationsManager.addOperation(crop.getId(),
                FertilizerApplicationDTO.builder()
                        .dateStarted(LocalDate.now())
                        .dateFinished(LocalDate.now().plusDays(1))
                        .quantityPerAreaUnit(BigDecimal.valueOf(100))
                        .fertilizer(fertilizerManager.getDefaultFertilizers(0).getContent().getFirst().getId())
                        .farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(
                                OperationType.FERTILIZER_APPLICATION, 0).getContent().getFirst().getId())
                        .build());


        agriculturalOperationsManager.addOperation(crop.getId(),
                SprayApplicationDTO.builder()
                        .dateStarted(LocalDate.now())
                        .dateFinished(LocalDate.now().plusDays(1))
                        .quantityPerAreaUnit(BigDecimal.valueOf(0.5))
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
                        .quantityPerAreaUnit(BigDecimal.valueOf(10))
                        .farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(
                                OperationType.HARVEST, 0).getContent().getFirst().getId())
                        .cropParameters(cropParametersManager.getUndefinedCropParameters().getId())
                        .build());


        cropManager.addSubside(crop.getId(),
                subsideManager.getDefaultSubsides(0).getContent().getFirst().getId());

        cropSaleManager.addCropSale(crop.getId(),
                CropSaleDTO.builder()
                        .resourceType(ResourceType.GRAIN)
                        .amountSold(BigDecimal.valueOf(100))
                        .dateSold(LocalDate.now()
                                .minusDays(111))
                        .soldTo("CEFETRA")
                        .cropParameters(cropParametersManager
                                .addCropParameters(GrainParametersDTO.builder()
                                        .density(BigDecimal.valueOf(800))
                                        .humidity(BigDecimal.valueOf(10))
                                        .name("cefetra 11.01.2024")
                                        .resourceType(ResourceType.GRAIN)
                                        .build()).getId())
                        .build());

        crop = (MainCropDTO) cropManager.getCropById(crop.getId());

        assertThat(crop).isNotNull();
        assertThat(crop.getSeeding().size()).isEqualTo(1);
        assertThat(crop.getCultivations().size()).isEqualTo(1);
        assertThat(crop.getSprayApplications().size()).isEqualTo(1);
        assertThat(crop.getFertilizerApplications().size()).isEqualTo(1);
        assertThat(crop.getHarvest().size()).isEqualTo(1);
        assertThat(crop.getCropSales().size()).isEqualTo(1);
        assertThat(crop.getSubsides().size()).isEqualTo(1);
    }


}
