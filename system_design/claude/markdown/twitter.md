# System Design: Twitter (Microblogging Platform)


## Functional Requirements


- Users can post tweets (280 chars max, with optional media)

- Follow/unfollow other users (asymmetric relationship)

- View home timeline (tweets from followed users, ranked)

- Like, retweet, quote tweet, and reply to tweets

- Hashtags and trending topics

- User mentions and notifications


## Non-Functional Requirements


- **Read-heavy:** ~600K tweets/sec read vs ~6K tweets/sec write (100:1 ratio)

- **Low Latency:** Timeline loads in <300ms

- **High Availability:** 99.99% uptime

- **Scalability:** 500M+ daily active users

- **Eventual Consistency:** Acceptable for timelines (seconds delay OK)


## Flow 1: Posting a Tweet


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 450" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#1da1f2">
</polygon>
</marker>
</defs>


<rect x="20" y="180" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="80" y="215" text-anchor="middle" fill="white" font-size="13">
User
</text>


<rect x="190" y="180" width="130" height="60" rx="10" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="255" y="215" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>


<rect x="380" y="180" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="445" y="215" text-anchor="middle" fill="white" font-size="13">
Tweet Service
</text>


<rect x="580" y="80" width="140" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="650" y="110" text-anchor="middle" fill="white" font-size="12">
Fan-out Service
</text>


<rect x="580" y="200" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="650" y="230" text-anchor="middle" fill="white" font-size="12">
Media Service
</text>


<rect x="580" y="310" width="140" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="650" y="340" text-anchor="middle" fill="white" font-size="12">
Kafka
</text>


<ellipse cx="850" cy="105" rx="55" ry="25" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="850" y="110" text-anchor="middle" fill="white" font-size="11">
Tweet DB
</text>


<rect x="800" y="180" width="110" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="855" y="208" text-anchor="middle" fill="white" font-size="11">
Timeline Cache
</text>
<text x="855" y="222" text-anchor="middle" fill="white" font-size="10">
(Redis)
</text>


<rect x="800" y="300" width="110" height="50" rx="8" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="855" y="330" text-anchor="middle" fill="white" font-size="12">
S3 / CDN
</text>


<line x1="140" y1="210" x2="188" y2="210" stroke="#1da1f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="320" y1="210" x2="378" y2="210" stroke="#1da1f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="195" x2="578" y2="110" stroke="#1da1f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="215" x2="578" y2="225" stroke="#1da1f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="230" x2="578" y2="330" stroke="#1da1f2" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="720" y1="100" x2="793" y2="100" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="720" y1="110" x2="798" y2="200" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="720" y1="225" x2="798" y2="320" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<text x="550" y="30" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Twitter: Post Tweet Flow
</text>


</svg>

</details>


### Step-by-Step


1. **User posts tweet:** `POST /v1/tweets { text: "Hello world! #firsttweet", media_ids: [] }`

2. **Tweet Service:** Validates content (280 char limit, content policy), stores in Tweet DB (Cassandra)

3. **Fan-out Service:** For regular users (<5K followers), pushes tweet_id to each follower's timeline cache (Redis sorted set). For celebrities (>5K followers), skips fan-out (handled at read time)

4. **Media Service:** If media attached, uploads to S3, generates thumbnails, returns CDN URLs

5. **Kafka:** Publishes tweet event for async consumers (search indexing, trending detection, notification service, analytics)


> **
**Example:** User with 2000 followers tweets "Just shipped a new feature! 🚀 #engineering". Tweet Service stores in Cassandra. Fan-out Service reads 2000 follower IDs from Follow Graph → ZADD tweet_id to 2000 Redis sorted sets (pipelined, ~30ms). Kafka event → Search Service indexes tweet for #engineering search → Trending Detector counts #engineering mentions → Notification Service notifies users with notifications enabled.


### Deep Dive: Fan-out Service


- **Fan-out on write (push):** For users with <5K followers. Pre-computes timelines. Read is O(1) — just read from Redis

