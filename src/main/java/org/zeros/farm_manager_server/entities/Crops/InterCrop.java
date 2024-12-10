package org.zeros.farm_manager_server.entities.Crops;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
/*import org.zeros.farm_manager_server.entities.AgriculturalOperations.Cultivation;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("INTER_CROP")
public class InterCrop extends Crop{
@OneToOne
    private Cultivation destructiveCultivation;
}
*/