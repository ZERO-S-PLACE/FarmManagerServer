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
import org.zeros.farm_manager_server.DAO.DefaultImpl.SpeciesManagerDefault;
import org.zeros.farm_manager_server.DAO.DefaultImpl.UserManagerDefault;
import org.zeros.farm_manager_server.DAO.Interface.SpeciesManager;
import org.zeros.farm_manager_server.DAO.Interface.UserManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.repositories.Data.PlantRepository;
import org.zeros.farm_manager_server.repositories.Data.SpeciesRepository;

import java.rmi.NoSuchObjectException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@Import({SpeciesManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
        user = userManager.logInNewUserByUsernameAndPassword("TestUser1", "password");
    }

    @Test
    void testCreateSpecies() {
        Species species= speciesManager.addSpecies(Species.builder()
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
        Species species= speciesManager.addSpecies(Species.builder()
                .name("test1")
                .family("X11")
                .build());
        speciesManager.addSpecies(species);
        Page<Species> speciesAll= speciesManager.getAllSpecies(0);
       assertThat(speciesAll.getTotalElements()).isEqualTo(6);
    }

    @Test
    void testGetDefaultSpecies() {
        Page<Species> species=speciesManager.getDefaultSpecies(0);
        assertThat(species.getTotalElements()).isEqualTo(5);
    }
    @Test
    void testGetUserSpecies() {
        Species species= speciesManager.addSpecies(Species.builder()
                .name("test1")
                .family("X11")
                .build());
        speciesManager.addSpecies(species);
        Page<Species> speciesUser=speciesManager.getUserSpecies(0);
        assertThat(speciesUser.getTotalElements()).isEqualTo(1);
    }
    @Test
    void testUpdateSpecies() throws NoSuchObjectException {
        Species species= speciesManager.addSpecies(Species.builder()
                .name("test1")
                .family("X11")
                .build());
        speciesManager.addSpecies(species);
        Species speciesToUpdate= speciesManager.getSpeciesById(species.getId());
        entityManager.detach(speciesToUpdate);
        speciesToUpdate.setName("TEST_UPDATE");
        Species speciesUpdated= speciesManager.updateSpecies(speciesToUpdate);
        assertThat(speciesUpdated.getId()).isEqualTo(species.getId());
        assertThat(speciesUpdated.getName()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() throws NoSuchObjectException {
        Species speciesToUpdate= speciesManager.getDefaultSpecies(0).stream().findFirst().orElse(Species.NONE);
        entityManager.detach(speciesToUpdate);
        speciesToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class,()-> speciesManager.updateSpecies(speciesToUpdate));
        assertThat(speciesManager.getSpeciesById(speciesToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(speciesManager.getSpeciesById(speciesToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteSpecies(){
        Species species= speciesManager.addSpecies(Species.builder()
                .name("test1")
                .family("X11")
                .build());
        speciesManager.addSpecies(species);
        entityManager.detach(species);
        speciesManager.deleteSpeciesSafe(species);
        assertThat(speciesManager.getSpeciesById(species.getId())).isEqualTo(Species.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied(){
        Species speciesToDelete= speciesManager.getDefaultSpecies(0).stream().findFirst().orElse(Species.NONE);
        assertThrows(IllegalAccessError.class,()->speciesManager.deleteSpeciesSafe(speciesToDelete));
        assertThat(speciesManager.getSpeciesById(speciesToDelete.getId())).isNotEqualTo(Species.NONE);
    }
}
