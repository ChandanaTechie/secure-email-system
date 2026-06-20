package com.chandana.email.service;

import com.chandana.email.dto.EmailRequest;
import com.chandana.email.dto.EmailResponse;
import com.chandana.email.entity.EmailDirection;
import com.chandana.email.entity.EmailMessage;
import com.chandana.email.entity.EmailStatus;
import com.chandana.email.entity.Role;
import com.chandana.email.repository.EmailMessageRepository;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

@Service
public class EmailService {
    private final EmailMessageRepository emailRepository;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String mailHost;
    private final String mailUsername;
    private final String mailFrom;
    private final boolean receiveEnabled;
    private final String receiveProtocol;
    private final String receiveHost;
    private final int receivePort;
    private final String receiveUsername;
    private final String receivePassword;
    private final String receiveFolder;
    private final int maxFetch;

    public EmailService(EmailMessageRepository emailRepository,
                        ObjectProvider<JavaMailSender> mailSenderProvider,
                        @Value("${spring.mail.host:}") String mailHost,
                        @Value("${spring.mail.username:}") String mailUsername,
                        @Value("${app.mail.from:no-reply@example.com}") String mailFrom,
                        @Value("${app.mail.receive.enabled:false}") boolean receiveEnabled,
                        @Value("${app.mail.receive.protocol:imaps}") String receiveProtocol,
                        @Value("${app.mail.receive.host:imap.gmail.com}") String receiveHost,
                        @Value("${app.mail.receive.port:993}") int receivePort,
                        @Value("${app.mail.receive.username:}") String receiveUsername,
                        @Value("${app.mail.receive.password:}") String receivePassword,
                        @Value("${app.mail.receive.folder:INBOX}") String receiveFolder,
                        @Value("${app.mail.receive.max-fetch:10}") int maxFetch) {
        this.emailRepository = emailRepository;
        this.mailSenderProvider = mailSenderProvider;
        this.mailHost = mailHost;
        this.mailUsername = mailUsername;
        this.mailFrom = mailFrom;
        this.receiveEnabled = receiveEnabled;
        this.receiveProtocol = receiveProtocol;
        this.receiveHost = receiveHost;
        this.receivePort = receivePort;
        this.receiveUsername = receiveUsername;
        this.receivePassword = receivePassword;
        this.receiveFolder = receiveFolder;
        this.maxFetch = maxFetch;
    }

    public EmailResponse sendEmail(String currentUserEmail, EmailRequest request) {
        EmailMessage record = new EmailMessage();
        record.setOwnerEmail(currentUserEmail);
        record.setDirection(EmailDirection.SENT);
        record.setSenderEmail(resolveSenderEmail(currentUserEmail));
        record.setReceiverEmail(request.to().trim());
        record.setSubject(request.subject().trim());
        record.setBody(request.body());
        record.setCreatedAt(Instant.now());

        if (!isSmtpConfigured()) {
            record.setStatus(EmailStatus.DEMO_SAVED);
            record.setErrorMessage("SMTP is not configured. Email stored in demo mode only.");
            record.setSentAt(Instant.now());
            return EmailResponse.from(emailRepository.save(record));
        }

        try {
            JavaMailSender sender = mailSenderProvider.getIfAvailable();
            if (sender == null) {
                throw new IllegalStateException("JavaMailSender is not available");
            }
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(resolveSenderEmail(currentUserEmail));
            message.setTo(request.to().trim());
            message.setSubject(request.subject().trim());
            message.setText(request.body());
            sender.send(message);
            record.setStatus(EmailStatus.SENT);
            record.setSentAt(Instant.now());
        } catch (Exception ex) {
            record.setStatus(EmailStatus.FAILED);
            record.setErrorMessage(ex.getMessage());
        }
        return EmailResponse.from(emailRepository.save(record));
    }

    public List<EmailResponse> getSentEmails(String currentUserEmail) {
        return emailRepository.findByOwnerEmailAndDirectionOrderByCreatedAtDesc(currentUserEmail, EmailDirection.SENT)
                .stream().map(EmailResponse::from).toList();
    }

    public List<EmailResponse> getInboxEmails(String currentUserEmail) {
        return emailRepository.findByOwnerEmailAndDirectionOrderByCreatedAtDesc(currentUserEmail, EmailDirection.RECEIVED)
                .stream().map(EmailResponse::from).toList();
    }

