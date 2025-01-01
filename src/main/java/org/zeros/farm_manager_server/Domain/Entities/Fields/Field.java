package org.zeros.farm_manager_server.Domain.Entities.Fields;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Util.StringSetDatabaseConverter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"user", "fieldGroup", "fieldParts",})
public class Field extends BaseEntity {

    @NonNull
    @NotBlank
    private String fieldName;

    @NonNull
    @Builder.Default
    private String description = "";

    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal area = BigDecimal.ZERO;

    @NonNull
    @ManyToOne
    @Builder.Default
    private User user=User.NONE;

    @NonNull
    @Builder.Default
    @ManyToOne
    private FieldGroup fieldGroup=FieldGroup.NONE;

    @NonNull
    @Builder.Default
    @OneToMany(mappedBy = "field", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<FieldPart> fieldParts=new HashSet<>();

    @NonNull
    @Builder.Default
    @Convert(converter = StringSetDatabaseConverter.class)
    private Set<String> surveyingPlots = new HashSet<>();

    @NonNull
    @Builder.Default
    private Boolean isOwnField = true;

    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal propertyTax = BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal rent = BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    private Boolean isArchived = false;



    @Transient
    public static final Field NONE = Field.builder()
            .fieldName("NONE")
            .build();

    public void setUser(User user) {
        this.user = user;
        user.addField(this);
    }
}
