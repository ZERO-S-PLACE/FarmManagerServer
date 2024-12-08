package org.zeros.farm_manager_server.entities.agricultural_operations;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Cultivation extends AgriculturalOperation {
    private Float depth;
    @Enumerated(EnumType.STRING)
    private CultivationType cultivationType;
}
