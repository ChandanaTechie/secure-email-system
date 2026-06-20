package com.chandana.email.dto;

import com.chandana.email.entity.EmailDirection;
import com.chandana.email.entity.EmailMessage;
import com.chandana.email.entity.EmailStatus;

import java.time.Instant;

public record EmailResponse(
        Long id,
        String ownerEmail,
        EmailDirection direction,
        String senderEmail,
        String receiverEmail,
        String subject,
        String body,
        EmailStatus status,
        String errorMessage,
        Instant sentAt,
        Instant receivedAt,
        Instant createdAt
) {
    public static EmailResponse from(EmailMessage message) {
        return new EmailResponse(
                message.getId(),
                message.getOwnerEmail(),
                message.getDirection(),
                message.getSenderEmail(),
                message.getReceiverEmail(),
                message.getSubject(),
                message.getBody(),
                message.getStatus(),
                message.getErrorMessage(),
                message.getSentAt(),
                message.getReceivedAt(),
                message.getCreatedAt()
        );
    }
}
