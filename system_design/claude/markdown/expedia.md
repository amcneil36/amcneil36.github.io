# System Design: Expedia (Online Travel Agency)


## Functional Requirements


- Search for hotels, flights, car rentals, and vacation packages by destination, dates, guests

- Real-time availability and pricing from multiple suppliers (airlines, hotel chains, GDS systems)

- Filter/sort results by price, rating, amenities, location, airline

- Booking management (create, modify, cancel reservations)

- User reviews and ratings for hotels

- Price alerts and fare tracking

- Bundle deals (flight + hotel discount)


## Non-Functional Requirements


- **Low Latency:** Search results in <3 seconds (aggregating from multiple suppliers)

- **High Availability:** 99.99% — travel bookings are time-sensitive

- **Consistency:** Strong consistency for bookings (prevent double-booking). Eventual consistency OK for search/pricing

- **Scalability:** Handle peak traffic during holidays (2-3x normal). Millions of searches/day

- **Data freshness:** Prices may be stale by minutes — verified at booking time


## Flow 1: Hotel Search & Availability


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1150 450" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ffc72c">
</polygon>
</marker>
</defs>


<rect x="20" y="180" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="80" y="215" text-anchor="middle" fill="white" font-size="13">
User
</text>


<rect x="190" y="180" width="120" height="60" rx="10" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="250" y="215" text-anchor="middle" fill="white" font-size="12">
CDN / LB
</text>


<rect x="360" y="180" width="140" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="430" y="215" text-anchor="middle" fill="white" font-size="12">
Search Service
</text>


<rect x="560" y="80" width="150" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="635" y="108" text-anchor="middle" fill="white" font-size="11">
Supplier Aggregator
</text>


<rect x="560" y="180" width="150" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="635" y="208" text-anchor="middle" fill="white" font-size="11">
Price Cache (Redis)
</text>


<rect x="560" y="280" width="150" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="635" y="310" text-anchor="middle" fill="white" font-size="11">
Ranking / Filter
</text>


<rect x="790" y="50" width="130" height="40" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="855" y="75" text-anchor="middle" fill="white" font-size="10">
GDS (Amadeus)
</text>


<rect x="790" y="110" width="130" height="40" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="855" y="135" text-anchor="middle" fill="white" font-size="10">
Hotel APIs (Marriott)
</text>


<rect x="790" y="170" width="130" height="40" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="855" y="195" text-anchor="middle" fill="white" font-size="10">
OTA Partners
</text>


<rect x="560" y="380" width="150" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="635" y="410" text-anchor="middle" fill="white" font-size="12">
Elasticsearch
</text>


<line x1="140" y1="210" x2="188" y2="210" stroke="#ffc72c" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="310" y1="210" x2="358" y2="210" stroke="#ffc72c" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="500" y1="198" x2="558" y2="108" stroke="#ffc72c" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="500" y1="210" x2="558" y2="205" stroke="#ffc72c" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="500" y1="225" x2="558" y2="300" stroke="#ffc72c" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="710" y1="105" x2="788" y2="75" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="710" y1="105" x2="788" y2="130" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="710" y1="105" x2="788" y2="190" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="500" y1="230" x2="558" y2="400" stroke="#ffc72c" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow1)">
</line>


<text x="575" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Expedia: Hotel Search Flow
</text>


</svg>

</details>


### Step-by-Step


1. **User searches:** `GET /v1/hotels/search?dest=new+york&checkin=2024-03-15&checkout=2024-03-18&guests=2&rooms=1`

2. **Search Service:** Checks Price Cache for pre-fetched rates. Cache hit for popular destinations (~60%)

3. **Supplier Aggregator (on cache miss):** Fans out requests to multiple suppliers in parallel (GDS, direct hotel APIs, OTA partners). Uses circuit breakers — if supplier doesn't respond in 2.5s, use cached/stale data

4. **Response aggregation:** Normalizes different supplier formats into unified hotel objects. Deduplicates same hotel from multiple suppliers (choose best price)

5. **Elasticsearch:** Used for property-level search (filter by amenities, star rating, neighborhood). Prices come from supplier APIs, property metadata from Elasticsearch

