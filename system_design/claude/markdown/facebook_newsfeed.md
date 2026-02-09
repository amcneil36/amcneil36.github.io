# System Design: Facebook Newsfeed (Deep Dive)


## Functional Requirements


- Display a personalized, ranked feed of posts from friends, followed pages, and groups

- Feed includes posts, photos, videos, shared links, life events, and ads

- Infinite scroll with real-time updates ("X new posts" indicator)

- Ranking based on relevance (engagement prediction) not just chronological

- Support "seen" tracking to avoid showing same posts repeatedly

- Allow users to hide posts, unfollow, or snooze sources


## Non-Functional Requirements


- **Latency:** Feed loads in <500ms (first page of ~50 posts)

- **Freshness:** New posts appear within 30 seconds of creation

- **Scalability:** 2B+ daily active users, each generating unique personalized feed

- **Availability:** 99.99% — feed is the core product surface

- **Read-heavy:** 100x more feed reads than post writes


## Flow 1: Feed Generation — Write Path (Fan-out on Write)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 450" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#1877f2">
</polygon>
</marker>
</defs>


<rect x="20" y="180" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="80" y="215" text-anchor="middle" fill="white" font-size="13">
Author
</text>


<rect x="190" y="180" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="255" y="215" text-anchor="middle" fill="white" font-size="13">
Post Service
</text>


<rect x="370" y="100" width="140" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="440" y="130" text-anchor="middle" fill="white" font-size="12">
Kafka (Post Events)
</text>


<rect x="370" y="220" width="140" height="60" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="440" y="248" text-anchor="middle" fill="white" font-size="12">
Fan-out Workers
</text>
<text x="440" y="265" text-anchor="middle" fill="white" font-size="10">
(parallel, async)
</text>


<rect x="570" y="140" width="140" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="640" y="168" text-anchor="middle" fill="white" font-size="11">
Social Graph (TAO)
</text>


<rect x="570" y="260" width="140" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="640" y="288" text-anchor="middle" fill="white" font-size="11">
Feed Cache (Redis)
</text>


<text x="640" y="330" fill="#aaa" font-size="11">
Sorted Set per user
</text>


<text x="640" y="348" fill="#aaa" font-size="11">
ZADD feed:{user_id} {timestamp} {post_id}
</text>


<rect x="570" y="370" width="140" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="640" y="400" text-anchor="middle" fill="white" font-size="12">
Eligibility Filter
</text>


<ellipse cx="830" cy="165" rx="60" ry="25" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="830" y="170" text-anchor="middle" fill="white" font-size="11">
Post DB
</text>


<line x1="140" y1="210" x2="188" y2="210" stroke="#1877f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="320" y1="195" x2="368" y2="130" stroke="#1877f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="320" y1="220" x2="368" y2="245" stroke="#1877f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="440" y1="150" x2="440" y2="218" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="235" x2="568" y2="170" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="265" x2="568" y2="280" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="270" x2="568" y2="390" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="710" y1="160" x2="768" y2="162" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<text x="550" y="30" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Facebook Newsfeed: Write Path (Fan-out)
</text>


</svg>

</details>


### Step-by-Step


1. **Author publishes post** → Post Service stores in Post DB → publishes event to Kafka

2. **Fan-out Workers** consume from Kafka. For each post:
    

      3. Query TAO for author's friend list (e.g., 500 friends)

      4. For each friend, check **Eligibility Filter**: Is friend active? Has friend muted/unfollowed author? Privacy check?

      5. For eligible friends: `ZADD feed:{friend_id} {timestamp} {post_id}` into Redis

    


6. **Skip fan-out** for authors with >5000 followers (celebrities/pages) — handled at read time

7. **Feed cache bounded:** Each user's sorted set capped at 800 post_ids. Older entries evicted automatically


> **
**Example:** Alice (350 friends) posts a photo. Kafka event consumed by fan-out worker. TAO returns 350 friends. Eligibility filter removes 20 (10 inactive >30 days, 5 unfollowed Alice, 5 blocked). Worker does 330 ZADD operations on Redis. Each ZADD is O(log N) ~ 1μs. Total fan-out time: ~50ms (pipelined Redis commands). Bob's feed cache now contains Alice's post_id.


### Deep Dive: Eligibility Filter


- **Checks performed:** User active in last 30 days? User unfollowed author? User blocked author? Privacy settings allow? Content policy violation?

- **Why filter at write time:** Avoids wasting cache space on ineligible users. Reduces read-time processing

- **Data sources:** User activity status (Redis counter), unfollow/block relationships (TAO), privacy settings (User Service cache)


## Flow 2: Feed Reading — Read Path (Ranking & Serving)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1150 450" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ffaa00">
</polygon>
</marker>
</defs>


<rect x="20" y="180" width="110" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="75" y="215" text-anchor="middle" fill="white" font-size="13">
User
</text>


