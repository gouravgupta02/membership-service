package com.membership.platform.dto.response;

import com.membership.platform.entity.User;
import com.membership.platform.enums.UserCohort;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final UserCohort cohort;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .cohort(user.getCohort())
                .build();
    }
}
