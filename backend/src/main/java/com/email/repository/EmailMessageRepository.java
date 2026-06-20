package com.email.repository;

import com.email.entity.EmailDirection;
import com.email.entity.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long> {
    List<EmailMessage> findByOwnerEmailOrderByCreatedAtDesc(String ownerEmail);
    List<EmailMessage> findByOwnerEmailAndDirectionOrderByCreatedAtDesc(String ownerEmail, EmailDirection direction);
    Optional<EmailMessage> findByIdAndOwnerEmail(Long id, String ownerEmail);
}
