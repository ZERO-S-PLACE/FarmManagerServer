package org.zeros.farm_manager_server.repositories.Fields;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.fields.Field;
import org.zeros.farm_manager_server.entities.fields.FieldPart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FieldPartRepository extends JpaRepository<FieldPart, UUID> {
    List<FieldPart> findAllByField( Field field);

    @NonNull
    Optional<FieldPart> findById(@NonNull UUID id);

    List<FieldPart> findAllByFieldAndIsArchived(Field field, boolean b);
}
