package com.example.sajhaKrishi.Controller.serviceController;

import com.example.sajhaKrishi.Services.EmailSendService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    private final EmailSendService emailSendService;

    @Autowired
    public EmailController(EmailSendService emailSendService) {
        this.emailSendService = emailSendService;
    }

    @PostMapping("/send-simple")
    public ResponseEntity<String> sendSimpleEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String text) {
        try {
            emailSendService.sendSimpleEmail(to, subject, text);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/send-attachment")
    public ResponseEntity<String> sendEmailWithAttachment (
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String text,
            @RequestParam String attachmentPath) throws MessagingException {
        try {
            File attachment = new File(attachmentPath);
            emailSendService.sendEmailWithAttachment(to, subject, text, attachment);
            return ResponseEntity.ok("Email with attachment sent successfully");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }
}
