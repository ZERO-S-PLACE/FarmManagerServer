package org.zeros.farm_manager_server.entities.AgriculturalOperations.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Fertilizer extends DatabaseEntity {
    @NonNull
    @NotBlank
    private String name;
    @Builder.Default
    private String producer="";
    @NonNull
    @Builder.Default
    private Boolean isNaturalFertilizer=false;
    @NonNull
    @Builder.Default
    private String createdBy="ADMIN";

    /*PERCENTAGE OF PURE ELEMENTS*/

    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal organicMatterPercent=BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal N_Percent=BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal P_Percent=BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal K_Percent=BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal Ca_Percent=BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal Mg_Percent=BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal S_Percent=BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal Na_Percent=BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal Cl_Percent=BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal Fe_Percent=BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal B_Percent=BigDecimal.ZERO;


    @Transient
    public final static Fertilizer NONE=Fertilizer.builder().name("NONE").build();
}
