package org.zeros.farm_manager_server.Entities.Crop.Plant;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.zeros.farm_manager_server.Entities.BaseEntity;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Species extends BaseEntity {


    @NonNull
    private String name;

    @NotNull
    private String family;

    @NonNull
    @Builder.Default
    private String description = "";

    @NonNull
    @Builder.Default
    private String createdBy = "ADMIN";

    @Transient
    public static final Species ANY = Species.builder()
            .name("ANY")
            .family("ANY")
            .build();

    @Transient
    public static final Species NONE = Species.builder()
            .name("NONE")
            .family("NONE")
            .build();

}
