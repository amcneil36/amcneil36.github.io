# System Design: Twitch


## Functional Requirements


  1. **Live streaming** – Streamers broadcast live video/audio to thousands or millions of viewers.

  2. **Live chat** – Real-time text chat alongside each stream with emotes and moderation.

  3. **VOD (Video on Demand)** – Past broadcasts are saved and viewable as VODs.

  4. **Follow/subscribe** – Users follow channels and subscribe (paid) for perks.

  5. **Stream discovery** – Browse streams by game/category, recommended streams, search.

  6. **Clips** – Viewers create short clips from live streams.

  7. **Donations/bits** – Viewers can donate to streamers.

  8. **Notifications** – Push notifications when followed streamers go live.


## Non-Functional Requirements


  1. **Low latency video** – Stream-to-viewer latency <5 seconds (sub-second for competitive gaming).

  2. **High availability** – 99.99%; any stream interruption is unacceptable.

  3. **Scalability** – Support 100K+ concurrent streams, individual streams with 500K+ concurrent viewers.

  4. **Adaptive bitrate** – Serve multiple video qualities (160p to 1080p60) based on viewer's bandwidth.

  5. **Global reach** – Serve viewers worldwide with low buffering.


## Flow 1: Live Video Streaming (Streamer → Viewer)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 400" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a1" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="150" width="100" height="55" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="60" y="175" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Streamer
</text>

  
<text x="60" y="190" text-anchor="middle" fill="#fff" font-size="9">
(OBS/XSplit)
</text>

  
<line x1="110" y1="177" x2="180" y2="177" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<text x="145" y="167" text-anchor="middle" fill="#555" font-size="8">
RTMP
</text>

  
<rect x="180" y="148" width="110" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="235" y="175" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Ingest
</text>

  
<text x="235" y="190" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Server
</text>

  
<line x1="290" y1="177" x2="370" y2="177" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="370" y="148" width="120" height="60" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="430" y="175" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Transcoding
</text>

  
<text x="430" y="190" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Service
</text>

  
<text x="430" y="220" text-anchor="middle" fill="#555" font-size="8">
Multiple qualities
</text>

  

  
<line x1="490" y1="177" x2="570" y2="177" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="570" y="148" width="110" height="60" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="625" y="175" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Origin
</text>

  
<text x="625" y="190" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Server
</text>

  

  
<line x1="680" y1="177" x2="760" y2="177" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<text x="720" y="167" text-anchor="middle" fill="#555" font-size="8">
HLS/DASH
</text>

  
<rect x="760" y="148" width="80" height="60" rx="8" fill="#607d8b" stroke="#37474f" stroke-width="2">
</rect>

  
<text x="800" y="183" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
CDN
</text>

  

  
<line x1="840" y1="165" x2="910" y2="120" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<line x1="840" y1="178" x2="910" y2="178" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<line x1="840" y1="190" x2="910" y2="235" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="910" y="100" width="80" height="35" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="950" y="122" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Viewer 1
</text>

  
<rect x="910" y="160" width="80" height="35" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="950" y="182" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Viewer 2
</text>

  
<rect x="910" y="218" width="80" height="35" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="950" y="240" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Viewer N
</text>

  

  
<line x1="625" y1="208" x2="625" y2="280" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="575" y="280" width="100" height="40" rx="6" fill="#795548" stroke="#4e342e" stroke-width="1.5">
</rect>

  
<text x="625" y="305" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
VOD Storage
</text>

  
<text x="625" y="335" text-anchor="middle" fill="#555" font-size="8">
(S3)
</text>


</svg>

</details>


> **
Example: Streamer goes live


Alice opens OBS Studio, configures her stream key, and starts streaming. OBS sends the video/audio stream via **RTMP (Real-Time Messaging Protocol)** over TCP to the nearest **Ingest Server**. The Ingest Server receives the raw video and forwards it to the **Transcoding Service**, which encodes the stream into multiple quality levels: 1080p60 (6 Mbps), 720p60 (4.5 Mbps), 480p (1.5 Mbps), 160p (0.4 Mbps). Each quality is segmented into 2-second **HLS** (HTTP Live Streaming) chunks. The **Origin Server** packages these chunks and generates HLS manifests (.m3u8 playlists). The **CDN** pulls chunks from the origin and distributes to edge locations globally. Viewers' video players request the HLS manifest, select a quality based on bandwidth, and download chunks from the nearest CDN edge. Latency: ~3-5 seconds from streamer to viewer. The Origin Server also writes chunks to **S3** for VOD.


> **
Example: Viewer switches quality


