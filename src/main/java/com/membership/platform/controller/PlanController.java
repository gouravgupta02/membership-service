package com.membership.platform.controller;

import com.membership.platform.dto.response.ApiResponse;
import com.membership.platform.dto.response.PlanResponse;
import com.membership.platform.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "Membership Plans", description = "Available membership plans")
public class PlanController {

    private final PlanService planService;

    @GetMapping
    @Operation(summary = "List all membership plans")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getAllPlans() {
        return ResponseEntity.ok(ApiResponse.success(planService.getAllPlans()));
    }
}
