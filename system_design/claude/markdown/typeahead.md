# System Design: Typeahead / Autocomplete


## Functional Requirements


- As a user types, suggest top-N completions (5-10 results) in real-time

- Suggestions ranked by popularity/relevance (frequency of past queries)

- Support for personalized suggestions based on user's search history

- Filter offensive/inappropriate suggestions

- Update suggestion corpus based on trending queries


## Non-Functional Requirements


- **Ultra-low Latency:** <50ms response time (must feel instantaneous as user types)

- **High Availability:** 99.99% — search box unusable without typeahead

- **Scalability:** Billions of queries/day, millions of unique suggestions

- **Freshness:** Trending topics appear within minutes (e.g., breaking news)

- **Read-heavy:** Read:Write ratio ~1000:1 (every keystroke triggers a read)


## Flow 1: Serving Typeahead Suggestions (Read Path)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 400" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#00aaff">
</polygon>
</marker>
</defs>


<rect x="20" y="160" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="80" y="188" text-anchor="middle" fill="white" font-size="12">
User Client
</text>
<text x="80" y="205" text-anchor="middle" fill="white" font-size="11">
(Browser)
</text>


<rect x="190" y="160" width="130" height="60" rx="10" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="255" y="188" text-anchor="middle" fill="white" font-size="12">
API Gateway /
</text>
<text x="255" y="205" text-anchor="middle" fill="white" font-size="12">
CDN Edge
</text>


<rect x="380" y="160" width="150" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="455" y="188" text-anchor="middle" fill="white" font-size="12">
Typeahead Service
</text>
<text x="455" y="205" text-anchor="middle" fill="white" font-size="11">
(Trie in Memory)
</text>


<rect x="600" y="100" width="140" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="670" y="130" text-anchor="middle" fill="white" font-size="12">
Redis Cache
</text>


<rect x="600" y="220" width="140" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="670" y="248" text-anchor="middle" fill="white" font-size="11">
Personalization
</text>
<text x="670" y="262" text-anchor="middle" fill="white" font-size="10">
Service
</text>


<rect x="600" y="320" width="140" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="670" y="350" text-anchor="middle" fill="white" font-size="12">
Filter Service
</text>


<rect x="830" y="160" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="900" y="188" text-anchor="middle" fill="white" font-size="11">
Ranking/Blending
</text>


<line x1="140" y1="190" x2="188" y2="190" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="320" y1="190" x2="378" y2="190" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="530" y1="175" x2="598" y2="130" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="530" y1="195" x2="598" y2="240" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="530" y1="210" x2="598" y2="340" stroke="#00aaff" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="740" y1="125" x2="828" y2="175" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="740" y1="245" x2="828" y2="190" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<text x="520" y="30" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Typeahead: Serving Suggestions (Read Path)
</text>


</svg>

</details>


### Step-by-Step


1. **User types "fac"** → client debounces (50ms) → sends `GET /v1/suggestions?q=fac&limit=10`

2. **CDN edge check:** Popular prefixes (e.g., "fac") cached at CDN edge. If hit, return immediately (~5ms)

3. **Typeahead Service:** Traverses in-memory Trie to node "f" → "a" → "c", collects top-K suggestions stored at each node

4. **Redis cache:** Prefix → top-10 suggestions cached. Reduces Trie traversal for hot prefixes

5. **Personalization:** If user logged in, blend user's recent searches + global suggestions (e.g., user searched "facebook marketplace" recently → boost it)

6. **Filter:** Remove blocked/offensive terms from results

7. **Ranking/Blending:** Merge global + personalized results, rank by weighted score, return top 10


> **
**Example:** User types "fac" → debounce 50ms → request hits CDN edge (cache miss) → Typeahead Service traverses Trie → finds ["facebook", "facebook login", "face mask", "facebook marketplace", "factory reset", "facetime", "facebook messenger", "facial recognition", "facts", "face swap"] → Filter removes none → Personalization boosts "facebook marketplace" (user searched it yesterday) → Returns ordered: ["facebook", "facebook marketplace", "facebook login", "facetime", "face mask", ...]. Total latency: ~30ms.


### Deep Dive: Trie Data Structure


- **Structure:** Each node represents a character. Path from root to node = prefix. Each node stores top-K suggestions (precomputed) for that prefix

- **Memory:** ~26 children per node (English alphabet + digits + space). For 10M unique queries, Trie uses ~10-50GB RAM depending on implementation

- **Top-K at each node:** Instead of traversing all leaf descendants (O(n)), store precomputed top-10 suggestions at each prefix node. Lookup is O(prefix_length) — typically O(3-5)

- **Compressed Trie (Radix Tree):** Merge single-child chains. "facebook" takes 1 node instead of 8. Reduces memory by ~60%


