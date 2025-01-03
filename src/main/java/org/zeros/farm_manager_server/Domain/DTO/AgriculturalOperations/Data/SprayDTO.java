package org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data;

import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.SprayType;

import java.util.Set;
import java.util.UUID;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SprayDTO extends BaseEntityDTO {
    private String name;
    private String producer;
    private SprayType sprayType;
    private Set<String> activeSubstances;
    private String description;

    @Transient
    public static final SprayDTO UNDEFINED = SprayDTO.builder()
            .name("UNDEFINED")
            .producer("UNDEFINED")
            .sprayType(SprayType.OTHER)
            .build();
}
