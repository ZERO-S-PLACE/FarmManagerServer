package org.zeros.farm_manager_server.Repositories.Fields;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;

import java.util.Set;
import java.util.UUID;

public interface FieldPartRepository extends JpaRepository<FieldPart, UUID> {
    Set<FieldPart> findAllByField(Field field);

    Set<FieldPart> findAllByFieldAndIsArchived(Field field, boolean b);
}
