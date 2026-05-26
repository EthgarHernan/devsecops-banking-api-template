# Security Controls

This document maps the template to common financial-sector control expectations. It does not claim certified compliance with any regulation or standard.

| Control Area | Template Implementation |
|---|---|
| Authentication | Local-only Basic Auth for demo execution. Production should use OAuth2/OIDC with a corporate identity provider. |
| Authorization | API endpoints require authentication. Real deployments should add role and scope-based access. |
| Input validation | Bean Validation protects request DTOs before service execution. |
| Audit logging | `AuditAspect` records operation, method, timestamp, and outcome without logging sensitive payloads. |
| Dependency scanning | OWASP Dependency Check workflow and Maven plugin configuration. |
| Sensitive value management | Runtime configuration comes from environment variables; real deployments should use a managed vault. |
| Database migration control | Flyway migrations version the schema. Manual database changes are not the normal release path. |
| Least privilege | The compose file uses a dedicated local database user for the template. Production should reduce privileges further. |
| Error handling | Controller exception handlers return controlled errors and avoid stack trace exposure. |
| Secure Docker image | Multi-stage Docker build separates build tools from runtime image. |

## Notes

The template intentionally avoids money movement. Transaction APIs require stronger controls such as idempotency keys, ledger modeling, authorization scopes, fraud checks, reconciliation, and operational monitoring.

