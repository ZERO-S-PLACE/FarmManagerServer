package org.zeros.farm_manager_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;

public interface FieldGroupRepository extends JpaRepository<FieldGroup, Long> {
}