Bob is watching on mobile with limited bandwidth. His player starts at 480p. He moves to a WiFi network — the player detects increased bandwidth and seamlessly switches to 1080p by requesting higher-quality chunks from the CDN. The HLS manifest lists all available quality levels.


### Deep Dive: Components


> **
RTMP Ingest


**Protocol:** RTMP over TCP (port 1935). RTMP is chosen because all major streaming software (OBS, XSplit, Streamlabs) supports it natively. It provides low-latency transport with TCP reliability. Alternative: SRT (Secure Reliable Transport) over UDP — lower latency but less software support.


**Why TCP for ingest:** The streamer-to-server path must be reliable; dropped frames degrade video quality for all viewers. TCP's retransmission guarantees no frame loss.


> **
Transcoding Service


GPU-accelerated video encoding (H.264/H.265 codec). Each stream requires ~1 GPU for real-time transcoding into multiple quality levels. For a platform with 100K concurrent streams, that's ~100K GPUs (or fewer with hardware encoding like NVENC which handles multiple streams per GPU).


> **
CDN (Critical for Live Streaming)


**Why CDN is essential:** A single popular stream may have 500K concurrent viewers. Serving all of them from the origin would require ~3 Tbps of bandwidth. The CDN distributes this across 200+ edge locations, each serving a fraction of viewers.


**Caching strategy:** Each 2-second HLS chunk is cached at the edge for 10 seconds (just enough to serve it to all viewers requesting it). Pull-based: the edge fetches from origin on the first viewer request.


**Eviction:** LRU, but chunks are naturally evicted because live stream chunks have very short TTLs.


**Protocol to viewers:** HTTPS GET for HLS chunks and manifests. HTTP/2 for multiplexing multiple chunk requests.


## Flow 2: Live Chat


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 850 280" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a2" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="100" width="80" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="50" y="130" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Viewer
</text>

  
<line x1="90" y1="125" x2="160" y2="125" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<text x="125" y="115" text-anchor="middle" fill="#555" font-size="8">
WebSocket
</text>

  
<rect x="160" y="95" width="100" height="60" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="210" y="123" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Chat
</text>

  
<text x="210" y="138" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Service
</text>

  

  
<line x1="260" y1="125" x2="340" y2="125" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<rect x="340" y="100" width="90" height="50" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="385" y="123" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Pub/Sub
</text>

  
<text x="385" y="138" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
(Redis)
</text>

  

  
<line x1="430" y1="115" x2="510" y2="80" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<line x1="430" y1="130" x2="510" y2="130" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<line x1="430" y1="140" x2="510" y2="175" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<rect x="510" y="60" width="100" height="40" rx="6" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="560" y="84" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Chat Svc (1)
</text>

  
<rect x="510" y="110" width="100" height="40" rx="6" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="560" y="134" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Chat Svc (2)
</text>

  
<rect x="510" y="160" width="100" height="40" rx="6" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="560" y="184" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Chat Svc (N)
</text>

  

  
<line x1="610" y1="80" x2="680" y2="80" stroke="#333" stroke-width="1" marker-end="url(#a2)">
</line>

  
<line x1="610" y1="130" x2="680" y2="130" stroke="#333" stroke-width="1" marker-end="url(#a2)">
</line>

  
<line x1="610" y1="180" x2="680" y2="180" stroke="#333" stroke-width="1" marker-end="url(#a2)">
</line>

  
<text x="645" y="72" text-anchor="middle" fill="#555" font-size="7">
WebSocket
</text>

  
<rect x="680" y="60" width="70" height="35" rx="5" fill="#4caf50" stroke="#388e3c" stroke-width="1">
</rect>

  
<text x="715" y="82" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Viewers
</text>

  
<rect x="680" y="115" width="70" height="35" rx="5" fill="#4caf50" stroke="#388e3c" stroke-width="1">
</rect>

  
<text x="715" y="137" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Viewers
</text>

  
<rect x="680" y="165" width="70" height="35" rx="5" fill="#4caf50" stroke="#388e3c" stroke-width="1">
</rect>

  
<text x="715" y="187" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Viewers
</text>

  

  
<line x1="210" y1="155" x2="210" y2="220" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a2)">
</line>

  
<rect x="165" y="220" width="90" height="35" rx="6" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="210" y="242" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Mod Filter
</text>


</svg>

</details>


> **
Example: Chat message in a 50K-viewer stream


