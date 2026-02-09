# System Design: Instagram Push Notifications


## Functional Requirements


  1. **Push notifications to mobile devices** – Deliver push notifications for likes, comments, follows, mentions, DMs, live videos, and stories to iOS (APNs) and Android (FCM) devices.

  2. **Notification preferences** – Users can enable/disable specific notification types (e.g., turn off like notifications but keep comment notifications).

  3. **Rate limiting/throttling** – Don't overwhelm users with too many notifications in a short period; batch or suppress excess notifications.

  4. **Rich notifications** – Include thumbnail images, action buttons (like, reply), and deep links in the notification payload.

  5. **Multi-device support** – Users logged into multiple devices receive notifications on all devices.

  6. **Quiet hours** – Respect user-configured do-not-disturb schedules.


## Non-Functional Requirements


  1. **Low latency** – Push notifications delivered within 1-3 seconds of the triggering event.

  2. **High throughput** – Handle billions of push notifications per day.

  3. **Reliability** – At-least-once delivery to the push notification provider (APNs/FCM).

  4. **Scalability** – Scale horizontally to handle viral events (celebrity posts generating millions of likes).


## Flow 1: Sending a Push Notification


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1150 420" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a1" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  

  
<rect x="10" y="120" width="95" height="40" rx="6" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="57" y="144" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Like Svc
</text>

  
<rect x="10" y="170" width="95" height="40" rx="6" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="57" y="194" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Comment Svc
</text>

  
<rect x="10" y="220" width="95" height="40" rx="6" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="57" y="244" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Follow Svc
</text>

  

  
<line x1="105" y1="140" x2="180" y2="190" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<line x1="105" y1="190" x2="180" y2="190" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<line x1="105" y1="240" x2="180" y2="195" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  

  
<rect x="180" y="168" width="110" height="50" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="235" y="197" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Event Queue
</text>

  

  
<line x1="290" y1="192" x2="370" y2="192" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="370" y="160" width="130" height="65" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="435" y="187" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Push Notif
</text>

  
<text x="435" y="202" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Service
</text>

  

  
<line x1="435" y1="160" x2="435" y2="80" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="380" y="30" width="110" height="50" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="435" y="53" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Preference
</text>

  
<text x="435" y="68" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Service
</text>

  

  
<line x1="490" y1="55" x2="560" y2="55" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="610" cy="48" rx="40" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="570" y="48" width="80" height="22" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="610" cy="70" rx="40" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="570" y1="48" x2="570" y2="70" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="650" y1="48" x2="650" y2="70" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="610" y="63" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Pref DB
</text>

  

  
<line x1="435" y1="225" x2="435" y2="290" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="380" y="290" width="110" height="45" rx="8" fill="#00bcd4" stroke="#00838f" stroke-width="2">
</rect>

  
<text x="435" y="317" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Rate Limiter
</text>

  

  
<line x1="490" y1="312" x2="560" y2="312" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="610" cy="305" rx="40" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="570" y="305" width="80" height="22" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="610" cy="327" rx="40" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="570" y1="305" x2="570" y2="327" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="650" y1="305" x2="650" y2="327" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="610" y="320" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Redis
</text>

  

  
<line x1="500" y1="185" x2="580" y2="155" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="630" cy="142" rx="45" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="585" y="142" width="90" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="630" cy="167" rx="45" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="585" y1="142" x2="585" y2="167" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="675" y1="142" x2="675" y2="167" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="630" y="159" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Device Tokens
</text>

  

  
<line x1="500" y1="200" x2="580" y2="220" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="580" y="200" width="110" height="45" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="635" y="222" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Send Queue
</text>

  

  
<line x1="690" y1="222" x2="770" y2="222" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="770" y="190" width="100" height="55" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="820" y="215" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Push
</text>

  
<text x="820" y="230" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Workers
</text>

  

  
<line x1="870" y1="205" x2="950" y2="175" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="950" y="155" width="80" height="40" rx="6" fill="#607d8b" stroke="#37474f" stroke-width="1.5">
</rect>

  
<text x="990" y="179" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
APNs
</text>

  

  
<line x1="870" y1="230" x2="950" y2="255" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="950" y="240" width="80" height="40" rx="6" fill="#607d8b" stroke="#37474f" stroke-width="1.5">
</rect>

  
<text x="990" y="264" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
FCM
</text>

  

  
<line x1="1030" y1="175" x2="1080" y2="175" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="1080" y="155" width="50" height="40" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="1105" y="179" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
iOS
</text>

  
<line x1="1030" y1="260" x2="1080" y2="260" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="1080" y="240" width="50" height="40" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="1105" y="264" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Andro
</text>


</svg>

</details>


> **
Example 1: Normal push notification


