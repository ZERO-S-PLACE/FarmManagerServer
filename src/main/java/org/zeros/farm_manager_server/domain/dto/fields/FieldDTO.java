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
public class FieldDTO extends BaseEntityDTO {
    private String fieldName;
    private String description;
    private BigDecimal area;
    private UUID user;
    private UUID fieldGroup;
    private Set<UUID> fieldParts;
    private Set<String> surveyingPlots;
    private Boolean isOwnField;
    private BigDecimal propertyTax;
    private BigDecimal rent;
    private Boolean isArchived;
}
