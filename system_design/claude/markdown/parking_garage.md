# 🅿️ System Design: Parking Garage System


## 📋 Functional Requirements


  - Vehicles can enter the garage — system assigns a spot based on vehicle size (compact, regular, large)

  - Vehicles can exit — system calculates parking fee based on duration and vehicle type

  - Real-time capacity tracking — display available spots per level/type on entrance displays

  - Support multiple payment methods (credit card, mobile app, cash, validated parking)


## 🔒 Non-Functional Requirements


  - **High availability:** Gate system must work 24/7 — cars cannot be stuck at entry/exit

  - **Consistency:** No double-assignment of parking spots — strong consistency required

  - **Low latency:** <2s for gate open decision (entry/exit)

  - **Offline resilience:** System must function during network/cloud outages (local fallback)

  - **Scale:** Multi-garage chain — thousands of garages, millions of spots globally


## 🔄 Flow 1 — Vehicle Entry


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 380" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="150" width="110" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="178" text-anchor="middle" fill="white" font-size="13">
Vehicle at
</text>

  
<text x="75" y="195" text-anchor="middle" fill="white" font-size="13">
Entry Gate
</text>

  
<line x1="130" y1="180" x2="190" y2="180" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="200" y="150" width="130" height="60" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="265" y="175" text-anchor="middle" fill="white" font-size="12">
LPR Camera
</text>

  
<text x="265" y="192" text-anchor="middle" fill="white" font-size="10">
(License Plate Read)
</text>

  
<line x1="330" y1="180" x2="400" y2="180" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="410" y="150" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="480" y="175" text-anchor="middle" fill="white" font-size="12">
Entry Controller
</text>

  
<text x="480" y="192" text-anchor="middle" fill="white" font-size="10">
(Local + Cloud)
</text>

  
<line x1="550" y1="165" x2="620" y2="105" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="550" y1="180" x2="620" y2="180" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="550" y1="195" x2="620" y2="260" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="630" y="75" width="150" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="705" y="100" text-anchor="middle" fill="white" font-size="12">
Spot Allocator
</text>

  
<text x="705" y="117" text-anchor="middle" fill="white" font-size="10">
(Best-Fit Assignment)
</text>

  
<rect x="630" y="155" width="150" height="55" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="705" y="178" text-anchor="middle" fill="white" font-size="12">
PostgreSQL
</text>

  
<text x="705" y="195" text-anchor="middle" fill="white" font-size="10">
(Ticket + Spot DB)
</text>

  
<rect x="630" y="235" width="150" height="55" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="705" y="258" text-anchor="middle" fill="white" font-size="12">
Display Board
</text>

  
<text x="705" y="275" text-anchor="middle" fill="white" font-size="10">
(Capacity Update)
</text>

  
<line x1="780" y1="103" x2="850" y2="180" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="860" y="155" width="140" height="55" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="930" y="178" text-anchor="middle" fill="white" font-size="12">
Gate Opens
</text>

  
<text x="930" y="195" text-anchor="middle" fill="white" font-size="10">
(Ticket Printed)
</text>


</svg>

</details>


### Step-by-Step


  1. **Vehicle detected:** Loop sensor or infrared beam detects vehicle at entry gate

  2. **LPR Camera:** License Plate Recognition camera captures plate → OCR extracts plate number → checked against reserved/banned list

  3. **Entry Controller:** Local edge controller (Raspberry Pi or industrial PC) handles logic. Communicates with cloud backend for cross-garage data, falls back to local DB if network is down

  4. **Capacity check:** Query available spots by vehicle type. If garage full → display "FULL" on entrance sign, don't open gate

  5. **Spot Allocator:** Selects optimal spot — nearest to entrance, matching vehicle size (compact car → compact spot preferred, can use regular if compact full). Uses `SELECT ... FOR UPDATE SKIP LOCKED` for concurrent allocation

  6. **Create ticket:** Insert parking_ticket record with `entry_time = NOW()`, assigned spot, plate number. Status = `active`

  7. **Gate opens:** Physical barrier lifts. Ticket printed (if paper-based) or QR code sent to mobile app. Display board updates: "Level 2: 45 spots available"


