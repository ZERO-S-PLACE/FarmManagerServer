package org.zeros.farm_manager_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.fields.Field;

import java.util.UUID;

public interface FieldRepository extends JpaRepository<Field, UUID> {
}
