package org.zeros.farm_manager_server.repositories.Fields;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.Fields.Field;

import java.util.List;
import java.util.UUID;

public interface FieldRepository extends JpaRepository<Field, UUID> {

    List<Field> findAllByUser(User user);
}
