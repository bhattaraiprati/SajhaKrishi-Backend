package com.example.sajhaKrishi.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailSendService {
 private final JavaMailSender mailSender;

 @Autowired
 public EmailSendService(JavaMailSender mailSender) {
  this.mailSender = mailSender;
 }
 public void sendSimpleEmail(String to, String subject, String text) {
  SimpleMailMessage message = new SimpleMailMessage();
  message.setTo(to);
  message.setSubject(subject);
  message.setText(text);
  message.setFrom("bhattaraipratik44@gmail.com"); // Must match spring.mail.username
  mailSender.send(message);
 }

 // Send an email with attachment
 public void sendEmailWithAttachment(String to, String subject, String text, File attachment) throws MessagingException {
  MimeMessage message = mailSender.createMimeMessage();
  MimeMessageHelper helper = new MimeMessageHelper(message, true);

  helper.setTo(to);
  helper.setSubject(subject);
  helper.setText(text);
  helper.setFrom("bhattaraipratik44@gmail.com");

  if (attachment != null && attachment.exists()) {
   helper.addAttachment(attachment.getName(), attachment);
  }

  mailSender.send(message);
 }
}
