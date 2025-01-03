package org.zeros.farm_manager_server.Domain.DTO.Crop.Plant;

import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;

import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SpeciesDTO extends BaseEntityDTO {
    private String name;
    private String family;
    private String description;

    @Transient
    public static final SpeciesDTO ANY = SpeciesDTO.builder()
            .name("ANY")
            .family("ANY")
            .build();

    @Transient
    public static final SpeciesDTO NONE = SpeciesDTO.builder()
            .name("NONE")
            .family("NONE")
            .build();
}
