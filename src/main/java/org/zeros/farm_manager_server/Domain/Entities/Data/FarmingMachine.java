package org.zeros.farm_manager_server.Domain.Entities.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;

import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FarmingMachine extends BaseEntity {

    @Transient
    public final static FarmingMachine NONE = FarmingMachine.builder()
            .producer("NONE")
            .model("NONE")
            .supportedOperationTypes(Set.of(OperationType.NONE)).build();
    @Transient
    public final static FarmingMachine UNDEFINED = FarmingMachine.builder()
            .producer("UNDEFINED")
            .model("UNDEFINED")
            .supportedOperationTypes(Set.of(OperationType.ANY))
            .build();
    @NonNull
    @NotBlank
    private String producer;
    @NonNull
    @NotBlank
    private String model;
    @NonNull
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<OperationType> supportedOperationTypes = Set.of(OperationType.NONE);
    @NonNull
    @Builder.Default
    private String description = "";
    @NonNull
    @Builder.Default
    private String createdBy = "ADMIN";


}
