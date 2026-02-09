# ⏱️ Distributed Task Scheduler — System Design


Cron-at-Scale, Delayed Execution, Priority Queues, Exactly-Once Delivery & Dead Letter Handling


## Functional Requirements


  1. **Task Submission** — Submit one-time or recurring tasks with execution time, priority, payload, and callback endpoint

  2. **Cron Scheduling** — Support cron expressions (minute/hour/day/month/weekday) with timezone awareness for recurring tasks

  3. **Delayed Execution** — Schedule tasks for future execution (seconds to months ahead) with guaranteed delivery

  4. **Priority Queues** — Multiple priority levels (CRITICAL, HIGH, MEDIUM, LOW) with preemptive scheduling for higher priorities

  5. **At-Least-Once / Exactly-Once Delivery** — Configurable delivery semantics with idempotency keys

  6. **Dead Letter Queue (DLQ)** — Failed tasks routed to DLQ after configurable retry policy (exponential backoff + jitter)

  7. **Task Management** — Cancel, pause, resume, query status of submitted tasks via API

  8. **Observability** — Execution metrics, latency histograms, failure rates, queue depth monitoring


## Non-Functional Requirements


  - **Scale** — 100M+ scheduled tasks, 1M+ executions/minute at peak, 10K+ concurrent worker processes

  - **Timing Accuracy** — <1 second jitter for immediate tasks, <5 second jitter for scheduled tasks

  - **Durability** — Zero task loss. All submitted tasks persisted before acknowledgement.

  - **Availability** — 99.99% uptime. Scheduler, queue, and worker tiers all independently fault-tolerant

  - **Consistency** — No double-execution for exactly-once tasks (distributed locking + idempotency tokens)

  - **Latency** — <50ms task submission API response; <100ms scheduling decision time

  - **Multi-Tenancy** — Per-tenant rate limits, quota, isolation; fair scheduling across tenants


## Flow 1: Task Submission & Scheduling


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 400" xmlns="http://www.w3.org/2000/svg">

  
<defs>

    
<marker id="ah1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">

      
<polygon points="0 0, 10 3.5, 0 7" fill="#ffa726">
</polygon>

    
</marker>

  
</defs>

  

  
<rect x="10" y="40" width="120" height="50" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="65" text-anchor="middle" fill="white" font-size="11">
Service A (API)
</text>

  
<text x="70" y="80" text-anchor="middle" fill="white" font-size="10">
Submit Task
</text>

  
<rect x="10" y="110" width="120" height="50" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="135" text-anchor="middle" fill="white" font-size="11">
Service B (Cron)
</text>

  
<text x="70" y="150" text-anchor="middle" fill="white" font-size="10">
Recurring Job
</text>

  
<rect x="10" y="180" width="120" height="50" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="205" text-anchor="middle" fill="white" font-size="11">
Admin UI
</text>

  
<text x="70" y="220" text-anchor="middle" fill="white" font-size="10">
Manual Trigger
</text>


  

  
<rect x="190" y="100" width="130" height="60" rx="8" fill="#e65100">
</rect>

  
<text x="255" y="125" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>

  
<text x="255" y="142" text-anchor="middle" fill="white" font-size="10">
(Auth + Rate Limit)
</text>


  

  
<rect x="380" y="40" width="160" height="65" rx="8" fill="#1565c0">
</rect>

  
<text x="460" y="65" text-anchor="middle" fill="white" font-size="12">
Scheduler Service
</text>

  
<text x="460" y="82" text-anchor="middle" fill="white" font-size="10">
(Validate + Persist + Ack)
</text>


  

  
<rect x="380" y="140" width="160" height="55" rx="10" fill="#5d4037">
</rect>

  
<text x="460" y="163" text-anchor="middle" fill="white" font-size="12">
Task Store (DB)
</text>

  
<text x="460" y="180" text-anchor="middle" fill="white" font-size="10">
(PostgreSQL + Sharded)
</text>


  

  
<rect x="610" y="40" width="150" height="65" rx="8" fill="#7b1fa2">
</rect>

  
<text x="685" y="65" text-anchor="middle" fill="white" font-size="12">
Timer Wheel
</text>

  
<text x="685" y="82" text-anchor="middle" fill="white" font-size="10">
(Hierarchical, per-shard)
</text>


  

  
<rect x="610" y="140" width="150" height="55" rx="8" fill="#c62828">
</rect>

  
<text x="685" y="163" text-anchor="middle" fill="white" font-size="12">
Ready Queue
</text>

  
<text x="685" y="180" text-anchor="middle" fill="white" font-size="10">
(Kafka / Redis Sorted Set)
</text>


  

  
<rect x="380" y="240" width="160" height="55" rx="8" fill="#006064">
</rect>

  
<text x="460" y="263" text-anchor="middle" fill="white" font-size="12">
Cron Evaluator
</text>

  
<text x="460" y="280" text-anchor="middle" fill="white" font-size="10">
(Next-fire Calculator)
</text>


  

  
<rect x="610" y="240" width="150" height="55" rx="8" fill="#37474f">
</rect>

  
<text x="685" y="263" text-anchor="middle" fill="white" font-size="11">
Partition Scanner
</text>

  
<text x="685" y="280" text-anchor="middle" fill="white" font-size="10">
(Poll for due tasks)
</text>


  

  
<line x1="130" y1="65" x2="188" y2="118" stroke="#ffa726" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="130" y1="135" x2="188" y2="130" stroke="#ffa726" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="130" y1="205" x2="188" y2="142" stroke="#ffa726" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="320" y1="130" x2="378" y2="72" stroke="#ffa726" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="460" y1="105" x2="460" y2="138" stroke="#ffa726" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="540" y1="72" x2="608" y2="72" stroke="#ffa726" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="685" y1="105" x2="685" y2="138" stroke="#ffa726" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="460" y1="195" x2="460" y2="238" stroke="#ffa726" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah1)">
</line>

  
<line x1="540" y1="267" x2="608" y2="267" stroke="#ffa726" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="685" y1="240" x2="685" y2="197" stroke="#ffa726" stroke-width="2" marker-end="url(#ah1)">
</line>


