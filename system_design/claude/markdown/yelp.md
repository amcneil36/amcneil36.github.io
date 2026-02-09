# 🍽️ System Design: Yelp (Local Business Search & Reviews)


## 📋 Functional Requirements


  - Users can search for businesses by name, category, or location (with radius)

  - Users can view business details (hours, photos, menu, contact info)

  - Users can write reviews with ratings (1-5 stars), text, and photos

  - Users can discover nearby businesses on a map view


## 🔒 Non-Functional Requirements


  - **Low latency search:** <200ms for proximity-based queries with ranking

  - **High read throughput:** Read-heavy system — search:write ratio ~100:1

  - **Geospatial accuracy:** Precise distance calculations and radius filtering

  - **Availability:** 99.9% uptime, eventual consistency acceptable for reviews

  - **Scalability:** ~250M businesses globally, billions of reviews


## 🔄 Flow 1 — Business Search (Nearby + Text)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 400" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="160" width="120" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="80" y="195" text-anchor="middle" fill="white" font-size="14">
Mobile / Web
</text>

  
<line x1="140" y1="190" x2="210" y2="190" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="220" y="160" width="130" height="60" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="285" y="188" text-anchor="middle" fill="white" font-size="13">
API Gateway
</text>

  
<text x="285" y="205" text-anchor="middle" fill="white" font-size="10">
(Rate Limit)
</text>

  
<line x1="350" y1="190" x2="420" y2="190" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="430" y="160" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="500" y="188" text-anchor="middle" fill="white" font-size="13">
Search Service
</text>

  
<text x="500" y="205" text-anchor="middle" fill="white" font-size="10">
(Query Parser)
</text>

  
<line x1="570" y1="190" x2="640" y2="150" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="570" y1="190" x2="640" y2="240" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="650" y="120" width="150" height="55" rx="10" fill="#8a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="725" y="145" text-anchor="middle" fill="white" font-size="12">
Elasticsearch
</text>

  
<text x="725" y="162" text-anchor="middle" fill="white" font-size="10">
(Geospatial + Text)
</text>

  
<rect x="650" y="215" width="150" height="55" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="725" y="240" text-anchor="middle" fill="white" font-size="12">
Redis Cache
</text>

  
<text x="725" y="257" text-anchor="middle" fill="white" font-size="10">
(Popular Queries)
</text>

  
<line x1="800" y1="148" x2="870" y2="190" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="800" y1="243" x2="870" y2="200" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="880" y="165" width="150" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="955" y="188" text-anchor="middle" fill="white" font-size="12">
Ranking Service
</text>

  
<text x="955" y="205" text-anchor="middle" fill="white" font-size="10">
(ML Relevance)
</text>


</svg>

</details>


### Step-by-Step


  1. **Client sends search:** `GET /api/v1/search?q=sushi&lat=37.77&lng=-122.42&radius=5000&sort=best_match`

  2. **API Gateway** validates auth, applies rate limits per user/IP, routes to Search Service

  3. **Search Service** parses the query — extracts text terms, geolocation, filters (price, open_now, categories)

  4. **Cache check:** Hash the normalized query → check Redis for cached result set (TTL 5 min for popular queries)

  5. **Elasticsearch geo_distance query:** Combines `bool` with `geo_distance` filter and `multi_match` for text, using `function_score` to blend relevance + distance + rating

  6. **Ranking Service** re-ranks top-100 candidates using ML model considering user preferences, review recency, response rate, sponsor bids

  7. **Return paginated results** with business summaries, ratings, distance, snippet of top review


