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
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.FarmingMachineDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.Data.FarmingMachineRepository;
import org.zeros.farm_manager_server.Services.Default.Data.FarmingMachineManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@Import({UserFieldsManagerDefault.class, FarmingMachineManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
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

        FarmingMachine machine = saveNewTestMachine();
        assertThat(machine.getId()).isNotNull();
        assertThat(machine.getModel()).isEqualTo("TEST_MODEL");
        assertThat(machine.getProducer()).isEqualTo("TEST_PRODUCER");
        assertThat(machine.getSupportedOperationTypes()).isEqualTo(Set.of(OperationType.CULTIVATION));
        assertThat(machine.getCreatedBy()).isEqualTo(user.getUsername());
        assertThat(farmingMachineRepository.findById(machine.getId()).get()).isEqualTo(machine);
    }

    @Test
    void testGetAllMachines() {
        FarmingMachine machine = saveNewTestMachine();
        Page<FarmingMachine> machines = farmingMachineManager.getAllFarmingMachines(0);
        assertThat(machines.getTotalElements()).isEqualTo(9);
        assertThat(machines.getContent()).contains(machine);
    }

    private FarmingMachine saveNewTestMachine() {
        return farmingMachineManager.addFarmingMachine(FarmingMachineDTO.builder()
                .model("TEST_MODEL")
                .producer("TEST_PRODUCER")
                .supportedOperationTypes(Set.of(OperationType.CULTIVATION))
                .build());
    }

    @Test
    void testGetDefaultMachines() {
        FarmingMachine machine = saveNewTestMachine();
        Page<FarmingMachine> machines = farmingMachineManager.getDefaultFarmingMachines(0);
        assertThat(machines.getTotalElements()).isEqualTo(8);
        assertThat(machines.getContent().contains(machine)).isFalse();
    }

    @Test
    void testGetUserMachines() {
        FarmingMachine machine = saveNewTestMachine();
        Page<FarmingMachine> machines = farmingMachineManager.getUserFarmingMachines(0);
        assertThat(machines.getTotalElements()).isEqualTo(1);
        assertThat(machines.getContent()).contains(machine);
    }

    @Test
    void testUpdateMachine() {
        FarmingMachine machine = saveNewTestMachine();
        FarmingMachine toUpdate = farmingMachineManager.getFarmingMachineById(machine.getId());
        entityManager.detach(toUpdate);
        toUpdate.setModel("TEST_UPDATE");
        FarmingMachine machineUpdated = farmingMachineManager.updateFarmingMachine(
                DefaultMappers.farmingMachineMapper.entityToDto(toUpdate));
        assertThat(machineUpdated.getId()).isEqualTo(toUpdate.getId());
        assertThat(machineUpdated.getModel()).isEqualTo("TEST_UPDATE");
    }

    @Test
    void testUpdateFailedAccessDenied() {
        FarmingMachine farmingMachineToUpdate = farmingMachineManager.getDefaultFarmingMachines(0).stream().findFirst().orElse(FarmingMachine.NONE);
        entityManager.detach(farmingMachineToUpdate);
        farmingMachineToUpdate.setModel("TEST_UPDATE");
        assertThrows(IllegalAccessError.class, () -> farmingMachineManager.updateFarmingMachine(
                DefaultMappers.farmingMachineMapper.entityToDto(farmingMachineToUpdate)));
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachineToUpdate.getId()).getModel()).isNotEqualTo("TEST_UPDATE");
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachineToUpdate.getId()).getModel()).isNotEqualTo("NONE");
    }

    @Test
    void testDeleteMachine() {
        FarmingMachine machine = saveNewTestMachine();
        farmingMachineManager.deleteFarmingMachineSafe(machine.getId());
        assertThat(farmingMachineManager.getFarmingMachineById(machine.getId())).isEqualTo(FarmingMachine.NONE);
    }

    @Test
    void testDeleteFailedAccessDenied() {
        FarmingMachine machine = farmingMachineManager.getDefaultFarmingMachines(0).getContent().get(1);
        assertThrows(IllegalAccessError.class, () -> farmingMachineManager.deleteFarmingMachineSafe(machine.getId()));
        assertThat(farmingMachineManager.getFarmingMachineById(machine.getId())).isNotEqualTo(FarmingMachine.NONE);
    }

}
