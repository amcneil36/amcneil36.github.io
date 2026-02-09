# System Design: Instagram Feed


## Functional Requirements


  1. **Personalized feed** – Users see a ranked feed of posts from accounts they follow.

  2. **Chronological option** – Users can switch to a chronologically ordered feed.

  3. **Infinite scroll** – Seamless pagination as user scrolls down.

  4. **Mixed content types** – Feed contains photos, carousels, videos, reels, and sponsored posts.

  5. **Engagement metadata** – Each post shows like count, comment count, and whether the viewer has liked it.

  6. **Stories bar** – Top of feed shows stories from followed users (separate system).

  7. **Suggested posts** – After viewing followed content, show algorithmically suggested posts from non-followed accounts.


## Non-Functional Requirements


  1. **Low latency** – Feed loads in <500ms (p99).

  2. **High availability** – 99.99%; feed is the core experience.

  3. **Scalability** – 2B+ users, 500M+ daily active users opening feed.

  4. **Eventual consistency** – New posts appear within seconds, not necessarily instantly.

  5. **Bandwidth efficiency** – Lazy-load images, use appropriate resolutions.


## Flow 1: Feed Generation (Write Path – Fan-out on Write)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 400" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a1" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="150" width="90" height="55" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="55" y="175" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Author
</text>

  
<text x="55" y="190" text-anchor="middle" fill="#fff" font-size="10">
posts photo
</text>

  
<line x1="100" y1="177" x2="170" y2="177" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="170" y="150" width="100" height="55" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="220" y="182" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Post Svc
</text>

  

  
<line x1="220" y1="205" x2="220" y2="280" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="220" cy="290" rx="45" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="175" y="290" width="90" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="220" cy="315" rx="45" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="175" y1="290" x2="175" y2="315" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="265" y1="290" x2="265" y2="315" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="220" y="307" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Post DB
</text>

  

  
<line x1="270" y1="177" x2="350" y2="177" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="350" y="150" width="100" height="55" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="400" y="175" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Feed Event
</text>

  
<text x="400" y="190" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Queue
</text>

  

  
<line x1="450" y1="177" x2="530" y2="177" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="530" y="145" width="120" height="65" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="590" y="172" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Fan-out
</text>

  
<text x="590" y="187" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Service
</text>

  

  
<line x1="590" y1="145" x2="590" y2="80" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="590" cy="68" rx="45" ry="10" fill="#3f51b5" stroke="#1a237e" stroke-width="1.5">
</ellipse>

  
<rect x="545" y="40" width="90" height="28" fill="#3f51b5" stroke="#1a237e" stroke-width="1.5">
</rect>

  
<ellipse cx="590" cy="40" rx="45" ry="10" fill="#283593" stroke="#1a237e" stroke-width="1.5">
</ellipse>

  
<line x1="545" y1="40" x2="545" y2="68" stroke="#1a237e" stroke-width="1.5">
</line>

  
<line x1="635" y1="40" x2="635" y2="68" stroke="#1a237e" stroke-width="1.5">
</line>

  
<text x="590" y="58" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Follows DB
</text>

  
<text x="590" y="30" text-anchor="middle" fill="#555" font-size="8">
Get followers
</text>

  

  
<line x1="650" y1="177" x2="740" y2="120" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<line x1="650" y1="177" x2="740" y2="177" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<line x1="650" y1="177" x2="740" y2="234" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="800" cy="110" rx="50" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="750" y="110" width="100" height="22" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="800" cy="132" rx="50" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="750" y1="110" x2="750" y2="132" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="850" y1="110" x2="850" y2="132" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="800" y="125" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
User A Feed
</text>

  
<ellipse cx="800" cy="170" rx="50" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="750" y="170" width="100" height="22" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="800" cy="192" rx="50" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="750" y1="170" x2="750" y2="192" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="850" y1="170" x2="850" y2="192" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="800" y="185" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
User B Feed
</text>

  
<ellipse cx="800" cy="228" rx="50" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="750" y="228" width="100" height="22" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="800" cy="250" rx="50" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="750" y1="228" x2="750" y2="250" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="850" y1="228" x2="850" y2="250" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="800" y="243" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
User N Feed
</text>

  
<text x="870" y="185" text-anchor="start" fill="#555" font-size="9">
(Redis)
</text>


