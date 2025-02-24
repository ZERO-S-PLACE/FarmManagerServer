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
import org.zeros.farm_manager_server.domain.dto.data.FertilizerDTO;
import org.zeros.farm_manager_server.domain.entities.user.User;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.repositories.data.FertilizerRepository;
import org.zeros.farm_manager_server.repositories.user.UserRepository;
import org.zeros.farm_manager_server.services.interfaces.data.FertilizerManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.services")
@Import(LoggedUserConfigurationForServiceTest.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class FertilizerManagerTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    FertilizerRepository fertilizerRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    private FertilizerManager fertilizerManager;

    @BeforeEach
    public void setUp() {
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        loggedUserConfiguration.replaceUser(user);
    }

    @Test
    void testCreateFertilizer() {
        FertilizerDTO fertilizer = addNewFertilizer();
        assertThat(fertilizer.getId()).isNotNull();
        assertThat(fertilizer.getIsNaturalFertilizer()).isEqualTo(false);
    }

    private FertilizerDTO addNewFertilizer() {
        return fertilizerManager.addFertilizer(FertilizerDTO.builder()
                .name("Test Fertilizer")
                .isNaturalFertilizer(false)
                .totalNPercent(BigDecimal.valueOf(10))
                .totalKPercent(BigDecimal.valueOf(30))
                .build());
    }

    @Test
    void testGetAllFertilizers() {
        FertilizerDTO fertilizer = addNewFertilizer();
        Page<FertilizerDTO> fertilizers = fertilizerManager.getAllFertilizers(0);
        assertThat(fertilizers.getTotalElements()).isEqualTo(5);
        assertThat(fertilizers.getContent()).contains(fertilizer);
    }

    @Test
    void testGetDefaultFertilizers() {
        FertilizerDTO fertilizer = addNewFertilizer();
        Page<FertilizerDTO> fertilizers = fertilizerManager.getDefaultFertilizers(0);
        assertThat(fertilizers.getTotalElements()).isEqualTo(4);
        assertThat(fertilizers.getContent().contains(fertilizer)).isFalse();
    }

    @Test
    void testGetUserFertilizers() {
        FertilizerDTO fertilizer = addNewFertilizer();
        Page<FertilizerDTO> fertilizers = fertilizerManager.getUserFertilizers(0);
        assertThat(fertilizers.getTotalElements()).isEqualTo(1);
        assertThat(fertilizers.getContent()).contains(fertilizer);
    }

    @Test
    void testUpdateFertilizer() {
        FertilizerDTO fertilizerToUpdate = addNewFertilizer();
        fertilizerToUpdate.setName("TEST_UPDATE");
        fertilizerToUpdate.setTotalNaPercent(BigDecimal.valueOf(5));
        FertilizerDTO fertilizerUpdated = fertilizerManager.updateFertilizer(fertilizerToUpdate);
        assertThat(fertilizerUpdated.getId()).isEqualTo(fertilizerToUpdate.getId());
        assertThat(fertilizerUpdated.getName()).isEqualTo("TEST_UPDATE");
        assertThat(fertilizerUpdated.getTotalNaPercent().doubleValue()).isEqualTo(5);
    }

    @Test
    void testUpdateFailedAccessDenied() {
        FertilizerDTO fertilizerToUpdate = fertilizerManager.getDefaultFertilizers(0)
                .stream().findFirst().orElseThrow();
        fertilizerToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> fertilizerManager.updateFertilizer(fertilizerToUpdate));
        assertThat(fertilizerManager.getFertilizerById(fertilizerToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(fertilizerManager.getFertilizerById(fertilizerToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteFertilizer() {
        FertilizerDTO fertilizer = addNewFertilizer();
        fertilizerManager.deleteFertilizerSafe(fertilizer.getId());
        assertThrows(IllegalArgumentException.class, () -> fertilizerManager.getFertilizerById(fertilizer.getId()));
    }

    @Test
    void testDeleteFailedAccessDenied() {
        FertilizerDTO fertilizerToDelete = fertilizerManager.getDefaultFertilizers(0).stream().findFirst().orElseThrow();
        assertThrows(IllegalAccessError.class, () -> fertilizerManager.deleteFertilizerSafe(fertilizerToDelete.getId()));
        assertThat(fertilizerManager.getFertilizerById(fertilizerToDelete.getId())).isEqualTo(fertilizerToDelete);
    }


}
