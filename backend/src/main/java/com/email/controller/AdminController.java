package com.email.controller;

import com.email.dto.EmailResponse;
import com.email.dto.UpdateUserStatusRequest;
import com.email.dto.UserResponse;
import com.email.service.AdminService;
import com.email.service.EmailService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final EmailService emailService;

    public AdminController(AdminService adminService, EmailService emailService) {
        this.adminService = adminService;
        this.emailService = emailService;
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return adminService.getUsers();
    }

    @PutMapping("/users/{id}/status")
    public UserResponse updateUserStatus(@PathVariable Long id, @RequestBody UpdateUserStatusRequest request) {
        return adminService.updateStatus(id, request.enabled());
    }

    @GetMapping("/email-logs")
    public List<EmailResponse> emailLogs() {
        return emailService.getAllEmailsForAdmin();
    }
}
