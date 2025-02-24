package org.zeros.farm_manager_server.mappers_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.zeros.farm_manager_server.domain.dto.data.FertilizerDTO;
import org.zeros.farm_manager_server.domain.entities.data.Fertilizer;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.repositories.data.FertilizerRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({DefaultMappers.class})
public class FertilizerMapperTest {
    static Fertilizer fertilizer;
    @Autowired
    FertilizerRepository fertilizerRepository;

    @BeforeEach
    public void setUp() {
        fertilizer = fertilizerRepository.saveAndFlush(Fertilizer.builder()
                .createdBy("ADMIN")
                .name("TEST1")
                .producer("TEST2")
                .totalCaPercent(BigDecimal.valueOf(0.1))
                .build()
        );
    }


    @Test
    public void testFarmingMachineToDTO() {
        FertilizerDTO dto = DefaultMappers.fertilizerMapper.entityToDto(fertilizer);
        assertThat(dto.getId()).isEqualTo(fertilizer.getId());
        assertThat(dto.getName()).isEqualTo(fertilizer.getName());
        assertThat(dto.getTotalCaPercent().floatValue()).isEqualTo(fertilizer.getTotalCaPercent().floatValue());
    }

    @Test
    public void testFarmingMachineToEntity() {
        FertilizerDTO dto = DefaultMappers.fertilizerMapper.entityToDto(fertilizer);
        Fertilizer entity = DefaultMappers.fertilizerMapper.dtoToEntitySimpleProperties(dto);
        assertThat(entity.getId()).isEqualTo(fertilizer.getId());
        assertThat(entity.getName()).isEqualTo(fertilizer.getName());
        assertThat(entity.getTotalCaPercent().doubleValue()).isEqualTo(fertilizer.getTotalCaPercent().doubleValue());
    }
}
