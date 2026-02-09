# 🎙️ Clubhouse (Audio Social) - System Design


## Functional Requirements


1. **Live Audio Rooms:** Create, join, and participate in real-time audio rooms with speakers, moderators, and listeners (up to 8K participants)

2. **Room Discovery & Hallway:** Browse active/upcoming rooms, follow clubs/topics, personalized room recommendations, event scheduling

3. **Social Graph & Notifications:** Follow users, get notified when followed users start/join rooms, invite-only rooms, raise-hand/speaker queue


## Non-Functional Requirements


- **Latency:** Audio end-to-end <300ms (conversational quality requires <400ms perceived delay)

- **Scale:** 10M+ concurrent users, thousands of simultaneous rooms, rooms up to 8K listeners

- **Reliability:** No audio drops during room sessions; graceful degradation under network jitter

- **Quality:** Opus codec at 32-64kbps, adaptive bitrate based on network conditions, echo cancellation

- **Ephemerality:** Rooms are not recorded by default — audio is transient (privacy by design)


## Flow 1: Joining a Room & Real-Time Audio


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 370" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FFD700">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="80" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Mobile Client
</text>


<rect x="190" y="50" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="255" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API Gateway
</text>
<text x="255" y="87" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(REST / WS)
</text>


<rect x="370" y="50" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="435" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Room Service
</text>
<text x="435" y="87" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(State Machine)
</text>


<rect x="550" y="20" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="615" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Agora SFU
</text>
<text x="615" y="57" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Audio Relay)
</text>


<rect x="550" y="90" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="615" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Signaling Server
</text>
<text x="615" y="127" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(WebRTC)
</text>


<rect x="730" y="50" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="795" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
TURN/STUN
</text>
<text x="795" y="87" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(NAT Traversal)
</text>


<rect x="370" y="180" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="435" y="202" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Room State DB
</text>
<text x="435" y="217" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Redis Cluster)
</text>


<rect x="550" y="180" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="615" y="202" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Presence Service
</text>
<text x="615" y="217" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Who's online)
</text>


<rect x="190" y="260" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="255" y="282" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
WebSocket Hub
</text>
<text x="255" y="297" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Room Events)
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#FFD700" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="320" y1="75" x2="365" y2="75" stroke="#FFD700" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="65" x2="545" y2="45" stroke="#FFD700" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="85" x2="545" y2="110" stroke="#FFD700" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="680" y1="45" x2="725" y2="65" stroke="#FFD700" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="680" y1="115" x2="725" y2="85" stroke="#FFD700" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="435" y1="100" x2="435" y2="175" stroke="#FFD700" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="205" x2="545" y2="205" stroke="#FFD700" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="370" y1="130" x2="300" y2="255" stroke="#FFD700" stroke-width="1.5" marker-end="url(#ah)">
</line>


</svg>

</details>


### Steps


1. Client sends JOIN request via API Gateway with room_id and auth token

2. Room Service validates room access (public/invite-only/club-member), checks capacity, adds user to participant list

3. Signaling Server initiates WebRTC session — SDP offer/answer exchange via WebSocket

4. STUN server determines client's public IP:port; TURN relay used if direct P2P fails (symmetric NAT)

5. Agora SFU (Selective Forwarding Unit) receives audio from speakers and selectively relays to all listeners

6. Room state (participants, roles, speaker queue) stored in Redis Cluster for fast reads

7. WebSocket Hub pushes real-time events: user_joined, speaker_changed, hand_raised, room_ended

8. Presence Service updates user's current room for social graph notifications


### Example


User opens app, sees "Tech Talk with @elonmusk" with 3,200 listeners. Taps join → Room Service checks capacity (OK, <8K), signaling exchanges SDP, STUN resolves public endpoint, connects to nearest Agora SFU edge node in us-east. Audio streams from 4 speakers forwarded to client. WebSocket delivers participant count updates and speaker changes in real-time.


### Deep Dives


SFU Architecture (Agora)


**Protocol:** UDP-based RTP/RTCP with custom congestion control (GCC — Google Congestion Control variant)


**Topology:** SFU (not MCU) — each speaker's audio stream forwarded individually; client-side mixing saves server CPU


**Edge Nodes:** SFUs deployed in 200+ PoPs worldwide; users connect to nearest edge for minimum RTT


