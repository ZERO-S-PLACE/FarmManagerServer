package org.zeros.farm_manager_server.UnitTests.Service.Data;


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
import org.zeros.farm_manager_server.Domain.DTO.Data.SprayDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Spray;
import org.zeros.farm_manager_server.Domain.Enum.SprayType;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.Data.SprayRepository;
import org.zeros.farm_manager_server.Services.Default.Data.SprayManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldGroupManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldPartManagerDefault;
import org.zeros.farm_manager_server.Services.Default.User.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Data.SprayManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@Import({FieldManagerDefault.class,FieldPartManagerDefault.class, FieldGroupManagerDefault.class, SprayManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
        user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
    }

    @Test
    void testCreateSpray() {
        Spray spray = saveNewSpray();
        assertThat(spray.getId()).isNotNull();
        assertThat(spray.getName()).isEqualTo("TEST_SPRAY");
        assertThat(spray.getProducer()).isEqualTo("Baywa");
        assertThat(spray.getCreatedBy()).isEqualTo(user.getUsername());
        assertThat(sprayRepository.findById(spray.getId()).get()).isEqualTo(spray);
    }

    private Spray saveNewSpray() {
        return sprayManager.addSpray(SprayDTO.builder()
                .name("TEST_SPRAY")
                .sprayType(SprayType.HERBICIDE)
                .producer("Baywa")
                .build());
    }

    @Test
    void testGetAllSprays() {
        saveNewSpray();
        Page<Spray> sprays = sprayManager.getAllSprays(0);
        assertThat(sprays.getTotalElements()).isEqualTo(7);
    }

    @Test
    void testGetDefaultSprays() {
        Page<Spray> sprays = sprayManager.getDefaultSprays(0);
        assertThat(sprays.getTotalElements()).isEqualTo(6);
    }

    @Test
    void testGetUserSprays() {
        Spray spray = saveNewSpray();
        Page<Spray> sprays = sprayManager.getUserSprays(0);
        assertThat(sprays.getTotalElements()).isEqualTo(1);
        assertThat(sprays.getContent()).contains(spray);
    }

    @Test
    void testUpdateSpray() {
        Spray spray = saveNewSpray();
        SprayDTO sprayToUpdate = DefaultMappers.sprayMapper.entityToDto(spray);
        sprayToUpdate.setName("TEST_UPDATE");
        Spray sprayUpdated = sprayManager.updateSpray(sprayToUpdate);
        assertThat(sprayUpdated.getId()).isEqualTo(spray.getId());
        assertThat(sprayUpdated.getName()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() {
        SprayDTO sprayToUpdate = DefaultMappers.sprayMapper.entityToDto(
                sprayManager.getDefaultSprays(0).getContent().getFirst());
        sprayToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> sprayManager.updateSpray(sprayToUpdate));
        assertThat(sprayManager.getSprayById(sprayToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(sprayManager.getSprayById(sprayToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteSpray() {
        Spray spray = saveNewSpray();
        sprayManager.deleteSpraySafe(spray.getId());
        assertThat(sprayManager.getSprayById(spray.getId())).isEqualTo(Spray.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied() {
        Spray sprayToDelete = sprayManager.getDefaultSprays(0).stream().findFirst().orElse(Spray.NONE);
        assertThrows(IllegalAccessError.class, () -> sprayManager.deleteSpraySafe(sprayToDelete.getId()));
        assertThat(sprayManager.getSprayById(sprayToDelete.getId())).isNotEqualTo(Spray.NONE);
    }


}