### Deep Dive: Client-Side Optimization


- **Debouncing:** Don't send request on every keystroke. Wait 50-100ms after last keystroke

- **Client-side caching:** Cache prefix → results in browser. If user types "fac" then "face", the "fac" results can be filtered client-side for "face" without a network request

- **Prefix reuse:** If "fac" results include "facebook", and user types "face", client filters locally first. Only sends network request if local results are insufficient


## Flow 2: Updating Suggestion Corpus (Write Path)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 380" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ffaa00">
</polygon>
</marker>
</defs>


<rect x="20" y="150" width="130" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="85" y="178" text-anchor="middle" fill="white" font-size="12">
Search Queries
</text>
<text x="85" y="195" text-anchor="middle" fill="white" font-size="11">
(user searches)
</text>


<rect x="200" y="150" width="140" height="60" rx="10" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="270" y="178" text-anchor="middle" fill="white" font-size="12">
Kafka Stream
</text>
<text x="270" y="195" text-anchor="middle" fill="white" font-size="11">
(Query Events)
</text>


<rect x="400" y="80" width="150" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="475" y="108" text-anchor="middle" fill="white" font-size="12">
Aggregation Svc
</text>
<text x="475" y="122" text-anchor="middle" fill="white" font-size="10">
(Flink/Spark)
</text>


<rect x="400" y="220" width="150" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="475" y="248" text-anchor="middle" fill="white" font-size="11">
Trending Detector
</text>
<text x="475" y="262" text-anchor="middle" fill="white" font-size="10">
(Count-Min Sketch)
</text>


<ellipse cx="700" cy="105" rx="75" ry="30" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="700" y="110" text-anchor="middle" fill="white" font-size="12">
Frequency DB
</text>


<rect x="620" y="220" width="160" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="700" y="248" text-anchor="middle" fill="white" font-size="11">
Trie Builder (Offline)
</text>


<rect x="860" y="150" width="150" height="60" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="935" y="178" text-anchor="middle" fill="white" font-size="12">
Typeahead Servers
</text>
<text x="935" y="195" text-anchor="middle" fill="white" font-size="11">
(Hot-swap Trie)
</text>


<line x1="150" y1="180" x2="198" y2="180" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="340" y1="168" x2="398" y2="108" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="340" y1="192" x2="398" y2="242" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="550" y1="105" x2="623" y2="105" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="550" y1="250" x2="618" y2="250" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="700" y1="135" x2="700" y2="218" stroke="#ff6b6b" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow2)">
</line>


<line x1="780" y1="240" x2="858" y2="185" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<text x="520" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Typeahead: Updating Suggestions (Write Path)
</text>


</svg>

</details>


### Step-by-Step


1. **Search queries** published to Kafka as events: `{ query: "facebook marketplace", timestamp: ..., user_id: ... }`

2. **Aggregation Service (Flink):** Counts query frequencies in tumbling windows (5-minute windows). Outputs: `{ query: "facebook marketplace", count: 15420, window: "2024-01-15T10:00-10:05" }`

3. **Trending Detector:** Uses Count-Min Sketch to identify queries with sudden frequency spikes (e.g., "super bowl" → 100x normal rate)

4. **Frequency DB:** Stores historical query frequencies. Aggregation service merges new counts with existing. Exponential decay applied (recent queries weighted more)

5. **Trie Builder (offline, every 15 min):** Reads top-N queries from Frequency DB, builds new compressed Trie with precomputed top-K per node

6. **Hot-swap:** New Trie pushed to Typeahead Servers. Servers swap atomic pointer from old Trie to new Trie (zero-downtime update)


> **
**Example:** Breaking news: "earthquake california" trends suddenly. Within 5 minutes, Flink aggregation detects 50K searches for "earthquake california". Trending Detector flags it. Frequency DB updated. Next Trie build (15 min cycle) includes "earthquake california" with high score. All Typeahead Servers get updated Trie. Users typing "ear" now see "earthquake california" in suggestions.


### Deep Dive: Count-Min Sketch


- **Purpose:** Probabilistic data structure to count frequencies of items in a stream using sub-linear memory

- **How it works:** 2D array of counters with d hash functions. Each item hashed d times, increment counters. Query: take minimum of d counter values (overcounts possible, undercounts impossible)

- **Memory:** ~10MB can track millions of unique queries with <1% error rate

- **Why not HashMap:** Millions of unique queries = GBs of memory. Count-Min Sketch uses constant memory regardless of cardinality


## Combined Overall Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 550" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#00aaff">
</polygon>
</marker>
</defs>


<rect x="20" y="220" width="110" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="75" y="255" text-anchor="middle" fill="white" font-size="13">
Client
</text>


