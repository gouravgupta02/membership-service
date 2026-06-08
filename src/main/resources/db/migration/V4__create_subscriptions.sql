CREATE TABLE subscriptions
(
    id                       BIGSERIAL      PRIMARY KEY,
    user_id                  BIGINT         NOT NULL REFERENCES users (id),
    plan_id                  BIGINT         NOT NULL REFERENCES membership_plans (id),
    tier_id                  BIGINT         NOT NULL REFERENCES membership_tiers (id),
    status                   VARCHAR(50)    NOT NULL DEFAULT 'ACTIVE',
    start_date               TIMESTAMP      NOT NULL,
    expiry_date              TIMESTAMP      NOT NULL,
    price_at_subscription    NUMERIC(10, 2) NOT NULL,
    cancellation_reason      TEXT,
    version                  BIGINT         NOT NULL DEFAULT 0,
    created_at               TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Partial unique index: only one ACTIVE subscription is allowed per user.
-- If two concurrent requests both pass the application-level check and race
-- to INSERT, the second one hits this constraint and is rejected by the DB.
-- This is the final line of defense against duplicate active subscriptions.
CREATE UNIQUE INDEX uq_active_subscription_per_user
    ON subscriptions (user_id)
    WHERE status = 'ACTIVE';

CREATE INDEX idx_subscriptions_user_id ON subscriptions (user_id);
CREATE INDEX idx_subscriptions_expiry  ON subscriptions (expiry_date) WHERE status = 'ACTIVE';
