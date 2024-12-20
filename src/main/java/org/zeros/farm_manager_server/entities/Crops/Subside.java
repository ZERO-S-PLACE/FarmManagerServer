package org.zeros.farm_manager_server.entities.Crops;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.zeros.farm_manager_server.entities.Crops.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true,exclude = {"speciesAllowed"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subside extends DatabaseEntity {

    @NonNull
    @NotBlank
    private String name;
    @NonNull
    @Builder.Default
    private String description="";
    @NonNull
    @Builder.Default
    private LocalDate yearOfSubside=LocalDate.MIN;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "subside_species",
            joinColumns = @JoinColumn(name = "subside_id"),
            inverseJoinColumns = @JoinColumn(name = "species_id")
    )
    private Set<Species> speciesAllowed;
    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal subsideValuePerAreaUnit=BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    private String createdBy="ADMIN";

    @Transient
    public static final Subside NONE =Subside.builder().name("NONE").speciesAllowed(Set.of(Species.NONE)).build();
}
