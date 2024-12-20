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
import org.zeros.farm_manager_server.DAO.DefaultImpl.SprayManagerDefault;
import org.zeros.farm_manager_server.DAO.DefaultImpl.UserManagerDefault;
import org.zeros.farm_manager_server.DAO.Interface.PlantManager;
import org.zeros.farm_manager_server.DAO.Interface.SpeciesManager;
import org.zeros.farm_manager_server.DAO.Interface.SprayManager;
import org.zeros.farm_manager_server.DAO.Interface.UserManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.SprayType;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.repositories.Data.PlantRepository;
import org.zeros.farm_manager_server.repositories.Data.SpeciesRepository;
import org.zeros.farm_manager_server.repositories.Data.SprayRepository;

import java.rmi.NoSuchObjectException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@Import({SprayManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SprayManagerTest {
    @Autowired
    UserManager userManager;

    @Autowired
    SprayRepository sprayRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    EntityManager entityManager;
    private User user;
    @Autowired
    private SprayManager sprayManager;

    @BeforeEach
    public void setUp() {
        user = userManager.logInNewUserByUsernameAndPassword("TestUser1", "password");
    }

    @Test
    void testCreateSpray() {
        Spray spray= sprayManager.addSpray(Spray.builder()
                        .name("TEST_SPRAY")
                        .sprayType(SprayType.HERBICIDE)
                        .producer("Baywa")
                .build());
        assertThat(spray.getId()).isNotNull();
        assertThat(spray.getName()).isEqualTo("TEST_SPRAY");
        assertThat(spray.getProducer()).isEqualTo("Baywa");
        assertThat(spray.getCreatedBy()).isEqualTo(user.getUsername());
        assertThat(sprayRepository.findById(spray.getId()).get()).isEqualTo(spray);
    }

    @Test
    void testGetAllSprays() {
        Spray spray= sprayManager.addSpray(Spray.builder()
                .name("TEST_SPRAY")
                .sprayType(SprayType.HERBICIDE)
                .producer("Baywa")
                .build());
        Page<Spray> sprays= sprayManager.getAllSprays(0);
       assertThat(sprays.getTotalElements()).isEqualTo(7);
    }

    @Test
    void testGetDefaultSprays() {
        Page<Spray> sprays=sprayManager.getDefaultSprays(0);
        assertThat(sprays.getTotalElements()).isEqualTo(6);
    }
    @Test
    void testGetUserSprays() {
        Spray spray= sprayManager.addSpray(Spray.builder()
                .name("TEST_SPRAY")
                .sprayType(SprayType.HERBICIDE)
                .producer("Baywa")
                .build());
        Page<Spray> sprays=sprayManager.getUserSprays(0);
        assertThat(sprays.getTotalElements()).isEqualTo(1);
        assertThat(sprays.getContent()).contains(spray);
    }
    @Test
    void testUpdateSpray()  {
        Spray spray= sprayManager.addSpray(Spray.builder()
                .name("TEST_SPRAY")
                .sprayType(SprayType.HERBICIDE)
                .producer("Baywa")
                .build());
        Spray sprayToUpdate= sprayManager.getSprayById(spray.getId());
        entityManager.detach(sprayToUpdate);
        sprayToUpdate.setName("TEST_UPDATE");
        Spray sprayUpdated= sprayManager.updateSpray(sprayToUpdate);
        assertThat(sprayUpdated.getId()).isEqualTo(spray.getId());
        assertThat(sprayUpdated.getName()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() throws NoSuchObjectException {
        Spray sprayToUpdate= sprayManager.getDefaultSprays(0).stream().findFirst().orElse(Spray.NONE);
        entityManager.detach(sprayToUpdate);
        sprayToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class,()-> sprayManager.updateSpray(sprayToUpdate));
        assertThat(sprayManager.getSprayById(sprayToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(sprayManager.getSprayById(sprayToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteSpray(){
        Spray spray= sprayManager.addSpray(Spray.builder()
                .name("TEST_SPRAY")
                .sprayType(SprayType.HERBICIDE)
                .producer("Baywa")
                .build());
        Spray sprayToDelete= sprayManager.getSprayById(spray.getId());
        sprayManager.deleteSpraySafe(spray);
        assertThat(sprayManager.getSprayById(spray.getId())).isEqualTo(Spray.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied(){
        Spray sprayToDelete= sprayManager.getDefaultSprays(0).stream().findFirst().orElse(Spray.NONE);
        assertThrows(IllegalAccessError.class,()->sprayManager.deleteSpraySafe(sprayToDelete));
        assertThat(sprayManager.getSprayById(sprayToDelete.getId())).isNotEqualTo(Spray.NONE);
    }


}