</svg>

</details>


> **
Example: Regular user posts a photo


Alice (500 followers) posts a sunset photo. Post Service saves the post to Post DB and publishes a `POST_CREATED` event to the **Feed Event Queue** (Kafka). The **Fan-out Service** consumes the event, queries the **Follows DB** to get Alice's 500 followers, and for each follower, prepends `post_id=789` to their feed in the **Feed Cache** (Redis sorted set, scored by timestamp). This takes ~200ms for 500 followers. All 500 followers now have Alice's post in their pre-computed feed.


> **
Example: Celebrity post (hybrid fan-out)


Selena Gomez (400M followers) posts a photo. Fan-out on write for 400M followers would take hours and write to 400M Redis entries. Instead, the Fan-out Service detects that Selena is a celebrity (>10K followers) and **skips fan-out**. Selena's post is stored in a **"celebrity posts" table**. When any follower opens their feed, the Feed Service merges the pre-computed feed (from Redis) with recent celebrity posts (fetched at read time) — this is fan-out on read for celebrities only.


## Flow 2: Feed Reading (Read Path)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 380" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a2" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="150" width="80" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="50" y="180" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
User
</text>

  
<line x1="90" y1="175" x2="150" y2="175" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<text x="120" y="166" text-anchor="middle" fill="#555" font-size="8">
GET /feed
</text>

  
<rect x="150" y="150" width="80" height="50" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="190" y="173" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
API
</text>

  
<text x="190" y="185" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
GW
</text>

  
<line x1="230" y1="175" x2="300" y2="175" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<rect x="300" y="148" width="110" height="55" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="355" y="180" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Feed Svc
</text>

  

  
<line x1="410" y1="165" x2="490" y2="110" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="460" y="125" text-anchor="middle" fill="#555" font-size="8">
1. Post IDs
</text>

  
<ellipse cx="545" cy="95" rx="45" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="500" y="95" width="90" height="25" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="545" cy="120" rx="45" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="500" y1="95" x2="500" y2="120" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="590" y1="95" x2="590" y2="120" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="545" y="111" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Feed Cache
</text>

  

  
<line x1="410" y1="155" x2="490" y2="55" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a2)">
</line>

  
<text x="460" y="75" text-anchor="middle" fill="#555" font-size="8">
2. Celebrity posts
</text>

  
<ellipse cx="545" cy="40" rx="50" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="495" y="15" width="100" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="545" cy="15" rx="50" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="495" y1="15" x2="495" y2="40" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="595" y1="15" x2="595" y2="40" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="545" y="32" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Celebrity Posts
</text>

  

  
<line x1="410" y1="185" x2="490" y2="195" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="460" y="208" text-anchor="middle" fill="#555" font-size="8">
3. Hydrate
</text>

  
<ellipse cx="545" cy="195" rx="45" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="500" y="195" width="90" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="545" cy="220" rx="45" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="500" y1="195" x2="500" y2="220" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="590" y1="195" x2="590" y2="220" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="545" y="212" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Post DB
</text>

  

  
<line x1="355" y1="203" x2="355" y2="270" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="355" y="250" text-anchor="start" fill="#555" font-size="8">
 4. Rank
</text>

  
<rect x="300" y="270" width="110" height="45" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="355" y="297" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Ranking Svc
</text>

  

  
<line x1="410" y1="292" x2="490" y2="292" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<rect x="490" y="270" width="100" height="45" rx="8" fill="#00bcd4" stroke="#00838f" stroke-width="2">
</rect>

  
<text x="540" y="290" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
ML Model
</text>

  
<text x="540" y="304" text-anchor="middle" fill="#fff" font-size="9">
(Feature Store)
</text>

  

  
<line x1="355" y1="315" x2="355" y2="350" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<rect x="300" y="345" width="110" height="35" rx="6" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="355" y="367" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Ad Svc
</text>

  
<text x="355" y="338" text-anchor="middle" fill="#555" font-size="8">
5. Insert ads
</text>


</svg>

</details>


> **
Example: User opens feed


Bob opens Instagram. Client sends **GET /api/v1/feed?limit=20**. The **Feed Service** executes these steps in sequence:
  
**Step 1:** Fetch Bob's pre-computed post IDs from the **Feed Cache** (Redis ZREVRANGE) — returns 200 post IDs.
  