</svg>

</details>


### Submission Steps


  1. **API Validation** — Client submits task: `POST /api/v1/tasks` with body `{"task_type": "send_email", "execute_at": "2026-02-10T09:00:00Z", "priority": "HIGH", "payload": {...}, "idempotency_key": "abc123", "retry_policy": {"max_retries": 3, "backoff": "exponential"}}`. Gateway validates auth (JWT/API key), rate limits per tenant, validates schema.

  2. **Persistence** — Scheduler Service writes task to Task Store (PostgreSQL). Task status: `SCHEDULED`. Write is synchronous — API returns 202 Accepted only after DB commit. Task ID (UUIDv7, time-ordered) returned to client.

  3. **Timer Wheel Insertion** — For near-future tasks (<1 hour), inserted into in-memory Hierarchical Timer Wheel (HTW). For far-future tasks, stored in DB only — a Partition Scanner polls periodically.

  4. **Cron Evaluation** — For recurring tasks, Cron Evaluator calculates next fire time using cron expression + timezone. Creates the next scheduled instance. After each execution, the evaluator re-calculates and schedules the next instance (no pre-creation of all future runs).

  5. **Ready Queue** — When `execute_at` time arrives, task is moved from timer wheel → Ready Queue (Kafka topic partitioned by priority, or Redis Sorted Set with score = timestamp). Workers consume from Ready Queue.


> **
  **Example — Delayed email reminder:**  

  E-commerce service schedules: `POST /api/v1/tasks` body: `{"task_type": "abandoned_cart_email", "execute_at": "2026-02-09T22:00:00Z", "priority": "MEDIUM", "payload": {"user_id": "u_789", "cart_id": "c_456", "items": ["Widget A", "Gadget B"]}, "idempotency_key": "cart-reminder-c_456", "callback_url": "https://email-service/send"}`  

  Response: `{"task_id": "01952a3b-...", "status": "SCHEDULED", "execute_at": "2026-02-09T22:00:00Z"}`  

  At 22:00 UTC, task fires → worker POSTs to callback_url with payload → email sent → task status: COMPLETED.


> **
  **Deep Dive — Hierarchical Timer Wheel (HTW):**  

  Inspired by Hashed and Hierarchical Timing Wheels (Varghese & Lauck, 1987). Structure: multiple wheel levels — *second wheel* (60 slots), *minute wheel* (60 slots), *hour wheel* (24 slots). A task at T+3h47m22s is placed in hour wheel slot 3. When hour 3 ticks, tasks are cascaded down to minute wheel slot 47. When minute 47 ticks, cascaded to second wheel slot 22. When slot fires, task moves to Ready Queue. Insertion: O(1). Firing: O(1) amortized. Memory: minimal — only near-future tasks in memory. Each scheduler shard runs its own HTW for its partition of tasks.


> **
  **Deep Dive — Partition Scanner (Long-Duration Tasks):**  

  Tasks scheduled far in the future (hours to months) stay in the database only — no in-memory representation. A **Partition Scanner** runs every 30-60 seconds per shard:  

  `SELECT task_id, execute_at, priority FROM tasks WHERE shard_id = :s AND status = 'SCHEDULED' AND execute_at <= NOW() + INTERVAL '5 minutes' ORDER BY execute_at LIMIT 1000`  

  Matching tasks are loaded into the Timer Wheel for precise sub-second firing. The scan window (5 minutes ahead) ensures tasks are always loaded before their deadline. Index on `(shard_id, status, execute_at)` makes this query efficient even with billions of rows.


