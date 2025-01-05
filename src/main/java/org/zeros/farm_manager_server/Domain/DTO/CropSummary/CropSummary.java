package org.zeros.farm_manager_server.Domain.DTO.CropSummary;

import jakarta.persistence.Transient;
import lombok.*;
import org.zeros.farm_manager_server.Domain.Entities.Enum.ResourceType;
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

    @Transient
    public static final CropSummary NONE = CropSummary.builder().build();
    @NonNull
    @Builder.Default
    private Boolean estimatedValue = true;
    @NonNull
    @Builder.Default
    private UUID cropId = ApplicationDefaults.UUID_UNDEFINED;
    private float area;
    @NonNull
    @Builder.Default
    private Map<ResourceType, BigDecimal> yieldPerAreaUnit = new HashMap<>();
    @NonNull
    @Builder.Default
    private Map<ResourceType, BigDecimal> meanSellPrice = new HashMap<>();
    @NonNull
    @Builder.Default
    private Map<ResourceType, BigDecimal> estimatedAmountNotSoldPerAreaUnit = new HashMap<>();
    private float totalFuelCostPerAreaUnit;
    private float totalFertilizerCostPerAreaUnit;
    private float totalSprayCostPerAreaUnit;
    private float totalSubsidesValuePerAreaUnit;

}
