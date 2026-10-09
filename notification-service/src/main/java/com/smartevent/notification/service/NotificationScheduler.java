package com.smartevent.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.smartevent.notification.entity.Notification;
import com.smartevent.notification.entity.NotificationStatus;
import com.smartevent.notification.repository.NotificationRepository;

@Component
public class NotificationScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationScheduler.class);

    private static final int MAX_RETRIES = 3;
    private static final int STALE_MINUTES = 2;

    private final NotificationRepository notificationRepository;

    public NotificationScheduler(
            NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Scheduled(fixedDelay = 15000)
    public void processPendingNotifications() {

        recoverStaleNotifications();

        List<Notification> pendingNotifications =
                notificationRepository.findByNotificationStatus(
                        NotificationStatus.PENDING);

        for (Notification notification : pendingNotifications) {

            Long notificationId = notification.getNotificationId();
            boolean claimedByThisWorker = false;

            try {
                LocalDateTime startedAt = LocalDateTime.now();

                int claimed = notificationRepository.claimNotification(
                        notificationId,
                        NotificationStatus.PENDING,
                        NotificationStatus.PROCESSING,
                        startedAt);

                if (claimed == 0) {
                    logger.debug(
                            "Notification ID {} was already claimed. Skipping.",
                            notificationId);
                    continue;
                }

                claimedByThisWorker = true;

                int attempt = notification.getRetryCount() + 1;

                logger.info(
                        "Processing notification ID: {}, Attempt: {}",
                        notificationId, attempt);

                // Simulated successful processing.
                // Actual email/SMS delivery is not implemented yet.
                notification.setNotificationStatus(NotificationStatus.SENT);
                notification.setProcessingStartedAt(null);

                notificationRepository.save(notification);

                logger.info(
                        "Notification ID {} marked as SENT.",
                        notificationId);

            } catch (Exception exception) {

                if (!claimedByThisWorker) {
                    logger.error(
                            "Could not claim notification ID {}.",
                            notificationId, exception);
                    continue;
                }

                try {
                    Notification latestNotification =
                            notificationRepository.findById(notificationId)
                                    .orElse(null);

                    if (latestNotification == null) {
                        logger.error(
                                "Notification ID {} no longer exists.",
                                notificationId);
                        continue;
                    }

                    int retryCount =
                            latestNotification.getRetryCount() + 1;

                    latestNotification.setRetryCount(retryCount);
                    latestNotification.setProcessingStartedAt(null);

                    if (retryCount >= MAX_RETRIES) {
                        latestNotification.setNotificationStatus(
                                NotificationStatus.FAILED);

                        logger.error(
                                "Notification ID {} failed after {} attempts.",
                                notificationId, retryCount, exception);
                    } else {
                        latestNotification.setNotificationStatus(
                                NotificationStatus.PENDING);

                        logger.warn(
                                "Notification ID {} failed. Retry {} of {}.",
                                notificationId, retryCount, MAX_RETRIES,
                                exception);
                    }

                    notificationRepository.save(latestNotification);

                } catch (Exception recoveryException) {
                    logger.error(
                            "Could not recover notification ID {}.",
                            notificationId, recoveryException);
                }
            }
        }
    }

    private void recoverStaleNotifications() {

        LocalDateTime cutoffTime =
                LocalDateTime.now().minusMinutes(STALE_MINUTES);

        try {
            int recovered =
                    notificationRepository.recoverStaleNotifications(
                            NotificationStatus.PROCESSING,
                            NotificationStatus.PENDING,
                            cutoffTime,
                            MAX_RETRIES - 1);

            int failed =
                    notificationRepository.failExhaustedStaleNotifications(
                            NotificationStatus.PROCESSING,
                            NotificationStatus.FAILED,
                            cutoffTime,
                            MAX_RETRIES - 1,
                            MAX_RETRIES);

            if (recovered > 0 || failed > 0) {
                logger.warn(
                        "Stale notification recovery completed. Recovered: {}, Failed: {}",
                        recovered, failed);
            }

        } catch (Exception exception) {
            logger.error(
                    "Could not recover stale notifications.",
                    exception);
        }
    }}