6. **Ranking/Filter:** Ranks by: sponsored (revenue), price, user rating, conversion history. Applies user filters (price range, stars, amenities)


> **
**Example:** User searches "Hotels in New York, March 15-18, 2 guests". Search Service checks Redis cache for (NYC, 2024-03-15, 2024-03-18) — cache miss. Supplier Aggregator fans out to 5 suppliers: Amadeus returns 200 hotels in 1.2s, Marriott direct API returns 45 in 0.8s, Booking.com returns 180 in 2.0s, Hilton returns 30 in 0.6s, Sabre times out at 2.5s (circuit breaker trips, use cached Sabre data). Total: 400 unique hotels (after dedup). Ranking sorts: Times Square Marriott ($289/night, 4.5★) ranked #3 (sponsored). Results returned in 2.8s.


### Deep Dive: Supplier Aggregator


- **Protocol:** HTTPS REST or SOAP (legacy GDS systems use XML/SOAP). Amadeus uses XML-based NDC protocol for flights

- **Fan-out pattern:** Parallel async calls to all suppliers. Use timeout + circuit breaker per supplier. Return best-effort results even if some suppliers fail

- **Rate limiting:** Each supplier has API rate limits. Maintain per-supplier token bucket. Queue excess requests

- **Normalization:** Different suppliers return different formats. Mapper converts each to canonical format: `{ hotel_id, name, address, price, room_types[], amenities[], images[] }`


## Flow 2: Hotel Booking


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ffaa00">
</polygon>
</marker>
</defs>


<rect x="20" y="140" width="110" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="75" y="175" text-anchor="middle" fill="white" font-size="13">
User
</text>


<rect x="180" y="140" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="245" y="175" text-anchor="middle" fill="white" font-size="12">
Booking Service
</text>


<rect x="370" y="80" width="130" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="435" y="108" text-anchor="middle" fill="white" font-size="11">
Price Verification
</text>


<rect x="370" y="180" width="130" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="435" y="208" text-anchor="middle" fill="white" font-size="11">
Supplier Booking
</text>


<rect x="370" y="280" width="130" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="435" y="310" text-anchor="middle" fill="white" font-size="12">
Payment Svc
</text>


<rect x="560" y="140" width="130" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="625" y="170" text-anchor="middle" fill="white" font-size="12">
Confirmation
</text>


<rect x="750" y="140" width="130" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="815" y="170" text-anchor="middle" fill="white" font-size="12">
Notification Svc
</text>


<line x1="130" y1="170" x2="178" y2="170" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="310" y1="157" x2="368" y2="110" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="310" y1="175" x2="368" y2="205" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="310" y1="190" x2="368" y2="300" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="500" y1="200" x2="558" y2="170" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="690" y1="165" x2="748" y2="165" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<text x="490" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Expedia: Booking Flow
</text>


</svg>

</details>


### Step-by-Step


1. **User selects hotel and clicks "Book Now"** → `POST /v1/bookings { hotel_id, room_type, checkin, checkout, guest_info, payment }`

2. **Price Verification:** Re-query supplier for current price (search prices may be stale). If price changed, notify user before proceeding

3. **Supplier Booking:** Send reservation request to the specific supplier (e.g., Marriott API). Receives confirmation number from supplier

4. **Payment Service:** Pre-authorize card (hold amount). Only capture after supplier confirms. If supplier rejects → release hold

5. **Confirmation:** Create booking record with status "confirmed", store supplier confirmation number

6. **Notification:** Email confirmation with booking details, PDF itinerary, calendar invite


> **
**Example:** User books Times Square Marriott, March 15-18, King room. Price Verification queries Marriott API — price increased from $289 to $299/night. User shown updated price, confirms. Supplier Booking sends to Marriott → confirmation #MR-78923. Payment Service captures $897 + $89.70 tax = $986.70 on Visa. Booking #EXP-456789 created. Email sent with confirmation details + "Manage booking" link.


## Combined Overall Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1150 550" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ffc72c">
</polygon>
</marker>
</defs>


<text x="575" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Expedia: Combined Architecture
</text>


<rect x="20" y="240" width="100" height="50" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="70" y="270" text-anchor="middle" fill="white" font-size="12">
Clients
</text>


