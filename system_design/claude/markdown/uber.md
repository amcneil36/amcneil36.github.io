# System Design: Uber (Ride-Sharing Platform)


## Functional Requirements


- Riders request rides by specifying pickup and destination locations

- Match riders with nearby available drivers in real-time

- Real-time driver location tracking (GPS updates every 3-4 seconds)

- Dynamic pricing (surge pricing based on supply/demand)

- ETA calculation for pickup and trip duration

- Payment processing (ride fare calculation, driver payout)

- Trip history and ratings for riders and drivers


## Non-Functional Requirements


- **Low Latency:** Matching rider to driver in <10 seconds

- **Real-time:** Location updates processed within 1-2 seconds

- **High Availability:** 99.99% — outage means people stranded

- **Scalability:** Millions of concurrent rides, millions of driver location updates/sec

- **Consistency:** Strong consistency for ride matching (a driver can't be matched to two riders)

- **Location accuracy:** GPS precision within 5-10 meters


## Flow 1: Ride Request & Driver Matching


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1150 500" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#00c9ff">
</polygon>
</marker>
</defs>


<rect x="20" y="200" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="80" y="235" text-anchor="middle" fill="white" font-size="13">
Rider App
</text>


<rect x="190" y="200" width="130" height="60" rx="10" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="255" y="228" text-anchor="middle" fill="white" font-size="12">
API Gateway /
</text>
<text x="255" y="245" text-anchor="middle" fill="white" font-size="12">
Load Balancer
</text>


<rect x="380" y="200" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="445" y="235" text-anchor="middle" fill="white" font-size="13">
Trip Service
</text>


<rect x="580" y="100" width="150" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="655" y="130" text-anchor="middle" fill="white" font-size="12">
Matching Engine
</text>


<rect x="580" y="200" width="150" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="655" y="225" text-anchor="middle" fill="white" font-size="11">
Location Service
</text>
<text x="655" y="240" text-anchor="middle" fill="white" font-size="10">
(Geospatial Index)
</text>


<rect x="580" y="310" width="150" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="655" y="340" text-anchor="middle" fill="white" font-size="12">
Pricing Service
</text>


<rect x="580" y="410" width="150" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="655" y="440" text-anchor="middle" fill="white" font-size="12">
ETA Service
</text>


<rect x="810" y="100" width="130" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="875" y="128" text-anchor="middle" fill="white" font-size="11">
Driver Supply
</text>
<text x="875" y="142" text-anchor="middle" fill="white" font-size="10">
Index (Redis)
</text>


<rect x="810" y="200" width="130" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="875" y="230" text-anchor="middle" fill="white" font-size="12">
Map Service
</text>


<rect x="810" y="310" width="130" height="50" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="875" y="340" text-anchor="middle" fill="white" font-size="13">
Driver App
</text>


<ellipse cx="1030" cy="125" rx="60" ry="25" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="1030" y="130" text-anchor="middle" fill="white" font-size="11">
Trip DB
</text>


<line x1="140" y1="230" x2="188" y2="230" stroke="#00c9ff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="320" y1="230" x2="378" y2="230" stroke="#00c9ff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="215" x2="578" y2="130" stroke="#00c9ff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="230" x2="578" y2="225" stroke="#00c9ff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="245" x2="578" y2="330" stroke="#00c9ff" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="250" x2="578" y2="430" stroke="#00c9ff" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="730" y1="125" x2="808" y2="125" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="730" y1="225" x2="808" y2="225" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="730" y1="130" x2="808" y2="330" stroke="#ff6b6b" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow1)">
</line>


<line x1="940" y1="125" x2="968" y2="125" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<text x="575" y="30" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Uber: Ride Request & Matching Flow
</text>


</svg>

</details>


### Step-by-Step


1. **Rider requests ride:** `POST /v1/trips { pickup: {lat, lng}, destination: {lat, lng}, ride_type: "UberX" }`

2. **Pricing Service:** Calculates fare estimate using distance, time, surge multiplier (based on supply/demand ratio in geohash region)

3. **ETA Service:** Calculates pickup ETA and trip ETA using road network graph + real-time traffic data

4. **Rider confirms price** → Trip Service creates trip with status "matching"

5. **Matching Engine:** Queries Location Service for nearby available drivers within expanding radius (start 2km, expand to 5km)

6. **Location Service (Geospatial Index):** Uses geohash + Redis to find drivers. `GEORADIUS drivers:active {lat} {lng} 2km`

7. **Matching Engine ranks candidates:** Score = f(distance, ETA to pickup, driver rating, acceptance rate, vehicle type match)

8. **Best driver sent ride request** via push notification. Driver has 15 seconds to accept

9. **If accepted:** Trip status → "driver_assigned". Driver location shared with rider in real-time

10. **If declined/timeout:** Next best driver gets the request (cascade)


> **
**Example:** Rider at (37.7749, -122.4194) requests UberX to SFO airport. Pricing: base $2.50 + $1.75/mile × 13 miles + $0.35/min × 25 min = ~$34 (no surge). ETA Service: 5 min pickup ETA. Matching Engine finds 8 available drivers within 2km via Redis GEORADIUS. Ranks by ETA to pickup: Driver A (3 min, 4.92★), Driver B (4 min, 4.85★). Driver A gets request → accepts in 6 seconds → trip status "driver_assigned" → rider sees Driver A's live location.


### Deep Dive: Geospatial Index (Location Service)


- **Geohash:** Encode lat/lng into string prefix. "9q8yy" = San Francisco area. Adjacent geohashes share prefix. Enables range queries

- **Redis GEOADD/GEORADIUS:** Store driver locations: `GEOADD drivers:active {lng} {lat} driver_123`. Query nearby: `GEORADIUS drivers:active -122.4194 37.7749 2 km WITHCOORD COUNT 20`

- **Update frequency:** Drivers send GPS location every 4 seconds. Location Service updates Redis geospatial index

- **Cell-based partitioning:** World divided into S2 cells (Google S2 geometry). Each cell managed by specific server. Rider's request routed to cell containing pickup location

- **Why not PostGIS:** Too slow for millions of updates/sec. Redis geospatial commands are O(N+logN) and in-memory — sub-millisecond


### Deep Dive: Matching Engine


- **Algorithm:** Batched matching every 2 seconds. Collect all pending ride requests and available drivers → solve assignment problem (minimize total ETA) → Hungarian algorithm or greedy assignment

- **Why batched:** Individual matching (first-come-first-served) can be suboptimal. Batching finds globally optimal driver-rider pairs

- **Constraints:** Driver must be available, vehicle type matches, driver not in restricted zone, driver rating above threshold


## Flow 2: Real-time Driver Location Tracking


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ffaa00">
</polygon>
</marker>
</defs>


<rect x="20" y="140" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="80" y="175" text-anchor="middle" fill="white" font-size="13">
Driver App
</text>


<rect x="190" y="140" width="130" height="60" rx="10" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="255" y="168" text-anchor="middle" fill="white" font-size="11">
Kafka / gRPC
</text>
<text x="255" y="185" text-anchor="middle" fill="white" font-size="10">
(Location Stream)
</text>


<rect x="380" y="80" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="450" y="110" text-anchor="middle" fill="white" font-size="12">
Location Svc
</text>


<rect x="380" y="200" width="140" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="450" y="228" text-anchor="middle" fill="white" font-size="11">
Trip Tracking Svc
</text>


<rect x="600" y="80" width="120" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="660" y="108" text-anchor="middle" fill="white" font-size="11">
Geospatial Index
</text>
<text x="660" y="122" text-anchor="middle" fill="white" font-size="10">
(Redis)
</text>


<rect x="600" y="200" width="120" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="660" y="228" text-anchor="middle" fill="white" font-size="11">
WebSocket Hub
</text>


<rect x="800" y="200" width="120" height="50" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="860" y="230" text-anchor="middle" fill="white" font-size="12">
Rider App
</text>


<line x1="140" y1="170" x2="188" y2="170" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="320" y1="160" x2="378" y2="110" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="320" y1="180" x2="378" y2="225" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="520" y1="105" x2="598" y2="105" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="520" y1="225" x2="598" y2="225" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="720" y1="225" x2="798" y2="225" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<text x="500" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Uber: Real-time Location Tracking
</text>


</svg>

</details>


### Step-by-Step


1. **Driver App** sends GPS coordinates every 4 seconds: `{ driver_id, lat, lng, heading, speed, timestamp }`

2. **Location stream** via gRPC (bidirectional streaming) to Location Service

3. **Location Service:** Updates Redis geospatial index for matching. Also writes to Kafka for analytics/logging

4. **Trip Tracking Service:** For active trips, forwards driver location to matched rider via WebSocket Hub

5. **WebSocket Hub:** Maintains persistent WebSocket connections with rider apps. Pushes real-time location updates

6. **Rider sees driver moving** on map in real-time (location update every 4 seconds)


> **
**Example:** Driver sends location (37.7850, -122.4090) → Location Service updates Redis: `GEOADD drivers:active -122.4090 37.7850 driver_456` (O(logN), ~0.1ms). Trip Tracking Service sees driver_456 is on active trip_789 → forwards location to WebSocket Hub → Hub pushes to rider's WebSocket connection → rider sees car icon move on map. 4 seconds later, next update arrives: (37.7855, -122.4085).


### Deep Dive: WebSocket for Live Tracking


- **Why WebSocket:** Server-push every 4 seconds. HTTP polling would waste bandwidth and add latency. WebSocket = persistent connection, server sends when ready

- **Scale:** Millions of concurrent WebSocket connections. Each server handles ~50K connections. Horizontally scaled behind L4 load balancer with sticky sessions

- **Fallback:** If WebSocket fails (corporate firewall), fall back to HTTP long-polling with 4-second intervals

- **Connection management:** Heartbeat ping/pong every 30 seconds. If no pong, consider rider disconnected. Reconnect with last-known state


## Combined Overall Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 600" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#00c9ff">
</polygon>
</marker>
</defs>


<text x="600" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Uber: Combined Architecture
</text>


<rect x="20" y="200" width="100" height="50" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="70" y="230" text-anchor="middle" fill="white" font-size="11">
Rider App
</text>


<rect x="20" y="320" width="100" height="50" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="70" y="350" text-anchor="middle" fill="white" font-size="11">
Driver App
</text>


<rect x="170" y="250" width="110" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="225" y="280" text-anchor="middle" fill="white" font-size="11">
API Gateway
</text>


<rect x="340" y="80" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="400" y="105" text-anchor="middle" fill="white" font-size="11">
Trip Svc
</text>


<rect x="340" y="150" width="120" height="40" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="400" y="175" text-anchor="middle" fill="white" font-size="11">
Matching Engine
</text>


<rect x="340" y="220" width="120" height="40" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="400" y="245" text-anchor="middle" fill="white" font-size="11">
Location Svc
</text>


<rect x="340" y="290" width="120" height="40" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="400" y="315" text-anchor="middle" fill="white" font-size="11">
Pricing Svc
</text>


<rect x="340" y="360" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="400" y="385" text-anchor="middle" fill="white" font-size="11">
ETA Svc
</text>


<rect x="340" y="430" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="400" y="455" text-anchor="middle" fill="white" font-size="11">
Payment Svc
</text>


<rect x="340" y="500" width="120" height="40" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="400" y="525" text-anchor="middle" fill="white" font-size="11">
Notification Svc
</text>


<rect x="540" y="80" width="110" height="40" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="595" y="105" text-anchor="middle" fill="white" font-size="10">
Geo Index (Redis)
</text>


<rect x="540" y="150" width="110" height="40" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="595" y="175" text-anchor="middle" fill="white" font-size="10">
Kafka
</text>


<rect x="540" y="220" width="110" height="40" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="595" y="245" text-anchor="middle" fill="white" font-size="10">
WebSocket Hub
</text>


<rect x="540" y="290" width="110" height="40" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="595" y="315" text-anchor="middle" fill="white" font-size="10">
Map/Routing
</text>


<ellipse cx="740" cy="100" rx="55" ry="22" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="740" y="105" text-anchor="middle" fill="white" font-size="10">
Trip DB
</text>


<ellipse cx="740" cy="175" rx="55" ry="22" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="740" y="180" text-anchor="middle" fill="white" font-size="10">
User DB
</text>


<ellipse cx="740" cy="250" rx="55" ry="22" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="740" y="255" text-anchor="middle" fill="white" font-size="10">
Payment DB
</text>


<rect x="700" y="310" width="100" height="35" rx="8" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="750" y="332" text-anchor="middle" fill="white" font-size="10">
Location Hist
</text>


<line x1="120" y1="225" x2="168" y2="265" stroke="#00c9ff" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="120" y1="345" x2="168" y2="290" stroke="#00c9ff" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="280" y1="270" x2="338" y2="100" stroke="#00c9ff" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="280" y1="275" x2="338" y2="170" stroke="#00c9ff" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="280" y1="280" x2="338" y2="240" stroke="#00c9ff" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="280" y1="285" x2="338" y2="310" stroke="#00c9ff" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="280" y1="290" x2="338" y2="380" stroke="#00c9ff" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="460" y1="100" x2="538" y2="100" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="460" y1="240" x2="538" y2="240" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="460" y1="170" x2="538" y2="170" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="460" y1="385" x2="538" y2="315" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="650" y1="100" x2="683" y2="100" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="650" y1="175" x2="683" y2="175" stroke="#ff6b6b" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="650" y1="245" x2="683" y2="245" stroke="#ff6b6b" stroke-width="1" marker-end="url(#arrow3)">
</line>


</svg>

</details>


> **
**Example — Full ride lifecycle:** Rider opens app → sees ETA "3 min" (ETA Service pre-computes based on nearby supply) → requests ride → Pricing Service: $22.50 (1.2x surge) → rider confirms → Matching Engine finds 5 nearby drivers → sends request to Driver A (closest, 4.95★) → Driver A accepts (8 sec) → Rider sees driver on map (WebSocket location updates every 4 sec) → Driver arrives at pickup → "Start trip" → GPS tracking records route → arrives at destination → fare calculated: $22.50 → Payment Service charges rider's card → driver receives $18.00 (80% cut) → both rate each other → trip saved to Trip DB.


## Database Schema


### SQL — PostgreSQL (Trips, Users, Payments — ACID required)


| Table                  | Column       | Type                                                        | Details |
|------------------------|--------------|-------------------------------------------------------------|---------|
| **trips**              | trip_id      | UUID                                                        | `PK`    |
| rider_id               | BIGINT       | `FK`  `IDX`                                                 |         |
| driver_id              | BIGINT       | `FK`  `IDX`                                                 |         |
| status                 | ENUM         | matching, driver_assigned, in_progress, completed, canceled |         |
| pickup_lat, pickup_lng | DOUBLE       |                                                             |         |
| dest_lat, dest_lng     | DOUBLE       |                                                             |         |
| fare_amount            | BIGINT       | In cents                                                    |         |
| surge_multiplier       | DECIMAL(3,2) | 1.00 = no surge                                             |         |
| distance_miles         | DECIMAL(6,2) |                                                             |         |
| created_at             | TIMESTAMP    | `IDX`                                                       |         |
| **users**              | user_id      | BIGINT                                                      | `PK`    |
| type                   | ENUM         | rider, driver, both                                         |         |
| rating                 | DECIMAL(3,2) | Denormalized avg rating                                     |         |
| phone                  | VARCHAR(20)  | `IDX`                                                       |         |
| payment_method_id      | VARCHAR(30)  | Default payment                                             |         |


### Geospatial — Redis


| Key                | Type       | Details                                            |
|--------------------|------------|----------------------------------------------------|
| `drivers:active`   | GEOSPATIAL | All available drivers' locations. GEOADD/GEORADIUS |
| `drivers:busy`     | GEOSPATIAL | Drivers on active trips (for supply visibility)    |
| `surge:{geohash6}` | Hash       | { demand_count, supply_count, multiplier }         |


### NoSQL — Cassandra (Location History)


| Table                | Column    | Type     | Details |
|----------------------|-----------|----------|---------|
| **location_history** | trip_id   | UUID     | `PK`    |
| timestamp            | TIMESTAMP | `PK` ASC |         |
| lat                  | DOUBLE    |          |         |
| lng                  | DOUBLE    |          |         |
| speed                | FLOAT     | km/h     |         |


Sharding


- **trips:** Shard by `rider_id` — rider's trip history is shard-local. Driver's trip history uses scatter-gather (less frequent query)

- **Redis geospatial:** Partitioned by S2 cell (geographic region). SF drivers on one Redis, NYC on another. Matching engine queries local cell

- **location_history:** Partitioned by `trip_id`. All GPS points for one trip co-located. Excellent for trip route replay


### Indexes


- `trips.rider_id` + `trips.driver_id` — B-tree indexes for "my trip history" queries

- `trips.created_at` — For admin queries, analytics, and cleanup

- Redis GEOSPATIAL index — O(N+logN) for GEORADIUS queries, effectively O(logN) for nearby driver lookup


## Cache Deep Dive


| Cache                       | Strategy                 | Eviction                                  | TTL          |
|-----------------------------|--------------------------|-------------------------------------------|--------------|
| Driver Location (Redis GEO) | Write-through (every 4s) | Stale entries removed after 60s no-update | 60s implicit |
| Surge Pricing (Redis)       | Computed every 30s       | TTL                                       | 30 seconds   |
| ETA Cache (Redis)           | Read-through             | TTL                                       | 15 seconds   |
| User Profile (Redis)        | Read-through             | LRU                                       | 1 hour       |


### CDN


- **Map tiles:** Served via CDN (pre-rendered at various zoom levels). Raster or vector tiles

- **Static assets:** App icons, UI assets cached at CDN

- **No CDN for real-time data:** Driver locations, surge prices, ETAs are dynamic — served directly from services


## Scaling Considerations


- **Location updates:** 1M active drivers × 1 update/4s = 250K writes/sec to Redis. Redis handles this easily (single instance does 100K+ ops/sec). Shard by city for parallelism

- **Matching at scale:** Batched matching runs per-city. SF matching engine independent from NYC. Each city can scale independently

- **Cell-based architecture:** World divided into hexagonal cells (H3 from Uber). Services partitioned by cell. Queries only hit relevant cells

- **Surge computation:** Supply/demand ratio computed per geohash6 cell every 30 seconds. Kafka stream of ride requests and driver availability → Flink aggregation

- **Multi-region:** Each country/region has its own deployment. Data locality reduces latency. Regulations (GDPR) require data residency


## Tradeoffs & Deep Dives


> **
✅ Batched Matching
- Globally optimal matching (minimize total pickup ETAs)
- Reduces driver competition for same rider
- More efficient fleet utilization


❌ Batched Matching Downsides
- 2-second delay before matching starts
- Complex optimization algorithm (Hungarian/assignment problem)
- Must handle riders who cancel during batch window


### WebSocket vs Polling for Location


- **WebSocket chosen:** Persistent connection, server-push model. 4-second updates feel instantaneous on map. Low overhead after connection established

- **Alternative — HTTP polling:** Client polls every 4 seconds. Pros: simpler, works through all proxies. Cons: overhead of HTTP headers per request, slight latency increase

- **Alternative — Server-Sent Events (SSE):** Server-push over HTTP. Simpler than WebSocket (one-way only). Good enough for location tracking since rider doesn't send data back on this channel


## Alternative Approaches


- **Quadtree for spatial indexing:** Instead of Redis geohash. Quadtree recursively divides 2D space into quadrants. Better for non-uniform distribution (dense cities + sparse rural). But harder to distribute across servers

- **gRPC streaming for location:** Instead of REST + WebSocket. gRPC bidirectional streaming for both driver uploads and rider tracking. Type-safe, efficient binary protocol (Protocol Buffers). Uber actually uses this

- **Machine learning for ETA:** Instead of graph-based routing, use ML model trained on historical trip data. Captures traffic patterns, weather effects, event impacts. Uber's ETA model uses gradient boosted trees with 100+ features


## Additional Information


- **H3 (Hexagonal Hierarchical Spatial Index):** Uber's open-source geospatial system. Divides world into hexagonal cells at multiple resolutions. Better than square grids for distance calculations (hexagons have more uniform neighbor distances)

- **Surge pricing formula:** `multiplier = max(1.0, demand_requests / available_drivers * sensitivity_factor)`. Capped at 8x. Shown to rider before confirming. Incentivizes more drivers to come online in high-demand areas

- **Safety features:** Share trip with contacts (real-time location sharing), emergency button (one-tap 911 with location), driver background checks, ride verification PIN

- **Driver state machine:** offline → online(available) → matching → en_route_to_pickup → waiting_at_pickup → in_trip → trip_complete → online(available). Each transition triggers different system actions