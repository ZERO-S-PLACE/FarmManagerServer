package org.zeros.farm_manager_server.UnitTests.Service.Crop;

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
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Data.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;
import org.zeros.farm_manager_server.Domain.Entities.Data.Subside;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Data.*;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

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
@Import(LoggedUserConfiguration.class)
public class CropManagerTest {

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
        Crop crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        assertThat(crop).isNotNull();
        assertThat(crop.getId()).isNotNull();
        assertThat(crop.getCreatedDate()).isNotNull();
        assertThat(crop.getLastModifiedDate()).isNotNull();
        assertThat(crop.getCultivatedPlants()).isEqualTo(Set.of(plant1, plant2));
        assertThat(crop.getFieldPart()).isEqualTo(fieldPart);

    }

    @Test
    void testCreateInterCrop() {
        Crop crop = cropManager.createNewMainCrop(fieldPart.getId(), Set.of(plant1.getId(), plant2.getId()));
        assertThat(crop).isNotNull();
        assertThat(crop.getId()).isNotNull();
        assertThat(crop.getCreatedDate()).isNotNull();
        assertThat(crop.getLastModifiedDate()).isNotNull();
        assertThat(crop.getCultivatedPlants()).isEqualTo(Set.of(plant1, plant2));
        assertThat(fieldPartManager.getFieldPartById(fieldPart.getId())).isEqualTo(crop.getFieldPart());
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