> **
**Example:** Honda Civic (compact) arrives at Gate A → LPR reads "ABC-1234" → System: 342 spots available (120 compact, 180 regular, 42 large) → Allocator assigns Spot C2-15 (Level 2, compact section) → Ticket T-20240209-001 created → Gate opens → Display: "Compact: 119 | Regular: 180 | Large: 42"


### 🔍 Component Deep Dives — Entry Flow


Spot Allocation Algorithm

-- Atomic spot allocation with pessimistic locking
BEGIN;
SELECT id, level, section, spot_number
FROM parking_spots
WHERE garage_id = 'G001'
  AND status = 'available'
  AND spot_type = 'compact'       -- match vehicle type
ORDER BY
  distance_to_entrance ASC,       -- nearest first
  level ASC                       -- lower levels preferred
LIMIT 1
FOR UPDATE SKIP LOCKED;           -- skip spots being allocated by other transactions

UPDATE parking_spots SET status = 'occupied', current_ticket_id = 'T-001' WHERE id = selected_spot_id;
INSERT INTO parking_tickets (id, plate, spot_id, entry_time, status) VALUES (...);
COMMIT;


**`FOR UPDATE SKIP LOCKED`:** Critical for concurrency. Multiple entry gates may try to allocate simultaneously. Without SKIP LOCKED, transactions would block each other. With it, each transaction locks one row and skips already-locked rows — no contention.


License Plate Recognition (LPR)


**Hardware:** IR camera (works at night) + OCR ML model (typically YOLO for detection + CRNN for character recognition)


**Accuracy:** ~97-99% in good conditions. Fallback: if OCR fails, issue ticket with manual plate entry option or ticket barcode only


**Uses:** (1) Match against reservations for pre-booked spots, (2) Ticketless exit (plate-based billing), (3) Ban list enforcement


Offline Resilience


**Edge controller** has local SQLite DB mirroring garage state. If cloud connection is lost:


  - Entry/exit continues using local DB

  - Spot allocation done locally (each gate has partition of spots to avoid conflicts)

  - When connection restores, sync queue replays events to cloud DB

  - Conflict resolution: cloud state wins for cross-garage data, local state wins for garage-specific operations during outage


## 🔄 Flow 2 — Vehicle Exit & Payment


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 380" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="150" width="110" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="178" text-anchor="middle" fill="white" font-size="13">
Vehicle at
</text>

  
<text x="75" y="195" text-anchor="middle" fill="white" font-size="13">
Pay Station
</text>

  
<line x1="130" y1="180" x2="200" y2="180" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="210" y="150" width="130" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="275" y="175" text-anchor="middle" fill="white" font-size="12">
Fee Calculator
</text>

  
<text x="275" y="192" text-anchor="middle" fill="white" font-size="10">
(Rate Engine)
</text>

  
<line x1="340" y1="180" x2="410" y2="180" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="420" y="150" width="140" height="60" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="490" y="175" text-anchor="middle" fill="white" font-size="12">
Payment Gateway
</text>

  
<text x="490" y="192" text-anchor="middle" fill="white" font-size="10">
(Card/Mobile/Cash)
</text>

  
<line x1="560" y1="180" x2="630" y2="180" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="640" y="150" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="710" y="175" text-anchor="middle" fill="white" font-size="12">
Exit Controller
</text>

  
<text x="710" y="192" text-anchor="middle" fill="white" font-size="10">
(Release Spot)
</text>

  
<line x1="780" y1="180" x2="850" y2="180" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="860" y="150" width="130" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="925" y="178" text-anchor="middle" fill="white" font-size="12">
Gate Opens
</text>

  
<text x="925" y="195" text-anchor="middle" fill="white" font-size="10">
(Spot Released)
</text>


</svg>

</details>


### Step-by-Step


  1. **Ticket scan:** Driver scans ticket barcode at pay station (or LPR matches plate to ticket)

  2. **Fee calculation:** Rate Engine computes fee based on: duration (entry_time → now), vehicle type, time-of-day rates, special events, validated parking discounts

  3. **Payment:** Driver pays via credit card (contactless tap), mobile app (Apple Pay / Google Pay), or cash machine. Payment processed through gateway (Stripe/Square)

  4. **Ticket marked paid:** Update ticket status to `paid`, record payment_amount, payment_method, exit_time

  5. **Exit gate:** Driver proceeds to exit gate. Scans paid ticket or LPR confirms paid status → Gate opens. Grace period: 15 minutes to exit after payment

  6. **Spot released:** Parking spot status → `available`. Display board updated. Analytics event emitted


