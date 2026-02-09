# System Design: Facebook (Social Network)


## Functional Requirements


- Users can create profiles and manage personal information

- Send and accept friend requests (bidirectional friendship)

- Create posts (text, photos, videos) visible to friends or public

- Like, comment, and share posts

- View a personalized news feed of friends' posts

- Real-time chat/messaging (Messenger)

- Search for users, posts, and groups

- Groups and Pages for communities and businesses


## Non-Functional Requirements


- **Scalability:** 3+ billion monthly active users, billions of posts/day

- **Low Latency:** Feed loads in <500ms, messages in <100ms

- **High Availability:** 99.99% uptime

- **Eventual Consistency:** Acceptable for feed (seconds delay OK); strong consistency for friend relationships

- **Storage:** Petabytes of photos/videos, trillions of social graph edges


## Flow 1: Creating a Post


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 480" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#1877f2">
</polygon>
</marker>
</defs>


<rect x="20" y="200" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="80" y="235" text-anchor="middle" fill="white" font-size="13">
User Client
</text>


<rect x="190" y="200" width="130" height="60" rx="10" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="255" y="228" text-anchor="middle" fill="white" font-size="12">
API Gateway /
</text>
<text x="255" y="245" text-anchor="middle" fill="white" font-size="12">
Load Balancer
</text>


<rect x="380" y="200" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="445" y="235" text-anchor="middle" fill="white" font-size="13">
Post Service
</text>


<rect x="570" y="100" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="640" y="130" text-anchor="middle" fill="white" font-size="12">
Media Service
</text>


<rect x="570" y="200" width="140" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="640" y="230" text-anchor="middle" fill="white" font-size="12">
Fan-out Service
</text>


<rect x="570" y="300" width="140" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="640" y="325" text-anchor="middle" fill="white" font-size="11">
Notification Svc
</text>


<ellipse cx="820" cy="125" rx="60" ry="25" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="820" y="130" text-anchor="middle" fill="white" font-size="11">
Post DB
</text>


<rect x="780" y="200" width="120" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="840" y="228" text-anchor="middle" fill="white" font-size="11">
Feed Cache
</text>
<text x="840" y="243" text-anchor="middle" fill="white" font-size="10">
(Redis)
</text>


<rect x="780" y="300" width="120" height="50" rx="8" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="840" y="330" text-anchor="middle" fill="white" font-size="12">
S3 / CDN
</text>


<rect x="570" y="400" width="140" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="640" y="430" text-anchor="middle" fill="white" font-size="12">
Kafka Queue
</text>


<line x1="140" y1="230" x2="188" y2="230" stroke="#1877f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="320" y1="230" x2="378" y2="230" stroke="#1877f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="215" x2="568" y2="130" stroke="#1877f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="230" x2="568" y2="225" stroke="#1877f2" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="510" y1="245" x2="568" y2="320" stroke="#1877f2" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="710" y1="120" x2="758" y2="120" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="710" y1="225" x2="778" y2="225" stroke="#ff6b6b" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="710" y1="130" x2="778" y2="320" stroke="#ffaa00" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow1)">
</line>


<line x1="445" y1="260" x2="445" y2="370" stroke="#ffaa00" stroke-width="1.5" stroke-dasharray="5,5">
</line>
<line x1="445" y1="370" x2="568" y2="420" stroke="#ffaa00" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow1)">
</line>


<text x="550" y="30" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Facebook: Post Creation Flow
</text>


</svg>

</details>


### Step-by-Step


1. **User creates post** → `POST /v1/posts { text: "...", media_ids: [...], privacy: "friends" }`

2. **Post Service:** Validates content, creates post record in Post DB (Cassandra)

3. **Media Service:** If photos/videos attached, uploaded to S3, thumbnails generated, CDN URLs returned

4. **Fan-out Service:** Gets user's friend list from Social Graph → writes post_id to each friend's feed cache (Redis sorted set) — **fan-out on write** for users with <5000 friends

5. **For celebrities (>5000 followers):** Skip fan-out on write → handled at read time (hybrid approach)

6. **Notification Service:** Notifies close friends (if notification preferences allow)

7. **Kafka:** Post event published for async consumers (search indexing, analytics, content moderation)


