package com.membership.platform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tier_benefits",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_tier_benefit",
                columnNames = {"tier_id", "benefit_id"}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    // Benefit is loaded eagerly: whenever we load a TierBenefit we always need its Benefit.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "benefit_id", nullable = false)
    private Benefit benefit;
}
