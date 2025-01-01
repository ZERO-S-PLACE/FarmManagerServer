package org.zeros.farm_manager_server.Domain.DTO.DataTransfer;

import jakarta.persistence.Transient;
import lombok.*;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourcesSummary {


    @NonNull
    @Builder.Default
    private UUID cropId =ApplicationDefaults.UUID_UNDEFINED;

    @NonNull
    @Builder.Default
    private BigDecimal area=BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    private Map<Spray,BigDecimal> sprayPerAreaUnit=new HashMap<>();

    @NonNull
    @Builder.Default
    private Map<Fertilizer,BigDecimal> fertilizerPerAreaUnit=new HashMap<>();

    @NonNull
    @Builder.Default
    private Map<Set<Plant>,BigDecimal> seedingMaterialPerAreaUnit=new HashMap<>();

    @Transient
    public static final ResourcesSummary NONE = ResourcesSummary.builder().build();
}
