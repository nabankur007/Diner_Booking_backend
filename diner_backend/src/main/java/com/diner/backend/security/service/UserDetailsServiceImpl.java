package com.diner.backend.security.service;

import com.diner.backend.enitiy.Users;
import com.diner.backend.repository.UsersRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UsersRepo userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        Optional<Users> user = userRepository.findByUserName(identifier);

        if (user.isEmpty()) {
            user = Optional.ofNullable(userRepository.findByEmail(identifier)
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User Not Found with username: " + identifier)));
        }// Try with email if not found by username
        return UserDetailsImpl.build(user.orElse(null));
    }
}