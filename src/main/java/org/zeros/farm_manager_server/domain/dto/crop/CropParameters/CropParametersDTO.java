package org.zeros.farm_manager_server.domain.dto.crop.CropParameters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.dto.BaseEntityDTO;
import org.zeros.farm_manager_server.domain.enums.ResourceType;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CropParametersDTO.class, name = "COMMENT"),
        @JsonSubTypes.Type(value = GrainParametersDTO.class, name = "GRAIN"),
        @JsonSubTypes.Type(value = RapeSeedParametersDTO.class, name = "RAPE_SEED"),
        @JsonSubTypes.Type(value = SugarBeetParametersDTO.class, name = "SUGAR_BEET")
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
    private BigDecimal pollution;
    private ResourceType resourceType;
}