## Flow 2: Task Execution & Worker Pool


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 370" xmlns="http://www.w3.org/2000/svg">

  
<defs>

    
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">

      
<polygon points="0 0, 10 3.5, 0 7" fill="#ffa726">
</polygon>

    
</marker>

  
</defs>

  

  
<rect x="10" y="120" width="140" height="60" rx="8" fill="#c62828">
</rect>

  
<text x="80" y="145" text-anchor="middle" fill="white" font-size="12">
Ready Queue
</text>

  
<text x="80" y="162" text-anchor="middle" fill="white" font-size="10">
(Priority-Ordered)
</text>


  

  
<rect x="210" y="40" width="140" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="280" y="62" text-anchor="middle" fill="white" font-size="11">
Worker Pod 1
</text>

  
<text x="280" y="78" text-anchor="middle" fill="white" font-size="10">
(Claim + Execute)
</text>

  
<rect x="210" y="105" width="140" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="280" y="127" text-anchor="middle" fill="white" font-size="11">
Worker Pod 2
</text>

  
<rect x="210" y="170" width="140" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="280" y="192" text-anchor="middle" fill="white" font-size="11">
Worker Pod N
</text>


  

  
<rect x="210" y="250" width="140" height="50" rx="8" fill="#7b1fa2">
</rect>

  
<text x="280" y="272" text-anchor="middle" fill="white" font-size="11">
Distributed Lock
</text>

  
<text x="280" y="288" text-anchor="middle" fill="white" font-size="10">
(Redis / ZooKeeper)
</text>


  

  
<rect x="420" y="60" width="150" height="55" rx="8" fill="#e65100">
</rect>

  
<text x="495" y="83" text-anchor="middle" fill="white" font-size="11">
Task Executor
</text>

  
<text x="495" y="100" text-anchor="middle" fill="white" font-size="10">
(HTTP/gRPC Callback)
</text>


  

  
<rect x="640" y="30" width="130" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="705" y="57" text-anchor="middle" fill="white" font-size="11">
Email Service
</text>

  
<rect x="640" y="90" width="130" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="705" y="117" text-anchor="middle" fill="white" font-size="11">
Payment Service
</text>

  
<rect x="640" y="150" width="130" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="705" y="177" text-anchor="middle" fill="white" font-size="11">
Analytics Service
</text>


  

  
<rect x="420" y="160" width="150" height="55" rx="8" fill="#006064">
</rect>

  
<text x="495" y="183" text-anchor="middle" fill="white" font-size="11">
Result Handler
</text>

  
<text x="495" y="200" text-anchor="middle" fill="white" font-size="10">
(Ack / Retry / DLQ)
</text>


  

  
<rect x="420" y="260" width="150" height="50" rx="8" fill="#c62828">
</rect>

  
<text x="495" y="283" text-anchor="middle" fill="white" font-size="11">
Retry Queue
</text>

  
<text x="495" y="298" text-anchor="middle" fill="white" font-size="10">
(Exp Backoff + Jitter)
</text>


  

  
<rect x="640" y="260" width="130" height="50" rx="8" fill="#37474f">
</rect>

  
<text x="705" y="283" text-anchor="middle" fill="white" font-size="11">
Dead Letter Queue
</text>

  
<text x="705" y="298" text-anchor="middle" fill="white" font-size="10">
(Manual Review)
</text>


  

  
<line x1="150" y1="140" x2="208" y2="70" stroke="#ffa726" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="150" y1="150" x2="208" y2="130" stroke="#ffa726" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="150" y1="160" x2="208" y2="195" stroke="#ffa726" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="350" y1="65" x2="418" y2="82" stroke="#ffa726" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="350" y1="130" x2="418" y2="90" stroke="#ffa726" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="570" y1="77" x2="638" y2="52" stroke="#ffa726" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="570" y1="87" x2="638" y2="112" stroke="#ffa726" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="570" y1="97" x2="638" y2="172" stroke="#ffa726" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="495" y1="115" x2="495" y2="158" stroke="#ffa726" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="495" y1="215" x2="495" y2="258" stroke="#ffa726" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="570" y1="285" x2="638" y2="285" stroke="#ffa726" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="280" y1="220" x2="280" y2="248" stroke="#ffa726" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Execution Steps


  1. **Task Claiming** — Worker pulls task from Ready Queue. For exactly-once: worker acquires distributed lock (Redis `SET task:{id}:lock {worker_id} NX EX 300`) before processing. Lock TTL = expected execution time + buffer. Kafka-based: consumer group assigns partitions, at-least-once delivery with idempotency key dedup.

  2. **Execution** — Worker invokes the task's callback: HTTP POST to `callback_url` with payload, or gRPC call, or inline function execution (for co-located tasks). Timeout: configurable per task type (default 30s). Worker sends heartbeat to extend lock TTL for long-running tasks.

  3. **Result Handling** — On success (HTTP 2xx): mark task `COMPLETED`, release lock, record execution duration. On failure (HTTP 4xx/5xx or timeout): increment retry count, calculate next retry time using exponential backoff with jitter: `delay = min(base * 2^attempt + random(0, base), max_delay)`.

  4. **Retry Policy** — Task re-enqueued to Retry Queue with new `execute_at = NOW() + backoff_delay`. Retry Queue feeds back into Timer Wheel. Example: retries at 1s, 2s, 4s, 8s, 16s, 32s, 60s, 60s, 60s (max 9 retries).

  5. **Dead Letter Queue** — After max retries exhausted, task moved to DLQ with full execution history (all attempt timestamps, error responses, stack traces). DLQ tasks require manual review or automated remediation. Alert fires on DLQ depth threshold.


> **
  **Example — Payment retry flow:**  

  Task: charge customer subscription renewal. Attempt 1: payment gateway returns 503 (overloaded) → retry at +2s.  

  Attempt 2: timeout after 30s → retry at +4s + 1.3s jitter = +5.3s.  

  Attempt 3: payment gateway returns 200 OK → task COMPLETED.  

  Idempotency key `sub-renew-u789-2026-02` ensures even if worker crashes mid-execution and another worker retries, the payment gateway deduplicates the charge.


