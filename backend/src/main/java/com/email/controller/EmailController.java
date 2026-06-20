package com.email.controller;

import com.email.dto.EmailRequest;
import com.email.dto.EmailResponse;
import com.email.entity.Role;
import com.email.security.CustomUserDetailsService;
import com.email.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public EmailResponse sendEmail(@AuthenticationPrincipal CustomUserDetailsService.SecureUserDetails currentUser,
                                   @Valid @RequestBody EmailRequest request) {
        return emailService.sendEmail(currentUser.getUsername(), request);
    }

    @GetMapping("/sent")
    public List<EmailResponse> sentEmails(@AuthenticationPrincipal CustomUserDetailsService.SecureUserDetails currentUser) {
        return emailService.getSentEmails(currentUser.getUsername());
    }

    @GetMapping("/inbox")
    public List<EmailResponse> inboxEmails(@AuthenticationPrincipal CustomUserDetailsService.SecureUserDetails currentUser) {
        return emailService.getInboxEmails(currentUser.getUsername());
    }

    @PostMapping("/inbox/sync")
    public List<EmailResponse> syncInbox(@AuthenticationPrincipal CustomUserDetailsService.SecureUserDetails currentUser) {
        return emailService.syncInbox(currentUser.getUsername());
    }

    @GetMapping("/{id}")
    public EmailResponse getEmail(@AuthenticationPrincipal CustomUserDetailsService.SecureUserDetails currentUser,
                                  @PathVariable Long id) {
        Role role = currentUser.getUser().getRole();
        return emailService.getEmailById(currentUser.getUsername(), role, id);
    }
}
