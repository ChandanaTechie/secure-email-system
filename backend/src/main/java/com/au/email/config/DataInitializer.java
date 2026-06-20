package com.chandana.email.config;

import com.chandana.email.entity.Role;
import com.chandana.email.entity.UserAccount;
import com.chandana.email.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createUserIfMissing("Admin User", "admin@example.com", "admin123", Role.ADMIN);
        createUserIfMissing("Demo User", "user@example.com", "user123", Role.USER);
    }

    private void createUserIfMissing(String fullName, String email, String rawPassword, Role role) {
        if (!userRepository.existsByEmailIgnoreCase(email)) {
            UserAccount user = new UserAccount();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            user.setEnabled(true);
            userRepository.save(user);
        }
    }
}
