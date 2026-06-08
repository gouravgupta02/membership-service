package com.membership.platform.engine.rules;

import com.membership.platform.config.MembershipRuleProperties;
import com.membership.platform.engine.EligibilityContext;
import com.membership.platform.engine.TierEligibilityRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MonthlySpendRule implements TierEligibilityRule {

    private final MembershipRuleProperties properties;

    @Override
    public int evaluate(EligibilityContext context) {
        BigDecimal spend = context.getTotalSpendThisMonth();
        if (spend.compareTo(properties.getMonthlySpend().getPlatinumThreshold()) >= 0) return 3;
        if (spend.compareTo(properties.getMonthlySpend().getGoldThreshold()) >= 0) return 2;
        return 1;
    }
}
