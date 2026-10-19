# RTGS / IMPS Payment Service

A backend payment service built using Java 21, Spring Boot, PostgreSQL, Kafka, Docker and Kubernetes.

## Features

- REST APIs for initiating IMPS and RTGS payments
- Payment lookup with customer-level access control
- PostgreSQL persistence with Flyway migrations
- Idempotency key support to handle duplicate payment requests
- Payment state management and optimistic locking
- Provider interface with a mock implementation for local testing
- Payment attempts and outbox event storage
- Kafka-based event publishing starter
- JWT-based authentication for non-local profiles
- Database tables for webhooks, audit logs and reconciliation
- Docker and Kubernetes configuration templates

## Tech Stack

- Java 21
- Spring Boot
- Maven
- PostgreSQL
- Flyway
- Spring Security OAuth2 Resource Server
- Kafka
- Docker
- Kubernetes

## Local Setup

1. Copy `.env.example` to `.env` and configure the local database password.
2. Start PostgreSQL and Kafka:

   ```bash
   docker compose --env-file .env up -d postgres kafka
   ```

3. Set the environment variables and run the application:

   ```bash
   export DB_PASSWORD='your-local-password'
   export SPRING_PROFILES_ACTIVE=local

   mvn clean verify
   mvn spring-boot:run
   ```

4. Check the application health:

   ```text
   GET http://localhost:8080/actuator/health
   ```

## API Endpoints

- `POST /api/v1/payments/imps` — Initiate an IMPS payment
- `POST /api/v1/payments/rtgs` — Initiate an RTGS payment
- Payment lookup endpoint — Retrieve a payment belonging to the authenticated customer

Payment initiation returns `202 Accepted` with a `PROCESSING` status. This confirms that the request was accepted by the application; it does not confirm that funds were transferred.

## Architecture

The client sends a payment request to the Spring Boot API. The service validates the request and stores the payment and its outbox event in PostgreSQL. The outbox publisher can publish events to Kafka, where consumers can process them.

A durable payment dispatcher and a real bank-provider integration are not yet implemented.

## Current Limitations

- No real bank API is connected. The mock provider is for local testing only.
- A durable dispatcher is needed to process pending payments and update their status.
- Production idempotency needs stronger replay handling and concurrency testing.
- The outbox publisher needs reliable multi-instance processing, retry handling and deduplication.
- Production JWT issuer and claim configuration must be set for the actual identity provider.
- Balance management, payment approvals, compliance checks, reversal handling and full reconciliation are not implemented.
- The project must be built and integration-tested before deployment.


