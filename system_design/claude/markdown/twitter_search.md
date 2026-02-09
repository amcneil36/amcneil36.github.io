# System Design: Twitter Search


## Functional Requirements


- Full-text search across all tweets (hundreds of billions of tweets)

- Real-time indexing — new tweets searchable within seconds of posting

- Search by keywords, hashtags, user mentions, cashtags ($AAPL)

- Filter by: recency, popularity, media type, language, location

- Trending topics and hashtag discovery

- User search (find users by name or handle)

- Typeahead / autocomplete suggestions


## Non-Functional Requirements


- **Latency:** Search results in <500ms

- **Freshness:** New tweets indexed within 10 seconds of creation (near real-time)

- **Scalability:** Handle billions of queries/day, index 500M+ tweets/day

- **Relevance:** Results ranked by relevance + recency + engagement

- **Availability:** 99.99% — search is a core feature


## Flow 1: Tweet Indexing (Write Path)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 380" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#1da1f2">
</polygon>
</marker>
</defs>


<rect x="20" y="150" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="85" y="178" text-anchor="middle" fill="white" font-size="12">
Tweet Service
</text>
<text x="85" y="195" text-anchor="middle" fill="white" font-size="10">
(new tweet)
</text>


<rect x="200" y="150" width="130" height="60" rx="10" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="265" y="185" text-anchor="middle" fill="white" font-size="12">
Kafka
</text>


<rect x="390" y="90" width="150" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="465" y="118" text-anchor="middle" fill="white" font-size="11">
Tokenizer / NLP
</text>
<text x="465" y="132" text-anchor="middle" fill="white" font-size="10">
(text processing)
</text>


<rect x="390" y="200" width="150" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="465" y="228" text-anchor="middle" fill="white" font-size="11">
Index Writer
</text>
<text x="465" y="242" text-anchor="middle" fill="white" font-size="10">
(inverted index)
</text>


<rect x="390" y="300" width="150" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="465" y="330" text-anchor="middle" fill="white" font-size="11">
Entity Extractor
</text>
<text x="465" y="344" text-anchor="middle" fill="white" font-size="10">
(hashtags, mentions)
</text>


<rect x="620" y="90" width="150" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="695" y="115" text-anchor="middle" fill="white" font-size="11">
Real-time Index
</text>
<text x="695" y="130" text-anchor="middle" fill="white" font-size="10">
(Earlybird/Lucene)
</text>


<ellipse cx="695" cy="225" rx="70" ry="28" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="695" y="222" text-anchor="middle" fill="white" font-size="10">
Full Index
</text>
<text x="695" y="238" text-anchor="middle" fill="white" font-size="10">
(Historical)
</text>


<rect x="850" y="140" width="130" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="915" y="170" text-anchor="middle" fill="white" font-size="12">
Trending Svc
</text>


<line x1="150" y1="180" x2="198" y2="180" stroke="#1da1f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="330" y1="168" x2="388" y2="118" stroke="#1da1f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="330" y1="190" x2="388" y2="225" stroke="#1da1f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="330" y1="195" x2="388" y2="325" stroke="#1da1f2" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="540" y1="115" x2="618" y2="115" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="540" y1="230" x2="623" y2="228" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="540" y1="325" x2="848" y2="170" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<text x="520" y="30" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Twitter Search: Indexing Pipeline
</text>


</svg>

</details>


### Step-by-Step


1. **Tweet published** → event sent to Kafka "tweets" topic

2. **Tokenizer / NLP:** Processes tweet text — tokenization, stemming, language detection, stop word removal. "I'm running fast" → tokens: ["run", "fast"]

