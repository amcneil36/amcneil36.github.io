# System Design: Instagram Notification Feed


## Functional Requirements


  1. **Notification feed page** – Users can view a chronological list of notifications (likes, comments, follows, mentions, tags).

  2. **Notification grouping** – Similar notifications are grouped (e.g., "Alice, Bob, and 3 others liked your photo").

  3. **Read/unread status** – Unread notifications are visually highlighted; a badge count shows unread count.

  4. **Notification types** – Support for: like, comment, follow, mention, tag, story reply, live video, suggested follow.

  5. **Deep linking** – Tapping a notification navigates to the relevant content (post, profile, comment).

  6. **Real-time updates** – New notifications appear without requiring manual refresh.

  7. **Pagination** – Load older notifications on scroll with infinite scroll.


## Non-Functional Requirements


  1. **Low latency** – Notification feed loads in <300ms.

  2. **High throughput** – Process billions of notification events per day.

  3. **Eventual consistency** – Acceptable; a like notification may appear a few seconds after the like event.

  4. **Scalability** – Handle 2B+ users, each with potentially thousands of notifications.

  5. **Reliability** – No notification loss; at-least-once delivery to the notification feed.


## Flow 1: Generating a Notification (Event Ingestion)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 400" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a1" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  

  
<rect x="10" y="80" width="100" height="45" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="60" y="107" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Like Service
</text>

  
<rect x="10" y="150" width="100" height="45" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="60" y="177" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Comment Svc
</text>

  
<rect x="10" y="220" width="100" height="45" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="60" y="247" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Follow Svc
</text>

  

  
<line x1="110" y1="102" x2="200" y2="170" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<line x1="110" y1="172" x2="200" y2="172" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<line x1="110" y1="242" x2="200" y2="175" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  

  
<rect x="200" y="145" width="120" height="55" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="260" y="170" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Notification
</text>

  
<text x="260" y="185" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Event Queue
</text>

  

  
<line x1="320" y1="172" x2="410" y2="172" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="410" y="145" width="130" height="55" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="475" y="170" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Notification
</text>

  
<text x="475" y="185" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Service
</text>

  

  
<line x1="475" y1="145" x2="475" y2="75" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="420" y="25" width="110" height="50" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="475" y="48" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Grouping
</text>

  
<text x="475" y="63" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Engine
</text>

  

  
<line x1="540" y1="172" x2="630" y2="172" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<ellipse cx="690" cy="162" rx="50" ry="12" fill="#ff5722" stroke="#d84315" stroke-width="2">
</ellipse>

  
<rect x="640" y="162" width="100" height="35" fill="#ff5722" stroke="#d84315" stroke-width="2">
</rect>

  
<ellipse cx="690" cy="197" rx="50" ry="12" fill="#e64a19" stroke="#d84315" stroke-width="2">
</ellipse>

  
<line x1="640" y1="162" x2="640" y2="197" stroke="#d84315" stroke-width="2">
</line>

  
<line x1="740" y1="162" x2="740" y2="197" stroke="#d84315" stroke-width="2">
</line>

  
<text x="690" y="184" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Notif DB
</text>

  

  
<line x1="475" y1="200" x2="475" y2="270" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<ellipse cx="475" cy="280" rx="50" ry="12" fill="#009688" stroke="#00695c" stroke-width="2">
</ellipse>

  
<rect x="425" y="280" width="100" height="35" fill="#009688" stroke="#00695c" stroke-width="2">
</rect>

  
<ellipse cx="475" cy="315" rx="50" ry="12" fill="#00796b" stroke="#00695c" stroke-width="2">
</ellipse>

  
<line x1="425" y1="280" x2="425" y2="315" stroke="#00695c" stroke-width="2">
</line>

  
<line x1="525" y1="280" x2="525" y2="315" stroke="#00695c" stroke-width="2">
</line>

  
<text x="475" y="302" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Notif Cache
</text>

  
<text x="475" y="335" text-anchor="middle" fill="#555" font-size="9">
(Redis)
</text>

  

  
<line x1="540" y1="185" x2="630" y2="280" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="690" cy="275" rx="55" ry="12" fill="#009688" stroke="#00695c" stroke-width="2">
</ellipse>

  
<rect x="635" y="275" width="110" height="30" fill="#009688" stroke="#00695c" stroke-width="2">
</rect>

  
<ellipse cx="690" cy="305" rx="55" ry="12" fill="#00796b" stroke="#00695c" stroke-width="2">
</ellipse>

  
<line x1="635" y1="275" x2="635" y2="305" stroke="#00695c" stroke-width="2">
</line>

  
<line x1="745" y1="275" x2="745" y2="305" stroke="#00695c" stroke-width="2">
</line>

  
<text x="690" y="294" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Unread Counter
</text>

  
<text x="690" y="322" text-anchor="middle" fill="#555" font-size="9">
(Redis INCR)
</text>


