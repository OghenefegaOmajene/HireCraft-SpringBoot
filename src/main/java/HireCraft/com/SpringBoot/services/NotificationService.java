package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.NotificationRequest;
import HireCraft.com.SpringBoot.dtos.response.NotificationResponse;
import HireCraft.com.SpringBoot.enums.NotificationType;
import HireCraft.com.SpringBoot.models.Notification;
import HireCraft.com.SpringBoot.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Create a new notification
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .message(request.getMessage())
                .type(request.getType())
                .userId(request.getUserId())
                .priority(request.getPriority())
                .referenceId(request.getReferenceId())
                .referenceType(request.getReferenceType())
                .metadata(request.getMetadata())
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Created notification for user {}: {}", request.getUserId(), request.getMessage());

        return convertToDto(saved);
    }

    // Get all notifications for a user
    public List<NotificationResponse> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get notifications with pagination
    public Page<NotificationResponse> getUserNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(this::convertToDto);
    }

    // Get unread notifications for a user
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get unread notification count
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // Mark notification as read
    @Transactional
    public boolean markAsRead(Long notificationId, Long userId) {
        int updated = notificationRepository.markAsRead(notificationId, userId);
        return updated > 0;
    }

    // Mark all notifications as read for a user
    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsReadForUser(userId);
    }

    // Delete notification
    @Transactional
    public boolean deleteNotification(Long notificationId, Long userId) {
        return notificationRepository.findById(notificationId)
                .filter(notification -> notification.getUserId().equals(userId))
                .map(notification -> {
                    notificationRepository.delete(notification);
                    return true;
                })
                .orElse(false);
    }

    // Utility methods for creating specific types of notifications
    public NotificationResponse createMessageNotification(Long userId, String senderName) {
        String message = String.format("New message from %s", senderName);
        NotificationRequest request = NotificationRequest.builder()
                .message(message)
                .type(NotificationType.MESSAGE)
                .userId(userId)
                .build();
        return createNotification(request);
    }

    public NotificationResponse createBookingReminderNotification(Long userId, String appointmentTime) {
        String message = String.format("Booking reminder: %s appointment", appointmentTime);
        NotificationRequest request = NotificationRequest.builder()
                .message(message)
                .type(NotificationType.BOOKING_REMINDER)
                .userId(userId)
                .build();
        return createNotification(request);
    }

    public NotificationResponse createPaymentReceivedNotification(Long userId, String amount) {
        String message = String.format("Payment of %s received", amount);
        NotificationRequest request = NotificationRequest.builder()
                .message(message)
                .type(NotificationType.PAYMENT_RECEIVED)
                .userId(userId)
                .build();
        return createNotification(request);
    }

    public NotificationResponse createReviewReceivedNotification(Long userId, int stars) {
        String message = String.format("New %d-star review received", stars);
        NotificationRequest request = NotificationRequest.builder()
                .message(message)
                .type(NotificationType.REVIEW_RECEIVED)
                .userId(userId)
                .build();
        return createNotification(request);
    }

    // Convert entity to DTO
    private NotificationResponse convertToDto(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .timeAgo(calculateTimeAgo(notification.getCreatedAt()))
                .priority(notification.getPriority())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .metadata(notification.getMetadata())
                .build();
    }

    // Calculate human-readable time difference
    private String calculateTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " min ago";

        long hours = ChronoUnit.HOURS.between(createdAt, now);
        if (hours < 24) return hours + " hour" + (hours == 1 ? "" : "s") + " ago";

        long days = ChronoUnit.DAYS.between(createdAt, now);
        if (days < 7) return days + " day" + (days == 1 ? "" : "s") + " ago";

        long weeks = days / 7;
        if (weeks < 4) return weeks + " week" + (weeks == 1 ? "" : "s") + " ago";

        long months = ChronoUnit.MONTHS.between(createdAt, now);
        if (months < 12) return months + " month" + (months == 1 ? "" : "s") + " ago";

        long years = ChronoUnit.YEARS.between(createdAt, now);
        return years + " year" + (years == 1 ? "" : "s") + " ago";
    }

    // Clean up old notifications (can be called by scheduled task)
    @Transactional
    public int cleanupOldNotifications(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        return notificationRepository.deleteOldNotifications(cutoffDate);
    }
}