**Step 2:** Fetch recent posts from celebrities Bob follows (fan-out on read) — returns 15 celebrity post IDs.
  
**Step 3:** Merge and **hydrate** the top 50 candidates from the **Post DB** (batch get by post_ids) — returns full post objects with captions, media URLs, like counts.
  
**Step 4:** Send the 50 candidates to the **Ranking Service**, which scores each post using an ML model. Features: post recency, user-author affinity, engagement rate, content type preference, diversity. Returns a ranked list.
  
**Step 5:** The **Ad Service** inserts sponsored posts at positions 4, 8, 16 (every ~5 posts). The top 20 items (organic + ads) are returned to the client.
  
Bob sees a personalized feed in ~300ms.


> **
Example: Pagination (infinite scroll)


Bob scrolls to post 20 and the client sends **GET /api/v1/feed?cursor=post_xyz&limit=20**. The Feed Service skips the first 20 post IDs in the cache and repeats steps 3-5 for the next batch. The cursor is the last-seen post's timestamp/score, ensuring no duplicates even if new posts are added to the feed between requests.


### Deep Dive: Components


> **
Feed Service


**Protocol:** HTTPS GET.


**Input:** `user_id` (from JWT), `cursor` (optional, for pagination), `limit` (default 20).


**Output:** `{posts: [{post_id, author: {username, avatar_url}, media_urls, caption, like_count, comment_count, is_liked, created_at}], next_cursor, has_more}`


> **
Ranking Service (ML)


