# Membership Service

A backend system for a subscription-based membership program with tiered benefits, built as a Spring Boot microservice.

## Live Demo

**Swagger UI:** https://membership-service-production-506a.up.railway.app/membership-service/swagger-ui/index.html

## Tech Stack

- **Java 21** + **Spring Boot 3.3**
- **PostgreSQL** — primary database
- **Flyway** — versioned schema migrations
- **Docker** + **Docker Compose** — local development
- **Springdoc OpenAPI** — auto-generated API documentation

## Features

- Monthly, Quarterly, and Yearly membership plans
- Tier system: Silver → Gold → Platinum, each with configurable benefits (free delivery, discounts, priority support, early access)
- Subscribe, upgrade tier, downgrade tier, cancel subscription
- Automatic tier upgrade engine — evaluates order count, monthly spend, and user cohort via a pluggable rule engine
- Scheduled jobs — auto-expires subscriptions hourly, auto-upgrades tiers daily
- Concurrency-safe — optimistic locking on subscriptions + DB partial unique index prevents duplicate active subscriptions

## Running Locally

**Prerequisites:** Docker and Docker Compose

```bash
git clone https://github.com/gouravgupta02/membership-service.git
cd membership-service
docker-compose up --build
```

The app starts on `http://localhost:8080`.

| URL                                                      | Description          |
| -------------------------------------------------------- | -------------------- |
| http://localhost:8080/membership-service/swagger-ui.html | Interactive API docs |
| http://localhost:8080/membership-service/actuator/health | Health check         |

## API Overview

| Method | Endpoint                                               | Description                         |
| ------ | ------------------------------------------------------ | ----------------------------------- |
| GET    | `/membership-service/api/plans`                        | List all membership plans           |
| GET    | `/membership-service/api/tiers`                        | List all tiers with benefits        |
| GET    | `/membership-service/api/tiers/eligible/{userId}`      | Get tier a user qualifies for       |
| POST   | `/membership-service/api/users`                        | Create a user                       |
| POST   | `/membership-service/api/orders`                       | Record an order (feeds tier engine) |
| POST   | `/membership-service/api/subscriptions`                | Subscribe to a plan + tier          |
| GET    | `/membership-service/api/subscriptions/user/{userId}`  | Get active subscription             |
| PUT    | `/membership-service/api/subscriptions/{id}/upgrade`   | Upgrade tier                        |
| PUT    | `/membership-service/api/subscriptions/{id}/downgrade` | Downgrade tier                      |
| DELETE | `/membership-service/api/subscriptions/{id}`           | Cancel subscription                 |
| GET    | `/membership-service/api/membership/benefits/{userId}` | Resolve benefits for checkout       |

## Architecture

```
controller/     REST layer — delegates to services, no business logic
service/        Business logic — subscription lifecycle, tier evaluation
engine/         Rule engine — Strategy pattern for tier eligibility rules
  rules/        OrderCountRule, MonthlySpendRule, CohortRule
entity/         JPA entities
repository/     Spring Data JPA repositories
scheduler/      Background jobs — expiry and tier-upgrade
event/          Application events — subscribed, cancelled, expired
exception/      Global exception handling
config/         Rule thresholds bound from application.properties
```

Tier eligibility rules implement a common interface (`TierEligibilityRule`). Adding a new rule requires only a new `@Component` class — no changes to the engine or any existing code.

## Configuration

Rule thresholds are configurable in `application.properties` without recompiling:

```properties
membership.rules.order-count.gold-threshold=5
membership.rules.order-count.platinum-threshold=20
membership.rules.monthly-spend.gold-threshold=2000
membership.rules.monthly-spend.platinum-threshold=10000
membership.rules.cohort.gold-cohorts=VIP
membership.rules.cohort.platinum-cohorts=CORPORATE
```

## Note on `application.properties`

`application.properties` is committed intentionally to deploy over railway. The credentials it contains (`membership_user`/`membership_pass`) are local Docker defaults — identical to what is already in `docker-compose.yml`. In a production environment these values are overridden by environment variables injected at runtime. `application.properties.example` serves as the documented template for new project contributors.
