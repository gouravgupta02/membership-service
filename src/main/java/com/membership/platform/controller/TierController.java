package com.membership.platform.controller;

import com.membership.platform.dto.response.ApiResponse;
import com.membership.platform.dto.response.TierResponse;
import com.membership.platform.service.TierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tiers")
@RequiredArgsConstructor
@Tag(name = "Membership Tiers", description = "Tier listings and eligibility checks")
public class TierController {

    private final TierService tierService;

    @GetMapping
    @Operation(summary = "List all membership tiers with their benefits")
    public ResponseEntity<ApiResponse<List<TierResponse>>> getAllTiers() {
        return ResponseEntity.ok(ApiResponse.success(tierService.getAllTiers()));
    }

    @GetMapping("/eligible/{userId}")
    @Operation(summary = "Get the tier a user currently qualifies for based on order history and cohort")
    public ResponseEntity<ApiResponse<TierResponse>> getEligibleTier(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(tierService.getEligibleTierForUser(userId)));
    }
}
