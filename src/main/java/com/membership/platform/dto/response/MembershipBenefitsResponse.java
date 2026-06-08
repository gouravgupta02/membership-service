package com.membership.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MembershipBenefitsResponse {

    private final boolean hasMembership;
    private final String tierName;
    private final boolean hasFreeDelivery;
    private final int discountPercentage;
    private final boolean hasPrioritySupport;
    private final boolean hasEarlyAccess;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime expiryDate;

    public static MembershipBenefitsResponse noMembership() {
        return MembershipBenefitsResponse.builder()
                .hasMembership(false)
                .hasFreeDelivery(false)
                .discountPercentage(0)
                .hasPrioritySupport(false)
                .hasEarlyAccess(false)
                .build();
    }
}
