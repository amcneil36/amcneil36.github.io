# System Design: TripAdvisor (Travel Review Platform)


## Functional Requirements


- Search for hotels, restaurants, attractions by destination with rich filtering

- User-generated reviews and ratings (text, photos, 1-5 stars)

- Aggregated rating scores with breakdown (cleanliness, location, service, value)

- Price comparison across booking sites (meta-search: redirect to Expedia, Booking.com, etc.)

- Traveler forums and Q&A

- Personalized recommendations based on travel history and preferences

- "Near me" discovery using geolocation


## Non-Functional Requirements


- **Read-heavy:** 100:1 read-to-write ratio (reviews are read far more than written)

- **Low Latency:** Search results in <500ms, place pages in <200ms

- **SEO:** Billions of indexed pages — SEO is critical for organic traffic

- **Scalability:** Hundreds of millions of reviews, millions of places

- **Availability:** 99.99% uptime


## Flow 1: Search & Discovery


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 380" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#34e0a1">
</polygon>
</marker>
</defs>


<rect x="20" y="150" width="110" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="75" y="185" text-anchor="middle" fill="white" font-size="13">
User
</text>


<rect x="170" y="150" width="110" height="60" rx="10" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="225" y="185" text-anchor="middle" fill="white" font-size="12">
CDN
</text>


<rect x="330" y="150" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="395" y="185" text-anchor="middle" fill="white" font-size="12">
Search Service
</text>


<rect x="520" y="80" width="140" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="590" y="110" text-anchor="middle" fill="white" font-size="12">
Elasticsearch
</text>


<rect x="520" y="180" width="140" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="590" y="210" text-anchor="middle" fill="white" font-size="12">
Place Cache
</text>


<rect x="520" y="280" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="590" y="308" text-anchor="middle" fill="white" font-size="11">
Price Comparison
</text>


<rect x="740" y="80" width="130" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="805" y="110" text-anchor="middle" fill="white" font-size="11">
Ranking / ML
</text>


<rect x="740" y="280" width="130" height="40" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="805" y="305" text-anchor="middle" fill="white" font-size="10">
Booking Partners
</text>


<line x1="130" y1="180" x2="168" y2="180" stroke="#34e0a1" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="280" y1="180" x2="328" y2="180" stroke="#34e0a1" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="460" y1="168" x2="518" y2="110" stroke="#34e0a1" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="460" y1="185" x2="518" y2="205" stroke="#34e0a1" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="460" y1="198" x2="518" y2="300" stroke="#34e0a1" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="660" y1="105" x2="738" y2="105" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="660" y1="305" x2="738" y2="300" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<text x="520" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
TripAdvisor: Search & Discovery
</text>


</svg>

</details>


### Step-by-Step


1. **User searches** "restaurants in Rome" → `GET /v1/search?type=restaurant&dest=rome&sort=rating`

2. **CDN:** Static place pages for popular destinations may be cached. Search API results are dynamic (personalized)

3. **Elasticsearch:** Full-text search + geo-filtering + faceted search (cuisine type, price range, rating). Returns place IDs with scores

4. **Place Cache (Redis):** Hydrates place IDs with full details (name, address, photos, avg rating, review count)

5. **Price Comparison:** For hotels, queries booking partners (Expedia, Booking.com) for current prices. Returns cheapest options

6. **Ranking ML:** Personalizes order based on user history, popularity score (Bayesian average of ratings), freshness of reviews, photo quality


> **
**Example:** User searches "restaurants in Rome, Italian cuisine, $$". Elasticsearch returns 500 restaurants matching filters. Place Cache hydrates top 50 with details. ML Ranking considers: user visited high-end restaurants before → boosts upscale Italian places. "Roscioli" (4.7★, 3200 reviews) ranked #1. "Da Enzo" (4.6★, 2100 reviews) ranked #2. Response: 200ms.


## Flow 2: Submitting a Review


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 320" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ffaa00">
</polygon>
</marker>
</defs>


<rect x="20" y="130" width="110" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="75" y="165" text-anchor="middle" fill="white" font-size="13">
User
</text>


<rect x="180" y="130" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="245" y="165" text-anchor="middle" fill="white" font-size="12">
Review Service
</text>


<rect x="370" y="70" width="130" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="435" y="100" text-anchor="middle" fill="white" font-size="11">
Fraud Detector
</text>


<rect x="370" y="180" width="130" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="435" y="210" text-anchor="middle" fill="white" font-size="12">
Kafka
</text>


<rect x="570" y="70" width="130" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="635" y="100" text-anchor="middle" fill="white" font-size="11">
Rating Aggregator
</text>


