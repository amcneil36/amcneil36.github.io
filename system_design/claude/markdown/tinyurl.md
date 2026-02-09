# System Design: TinyURL (URL Shortener)


## Functional Requirements


  1. **Shorten URL** – Given a long URL, generate a unique short URL (e.g., tinyurl.com/abc123).

  2. **Redirect** – When users visit the short URL, redirect them (HTTP 301/302) to the original long URL.

  3. **Custom aliases** – Users can optionally specify a custom short code (e.g., tinyurl.com/my-brand).

  4. **Expiration** – URLs can optionally have an expiration time after which they stop working.

  5. **Analytics** – Track click counts, referrers, geographic data, and device info for each shortened URL.


## Non-Functional Requirements


  1. **High availability** – 99.99% uptime; redirect must always work.

  2. **Low latency** – Redirect in <50ms (p99). URL creation in <200ms.

  3. **Scalability** – Handle 1B+ shortened URLs, 100K reads/sec, 1K writes/sec (100:1 read-to-write ratio).

  4. **Uniqueness** – No two long URLs should generate the same short code (unless explicitly the same URL).

  5. **Durability** – Shortened URLs must be persistent and never lost.


## Flow 1: Creating a Short URL


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a1" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="130" width="80" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="50" y="160" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
User
</text>

  
<line x1="90" y1="155" x2="160" y2="155" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<text x="125" y="145" text-anchor="middle" fill="#555" font-size="9">
POST /shorten
</text>

  
<rect x="160" y="130" width="80" height="50" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="200" y="153" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
API
</text>

  
<text x="200" y="165" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
GW
</text>

  
<line x1="240" y1="155" x2="310" y2="155" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="310" y="125" width="120" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="370" y="153" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
URL Shortener
</text>

  
<text x="370" y="168" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Service
</text>

  

  
<line x1="370" y1="125" x2="370" y2="55" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="315" y="5" width="110" height="50" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="370" y="28" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Key Gen
</text>

  
<text x="370" y="43" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Service (KGS)
</text>

  

  
<line x1="425" y1="30" x2="500" y2="30" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="550" cy="23" rx="40" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="510" y="23" width="80" height="22" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="550" cy="45" rx="40" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="510" y1="23" x2="510" y2="45" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="590" y1="23" x2="590" y2="45" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="550" y="38" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Key DB
</text>

  

  
<line x1="430" y1="155" x2="520" y2="155" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<ellipse cx="575" cy="145" rx="45" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="2">
</ellipse>

  
<rect x="530" y="145" width="90" height="30" fill="#ff5722" stroke="#d84315" stroke-width="2">
</rect>

  
<ellipse cx="575" cy="175" rx="45" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="2">
</ellipse>

  
<line x1="530" y1="145" x2="530" y2="175" stroke="#d84315" stroke-width="2">
</line>

  
<line x1="620" y1="145" x2="620" y2="175" stroke="#d84315" stroke-width="2">
</line>

  
<text x="575" y="164" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
URL DB
</text>

  

  
<line x1="370" y1="185" x2="370" y2="250" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="370" cy="260" rx="45" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="325" y="260" width="90" height="25" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="370" cy="285" rx="45" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="325" y1="260" x2="325" y2="285" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="415" y1="260" x2="415" y2="285" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="370" y="277" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Cache (Redis)
</text>


</svg>

</details>


> **
Example 1: Auto-generated short code


Alice submits **POST /api/v1/shorten** with `{long_url: "https://www.example.com/very/long/path?param=value"}`. The API Gateway authenticates the request and routes to the **URL Shortener Service**. The service calls the **Key Generation Service (KGS)** to get a pre-generated unique 7-character key (e.g., "aB3xY9k"). The KGS maintains a pool of pre-generated keys in the **Key DB** — it marks the key as "used" atomically. The Shortener Service writes the mapping `{short_code: "aB3xY9k", long_url: "https://...", user_id: alice, created_at, expires_at: null}` to the **URL DB** and populates the **Cache** (Redis). Returns `{short_url: "https://tinyurl.com/aB3xY9k"}` to Alice.


