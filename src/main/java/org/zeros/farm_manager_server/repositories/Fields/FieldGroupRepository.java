package org.zeros.farm_manager_server.repositories.Fields;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.Fields.FieldGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FieldGroupRepository extends JpaRepository<FieldGroup, UUID> {

    List<FieldGroup> findAllByUser(@NotNull User user);

    Optional<FieldGroup> findByUserAndFieldGroupName(@NotNull User user, @NotNull @NotBlank String fieldGroupName);


}
