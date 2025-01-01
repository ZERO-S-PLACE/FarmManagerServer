package org.zeros.farm_manager_server.Domain.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@SuperBuilder
public abstract class BaseEntityDTO {
    protected UUID id;
    protected Integer version;
}
