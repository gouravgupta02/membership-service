package com.membership.platform.engine;

import com.membership.platform.entity.User;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Immutable snapshot of a user's activity data, built once per eligibility evaluation.
 * Passed to every TierEligibilityRule so rules don't hit the database independently.
 */
@Value
@Builder
public class EligibilityContext {
    User user;
    long totalOrderCount;
    long orderCountThisMonth;
    BigDecimal totalSpendThisMonth;
}
