package com.membership.platform.event;

import com.membership.platform.entity.Subscription;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MembershipSubscribedEvent extends ApplicationEvent {

    private final Subscription subscription;

    public MembershipSubscribedEvent(Object source, Subscription subscription) {
        super(source);
        this.subscription = subscription;
    }
}
