package org.zeros.farm_manager_server.entities.fields;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
import org.zeros.farm_manager_server.entities.User;

import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FieldGroup extends DatabaseEntity {

    private String fieldGroupName;
    private String description;
    @OneToMany(mappedBy = "id")
    private Set<Field> fields;
    @ManyToOne
    private User user;

}