> **
Example 2: Custom alias


Bob submits **POST /api/v1/shorten** with `{long_url: "https://mybusiness.com", custom_alias: "my-biz"}`. The Shortener Service checks if "my-biz" is already taken in the URL DB. It's not, so it creates the mapping and returns `{short_url: "https://tinyurl.com/my-biz"}`. If "my-biz" were already taken, a 409 Conflict error would be returned.


### Deep Dive: Components


> **
Key Generation Service (KGS)


Pre-generates unique 7-character codes using base62 encoding (a-z, A-Z, 0-9). 62^7 = 3.5 trillion possible codes — enough for centuries of usage. The KGS pre-generates keys in batches (e.g., 1M at a time) and stores them in the Key DB with a `used` flag. When the Shortener Service requests a key, KGS pops one from the pool and marks it used.


**Why KGS over hash-based approach:** Hashing (e.g., MD5/SHA256 of long URL, truncated to 7 chars) can produce collisions. KGS guarantees uniqueness without collision checking. KGS also decouples key generation from the main service, enabling horizontal scaling.


**Concurrency:** Each KGS instance pre-loads a batch of unused keys into memory. Multiple instances can serve keys in parallel without conflicts because each instance has its own batch.


> **
URL Shortener Service


**Endpoints:**


  - `POST /api/v1/shorten` – Input: `{long_url, custom_alias?, expires_at?}`. Output: `{short_url, short_code, created_at}`.

  - `GET /{short_code}` – Redirect endpoint (see Flow 2).

  - `GET /api/v1/stats/{short_code}` – Analytics endpoint.

  - `DELETE /api/v1/urls/{short_code}` – Delete a short URL.


## Flow 2: URL Redirect


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a2" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="100" width="80" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="50" y="130" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
User
</text>

  
<line x1="90" y1="125" x2="170" y2="125" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<text x="130" y="115" text-anchor="middle" fill="#555" font-size="9">
GET /aB3xY9k
</text>

  
<rect x="170" y="100" width="80" height="50" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="210" y="123" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Load
</text>

  
<text x="210" y="135" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Balancer
</text>

  
<line x1="250" y1="125" x2="330" y2="125" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<rect x="330" y="100" width="110" height="50" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="385" y="130" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
URL Service
</text>

  

  
<line x1="440" y1="115" x2="520" y2="70" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="500" y="80" text-anchor="middle" fill="#555" font-size="8">
1. Cache lookup
</text>

  
<ellipse cx="575" cy="58" rx="45" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="530" y="58" width="90" height="22" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="575" cy="80" rx="45" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="530" y1="58" x2="530" y2="80" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="620" y1="58" x2="620" y2="80" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="575" y="73" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Redis Cache
</text>

  

  
<line x1="440" y1="135" x2="520" y2="175" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="500" y="170" text-anchor="middle" fill="#555" font-size="8">
2. DB (cache miss)
</text>

  
<ellipse cx="575" cy="170" rx="45" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="530" y="170" width="90" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="575" cy="195" rx="45" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="530" y1="170" x2="530" y2="195" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="620" y1="170" x2="620" y2="195" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="575" y="187" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
URL DB
</text>

  

  
<line x1="330" y1="150" x2="90" y2="175" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="210" y="178" text-anchor="middle" fill="#555" font-size="9">
HTTP 301 → long_url
</text>

  

  
<line x1="385" y1="150" x2="385" y2="230" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a2)">
</line>

  
<rect x="330" y="230" width="110" height="35" rx="6" fill="#9c27b0" stroke="#6a1b9a" stroke-width="1.5">
</rect>

  
<text x="385" y="252" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Analytics Queue
</text>


</svg>

</details>


> **
Example 1: Cache hit


