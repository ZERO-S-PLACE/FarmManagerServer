package org.zeros.farm_manager_server.entities.Crops.CropParameters;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
//@Entity
@DiscriminatorValue("SUGAR_BEET")
public class SugarBeetParameters extends CropParameters {
    private Float sugarContent;
    private Float pollution;
}
