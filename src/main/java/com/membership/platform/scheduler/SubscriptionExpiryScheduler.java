package com.membership.platform.scheduler;

import com.membership.platform.entity.Subscription;
import com.membership.platform.repository.SubscriptionRepository;
import com.membership.platform.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpiryScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;

    @Value("${membership.scheduler.batch-size}")
    private int batchSize;

    /**
     * Processes expired subscriptions in fixed-size batches to avoid loading the
     * entire table into memory at once.
     *
     * We always fetch page 0: once a batch is processed, those rows flip to EXPIRED
     * and drop out of the WHERE clause, so the next "page 0" naturally returns new rows.
     * Each batch is committed in its own transaction via SubscriptionService.expireBatch(),
     * so a failure in one batch does not roll back the others.
     */
    @Scheduled(cron = "${membership.scheduler.expiry-cron}")
    public void expireSubscriptions() {
        int totalExpired = 0;
        List<Subscription> batch;

        do {
            batch = subscriptionRepository.findExpiredActiveBatch(
                    LocalDateTime.now(), PageRequest.of(0, batchSize));

            if (batch.isEmpty()) break;

            totalExpired += subscriptionService.expireBatch(batch);

        } while (batch.size() == batchSize);

        if (totalExpired > 0) {
            log.info("Expiry run complete. Total expired: {}", totalExpired);
        }
    }
}
