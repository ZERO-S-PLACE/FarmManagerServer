package org.zeros.farm_manager_server.UnitTests.Service.Operations;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfigurationService;
import org.zeros.farm_manager_server.Domain.DTO.Operations.SeedingDTO;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Data.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;
import org.zeros.farm_manager_server.Domain.Entities.Data.Subside;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.Operations.Seeding;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Data.*;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;
import org.zeros.farm_manager_server.Services.Interface.Operations.AgriculturalOperationsManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

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
@Import(LoggedUserConfigurationService.class)
public class OperationsManagerTest {

    @Autowired
    FieldPartManager fieldPartManager;
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
    private CropManager cropManager;
    @Autowired
    private PlantManager plantManager;

    private FieldPart fieldPart;
    private Plant plant1;
    private Plant plant2;
    @Autowired
    private CropRepository cropRepository;
    @Autowired
    private SubsideManager subsideManager;
    @Autowired
    private AgriculturalOperationsManager agriculturalOperationsManager;


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
    void testPlanSeeding() {
        Crop crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Seeding seedingSaved = saveNewSeeding(crop, true);
        crop = cropManager.getCropById(crop.getId());
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

    private Seeding saveNewSeeding(Crop crop, boolean planned) {
        SeedingDTO seedingDTO = SeedingDTO.builder()
                .sownPlants(crop.getCultivatedPlants().stream().map(BaseEntity::getId).collect(Collectors.toSet()))
                .depth(10)
                .quantityPerAreaUnit(120)
                .build();
        if (planned) {
            return (Seeding)agriculturalOperationsManager.planOperation(crop.getId(), seedingDTO);
        }
        return (Seeding) agriculturalOperationsManager.addOperation(crop.getId(), seedingDTO);

    }

    @Test
    void testAddSeedingNew() {
        Crop crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Seeding seedingSaved = saveNewSeeding(crop, false);
        crop = cropManager.getCropById(crop.getId());
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
        Crop crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Seeding seedingSaved = saveNewSeeding(crop, false);
        crop = cropManager.getCropById(crop.getId());
        agriculturalOperationsManager.setPlannedOperationPerformed(seedingSaved.getId(), OperationType.SEEDING);
        seedingSaved = (Seeding) agriculturalOperationsManager.getOperationById(seedingSaved.getId(), OperationType.SEEDING);
        crop = cropManager.getCropById(crop.getId());
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
        Crop crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Seeding seedingSaved = saveNewSeeding(crop, false);

        SeedingDTO seedingDTO = DefaultMappers.seedingMapper.entityToDto(seedingSaved);
        seedingDTO.setGerminationRate(0.95f);
        seedingDTO.setRowSpacing(30);

        agriculturalOperationsManager.updateOperationParameters(seedingDTO);
        seedingSaved = (Seeding) agriculturalOperationsManager.getOperationById(seedingSaved.getId(), OperationType.SEEDING);

        assertThat(seedingSaved.getIsPlannedOperation()).isFalse();
        assertThat(seedingSaved.getGerminationRate()).isEqualTo(BigDecimal.valueOf(0.95));
        assertThat(seedingSaved.getRowSpacing().floatValue()).isEqualTo(30);

    }

    @Test
    void testDeleteSeeding() {
        Crop crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Seeding seedingSaved = saveNewSeeding(crop, false);
        crop = cropManager.getCropById(crop.getId());
        assertThat(seedingSaved).isNotNull();
        assertThat(seedingSaved.getId()).isNotNull();

        agriculturalOperationsManager.deleteOperation(seedingSaved.getId(), OperationType.SEEDING);

        crop = cropManager.getCropById(crop.getId());
        Seeding seeding1 = (Seeding) agriculturalOperationsManager.getOperationById(seedingSaved.getId(), OperationType.SEEDING);

        assertThat(seeding1).isNotNull();
        assertThat(seeding1).isEqualTo(Seeding.NONE);
        assertThat(crop.getSeeding().contains(seedingSaved)).isFalse();
    }

    @Test
    void testAddSubside() {
        Crop crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Species species = crop.getCultivatedPlants().stream().findFirst().get().getSpecies();
        List<Subside> subsides = subsideManager.getAllSubsides(0).stream()
                .filter(subside -> subside.getSpeciesAllowed().contains(species)).toList();
        cropManager.addSubside(crop.getId(), subsides.get(0).getId());
        cropManager.addSubside(crop.getId(), subsides.get(1).getId());
        cropManager.addSubside(crop.getId(), subsides.get(1).getId());
        cropManager.addSubside(crop.getId(), subsides.get(1).getId());
        crop = cropManager.getCropById(crop.getId());
        assertThat(crop.getSubsides()).contains(subsides.get(0));
        assertThat(crop.getSubsides()).contains(subsides.get(1));
        assertThat(crop.getSubsides().size()).isEqualTo(2);
        assertThrows(IllegalAccessError.class, () -> subsideManager.deleteSubsideSafe(subsides.getFirst().getId()));

    }

    @Test
    void testRemoveSubside() {
        Crop crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        Species species = crop.getCultivatedPlants().stream().findFirst().get().getSpecies();
        List<Subside> subsides = subsideManager.getAllSubsides(0).stream()
                .filter(subside -> subside.getSpeciesAllowed().contains(species)).toList();
        Subside subside = subsides.getFirst();
        cropManager.addSubside(crop.getId(), subside.getId());
        crop = cropManager.getCropById(crop.getId());
        assertThat(crop.getSubsides()).contains(subside);
        cropManager.removeSubside(crop.getId(), subside.getId());
        crop = cropManager.getCropById(crop.getId());
        assertThat(crop.getSubsides().contains(subside)).isFalse();
        List<Crop> cropsWithSubside = cropRepository.findAllBySubsidesContains(subside);
        assertThat(cropsWithSubside).isEmpty();

    }

}
