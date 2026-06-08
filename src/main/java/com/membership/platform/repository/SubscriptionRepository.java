package com.membership.platform.repository;

import com.membership.platform.entity.Subscription;
import com.membership.platform.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    // Always fetch page 0 in a loop — processed records flip to EXPIRED so they
    // drop out of this query naturally, avoiding the offset-drift problem.
    @Query("SELECT s FROM Subscription s WHERE s.expiryDate < :now AND s.status = 'ACTIVE'")
    List<Subscription> findExpiredActiveBatch(@Param("now") LocalDateTime now, Pageable pageable);

    // JOIN FETCH user so the scheduler can access user data without an open session.
    // Offset-based pagination is safe here: we never change status, so pages are stable.
    @Query("SELECT s FROM Subscription s JOIN FETCH s.user WHERE s.status = 'ACTIVE'")
    Page<Subscription> findAllActiveWithUser(Pageable pageable);
}
