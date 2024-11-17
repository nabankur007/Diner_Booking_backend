package com.diner.backend.repository;
import com.diner.backend.entity.AppRole;
import com.diner.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
