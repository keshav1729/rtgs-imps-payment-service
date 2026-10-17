# Production-readiness checklist

This repository is a structured starter, not a certified payment processor. Do not connect real money until each applicable item has an owner, tests, evidence and sign-off.

## Money movement correctness
- [ ] Official provider docs reviewed and exact IMPS/RTGS limits, cutoffs, holidays, fees and response semantics encoded.
- [ ] Stable provider-side idempotency / unique client reference contract confirmed.
- [ ] A durable worker claims payments using DB locking/leases; no duplicate concurrent dispatch.
- [ ] Ambiguous timeouts become UNKNOWN and trigger inquiry/reconciliation; never blind-resubmit.
- [ ] Cancellation, reversal, refund and return flows defined with provider semantics.
- [ ] Double-entry ledger or authoritative balance system integration defined.
- [ ] Beneficiary ownership and account validation enforced.
- [ ] Out-of-order and duplicate callbacks verified, deduplicated and audited.
- [ ] Daily and intraday reconciliation, mismatch queue and operations workflow tested.

## Security and compliance
- [ ] Approved OAuth2 issuer, audience, scopes and customer-claim mapping configured.
- [ ] Per-customer authorization verified for every read/write path.
- [ ] Provider mTLS/signature requirements implemented using managed secrets/certificates.
- [ ] Secrets rotation, encryption, log redaction, retention and access reviews.
- [ ] Threat model, dependency scan, SAST/DAST, penetration test and incident response reviewed.
- [ ] Applicable bank, RBI, privacy, AML/sanctions and payment-network requirements assessed by accountable teams.

## Reliability and operations
- [ ] Outbox row claiming safe across multiple replicas (e.g. SKIP LOCKED / lease) and backoff implemented.
- [ ] Kafka consumer inbox/deduplication, schema compatibility and poison-message handling.
- [ ] DB HA, backups, point-in-time recovery and restore drills completed.
- [ ] SLOs, dashboards, alerts, trace IDs and on-call runbooks.
- [ ] Load, soak, chaos, failover and concurrency tests.
- [ ] Staged rollout, rollback and emergency kill switch for new payment initiation.
- [ ] Provider sandbox/UAT/certification and production approval completed.
