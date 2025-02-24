package org.zeros.farm_manager_server.domain.dto.fields;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.dto.BaseEntityDTO;

import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class FieldGroupDTO extends BaseEntityDTO {
    private String fieldGroupName;
    private String description;
    private Set<UUID> fields;
    private UUID user;
}
