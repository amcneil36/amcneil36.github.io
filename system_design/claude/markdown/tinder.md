# 🔥 Design Tinder


Location-based dating app with swipe-based matching, geospatial search, real-time chat — 75M+ MAU, 2B+ swipes/day


## 1. Functional Requirements


- **Profile Management:** Create profile with photos (up to 9), bio, interests, preferences (age range, distance, gender)

- **Discovery (Swipe Deck):** Serve a stack of nearby profiles based on location, preferences, and recommendation algorithm

- **Swipe Actions:** Like (right swipe), Nope (left swipe), Super Like (up swipe), Rewind (undo last swipe)

- **Matching:** When both users like each other → mutual match → unlock chat

- **Real-Time Chat:** 1:1 messaging between matched users with text, GIFs, reactions, Spotify integration

- **Location Updates:** Track user location for proximity-based recommendations (background GPS updates)

- **Boost & Premium:** Tinder Plus/Gold/Platinum features (unlimited likes, see who liked you, passport/location change)

- **Safety:** Photo verification, block/report, message filtering, panic button (Noonlight integration)


## 2. Non-Functional Requirements


- **Scale:** 75M+ MAU, 2B+ swipes/day, 1.5M+ dates/week

- **Latency:** Swipe deck loading <200ms, match notification <500ms, message delivery <300ms

- **Geospatial:** Efficient proximity queries within configurable radius (2-160km)

- **Availability:** 99.9%+ uptime (peak usage: Sunday 6-9pm local time)

- **Freshness:** Location updates reflected within minutes, new profiles discoverable within minutes

- **Privacy:** Location precision limited (no exact coordinates exposed), GDPR compliance, right to delete


## 3. Flow 1 — Discovery & Swipe Deck Generation


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 400" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#FD297B">
</polygon>
</marker>
</defs>


<rect x="20" y="160" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="190" text-anchor="middle" fill="#fff" font-size="13">
User
</text>
<text x="75" y="205" text-anchor="middle" fill="#fff" font-size="11">
opens app
</text>


<rect x="180" y="160" width="120" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="240" y="190" text-anchor="middle" fill="#fff" font-size="13">
API Gateway
</text>


<rect x="350" y="60" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="420" y="90" text-anchor="middle" fill="#fff" font-size="13">
Location Service
</text>


<rect x="350" y="160" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="420" y="185" text-anchor="middle" fill="#fff" font-size="13">
Recommendation
</text>
<text x="420" y="202" text-anchor="middle" fill="#fff" font-size="11">
Engine
</text>


<rect x="350" y="270" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="420" y="295" text-anchor="middle" fill="#fff" font-size="13">
Swipe Filter
</text>
<text x="420" y="312" text-anchor="middle" fill="#fff" font-size="11">
(already seen)
</text>


<rect x="560" y="60" width="140" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="630" y="90" text-anchor="middle" fill="#fff" font-size="13">
Geospatial Index
</text>
<text x="630" y="105" text-anchor="middle" fill="#fff" font-size="11">
(Redis GEO / S2)
</text>


<rect x="560" y="160" width="140" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="630" y="185" text-anchor="middle" fill="#fff" font-size="13">
ML Ranker
</text>
<text x="630" y="202" text-anchor="middle" fill="#fff" font-size="11">
(ELO + features)
</text>


<rect x="560" y="270" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="630" y="295" text-anchor="middle" fill="#fff" font-size="13">
Swipe History
</text>
<text x="630" y="312" text-anchor="middle" fill="#fff" font-size="11">
(Cassandra)
</text>


<rect x="770" y="160" width="130" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="835" y="190" text-anchor="middle" fill="#fff" font-size="13">
Profile Store
</text>
<text x="835" y="205" text-anchor="middle" fill="#fff" font-size="11">
(MongoDB)
</text>


<rect x="960" y="160" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="1015" y="190" text-anchor="middle" fill="#fff" font-size="13">
Swipe Deck
</text>
<text x="1015" y="205" text-anchor="middle" fill="#fff" font-size="11">
(~100 cards)
</text>


