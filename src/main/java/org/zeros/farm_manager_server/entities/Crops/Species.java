package org.zeros.farm_manager_server.entities.Crops;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
//@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder

public class Species extends DatabaseEntity {
    @Column(nullable = false,unique = true)
    private  String speciesName;
    @NotNull
    private  String speciesType;
    private  String speciesDescription;

    @Transient
    public final Species ANY=Species.builder().speciesName("ANY").speciesType("ANY").build();
    @Transient
    public final Species NONE=Species.builder().speciesName("NONE").speciesType("NONE").build();

}