> **
  **Deep Dive — Exactly-Once Delivery:**  

  True exactly-once is impossible in distributed systems (Two Generals' Problem). Practical approach: **at-least-once delivery + idempotent execution**.  

  1. *Producer side*: client provides `idempotency_key`. Scheduler deduplicates submissions within a window (check Redis/DB: `SET idempotency:{key} {task_id} NX EX 86400`).  

  2. *Consumer side*: worker passes idempotency key to target service. Target service maintains idempotency store (Redis hash or DB table). Before executing side effects, checks if key already processed.  

  3. *Fencing tokens*: distributed lock returns a monotonically increasing token. Worker includes token in callback. Target service rejects requests with stale tokens (from previous lock holder who may still be running after lock expiry).


> **
  **Deep Dive — Exponential Backoff with Full Jitter:**  

  Formula: `sleep = random_between(0, min(cap, base * 2^attempt))`  

  Where `base = 1 second`, `cap = 60 seconds`. Full jitter (vs. equal jitter or decorrelated jitter) provides the best spread to avoid thundering herd when many tasks fail simultaneously. AWS recommends full jitter for retries. Example progression: attempt 0: 0-1s, attempt 1: 0-2s, attempt 2: 0-4s, attempt 3: 0-8s, attempt 4: 0-16s, attempt 5: 0-32s, attempt 6+: 0-60s. The randomization ensures retry storms don't create synchronized load spikes on downstream services.


## Flow 3: Cron Scheduling & Recurring Tasks


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 320" xmlns="http://www.w3.org/2000/svg">

  
<defs>

    
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">

      
<polygon points="0 0, 10 3.5, 0 7" fill="#ffa726">
</polygon>

    
</marker>

  
</defs>

  

  
<rect x="10" y="110" width="140" height="60" rx="8" fill="#2e7d32">
</rect>

  
<text x="80" y="135" text-anchor="middle" fill="white" font-size="11">
Cron Job Definition
</text>

  
<text x="80" y="152" text-anchor="middle" fill="white" font-size="10">
"0 9 * * MON-FRI"
</text>


  

  
<rect x="200" y="50" width="150" height="55" rx="8" fill="#7b1fa2">
</rect>

  
<text x="275" y="73" text-anchor="middle" fill="white" font-size="12">
Cron Registry
</text>

  
<text x="275" y="90" text-anchor="middle" fill="white" font-size="10">
(Job Definitions DB)
</text>


  

  
<rect x="200" y="140" width="150" height="55" rx="8" fill="#1565c0">
</rect>

  
<text x="275" y="163" text-anchor="middle" fill="white" font-size="12">
Cron Trigger Service
</text>

  
<text x="275" y="180" text-anchor="middle" fill="white" font-size="10">
(Leader-Elected)
</text>


  

  
<rect x="410" y="50" width="160" height="55" rx="8" fill="#006064">
</rect>

  
<text x="490" y="73" text-anchor="middle" fill="white" font-size="11">
Next Fire Calculator
</text>

  
<text x="490" y="90" text-anchor="middle" fill="white" font-size="10">
(Quartz-like Engine)
</text>


  

  
<rect x="410" y="140" width="160" height="55" rx="8" fill="#e65100">
</rect>

  
<text x="490" y="163" text-anchor="middle" fill="white" font-size="12">
Task Instance Creator
</text>

  
<text x="490" y="180" text-anchor="middle" fill="white" font-size="10">
(Create One-Time Task)
</text>


  

  
<rect x="410" y="230" width="160" height="55" rx="8" fill="#c62828">
</rect>

  
<text x="490" y="253" text-anchor="middle" fill="white" font-size="11">
Overlap Policy Engine
</text>

  
<text x="490" y="270" text-anchor="middle" fill="white" font-size="10">
(Skip / Queue / Allow)
</text>


  

  
<rect x="640" y="140" width="150" height="55" rx="8" fill="#1565c0">
</rect>

  
<text x="715" y="163" text-anchor="middle" fill="white" font-size="12">
Scheduler Service
</text>

  
<text x="715" y="180" text-anchor="middle" fill="white" font-size="10">
(Timer Wheel)
</text>


  

  
<rect x="640" y="230" width="150" height="55" rx="8" fill="#37474f">
</rect>

  
<text x="715" y="253" text-anchor="middle" fill="white" font-size="11">
Execution History
</text>

  
<text x="715" y="270" text-anchor="middle" fill="white" font-size="10">
(Per-Job Audit Log)
</text>


  

  
<line x1="150" y1="130" x2="198" y2="80" stroke="#ffa726" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="150" y1="145" x2="198" y2="165" stroke="#ffa726" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="350" y1="77" x2="408" y2="77" stroke="#ffa726" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="350" y1="167" x2="408" y2="167" stroke="#ffa726" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="490" y1="105" x2="490" y2="138" stroke="#ffa726" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="490" y1="195" x2="490" y2="228" stroke="#ffa726" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah3)">
</line>

  
<line x1="570" y1="167" x2="638" y2="167" stroke="#ffa726" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="715" y1="195" x2="715" y2="228" stroke="#ffa726" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Cron Scheduling Steps


  1. **Job Registration** — Client registers recurring job: `POST /api/v1/cron-jobs` with `{"name": "daily_report", "cron": "0 9 * * MON-FRI", "timezone": "America/New_York", "task_type": "generate_report", "payload": {...}, "overlap_policy": "SKIP"}`. Job definition persisted in Cron Registry.

  2. **Next Fire Calculation** — Cron Trigger Service (leader-elected via ZooKeeper/etcd) evaluates all active cron jobs. For each: parse cron expression, compute next fire time respecting timezone and DST transitions. Uses Quartz-like CronExpression parser supporting: `L` (last day), `W` (nearest weekday), `#` (nth weekday of month).

  3. **Instance Creation** — At the computed fire time, Trigger Service creates a one-time task instance (same as regular task submission). The instance inherits payload, priority, retry policy from the cron job definition.

  4. **Overlap Policy** — Before creating a new instance, checks if previous instance is still running:  

    • **SKIP**: Don't create new instance if previous still executing (prevents pile-up)  

    • **QUEUE**: Create instance but delay until previous completes  

    • **ALLOW**: Create regardless (allow parallel executions)  

    • **CANCEL_RUNNING**: Cancel previous, start new
  

  5. **Re-scheduling** — After instance executes (success or exhausted retries), Trigger Service computes next fire time and schedules next instance. Execution history maintained for debugging and SLA reporting.


> **
  **Example — Weekday report generation:**  

  Cron: `0 9 * * MON-FRI` (TZ: America/New_York)  

  Monday 9:00 AM ET → Trigger Service creates task → worker calls Report Service → generates PDF → uploads to S3 → sends Slack notification → task COMPLETED.  

  Next fire: Tuesday 9:00 AM ET. During EST→EDT transition (March clock forward): no 2:00 AM fire for `0 2 * * *` (skipped); for EDT→EST (November clock back): fires only once (dedup by instance ID).


