package org.zeros.farm_manager_server.Domain.DTO.Data;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SubsideDTO extends BaseEntityDTO {
    private String name;
    private String description;
    private LocalDate yearOfSubside;
    private Set<UUID> speciesAllowed;
    private BigDecimal subsideValuePerAreaUnit;

    public void setYearOfSubside(LocalDate date) {
        yearOfSubside = LocalDate.ofYearDay(date.getYear(), 1);
    }
}
