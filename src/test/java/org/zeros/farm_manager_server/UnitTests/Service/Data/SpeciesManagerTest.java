package org.zeros.farm_manager_server.UnitTests.Service.Data;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfigurationForServiceTest;
import org.zeros.farm_manager_server.Domain.DTO.Data.SpeciesDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.Data.SpeciesRepository;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.Services")
@Import(LoggedUserConfigurationForServiceTest.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SpeciesManagerTest {
    @Autowired
    SpeciesRepository speciesRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private SpeciesManager speciesManager;

    @BeforeEach
    public void setUp() {
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        loggedUserConfiguration.replaceUser(user);
    }

    @Test
    void testCreateSpecies() {
        SpeciesDTO species = saveNewTestSpecies();
        assertThat(species.getId()).isNotNull();
        assertThat(species.getName()).isEqualTo("TEST");
        assertThat(species.getFamily()).isEqualTo("TEST");
    }

    private SpeciesDTO saveNewTestSpecies() {
        return speciesManager.addSpecies(SpeciesDTO.builder()
                .name("TEST")
                .family("TEST")
                .build());
    }

    @Test
    void testGetAllSpecies() {
        saveNewTestSpecies();
        Page<SpeciesDTO> speciesAll = speciesManager.getAllSpecies(0);
        assertThat(speciesAll.getTotalElements()).isEqualTo(6);
    }

    @Test
    void testGetDefaultSpecies() {
        Page<SpeciesDTO> species = speciesManager.getDefaultSpecies(0);
        assertThat(species.getTotalElements()).isEqualTo(5);
    }

    @Test
    void testGetUserSpecies() {
        saveNewTestSpecies();
        Page<SpeciesDTO> speciesUser = speciesManager.getUserSpecies(0);
        assertThat(speciesUser.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testUpdateSpecies() {
        SpeciesDTO speciesToUpdate = saveNewTestSpecies();
        speciesToUpdate.setName("TEST_UPDATE");
        SpeciesDTO speciesUpdated = speciesManager.updateSpecies(speciesToUpdate);
        assertThat(speciesUpdated.getId()).isEqualTo(speciesToUpdate.getId());
        assertThat(speciesUpdated.getName()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() {
        SpeciesDTO speciesToUpdate = speciesManager.getDefaultSpecies(0).stream().findFirst().orElseThrow();
        speciesToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> speciesManager.updateSpecies(speciesToUpdate));
        assertThat(speciesManager.getSpeciesById(speciesToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(speciesManager.getSpeciesById(speciesToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteSpecies() {
        SpeciesDTO species = saveNewTestSpecies();
        speciesManager.deleteSpeciesSafe(species.getId());
        assertThrows(IllegalArgumentException.class, () -> speciesManager.getSpeciesById(species.getId()));
    }

    @Test
    void testDeleteFailedAccessDenied() {
        SpeciesDTO species = speciesManager.getDefaultSpecies(0).stream().findFirst().orElseThrow();
        assertThrows(IllegalAccessError.class, () -> speciesManager.deleteSpeciesSafe(species.getId()));
        assertThat(speciesManager.getSpeciesById(species.getId())).isNotEqualTo(Species.NONE);
        assertThat(speciesManager.getSpeciesById(species.getId())).isEqualTo(species);
    }
}