A user clicks `https://tinyurl.com/aB3xY9k`. The browser sends **GET /aB3xY9k** to the Load Balancer → **URL Service**. The service looks up "aB3xY9k" in the **Redis Cache** — cache hit! Returns the long URL. The service responds with **HTTP 301 Moved Permanently** with `Location: https://www.example.com/very/long/path?param=value`. The browser redirects. Asynchronously, a click event is published to the **Analytics Queue**.


> **
Example 2: Cache miss


The short code "xYz987" hasn't been accessed recently and was evicted from cache. The URL Service queries the **URL DB** (cache miss → DB lookup), finds the mapping, populates the Redis Cache for future requests, and returns the 301 redirect.


> **
Example 3: Expired URL


The short code "abc123" was set to expire on 2024-01-01. A user visits it on 2024-03-15. The URL Service finds the record but checks `expires_at < now()` → expired. Returns **HTTP 410 Gone** with a friendly error page.


> **
HTTP 301 vs. 302


**301 (Moved Permanently):** Browser caches the redirect. Subsequent visits to the short URL don't hit our server — faster for the user, less server load. But we lose analytics on repeat visits.


**302 (Found/Temporary):** Browser doesn't cache. Every visit hits our server — enables accurate click counting. Slightly slower for repeat visits.


**Our choice:** Use **301** by default for speed and reduced load. For URLs with analytics tracking enabled, use **302** to capture every click.


## Overall Combined Flow


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 380" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a3" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="150" width="70" height="45" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="45" y="177" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Client
</text>

  
<line x1="80" y1="172" x2="130" y2="172" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="130" y="150" width="70" height="45" rx="6" fill="#ff9800" stroke="#f57c00" stroke-width="1.5">
</rect>

  
<text x="165" y="170" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Load
</text>

  
<text x="165" y="182" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Balancer
</text>

  
<line x1="200" y1="172" x2="250" y2="172" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="250" y="145" width="100" height="55" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="300" y="170" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
URL
</text>

  
<text x="300" y="183" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Service
</text>

  

  
<line x1="350" y1="162" x2="420" y2="110" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<ellipse cx="470" cy="100" rx="40" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="430" y="100" width="80" height="20" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="470" cy="120" rx="40" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="430" y1="100" x2="430" y2="120" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="510" y1="100" x2="510" y2="120" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="470" y="114" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Redis
</text>

  

  
<line x1="350" y1="180" x2="420" y2="230" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<ellipse cx="470" cy="225" rx="40" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="430" y="225" width="80" height="22" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="470" cy="247" rx="40" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="430" y1="225" x2="430" y2="247" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="510" y1="225" x2="510" y2="247" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="470" y="240" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
URL DB
</text>

  

  
<line x1="300" y1="145" x2="300" y2="70" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="255" y="25" width="90" height="45" rx="6" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="300" y="52" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
KGS
</text>

  
<line x1="345" y1="47" x2="400" y2="47" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<ellipse cx="440" cy="40" rx="30" ry="8" fill="#ff5722" stroke="#d84315" stroke-width="1">
</ellipse>

  
<rect x="410" y="40" width="60" height="16" fill="#ff5722" stroke="#d84315" stroke-width="1">
</rect>

  
<ellipse cx="440" cy="56" rx="30" ry="8" fill="#e64a19" stroke="#d84315" stroke-width="1">
</ellipse>

  
<line x1="410" y1="40" x2="410" y2="56" stroke="#d84315" stroke-width="1">
</line>

  
<line x1="470" y1="40" x2="470" y2="56" stroke="#d84315" stroke-width="1">
</line>

  
<text x="440" y="52" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
Key DB
</text>

  

  
<line x1="300" y1="200" x2="300" y2="290" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a3)">
</line>

  
<rect x="255" y="290" width="90" height="35" rx="6" fill="#9c27b0" stroke="#6a1b9a" stroke-width="1.5">
</rect>

  
<text x="300" y="312" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Analytics Q
</text>

  
<line x1="345" y1="307" x2="410" y2="307" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="410" y="290" width="90" height="35" rx="6" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="455" y="312" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Analytics Svc
</text>

  
<line x1="500" y1="307" x2="560" y2="307" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<ellipse cx="605" cy="300" rx="35" ry="8" fill="#ff5722" stroke="#d84315" stroke-width="1">
</ellipse>

  
<rect x="570" y="300" width="70" height="16" fill="#ff5722" stroke="#d84315" stroke-width="1">
</rect>

  
<ellipse cx="605" cy="316" rx="35" ry="8" fill="#e64a19" stroke="#d84315" stroke-width="1">
</ellipse>

  
<line x1="570" y1="300" x2="570" y2="316" stroke="#d84315" stroke-width="1">
</line>

  
<line x1="640" y1="300" x2="640" y2="316" stroke="#d84315" stroke-width="1">
</line>

  
<text x="605" y="312" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
Analytics DB
</text>


