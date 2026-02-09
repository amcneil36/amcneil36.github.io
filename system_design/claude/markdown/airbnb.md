# 🏠 Airbnb - System Design


## Functional Requirements


1. **Search & Discovery:** Search listings by location, dates, guests, filters (price, amenities, property type); map-based browsing; personalized ranking; instant results

2. **Booking & Payments:** Real-time availability calendar, instant book or request-to-book, secure payment processing (multi-currency), host payout, cancellation policies, pricing rules (smart pricing)

3. **Listing Management:** Hosts create/manage listings with photos, descriptions, amenities, house rules, availability calendar, pricing (nightly/weekly/monthly), reviews/ratings


## Non-Functional Requirements


- **Scale:** 7M+ active listings in 220+ countries, 150M+ users, ~1M bookings/night peak season

- **Latency:** Search results <500ms, booking confirmation <2s, calendar load <300ms

- **Availability:** 99.99% — downtime during peak booking periods directly impacts revenue

- **Consistency:** Double-booking prevention — calendar availability must be strongly consistent

- **Trust:** Identity verification, review integrity, payment security (PCI DSS Level 1)


## Flow 1: Search & Discovery


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#00A699">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Client
</text>
<text x="80" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Web / Mobile)
</text>


<rect x="190" y="50" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="255" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API Gateway
</text>
<text x="255" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Envoy + Auth)
</text>


<rect x="370" y="50" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="435" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Search Service
</text>
<text x="435" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Elasticsearch)
</text>


<rect x="550" y="20" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="615" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Ranking ML
</text>
<text x="615" y="57" text-anchor="middle" fill="#fff" font-size="10">
(Personalization)
</text>


<rect x="550" y="90" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="615" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Availability Svc
</text>
<text x="615" y="127" text-anchor="middle" fill="#fff" font-size="10">
(Calendar Check)
</text>


<rect x="740" y="50" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="805" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Listing DB
</text>
<text x="805" y="87" text-anchor="middle" fill="#fff" font-size="10">
(MySQL + Redis)
</text>


<rect x="370" y="200" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="435" y="222" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Geo Index
</text>
<text x="435" y="237" text-anchor="middle" fill="#fff" font-size="10">
(S2 Geometry)
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#00A699" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="320" y1="75" x2="365" y2="75" stroke="#00A699" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="65" x2="545" y2="45" stroke="#00A699" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="85" x2="545" y2="110" stroke="#00A699" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="680" y1="45" x2="735" y2="65" stroke="#00A699" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="680" y1="115" x2="735" y2="85" stroke="#00A699" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="435" y1="100" x2="435" y2="195" stroke="#00A699" stroke-width="2" marker-end="url(#ah)">
</line>


</svg>

</details>


### Steps


1. User searches: location "Paris", check-in Feb 15, check-out Feb 20, 2 guests

2. Geo Index (S2 Geometry cells) identifies listings within bounding box of "Paris" map viewport

3. Search Service (Elasticsearch) filters: property type, price range, amenities, instant book, superhost

4. Availability Service checks calendar for date range — filters out listings already booked for those dates

5. Ranking ML model scores remaining listings: relevance × quality × price competitiveness × booking probability × personalization

6. Personalization: user's past bookings, saved listings, search history, device/locale → fine-tune ranking

7. Results returned paginated (20 per page) with listing cards: photos, price, rating, superhost badge


### Example


Search "Paris, Feb 15-20, 2 guests, $100-200/night". Geo filter: 45,000 listings in Paris metro area. Price filter: 18,000. Availability filter: 12,000 available those dates. Amenity filters (WiFi, kitchen): 8,500. ML ranking: top 20 returned. User previously booked apartments → apartments ranked higher. Listing #1: "Charming Marais Studio" — $145/night, 4.92 rating, Superhost, 200+ reviews.


### Deep Dives


Search Ranking (ML)


**Model:** Gradient boosted decision trees (XGBoost/LightGBM) → neural ranking model (DNN)


**Features:** Listing quality score, price vs. market median, review sentiment, response rate, photo quality score (CV model), distance from search center, guest-host match (language, preferences)


**Training:** Trained on billions of search-click-book sessions; label = booked (positive) vs. viewed-not-booked (negative)


**Airbnb-specific:** "Smart Pricing" factor — hosts using dynamic pricing rank higher (better conversion rate)


Geospatial Index (S2 Geometry)


**Library:** Google S2 Geometry — maps Earth's surface to hierarchical cells (levels 0-30)


**Index:** Each listing stored with S2 cell ID at level 13 (~1km²); search uses S2 region covering to find relevant cells


