CREATE TABLE benefits
(
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    config_json TEXT         NOT NULL DEFAULT '{}'
);

CREATE TABLE tier_benefits
(
    id         BIGSERIAL PRIMARY KEY,
    tier_id    BIGINT    NOT NULL REFERENCES membership_tiers (id),
    benefit_id BIGINT    NOT NULL REFERENCES benefits (id),
    CONSTRAINT uq_tier_benefit UNIQUE (tier_id, benefit_id)
);
