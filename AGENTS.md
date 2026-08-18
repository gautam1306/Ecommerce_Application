# AGENTS.md — Codebase Map for AI Coding Agents

Purpose: give an agent enough structural/behavioral context to work in this repo
**without** grepping/opening every file first. Read this before exploring source code.

## 1. What this repo is

A multi-module Maven **Spring Boot microservices** e-commerce backend. Each top-level
folder is an independent, independently-runnable Spring Boot application (its own
`pom.xml`, own `mvnw`, own `docker-compose.yml`). There is no parent/aggregator POM —
modules are siblings, not a reactor build.

```
eureka-server/     Service registry (Netflix Eureka). Must start first.  Port 8761
config-server/     Spring Cloud Config Server (native profile, serves config-repo/).  Port 8888
api-gateway/       Spring Cloud Gateway (MVC-style routes). Single client entry point.  Port 8085
product-service/   Product catalog, MongoDB-backed.  Port 8080 (default)
inventory-service/ Stock levels, MySQL-backed.  Port 8082
order-service/     Order placement/lookup, MySQL-backed, calls inventory-service.  Port 8081
```

Startup order matters: `eureka-server` → `config-server` → the rest (each service does
`spring.config.import=optional:configserver:http://localhost:8888`, and registers with
Eureka at `http://localhost:8761/eureka/`).

## 2. Where config actually lives

Do **not** look for DB URLs / ports / Eureka URLs inside each service's own
`src/main/resources/application.properties` — those files only set
`spring.application.name` and the config-server import. The **real** per-service config
(ports, datasource URLs, Eureka URL, OAuth2 issuer) lives centrally in:

```
config-server/src/main/resources/config-repo/
  ├── product-service.properties
  ├── order-service.properties
  ├── inventory-service.properties
  └── api-gateway.properties
```

Config Server serves these over HTTP using the `native` profile
(`spring.cloud.config.server.native.search-locations=classpath:/config-repo`).
If asked to change a port, DB URL, or Eureka URL for a service, edit the file in
`config-repo/`, not the service's own `application.properties`.

## 3. Package layout convention (identical across product/order/inventory-service)

Each service follows the same package skeleton under `com.gautam.<service>`:

```
controller/   @RestController — thin, delegates to service layer
dto/          Request DTOs (records), validated with Jakarta Bean Validation (@Valid)
model/        JPA @Entity (order/inventory) or MongoDB @Document (product)
repository/   Spring Data JpaRepository / MongoRepository
service/      Business logic
exception/    Domain exceptions + GlobalExceptionHandler (@RestControllerAdvice) + ErrorResponse record
```

Each service also has its own `GlobalExceptionHandler` + `ErrorResponse` — these are
**not shared** (no common/shared module exists); if fixing error-handling, fix each
service's copy independently.

## 4. Service responsibilities & REST contracts

- **product-service** — `GET/POST /api/product` (MongoDB `Product` document).
- **inventory-service** — `GET /api/inventory?skuCode=&quantity=` → `boolean` (in stock?);
  `POST /api/inventory` (add stock, validated `InventoryRequest`).
- **order-service** — `POST /api/order` (validated `OrderRequest`) places an order;
  `GET /api/order?id=` returns an `Order` or throws `OrderNotFoundException` (→ HTTP 404).
  Placing an order first calls **inventory-service** via `InventoryClient`
  (`org.springframework.cloud.openfeign.FeignClient(name = "inventory-service")`,
  resolved through Eureka + Spring Cloud LoadBalancer — **not** a hardcoded URL). If out
  of stock, throws `OutOfStockException` (→ HTTP 409).
- **api-gateway** — routes are defined in code (`route/Routes.java`), one
  `RouterFunction` bean per downstream service, each using `.filter(lb("<service-name>"))`
  for load-balanced routing by Eureka service name. Also has `SecurityConfig` enforcing
  OAuth2/JWT resource-server auth (issuer: Keycloak realm `spring-security-microservice`
  at `localhost:8181`, see `api-gateway/docker-compose.yml` for the Keycloak container).

## 5. Cross-service call graph (for anyone changing inter-service behavior)

```
Client → api-gateway (JWT check) → order-service --Feign/LB (via Eureka)--> inventory-service
                                 → product-service
                                 → inventory-service
```

Only `order-service → inventory-service` is a service-to-service call. product-service
and inventory-service have no outbound calls to other services.

## 6. Local infra (per-service docker-compose.yml, no root-level compose file)

- `product-service/docker-compose.yml` — MongoDB
- `order-service/docker-compose.yml` — MySQL on 3306 (`OrderService` DB)
- `inventory-service/docker-compose.yml` — MySQL on 3308 (`InventoryService` DB)
- `api-gateway/docker-compose.yml` — Keycloak + its own MySQL (on 3310)

There is no single `docker-compose.yml` that starts everything — bring up each
service's compose file individually, or add a root-level one if asked.

## 7. Build/test commands (verified)

Run per-module, from inside each service folder (there's no aggregator POM):
```
./mvnw compile      # or mvnw.cmd on Windows
./mvnw test
```
Default test coverage is minimal — only the generated `contextLoads()` smoke test per
service (see `CHANGES.md` "Not yet addressed" section).

## 8. Known/intentional gaps (see CHANGES.md for full history)

- No shared/common library module — exception handling & DTO validation are duplicated
  per service by design (not an oversight to "fix" by merging into one module unless asked).
- `spring.jpa.hibernate.ddl-auto=update` in order/inventory-service (was `create`,
  fixed to `update`) — do not revert to `create` without discussing schema-drop implications.
- Config Server currently uses the `native` profile (local filesystem `config-repo/`),
  not a Git-backed backend.

## 9. Where to look first for common tasks

| Task | Start here |
|---|---|
| Change a port / DB URL / Eureka URL | `config-server/.../config-repo/<service>.properties` |
| Add a new REST endpoint | `<service>/.../controller/` + `service/` |
| Change validation rules | `<service>/.../dto/*Request.java` |
| Change error response shape | `<service>/.../exception/GlobalExceptionHandler.java` + `ErrorResponse.java` |
| Change gateway routing | `api-gateway/.../route/Routes.java` |
| Change gateway security | `api-gateway/.../config/SecurityConfig.java` |
| Change order↔inventory call | `order-service/.../client/InventoryClient.java` |
