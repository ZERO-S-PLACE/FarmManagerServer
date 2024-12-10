package org.zeros.farm_manager_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