</svg>

</details>


> **
Example 1: Like event generates notification


Bob likes Alice's photo. The **Like Service** publishes a `{event: "POST_LIKED", actor: bob_id, target_user: alice_id, post_id: 123, timestamp}` event to the **Notification Event Queue** (Kafka topic: `notification-events`). The **Notification Service** consumes this event, checks notification preferences (Alice hasn't muted Bob), and passes it to the **Grouping Engine**. The Grouping Engine checks: has Alice received other likes on post 123 in the last 5 minutes? If yes, it updates the existing grouped notification ("Bob, Carol, and 2 others liked your photo") instead of creating a new one. The notification is persisted to the **Notification DB** (Cassandra) and prepended to Alice's notification list in the **Notification Cache** (Redis). The **Unread Counter** is incremented via Redis INCR on key `unread:{alice_id}`.


> **
Example 2: Follow event (no grouping)


Dave follows Alice. The Follow Service publishes `{event: "USER_FOLLOWED", actor: dave_id, target_user: alice_id}` to Kafka. The Notification Service creates a new notification "Dave started following you" (follow events are not grouped). Written to Notification DB and Cache. Unread counter incremented.


### Deep Dive: Flow 1 Components


> **
Notification Event Queue (Kafka)


Central event bus for all notification-generating events. Multiple source services (Like, Comment, Follow, Mention, etc.) publish events to Kafka topics. The Notification Service is a consumer group that processes events.


**Why Kafka (Message Queue):** Decouples event generation from notification processing. Provides durability (events survive service crashes), ordering (per-partition), and replay capability. Alternative SSE/WebSocket would be direct push but lacks persistence and decoupling.


**Why not direct HTTP calls:** Tight coupling; if Notification Service is down, events are lost. Kafka buffers events during outages.


Partitioned by `target_user_id` to ensure all notifications for a user are processed in order by the same consumer.


> **
Notification Service


Core service that processes raw events and generates user-facing notifications. Responsibilities: event deduplication, preference checking (muted users, notification settings), grouping delegation, persistence, cache update, counter update.


**Protocol:** Kafka consumer (internal). Exposes gRPC for internal queries.


> **
Grouping Engine


Groups similar notifications to reduce noise. Rules: likes on same post within 5 minutes → grouped; comments on same post → individual (each comment has different content). Uses a short-lived cache (Redis) to track recent notification groups per user per entity.


**Algorithm:** Key = `group:{user_id}:{event_type}:{entity_id}`. If key exists and group size < 100, append actor to group. Otherwise, create new group.


