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
import org.zeros.farm_manager_server.DAO.Default.Data.FertilizerManagerDefault;
import org.zeros.farm_manager_server.DAO.Default.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.DAO.Default.UserManagerDefault;
import org.zeros.farm_manager_server.DAO.Interface.Data.FertilizerManager;
import org.zeros.farm_manager_server.DAO.Interface.UserManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.repositories.Data.FertilizerRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@Import({UserFieldsManagerDefault.class,FertilizerManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class FertilizerManagerTest {
    @Autowired
    UserManager userManager;
    @Autowired
    FertilizerRepository fertilizerRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    EntityManager entityManager;
    private User user;
    @Autowired
    private FertilizerManager fertilizerManager;

    @BeforeEach
    public void setUp() {
        user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
    }

    @Test
    void testCreateFertilizer() {

        Fertilizer fertilizer = fertilizerManager.addFertilizer(Fertilizer.builder()
                .name("Test Fertilizer")
                .isNaturalFertilizer(false)
                .N_Percent(BigDecimal.TEN)
                .K_Percent(BigDecimal.TEN)
                .build());
        assertThat(fertilizer.getId()).isNotNull();
        assertThat(fertilizer.getIsNaturalFertilizer()).isEqualTo(false);
        assertThat(fertilizer.getCreatedBy()).isEqualTo(user.getUsername());
        assertThat(fertilizerRepository.findById(fertilizer.getId()).get()).isEqualTo(fertilizer);
    }

    @Test
    void testGetAllFertilizers() {
        Fertilizer fertilizer = fertilizerManager.addFertilizer(Fertilizer.builder()
                .name("Test Fertilizer")
                .isNaturalFertilizer(false)
                .N_Percent(BigDecimal.TEN)
                .K_Percent(BigDecimal.TEN)
                .build());
        Page<Fertilizer> fertilizers = fertilizerManager.getAllFertilizers(0);
        assertThat(fertilizers.getTotalElements()).isEqualTo(5);
        assertThat(fertilizers.getContent()).contains(fertilizer);
    }

    @Test
    void testGetDefaultFertilizers() {
        Fertilizer fertilizer = fertilizerManager.addFertilizer(Fertilizer.builder()
                .name("Test Fertilizer")
                .isNaturalFertilizer(false)
                .N_Percent(BigDecimal.TEN)
                .K_Percent(BigDecimal.TEN)
                .build());
        Page<Fertilizer> fertilizers = fertilizerManager.getDefaultFertilizers(0);
        assertThat(fertilizers.getTotalElements()).isEqualTo(4);
        assertThat(fertilizers.getContent().contains(fertilizer)).isFalse();
    }

    @Test
    void testGetUserFertilizers() {
        Fertilizer fertilizer = fertilizerManager.addFertilizer(Fertilizer.builder()
                .name("Test Fertilizer")
                .isNaturalFertilizer(false)
                .N_Percent(BigDecimal.TEN)
                .K_Percent(BigDecimal.TEN)
                .build());
        Page<Fertilizer> fertilizers = fertilizerManager.getUserFertilizers(0);
        assertThat(fertilizers.getTotalElements()).isEqualTo(1);
        assertThat(fertilizers.getContent()).contains(fertilizer);
    }

    @Test
    void testUpdateFertilizer() {
        Fertilizer fertilizer = fertilizerManager.addFertilizer(Fertilizer.builder()
                .name("Test Fertilizer")
                .isNaturalFertilizer(false)
                .N_Percent(BigDecimal.TEN)
                .K_Percent(BigDecimal.TEN)
                .build());
        Fertilizer fertilizerToUpdate = fertilizerManager.getFertilizerById(fertilizer.getId());
        entityManager.detach(fertilizerToUpdate);
        fertilizerToUpdate.setName("TEST_UPDATE");
        fertilizerToUpdate.setNa_Percent(BigDecimal.valueOf(1));
        Fertilizer fertilizerUpdated = fertilizerManager.updateFertilizer(fertilizerToUpdate);
        assertThat(fertilizerUpdated.getId()).isEqualTo(fertilizerToUpdate.getId());
        assertThat(fertilizerUpdated.getName()).isEqualTo("TEST_UPDATE");
        assertThat(fertilizerUpdated.getNa_Percent()).isEqualTo(BigDecimal.valueOf(1));
    }

    @Test
    void testUpdateFailedAccessDenied() {
        Fertilizer fertilizerToUpdate = fertilizerManager.getDefaultFertilizers(0).stream().findFirst().orElse(Fertilizer.NONE);
        entityManager.detach(fertilizerToUpdate);
        fertilizerToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> fertilizerManager.updateFertilizer(fertilizerToUpdate));
        assertThat(fertilizerManager.getFertilizerById(fertilizerToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(fertilizerManager.getFertilizerById(fertilizerToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteFertilizer() {
        Fertilizer fertilizer = fertilizerManager.addFertilizer(Fertilizer.builder()
                .name("Test Fertilizer")
                .isNaturalFertilizer(false)
                .N_Percent(BigDecimal.TEN)
                .K_Percent(BigDecimal.TEN)
                .build());
        fertilizerManager.deleteFertilizerSafe(fertilizer);
        assertThat(fertilizerManager.getFertilizerById(fertilizer.getId())).isEqualTo(Fertilizer.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied() {
        Fertilizer fertilizerToDelete = fertilizerManager.getDefaultFertilizers(0).stream().findFirst().orElse(Fertilizer.NONE);
        assertThrows(IllegalAccessError.class, () -> fertilizerManager.deleteFertilizerSafe(fertilizerToDelete));
        assertThat(fertilizerManager.getFertilizerById(fertilizerToDelete.getId())).isNotEqualTo(Fertilizer.NONE);
    }


}
