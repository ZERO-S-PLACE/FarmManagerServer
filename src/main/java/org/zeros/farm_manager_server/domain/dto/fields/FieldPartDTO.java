package org.zeros.farm_manager_server.domain.dto.fields;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.dto.BaseEntityDTO;

import java.math.BigDecimal;
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
    private BigDecimal area;
    private Boolean isArchived;
    private Set<UUID> crops;
    private UUID field;

}