> **
**Example:** Driver scans ticket at 5:45 PM, entered at 9:15 AM → Duration: 8h 30m → Rate: $3/hr first 2h, $5/hr after → Fee: $3×2 + $5×6.5 = $38.50 → Driver taps credit card → Payment approved → Ticket T-001 status = paid → Drives to exit gate → LPR confirms "ABC-1234" is paid → Gate opens → Spot C2-15 released


### 🔍 Component Deep Dives — Exit Flow


Rate Engine (Fee Calculation)


**Rate structures supported:**


  - **Flat rate:** $20/day regardless of duration

  - **Tiered hourly:** $3/hr first 2h, $5/hr for 2-8h, $2/hr after 8h

  - **Time-of-day:** Peak rates (8AM-6PM) vs off-peak (6PM-8AM)

  - **Event pricing:** Special rate during nearby stadium events (+50% surcharge)

  - **Daily maximum:** Cap at $35/day regardless of hourly calculation

  - **Validation:** Merchant stamps reduce fee (e.g., "2 hours free with restaurant purchase")


**Implementation:** Rate rules stored as JSON configuration per garage. Engine applies rules in priority order, selects minimum of (calculated rate, daily max).


Payment Integration


**Protocol:** HTTPS REST to payment gateway (Stripe Terminal for card, in-app SDK for mobile)


**Offline payment:** If gateway is down, kiosk stores payment intent locally. Card data encrypted with public key (not stored raw). When online, process stored payments. Cash machine is always offline-capable.


**Recurring/subscription:** Monthly parkers pre-pay — LPR recognizes plate, gate opens without payment stop


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 600" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ahc" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  

  
<text x="80" y="30" fill="#90a4ae" font-size="14" font-weight="bold">
Physical Layer (Per Garage)
</text>

  
<rect x="20" y="45" width="120" height="45" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="80" y="72" text-anchor="middle" fill="white" font-size="11">
Entry Gates (LPR)
</text>

  
<rect x="160" y="45" width="120" height="45" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="220" y="72" text-anchor="middle" fill="white" font-size="11">
Exit Gates (LPR)
</text>

  
<rect x="300" y="45" width="120" height="45" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="360" y="72" text-anchor="middle" fill="white" font-size="11">
Pay Stations
</text>

  
<rect x="440" y="45" width="120" height="45" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="500" y="72" text-anchor="middle" fill="white" font-size="11">
Spot Sensors
</text>

  
<rect x="580" y="45" width="120" height="45" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="640" y="72" text-anchor="middle" fill="white" font-size="11">
Display Boards
</text>

  

  
<line x1="350" y1="90" x2="350" y2="130" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="250" y="135" width="200" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="350" y="158" text-anchor="middle" fill="white" font-size="12">
Edge Controller (Local)
</text>

  
<text x="350" y="175" text-anchor="middle" fill="white" font-size="10">
(SQLite + Business Logic)
</text>

  

  
<line x1="350" y1="190" x2="350" y2="240" stroke="#aaa" stroke-dasharray="5,5" marker-end="url(#ahc)">
</line>

  
<text x="420" y="220" fill="#aaa" font-size="10">
Sync (MQTT/HTTPS)
</text>

  

  
<text x="80" y="260" fill="#90a4ae" font-size="14" font-weight="bold">
Cloud Layer (Multi-Garage)
</text>

  
<rect x="250" y="275" width="200" height="55" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="350" y="298" text-anchor="middle" fill="white" font-size="12">
API Gateway / LB
</text>

  
<text x="350" y="315" text-anchor="middle" fill="white" font-size="10">
(Auth + Routing)
</text>

  

  
<line x1="350" y1="330" x2="150" y2="380" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="350" y1="330" x2="350" y2="380" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="350" y1="330" x2="550" y2="380" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="350" y1="330" x2="750" y2="380" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="60" y="385" width="150" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="135" y="415" text-anchor="middle" fill="white" font-size="11">
Garage Mgmt Service
</text>

  
<rect x="260" y="385" width="150" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="335" y="415" text-anchor="middle" fill="white" font-size="11">
Billing Service
</text>

  
<rect x="460" y="385" width="150" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="535" y="415" text-anchor="middle" fill="white" font-size="11">
Reservation Service
</text>

  
<rect x="660" y="385" width="150" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="735" y="415" text-anchor="middle" fill="white" font-size="11">
Analytics Service
</text>

  

  
<line x1="335" y1="435" x2="335" y2="490" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="60" y="495" width="150" height="50" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="135" y="525" text-anchor="middle" fill="white" font-size="11">
PostgreSQL (Primary)
</text>

  
<rect x="260" y="495" width="150" height="50" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="335" y="525" text-anchor="middle" fill="white" font-size="11">
Redis (Real-time)
</text>

  
<rect x="460" y="495" width="150" height="50" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="535" y="525" text-anchor="middle" fill="white" font-size="11">
Kafka (Events)
</text>

  
<rect x="660" y="495" width="150" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="735" y="525" text-anchor="middle" fill="white" font-size="11">
TimescaleDB (Metrics)
</text>

  
<line x1="135" y1="435" x2="135" y2="490" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="535" y1="435" x2="535" y2="490" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="735" y1="435" x2="735" y2="490" stroke="#aaa" marker-end="url(#ahc)">
</line>


