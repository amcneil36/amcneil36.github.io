# System Design: Pastebin


## Functional Requirements


  1. **Create paste** – Users can upload text content and get a unique URL to share it.

  2. **Read paste** – Anyone with the URL can view the paste content.

  3. **Expiration** – Pastes can have configurable expiration (10 min, 1 hour, 1 day, 1 week, 1 month, never).

  4. **Syntax highlighting** – Pastes can specify a language for syntax highlighting.

  5. **User accounts (optional)** – Registered users can manage their pastes (edit, delete, view history).

  6. **Private pastes** – Pastes can be public (listed) or unlisted (only accessible via URL).

  7. **Custom URL** – Users can optionally specify a custom URL suffix.


## Non-Functional Requirements


  1. **High availability** – 99.9% uptime.

  2. **Low latency** – Paste read in <100ms, creation in <500ms.

  3. **Scalability** – Handle 5M pastes/day, 25M reads/day (5:1 read-to-write).

  4. **Storage** – Pastes up to 10MB in size; average ~10KB.

  5. **Durability** – Pastes must not be lost before expiration.


## Flow 1: Creating a Paste


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 320" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a1" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="120" width="80" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="50" y="150" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
User
</text>

  
<line x1="90" y1="145" x2="160" y2="145" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<text x="125" y="135" text-anchor="middle" fill="#555" font-size="9">
POST /paste
</text>

  
<rect x="160" y="120" width="80" height="50" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="200" y="143" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
API
</text>

  
<text x="200" y="155" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
GW + LB
</text>

  
<line x1="240" y1="145" x2="310" y2="145" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="310" y="115" width="110" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="365" y="143" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Paste
</text>

  
<text x="365" y="158" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Service
</text>

  

  
<line x1="420" y1="135" x2="510" y2="85" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<text x="480" y="95" text-anchor="middle" fill="#555" font-size="8">
Store content
</text>

  
<rect x="510" y="60" width="100" height="45" rx="8" fill="#795548" stroke="#4e342e" stroke-width="2">
</rect>

  
<text x="560" y="80" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Object Store
</text>

  
<text x="560" y="93" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
(S3)
</text>

  

  
<line x1="420" y1="155" x2="510" y2="195" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<text x="480" y="190" text-anchor="middle" fill="#555" font-size="8">
Store metadata
</text>

  
<ellipse cx="565" cy="195" rx="45" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="520" y="195" width="90" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="565" cy="220" rx="45" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="520" y1="195" x2="520" y2="220" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="610" y1="195" x2="610" y2="220" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="565" y="212" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Metadata DB
</text>

  

  
<line x1="365" y1="115" x2="365" y2="50" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="315" y="5" width="100" height="45" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="365" y="32" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
KGS
</text>


</svg>

</details>


> **
Example: Creating a code paste


Alice pastes a Python code snippet (500 bytes), selects language=Python, expiration=1 week. Client sends **POST /api/v1/paste** with `{content: "def hello()...", language: "python", expires_in: "7d"}`. The API Gateway routes to **Paste Service**. It obtains a unique 8-char key "xK9mL2pQ" from **KGS**, stores the content in **S3** at key `pastes/xK9mL2pQ`, and writes metadata to **Metadata DB** (MySQL): `{paste_id: "xK9mL2pQ", user_id: alice, language: "python", size: 500, created_at: now, expires_at: now+7d, visibility: "unlisted"}`. Returns `{url: "https://pastebin.com/xK9mL2pQ"}`.


## Flow 2: Reading a Paste


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 250" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a2" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="90" width="80" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="50" y="120" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
User
</text>

  
<line x1="90" y1="115" x2="160" y2="115" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<text x="125" y="105" text-anchor="middle" fill="#555" font-size="9">
GET /xK9mL2pQ
</text>

  
<rect x="160" y="90" width="80" height="50" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="200" y="113" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
LB
</text>

  
<line x1="240" y1="115" x2="310" y2="115" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<rect x="310" y="90" width="100" height="50" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="360" y="120" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Paste Svc
</text>

  

  
<line x1="410" y1="105" x2="490" y2="55" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="465" y="68" text-anchor="middle" fill="#555" font-size="8">
1. Cache?
</text>

  
<ellipse cx="540" cy="45" rx="40" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="500" y="45" width="80" height="20" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="540" cy="65" rx="40" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="500" y1="45" x2="500" y2="65" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="580" y1="45" x2="580" y2="65" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="540" y="59" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Redis
</text>

  

  
<line x1="410" y1="125" x2="490" y2="165" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="465" y="158" text-anchor="middle" fill="#555" font-size="8">
2. S3
</text>

  
<rect x="490" y="145" width="90" height="40" rx="6" fill="#795548" stroke="#4e342e" stroke-width="1.5">
</rect>

  
<text x="535" y="170" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
S3
</text>

  

  
<line x1="360" y1="140" x2="360" y2="200" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a2)">
</line>

  
<text x="360" y="190" text-anchor="start" fill="#555" font-size="8">
 Check expiry
