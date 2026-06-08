CREATE TABLE membership_plans
(
    id            BIGSERIAL      PRIMARY KEY,
    type          VARCHAR(50)    NOT NULL UNIQUE,
    display_name  VARCHAR(100)   NOT NULL,
    price         NUMERIC(10, 2) NOT NULL,
    validity_days INTEGER        NOT NULL
);

CREATE TABLE membership_tiers
(
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    priority    INTEGER      NOT NULL UNIQUE,
    description TEXT
);
