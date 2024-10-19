package com.diner.backend.repository;

import com.diner.backend.enitiy.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepo extends JpaRepository<Users, Long> {
    Users findByUserName(String username);
}
