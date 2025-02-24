package org.zeros.farm_manager_server.domain.dto.data;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.dto.BaseEntityDTO;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PlantDTO extends BaseEntityDTO {
    private UUID species;
    private String variety;
    private LocalDate registrationDate;
    private String productionCompany;
    private String description;
    private String countryOfOrigin;
}
