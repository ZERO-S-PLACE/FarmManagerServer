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
import org.zeros.farm_manager_server.DAO.Default.Data.FarmingMachineManagerDefault;
import org.zeros.farm_manager_server.DAO.Default.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.DAO.Default.UserManagerDefault;
import org.zeros.farm_manager_server.DAO.Interface.Data.FarmingMachineManager;
import org.zeros.farm_manager_server.DAO.Interface.UserManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.repositories.Data.FarmingMachineRepository;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@Import({UserFieldsManagerDefault.class,FarmingMachineManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FarmingMachineManagerTest {
    @Autowired
    UserManager userManager;
    @Autowired
    FarmingMachineRepository farmingMachineRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    EntityManager entityManager;
    @Autowired
    private FarmingMachineManager farmingMachineManager;
    private User user;

    @BeforeEach
    public void setUp() {
        user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
    }

    @Test
    void testCreateMachine() {

        FarmingMachine machine = farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                .model("TEST_MODEL")
                .producer("TEST_PRODUCER")
                .supportedOperationTypes(Set.of(OperationType.CULTIVATION))
                .build());
        assertThat(machine.getId()).isNotNull();
        assertThat(machine.getModel()).isEqualTo("TEST_MODEL");
        assertThat(machine.getProducer()).isEqualTo("TEST_PRODUCER");
        assertThat(machine.getSupportedOperationTypes()).isEqualTo(Set.of(OperationType.CULTIVATION));
        assertThat(machine.getCreatedBy()).isEqualTo(user.getUsername());
        assertThat(farmingMachineRepository.findById(machine.getId()).get()).isEqualTo(machine);
    }

    @Test
    void testGetAllMachines() {
        FarmingMachine machine = farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                .model("TEST_MODEL")
                .producer("TEST_PRODUCER")
                .supportedOperationTypes(Set.of(OperationType.CULTIVATION))
                .build());
        Page<FarmingMachine> machines = farmingMachineManager.getAllFarmingMachines(0);
        assertThat(machines.getTotalElements()).isEqualTo(9);
        assertThat(machines.getContent()).contains(machine);
    }

    @Test
    void testGetDefaultMachines() {
        FarmingMachine machine = farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                .model("TEST_MODEL")
                .producer("TEST_PRODUCER")
                .supportedOperationTypes(Set.of(OperationType.CULTIVATION))
                .build());
        Page<FarmingMachine> machines = farmingMachineManager.getDefaultFarmingMachines(0);
        assertThat(machines.getTotalElements()).isEqualTo(8);
        assertThat(machines.getContent().contains(machine)).isFalse();
    }

    @Test
    void testGetUserMachines() {
        FarmingMachine machine = farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                .model("TEST_MODEL")
                .producer("TEST_PRODUCER")
                .supportedOperationTypes(Set.of(OperationType.CULTIVATION))
                .build());
        Page<FarmingMachine> machines = farmingMachineManager.getUserFarmingMachines(0);
        assertThat(machines.getTotalElements()).isEqualTo(1);
        assertThat(machines.getContent()).contains(machine);
    }

    @Test
    void testUpdateMachine() {
        FarmingMachine machine = farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                .model("TEST_MODEL")
                .producer("TEST_PRODUCER")
                .supportedOperationTypes(Set.of(OperationType.CULTIVATION))
                .build());
        FarmingMachine toUpdate = farmingMachineManager.getFarmingMachineById(machine.getId());
        entityManager.detach(toUpdate);
        toUpdate.setModel("TEST_UPDATE");
        FarmingMachine machineUpdated = farmingMachineManager.updateFarmingMachine(toUpdate);
        assertThat(machineUpdated.getId()).isEqualTo(toUpdate.getId());
        assertThat(machineUpdated.getModel()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() {
        FarmingMachine farmingMachineToUpdate = farmingMachineManager.getDefaultFarmingMachines(0).stream().findFirst().orElse(FarmingMachine.NONE);
        entityManager.detach(farmingMachineToUpdate);
        farmingMachineToUpdate.setModel("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> farmingMachineManager.updateFarmingMachine(farmingMachineToUpdate));
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachineToUpdate.getId()).getModel()).isNotEqualTo("TEST_UPDATE");
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachineToUpdate.getId()).getModel()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteMachine() {
        FarmingMachine machine = farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                .model("TEST_MODEL")
                .producer("TEST_PRODUCER")
                .supportedOperationTypes(Set.of(OperationType.CULTIVATION))
                .build());
        FarmingMachine farmingMachineToDelete = farmingMachineManager.getFarmingMachineById(machine.getId());
        farmingMachineManager.deleteFarmingMachineSafe(farmingMachineToDelete);
        assertThat(farmingMachineManager.getFarmingMachineById(machine.getId())).isEqualTo(FarmingMachine.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied() {
        FarmingMachine machine = farmingMachineManager.getDefaultFarmingMachines(0).stream().findFirst().orElse(FarmingMachine.NONE);
        assertThrows(IllegalAccessError.class, () -> farmingMachineManager.deleteFarmingMachineSafe(machine));
        assertThat(farmingMachineManager.getFarmingMachineById(machine.getId())).isNotEqualTo(FarmingMachine.NONE);
    }

}
