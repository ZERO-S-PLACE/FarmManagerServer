package org.zeros.farm_manager_server.entities.AgriculturalOperations;

import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
//@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Fertilizer extends DatabaseEntity {
    private String name;
    private String producer;
    private Boolean isNaturalFertilizer;
    private Float organicMatterPercentage;
    private Float N_PercentageOfPureComponent;
    private Float P_PercentageOfPureComponent;
    private Float K_PercentageOfPureComponent;
    private Float Ca_PercentageOfPureComponent;
    private Float Mg_PercentageOfPureComponent;
    private Float S_PercentageOfPureComponent;
    private Float Na_PercentageOfPureComponent;
    private Float Cl_PercentageOfPureComponent;
    private Float Fe_PercentageOfPureComponent;
    private Float B_PercentageOfPureComponent;
}
