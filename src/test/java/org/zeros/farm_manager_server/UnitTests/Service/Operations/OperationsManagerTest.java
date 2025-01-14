package org.zeros.farm_manager_server.UnitTests.Service.Operations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfigurationForServiceTest;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Data.PlantDTO;
import org.zeros.farm_manager_server.Domain.DTO.Data.SubsideDTO;
import org.zeros.farm_manager_server.Domain.DTO.Operations.SeedingDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Data.PlantManager;
import org.zeros.farm_manager_server.Services.Interface.Data.SubsideManager;
import org.zeros.farm_manager_server.Services.Interface.Operations.AgriculturalOperationsManager;

import java.math.BigDecimal;
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
public class OperationsManagerTest {

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
    @Autowired
    private AgriculturalOperationsManager agriculturalOperationsManager;


    private FieldPart fieldPart;
    private Plant plant1;
    private Plant plant2;
    private Species plant1Species;
    @Autowired
    private TestEntityManager testEntityManager;


    @BeforeEach
    @Transactional
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
    void testPlanSeeding() {
        CropDTO crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        SeedingDTO seedingSaved = saveNewSeeding(crop, true);
        crop = cropManager.getCropById(crop.getId());
        assertThat(seedingSaved).isNotNull();
        assertThat(seedingSaved.getId()).isNotNull();
        assertThat(seedingSaved.getIsPlannedOperation()).isTrue();
        assertThat(seedingSaved.getOperationType()).isEqualTo(OperationType.SEEDING);
        assertThat(seedingSaved.getCrop()).isEqualTo(crop.getId());
        assertThat(crop.getSeeding()).contains(seedingSaved.getId());
    }

    private SeedingDTO saveNewSeeding(CropDTO crop, boolean planned) {
        SeedingDTO seedingDTO = SeedingDTO.builder()
                .sownPlants(crop.getCultivatedPlants())
                .depth(BigDecimal.valueOf(10))
                .quantityPerAreaUnit(BigDecimal.valueOf(120))
                .build();
        if (planned) {
            return (SeedingDTO) agriculturalOperationsManager.planOperation(crop.getId(), seedingDTO);
        }
        return (SeedingDTO) agriculturalOperationsManager.addOperation(crop.getId(), seedingDTO);

    }

    @Test
    void testAddSeedingNew() {
        CropDTO crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        SeedingDTO seedingSaved = saveNewSeeding(crop, false);
        crop = cropManager.getCropById(crop.getId());
        assertThat(seedingSaved).isNotNull();
        assertThat(seedingSaved.getId()).isNotNull();
        assertThat(seedingSaved.getIsPlannedOperation()).isFalse();
        assertThat(seedingSaved.getOperationType()).isEqualTo(OperationType.SEEDING);
        assertThat(seedingSaved.getCrop()).isEqualTo(crop.getId());
        assertThat(crop.getSeeding()).contains(seedingSaved.getId());
    }

    @Test
    void testAddSeedingPlanned() {
        CropDTO crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        SeedingDTO seedingSaved = saveNewSeeding(crop, false);
        crop = cropManager.getCropById(crop.getId());
        agriculturalOperationsManager.setPlannedOperationPerformed(seedingSaved.getId(), OperationType.SEEDING);
        seedingSaved = (SeedingDTO) agriculturalOperationsManager.getOperationById(seedingSaved.getId(), OperationType.SEEDING);
        testEntityManager.flush();
        crop = cropManager.getCropById(crop.getId());
        assertThat(seedingSaved).isNotNull();
        assertThat(seedingSaved.getId()).isNotNull();
        assertThat(seedingSaved.getIsPlannedOperation()).isFalse();
        assertThat(seedingSaved.getOperationType()).isEqualTo(OperationType.SEEDING);
        assertThat(seedingSaved.getCrop()).isEqualTo(crop.getId());
        assertThat(crop.getSeeding()).contains(seedingSaved.getId());

    }

    @Test
    void testUpdateSeeding() {
        CropDTO crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        SeedingDTO seedingDTO = saveNewSeeding(crop, false);

        seedingDTO.setGerminationRate(BigDecimal.valueOf(0.95f));
        seedingDTO.setRowSpacing(BigDecimal.valueOf(30));
        agriculturalOperationsManager.updateOperationParameters(seedingDTO);
        SeedingDTO seedingSaved = (SeedingDTO) agriculturalOperationsManager.getOperationById(seedingDTO.getId(), OperationType.SEEDING);

        assertThat(seedingSaved.getIsPlannedOperation()).isFalse();
        assertThat(seedingSaved.getGerminationRate()).isEqualTo(BigDecimal.valueOf(0.95));
        assertThat(seedingSaved.getRowSpacing().floatValue()).isEqualTo(30);

    }

    @Test
    void testDeleteSeeding() {
        CropDTO crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        SeedingDTO seedingSaved = saveNewSeeding(crop, false);
        crop = cropManager.getCropById(crop.getId());
        assertThat(seedingSaved).isNotNull();
        assertThat(seedingSaved.getId()).isNotNull();

        agriculturalOperationsManager.deleteOperation(seedingSaved.getId(), OperationType.SEEDING);
        testEntityManager.flush();
        crop = cropManager.getCropById(crop.getId());
        assertThrows(IllegalArgumentException.class, () -> agriculturalOperationsManager.getOperationById(seedingSaved.getId(), OperationType.SEEDING));


        assertThat(crop.getSeeding().contains(seedingSaved.getId())).isFalse();
    }

    @Test
    void testAddSubside() {
        CropDTO crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
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

}
