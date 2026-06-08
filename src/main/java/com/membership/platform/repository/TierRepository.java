package com.membership.platform.repository;

import com.membership.platform.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TierRepository extends JpaRepository<MembershipTier, Long> {

    Optional<MembershipTier> findByPriority(int priority);

    @Query("SELECT t FROM MembershipTier t ORDER BY t.priority ASC")
    List<MembershipTier> findAllOrderedByPriority();
}
