package com.diner.backend.service;

import com.diner.backend.enitiy.Users;

import java.util.List;

public interface UsersService {
    void updateUserRole(Long userId, String roleName);

    List<Users> getAllUsers();



    Users findByUsername(String username);

    void generatePasswordResetToken(String email);

    void resetPassword(String token, String newPassword);
}
