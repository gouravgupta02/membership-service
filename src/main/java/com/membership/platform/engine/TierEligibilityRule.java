package com.membership.platform.engine;

/**
 * Strategy interface for tier eligibility rules.
 *
 * Each implementation encapsulates one eligibility criterion (e.g. order count,
 * monthly spend, cohort). All @Component implementations are automatically
 * collected by TierRuleEngine via Spring's dependency injection.
 *
 * To add a new rule: create a class implementing this interface, annotate with
 * @Component. No changes to TierRuleEngine or any other class are needed.
 */
public interface TierEligibilityRule {

    /**
     * Returns the tier priority this rule grants to the user.
     * Silver=1, Gold=2, Platinum=3.
     * Must return at least 1 (Silver) — never 0 or negative.
     */
    int evaluate(EligibilityContext context);
}
