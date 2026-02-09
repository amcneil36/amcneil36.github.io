# 🥤 System Design: Vending Machine


## 📋 Functional Requirements


  - User can select a product and make payment (coins, bills, card, mobile)

  - Machine dispenses the selected product and returns correct change

  - Real-time inventory tracking — shows available products and quantities

  - Remote fleet management — operators can monitor stock levels, revenue, and machine health across all machines


## 🔒 Non-Functional Requirements


  - **Reliability:** Machine must correctly handle payments and dispensing — no double-charge, no free products

  - **Offline capability:** Cash transactions must work without internet; card transactions require connectivity

  - **Durability:** Transaction records persisted locally before cloud sync

  - **Scale:** Fleet of 100,000+ machines globally with centralized management

  - **Low latency:** <3s from payment confirmation to product dispensing


## 🔄 Flow 1 — Product Purchase (State Machine)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 500" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  

  
<rect x="50" y="50" width="140" height="50" rx="25" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="120" y="80" text-anchor="middle" fill="white" font-size="13">
IDLE
</text>

  
<line x1="190" y1="75" x2="280" y2="75" stroke="#4aff4a" marker-end="url(#ah1)">
</line>

  
<text x="235" y="65" text-anchor="middle" fill="#4aff4a" font-size="10">
select product
</text>

  
<rect x="290" y="50" width="160" height="50" rx="25" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="370" y="80" text-anchor="middle" fill="white" font-size="13">
PRODUCT_SELECTED
</text>

  
<line x1="450" y1="75" x2="540" y2="75" stroke="#4aff4a" marker-end="url(#ah1)">
</line>

  
<text x="495" y="65" text-anchor="middle" fill="#4aff4a" font-size="10">
insert money
</text>

  
<rect x="550" y="50" width="160" height="50" rx="25" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="630" y="80" text-anchor="middle" fill="white" font-size="13">
ACCEPTING_PAYMENT
</text>

  
<line x1="630" y1="100" x2="630" y2="170" stroke="#4aff4a" marker-end="url(#ah1)">
</line>

  
<text x="680" y="140" fill="#4aff4a" font-size="10">
sufficient funds
</text>

  
<rect x="550" y="180" width="160" height="50" rx="25" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="630" y="210" text-anchor="middle" fill="white" font-size="13">
DISPENSING
</text>

  
<line x1="630" y1="230" x2="630" y2="300" stroke="#4aff4a" marker-end="url(#ah1)">
</line>

  
<text x="680" y="270" fill="#4aff4a" font-size="10">
product delivered
</text>

  
<rect x="550" y="310" width="160" height="50" rx="25" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="630" y="340" text-anchor="middle" fill="white" font-size="13">
RETURNING_CHANGE
</text>

  
<line x1="550" y1="335" x2="190" y2="100" stroke="#4aff4a" stroke-dasharray="5,5" marker-end="url(#ah1)">
</line>

  
<text x="300" y="230" fill="#4aff4a" font-size="10">
change returned → IDLE
</text>

  

  
<line x1="370" y1="100" x2="190" y2="100" stroke="#ff4a4a" stroke-dasharray="3,3" marker-end="url(#ah1)">
</line>

  
<text x="280" y="115" text-anchor="middle" fill="#ff4a4a" font-size="10">
cancel / timeout
</text>

  
<line x1="550" y1="80" x2="455" y2="120" stroke="#ff4a4a" stroke-dasharray="3,3">
</line>

  
<text x="480" y="108" fill="#ff4a4a" font-size="10">
cancel
</text>

  

  
<rect x="50" y="250" width="140" height="50" rx="25" fill="#8a1a1a" stroke="#ff4a4a" stroke-width="2">
</rect>

  
<text x="120" y="280" text-anchor="middle" fill="white" font-size="13">
ERROR / JAM
</text>

  
<line x1="550" y1="210" x2="190" y2="270" stroke="#ff4a4a" stroke-dasharray="3,3" marker-end="url(#ah1)">
</line>

  
<text x="350" y="250" fill="#ff4a4a" font-size="10">
dispense failure
</text>


