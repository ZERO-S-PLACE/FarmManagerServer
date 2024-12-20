package org.zeros.farm_manager_server.entities.AgriculturalOperations.Data;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.OperationType;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.OperationTypesConverter;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmingMachine extends DatabaseEntity {
    @NonNull
    @NotBlank
    private String producer;
    @NonNull
    @NotBlank
    private String model;
    @NonNull
    @Convert(converter = OperationTypesConverter.class)
    private Set<OperationType> supportedOperationTypes;
    @Builder.Default
    private String description="";
    @NonNull
    @Builder.Default
    private String createdBy="ADMIN";
    @Transient
    public final static FarmingMachine NONE= FarmingMachine.builder()
            .producer("NONE")
            .model("NONE")
            .supportedOperationTypes(Set.of(OperationType.ANY)).build();
}