<rect x="170" y="180" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="235" y="215" text-anchor="middle" fill="white" font-size="13">
Feed Service
</text>


<rect x="340" y="80" width="140" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="410" y="108" text-anchor="middle" fill="white" font-size="12">
Feed Cache
</text>


<rect x="340" y="180" width="140" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="410" y="208" text-anchor="middle" fill="white" font-size="11">
Celebrity Post Svc
</text>


<rect x="340" y="280" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="410" y="308" text-anchor="middle" fill="white" font-size="12">
Post Hydration
</text>


<rect x="540" y="80" width="150" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="615" y="108" text-anchor="middle" fill="white" font-size="12">
Seen Filter
</text>


<rect x="540" y="180" width="150" height="60" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="615" y="205" text-anchor="middle" fill="white" font-size="12">
Ranking Service
</text>
<text x="615" y="225" text-anchor="middle" fill="white" font-size="10">
(ML Model)
</text>


<rect x="540" y="300" width="150" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="615" y="330" text-anchor="middle" fill="white" font-size="12">
Ad Insertion
</text>


<rect x="760" y="180" width="140" height="60" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="830" y="208" text-anchor="middle" fill="white" font-size="12">
Diversity Filter
</text>
<text x="830" y="225" text-anchor="middle" fill="white" font-size="10">
(dedup authors)
</text>


<rect x="960" y="180" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="1020" y="208" text-anchor="middle" fill="white" font-size="12">
Final Feed
</text>
<text x="1020" y="225" text-anchor="middle" fill="white" font-size="11">
(50 posts)
</text>


<line x1="130" y1="210" x2="168" y2="210" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="300" y1="195" x2="338" y2="110" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="300" y1="210" x2="338" y2="205" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="480" y1="105" x2="538" y2="105" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="480" y1="200" x2="538" y2="200" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="300" y1="225" x2="338" y2="300" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="480" y1="305" x2="538" y2="310" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="690" y1="105" x2="690" y2="175" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="690" y1="210" x2="758" y2="210" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="690" y1="325" x2="758" y2="225" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="900" y1="210" x2="958" y2="210" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<text x="575" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Facebook Newsfeed: Read Path (5-Stage Pipeline)
</text>


</svg>

</details>


### 5-Stage Pipeline


1. **Stage 1 — Candidate Retrieval:**
    

      2. Read top 500 post_ids from Feed Cache (Redis sorted set)

      3. Fetch recent posts from followed celebrities/pages (fan-out on read for >5K follower accounts)

      4. Merge into ~600 candidate posts

    


5. **Stage 2 — Seen Filter:** Remove posts user has already seen (tracked in Redis bitmap `seen:{user_id}`). Removes ~30%

6. **Stage 3 — Post Hydration:** Fetch full post objects (text, media URLs, author info, like/comment counts) from Post DB + caches. Batch fetch for efficiency

7. **Stage 4 — ML Ranking:**
    

      8. Neural network predicts P(like), P(comment), P(share), P(hide) for each post

      9. Score = weighted sum: `0.3*P(like) + 0.35*P(comment) + 0.25*P(share) - 0.5*P(hide)`

      10. Features: author-viewer relationship strength, post type, recency, historical engagement, time of day

    


11. **Stage 5 — Diversity + Ad Insertion:**
    

      12. Diversity filter: max 2 consecutive posts from same author, mix content types (photos, text, videos, links)

      13. Ad Service inserts sponsored posts at positions 3, 8, 15, etc. (based on ad auction results)

    


> **
**Example:** Bob opens Facebook. Feed Service retrieves 500 post_ids from Redis cache + 100 celebrity/page posts. Seen filter removes 180 already-viewed posts. 420 posts hydrated (batch Cassandra read: ~30ms). ML Ranking scores all 420 — Alice's wedding photo scores 0.92 (high engagement signals), random acquaintance's check-in scores 0.15. Top 50 posts selected. Diversity filter reorders: no 3 consecutive text posts. Ad inserted at position 4. Final 50-post feed returned in ~350ms total.


### Deep Dive: ML Ranking Model


- **Model:** Deep neural network (DeepFM or DIN architecture) with ~1000 features

- **Key features:**
    

      - **Edge features:** How often viewer interacts with author (message, like, comment frequency → affinity score)

      - **Post features:** Content type, text length, media quality score, number of reactions, age of post

      - **User features:** User's historical engagement rates, preferred content types, active hours

      - **Context features:** Time of day, day of week, device type, network speed

    


- **Inference:** ~10ms per batch of 500 posts using GPU inference servers

- **Training:** Continuous learning on billions of daily interactions. Model updated every few hours


## Combined Overall Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 550" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#1877f2">
</polygon>
</marker>
</defs>


<text x="600" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Facebook Newsfeed: Combined Architecture
</text>


