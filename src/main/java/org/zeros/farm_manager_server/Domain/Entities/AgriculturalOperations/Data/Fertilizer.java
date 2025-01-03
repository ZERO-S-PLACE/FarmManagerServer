package org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Fertilizer extends BaseEntity {

    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @Builder.Default
    private String producer = "";

    @NonNull
    @Builder.Default
    private Boolean isNaturalFertilizer = false;

    @NonNull
    @Builder.Default
    private String createdBy = "ADMIN";

    /*PERCENTAGE OF PURE ELEMENTS*/
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal organicMatterPercent = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "total_n_percent")
    private BigDecimal totalNPercent = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "total_p_percent")
    private BigDecimal  totalPPercent = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "total_k_percent")
    private BigDecimal  totalKPercent = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "total_ca_percent")
    private BigDecimal  totalCaPercent = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "total_mg_percent")
    private BigDecimal  totalMgPercent = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "total_s_percent")
    private BigDecimal  totalSPercent = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "total_na_percent")
    private BigDecimal  totalNaPercent = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "total_cl_percent")
    private BigDecimal  totalClPercent = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "total_fe_percent")
    private BigDecimal  totalFePercent = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "total_b_percent")
    private BigDecimal  totalBPercent = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "total_si_percent")
    private BigDecimal  totalSiPercent = BigDecimal.ZERO;

    @Transient
    public final static Fertilizer NONE = Fertilizer.builder()
            .name("NONE")
            .producer("NONE")
            .build();
    @Transient
    public final static Fertilizer UNDEFINED = Fertilizer.builder()
            .name("UNDEFINED")
            .producer("UNDEFINED")
            .build();
}
