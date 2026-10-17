# Operations runbook starter

1. For provider timeouts, do not manually resubmit immediately. Query provider status using the stable provider reference/idempotency key.
2. For stale PENDING/UNKNOWN payments, run the approved status inquiry and reconciliation process; route mismatches to operations.
3. For Kafka outage, keep outbox durable, monitor backlog, restore broker and replay with idempotent consumers.
4. For database outage, stop new payment acceptance if durable persistence cannot be guaranteed; never acknowledge a payment that was not persisted.
5. For security incident, disable initiation using a reviewed kill switch, preserve audit evidence, rotate compromised secrets and follow incident response.
6. Before recovery actions, confirm whether bank-side transfer already occurred. Never infer failure from a client timeout alone.
