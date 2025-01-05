package org.zeros.farm_manager_server.Domain.DTO.CropParameters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CropParametersDTO.class, name = "CropParameters"),
        @JsonSubTypes.Type(value = GrainParametersDTO.class, name = "GrainParameters"),
        @JsonSubTypes.Type(value = RapeSeedParametersDTO.class, name = "RapeSeedParameters"),
        @JsonSubTypes.Type(value = SugarBeetParametersDTO.class, name = "SugarBeetParameters")
})
public class CropParametersDTO extends BaseEntityDTO {
    @Transient
    public static final CropParametersDTO UNDEFINED = CropParametersDTO.builder()
            .name("UNDEFINED")
            .resourceType(ResourceType.ANY)
            .comment("UNDEFINED")
            .build();
    private String name;
    private String comment;
    private float pollution;
    private ResourceType resourceType;
}
