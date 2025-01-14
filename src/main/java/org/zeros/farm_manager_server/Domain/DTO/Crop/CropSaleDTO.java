package org.zeros.farm_manager_server.Domain.DTO.Crop;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CropSaleDTO extends BaseEntityDTO {
    private UUID id;
    private UUID crop;
    private LocalDate dateSold;
    private String soldTo;
    private BigDecimal amountSold;
    private BigDecimal pricePerUnit;
    private ResourceType resourceType;
    private String unit;
    private UUID cropParameters;
}