- **Fan-out on read (pull):** For celebrities with >5K followers. At read time, merge celebrity tweets with cached timeline

- **Hybrid:** Timeline = cached fan-out tweets + on-demand celebrity tweets. Merge + rank at read time

- **Worker pool:** Fan-out workers consume from internal queue. Partitioned by follower_id for locality. Batch Redis writes for efficiency


## Flow 2: Reading Home Timeline


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 350" xmlns="http://www.w3.org/2000/svg">


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
<text x="245" y="168" text-anchor="middle" fill="white" font-size="12">
Timeline
</text>
<text x="245" y="185" text-anchor="middle" fill="white" font-size="12">
Service
</text>


<rect x="370" y="80" width="130" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="435" y="110" text-anchor="middle" fill="white" font-size="12">
Timeline Cache
</text>


<rect x="370" y="180" width="130" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="435" y="208" text-anchor="middle" fill="white" font-size="11">
Celebrity Merge
</text>


<rect x="370" y="280" width="130" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="435" y="310" text-anchor="middle" fill="white" font-size="12">
Tweet Hydrator
</text>


<rect x="570" y="140" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="640" y="170" text-anchor="middle" fill="white" font-size="12">
Ranking Service
</text>


<rect x="780" y="140" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="840" y="175" text-anchor="middle" fill="white" font-size="12">
Final Feed
</text>


<line x1="130" y1="170" x2="178" y2="170" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="310" y1="155" x2="368" y2="110" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="310" y1="175" x2="368" y2="205" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="500" y1="110" x2="500" y2="178" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="500" y1="210" x2="500" y2="278" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="500" y1="305" x2="568" y2="170" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="710" y1="165" x2="778" y2="170" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<text x="520" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Twitter: Home Timeline Read Flow
</text>


</svg>

</details>


### Step-by-Step


1. **Timeline Service** reads top 200 tweet_ids from user's Timeline Cache (Redis `ZREVRANGE feed:{user_id} 0 199`)

2. **Celebrity Merge:** Fetches recent tweets from celebrities the user follows (stored separately in `celeb_recent:{celeb_id}`). Merges with cached tweets

3. **Tweet Hydrator:** Batch-fetches full tweet objects (text, author info, media URLs, engagement counts) from Tweet DB + cache

4. **Ranking Service:** ML model scores tweets by predicted engagement. Considers: recency, author-viewer relationship, tweet type (text vs media), engagement velocity

5. **Returns top 50 ranked tweets** to client


> **
**Example:** User follows 500 accounts (10 celebrities, 490 regular). Timeline Cache has 200 pre-pushed tweet_ids. Celebrity Merge adds 30 recent tweets from 10 celebrities. Total: 230 candidates. Hydrate from Cassandra (180 cache hits, 50 DB reads). Ranking Service scores: viral tweet from friend (high engagement velocity) scores 0.95, old tweet from acquaintance scores 0.12. Top 50 returned. Total: ~250ms.


## Combined Overall Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1150 550" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#1da1f2">
</polygon>
</marker>
</defs>


<rect x="20" y="240" width="100" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="70" y="275" text-anchor="middle" fill="white" font-size="12">
Clients
</text>


<rect x="160" y="240" width="110" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="215" y="270" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>


<rect x="320" y="80" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="380" y="105" text-anchor="middle" fill="white" font-size="11">
Tweet Svc
</text>


<rect x="320" y="150" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="380" y="175" text-anchor="middle" fill="white" font-size="11">
Timeline Svc
</text>


<rect x="320" y="220" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="380" y="245" text-anchor="middle" fill="white" font-size="11">
User Svc
</text>


<rect x="320" y="290" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="380" y="315" text-anchor="middle" fill="white" font-size="11">
Search Svc
</text>


<rect x="320" y="360" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="380" y="385" text-anchor="middle" fill="white" font-size="11">
Trending Svc
</text>


<rect x="320" y="430" width="120" height="40" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="380" y="455" text-anchor="middle" fill="white" font-size="11">
Fan-out Svc
</text>


