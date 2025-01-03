package org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data;

import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Fertilizer;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class FertilizerDTO extends BaseEntityDTO {
    private String name;
    private String producer;
    private Boolean isNaturalFertilizer;
    private float organicMatterPercent;
    private float totalNPercent;
    private float totalPPercent;
    private float totalKPercent;
    private float totalCaPercent;
    private float totalMgPercent;
    private float totalSPercent;
    private float totalNaPercent;
    private float totalClPercent;
    private float totalFePercent;
    private float totalBPercent;
    private float totalSiPercent;

    @Transient
    public final static FertilizerDTO UNDEFINED = FertilizerDTO.builder()
            .name("UNDEFINED")
            .producer("UNDEFINED")
            .isNaturalFertilizer(false)
            .build();

}
