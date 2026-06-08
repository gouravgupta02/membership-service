package com.membership.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.membership.platform.entity.Subscription;
import com.membership.platform.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class SubscriptionResponse {

    private final Long id;
    private final Long userId;
    private final String userName;
    private final PlanResponse plan;
    private final TierResponse tier;
    private final SubscriptionStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime expiryDate;

    private final BigDecimal priceAtSubscription;
    private final String cancellationReason;

    public static SubscriptionResponse from(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getUser().getId())
                .userName(subscription.getUser().getName())
                .plan(PlanResponse.from(subscription.getPlan()))
                .tier(TierResponse.from(subscription.getTier()))
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .expiryDate(subscription.getExpiryDate())
                .priceAtSubscription(subscription.getPriceAtSubscription())
                .cancellationReason(subscription.getCancellationReason())
                .build();
    }
}
