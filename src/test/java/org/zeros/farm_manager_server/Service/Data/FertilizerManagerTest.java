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
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.FertilizerDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.Data.FertilizerRepository;
import org.zeros.farm_manager_server.Services.Default.Data.FertilizerManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Data.FertilizerManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@Import({UserFieldsManagerDefault.class, FertilizerManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
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
        Fertilizer fertilizer = addNewFertilizer();
        assertThat(fertilizer.getId()).isNotNull();
        assertThat(fertilizer.getIsNaturalFertilizer()).isEqualTo(false);
        assertThat(fertilizer.getCreatedBy()).isEqualTo(user.getUsername());
        assertThat(fertilizerRepository.findById(fertilizer.getId()).get()).isEqualTo(fertilizer);
    }

    private Fertilizer addNewFertilizer() {
        return fertilizerManager.addFertilizer(FertilizerDTO.builder()
                .name("Test Fertilizer")
                .isNaturalFertilizer(false)
                .totalNPercent(10)
                .totalKPercent(30)
                .build());
    }

    @Test
    void testGetAllFertilizers() {
        Fertilizer fertilizer = addNewFertilizer();
        Page<Fertilizer> fertilizers = fertilizerManager.getAllFertilizers(0);
        assertThat(fertilizers.getTotalElements()).isEqualTo(5);
        assertThat(fertilizers.getContent()).contains(fertilizer);
    }

    @Test
    void testGetDefaultFertilizers() {
        Fertilizer fertilizer = addNewFertilizer();
        Page<Fertilizer> fertilizers = fertilizerManager.getDefaultFertilizers(0);
        assertThat(fertilizers.getTotalElements()).isEqualTo(4);
        assertThat(fertilizers.getContent().contains(fertilizer)).isFalse();
    }

    @Test
    void testGetUserFertilizers() {
        Fertilizer fertilizer = addNewFertilizer();
        Page<Fertilizer> fertilizers = fertilizerManager.getUserFertilizers(0);
        assertThat(fertilizers.getTotalElements()).isEqualTo(1);
        assertThat(fertilizers.getContent()).contains(fertilizer);
    }

    @Test
    void testUpdateFertilizer() {
        Fertilizer fertilizer = addNewFertilizer();

        FertilizerDTO fertilizerToUpdate = DefaultMappers.fertilizerMapper.entityToDto(fertilizer);
        fertilizerToUpdate.setName("TEST_UPDATE");
        fertilizerToUpdate.setTotalNaPercent(5);
        Fertilizer fertilizerUpdated = fertilizerManager.updateFertilizer(fertilizerToUpdate);
        assertThat(fertilizerUpdated.getId()).isEqualTo(fertilizerToUpdate.getId());
        assertThat(fertilizerUpdated.getName()).isEqualTo("TEST_UPDATE");
        assertThat(fertilizerUpdated.getTotalNaPercent().doubleValue()).isEqualTo(5);
    }

    @Test
    void testUpdateFailedAccessDenied() {
        FertilizerDTO fertilizerToUpdate = DefaultMappers.fertilizerMapper.entityToDto(fertilizerManager.getDefaultFertilizers(0)
                .stream().findFirst().orElse(Fertilizer.NONE));
        fertilizerToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> fertilizerManager.updateFertilizer(fertilizerToUpdate));
        assertThat(fertilizerManager.getFertilizerById(fertilizerToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(fertilizerManager.getFertilizerById(fertilizerToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteFertilizer() {
        Fertilizer fertilizer = addNewFertilizer();
        fertilizerManager.deleteFertilizerSafe(fertilizer.getId());
        assertThat(fertilizerManager.getFertilizerById(fertilizer.getId())).isEqualTo(Fertilizer.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied() {
        Fertilizer fertilizerToDelete = fertilizerManager.getDefaultFertilizers(0).stream().findFirst().orElse(Fertilizer.NONE);
        assertThrows(IllegalAccessError.class, () -> fertilizerManager.deleteFertilizerSafe(fertilizerToDelete.getId()));
        assertThat(fertilizerManager.getFertilizerById(fertilizerToDelete.getId())).isEqualTo(fertilizerToDelete);
    }


}
