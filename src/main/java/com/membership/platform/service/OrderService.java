package com.membership.platform.service;

import com.membership.platform.dto.request.CreateOrderRequest;
import com.membership.platform.dto.response.OrderResponse;
import com.membership.platform.entity.Order;
import com.membership.platform.entity.User;
import com.membership.platform.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = userService.findUserOrThrow(request.getUserId());
        Order order = Order.builder()
                .user(user)
                .amount(request.getAmount())
                .build();
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        User user = userService.findUserOrThrow(userId);
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(OrderResponse::from)
                .toList();
    }
}
