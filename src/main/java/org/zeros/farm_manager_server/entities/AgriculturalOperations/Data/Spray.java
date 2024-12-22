package org.zeros.farm_manager_server.entities.AgriculturalOperations.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.SprayType;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
import org.zeros.farm_manager_server.util.StringSetDatabaseConverter;

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
    @Builder.Default
    private String producer="";
    @NonNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SprayType sprayType=SprayType.OTHER;
    @NonNull
    @Builder.Default
    @ElementCollection
    private Set<String> activeSubstances=new HashSet<>();
    @NonNull
    @Builder.Default
    private String description="";
    @NonNull
    @Builder.Default
    private String createdBy="ADMIN";
    @Transient
    public static final Spray NONE =Spray.builder().name("NONE").producer("NONE").build();

    @Transient
    public static final Spray UNDEFINED =Spray.builder().name("UNDEFINED").producer("UNDEFINED").build();

}