</svg>

</details>


### Step-by-Step


  1. **IDLE:** Machine displays available products with prices. Touch screen or physical buttons for selection

  2. **PRODUCT_SELECTED:** User selects product (e.g., B4 → Coke $1.75). Display shows "Insert $1.75". 30-second timeout to cancel

  3. **ACCEPTING_PAYMENT:** Machine accepts coins/bills incrementally. Display shows remaining amount. Card/mobile payment via NFC terminal. Running total: accepted $1.00 / needed $1.75

  4. **DISPENSING:** When amount ≥ price, motor activates to push product from slot. Infrared sensor at dispensing chute confirms product fell. If sensor doesn't trigger within 5s → ERROR state (product jammed)

  5. **RETURNING_CHANGE:** Calculate change = amount_inserted - price. Coin dispenser returns exact change using greedy algorithm (largest denominations first). If cannot make exact change → dispense maximum possible, credit remainder to loyalty account

  6. **IDLE:** Transaction complete. Log transaction locally → sync to cloud when online


> **
**Example:** User presses B4 (Coke, $1.75) → Inserts $1.00 bill → Display: "$0.75 remaining" → Inserts $1.00 bill → Total $2.00 ≥ $1.75 → Motor dispenses Coke → IR sensor confirms delivery → Change: $0.25 → Dispenses 1 quarter → Transaction logged: {product: "B4", price: 1.75, paid: 2.00, change: 0.25, method: "cash"}


### 🔍 Component Deep Dives — Purchase Flow


State Machine Pattern


The vending machine is a textbook **Finite State Machine (FSM)**:


class VendingMachine:
    state = IDLE
    selected_product = None
    amount_inserted = 0.00

    def on_event(self, event):
        if self.state == IDLE:
            if event.type == PRODUCT_SELECTED:
                if self.inventory[event.product_id] > 0:
                    self.state = PRODUCT_SELECTED
                    self.selected_product = event.product_id
                else:
                    display("OUT OF STOCK")
        elif self.state == PRODUCT_SELECTED:
            if event.type == MONEY_INSERTED:
                self.amount_inserted += event.amount
                self.state = ACCEPTING_PAYMENT
                self._check_sufficient_funds()
            elif event.type == CANCEL or event.type == TIMEOUT:
                self._refund_and_reset()
        elif self.state == ACCEPTING_PAYMENT:
            if event.type == MONEY_INSERTED:
                self.amount_inserted += event.amount
                self._check_sufficient_funds()
            elif event.type == SUFFICIENT_FUNDS:
                self.state = DISPENSING
                self._dispense_product()
        elif self.state == DISPENSING:
            if event.type == PRODUCT_DELIVERED:
                self.state = RETURNING_CHANGE
                self._return_change()
            elif event.type == DISPENSE_FAILURE:
                self.state = ERROR
                self._refund_and_alert()


Change-Making Algorithm (Greedy)

def make_change(amount_cents, coin_inventory):
    """Greedy algorithm - works for US coin denominations"""
    denominations = [100, 25, 10, 5, 1]  # dollar, quarter, dime, nickel, penny
    change = []
    remaining = amount_cents
    for denom in denominations:
        while remaining >= denom and coin_inventory[denom] > 0:
            change.append(denom)
            remaining -= denom
            coin_inventory[denom] -= 1
    if remaining > 0:
        raise CannotMakeChangeError(remaining)
    return change


**Note:** Greedy works for US denominations (1, 5, 10, 25) because they form a canonical coin system. For arbitrary denominations, dynamic programming is needed (minimum coins problem).


Hardware Interface


**Coin acceptor:** Uses optical sensors and electromagnetic coils to identify coin type by size, weight, and metal composition. Communicates via MDB (Multi-Drop Bus) protocol — industry standard for vending peripherals