<rect x="170" y="100" width="120" height="50" rx="8" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="230" y="130" text-anchor="middle" fill="white" font-size="12">
CDN Edge
</text>


<rect x="170" y="220" width="120" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="230" y="250" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>


<rect x="350" y="130" width="150" height="60" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="425" y="158" text-anchor="middle" fill="white" font-size="12">
Typeahead Svc
</text>
<text x="425" y="175" text-anchor="middle" fill="white" font-size="11">
(In-Memory Trie)
</text>


<rect x="350" y="260" width="150" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="425" y="290" text-anchor="middle" fill="white" font-size="12">
Personalization
</text>


<rect x="350" y="370" width="150" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="425" y="400" text-anchor="middle" fill="white" font-size="12">
Filter Service
</text>


<rect x="570" y="130" width="120" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="630" y="160" text-anchor="middle" fill="white" font-size="12">
Redis Cache
</text>


<rect x="570" y="260" width="120" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="630" y="290" text-anchor="middle" fill="white" font-size="12">
User History
</text>


<rect x="170" y="420" width="130" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="235" y="450" text-anchor="middle" fill="white" font-size="12">
Kafka Stream
</text>


<rect x="350" y="480" width="150" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="425" y="505" text-anchor="middle" fill="white" font-size="11">
Aggregation (Flink)
</text>


<ellipse cx="630" cy="420" rx="75" ry="30" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="630" y="425" text-anchor="middle" fill="white" font-size="12">
Frequency DB
</text>


<rect x="780" y="405" width="140" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="850" y="435" text-anchor="middle" fill="white" font-size="12">
Trie Builder
</text>


<line x1="130" y1="240" x2="168" y2="130" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="130" y1="250" x2="168" y2="245" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="290" y1="240" x2="348" y2="165" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="500" y1="160" x2="568" y2="155" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="290" y1="260" x2="348" y2="280" stroke="#00aaff" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="500" y1="285" x2="568" y2="285" stroke="#00aaff" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="130" y1="270" x2="168" y2="440" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="300" y1="445" x2="348" y2="500" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="500" y1="505" x2="558" y2="430" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="705" y1="420" x2="778" y2="425" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="850" y1="405" x2="500" y2="170" stroke="#ff6b6b" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow3)">
</line>


<text x="530" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Typeahead: Combined Architecture
</text>


<text x="200" y="70" fill="#66ff66" font-size="12">
READ PATH (green)
</text>


<text x="500" y="70" fill="#ffaa00" font-size="12">
WRITE PATH (yellow)
</text>


</svg>

</details>


> **
**Example — Combined flow:** User types "wor" → CDN cache miss → API Gateway routes to Typeahead Service → Trie lookup returns ["world cup", "world news", "wordle", "workout", "world map"] → Personalization checks user history (user searched "workout routine" before) → boosts "workout" → Final: ["workout", "world cup", "wordle", "world news", "world map"]. Meanwhile, user's completed search "workout at home" flows to Kafka → Flink aggregates → Frequency DB updates → Next Trie build incorporates updated counts.


## Database Schema


### NoSQL — Cassandra / DynamoDB (Query Frequencies)


| Table                 | Column       | Type                                 | Details              |
|-----------------------|--------------|--------------------------------------|----------------------|
| **query_frequencies** | query_prefix | TEXT                                 | `PK` (first 3 chars) |
| query_text            | TEXT         | `PK`                                 |                      |
| frequency             | BIGINT       | Decayed count (recent weighted more) |                      |
| last_updated          | TIMESTAMP    |                                      |                      |
| **trending_queries**  | time_bucket  | TEXT                                 | `PK` (hourly bucket) |
| rank                  | INT          | `PK` (ASC)                           |                      |
| query_text            | TEXT         |                                      |                      |
| spike_score           | FLOAT        | current_rate / baseline_rate         |                      |


### SQL — PostgreSQL (Blocked Terms, User History)


| Table                   | Column       | Type                   | Details |
|-------------------------|--------------|------------------------|---------|
| **blocked_terms**       | term_id      | SERIAL                 | `PK`    |
| term                    | VARCHAR(255) | `IDX`                  |         |
| reason                  | VARCHAR(50)  | offensive, legal, spam |         |
| **user_search_history** | user_id      | BIGINT                 | `PK`    |
| query_text              | VARCHAR(255) | `PK`                   |         |
| search_count            | INT          |                        |         |
| last_searched           | TIMESTAMP    | `IDX`                  |         |


Sharding Strategy


- **query_frequencies:** Partition by first 3 characters of query (prefix-based sharding). All queries starting with "fac" on same partition. Trie Builder reads partitions in parallel

- **user_search_history:** Shard by `user_id` — all history for one user co-located

