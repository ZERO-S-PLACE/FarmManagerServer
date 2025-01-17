package org.zeros.farm_manager_server.Domain.DTO.Data;

import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.Enum.SprayType;

import java.util.Set;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SprayDTO extends BaseEntityDTO {
    @Transient
    public static final SprayDTO UNDEFINED = SprayDTO.builder()
            .name("UNDEFINED")
            .producer("UNDEFINED")
            .sprayType(SprayType.OTHER)
            .build();
    private String name;
    private String producer;
    private SprayType sprayType;
    private Set<String> activeSubstances;
    private String description;
}
