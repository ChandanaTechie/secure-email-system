package com.chandana.email.service;

import com.chandana.email.dto.UserResponse;
import com.chandana.email.entity.Role;
import com.chandana.email.entity.UserAccount;
import com.chandana.email.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(UserAccount::getCreatedAt).reversed())
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse updateStatus(Long userId, boolean enabled) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRole() == Role.ADMIN && !enabled) {
            throw new IllegalArgumentException("Admin account cannot be disabled from this screen");
        }
        user.setEnabled(enabled);
        return UserResponse.from(userRepository.save(user));
    }
}