</text>

  
<ellipse cx="360" cy="210" rx="40" ry="8" fill="#ff5722" stroke="#d84315" stroke-width="1">
</ellipse>

  
<rect x="320" y="210" width="80" height="16" fill="#ff5722" stroke="#d84315" stroke-width="1">
</rect>

  
<ellipse cx="360" cy="226" rx="40" ry="8" fill="#e64a19" stroke="#d84315" stroke-width="1">
</ellipse>

  
<line x1="320" y1="210" x2="320" y2="226" stroke="#d84315" stroke-width="1">
</line>

  
<line x1="400" y1="210" x2="400" y2="226" stroke="#d84315" stroke-width="1">
</line>

  
<text x="360" y="222" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
Meta DB
</text>


</svg>

</details>


> **
Example: Reading a paste (cache hit)


Bob visits `pastebin.com/xK9mL2pQ`. Client sends **GET /api/v1/paste/xK9mL2pQ**. Paste Service checks **Redis Cache** — hit! Returns the paste content with metadata (language, created_at). The HTML page renders with Python syntax highlighting.


> **
Example: Expired paste


Carol visits a paste that expired 2 days ago. Paste Service checks metadata — `expires_at < now()`. Returns **HTTP 404** with "This paste has expired."


## Overall Combined Flow


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 300" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a3" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="120" width="70" height="45" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="45" y="147" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Client
</text>

  
<line x1="80" y1="142" x2="130" y2="142" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="130" y="120" width="65" height="45" rx="6" fill="#ff9800" stroke="#f57c00" stroke-width="1.5">
</rect>

  
<text x="162" y="147" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
LB
</text>

  
<line x1="195" y1="142" x2="240" y2="142" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="240" y="115" width="95" height="55" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="287" y="147" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Paste Svc
</text>

  
<line x1="335" y1="132" x2="400" y2="80" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<ellipse cx="440" cy="70" rx="35" ry="8" fill="#009688" stroke="#00695c" stroke-width="1">
</ellipse>

  
<rect x="405" y="70" width="70" height="16" fill="#009688" stroke="#00695c" stroke-width="1">
</rect>

  
<ellipse cx="440" cy="86" rx="35" ry="8" fill="#00796b" stroke="#00695c" stroke-width="1">
</ellipse>

  
<line x1="405" y1="70" x2="405" y2="86" stroke="#00695c" stroke-width="1">
</line>

  
<line x1="475" y1="70" x2="475" y2="86" stroke="#00695c" stroke-width="1">
</line>

  
<text x="440" y="82" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
Redis
</text>

  
<line x1="335" y1="152" x2="400" y2="195" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="400" y="180" width="80" height="35" rx="6" fill="#795548" stroke="#4e342e" stroke-width="1.5">
</rect>

  
<text x="440" y="202" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
S3
</text>

  
<line x1="335" y1="142" x2="400" y2="142" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<ellipse cx="440" cy="135" rx="40" ry="8" fill="#ff5722" stroke="#d84315" stroke-width="1">
</ellipse>

  
<rect x="400" y="135" width="80" height="16" fill="#ff5722" stroke="#d84315" stroke-width="1">
</rect>

  
<ellipse cx="440" cy="151" rx="40" ry="8" fill="#e64a19" stroke="#d84315" stroke-width="1">
</ellipse>

  
<line x1="400" y1="135" x2="400" y2="151" stroke="#d84315" stroke-width="1">
</line>

  
<line x1="480" y1="135" x2="480" y2="151" stroke="#d84315" stroke-width="1">
</line>

  
<text x="440" y="147" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
Meta DB
</text>

  
<line x1="287" y1="115" x2="287" y2="60" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="250" y="20" width="75" height="40" rx="6" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="287" y="44" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
KGS
</text>

  

  
<rect x="550" y="130" width="90" height="40" rx="6" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="595" y="155" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Cleanup Job
</text>

  
<line x1="550" y1="150" x2="480" y2="145" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a3)">
</line>

  
<line x1="550" y1="155" x2="480" y2="195" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a3)">
</line>


</svg>

</details>


> **
Combined Example


