package com.smartevent.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartevent.notification.entity.Notification;
import com.smartevent.notification.entity.NotificationStatus;
import com.smartevent.notification.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    // =========================================================
    // CREATE NOTIFICATION
    // =========================================================

    @Transactional
    public Notification createNotification(Notification notification) {

        notification.setCreatedAt(LocalDateTime.now());

        notification.setNotificationStatus(NotificationStatus.PENDING);

        return notificationRepository.save(notification);
    }

    // =========================================================
    // GET ALL NOTIFICATIONS
    // =========================================================

    public List<Notification> getAllNotifications() {

        return notificationRepository.findAll();
    }

    // =========================================================
    // GET NOTIFICATION BY ID
    // =========================================================

    public Notification getNotificationById(Long notificationId) {

        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException(
                        "Notification not found with ID: " + notificationId));
    }

    // =========================================================
    // UPDATE NOTIFICATION STATUS
    // =========================================================

    @Transactional
    public Notification updateNotificationStatus(
            Long notificationId,
            NotificationStatus status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Notification status cannot be null");
        }

        Notification notification = getNotificationById(notificationId);

        notification.setNotificationStatus(status);

        return notificationRepository.save(notification);
    }

    // =========================================================
    // MARK NOTIFICATION AS SENT
    // =========================================================

    @Transactional
    public Notification markAsSent(Long notificationId) {

        return updateNotificationStatus(
                notificationId,
                NotificationStatus.SENT);
    }

    // =========================================================
    // MARK NOTIFICATION AS FAILED
    // =========================================================

    @Transactional
    public Notification markAsFailed(Long notificationId) {

        return updateNotificationStatus(
                notificationId,
                NotificationStatus.FAILED);
    }
}