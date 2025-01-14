package org.zeros.farm_manager_server.Domain.DTO.Crop;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;

import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CropDTO extends BaseEntityDTO {
    private Boolean workFinished;
    private UUID fieldPart;
    private Set<UUID> cultivatedPlants;
    private Set<UUID> seeding;
    private Set<UUID> cultivations;
    private Set<UUID> sprayApplications;
    private Set<UUID> fertilizerApplications;
    private Set<UUID> subsides;
}