**Bill validator:** UV, magnetic, and optical sensors to detect counterfeit bills. Accepts/rejects within 1-2 seconds


**Card reader:** NFC (contactless) + EMV chip reader. Communicates with payment processor via cellular (4G) connection


**Motor controller:** Each product slot has a spiral motor. Microcontroller sends PWM signal to rotate motor exactly one turn to push product forward


## 🔄 Flow 2 — Remote Fleet Management & Monitoring


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 380" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="80" width="120" height="50" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="80" y="110" text-anchor="middle" fill="white" font-size="12">
Machine 1
</text>

  
<rect x="20" y="150" width="120" height="50" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="80" y="180" text-anchor="middle" fill="white" font-size="12">
Machine 2
</text>

  
<rect x="20" y="220" width="120" height="50" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="80" y="250" text-anchor="middle" fill="white" font-size="12">
Machine N
</text>

  
<text x="80" y="210" text-anchor="middle" fill="#aaa" font-size="20">
⋮
</text>

  
<line x1="140" y1="105" x2="230" y2="165" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="140" y1="175" x2="230" y2="170" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="140" y1="245" x2="230" y2="175" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="240" y="140" width="140" height="60" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="310" y="165" text-anchor="middle" fill="white" font-size="12">
MQTT Broker
</text>

  
<text x="310" y="182" text-anchor="middle" fill="white" font-size="10">
(IoT Gateway)
</text>

  
<line x1="380" y1="170" x2="450" y2="170" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="460" y="140" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="530" y="165" text-anchor="middle" fill="white" font-size="12">
Fleet Mgmt Service
</text>

  
<text x="530" y="182" text-anchor="middle" fill="white" font-size="10">
(Cloud Backend)
</text>

  
<line x1="600" y1="155" x2="680" y2="105" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="600" y1="170" x2="680" y2="170" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="600" y1="185" x2="680" y2="240" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="690" y="80" width="150" height="50" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="765" y="110" text-anchor="middle" fill="white" font-size="12">
PostgreSQL (Fleet DB)
</text>

  
<rect x="690" y="145" width="150" height="50" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="765" y="175" text-anchor="middle" fill="white" font-size="12">
TimescaleDB (Telemetry)
</text>

  
<rect x="690" y="220" width="150" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="765" y="250" text-anchor="middle" fill="white" font-size="12">
Alert Engine
</text>

  
<line x1="840" y1="245" x2="910" y2="245" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="920" y="220" width="130" height="50" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="985" y="250" text-anchor="middle" fill="white" font-size="12">
Operator Dashboard
</text>


</svg>

</details>


### Step-by-Step


  1. **Machine telemetry:** Each machine sends periodic heartbeats via MQTT: inventory levels, temperature, coin/bill counts, error codes, revenue since last report

  2. **MQTT Broker:** AWS IoT Core or HiveMQ handles 100K+ concurrent machine connections. Topics: `fleet/{machine_id}/telemetry`, `fleet/{machine_id}/events`

  3. **Fleet Management Service:** Processes telemetry events, updates machine state in PostgreSQL, stores time-series metrics in TimescaleDB

  4. **Alert Engine:** Rules trigger alerts: "Machine M-1234 slot B4 is empty", "Machine M-5678 coin hopper low", "Machine M-9012 temperature above 45°F (cooling unit failure)"

  5. **Operator Dashboard:** Web UI showing fleet map, machine health, inventory heatmap, revenue charts. Route optimization for restocking trucks


