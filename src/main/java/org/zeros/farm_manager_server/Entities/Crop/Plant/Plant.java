package org.zeros.farm_manager_server.Entities.Crop.Plant;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.zeros.farm_manager_server.Entities.DatabaseEntity;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plant extends DatabaseEntity {

    @NonNull
    @OneToOne
    private Species species;

    @NonNull
    @NotBlank
    private String variety;

    @NonNull
    @Builder.Default
    private LocalDate registrationDate= ApplicationDefaults.UNDEFINED_DATE_MIN;

    @NonNull
    @Builder.Default
    private String productionCompany="";

    @NonNull
    @Builder.Default
    private String description="";

    @NonNull
    @Builder.Default
    private String countryOfOrigin="";

    @NonNull
    @Builder.Default
    private String createdBy="ADMIN";

@Transient
    public static final Plant NONE =Plant.builder().species(Species.NONE).variety("NONE").build();
}
