package org.zeros.farm_manager_server.entities.fields;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
import org.zeros.farm_manager_server.entities.User.User;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldPart extends DatabaseEntity {
    @NotBlank
    @NonNull
    @Builder.Default
    private String fieldPartName="";
    @Builder.Default
    private String description="";
    @Builder.Default
    private BigDecimal area=BigDecimal.ZERO;
    @Builder.Default
    private Boolean isArchived=false;
    @ManyToOne
    private Field field;
    //@OneToMany
    //private Set<Crop> crops;

@Transient
    public final static FieldPart NONE =FieldPart.builder().fieldPartName("NONE").field(Field.NONE).build();

public static FieldPart getDefaultFieldPart(Field field){
    return FieldPart.builder().fieldPartName("WHOLE").field(field).area(field.getArea()).build();
}
}
