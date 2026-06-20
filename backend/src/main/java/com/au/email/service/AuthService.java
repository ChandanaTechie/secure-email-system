package com.chandana.email.service;

import com.chandana.email.dto.AuthResponse;
import com.chandana.email.dto.LoginRequest;
import com.chandana.email.dto.RegisterRequest;
import com.chandana.email.dto.UserResponse;
import com.chandana.email.entity.Role;
import com.chandana.email.entity.UserAccount;
import com.chandana.email.repository.UserRepository;
import com.chandana.email.security.CustomUserDetailsService;
import com.chandana.email.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       CustomUserDetailsService userDetailsService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.email().toLowerCase().trim();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        UserAccount user = new UserAccount();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setEnabled(true);
        UserAccount saved = userRepository.save(user);

        var details = userDetailsService.loadUserByUsername(saved.getEmail());
        String token = jwtService.generateToken(details, Map.of("role", saved.getRole().name()));
        return new AuthResponse(token, UserResponse.from(saved));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().toLowerCase().trim(), request.password())
        );
        UserAccount user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        var details = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(details, Map.of("role", user.getRole().name()));
        return new AuthResponse(token, UserResponse.from(user));
    }
}
