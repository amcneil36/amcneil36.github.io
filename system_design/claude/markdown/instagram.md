# System Design: Instagram


## Functional Requirements


  1. **Upload photos/videos** – Users can upload photos and short videos with captions, filters, and tags.

  2. **View feed** – Users see a personalized feed of posts from people they follow.

  3. **Follow/unfollow users** – Users can follow other users to see their content.

  4. **Like and comment** – Users can like posts and leave comments.

  5. **User profile** – Users have profiles with bio, avatar, follower/following counts, and a grid of their posts.

  6. **Search** – Users can search for other users, hashtags, and locations.

  7. **Stories** – Users can post ephemeral content that disappears after 24 hours.

  8. **Direct messaging** – Users can send private messages (text, photos, videos).

  9. **Explore page** – Content discovery through algorithmically curated posts.

  10. **Notifications** – Users receive notifications for likes, comments, follows, mentions.


## Non-Functional Requirements


  1. **High availability** – 99.99% uptime; read-heavy workload (100:1 read-to-write ratio).

  2. **Low latency** – Feed loads in <500ms; image loads in <200ms.

  3. **Scalability** – 2B+ monthly active users, 100M+ photos uploaded daily.

  4. **Eventual consistency** – Acceptable for feeds and like counts; strong consistency for follows and user data.

  5. **Durability** – Zero data loss for uploaded photos/videos.

  6. **Cost efficiency** – Optimize storage costs for petabytes of media.


## Flow 1: Uploading a Post


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 450" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a1" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="170" width="90" height="60" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="55" y="205" text-anchor="middle" fill="#fff" font-size="13" font-weight="bold">
User
</text>

  
<line x1="100" y1="200" x2="170" y2="200" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<text x="135" y="190" text-anchor="middle" fill="#555" font-size="9">
HTTPS POST
</text>

  
<rect x="170" y="170" width="90" height="60" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="215" y="198" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API
</text>

  
<text x="215" y="212" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Gateway
</text>

  
<line x1="260" y1="200" x2="330" y2="200" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="330" y="170" width="110" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="385" y="198" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Post
</text>

  
<text x="385" y="213" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Service
</text>

  

  
<line x1="385" y1="170" x2="385" y2="80" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="330" y="20" width="110" height="50" rx="8" fill="#00bcd4" stroke="#00838f" stroke-width="2">
</rect>

  
<text x="385" y="50" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Media Svc
</text>

  

  
<line x1="440" y1="45" x2="530" y2="45" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="530" y="25" width="90" height="40" rx="6" fill="#795548" stroke="#4e342e" stroke-width="2">
</rect>

  
<text x="575" y="50" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
S3
</text>

  

  
<line x1="620" y1="45" x2="700" y2="45" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="700" y="25" width="80" height="40" rx="6" fill="#607d8b" stroke="#37474f" stroke-width="2">
</rect>

  
<text x="740" y="50" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
CDN
</text>

  

  
<line x1="385" y1="230" x2="385" y2="310" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<ellipse cx="385" cy="320" rx="50" ry="12" fill="#ff5722" stroke="#d84315" stroke-width="2">
</ellipse>

  
<rect x="335" y="320" width="100" height="35" fill="#ff5722" stroke="#d84315" stroke-width="2">
</rect>

  
<ellipse cx="385" cy="355" rx="50" ry="12" fill="#e64a19" stroke="#d84315" stroke-width="2">
</ellipse>

  
<line x1="335" y1="320" x2="335" y2="355" stroke="#d84315" stroke-width="2">
</line>

  
<line x1="435" y1="320" x2="435" y2="355" stroke="#d84315" stroke-width="2">
</line>

  
<text x="385" y="342" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Post DB
</text>

  

  
<line x1="440" y1="200" x2="530" y2="200" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<text x="485" y="190" text-anchor="middle" fill="#555" font-size="9">
Fan-out event
</text>

  
<rect x="530" y="170" width="110" height="60" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="585" y="198" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Message
</text>

  
<text x="585" y="213" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Queue
</text>

  

  
<line x1="640" y1="200" x2="720" y2="200" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="720" y="170" width="120" height="60" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="780" y="198" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Fan-out
</text>

  
<text x="780" y="213" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Service
</text>

  

  
<line x1="780" y1="230" x2="780" y2="310" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<ellipse cx="780" cy="320" rx="55" ry="12" fill="#009688" stroke="#00695c" stroke-width="2">
</ellipse>

  
<rect x="725" y="320" width="110" height="35" fill="#009688" stroke="#00695c" stroke-width="2">
</rect>

  
<ellipse cx="780" cy="355" rx="55" ry="12" fill="#00796b" stroke="#00695c" stroke-width="2">
</ellipse>

  
<line x1="725" y1="320" x2="725" y2="355" stroke="#00695c" stroke-width="2">
</line>

  
<line x1="835" y1="320" x2="835" y2="355" stroke="#00695c" stroke-width="2">
</line>

  
<text x="780" y="340" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Feed Cache
</text>

  
<text x="780" y="375" text-anchor="middle" fill="#555" font-size="9">
(Redis)
</text>