> **
**Example:** Machine M-1234 in office lobby sends telemetry every 5 min → MQTT topic: `fleet/M-1234/telemetry` → Payload: {inventory: [{slot: "B4", product: "Coke", qty: 2}, ...], temp: 38°F, coins: {25: 45, 10: 30, 5: 20}, revenue_today: $127.50} → Alert: "B4 stock low (2 remaining)" → Operator dashboard adds M-1234 to restock route → Truck driver arrives, restocks, scans confirmation → Inventory updated


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 550" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ahc" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  

  
<text x="180" y="30" fill="#f06292" font-size="14" font-weight="bold">
Vending Machine (Edge)
</text>

  
<rect x="30" y="45" width="100" height="40" rx="6" fill="#8a4a1a" stroke="#ffa54a" stroke-width="1.5">
</rect>

  
<text x="80" y="70" text-anchor="middle" fill="white" font-size="10">
Coin Acceptor
</text>

  
<rect x="140" y="45" width="100" height="40" rx="6" fill="#8a4a1a" stroke="#ffa54a" stroke-width="1.5">
</rect>

  
<text x="190" y="70" text-anchor="middle" fill="white" font-size="10">
Bill Validator
</text>

  
<rect x="250" y="45" width="100" height="40" rx="6" fill="#8a4a1a" stroke="#ffa54a" stroke-width="1.5">
</rect>

  
<text x="300" y="70" text-anchor="middle" fill="white" font-size="10">
NFC Reader
</text>

  
<rect x="30" y="95" width="100" height="40" rx="6" fill="#8a4a1a" stroke="#ffa54a" stroke-width="1.5">
</rect>

  
<text x="80" y="120" text-anchor="middle" fill="white" font-size="10">
Motor Array
</text>

  
<rect x="140" y="95" width="100" height="40" rx="6" fill="#8a4a1a" stroke="#ffa54a" stroke-width="1.5">
</rect>

  
<text x="190" y="120" text-anchor="middle" fill="white" font-size="10">
Display/Touch
</text>

  
<rect x="250" y="95" width="100" height="40" rx="6" fill="#8a4a1a" stroke="#ffa54a" stroke-width="1.5">
</rect>

  
<text x="300" y="120" text-anchor="middle" fill="white" font-size="10">
Temp Sensor
</text>

  

  
<rect x="100" y="155" width="200" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="200" y="178" text-anchor="middle" fill="white" font-size="12">
Main Controller (SBC)
</text>

  
<text x="200" y="195" text-anchor="middle" fill="white" font-size="10">
(State Machine + Local DB)
</text>

  
<line x1="200" y1="85" x2="200" y2="150" stroke="#aaa" marker-end="url(#ahc)">
</line>

  

  
<line x1="300" y1="183" x2="420" y2="183" stroke="#aaa" stroke-dasharray="5,5" marker-end="url(#ahc)">
</line>

  
<text x="360" y="175" fill="#aaa" font-size="10">
4G/WiFi
</text>

  

  
<text x="620" y="30" fill="#f06292" font-size="14" font-weight="bold">
Cloud Platform
</text>

  
<rect x="430" y="45" width="140" height="50" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="500" y="75" text-anchor="middle" fill="white" font-size="11">
API Gateway
</text>

  
<rect x="430" y="120" width="140" height="50" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="500" y="150" text-anchor="middle" fill="white" font-size="11">
MQTT Broker (IoT)
</text>

  
<rect x="430" y="195" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="500" y="225" text-anchor="middle" fill="white" font-size="11">
Fleet Mgmt Service
</text>

  
<rect x="430" y="270" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="500" y="300" text-anchor="middle" fill="white" font-size="11">
Payment Service
</text>

  
<rect x="430" y="345" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="500" y="375" text-anchor="middle" fill="white" font-size="11">
Route Optimizer
</text>

  

  
<rect x="650" y="120" width="140" height="50" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="720" y="150" text-anchor="middle" fill="white" font-size="11">
PostgreSQL
</text>

  
<rect x="650" y="195" width="140" height="50" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="720" y="225" text-anchor="middle" fill="white" font-size="11">
TimescaleDB
</text>

  
<rect x="650" y="270" width="140" height="50" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="720" y="300" text-anchor="middle" fill="white" font-size="11">
Redis (Sessions)
</text>

  
<line x1="570" y1="145" x2="640" y2="145" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="570" y1="220" x2="640" y2="220" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="570" y1="295" x2="640" y2="295" stroke="#aaa" marker-end="url(#ahc)">
</line>

  

  
<rect x="650" y="345" width="140" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="720" y="375" text-anchor="middle" fill="white" font-size="11">
Stripe/Square
</text>

  
<line x1="570" y1="295" x2="640" y2="370" stroke="#aaa" marker-end="url(#ahc)">
</line>

  

  
<rect x="850" y="120" width="140" height="50" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="920" y="150" text-anchor="middle" fill="white" font-size="11">
Operator Dashboard
</text>

  
<rect x="850" y="195" width="140" height="50" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="920" y="225" text-anchor="middle" fill="white" font-size="11">
Mobile App (User)
</text>

  
<line x1="790" y1="145" x2="840" y2="145" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="570" y1="70" x2="840" y2="220" stroke="#aaa" stroke-dasharray="5,5" marker-end="url(#ahc)">
</line>


