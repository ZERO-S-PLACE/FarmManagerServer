package org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class RapeSeedParametersDTO extends CropParametersDTO {
    private BigDecimal density;
    private BigDecimal humidity;
    private BigDecimal oilContent;

}
