package com.diner.backend.repository;

import com.diner.backend.enitiy.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Long> {
    Optional<Users> findByUserName(String username);

    Boolean existsByUserName(String username);

    boolean existsByEmail(@NotBlank @Size(max = 50) @Email String email);

    Optional<Users> findByEmail(String identifier);
}
