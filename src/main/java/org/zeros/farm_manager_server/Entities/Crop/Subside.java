package org.zeros.farm_manager_server.Entities.Crop;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.zeros.farm_manager_server.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Entities.DatabaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"speciesAllowed"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subside extends DatabaseEntity {

    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @Builder.Default
    private String description = "";

    @NonNull
    @Builder.Default
    private LocalDate yearOfSubside = LocalDate.now();

    @NonNull
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "subside_species",
            joinColumns = @JoinColumn(name = "subside_id"),
            inverseJoinColumns = @JoinColumn(name = "species_id")
    )
    private Set<Species> speciesAllowed= new HashSet<>();

    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal subsideValuePerAreaUnit = BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    private String createdBy = "ADMIN";

    @Transient
    public static final Subside NONE = Subside.builder()
            .name("NONE")
            .speciesAllowed(Set.of(Species.NONE))
            .build();
}