Bob likes Alice's photo. The Like Service publishes a `{event: POST_LIKED, actor: bob, target: alice, post_id: 123}` event to the **Event Queue** (Kafka). The **Push Notification Service** consumes the event and: (1) Calls the **Preference Service** to check if Alice has like notifications enabled — yes. (2) Checks the **Rate Limiter** (Redis sliding window: max 50 push notifs/hour per user) — Alice has received 12 this hour, under the limit. (3) Fetches Alice's device tokens from the **Device Token Store** — Alice has 2 devices (iPhone, iPad). (4) Constructs the notification payload: `{title: "Instagram", body: "bob liked your photo", image: thumbnail_url, deep_link: "instagram://post/123"}`. (5) Enqueues two messages (one per device) to the **Send Queue**. **Push Workers** dequeue and send to **APNs** via HTTP/2 for both iOS devices. Alice sees a push notification on both devices.


> **
Example 2: Notification suppressed (user preference)


Carol tags Alice in a story. The Push Notification Service checks Preference Service — Alice has disabled "Tags" notifications. The notification is dropped. No push is sent, but the notification still appears in Alice's in-app notification feed (handled by the Notification Feed system).


> **
Example 3: Rate limited


A celebrity posts a photo and gets 10,000 likes in 1 minute. The celebrity would normally get 10,000 push notifications. The Rate Limiter detects the burst: after the first 50 in the hour, subsequent individual like notifications are suppressed. Instead, a summary notification is sent after a cooldown: "10,423 people liked your photo." This is achieved by aggregating suppressed events and sending a batch notification every 5 minutes when the rate limit is exceeded.


### Deep Dive: Flow 1 Components


> **
Push Notification Service


Core orchestrator that processes notification events. Steps: validate event → check preferences → apply rate limiting → resolve device tokens → construct payload → enqueue for delivery.


**Protocol:** Kafka consumer (event ingestion). Internal gRPC to Preference Service. HTTP/2 to APNs, HTTPS to FCM.


> **
Preference Service


Manages user notification settings. Exposes a gRPC API: `GetPreferences(user_id) → {likes: true, comments: true, follows: true, mentions: false, ...}`. Caches preferences in Redis (read-heavy, rarely updated).


**Cache strategy:** Read-through cache. TTL = 1 hour. Invalidated on preference update (write-through).


> **
Rate Limiter


Implemented using a **sliding window counter** in Redis. Key: `ratelimit:{user_id}:{hour}`. INCR on each notification. If count > 50, suppress. Uses a secondary key `batch:{user_id}` to accumulate suppressed events for batch notification.


**Why sliding window:** Fixed window can allow bursts at window boundaries. Sliding window provides smoother rate limiting.


> **
Device Token Store


Stores APNs/FCM device tokens per user. Schema: `{user_id → [{device_token, platform, app_version, last_active}]}`. Users can have multiple devices. Stale tokens (device not seen in 30 days) are periodically purged. Stored in Cassandra for high availability.


> **
Push Workers


Worker pool that dequeues from the Send Queue and makes HTTP calls to APNs/FCM. Uses connection pooling and HTTP/2 multiplexing for high throughput to APNs. Handles retries with exponential backoff for transient failures. Invalid device tokens (APNs returns 410 Gone) are flagged for removal from the Device Token Store.


> **
APNs and FCM (External Services)


**APNs (Apple Push Notification service):** Connected via HTTP/2 with persistent connections. Requires JWT-based authentication. Supports alert, badge, and silent notifications.


**FCM (Firebase Cloud Messaging):** Connected via HTTPS POST to `https://fcm.googleapis.com/v1/projects/{id}/messages:send`. Supports data messages and notification messages.


**Protocol:** Both use TCP/TLS. APNs uses HTTP/2 for multiplexing (sending multiple notifications on one connection). FCM uses standard HTTPS.


## Flow 2: Device Token Registration


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 700 200" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a2" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="70" width="90" height="55" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="55" y="102" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Client
</text>

  
<line x1="100" y1="97" x2="180" y2="97" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<text x="140" y="88" text-anchor="middle" fill="#555" font-size="9">
PUT /device-token
</text>

  
<rect x="180" y="70" width="100" height="55" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="230" y="95" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
API
</text>

  
<text x="230" y="108" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Gateway
</text>

  
<line x1="280" y1="97" x2="360" y2="97" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<rect x="360" y="70" width="120" height="55" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="420" y="95" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Device Token
</text>

  
<text x="420" y="108" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Service
</text>

  
<line x1="480" y1="97" x2="560" y2="97" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<ellipse cx="610" cy="90" rx="40" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="570" y="90" width="80" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="610" cy="115" rx="40" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="570" y1="90" x2="570" y2="115" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="650" y1="90" x2="650" y2="115" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="610" y="107" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Token Store
</text>