<line x1="130" y1="190" x2="178" y2="190" stroke="#FD297B" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="175" x2="348" y2="95" stroke="#FD297B" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="190" x2="348" y2="190" stroke="#FD297B" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="205" x2="348" y2="295" stroke="#FD297B" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="90" x2="558" y2="90" stroke="#FD297B" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="190" x2="558" y2="190" stroke="#FD297B" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="300" x2="558" y2="300" stroke="#FD297B" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="700" y1="190" x2="768" y2="190" stroke="#FD297B" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="900" y1="190" x2="958" y2="190" stroke="#FD297B" stroke-width="2" marker-end="url(#a1)">
</line>


</svg>

</details>


### Flow Steps


1. **User opens app** → client sends location update + requests new swipe deck via REST API

2. **Location Service** updates user's position in geospatial index (Redis GEO or S2 cell-based index)

3. **Candidate Retrieval:** Query geospatial index for active users within user's distance preference (e.g., 50km radius) matching gender/age preferences → returns ~10,000 candidate IDs

4. **Swipe Filter:** Remove already-swiped users by checking Bloom filter (compact set of user's swipe history) → ~8,000 remaining

5. **ML Ranker:** Score remaining candidates using features: mutual compatibility score (desirability + preference alignment), activity recency, photo quality score, profile completeness, mutual interests count

6. **Deck Assembly:** Select top ~100 candidates, order with strategic mixing: high-compatibility profiles early (hook), some "stretch" profiles (slightly outside preferences), recently active users prioritized

7. **Profile Hydration:** Batch-fetch full profile data (photos, bio, interests) from MongoDB for the ~100 selected candidates

8. **Return swipe deck** to client with pre-loaded images for smooth swiping experience


### Example


**User:** Alice, 28F, in San Francisco, seeking M ages 25-35 within 30km


**Geo query:** `GEORADIUS users_active 37.78 -122.42 30 km COUNT 10000` → 12,000 male profiles within 30km


**Age filter:** 12,000 → 8,500 (ages 25-35 only)


**Swipe filter (Bloom):** Alice has swiped on 3,000 profiles → 5,500 remaining candidates


**ML Ranking features:**

{
  "candidate": "Bob, 29M, 3km away",
  "features": {
    "distance_km": 3,
    "age_diff": 1,
    "mutual_interests": 4,      // hiking, dogs, coffee, travel
    "elo_score": 0.72,          // desirability percentile
    "last_active_hours": 2,
    "photo_quality_score": 0.85,
    "profile_completeness": 0.9
  },
  "compatibility_score": 0.87
}


**Deck:** 100 profiles ordered by score with diversity injection (don't show all 3km profiles first)


### Deep Dives


Geospatial Indexing


Tinder needs to efficiently find users within a radius. Two approaches: **Redis GEO** (uses sorted set with geohash scoring, `GEORADIUS` command, O(N+log(M)) where N=results, M=total). **S2 Geometry (Google)** — divides Earth into hierarchical cells (S2 cells at various levels). For a 30km radius, compute covering S2 cells at level 12 (~3.3km² per cell), query cell index. S2 is better for non-circular regions and varied zoom levels. Tinder likely uses a combination: S2 for partitioning, Redis for fast per-cell lookups. Location updated on app open + periodic background updates (every 5-10 minutes when active).


ELO / Desirability Score


Tinder historically used an ELO-like rating system (similar to chess). When a high-rated user swipes right on you → your score increases more. The system tends to show you profiles with similar desirability scores (assortative matching). **Modern approach:** Tinder moved from pure ELO to a multi-factor ML model. Features: swipe-right rate received, message response rate, profile completeness, photo quality (CNN-based), activity level. The score is NOT just attractiveness — it's "engagement potential." Users who get more right-swipes AND who themselves swipe selectively rank higher.


Bloom Filter for Swipe Dedup


Each user may have swiped on 50K+ profiles over time. Storing exact sets for intersection is expensive. **Bloom filter:** probabilistic data structure, false positives OK (might skip a candidate they haven't seen — acceptable), no false negatives. 50K entries at 1% false positive rate ≈ 60KB per user. Total for 75M users: ~4.5TB — distributed across Redis cluster. On swipe, add to Bloom filter. On deck generation, test candidates against filter. Bloom filters are also used for the "already liked you" optimization (Tinder Gold's "Likes You" feature).


Photo Quality & Verification


Tinder uses ML to score photo quality: face detection (MTCNN), blur detection, lighting quality, group photo detection (which person?), sunglasses/hat detection. **Smart Photos:** automatically reorder a user's photos based on which gets the most right-swipes (A/B test across swipe sessions). **Photo Verification:** user takes a selfie matching a random pose → face embedding compared with uploaded photos using FaceNet/ArcFace. Verified badge granted if match confidence >95%.


## 4. Flow 2 — Matching & Match Notification


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#FF5864">
</polygon>
</marker>
</defs>


<rect x="20" y="140" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="170" text-anchor="middle" fill="#fff" font-size="13">
User A
</text>
<text x="75" y="185" text-anchor="middle" fill="#fff" font-size="11">
swipes right
</text>


<rect x="180" y="140" width="120" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="240" y="170" text-anchor="middle" fill="#fff" font-size="13">
Swipe Service
</text>


<rect x="350" y="40" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="420" y="70" text-anchor="middle" fill="#fff" font-size="13">
Swipe Store
</text>
<text x="420" y="85" text-anchor="middle" fill="#fff" font-size="11">
(Cassandra)
</text>


<rect x="350" y="140" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="420" y="165" text-anchor="middle" fill="#fff" font-size="13">
Match Checker
</text>
<text x="420" y="182" text-anchor="middle" fill="#fff" font-size="11">
(reciprocal like?)
</text>


<rect x="350" y="240" width="140" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="420" y="265" text-anchor="middle" fill="#fff" font-size="13">
Kafka
</text>
<text x="420" y="282" text-anchor="middle" fill="#fff" font-size="11">
match-events
</text>


<rect x="560" y="40" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="630" y="65" text-anchor="middle" fill="#fff" font-size="13">
Match Service
</text>
<text x="630" y="82" text-anchor="middle" fill="#fff" font-size="11">
(create match)
</text>


<rect x="560" y="140" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="630" y="170" text-anchor="middle" fill="#fff" font-size="13">
Push Notification
</text>


<rect x="560" y="240" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="630" y="265" text-anchor="middle" fill="#fff" font-size="13">
WebSocket
</text>
<text x="630" y="282" text-anchor="middle" fill="#fff" font-size="11">
(real-time push)
</text>


<rect x="770" y="140" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="825" y="170" text-anchor="middle" fill="#fff" font-size="13">
User B
</text>
<text x="825" y="185" text-anchor="middle" fill="#fff" font-size="11">
(match notif)
</text>


<line x1="130" y1="170" x2="178" y2="170" stroke="#FF5864" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="300" y1="155" x2="348" y2="75" stroke="#FF5864" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="300" y1="170" x2="348" y2="170" stroke="#FF5864" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="420" y1="202" x2="420" y2="238" stroke="#FF5864" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="490" y1="265" x2="558" y2="65" stroke="#FF5864" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="490" y1="265" x2="558" y2="165" stroke="#FF5864" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="490" y1="265" x2="558" y2="265" stroke="#FF5864" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="700" y1="170" x2="768" y2="170" stroke="#FF5864" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="700" y1="270" x2="768" y2="185" stroke="#FF5864" stroke-width="2" marker-end="url(#a2)">
</line>


</svg>

</details>


### Flow Steps


1. **User A swipes right on User B** → POST `/swipes` with `{swiper: A, target: B, action: "like"}`

2. **Swipe Store:** Persist swipe in Cassandra: `(swiper_id, target_id) → {action, timestamp}`

3. **Match Checker:** Query Cassandra for reverse swipe: `swiper=B, target=A`. If exists AND action="like" → **MATCH!**

4. **Match creation:** Insert match record in Match Store with `match_id, user_a, user_b, matched_at`. Create empty conversation in Chat Service.

5. **Kafka event:** Publish `{match_id, user_a, user_b, timestamp}` to `match-events` topic

6. **Real-time notification:** If User A is online → send match animation via WebSocket. If User B is online → send match notification via WebSocket. If offline → push notification (APNs/FCM): "You have a new match!"

7. **Analytics:** Kafka consumer updates match metrics, updates both users' ELO/desirability scores (getting matched is a positive signal)


### Example


**Timeline:**


Monday 7pm: Bob swipes right on Alice → stored: `(Bob, Alice) → like`. Match check: does `(Alice, Bob) → like` exist? No → no match yet.


Wednesday 9pm: Alice swipes right on Bob → stored: `(Alice, Bob) → like`. Match check: does `(Bob, Alice) → like` exist? **YES!** → MATCH!


**Match created:** match_id=m_789, conversation_id=conv_789, matched_at=Wed 9pm


**Notifications:** Alice sees "It's a Match!" animation immediately (she just swiped). Bob gets push notification: "You have a new match with Alice!" + opens app to see match animation.


**Tinder Gold optimization:** "Likes You" feature shows Alice that Bob already liked her (before she swipes). This is a simple query on the swipe store: `WHERE target=Alice AND action=like AND NOT EXISTS match`.


### Deep Dives


Swipe Store Design (Cassandra)


Partition key: `swiper_id`. Clustering key: `target_id`. This enables: (1) Record a swipe: `INSERT INTO swipes (swiper, target, action, ts)`. (2) Check for match: `SELECT * FROM swipes WHERE swiper=B AND target=A` (reverse lookup requires a secondary table or materialized view with partition_key=target_id). Tinder likely maintains two tables: `swipes_by_swiper` and `swipes_by_target` (denormalized) for efficient bidirectional lookups. At 2B swipes/day × 365 days retention = ~730B rows. Cassandra handles this with time-windowed compaction and TTL (old swipes can expire after matches are resolved).


Match Notification Delivery


Match notifications are time-sensitive (excitement decays quickly). Delivery pipeline: (1) **WebSocket** for users currently in-app — sub-second delivery. (2) **APNs/FCM push** for backgrounded/offline users — 1-5 second delivery. (3) **Email** if user hasn't opened app in 24h — batch daily digest. The "It's a Match!" animation is triggered client-side by a WebSocket event or push notification payload with match data. Both users' match lists are updated atomically. The match animation includes the other user's top photo — pre-fetched from CDN for instant display.


## 5. Flow 3 — Real-Time Chat (Messaging)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 300" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#FF655B">
</polygon>
</marker>
</defs>


<rect x="20" y="120" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="150" text-anchor="middle" fill="#fff" font-size="13">
Sender
</text>


<rect x="180" y="120" width="120" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="240" y="150" text-anchor="middle" fill="#fff" font-size="13">
Chat Service
</text>


<rect x="350" y="30" width="130" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="415" y="60" text-anchor="middle" fill="#fff" font-size="13">
Message Store
</text>
<text x="415" y="75" text-anchor="middle" fill="#fff" font-size="11">
(Cassandra)
</text>


<rect x="350" y="120" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="415" y="150" text-anchor="middle" fill="#fff" font-size="13">
Safety Filter
</text>
<text x="415" y="165" text-anchor="middle" fill="#fff" font-size="11">
(spam/harassment)
</text>


<rect x="350" y="210" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="415" y="240" text-anchor="middle" fill="#fff" font-size="13">
WebSocket Hub
</text>


<rect x="540" y="120" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="605" y="150" text-anchor="middle" fill="#fff" font-size="13">
Push Service
</text>


<rect x="730" y="120" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="785" y="150" text-anchor="middle" fill="#fff" font-size="13">
Recipient
</text>


<line x1="130" y1="150" x2="178" y2="150" stroke="#FF655B" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="300" y1="135" x2="348" y2="65" stroke="#FF655B" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="300" y1="150" x2="348" y2="150" stroke="#FF655B" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="300" y1="165" x2="348" y2="235" stroke="#FF655B" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="480" y1="240" x2="728" y2="155" stroke="#FF655B" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="480" y1="150" x2="538" y2="150" stroke="#FF655B" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="670" y1="150" x2="728" y2="150" stroke="#FF655B" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


### Flow Steps


1. **Sender sends message** via WebSocket: `{match_id, content: "Hey! I love hiking too!", type: "text"}`

2. **Chat Service** validates: match still active? Sender not blocked? Rate limit OK?

3. **Safety Filter:** ML model scans for harassment, solicitation, underage content, scam patterns. Flagged messages may be held for review or auto-actioned

4. **Message stored** in Cassandra: partition_key=match_id, clustering_key=timestamp (chronological within match)

5. **WebSocket Hub:** Look up recipient's WebSocket connection → push message in real-time if online

6. **If offline:** Push notification: "New message from [Name]: Hey! I love hiking too!"

7. **Read receipts:** When recipient opens conversation → client sends "read" event → updates message status


### Example


**Conversation between Alice and Bob (matched 2 hours ago):**

Alice: "Hey! I noticed you like hiking too! Have you been to Mission Peak?"
[Safety filter: clean, score=0.02]
[Stored: match_m789, ts=2024-01-15T20:01:00Z]
[WebSocket: Bob online → delivered instantly]

Bob: "Yes! It was amazing. Have you done the summit?"
[Stored: match_m789, ts=2024-01-15T20:01:45Z]

Alice: [sends GIF: hiking celebration]
[Stored as media message with CDN URL]

Alice: [sends Spotify song link]
[Embed resolved: "Here Comes the Sun - The Beatles"]


**Safety filter example:** If Alice sent a message with solicitation keywords → flagged (score=0.91) → message held, Alice warned, reported to Trust & Safety team.


## 6. Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 520" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#FD297B">
</polygon>
</marker>
</defs>


<rect x="10" y="220" width="100" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="60" y="250" text-anchor="middle" fill="#fff" font-size="12">
Mobile Apps
</text>
<text x="60" y="265" text-anchor="middle" fill="#fff" font-size="10">
iOS/Android
</text>


<rect x="150" y="160" width="110" height="50" rx="8" fill="#546e7a" stroke="#90a4ae" stroke-width="2">
</rect>
<text x="205" y="190" text-anchor="middle" fill="#fff" font-size="12">
CDN (photos)
</text>


<rect x="150" y="225" width="110" height="50" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="205" y="255" text-anchor="middle" fill="#fff" font-size="12">
API Gateway
</text>


<rect x="150" y="295" width="110" height="50" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="205" y="325" text-anchor="middle" fill="#fff" font-size="12">
WebSocket GW
</text>


<rect x="310" y="60" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="86" text-anchor="middle" fill="#fff" font-size="11">
Profile Service
</text>


<rect x="310" y="115" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="141" text-anchor="middle" fill="#fff" font-size="11">
Discovery Engine
</text>


<rect x="310" y="170" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="196" text-anchor="middle" fill="#fff" font-size="11">
Swipe Service
</text>


<rect x="310" y="225" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="251" text-anchor="middle" fill="#fff" font-size="11">
Match Service
</text>


<rect x="310" y="280" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="306" text-anchor="middle" fill="#fff" font-size="11">
Chat Service
</text>


<rect x="310" y="335" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="361" text-anchor="middle" fill="#fff" font-size="11">
Location Service
</text>


<rect x="310" y="390" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="416" text-anchor="middle" fill="#fff" font-size="11">
Safety / ML
</text>


<rect x="490" y="80" width="120" height="42" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="550" y="106" text-anchor="middle" fill="#fff" font-size="11">
Kafka
</text>


<rect x="670" y="60" width="120" height="42" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="730" y="86" text-anchor="middle" fill="#fff" font-size="11">
MongoDB
</text>


<rect x="670" y="115" width="120" height="42" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="730" y="141" text-anchor="middle" fill="#fff" font-size="11">
Cassandra
</text>


<rect x="670" y="170" width="120" height="42" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="730" y="196" text-anchor="middle" fill="#fff" font-size="11">
Redis
</text>


<rect x="670" y="225" width="120" height="42" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="730" y="251" text-anchor="middle" fill="#fff" font-size="11">
S3 (photos)
</text>


<rect x="670" y="280" width="120" height="42" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="730" y="306" text-anchor="middle" fill="#fff" font-size="11">
Elasticsearch
</text>


<line x1="110" y1="240" x2="148" y2="185" stroke="#FD297B" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="110" y1="250" x2="148" y2="250" stroke="#FD297B" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="110" y1="260" x2="148" y2="320" stroke="#FD297B" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="260" y1="250" x2="308" y2="196" stroke="#FD297B" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="86" x2="668" y2="80" stroke="#FD297B" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="196" x2="668" y2="135" stroke="#FD297B" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="360" x2="668" y2="195" stroke="#FD297B" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="140" x2="488" y2="105" stroke="#FD297B" stroke-width="2" marker-end="url(#a4)">
</line>


</svg>

</details>


## 7. Database Schema


### Profiles (MongoDB)


Collection: profiles   `SHARD`
{
  _id: ObjectId,
  user_id: string,             //  `PK`
  name: string,
  birth_date: Date,
  gender: string,
  bio: string (max 500 chars),
  photos: [                    // max 9 photos
    {url: string, order: int, is_verified: bool, quality_score: float}
  ],
  interests: [string],         // max 5 interests (from predefined list)
  job_title: string,
  company: string,
  school: string,
  spotify_anthem: {track_id, name, artist},
  instagram: string,
  location: {type: "Point", coordinates: [lng, lat]},  //  `IDX`
  preferences: {
    gender_filter: [string],
    age_min: int, age_max: int,
    distance_max_km: int
  },
  desirability_score: float,   // ELO/ML-computed
  last_active: Date,           //  `IDX`
  verified: bool,
  premium_tier: string,        // free/plus/gold/platinum
  created_at: Date
}


### Swipes (Cassandra)


Table: swipes_by_swiper   `SHARD`
- swiper_id: uuid              `PK`
- target_id: uuid              `PK`
- action: text                // 'like', 'nope', 'super_like'
- timestamp: timestamp
TTL: 180 days (old nopes can expire)

Table: swipes_by_target   `SHARD`
- target_id: uuid              `PK`
- swiper_id: uuid              `PK`
- action: text
- timestamp: timestamp
// Denormalized for "Who liked me?" (Tinder Gold feature)


### Matches (Cassandra)


Table: matches_by_user   `SHARD`
- user_id: uuid                `PK`
- match_id: uuid               `PK`
- matched_user_id: uuid
- matched_at: timestamp
- last_message_at: timestamp
- unread_count: int
// Stored for BOTH users (denormalized): A→B and B→A


### Messages (Cassandra)


Table: messages   `SHARD`
- match_id: uuid               `PK`
- message_id: timeuuid         `PK`
- sender_id: uuid
- content: text
- message_type: text          // 'text', 'gif', 'image', 'spotify'
- media_url: text
- read: boolean
- created_at: timestamp


### Geospatial Index (Redis)


Key: geo:active_users
Type: ZSET with geohash scores
Command: GEOADD geo:active_users -122.42 37.78 "user_alice"
Query: GEORADIUS geo:active_users -122.42 37.78 30 km COUNT 10000

// Separate sets per gender for faster filtered queries:
Key: geo:active_users:male
Key: geo:active_users:female


## 8. Cache & CDN Deep Dive


| Layer         | Technology              | What's Cached                        | TTL                            | Notes                                        |
|---------------|-------------------------|--------------------------------------|--------------------------------|----------------------------------------------|
| CDN           | CloudFront / Cloudflare | Profile photos (all resolutions)     | 1yr (content-addressed)        | Multiple sizes: 320px, 640px, original       |
| Geo Cache     | Redis GEO               | Active user locations                | Real-time (updated on app use) | Partitioned by gender for faster queries     |
| Bloom Filters | Redis                   | Per-user swipe history               | 180 days                       | ~60KB per user, 1% false positive            |
| Profile Cache | Redis                   | Hot profiles (frequently shown)      | 1 hour                         | LRU eviction, top 10% profiles by view count |
| Deck Cache    | Redis                   | Pre-computed swipe decks             | 30 min (or until exhausted)    | Regenerated when user swipes through deck    |
| Session       | Redis                   | Auth tokens, WebSocket session state | 7 days                         | JWT token validation cache                   |


### Photo CDN Pipeline


Photos are the core UX element. Upload: client uploads to S3 via pre-signed URL → Lambda trigger for processing → resize to multiple variants (320w, 640w, original) → face detection to confirm human face → blur detection (reject blurry photos) → store all variants in S3 → invalidate CDN if updating existing photo. Photos served via CDN with WebP format (30% smaller than JPEG). Client pre-fetches next ~5 photos in the swipe deck for smooth swiping. Average photo load: ~100KB (640w WebP). Pre-fetching 5 cards: ~500KB — acceptable on mobile.


## 9. Scaling Considerations


### Peak Traffic Patterns


- **Peak usage:** Sunday 6-9pm local time (30% above average)

- **Secondary peak:** Weekday evenings 8-10pm

- **Geographic rollout:** Peak moves across timezones — US East Coast peaks → Central → West Coast

- **Auto-scaling:** Kubernetes HPA scales swipe/discovery services based on active user count


### Geospatial Scaling


- Redis GEO cluster sharded by geographic region (US-East, US-West, Europe, Asia)

- Cross-region users (Tinder Passport feature) require cross-shard queries

- Dense urban areas (NYC, London) have 100K+ active users within 10km — candidate set management critical


### Load Balancing


- **DNS:** Route53 latency-based routing to nearest AWS region

- **L7:** AWS ALB with path-based routing (/swipes → swipe service, /matches → match service)

- **WebSocket:** Sticky sessions via connection ID → specific WebSocket server. Redis pub/sub for cross-server message delivery


## 10. Tradeoffs


> **

> **
✅ Bloom Filter for Swipe Dedup


60KB per user vs storing full swipe set (~500KB+). Fast O(k) lookup. False positives only mean occasionally skipping an unseen profile — acceptable UX impact.


> **
❌ Bloom Filter Limitations


Cannot remove entries (user can't "un-nope" without a separate Rewind mechanism). False positive rate grows with size. Cannot enumerate contents (can't build "Likes You" feature from Bloom alone — need the denormalized swipe table).


> **
✅ ELO-Based Matching


Naturally pairs users of similar desirability, improving match rates. Users see profiles they're more likely to match with, reducing futile swipes. Self-regulating: new users start at median and quickly calibrate.


> **
❌ ELO Ethics & UX


Controversial "attractiveness ranking." Users unaware their score affects who they see. Can create echo chambers (attractive users only see attractive users). Tinder has publicly moved away from pure ELO to a more holistic ML model, but desirability scoring remains core.


> **
✅ Pre-Computed Decks


Computing a deck once and caching it (vs. scoring per swipe) reduces ML inference cost by ~100x. Smooth UX — no loading between cards.


> **
❌ Deck Staleness


Pre-computed deck may include users who went offline, changed preferences, or matched with someone else since deck generation. Mitigated by short TTL and fallback re-generation.


## 11. Alternative Approaches


Graph-Based Matching (Hinge Model)


Instead of pure geospatial + ELO, use social graph signals. Hinge shows "friends of friends." Requires importing contact/social graph. Better for relationship-oriented matching. Tinder is more discovery-focused (strangers within radius).


Queue-Based Swipe Processing


Instead of synchronous match checking on each swipe, queue all swipes and batch-process matches asynchronously. Lower write latency (fire-and-forget). But delays match notification by seconds-to-minutes, reducing the "instant match" dopamine hit that's core to Tinder's UX.


PostGIS Instead of Redis GEO


PostgreSQL with PostGIS extension for geospatial queries. Supports complex geo shapes, not just circles. Better for "show me users near this neighborhood boundary." But higher latency than Redis (disk-based vs in-memory). Tinder's simple radius queries don't need PostGIS complexity.


Collaborative Filtering


Recommend profiles based on "users similar to you liked these profiles." Matrix factorization or neural collaborative filtering. Would help with cold-start problem (new users with no swipe history). But privacy concerns: inferring preferences from others' behavior. Tinder uses this as one signal among many.


## 12. Additional Information


### Tech Stack


- **Backend:** Java/Kotlin microservices on Kubernetes (AWS EKS)

- **API:** REST + WebSocket (match/chat real-time)

- **Mobile:** Swift (iOS), Kotlin (Android)

- **Data:** MongoDB (profiles), Cassandra (swipes/matches/messages), Redis (geo/cache/sessions)

- **ML:** Python/TensorFlow for recommendation models, deployed via TFServing

- **Streaming:** Kafka for event pipeline, Flink for real-time analytics

- **Infrastructure:** AWS (EC2, S3, CloudFront, DynamoDB for some workloads)


### Key Numbers


| Monthly Active Users  | 75M+                        |
|-----------------------|-----------------------------|
| Swipes per day        | 2B+                         |
| Matches per day       | ~65M                        |
| Messages per day      | ~500M                       |
| Countries available   | 190+                        |
| Photos stored         | Billions (multi-resolution) |
| Revenue (Match Group) | ~$3.2B/year                 |