**Scaling:** Large rooms cascaded across multiple SFU instances — inter-SFU relay over dedicated backbone


WebRTC Signaling


**Protocol:** WebSocket (wss://) for signaling; UDP for media


**SDP:** Session Description Protocol — describes codec capabilities, ICE candidates, DTLS fingerprint


**ICE:** Interactive Connectivity Establishment — tries direct P2P first, falls back to TURN relay


**SRTP:** Secure RTP with DTLS-SRTP key exchange — end-to-hop encryption (not E2E in SFU model)


Opus Codec


**Codec:** Opus (RFC 6716) — hybrid codec combining SILK (speech) and CELT (music)


**Bitrate:** 32kbps (speech-optimized) to 64kbps (high quality); adaptive based on network


**Frame:** 20ms frames (50 frames/second), low algorithmic delay (~26.5ms)


**FEC:** In-band Forward Error Correction — redundant data in subsequent packets for loss recovery


Room State (Redis)


**Data:** Hash per room — participants list, speaker queue, moderator list, room metadata


**Pub/Sub:** Redis Pub/Sub channels per room for real-time event distribution to WebSocket hubs


**Expiry:** Rooms auto-expire after 24 hours of inactivity; TTL on all room keys


**Scaling:** Redis Cluster with hash-slot based sharding by room_id


## Flow 2: Room Discovery & Hallway Feed


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#DAA520">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="90" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Client
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Discovery Service
</text>
<text x="255" y="97" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Feed Builder)
</text>


<rect x="370" y="30" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Social Graph
</text>
<text x="435" y="67" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Neo4j / DGraph)
</text>


<rect x="370" y="100" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="435" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Active Rooms
</text>
<text x="435" y="137" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Redis Cache)
</text>


<rect x="550" y="60" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="615" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Ranking ML
</text>
<text x="615" y="97" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Personalized)
</text>


<rect x="730" y="60" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="795" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Topic/Club DB
</text>
<text x="795" y="97" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(PostgreSQL)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#DAA520" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#DAA520" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="95" x2="365" y2="120" stroke="#DAA520" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="500" y1="55" x2="545" y2="75" stroke="#DAA520" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="500" y1="125" x2="545" y2="95" stroke="#DAA520" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="680" y1="85" x2="725" y2="85" stroke="#DAA520" stroke-width="2" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Steps


1. Client opens "Hallway" — sends GET /discover with user's interests, followed clubs, followed users

2. Discovery Service queries Social Graph: who does user follow → which rooms are they in?

3. Active Rooms cache (Redis) provides real-time room metadata: topic, speaker count, listener count, duration

4. Ranking ML model personalizes room order: social signal (friends in room) × topic relevance × room momentum (growth rate)

5. Scheduled rooms from followed clubs/topics surfaced alongside live rooms

6. Results returned paginated with refresh interval (pull-to-refresh + periodic background sync)


### Example


User follows 200 people, 5 clubs. Discovery finds 3 followed users currently speaking in rooms. ML ranks: (1) "Startup Fundraising" (2 friends speaking, topic match), (2) "Music Production" (1 friend listening, club match), (3) "Daily News" (trending, 5K listeners). Scheduled: "AI Ethics Panel" in 2 hours from followed club.


### Deep Dives


Social Graph


**Store:** Graph database (Neo4j or DGraph) for follow relationships, club memberships, topic interests


**Queries:** 2-hop traversal: user → follows → currently_in_room (real-time join with Redis)


**Caching:** User's follow list cached in Redis (invalidated on follow/unfollow); TTL 5 minutes


Ranking Algorithm


**Score:** w1×social_signal + w2×topic_match + w3×room_momentum + w4×speaker_quality


**Social Signal:** Number of followed users in room, weighted by interaction frequency


**Momentum:** Listener growth rate (joins/minute) — indicates engaging content


**Cold Start:** New users get topic-based recommendations until social graph is built


## Flow 3: Room Moderation & Speaker Management


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#8B6914">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Listener Client
</text>
<text x="80" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Raise Hand)
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
WebSocket Hub
</text>
<text x="255" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Event Broker)
</text>


<rect x="370" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="435" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Room Service
</text>
<text x="435" y="97" text-anchor="middle" fill="#fff" font-size="10">
(State Machine)
</text>


