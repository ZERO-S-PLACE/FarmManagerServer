package org.zeros.farm_manager_server.entities.Crops.CropParameters;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("GRAIN")
public class GrainParameters extends CropParameters {
    private Float glutenContent;
    private Float proteinContent;
    private Float fallingNumber;
    private Float density;
    private Float humidity;
    private Float pollution;
}
