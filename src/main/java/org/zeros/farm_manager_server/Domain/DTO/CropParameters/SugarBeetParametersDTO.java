package org.zeros.farm_manager_server.Domain.DTO.CropParameters;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SugarBeetParametersDTO extends CropParametersDTO {
    private float sugarContent;
}
