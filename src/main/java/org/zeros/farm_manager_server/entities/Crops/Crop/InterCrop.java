package org.zeros.farm_manager_server.entities.Crops.Crop;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.Cultivation;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("INTER_CROP")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InterCrop extends Crop {

    @Builder.Default
    @NonNull
    private LocalDate dateDestroyed=LocalDate.MAX;
}
