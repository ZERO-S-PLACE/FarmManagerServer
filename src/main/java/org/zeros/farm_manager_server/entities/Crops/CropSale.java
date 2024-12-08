package org.zeros.farm_manager_server.entities.Crops;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.zeros.farm_manager_server.entities.Crops.CropParameters.CropParameters;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CropSale extends DatabaseEntity {
    private Float amountSold;
    private Float pricePerUnit;
    @OneToOne
    private CropParameters cropParameters;
}