<rect x="550" y="30" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="615" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Moderator Client
</text>
<text x="615" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Accept/Deny)
</text>


<rect x="550" y="100" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="615" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Abuse Detection
</text>
<text x="615" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Audio Classifier)
</text>


<rect x="730" y="60" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="795" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Trust & Safety
</text>
<text x="795" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Action Pipeline)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#8B6914" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="85" x2="365" y2="85" stroke="#8B6914" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="75" x2="545" y2="55" stroke="#8B6914" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="95" x2="545" y2="120" stroke="#8B6914" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="680" y1="55" x2="725" y2="75" stroke="#8B6914" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="680" y1="125" x2="725" y2="95" stroke="#8B6914" stroke-width="2" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Steps


1. Listener taps "Raise Hand" → event sent via WebSocket to Room Service

2. Room Service adds user to speaker queue (Redis sorted set, scored by request time)

3. Moderator receives real-time notification of hand raise; sees queue of pending speakers

4. Moderator accepts → Room Service changes user role from LISTENER to SPEAKER

5. Signaling server renegotiates: opens user's audio upstream channel to SFU

6. Abuse Detection ML monitors speaker audio streams for policy violations (hate speech, harassment)

7. Trust & Safety pipeline can auto-mute, remove speaker, or flag for human review

8. All role changes broadcast to all room participants via WebSocket


### Example


In a 500-person room, listener @alice raises hand. Moderator sees her at position #3 in queue. After 2 minutes, moderator promotes @alice to speaker. Signaling renegotiates SDP — Alice's microphone input now routed through SFU to all 500 listeners. Audio classifier monitors in real-time at 1-second windows.


### Deep Dives


Role State Machine


**Roles:** LISTENER → INVITED_SPEAKER → SPEAKER → MODERATOR (progressive promotion)


**Transitions:** Raise hand (L→queue), Accept (queue→S), Promote (S→M), Mute (S→L), Kick (any→removed)


**Consistency:** Redis MULTI/EXEC for atomic role transitions; distributed lock per room


Audio Abuse Detection


**Model:** CNN-based audio classifier (trained on labeled hate speech audio datasets)


**Processing:** 1-second sliding windows, real-time inference on GPU edge servers


**Actions:** Confidence >0.9 → auto-mute speaker; 0.7-0.9 → flag for moderator review


**Fallback:** Speech-to-text (Whisper) → text classifier for nuanced policy violations


## Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 480" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FFD700">
</polygon>
</marker>
</defs>


<rect x="20" y="80" width="130" height="50" rx="8" fill="#34A853">
</rect>
<text x="85" y="105" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Mobile Clients
</text>
<text x="85" y="120" text-anchor="middle" fill="#fff" font-size="10">
(iOS / Android)
</text>


<rect x="200" y="50" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="265" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API Gateway
</text>
<text x="265" y="87" text-anchor="middle" fill="#fff" font-size="10">
(REST + Auth)
</text>


<rect x="200" y="120" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="265" y="142" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
WebSocket Hub
</text>
<text x="265" y="157" text-anchor="middle" fill="#fff" font-size="10">
(Real-time Events)
</text>


<rect x="390" y="20" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="455" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Room Service
</text>


<rect x="390" y="90" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="455" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Discovery Service
</text>


<rect x="390" y="160" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="455" y="182" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Presence Service
</text>


<rect x="580" y="20" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="645" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Agora SFU
</text>
<text x="645" y="57" text-anchor="middle" fill="#fff" font-size="10">
(Audio Relay)
</text>


<rect x="580" y="90" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="645" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Signaling Server
</text>
<text x="645" y="127" text-anchor="middle" fill="#fff" font-size="10">
(WebRTC)
</text>


<rect x="770" y="50" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="835" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
TURN / STUN
</text>
<text x="835" y="87" text-anchor="middle" fill="#fff" font-size="10">
(NAT Traversal)
</text>


<rect x="390" y="300" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="455" y="322" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
PostgreSQL
</text>
<text x="455" y="337" text-anchor="middle" fill="#fff" font-size="10">
(Users, Clubs)
</text>


<rect x="580" y="300" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="645" y="322" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Redis Cluster
</text>
<text x="645" y="337" text-anchor="middle" fill="#fff" font-size="10">
(Room State)
</text>


