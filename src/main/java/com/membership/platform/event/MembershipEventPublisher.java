package com.membership.platform.event;

import com.membership.platform.entity.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Single integration point for publishing membership lifecycle events.
 *
 * Currently uses Spring's synchronous in-process ApplicationEvent mechanism.
 * To migrate to Kafka: replace ApplicationEventPublisher calls with
 * KafkaTemplate.send() in this class only — callers remain unchanged.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MembershipEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishSubscribed(Subscription subscription) {
        log.info("Event: MembershipSubscribed — subscriptionId={}, userId={}",
                subscription.getId(), subscription.getUser().getId());
        eventPublisher.publishEvent(new MembershipSubscribedEvent(this, subscription));
    }

    public void publishCancelled(Subscription subscription) {
        log.info("Event: MembershipCancelled — subscriptionId={}, userId={}",
                subscription.getId(), subscription.getUser().getId());
        eventPublisher.publishEvent(new MembershipCancelledEvent(this, subscription));
    }

    public void publishExpired(Subscription subscription) {
        log.info("Event: MembershipExpired — subscriptionId={}, userId={}",
                subscription.getId(), subscription.getUser().getId());
        eventPublisher.publishEvent(new MembershipExpiredEvent(this, subscription));
    }
}