**Map View:** As user pans/zooms map, new S2 cell query issued → listings in visible viewport returned


**Elasticsearch:** geo_shape field with S2 cell prefix for efficient geo-range queries


## Flow 2: Booking & Payment


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 320" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FC642D">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Guest
</text>
<text x="80" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Book Now)
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Booking Service
</text>
<text x="255" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Orchestrator)
</text>


<rect x="370" y="30" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="435" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Availability Svc
</text>
<text x="435" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Lock + Verify)
</text>


<rect x="370" y="100" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Payment Service
</text>
<text x="435" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Stripe/Braintree)
</text>


<rect x="560" y="30" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="625" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Reservation DB
</text>
<text x="625" y="67" text-anchor="middle" fill="#fff" font-size="10">
(MySQL + Locks)
</text>


<rect x="560" y="100" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="625" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Notification Svc
</text>
<text x="625" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Email/Push/SMS)
</text>


<rect x="740" y="60" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="805" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Pricing Engine
</text>
<text x="805" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Dynamic Pricing)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#FC642D" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#FC642D" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="95" x2="365" y2="120" stroke="#FC642D" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="500" y1="55" x2="555" y2="55" stroke="#FC642D" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="500" y1="125" x2="555" y2="125" stroke="#FC642D" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="690" y1="55" x2="735" y2="75" stroke="#FC642D" stroke-width="2" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Steps


1. Guest clicks "Reserve" → Booking Service receives listing_id, dates, guest count, payment method

2. Availability Service acquires pessimistic lock on calendar dates (SELECT ... FOR UPDATE on reservation rows)

3. Double-booking check: verify no overlapping confirmed reservation exists for those dates

4. Pricing Engine calculates total: nightly_rate × nights + cleaning_fee + service_fee + occupancy_taxes (multi-currency)

5. Payment Service authorizes payment (Stripe/Braintree) — hold funds, don't capture yet

6. If Instant Book: reservation confirmed immediately; if Request: host has 24h to accept/decline

7. On confirmation: payment captured, calendar blocked, confirmation emails/push sent to guest and host

8. Reservation written to MySQL with ACID transaction; calendar availability updated atomically


### Example


Guest books "Charming Marais Studio" for Feb 15-20 (5 nights). Pricing: $145/night × 5 = $725 + $50 cleaning + $102 service fee + $58 occupancy tax = $935 total. Availability lock acquired. No conflicts found. Stripe authorization succeeds. Instant Book enabled → reservation confirmed. Guest charged $935, host payout scheduled for $725 (after 3% host fee) 24h after check-in.


### Deep Dives


Double-Booking Prevention


**Strategy:** Pessimistic locking — `SELECT ... FOR UPDATE` on calendar rows for the date range


**Lock Scope:** Per-listing date range; lock held for duration of booking transaction (~2-5 seconds)


**Alternative:** Optimistic locking with version column — retry on conflict; lower contention for low-demand listings


**Edge Case:** iCal sync from external platforms (VRBO, Booking.com) — synced every 15 min; race window handled by lock


Smart Pricing (Dynamic)


**Model:** ML-based dynamic pricing — factors: local demand, seasonality, day-of-week, events nearby, competitor pricing, listing quality


**Range:** Host sets min/max price; Smart Pricing adjusts within range to maximize bookings


**Market Data:** Airbnb aggregates booking data across all listings in an area to estimate demand curves


## Flow 3: Host Listing Management & Reviews


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 260" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FF5A5F">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Host
</text>
<text x="80" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Listing Editor)
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Listing Service
</text>


<rect x="370" y="30" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Photo Service
</text>
<text x="435" y="67" text-anchor="middle" fill="#fff" font-size="10">
(ML Quality + CDN)
</text>


<rect x="370" y="100" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="435" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Review Service
</text>
<text x="435" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Dual-Blind)
</text>


<rect x="560" y="60" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="625" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Search Index
</text>
<text x="625" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Sync on Change)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#FF5A5F" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#FF5A5F" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="95" x2="365" y2="120" stroke="#FF5A5F" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="55" x2="555" y2="75" stroke="#FF5A5F" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="125" x2="555" y2="95" stroke="#FF5A5F" stroke-width="2" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Steps


1. Host creates listing: title, description, property type, rooms/beds/baths, amenities checklist, house rules, location

2. Photos uploaded to Photo Service → resized to multiple dimensions, ML quality scoring (composition, lighting, resolution), stored in S3

3. Calendar setup: mark available dates, set nightly/weekly/monthly pricing, minimum stay requirements