> **
  **Deep Dive — DST & Timezone Handling:**  

  Timezone-aware scheduling is surprisingly complex. Problems: *Spring forward* — 2:00 AM doesn't exist in America/New_York on the transition day. If cron fires at 2:30 AM, skip or fire at 3:00 AM? *Fall back* — 1:30 AM occurs twice. Fire once or twice? Industry convention (adopted by Quartz, Kubernetes CronJob, Temporal.io): spring forward → fire at next valid time (3:00 AM); fall back → fire on first occurrence only. Implementation: store cron in local timezone string, use IANA tz database (via Go's `time.LoadLocation` or Java's `ZoneId`). Never convert cron to UTC — it breaks on DST transitions. Always compute next fire time using the timezone-aware library.


## Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 520" xmlns="http://www.w3.org/2000/svg">

  
<defs>

    
<marker id="ah4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">

      
<polygon points="0 0, 10 3.5, 0 7" fill="#ffa726">
</polygon>

    
</marker>

  
</defs>

  

  
<rect x="10" y="30" width="120" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="57" text-anchor="middle" fill="white" font-size="11">
API Clients
</text>

  
<rect x="10" y="90" width="120" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="117" text-anchor="middle" fill="white" font-size="11">
Internal Services
</text>

  
<rect x="10" y="150" width="120" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="177" text-anchor="middle" fill="white" font-size="11">
Admin Dashboard
</text>


  

  
<rect x="180" y="70" width="130" height="55" rx="8" fill="#e65100">
</rect>

  
<text x="245" y="93" text-anchor="middle" fill="white" font-size="11">
API Gateway
</text>

  
<text x="245" y="110" text-anchor="middle" fill="white" font-size="10">
Auth + Rate Limit
</text>


  

  
<rect x="370" y="30" width="150" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="445" y="52" text-anchor="middle" fill="white" font-size="11">
Scheduler Shard 1
</text>

  
<text x="445" y="68" text-anchor="middle" fill="white" font-size="10">
(Timer Wheel)
</text>

  
<rect x="370" y="95" width="150" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="445" y="117" text-anchor="middle" fill="white" font-size="11">
Scheduler Shard N
</text>


  

  
<rect x="370" y="170" width="150" height="45" rx="8" fill="#006064">
</rect>

  
<text x="445" y="197" text-anchor="middle" fill="white" font-size="11">
Cron Trigger (Leader)
</text>


  

  
<rect x="600" y="30" width="150" height="55" rx="10" fill="#5d4037">
</rect>

  
<text x="675" y="52" text-anchor="middle" fill="white" font-size="11">
Task Store (PG)
</text>

  
<text x="675" y="70" text-anchor="middle" fill="white" font-size="10">
Sharded by task_id
</text>


  

  
<rect x="600" y="105" width="150" height="50" rx="8" fill="#c62828">
</rect>

  
<text x="675" y="127" text-anchor="middle" fill="white" font-size="11">
Ready Queue (Kafka)
</text>

  
<text x="675" y="145" text-anchor="middle" fill="white" font-size="10">
Priority Partitions
</text>


  

  
<rect x="600" y="175" width="150" height="45" rx="8" fill="#7b1fa2">
</rect>

  
<text x="675" y="202" text-anchor="middle" fill="white" font-size="11">
Lock Service (Redis)
</text>


  

  
<rect x="830" y="30" width="140" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="900" y="52" text-anchor="middle" fill="white" font-size="11">
Worker Pod 1
</text>

  
<text x="900" y="68" text-anchor="middle" fill="white" font-size="10">
(Execute + Callback)
</text>

  
<rect x="830" y="95" width="140" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="900" y="117" text-anchor="middle" fill="white" font-size="11">
Worker Pod N
</text>


  

  
<rect x="830" y="170" width="140" height="45" rx="8" fill="#37474f">
</rect>

  
<text x="900" y="197" text-anchor="middle" fill="white" font-size="11">
Dead Letter Queue
</text>


  

  
<rect x="370" y="260" width="150" height="50" rx="8" fill="#006064">
</rect>

  
<text x="445" y="282" text-anchor="middle" fill="white" font-size="11">
Prometheus Metrics
</text>

  
<text x="445" y="298" text-anchor="middle" fill="white" font-size="10">
+ Grafana Dashboards
</text>


  

  
<rect x="600" y="260" width="150" height="50" rx="8" fill="#c62828">
</rect>

  
<text x="675" y="282" text-anchor="middle" fill="white" font-size="11">
Alert Manager
</text>

  
<text x="675" y="298" text-anchor="middle" fill="white" font-size="10">
PagerDuty / Slack
</text>


  

  
<rect x="830" y="260" width="140" height="50" rx="8" fill="#37474f">
</rect>

  
<text x="900" y="282" text-anchor="middle" fill="white" font-size="11">
Config Store (etcd)
</text>

  
<text x="900" y="298" text-anchor="middle" fill="white" font-size="10">
Leader Election
</text>


  

  
<line x1="130" y1="52" x2="178" y2="87" stroke="#ffa726" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="130" y1="112" x2="178" y2="97" stroke="#ffa726" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="130" y1="172" x2="178" y2="107" stroke="#ffa726" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="310" y1="87" x2="368" y2="55" stroke="#ffa726" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="310" y1="100" x2="368" y2="120" stroke="#ffa726" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="520" y1="55" x2="598" y2="55" stroke="#ffa726" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="520" y1="120" x2="598" y2="125" stroke="#ffa726" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="520" y1="192" x2="598" y2="197" stroke="#ffa726" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="750" y1="55" x2="828" y2="55" stroke="#ffa726" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="750" y1="130" x2="828" y2="115" stroke="#ffa726" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="750" y1="197" x2="828" y2="192" stroke="#ffa726" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="445" y1="215" x2="445" y2="258" stroke="#ffa726" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah4)">
</line>

  
<line x1="520" y1="285" x2="598" y2="285" stroke="#ffa726" stroke-width="1.5" marker-end="url(#ah4)">
</line>


