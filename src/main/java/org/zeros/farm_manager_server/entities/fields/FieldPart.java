package org.zeros.farm_manager_server.entities.fields;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.zeros.farm_manager_server.entities.Crops.Crop.Crop;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true,exclude = {"crops","field"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldPart extends DatabaseEntity {
    @Transient
    public final static FieldPart NONE = FieldPart.builder().fieldPartName("NONE").field(Field.NONE).build();
    @NotBlank
    @NonNull
    @Builder.Default
    private String fieldPartName = "";
    @Builder.Default
    private String description = "";
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    private BigDecimal area = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    private Boolean isArchived = false;
    @NonNull
    @Builder.Default
    @OneToMany(mappedBy = "fieldPart",fetch = FetchType.EAGER)
    private Set<Crop> crops=new HashSet<>();

    @NonNull
    @ManyToOne
    @Builder.Default
    private Field field=Field.NONE;

    public static FieldPart getDefaultFieldPart(Field field) {
        return FieldPart.builder().fieldPartName("WHOLE").field(field).area(field.getArea()).build();
    }

    public void addCrop(Crop crop) {
            crops.add(crop);
    }
}
