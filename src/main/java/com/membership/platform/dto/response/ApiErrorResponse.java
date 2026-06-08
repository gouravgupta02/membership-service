package com.membership.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiErrorResponse {

    private final boolean success;
    private final String error;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public static ApiErrorResponse of(String message) {
        return ApiErrorResponse.builder()
                .success(false)
                .error(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
