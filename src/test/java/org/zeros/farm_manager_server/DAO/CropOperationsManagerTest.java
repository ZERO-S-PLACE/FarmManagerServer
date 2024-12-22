package org.zeros.farm_manager_server.DAO;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.DAO.DefaultImpl.*;
import org.zeros.farm_manager_server.DAO.Interface.*;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.OperationType;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.ResourceType;
import org.zeros.farm_manager_server.entities.Crops.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crops.Crop.MainCrop;
import org.zeros.farm_manager_server.entities.Crops.CropParameters.GrainParameters;
import org.zeros.farm_manager_server.entities.Crops.CropSale;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.entities.Crops.Subside;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.fields.Field;
import org.zeros.farm_manager_server.entities.fields.FieldPart;
import org.zeros.farm_manager_server.repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.repositories.Data.FertilizerRepository;
import org.zeros.farm_manager_server.repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.repositories.Fields.FieldRepository;

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

//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
    private User user;
    private Field field;
    private FieldPart fieldPart;
    private Plant plant1;
    private Plant plant2;
    @Autowired
    private CropRepository cropRepository;
    @Autowired
    private SubsideManager subsideManager;
    @Autowired
    private FertilizerRepository fertilizerRepository;
    @Autowired
    private FertilizerManager fertilizerManager;
    @Autowired
    private CropParametersManager cropParametersManager;


    @BeforeEach
    public void setUp() {

        user = userManager.logInNewUserByUsernameAndPassword("TestUser1", "password");
        field = user.getFields().stream().findFirst().orElse(Field.NONE);
        fieldPart = field.getFieldParts().stream().findFirst().orElse(FieldPart.NONE);
        ArrayList<Plant> plants = plantManager.getDefaultPlants(0).stream().collect(Collectors.toCollection(ArrayList::new));
        plant1 = plants.getFirst();
        plant2 = plants.getLast();
    }


    @Test
    void testCreateMainCrop() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart, Set.of(plant1, plant2));
        assertThat(crop).isNotNull();
        assertThat(crop.getId()).isNotNull();
        assertThat(crop.getCreatedDate()).isNotNull();
        assertThat(crop.getLastModifiedDate()).isNotNull();
        assertThat(crop.getCultivatedPlants()).isEqualTo(Set.of(plant1, plant2));
        assertThat(crop.getFieldPart()).isEqualTo(fieldPart);

    }

    @Test
    void testCreateInterCrop() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart, Set.of(plant1, plant2));
        assertThat(crop).isNotNull();
        assertThat(crop.getId()).isNotNull();
        assertThat(crop.getCreatedDate()).isNotNull();
        assertThat(crop.getLastModifiedDate()).isNotNull();
        assertThat(crop.getCultivatedPlants()).isEqualTo(Set.of(plant1, plant2));
        assertThat(userFieldsManager.getFieldPartById(fieldPart.getId())).isEqualTo(crop.getFieldPart());
    }

    @Test
    void testPlanSeeding() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart, Set.of(plant1, plant2));
        Seeding seeding = Seeding.builder().depth(BigDecimal.valueOf(10)).quantityPerAreaUnit(BigDecimal.valueOf(120)).build();

        Seeding seedingSaved = cropOperationsManager.planSeeding(crop, seeding);
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

    @Test
    void testAddSeedingNew() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart, Set.of(plant1, plant2));
        Seeding seeding = Seeding.builder().depth(BigDecimal.valueOf(10)).quantityPerAreaUnit(BigDecimal.valueOf(120)).build();

        Seeding seedingSaved = cropOperationsManager.addSeeding(crop, seeding);
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
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart, Set.of(plant1, plant2));
        Seeding seeding = Seeding.builder().depth(BigDecimal.valueOf(10)).quantityPerAreaUnit(BigDecimal.valueOf(120)).build();

        cropOperationsManager.planSeeding(crop, seeding);
        Seeding seedingSaved = (Seeding) cropOperationsManager.commitPlannedOperation(seeding);
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
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart, Set.of(plant1, plant2));
        Seeding seeding = Seeding.builder().depth(BigDecimal.valueOf(10)).quantityPerAreaUnit(BigDecimal.valueOf(120)).build();

        cropOperationsManager.addSeeding(crop, seeding);
        entityManager.detach(seeding);
        seeding.setGerminationRate(BigDecimal.ONE);
        seeding.setRowSpacing(BigDecimal.valueOf(30));
        Seeding seedingSaved = (Seeding) cropOperationsManager.updateOperationParameters(seeding);
        crop = cropOperationsManager.getCropById(crop.getId());
        assertThat(seedingSaved.getIsPlannedOperation()).isFalse();
        assertThat(seedingSaved.getGerminationRate()).isEqualTo(BigDecimal.ONE);
        assertThat(seedingSaved.getRowSpacing()).isEqualTo(BigDecimal.valueOf(30));

    }

    @Test
    void testDeleteSeeding() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart, Set.of(plant1, plant2));
        Seeding seeding = Seeding.builder().depth(BigDecimal.valueOf(10)).quantityPerAreaUnit(BigDecimal.valueOf(120)).build();

        Seeding seedingSaved = cropOperationsManager.addSeeding(crop, seeding);
        assertThat(seedingSaved).isNotNull();
        assertThat(seedingSaved.getId()).isNotNull();

        cropOperationsManager.deleteSeeding(seedingSaved);

        Crop crop1 = cropOperationsManager.getCropById(crop.getId());
        Seeding seeding1 = cropOperationsManager.getSeedingById(seedingSaved.getId());
        assertThat(seeding1).isNotNull();
        assertThat(seeding1).isEqualTo(Seeding.NONE);
        assertThat(crop1.getSeeding().contains(seedingSaved)).isFalse();
    }

    @Test
    void testAddSubside() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart, Set.of(plant1, plant2));
        ArrayList<Subside> subsides = subsideManager.getAllSubsides(0).stream().collect(Collectors.toCollection(ArrayList::new));
        cropOperationsManager.addSubside(crop, subsides.get(0));
        cropOperationsManager.addSubside(crop, subsides.get(1));
        cropOperationsManager.addSubside(crop, subsides.get(1));
        cropOperationsManager.addSubside(crop, subsides.get(1));
        crop = cropOperationsManager.getCropById(crop.getId());
        assertThat(crop.getSubsides()).contains(subsides.get(0));
        assertThat(crop.getSubsides()).contains(subsides.get(1));
        assertThat(crop.getSubsides().size()).isEqualTo(2);
        assertThrows(IllegalAccessError.class, () -> subsideManager.deleteSubsideSafe(subsides.get(0)));

    }

    @Test
    void testRemoveSubside() {
        Crop crop = cropOperationsManager.createNewMainCrop(fieldPart, Set.of(plant1, plant2));
        ArrayList<Subside> subsides = subsideManager.getAllSubsides(0).stream().collect(Collectors.toCollection(ArrayList::new));
        Subside subside = subsideManager.addSubside(Subside.builder().speciesAllowed(Set.of(speciesManager.getSpeciesByNameAs("ANY", 0).stream().findFirst().orElse(Species.NONE))).name("TEST_SUBSIDE").yearOfSubside(LocalDate.now()).build());

        cropOperationsManager.addSubside(crop, subsides.get(0));
        cropOperationsManager.addSubside(crop, subsides.get(1));
        cropOperationsManager.addSubside(crop, subside);
        cropOperationsManager.removeSubside(crop, subsides.get(0));
        crop = cropOperationsManager.getCropById(crop.getId());
        assertThat(crop.getSubsides()).contains(subsides.get(1));
        assertThat(crop.getSubsides()).contains(subsideManager.getSubsideById(subside.getId()));
        assertThat(crop.getSubsides().contains(subsides.get(0))).isFalse();
        assertThat(crop.getSubsides().size()).isEqualTo(2);
        cropOperationsManager.removeSubside(crop, subside);
        crop = cropOperationsManager.getCropById(crop.getId());
        assertThat(crop.getSubsides()).contains(subsides.get(1));
        assertThat(crop.getSubsides().contains(subside)).isFalse();
        assertThat(crop.getSubsides().contains(subsides.get(0))).isFalse();
        List<Crop> cropsWithSubside = cropRepository.findAllBySubsidesContains(subside);
        assertThat(cropsWithSubside).isEmpty();
        subsideManager.deleteSubsideSafe(subside);
        assertThat(subsideManager.getSubsideById(subside.getId())).isEqualTo(Subside.NONE);
    }


    @Test
    void testCreationOfCropWithProperties() {
        MainCrop crop = cropOperationsManager.createNewMainCrop(fieldPart, Set.of(plant1, plant2));

        Cultivation cultivation = cropOperationsManager.addCultivation(crop, Cultivation.builder().dateStarted(LocalDate.now()).dateFinished(LocalDate.now().plusDays(1)).depth(BigDecimal.valueOf(10)).farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(OperationType.CULTIVATION, 0).stream().findFirst().orElse(FarmingMachine.UNDEFINED)).build());
        cropOperationsManager.addCultivation(crop, cultivation);

        Seeding seeding = cropOperationsManager.addSeeding(crop, Seeding.builder().dateStarted(LocalDate.now()).dateFinished(LocalDate.now().plusDays(1)).depth(BigDecimal.valueOf(10)).farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(OperationType.SEEDING, 0).stream().findFirst().orElse(FarmingMachine.UNDEFINED)).build());
        cropOperationsManager.addSeeding(crop, seeding);

        FertilizerApplication fertilizerApplication = cropOperationsManager.addFertilizerApplication(crop, FertilizerApplication.builder().dateStarted(LocalDate.now()).dateFinished(LocalDate.now().plusDays(1)).quantityPerAreaUnit(BigDecimal.valueOf(100)).fertilizer(fertilizerManager.getDefaultFertilizers(0).stream().findFirst().orElse(Fertilizer.NONE)).farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(OperationType.FERTILIZER_APPLICATION, 0).stream().findFirst().orElse(FarmingMachine.UNDEFINED)).build());
        cropOperationsManager.addFertilizerApplication(crop, fertilizerApplication);

        SprayApplication sprayApplication = cropOperationsManager.addSprayApplication(crop, SprayApplication.builder().dateStarted(LocalDate.now()).dateFinished(LocalDate.now().plusDays(1)).fertilizer(Fertilizer.NONE).quantityPerAreaUnit(BigDecimal.valueOf(0.5)).spray(sprayManager.getAllSprays(0).stream().findFirst().orElse(Spray.NONE)).farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(OperationType.SPRAY_APPLICATION, 0).stream().findFirst().orElse(FarmingMachine.UNDEFINED)).build());
        cropOperationsManager.addSprayApplication(crop, sprayApplication);

        Harvest harvest = cropOperationsManager.addHarvest(crop, Harvest.builder().dateStarted(LocalDate.now()).resourceType(ResourceType.GRAIN).dateFinished(LocalDate.now().plusDays(1)).quantityPerAreaUnit(BigDecimal.valueOf(10)).farmingMachine(farmingMachineManager.getFarmingMachineBySupportedOperation(OperationType.HARVEST, 0).stream().findFirst().orElse(FarmingMachine.UNDEFINED)).cropParameters(cropParametersManager.getUndefinedCropParameters()).build());
        cropOperationsManager.addHarvest(crop, harvest);

        cropOperationsManager.addSubside(crop, subsideManager.getDefaultSubsides(0).stream().findFirst().orElse(Subside.NONE));
        crop = (MainCrop) cropOperationsManager.getCropById(crop.getId());
        cropOperationsManager.addCropSale(crop,
                CropSale.builder()
                        .resourceType(ResourceType.GRAIN)
                        .amountSold(BigDecimal.valueOf(100))
                        .dateSold(LocalDate.now()
                                .minusDays(111))
                        .soldTo("CEFETRA")
                        .cropParameters(cropParametersManager
                                .createCropParameters(GrainParameters.builder()
                                        .density(BigDecimal.valueOf(800))
                                        .humidity(BigDecimal.valueOf(10))
                                        .name("cefetra 11.01.2024")
                                        .resourceType(ResourceType.GRAIN)
                                        .build())).build());

        crop = (MainCrop) cropOperationsManager.getCropById(crop.getId());

        assertThat(crop).isNotNull();
        assertThat(crop.getSeeding().size()).isEqualTo(1);
        assertThat(crop.getCultivations().size()).isEqualTo(1);
        assertThat(crop.getSprayApplications().size()).isEqualTo(1);
        assertThat(crop.getFertilizerApplications().size()).isEqualTo(1);
        assertThat(crop.getHarvest().size()).isEqualTo(1);
        assertThat(crop.getCropSales().size()).isEqualTo(1);
        assertThat(crop.getSeeding().size()).isEqualTo(1);
        assertThat(crop.getSubsides().size()).isEqualTo(1);
    }


}
