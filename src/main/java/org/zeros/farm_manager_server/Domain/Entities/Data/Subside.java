package org.zeros.farm_manager_server.Domain.Entities.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;

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
@SuperBuilder
public class Subside extends BaseEntity {

    @Transient
    public static final Subside NONE = Subside.builder()
            .name("NONE")
            .speciesAllowed(Set.of(Species.NONE))
            .build();
    @NonNull
    @NotBlank
    private String name;
    @NonNull
    @Builder.Default
    private String description = "";
    @NonNull
    @Builder.Default
    private LocalDate yearOfSubside = ApplicationDefaults.UNDEFINED_DATE_MIN;
    @NonNull
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "subside_species",
            joinColumns = @JoinColumn(name = "subside_id"),
            inverseJoinColumns = @JoinColumn(name = "species_id")
    )
    private Set<Species> speciesAllowed = new HashSet<>();
    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal subsideValuePerAreaUnit = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    private String createdBy = "ADMIN";

    public void setYearOfSubside(LocalDate date) {
        yearOfSubside = LocalDate.ofYearDay(date.getYear(), 1);
    }
}
