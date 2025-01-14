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
public class GrainParametersDTO extends CropParametersDTO {
    private BigDecimal glutenContent;
    private BigDecimal proteinContent;
    private BigDecimal fallingNumber;
    private BigDecimal density;
    private BigDecimal humidity;
}