<rect x="510" y="80" width="110" height="40" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="565" y="105" text-anchor="middle" fill="white" font-size="11">
Kafka
</text>


<rect x="510" y="150" width="110" height="40" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="565" y="175" text-anchor="middle" fill="white" font-size="11">
Timeline Cache
</text>


<rect x="510" y="220" width="110" height="40" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="565" y="245" text-anchor="middle" fill="white" font-size="11">
Ranking Svc
</text>


<rect x="510" y="290" width="110" height="40" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="565" y="315" text-anchor="middle" fill="white" font-size="11">
Elasticsearch
</text>


<rect x="510" y="360" width="110" height="40" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="565" y="385" text-anchor="middle" fill="white" font-size="11">
Notification
</text>


<rect x="510" y="430" width="110" height="40" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="565" y="455" text-anchor="middle" fill="white" font-size="11">
Follow Graph
</text>


<ellipse cx="720" cy="100" rx="55" ry="22" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="720" y="105" text-anchor="middle" fill="white" font-size="10">
Tweet DB
</text>


<ellipse cx="720" cy="175" rx="55" ry="22" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="720" y="180" text-anchor="middle" fill="white" font-size="10">
User DB
</text>


<ellipse cx="720" cy="250" rx="55" ry="22" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="720" y="255" text-anchor="middle" fill="white" font-size="10">
Graph DB
</text>


<rect x="680" y="310" width="100" height="35" rx="8" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="730" y="332" text-anchor="middle" fill="white" font-size="10">
S3 / CDN
</text>


<line x1="120" y1="265" x2="158" y2="265" stroke="#1da1f2" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="255" x2="318" y2="100" stroke="#1da1f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="260" x2="318" y2="170" stroke="#1da1f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="265" x2="318" y2="240" stroke="#1da1f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="270" x2="318" y2="310" stroke="#1da1f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="440" y1="100" x2="508" y2="100" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="440" y1="170" x2="508" y2="170" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="440" y1="310" x2="508" y2="310" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="440" y1="450" x2="508" y2="450" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="620" y1="100" x2="663" y2="100" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="620" y1="245" x2="663" y2="175" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="620" y1="455" x2="663" y2="250" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<text x="575" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Twitter: Combined Architecture
</text>


</svg>

</details>


> **
**Example — Full lifecycle:** User tweets "Breaking: major acquisition announced! $AAPL #tech" → Tweet Service stores in Cassandra → Kafka event → Fan-out to 2K followers' timeline caches → Search Service indexes for #tech and $AAPL → Trending Service counts #tech (if spiking, adds to trending list) → Notification Service alerts users with notifications for @author. Follower opens app → Timeline Service reads cache + celebrity tweets → Ranking ML sorts → feed displayed in ~200ms.


## Database Schema


### NoSQL — Cassandra (Tweets)


| Table           | Column       | Type               | Details |
|-----------------|--------------|--------------------|---------|
| **tweets**      | tweet_id     | BIGINT (Snowflake) | `PK`    |
| user_id         | BIGINT       | `IDX`              |         |
| text            | VARCHAR(280) |                    |         |
| media_urls      | LIST<TEXT>   |                    |         |
| reply_to_id     | BIGINT       | Nullable           |         |
| retweet_of_id   | BIGINT       | Nullable           |         |
| created_at      | TIMESTAMP    |                    |         |
| **user_tweets** | user_id      | BIGINT             | `PK`    |
| tweet_id        | BIGINT       | `PK` DESC          |         |
| created_at      | TIMESTAMP    |                    |         |


### SQL — MySQL/PostgreSQL (Users, Follow Graph)


| Table           | Column      | Type         | Details     |
|-----------------|-------------|--------------|-------------|
| **users**       | user_id     | BIGINT       | `PK`        |
| username        | VARCHAR(15) | `IDX`        |             |
| display_name    | VARCHAR(50) |              |             |
| follower_count  | INT         | Denormalized |             |
| following_count | INT         | Denormalized |             |
| **follows**     | follower_id | BIGINT       | `PK`  `IDX` |
| followee_id     | BIGINT      | `PK`  `IDX`  |             |
| created_at      | TIMESTAMP   |              |             |


