package org.zeros.farm_manager_server.Service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.GrainParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Subside;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Services.Default.CropOperationsManagerDefault;
import org.zeros.farm_manager_server.Services.Default.CropParametersManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Data.*;
import org.zeros.farm_manager_server.Services.Default.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.CropOperationsManager;
import org.zeros.farm_manager_server.Services.Interface.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Data.*;
import org.zeros.farm_manager_server.Services.Interface.UserFieldsManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

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
@Import({UserFieldsManagerDefault.class, PlantManagerDefault.class, SpeciesManagerDefault.class, SprayManagerDefault.class, FertilizerManagerDefault.class, FarmingMachineManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class, CropOperationsManagerDefault.class, CropParametersManagerDefault.class, SubsideManagerDefault.class})
public class CropOperationsManagerTest {

    @Autowired
    UserFieldsManager userFieldsManager;
    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    FieldGroupRepository fieldGroupRepositoryRepository;
    @Autowired
    FieldPartRepository fieldPartRepository;
    @Autowired
    UserManager userManager;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    EntityManager entityManager;
    @Autowired
    private CropOperationsManager cropOperationsManager;
    @Autowired
    private PlantManager plantManager;
    @Autowired
    private SpeciesManager speciesManager;
    @Autowired
    private SprayManager sprayManager;
    @Autowired
    private FarmingMachineManager farmingMachineManager;
    private FieldPart fieldPart;
    private Plant plant1;
    private Plant plant2;
    @Autowired
    private CropRepository cropRepository;
    @Autowired
    private SubsideManager subsideManager;
    @Autowired
    private FertilizerManager fertilizerManager;
    @Autowired
    private CropParametersManager cropParametersManager;


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
    void testCreateMainCrop() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        assertThat(crop).isNotNull();
        assertThat(crop.getId()).isNotNull();
        assertThat(crop.getCreatedDate()).isNotNull();
        assertThat(crop.getLastModifiedDate()).isNotNull();
        assertThat(crop.getCultivatedPlants()).isEqualTo(Set.of(plant1, plant2));
        assertThat(crop.getFieldPart()).isEqualTo(fieldPart);

    }

    @Test
    void testCreateInterCrop() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        assertThat(crop).isNotNull();
        assertThat(crop.getId()).isNotNull();
        assertThat(crop.getCreatedDate()).isNotNull();
        assertThat(crop.getLastModifiedDate()).isNotNull();
        assertThat(crop.getCultivatedPlants()).isEqualTo(Set.of(plant1, plant2));
        assertThat(userFieldsManager.getFieldPartById(fieldPart.getId())).isEqualTo(crop.getFieldPart());
    }

    @Test
    void testPlanSeeding() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Seeding seedingSaved = saveNewSeeding(crop,true);
        crop = cropOperationsManager.getCropById(crop.getId());
        assertThat(seedingSaved).isNotNull();
        assertThat(seedingSaved.getId()).isNotNull();
        assertThat(seedingSaved.getCreatedDate()).isNotNull();
        assertThat(seedingSaved.getLastModifiedDate()).isNotNull();
        assertThat(seedingSaved.getIsPlannedOperation()).isTrue();
        assertThat(seedingSaved.getOperationType()).isEqualTo(OperationType.SEEDING);
        assertThat(seedingSaved.getCrop()).isEqualTo(crop);
        assertThat(seedingSaved.getCrop().getCultivatedPlants()).isEqualTo(Set.of(plant1, plant2));
        assertThat(seedingSaved.getCrop().getSeeding()).contains(seedingSaved);
        assertThat(crop.getSeeding()).contains(seedingSaved);
    }

    private Seeding saveNewSeeding(Crop crop,boolean planned) {
        SeedingDTO seedingDTO = SeedingDTO.builder()
                .sownPlants(crop.getCultivatedPlants().stream().map(BaseEntity::getId).collect(Collectors.toSet()))
                .depth(10)
                .quantityPerAreaUnit(120)
                .build();
        if(planned){
        return cropOperationsManager.planSeeding(crop.getId(), seedingDTO);}
        return cropOperationsManager.addSeeding(crop.getId(), seedingDTO);

    }

    @Test
    void testAddSeedingNew() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Seeding seedingSaved = saveNewSeeding(crop,false);
        crop = cropOperationsManager.getCropById(crop.getId());
        assertThat(seedingSaved).isNotNull();
        assertThat(seedingSaved.getId()).isNotNull();
        assertThat(seedingSaved.getCreatedDate()).isNotNull();
        assertThat(seedingSaved.getLastModifiedDate()).isNotNull();
        assertThat(seedingSaved.getIsPlannedOperation()).isFalse();
        assertThat(seedingSaved.getOperationType()).isEqualTo(OperationType.SEEDING);
        assertThat(seedingSaved.getCrop()).isEqualTo(crop);
        assertThat(seedingSaved.getCrop().getCultivatedPlants()).isEqualTo(Set.of(plant1, plant2));
        assertThat(seedingSaved.getCrop().getSeeding()).contains(seedingSaved);
        assertThat(crop.getSeeding()).contains(seedingSaved);
    }

    @Test
    void testAddSeedingPlanned() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Seeding seedingSaved = saveNewSeeding(crop,false);
        crop = cropOperationsManager.getCropById(crop.getId());
        seedingSaved = (Seeding) cropOperationsManager.commitPlannedOperation(seedingSaved.getId(), OperationType.SEEDING);
        crop = cropOperationsManager.getCropById(crop.getId());
        assertThat(seedingSaved).isNotNull();
        assertThat(seedingSaved.getId()).isNotNull();
        assertThat(seedingSaved.getCreatedDate()).isNotNull();
        assertThat(seedingSaved.getLastModifiedDate()).isNotNull();
        assertThat(seedingSaved.getIsPlannedOperation()).isFalse();
        assertThat(seedingSaved.getOperationType()).isEqualTo(OperationType.SEEDING);
        assertThat(seedingSaved.getCrop()).isEqualTo(crop);
        assertThat(seedingSaved.getCrop().getCultivatedPlants()).isEqualTo(Set.of(plant1, plant2));
        assertThat(seedingSaved.getCrop().getSeeding()).contains(seedingSaved);
        assertThat(crop.getSeeding()).contains(seedingSaved);
    }

    @Test
    void testUpdateSeeding() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Seeding seedingSaved = saveNewSeeding(crop,false);
        crop = cropOperationsManager.getCropById(crop.getId());
        SeedingDTO seedingDTO = DefaultMappers.seedingMapper.entityToDto(seedingSaved);
        seedingDTO.setGerminationRate(0.95f);
        seedingDTO.setRowSpacing(30);
        seedingSaved = (Seeding) cropOperationsManager.updateOperationParameters(seedingDTO);
        assertThat(seedingSaved.getIsPlannedOperation()).isFalse();
        assertThat(seedingSaved.getGerminationRate()).isEqualTo(BigDecimal.valueOf(0.95));
        assertThat(seedingSaved.getRowSpacing()).isEqualTo(BigDecimal.valueOf(30));

    }

    @Test
    void testDeleteSeeding() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Seeding seedingSaved = saveNewSeeding(crop,false);
        crop = cropOperationsManager.getCropById(crop.getId());
        assertThat(seedingSaved).isNotNull();
        assertThat(seedingSaved.getId()).isNotNull();

        cropOperationsManager.deleteOperation(seedingSaved.getId(), OperationType.SEEDING);

        crop = cropOperationsManager.getCropById(crop.getId());
        Seeding seeding1 = cropOperationsManager.getSeedingById(seedingSaved.getId());
        assertThat(seeding1).isNotNull();
        assertThat(seeding1).isEqualTo(Seeding.NONE);
        assertThat(crop.getSeeding().contains(seedingSaved)).isFalse();
    }

    @Test
    void testAddSubside() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Species species = crop.getCultivatedPlants().stream().findFirst().get().getSpecies();
        List<Subside> subsides = subsideManager.getAllSubsides(0).stream()
                .filter(subside -> subside.getSpeciesAllowed().contains(species)).toList();
        cropOperationsManager.addSubside(crop.getId(), subsides.get(0).getId());
        cropOperationsManager.addSubside(crop.getId(), subsides.get(1).getId());
        cropOperationsManager.addSubside(crop.getId(), subsides.get(1).getId());
        cropOperationsManager.addSubside(crop.getId(), subsides.get(1).getId());
        crop = cropOperationsManager.getCropById(crop.getId());
        assertThat(crop.getSubsides()).contains(subsides.get(0));
        assertThat(crop.getSubsides()).contains(subsides.get(1));
        assertThat(crop.getSubsides().size()).isEqualTo(2);
        assertThrows(IllegalAccessError.class, () -> subsideManager.deleteSubsideSafe(subsides.getFirst().getId()));

    }

    @Test
    void testRemoveSubside() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Species species = crop.getCultivatedPlants().stream().findFirst().get().getSpecies();
        List<Subside> subsides = subsideManager.getAllSubsides(0).stream()
                .filter(subside -> subside.getSpeciesAllowed().contains(species)).toList();
        Subside subside = subsides.getFirst();
        cropOperationsManager.addSubside(crop.getId(), subside.getId());
        crop = cropOperationsManager.getCropById(crop.getId());
        assertThat(crop.getSubsides()).contains(subside);
        cropOperationsManager.removeSubside(crop.getId(), subside.getId());
        crop = cropOperationsManager.getCropById(crop.getId());
        assertThat(crop.getSubsides().contains(subside)).isFalse();
        List<Crop> cropsWithSubside = cropRepository.findAllBySubsidesContains(subside);
        assertThat(cropsWithSubside).isEmpty();

    }


    @Test
    void testCreationOfCropWithProperties() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));

        Cultivation cultivation = cropOperationsManager.addCultivation(crop.getId(),
                CultivationDTO.builder()
                        .dateStarted(LocalDate.now())
                        .dateFinished(LocalDate.now().plusDays(1))
                        .depth(10)
                        .farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(
                                OperationType.CULTIVATION, 0).getContent().getFirst().getId()).build());

        Seeding seeding = cropOperationsManager.addSeeding(crop.getId(), SeedingDTO.builder()
                        .sownPlants(crop.getCultivatedPlants().stream().map(BaseEntity::getId).collect(Collectors.toSet()))
                .dateStarted(LocalDate.now())
                .dateFinished(LocalDate.now().plusDays(1))
                .depth(10)
                .quantityPerAreaUnit(40)
                .farmingMachine(farmingMachineManager
                        .getFarmingMachineBySupportedOperation(
                                OperationType.SEEDING, 0).getContent().getFirst().getId())
                .build());

        FertilizerApplication fertilizerApplication = cropOperationsManager.addFertilizerApplication(crop.getId(),
                FertilizerApplicationDTO.builder()
                        .dateStarted(LocalDate.now())
                        .dateFinished(LocalDate.now().plusDays(1))
                        .quantityPerAreaUnit(100)
                        .fertilizer(fertilizerManager.getDefaultFertilizers(0).getContent().getFirst().getId())
                        .farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(
                                OperationType.FERTILIZER_APPLICATION, 0).getContent().getFirst().getId())
                        .build());


        SprayApplication sprayApplication = cropOperationsManager.addSprayApplication(crop.getId(),
                SprayApplicationDTO.builder()
                        .dateStarted(LocalDate.now())
                        .dateFinished(LocalDate.now().plusDays(1))
                        .quantityPerAreaUnit(0.5f)
                        .spray(sprayManager.getAllSprays(0).getContent().getFirst().getId())
                        .farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(
                                OperationType.SPRAY_APPLICATION, 0).getContent().getFirst().getId())
                        .build());


        Harvest harvest = cropOperationsManager.addHarvest(crop.getId(),
                HarvestDTO.builder()
                        .dateStarted(LocalDate.now())
                        .resourceType(ResourceType.GRAIN)
                        .dateFinished(LocalDate.now().plusDays(1))
                        .quantityPerAreaUnit(10)
                        .farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(
                                OperationType.HARVEST, 0).getContent().getFirst().getId())
                        .cropParameters(cropParametersManager.getUndefinedCropParameters().getId())
                        .build());


        cropOperationsManager.addSubside(crop.getId(),
                subsideManager.getDefaultSubsides(0).getContent().getFirst().getId());

        cropOperationsManager.addCropSale(crop.getId(),
                CropSaleDTO.builder()
                        .resourceType(ResourceType.GRAIN)
                        .amountSold(100)
                        .dateSold(LocalDate.now()
                                .minusDays(111))
                        .soldTo("CEFETRA")
                        .cropParameters(cropParametersManager
                                .createCropParameters(GrainParametersDTO.builder()
                                        .density(800)
                                        .humidity(10)
                                        .name("cefetra 11.01.2024")
                                        .resourceType(ResourceType.GRAIN)
                                        .build()).getId())
                        .build());

        crop = cropOperationsManager.getCropById(crop.getId());

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
