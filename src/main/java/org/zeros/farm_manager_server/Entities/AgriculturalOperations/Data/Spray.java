package org.zeros.farm_manager_server.Entities.AgriculturalOperations.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Enum.SprayType;
import org.zeros.farm_manager_server.Entities.DatabaseEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Spray extends DatabaseEntity {

    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @Builder.Default
    private String producer = "";

    @NonNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SprayType sprayType = SprayType.OTHER;

    @NonNull
    @Builder.Default
    @ElementCollection
    private Set<String> activeSubstances = new HashSet<>();

    @NonNull
    @Builder.Default
    private String description = "";

    @NonNull
    @Builder.Default
    private String createdBy = "ADMIN";

    @Transient
    public static final Spray NONE = Spray.builder()
            .name("NONE")
            .producer("NONE")
            .build();
    @Transient
    public static final Spray UNDEFINED = Spray.builder()
            .name("UNDEFINED")
            .producer("UNDEFINED")
            .build();
}
