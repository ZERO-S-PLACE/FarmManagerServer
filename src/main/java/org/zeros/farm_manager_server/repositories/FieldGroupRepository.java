package org.zeros.farm_manager_server.repositories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface FieldGroupRepository extends JpaRepository<FieldGroup, Long> {
    Optional<FieldGroup> findById(@NotNull UUID Id);
    List<FieldGroup> findAllByUser(@NotNull User user);
    Optional<FieldGroup> findByUserAndFieldGroupName(@NotNull User user, @NotNull @NotBlank String fieldGroupName);


}