</svg>

</details>


> **
  **Example — Full end-to-end flow:**  

  1. E-commerce service → API Gateway → Scheduler Shard 3 (hash(task_id) % N) → persists to Task Store (PostgreSQL shard 3)  

  2. Task: `execute_at = NOW() + 30 minutes` → Partition Scanner loads into Timer Wheel in 25 minutes  

  3. Timer Wheel fires → task pushed to Kafka Ready Queue (partition = MEDIUM priority)  

  4. Worker Pod 7 consumes → acquires Redis lock `task:{id}:lock` → HTTP POST to Payment Service callback  

  5. Payment Service returns 200 → Worker releases lock → updates Task Store: status=COMPLETED, duration=1.2s  

  6. Prometheus scrapes: `task_execution_duration_seconds{task_type="charge_subscription"}` → Grafana dashboard updates  

  7. If DLQ depth > 100 → AlertManager → PagerDuty page to on-call engineer


## Database Schema


### PostgreSQL — Task Store (Sharded by task_id)


  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  


| Column            | Type                | Description                                                            |
|-------------------|---------------------|------------------------------------------------------------------------|
| `task_id`         | UUID (v7)  `PK`     | Time-ordered UUID. Shard key: `task_id % num_shards`                   |
| `tenant_id`       | VARCHAR(64)  `IDX`  | Multi-tenant isolation key                                             |
| `task_type`       | VARCHAR(128)  `IDX` | Task category: "send_email", "charge_payment", "generate_report"       |
| `status`          | ENUM  `IDX`         | SCHEDULED, READY, RUNNING, COMPLETED, FAILED, DEAD_LETTERED, CANCELLED |
| `priority`        | SMALLINT            | 0=CRITICAL, 1=HIGH, 2=MEDIUM, 3=LOW                                    |
| `execute_at`      | TIMESTAMPTZ  `IDX`  | Scheduled execution time (UTC stored, TZ-aware)                        |
| `payload`         | JSONB               | Task payload (max 256KB). Compressed for large payloads.               |
| `callback_url`    | TEXT                | HTTP/gRPC endpoint to invoke on execution                              |
| `callback_method` | VARCHAR(8)          | POST, PUT, gRPC                                                        |
| `idempotency_key` | VARCHAR(256)  `IDX` | Dedup key (scoped per tenant). Nullable.                               |
| `retry_count`     | SMALLINT            | Current retry attempt (0-indexed)                                      |
| `max_retries`     | SMALLINT            | Max retry attempts before DLQ                                          |
| `last_error`      | TEXT                | Last execution error message / HTTP status                             |
| `cron_job_id`     | UUID  `FK`          | Parent cron job (null for one-time tasks)                              |
| `shard_id`        | INT  `SHARD`        | Scheduler shard assignment (hash-based)                                |
| `locked_by`       | VARCHAR(128)        | Worker ID holding execution lock                                       |
| `locked_until`    | TIMESTAMPTZ         | Lock expiry time                                                       |
| `created_at`      | TIMESTAMPTZ         | Submission time                                                        |
| `started_at`      | TIMESTAMPTZ         | Execution start time                                                   |
| `completed_at`    | TIMESTAMPTZ         | Execution completion time                                              |


### Critical Indexes


  
  
  
  
  
  


| Index             | Columns                                            | Purpose                                                                                    |
|-------------------|----------------------------------------------------|--------------------------------------------------------------------------------------------|
| Partition Scanner | `(shard_id, status, execute_at)`                   | Find due tasks per shard. Most critical query. Partial index: `WHERE status = 'SCHEDULED'` |
| Tenant Lookup     | `(tenant_id, status, created_at DESC)`             | Task status dashboard per tenant                                                           |
| Idempotency       | `(tenant_id, idempotency_key)` UNIQUE              | Dedup on submission. Scoped per tenant.                                                    |
| Stale Lock Finder | `(status, locked_until)` WHERE status='RUNNING'    | Find tasks with expired locks for recovery                                                 |
| DLQ Query         | `(status, task_type)` WHERE status='DEAD_LETTERED' | DLQ review and manual replay                                                               |


### Cron Jobs Table


  
  
  
  
  
  
  
  
  
  
  
  


| Column             | Type               | Description                                              |
|--------------------|--------------------|----------------------------------------------------------|
| `cron_job_id`      | UUID  `PK`         | Job definition ID                                        |
| `tenant_id`        | VARCHAR(64)  `IDX` | Owner tenant                                             |
| `name`             | VARCHAR(256)       | Human-readable job name                                  |
| `cron_expression`  | VARCHAR(128)       | Standard cron: "0 9 * * MON-FRI"                         |
| `timezone`         | VARCHAR(64)        | IANA timezone: "America/New_York"                        |
| `task_type`        | VARCHAR(128)       | Task type for generated instances                        |
| `payload_template` | JSONB              | Template payload (can include `{{fire_time}}` variables) |
| `overlap_policy`   | ENUM               | SKIP, QUEUE, ALLOW, CANCEL_RUNNING                       |
| `next_fire_at`     | TIMESTAMPTZ  `IDX` | Pre-computed next fire time                              |
| `enabled`          | BOOLEAN            | Active/paused toggle                                     |
| `last_fired_at`    | TIMESTAMPTZ        | Last successful trigger time                             |


