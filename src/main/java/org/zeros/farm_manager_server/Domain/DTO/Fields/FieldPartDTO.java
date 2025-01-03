package org.zeros.farm_manager_server.Domain.DTO.Fields;

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
public class FieldPartDTO extends BaseEntityDTO {
    private String fieldPartName;
    private String description;
    private float area;
    private Boolean isArchived;
    private Set<UUID> crops;
    private UUID field;

}