</svg>

</details>


> **
**End-to-End Example:** Multi-garage operator runs 50 garages across 10 cities → Each garage has edge controller with local SQLite → Events sync to cloud via MQTT → User reserves spot via mobile app (Reservation Service) → Arrives at garage → LPR reads plate, matches reservation → Gate opens, spot C2-15 assigned → Parks for 4 hours → Pay station calculates $18 → Pays with Apple Pay → Exit gate opens → Cloud analytics shows: 85% occupancy peak at 2 PM, $12K daily revenue, avg stay 3.2 hours


## 🗄️ Database Schema


### PostgreSQL — Core Data


garages


| Column          | Type          | Notes                       |
|-----------------|---------------|-----------------------------|
| id              | UUID          | `PK`                        |
| name            | VARCHAR(255)  |                             |
| address         | TEXT          |                             |
| latitude        | DECIMAL(10,7) |                             |
| longitude       | DECIMAL(10,7) |                             |
| total_spots     | INTEGER       |                             |
| levels          | INTEGER       |                             |
| operating_hours | JSONB         | Per-day open/close times    |
| rate_config     | JSONB         | Pricing rules               |
| status          | VARCHAR(20)   | open / closed / maintenance |


parking_spots


| Column               | Type        | Notes                                              |
|----------------------|-------------|----------------------------------------------------|
| id                   | UUID        | `PK`                                               |
| garage_id            | UUID        | `FK` `IDX`                                         |
| level                | SMALLINT    |                                                    |
| section              | VARCHAR(10) | A, B, C, etc.                                      |
| spot_number          | VARCHAR(10) |                                                    |
| spot_type            | VARCHAR(20) | compact / regular / large / handicap / ev_charging |
| status               | VARCHAR(20) | available / occupied / reserved / maintenance      |
| current_ticket_id    | UUID        | `FK`                                               |
| distance_to_entrance | INTEGER     | Meters (for allocation priority)                   |
| has_sensor           | BOOLEAN     |                                                    |


 `IDX` on (garage_id, status, spot_type) — efficient spot allocation query


parking_tickets


| Column         | Type          | Notes                                |
|----------------|---------------|--------------------------------------|
| id             | UUID          | `PK`                                 |
| garage_id      | UUID          | `FK` `IDX`                           |
| spot_id        | UUID          | `FK`                                 |
| plate_number   | VARCHAR(20)   | `IDX`                                |
| vehicle_type   | VARCHAR(20)   |                                      |
| entry_time     | TIMESTAMPTZ   |                                      |
| exit_time      | TIMESTAMPTZ   | NULL until exit                      |
| fee_amount     | DECIMAL(10,2) |                                      |
| payment_method | VARCHAR(20)   | card / mobile / cash / validated     |
| payment_status | VARCHAR(20)   | pending / paid / failed              |
| barcode        | VARCHAR(64)   | `IDX`                                |
| status         | VARCHAR(20)   | active / paid / exited / lost_ticket |


reservations