### Timeline Cache — Redis


| Key                      | Type       | Details                                        |
|--------------------------|------------|------------------------------------------------|
| `timeline:{user_id}`     | Sorted Set | tweet_ids scored by timestamp. Max 800 entries |
| `celeb_recent:{user_id}` | Sorted Set | Latest 100 tweets from celebrities             |


Sharding


- **tweets (Cassandra):** Partition by `tweet_id`. user_tweets table partitions by `user_id` for profile timeline

- **follows:** Shard by `follower_id` — "who do I follow?" is shard-local. Reverse index on `followee_id` for "who follows me?"

- **Timeline Cache:** Redis Cluster, shard by `user_id`


### Indexes


- `tweets.user_id` — Secondary index for "all tweets by user X" (user profile)

- `follows(follower_id, followee_id)` — Composite PK + index on followee_id for fan-out (get all followers of user X)

- Elasticsearch: full-text index on tweet text, hashtag index, user mention index


## Cache Deep Dive


| Cache                   | Strategy                | Eviction          | TTL    |
|-------------------------|-------------------------|-------------------|--------|
| Timeline Cache (Redis)  | Write-through (fan-out) | Size-capped (800) | None   |
| Tweet Cache (Memcached) | Read-through            | LRU               | 1 hour |
| User Cache (Redis)      | Read-through            | LRU               | 30 min |
| Trending Cache (Redis)  | Write-through           | TTL               | 5 min  |


### CDN


- **Media CDN:** All tweet images, videos, GIFs served via CDN. Multiple resolution variants per image

- **Video:** HLS adaptive streaming for video tweets. CDN caches HLS segments

- **TTL:** Media: 365 days. Profile pics: 30 days. Trending content pre-warmed at edge


## Scaling Considerations


- **Fan-out bottleneck:** Celebrity tweet = millions of cache writes. Solution: hybrid approach (no fan-out for celebrities) + priority queues (fan-out to online users first)

- **Timeline Cache:** 500M users × 800 IDs × 8 bytes = ~3.2TB Redis. Partitioned across thousands of Redis instances

- **Trending:** Count-Min Sketch for approximate hashtag counting in streaming fashion. Top-K maintained per region + global

- **Search:** Elasticsearch cluster, partitioned by time (hourly shards). Old shards merged and moved to cheaper storage


## Tradeoffs & Deep Dives


> **
✅ Hybrid Fan-out
- Best of both worlds: fast reads for majority, manageable writes
- Only ~0.1% of users are celebrities — fan-out savings are massive


❌ Hybrid Complexity
- Two code paths increase complexity
- Celebrity tweet merge at read time adds ~50ms
- Need to maintain "is celebrity" threshold


## Alternative Approaches


- **Pure pull model:** No pre-computation. Every timeline request queries all followed users' tweets. Simple but O(following_count) per read — too slow at scale

- **GraphQL API:** Single flexible query instead of multiple REST endpoints. Client specifies exactly which tweet fields needed. Reduces over-fetching

- **Event sourcing:** Store all events (tweet, like, retweet) as immutable log. Derive timeline views from event stream. Better for analytics but complex to implement


## Additional Information


- **Snowflake IDs:** Twitter invented Snowflake for globally unique, time-sortable, decentralized ID generation. 64-bit = timestamp(41) + datacenter(5) + machine(5) + sequence(12)

- **Rate limiting:** 300 tweets/3hrs per user, 900 reads/15min per API key. Sliding window counter in Redis

- **Content moderation:** ML classifiers run on every tweet: hate speech, spam, misinformation. Flagged tweets hidden pending review. Appeal process for false positives

- **Real-time updates:** WebSocket for real-time timeline updates ("new tweets available" indicator). Long-polling fallback for environments blocking WebSocket