package org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class RapeSeedParametersDTO extends CropParametersDTO {
    private float density;
    private float humidity;
    private float oilContent;

}