</svg>

</details>


> **
**End-to-End Example:** 50,000 machines across US → Each connects via 4G to AWS IoT MQTT broker → Telemetry streamed to TimescaleDB every 5 min → Fleet dashboard shows: 847 machines need restocking, 23 have errors, $2.3M revenue today → Route optimizer generates optimal restock routes for 200 trucks → User in office approaches machine → Taps phone (Apple Pay) for $2.50 coffee → NFC reader → 4G → Stripe payment → approved → Motor dispenses → Transaction synced to cloud → Machine inventory decremented


## 🗄️ Database Schema


### PostgreSQL — Fleet Management


machines


| Column           | Type          | Notes                                       |
|------------------|---------------|---------------------------------------------|
| id               | UUID          | `PK`                                        |
| serial_number    | VARCHAR(50)   | `IDX`                                       |
| location_name    | VARCHAR(255)  |                                             |
| latitude         | DECIMAL(10,7) |                                             |
| longitude        | DECIMAL(10,7) |                                             |
| model            | VARCHAR(50)   |                                             |
| firmware_version | VARCHAR(20)   |                                             |
| status           | VARCHAR(20)   | operational / error / offline / maintenance |
| last_heartbeat   | TIMESTAMPTZ   | `IDX`                                       |
| operator_id      | UUID          | `FK`                                        |
| installed_at     | TIMESTAMPTZ   |                                             |


machine_slots


| Column       | Type         | Notes               |
|--------------|--------------|---------------------|
| machine_id   | UUID         | `PK` `FK`           |
| slot_code    | VARCHAR(5)   | `PK` e.g., "B4"     |
| product_id   | UUID         | `FK`                |
| quantity     | SMALLINT     |                     |
| max_capacity | SMALLINT     |                     |
| price        | DECIMAL(6,2) | Per-machine pricing |


transactions


| Column         | Type         | Notes                         |
|----------------|--------------|-------------------------------|
| id             | UUID         | `PK`                          |
| machine_id     | UUID         | `FK` `IDX`                    |
| slot_code      | VARCHAR(5)   |                               |
| product_id     | UUID         |                               |
| amount         | DECIMAL(6,2) |                               |
| payment_method | VARCHAR(20)  | cash / card / mobile          |
| payment_ref    | VARCHAR(100) | External payment ID           |
| status         | VARCHAR(20)  | completed / refunded / failed |
| dispensed_at   | TIMESTAMPTZ  | `IDX`                         |


products


| Column        | Type         | Notes                    |
|---------------|--------------|--------------------------|
| id            | UUID         | `PK`                     |
| name          | VARCHAR(100) |                          |
| category      | VARCHAR(50)  | beverage / snack / candy |
| sku           | VARCHAR(50)  | `IDX`                    |
| default_price | DECIMAL(6,2) |                          |
| image_url     | TEXT         |                          |


### Sharding Strategy


**machines + machine_slots:** Shard by `operator_id` — each operator manages their fleet on same shard  `SHARD`