<rect x="160" y="240" width="100" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="210" y="270" text-anchor="middle" fill="white" font-size="11">
API Gateway
</text>


<rect x="310" y="80" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="370" y="105" text-anchor="middle" fill="white" font-size="11">
Search Svc
</text>


<rect x="310" y="150" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="370" y="175" text-anchor="middle" fill="white" font-size="11">
Booking Svc
</text>


<rect x="310" y="220" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="370" y="245" text-anchor="middle" fill="white" font-size="11">
User Svc
</text>


<rect x="310" y="290" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="370" y="315" text-anchor="middle" fill="white" font-size="11">
Review Svc
</text>


<rect x="310" y="360" width="120" height="40" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="370" y="385" text-anchor="middle" fill="white" font-size="11">
Price Alert Svc
</text>


<rect x="310" y="430" width="120" height="40" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="370" y="455" text-anchor="middle" fill="white" font-size="11">
Recommendation
</text>


<rect x="500" y="60" width="130" height="40" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="565" y="85" text-anchor="middle" fill="white" font-size="10">
Supplier Aggregator
</text>


<rect x="500" y="120" width="130" height="40" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="565" y="145" text-anchor="middle" fill="white" font-size="10">
Price Cache
</text>


<rect x="500" y="180" width="130" height="40" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="565" y="205" text-anchor="middle" fill="white" font-size="10">
Elasticsearch
</text>


<rect x="500" y="240" width="130" height="40" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="565" y="265" text-anchor="middle" fill="white" font-size="10">
Kafka
</text>


<rect x="500" y="300" width="130" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="565" y="325" text-anchor="middle" fill="white" font-size="10">
Payment Svc
</text>


<rect x="700" y="50" width="110" height="35" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="755" y="72" text-anchor="middle" fill="white" font-size="9">
GDS (Amadeus)
</text>


<rect x="700" y="100" width="110" height="35" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="755" y="122" text-anchor="middle" fill="white" font-size="9">
Hotel APIs
</text>


<rect x="700" y="150" width="110" height="35" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="755" y="172" text-anchor="middle" fill="white" font-size="9">
Airline APIs
</text>


<ellipse cx="755" cy="255" rx="55" ry="22" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="755" y="260" text-anchor="middle" fill="white" font-size="10">
Booking DB
</text>


<ellipse cx="755" cy="325" rx="55" ry="22" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="755" y="330" text-anchor="middle" fill="white" font-size="10">
User DB
</text>


<line x1="120" y1="265" x2="158" y2="265" stroke="#ffc72c" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="260" y1="258" x2="308" y2="100" stroke="#ffc72c" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="260" y1="262" x2="308" y2="170" stroke="#ffc72c" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="260" y1="266" x2="308" y2="240" stroke="#ffc72c" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="430" y1="100" x2="498" y2="80" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="430" y1="100" x2="498" y2="140" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="430" y1="100" x2="498" y2="200" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="80" x2="698" y2="67" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="80" x2="698" y2="117" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="80" x2="698" y2="167" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="260" x2="698" y2="255" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


</svg>

</details>


## Database Schema


### SQL — PostgreSQL (Bookings, Users — ACID required)


| Table                         | Column      | Type                                    | Details |
|-------------------------------|-------------|-----------------------------------------|---------|
| **bookings**                  | booking_id  | VARCHAR(20)                             | `PK`    |
| user_id                       | BIGINT      | `FK`  `IDX`                             |         |
| booking_type                  | ENUM        | hotel, flight, car, package             |         |
| supplier_id                   | VARCHAR(50) |                                         |         |
| supplier_confirmation         | VARCHAR(50) | Supplier's reference number             |         |
| status                        | ENUM        | pending, confirmed, canceled, completed |         |
| total_amount                  | BIGINT      | In cents                                |         |
| checkin_date / departure_date | DATE        | `IDX`                                   |         |
| created_at                    | TIMESTAMP   |                                         |         |


### NoSQL — Elasticsearch (Hotel/Flight Catalog)


| Index   | Fields                                                                                           | Purpose                                    |
|---------|--------------------------------------------------------------------------------------------------|--------------------------------------------|
| hotels  | hotel_id, name, city, lat/lng, star_rating, amenities[], avg_review_score, images[], description | Full-text search + geo + faceted filtering |
| flights | flight_id, airline, origin, destination, departure_time, arrival_time, aircraft_type             | Route search + time filtering              |


