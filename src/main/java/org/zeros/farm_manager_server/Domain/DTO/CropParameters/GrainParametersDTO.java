package org.zeros.farm_manager_server.Domain.DTO.CropParameters;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class GrainParametersDTO extends CropParametersDTO {
    private float glutenContent;
    private float proteinContent;
    private float fallingNumber;
    private float density;
    private float humidity;
}