<rect x="570" y="180" width="130" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="635" y="210" text-anchor="middle" fill="white" font-size="11">
Search Indexer
</text>


<rect x="570" y="260" width="130" height="40" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="635" y="285" text-anchor="middle" fill="white" font-size="11">
Notification Svc
</text>


<line x1="130" y1="160" x2="178" y2="160" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="310" y1="148" x2="368" y2="100" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="310" y1="175" x2="368" y2="205" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="500" y1="200" x2="568" y2="100" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="500" y1="205" x2="568" y2="205" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="500" y1="210" x2="568" y2="275" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<text x="490" y="20" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
TripAdvisor: Review Submission
</text>


</svg>

</details>


### Step-by-Step


1. **User submits review:** `POST /v1/reviews { place_id, rating: 5, subcategories: {food: 5, service: 4, atmosphere: 5}, text: "...", photos: [...] }`

2. **Fraud Detector (ML):** Checks for fake reviews — linguistic analysis, reviewer history, IP/device patterns, timing patterns. Score < 0.3 = auto-reject, 0.3-0.7 = manual review queue

3. **Review Service:** Stores review in Review DB. Status: "published" or "pending_review"

4. **Kafka event:** "review.published" consumed by:
    

      5. **Rating Aggregator:** Recalculates place's average rating (Bayesian average), updates Place DB

      6. **Search Indexer:** Updates Elasticsearch document with new review count and rating

      7. **Notification:** Notifies place owner of new review

    


> **
**Example:** User reviews "Roscioli" — 5 stars, "Best carbonara in Rome! Authentic and incredible pasta." + 3 photos. Fraud Detector: score 0.95 (legitimate — user has verified bookings, diverse review history, unique photo EXIF data). Published immediately. Rating Aggregator: old avg 4.68 from 3199 reviews → new avg 4.68 from 3200 reviews (minimal change). Elasticsearch updated: review_count: 3200. Notification sent to restaurant owner.


## Combined Overall Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 450" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#34e0a1">
</polygon>
</marker>
</defs>


<text x="525" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
TripAdvisor: Combined Architecture
</text>


<rect x="20" y="190" width="90" height="50" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="65" y="220" text-anchor="middle" fill="white" font-size="11">
Clients
</text>


<rect x="140" y="130" width="90" height="35" rx="8" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="185" y="152" text-anchor="middle" fill="white" font-size="10">
CDN
</text>


<rect x="140" y="200" width="90" height="35" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="185" y="222" text-anchor="middle" fill="white" font-size="10">
API GW
</text>


<rect x="280" y="60" width="110" height="35" rx="6" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="335" y="82" text-anchor="middle" fill="white" font-size="10">
Search Svc
</text>


<rect x="280" y="120" width="110" height="35" rx="6" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="335" y="142" text-anchor="middle" fill="white" font-size="10">
Review Svc
</text>


<rect x="280" y="180" width="110" height="35" rx="6" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="335" y="202" text-anchor="middle" fill="white" font-size="10">
Place Svc
</text>


<rect x="280" y="240" width="110" height="35" rx="6" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="335" y="262" text-anchor="middle" fill="white" font-size="10">
Price Compare
</text>


<rect x="280" y="300" width="110" height="35" rx="6" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="335" y="322" text-anchor="middle" fill="white" font-size="10">
Recommendation
</text>


<rect x="280" y="360" width="110" height="35" rx="6" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="335" y="382" text-anchor="middle" fill="white" font-size="10">
Fraud Detector
</text>


<rect x="460" y="60" width="100" height="35" rx="6" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="510" y="82" text-anchor="middle" fill="white" font-size="10">
Elasticsearch
</text>


<rect x="460" y="120" width="100" height="35" rx="6" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="510" y="142" text-anchor="middle" fill="white" font-size="10">
Place Cache
</text>


<rect x="460" y="180" width="100" height="35" rx="6" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="510" y="202" text-anchor="middle" fill="white" font-size="10">
Kafka
</text>


<ellipse cx="650" cy="78" rx="55" ry="20" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="650" y="83" text-anchor="middle" fill="white" font-size="9">
Place DB
</text>


<ellipse cx="650" cy="142" rx="55" ry="20" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="650" y="147" text-anchor="middle" fill="white" font-size="9">
Review DB
</text>


<ellipse cx="650" cy="205" rx="55" ry="20" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="650" y="210" text-anchor="middle" fill="white" font-size="9">
User DB
</text>


<rect x="620" y="250" width="80" height="30" rx="6" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="660" y="270" text-anchor="middle" fill="white" font-size="9">
S3 (Photos)
</text>


