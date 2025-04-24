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
@CrossOrigin // Enables cross-origin requests
public class AuthController {

    @Autowired
    JwtUtils jwtUtils; // Utility class for generating and managing JWT tokens

    @Autowired
    AuthenticationManager authenticationManager; // Manages authentication requests

    @Autowired
    UsersRepo userRepository; // Repository for user data

    @Autowired
    RoleRepo roleRepository; // Repository for role data

    @Autowired
    PasswordEncoder encoder; // Encoder for securely hashing passwords

    @Autowired
    UsersService userService; // Service for user-related operations

    /**
     * Sign in endpoint.
     * Authenticates the user and generates a JWT token upon successful login.
     *
     * @param loginRequest Contains username and password.
     * @return JWT token and user details, or an error response if authentication fails.
     */
    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user credentials
            Authentication authentication = authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            // Generate JWT token
            String jwtToken = generateJwtToken(authentication);
            // Retrieve roles of the authenticated user
            List<String> roles = getRolesFromAuthentication(authentication);

            // Return response with JWT token and user info
            LoginResponse response = new LoginResponse(authentication.getName(), roles, jwtToken);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException exception) {
            // Return error response for invalid credentials
            return new ResponseEntity<>(new MessageResponse("Bad credentials"), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Sign up endpoint.
     * Registers a new user and authenticates them automatically.
     *
     * @param signUpRequest Contains user registration details.
     * @return JWT token and user details, or an error response if validation fails.
     */
    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Check for existing username or email
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user and encode the password
        Users user = new Users(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getPhone());

        // Assign role to the user
        Role role = getRoleFromRequest(signUpRequest.getRole());
        user.setRole(role);
        userRepository.save(user); // Save user to the database

        // Authenticate the new user and return their JWT token
        try {
            Authentication authentication = authenticate(user.getUserName(), signUpRequest.getPassword());
            String jwtToken = generateJwtToken(authentication);
            List<String> roles = getRolesFromAuthentication(authentication);

            // Return response with JWT token
            LoginResponse response = new LoginResponse(authentication.getName(), roles, jwtToken);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException exception) {
            return new ResponseEntity<>(new MessageResponse("Bad credentials"), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Authenticate a user with the provided username and password.
     *
     * @param username The user's username.
     * @param password The user's password.
     * @return An authentication object on successful authentication.
     */
    private Authentication authenticate(String username, String password) throws AuthenticationException {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    /**
     * Generate a JWT token for the authenticated user.
     *
     * @param authentication The authentication object.
     * @return A JWT token.
     */
    private String generateJwtToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtils.generateTokenFromUsername(userDetails);
    }

    /**
     * Extract roles from the authentication object.
     *
     * @param authentication The authentication object.
     * @return A list of roles.
     */
    private List<String> getRolesFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
    }

    /**
     * Determine the correct role for a user based on the signup request.
     *
     * @param strRoles A set of role strings from the signup request.
     * @return A role object.
     */
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
                case "RESTAURENT" -> roleRepository.findByRoleName(AppRole.ROLE_RESTAURANT)
                        .orElseThrow(() -> new RuntimeException("Error: ROLE_RESTAURENT is not found."));
                default -> roleRepository.findByRoleName(AppRole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: ROLE_USER is not found."));
            };
        }
        return role;
    }

    /**
     * Retrieve authenticated user details.
     *
     * @param userDetails The authenticated user's details.
     * @return User information.
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        Users user = userService.findByUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getPhoneNumber(),
                user.getEmail(),
                roles
        );
        return ResponseEntity.ok().body(response);
    }

    /**
     * Retrieve the current authenticated username.
     *
     * @param userDetails The authenticated user's details.
     * @return The username, or an empty string if not authenticated.
     */
    @GetMapping("/username")
    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUsername() : "";
    }

    /**
     * Initiate a password reset process for the user.
     *
     * @param email The user's email.
     * @return A response indicating success or failure.
     */
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

    /**
     * Reset the user's password using a reset token.
     *
     * @param token       The reset token.
     * @param newPassword The new password.
     * @return A response indicating success or failure.
     */
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