3. **Entity Extractor:** Identifies hashtags (#tech), mentions (@elonmusk), cashtags ($AAPL), URLs, emojis. Creates structured entities

4. **Index Writer:** Builds inverted index entries: token → [tweet_id_1, tweet_id_2, ...]. Writes to both Real-time Index (in-memory, last 7 days) and Full Index (on-disk, all time)

5. **Trending Service:** Counts hashtag/topic frequency using Count-Min Sketch. Detects spikes against baseline


> **
**Example:** Tweet: "Just bought $AAPL after the earnings call! 🚀 #investing #stocks" → Tokenizer produces: ["bought", "aapl", "earnings", "call", "investing", "stocks"]. Entity Extractor: hashtags=[#investing, #stocks], cashtags=[$AAPL]. Index Writer creates entries: "aapl" → [tweet_12345], "investing" → [tweet_12345], etc. Written to Earlybird real-time index within 5 seconds. Searchable immediately.


### Deep Dive: Earlybird (Real-time Index)


- **What:** Twitter's custom real-time search engine (based on Lucene). Holds tweets from last 7 days in memory

- **Architecture:** Tweets hash-partitioned across ~200 Earlybird servers. Each server holds a shard of the index

- **Inverted index in memory:** Token → posting list (sorted tweet_ids with positions). ~50GB RAM per server

- **Updates:** New tweets indexed within seconds. Index segments merged periodically (like Lucene merge policy)

- **Why custom:** Standard Elasticsearch not fast enough for Twitter's real-time indexing latency requirements (~5 second freshness)


## Flow 2: Search Query Execution (Read Path)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 400" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ffaa00">
</polygon>
</marker>
</defs>


<rect x="20" y="160" width="110" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="75" y="195" text-anchor="middle" fill="white" font-size="13">
User
</text>


<rect x="170" y="160" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="235" y="195" text-anchor="middle" fill="white" font-size="12">
Search Service
</text>


<rect x="350" y="80" width="140" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="420" y="108" text-anchor="middle" fill="white" font-size="11">
Earlybird (Real-time)
</text>
<text x="420" y="122" text-anchor="middle" fill="white" font-size="10">
Last 7 days
</text>


<rect x="350" y="180" width="140" height="50" rx="8" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</rect>
<text x="420" y="208" text-anchor="middle" fill="white" font-size="11">
Full Index
</text>
<text x="420" y="222" text-anchor="middle" fill="white" font-size="10">
Historical
</text>


<rect x="350" y="280" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="420" y="308" text-anchor="middle" fill="white" font-size="11">
Blender
</text>
<text x="420" y="322" text-anchor="middle" fill="white" font-size="10">
(Result Merger)
</text>


<rect x="560" y="160" width="140" height="60" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="630" y="188" text-anchor="middle" fill="white" font-size="12">
Ranking Service
</text>
<text x="630" y="205" text-anchor="middle" fill="white" font-size="10">
(ML Scorer)
</text>


<rect x="770" y="80" width="130" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="835" y="108" text-anchor="middle" fill="white" font-size="11">
Tweet Hydrator
</text>


<rect x="770" y="200" width="130" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="835" y="228" text-anchor="middle" fill="white" font-size="11">
Safety Filter
</text>


<rect x="960" y="160" width="110" height="50" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="1015" y="190" text-anchor="middle" fill="white" font-size="12">
Results
</text>


<line x1="130" y1="190" x2="168" y2="190" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="300" y1="178" x2="348" y2="110" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="300" y1="195" x2="348" y2="205" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="490" y1="110" x2="490" y2="278" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="490" y1="215" x2="490" y2="278" stroke="#ffaa00" stroke-width="1.5">
</line>


<line x1="490" y1="305" x2="558" y2="195" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="700" y1="180" x2="768" y2="110" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="700" y1="200" x2="768" y2="225" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="900" y1="105" x2="958" y2="175" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<text x="550" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Twitter Search: Query Execution
</text>


</svg>

</details>


### Step-by-Step


1. **User searches "earthquake california"** → `GET /v1/search?q=earthquake+california&result_type=recent`

2. **Search Service:** Parses query, identifies tokens ["earthquake", "california"], detects filter mode (recent/top/media)

3. **Scatter-gather:** Query sent to all Earlybird shards (real-time, last 7 days) AND full index shards (historical). Each shard returns top-K local results

4. **Blender:** Merges results from all shards. Deduplicates. Selects top 500 candidates

5. **Ranking Service (ML):** Scores each tweet: `score = text_relevance * 0.3 + engagement * 0.25 + recency * 0.25 + author_authority * 0.2`

6. **Tweet Hydrator:** Fetches full tweet objects (text, author info, media, engagement counts)

7. **Safety Filter:** Removes NSFW, spam, blocked users, muted keywords from results

8. **Returns top 20 results** per page


> **
**Example:** User searches "earthquake california". Earlybird real-time shards find 2000 recent tweets containing both tokens. Full index finds 50K historical matches. Blender merges and selects top 500 by initial score. ML Ranking: breaking news tweet from @USGS (verified, 10K retweets, 2 min old) scores 0.98. Old news article from 2019 scores 0.15. Top 20 returned. Total latency: ~350ms (scatter: 100ms, rank: 50ms, hydrate: 100ms, network: 100ms).


### Deep Dive: Inverted Index


- **Structure:** Dictionary (token → posting list). Posting list = sorted array of (tweet_id, position, metadata)

- **Boolean query:** "earthquake AND california" → intersect posting lists for "earthquake" and "california". Use skip pointers for efficient intersection

- **Phrase query:** "earthquake in california" → check position offsets (earthquake at pos 0, in at pos 1, california at pos 2 in same tweet)

- **Compression:** Delta encoding + variable-byte encoding for posting lists. 4x compression ratio


## Combined Overall Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 500" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#1da1f2">
</polygon>
</marker>
</defs>


<text x="550" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Twitter Search: Combined Architecture
</text>


<rect x="20" y="200" width="100" height="50" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="70" y="230" text-anchor="middle" fill="white" font-size="12">
Clients
</text>


<rect x="160" y="200" width="110" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="215" y="230" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>


<rect x="320" y="100" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="380" y="125" text-anchor="middle" fill="white" font-size="11">
Search Svc
</text>


<rect x="320" y="180" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="380" y="205" text-anchor="middle" fill="white" font-size="11">
Typeahead Svc
</text>


<rect x="320" y="260" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="380" y="285" text-anchor="middle" fill="white" font-size="11">
Trending Svc
</text>


<rect x="320" y="340" width="120" height="40" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="380" y="365" text-anchor="middle" fill="white" font-size="11">
Kafka
</text>


<rect x="520" y="60" width="130" height="40" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="585" y="85" text-anchor="middle" fill="white" font-size="10">
Earlybird (RT)
</text>


<rect x="520" y="120" width="130" height="40" rx="8" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</rect>
<text x="585" y="145" text-anchor="middle" fill="white" font-size="10">
Full Index (Hist)
</text>


<rect x="520" y="180" width="130" height="40" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="585" y="205" text-anchor="middle" fill="white" font-size="10">
Blender
</text>


<rect x="520" y="240" width="130" height="40" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="585" y="265" text-anchor="middle" fill="white" font-size="10">
ML Ranker
</text>


<rect x="520" y="300" width="130" height="40" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="585" y="325" text-anchor="middle" fill="white" font-size="10">
Safety Filter
</text>


<rect x="520" y="360" width="130" height="40" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="585" y="385" text-anchor="middle" fill="white" font-size="10">
Index Writer
</text>


<rect x="730" y="180" width="110" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="785" y="205" text-anchor="middle" fill="white" font-size="10">
Hydrator
</text>


<rect x="730" y="260" width="110" height="40" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="785" y="285" text-anchor="middle" fill="white" font-size="10">
Result Cache
</text>


<line x1="120" y1="225" x2="158" y2="225" stroke="#1da1f2" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="218" x2="318" y2="120" stroke="#1da1f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="225" x2="318" y2="200" stroke="#1da1f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="232" x2="318" y2="280" stroke="#1da1f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="440" y1="120" x2="518" y2="80" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="440" y1="120" x2="518" y2="140" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="585" y1="100" x2="585" y2="178" stroke="#ffaa00" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="585" y1="160" x2="585" y2="178" stroke="#ffaa00" stroke-width="1">
</line>


<line x1="585" y1="220" x2="585" y2="238" stroke="#ffaa00" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="585" y1="280" x2="585" y2="298" stroke="#ffaa00" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="650" y1="200" x2="728" y2="200" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="440" y1="360" x2="518" y2="380" stroke="#66ff66" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="650" y1="380" x2="650" y2="80" stroke="#66ff66" stroke-width="1" stroke-dasharray="4,4" marker-end="url(#arrow3)">
</line>


</svg>

</details>


## Database Schema


### Inverted Index (Earlybird / Custom Lucene)


| Structure           | Key            | Value                                          | Storage                     |
|---------------------|----------------|------------------------------------------------|-----------------------------|
| Token Dictionary    | token (string) | Pointer to posting list                        | In-memory hash map          |
| Posting List        | token          | Sorted [tweet_id, position, field, flags]      | In-memory compressed arrays |
| Tweet Forward Index | tweet_id       | {author_id, timestamp, engagement_score, lang} | In-memory columnar          |


### Trending Data — Redis


| Key                              | Type       | Details                                        |
|----------------------------------|------------|------------------------------------------------|
| `trending:{region}:{timewindow}` | Sorted Set | Members: hashtag/topic, Score: count in window |
| `trending:global:current`        | Sorted Set | Top 50 global trending topics                  |


Sharding Strategy


- **Earlybird:** Tweets hash-partitioned by tweet_id across ~200 shards. Search query sent to ALL shards (scatter-gather)

- **Full Index:** Time-based sharding. Each shard holds 1 week of data. Recent shards queried first. Old shards only queried if user requests historical results

- **Trending:** Redis per-region sharding. Global trending computed from regional aggregates


## Cache Deep Dive


| Cache                   | Strategy      | Eviction | TTL                                              |
|-------------------------|---------------|----------|--------------------------------------------------|
| Search Result Cache     | Read-through  | LRU      | 30 seconds (very short — results change rapidly) |
| Typeahead Cache         | Write-through | LRU      | 5 minutes                                        |
| Tweet Cache (Memcached) | Read-through  | LRU      | 1 hour                                           |
| Trending Cache          | Write-through | TTL      | 1 minute                                         |


**CDN:** Search results NOT cached at CDN (dynamic, personalized). Only media (images, videos) in search results served via CDN.


## Scaling Considerations


- **Scatter-gather:** Query goes to 200+ Earlybird shards. Each shard processes independently, returns top-K local results. Blender merges. Latency = max(shard latencies). Mitigated by: hedged requests (send to 2 replicas, take first response), timeout and skip slow shards

- **Real-time indexing scale:** 500M tweets/day = ~6K tweets/sec. Each Earlybird shard indexes ~30 tweets/sec. Index stays in memory for speed

- **Historical index:** Petabytes of historical data on SSDs. Only queried on-demand (when user scrolls past recent results)

- **Surge handling:** Major events (Super Bowl, elections) cause 10x search spikes. Pre-scale based on event calendar. Auto-scale Earlybird replicas


## Tradeoffs & Deep Dives


> **
✅ Custom Search (Earlybird)
- Optimized for Twitter's specific access patterns
- ~5 second indexing latency (Elasticsearch: 1-30 seconds)
- In-memory for recent tweets = ultra-fast queries


❌ Custom Search Downsides
- Massive engineering investment to build and maintain
- No community support (vs Elasticsearch ecosystem)
- Specialized knowledge required


## Alternative Approaches


- **Elasticsearch:** Off-the-shelf full-text search. Easier to operate. Good enough for most scale. Tradeoff: 1-30s indexing latency, higher resource usage

- **Apache Solr:** Another Lucene-based option. Strong for faceted search. Less real-time oriented than Earlybird

- **Embedding-based search:** Use neural embeddings (BERT) for semantic search. "earthquake damage" matches "seismic destruction" even without exact keyword match. Higher latency, requires ANN index (FAISS/HNSW)


## Additional Information


- **Query understanding:** NLP pipeline parses queries: spell correction ("eathquake" → "earthquake"), synonym expansion ("car" → "car OR automobile"), intent detection (is user searching for news, people, or media?)

- **Personalization:** Same query, different results per user. If user frequently engages with sports content, sports-related results boosted

- **Safe search:** Sensitive content filtered by default. NSFW classifier runs on indexed tweets. Users can opt-in to see all results

- **Search ads:** Promoted tweets inserted into search results. Ad auction runs in parallel with organic search. Relevance threshold: ad must be relevant to query to be shown