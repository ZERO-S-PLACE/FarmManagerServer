package org.zeros.farm_manager_server.Domain.DataTransfer;

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


    private float area;

    @NonNull
    @Builder.Default
    private Map<UUID,BigDecimal> sprayPerAreaUnit=new HashMap<>();

    @NonNull
    @Builder.Default
    private Map<UUID,BigDecimal> fertilizerPerAreaUnit=new HashMap<>();

    @NonNull
    @Builder.Default
    private Map<Set<UUID>,BigDecimal> seedingMaterialPerAreaUnit=new HashMap<>();

    @Transient
    public static final ResourcesSummary NONE = ResourcesSummary.builder().build();
}