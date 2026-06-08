package com.membership.platform.controller;

import com.membership.platform.dto.request.CancelSubscriptionRequest;
import com.membership.platform.dto.request.SubscribeRequest;
import com.membership.platform.dto.response.ApiResponse;
import com.membership.platform.dto.response.SubscriptionResponse;
import com.membership.platform.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscribe, upgrade, downgrade, and cancel memberships")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @Operation(summary = "Subscribe a user to a plan and tier")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> subscribe(
            @Valid @RequestBody SubscribeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(subscriptionService.subscribe(request), "Subscription created"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get the active subscription for a user")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getActiveSubscription(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getActiveSubscription(userId)));
    }

    @PutMapping("/{id}/upgrade")
    @Operation(summary = "Upgrade to the next tier. Billing for the price difference is handled externally.")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> upgradeTier(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(subscriptionService.upgradeTier(id), "Tier upgraded successfully"));
    }

    @PutMapping("/{id}/downgrade")
    @Operation(summary = "Downgrade to the previous tier")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> downgradeTier(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(subscriptionService.downgradeTier(id), "Tier downgraded successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a subscription")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @PathVariable Long id,
            @RequestBody(required = false) CancelSubscriptionRequest request) {
        subscriptionService.cancel(id, request != null ? request : new CancelSubscriptionRequest());
        return ResponseEntity.ok(ApiResponse.success(null, "Subscription cancelled"));
    }
}