<text x="200" y="55" fill="#66ff66" font-size="12">
WRITE PATH (post creation → fan-out)
</text>


<text x="700" y="55" fill="#ffaa00" font-size="12">
READ PATH (feed request → ranked feed)
</text>


<rect x="20" y="250" width="100" height="50" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="70" y="280" text-anchor="middle" fill="white" font-size="12">
Clients
</text>


<rect x="160" y="150" width="110" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="215" y="175" text-anchor="middle" fill="white" font-size="11">
Post Service
</text>


<rect x="160" y="320" width="110" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="215" y="345" text-anchor="middle" fill="white" font-size="11">
Feed Service
</text>


<rect x="320" y="100" width="120" height="40" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="380" y="125" text-anchor="middle" fill="white" font-size="11">
Kafka
</text>


<rect x="320" y="200" width="120" height="40" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="380" y="225" text-anchor="middle" fill="white" font-size="11">
Fan-out Workers
</text>


<rect x="320" y="320" width="120" height="40" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="380" y="345" text-anchor="middle" fill="white" font-size="11">
TAO (Graph)
</text>


<rect x="510" y="100" width="120" height="40" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="570" y="125" text-anchor="middle" fill="white" font-size="11">
Feed Cache
</text>


<rect x="510" y="200" width="120" height="40" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="570" y="225" text-anchor="middle" fill="white" font-size="11">
Seen Filter
</text>


<rect x="510" y="300" width="120" height="40" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="570" y="325" text-anchor="middle" fill="white" font-size="11">
Post Hydration
</text>


<rect x="700" y="200" width="130" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="765" y="225" text-anchor="middle" fill="white" font-size="11">
ML Ranking
</text>


<rect x="700" y="310" width="130" height="40" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="765" y="335" text-anchor="middle" fill="white" font-size="11">
Ad Insertion
</text>


<rect x="700" y="100" width="130" height="40" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="765" y="125" text-anchor="middle" fill="white" font-size="11">
Diversity Filter
</text>


<rect x="900" y="200" width="110" height="50" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="955" y="230" text-anchor="middle" fill="white" font-size="11">
Final Feed
</text>


<ellipse cx="570" cy="430" rx="70" ry="25" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="570" y="435" text-anchor="middle" fill="white" font-size="11">
Post DB
</text>


<line x1="120" y1="265" x2="158" y2="175" stroke="#66ff66" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="120" y1="285" x2="158" y2="340" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="170" x2="318" y2="125" stroke="#66ff66" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="440" y1="120" x2="440" y2="198" stroke="#66ff66" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="380" y1="240" x2="380" y2="318" stroke="#66ff66" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow3)">
</line>


<line x1="440" y1="220" x2="508" y2="120" stroke="#66ff66" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="340" x2="508" y2="120" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="120" x2="630" y2="198" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="570" y1="240" x2="570" y2="298" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="570" y1="340" x2="570" y2="403" stroke="#ff6b6b" stroke-width="1" stroke-dasharray="4,4" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="320" x2="698" y2="225" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="830" y1="225" x2="898" y2="225" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="765" y1="250" x2="765" y2="308" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="830" y1="120" x2="898" y2="210" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


</svg>

</details>


> **
**Example — End-to-end:** Alice posts "Got engaged! 💍" with a photo. POST /posts → stored in Post DB → Kafka event → Fan-out Worker reads Alice's 350 eligible friends from TAO → ZADD to 350 Redis sorted sets (~50ms). 10 minutes later, Bob opens Facebook. Feed Service reads 500 post_ids from his Redis cache + 80 celebrity posts → Seen filter removes 200 → Hydrates remaining 380 posts → ML Ranking scores Alice's engagement post at 0.95 (top score — close friend, photo, high engagement type) → Diversity filter ensures mix → Ad at position 4 → Bob sees Alice's post at #1 in his feed.


## Database Schema


### Feed Cache — Redis


| Structure       | Key                      | Type                 | Details                                                                         |
|-----------------|--------------------------|----------------------|---------------------------------------------------------------------------------|
| User Feed       | `feed:{user_id}`         | Sorted Set           | Members: post_ids, Scores: timestamps. Max 800 entries (ZREMRANGEBYRANK to cap) |
| Seen Posts      | `seen:{user_id}:{date}`  | Bitmap / HyperLogLog | Tracks which post_ids user has viewed today. 1 bit per post. TTL: 7 days        |
| Celebrity Posts | `celeb_recent:{user_id}` | Sorted Set           | Latest 50 posts from celebrities. Queried at read time                          |


### NoSQL — Cassandra (Post Storage)


