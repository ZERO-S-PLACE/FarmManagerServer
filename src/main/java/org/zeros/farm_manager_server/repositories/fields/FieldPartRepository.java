package org.zeros.farm_manager_server.repositories.fields;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.domain.entities.fields.Field;
import org.zeros.farm_manager_server.domain.entities.fields.FieldPart;

import java.util.Set;
import java.util.UUID;

public interface FieldPartRepository extends JpaRepository<FieldPart, UUID> {
    Set<FieldPart> findAllByField(Field field);

    Set<FieldPart> findAllByFieldAndIsArchived(Field field, boolean b);
}
