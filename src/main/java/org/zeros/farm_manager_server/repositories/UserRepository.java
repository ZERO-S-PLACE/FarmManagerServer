package org.zeros.farm_manager_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.User.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserById(UUID id);
}
