package com.membership.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "membership_tiers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Higher priority = better tier: Silver=1, Gold=2, Platinum=3
    @Column(nullable = false, unique = true)
    private Integer priority;

    private String description;

    @OneToMany(mappedBy = "tier", fetch = FetchType.LAZY)
    @Builder.Default
    private List<TierBenefit> tierBenefits = new ArrayList<>();
}
