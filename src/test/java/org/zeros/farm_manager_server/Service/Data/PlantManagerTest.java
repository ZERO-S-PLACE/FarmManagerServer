package org.zeros.farm_manager_server.Service.Data;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.Crop.Plant.PlantDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.Data.PlantRepository;
import org.zeros.farm_manager_server.Repositories.Data.SpeciesRepository;
import org.zeros.farm_manager_server.Services.Default.Data.PlantManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Data.SpeciesManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Data.PlantManager;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@Import({UserFieldsManagerDefault.class, PlantManagerDefault.class, SpeciesManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

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
        user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
    }

    @Test
    void testCreatePlant() {
        Species species = speciesManager.getSpeciesByNameAs("Wheat", 0).getContent().getFirst();
        Plant plant = addNewPlant(species);
        assertThat(plant.getId()).isNotNull();
        assertThat(plant.getVariety()).isEqualTo("test1");
        assertThat(plant.getSpecies()).isEqualTo(species);
        assertThat(plant.getCreatedBy()).isEqualTo(user.getUsername());
        assertThat(plantRepository.findById(plant.getId()).get()).isEqualTo(plant);
    }

    private Plant addNewPlant(Species species) {

        return plantManager.addPlant(PlantDTO.builder()
                .variety("test1")
                .species(species.getId())
                .countryOfOrigin("POLAND")
                .productionCompany("RAGT")
                .build());
    }

    @Test
    void testGetAllPlants() {
        Species species = speciesManager.getSpeciesByNameAs("Wheat", 0).getContent().getFirst();
        addNewPlant(species);
        Page<Plant> plants = plantManager.getAllPlants(0);
        assertThat(plants.getTotalElements()).isEqualTo(7);
    }

    @Test
    void testGetDefaultPlants() {
        Page<Plant> plants = plantManager.getDefaultPlants(0);
        assertThat(plants.getTotalElements()).isEqualTo(6);
    }

    @Test
    void testGetUserPlants() {
        Species species = speciesManager.getSpeciesByNameAs("Wheat", 0).getContent().getFirst();
        Plant plant = addNewPlant(species);
        Page<Plant> plants = plantManager.getUserPlants(0);
        assertThat(plants.getTotalElements()).isEqualTo(1);
        assertThat(plants.getContent()).contains(plant);
    }

    @Test
    void testUpdatePlant() {
        Species species = speciesManager.getSpeciesByNameAs("Wheat", 0).stream().findFirst().get();
        Plant plant = addNewPlant(species);
        PlantDTO plantToUpdate = DefaultMappers.plantMapper.entityToDto(plantManager.getPlantById(plant.getId()));
        plantToUpdate.setVariety("TEST_UPDATE");
        Plant plantUpdated = plantManager.updatePlant(plantToUpdate);
        assertThat(plantUpdated.getId()).isEqualTo(plantToUpdate.getId());
        assertThat(plantUpdated.getVariety()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() {
        PlantDTO plantToUpdate = DefaultMappers.plantMapper.entityToDto(
                plantManager.getDefaultPlants(0).getContent().getFirst());
        plantToUpdate.setVariety("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> plantManager.updatePlant(plantToUpdate));
        assertThat(plantManager.getPlantById(plantToUpdate.getId()).getVariety()).isNotEqualTo("TEST_UPDATE");
        assertThat(plantManager.getPlantById(plantToUpdate.getId()).getVariety()).isNotEqualTo("NONE");
    }

    @Test
    void testDeletePlant() {
        Species species = speciesManager.getSpeciesByNameAs("Wheat", 0).stream().findFirst().get();
        Plant plant = addNewPlant(species);
        plantManager.deletePlantSafe(plant.getId());
        assertThat(plantManager.getPlantById(plant.getId())).isEqualTo(Plant.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied() {
        Plant plantToDelete = plantManager.getDefaultPlants(0).getContent().getFirst();
        assertThrows(IllegalAccessError.class, () -> plantManager.deletePlantSafe(plantToDelete.getId()));
        assertThat(plantManager.getPlantById(plantToDelete.getId())).isNotEqualTo(Plant.NONE);
    }


}
