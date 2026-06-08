package com.membership.platform.repository;

import com.membership.platform.entity.Order;
import com.membership.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    long countByUser(User user);

    long countByUserAndCreatedAtAfter(User user, LocalDateTime since);

    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM Order o WHERE o.user = :user AND o.createdAt >= :since")
    BigDecimal sumAmountByUserAndCreatedAtSince(@Param("user") User user, @Param("since") LocalDateTime since);
}