Uses a multi-stage ranking pipeline: (1) **Candidate generation** — the 200+ post IDs from cache/celebrity fetch. (2) **Feature extraction** — looks up pre-computed features from a Feature Store (Redis/Cassandra): user engagement history, post engagement rate, time decay. (3) **Scoring** — lightweight gradient-boosted tree model scores each candidate. (4) **Re-ranking** — diversity injection (don't show 5 photos from same author consecutively), freshness boost.


The model is trained offline on engagement data and deployed as a real-time inference service.


> **
Fan-out Service


Asynchronous service that distributes posts to followers' feed caches. For a user with N followers, it writes N entries to Redis. Uses Kafka consumer groups for parallel processing. Follower lists are fetched from the Follows DB and cached in Redis for hot users.


**Hybrid approach detail:** Threshold = 10,000 followers. Users above this are treated as celebrities (fan-out on read). This threshold is configurable and was chosen based on analysis: 99.5% of users have <10K followers, so fan-out on write handles the vast majority without the celebrity problem.


## Overall Combined Flow


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 480" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a3" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  

  
<text x="10" y="20" fill="#1a73e8" font-size="14" font-weight="bold">
WRITE PATH
</text>

  
<rect x="10" y="35" width="80" height="40" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="50" y="59" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Author
</text>

  
<line x1="90" y1="55" x2="140" y2="55" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="140" y="35" width="80" height="40" rx="6" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="180" y="59" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Post Svc
</text>

  
<line x1="220" y1="55" x2="270" y2="55" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="270" y="35" width="80" height="40" rx="6" fill="#9c27b0" stroke="#6a1b9a" stroke-width="1.5">
</rect>

  
<text x="310" y="59" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Kafka
</text>

  
<line x1="350" y1="55" x2="400" y2="55" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="400" y="30" width="100" height="50" rx="6" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="450" y="55" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Fan-out Svc
</text>

  
<text x="450" y="67" text-anchor="middle" fill="#fff" font-size="8">
(if <10K)
</text>

  
<line x1="500" y1="55" x2="570" y2="55" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<ellipse cx="625" cy="48" rx="45" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="580" y="48" width="90" height="22" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="625" cy="70" rx="45" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="580" y1="48" x2="580" y2="70" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="670" y1="48" x2="670" y2="70" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="625" y="63" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Feed Caches
</text>

  

  
<line x1="180" y1="75" x2="180" y2="110" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<ellipse cx="180" cy="118" rx="35" ry="8" fill="#ff5722" stroke="#d84315" stroke-width="1">
</ellipse>

  
<rect x="145" y="118" width="70" height="18" fill="#ff5722" stroke="#d84315" stroke-width="1">
</rect>

  
<ellipse cx="180" cy="136" rx="35" ry="8" fill="#e64a19" stroke="#d84315" stroke-width="1">
</ellipse>

  
<line x1="145" y1="118" x2="145" y2="136" stroke="#d84315" stroke-width="1">
</line>

  
<line x1="215" y1="118" x2="215" y2="136" stroke="#d84315" stroke-width="1">
</line>

  
<text x="180" y="131" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
Post DB
</text>

  

  
<line x1="450" y1="30" x2="450" y2="0" stroke="#333" stroke-width="1">
</line>

  
<line x1="450" y1="0" x2="560" y2="0" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<ellipse cx="605" cy="-5" rx="35" ry="8" fill="#3f51b5" stroke="#1a237e" stroke-width="1">
</ellipse>

  
<rect x="570" y="-5" width="70" height="18" fill="#3f51b5" stroke="#1a237e" stroke-width="1">
</rect>

  
<ellipse cx="605" cy="13" rx="35" ry="8" fill="#283593" stroke="#1a237e" stroke-width="1">
</ellipse>

  
<line x1="570" y1="-5" x2="570" y2="13" stroke="#1a237e" stroke-width="1">
</line>

  
<line x1="640" y1="-5" x2="640" y2="13" stroke="#1a237e" stroke-width="1">
</line>

  
<text x="605" y="8" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
Follows
</text>


  

  
<text x="10" y="195" fill="#1a73e8" font-size="14" font-weight="bold">
READ PATH
</text>

  
<rect x="10" y="210" width="80" height="40" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="50" y="234" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Reader
</text>

  
<line x1="90" y1="230" x2="140" y2="230" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="140" y="210" width="70" height="40" rx="6" fill="#ff9800" stroke="#f57c00" stroke-width="1.5">
</rect>

  
<text x="175" y="234" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
API GW
</text>

  
<line x1="210" y1="230" x2="260" y2="230" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="260" y="210" width="90" height="40" rx="6" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="305" y="234" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Feed Svc
</text>

  

  
<line x1="350" y1="220" x2="580" y2="70" stroke="#333" stroke-width="1" stroke-dasharray="4,3">
</line>

  
<text x="480" y="140" text-anchor="middle" fill="#555" font-size="8">
Read post IDs
</text>

  

  
<line x1="305" y1="250" x2="305" y2="300" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<text x="305" y="290" text-anchor="start" fill="#555" font-size="8">
 Hydrate
</text>

  
<ellipse cx="305" cy="310" rx="35" ry="8" fill="#ff5722" stroke="#d84315" stroke-width="1">
</ellipse>

  
<rect x="270" y="310" width="70" height="18" fill="#ff5722" stroke="#d84315" stroke-width="1">
</rect>

  
<ellipse cx="305" cy="328" rx="35" ry="8" fill="#e64a19" stroke="#d84315" stroke-width="1">
</ellipse>

  
<line x1="270" y1="310" x2="270" y2="328" stroke="#d84315" stroke-width="1">
</line>

  
<line x1="340" y1="310" x2="340" y2="328" stroke="#d84315" stroke-width="1">
</line>

  
<text x="305" y="323" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
Post DB
</text>

  

  
<line x1="350" y1="235" x2="420" y2="235" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="420" y="215" width="90" height="40" rx="6" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="465" y="239" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Ranking
</text>

  

  
<line x1="510" y1="235" x2="570" y2="235" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="570" y="215" width="80" height="40" rx="6" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="610" y="239" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Ad Svc
</text>

  

  
<line x1="350" y1="225" x2="420" y2="170" stroke="#333" stroke-width="1" stroke-dasharray="4,3">
</line>

  
<text x="420" y="185" text-anchor="middle" fill="#555" font-size="8">
Celebrity merge
</text>


</svg>

</details>


> **
Combined Example


**Write:** Alice posts → Post Svc saves to Post DB → Kafka → Fan-out Svc gets Alice's 500 followers from Follows DB → prepends post_id to each follower's Feed Cache in Redis.  

**Read:** Bob opens app → GET /feed → Feed Svc reads Bob's Feed Cache → merges with celebrity posts → hydrates from Post DB → Ranking Svc scores using ML → Ad Svc inserts ads → returns 20 items to Bob in ~300ms.


## Database Schema


### Redis (Feed Cache)

feed:{user_id} (Sorted Set)

  
  


| Element | Score                | Description                                           |
|---------|----------------------|-------------------------------------------------------|
| post_id | timestamp (epoch ms) | Each post in the user's feed, scored by creation time |


Max 800 entries per user (ZREMRANGEBYRANK trims oldest). Commands: ZADD for writes, ZREVRANGE for reads.


### NoSQL (Cassandra)

posts (see Instagram design)


Partition by post_id. Contains full post data for hydration.


celebrity_recent_posts

  
  
  


| Field   | Type     | Key  | Description                  |
|---------|----------|------|------------------------------|
| user_id | BIGINT   | `PK` | Celebrity user ID            |
| post_id | TimeUUID | `PK` | Recent posts, sorted by time |


Stores only last 100 posts per celebrity. Read at feed fetch time for users who follow celebrities.


### SQL (PostgreSQL)

follows (see Instagram design)


Composite PK (follower_id, followee_id). B-tree index on followee_id.


## Cache Deep Dive


> **
Feed Cache (Redis Sorted Set)


**Caching strategy:** **Write-through** on the write path (Fan-out Service writes to Redis as part of fan-out). **Read-through** on cache miss (if a user's feed is evicted, reconstruct from followed users' recent posts).


**Eviction:** LRU at cluster level + per-user cap of 800 posts.


**Expiration:** TTL = 7 days for inactive users. Active users continuously refreshed by fan-out writes.


> **
Post Hydration Cache (Redis Hash)


Frequently accessed posts are cached in Redis as hashes: `post:{id} → {caption, media_urls, like_count, ...}`. **Write-behind:** Updated asynchronously when like/comment counts change. TTL = 48 hours. LRU eviction.


## CDN


> **


**CDN is critical for feed performance.** Every image/video URL in the feed response points to a CDN edge node. The client fetches images from the nearest CDN PoP. Without CDN, loading 20 images from a central S3 bucket would add 1-2 seconds of latency.


CDN is pull-based with LRU eviction and 30-day TTL. Popular posts (viral content) stay cached at the edge; old posts are evicted and re-fetched from S3 on the rare occasion someone scrolls far back.


## Scaling Considerations


  - **Load Balancer:** L7 LB in front of API Gateway. Consistent hashing for Feed Service to improve cache hit rates (same user's requests go to the same Feed Service instance, which can maintain local caches).

  - **Feed Service:** Stateless, scales horizontally. ~1000 instances for peak load.

  - **Fan-out Service:** Scale based on post creation rate. During peak hours (~6-9 PM local time), auto-scale up. Each instance processes ~10K fan-outs/sec.

  - **Redis Cluster:** ~100 nodes, each handling ~5M users' feed caches. Total memory: ~50TB.


## Tradeoffs and Deep Dives


> **
Hybrid Fan-out


The 10K follower threshold is a pragmatic balance. Below 10K: fan-out on write provides fast reads with modest write amplification. Above 10K: fan-out on read avoids writing to millions of caches but adds read latency (~50ms to merge celebrity posts). The threshold could be dynamically adjusted based on system load.


> **
Ranked vs. Chronological Feed


Instagram defaults to ranked feed (increases engagement by 5-10% per internal studies). Chronological is offered as an option. Ranked feed requires the Ranking Service (adds ~50ms latency). Chronological skips ranking (faster but lower engagement).


## Alternative Approaches


Pure Fan-out on Read


No pre-computation. At read time, fetch the latest posts from all followed users and merge. Pros: simpler write path, no storage for feed caches. Cons: extremely slow for users following 1000+ accounts (must query 1000+ user timelines). Not chosen due to unacceptable read latency.


Pure Fan-out on Write


Pre-compute feeds for all users, including celebrity followers. Pros: fastest reads. Cons: celebrity posts cause massive write amplification (400M writes for one post). Not chosen due to write cost and latency.


## Additional Information


### Feed Diversity


The Ranking Service enforces diversity rules: no more than 2 posts from the same author in a row, mix content types (photos, videos, reels), and insert "discovery" posts from non-followed accounts after the user has seen most followed content.


### Negative Signals


If a user hides a post or reports an account, these signals are fed back to the Ranking Service to downrank similar content in future feed loads.