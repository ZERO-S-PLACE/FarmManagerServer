package org.zeros.farm_manager_server.repositories.fields;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.domain.entities.fields.FieldGroup;
import org.zeros.farm_manager_server.domain.entities.user.User;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface FieldGroupRepository extends JpaRepository<FieldGroup, UUID> {

    Set<FieldGroup> findAllByUser(@NotNull User user);

    Optional<FieldGroup> findByUserAndFieldGroupName(@NotNull User user, @NotNull @NotBlank String fieldGroupName);

}
