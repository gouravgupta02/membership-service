package com.membership.platform.dto.response;

import com.membership.platform.entity.Benefit;
import com.membership.platform.enums.BenefitType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BenefitResponse {

    private final Long id;
    private final String name;
    private final BenefitType type;
    private final String description;

    public static BenefitResponse from(Benefit benefit) {
        return BenefitResponse.builder()
                .id(benefit.getId())
                .name(benefit.getName())
                .type(benefit.getType())
                .description(benefit.getDescription())
                .build();
    }
}