| Table        | Column           | Type                                 | Details |
|--------------|------------------|--------------------------------------|---------|
| **posts**    | post_id          | BIGINT                               | `PK`    |
| author_id    | BIGINT           | `IDX`                                |         |
| content_text | TEXT             |                                      |         |
| media_urls   | LIST<TEXT>       |                                      |         |
| post_type    | TEXT             | text, photo, video, link, life_event |         |
| engagement   | MAP<TEXT,BIGINT> | {likes: 42, comments: 5, shares: 2}  |         |
| created_at   | TIMESTAMP        |                                      |         |


### Ranking Feature Store — Redis / Feature Service


| Feature                    | Storage                         | Update Frequency                     |
|----------------------------|---------------------------------|--------------------------------------|
| User-Author affinity score | Redis Hash `affinity:{user_id}` | Daily batch + real-time increments   |
| Post engagement velocity   | Redis `velocity:{post_id}`      | Real-time (INCR on each interaction) |
| User engagement history    | Feature Service (ML platform)   | Hourly batch                         |


Sharding


- **Feed Cache:** Redis Cluster, sharded by `user_id` consistent hashing. Each user's feed on exactly one shard

- **posts table:** Partitioned by `post_id` in Cassandra. Hydration does batch multi-get across partitions

- **Affinity scores:** Sharded by `user_id` — all a user's affinity data co-located for efficient ranking feature retrieval


## Cache Deep Dive


| Cache                  | Strategy                       | Eviction                | TTL                  | Hit Rate |
|------------------------|--------------------------------|-------------------------|----------------------|----------|
| Feed Cache (Redis)     | Write-through (fan-out writes) | Size-capped (800 items) | None                 | ~95%     |
| Post Cache (Memcached) | Read-through                   | LRU                     | 1 hr                 | ~92%     |
| TAO Cache              | Write-through                  | LRU                     | Invalidated on write | ~99.9%   |
| Feature Cache (Redis)  | Write-behind (batch update)    | TTL                     | 1 hr                 | ~90%     |


### CDN


- CDN serves media (photos, video thumbnails) referenced in feed posts. Each media URL is a CDN URL

- Feed API responses are NOT cached at CDN — they're personalized per user and change on every refresh

- Static assets (app JS/CSS bundles) cached at CDN with 30-day TTL


## Scaling Considerations


- **Fan-out Worker scaling:** Kafka consumer group with thousands of workers. Auto-scale based on queue depth. During peak (e.g., election night), scale up 3-5x

- **ML Ranking inference:** GPU cluster for real-time inference. Batch multiple users' ranking requests for GPU throughput. ~10ms per 500 posts per user

- **Feed Cache size:** 2B users × 800 post_ids × 8 bytes = ~12TB of Redis. Distributed across thousands of Redis instances

- **Thundering herd:** Celebrity post → millions of feed cache writes. Mitigated by: (1) async fan-out, (2) rate limiting per-author fan-out, (3) celebrity posts handled at read time instead

- **Cold start:** New user or user returning after long absence → feed cache empty. Fall back to "pull model": query friends' recent posts directly from Post DB. Slower (~2s) but only for first load


## Tradeoffs & Deep Dives


> **
✅ ML Ranking
- Dramatically higher engagement than reverse-chronological
- Personalizes content to each user's interests
- Can optimize for multiple objectives (engagement, time well spent, diversity)


❌ ML Ranking Downsides
- Filter bubble: users see increasingly homogeneous content
- Engagement optimization can promote sensational/clickbait content
- Lack of transparency: users don't understand why they see what they see
- Computational cost: GPU inference for every feed load


## Alternative Approaches


- **Pure chronological feed:** Twitter's original approach. Simple, transparent, no ranking bias. But: information overload for users following 1000+ accounts. Users miss important posts buried under noise

- **Interest-based clustering:** Instead of social graph, cluster content by topic. Recommend content from strangers based on interests (TikTok model). Higher discovery but weaker social bonds

- **Federated fan-out:** Instead of centralized Redis, each server maintains its shard of feeds. Reduces network hops but complicates cross-shard friend fan-out

- **Edge ranking at client:** Server sends candidate pool (200 posts), client does final ranking on-device using lightweight model. Reduces server compute, enables offline feed construction


## Additional Information


- **Feed invalidation:** When a post is deleted, must remove from all friends' feed caches. Fan-out deletion event to all friends' Redis sets. Eventual consistency — may take seconds

- **"New posts" indicator:** WebSocket/long-poll connection. When fan-out writes to user's feed cache, push notification via WebSocket: "5 new posts available". User clicks to refresh — Feed Service re-reads cache

- **A/B testing:** Different ranking models tested on user segments. Metrics: session duration, interactions per session, negative actions (hide, unfollow). ~1000 concurrent experiments at any time

- **Integrity signals:** Misinformation demoted in ranking. Fact-checked posts get -0.8 ranking penalty. Repeat offenders get -0.5 author-level penalty. These adjustments layered into the ranking model