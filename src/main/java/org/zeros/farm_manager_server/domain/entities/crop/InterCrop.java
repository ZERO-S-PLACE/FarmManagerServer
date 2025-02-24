package org.zeros.farm_manager_server.domain.entities.crop;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.entities.fields.FieldPart;
import org.zeros.farm_manager_server.model.ApplicationDefaults;

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

    @Transient
    public static final InterCrop NONE = InterCrop.builder()
            .fieldPart(FieldPart.NONE)
            .build();
    @NonNull
    @Builder.Default
    private LocalDate dateDestroyed = ApplicationDefaults.UNDEFINED_DATE_MAX;
}