- **In-memory Trie:** Each Typeahead Server holds complete Trie replica (fits in ~50GB RAM). No sharding needed — full replication for speed


### Indexes


- `blocked_terms.term` — Hash index for O(1) lookup during filtering

- `user_search_history.last_searched` — B-tree index for "recent searches" feature (ORDER BY last_searched DESC LIMIT 10)

- Trie itself acts as the primary "index" — no traditional DB indexes needed for the read path


## Cache Deep Dive


| Cache Layer    | What                                          | Strategy                        | Eviction          | TTL              |
|----------------|-----------------------------------------------|---------------------------------|-------------------|------------------|
| CDN Edge       | Popular prefix results (e.g., "fac" → top 10) | Pull-based                      | TTL-based         | 5 min            |
| Redis (L1)     | Hot prefix → suggestions mapping              | Write-through (on Trie rebuild) | LRU               | 15 min           |
| In-Memory Trie | Complete suggestion corpus                    | Full rebuild every 15 min       | N/A (replaced)    | N/A              |
| Browser Cache  | Recently fetched prefix results               | Client-side HashMap             | LRU (100 entries) | Session duration |


### CDN Deep Dive


- **What's cached:** Top ~100K most common prefixes (covers ~95% of all typeahead requests). E.g., "fac", "goo", "ama", "you" are extremely popular

- **Cache key:** `typeahead:v2:{prefix}:{locale}` (locale because suggestions differ by country)

- **TTL: 5 minutes** — balance between freshness (trending queries) and cache hit ratio

- **Cache hit ratio:** ~85-90% at CDN edge (Zipf's law — small number of prefixes dominate)

- **Invalidation:** Trending queries trigger targeted CDN invalidation for affected prefixes


## Scaling Considerations


- **Typeahead Servers:** Full Trie replicated on every server (read-only). Horizontally scale by adding more replicas. L7 load balancer distributes requests

- **Memory optimization:** Compressed Trie (radix tree) + store only top-K per node (not all descendants). ~50GB for 100M unique queries

- **Trie sharding (for very large corpora):** Shard by prefix range. Servers A-M handle "a"-"m", Servers N-Z handle "n"-"z". API Gateway routes by first character. Doubles available memory

- **Geographic replication:** Trie replicas in every region. Trie Builder in central region pushes to all edges via S3 + notification

- **Kafka partitioning:** Query events partitioned by query hash. Flink aggregation parallelized across partitions

- **Debounce + client caching reduces server load by ~60%** (most keystrokes resolved client-side)


## Tradeoffs & Deep Dives


> **
✅ In-Memory Trie
- O(prefix_length) lookup — ~10μs per query
- Pre-computed top-K eliminates sorting at query time
- No disk I/O, no network call to DB


❌ In-Memory Trie Downsides
- High memory cost (50GB per server × N replicas)
- Cold start: loading Trie from disk takes minutes
- Staleness: 15-min rebuild cycle means trending queries delayed


> **
✅ Periodic Rebuild
- Simple: batch process, atomic swap
- No real-time Trie mutations (avoid concurrent modification)
- Consistent state across all servers after swap


❌ Alternative: Real-time Trie Update
- Lower staleness (seconds vs minutes)
- But: concurrent mutations require locking, complex consistency
- Difficult to maintain sorted top-K during live updates


## Alternative Approaches


- **Elasticsearch prefix queries:** Use `prefix` or `completion suggester` instead of custom Trie. Pros: built-in fuzzy matching, scoring, easy to operate. Cons: higher latency (~5-10ms vs ~1ms Trie), more resource-heavy, less control over ranking

- **Ternary Search Tree:** More memory-efficient than standard Trie (pointers: left/equal/right). Trades slightly slower lookup for ~30% memory savings

- **Bloom filter pre-check:** Before Trie lookup, check if prefix exists in Bloom filter. Quickly reject nonsensical prefixes (e.g., "xzqw") without Trie traversal

- **ML-based ranking:** Instead of frequency-based ranking, use a small neural network (BERT-tiny) to rank suggestions based on context (time of day, user demographics, location). Higher relevance but adds ~10ms latency


## Additional Information


- **Spelling correction:** "facebok" → "facebook". Edit distance (Levenshtein) computed against known queries. Stored as aliases in the Trie: "facebok" node points to "facebook" suggestions

- **Multi-language support:** Separate Tries per locale. Japanese/Chinese use word-level tokenization instead of character-level Trie. Unicode normalization (NFC) applied to all inputs

- **Privacy:** Individual search queries anonymized before aggregation. Only aggregate counts stored. User search history encrypted and deletable (GDPR right to erasure)

- **A/B testing:** Different ranking models tested by routing % of traffic to experimental Typeahead servers with different Tries or ranking weights