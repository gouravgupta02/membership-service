package com.membership.platform.engine.rules;

import com.membership.platform.config.MembershipRuleProperties;
import com.membership.platform.engine.EligibilityContext;
import com.membership.platform.engine.TierEligibilityRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCountRule implements TierEligibilityRule {

    private final MembershipRuleProperties properties;

    @Override
    public int evaluate(EligibilityContext context) {
        long count = context.getTotalOrderCount();
        if (count >= properties.getOrderCount().getPlatinumThreshold()) return 3;
        if (count >= properties.getOrderCount().getGoldThreshold()) return 2;
        return 1;
    }
}