<rect x="770" y="300" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="835" y="322" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Neo4j / DGraph
</text>
<text x="835" y="337" text-anchor="middle" fill="#fff" font-size="10">
(Social Graph)
</text>


<rect x="580" y="400" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="645" y="422" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Abuse Detection
</text>
<text x="645" y="437" text-anchor="middle" fill="#fff" font-size="10">
(Audio ML)
</text>


<line x1="150" y1="90" x2="195" y2="75" stroke="#FFD700" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="150" y1="115" x2="195" y2="140" stroke="#FFD700" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="330" y1="75" x2="385" y2="45" stroke="#FFD700" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="330" y1="85" x2="385" y2="115" stroke="#FFD700" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="330" y1="155" x2="385" y2="185" stroke="#FFD700" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="520" y1="45" x2="575" y2="45" stroke="#FFD700" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="520" y1="55" x2="575" y2="110" stroke="#FFD700" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="710" y1="45" x2="765" y2="65" stroke="#FFD700" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="710" y1="115" x2="765" y2="85" stroke="#FFD700" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="455" y1="70" x2="455" y2="295" stroke="#FFD700" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="455" y1="210" x2="580" y2="320" stroke="#FFD700" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="520" y1="140" x2="770" y2="315" stroke="#FFD700" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="645" y1="70" x2="645" y2="395" stroke="#FFD700" stroke-width="1.5" marker-end="url(#ah4)">
</line>


</svg>

</details>


## Database Schema


### PostgreSQL — Users & Clubs

`CREATE TABLE users (
  user_id      UUID PRIMARY KEY,      `PK`
  username     VARCHAR(30) UNIQUE,    `IDX`
  display_name VARCHAR(100),
  bio          TEXT,
  avatar_url   TEXT,
  phone_hash   CHAR(64) UNIQUE,      `IDX`
  is_verified  BOOLEAN DEFAULT FALSE,
  follower_ct  INT DEFAULT 0,
  following_ct INT DEFAULT 0,
  created_at   TIMESTAMPTZ
);

CREATE TABLE clubs (
  club_id      UUID PRIMARY KEY,      `PK`
  name         VARCHAR(100),
  description  TEXT,
  avatar_url   TEXT,
  topic_ids    UUID[],
  member_count INT DEFAULT 0,
  is_private   BOOLEAN DEFAULT FALSE,
  created_by   UUID REFERENCES users,  `FK`
  created_at   TIMESTAMPTZ
);

CREATE TABLE club_members (
  club_id  UUID REFERENCES clubs,   `FK`
  user_id  UUID REFERENCES users,   `FK`
  role     VARCHAR(20),  -- admin, member, follower
  joined_at TIMESTAMPTZ,
  PRIMARY KEY (club_id, user_id)
);`


### Redis — Room State

`# Room metadata (Hash)
HSET room:{room_id} title "Tech Talk"
HSET room:{room_id} topic "Technology"
HSET room:{room_id} created_by {user_id}
HSET room:{room_id} started_at 1707500000
HSET room:{room_id} is_private false

# Participants by role (Sorted Sets)
ZADD room:{room_id}:speakers {join_ts} {user_id}
ZADD room:{room_id}:listeners {join_ts} {user_id}
ZADD room:{room_id}:moderators {join_ts} {user_id}

# Speaker queue (Sorted Set — score = request time)
ZADD room:{room_id}:hand_raised {ts} {user_id}

# User presence (Hash with TTL)
HSET presence:{user_id} room_id {room_id}
HSET presence:{user_id} role "speaker"
EXPIRE presence:{user_id} 300  # heartbeat refresh`


 `SHARD` Sharded by room_id hash slot across Redis Cluster


 `TTL` Room keys expire 24h after last activity; presence keys expire on 5-min heartbeat


### Graph DB — Social Graph

`# Follow relationship
(user_a)-[:FOLLOWS {since: timestamp}]->(user_b)

# Club membership
(user)-[:MEMBER_OF {role: "admin", since: ts}]->(club)

# Topic interest
(user)-[:INTERESTED_IN {weight: 0.8}]->(topic)

# Room participation (ephemeral — cleared on room end)
(user)-[:IN_ROOM {role: "speaker"}]->(room)

Key queries:
- "Which of my followed users are currently in rooms?"
  MATCH (me)-[:FOLLOWS]->(friend)-[:IN_ROOM]->(room) RETURN room, friend
- "Rooms from clubs I'm in"
  MATCH (me)-[:MEMBER_OF]->(club)<-[:HOSTED_BY]-(room) RETURN room`


