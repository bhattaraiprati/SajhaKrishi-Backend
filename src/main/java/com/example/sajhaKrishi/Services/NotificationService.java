package com.example.sajhaKrishi.Services;

import com.example.sajhaKrishi.Model.Notification;
import com.example.sajhaKrishi.Model.NotificationType;
import com.example.sajhaKrishi.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Notification createNotification(Long userId, String title, String message, NotificationType type) {
        Notification notification = new Notification(userId, title, message, type);
        notification = notificationRepository.save(notification);

        // Send real-time notification via WebSocket
        sendRealTimeNotification(notification);

        logger.info("Created notification for user {} with type {}", userId, type);
        return notification;
    }

    public Notification createOrderNotification(Long userId, String title, String message, Long orderId) {
        Notification notification = new Notification(userId, title, message, NotificationType.ORDER_STATUS_CHANGE);
        notification.setOrderId(orderId);
        notification = notificationRepository.save(notification);

        sendRealTimeNotification(notification);

        logger.info("Created order notification for user {} and order {}", userId, orderId);
        return notification;
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);

        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    private void sendRealTimeNotification(Notification notification) {
        try {
            // Send to specific user's private queue
            messagingTemplate.convertAndSendToUser(
                    notification.getUserId().toString(),
                    "/queue/notifications",  // Changed from /topic/notifications
                    notification
            );
            logger.info("Sent real-time notification to user {}", notification.getUserId());
        } catch (Exception e) {
            logger.error("Error sending real-time notification: {}", e.getMessage());
        }
    }
}
