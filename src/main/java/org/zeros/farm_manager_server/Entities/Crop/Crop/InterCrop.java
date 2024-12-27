package org.zeros.farm_manager_server.Entities.Crop.Crop;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;

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

    @NonNull
    @Builder.Default
    private LocalDate dateDestroyed = ApplicationDefaults.UNDEFINED_DATE_MAX;

    @Transient
    public static final InterCrop NONE = InterCrop.builder()
            .fieldPart(FieldPart.NONE)
            .build();
}