Bob types "PogChamp nice play!" in Alice's stream chat. His message is sent via **WebSocket** to the **Chat Service** instance holding his connection. The service runs the message through the **Moderation Filter** (banned words, spam detection, slow mode enforcement). The approved message is published to a **Redis Pub/Sub** channel for this stream (`chat:stream_alice`). All Chat Service instances subscribed to this channel receive the message and push it to their connected viewers via WebSocket. With 50K viewers spread across 50 Chat Service instances (~1K connections each), every viewer sees Bob's message within ~100ms.


> **
WebSocket for Chat


**Why WebSocket:** Chat requires real-time bidirectional communication (viewers both send and receive messages). In a busy stream, 100+ messages/second arrive — WebSocket's persistent connection avoids the overhead of establishing a new connection for each message.


**Connection management:** Viewers connect to a Chat Service instance via L4 load balancer with consistent hashing on stream_id (so all viewers of the same stream are likely on the same instance, reducing Pub/Sub overhead).


> **
Redis Pub/Sub for Chat Fan-out


Each stream has a Pub/Sub channel. When a message is published, Redis broadcasts to all subscribed Chat Service instances. This is more efficient than a message queue because chat messages are ephemeral — we don't need durability or replay.


**Why not Kafka:** Kafka provides durability and ordering but adds latency (~5-10ms). Chat messages are fire-and-forget; if one is lost, it's not critical. Redis Pub/Sub delivers in <1ms.


## Overall Combined Flow


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a3" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<text x="10" y="20" fill="#1a73e8" font-size="12" font-weight="bold">
VIDEO PATH
</text>

  
<rect x="10" y="30" width="75" height="35" rx="5" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="47" y="52" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Streamer
</text>

  
<line x1="85" y1="47" x2="120" y2="47" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<text x="102" y="40" text-anchor="middle" fill="#555" font-size="7">
RTMP
</text>

  
<rect x="120" y="30" width="70" height="35" rx="5" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="155" y="52" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Ingest
</text>

  
<line x1="190" y1="47" x2="225" y2="47" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="225" y="30" width="80" height="35" rx="5" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="265" y="52" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Transcode
</text>

  
<line x1="305" y1="47" x2="340" y2="47" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="340" y="30" width="70" height="35" rx="5" fill="#ff9800" stroke="#f57c00" stroke-width="1.5">
</rect>

  
<text x="375" y="52" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Origin
</text>

  
<line x1="410" y1="47" x2="445" y2="47" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="445" y="30" width="55" height="35" rx="5" fill="#607d8b" stroke="#37474f" stroke-width="1.5">
</rect>

  
<text x="472" y="52" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
CDN
</text>

  
<line x1="500" y1="47" x2="535" y2="47" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<text x="517" y="40" text-anchor="middle" fill="#555" font-size="7">
HLS
</text>

  
<rect x="535" y="30" width="65" height="35" rx="5" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="567" y="52" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Viewers
</text>

  
<line x1="375" y1="65" x2="375" y2="90" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="345" y="90" width="60" height="25" rx="4" fill="#795548" stroke="#4e342e" stroke-width="1">
</rect>

  
<text x="375" y="107" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
S3 VOD
</text>

  
<text x="10" y="145" fill="#1a73e8" font-size="12" font-weight="bold">
CHAT PATH
</text>

  
<rect x="10" y="155" width="65" height="30" rx="5" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="42" y="174" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Viewer
</text>

  
<line x1="75" y1="170" x2="110" y2="170" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<text x="92" y="163" text-anchor="middle" fill="#555" font-size="7">
WS
</text>

  
<rect x="110" y="155" width="75" height="30" rx="5" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="147" y="174" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Chat Svc
</text>

  
<line x1="185" y1="170" x2="220" y2="170" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="220" y="155" width="75" height="30" rx="5" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="257" y="174" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Mod Filter
</text>

  
<line x1="295" y1="170" x2="330" y2="170" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="330" y="155" width="70" height="30" rx="5" fill="#9c27b0" stroke="#6a1b9a" stroke-width="1.5">
</rect>

  
<text x="365" y="174" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Pub/Sub
</text>

  
<line x1="400" y1="170" x2="435" y2="170" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="435" y="155" width="75" height="30" rx="5" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="472" y="174" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
All Chat Svcs
</text>

  
<line x1="510" y1="170" x2="545" y2="170" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="545" y="155" width="70" height="30" rx="5" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="580" y="174" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
All Viewers
</text>


</svg>

</details>


> **Combined Example

**Video:** Streamer → RTMP → Ingest → Transcode (multi-quality) → Origin → CDN → HLS to viewers (3-5s latency). Origin also saves chunks to S3 for VOD.  
**Chat:** Viewer sends message → WS → Chat Service → Mod Filter → Redis Pub/Sub → all Chat Service instances → WS push to all viewers.