</svg>

</details>


> **
Example: App install and token registration


Alice installs Instagram on her new iPhone. On first launch, the app requests push notification permission from iOS. iOS returns an APNs device token. The app sends **PUT /api/v1/device-token** with `{user_id: alice, device_token: "abc123...", platform: "ios", app_version: "310.0"}` to the API Gateway → **Device Token Service** → upserts into the **Token Store** (Cassandra). If Alice reinstalls the app, a new token is generated and the old one is replaced.


## Overall Combined Flow


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1150 400" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a3" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  

  
<rect x="5" y="150" width="80" height="35" rx="5" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="45" y="172" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Services
</text>

  

  
<line x1="85" y1="167" x2="140" y2="167" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="140" y="148" width="80" height="38" rx="6" fill="#9c27b0" stroke="#6a1b9a" stroke-width="1.5">
</rect>

  
<text x="180" y="171" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Kafka
</text>

  

  
<line x1="220" y1="167" x2="280" y2="167" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="280" y="140" width="110" height="55" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="335" y="165" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Push Notif
</text>

  
<text x="335" y="178" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Service
</text>

  

  
<line x1="335" y1="140" x2="335" y2="75" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="295" y="35" width="80" height="40" rx="5" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="335" y="55" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Prefs
</text>

  
<text x="335" y="65" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
+ Cache
</text>

  

  
<line x1="335" y1="195" x2="335" y2="250" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="295" y="250" width="80" height="35" rx="5" fill="#00bcd4" stroke="#00838f" stroke-width="1.5">
</rect>

  
<text x="335" y="272" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Rate Limit
</text>

  

  
<line x1="390" y1="155" x2="440" y2="105" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<ellipse cx="480" cy="95" rx="35" ry="8" fill="#ff5722" stroke="#d84315" stroke-width="1">
</ellipse>

  
<rect x="445" y="95" width="70" height="18" fill="#ff5722" stroke="#d84315" stroke-width="1">
</rect>

  
<ellipse cx="480" cy="113" rx="35" ry="8" fill="#e64a19" stroke="#d84315" stroke-width="1">
</ellipse>

  
<line x1="445" y1="95" x2="445" y2="113" stroke="#d84315" stroke-width="1">
</line>

  
<line x1="515" y1="95" x2="515" y2="113" stroke="#d84315" stroke-width="1">
</line>

  
<text x="480" y="108" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
Tokens
</text>

  

  
<line x1="390" y1="170" x2="450" y2="170" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="450" y="150" width="90" height="38" rx="6" fill="#9c27b0" stroke="#6a1b9a" stroke-width="1.5">
</rect>

  
<text x="495" y="173" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Send Queue
</text>

  

  
<line x1="540" y1="170" x2="600" y2="170" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="600" y="148" width="90" height="42" rx="6" fill="#ff9800" stroke="#f57c00" stroke-width="1.5">
</rect>

  
<text x="645" y="173" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Workers
</text>

  

  
<line x1="690" y1="160" x2="750" y2="135" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="750" y="118" width="60" height="30" rx="5" fill="#607d8b" stroke="#37474f" stroke-width="1.5">
</rect>

  
<text x="780" y="137" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
APNs
</text>

  
<line x1="690" y1="178" x2="750" y2="200" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="750" y="188" width="60" height="30" rx="5" fill="#607d8b" stroke="#37474f" stroke-width="1.5">
</rect>

  
<text x="780" y="207" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
FCM
</text>

  

  
<line x1="810" y1="133" x2="860" y2="133" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="860" y="118" width="50" height="30" rx="5" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="885" y="137" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
iOS
</text>

  
<line x1="810" y1="203" x2="860" y2="203" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="860" y="188" width="60" height="30" rx="5" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="890" y="207" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Android
</text>

  

  
<rect x="5" y="320" width="80" height="35" rx="5" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="45" y="342" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Client
</text>

  
<line x1="85" y1="337" x2="140" y2="337" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<text x="112" y="330" text-anchor="middle" fill="#555" font-size="7">
PUT
</text>

  
<rect x="140" y="320" width="80" height="35" rx="5" fill="#ff9800" stroke="#f57c00" stroke-width="1.5">
</rect>

  
<text x="180" y="342" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
API GW
</text>

  
<line x1="220" y1="337" x2="280" y2="337" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="280" y="320" width="90" height="35" rx="5" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="325" y="342" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Token Svc
</text>

  
<line x1="370" y1="337" x2="445" y2="120" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>


</svg>

</details>


> **
Combined Example