> **
**Example:** Alice (500 friends) posts a photo. Post Service stores metadata in Cassandra. Photo uploaded to S3, thumbnail generated. Fan-out Service reads Alice's 500 friends from TAO (social graph) → writes Alice's post_id to 500 Redis sorted sets (one per friend's feed cache). Each write takes ~1ms. Total fan-out: ~500ms. Bob opens Facebook → his feed cache already has Alice's post. Latency for Bob: ~50ms (read from Redis).


### Deep Dive: Post Service


- **Protocol:** HTTPS (REST)

- **HTTP Method:** `POST /v1/posts`

- **Input:** `{ user_id, text, media_ids[], privacy: "friends|public|only_me", tagged_users[] }`

- **Output:** `{ post_id: "post_abc123", created_at: "...", status: "published" }`

- **Content moderation:** Async — post published immediately, ML classifier checks in background. If flagged, post hidden within seconds


### Deep Dive: Fan-out Service


- **Fan-out on Write (push model):** Pre-compute feeds. On new post, push to all friends' caches. Read is O(1) — just read from cache

- **Fan-out on Read (pull model):** Don't pre-compute. On feed request, query all friends' posts and merge. Write is O(1) but read is O(friends × posts)

- **Hybrid approach:** Fan-out on write for regular users (<5K friends). Fan-out on read for celebrities (>5K followers). At read time, merge cached posts + celebrity posts

- **Why hybrid:** A celebrity with 10M followers would create 10M writes per post (too expensive). But only a small fraction of followers are online — wasteful push


## Flow 2: Sending/Accepting Friend Requests


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ffaa00">
</polygon>
</marker>
</defs>


<rect x="20" y="140" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="80" y="175" text-anchor="middle" fill="white" font-size="13">
User A
</text>


<rect x="200" y="140" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="265" y="168" text-anchor="middle" fill="white" font-size="12">
Friend Request
</text>
<text x="265" y="185" text-anchor="middle" fill="white" font-size="12">
Service
</text>


<rect x="400" y="80" width="150" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="475" y="108" text-anchor="middle" fill="white" font-size="11">
Social Graph (TAO)
</text>


<rect x="400" y="200" width="150" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="475" y="230" text-anchor="middle" fill="white" font-size="12">
Notification Svc
</text>


<ellipse cx="700" cy="105" rx="70" ry="30" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="700" y="110" text-anchor="middle" fill="white" font-size="12">
Graph DB
</text>


<rect x="620" y="200" width="130" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="685" y="228" text-anchor="middle" fill="white" font-size="11">
Graph Cache (TAO)
</text>


<rect x="780" y="140" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="840" y="175" text-anchor="middle" fill="white" font-size="13">
User B
</text>


<line x1="140" y1="170" x2="198" y2="170" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="330" y1="160" x2="398" y2="110" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="330" y1="185" x2="398" y2="220" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="550" y1="105" x2="628" y2="105" stroke="#ff6b6b" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="550" y1="115" x2="618" y2="220" stroke="#ff6b6b" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow2)">
</line>


<line x1="550" y1="225" x2="778" y2="175" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<text x="475" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Facebook: Friend Request Flow
</text>


</svg>

</details>


### Step-by-Step


1. **User A sends friend request:** `POST /v1/friend_requests { target_user_id: B }`

2. **Friend Request Service:** Creates pending edge in Social Graph: `A → FRIEND_REQUEST → B`

3. **Notification sent** to User B (push notification + in-app)

4. **User B accepts:** `POST /v1/friend_requests/{id}/accept`

5. **Social Graph updated:** Bidirectional friendship edges created: `A ↔ FRIENDS ↔ B`. Friend request edge deleted

6. **TAO Cache invalidated** for both users' friend lists


> **
**Example:** Alice sends friend request to Bob. Social Graph stores `(Alice, FRIEND_REQUEST, Bob)`. Bob gets notification. Bob accepts → Transaction: DELETE friend_request edge, INSERT friendship edge (Alice→Bob) and (Bob→Alice). TAO cache invalidated for both. Next time either opens their friends list, updated graph is fetched.


### Deep Dive: TAO (The Associations and Objects)


