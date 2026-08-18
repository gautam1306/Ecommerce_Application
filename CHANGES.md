# Changes

This document summarizes the code review findings and the fixes applied across the
`api-gateway`, `product-service`, `inventory-service`, and `order-service` modules.

## Bug fixes

- **inventory-service**
  - `InventoryService.isInStock()`: removed a dead `skuCode.split(" ")` statement whose
    result was discarded and had no effect.
  - `InventoryService.addStock()`: fixed to return `true` on success instead of always
    returning `false`.
  - `InventoryController` / `InventoryRequest`: added Jakarta Bean Validation
    (`@NotBlank`, `@NotNull`, `@Min`) and `@Valid` on the controller method.

- **order-service**
  - `OrderRepository.getOrdersById(Long id)` (returned `List<Order>` for a single id) was
    replaced with the standard `JpaRepository.findById(Long)`.
  - `OrderService.getOrders(Long id)` renamed to `getOrder(Long id)`, now returns a single
    `Order` and throws a new `OrderNotFoundException` (mapped to HTTP 404) when not found.
  - `OrderController.getOrder()` now returns the `Order` as JSON instead of `list.toString()`.
  - `OrderController.placeOrder()` now returns HTTP 201 Created and validates the request
    body with `@Valid`.
  - Replaced the raw `RuntimeException` thrown for out-of-stock orders with a dedicated
    `OutOfStockException`, mapped to HTTP 409 Conflict.
  - `OrderRequest`: added Jakarta Bean Validation annotations (`@NotBlank`, `@NotNull`,
    `@Min`, `@DecimalMin`).
  - Fixed MySQL JDBC URL to include `allowPublicKeyRetrieval=true`, required after
    switching from the deprecated `mysql-connector-java` driver to `mysql-connector-j`
    (the newer driver defaults to `caching_sha2_password`, which needs this flag over a
    non-SSL connection).

- **api-gateway**
  - `SecurityConfig.securityFilterChain()` was missing the `@Bean` annotation, so the
    method was never registered as a Spring bean and the intended security rules were not
    applied. Added `@Bean`.

- **product-service**
  - `ProductController.createProduct()` now validates the request body with `@Valid`.
  - `ProductRequest`: added Jakarta Bean Validation annotations (`@NotBlank`, `@NotNull`,
    `@DecimalMin`).

## New: global exception handling

Added a `@RestControllerAdvice`-based `GlobalExceptionHandler` (plus a shared
`ErrorResponse` record) to `order-service`, `inventory-service`, and `product-service`.
Each handler returns a consistent JSON error body (`timestamp`, `status`, `error`,
`message`, and `fieldErrors` for validation failures) for:
- Domain-specific exceptions (`OrderNotFoundException` → 404, `OutOfStockException` → 409
  in order-service)
- `MethodArgumentNotValidException` → 400, with per-field validation messages
- Any other unhandled exception → 500

## Dependency / build fixes

- **product-service/pom.xml**: removed a duplicate `spring-boot-starter-data-mongodb`
  dependency declaration; added `spring-boot-starter-validation`.
- **order-service/pom.xml**: replaced the deprecated `mysql:mysql-connector-java:8.0.33`
  dependency with `com.mysql:mysql-connector-j` (matching inventory-service), and added
  `spring-boot-starter-validation`.
- **inventory-service/pom.xml**: added `spring-boot-starter-validation`.

## Housekeeping

- Removed the stray `order-service/reader.txt` debug leftover file
  (contained only `[2025-09-08 22:31:40] createevent failed 123`).
- Added a root-level `.gitignore` covering build output, IDE files, OS files, and local
  `.env` files for the whole multi-module repository (previously only per-service
  `.gitignore` files existed).

## Verified by local deployment

All four services were built (`mvnw compile`) and run locally against Podman containers
standing in for the `docker-compose.yml` infrastructure (MySQL for order-service and
inventory-service, MongoDB for product-service):

- `inventory-service` (8082): `POST /api/inventory` (add stock) and
  `GET /api/inventory` (check stock) verified working, `addStock` now returns `true`.
- `order-service` (8081): `POST /api/order` places an order against inventory;
  `GET /api/order?id=<id>` returns the order as JSON (200) or a structured 404 when the
  order does not exist; placing an order for an out-of-stock SKU returns a structured 409;
  an invalid request body returns a structured 400 with field-level validation errors.
- `api-gateway` (8085): starts successfully and now actually enforces the OAuth2/JWT
  security rule on every request (confirmed by a 401 response), which was previously not
  the case because the `securityFilterChain` bean was never registered.
- `product-service` (8080): compiles and starts successfully. Its MongoDB connectivity
  could not be fully exercised in this session because of an environment-specific Podman
  networking quirk on Windows (the container's port-forward for 27017 does not correctly
  proxy the MongoDB wire protocol over the machine's loopback interface); this is a local
  tooling limitation, not an application defect, and does not affect a normal
  Docker/`docker-compose` deployment.

## Not yet addressed (tracked as follow-up improvements)

- No Eureka/service-discovery module, despite being referenced in `ReadME.md`; all
  inter-service URLs are still hardcoded (`inventory.url`, gateway routes, Feign client).
- No centralized configuration server; DB credentials are duplicated per service and
  hardcoded in `docker-compose.yml` files.
- `spring.jpa.hibernate.ddl-auto=create` in inventory-service/order-service will drop and
  recreate the schema on every restart; recommend `update` or a migration tool
  (Flyway/Liquibase) for anything beyond local development.
- Test coverage is still limited to the default `contextLoads()` smoke test in each
  service.