**transactions:** Shard by `machine_id` + time-based partitioning — recent transactions on hot partition, older data archived  `SHARD`


### Local Database (On-Machine — SQLite)


-- Simplified local schema on machine controller
CREATE TABLE local_inventory (
    slot_code TEXT PRIMARY KEY,
    product_name TEXT,
    quantity INTEGER,
    price REAL
);

CREATE TABLE local_transactions (
    id TEXT PRIMARY KEY,
    slot_code TEXT,
    amount REAL,
    payment_method TEXT,
    timestamp TEXT,
    synced INTEGER DEFAULT 0  -- 0 = pending sync to cloud
);


Local DB is source of truth for immediate operations. Unsynced transactions queued and uploaded when connectivity restored. Cloud DB is source of truth for reporting and fleet management.


## 💾 Cache & CDN Deep Dive


**Redis (Cloud):** Caches machine state for dashboard queries. Key: `machine:{id}:state` → {inventory, temp, revenue, errors}. Updated on each telemetry event. TTL: none (always fresh from MQTT stream). Eviction: noeviction — all data is essential.


**Local cache (on-machine):** SQLite acts as persistent cache. Product catalog, pricing, and firmware config stored locally. Updated via OTA (over-the-air) push from cloud. Machine operates fully offline using local data.


**CDN:** Product images for display screen served via CDN. Firmware update binaries stored on S3 + CDN for fast global download to machines.


## ⚖️ Tradeoffs


> **
  
> **
    ✅ State Machine Design
    

Deterministic behavior — every state transition is explicit. Easy to test, audit, and debug. Prevents invalid states (e.g., dispensing without payment)

  
  
> **
    ❌ State Machine Design
    

Adding new flows (e.g., combo deals, loyalty points) requires modifying state machine — can become complex. State explosion with many edge cases

  
  
> **
    ✅ MQTT for IoT Communication
    

Lightweight protocol (minimal bandwidth — critical for cellular data). Built-in QoS levels. Bi-directional (cloud can push firmware updates, price changes to machines)

  
  
> **
    ❌ MQTT for IoT Communication
    

Requires MQTT broker infrastructure. Less tooling/debugging support than HTTP. Security requires careful TLS configuration per device

  


## 🔄 Alternative Approaches


HTTP Polling Instead of MQTT


Machines periodically call REST API to report telemetry and check for commands. Simpler implementation but higher bandwidth, higher latency for commands, and more server load from 100K machines polling simultaneously. MQTT's push model is better for IoT scale.


Cashless-Only Machine


Eliminate coin/bill hardware entirely — NFC card/mobile only. Dramatically reduces mechanical complexity (no coin jam, no change-making). Lower hardware cost (~40% reduction). Tradeoff: excludes unbanked users, some locations require cash acceptance by law.


## 📚 Additional Information


### OOP Design Patterns Used


  - **State Pattern:** Each state (Idle, ProductSelected, etc.) is a class implementing a common interface. The machine delegates behavior to current state object

  - **Strategy Pattern:** Payment methods (CashPayment, CardPayment, MobilePayment) implement a PaymentStrategy interface

  - **Observer Pattern:** Display, inventory tracker, and telemetry reporter observe state changes

  - **Command Pattern:** Remote commands (updatePrices, reboot, lockMachine) encapsulated as command objects sent via MQTT


### Predictive Restocking (ML)


Using historical sales data (from TimescaleDB), predict when each slot will run out. Features: day of week, time of day, location type (office vs gym vs airport), weather, nearby events. Model predicts daily sales per product per machine → generates optimal restocking schedule → route optimizer plans truck routes minimizing distance while ensuring no machine runs out during business hours.


### MDB Protocol


Multi-Drop Bus is the industry standard serial communication protocol for vending peripherals. 9600 baud, 9-bit bytes (8 data + mode bit). Master (main controller) polls peripherals (coin acceptor, bill validator, card reader) on shared bus. Each peripheral has unique address. Defined by NAMA (National Automatic Merchandising Association).