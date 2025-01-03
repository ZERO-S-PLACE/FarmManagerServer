package org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Operations;


import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.CultivationType;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CultivationDTO extends AgriculturalOperationDTO {
    private float depth;
    private CultivationType cultivationType;
}
