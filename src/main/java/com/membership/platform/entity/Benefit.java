package com.membership.platform.entity;

import com.membership.platform.enums.BenefitType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "benefits")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Benefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BenefitType type;

    private String description;

    // Stores type-specific configuration as JSON.
    // e.g. EXTRA_DISCOUNT: {"discountPercentage": 10}
    //      FREE_DELIVERY:  {"freeDeliveries": 9999}
    // Parsed at runtime; new benefit types require no schema change.
    @Column(name = "config_json", nullable = false)
    @Builder.Default
    private String configJson = "{}";
}