</svg>

</details>


> **
Combined Example


**Create:** User POSTs long URL → LB → URL Service → KGS provides unique key "aB3xY9k" → saved to URL DB + Redis Cache → returns short URL.  

**Redirect:** User GETs /aB3xY9k → LB → URL Service → Redis cache hit → HTTP 301 redirect to long URL. Click event → Analytics Queue → Analytics Service → Analytics DB.


## Database Schema


### NoSQL Table (DynamoDB/Cassandra)


1. urls

  
  
  
  
  
  
  


| Field       | Type       | Key   | Description                      |
|-------------|------------|-------|----------------------------------|
| short_code  | VARCHAR(7) | `PK`  | The 7-char unique short code     |
| long_url    | TEXT       |       | Original URL (up to 2048 chars)  |
| user_id     | BIGINT     | `IDX` | Creator (nullable for anonymous) |
| created_at  | TIMESTAMP  |       | Creation time                    |
| expires_at  | TIMESTAMP  |       | Expiration time (nullable)       |
| click_count | COUNTER    |       | Total clicks (denormalized)      |


**Why NoSQL:** This is a simple key-value lookup (short_code → long_url). No joins, no complex queries. NoSQL provides low-latency reads and easy horizontal scaling. Cassandra or DynamoDB both work well.


**Index:** Hash index on `short_code` (partition key) for O(1) lookups. Secondary hash index on `user_id` for "get all URLs created by user X" queries (used in the dashboard).


**Sharding:** Auto-sharded by `short_code` (partition key) using consistent hashing. Distributes evenly due to random nature of generated codes.


**Read trigger:** Every redirect (GET /{short_code}). **Write trigger:** URL creation (POST /shorten).


2. keys (Key Generation)

  
  
  


| Field     | Type       | Key   | Description                       |
|-----------|------------|-------|-----------------------------------|
| key_value | VARCHAR(7) | `PK`  | Pre-generated short code          |
| is_used   | BOOLEAN    | `IDX` | Whether the key has been assigned |


Stored in a simple key-value store. KGS instances pre-load batches of unused keys into memory.


3. analytics (Time-series)

  
  
  
  
  
  
  


| Field      | Type       | Key  | Description          |
|------------|------------|------|----------------------|
| short_code | VARCHAR(7) | `PK` | The short code       |
| timestamp  | TIMESTAMP  | `PK` | Click timestamp      |
| ip_address | TEXT       |      | Client IP            |
| user_agent | TEXT       |      | Browser/device info  |
| referrer   | TEXT       |      | HTTP referrer        |
| country    | TEXT       |      | Geo-resolved country |


## Cache Deep Dive


> **
Redis Cache


**Purpose:** Cache the short_code → long_url mapping for fast redirect lookups.


**Caching strategy:** **Write-through + Read-through.** On URL creation: write to both DB and Redis. On redirect: check Redis first; on miss, query DB and populate Redis.