**Registration:** Client installs app → requests push permission → gets device token from OS → PUT /device-token → stored in Token Store. **Notification:** Bob likes Alice's photo → Like Service → Kafka → Push Notification Service checks prefs (enabled), checks rate limit (under threshold), fetches Alice's 2 device tokens → constructs payload → enqueues to Send Queue → Push Workers send to APNs (iPhone) and FCM (Android tablet) → Alice sees push on both devices.


## Database Schema


### NoSQL Tables (Cassandra)


1. device_tokens

  
  
  
  
  
  
  


| Field        | Type      | Key  | Description               |
|--------------|-----------|------|---------------------------|
| user_id      | BIGINT    | `PK` | User who owns the device  |
| device_token | TEXT      | `PK` | APNs or FCM token         |
| platform     | TEXT      |      | ios, android              |
| app_version  | TEXT      |      | App version for targeting |
| last_active  | TIMESTAMP |      | Last time device was seen |
| created_at   | TIMESTAMP |      | Registration time         |


**Why NoSQL:** Simple key-value lookup (get all tokens for user_id). High availability required. No relational queries.


**Read:** When sending a push notification (fetch all device tokens for a user). **Write:** On app launch (device token registration/update), on token invalidation (APNs returns error).


### SQL Tables (PostgreSQL)


2. notification_preferences

  
  
  
  
  
  
  
  
  
  


| Field             | Type      | Key  | Description               |
|-------------------|-----------|------|---------------------------|
| user_id           | BIGINT    | `PK` | User                      |
| likes_enabled     | BOOLEAN   |      | Default: true             |
| comments_enabled  | BOOLEAN   |      | Default: true             |
| follows_enabled   | BOOLEAN   |      | Default: true             |
| mentions_enabled  | BOOLEAN   |      | Default: true             |
| dm_enabled        | BOOLEAN   |      | Default: true             |
| quiet_hours_start | TIME      |      | DND start time (nullable) |
| quiet_hours_end   | TIME      |      | DND end time (nullable)   |
| updated_at        | TIMESTAMP |      | Last update time          |


**Why SQL:** Small table (one row per user), strong consistency needed (preference changes must take effect immediately), ACID for updates.


**Index:** PK on user_id (hash). No additional indexes needed.


**Read:** On every push notification event (cached in Redis). **Write:** When user changes notification settings.


## Cache Deep Dive


> **
Preference Cache (Redis)


**Purpose:** Cache notification preferences to avoid hitting PostgreSQL on every event.


**Strategy:** **Read-through with write-invalidation.** On read: check Redis first, if miss → query PostgreSQL → populate Redis. On write: update PostgreSQL → invalidate Redis key → next read repopulates.


**Eviction:** LRU. **Expiration:** TTL = 1 hour.


## Scaling Considerations


  - **Load Balancer:** L7 LB in front of API Gateway for device token registration. Internal LB for Push Workers.

  - **Push Workers:** Auto-scale based on Send Queue depth. Peak load (celebrity posts) may require 10x normal capacity.

  - **Connection pooling:** Each Push Worker maintains persistent HTTP/2 connections to APNs (multiplexed) and connection pools to FCM.

  - **Kafka partitioning:** Partition by `target_user_id % N` to distribute load evenly across consumers.


## Tradeoffs and Deep Dives


> **
Push vs. Pull for Notifications


Push (proactive delivery via APNs/FCM) is chosen over pull (client polling). Push provides real-time delivery with minimal battery drain. The tradeoff is dependency on third-party services (APNs/FCM) and their rate limits/quotas.


> **
Two-Queue Architecture


We use two queues: Event Queue (Kafka, for raw events) and Send Queue (for processed, ready-to-send notifications). This separation allows the Push Notification Service to filter/enrich events without blocking delivery workers.


## Alternative Approaches


Alternative: WebSocket for Real-time In-app Notifications


Instead of APNs/FCM, use a persistent WebSocket to push notifications while the app is open. This is used for in-app real-time updates but doesn't replace push notifications because WebSocket only works when the app is in the foreground. Push notifications via APNs/FCM work even when the app is closed/backgrounded.


Alternative: SMS Fallback


For critical notifications (e.g., security alerts), fall back to SMS if push delivery fails. Not used for social notifications due to cost (~$0.01/SMS at scale = millions of dollars/day).


## Additional Information


### Notification Analytics


Track notification delivery rates, open rates, and engagement (did the user tap the notification?). APNs provides delivery receipts; FCM provides message delivery data. Analytics are streamed to a data warehouse (BigQuery/Hive) for analysis.


### A/B Testing Notifications


The notification payload (copy, image, timing) can be A/B tested. The Push Notification Service integrates with an experimentation platform to select notification variants per user cohort.