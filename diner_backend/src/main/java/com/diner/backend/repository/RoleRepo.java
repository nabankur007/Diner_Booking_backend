package com.diner.backend.repository;
import com.diner.backend.enitiy.AppRole;
import com.diner.backend.enitiy.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