### Redis (Price Cache, Session)


| Key                                         | Type   | TTL     | Details                              |
|---------------------------------------------|--------|---------|--------------------------------------|
| `price:{dest}:{checkin}:{checkout}:{rooms}` | Hash   | 15 min  | Cached search results from suppliers |
| `session:{token}`                           | Hash   | 24 hr   | User session, recent searches        |
| `alert:{user_id}:{route_or_hotel}`          | String | 30 days | Price alert thresholds               |


Sharding Strategy


- **bookings:** Shard by `user_id` — "my trips" query is shard-local

- **Price cache:** Redis Cluster, shard by cache key hash (destination-based)

- **Elasticsearch:** Sharded by destination region. Hotels in Europe on different shards from North America. Query routes to relevant shard


## Cache Deep Dive


| Cache                  | Strategy                             | Eviction | TTL                      |
|------------------------|--------------------------------------|----------|--------------------------|
| Search Results (Redis) | Write-through (on supplier response) | TTL      | 15 min (prices volatile) |
| Hotel Metadata (Redis) | Read-through                         | LRU      | 24 hr (static info)      |
| User Sessions          | Write-through                        | TTL      | 24 hr                    |


### CDN


- **Hotel images:** Served via CDN. Multiple resolutions (thumbnail, gallery, fullscreen). TTL: 30 days

- **Static pages:** Destination landing pages, travel guides cached at CDN edge. TTL: 1 hour

- **Search results:** NOT cached at CDN — personalized and price-sensitive


## Scaling Considerations


- **Supplier fan-out:** Each search hits 3-8 suppliers in parallel. Circuit breaker per supplier. Timeout: 2.5s. If supplier slow, return partial results + note "some results unavailable"

- **Holiday spikes:** Pre-warm caches for popular destinations before holiday season. Auto-scale search services based on request rate

- **Price cache warming:** Background job pre-fetches prices for top 1000 destination-date combos every 10 minutes. Reduces cold-start latency

- **Read replicas:** Booking DB uses read replicas for "my trips" queries. Write to primary only for new bookings


## Tradeoffs & Deep Dives


> **
✅ Aggregator Pattern
- Best price across multiple suppliers — value proposition for users
- Supplier independence — can add/remove suppliers without changing core
- Graceful degradation — partial results if some suppliers fail


❌ Aggregator Challenges
- Latency = slowest supplier (mitigated by timeouts)
- Price staleness — search prices may differ from booking prices
- Each supplier has different API, data formats, error handling


### Polling for Price Alerts


- **Mechanism:** Background workers poll suppliers every 30 minutes for tracked routes/hotels. Compare with user's price threshold

- **If price drops below threshold:** Push notification + email to user

- **Scale:** Millions of active price alerts. Batch queries to suppliers to reduce API calls. Group alerts by route/hotel


## Alternative Approaches


- **Direct supplier integration only:** Skip GDS, connect directly to each airline/hotel. Better margins but more integration work. Modern trend: airlines moving to NDC (New Distribution Capability) for direct booking

- **Pre-fetch all inventory:** Instead of real-time supplier queries, cache all availability. Pros: instant results. Cons: massive data volume, staleness issues, not feasible for flights (too many combinations)

- **GraphQL BFF:** Backend-for-Frontend pattern with GraphQL. Mobile app fetches exactly what it needs (less data than REST). Web fetches more details. Both from same backend


## Additional Information


- **Package bundling:** Flight + hotel bundle discounted ~10-20%. System tries all supplier combinations, finds cheapest bundle. Combinatorial optimization problem — use heuristics, not brute force

- **Cancellation policy:** Stored per booking from supplier. Free cancellation deadline tracked — reminder email 48 hours before deadline

- **GDS (Global Distribution Systems):** Legacy systems (Amadeus, Sabre, Travelport) that aggregate airline/hotel inventory. SOAP/XML APIs. High latency (1-3 seconds). Still handle majority of airline bookings

- **Loyalty integration:** Users can earn/redeem points from various loyalty programs. Points-to-cash conversion at booking time