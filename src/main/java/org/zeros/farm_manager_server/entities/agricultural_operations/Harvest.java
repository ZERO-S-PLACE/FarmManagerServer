package org.zeros.farm_manager_server.entities.agricultural_operations;

import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.zeros.farm_manager_server.entities.Crops.CropParameters.CropParameters;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Harvest extends AgriculturalOperation{

    private Float quantityPerAreaUnit;
    private CropParameters cropParameters;
}