</svg>

</details>


> **
Example: User uploads a photo


Alice selects a photo from her camera roll, applies a filter, adds caption "Sunset vibes 🌅 #travel", and taps Share. The client sends an **HTTPS POST** (multipart/form-data) to the **API Gateway**, which authenticates the request via JWT and routes it to the **Post Service**. The Post Service calls the **Media Service** to upload the image: it resizes the image into multiple resolutions (150px thumbnail, 640px medium, 1080px full), stores all versions in **S3**, and returns the media URLs. The Post Service creates a post record in the **Post DB** (PostgreSQL) with the media URLs, caption, hashtags, and timestamp. It then publishes a `POST_CREATED` event to the **Message Queue** (Kafka). The **Fan-out Service** consumes this event, fetches Alice's follower list, and prepends Alice's post_id to each follower's feed in the **Feed Cache** (Redis). Alice's 500 followers will now see the post when they open their feed.


### Deep Dive: Flow 1 Components


> **
API Gateway


Entry point for all client requests. Handles authentication (JWT token validation), rate limiting, request routing, and SSL termination. Routes requests to appropriate microservices based on URL path.


**Protocol:** HTTPS (REST). For uploads: POST with multipart/form-data.


> **
Post Service


Manages post creation, deletion, and retrieval.


**Endpoints:**


  - `POST /api/v1/posts` – Create a new post. Input: image/video file, caption, hashtags, location. Output: `{post_id, media_urls, created_at}`

  - `GET /api/v1/posts/{post_id}` – Get a single post. Output: `{post_id, user_id, media_urls, caption, like_count, comment_count, created_at}`

  - `DELETE /api/v1/posts/{post_id}` – Delete a post.


> **
Media Service


Handles image/video processing: resizing, compression, format conversion (WebP for images, HLS/DASH for videos), and storage.


**Protocol:** Internal gRPC from Post Service. HTTPS PUT to S3.


**Input:** Raw binary image/video. **Output:** List of media URLs (thumbnail, medium, full resolution).


> **
Fan-out Service


Consumes `POST_CREATED` events from Kafka and distributes the post to followers' feeds. Uses a **hybrid fan-out** approach:


  - **Fan-out on write** for regular users (<10K followers): Prepend post_id to each follower's feed in Redis. Fast feed reads.

  - **Fan-out on read** for celebrities (>10K followers): Don't pre-compute; merge celebrity posts at feed read time. Avoids writing to millions of feed caches.


> **
CDN


**Why CDN is critical:** Instagram is image-heavy. Serving images from S3 directly would be too slow and costly. CDN caches images at 200+ edge locations globally, reducing latency from ~500ms to ~50ms.


**Caching strategy:** Pull-based (origin pull from S3). First request fetches from S3 and caches at edge.


**Eviction policy:** LRU with size-aware eviction (larger files evicted first when cache is full).