<line x1="110" y1="210" x2="138" y2="145" stroke="#34e0a1" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="110" y1="218" x2="138" y2="218" stroke="#34e0a1" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="230" y1="217" x2="278" y2="78" stroke="#34e0a1" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="230" y1="217" x2="278" y2="137" stroke="#34e0a1" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="230" y1="217" x2="278" y2="197" stroke="#34e0a1" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="390" y1="78" x2="458" y2="78" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="390" y1="140" x2="458" y2="140" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="390" y1="200" x2="458" y2="195" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="560" y1="78" x2="593" y2="78" stroke="#ff6b6b" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="560" y1="140" x2="593" y2="142" stroke="#ff6b6b" stroke-width="1" marker-end="url(#arrow3)">
</line>


</svg>

</details>


## Database Schema


### SQL — PostgreSQL (Places, Users)


| Table         | Column       | Type                          | Details |
|---------------|--------------|-------------------------------|---------|
| **places**    | place_id     | BIGINT                        | `PK`    |
| name          | VARCHAR(255) | `IDX`                         |         |
| type          | ENUM         | hotel, restaurant, attraction |         |
| lat, lng      | DOUBLE       | `IDX`                         |         |
| city, country | VARCHAR      | `IDX`                         |         |
| avg_rating    | DECIMAL(3,2) | Denormalized Bayesian average |         |
| review_count  | INT          | Denormalized                  |         |
| price_level   | ENUM         | $, $$, $$$, $$$$              |         |


### NoSQL — Cassandra (Reviews)


| Table               | Column        | Type                            | Details |
|---------------------|---------------|---------------------------------|---------|
| **reviews**         | place_id      | BIGINT                          | `PK`    |
| review_id           | TIMEUUID      | `PK` DESC                       |         |
| user_id             | BIGINT        |                                 |         |
| overall_rating      | INT           | 1-5                             |         |
| subcategory_ratings | MAP<TEXT,INT> | {food: 5, service: 4, value: 4} |         |
| review_text         | TEXT          |                                 |         |
| photo_urls          | LIST<TEXT>    |                                 |         |


Sharding & Indexes


- **places:** Shard by `city` — all places in Rome on same shard. City-level searches are shard-local

- **reviews:** Partition by `place_id` — all reviews for one place co-located. Clustering DESC for newest-first display

- **Bayesian average:** `weighted_avg = (global_avg * C + sum_ratings) / (C + review_count)` where C = minimum reviews for statistical significance (~25). Prevents places with 1 review at 5★ from ranking above 4.5★ with 5000 reviews


## Cache & CDN Deep Dive


| Cache                  | Strategy                     | TTL      | Purpose                                   |
|------------------------|------------------------------|----------|-------------------------------------------|
| Place Details (Redis)  | Read-through                 | 1 hr     | Place pages load fast                     |
| Review Summary (Redis) | Write-behind (on new review) | 30 min   | Rating breakdown, review count            |
| CDN — Place Pages      | Pull-based                   | 1 hr     | SEO: pre-rendered HTML pages for crawlers |
| CDN — Photos           | Pull-based                   | 365 days | User-uploaded review photos               |


**SEO strategy:** Server-side rendered HTML for place pages. CDN caches rendered HTML for Googlebot. Dynamic content (prices, availability) loaded client-side via JavaScript.


## Tradeoffs & Alternatives


> **
✅ Meta-search (Price Comparison)
- Users get best price without visiting each booking site
- Revenue via CPC (cost-per-click) when users click through to booking partner
- TripAdvisor doesn't handle booking complexity


❌ Meta-search Downsides
- No control over booking experience (user leaves TripAdvisor)
- Price accuracy: displayed price may differ on partner site
- Revenue dependent on partner willingness to pay CPC


- **Alternative — Direct booking:** TripAdvisor could handle bookings directly (like Booking.com). Higher revenue per transaction but massive operational complexity (payments, cancellations, customer support)

- **Alternative — Embedding-based search:** Instead of keyword search, semantic search using BERT embeddings. "Romantic dinner spot with view" finds relevant restaurants even without those exact keywords


## Additional Information


- **Review fraud detection:** ML model features: reviewer's review velocity, text similarity to other reviews, device/IP clustering, rating distribution anomaly. Paid fake review farms create distinct patterns

- **Traveler types:** Business, family, solo, couple. Different ranking weights per type: families → kid-friendly, business → WiFi + work desk, couples → romantic

- **Photo quality scoring:** ML model ranks user photos by quality (lighting, composition, relevance). Best photos shown first on place page