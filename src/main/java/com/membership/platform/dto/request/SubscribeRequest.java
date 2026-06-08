package com.membership.platform.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubscribeRequest {

    @NotNull(message = "userId is required")
    @Positive(message = "userId must be a positive number")
    private Long userId;

    @NotNull(message = "planId is required")
    @Positive(message = "planId must be a positive number")
    private Long planId;

    @NotNull(message = "tierId is required")
    @Positive(message = "tierId must be a positive number")
    private Long tierId;
}
