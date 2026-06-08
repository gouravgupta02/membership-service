package com.membership.platform.exception;

public class DuplicateSubscriptionException extends RuntimeException {

    public DuplicateSubscriptionException(Long userId) {
        super("User " + userId + " already has an active subscription");
    }
}