**Expiration policy:** TTL = 30 days for images (they don't change), 1 year for profile avatars with cache-busting URL parameters on update.


## Flow 2: Viewing the Feed


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a2" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="140" width="90" height="60" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="55" y="175" text-anchor="middle" fill="#fff" font-size="13" font-weight="bold">
User
</text>

  
<line x1="100" y1="170" x2="170" y2="170" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<text x="135" y="160" text-anchor="middle" fill="#555" font-size="9">
HTTPS GET
</text>

  
<rect x="170" y="140" width="90" height="60" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="215" y="168" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API
</text>

  
<text x="215" y="182" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Gateway
</text>

  
<line x1="260" y1="170" x2="340" y2="170" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<rect x="340" y="140" width="110" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="395" y="168" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Feed
</text>

  
<text x="395" y="183" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Service
</text>

  

  
<line x1="450" y1="170" x2="530" y2="170" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<text x="490" y="160" text-anchor="middle" fill="#555" font-size="9">
1. Get post IDs
</text>

  
<ellipse cx="590" cy="160" rx="50" ry="12" fill="#009688" stroke="#00695c" stroke-width="2">
</ellipse>

  
<rect x="540" y="160" width="100" height="35" fill="#009688" stroke="#00695c" stroke-width="2">
</rect>

  
<ellipse cx="590" cy="195" rx="50" ry="12" fill="#00796b" stroke="#00695c" stroke-width="2">
</ellipse>

  
<line x1="540" y1="160" x2="540" y2="195" stroke="#00695c" stroke-width="2">
</line>

  
<line x1="640" y1="160" x2="640" y2="195" stroke="#00695c" stroke-width="2">
</line>

  
<text x="590" y="182" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Feed Cache
</text>

  

  
<line x1="395" y1="200" x2="395" y2="280" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<text x="395" y="250" text-anchor="start" fill="#555" font-size="9">
 2. Hydrate
</text>

  
<ellipse cx="395" cy="290" rx="50" ry="12" fill="#ff5722" stroke="#d84315" stroke-width="2">
</ellipse>

  
<rect x="345" y="290" width="100" height="30" fill="#ff5722" stroke="#d84315" stroke-width="2">
</rect>

  
<ellipse cx="395" cy="320" rx="50" ry="12" fill="#e64a19" stroke="#d84315" stroke-width="2">
</ellipse>

  
<line x1="345" y1="290" x2="345" y2="320" stroke="#d84315" stroke-width="2">
</line>

  
<line x1="445" y1="290" x2="445" y2="320" stroke="#d84315" stroke-width="2">
</line>

  
<text x="395" y="310" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Post DB
</text>

  

  
<line x1="395" y1="140" x2="395" y2="70" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<rect x="340" y="20" width="110" height="50" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="395" y="50" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Ranking Svc
</text>

  
<text x="395" y="68" text-anchor="middle" fill="#555" font-size="9">
3. Rank & filter
</text>

  

  
<line x1="740" y1="170" x2="820" y2="170" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<text x="780" y="160" text-anchor="middle" fill="#555" font-size="9">
4. Images
</text>

  
<rect x="820" y="140" width="80" height="60" rx="8" fill="#607d8b" stroke="#37474f" stroke-width="2">
</rect>

  
<text x="860" y="175" text-anchor="middle" fill="#fff" font-size="13" font-weight="bold">
CDN
</text>

  
<line x1="640" y1="170" x2="740" y2="170" stroke="#aaa" stroke-width="1" stroke-dasharray="5,3">
</line>


</svg>

</details>


> **
Example: User opens Instagram home feed


Bob opens the Instagram app. The client sends **GET /api/v1/feed?cursor=<last_seen_id>&limit=20** to the **API Gateway**, which routes to the **Feed Service**. The Feed Service retrieves Bob's pre-computed list of post IDs from the **Feed Cache** (Redis list). It then **hydrates** these post IDs by fetching full post details (caption, like count, media URLs, author info) from the **Post DB** with a multi-get query. If Bob follows celebrities, the Feed Service also fetches recent posts from those celebrities at read time (fan-out on read) and merges them. The combined list is sent to the **Ranking Service**, which re-ranks posts based on engagement signals (likes, comments, user affinity). The top 20 posts are returned to the client. The client then loads images directly from the **CDN** using the media URLs.


> **
Example: Infinite scroll (pagination)


Bob scrolls to the bottom of his feed and the client requests the next page: **GET /api/v1/feed?cursor=post_12345&limit=20**. The Feed Service uses the cursor (last seen post_id) to fetch the next 20 post IDs from Redis (LRANGE command with offset). Hydration and ranking proceed as before.


> **
Feed Service


**Protocol:** HTTPS GET.


**Input:** user_id (from JWT), cursor (for pagination), limit.


**Output:** `{posts: [{post_id, user_id, username, avatar_url, media_urls, caption, like_count, comment_count, created_at, is_liked}], next_cursor}`


Uses cursor-based pagination (not offset-based) for consistency in a constantly updating feed.


> **
Ranking Service


ML-based service that scores and ranks feed items. Features include: recency, engagement rate, user-author affinity (how often Bob interacts with Alice's posts), content type preference, and diversity. Runs a lightweight model for real-time scoring.


## Flow 3: Liking a Post


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a3" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="130" width="90" height="60" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="55" y="165" text-anchor="middle" fill="#fff" font-size="13" font-weight="bold">
User
</text>

  
<line x1="100" y1="160" x2="180" y2="160" stroke="#333" stroke-width="2" marker-end="url(#a3)">
</line>

  
<text x="140" y="150" text-anchor="middle" fill="#555" font-size="9">
POST /like
</text>

  
<rect x="180" y="130" width="90" height="60" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="225" y="158" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API
</text>

  
<text x="225" y="172" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Gateway
</text>

  
<line x1="270" y1="160" x2="350" y2="160" stroke="#333" stroke-width="2" marker-end="url(#a3)">
</line>

  
<rect x="350" y="130" width="110" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="405" y="158" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Like
</text>

  
<text x="405" y="173" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Service
</text>

  

  
<line x1="405" y1="190" x2="405" y2="260" stroke="#333" stroke-width="2" marker-end="url(#a3)">
</line>

  
<ellipse cx="405" cy="270" rx="50" ry="12" fill="#ff5722" stroke="#d84315" stroke-width="2">
</ellipse>

  
<rect x="355" y="270" width="100" height="30" fill="#ff5722" stroke="#d84315" stroke-width="2">
</rect>

  
<ellipse cx="405" cy="300" rx="50" ry="12" fill="#e64a19" stroke="#d84315" stroke-width="2">
</ellipse>

  
<line x1="355" y1="270" x2="355" y2="300" stroke="#d84315" stroke-width="2">
</line>

  
<line x1="455" y1="270" x2="455" y2="300" stroke="#d84315" stroke-width="2">
</line>

  
<text x="405" y="290" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Like DB
</text>

  

  
<line x1="460" y1="160" x2="540" y2="160" stroke="#333" stroke-width="2" marker-end="url(#a3)">
</line>

  
<ellipse cx="600" cy="150" rx="50" ry="12" fill="#009688" stroke="#00695c" stroke-width="2">
</ellipse>

  
<rect x="550" y="150" width="100" height="30" fill="#009688" stroke="#00695c" stroke-width="2">
</rect>

  
<ellipse cx="600" cy="180" rx="50" ry="12" fill="#00796b" stroke="#00695c" stroke-width="2">
</ellipse>

  
<line x1="550" y1="150" x2="550" y2="180" stroke="#00695c" stroke-width="2">
</line>

  
<line x1="650" y1="150" x2="650" y2="180" stroke="#00695c" stroke-width="2">
</line>

  
<text x="600" y="168" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Count Cache
</text>

  
<text x="600" y="195" text-anchor="middle" fill="#555" font-size="9">
INCR post:{id}:likes
</text>

  

  
<line x1="405" y1="130" x2="405" y2="60" stroke="#333" stroke-width="2" marker-end="url(#a3)">
</line>

  
<rect x="350" y="10" width="110" height="50" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="405" y="40" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Notif Queue
</text>


</svg>

</details>


> **
Example: User likes a post


Bob double-taps Alice's sunset photo. The client sends **POST /api/v1/posts/{post_id}/like** with `{user_id: bob_id}` to the API Gateway → **Like Service**. The Like Service writes a like record to the **Like DB** (Cassandra: partition by post_id, clustering by user_id) to prevent duplicate likes. It atomically increments the like count in the **Count Cache** (Redis INCR). It also publishes a `POST_LIKED` event to the **Notification Queue** (Kafka) so Alice receives a "Bob liked your photo" notification.


> **
Example: User unlikes a post


Bob taps the heart again to unlike. The client sends **DELETE /api/v1/posts/{post_id}/like**. The Like Service deletes the like record from the Like DB and decrements the count in Redis (DECR). No notification is sent for unlikes.


## Overall Combined Flow


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 600" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a4" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  

  
<rect x="10" y="250" width="80" height="55" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="50" y="282" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Client
</text>

  

  
<line x1="90" y1="278" x2="150" y2="278" stroke="#333" stroke-width="2" marker-end="url(#a4)">
</line>

  
<rect x="150" y="250" width="80" height="55" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="190" y="275" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
API
</text>

  
<text x="190" y="288" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Gateway
</text>

  

  
<line x1="230" y1="260" x2="300" y2="160" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<rect x="300" y="130" width="100" height="50" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="350" y="160" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Post Svc
</text>

  

  
<line x1="230" y1="278" x2="300" y2="278" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<rect x="300" y="253" width="100" height="50" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="350" y="283" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Feed Svc
</text>

  

  
<line x1="230" y1="295" x2="300" y2="380" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<rect x="300" y="360" width="100" height="50" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="350" y="390" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Like Svc
</text>

  

  
<line x1="230" y1="250" x2="300" y2="65" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<rect x="300" y="40" width="100" height="50" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="350" y="70" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
User Svc
</text>

  

  
<line x1="350" y1="130" x2="350" y2="40" stroke="#aaa" stroke-width="1" stroke-dasharray="4,3">
</line>

  
<line x1="400" y1="55" x2="480" y2="55" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<rect x="480" y="30" width="90" height="50" rx="8" fill="#00bcd4" stroke="#00838f" stroke-width="2">
</rect>

  
<text x="525" y="60" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Media Svc
</text>

  

  
<line x1="570" y1="55" x2="630" y2="55" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<rect x="630" y="40" width="60" height="30" rx="5" fill="#795548" stroke="#4e342e" stroke-width="1.5">
</rect>

  
<text x="660" y="60" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
S3
</text>

  

  
<line x1="690" y1="55" x2="740" y2="55" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<rect x="740" y="40" width="60" height="30" rx="5" fill="#607d8b" stroke="#37474f" stroke-width="1.5">
</rect>

  
<text x="770" y="60" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
CDN
</text>

  

  
<line x1="400" y1="155" x2="480" y2="155" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<ellipse cx="530" cy="148" rx="40" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="490" y="148" width="80" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="530" cy="173" rx="40" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="490" y1="148" x2="490" y2="173" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="570" y1="148" x2="570" y2="173" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="530" y="165" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Post DB
</text>

  

  
<line x1="400" y1="170" x2="480" y2="220" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<rect x="480" y="200" width="100" height="45" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="530" y="227" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Kafka
</text>

  

  
<line x1="580" y1="222" x2="650" y2="222" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<rect x="650" y="200" width="100" height="45" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="700" y="227" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Fan-out Svc
</text>

  

  
<line x1="400" y1="278" x2="480" y2="278" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<ellipse cx="530" cy="270" rx="40" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="490" y="270" width="80" height="25" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="530" cy="295" rx="40" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="490" y1="270" x2="490" y2="295" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="570" y1="270" x2="570" y2="295" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="530" y="287" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Feed Cache
</text>

  

  
<line x1="700" y1="245" x2="700" y2="280" stroke="#333" stroke-width="1.5">
</line>

  
<line x1="700" y1="280" x2="570" y2="280" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  

  
<line x1="400" y1="385" x2="480" y2="385" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<ellipse cx="530" cy="378" rx="40" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="490" y="378" width="80" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="530" cy="403" rx="40" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="490" y1="378" x2="490" y2="403" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="570" y1="378" x2="570" y2="403" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="530" y="395" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Like DB
</text>

  

  
<line x1="400" y1="370" x2="480" y2="340" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<ellipse cx="530" cy="328" rx="45" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="485" y="328" width="90" height="22" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="530" cy="350" rx="45" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="485" y1="328" x2="485" y2="350" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="575" y1="328" x2="575" y2="350" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="530" y="343" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Count Cache
</text>

  

  
<line x1="400" y1="65" x2="480" y2="100" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>

  
<ellipse cx="530" cy="95" rx="40" ry="10" fill="#3f51b5" stroke="#1a237e" stroke-width="1.5">
</ellipse>

  
<rect x="490" y="95" width="80" height="25" fill="#3f51b5" stroke="#1a237e" stroke-width="1.5">
</rect>

  
<ellipse cx="530" cy="120" rx="40" ry="10" fill="#283593" stroke="#1a237e" stroke-width="1.5">
</ellipse>

  
<line x1="490" y1="95" x2="490" y2="120" stroke="#1a237e" stroke-width="1.5">
</line>

  
<line x1="570" y1="95" x2="570" y2="120" stroke="#1a237e" stroke-width="1.5">
</line>

  
<text x="530" y="112" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
User DB
</text>

  

  
<rect x="650" y="260" width="100" height="40" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="700" y="285" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Ranking Svc
</text>

  
<line x1="570" y1="282" x2="650" y2="282" stroke="#aaa" stroke-width="1" stroke-dasharray="4,3">
</line>

  

  
<rect x="650" y="360" width="100" height="40" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="700" y="385" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Notif Svc
</text>

  
<line x1="530" y1="222" x2="530" y2="360" stroke="#aaa" stroke-width="1" stroke-dasharray="4,3">
</line>

  
<line x1="530" y1="360" x2="530" y2="385" stroke="#aaa" stroke-width="1" stroke-dasharray="4,3">
</line>

  
<line x1="580" y1="385" x2="650" y2="385" stroke="#333" stroke-width="1.5" marker-end="url(#a4)">
</line>


</svg>

</details>


> **
Combined Example


Alice uploads a photo → Post Service stores metadata in Post DB, media in S3/CDN, triggers fan-out via Kafka → Fan-out Service populates followers' Feed Caches. Bob opens Instagram → Feed Service reads post IDs from Feed Cache, hydrates from Post DB, ranks via Ranking Service, returns feed. Bob double-taps Alice's photo → Like Service writes to Like DB, increments Count Cache, publishes notification event → Notification Service sends "Bob liked your photo" to Alice.


## Database Schema


### SQL Tables (PostgreSQL)


1. users

  
  
  
  
  
  
  
  
  
  
  
  


| Field           | Type         | Key   | Description                   |
|-----------------|--------------|-------|-------------------------------|
| user_id         | BIGINT       | `PK`  | Auto-increment unique user ID |
| username        | VARCHAR(30)  | `IDX` | Unique username               |
| email           | VARCHAR(255) | `IDX` | User email                    |
| password_hash   | VARCHAR(255) |       | Bcrypt hashed password        |
| display_name    | VARCHAR(100) |       | Display name                  |
| bio             | TEXT         |       | User biography                |
| avatar_url      | TEXT         |       | Profile picture CDN URL       |
| follower_count  | INT          |       | Denormalized follower count   |
| following_count | INT          |       | Denormalized following count  |
| post_count      | INT          |       | Denormalized post count       |
| created_at      | TIMESTAMP    |       | Registration time             |


**Why SQL:** User data requires strong consistency (unique username/email enforcement), ACID transactions for registration, and relational queries for profile data.


**Index:** Hash index on `username` and `email` for O(1) lookups during login and registration. No range queries needed on these fields.


**Denormalization:** `follower_count`, `following_count`, and `post_count` are denormalized from the follows and posts tables. This avoids expensive COUNT(*) queries on every profile view. The counts are updated via atomic increment/decrement when follows/posts are created/deleted.


**Sharding:** Hash-based sharding on `user_id`. Username lookups use a global secondary index or a separate username→user_id mapping table.


**Read/Write:** Written on registration, profile update. Read on login, profile view, feed hydration (author info).


2. follows

  
  
  
  


| Field       | Type      | Key        | Description              |
|-------------|-----------|------------|--------------------------|
| follower_id | BIGINT    | `PK`  `FK` | The user who follows     |
| followee_id | BIGINT    | `PK`  `FK` | The user being followed  |
| created_at  | TIMESTAMP |            | When the follow happened |


**Index:** Composite PK on (`follower_id`, `followee_id`) prevents duplicate follows. Additional **B-tree index** on `followee_id` for "get all followers of user X" queries (used by fan-out service).


**Why SQL:** Relational data with strong consistency requirements. Must prevent duplicate follows.


### NoSQL Tables (Cassandra)


3. posts

  
  
  
  
  
  
  
  
  
  


| Field         | Type             | Key  | Description                |
|---------------|------------------|------|----------------------------|
| post_id       | UUID (Snowflake) | `PK` | Globally unique post ID    |
| user_id       | BIGINT           |      | Author's user ID           |
| media_urls    | LIST<TEXT>       |      | CDN URLs for images/videos |
| caption       | TEXT             |      | Post caption               |
| hashtags      | SET<TEXT>        |      | Extracted hashtags         |
| location      | TEXT             |      | Location tag               |
| like_count    | COUNTER          |      | Denormalized like count    |
| comment_count | COUNTER          |      | Denormalized comment count |
| created_at    | TIMESTAMP        |      | Post creation time         |


**Why NoSQL:** Posts are read-heavy (billions of reads/day from feeds). Cassandra provides low-latency reads with horizontal scaling. Each post is self-contained (no joins needed).


4. user_posts (timeline table)

  
  
  


| Field   | Type     | Key  | Description                         |
|---------|----------|------|-------------------------------------|
| user_id | BIGINT   | `PK` | The author                          |
| post_id | TimeUUID | `PK` | Post ID (sorted by time descending) |


**Why:** Denormalized table for "get all posts by user X" (profile grid). Avoids secondary index on the posts table.


5. likes

  
  
  
  


| Field      | Type      | Key  | Description            |
|------------|-----------|------|------------------------|
| post_id    | UUID      | `PK` | The liked post         |
| user_id    | BIGINT    | `PK` | The user who liked     |
| created_at | TIMESTAMP |      | When the like happened |


**Why NoSQL:** Like data is extremely high-volume. Partition by post_id gives efficient "did user X like post Y?" checks and "get all likers of post Z" queries.


6. comments

  
  
  
  
  
  


| Field      | Type      | Key  | Description                 |
|------------|-----------|------|-----------------------------|
| post_id    | UUID      | `PK` | The post being commented on |
| comment_id | TimeUUID  | `PK` | Chronologically sorted      |
| user_id    | BIGINT    |      | Commenter                   |
| text       | TEXT      |      | Comment text                |
| created_at | TIMESTAMP |      | Comment time                |


## Cache Deep Dive


> **
Feed Cache (Redis)


**Purpose:** Pre-computed feed for each user. Stores a list of post_ids (last ~800 posts).


**Data structure:** Redis Sorted Set per user, scored by timestamp. `ZREVRANGE user:{id}:feed 0 19` gets the top 20.


**Caching strategy:** **Write-through** – When a new post is created, the Fan-out Service writes to the Feed Cache as part of the fan-out process. This ensures the feed is always up-to-date.


**Eviction policy:** **LRU** at the Redis cluster level. Additionally, each feed is capped at 800 entries (older posts are trimmed via ZREMRANGEBYRANK).


**Expiration policy:** TTL = 7 days for inactive users' feeds. Active users' feeds are refreshed continuously by fan-out writes.


> **
Count Cache (Redis)


**Purpose:** Stores like/comment counts for posts. `post:{id}:likes`, `post:{id}:comments`.


**Caching strategy:** **Write-through** – Counts are incremented/decremented atomically on every like/comment action using Redis INCR/DECR.


**Eviction policy:** LRU. Older posts' counts are evicted and re-fetched from DB on cache miss.


**Expiration policy:** TTL = 48 hours. Refreshed from DB if accessed after expiry.


## Scaling Considerations


### Load Balancers


  - **L7 Load Balancer** in front of the API Gateway – round-robin for stateless HTTP requests. Handles SSL termination, request routing by URL path to appropriate microservices.

  - **Internal L4 Load Balancers** between microservices (e.g., Feed Service → Post DB read replicas).


### Horizontal Scaling


  - All services are stateless and horizontally scalable behind load balancers.

  - Cassandra: Add nodes; data rebalances automatically.

  - PostgreSQL: Read replicas for user lookups; hash sharding for writes.

  - Redis: Cluster mode with hash slots.

  - Kafka: Add partitions and brokers for throughput.

  - CDN: Scales inherently with global edge network.


## Tradeoffs and Deep Dives


> **
Fan-out on Write vs. Fan-out on Read


**Fan-out on write:** Pre-compute feeds at post time. Pros: Fast reads (just read from cache). Cons: Expensive for celebrities with millions of followers (writing to millions of caches).


**Fan-out on read:** Compute feed at read time by fetching posts from all followed users. Pros: No write amplification. Cons: Slow reads (must query many users' posts and merge).


**Our approach:** Hybrid. Fan-out on write for regular users, fan-out on read for celebrities. This balances write amplification and read latency.


> **
Eventual Consistency for Feeds


Feed updates are eventually consistent — there's a brief delay between post creation and it appearing in all followers' feeds. This is acceptable because users won't notice a 1-2 second delay. The alternative (synchronous fan-out) would make post uploads slow.


## Alternative Approaches


Alternative 1: Graph Database for Social Graph


Neo4j or similar graph DB for follows/followers. Efficient for graph traversals (friends-of-friends, recommendations). Not chosen because: the social graph is relatively simple (just follow edges), and sharding graph databases is notoriously difficult at Instagram's scale. SQL with denormalized counts works well enough.


Alternative 2: Pull-based Feed (No Pre-computation)


Compute feed entirely at read time. Simpler architecture, no fan-out infrastructure needed. Not chosen because read latency would be too high — merging posts from 500+ followed users at request time is slow.


Alternative 3: Single Monolithic Database


Use a single PostgreSQL instance for everything. Simpler development. Not chosen because it cannot scale to billions of posts and trillions of likes. Polyglot persistence (SQL for users, NoSQL for posts/likes) leverages each database's strengths.


## Additional Information


### Snowflake IDs


Post IDs use a Snowflake-like ID generator: 64-bit IDs composed of timestamp + machine ID + sequence number. This ensures globally unique, time-sortable IDs without a central coordination service.


### Image Processing Pipeline


When a photo is uploaded, the Media Service creates multiple resolutions (150px, 320px, 640px, 1080px) and converts to WebP format for ~30% smaller file sizes. This happens asynchronously — the user sees their post immediately while processing continues in the background.


### Rate Limiting


The API Gateway enforces rate limits: 200 API calls/hour per user, 30 posts/day, 100 likes/minute. This prevents spam and abuse. Implemented with a sliding window counter in Redis.