    public List<EmailResponse> getAllEmailsForAdmin() {
        return emailRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(EmailMessage::getCreatedAt).reversed())
                .map(EmailResponse::from)
                .toList();
    }

    public EmailResponse getEmailById(String currentUserEmail, Role role, Long id) {
        EmailMessage message;
        if (role == Role.ADMIN) {
            message = emailRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Email not found"));
        } else {
            message = emailRepository.findByIdAndOwnerEmail(id, currentUserEmail)
                    .orElseThrow(() -> new AccessDeniedException("Email not found"));
        }
        return EmailResponse.from(message);
    }

    public List<EmailResponse> syncInbox(String currentUserEmail) {
        if (!receiveEnabled || !StringUtils.hasText(receiveUsername) || !StringUtils.hasText(receivePassword)) {
            return List.of();
        }

        Store store = null;
        Folder folder = null;
        List<EmailResponse> synced = new ArrayList<>();
        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", receiveProtocol);
            props.put("mail." + receiveProtocol + ".host", receiveHost);
            props.put("mail." + receiveProtocol + ".port", String.valueOf(receivePort));
            props.put("mail." + receiveProtocol + ".ssl.enable", "true");

            Session session = Session.getInstance(props);
            store = session.getStore(receiveProtocol);
            store.connect(receiveHost, receivePort, receiveUsername, receivePassword);
            folder = store.getFolder(receiveFolder);
            folder.open(Folder.READ_ONLY);

            int total = folder.getMessageCount();
            if (total == 0) {
                return List.of();
            }
            int start = Math.max(1, total - maxFetch + 1);
            Message[] messages = folder.getMessages(start, total);

            for (Message mail : messages) {
                EmailMessage record = new EmailMessage();
                record.setOwnerEmail(currentUserEmail);
                record.setDirection(EmailDirection.RECEIVED);
                record.setSenderEmail(extractAddress(mail.getFrom()));
                record.setReceiverEmail(currentUserEmail);
                record.setSubject(mail.getSubject() == null ? "(No subject)" : mail.getSubject());
                record.setBody(extractSimpleBody(mail));
                record.setStatus(EmailStatus.RECEIVED);
                record.setReceivedAt(mail.getReceivedDate() == null ? Instant.now() : mail.getReceivedDate().toInstant());
                record.setCreatedAt(Instant.now());
                synced.add(EmailResponse.from(emailRepository.save(record)));
            }
            return synced;
        } catch (Exception ex) {
            EmailMessage record = new EmailMessage();
            record.setOwnerEmail(currentUserEmail);
            record.setDirection(EmailDirection.RECEIVED);
            record.setSenderEmail("system");
            record.setReceiverEmail(currentUserEmail);
            record.setSubject("Inbox sync failed");
            record.setBody("Inbox sync failed. Check IMAP settings and app password.");
            record.setStatus(EmailStatus.FAILED);
            record.setErrorMessage(ex.getMessage());
            record.setCreatedAt(Instant.now());
            return List.of(EmailResponse.from(emailRepository.save(record)));
        } finally {
            try {
                if (folder != null && folder.isOpen()) {
                    folder.close(false);
                }
                if (store != null && store.isConnected()) {
                    store.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private boolean isSmtpConfigured() {
        return StringUtils.hasText(mailHost) && StringUtils.hasText(mailUsername);
    }

    private String resolveSenderEmail(String currentUserEmail) {
        if (StringUtils.hasText(mailFrom) && !"no-reply@example.com".equalsIgnoreCase(mailFrom)) {
            return mailFrom;
        }
        return currentUserEmail;
    }

    private String extractAddress(jakarta.mail.Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return "unknown";
        }
        if (addresses[0] instanceof InternetAddress internetAddress) {
            return internetAddress.getAddress();
        }
        return addresses[0].toString();
    }

    private String extractSimpleBody(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof String text) {
                return text;
            }
            if (message instanceof MimeMessage mimeMessage) {
                return mimeMessage.getContent().toString();
            }
            return "Unsupported message body type";
        } catch (Exception ex) {
            return "Unable to read message body: " + ex.getMessage();
        }
    }
}
