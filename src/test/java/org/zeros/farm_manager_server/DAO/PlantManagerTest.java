package org.zeros.farm_manager_server.DAO;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.DAO.DefaultImpl.PlantManagerDefault;
import org.zeros.farm_manager_server.DAO.DefaultImpl.SpeciesManagerDefault;
import org.zeros.farm_manager_server.DAO.DefaultImpl.UserManagerDefault;
import org.zeros.farm_manager_server.DAO.Interface.PlantManager;
import org.zeros.farm_manager_server.DAO.Interface.SpeciesManager;
import org.zeros.farm_manager_server.DAO.Interface.UserManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.repositories.Data.PlantRepository;
import org.zeros.farm_manager_server.repositories.Data.SpeciesRepository;

import java.rmi.NoSuchObjectException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@Import({PlantManagerDefault.class, SpeciesManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PlantManagerTest {
    @Autowired
    UserManager userManager;
    @Autowired
    PlantRepository plantRepository;
    @Autowired
    SpeciesRepository speciesRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    EntityManager entityManager;
    private User user;
    @Autowired
    private PlantManager plantManager;
    @Autowired
    private SpeciesManager speciesManager;

    @BeforeEach
    public void setUp() {
        user = userManager.logInNewUserByUsernameAndPassword("TestUser1", "password");
    }

    @Test
    void testCreatePlant() {
        Species species=speciesManager.getSpeciesByNameAs("Wheat",0).stream().findFirst().get();
        Plant plant= plantManager.addPlant(Plant.builder()
                .variety("test1")
                .species(species)
                .build());
        assertThat(plant.getId()).isNotNull();
        assertThat(plant.getVariety()).isEqualTo("test1");
        assertThat(plant.getSpecies()).isEqualTo(species);
        assertThat(plant.getCreatedBy()).isEqualTo(user.getUsername());
        assertThat(plantRepository.findById(plant.getId()).get()).isEqualTo(plant);
    }

    @Test
    void testGetAllPlants() {
        Species species=speciesManager.getSpeciesByNameAs("Wheat",0).stream().findFirst().get();
        Plant plant= plantManager.addPlant(Plant.builder()
                .variety("test1")
                .species(species)
                .build());
        Page<Plant> plants= plantManager.getAllPlants(0);
       assertThat(plants.getTotalElements()).isEqualTo(6);
    }

    @Test
    void testGetDefaultPlants() {
        Page<Plant> plants=plantManager.getDefaultPlants(0);
        assertThat(plants.getTotalElements()).isEqualTo(5);
    }
    @Test
    void testGetUserPlants() {
        Species species=speciesManager.getSpeciesByNameAs("Wheat",0).stream().findFirst().get();
        Plant plant= plantManager.addPlant(Plant.builder()
                .variety("test1")
                .species(species)
                .build());
        Page<Plant> plants=plantManager.getUserPlants(0);
        assertThat(plants.getTotalElements()).isEqualTo(1);
        assertThat(plants.getContent()).contains(plant);
    }
    @Test
    void testUpdatePlant() throws NoSuchObjectException {
        Species species=speciesManager.getSpeciesByNameAs("Wheat",0).stream().findFirst().get();
        Plant plant= plantManager.addPlant(Plant.builder()
                .variety("test1")
                .species(species)
                .build());
        Plant plantToUpdate= plantManager.getPlantById(plant.getId());
        entityManager.detach(plantToUpdate);
        plantToUpdate.setVariety("TEST_UPDATE");
        Plant plantUpdated= plantManager.updatePlant(plantToUpdate);
        assertThat(plantUpdated.getId()).isEqualTo(plantToUpdate.getId());
        assertThat(plantUpdated.getVariety()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() throws NoSuchObjectException {
        Plant plantToUpdate= plantManager.getDefaultPlants(0).stream().findFirst().orElse(Plant.NONE);
        entityManager.detach(plantToUpdate);
        plantToUpdate.setVariety("TEST_UPDATE");
        assertThrows(IllegalAccessError.class,()-> plantManager.updatePlant(plantToUpdate));
        assertThat(plantManager.getPlantById(plantToUpdate.getId()).getVariety()).isNotEqualTo("TEST_UPDATE");
        assertThat(plantManager.getPlantById(plantToUpdate.getId()).getVariety()).isNotEqualTo("NONE");
    }

    @Test
    void testDeletePlant(){
        Species species=speciesManager.getSpeciesByNameAs("Wheat",0).stream().findFirst().get();
        Plant plant= plantManager.addPlant(Plant.builder()
                .variety("test1")
                .species(species)
                .build());
        Plant plantToDelete= plantManager.getPlantById(plant.getId());
        plantManager.deletePlantSafe(plantToDelete);
        assertThat(plantManager.getPlantById(plant.getId())).isEqualTo(Plant.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied(){
        Plant plantToDelete= plantManager.getDefaultPlants(0).stream().findFirst().orElse(Plant.NONE);
        assertThrows(IllegalAccessError.class,()->plantManager.deletePlantSafe(plantToDelete));
        assertThat(plantManager.getPlantById(plantToDelete.getId())).isNotEqualTo(Plant.NONE);
    }


}
