package org.zeros.farm_manager_server.domain.dto.crop.CropSummary;

import jakarta.persistence.Transient;
import lombok.*;
import org.zeros.farm_manager_server.model.ApplicationDefaults;

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


    @Transient
    public static final ResourcesSummary NONE = ResourcesSummary.builder().build();
    @NonNull
    @Builder.Default
    private UUID cropId = ApplicationDefaults.UUID_UNDEFINED;
    private BigDecimal area;
    @NonNull
    @Builder.Default
    private Map<UUID, BigDecimal> sprayPerAreaUnit = new HashMap<>();
    @NonNull
    @Builder.Default
    private Map<UUID, BigDecimal> fertilizerPerAreaUnit = new HashMap<>();
    @NonNull
    @Builder.Default
    private Map<Set<UUID>, BigDecimal> seedingMaterialPerAreaUnit = new HashMap<>();
}
