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
import org.zeros.farm_manager_server.Domain.DTO.Data.SprayDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Spray;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Enum.SprayType;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.Data.SprayRepository;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.SprayManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.Services")
@Import(LoggedUserConfigurationForServiceTest.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SprayManagerTest {
    @Autowired
    SprayRepository sprayRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private SprayManager sprayManager;

    @BeforeEach
    public void setUp() {
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        loggedUserConfiguration.replaceUser(user);
    }

    @Test
    void testCreateSpray() {
        SprayDTO spray = saveNewSpray();
        assertThat(spray.getId()).isNotNull();
        assertThat(spray.getName()).isEqualTo("TEST_SPRAY");
        assertThat(spray.getProducer()).isEqualTo("Baywa");
    }

    private SprayDTO saveNewSpray() {
        return sprayManager.addSpray(SprayDTO.builder()
                .name("TEST_SPRAY")
                .sprayType(SprayType.HERBICIDE)
                .producer("Baywa")
                .build());
    }

    @Test
    void testGetAllSprays() {
        saveNewSpray();
        Page<SprayDTO> sprays = sprayManager.getAllSprays(0);
        assertThat(sprays.getTotalElements()).isEqualTo(7);
    }

    @Test
    void testGetDefaultSprays() {
        Page<SprayDTO> sprays = sprayManager.getDefaultSprays(0);
        assertThat(sprays.getTotalElements()).isEqualTo(6);
    }

    @Test
    void testGetUserSprays() {
        SprayDTO spray = saveNewSpray();
        Page<SprayDTO> sprays = sprayManager.getUserSprays(0);
        assertThat(sprays.getTotalElements()).isEqualTo(1);
        assertThat(sprays.getContent()).contains(spray);
    }

    @Test
    void testUpdateSpray() {
        SprayDTO sprayToUpdate = saveNewSpray();
        sprayToUpdate.setName("TEST_UPDATE");
        SprayDTO sprayUpdated = sprayManager.updateSpray(sprayToUpdate);
        assertThat(sprayUpdated.getId()).isEqualTo(sprayToUpdate.getId());
        assertThat(sprayUpdated.getName()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() {
        SprayDTO sprayToUpdate = sprayManager.getDefaultSprays(0).getContent().getFirst();
        sprayToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> sprayManager.updateSpray(sprayToUpdate));
        assertThat(sprayManager.getSprayById(sprayToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(sprayManager.getSprayById(sprayToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteSpray() {
        SprayDTO spray = saveNewSpray();
        sprayManager.deleteSpraySafe(spray.getId());
        assertThrows(IllegalArgumentExceptionCustom.class, () -> sprayManager.getSprayById(spray.getId()));
    }

    @Test
    void testDeleteFailedAccessDenied() {
        SprayDTO sprayToDelete = sprayManager.getDefaultSprays(0).stream().findFirst().orElseThrow();
        assertThrows(IllegalAccessError.class, () -> sprayManager.deleteSpraySafe(sprayToDelete.getId()));
        assertThat(sprayManager.getSprayById(sprayToDelete.getId())).isNotEqualTo(Spray.NONE);
    }


}
