package com.membership.platform.service;

import com.membership.platform.dto.request.CancelSubscriptionRequest;
import com.membership.platform.dto.request.SubscribeRequest;
import com.membership.platform.dto.response.SubscriptionResponse;
import com.membership.platform.entity.MembershipPlan;
import com.membership.platform.entity.MembershipTier;
import com.membership.platform.entity.Subscription;
import com.membership.platform.entity.User;
import com.membership.platform.enums.SubscriptionStatus;
import com.membership.platform.event.MembershipEventPublisher;
import com.membership.platform.exception.BusinessException;
import com.membership.platform.exception.DuplicateSubscriptionException;
import com.membership.platform.exception.ResourceNotFoundException;
import com.membership.platform.repository.PlanRepository;
import com.membership.platform.repository.SubscriptionRepository;
import com.membership.platform.repository.TierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final TierRepository tierRepository;
    private final UserService userService;
    private final MembershipEventPublisher eventPublisher;

    @Transactional
    public SubscriptionResponse subscribe(SubscribeRequest request) {
        User user = userService.findUserOrThrow(request.getUserId());

        // Application-level guard. The DB unique partial index is the final guarantee.
        subscriptionRepository.findByUserIdAndStatus(user.getId(), SubscriptionStatus.ACTIVE)
                .ifPresent(existing -> {
                    throw new DuplicateSubscriptionException(user.getId());
                });

        MembershipPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", request.getPlanId()));

        MembershipTier tier = tierRepository.findById(request.getTierId())
                .orElseThrow(() -> new ResourceNotFoundException("MembershipTier", request.getTierId()));

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .tier(tier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(plan.getValidityDays()))
                .priceAtSubscription(plan.getPrice())
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        eventPublisher.publishSubscribed(saved);

        log.info("User {} subscribed: plan={}, tier={}", user.getId(), plan.getType(), tier.getName());
        return SubscriptionResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getActiveSubscription(Long userId) {
        Subscription subscription = subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active subscription for user", userId));
        return SubscriptionResponse.from(subscription);
    }

    @Transactional
    public SubscriptionResponse upgradeTier(Long subscriptionId) {
        Subscription subscription = findActiveOrThrow(subscriptionId);
        MembershipTier current = subscription.getTier();

        MembershipTier next = tierRepository.findByPriority(current.getPriority() + 1)
                .orElseThrow(() -> new BusinessException(
                        "Already at the highest tier (" + current.getName() + "). "
                        + "Billing for tier changes is handled externally."
                ));

        subscription.setTier(next);
        log.info("Subscription {} upgraded: {} -> {}", subscriptionId, current.getName(), next.getName());
        return SubscriptionResponse.from(subscriptionRepository.save(subscription));
    }

    @Transactional
    public SubscriptionResponse downgradeTier(Long subscriptionId) {
        Subscription subscription = findActiveOrThrow(subscriptionId);
        MembershipTier current = subscription.getTier();

        MembershipTier previous = tierRepository.findByPriority(current.getPriority() - 1)
                .orElseThrow(() -> new BusinessException(
                        "Already at the lowest tier (" + current.getName() + ")."
                ));

        subscription.setTier(previous);
        log.info("Subscription {} downgraded: {} -> {}", subscriptionId, current.getName(), previous.getName());
        return SubscriptionResponse.from(subscriptionRepository.save(subscription));
    }

    @Transactional
    public void cancel(Long subscriptionId, CancelSubscriptionRequest request) {
        Subscription subscription = findActiveOrThrow(subscriptionId);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancellationReason(request.getReason());
        subscriptionRepository.save(subscription);
        eventPublisher.publishCancelled(subscription);
        log.info("Subscription {} cancelled. Reason: {}", subscriptionId, request.getReason());
    }

    /**
     * Called by SubscriptionExpiryScheduler, one batch at a time.
     * Each call is its own transaction so a single batch failure doesn't roll back others.
     * Returns the number of subscriptions expired in this batch.
     */
    @Transactional
    public int expireBatch(List<Subscription> subscriptions) {
        subscriptions.forEach(s -> s.setStatus(SubscriptionStatus.EXPIRED));
        subscriptionRepository.saveAll(subscriptions);
        subscriptions.forEach(eventPublisher::publishExpired);
        return subscriptions.size();
    }

    /**
     * Called by TierEligibilityScheduler. Updates tier only if the evaluated tier
     * is strictly higher than the current one — never auto-downgrades.
     */
    @Transactional
    public void applyTierUpgradeIfEligible(Long subscriptionId, MembershipTier eligibleTier) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", subscriptionId));

        if (eligibleTier.getPriority() > subscription.getTier().getPriority()) {
            String previous = subscription.getTier().getName();
            subscription.setTier(eligibleTier);
            subscriptionRepository.save(subscription);
            log.info("Auto-upgraded subscription {}: {} -> {}", subscriptionId, previous, eligibleTier.getName());
        }
    }

    private Subscription findActiveOrThrow(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", subscriptionId));

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new BusinessException(
                    "Subscription " + subscriptionId + " is not active (status=" + subscription.getStatus() + ")"
            );
        }
        return subscription;
    }
}
