package org.zeros.farm_manager_server.entities.fields;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Field extends DatabaseEntity {

    private String fieldName;

    private Float area;

    @ManyToOne
    private FieldGroup fieldGroup;
    @OneToMany
    private Set<FieldPart> fieldParts;

    private Set<String> surveyingPlots;

    private Boolean isOwnField;
    private Float propertyTax;
    private Float rent;

}