4. Listing indexed in Elasticsearch for search discovery; updates propagated via change data capture (CDC) from MySQL

5. Reviews: dual-blind system — both guest and host submit reviews within 14 days; revealed simultaneously to prevent retaliation

6. Rating dimensions: cleanliness, accuracy, communication, location, check-in, value (1-5 stars each)


### Deep Dives


Photo Quality ML


**Model:** CNN-based photo quality scorer — trained on professional vs. amateur listing photos


**Features:** Lighting quality, composition, resolution, room coverage, object detection (bed, kitchen, bathroom presence)


**Impact:** Higher-quality photos → better search ranking → more bookings; AI photo tips for hosts


**Processing:** Photos resized to 5 dimensions (thumbnail, card, gallery, hero, original); WebP + JPEG; progressive loading


Review System


**Dual-Blind:** Both parties write reviews independently; revealed simultaneously after 14-day window (or both submit)


**Fraud Detection:** ML detects fake reviews — unusual patterns, timing, reviewer history, text similarity


**Rating Bias:** Airbnb reviews average ~4.7/5 (J-curve distribution); considering redesigning to reduce positivity bias


## Database Schema


### MySQL — Listings & Reservations

`CREATE TABLE listings (
  listing_id      BIGINT PRIMARY KEY,     `PK`
  host_id         BIGINT,                 `FK`  `IDX`
  title           VARCHAR(255),
  description     TEXT,
  property_type   VARCHAR(30),
  room_type       VARCHAR(20),  -- entire_place, private_room, shared_room
  lat             DECIMAL(9,6),           `IDX`
  lng             DECIMAL(9,6),
  s2_cell_id      BIGINT,                 `IDX`
  city            VARCHAR(100),           `IDX`
  country         CHAR(2),
  bedrooms        TINYINT,
  beds            TINYINT,
  bathrooms       DECIMAL(3,1),
  max_guests      TINYINT,
  amenities       JSON,  -- ["wifi","kitchen","parking",...]
  price_nightly   DECIMAL(10,2),
  price_weekly    DECIMAL(10,2),
  cleaning_fee    DECIMAL(10,2),
  currency        CHAR(3),
  min_nights      SMALLINT DEFAULT 1,
  instant_book    BOOLEAN DEFAULT FALSE,
  superhost       BOOLEAN DEFAULT FALSE,
  avg_rating      DECIMAL(3,2),
  review_count    INT DEFAULT 0,
  status          VARCHAR(20),  -- active, unlisted, suspended
  created_at      TIMESTAMP,
  updated_at      TIMESTAMP
);

CREATE TABLE reservations (
  reservation_id  BIGINT PRIMARY KEY,     `PK`
  listing_id      BIGINT,                 `FK`  `IDX`
  guest_id        BIGINT,                 `FK`
  host_id         BIGINT,                 `FK`
  check_in        DATE,                   `IDX`
  check_out       DATE,
  guests          TINYINT,
  nightly_rate    DECIMAL(10,2),
  total_price     DECIMAL(10,2),
  currency        CHAR(3),
  service_fee     DECIMAL(10,2),
  cleaning_fee    DECIMAL(10,2),
  taxes           DECIMAL(10,2),
  status          VARCHAR(20),  -- pending, confirmed, cancelled, completed
  payment_intent  VARCHAR(64),  -- Stripe payment intent ID
  cancellation_policy VARCHAR(30),
  created_at      TIMESTAMP
);`


 `SHARD` Sharded by listing_id for reservation queries; user-centric queries via secondary index on guest_id/host_id


### Calendar & Reviews

`CREATE TABLE calendar (
  listing_id      BIGINT,                 `FK`
  date            DATE,
  available       BOOLEAN DEFAULT TRUE,
  price           DECIMAL(10,2),  -- per-night price for this date
  min_nights      SMALLINT,
  reservation_id  BIGINT,                 `FK`
  PRIMARY KEY (listing_id, date)
);
-- Calendar row per listing per date
-- Availability check: WHERE listing_id=X AND date BETWEEN Y AND Z AND available=TRUE

CREATE TABLE reviews (
  review_id       BIGINT PRIMARY KEY,     `PK`
  reservation_id  BIGINT UNIQUE,          `FK`
  listing_id      BIGINT,                 `FK`  `IDX`
  reviewer_id     BIGINT,                 `FK`
  reviewee_id     BIGINT,                 `FK`
  role            VARCHAR(10),  -- guest_to_host, host_to_guest
  overall_rating  TINYINT,  -- 1-5
  cleanliness     TINYINT,
  accuracy        TINYINT,
  communication   TINYINT,
  location_rating TINYINT,
  checkin_rating  TINYINT,
  value_rating    TINYINT,
  comment         TEXT,
  is_public       BOOLEAN DEFAULT FALSE, -- dual-blind until both submit
  created_at      TIMESTAMP
);`


