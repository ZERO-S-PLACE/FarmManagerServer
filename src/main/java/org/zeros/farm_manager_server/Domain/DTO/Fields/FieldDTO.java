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
public class FieldDTO extends BaseEntityDTO {
    private String fieldName;
    private String description;
    private float area;
    private UUID user;
    private UUID fieldGroup;
    private Set<UUID> fieldParts;
    private Set<String> surveyingPlots;
    private Boolean isOwnField;
    private float propertyTax;
    private float rent;
    private Boolean isArchived;
}
