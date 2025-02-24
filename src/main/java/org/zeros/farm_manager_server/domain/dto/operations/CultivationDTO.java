package org.zeros.farm_manager_server.domain.dto.operations;


import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.enums.CultivationType;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CultivationDTO extends AgriculturalOperationDTO {
    private BigDecimal depth;
    private CultivationType cultivationType;
}
