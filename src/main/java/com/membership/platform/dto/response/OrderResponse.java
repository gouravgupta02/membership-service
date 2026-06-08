package com.membership.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.membership.platform.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponse {

    private final Long id;
    private final Long userId;
    private final BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .amount(order.getAmount())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
