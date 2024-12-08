package org.zeros.farm_manager_server.entities.fields;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

public class FieldPart extends DatabaseEntity {

    private String fieldPartName;

    private Float area;

    @ManyToOne
    private Field field;
    @OneToMany
    private Set<Crops> crops;

}
