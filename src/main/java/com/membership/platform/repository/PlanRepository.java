package com.membership.platform.repository;

import com.membership.platform.entity.MembershipPlan;
import com.membership.platform.enums.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<MembershipPlan, Long> {

    Optional<MembershipPlan> findByType(PlanType type);
}