- **What:** Facebook's custom graph store. Objects (users, posts) and Associations (edges: friendship, likes, comments)

- **Architecture:** MySQL backend + massive in-memory cache layer (memcached). Reads hit cache ~99.9% of the time

- **Why custom:** General-purpose graph DBs don't scale to trillions of edges. TAO is optimized for social graph patterns (friend lists, mutual friends, friend-of-friend)

- **Consistency:** Write-through cache. Writes go to MySQL first, then cache updated. Cache invalidation on writes ensures consistency


## Combined Overall Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 650" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#1877f2">
</polygon>
</marker>
</defs>


<rect x="20" y="280" width="110" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="75" y="315" text-anchor="middle" fill="white" font-size="13">
Clients
</text>


<rect x="170" y="280" width="120" height="50" rx="8" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="230" y="310" text-anchor="middle" fill="white" font-size="12">
CDN
</text>


<rect x="170" y="370" width="120" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="230" y="400" text-anchor="middle" fill="white" font-size="12">
Load Balancer
</text>


<rect x="350" y="80" width="130" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="415" y="110" text-anchor="middle" fill="white" font-size="12">
Post Service
</text>


<rect x="350" y="170" width="130" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="415" y="200" text-anchor="middle" fill="white" font-size="12">
Feed Service
</text>


<rect x="350" y="260" width="130" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="415" y="290" text-anchor="middle" fill="white" font-size="12">
User Service
</text>


<rect x="350" y="350" width="130" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="415" y="380" text-anchor="middle" fill="white" font-size="12">
Friend Service
</text>


<rect x="350" y="440" width="130" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="415" y="470" text-anchor="middle" fill="white" font-size="12">
Search Service
</text>


<rect x="350" y="530" width="130" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="415" y="560" text-anchor="middle" fill="white" font-size="12">
Messenger
</text>


<rect x="560" y="80" width="130" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="625" y="110" text-anchor="middle" fill="white" font-size="12">
Fan-out Svc
</text>


<rect x="560" y="170" width="130" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="625" y="200" text-anchor="middle" fill="white" font-size="12">
Ranking Svc
</text>


<rect x="560" y="260" width="130" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="625" y="288" text-anchor="middle" fill="white" font-size="11">
Social Graph (TAO)
</text>


<rect x="560" y="350" width="130" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="625" y="380" text-anchor="middle" fill="white" font-size="12">
Notification Svc
</text>


<rect x="560" y="440" width="130" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="625" y="470" text-anchor="middle" fill="white" font-size="12">
Kafka Bus
</text>


<rect x="770" y="80" width="110" height="40" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="825" y="105" text-anchor="middle" fill="white" font-size="11">
Feed Cache
</text>


<ellipse cx="825" cy="190" rx="60" ry="25" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="825" y="195" text-anchor="middle" fill="white" font-size="11">
Post DB
</text>


<ellipse cx="825" cy="280" rx="60" ry="25" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="825" y="285" text-anchor="middle" fill="white" font-size="11">
User DB
</text>


<ellipse cx="825" cy="370" rx="60" ry="25" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="825" y="375" text-anchor="middle" fill="white" font-size="11">
Graph DB
</text>


<rect x="770" y="430" width="110" height="40" rx="8" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="825" y="455" text-anchor="middle" fill="white" font-size="11">
S3 (Media)
</text>


<rect x="950" y="160" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="1010" y="185" text-anchor="middle" fill="white" font-size="11">
Elasticsearch
</text>


<line x1="130" y1="300" x2="168" y2="300" stroke="#1877f2" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="130" y1="320" x2="168" y2="390" stroke="#1877f2" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="290" y1="395" x2="348" y2="105" stroke="#1877f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="290" y1="395" x2="348" y2="195" stroke="#1877f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="290" y1="395" x2="348" y2="285" stroke="#1877f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="290" y1="395" x2="348" y2="375" stroke="#1877f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="290" y1="395" x2="348" y2="465" stroke="#1877f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="290" y1="395" x2="348" y2="555" stroke="#1877f2" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="480" y1="105" x2="558" y2="105" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="480" y1="195" x2="558" y2="195" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="480" y1="285" x2="558" y2="285" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="690" y1="100" x2="768" y2="100" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="690" y1="195" x2="763" y2="190" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="690" y1="280" x2="763" y2="280" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="690" y1="285" x2="763" y2="370" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="885" y1="190" x2="948" y2="180" stroke="#ff6b6b" stroke-width="1" stroke-dasharray="5,5" marker-end="url(#arrow3)">
</line>