## Flow 2: Reading the Notification Feed


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a2" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="140" width="90" height="55" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="55" y="172" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
User
</text>

  
<line x1="100" y1="167" x2="170" y2="167" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<text x="135" y="157" text-anchor="middle" fill="#555" font-size="9">
GET /notifs
</text>

  
<rect x="170" y="140" width="90" height="55" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="215" y="165" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
API
</text>

  
<text x="215" y="178" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Gateway
</text>

  
<line x1="260" y1="167" x2="340" y2="167" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<rect x="340" y="140" width="120" height="55" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="400" y="165" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Notification
</text>

  
<text x="400" y="180" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Service
</text>

  

  
<line x1="460" y1="155" x2="550" y2="105" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="520" y="118" text-anchor="middle" fill="#555" font-size="9">
1. Cache hit?
</text>

  
<ellipse cx="610" cy="90" rx="50" ry="12" fill="#009688" stroke="#00695c" stroke-width="2">
</ellipse>

  
<rect x="560" y="90" width="100" height="30" fill="#009688" stroke="#00695c" stroke-width="2">
</rect>

  
<ellipse cx="610" cy="120" rx="50" ry="12" fill="#00796b" stroke="#00695c" stroke-width="2">
</ellipse>

  
<line x1="560" y1="90" x2="560" y2="120" stroke="#00695c" stroke-width="2">
</line>

  
<line x1="660" y1="90" x2="660" y2="120" stroke="#00695c" stroke-width="2">
</line>

  
<text x="610" y="109" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Notif Cache
</text>

  

  
<line x1="460" y1="180" x2="550" y2="230" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="520" y="220" text-anchor="middle" fill="#555" font-size="9">
2. Cache miss
</text>

  
<ellipse cx="610" cy="225" rx="50" ry="12" fill="#ff5722" stroke="#d84315" stroke-width="2">
</ellipse>

  
<rect x="560" y="225" width="100" height="30" fill="#ff5722" stroke="#d84315" stroke-width="2">
</rect>

  
<ellipse cx="610" cy="255" rx="50" ry="12" fill="#e64a19" stroke="#d84315" stroke-width="2">
</ellipse>

  
<line x1="560" y1="225" x2="560" y2="255" stroke="#d84315" stroke-width="2">
</line>

  
<line x1="660" y1="225" x2="660" y2="255" stroke="#d84315" stroke-width="2">
</line>

  
<text x="610" y="245" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Notif DB
</text>

  

  
<line x1="400" y1="140" x2="400" y2="60" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<rect x="345" y="15" width="110" height="45" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="400" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
User Service
</text>

  
<text x="400" y="58" text-anchor="middle" fill="#555" font-size="9">
3. Hydrate actors
</text>

  

  
<line x1="400" y1="195" x2="400" y2="270" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<ellipse cx="400" cy="280" rx="55" ry="12" fill="#009688" stroke="#00695c" stroke-width="2">
</ellipse>

  
<rect x="345" y="280" width="110" height="30" fill="#009688" stroke="#00695c" stroke-width="2">
</rect>

  
<ellipse cx="400" cy="310" rx="55" ry="12" fill="#00796b" stroke="#00695c" stroke-width="2">
</ellipse>

  
<line x1="345" y1="280" x2="345" y2="310" stroke="#00695c" stroke-width="2">
</line>

  
<line x1="455" y1="280" x2="455" y2="310" stroke="#00695c" stroke-width="2">
</line>

  
<text x="400" y="298" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Unread Counter
</text>

  
<text x="400" y="327" text-anchor="middle" fill="#555" font-size="9">
4. Reset to 0
</text>


</svg>

</details>


> **
Example: User opens notification tab


Alice taps the heart icon (notification tab) in Instagram. The client sends **GET /api/v1/notifications?cursor=&limit=20** to the API Gateway → **Notification Service**. The service first checks the **Notification Cache** (Redis sorted set `notifs:{alice_id}`) for cached notification IDs. On cache hit, it retrieves the top 20 notification objects. On cache miss, it queries the **Notification DB** (Cassandra) and populates the cache. The notification data is then **hydrated** by calling the **User Service** to resolve actor IDs to usernames and avatar URLs (e.g., "bob_id" → "Bob", avatar URL). The fully hydrated notifications are returned to the client. The **Unread Counter** for Alice is reset to 0 via Redis SET.


> **
Example: Paginated scroll


