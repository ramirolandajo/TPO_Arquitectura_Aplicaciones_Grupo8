# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Spring Boot 3.4 / Java 21 microservices ecosystem (Maven multi-module, parent `pom.xml`). It is a university project (UADE) demonstrating service discovery, centralized config, JWT security, event-driven messaging, distributed tracing, and centralized logging. Documentation and code comments are in Spanish.

## Build & Run

There is **no Maven wrapper** (`mvnw`); a local Maven 3.9+ and JDK 21 are required.

```bash
mvn clean install              # build all modules from the repo root
mvn -pl inventory-service test # run tests for one module
```

> Note: there are currently no test classes in `src/test`. `mvn test` is effectively a no-op until tests are added.

### Running the system

Two mutually exclusive messaging brokers are selectable via Spring profiles (`rabbitmq` or `kafka`). The choice is purely infrastructure — no code changes needed.

**Docker (recommended):**
```bash
docker compose --profile rabbitmq up --build   # or --profile kafka
docker compose --profile rabbitmq down
```
Docker Compose handles startup ordering via `depends_on` + healthchecks. The `rabbitmq`/`kafka` Compose profiles gate which broker container and which `inventory-service-*` / `notification-service-*` variant run. Logstash is **not** containerized — run it from a local install.

**Manual (lower RAM):** start in this order, each in its own terminal. The config-server **must** use the `native` profile or it fails with _"Invalid config server configuration"_ (it defaults to a `git` backend with no repo configured):
```bash
cd config-server && mvn spring-boot:run -Dspring-boot.run.profiles=native   # 8888 — start first
cd eureka-server && mvn spring-boot:run                                      # 8761
cd auth-service && mvn spring-boot:run                                       # 8083
cd inventory-service && mvn spring-boot:run -Dspring-boot.run.profiles=rabbitmq   # 8082
cd api-gateway && mvn spring-boot:run                                        # 8080 — start after services register
cd notification-service && mvn spring-boot:run -Dspring-boot.run.profiles=rabbitmq  # 8084
```
inventory-service and notification-service must use the **same** broker profile. Without a broker profile, inventory-service falls back to `NoOpEventPublisherAdapter` (events are dropped, not published).

## Architecture

### Service topology

| Module | Port | Role |
|--------|------|------|
| config-server | 8888 | Centralized config (Spring Cloud Config, native/filesystem backend) |
| eureka-server | 8761 | Service discovery |
| auth-service | 8083 | Identity: login, JWT issuance (jjwt HS384), BCrypt, H2 (`authdb`) |
| api-gateway | 8080 | Single entry point: Spring Cloud Gateway (WebFlux), OAuth2 resource server, JWT validation |
| inventory-service | 8082 | Product CRUD, H2 (`inventorydb`), publishes `ProductCreatedEvent` |
| order-service | 8085 | Order CRUD, H2 (`ordersdb`), publishes `OrderCreatedEvent`, consumes `ProductCreatedEvent` (hexagonal, igual que inventory) |
| notification-service | 8084 | Consumes `ProductCreatedEvent` + `OrderCreatedEvent`, logs them |

Infrastructure: RabbitMQ (5672 / mgmt 15672), Kafka (9092, KRaft), Zipkin (9411), Elasticsearch (9200), Logstash (5044, local install only), Kibana (5601).

### Request & event flow

Client → **api-gateway** (validates JWT, propagates `Authorization` header downstream) → **inventory-service** (validates JWT as OAuth2 resource server, saves to H2, publishes event) → broker → **notification-service** (consumes, logs). auth-service issues the token at `/auth/login`.

**order-service** (8085) extiende el flujo: expone `/api/orders` (protegido por JWT vía gateway), persiste `Order` en H2, **publica** `OrderCreatedEvent` a `order.exchange` (lo consume notification-service) y **consume** `ProductCreatedEvent` desde `inventory.exchange` (cola propia `product.created.order.queue`). Está construido con la misma arquitectura hexagonal que inventory-service. Solo soporta el broker **RabbitMQ** (bajo perfil `kafka` arranca pero no publica/consume: usa `NoOpEventPublisherAdapter`).

### Config server is the source of truth

Each service has a minimal `src/main/resources/application.yml`, but the authoritative per-service config lives in **`config-server/config/<service-name>.yml`** plus shared `application.yml`, served at startup. When changing ports, datasource, JWT, tracing, etc., edit the file under `config-server/config/` — not just the one bundled in the service.

### JWT secret must stay in sync

`jwt.secret` is shared by auth-service (signs), api-gateway, and inventory-service (both validate). It must be identical across all three or token validation fails. Defined in their config files.

### inventory-service uses Hexagonal Architecture (ports & adapters)

This is the only service refactored to hexagonal; the others are conventional layered Spring services. Structure under `com.uade.inventory`:

- `domain/` — pure core, no framework deps. `model/Product.java` is a plain POJO (no JPA annotations). `domain/event/ProductCreatedEvent.java`. Ports: `port/in/ProductUseCase`, `port/out/ProductRepositoryPort`, `port/out/EventPublisherPort`.
- `application/service/ProductService.java` — use case implementation; publishes events through `EventPublisherPort` (does not know the broker).
- `infrastructure/adapter/in/web/` — REST controller (drives `ProductUseCase`).
- `infrastructure/adapter/out/persistence/` — JPA entity + `ProductMapper` (domain ↔ JPA) + `ProductPersistenceAdapter` implementing the repository port.
- `infrastructure/adapter/out/messaging/` — `RabbitMQPublisherAdapter` (`@Profile("rabbitmq")`), `KafkaPublisherAdapter` (`@Profile("kafka")`), `NoOpEventPublisherAdapter` (fallback when neither profile is active). The broker is a profile-selected adapter behind `EventPublisherPort`.

When adding a persistence backend or broker, implement the corresponding port and add an adapter — do not touch domain or application code.

### Messaging is profile-gated on both ends

inventory-service (producer) and notification-service (consumer) each ship `application-rabbitmq.yml` / `application-kafka.yml` and `@Profile`-annotated config/adapter/listener classes. notification-service has parallel `listener/ProductEventListener` (`@RabbitListener`) and `listener/KafkaProductEventListener` (`@KafkaListener`).

### Observability

- **Tracing:** Micrometer Tracing + Brave + Zipkin reporter on all services. `traceId`/`spanId` injected into log lines via the `logging.pattern.correlation` config. Sampling probability is set per service (e.g. inventory-service `application.yml` sets `0.0`; config-server files set `1.0` for dev).
- **Logging:** each service has `logback-spring.xml` shipping JSON logs over TCP to Logstash:5044 (`logstash-logback-encoder`). Pipeline in `logstash/pipeline.conf` filters healthchecks, extracts MDC fields, writes daily `logs-YYYY.MM.dd` indices to Elasticsearch, viewed in Kibana (`logs-*` index pattern).

## Test credentials & endpoints

- Users (auth-service, seeded): `admin`/`admin123` (ADMIN, USER), `user`/`user123` (USER).
- Login: `POST /auth/login` → `{ "token", "type": "Bearer", "expiresIn" }`. All requests go through the gateway on **8080**.
- H2 consoles (separate DBs per service): inventory `http://localhost:8082/h2-console` (`jdbc:h2:mem:inventorydb`), auth `http://localhost:8083/h2-console` (`jdbc:h2:mem:authdb`), user `sa`, empty password. `ddl-auto: create-drop` — data is wiped on restart.
- `requests.http` at the repo root has ready-to-run HTTP requests.
