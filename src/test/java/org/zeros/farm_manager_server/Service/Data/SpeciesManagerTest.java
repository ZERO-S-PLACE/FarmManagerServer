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
import org.zeros.farm_manager_server.Domain.DTO.Crop.Plant.SpeciesDTO;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Services.Default.Data.SpeciesManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Repositories.Data.PlantRepository;
import org.zeros.farm_manager_server.Repositories.Data.SpeciesRepository;

import java.rmi.NoSuchObjectException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@Import({UserFieldsManagerDefault.class,SpeciesManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SpeciesManagerTest {
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
    private SpeciesManager speciesManager;

    @BeforeEach
    public void setUp() {
        user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
    }

    @Test
    void testCreateSpecies() {
        Species species = saveNewTestSpecies();
        assertThat(species.getId()).isNotNull();
        assertThat(species.getName()).isEqualTo("TEST");
        assertThat(species.getFamily()).isEqualTo("TEST");
        assertThat(species.getCreatedBy()).isEqualTo(user.getUsername());
        assertThat(speciesRepository.findById(species.getId()).get()).isEqualTo(species);
    }

    private Species saveNewTestSpecies() {
        return speciesManager.addSpecies(SpeciesDTO.builder()
                .name("TEST")
                .family("TEST")
                .build());
    }

    @Test
    void testGetAllSpecies() {
        saveNewTestSpecies();
        Page<Species> speciesAll = speciesManager.getAllSpecies(0);
        assertThat(speciesAll.getTotalElements()).isEqualTo(6);
    }

    @Test
    void testGetDefaultSpecies() {
        Page<Species> species = speciesManager.getDefaultSpecies(0);
        assertThat(species.getTotalElements()).isEqualTo(5);
    }

    @Test
    void testGetUserSpecies() {
        saveNewTestSpecies();
        Page<Species> speciesUser = speciesManager.getUserSpecies(0);
        assertThat(speciesUser.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testUpdateSpecies() throws NoSuchObjectException {
        Species species = saveNewTestSpecies();
        SpeciesDTO speciesToUpdate = DefaultMappers.speciesMapper.entityToDto(species);
        speciesToUpdate.setName("TEST_UPDATE");
        Species speciesUpdated = speciesManager.updateSpecies(speciesToUpdate);
        assertThat(speciesUpdated.getId()).isEqualTo(species.getId());
        assertThat(speciesUpdated.getName()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() {
        Species species = speciesManager.getDefaultSpecies(0).stream().findFirst().orElse(Species.NONE);
        SpeciesDTO speciesToUpdate = DefaultMappers.speciesMapper.entityToDto(species);
        speciesToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> speciesManager.updateSpecies(speciesToUpdate));
        assertThat(speciesManager.getSpeciesById(speciesToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(speciesManager.getSpeciesById(speciesToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteSpecies() {
        Species species = saveNewTestSpecies();
        speciesManager.deleteSpeciesSafe(species.getId());
        assertThat(speciesManager.getSpeciesById(species.getId())).isEqualTo(Species.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied() {
        Species species = speciesManager.getDefaultSpecies(0).stream().findFirst().orElse(Species.NONE);
        assertThrows(IllegalAccessError.class, () -> speciesManager.deleteSpeciesSafe(species.getId()));
        assertThat(speciesManager.getSpeciesById(species.getId())).isNotEqualTo(Species.NONE);
        assertThat(speciesManager.getSpeciesById(species.getId())).isEqualTo(species);
    }
}
