package org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FertilizerApplication extends AgriculturalOperation {
    @NonNull
    @ManyToOne
    private Fertilizer fertilizer;
    @NonNull
    @Builder.Default
    private BigDecimal quantityPerAreaUnit=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    private BigDecimal pricePerUnit=BigDecimal.ZERO;

}
