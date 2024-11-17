package com.diner.backend.controller;

import com.diner.backend.entity.AppRole;
import com.diner.backend.entity.Role;
import com.diner.backend.entity.Users;
import com.diner.backend.repository.RoleRepo;
import com.diner.backend.repository.UsersRepo;
import com.diner.backend.security.jwt.JwtUtils;
import com.diner.backend.security.request.LoginRequest;
import com.diner.backend.security.request.SignupRequest;
import com.diner.backend.security.response.LoginResponse;
import com.diner.backend.security.response.MessageResponse;
import com.diner.backend.security.response.UserInfoResponse;
import com.diner.backend.service.UsersService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsersRepo userRepository;

    @Autowired
    RoleRepo roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UsersService userService;

    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            String jwtToken = generateJwtToken(authentication);
            List<String> roles = getRolesFromAuthentication(authentication);

            // Prepare the response body with JWT token
            LoginResponse response = new LoginResponse(authentication.getName(), roles, jwtToken);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException exception) {
            return new ResponseEntity<>(new MessageResponse("Bad credentials"), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Check if username or email already exists
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        Users user = new Users(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getPhone()
        );

        // Set role for the new user
        Role role = getRoleFromRequest(signUpRequest.getRole());
        user.setRole(role); // Setting role
        userRepository.save(user); // Save the user to the database

        // Authenticate the newly registered user
        try {
            Authentication authentication = authenticate(user.getUserName(), signUpRequest.getPassword());
            String jwtToken = generateJwtToken(authentication);
            List<String> roles = getRolesFromAuthentication(authentication);

            // Prepare the response body with JWT token
            LoginResponse response = new LoginResponse(authentication.getName(), roles, jwtToken);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException exception) {
            return new ResponseEntity<>(new MessageResponse("Bad credentials"), HttpStatus.NOT_FOUND);
        }
    }

    // Helper method to authenticate a user
    private Authentication authenticate(String username, String password) throws AuthenticationException {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    // Helper method to generate JWT token from authentication
    private String generateJwtToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtils.generateTokenFromUsername(userDetails);
    }

    // Helper method to extract roles from authentication object
    private List<String> getRolesFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
    }

    // Helper method to get the correct Role based on the signUpRequest roles
    private Role getRoleFromRequest(Set<String> strRoles) {
        Role role;
        if (strRoles == null || strRoles.isEmpty()) {
            // Default to ROLE_USER
            role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Default role (ROLE_USER) is not found."));
        } else {
            String roleStr = strRoles.iterator().next().toUpperCase(); // Convert to uppercase
            role = switch (roleStr) {
                case "ADMIN" -> roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: ROLE_ADMIN is not found."));
                case "RESTAURENT" -> roleRepository.findByRoleName(AppRole.ROLE_RESTAURENT)
                        .orElseThrow(() -> new RuntimeException("Error: ROLE_RESTAURENT is not found."));
                default -> roleRepository.findByRoleName(AppRole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: ROLE_USER is not found."));
            };
        }
        return role;
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        Users user = userService.findByUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getPhoneNumber(),
                roles
        );

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/username")
    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUsername() : "";
    }

    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            userService.generatePasswordResetToken(email);
            return ResponseEntity.ok(new MessageResponse("Password reset email sent!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error sending password reset email"));
        }

    }

    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token,
                                           @RequestParam String newPassword) {
        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok(new MessageResponse("Password reset successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }
}
