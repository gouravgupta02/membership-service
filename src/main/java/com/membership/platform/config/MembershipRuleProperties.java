package com.membership.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Binds the membership.rules.* block from application.yml into a typed object.
 * Thresholds can be changed in config without recompiling or redeploying.
 */
@Data
@Component
@ConfigurationProperties(prefix = "membership.rules")
public class MembershipRuleProperties {

    private OrderCountProperties orderCount = new OrderCountProperties();
    private MonthlySpendProperties monthlySpend = new MonthlySpendProperties();
    private CohortProperties cohort = new CohortProperties();

    @Data
    public static class OrderCountProperties {
        private int goldThreshold = 5;
        private int platinumThreshold = 20;
    }

    @Data
    public static class MonthlySpendProperties {
        private BigDecimal goldThreshold = new BigDecimal("2000");
        private BigDecimal platinumThreshold = new BigDecimal("10000");
    }

    @Data
    public static class CohortProperties {
        private List<String> goldCohorts = List.of("VIP");
        private List<String> platinumCohorts = List.of("CORPORATE");
    }
}
