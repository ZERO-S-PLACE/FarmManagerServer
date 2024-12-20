package org.zeros.farm_manager_server.entities.fields;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.util.StringSetDatabaseConverter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"user", "fieldGroup", "fieldParts",})
public class Field extends DatabaseEntity {

    @Transient
    public static final Field NONE = Field.builder().fieldName("NONE").build();
    @NonNull
    @NotBlank
    private String fieldName;
    @Builder.Default
    private String description = "";
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal area = BigDecimal.ZERO;
    @ManyToOne
    private User user;
    @ManyToOne
    private FieldGroup fieldGroup;
    @OneToMany(mappedBy = "field", fetch = FetchType.EAGER)
    private Set<FieldPart> fieldParts;
    @NonNull
    @Builder.Default
    @Convert(converter = StringSetDatabaseConverter.class)
    private Set<String> surveyingPlots =new HashSet<>();
    @NonNull
    @Builder.Default
    private Boolean isOwnField = true;
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal propertyTax = BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal rent = BigDecimal.ZERO;
    @Builder.Default
    @NonNull
    private Boolean isArchived = false;

    public void setUser(User user) {
        this.user = user;
        user.addField(this);
    }
}