> **
  **Deep Dive — Sharding Strategy:**  

  Tasks are sharded across PostgreSQL instances by `task_id % num_shards` (consistent hashing for rebalancing). Each scheduler shard owns a range of task shards — ensuring that Timer Wheel loading and Partition Scanner queries are localized to one DB shard. **Shard rebalancing**: when adding scheduler instances, use consistent hashing ring (virtual nodes) to minimize task redistribution. During rebalance, both old and new owners scan for tasks — dedup via distributed lock prevents double-execution. Target: 16-64 DB shards, 8-32 scheduler instances.


## Cache & Queue Deep Dive


### Caching Layers


  
  
  
  
  


| Cache                 | Technology         | Strategy         | Eviction     | Details                                                                                                  |
|-----------------------|--------------------|------------------|--------------|----------------------------------------------------------------------------------------------------------|
| Idempotency Cache     | Redis              | Write-through    | TTL (24h)    | `SET idempotency:{tenant}:{key} {task_id} NX EX 86400`. Fast dedup on submission. DB is source of truth. |
| Task Status Cache     | Redis              | Write-behind     | TTL (5 min)  | Cache hot task statuses for dashboard queries. Invalidated on status change.                             |
| Cron Definition Cache | Local (in-process) | Periodic refresh | 30s TTL      | Cron Trigger Service caches all active job definitions. Refresh from DB every 30s.                       |
| Distributed Lock      | Redis (Redlock)    | Acquire/Release  | TTL per lock | Worker lock: TTL = task timeout + 30s buffer. Heartbeat extends TTL every 10s for long-running tasks.    |


### Queue Architecture (Kafka)


  
  
  
  
  
  
  


| Topic                  | Partitions | Purpose                                                                                    |
|------------------------|------------|--------------------------------------------------------------------------------------------|
| `ready-tasks-critical` | 32         | Critical priority tasks. Separate topic for isolation — never starved by lower priorities. |
| `ready-tasks-high`     | 64         | High priority. Most task types.                                                            |
| `ready-tasks-medium`   | 64         | Medium priority. Bulk operations, reports.                                                 |
| `ready-tasks-low`      | 32         | Low priority. Analytics, cleanup jobs.                                                     |
| `task-results`         | 32         | Execution results: success/failure status, duration, error details.                        |
| `dead-letter`          | 16         | Failed tasks after max retries. Alerts on consumer lag.                                    |


> **
  **Deep Dive — Priority Scheduling with Weighted Consumers:**  

  Workers subscribe to multiple priority topics with weighted consumption. Configuration: `critical:high:medium:low = 4:3:2:1`. Worker fetches 4 messages from critical per 1 from low. Implementation: each worker runs multiple Kafka consumers with different `max.poll.records` settings. Alternative: single consumer with priority-aware message selection using Kafka headers. During peak load: low-priority queues may accumulate — acceptable. If critical queue depth > 1000: auto-scale worker pods (HPA based on Kafka consumer group lag metric).


## Scaling Considerations


### Scaling by Tier


  
  
  
  
  
  
  


| Tier                    | Scaling Strategy                   | Key Metric               | Auto-Scale Trigger                                                      |
|-------------------------|------------------------------------|--------------------------|-------------------------------------------------------------------------|
| API Gateway             | Horizontal (stateless pods)        | Requests/sec             | CPU > 60% or RPS > 10K/pod                                              |
| Scheduler Service       | Horizontal + shard assignment      | Tasks managed per shard  | Manual shard rebalance (add scheduler → consistent hash redistribution) |
| Task Store (PostgreSQL) | Horizontal sharding (Citus/Vitess) | Queries/sec, storage     | Add shard at 80% capacity. Online shard split.                          |
| Ready Queue (Kafka)     | Add partitions + brokers           | Consumer lag, throughput | Partition count = max worker parallelism. Add brokers for throughput.   |
| Worker Pool             | Horizontal (K8s HPA)               | Queue depth, CPU         | Kafka consumer lag > 5000 → scale up. Lag = 0 for 5m → scale down.      |
| Lock Service (Redis)    | Redis Cluster (hash slots)         | Operations/sec, memory   | Add nodes at 70% memory. 16384 hash slot rebalance.                     |


### Multi-Region Deployment


  - **Active-Active** — Scheduler instances in each region. Tasks pinned to region of submission (data locality). Cross-region failover via DNS (Route53 health check).

  - **Global Tasks** — Some tasks need exactly-once globally (not per-region). Use global lock with Redis Cluster spanning regions (higher latency) or central coordination service.

  - **Data Replication** — PostgreSQL streaming replication (async, <1s lag) to standby region. On failover: promote standby, redirect traffic, re-scan for missed tasks.


> **
  **Deep Dive — Stale Lock Recovery:**  

  Problem: worker crashes mid-execution without releasing lock. Lock TTL expires but task status still RUNNING. Solution: **Stale Lock Sweeper** runs every 60s per shard:  

  `SELECT task_id FROM tasks WHERE status = 'RUNNING' AND locked_until < NOW() - INTERVAL '30 seconds'`  

  Matched tasks: reset status to SCHEDULED (for retry) or DEAD_LETTERED (if max retries reached). Sweeper also checks Redis lock state to confirm lock actually expired (avoid race with slow-but-alive workers). **Heartbeat protocol**: workers extend lock TTL every 10s by `PEXPIRE task:{id}:lock 300000`. If heartbeat stops, lock naturally expires.


## Tradeoffs & Alternative Approaches


