package org.zeros.farm_manager_server.service_tests.data;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.configuration.LoggedUserConfigurationForServiceTest;
import org.zeros.farm_manager_server.domain.dto.data.PlantDTO;
import org.zeros.farm_manager_server.domain.dto.data.SpeciesDTO;
import org.zeros.farm_manager_server.domain.entities.data.Plant;
import org.zeros.farm_manager_server.domain.entities.user.User;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.repositories.data.PlantRepository;
import org.zeros.farm_manager_server.repositories.user.UserRepository;
import org.zeros.farm_manager_server.services.interfaces.data.PlantManager;
import org.zeros.farm_manager_server.services.interfaces.data.SpeciesManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.services")
@Import(LoggedUserConfigurationForServiceTest.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class PlantManagerTest {

    @Autowired
    PlantRepository plantRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    private PlantManager plantManager;
    @Autowired
    private SpeciesManager speciesManager;

    @BeforeEach
    public void setUp() {
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        loggedUserConfiguration.replaceUser(user);
    }

    @Test
    void testCreatePlant() {
        SpeciesDTO species = speciesManager.getSpeciesByNameAs("Wheat", 0).getContent().getFirst();
        PlantDTO plant = addNewPlant(species);
        assertThat(plant.getId()).isNotNull();
        assertThat(plant.getVariety()).isEqualTo("test1");
        assertThat(plant.getSpecies()).isEqualTo(species.getId());
    }

    private PlantDTO addNewPlant(SpeciesDTO species) {

        return plantManager.addPlant(PlantDTO.builder()
                .variety("test1")
                .species(species.getId())
                .countryOfOrigin("POLAND")
                .productionCompany("RAGT")
                .build());
    }

    @Test
    void testGetAllPlants() {
        SpeciesDTO species = speciesManager.getSpeciesByNameAs("Wheat", 0).getContent().getFirst();
        addNewPlant(species);
        Page<PlantDTO> plants = plantManager.getAllPlants(0);
        assertThat(plants.getTotalElements()).isEqualTo(7);
    }

    @Test
    void testGetDefaultPlants() {
        Page<PlantDTO> plants = plantManager.getDefaultPlants(0);
        assertThat(plants.getTotalElements()).isEqualTo(6);
    }

    @Test
    void testGetUserPlants() {
        SpeciesDTO species = speciesManager.getSpeciesByNameAs("Wheat", 0).getContent().getFirst();
        PlantDTO plant = addNewPlant(species);
        Page<PlantDTO> plants = plantManager.getUserPlants(0);
        assertThat(plants.getTotalElements()).isEqualTo(1);
        assertThat(plants.getContent()).contains(plant);
    }

    @Test
    void testUpdatePlant() {
        SpeciesDTO species = speciesManager.getSpeciesByNameAs("Wheat", 0).stream().findFirst().get();
        PlantDTO plantToUpdate = addNewPlant(species);
        plantToUpdate.setVariety("TEST_UPDATE");
        PlantDTO plantUpdated = plantManager.updatePlant(plantToUpdate);
        assertThat(plantUpdated.getId()).isEqualTo(plantToUpdate.getId());
        assertThat(plantUpdated.getVariety()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() {
        PlantDTO plantToUpdate = plantManager.getDefaultPlants(0).getContent().getFirst();
        plantToUpdate.setVariety("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> plantManager.updatePlant(plantToUpdate));
        assertThat(plantManager.getPlantById(plantToUpdate.getId()).getVariety()).isNotEqualTo("TEST_UPDATE");
        assertThat(plantManager.getPlantById(plantToUpdate.getId()).getVariety()).isNotEqualTo("NONE");
    }

    @Test
    void testDeletePlant() {
        SpeciesDTO species = speciesManager.getSpeciesByNameAs("Wheat", 0).stream().findFirst().get();
        PlantDTO plant = addNewPlant(species);
        plantManager.deletePlantSafe(plant.getId());
        assertThrows(IllegalArgumentException.class, () -> plantManager.getPlantById(plant.getId()));
    }

    @Test
    void testDeleteFailedAccessDenied() {
        PlantDTO plantToDelete = plantManager.getDefaultPlants(0).getContent().getFirst();
        assertThrows(IllegalAccessError.class, () -> plantManager.deletePlantSafe(plantToDelete.getId()));
        assertThat(plantManager.getPlantById(plantToDelete.getId())).isNotEqualTo(Plant.NONE);
    }


}
