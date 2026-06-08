package com.membership.platform.dto.response;

import com.membership.platform.entity.MembershipPlan;
import com.membership.platform.enums.PlanType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PlanResponse {

    private final Long id;
    private final PlanType type;
    private final String displayName;
    private final BigDecimal price;
    private final Integer validityDays;

    public static PlanResponse from(MembershipPlan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .type(plan.getType())
                .displayName(plan.getDisplayName())
                .price(plan.getPrice())
                .validityDays(plan.getValidityDays())
                .build();
    }
}