## Database Schema


### SQL (PostgreSQL)

1. channels

  
  
  
  
  
  
  
  
  


| Field          | Type         | Key   | Description                  |
|----------------|--------------|-------|------------------------------|
| channel_id     | BIGINT       | `PK`  | Unique channel ID            |
| user_id        | BIGINT       | `FK`  | Channel owner                |
| stream_key     | VARCHAR(64)  | `IDX` | Authentication key for RTMP  |
| is_live        | BOOLEAN      | `IDX` | Currently streaming?         |
| viewer_count   | INT          |       | Denormalized current viewers |
| follower_count | INT          |       | Denormalized follower count  |
| category       | VARCHAR(100) | `IDX` | Game/category name           |
| title          | VARCHAR(255) |       | Stream title                 |


**Index:** B-tree on `(is_live, category, viewer_count DESC)` for "browse live streams by category, sorted by viewers."


### NoSQL (Cassandra)

2. chat_messages

  
  
  
  
  
  


| Field      | Type       | Key  | Description            |
|------------|------------|------|------------------------|
| channel_id | BIGINT     | `PK` | Stream channel         |
| message_id | TimeUUID   | `PK` | Chronological order    |
| user_id    | BIGINT     |      | Message sender         |
| text       | TEXT       |      | Message content        |
| emotes     | LIST<TEXT> |      | Emote codes in message |


**Why NoSQL:** Chat messages are extremely high-volume, write-heavy, and time-series in nature. No complex queries needed.


3. vods

  
  
  
  
  
  
  


| Field            | Type      | Key         | Description        |
|------------------|-----------|-------------|--------------------|
| vod_id           | BIGINT    | `PK`        | VOD ID             |
| channel_id       | BIGINT    | `FK`  `IDX` | Channel            |
| s3_manifest_url  | TEXT      |             | HLS manifest in S3 |
| duration_seconds | INT       |             | VOD duration       |
| view_count       | INT       |             | Views              |
| created_at       | TIMESTAMP |             | Stream date        |


## Cache Deep Dive


> **
CDN for Video


**This is the most cache-heavy system design.** Live HLS chunks are cached at CDN edge for ~10 seconds. All viewers of the same stream get served from the same edge cache. CDN hit ratio for live streams: ~99.9%. VOD content is also served via CDN with TTL = 24 hours.


> **
Redis for Real-time Data


Viewer counts per stream (INCR/DECR on join/leave), chat Pub/Sub channels, and browse page data (top live streams by category) are cached in Redis. Write-through for viewer counts. LRU eviction.


## Scaling Considerations


  - **Ingest Servers:** Geo-distributed; streamers connect to the nearest ingest point. 1 ingest server per ~1000 concurrent streamers.

  - **Transcoding:** GPU-based, auto-scales with number of live streams. Most expensive component.

  - **CDN:** Handles viewer scale inherently. No action needed per viewer.

  - **Chat:** Each Chat Service instance handles ~100K WebSocket connections. For a 500K-viewer stream: 5 instances, all subscribed to the same Redis Pub/Sub channel.

  - **Load Balancer:** L4 LB for RTMP ingest (TCP). L4 LB for WebSocket chat (sticky sessions). L7 LB for REST APIs.


## Tradeoffs and Deep Dives


> **HLS vs. WebRTC

HLS has 3-5s latency but scales to millions via CDN. WebRTC has sub-second latency but doesn't scale through CDN (peer-to-peer). Twitch uses HLS for normal streams and a low-latency HLS mode (~2s) for competitive gaming. WebRTC is used for 1-on-1 calls (like Zoom), not broadcast.


## Alternative Approaches

WebRTC-based Broadcasting

Sub-second latency, ideal for interactive streams. Not chosen because CDN can't cache WebRTC (it's P2P), making it prohibitively expensive at 500K+ viewers.

MPEG-DASH instead of HLS

Open standard (HLS is Apple). Both work similarly. HLS chosen due to wider device support (iOS native).


## Additional Information


### Slow Mode & Chat Throttling


In busy streams (100K+ viewers), chat moves too fast to read. Slow mode limits users to 1 message per N seconds. The Chat Service enforces this with a per-user rate limiter (Redis TTL key: `slowmode:{stream_id}:{user_id}`).


### Stream Health Monitoring


The Ingest Server monitors stream bitrate, frame rate, and keyframe intervals. If the streamer's upload bandwidth drops, a warning is displayed. If the stream disconnects, a "reconnecting" screen is shown to viewers for 30 seconds before the stream is marked offline.