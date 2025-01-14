package org.zeros.farm_manager_server.Domain.DTO.Data;

import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class FertilizerDTO extends BaseEntityDTO {
    @Transient
    public final static FertilizerDTO UNDEFINED = FertilizerDTO.builder()
            .name("UNDEFINED")
            .producer("UNDEFINED")
            .isNaturalFertilizer(false)
            .build();
    private String name;
    private String producer;
    private Boolean isNaturalFertilizer;
    private BigDecimal organicMatterPercent;
    private BigDecimal totalNPercent;
    private BigDecimal totalPPercent;
    private BigDecimal totalKPercent;
    private BigDecimal totalCaPercent;
    private BigDecimal totalMgPercent;
    private BigDecimal totalSPercent;
    private BigDecimal totalNaPercent;
    private BigDecimal totalClPercent;
    private BigDecimal totalFePercent;
    private BigDecimal totalBPercent;
    private BigDecimal totalSiPercent;

}
