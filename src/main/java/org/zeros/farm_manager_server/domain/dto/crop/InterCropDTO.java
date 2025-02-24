package org.zeros.farm_manager_server.domain.dto.crop;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class InterCropDTO extends CropDTO {
    private LocalDate dateDestroyed;
}
