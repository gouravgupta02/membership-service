package com.membership.platform.scheduler;

import com.membership.platform.engine.TierRuleEngine;
import com.membership.platform.entity.MembershipTier;
import com.membership.platform.entity.Subscription;
import com.membership.platform.repository.SubscriptionRepository;
import com.membership.platform.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TierEligibilityScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final TierRuleEngine tierRuleEngine;
    private final SubscriptionService subscriptionService;

    @Value("${membership.scheduler.batch-size}")
    private int batchSize;

    /**
     * Runs daily at 2 AM. Evaluates every active subscription against the rule engine
     * and auto-upgrades users who now qualify for a higher tier. Never auto-downgrades.
     *
     * Offset-based pagination is safe here because we only change the tier column,
     * never the status — so the set of ACTIVE subscriptions stays stable across pages.
     * Each subscription is upgraded in its own transaction so one failure does not
     * roll back the entire batch.
     */
    @Scheduled(cron = "${membership.scheduler.tier-upgrade-cron}")
    public void evaluateAndUpgradeTiers() {
        int page = 0;
        int totalProcessed = 0;
        int totalUpgraded = 0;
        Page<Subscription> batch;

        do {
            batch = subscriptionRepository.findAllActiveWithUser(PageRequest.of(page++, batchSize));

            for (Subscription subscription : batch.getContent()) {
                try {
                    MembershipTier eligible = tierRuleEngine.evaluate(subscription.getUser());
                    subscriptionService.applyTierUpgradeIfEligible(subscription.getId(), eligible);
                    totalUpgraded++;
                } catch (OptimisticLockingFailureException e) {
                    log.warn("Skipping subscription {} — concurrent modification; will retry on next run",
                            subscription.getId());
                } catch (Exception e) {
                    log.error("Failed to evaluate tier for subscription {}: {}",
                            subscription.getId(), e.getMessage());
                }
            }

            totalProcessed += batch.getNumberOfElements();

        } while (batch.hasNext());

        log.info("Tier eligibility run complete. Processed: {}, upgraded: {}", totalProcessed, totalUpgraded);
    }
}
