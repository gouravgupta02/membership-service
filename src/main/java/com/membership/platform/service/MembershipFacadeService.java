package com.membership.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.membership.platform.dto.response.MembershipBenefitsResponse;
import com.membership.platform.entity.Benefit;
import com.membership.platform.entity.Subscription;
import com.membership.platform.entity.TierBenefit;
import com.membership.platform.enums.SubscriptionStatus;
import com.membership.platform.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipFacadeService {

    private final SubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper;

    /**
     * Checkout integration point. Returns a flat benefit summary for a user.
     * Returns a zero-benefit response when no active membership exists,
     * so callers never need to handle null or check membership status separately.
     */
    @Transactional(readOnly = true)
    public MembershipBenefitsResponse getBenefits(Long userId) {
        return subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .map(this::resolveBenefits)
                .orElse(MembershipBenefitsResponse.noMembership());
    }

    private MembershipBenefitsResponse resolveBenefits(Subscription subscription) {
        boolean hasFreeDelivery = false;
        int discountPercentage = 0;
        boolean hasPrioritySupport = false;
        boolean hasEarlyAccess = false;

        for (TierBenefit tierBenefit : subscription.getTier().getTierBenefits()) {
            Benefit benefit = tierBenefit.getBenefit();
            switch (benefit.getType()) {
                case FREE_DELIVERY -> hasFreeDelivery = true;
                case EXTRA_DISCOUNT -> discountPercentage = Math.max(discountPercentage, parseDiscount(benefit));
                case PRIORITY_SUPPORT -> hasPrioritySupport = true;
                case EARLY_ACCESS -> hasEarlyAccess = true;
            }
        }

        return MembershipBenefitsResponse.builder()
                .hasMembership(true)
                .tierName(subscription.getTier().getName())
                .hasFreeDelivery(hasFreeDelivery)
                .discountPercentage(discountPercentage)
                .hasPrioritySupport(hasPrioritySupport)
                .hasEarlyAccess(hasEarlyAccess)
                .expiryDate(subscription.getExpiryDate())
                .build();
    }

    private int parseDiscount(Benefit benefit) {
        try {
            JsonNode node = objectMapper.readTree(benefit.getConfigJson());
            return node.path("discountPercentage").asInt(0);
        } catch (Exception e) {
            log.warn("Could not parse configJson for benefitId={}: {}", benefit.getId(), e.getMessage());
            return 0;
        }
    }
}
