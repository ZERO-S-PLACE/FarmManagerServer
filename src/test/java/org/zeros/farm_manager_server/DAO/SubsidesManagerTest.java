package org.zeros.farm_manager_server.DAO;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.DAO.DefaultImpl.SubsideManagerDefault;
import org.zeros.farm_manager_server.DAO.DefaultImpl.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.DAO.DefaultImpl.UserManagerDefault;
import org.zeros.farm_manager_server.DAO.Interface.SubsideManager;
import org.zeros.farm_manager_server.DAO.Interface.UserManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.Crops.Subside;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.repositories.Data.SubsideRepository;

import java.math.BigDecimal;
import java.rmi.NoSuchObjectException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@Import({SubsideManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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



    @BeforeEach
    public void setUp() {
        user = userManager.logInNewUserByUsernameAndPassword("TestUser1", "password");
    }

    @Test
    void testCreateSubside() {
        Subside subside=subsideManager.addSubside(Subside.builder()
                .name("test1")
                .subsideValuePerAreaUnit(BigDecimal.valueOf(10))
                .yearOfSubside(LocalDate.of(2024,1,1))
                .build());
        assertThat(subside.getId()).isNotNull();
        assertThat(subside.getName()).isEqualTo("test1");
        assertThat(subside.getCreatedBy()).isEqualTo(user.getUsername());
        assertThat(subside.getSubsideValuePerAreaUnit()).isEqualTo(BigDecimal.valueOf(10));
        assertThat(subsideRepository.findById(subside.getId()).get()).isEqualTo(subside);
    }
    @Test
    void testGetAllSubsides() {
        Subside subside=subsideManager.addSubside(Subside.builder()
                .name("test1")
                .subsideValuePerAreaUnit(BigDecimal.valueOf(10))
                .yearOfSubside(LocalDate.of(2024,1,1))
                .build());
        Page<Subside> subsides=subsideManager.getAllSubsides(0);
        assertThat(subside.getId()).isNotNull();
       assertThat(subsides.getTotalElements()).isEqualTo(4);
    }

    @Test
    void testGetDefaultSubsides() {
        Page<Subside> subsides=subsideManager.getDefaultSubsides(0);
        assertThat(subsides.getTotalElements()).isEqualTo(3);
    }
    @Test
    void testGetUserSubsides() {
        Subside subside=subsideManager.addSubside(Subside.builder()
                .name("test1")
                .subsideValuePerAreaUnit(BigDecimal.valueOf(10))
                .yearOfSubside(LocalDate.of(2024,1,1))
                .build());
        Page<Subside> subsides=subsideManager.getUserSubsides(0);
        assertThat(subside.getId()).isNotNull();
        assertThat(subsides.getTotalElements()).isEqualTo(1);
    }
    @Test
    void testUpdateSubside() throws NoSuchObjectException {
        Subside subside=subsideManager.addSubside(Subside.builder()
                .name("test1")
                .subsideValuePerAreaUnit(BigDecimal.valueOf(10))
                .yearOfSubside(LocalDate.of(2024,1,1))
                .build());
        Subside subsideToUpdate=subsideManager.getSubsideById(subside.getId());
        entityManager.detach(subsideToUpdate);
        subsideToUpdate.setName("TEST_UPDATE");
        Subside subsideUpdated=subsideManager.updateSubside(subsideToUpdate);
        assertThat(subsideUpdated.getId()).isEqualTo(subside.getId());
        assertThat(subsideUpdated.getName()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() throws NoSuchObjectException {
        Subside subsideToUpdate=subsideManager.getDefaultSubsides(0).stream().findFirst().orElse(Subside.NONE);
        entityManager.detach(subsideToUpdate);
        subsideToUpdate.setName("TEST_UPDATE");
        assertThrows(IllegalAccessError.class,()->subsideManager.updateSubside(subsideToUpdate));
        assertThat(subsideManager.getSubsideById(subsideToUpdate.getId()).getName()).isNotEqualTo("TEST_UPDATE");
        assertThat(subsideManager.getSubsideById(subsideToUpdate.getId()).getName()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteSubside(){
        Subside subside=subsideManager.addSubside(Subside.builder()
                .name("test1")
                .subsideValuePerAreaUnit(BigDecimal.valueOf(10))
                .yearOfSubside(LocalDate.of(2024,1,1))
                .build());
        Subside subsideToDelete=subsideManager.getSubsideById(subside.getId());
        subsideManager.deleteSubsideSafe(subsideToDelete);
        assertThat(subsideManager.getSubsideById(subsideToDelete.getId())).isEqualTo(Subside.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied(){
        Subside subsideToDelete=subsideManager.getDefaultSubsides(0).stream().findFirst().orElse(Subside.NONE);
        assertThrows(IllegalAccessError.class,()->subsideManager.deleteSubsideSafe(subsideToDelete));
        assertThat(subsideManager.getSubsideById(subsideToDelete.getId())).isNotEqualTo(Subside.NONE);
    }


}
