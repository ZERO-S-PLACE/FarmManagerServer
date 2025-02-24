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
import org.zeros.farm_manager_server.domain.dto.data.SubsideDTO;
import org.zeros.farm_manager_server.domain.entities.user.User;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.repositories.data.SubsideRepository;
import org.zeros.farm_manager_server.repositories.user.UserRepository;
import org.zeros.farm_manager_server.services.default_impl.data.SpeciesManagerDefault;
import org.zeros.farm_manager_server.services.interfaces.data.SubsideManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.services")
@Import(LoggedUserConfigurationForServiceTest.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SubsidesManagerTest {
    @Autowired
    SubsideManager subsideManager;
    @Autowired
    SubsideRepository subsideRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private SpeciesManagerDefault speciesManagerDefault;


    @BeforeEach
    public void setUp() {
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        loggedUserConfiguration.replaceUser(user);
    }

    @Test
    void testCreateSubside() {
        SubsideDTO subside = saveNewSubside();
        assertThat(subside.getId()).isNotNull();
        assertThat(subside.getName()).isEqualTo("test1");
        assertThat(subside.getSubsideValuePerAreaUnit().floatValue()).isEqualTo(10);
    }

    private SubsideDTO saveNewSubside() {
        return subsideManager.addSubside(SubsideDTO.builder()
                .name("test1")
                .subsideValuePerAreaUnit(BigDecimal.valueOf(10))
                .speciesAllowed(Set.of(speciesManagerDefault.getDefaultSpecies(0).getContent().getFirst().getId()))
                .yearOfSubside(LocalDate.ofYearDay(2024, 1))
                .build());
    }

    @Test
    void testGetAllSubsides() {
        SubsideDTO subside = saveNewSubside();
        Page<SubsideDTO> subsides = subsideManager.getAllSubsides(0);
        assertThat(subside.getId()).isNotNull();
        assertThat(subsides.getTotalElements()).isGreaterThan(4);
    }

    @Test
    void testGetDefaultSubsides() {
        Page<SubsideDTO> subsides = subsideManager.getDefaultSubsides(0);
        assertThat(subsides.getTotalElements()).isEqualTo(3);
    }

    @Test
    void testGetUserSubsides() {
        SubsideDTO subside = saveNewSubside();
        Page<SubsideDTO> subsides = subsideManager.getUserSubsides(0);
        assertThat(subside.getId()).isNotNull();
        assertThat(subsides.getTotalElements()).isGreaterThan(1);
    }

    @Test
    void testUpdateSubside() {
        SubsideDTO subsideToUpdate = saveNewSubside();
        subsideToUpdate.setName("TEST_UPDATE");
        SubsideDTO subsideUpdated = subsideManager.updateSubside(subsideToUpdate);
        assertThat(subsideUpdated.getId()).isEqualTo(subsideToUpdate.getId());
        assertThat(subsideUpdated.getName()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() {
        SubsideDTO subsideToUpdate =
                subsideManager.getDefaultSubsides(0).stream().findFirst().orElseThrow();
        subsideToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> subsideManager.updateSubside(subsideToUpdate));
        assertThat(subsideManager.getSubsideById(subsideToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(subsideManager.getSubsideById(subsideToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteSubside() {
        SubsideDTO subside = saveNewSubside();
        subsideManager.deleteSubsideSafe(subside.getId());
        assertThrows(IllegalArgumentException.class, () -> subsideManager.getSubsideById(subside.getId()));
    }

    @Test
    void testDeleteFailedAccessDenied() {
        SubsideDTO subsideToDelete = subsideManager.getDefaultSubsides(0).stream().findFirst().orElseThrow();
        assertThrows(IllegalAccessError.class, () -> subsideManager.deleteSubsideSafe(subsideToDelete.getId()));
        assertThat(subsideManager.getSubsideById(subsideToDelete.getId())).isEqualTo(subsideToDelete);
    }


}
