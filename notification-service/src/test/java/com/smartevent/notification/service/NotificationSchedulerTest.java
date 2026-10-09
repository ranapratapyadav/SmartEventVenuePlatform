package com.smartevent.notification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartevent.notification.entity.Notification;
import com.smartevent.notification.entity.NotificationStatus;
import com.smartevent.notification.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerTest {

    @Mock
    private NotificationRepository notificationRepository;

    private NotificationScheduler notificationScheduler;

    @BeforeEach
    void setUp() {
        notificationScheduler =
                new NotificationScheduler(notificationRepository);
    }

    @Test
    void shouldMarkNotificationAsSent() {

        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setRetryCount(0);
        notification.setNotificationStatus(NotificationStatus.PENDING);

        when(notificationRepository.recoverStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.PENDING),
                any(LocalDateTime.class),
                anyInt()))
                .thenReturn(0);

        when(notificationRepository.failExhaustedStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.FAILED),
                any(LocalDateTime.class),
                anyInt(),
                anyInt()))
                .thenReturn(0);

        when(notificationRepository.findByNotificationStatus(
                NotificationStatus.PENDING))
                .thenReturn(List.of(notification));

        when(notificationRepository.claimNotification(
                eq(1L),
                eq(NotificationStatus.PENDING),
                eq(NotificationStatus.PROCESSING),
                any(LocalDateTime.class)))
                .thenReturn(1);

        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        notificationScheduler.processPendingNotifications();

        assertEquals(
                NotificationStatus.SENT,
                notification.getNotificationStatus());

        assertEquals(null, notification.getProcessingStartedAt());

        verify(notificationRepository).save(notification);
    }

    @Test
    void shouldSkipNotificationWhenAnotherWorkerClaimsIt() {

        Notification notification = new Notification();
        notification.setNotificationId(2L);
        notification.setRetryCount(0);
        notification.setNotificationStatus(NotificationStatus.PENDING);

        when(notificationRepository.recoverStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.PENDING),
                any(LocalDateTime.class),
                anyInt()))
                .thenReturn(0);

        when(notificationRepository.failExhaustedStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.FAILED),
                any(LocalDateTime.class),
                anyInt(),
                anyInt()))
                .thenReturn(0);

        when(notificationRepository.findByNotificationStatus(
                NotificationStatus.PENDING))
                .thenReturn(List.of(notification));

        when(notificationRepository.claimNotification(
                eq(2L),
                eq(NotificationStatus.PENDING),
                eq(NotificationStatus.PROCESSING),
                any(LocalDateTime.class)))
                .thenReturn(0);

        notificationScheduler.processPendingNotifications();

        verify(notificationRepository, never())
                .save(any(Notification.class));

        verify(notificationRepository, never()).findById(2L);
    }

    @Test
    void shouldIncrementRetryCountWhenSavingFails() {

        Notification notification = new Notification();
        notification.setNotificationId(3L);
        notification.setRetryCount(0);
        notification.setNotificationStatus(NotificationStatus.PENDING);

        Notification latestNotification = new Notification();
        latestNotification.setNotificationId(3L);
        latestNotification.setRetryCount(0);
        latestNotification.setNotificationStatus(NotificationStatus.PROCESSING);

        when(notificationRepository.recoverStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.PENDING),
                any(LocalDateTime.class),
                anyInt()))
                .thenReturn(0);

        when(notificationRepository.failExhaustedStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.FAILED),
                any(LocalDateTime.class),
                anyInt(),
                anyInt()))
                .thenReturn(0);

        when(notificationRepository.findByNotificationStatus(
                NotificationStatus.PENDING))
                .thenReturn(List.of(notification));

        when(notificationRepository.claimNotification(
                eq(3L),
                eq(NotificationStatus.PENDING),
                eq(NotificationStatus.PROCESSING),
                any(LocalDateTime.class)))
                .thenReturn(1);

        // First save fails; the recovery save succeeds.
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> {
                    Notification saved = invocation.getArgument(0);

                    if (saved == notification) {
                        throw new RuntimeException(
                                "Simulated database failure");
                    }

                    return saved;
                });

        when(notificationRepository.findById(3L))
                .thenReturn(Optional.of(latestNotification));

        notificationScheduler.processPendingNotifications();

        assertEquals(1, latestNotification.getRetryCount());

        assertEquals(
                NotificationStatus.PENDING,
                latestNotification.getNotificationStatus());

        assertEquals(null, latestNotification.getProcessingStartedAt());

        verify(notificationRepository).save(notification);
        verify(notificationRepository).save(latestNotification);
    }
    
    @Test
    void shouldMarkNotificationAsFailedAfterMaximumRetries() {

        Notification notification = new Notification();
        notification.setNotificationId(4L);
        notification.setRetryCount(2);
        notification.setNotificationStatus(NotificationStatus.PENDING);

        Notification latestNotification = new Notification();
        latestNotification.setNotificationId(4L);
        latestNotification.setRetryCount(2);
        latestNotification.setNotificationStatus(NotificationStatus.PROCESSING);

        when(notificationRepository.recoverStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.PENDING),
                any(LocalDateTime.class),
                anyInt()))
                .thenReturn(0);

        when(notificationRepository.failExhaustedStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.FAILED),
                any(LocalDateTime.class),
                anyInt(),
                anyInt()))
                .thenReturn(0);

        when(notificationRepository.findByNotificationStatus(
                NotificationStatus.PENDING))
                .thenReturn(List.of(notification));

        when(notificationRepository.claimNotification(
                eq(4L),
                eq(NotificationStatus.PENDING),
                eq(NotificationStatus.PROCESSING),
                any(LocalDateTime.class)))
                .thenReturn(1);

        // Initial save fails; recovery save succeeds.
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> {
                    Notification saved = invocation.getArgument(0);

                    if (saved == notification) {
                        throw new RuntimeException(
                                "Simulated processing failure");
                    }

                    return saved;
                });

        when(notificationRepository.findById(4L))
                .thenReturn(Optional.of(latestNotification));

        notificationScheduler.processPendingNotifications();

        assertEquals(3, latestNotification.getRetryCount());

        assertEquals(
                NotificationStatus.FAILED,
                latestNotification.getNotificationStatus());

        assertEquals(null, latestNotification.getProcessingStartedAt());

        verify(notificationRepository).save(notification);
        verify(notificationRepository).save(latestNotification);
    }
    
    @Test
    void shouldRecoverStaleNotifications() {

        when(notificationRepository.recoverStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.PENDING),
                any(LocalDateTime.class),
                eq(2)))
                .thenReturn(1);

        when(notificationRepository.failExhaustedStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.FAILED),
                any(LocalDateTime.class),
                eq(2),
                eq(3)))
                .thenReturn(0);

        when(notificationRepository.findByNotificationStatus(
                NotificationStatus.PENDING))
                .thenReturn(List.of());

        notificationScheduler.processPendingNotifications();

        verify(notificationRepository).recoverStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.PENDING),
                any(LocalDateTime.class),
                eq(2));

        verify(notificationRepository).failExhaustedStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.FAILED),
                any(LocalDateTime.class),
                eq(2),
                eq(3));
    }
    
    @Test
    void shouldFailStaleNotificationsAfterRetryLimit() {

        when(notificationRepository.recoverStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.PENDING),
                any(LocalDateTime.class),
                eq(2)))
                .thenReturn(0);

        when(notificationRepository.failExhaustedStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.FAILED),
                any(LocalDateTime.class),
                eq(2),
                eq(3)))
                .thenReturn(1);

        when(notificationRepository.findByNotificationStatus(
                NotificationStatus.PENDING))
                .thenReturn(List.of());

        notificationScheduler.processPendingNotifications();

        verify(notificationRepository).failExhaustedStaleNotifications(
                eq(NotificationStatus.PROCESSING),
                eq(NotificationStatus.FAILED),
                any(LocalDateTime.class),
                eq(2),
                eq(3));
    }
}