**Eviction policy:** **LRU (Least Recently Used).** Popular URLs stay cached; rarely accessed URLs get evicted. This is optimal because URL access follows a power-law distribution — a small percentage of URLs account for the majority of traffic.


**Expiration policy:** TTL = 24 hours. Refreshed on each access (TTL reset on read). Expired URLs in the DB are not cached.


**Cache size:** With 20% of URLs accounting for 80% of traffic, caching the top 20% (~200M URLs × 1KB each = ~200GB) provides excellent hit rates (~95%).


> **
CDN Consideration


CDN is **appropriate for the redirect** endpoint. Configure CDN (e.g., CloudFront) to cache 301 redirects at edge locations. When a user in Tokyo clicks a short URL, the CDN edge in Tokyo responds directly with the cached redirect without hitting the origin server. This reduces redirect latency from ~50ms to ~5ms.


**CDN TTL:** 1 hour for 301 redirects. Invalidate CDN cache when a URL is deleted or updated.


## Scaling Considerations


### Load Balancers


  - **L7 Load Balancer** in front of URL Service instances. Round-robin distribution for stateless HTTP requests.

  - For redirect endpoint (GET), consider **consistent hashing** on the short_code to improve local caching on URL Service instances.


### Horizontal Scaling


  - URL Service: Stateless, add instances behind LB.

  - Redis: Cluster mode, shard by short_code hash slot.

  - Cassandra/DynamoDB: Add nodes, auto-rebalance.

  - KGS: Multiple instances, each pre-loads its own batch of unused keys.


## Tradeoffs and Deep Dives


> **
KGS vs. Hash-based Key Generation


**KGS (chosen):** Guarantees uniqueness without collision checking. Slight overhead of maintaining pre-generated keys. If KGS goes down, the pool of in-memory keys can serve requests until it recovers.


**Hashing:** MD5/SHA256 of long URL, take first 7 chars of base62-encoded hash. Simpler but collision-prone. Must check DB for uniqueness and retry with a different seed on collision. At scale (1B+ URLs), collision rate increases.


> **
Base62 vs. Base64


Base62 (a-z, A-Z, 0-9) produces URL-safe characters. Base64 includes +, /, = which must be URL-encoded. We chose base62 for cleaner URLs. The tradeoff is slightly longer codes for the same keyspace, but 62^7 = 3.5T is more than sufficient.


## Alternative Approaches


Alternative 1: Counter-based IDs


Use an auto-incrementing counter, convert to base62. Simple and unique. Not chosen because: (1) sequential IDs are predictable (security concern — users can enumerate URLs), (2) requires a single counter or distributed counter (coordination overhead).


Alternative 2: UUID


Use a UUID (128-bit), encode in base62 (22 chars). Guaranteed unique. Not chosen because the resulting short URL is too long (defeats the purpose of URL shortening).


Alternative 3: Snowflake ID


64-bit timestamp-based ID, base62 encoded (~11 chars). Time-sortable and unique. Viable alternative, but still produces longer codes than our 7-char target. Could work if we accept slightly longer URLs.


## Additional Information


### URL Deduplication


If Alice shortens the same long URL twice, should she get the same short code? Two approaches: (1) Always generate a new short code (simpler, allows per-link analytics). (2) Check if the long URL already exists and return the existing short code (saves storage, but requires a secondary index on long_url — expensive for long strings). We choose option (1) for simplicity.


### Abuse Prevention


Short URLs can be used for phishing. Mitigations: (1) Rate limit URL creation per user/IP. (2) Check long URLs against a phishing/malware blocklist (e.g., Google Safe Browsing API) before shortening. (3) Show a preview/interstitial page before redirecting for suspicious URLs.


### Cleanup Job


A periodic background job scans for expired URLs (where `expires_at < now()`) and soft-deletes them. This frees up storage and eventually returns the short codes to the key pool for reuse (after a quarantine period to avoid confusion).