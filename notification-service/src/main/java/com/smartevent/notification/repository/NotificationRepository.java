package com.smartevent.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smartevent.notification.entity.Notification;
import com.smartevent.notification.entity.NotificationStatus;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Find notifications by status
    List<Notification> findByNotificationStatus(
            NotificationStatus status);

    // Atomically claim a pending notification
    @Modifying
    @Transactional
    @Query("""
        UPDATE Notification n
        SET n.notificationStatus = :processing,
            n.processingStartedAt = :startedAt
        WHERE n.notificationId = :id
          AND n.notificationStatus = :pending
        """)
    int claimNotification(
            @Param("id") Long id,
            @Param("pending") NotificationStatus pending,
            @Param("processing") NotificationStatus processing,
            @Param("startedAt") LocalDateTime startedAt);

    // Recover stale notifications that still have retries available
    @Modifying
    @Transactional
    @Query("""
        UPDATE Notification n
        SET n.notificationStatus = :pending,
            n.retryCount = n.retryCount + 1,
            n.processingStartedAt = NULL
        WHERE n.notificationStatus = :processing
          AND n.processingStartedAt < :cutoffTime
          AND n.retryCount < :lastRetryCount
        """)
    int recoverStaleNotifications(
            @Param("processing") NotificationStatus processing,
            @Param("pending") NotificationStatus pending,
            @Param("cutoffTime") LocalDateTime cutoffTime,
            @Param("lastRetryCount") int lastRetryCount);

    // Mark stale notifications as FAILED after retries are exhausted
    @Modifying
    @Transactional
    @Query("""
        UPDATE Notification n
        SET n.notificationStatus = :failed,
            n.retryCount = :maxRetries,
            n.processingStartedAt = NULL
        WHERE n.notificationStatus = :processing
          AND n.processingStartedAt < :cutoffTime
          AND n.retryCount >= :lastRetryCount
        """)
    int failExhaustedStaleNotifications(
            @Param("processing") NotificationStatus processing,
            @Param("failed") NotificationStatus failed,
            @Param("cutoffTime") LocalDateTime cutoffTime,
            @Param("lastRetryCount") int lastRetryCount,
            @Param("maxRetries") int maxRetries);
}