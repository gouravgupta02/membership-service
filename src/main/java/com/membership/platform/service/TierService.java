package com.membership.platform.service;

import com.membership.platform.dto.response.TierResponse;
import com.membership.platform.engine.TierRuleEngine;
import com.membership.platform.entity.MembershipTier;
import com.membership.platform.entity.User;
import com.membership.platform.repository.TierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TierService {

    private final TierRepository tierRepository;
    private final TierRuleEngine tierRuleEngine;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<TierResponse> getAllTiers() {
        return tierRepository.findAllOrderedByPriority().stream()
                .map(TierResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TierResponse getEligibleTierForUser(Long userId) {
        User user = userService.findUserOrThrow(userId);
        MembershipTier eligible = tierRuleEngine.evaluate(user);
        return TierResponse.from(eligible);
    }
}