> **
  **Tradeoff — Pull-Based (Kafka) vs. Push-Based (Timer-Triggered) Workers:**  

  *Pull-based*: Workers poll Kafka for ready tasks. Advantage: natural backpressure — workers only fetch what they can handle, Kafka handles persistence and replay. Disadvantage: polling adds latency (up to `fetch.max.wait.ms`, typically 100-500ms). *Push-based*: Timer fires → directly dispatches to worker via gRPC. Advantage: lower latency (<10ms). Disadvantage: requires worker health tracking, backpressure is harder (must implement admission control). Best hybrid: Kafka for durability + reliable delivery, with aggressive polling (10ms intervals) for latency-sensitive tasks.


> **
  **Tradeoff — PostgreSQL vs. Cassandra for Task Store:**  

  *PostgreSQL (chosen)*: Strong consistency, complex queries (range scans on execute_at, joins for cron jobs), transactional updates (status + lock atomically). Disadvantage: sharding requires Citus/Vitess, max ~100 shards practical. *Cassandra*: Linear horizontal scale to 1000s of nodes, great write throughput. Disadvantage: no range scans on non-partition-key columns (Partition Scanner query is expensive), eventual consistency complicates exactly-once. Best for: PostgreSQL for <100M tasks, Cassandra/ScyllaDB for >1B tasks with relaxed consistency.


> **
  **Tradeoff — In-Memory Timer Wheel vs. Database Polling Only:**  

  *Timer Wheel*: O(1) insert/fire, sub-second accuracy. Disadvantage: state lost on crash (must recover from DB), memory proportional to near-future tasks. *DB Polling only*: simpler architecture, no state to lose on crash. Disadvantage: polling interval = minimum jitter (1-5 seconds), higher DB load. Hybrid approach (chosen): DB for durability, Timer Wheel for precision. Recovery on crash: Partition Scanner immediately loads all due tasks from DB.


### Alternative Approaches


  
  
  
  
  
  
  


| Approach               | Technology                                              | When to Prefer                                                            |
|------------------------|---------------------------------------------------------|---------------------------------------------------------------------------|
| Workflow Engine        | Temporal.io, Apache Airflow, Cadence                    | Complex multi-step workflows with dependencies, human-in-the-loop steps   |
| Cloud-Native Scheduler | AWS Step Functions, CloudWatch Events, Cloud Scheduler  | Cloud-first, managed service, lower operational burden                    |
| Message Queue + Delay  | RabbitMQ (delayed message exchange), SQS (delay queues) | Simple delayed tasks without complex scheduling (max 15min delay for SQS) |
| Database-Only          | pg_cron, MySQL Event Scheduler                          | Simple cron within database. Low scale, single-node.                      |
| Kubernetes CronJob     | K8s native CronJob resource                             | Container-oriented workloads, already in K8s, moderate scale              |
| Redis-Based            | BullMQ (Node.js), Celery Beat (Python)                  | Single-language ecosystem, moderate scale, quick to implement             |


## Additional Information


### API Design


  
  
  
  
  
  
  
  
  


| Endpoint                                  | Method | Description                                                                                                                          |
|-------------------------------------------|--------|--------------------------------------------------------------------------------------------------------------------------------------|
| `/api/v1/tasks`                           | POST   | Submit new task. Returns 202 + task_id. Body: task_type, execute_at, priority, payload, callback_url, idempotency_key, retry_policy. |
| `/api/v1/tasks/{id}`                      | GET    | Get task status, execution history, next retry time.                                                                                 |
| `/api/v1/tasks/{id}/cancel`               | POST   | Cancel scheduled/queued task. Returns 409 if already running.                                                                        |
| `/api/v1/tasks/{id}/retry`                | POST   | Manually retry a failed/dead-lettered task.                                                                                          |
| `/api/v1/cron-jobs`                       | POST   | Register recurring job. Body: name, cron_expression, timezone, task_type, payload_template, overlap_policy.                          |
| `/api/v1/cron-jobs/{id}`                  | PATCH  | Update cron expression, pause/resume, modify payload.                                                                                |
| `/api/v1/cron-jobs/{id}/trigger`          | POST   | Manually trigger a cron job instance (bypass schedule).                                                                              |
| `/api/v1/tasks?tenant_id=X&status=FAILED` | GET    | List tasks with filters. Pagination: cursor-based (task_id).                                                                         |


### Observability & Monitoring


  - **Metrics (Prometheus)**:
    `task_submitted_total{task_type, priority, tenant}`,
    `task_executed_total{task_type, status}`,
    `task_execution_duration_seconds{task_type}` (histogram),
    `task_scheduling_delay_seconds` (actual_fire - scheduled_fire),
    `ready_queue_depth{priority}`,
    `dlq_depth{task_type}`
  

  - **Distributed Tracing** — OpenTelemetry trace from submission → scheduling → queue → worker → callback. Trace ID propagated in task metadata for end-to-end visibility.

  - **SLOs** — Scheduling accuracy: 99.9% of tasks fire within 5s of execute_at. Task success rate: 99.5% complete without DLQ. API latency: p99 < 100ms.

  - **Alerting** — DLQ depth > 100 → P2 page. Scheduling delay p95 > 10s → P1 page. Task Store replication lag > 5s → P1 page.


### Multi-Tenancy & Fair Scheduling


  - **Tenant Quotas** — Per-tenant limits: max scheduled tasks (10K default), max executions/minute (1K default), max payload size (256KB). Configurable per plan tier.

  - **Fair Queuing** — Weighted Fair Queuing (WFQ) at the worker level: even if Tenant A submits 100x more tasks than Tenant B, Tenant B's tasks get fair share of execution capacity. Implemented via per-tenant virtual clock in the scheduler.

  - **Isolation** — Tenant A's task failures don't affect Tenant B's DLQ metrics. Separate Kafka consumer groups per high-value tenant for critical isolation.

  - **Billing** — Metered by: task submissions, successful executions, execution duration (compute-seconds), payload storage. Usage tracked via event stream to billing service.