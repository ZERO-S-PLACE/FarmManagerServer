package org.zeros.farm_manager_server.repositories.Fields;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.Fields.Field;
import org.zeros.farm_manager_server.entities.Fields.FieldPart;

import java.util.List;
import java.util.UUID;

public interface FieldPartRepository extends JpaRepository<FieldPart, UUID> {
    List<FieldPart> findAllByField(Field field);

    List<FieldPart> findAllByFieldAndIsArchived(Field field, boolean b);
}