<text x="600" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Facebook: Combined Overall Architecture
</text>


</svg>

</details>


> **
**Example — Full lifecycle:** Alice signs up (User Service → User DB) → adds Bob as friend (Friend Service → TAO → Graph DB) → creates a post with photo (Post Service → Post DB + S3) → Fan-out Service pushes post to 500 friends' feed caches → Notification Service alerts close friends → Bob opens app → Feed Service reads from feed cache → Ranking Service scores and orders posts → Bob sees Alice's post → Bob likes it (Post Service → like counter updated, Kafka event) → Elasticsearch indexes the post for search.


## Database Schema


### SQL — MySQL (Users, Core Entities — via TAO abstraction)


| Table           | Column       | Type                 | Details |
|-----------------|--------------|----------------------|---------|
| **users**       | user_id      | BIGINT               | `PK`    |
| username        | VARCHAR(50)  | `IDX`                |         |
| email           | VARCHAR(255) | `IDX`                |         |
| display_name    | VARCHAR(100) |                      |         |
| profile_pic_url | TEXT         | CDN URL              |         |
| friend_count    | INT          | Denormalized counter |         |


### NoSQL — Cassandra (Posts, Feed)


| Table          | Column     | Type                     | Details |
|----------------|------------|--------------------------|---------|
| **posts**      | post_id    | BIGINT (Snowflake)       | `PK`    |
| user_id        | BIGINT     | `IDX`                    |         |
| content_text   | TEXT       |                          |         |
| media_urls     | LIST<TEXT> | S3 URLs                  |         |
| privacy        | TEXT       | friends, public, only_me |         |
| like_count     | COUNTER    | Denormalized             |         |
| created_at     | TIMESTAMP  |                          |         |
| **user_posts** | user_id    | BIGINT                   | `PK`    |
| post_id        | BIGINT     | `PK` (DESC)              |         |
| created_at     | TIMESTAMP  |                          |         |


### Social Graph — TAO (MySQL + Memcached)


| Table            | Column    | Type                                   | Details          |
|------------------|-----------|----------------------------------------|------------------|
| **associations** | id1       | BIGINT                                 | `PK` Source node |
| assoc_type       | INT       | `PK` (FRIENDSHIP=1, LIKE=2, COMMENT=3) |                  |
| id2              | BIGINT    | `PK` Target node                       |                  |
| created_at       | TIMESTAMP |                                        |                  |


### Feed Cache — Redis


| Key              | Type       | Value                                         |
|------------------|------------|-----------------------------------------------|
| `feed:{user_id}` | Sorted Set | post_ids scored by timestamp. Max 800 entries |


Sharding Strategy


- **users:** Shard by `user_id` (consistent hashing). Billions of users across thousands of MySQL shards

- **posts (Cassandra):** Partition by `post_id`. user_posts table partitioned by `user_id` for "view my posts" queries

- **TAO associations:** Shard by `id1` — all associations for a user co-located. Friend list query is shard-local

- **Feed cache (Redis):** Shard by `user_id` consistent hashing across Redis cluster


### Denormalization


- `users.friend_count` — denormalized from association count. Updated atomically on friend add/remove

- `posts.like_count` — Cassandra counter column. Avoids counting association edges for every post render

- Feed cache is itself a denormalized view — precomputed subset of posts relevant to each user


### Indexes


- `users.username`, `users.email` — Unique B-tree indexes for login and search

- `posts.user_id` — For "user's timeline" queries (all posts by user_id)

- TAO: `(id1, assoc_type)` composite index — "all friends of user X" = query with id1=X, assoc_type=FRIENDSHIP


## Cache Deep Dive


