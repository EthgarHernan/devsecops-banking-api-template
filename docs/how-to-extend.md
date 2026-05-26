# How to Extend

## Add New Endpoints

Add a controller method, request/response DTOs, service method, repository method, and tests. Keep validation at the DTO and service layers. Do not let controllers bypass services for business mutations.

## Add New Modules

Create a package per module and preserve clear boundaries: controller, service, repository, domain, and tests. Shared cross-cutting concerns should remain in configuration, security, and audit packages.

## Add OAuth2/OIDC

Replace local Basic Auth with Spring Security OAuth2 Resource Server. Validate issuer, audience, scopes, and token expiry. Map scopes to API permissions. Keep local demo auth only for development profiles if needed.

## Add Observability

Add structured logs, request correlation IDs, metrics, tracing, and health endpoints. Avoid logging request bodies for financial operations.

## Add Testcontainers

Repository integration tests can use Testcontainers to validate Flyway migrations and SQL behavior against a real PostgreSQL container.

## Add OpenAPI

Add generated API documentation only after DTOs and error contracts are stable. Document authentication requirements and response codes.

## Add Rate Limiting

Apply rate limiting at the gateway or application layer for sensitive endpoints. Define different thresholds for read and mutation operations.

## Add Account Transactions

Do not add transaction endpoints without ledger modeling, idempotency, double-entry consistency, reconciliation, fraud controls, and strong authorization.

