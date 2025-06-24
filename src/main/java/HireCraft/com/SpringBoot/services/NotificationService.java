package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.NotificationRequest;
import HireCraft.com.SpringBoot.dtos.response.NotificationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NotificationService {

    NotificationResponse createNotification(NotificationRequest request);

    List<NotificationResponse> getUserNotifications(Long userId);

    Page<NotificationResponse> getUserNotifications(Long userId, int page, int size);

    List<NotificationResponse> getUnreadNotifications(Long userId);

    long getUnreadCount(Long userId);

    boolean markAsRead(Long notificationId, Long userId);

    int markAllAsRead(Long userId);

    boolean deleteNotification(Long notificationId, Long userId);


    NotificationResponse createMessageNotification(Long userId, String senderName);

    NotificationResponse createBookingReminderNotification(Long userId, String appointmentTime);

    NotificationResponse createPaymentReceivedNotification(Long userId, String amount);

    NotificationResponse createReviewReceivedNotification(Long userId, int stars);

    int cleanupOldNotifications(int daysToKeep);
}