| Cache                  | Strategy                | Eviction                      | TTL                         | Purpose                             |
|------------------------|-------------------------|-------------------------------|-----------------------------|-------------------------------------|
| TAO Cache (Memcached)  | Write-through           | LRU                           | None (invalidated on write) | Social graph lookups (friend lists) |
| Feed Cache (Redis)     | Write-through (fan-out) | Capped sorted set (800 items) | None                        | Pre-computed feeds                  |
| Post Cache (Memcached) | Read-through            | LRU                           | 1 hour                      | Recently viewed posts               |
| Session Cache (Redis)  | Write-through           | TTL-based                     | 30 days                     | User sessions/auth tokens           |


### CDN Deep Dive


- **What's cached:** All photos, videos, profile pictures, static assets (JS/CSS). Facebook operates its own CDN (PoP network)

- **Strategy:** Push-based for popular content. Pull-based for long-tail content

- **Photo serving:** Multiple resolutions stored (thumbnail, medium, full). CDN serves appropriate size based on client request

- **TTL:** Media: 365 days (immutable, content-addressed). Static assets: 30 days with cache-busting hashes in filenames

- **Haystack:** Facebook's custom photo storage — optimizes for "write once, read often, never modify, rarely delete". Reduces metadata overhead per photo vs traditional filesystems


## Scaling Considerations


- **TAO:** Hierarchical caching — leader cache (per-region) + follower caches (per-DC). 99.9% hit rate. Write fanout to leader, async propagation to followers

- **Multi-region:** Each geographic region has full stack (web servers, caches, DB replicas). MySQL replication across regions with eventual consistency. Strong consistency within primary region

- **Load balancing:** L4 (TCP) at edge, L7 (HTTP) within DC. Edge LB distributes across DCs. ECMP routing for even distribution

- **Fan-out workers:** Scaled based on post creation rate. During peak (e.g., New Year's), auto-scale fan-out workers. Use priority queues — fan-out for active users first

- **Photo storage:** Haystack — stores multiple photos per physical file. One disk seek per photo vs multiple for POSIX (no inode lookup). Handles billions of photos


## Tradeoffs & Deep Dives


> **
✅ Hybrid Fan-out
- Balances write amplification (celebrities) with read performance (regular users)
- Feed reads are O(1) for most users
- Only ~1% of users are celebrities — majority benefit from push model


❌ Hybrid Complexity
- Two code paths: push + pull + merge at read time
- Feed staleness: cached feed may miss posts from delayed fan-out
- Celebrity post merging adds ~50ms to feed read


### Pub/Sub Deep Dive


- **Kafka:** Used for async event processing — post events, like events, comment events. Topics partitioned by user_id for ordering

- **Consumers:** Search indexing (Elasticsearch), analytics pipeline, content moderation ML, notification batching

- **Real-time:** Facebook uses MQTT for mobile push. Lightweight protocol optimized for mobile (low bandwidth, battery efficient)


## Alternative Approaches


- **Pure fan-out on read:** No pre-computation. Every feed request queries all friends' posts. Simpler write path but O(friends) read complexity. Works for small-scale but not 3B users

- **Graph database (Neo4j):** Instead of TAO. Better for complex graph queries (friend-of-friend, mutual friends). But doesn't scale to Facebook's size without custom sharding

- **Event sourcing for posts:** Store all events (create, edit, delete) rather than current state. Enable undo, versioning. Higher storage cost, more complex reads

- **CRDT for counters:** Instead of centralized like counters, use CRDT (Conflict-free Replicated Data Types) for eventual-consistent distributed counting without coordination. Lower latency but approximate counts during convergence


## Additional Information


- **Privacy enforcement:** Every post read checks viewer's relationship to author via TAO. "Friends only" post requires friendship edge check. This is the critical hot path — must be <1ms (served from TAO cache)

- **Content moderation:** ML pipeline processes every post/comment. Text classifiers, image classifiers (nudity, violence, hate speech). Runs async but within seconds of posting

- **Real-time features:** Online status via heartbeat (MQTT ping every 30s). Typing indicators via MQTT pub/sub. Message delivery receipts (sent/delivered/read) via MQTT

- **Snowflake IDs:** 64-bit IDs = timestamp (41 bits, ~69 years) + datacenter (5 bits) + machine (5 bits) + sequence (12 bits, 4096/ms). Time-ordered, globally unique, no coordination needed