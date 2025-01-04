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
import org.zeros.farm_manager_server.Domain.DTO.Crop.SubsideDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Services.Default.Data.SpeciesManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Data.SubsideManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Data.SubsideManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Subside;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Repositories.Data.SubsideRepository;

import java.math.BigDecimal;
import java.rmi.NoSuchObjectException;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@Import({UserFieldsManagerDefault.class,SubsideManagerDefault.class, SpeciesManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SubsidesManagerTest {
    @Autowired
    UserManager userManager;
    @Autowired
    SubsideManager subsideManager;
    @Autowired
    SubsideRepository subsideRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    EntityManager entityManager;
    private User user;
    @Autowired
    private SpeciesManagerDefault speciesManagerDefault;


    @BeforeEach
    public void setUp() {
        user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
    }

    @Test
    void testCreateSubside() {
        Subside subside = saveNewSubside();
        assertThat(subside.getId()).isNotNull();
        assertThat(subside.getName()).isEqualTo("test1");
        assertThat(subside.getCreatedBy()).isEqualTo(user.getUsername());
        assertThat(subside.getSubsideValuePerAreaUnit().floatValue() ).isEqualTo(10);
        assertThat(subsideRepository.findById(subside.getId()).get()).isEqualTo(subside);
    }

    private Subside saveNewSubside() {
        return subsideManager.addSubside(SubsideDTO.builder()
                .name("test1")
                .subsideValuePerAreaUnit(10)
                        .speciesAllowed(Set.of(speciesManagerDefault.getDefaultSpecies(0).getContent().get(0).getId()))
                .yearOfSubside(LocalDate.ofYearDay(2024, 1))
                .build());
    }

    @Test
    void testGetAllSubsides() {
        Subside subside = saveNewSubside();
        Page<Subside> subsides = subsideManager.getAllSubsides(0);
        assertThat(subside.getId()).isNotNull();
        assertThat(subsides.getTotalElements()).isGreaterThan(4);
    }

    @Test
    void testGetDefaultSubsides() {
        Page<Subside> subsides = subsideManager.getDefaultSubsides(0);
        assertThat(subsides.getTotalElements()).isEqualTo(3);
    }

    @Test
    void testGetUserSubsides() {
        Subside subside = saveNewSubside();
        Page<Subside> subsides = subsideManager.getUserSubsides(0);
        assertThat(subside.getId()).isNotNull();
        assertThat(subsides.getTotalElements()).isGreaterThan(1);
    }

    @Test
    void testUpdateSubside() throws NoSuchObjectException {
        Subside subside = saveNewSubside();
        SubsideDTO subsideToUpdate = DefaultMappers.subsideMapper.entityToDto(subside);
        subsideToUpdate.setName("TEST_UPDATE");
        Subside subsideUpdated = subsideManager.updateSubside(subsideToUpdate);
        assertThat(subsideUpdated.getId()).isEqualTo(subside.getId());
        assertThat(subsideUpdated.getName()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied(){
        SubsideDTO subsideToUpdate = DefaultMappers.subsideMapper.entityToDto(
                subsideManager.getDefaultSubsides(0).stream().findFirst().orElse(Subside.NONE));
        subsideToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> subsideManager.updateSubside(subsideToUpdate));
        assertThat(subsideManager.getSubsideById(subsideToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(subsideManager.getSubsideById(subsideToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteSubside() {
        Subside subside = saveNewSubside();
        subsideManager.deleteSubsideSafe(subside.getId());
        assertThat(subsideManager.getSubsideById(subside.getId())).isEqualTo(Subside.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied() {
        Subside subsideToDelete = subsideManager.getDefaultSubsides(0).stream().findFirst().orElse(Subside.NONE);
        assertThrows(IllegalAccessError.class, () -> subsideManager.deleteSubsideSafe(subsideToDelete.getId()));
        assertThat(subsideManager.getSubsideById(subsideToDelete.getId())).isEqualTo(subsideToDelete);
    }


}