## Cache & CDN Deep Dive


CDN (Cloudflare / Fastly)


**Photos:** Listing photos served via CDN with aggressive caching (max-age: 30d, immutable hash-based URLs)


**Responsive:** `?w=400&h=300&fit=crop` — CDN image resizing at edge; WebP auto-conversion


**Static:** Web app bundles, map tiles, locale strings cached at 250+ edge PoPs


Redis Cache


**Search Results:** Recent search results cached by query hash (TTL: 5 min) — same search repeated returns instantly


**Listing Details:** Hot listing data cached in Redis (TTL: 10 min, invalidated on update)


**Availability:** Calendar availability bitmap cached per listing (365 days × 1 bit = 46 bytes); updated on booking


**Pricing:** Smart pricing results cached per listing per date range (TTL: 1hr)


Session & User Cache


**Session:** User sessions in Redis (JWT validation cache, wishlists, recent searches)


**Personalization:** User features vector cached for ML ranking — past bookings, preferences, price sensitivity


## Scaling & Load Balancing


- **Service Mesh:** Envoy-based service mesh (Airbnb's SmartStack → Envoy migration); automatic service discovery, load balancing, circuit breaking

- **Database:** MySQL (primary + read replicas) per service domain; Vitess for sharding; separate clusters for search (ES), analytics (Druid), ML features (Hive/Spark)

- **Search:** Elasticsearch cluster with 100+ nodes; sharded by geo-region for data locality; reindexed from MySQL via CDC pipeline

- **Seasonal Scaling:** Summer/holiday peak: 3-5x normal traffic; auto-scaling groups with pre-warming before known peaks (New Year's Eve, etc.)

- **Multi-Region:** US-West (primary), US-East, EU-West, APAC — reads from nearest region; writes routed to primary for strong consistency on bookings


## Tradeoffs


> **

✅ Calendar-Row-Per-Date Model


- Simple availability queries — just filter by date range + available=true

- Per-date pricing flexibility (weekend premium, holiday rates)

- Easy to implement minimum night requirements per date


❌ Calendar-Row-Per-Date Model


- 365 rows per listing × 7M listings = 2.5B+ rows (significant storage)

- Year-rollover: must pre-generate next year's calendar rows

- Bulk updates (e.g., "block all Mondays") require updating many rows


✅ Instant Book


- Faster conversion — guest books immediately, no waiting for host approval

- Better guest experience — reduces booking friction by 50%

- Higher search ranking for instant book listings


❌ Instant Book


- Hosts lose control over who books (mitigated by guest requirements)

- Higher cancellation rates from hosts who regret auto-approvals

- Guest verification less thorough before booking


## Alternative Approaches


Event-Sourced Availability


Instead of mutable calendar rows, store immutable events (DateBlocked, DateUnblocked, ReservationCreated). Current availability computed from event log. Perfect audit trail, supports undo, but read path requires event replay (mitigated by materialized views).


Graph-Based Discovery (Neo4j)


Model listings, users, reviews, bookings as graph. Recommendation queries become graph traversals: "listings similar to ones Alice liked, booked by people with similar preferences." Richer recommendations but operational complexity of graph databases at scale.


## Additional Information


- **Trust & Safety:** Identity verification (government ID + selfie match via ML), payment fraud detection (Stripe Radar), listing quality monitoring, neighborhood complaint handling, 24/7 safety hotline.

- **Airbnb Categories:** ML-powered listing categorization into 60+ categories (Treehouses, Castles, Beach, Ski-in/out, Tiny homes). Uses listing photos (CV model) + description (NLP) + amenities for classification.

- **Experience Platform:** Hosted experiences (tours, cooking classes, workshops) alongside accommodations — separate booking pipeline with per-person pricing, date/time slots, and host qualification verification.

- **Internationalization:** 220+ countries, 60+ languages, 70+ currencies. Dynamic currency conversion, localized content, region-specific legal compliance (tax collection, registration numbers, guest limits).

- **Service Architecture:** Airbnb migrated from Rails monolith to ~1,000 microservices (2017-2020). Key infrastructure: Chronos (job scheduler), Reair (data replication), Superset (data visualization), Aerosolve (ML pricing).