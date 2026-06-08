package com.membership.platform.dto.response;

import com.membership.platform.entity.MembershipTier;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TierResponse {

    private final Long id;
    private final String name;
    private final Integer priority;
    private final String description;
    private final List<BenefitResponse> benefits;

    public static TierResponse from(MembershipTier tier) {
        return TierResponse.builder()
                .id(tier.getId())
                .name(tier.getName())
                .priority(tier.getPriority())
                .description(tier.getDescription())
                .benefits(
                        tier.getTierBenefits().stream()
                                .map(tb -> BenefitResponse.from(tb.getBenefit()))
                                .toList()
                )
                .build();
    }
}