Alice scrolls to the bottom and the client sends **GET /api/v1/notifications?cursor=notif_456&limit=20**. The Notification Service uses the cursor to fetch the next page from Redis (ZREVRANGEBYSCORE with the cursor's score as the upper bound). For very old notifications beyond the cache, it falls through to Cassandra.


## Overall Combined Flow


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1150 450" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a3" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  

  
<rect x="10" y="100" width="90" height="40" rx="6" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="55" y="124" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Like Svc
</text>

  
<rect x="10" y="150" width="90" height="40" rx="6" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="55" y="174" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Comment Svc
</text>

  
<rect x="10" y="200" width="90" height="40" rx="6" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="55" y="224" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Follow Svc
</text>

  

  
<line x1="100" y1="120" x2="170" y2="170" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<line x1="100" y1="170" x2="170" y2="170" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<line x1="100" y1="220" x2="170" y2="175" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  

  
<rect x="170" y="150" width="100" height="45" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="220" y="177" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Kafka
</text>

  

  
<line x1="270" y1="172" x2="340" y2="172" stroke="#333" stroke-width="2" marker-end="url(#a3)">
</line>

  
<rect x="340" y="145" width="110" height="55" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="395" y="170" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Notification
</text>

  
<text x="395" y="185" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Service
</text>

  

  
<line x1="395" y1="145" x2="395" y2="80" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="345" y="40" width="100" height="40" rx="6" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="395" y="65" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Grouping
</text>

  

  
<line x1="450" y1="172" x2="530" y2="172" stroke="#333" stroke-width="2" marker-end="url(#a3)">
</line>

  
<ellipse cx="580" cy="165" rx="40" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="540" y="165" width="80" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="580" cy="190" rx="40" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="540" y1="165" x2="540" y2="190" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="620" y1="165" x2="620" y2="190" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="580" y="182" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Notif DB
</text>

  

  
<line x1="395" y1="200" x2="395" y2="260" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<ellipse cx="395" cy="270" rx="45" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="350" y="270" width="90" height="22" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="395" cy="292" rx="45" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="350" y1="270" x2="350" y2="292" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="440" y1="270" x2="440" y2="292" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="395" y="285" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Notif Cache
</text>

  

  
<ellipse cx="580" cy="265" rx="50" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="530" y="265" width="100" height="22" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="580" cy="287" rx="50" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="530" y1="265" x2="530" y2="287" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="630" y1="265" x2="630" y2="287" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="580" y="280" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Unread Ctr
</text>

  
<line x1="450" y1="185" x2="530" y2="270" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  

  
<rect x="750" y="150" width="80" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="790" y="180" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Client
</text>

  
<line x1="750" y1="175" x2="680" y2="175" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<text x="715" y="168" text-anchor="middle" fill="#555" font-size="9">
GET
</text>

  
<rect x="620" y="145" width="60" height="55" rx="6" fill="#ff9800" stroke="#f57c00" stroke-width="1.5">
</rect>

  
<text x="650" y="170" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
API
</text>

  
<text x="650" y="182" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
GW
</text>

  

  
<line x1="620" y1="172" x2="530" y2="172" stroke="#aaa" stroke-width="1" stroke-dasharray="4,3">
</line>

  

  
<rect x="750" y="40" width="100" height="45" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="800" y="67" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
User Svc
</text>

  
<line x1="650" y1="145" x2="750" y2="62" stroke="#aaa" stroke-width="1" stroke-dasharray="4,3">
</line>


</svg>

</details>


> **
Combined Example


**Write path:** Bob likes Alice's photo → Like Service → Kafka → Notification Service → Grouping Engine groups with recent likes → Write to Notif DB (Cassandra) + Notif Cache (Redis) + increment Unread Counter (Redis).  

**Read path:** Alice taps notification tab → GET request → API Gateway → Notification Service → reads from Notif Cache (Redis) → hydrates actor info from User Service → returns formatted notifications → resets Unread Counter.


## Database Schema


### NoSQL Tables (Cassandra)


1. notifications

  
  
  
  
  
  
  
  
  
  
  
  


| Field             | Type         | Key  | Description                            |
|-------------------|--------------|------|----------------------------------------|
| target_user_id    | BIGINT       | `PK` | User receiving the notification        |
| notification_id   | TimeUUID     | `PK` | Unique notification ID, sorted by time |
| notification_type | TEXT         |      | LIKE, COMMENT, FOLLOW, MENTION, TAG    |
| actor_ids         | LIST<BIGINT> |      | List of actors (for grouped notifs)    |
| entity_id         | BIGINT       |      | The post/comment/user being referenced |
| entity_type       | TEXT         |      | POST, COMMENT, USER, STORY             |
| group_size        | INT          |      | Number of actors in this group         |
| preview_text      | TEXT         |      | Pre-rendered notification text         |
| thumbnail_url     | TEXT         |      | Thumbnail of the post (denormalized)   |
| is_read           | BOOLEAN      |      | Whether the user has seen this         |
| created_at        | TIMESTAMP    |      | Notification creation time             |


**Why NoSQL (Cassandra):** Notifications are extremely write-heavy (every like/comment generates one) and read-heavy (notification feed loaded on app open). Cassandra handles both with linear horizontal scaling. Partition by `target_user_id` co-locates all notifications for a user; clustering by `notification_id` DESC provides reverse-chronological ordering.


**Denormalization:** `preview_text` and `thumbnail_url` are denormalized from posts/users tables. This avoids joins at read time — each notification is self-contained. The tradeoff is that if a post's image changes, the thumbnail in old notifications becomes stale. This is acceptable because old notification thumbnails rarely matter.


**Sharding:** Cassandra's native consistent hashing on `target_user_id` distributes data evenly across nodes.


**Index:** No secondary indexes needed. Primary access pattern is `target_user_id` + range on `notification_id`.


2. notification_groups (short-lived, Redis)

  
  


| Key Pattern                        | Value           | TTL   | Description                                         |
|------------------------------------|-----------------|-------|-----------------------------------------------------|
| group:{user_id}:{type}:{entity_id} | notification_id | 5 min | Maps to active group notification for deduplication |


This is a Redis key (not a Cassandra table). Used by the Grouping Engine to track active notification groups.


## Cache Deep Dive


> **
Notification Feed Cache (Redis Sorted Set)


**Purpose:** Pre-materialized notification feed per user. Key: `notifs:{user_id}`. Score: timestamp. Value: serialized notification object (JSON).


**Caching strategy:** **Write-through** – Every new notification is written to both Cassandra and Redis simultaneously by the Notification Service.


**Populated by:** Notification Service on event processing (write path). On cache miss, populated from Cassandra on first read.


**Eviction policy:** Each user's sorted set is capped at 200 entries (ZREMRANGEBYRANK to trim). LRU at the Redis cluster level for inactive users.


**Expiration policy:** TTL = 7 days. Inactive users' caches expire and are rebuilt from DB on next access.


**Why write-through:** Notification feed must be fresh — users expect to see new notifications immediately on opening the tab.


> **
Unread Count Cache (Redis)


**Purpose:** Stores unread notification count per user. Key: `unread:{user_id}`. Value: integer.


**Operations:** INCR on new notification, SET to 0 when user opens notification tab.


**No TTL needed** — the counter is always managed explicitly.


## Scaling Considerations


  - **Load Balancer:** L7 LB in front of API Gateway (round-robin for stateless HTTP). Internal L4 LB between Notification Service instances.

  - **Kafka:** Partition by target_user_id for ordering. Add brokers for throughput (~10M events/sec).

  - **Cassandra:** Add nodes for storage and throughput. ~3 replicas per data center.

  - **Redis Cluster:** Hash-slot sharding across nodes. Each node handles ~1M users' notification caches.

  - **Notification Service:** Stateless, horizontally scaled. Each instance in the Kafka consumer group processes a subset of partitions.


## Tradeoffs and Deep Dives


> **
Pre-rendered vs. Template-based Notifications


We store `preview_text` (pre-rendered) in the notification. Alternative: store only actor_ids and entity_id, then render the text on read. Pre-rendering is faster at read time but harder to update (e.g., if an actor changes their username, old notifications show the old name). We accept this tradeoff for lower read latency.


> **
Grouping Accuracy vs. Simplicity


5-minute grouping window means likes 6 minutes apart won't be grouped. More complex time-decay grouping could improve UX but adds processing cost. We chose simplicity with a fixed window.


## Alternative Approaches


Alternative 1: Polling for Unread Count


Client polls every 30 seconds for unread count. Simple but wasteful. Better: use a lightweight WebSocket or SSE connection for real-time badge updates (discussed in Push Notifications design).


Alternative 2: Materialized View in Cassandra


Use Cassandra's materialized views for the notification feed. Avoided because materialized views have performance issues at scale and limited support.


## Additional Information


### Notification Deduplication


If Bob likes and unlikes and re-likes a post within a short window, the Notification Service uses an idempotency key (`actor_id + event_type + entity_id`) to prevent duplicate notifications. If a notification already exists in the grouping cache, the re-like is ignored.


### CDN Consideration


CDN is **not used** for the notification feed itself (it's personalized, dynamic data). However, the thumbnail images referenced in notifications are served from CDN.