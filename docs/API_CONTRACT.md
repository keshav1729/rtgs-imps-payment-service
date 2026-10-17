# API contract (starter)

## Create payment
`POST /api/v1/payments/imps` or `/api/v1/payments/rtgs`

Headers: `Idempotency-Key` required; `Authorization: Bearer ...` required outside local/test.
Body:
```json
{"beneficiaryId":"beneficiary-123","amount":"2500.00","currency":"INR","reference":"ORDER-1001"}
```
Validation is illustrative. Implement provider-specific RTGS/IMPS amount, beneficiary and operating-window rules using official requirements.

## Get payment
- `GET /api/v1/payments/{paymentId}`
- `GET /api/v1/payments/reference/{reference}`

Lookup is scoped to the authenticated customer principal.

## Status meaning
- `CREATED`: stored internally.
- `PROCESSING`: internal workflow started; not proof the bank accepted it.
- `PENDING` / `UNKNOWN`: final outcome not confirmed.
- `SUCCESS` / `FAILED`: only use after authoritative provider response or reconciliation.