**Write:** User POSTs content → LB → Paste Svc → KGS gets unique key → content stored in S3 → metadata in MySQL → cached in Redis → return URL.  

**Read:** User GETs /key → LB → Paste Svc → Redis hit → return content. On cache miss: fetch from S3, check metadata for expiry → populate cache → return.  

**Cleanup:** Background Cleanup Job scans Metadata DB for expired pastes, deletes from S3 and Metadata DB.


## Database Schema


### SQL Table (MySQL)

1. paste_metadata

  
  
  
  
  
  
  
  
  
  


| Field      | Type                      | Key         | Description                          |
|------------|---------------------------|-------------|--------------------------------------|
| paste_id   | VARCHAR(8)                | `PK`        | Unique paste key                     |
| user_id    | BIGINT                    | `FK`  `IDX` | Creator (nullable)                   |
| s3_key     | TEXT                      |             | S3 object key for content            |
| language   | VARCHAR(30)               |             | Syntax highlighting language         |
| size_bytes | INT                       |             | Content size in bytes                |
| visibility | ENUM('public','unlisted') |             | Public or unlisted                   |
| created_at | TIMESTAMP                 |             | Creation time                        |
| expires_at | TIMESTAMP                 | `IDX`       | Expiration time (nullable for never) |
| view_count | INT DEFAULT 0             |             | Denormalized view count              |


**Why SQL:** Metadata is small, relational (users → pastes), and requires indexed queries (find expired pastes, list user's pastes sorted by date). MySQL handles this well with ACID guarantees.


**Index:** B-tree index on `expires_at` for the cleanup job to efficiently find expired pastes (`WHERE expires_at < NOW() AND expires_at IS NOT NULL`). B-tree is chosen because it's a range query. B-tree index on `user_id` for "my pastes" listing.


**Content stored in S3, not DB:** Paste content can be up to 10MB. Storing large blobs in MySQL is inefficient and would bloat the DB. S3 is designed for blob storage with 11 9's of durability.


**Sharding:** Hash-based sharding on `paste_id` across MySQL instances. Since paste_ids are random strings, they distribute evenly.


## Cache Deep Dive


> **
Redis Cache


**What's cached:** Paste content + metadata for recently accessed pastes. Key: `paste:{paste_id}`. Value: JSON with content and metadata.


**Caching strategy:** **Read-through with write-through.** On create: write to S3 + DB + Redis. On read: check Redis first, if miss → fetch from S3 + DB → populate Redis.


**Eviction:** LRU. ~80% of reads go to ~20% of pastes (popular code snippets shared on forums).


**Expiration:** TTL = min(paste_expiry, 24 hours). Ensures expired pastes don't linger in cache.


> **
CDN


**CDN is appropriate** for public/popular pastes. A CDN edge can cache paste content, reducing origin load. However, since pastes can be edited/deleted, CDN TTL should be short (5 minutes) with cache invalidation on update/delete.


## Scaling Considerations


  - **Load Balancer:** L7 LB with round-robin for stateless Paste Service instances.

  - **S3:** Infinitely scalable object storage. No action needed.

  - **MySQL:** Read replicas for read scaling. Sharding for write scaling.

  - **Redis:** Cluster mode for cache scaling.


## Tradeoffs and Deep Dives


> **
S3 vs. Database for Content Storage


S3 is cheaper ($0.023/GB/month), more durable (11 9's), and designed for blob storage. The tradeoff is an extra network hop to fetch content. For small pastes (<1KB), storing inline in the DB would be faster. Hybrid approach: inline small pastes in the metadata DB, use S3 for large ones. We chose S3-only for simplicity.


## Alternative Approaches


Alternative: Store everything in one DB


Use a single PostgreSQL instance with content stored as TEXT/BYTEA. Simpler architecture. Not chosen because large pastes would bloat the DB, and blob storage is S3's sweet spot.


Alternative: Content-addressable storage (deduplication)


Hash paste content and use the hash as the S3 key. Identical pastes share storage. Saves space but adds complexity (reference counting for deletion). Not worth it at Pastebin's relatively small scale.


## Additional Information


### Abuse Prevention


Rate limit paste creation: 10 pastes/minute per IP, 50/hour per account. Content scanning for malware/phishing links. CAPTCHA for anonymous paste creation.


### Cleanup Job


Runs every 5 minutes. Queries `SELECT paste_id, s3_key FROM paste_metadata WHERE expires_at < NOW() LIMIT 1000`. Deletes S3 objects and metadata rows in batches. Uses the B-tree index on `expires_at` for efficient scanning.