| Column         | Type          | Notes                                       |
|----------------|---------------|---------------------------------------------|
| id             | UUID          | `PK`                                        |
| user_id        | UUID          | `FK`                                        |
| garage_id      | UUID          | `FK`                                        |
| spot_id        | UUID          | `FK`                                        |
| plate_number   | VARCHAR(20)   |                                             |
| start_time     | TIMESTAMPTZ   |                                             |
| end_time       | TIMESTAMPTZ   |                                             |
| status         | VARCHAR(20)   | confirmed / cancelled / completed / no_show |
| prepaid_amount | DECIMAL(10,2) |                                             |


### Sharding Strategy


**All tables:** Shard by `garage_id` — all data for a garage is co-located. Queries are always scoped to a single garage (entry/exit/payment operations). Cross-garage queries (analytics dashboard) use scatter-gather.  `SHARD`


### Redis — Real-Time State


# Capacity counters (atomic, real-time)
HSET garage:{id}:capacity compact 119 regular 180 large 42
HINCRBY garage:{id}:capacity compact -1   # on entry

# Active ticket lookup by plate
SET plate:{plate_number} {ticket_json} EX 86400

# Monthly parker whitelist
SISMEMBER monthly:{garage_id} {plate_number}


## 💾 Cache Deep Dive


**Redis capacity counters** are the critical cache — updated atomically on every entry/exit. PostgreSQL is source of truth, but display boards and mobile app read from Redis for real-time counts. Periodic reconciliation (every 5 min) ensures Redis matches PostgreSQL.


**Eviction:** Not applicable — all keys are essential operational data with explicit TTLs. No LRU needed.


**CDN:** Not applicable for core operations. Static assets (mobile app) served via CDN. Garage information pages (address, rates) cached on CDN with 1-hour TTL.


## ⚖️ Tradeoffs


> **
  
> **
    ✅ Edge + Cloud Hybrid
    

Offline resilience — gates work during cloud outage. Local latency for physical operations. Cloud for cross-garage analytics and reservations

  
  
> **
    ❌ Edge + Cloud Hybrid
    

Data sync complexity. Conflict resolution during partition. Dual codebase maintenance (edge + cloud). Edge hardware management at scale

  
  
> **
    ✅ Spot Sensors (IoT)
    

Real-time per-spot occupancy — accurate capacity. Enables "guide to your spot" navigation. Detects vehicles that parked in wrong spot

  
  
> **
    ❌ Spot Sensors (IoT)
    

Expensive ($50-200 per spot × thousands of spots). Maintenance overhead (batteries, calibration). Most garages use gate-counting (entry - exit = occupancy) instead — less accurate but much cheaper

  


## 🔄 Alternative Approaches


Gate Counting vs Sensor-Based


Most garages use simple gate counting: capacity = total_spots - (entries - exits). No per-spot sensors needed. Cheaper but can't direct drivers to specific spots. Drift over time if gate miscounts (e.g., tailgating). Reset daily with manual audit.


Ticketless (LPR-Only) System


Modern garages eliminate paper tickets entirely. LPR at entry creates digital ticket. LPR at exit matches plate → calculates fee → pay at exit kiosk or mobile app. Advantages: faster throughput, no lost tickets, lower hardware cost. Disadvantage: LPR errors (~2% misread rate) require manual override.


## 📚 Additional Information


### OOP Design Patterns


Parking garage is a classic OOP interview question. Key patterns: **Strategy Pattern** for fee calculation (different rate strategies). **Observer Pattern** for display board updates (spots notify display on status change). **Singleton** for garage manager. **Factory Pattern** for creating different ticket types (hourly, daily, monthly).


### Concurrency Control


Critical challenge: two vehicles arriving simultaneously should not be assigned the same spot. Solutions: (1) Database-level: `SELECT FOR UPDATE SKIP LOCKED`, (2) Application-level: distributed lock per garage section using Redis `SETNX`, (3) Optimistic: attempt insert with unique constraint on (spot_id, status='occupied'), retry on conflict.


### IoT Communication — MQTT Protocol


Spot sensors and edge controllers communicate using MQTT (Message Queuing Telemetry Transport) — lightweight pub/sub protocol designed for IoT. QoS levels: 0 (at most once), 1 (at least once), 2 (exactly once). Topics: `garage/{id}/spot/{spot_id}/status`. Edge controller subscribes to all spots in its garage.