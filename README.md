# RTGS / IMPS Payment Service — Production-Oriented Starter

Java 21, Spring Boot 4.1.1, Maven, PostgreSQL, Flyway, Spring Security OAuth2 Resource Server, Kafka, Docker and Kubernetes templates. Package: `com.payment.rtgsimps`; database: `rtgs_imps_db`.

## Important truth
This is **not production-certified and not ready for real-money traffic**. It includes core scaffolding and some working code patterns, but real bank transfer logic cannot be invented without your provider's official API documentation, credentials/certificates and UAT rules. No real provider adapter is configured. The non-local profile fails closed for provider submission. Mock provider is local/test only and never transfers money.

## Included
- IMPS and RTGS REST initiation + customer-scoped payment lookup.
- PostgreSQL payment schema, Flyway migrations, unique idempotency key constraint.
- Request validation, standard error response, payment state transitions and optimistic version field.
- Provider interface and local mock implementation.
- Payment attempts and outbox event tables; optional Kafka outbox publisher starter.
- OAuth2/JWT resource-server security outside local/test; local profile has a fixed test principal.
- Webhook, audit and reconciliation database tables (workflow implementation remains to be completed).
- Dockerfile, local PostgreSQL/Kafka Compose dependencies, Kubernetes Deployment/Service/HPA/Secret template.
- State-machine unit tests and production-readiness checklist.

## Known incomplete items that must not be mistaken for finished production features
1. No real bank adapter. Provider-specific auth, signing, mTLS, certificates, error mapping, status inquiry and callbacks must be implemented from approved docs.
2. Payment request persists a `PROCESSING` record and outbox event, but no durable dispatcher currently invokes `PaymentService.process`. Add a database-claiming worker before expecting status to progress.
3. Idempotency is a useful starter: concurrent duplicate insert can return 409. A full production API should use explicit idempotency records and robust transaction/replay semantics, with concurrency tests.
4. The outbox publisher is a basic polling example; it needs multi-replica row claiming/leases, backoff, metrics and a proper consumer inbox/deduplication implementation.
5. Local/test profile disables authentication for development and uses `local-customer`. Never deploy with `SPRING_PROFILES_ACTIVE=local` or `test`.
6. Production JWT issuer is a placeholder. Set `JWT_ISSUER_URI` and implement issuer/audience/scope/claim mapping for your actual identity provider.
7. No customer balance ledger, balance reservation, sanctions/AML checks, maker-checker approvals, full cancellation/reversal/refund, operational reconciliation processor, or certified settlement logic is implemented.
8. Code has not been compiled or integration-tested in this generated environment. Run `mvn clean verify` and fix any version/API compatibility issues before use.

## Requirements
- JDK 21 and Maven 3.9+
- PostgreSQL 17 (or a compatible supported version)
- Docker Compose optional
- Spring Boot 4.1.1 is pinned because that was the selected version. If your repository does not resolve it, use a released version available in your environment and test thoroughly.

## Local setup
1. Copy `.env.example` to `.env`; set a unique local-only password.
2. Start dependencies:
   ```bash
   docker compose --env-file .env up -d postgres kafka
   ```
3. Set local environment and run:
   ```bash
   export DB_PASSWORD='your-local-password'
   export SPRING_PROFILES_ACTIVE=local
   mvn clean verify
   mvn spring-boot:run
   ```
   PowerShell:
   ```powershell
   $env:DB_PASSWORD='your-local-password'
   $env:SPRING_PROFILES_ACTIVE='local'
   mvn clean verify
   mvn spring-boot:run
   ```
   If PostgreSQL already occupies port 5432, stop the Compose database or adjust its host port and `DB_URL`.
4. Health: `GET http://localhost:8080/actuator/health`.

## API examples (local profile only)
```bash
curl -i -X POST http://localhost:8080/api/v1/payments/imps \
  -H 'Content-Type: application/json' -H 'Idempotency-Key: demo-imps-001' \
  -d '{"beneficiaryId":"beneficiary-123","amount":"2500.00","currency":"INR","reference":"ORDER-1001"}'
```
```bash
curl -i -X POST http://localhost:8080/api/v1/payments/rtgs \
  -H 'Content-Type: application/json' -H 'Idempotency-Key: demo-rtgs-001' \
  -d '{"beneficiaryId":"beneficiary-456","amount":"200000.00","currency":"INR","reference":"ORDER-1002"}'
```
The API responds `202 Accepted` with a `PROCESSING` record. That is not proof of a transfer. A dispatcher and bank adapter are intentionally not enabled.

## Build container
```bash
mvn clean verify
docker build -t rtgs-imps-payment-service:1.0.0 .
```
Compose starts PostgreSQL and Kafka only; run the app via Maven or create a reviewed app service with correct environment. Kubernetes manifests are templates; update image, secrets, managed DB and ingress/TLS before applying.

## Next required implementation
Read `docs/PRODUCTION_READINESS.md`. The next coding task should be a durable dispatcher with database locking and tests, then provider-specific adapter + status inquiry/reconciliation, not enabling mock transfers in production.
