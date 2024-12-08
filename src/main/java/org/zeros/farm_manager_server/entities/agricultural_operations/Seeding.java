package org.zeros.farm_manager_server.entities.agricultural_operations;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity

public class Seeding extends AgriculturalOperation {
    private Float depth;
    @Transient
    private Float quantityPerAreaUnit;

    private Float germinationRate;
    private Float materialPurity;
    private Float thousandSeedsMass;
    private Float seedsPerAreaUnit;


}
