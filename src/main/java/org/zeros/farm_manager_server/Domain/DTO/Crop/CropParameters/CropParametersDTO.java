package org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters;

import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CropParametersDTO extends BaseEntityDTO {
    private String name;
    private String comment;
    private float pollution;
    private ResourceType resourceType;
    @Transient
    public static final CropParametersDTO UNDEFINED = CropParametersDTO.builder()
            .name("UNDEFINED")
            .resourceType(ResourceType.ANY)
            .comment("UNDEFINED")
            .build();
}
