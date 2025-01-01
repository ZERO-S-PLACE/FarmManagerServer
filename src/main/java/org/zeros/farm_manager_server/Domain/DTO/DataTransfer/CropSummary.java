package org.zeros.farm_manager_server.Domain.DTO.DataTransfer;

import jakarta.persistence.Transient;
import lombok.*;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CropSummary {

    @NonNull
    @Builder.Default
    private UUID cropId = ApplicationDefaults.UUID_UNDEFINED;

    @NonNull
    @Builder.Default
    private BigDecimal area=BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    private Map<ResourceType, BigDecimal> yieldPerAreaUnit = new HashMap<>();

    @NonNull
    @Builder.Default
    private Map<ResourceType, BigDecimal> meanSellPrice = new HashMap<>();

    @NonNull
    @Builder.Default
    private Map<ResourceType, BigDecimal> estimatedAmountNotSoldPerAreaUnit = new HashMap<>();

    @NonNull
    @Builder.Default
    private BigDecimal totalFuelCostPerAreaUnit = BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    private BigDecimal totalFertilizerCostPerAreaUnit = BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    private BigDecimal totalSprayCostPerAreaUnit = BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    private BigDecimal totalSubsidesValuePerAreaUnit = BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    Boolean estimatedValue = true;

    @Transient
    public static final CropSummary NONE = CropSummary.builder().build();

}
