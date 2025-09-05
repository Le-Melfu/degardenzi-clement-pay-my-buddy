package com.paymybuddy.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.paymybuddy.models.User;
import com.paymybuddy.services.interfaces.UserService;
import com.paymybuddy.logging.LoggingService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final LoggingService loggingService;

    public CustomUserDetailsService(UserService userService, LoggingService loggingService) {
        this.userService = userService;
        this.loggingService = loggingService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userService.findByEmail(email)
                .orElseThrow(() -> {
                    loggingService.error("CustomUserDetailsService: User not found with email: " + email);
                    return new UsernameNotFoundException("User not found: " + email);
                });

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}