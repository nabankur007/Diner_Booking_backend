package com.diner.backend.repository;

import com.diner.backend.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Long> {
    Optional<Users> findByUserName(String username);

    Boolean existsByUserName(String username);

    boolean existsByEmail(@NotBlank @Size(max = 50) @Email String email);

    Optional<Users> findByEmail(String identifier);

    @Query("SELECT u.email FROM Users u WHERE u.userName = :username")
    Optional<String> getEmailFindByUserName(@Param("username") String username);


}
