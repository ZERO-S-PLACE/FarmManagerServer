package org.zeros.farm_manager_server.entities.fields;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
import org.zeros.farm_manager_server.entities.User;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Field extends DatabaseEntity {

    private String fieldName;
    private String description;

    private BigDecimal area;
    @ManyToOne
    private User user;
    @ManyToOne
    private FieldGroup fieldGroup;
    @OneToMany(mappedBy = "id")
    private Set<FieldPart> fieldParts;

    private String surveyingPlots;
    private Boolean isOwnField;
    private BigDecimal propertyTax;
    private BigDecimal rent;
    private Boolean isArchived;


    public void setUser(User user){
        this.user = user;
        user.addField(this);
    }
}