## Cache & CDN Deep Dive


Room State Cache (Redis)


**Strategy:** Write-through — all room mutations go through Room Service which writes to Redis atomically


**Eviction:** TTL-based — rooms auto-expire; no LRU needed since data is transient


**Pub/Sub:** Redis Pub/Sub per room channel broadcasts events to all connected WebSocket hubs


Social Graph Cache


**Cached:** User's follow list (up to 10K follows), club memberships cached in Redis


**Strategy:** Cache-aside with 5-minute TTL; invalidated on follow/unfollow actions


**Precompute:** "Friends in rooms" computed every 30 seconds and cached per-user for fast hallway loads


CDN (Cloudflare)


**Static:** Profile avatars, club images served via Cloudflare CDN with 24hr cache


**API:** No CDN caching for real-time endpoints (room state, discovery feed) — must be fresh


**Audio:** Audio not cached (real-time ephemeral) — SFU handles all media distribution


## Scaling & Load Balancing


- **SFU Scaling:** Cascaded SFU topology — large rooms span multiple SFU instances with inter-SFU relay over dedicated fiber backbone; each SFU handles ~500 audio streams

- **WebSocket Hub:** Consistent hashing by room_id — all participants of a room connect to same hub shard; hub instances behind L4 load balancer (sticky sessions via connection ID)

- **Geographic Distribution:** SFU edge nodes in 200+ PoPs; clients connect to nearest edge for minimum audio latency; signaling servers in 5 major regions

- **Room Service:** Stateless microservice, horizontally scaled behind API gateway; Redis Cluster handles state

- **Database:** PostgreSQL read replicas for user/club queries; graph DB sharded by user_id hash; Redis Cluster with 6+ nodes


## Tradeoffs


> **

✅ SFU vs MCU


- Lower server CPU — no audio mixing on server

- Lower latency — no encoding/decoding roundtrip

- Client can choose which streams to render


❌ SFU vs MCU


- Higher client bandwidth — receives N separate streams

- Client-side mixing complexity (especially on low-end devices)

- More complex congestion control with multiple streams


✅ Ephemeral Audio


- Privacy by design — no recordings to leak or subpoena

- Reduced storage costs — no audio blob storage needed

- Encourages authentic, unfiltered conversation


❌ Ephemeral Audio


- Cannot replay missed content

- Harder to moderate after-the-fact (no audit trail)

- Cannot build content library for SEO/discovery


## Alternative Approaches


MCU (Multipoint Control Unit)


Server mixes all audio into a single stream per participant. Reduces client bandwidth (receives 1 stream instead of N) but massively increases server CPU. Better for very large rooms (10K+) where clients can't handle many streams, but cost-prohibitive at scale.


P2P Mesh (Small Rooms)


For rooms with ≤5 speakers, full P2P mesh avoids server infrastructure entirely. Each participant sends audio directly to all others. Zero server cost but O(n²) connections make it impractical beyond ~5 participants.


Recorded Rooms (Twitter Spaces Model)


Record and store room audio for replay. Requires blob storage (S3), transcoding pipeline, transcription service, and content moderation review. Enables async consumption but changes the social dynamic and adds significant infrastructure cost.


## Additional Information


- **Agora.io Integration:** Clubhouse outsourced real-time audio infrastructure to Agora — their SDK handles WebRTC, SFU, TURN/STUN, adaptive bitrate, and echo cancellation out of the box

- **Invite-Only Mechanics:** Original invite system used phone number graph — each user gets N invites, creating scarcity and social pressure. Implemented as simple counter in users table + invite_log table.

- **Backchannel (DMs):** Separate messaging system using standard WebSocket + persistent storage (PostgreSQL) — not real-time audio; similar to Twitter DM architecture

- **Spatial Audio (Experimental):** 3D audio positioning using HRTF (Head-Related Transfer Functions) — each speaker positioned in virtual "room" for more natural conversation feel

- **Echo Cancellation:** Acoustic Echo Cancellation (AEC) runs on-device using WebRTC's built-in audio processing pipeline — critical for speaker-phone usage without headphones