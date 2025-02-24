package org.zeros.farm_manager_server.service_tests.data;


import jakarta.persistence.EntityManager;
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
import org.zeros.farm_manager_server.domain.dto.data.FarmingMachineDTO;
import org.zeros.farm_manager_server.domain.entities.data.FarmingMachine;
import org.zeros.farm_manager_server.domain.entities.user.User;
import org.zeros.farm_manager_server.domain.enums.OperationType;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.repositories.data.FarmingMachineRepository;
import org.zeros.farm_manager_server.repositories.user.UserRepository;
import org.zeros.farm_manager_server.services.interfaces.data.FarmingMachineManager;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.services")
@Import(LoggedUserConfigurationForServiceTest.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FarmingMachineManagerTest {
    @Autowired
    FarmingMachineRepository farmingMachineRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    EntityManager entityManager;
    @Autowired
    private FarmingMachineManager farmingMachineManager;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        loggedUserConfiguration.replaceUser(user);
    }

    @Test
    void testCreateMachine() {
        FarmingMachineDTO machine = saveNewTestMachine();

        assertThat(machine.getId()).isNotNull();
        assertThat(machine.getModel()).isEqualTo("TEST_MODEL");
        assertThat(machine.getProducer()).isEqualTo("TEST_PRODUCER");
        assertThat(machine.getSupportedOperationTypes()).isEqualTo(Set.of(OperationType.CULTIVATION));

    }

    @Test
    void testGetAllMachines() {
        FarmingMachineDTO machine = saveNewTestMachine();

        Page<FarmingMachineDTO> machines = farmingMachineManager.getAllFarmingMachines(0);

        assertThat(machines.getTotalElements()).isEqualTo(9);
        assertThat(machines.getContent()).contains(machine);
    }

    private FarmingMachineDTO saveNewTestMachine() {
        return farmingMachineManager.addFarmingMachine(FarmingMachineDTO.builder()
                .model("TEST_MODEL")
                .producer("TEST_PRODUCER")
                .supportedOperationTypes(Set.of(OperationType.CULTIVATION))
                .build());
    }

    @Test
    void testGetDefaultMachines() {
        FarmingMachineDTO machine = saveNewTestMachine();

        Page<FarmingMachineDTO> machines = farmingMachineManager.getDefaultFarmingMachines(0);

        assertThat(machines.getTotalElements()).isEqualTo(8);
        assertThat(machines.getContent().contains(machine)).isFalse();
    }

    @Test
    void testGetUserMachines() {
        FarmingMachineDTO machine = saveNewTestMachine();

        Page<FarmingMachineDTO> machines = farmingMachineManager.getUserFarmingMachines(0);

        assertThat(machines.getTotalElements()).isEqualTo(1);
        assertThat(machines.getContent()).contains(machine);
    }

    @Test
    void testUpdateMachine() {
        FarmingMachineDTO toUpdate = saveNewTestMachine();
        toUpdate.setModel("TEST_UPDATE");

        FarmingMachineDTO machineUpdated = farmingMachineManager.updateFarmingMachine(toUpdate);

        assertThat(machineUpdated.getId()).isEqualTo(toUpdate.getId());
        assertThat(machineUpdated.getModel()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() {
        FarmingMachineDTO farmingMachineToUpdate = farmingMachineManager.getDefaultFarmingMachines(0).stream().findFirst().orElseThrow();
        farmingMachineToUpdate.setModel("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> farmingMachineManager.updateFarmingMachine(farmingMachineToUpdate));
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachineToUpdate.getId()).getModel()).isNotEqualTo("TEST_UPDATE");
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachineToUpdate.getId()).getModel()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteMachine() {
        FarmingMachineDTO machine = saveNewTestMachine();
        farmingMachineManager.deleteFarmingMachineSafe(machine.getId());
        assertThrows(IllegalArgumentException.class, () -> farmingMachineManager.getFarmingMachineById(machine.getId()));
    }

    @Test
    void testDeleteFailedAccessDenied() {
        FarmingMachineDTO machine = farmingMachineManager.getDefaultFarmingMachines(0).getContent().get(1);
        assertThrows(IllegalAccessError.class, () -> farmingMachineManager.deleteFarmingMachineSafe(machine.getId()));
        assertThat(farmingMachineManager.getFarmingMachineById(machine.getId())).isNotEqualTo(FarmingMachine.NONE);
    }

}
