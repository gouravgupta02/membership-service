CREATE TABLE users
(
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    cohort     VARCHAR(50)  NOT NULL DEFAULT 'REGULAR',
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE orders
(
    id         BIGSERIAL      PRIMARY KEY,
    user_id    BIGINT         NOT NULL REFERENCES users (id),
    amount     NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_user_id      ON orders (user_id);
CREATE INDEX idx_orders_user_created ON orders (user_id, created_at);
