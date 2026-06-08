package com.membership.platform.controller;

import com.membership.platform.dto.response.ApiResponse;
import com.membership.platform.dto.response.MembershipBenefitsResponse;
import com.membership.platform.service.MembershipFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
@Tag(name = "Membership Facade", description = "Checkout integration — resolve active benefits for a user")
public class MembershipFacadeController {

    private final MembershipFacadeService membershipFacadeService;

    @GetMapping("/benefits/{userId}")
    @Operation(summary = "Get resolved membership benefits for a user (used by checkout service)")
    public ResponseEntity<ApiResponse<MembershipBenefitsResponse>> getBenefits(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(membershipFacadeService.getBenefits(userId)));
    }
}