> **
**Example:** User in San Francisco searches "sushi" within 5km → Elasticsearch returns 200 candidates within geo_distance circle → Ranking re-ranks with ML (considering user's past preferences for high-end sushi, price filter, open now) → Returns top 20 with paginated results: "Sushi Ran — ⭐4.6 — 2.3 mi — $$$"


### 🔍 Component Deep Dives — Search Flow


Elasticsearch Geospatial Query


**Protocol:** HTTP/REST to Elasticsearch cluster


**Index Mapping:** `geo_point` field for lat/lng, `text` fields with custom analyzers (synonym expansion, ngrams for autocomplete)


POST /businesses/_search
{
  "query": {
    "function_score": {
      "query": {
        "bool": {
          "must": [
            { "multi_match": { "query": "sushi", "fields": ["name^3", "categories^2", "description"] } }
          ],
          "filter": [
            { "geo_distance": { "distance": "5km", "location": { "lat": 37.77, "lon": -122.42 } } },
            { "term": { "is_open": true } }
          ]
        }
      },
      "functions": [
        { "field_value_factor": { "field": "avg_rating", "modifier": "log1p", "factor": 2 } },
        { "gauss": { "location": { "origin": "37.77,-122.42", "scale": "2km", "decay": 0.5 } } },
        { "field_value_factor": { "field": "review_count", "modifier": "log1p", "factor": 0.5 } }
      ],
      "score_mode": "sum",
      "boost_mode": "multiply"
    }
  }
}


**Key:** `gauss` decay function — businesses closer to user get exponentially higher scores. `log1p` on review_count prevents businesses with 10,000 reviews from dominating over newer ones with 50 quality reviews.


Geospatial Indexing Strategy


**Geohash:** Elasticsearch uses geohash internally for geo_point fields — encodes lat/lng into base-32 string prefixes for efficient bounding-box filtering


**Precision levels:** geohash-6 (~1.2km cells) used for proximity search, geohash-4 (~39km) for broader city searches


**Quadtree alternative:** For in-memory spatial index (e.g., Redis), quadtree divides space recursively — O(log n) point lookup


**S2 Geometry (Google):** Maps sphere to unit cube faces → Hilbert curve space-filling → S2 Cell IDs with hierarchical levels — better for covering irregular regions


Ranking Service (ML)


**Protocol:** gRPC from Search Service to Ranking Service


**Input:** Candidate list (business IDs + features), user context (location, history, time-of-day)


**Model:** LambdaMART or neural learning-to-rank — features include: distance, avg_rating, review_count, price_match, category_match, is_sponsored, recency_of_reviews, user_personalization_score


**Output:** Re-ranked list with predicted click-through probability


## 🔄 Flow 2 — Review Submission


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="140" width="120" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="80" y="175" text-anchor="middle" fill="white" font-size="14">
User Client
</text>

  
<line x1="140" y1="170" x2="210" y2="170" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="220" y="140" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="290" y="168" text-anchor="middle" fill="white" font-size="13">
Review Service
</text>

  
<text x="290" y="185" text-anchor="middle" fill="white" font-size="10">
(Validation)
</text>

  
<line x1="360" y1="170" x2="420" y2="110" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="360" y1="170" x2="420" y2="170" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="360" y1="170" x2="420" y2="240" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="430" y="80" width="150" height="55" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="505" y="105" text-anchor="middle" fill="white" font-size="12">
Fraud Detection
</text>

  
<text x="505" y="122" text-anchor="middle" fill="white" font-size="10">
(ML Pipeline)
</text>

  
<rect x="430" y="145" width="150" height="55" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="505" y="168" text-anchor="middle" fill="white" font-size="12">
Kafka
</text>

  
<text x="505" y="185" text-anchor="middle" fill="white" font-size="10">
(review-events)
</text>

  
<rect x="430" y="215" width="150" height="55" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="505" y="238" text-anchor="middle" fill="white" font-size="12">
Photo Service
</text>

  
<text x="505" y="255" text-anchor="middle" fill="white" font-size="10">
(S3 Upload)
</text>

  
<line x1="580" y1="172" x2="660" y2="120" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="580" y1="172" x2="660" y2="172" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="580" y1="172" x2="660" y2="240" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="670" y="90" width="140" height="55" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="740" y="115" text-anchor="middle" fill="white" font-size="12">
PostgreSQL
</text>

  
<text x="740" y="132" text-anchor="middle" fill="white" font-size="10">
(Reviews)
</text>

  
<rect x="670" y="150" width="140" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="740" y="175" text-anchor="middle" fill="white" font-size="12">
Rating Aggregator
</text>

  
<text x="740" y="192" text-anchor="middle" fill="white" font-size="10">
(Recalculate Avg)
</text>

  
<rect x="670" y="215" width="140" height="55" rx="10" fill="#8a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="740" y="238" text-anchor="middle" fill="white" font-size="12">
Elasticsearch
</text>

  
<text x="740" y="255" text-anchor="middle" fill="white" font-size="10">
(Index Update)
</text>


</svg>

</details>


### Step-by-Step


  1. **Client submits review:** `POST /api/v1/businesses/{id}/reviews` with JSON body: `{ "rating": 5, "text": "Best sushi in the city...", "photos": [presigned_urls] }`

  2. **Review Service** validates: user is authenticated, hasn't already reviewed this business, text length within limits, profanity filter

  3. **Fraud Detection (async):** ML model scores review — checks for patterns: review velocity (many reviews in short time), IP/device clustering, text similarity to known fake reviews, user review history anomalies

  4. **Photo Service:** Presigned S3 upload URLs provided, photos uploaded directly from client, thumbnails generated via Lambda

  5. **Write to PostgreSQL:** Insert review row with initial status `pending` (if fraud score > threshold) or `published`

  6. **Kafka event:** `review-created` event triggers consumers — Rating Aggregator recalculates business avg_rating, Elasticsearch updates business document with new review count/rating

  7. **Business owner notified** of new review via push notification


> **
**Example:** User writes a 5-star review for "Sushi Ran" with 2 photos → Fraud ML scores 0.15 (low risk) → Published immediately → Rating Aggregator: business had 1,247 reviews at 4.58 avg → new avg = (1247*4.58 + 5)/1248 = 4.5803 → Elasticsearch updated → Next search query reflects new rating


### 🔍 Component Deep Dives — Review Flow


Fraud Detection ML


**Features used:**


  - **User signals:** Account age, review count, profile completeness, device fingerprint

  - **Behavioral signals:** Time between reviews, geographic plausibility (reviewing in 5 cities in 1 day), copy-paste text detection

  - **Text signals:** NLP sentiment analysis, similarity to known spam templates, excessive superlatives

  - **Network signals:** IP clustering (many reviews from same IP), social graph isolation


**Recommendation Filter:** Yelp's "Recommended Reviews" system filters ~25% of reviews as "not recommended" — they're still visible but collapsed and don't count toward rating


Rating Aggregation


**Bayesian Average:** Rather than simple mean, use Bayesian average to handle businesses with few reviews:

bayesian_avg = (C * m + sum_of_ratings) / (C + review_count)
where C = minimum reviews threshold (e.g., 10)
      m = global mean rating across all businesses (~3.7)


This pulls businesses with very few reviews toward the global mean, preventing a single 5-star review from ranking #1.


## 🔄 Flow 3 — Nearby Business Discovery (Map View)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="140" width="120" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="80" y="168" text-anchor="middle" fill="white" font-size="13">
Mobile App
</text>

  
<text x="80" y="185" text-anchor="middle" fill="white" font-size="10">
(Map View)
</text>

  
<line x1="140" y1="170" x2="210" y2="170" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="220" y="140" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="290" y="168" text-anchor="middle" fill="white" font-size="13">
Discovery Service
</text>

  
<text x="290" y="185" text-anchor="middle" fill="white" font-size="10">
(Viewport Query)
</text>

  
<line x1="360" y1="170" x2="430" y2="120" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<line x1="360" y1="170" x2="430" y2="220" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="440" y="90" width="160" height="55" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="520" y="115" text-anchor="middle" fill="white" font-size="12">
Geohash Grid Cache
</text>

  
<text x="520" y="132" text-anchor="middle" fill="white" font-size="10">
(Redis — precomputed)
</text>

  
<rect x="440" y="195" width="160" height="55" rx="10" fill="#8a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="520" y="218" text-anchor="middle" fill="white" font-size="12">
Elasticsearch
</text>

  
<text x="520" y="235" text-anchor="middle" fill="white" font-size="10">
(geo_bounding_box)
</text>

  
<line x1="600" y1="118" x2="680" y2="170" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<line x1="600" y1="222" x2="680" y2="175" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="690" y="145" width="150" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="765" y="168" text-anchor="middle" fill="white" font-size="12">
Map Tile Service
</text>

  
<text x="765" y="185" text-anchor="middle" fill="white" font-size="10">
(Pin Clustering)
</text>


</svg>

</details>


### Step-by-Step


  1. **Client sends viewport:** `GET /api/v1/discover?sw_lat=37.75&sw_lng=-122.45&ne_lat=37.80&ne_lng=-122.40&zoom=14&category=restaurants`

  2. **Discovery Service** calculates geohash cells covering the viewport bounding box

  3. **Geohash Grid Cache:** At popular zoom levels, precomputed Redis hashes store top businesses per geohash cell — O(1) lookup per cell

  4. **Cache miss → Elasticsearch:** `geo_bounding_box` query returns all businesses in viewport, limited to top 200

  5. **Pin Clustering:** At low zoom levels (zoomed out), cluster nearby pins into numbered circles (e.g., "23 restaurants") using server-side grid clustering or client-side supercluster.js

  6. **Return pin data:** Array of {id, lat, lng, name, rating, price_level, category_icon} for map rendering


> **
**Example:** User views downtown SF at zoom=14 → viewport covers 12 geohash-6 cells → Redis has cached top-20 businesses per cell → Returns 240 pins → Client renders pin icons with star ratings on map tiles → User taps a pin → Business detail sheet slides up


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 700" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ahc" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="50" width="130" height="50" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="85" y="80" text-anchor="middle" fill="white" font-size="13">
Mobile App
</text>

  
<rect x="20" y="120" width="130" height="50" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="85" y="150" text-anchor="middle" fill="white" font-size="13">
Web Browser
</text>

  
<line x1="150" y1="80" x2="230" y2="150" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="150" y1="145" x2="230" y2="150" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="240" y="120" width="140" height="60" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="310" y="148" text-anchor="middle" fill="white" font-size="13">
API Gateway / LB
</text>

  
<text x="310" y="165" text-anchor="middle" fill="white" font-size="10">
(L7 + CDN)
</text>

  

  
<line x1="380" y1="140" x2="460" y2="80" stroke="#4a9eff" marker-end="url(#ahc)">
</line>

  
<rect x="470" y="50" width="140" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="540" y="75" text-anchor="middle" fill="white" font-size="12">
Search Service
</text>

  
<text x="540" y="92" text-anchor="middle" fill="white" font-size="10">
(Query + Rank)
</text>

  

  
<line x1="380" y1="155" x2="460" y2="160" stroke="#ff4a4a" marker-end="url(#ahc)">
</line>

  
<rect x="470" y="130" width="140" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="540" y="155" text-anchor="middle" fill="white" font-size="12">
Review Service
</text>

  
<text x="540" y="172" text-anchor="middle" fill="white" font-size="10">
(Write + Moderate)
</text>

  

  
<line x1="380" y1="165" x2="460" y2="235" stroke="#ffa54a" marker-end="url(#ahc)">
</line>

  
<rect x="470" y="210" width="140" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="540" y="235" text-anchor="middle" fill="white" font-size="12">
Business Service
</text>

  
<text x="540" y="252" text-anchor="middle" fill="white" font-size="10">
(CRUD + Hours)
</text>

  

  
<line x1="380" y1="170" x2="460" y2="310" stroke="#4afff0" marker-end="url(#ahc)">
</line>

  
<rect x="470" y="290" width="140" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="540" y="315" text-anchor="middle" fill="white" font-size="12">
Discovery Service
</text>

  
<text x="540" y="332" text-anchor="middle" fill="white" font-size="10">
(Map + Nearby)
</text>

  

  
<line x1="610" y1="78" x2="700" y2="78" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="610" y1="315" x2="700" y2="315" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="710" y="50" width="160" height="55" rx="10" fill="#8a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="790" y="73" text-anchor="middle" fill="white" font-size="12">
Elasticsearch Cluster
</text>

  
<text x="790" y="90" text-anchor="middle" fill="white" font-size="10">
(Geo + Full-Text)
</text>

  

  
<line x1="610" y1="158" x2="700" y2="200" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="610" y1="238" x2="700" y2="210" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="710" y="170" width="160" height="55" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="790" y="195" text-anchor="middle" fill="white" font-size="12">
PostgreSQL (Primary)
</text>

  
<text x="790" y="212" text-anchor="middle" fill="white" font-size="10">
(Users, Reviews, Biz)
</text>

  

  
<line x1="610" y1="310" x2="700" y2="310" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="710" y="285" width="160" height="55" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="790" y="308" text-anchor="middle" fill="white" font-size="12">
Redis Cluster
</text>

  
<text x="790" y="325" text-anchor="middle" fill="white" font-size="10">
(Cache + Geo Grid)
</text>

  

  
<line x1="540" y1="185" x2="540" y2="410" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="470" y="420" width="140" height="55" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="540" y="445" text-anchor="middle" fill="white" font-size="12">
Apache Kafka
</text>

  
<text x="540" y="462" text-anchor="middle" fill="white" font-size="10">
(Event Bus)
</text>

  

  
<line x1="610" y1="448" x2="700" y2="430" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="610" y1="448" x2="700" y2="490" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="610" y1="448" x2="700" y2="550" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="710" y="405" width="160" height="45" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="790" y="432" text-anchor="middle" fill="white" font-size="11">
Rating Aggregator
</text>

  
<rect x="710" y="465" width="160" height="45" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="790" y="492" text-anchor="middle" fill="white" font-size="11">
Fraud Detector (ML)
</text>

  
<rect x="710" y="525" width="160" height="45" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="790" y="552" text-anchor="middle" fill="white" font-size="11">
Notification Service
</text>

  

  
<rect x="950" y="170" width="140" height="55" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="1020" y="195" text-anchor="middle" fill="white" font-size="12">
S3 + CDN
</text>

  
<text x="1020" y="212" text-anchor="middle" fill="white" font-size="10">
(Photos / Static)
</text>

  
<line x1="870" y1="197" x2="940" y2="197" stroke="#aaa" marker-end="url(#ahc)">
</line>


</svg>

</details>


> **
**End-to-End Example:** User opens Yelp in SF → Map view loads pins from Redis geohash cache → Types "best tacos" → Search Service queries Elasticsearch with geo_distance + text match → ML ranker re-ranks by personalized relevance → User picks "La Taqueria" → Business detail page loaded from PostgreSQL + cached in Redis → User writes 5-star review → Review Service writes to PostgreSQL → Kafka event triggers: rating recalculation, Elasticsearch update, owner notification → Next user searching "tacos" sees updated rating


## 🗄️ Database Schema


### PostgreSQL — Relational Data (ACID)


businesses


| Column        | Type          | Notes                       |
|---------------|---------------|-----------------------------|
| id            | BIGINT        | `PK`                        |
| name          | VARCHAR(255)  | `IDX`                       |
| slug          | VARCHAR(255)  | `IDX`                       |
| latitude      | DECIMAL(10,7) | GiST spatial index          |
| longitude     | DECIMAL(10,7) | GiST spatial index          |
| address       | TEXT          |                             |
| city          | VARCHAR(100)  | `IDX`                       |
| state         | VARCHAR(50)   |                             |
| country       | VARCHAR(3)    |                             |
| zip_code      | VARCHAR(20)   |                             |
| phone         | VARCHAR(20)   |                             |
| website       | TEXT          |                             |
| category_ids  | BIGINT[]      | `IDX`                       |
| price_level   | SMALLINT      | 1-4 ($-$$$$)                |
| avg_rating    | DECIMAL(3,2)  | Denormalized, updated async |
| review_count  | INTEGER       | Denormalized                |
| is_claimed    | BOOLEAN       |                             |
| owner_user_id | BIGINT        | `FK`                        |
| created_at    | TIMESTAMPTZ   |                             |
| updated_at    | TIMESTAMPTZ   |                             |


business_hours


| Column      | Type     | Notes         |
|-------------|----------|---------------|
| business_id | BIGINT   | `PK` `FK`     |
| day_of_week | SMALLINT | `PK` 0=Monday |
| open_time   | TIME     |               |
| close_time  | TIME     |               |


reviews


| Column       | Type         | Notes                                 |
|--------------|--------------|---------------------------------------|
| id           | BIGINT       | `PK`                                  |
| business_id  | BIGINT       | `FK` `IDX`                            |
| user_id      | BIGINT       | `FK` `IDX`                            |
| rating       | SMALLINT     | 1-5                                   |
| text         | TEXT         |                                       |
| photo_urls   | TEXT[]       |                                       |
| useful_count | INTEGER      | Denormalized                          |
| funny_count  | INTEGER      | Denormalized                          |
| cool_count   | INTEGER      | Denormalized                          |
| status       | VARCHAR(20)  | published / pending / not_recommended |
| fraud_score  | DECIMAL(5,4) | ML confidence score                   |
| created_at   | TIMESTAMPTZ  | `IDX`                                 |


 `IDX` on (business_id, user_id) — one review per user per business


users


| Column        | Type         | Notes             |
|---------------|--------------|-------------------|
| id            | BIGINT       | `PK`              |
| email         | VARCHAR(255) | `IDX`             |
| display_name  | VARCHAR(100) |                   |
| avatar_url    | TEXT         |                   |
| review_count  | INTEGER      | Denormalized      |
| elite_year    | INTEGER      | NULL if not elite |
| home_location | POINT        | GiST index        |
| created_at    | TIMESTAMPTZ  |                   |


categories


| Column    | Type         | Notes         |
|-----------|--------------|---------------|
| id        | BIGINT       | `PK`          |
| name      | VARCHAR(100) | `IDX`         |
| parent_id | BIGINT       | `FK`          |
| alias     | VARCHAR(100) | URL-safe slug |


### Sharding Strategy


**businesses:** Shard by `city` or geographic region — businesses are heavily locality-bound, queries are always scoped to a region  `SHARD`


**reviews:** Shard by `business_id` — keeps all reviews for a business on one shard for efficient aggregation  `SHARD`


**users:** Shard by `user_id` hash — uniform distribution  `SHARD`


### Indexes Explained


  - **GiST spatial index** on (latitude, longitude): Enables efficient proximity queries in PostgreSQL — R-tree based structure for multi-dimensional data

  - **GIN index** on category_ids[]: Generalized Inverted Index — indexes each element of the array, enabling "find businesses in category X" queries

  - **B-tree composite** on reviews(business_id, created_at DESC): Efficiently fetches recent reviews for a business page

  - **Partial index** on reviews WHERE status = 'published': Only indexes recommended reviews, reduces index size by ~25%


### Denormalization Decisions


  - **avg_rating + review_count on businesses:** Avoids JOIN + AVG() on every search result — updated asynchronously by Rating Aggregator consumer

  - **useful/funny/cool counts on reviews:** Avoids COUNT() on reactions table for every review display

  - **review_count on users:** Profile badge display without counting reviews table


### Elasticsearch — Search Index


Index: businesses
{
  "name": { "type": "text", "analyzer": "custom_business_analyzer" },
  "categories": { "type": "keyword" },          // facetable
  "location": { "type": "geo_point" },           // lat/lng
  "city": { "type": "keyword" },
  "avg_rating": { "type": "float" },
  "review_count": { "type": "integer" },
  "price_level": { "type": "integer" },
  "is_open_now": { "type": "boolean" },          // updated by cron
  "hours": { "type": "nested" },                 // day + open/close
  "amenities": { "type": "keyword" },            // outdoor_seating, wifi, etc.
  "photos_count": { "type": "integer" }
}
Sharding: 12 primary shards (by geohash region prefix), 1 replica each


## 💾 Cache & CDN Deep Dive


### Redis Caching Strategy


| Cache           | Key Pattern                 | TTL    | Strategy                          |
|-----------------|-----------------------------|--------|-----------------------------------|
| Search Results  | `search:{hash(query)}`      | 5 min  | Read-through                      |
| Business Detail | `biz:{id}`                  | 15 min | Read-through, invalidate on write |
| Geohash Grid    | `geo:{geohash6}:{category}` | 30 min | Precomputed, batch refresh        |
| User Session    | `session:{token}`           | 24 hr  | Write-through                     |
| Popular Reviews | `reviews:{biz_id}:top`      | 10 min | Read-through                      |


Eviction Policy


**allkeys-lfu:** Least Frequently Used — popular search queries and business pages stay cached, rarely-accessed ones evicted first. LFU is better than LRU here because search traffic is highly skewed (popular businesses/queries dominate).


### CDN Strategy


  - **Photo CDN:** Business photos served via CloudFront/Akamai — aggressive caching with content-hash URLs (immutable). Thumbnails generated at upload in multiple sizes (150x150, 300x300, 600x600)

  - **Static assets:** JS/CSS/fonts on CDN with 1-year cache + content hashing

  - **API responses:** NOT cached on CDN — personalized, geo-dependent results. Exception: popular city landing pages (e.g., "Best Restaurants in NYC") served as pre-rendered HTML from CDN

  - **Pull-based CDN:** Origin pull on cache miss — CDN requests from origin server, caches response at edge


## ⚖️ Scaling Considerations


  - **Elasticsearch scaling:** Shard by geographic region — US-West, US-East, Europe, Asia clusters. Cross-cluster search for global queries. Hot-warm architecture: recent/popular data on SSD (hot), older data on HDD (warm)

  - **Read replicas:** PostgreSQL read replicas per region for business detail page reads (read-heavy). Writes go to primary, replicated async

  - **Connection pooling:** PgBouncer in front of PostgreSQL — reduces connection overhead from hundreds of service instances

  - **L7 Load Balancer:** Route by request type — search queries to search pods, review writes to review pods. Separate scaling groups per service

  - **Rate limiting:** Per-user and per-IP rate limits at API Gateway — prevents scraping and abuse. Stricter limits on write endpoints (reviews)

  - **Auto-scaling:** Elasticsearch and service pods scale on CPU/query-latency metrics. Weekday lunch hours and weekends see 3x traffic spikes


## ⚖️ Tradeoffs


> **
  
> **
    ✅ Elasticsearch for Search
    

Rich query DSL, built-in geo support, facets/aggregations, fast full-text search with custom scoring

  
  
> **
    ❌ Elasticsearch for Search
    

Not a primary datastore — requires syncing from PostgreSQL (CDC or dual-write), eventual consistency, complex cluster management, expensive memory for indices

  
  
> **
    ✅ Denormalized avg_rating
    

Avoids expensive aggregation on every search, sub-millisecond read from index

  
  
> **
    ❌ Denormalized avg_rating
    

Brief staleness after new review — acceptable (seconds of delay). Requires extra write-path complexity (Kafka consumer for aggregation)

  
  
> **
    ✅ Bayesian Average Rating
    

Prevents rating manipulation — new businesses with 1 five-star review don't rank #1

  
  
> **
    ❌ Bayesian Average Rating
    

New businesses are penalized until they accumulate enough reviews — cold start problem for new listings

  
  
> **
    ✅ Geohash Grid Pre-computation
    

O(1) lookup for map view pins, dramatically reduces Elasticsearch load for map browsing

  
  
> **
    ❌ Geohash Grid Pre-computation
    

Memory overhead for storing grid cells, staleness (30-min refresh), edge effects at geohash boundaries

  


## 🔄 Alternative Approaches


PostGIS Instead of Elasticsearch for Geo


PostgreSQL + PostGIS extension provides robust geospatial queries (ST_DWithin, ST_Distance) with GiST/SP-GiST indexes. Simpler stack (no Elasticsearch sync needed). Works well up to ~10M businesses but doesn't scale horizontally as easily as Elasticsearch for full-text + geo combined queries.


Quadtree / KD-Tree In-Memory Index


Build in-memory spatial index for nearby discovery — O(log n) range queries. Used by Uber's H3 for hexagonal spatial indexing. Better for real-time updates (driver locations) but overkill for relatively static business data.


Graph-Based Recommendation


Model user-business interactions as a graph (Neo4j or Amazon Neptune). Enables collaborative filtering: "Users who liked Sushi Ran also liked..." — better personalization but adds graph DB complexity and doesn't replace text search.


## 📚 Additional Information


### Yelp's Recommendation Engine


Yelp's "Recommended Reviews" system is a distinguishing feature — approximately 25% of reviews are filtered as "not recommended." The ML model considers:


  - User's established review history (new accounts penalized)

  - Review quality signals (length, specificity, photo attachment)

  - Social connections (isolated accounts flagged)

  - Temporal patterns (burst of reviews for one business = suspicious)


### SEO & Server-Side Rendering


Yelp relies heavily on organic search traffic. Business pages are server-side rendered (SSR) with structured data (JSON-LD schema.org LocalBusiness markup). This ensures Google can index business details, reviews, and ratings — showing rich snippets (star ratings) in search results.


### Business Owner Dashboard


Claimed businesses get analytics: page views, customer leads (calls, directions, website clicks), review response rate. This data is stored in a time-series database (InfluxDB or TimescaleDB) and aggregated for dashboard display.


### Content Moderation Pipeline


Reviews pass through: (1) automated profanity/hate speech detection, (2) ML spam classifier, (3) human moderation queue for edge cases. Business owners can flag reviews, which triggers manual review. Appeals process with SLA of 48 hours.