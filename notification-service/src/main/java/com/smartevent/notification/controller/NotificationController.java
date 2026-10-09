package com.smartevent.notification.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartevent.notification.entity.Notification;
import com.smartevent.notification.entity.NotificationStatus;
import com.smartevent.notification.entity.NotificationType;
import com.smartevent.notification.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    // =========================================================
    // CREATE NOTIFICATION
    // =========================================================

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestBody Notification notification) {

        Notification savedNotification =
                notificationService.createNotification(notification);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedNotification);
    }

    // =========================================================
    // SEND NOTIFICATION
    // =========================================================

    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(
            @RequestParam Long customerId,
            @RequestParam Long bookingId,
            @RequestParam String message) {

        Notification notification = new Notification();

        notification.setCustomerId(customerId);
        notification.setBookingId(bookingId);
        notification.setMessage(message);

        notification.setNotificationType(
                NotificationType.BOOKING_CREATED);

        Notification savedNotification =
                notificationService.createNotification(notification);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedNotification);
    }

    // =========================================================
    // GET ALL NOTIFICATIONS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {

        return ResponseEntity.ok(
                notificationService.getAllNotifications());
    }

    // =========================================================
    // GET NOTIFICATION BY ID
    // =========================================================

    @GetMapping("/{notificationId}")
    public ResponseEntity<Notification> getNotificationById(
            @PathVariable Long notificationId) {

        return ResponseEntity.ok(
                notificationService.getNotificationById(notificationId));
    }

    // =========================================================
    // UPDATE NOTIFICATION STATUS
    // =========================================================

    @PutMapping("/{notificationId}/status")
    public ResponseEntity<Notification> updateNotificationStatus(
            @PathVariable Long notificationId,
            @RequestParam NotificationStatus status) {

        Notification updatedNotification =
                notificationService.updateNotificationStatus(
                        notificationId,
                        status);

        return ResponseEntity.ok(updatedNotification);
    }

    // =========================================================
    // MARK NOTIFICATION AS SENT
    // =========================================================

    @PutMapping("/{notificationId}/sent")
    public ResponseEntity<Notification> markAsSent(
            @PathVariable Long notificationId) {

        Notification notification =
                notificationService.markAsSent(notificationId);

        return ResponseEntity.ok(notification);
    }

    // =========================================================
    // MARK NOTIFICATION AS FAILED
    // =========================================================

    @PutMapping("/{notificationId}/failed")
    public ResponseEntity<Notification> markAsFailed(
            @PathVariable Long notificationId) {

        Notification notification =
                notificationService.markAsFailed(notificationId);

        return ResponseEntity.ok(notification);
    }
}