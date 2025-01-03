package org.zeros.farm_manager_server.Domain.DTO.Crop.Crop;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class MainCropDTO extends CropDTO {

    private Set<UUID> harvest;
    private Set<UUID> cropSales;
    private Boolean isFullySold;
}
