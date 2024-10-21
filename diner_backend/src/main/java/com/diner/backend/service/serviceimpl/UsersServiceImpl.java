package com.diner.backend.service.serviceimpl;

import com.diner.backend.enitiy.AppRole;
import com.diner.backend.enitiy.Role;
import com.diner.backend.enitiy.Users;
import com.diner.backend.repository.RoleRepo;
import com.diner.backend.repository.UsersRepo;
import com.diner.backend.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    UsersRepo userRepository;

    @Autowired
    RoleRepo roleRepository;

    @Override
    public void updateUserRole(Long userId, String roleName) {
        Users user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        AppRole appRole = AppRole.valueOf(roleName);
        Role role = roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        userRepository.save(user);
    }


    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public Users findByUsername(String username) {
        Optional<Users> user = userRepository.findByUserName(username);
        return user.orElseThrow(
                () -> new RuntimeException("User not found with username: " + username));
    }
}
