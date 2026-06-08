-- ── Plans ────────────────────────────────────────────────────────────────────
INSERT INTO membership_plans (type, display_name, price, validity_days)
VALUES ('MONTHLY', 'Monthly', 99.00, 30),
       ('QUARTERLY', 'Quarterly', 249.00, 90),
       ('YEARLY', 'Yearly', 799.00, 365);

-- ── Tiers ────────────────────────────────────────────────────────────────────
INSERT INTO membership_tiers (name, priority, description)
VALUES ('Silver', 1, 'Essential membership with free delivery on all orders'),
       ('Gold', 2, 'Enhanced membership with free delivery and 5% discount'),
       ('Platinum', 3, 'Premium membership with all benefits and priority support');

-- ── Benefits ─────────────────────────────────────────────────────────────────
-- config_json is parsed at runtime by MembershipFacadeService.
-- Storing values in JSON makes benefit configuration extensible without schema changes.
INSERT INTO benefits (name, type, description, config_json)
VALUES ('Free Delivery', 'FREE_DELIVERY', 'Free delivery on all eligible orders', '{"freeDeliveries": 9999}'),
       ('5% Discount', 'EXTRA_DISCOUNT', '5% extra discount on selected items', '{"discountPercentage": 5}'),
       ('10% Discount', 'EXTRA_DISCOUNT', '10% extra discount on selected items', '{"discountPercentage": 10}'),
       ('Priority Support', 'PRIORITY_SUPPORT', 'Priority customer support within 30 minutes', '{"responseTimeMinutes": 30}'),
       ('Early Access', 'EARLY_ACCESS', 'Early access to exclusive deals and sales', '{}');

-- ── Tier → Benefit mappings ───────────────────────────────────────────────────
-- Silver: Free Delivery only
INSERT INTO tier_benefits (tier_id, benefit_id)
SELECT t.id, b.id
FROM membership_tiers t,
     benefits b
WHERE t.name = 'Silver'
  AND b.name = 'Free Delivery';

-- Gold: Free Delivery + 5% Discount
INSERT INTO tier_benefits (tier_id, benefit_id)
SELECT t.id, b.id
FROM membership_tiers t,
     benefits b
WHERE t.name = 'Gold'
  AND b.name IN ('Free Delivery', '5% Discount');

-- Platinum: Free Delivery + 10% Discount + Priority Support + Early Access
INSERT INTO tier_benefits (tier_id, benefit_id)
SELECT t.id, b.id
FROM membership_tiers t,
     benefits b
WHERE t.name = 'Platinum'
  AND b.name IN ('Free Delivery', '10% Discount', 'Priority Support', 'Early Access');
