package com.membership.platform.engine;

import com.membership.platform.entity.MembershipTier;
import com.membership.platform.entity.User;
import com.membership.platform.repository.OrderRepository;
import com.membership.platform.repository.TierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TierRuleEngine {

    private final List<TierEligibilityRule> rules;
    private final TierRepository tierRepository;
    private final OrderRepository orderRepository;

    /**
     * Evaluates all registered rules and returns the highest tier the user qualifies for.
     *
     * Spring automatically injects every @Component bean that implements TierEligibilityRule
     * into the List<TierEligibilityRule> constructor parameter. Adding a new rule class
     * requires no changes here.
     *
     * Logic: OR semantics — user qualifies for the best tier granted by ANY single rule.
     */
    @Transactional(readOnly = true)
    public MembershipTier evaluate(User user) {
        EligibilityContext context = buildContext(user);
        log.debug("Evaluating tier eligibility for userId={}", user.getId());

        int maxPriority = rules.stream()
                .mapToInt(rule -> rule.evaluate(context))
                .max()
                .orElse(1);

        return tierRepository.findByPriority(maxPriority)
                .orElseGet(() -> tierRepository.findByPriority(1)
                        .orElseThrow(() -> new IllegalStateException(
                                "Silver tier (priority=1) not found. Verify seed data in V5__seed_data.sql."
                        )));
    }

    private EligibilityContext buildContext(User user) {
        LocalDateTime monthStart = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        return EligibilityContext.builder()
                .user(user)
                .totalOrderCount(orderRepository.countByUser(user))
                .orderCountThisMonth(orderRepository.countByUserAndCreatedAtAfter(user, monthStart))
                .totalSpendThisMonth(orderRepository.sumAmountByUserAndCreatedAtSince(user, monthStart))
                .build();
    }
}
