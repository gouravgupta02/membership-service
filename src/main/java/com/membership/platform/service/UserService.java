package com.membership.platform.service;

import com.membership.platform.dto.request.CreateUserRequest;
import com.membership.platform.dto.response.UserResponse;
import com.membership.platform.entity.User;
import com.membership.platform.exception.BusinessException;
import com.membership.platform.exception.ResourceNotFoundException;
import com.membership.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("A user with email '" + request.getEmail() + "' already exists");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .cohort(request.getCohort())
                .build();
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        return UserResponse.from(findUserOrThrow(userId));
    }

    public User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }
}
