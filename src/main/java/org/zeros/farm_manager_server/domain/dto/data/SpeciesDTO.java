package org.zeros.farm_manager_server.domain.dto.data;

import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.dto.BaseEntityDTO;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SpeciesDTO extends BaseEntityDTO {
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
    private String name;
    private String family;
    private String description;
}
