package org.zeros.farm_manager_server.Service;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Services.Default.Data.SpeciesManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Entities.User.User;
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
        Species species = speciesManager.addSpecies(Species.builder()
                .name("test1")
                .family("X11")
                .build());
        assertThat(species.getId()).isNotNull();
        assertThat(species.getName()).isEqualTo("test1");
        assertThat(species.getFamily()).isEqualTo("X11");
        assertThat(species.getCreatedBy()).isEqualTo(user.getUsername());
        assertThat(speciesRepository.findById(species.getId()).get()).isEqualTo(species);
    }

    @Test
    void testGetAllSpecies() {
        speciesManager.addSpecies(Species.builder()
                .name("test13")
                .family("X113")
                .build());
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
        speciesManager.addSpecies(Species.builder()
                .name("test1323")
                .family("X113232")
                .build());

        Page<Species> speciesUser = speciesManager.getUserSpecies(0);
        assertThat(speciesUser.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testUpdateSpecies() throws NoSuchObjectException {
        Species species = speciesManager.addSpecies(Species.builder()
                .name("test1212121")
                .family("X1122222")
                .build());
        Species speciesToUpdate = speciesManager.getSpeciesById(species.getId());
        entityManager.detach(speciesToUpdate);
        speciesToUpdate.setName("TEST_UPDATE");
        Species speciesUpdated = speciesManager.updateSpecies(speciesToUpdate);
        assertThat(speciesUpdated.getId()).isEqualTo(species.getId());
        assertThat(speciesUpdated.getName()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() {
        Species speciesToUpdate = speciesManager.getDefaultSpecies(0).stream().findFirst().orElse(Species.NONE);
        entityManager.detach(speciesToUpdate);
        speciesToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> speciesManager.updateSpecies(speciesToUpdate));
        assertThat(speciesManager.getSpeciesById(speciesToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(speciesManager.getSpeciesById(speciesToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteSpecies() {
        Species species = speciesManager.addSpecies(Species.builder()
                .name("test1333")
                .family("X113323")
                .build());
        entityManager.detach(species);
        speciesManager.deleteSpeciesSafe(species);
        assertThat(speciesManager.getSpeciesById(species.getId())).isEqualTo(Species.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied() {
        Species speciesToDelete = speciesManager.getDefaultSpecies(0).stream().findFirst().orElse(Species.NONE);
        assertThrows(IllegalAccessError.class, () -> speciesManager.deleteSpeciesSafe(speciesToDelete));
        assertThat(speciesManager.getSpeciesById(speciesToDelete.getId())).isNotEqualTo(Species.NONE);
    }
}
