package org.zeros.farm_manager_server.mappers_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.zeros.farm_manager_server.domain.dto.data.FarmingMachineDTO;
import org.zeros.farm_manager_server.domain.entities.data.FarmingMachine;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.repositories.data.FarmingMachineRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({DefaultMappers.class})
public class FarmingMachineMapperTest {
    @Autowired
    FarmingMachineRepository farmingMachineRepository;

    FarmingMachine farmingMachine;

    @BeforeEach
    void setUp() {
        farmingMachine = farmingMachineRepository.findAll(PageRequest.of(0, 1)).stream().findFirst().get();
    }

    @Test
    public void testFarmingMachineToDTO() {
        FarmingMachineDTO dto = DefaultMappers.farmingMachineMapper.entityToDto(farmingMachine);
        assertThat(dto.getId()).isEqualTo(farmingMachine.getId());
        assertThat(dto.getSupportedOperationTypes()).isEqualTo(farmingMachine.getSupportedOperationTypes());
    }

    @Test
    public void testFarmingMachineToEntity() {
        FarmingMachineDTO dto = DefaultMappers.farmingMachineMapper.entityToDto(farmingMachine);
        dto.setDescription(null);
        FarmingMachine entity = DefaultMappers.farmingMachineMapper.dtoToEntitySimpleProperties(dto);
        assertThat(entity.getId()).isEqualTo(farmingMachine.getId());
        assertThat(entity.getSupportedOperationTypes()).isEqualTo(farmingMachine.getSupportedOperationTypes());
    }
}
