package org.zeros.farm_manager_server.entities.agricultural_operations;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class FertilizerApplication extends AgriculturalOperation{
    @OneToOne
    private Fertilizer fertilizer;
    private Float quantityPerAreaUnit;
    private Float pricePerUnit;

}
