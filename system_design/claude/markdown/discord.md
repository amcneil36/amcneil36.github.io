# 🎮 Design Discord


Real-time communication platform with text channels, voice/video, screen sharing — 200M+ MAU, 19M+ active servers


## 1. Functional Requirements


- **Servers & Channels:** Create servers (guilds) with text/voice/stage channels, categories, roles & permissions

- **Real-Time Messaging:** Send/receive messages instantly in channels and DMs with rich embeds, reactions, threads, replies

- **Voice & Video:** Low-latency voice chat in voice channels (up to 5000 users), video calls, Go Live screen sharing

- **Presence:** Online/idle/DND/invisible status, custom status, game activity detection

- **File Sharing:** Upload images, videos, files (up to 25MB free, 500MB Nitro) with CDN delivery

- **Bots & Integrations:** Bot API, slash commands, webhooks, OAuth2 app integrations

- **Search:** Full-text message search within servers with filters (from:, in:, has:, before:/after:)

- **Notifications:** Per-channel notification settings, @mentions, @everyone, push notifications


## 2. Non-Functional Requirements


- **Latency:** Message delivery <100ms (same region), voice latency <70ms (opus codec)

- **Scale:** 200M+ MAU, 4B+ messages/day, 15M+ concurrent voice users peak

- **Availability:** 99.99% for messaging, 99.9% for voice (UDP-based, inherently lossy)

- **Consistency:** Causal ordering within a channel (messages appear in send order); eventual for cross-channel

- **Concurrent Users per Server:** Up to 1M members in a server, 5000 in a voice channel

- **Storage:** Unlimited message history (no TTL for paid or free), efficient media storage


## 3. Flow 1 — Real-Time Text Messaging


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 400" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#5865F2">
</polygon>
</marker>
</defs>


<rect x="20" y="160" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="190" text-anchor="middle" fill="#fff" font-size="13">
Sender
</text>
<text x="75" y="205" text-anchor="middle" fill="#fff" font-size="11">
WebSocket
</text>


<rect x="180" y="160" width="130" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="245" y="190" text-anchor="middle" fill="#fff" font-size="13">
Gateway
</text>
<text x="245" y="205" text-anchor="middle" fill="#fff" font-size="11">
(WebSocket server)
</text>


<rect x="360" y="60" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="425" y="90" text-anchor="middle" fill="#fff" font-size="13">
Message Service
</text>


<rect x="360" y="160" width="130" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="425" y="185" text-anchor="middle" fill="#fff" font-size="13">
Pub/Sub
</text>
<text x="425" y="200" text-anchor="middle" fill="#fff" font-size="11">
(channel fanout)
</text>


<rect x="360" y="270" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="425" y="300" text-anchor="middle" fill="#fff" font-size="13">
Push Service
</text>


<rect x="560" y="60" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="630" y="85" text-anchor="middle" fill="#fff" font-size="13">
ScyllaDB
</text>
<text x="630" y="102" text-anchor="middle" fill="#fff" font-size="11">
(messages)
</text>


<rect x="560" y="160" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="630" y="185" text-anchor="middle" fill="#fff" font-size="13">
Gateway Cluster
</text>
<text x="630" y="202" text-anchor="middle" fill="#fff" font-size="11">
(recipient sessions)
</text>


<rect x="560" y="270" width="140" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="630" y="300" text-anchor="middle" fill="#fff" font-size="13">
Elasticsearch
</text>
<text x="630" y="315" text-anchor="middle" fill="#fff" font-size="11">
(message search)
</text>


<rect x="760" y="160" width="120" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="820" y="190" text-anchor="middle" fill="#fff" font-size="13">
Recipients
</text>
<text x="820" y="205" text-anchor="middle" fill="#fff" font-size="11">
WebSocket
</text>


<line x1="130" y1="190" x2="178" y2="190" stroke="#5865F2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="310" y1="175" x2="358" y2="95" stroke="#5865F2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="310" y1="190" x2="358" y2="190" stroke="#5865F2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="90" x2="558" y2="90" stroke="#5865F2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="190" x2="558" y2="190" stroke="#5865F2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="90" x2="558" y2="290" stroke="#5865F2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="190" x2="558" y2="300" stroke="#5865F2" stroke-width="2" marker-end="url(#a1)" stroke-dasharray="5,5">
</line>


<line x1="700" y1="190" x2="758" y2="190" stroke="#5865F2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="425" y1="222" x2="425" y2="268" stroke="#5865F2" stroke-width="2" marker-end="url(#a1)">
</line>


</svg>

</details>


### Flow Steps


1. **Sender** sends message over persistent WebSocket: `{"op": 0, "d": {"channel_id": "123", "content": "Hello!", "nonce": "abc"}}`

2. **Gateway** authenticates session (JWT token validated at connect time), forwards to Message Service via gRPC

3. **Message Service** validates permissions (can user post in this channel?), applies rate limits (5 msgs/5 sec), generates snowflake ID, persists to ScyllaDB

4. **Async:** Message indexed in Elasticsearch for search. Embeds resolved (URL previews, image dimensions). Mentions parsed (@user, @role, @everyone)

5. **Pub/Sub fanout:** Message published to channel's topic. All Gateway servers subscribed to this channel receive the message

6. **Gateway servers** push message via WebSocket to all online members who have this channel "subscribed" (visible in current guild or DM)

7. **Push Service:** For offline/mobile users with notifications enabled → APNs/FCM push notification

8. **Client ACK:** Sender receives ACK with assigned message ID and timestamp, replaces optimistic local message


### Example


**Scenario:** User sends "gg wp" in #general channel of a 50,000-member gaming server


**Write:** Message persisted in ScyllaDB partition `(channel_id=general_123)`, row key: snowflake_id `1234567890123456789`


**Fanout:** But NOT to all 50,000 members! Only to users who are **currently viewing this guild** (~2,000 online with guild open). Others get unread badge count increment.


**Gateway distribution:** 2,000 online viewers spread across ~100 Gateway servers. Pub/sub delivers to all 100 servers. Each server pushes to its local WebSocket connections.


**Latency:** Send → persist (5ms) → pub/sub (10ms) → gateway push (5ms) = ~20ms for same-region delivery


**@everyone mention:** Would trigger push notifications for ALL 50,000 members (if enabled). Rate limited: @everyone restricted to users with permission + 10-minute cooldown.


### Deep Dives


WebSocket Gateway Architecture


Discord's Gateway manages millions of concurrent WebSocket connections. Each Gateway server handles ~100K-1M connections. Protocol: custom binary (ETF — Erlang Term Format) or JSON over WebSocket with zlib compression. **Session management:** Each connection is a "session" with a session_id. On reconnect, clients send `RESUME` with session_id + last sequence number to recover missed events. Gateway servers are stateless — session state stored in Redis. **Sharding:** Large bots receive events across multiple Gateway shards (shard_id = guild_id % num_shards). **Heartbeat:** Client sends heartbeat every 41.25 seconds; server responds with ACK. Missed heartbeats → zombie connection detected and cleaned up.


Snowflake IDs


Discord uses Twitter's Snowflake format for all entity IDs: 64-bit integer = `timestamp(42 bits) + worker_id(5 bits) + process_id(5 bits) + sequence(12 bits)`. Epoch: Discord's custom epoch (2015-01-01). Benefits: IDs are **time-sortable** (newer messages have higher IDs), **globally unique** without coordination, **compact** (fits in a bigint). This enables range queries: "fetch messages before ID X" is a simple `WHERE id < X` query, which maps perfectly to ScyllaDB's clustering key ordering.


Pub/Sub for Channel Fanout


When a message is sent to a channel, it must reach all online viewers. Discord uses a pub/sub layer where each Gateway server subscribes to channels that its connected users are viewing. Implementation: **guild-level subscriptions** — when a user opens a guild, their Gateway subscribes to that guild's event stream. Events are filtered client-side (only render events for the active channel). This reduces subscription granularity from per-channel to per-guild. For massive servers (100K+ online), Discord uses a dedicated "Guild Pub/Sub" tier with fan-out workers.


Lazy Guilds & Member List


For large servers (1M members), Discord doesn't send the full member list. **Lazy guilds:** Client only receives member data for users visible in the current channel's member sidebar. As user scrolls, more members are lazy-loaded. This reduces the READY payload from potentially 1M members to ~200. Similarly, message history is lazy-loaded: only the last 50 messages fetched initially, with infinite scroll loading older messages via REST API (not WebSocket).


## 4. Flow 2 — Voice Channel (Real-Time Audio)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 380" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#57F287">
</polygon>
</marker>
</defs>


<rect x="20" y="150" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="180" text-anchor="middle" fill="#fff" font-size="13">
User Client
</text>
<text x="75" y="195" text-anchor="middle" fill="#fff" font-size="11">
(Opus encoder)
</text>


<rect x="180" y="50" width="130" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="245" y="80" text-anchor="middle" fill="#fff" font-size="13">
Gateway
</text>
<text x="245" y="95" text-anchor="middle" fill="#fff" font-size="11">
(signaling WS)
</text>


<rect x="180" y="150" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="245" y="180" text-anchor="middle" fill="#fff" font-size="13">
Voice Gateway
</text>
<text x="245" y="195" text-anchor="middle" fill="#fff" font-size="11">
(voice signaling)
</text>


<rect x="380" y="150" width="150" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="455" y="175" text-anchor="middle" fill="#fff" font-size="13">
SFU (Media Server)
</text>
<text x="455" y="192" text-anchor="middle" fill="#fff" font-size="11">
selective forwarding
</text>


<rect x="380" y="270" width="150" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="455" y="295" text-anchor="middle" fill="#fff" font-size="13">
Voice Region
</text>
<text x="455" y="312" text-anchor="middle" fill="#fff" font-size="11">
us-west, eu-west, etc.
</text>


<rect x="600" y="80" width="130" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="665" y="110" text-anchor="middle" fill="#fff" font-size="13">
Recipient A
</text>


<rect x="600" y="160" width="130" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="665" y="190" text-anchor="middle" fill="#fff" font-size="13">
Recipient B
</text>


<rect x="600" y="240" width="130" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="665" y="270" text-anchor="middle" fill="#fff" font-size="13">
Recipient C
</text>


<rect x="800" y="150" width="130" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="865" y="175" text-anchor="middle" fill="#fff" font-size="13">
TURN Server
</text>
<text x="865" y="192" text-anchor="middle" fill="#fff" font-size="11">
(NAT traversal)
</text>


<line x1="130" y1="170" x2="178" y2="80" stroke="#57F287" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="130" y1="180" x2="178" y2="180" stroke="#57F287" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="310" y1="180" x2="378" y2="180" stroke="#57F287" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="530" y1="165" x2="598" y2="110" stroke="#57F287" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="530" y1="180" x2="598" y2="190" stroke="#57F287" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="530" y1="195" x2="598" y2="265" stroke="#57F287" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="455" y1="212" x2="455" y2="268" stroke="#57F287" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="730" y1="190" x2="798" y2="180" stroke="#57F287" stroke-width="2" marker-end="url(#a2)" stroke-dasharray="5,5">
</line>


</svg>

</details>


### Flow Steps


1. **User joins voice channel** → Gateway sends VOICE_STATE_UPDATE to all guild members; provides voice server endpoint

2. **Voice Gateway** WebSocket established for signaling (session description, SSRC assignment, speaking state)

3. **UDP connection** established between client and SFU (Selective Forwarding Unit) media server — IP discovery via STUN

4. **Client captures audio** → Opus encoding (48kHz, 64kbps) → encrypted with XSalsa20-Poly1305 → sent as RTP over UDP to SFU

5. **SFU (Selective Forwarding Unit)** receives audio from all participants; forwards each participant's audio stream to all others WITHOUT mixing (preserves individual streams)

6. **Client-side mixing:** Each recipient decodes and mixes audio streams locally. Client can individually adjust volume per user

7. **NAT traversal:** If direct UDP fails, fall back to TURN relay servers

8. **Voice Activity Detection (VAD):** Client detects speech → sends SPEAKING event → gateway notifies other clients (green ring indicator)


### Example


**Scenario:** 25-person voice channel in gaming server during raid night


**Connection:** All 25 clients establish UDP connections to SFU in us-west region (~20ms RTT)


**Audio flow:** Each speaking user sends 1 Opus stream (64kbps). SFU forwards to 24 others. With 5 people speaking simultaneously: each recipient receives 5 streams, mixes locally.


**Bandwidth:** SFU ingress: 5 × 64kbps = 320kbps. SFU egress: 5 streams × 24 recipients = 120 streams × 64kbps = 7.68 Mbps. Client download: 5 × 64kbps = 320kbps per user.


**Latency:** Capture (5ms) + Opus encode (20ms frame) + network (20ms) + decode (5ms) = ~50ms glass-to-glass audio latency


**Screen share (Go Live):** H.264 encoded at 720p30/1080p60 (Nitro), sent as separate video RTP stream to SFU. SFU forwards to viewers (up to 50 for Go Live).


### Deep Dives


SFU vs MCU vs Mesh


**SFU (Selective Forwarding Unit)** — Discord's choice. Server receives all streams, selectively forwards without decoding/mixing. Pros: low server CPU (no transcoding), per-user volume control, low latency. Cons: higher client-side bandwidth (receives N streams). **MCU (Multipoint Control Unit)** — Server mixes all audio into single stream. Lower client bandwidth but higher server CPU and added latency. Used by traditional telephony. **Mesh (P2P)** — Each client sends to all others directly. Only works for <5 participants (N² connections). Discord uses SFU for voice channels, but P2P for 1:1 calls when possible.


Opus Codec & RTP


**Opus:** Open codec supporting 6kbps to 510kbps. Discord uses 64kbps for voice channels (great quality for voice). Frame size: 20ms. Supports Opus DTX (discontinuous transmission) — during silence, only send comfort noise packets, reducing bandwidth. **RTP (Real-time Transport Protocol):** UDP-based, with sequence numbers for reordering and timestamps for jitter buffer. Discord adds custom encryption: `XSalsa20-Poly1305` (libsodium) wrapping each RTP packet. No SRTP/DTLS (simpler custom protocol). Jitter buffer: adaptive 20-200ms based on network conditions.


## 5. Flow 3 — Server (Guild) Management & Permissions


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 300" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#FEE75C">
</polygon>
</marker>
</defs>


<rect x="20" y="120" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="150" text-anchor="middle" fill="#fff" font-size="13">
Admin
</text>


<rect x="180" y="120" width="120" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="240" y="150" text-anchor="middle" fill="#fff" font-size="13">
API Server
</text>


<rect x="350" y="30" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="415" y="60" text-anchor="middle" fill="#fff" font-size="13">
Guild Service
</text>


<rect x="350" y="120" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="415" y="150" text-anchor="middle" fill="#fff" font-size="13">
Permission
</text>
<text x="415" y="165" text-anchor="middle" fill="#fff" font-size="13">
Engine
</text>


<rect x="350" y="210" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="415" y="240" text-anchor="middle" fill="#fff" font-size="13">
Role Service
</text>


<rect x="540" y="30" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="610" y="60" text-anchor="middle" fill="#fff" font-size="13">
PostgreSQL
</text>
<text x="610" y="75" text-anchor="middle" fill="#fff" font-size="11">
(guilds, channels)
</text>


<rect x="540" y="120" width="140" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="610" y="150" text-anchor="middle" fill="#fff" font-size="13">
Permission Cache
</text>
<text x="610" y="165" text-anchor="middle" fill="#fff" font-size="11">
(computed perms)
</text>


<rect x="540" y="210" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="610" y="240" text-anchor="middle" fill="#fff" font-size="13">
ScyllaDB
</text>
<text x="610" y="255" text-anchor="middle" fill="#fff" font-size="11">
(member roles)
</text>


<line x1="130" y1="150" x2="178" y2="150" stroke="#FEE75C" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="300" y1="135" x2="348" y2="65" stroke="#FEE75C" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="300" y1="150" x2="348" y2="150" stroke="#FEE75C" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="300" y1="165" x2="348" y2="235" stroke="#FEE75C" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="480" y1="60" x2="538" y2="60" stroke="#FEE75C" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="480" y1="150" x2="538" y2="150" stroke="#FEE75C" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="480" y1="240" x2="538" y2="240" stroke="#FEE75C" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


### Flow Steps


1. **Admin creates channel** → POST `/guilds/{guildId}/channels` with name, type (text/voice/stage), category, permission overwrites

2. **Guild Service** persists channel in PostgreSQL, assigns position in category ordering

3. **Permission Engine** computes effective permissions: base @everyone role permissions → union of all user's role permissions → apply channel-specific overwrites (allow/deny per role per channel)

4. **Permission calculation:** Bitfield operations. Each permission is a bit in a 64-bit integer. `effective = (base | role_allows) & ~role_denies | channel_allows) & ~channel_denies`

5. **Cached computed permissions** stored for fast checks on every message send, channel view, voice join

6. **GUILD_UPDATE event** pushed via WebSocket to all online members → clients update channel list


### Example


**Scenario:** Admin creates #officers-only channel visible only to @Officer role


**Config:** Channel overwrites: @everyone → DENY VIEW_CHANNEL; @Officer → ALLOW VIEW_CHANNEL, SEND_MESSAGES


**Permission bits:** VIEW_CHANNEL = bit 10, SEND_MESSAGES = bit 11


**For regular member:** base = 0b...100 (VIEW_CHANNEL from @everyone) → channel deny = 0b...100 (deny bit 10) → effective bit 10 = 0 → CANNOT view channel


**For officer:** role grants bit 10,11 → channel allow bits 10,11 → effective: can view and send → channel appears in their list


### Deep Dives


Bitfield Permission System


Discord's permission system uses 53-bit integers (JavaScript safe integer limit). Each permission type is a bit: `CREATE_INVITE=0x1, KICK_MEMBERS=0x2, BAN_MEMBERS=0x4, ADMINISTRATOR=0x8, MANAGE_CHANNELS=0x10, ...` up to ~40 permissions. Permission computation is pure bitwise operations — extremely fast (nanoseconds). Role hierarchy: higher position roles override lower. ADMINISTRATOR bit (0x8) bypasses ALL permission checks. Channel overwrites: per-role allow/deny overrides stored per channel. Computed once and cached; invalidated on role/overwrite change.


Guild Sharding for Large Servers


Servers with 100K+ members require special handling. Discord's **"big guild"** optimization: lazy member loading (only send visible members), compressed presence updates (batch presence changes every 5 seconds instead of real-time), rate-limited @everyone mentions. The largest servers (1M+ members) have dedicated infrastructure. Guild data is sharded: member list in ScyllaDB (sharded by guild_id), messages in ScyllaDB (sharded by channel_id), metadata in PostgreSQL (sharded by guild_id).


## 6. Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 550" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#5865F2">
</polygon>
</marker>
</defs>


<rect x="10" y="240" width="100" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="60" y="270" text-anchor="middle" fill="#fff" font-size="12">
Clients
</text>
<text x="60" y="285" text-anchor="middle" fill="#fff" font-size="10">
200M+ MAU
</text>


<rect x="150" y="180" width="110" height="50" rx="8" fill="#546e7a" stroke="#90a4ae" stroke-width="2">
</rect>
<text x="205" y="210" text-anchor="middle" fill="#fff" font-size="12">
CDN (CloudFlare)
</text>


<rect x="150" y="245" width="110" height="50" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="205" y="275" text-anchor="middle" fill="#fff" font-size="12">
Gateway (WS)
</text>


<rect x="150" y="310" width="110" height="50" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="205" y="340" text-anchor="middle" fill="#fff" font-size="12">
REST API
</text>


<rect x="310" y="60" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="88" text-anchor="middle" fill="#fff" font-size="11">
Message Service
</text>


<rect x="310" y="120" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="148" text-anchor="middle" fill="#fff" font-size="11">
Guild Service
</text>


<rect x="310" y="180" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="208" text-anchor="middle" fill="#fff" font-size="11">
User Service
</text>


<rect x="310" y="240" width="120" height="45" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="370" y="260" text-anchor="middle" fill="#fff" font-size="11">
Voice Gateway
</text>
<text x="370" y="275" text-anchor="middle" fill="#fff" font-size="10">
(signaling)
</text>


<rect x="310" y="300" width="120" height="45" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="370" y="320" text-anchor="middle" fill="#fff" font-size="11">
SFU (Media)
</text>
<text x="370" y="335" text-anchor="middle" fill="#fff" font-size="10">
UDP voice/video
</text>


<rect x="310" y="360" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="388" text-anchor="middle" fill="#fff" font-size="11">
Presence Service
</text>


<rect x="310" y="420" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="448" text-anchor="middle" fill="#fff" font-size="11">
Push Service
</text>


<rect x="490" y="100" width="120" height="45" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="550" y="128" text-anchor="middle" fill="#fff" font-size="11">
Pub/Sub Layer
</text>


<rect x="670" y="60" width="120" height="45" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="730" y="88" text-anchor="middle" fill="#fff" font-size="11">
ScyllaDB
</text>


<rect x="670" y="120" width="120" height="45" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="730" y="148" text-anchor="middle" fill="#fff" font-size="11">
PostgreSQL
</text>


<rect x="670" y="180" width="120" height="45" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="730" y="208" text-anchor="middle" fill="#fff" font-size="11">
Redis
</text>


<rect x="670" y="240" width="120" height="45" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="730" y="268" text-anchor="middle" fill="#fff" font-size="11">
Elasticsearch
</text>


<rect x="670" y="300" width="120" height="45" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="730" y="328" text-anchor="middle" fill="#fff" font-size="11">
GCS (media)
</text>


<line x1="110" y1="260" x2="148" y2="205" stroke="#5865F2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="110" y1="270" x2="148" y2="270" stroke="#5865F2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="110" y1="280" x2="148" y2="335" stroke="#5865F2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="260" y1="270" x2="308" y2="85" stroke="#5865F2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="85" x2="488" y2="120" stroke="#5865F2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="610" y1="120" x2="668" y2="85" stroke="#5865F2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="145" x2="668" y2="140" stroke="#5865F2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="200" x2="668" y2="200" stroke="#5865F2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="85" x2="668" y2="80" stroke="#5865F2" stroke-width="2" marker-end="url(#a4)">
</line>


</svg>

</details>


## 7. Database Schema


### Messages (ScyllaDB — partitioned by channel_id)


Table: messages   `SHARD`
- channel_id: bigint            `PK`
- message_id: bigint (snowflake)  `PK`
- author_id: bigint             `FK`
- content: text
- embeds: json
- attachments: json            // [{url, filename, size, content_type}]
- mention_ids: set<bigint>
- mention_roles: set<bigint>
- mention_everyone: boolean
- reactions: map<text, set<bigint>>  // emoji → set of user_ids
- message_type: tinyint        // 0=default, 1=recipient_add, 7=reply, 19=thread_created
- referenced_message_id: bigint // for replies/threads
- flags: int                   // suppressed_embeds, crossposted, etc.
- edited_at: timestamp
- created_at: timestamp

// Clustering by message_id DESC enables efficient "load latest 50 messages" query


### Guilds (PostgreSQL — sharded by guild_id)


Table: guilds   `SHARD`
- guild_id: bigint              `PK`
- name: varchar(100)
- owner_id: bigint              `FK`
- icon_hash: varchar(40)
- region: varchar(20)          // voice region
- verification_level: smallint
- max_members: int             // default 500K
- premium_tier: smallint       // Nitro boost level (0-3)
- features: text[]             // ['COMMUNITY', 'ANIMATED_ICON', ...]
- created_at: timestamp


### Members (ScyllaDB — partitioned by guild_id)


Table: guild_members   `SHARD`
- guild_id: bigint              `PK`
- user_id: bigint               `PK`
- nickname: varchar(32)
- role_ids: set<bigint>
- joined_at: timestamp
- premium_since: timestamp     // Nitro boosting since
- muted: boolean
- deafened: boolean


### Channels (PostgreSQL)


Table: channels
- channel_id: bigint            `PK`
- guild_id: bigint              `FK`  `IDX`
- type: smallint               // 0=text, 2=voice, 4=category, 5=news, 13=stage
- name: varchar(100)
- topic: varchar(1024)
- position: smallint
- parent_id: bigint             `FK`
- rate_limit_per_user: int     // slowmode seconds
- nsfw: boolean
- permission_overwrites: jsonb // [{role_id, allow_bits, deny_bits}]


### Presence (Redis — ephemeral)


Key: presence:{user_id}
Value: {
  status: "online" | "idle" | "dnd" | "offline",
  activities: [{name: "Playing Valorant", type: 0, timestamps: {...}}],
  client_status: {desktop: "online", mobile: "idle", web: "offline"}
}
TTL: 90 seconds (refreshed by heartbeat)


## 8. Cache & CDN Deep Dive


### Caching Layers


| Layer            | Technology              | What's Cached                                        | TTL                                | Notes                                 |
|------------------|-------------------------|------------------------------------------------------|------------------------------------|---------------------------------------|
| CDN              | Cloudflare              | Attachments, avatars, emojis, static assets          | 1yr (content-addressed)            | ~10TB/day served from edge            |
| API Cache        | Redis Cluster           | Guild metadata, channel lists, permission overwrites | Minutes, event-driven invalidation | Reduces PostgreSQL load               |
| Session Store    | Redis                   | WebSocket session state (for RESUME)                 | 5 minutes after disconnect         | Enables seamless reconnection         |
| Presence         | Redis                   | User online status, activities                       | 90s TTL, heartbeat refresh         | Millions of concurrent entries        |
| Permission Cache | In-memory (per service) | Computed permission bitfields                        | Until role/overwrite change        | Event-driven invalidation via pub/sub |
| Message Cache    | Client-side             | Recent messages per channel                          | Session                            | Reduces REST API calls                |


### Media CDN Pipeline


When a user uploads an attachment: file uploaded to Discord's API → stored in Google Cloud Storage (GCS) → URL generated with CDN prefix (`cdn.discordapp.com`). Images are proxy-resized on-the-fly via query params (`?width=400&height=300`). Discord uses Cloudflare as their CDN provider with aggressive caching (content-addressed: same file hash = same URL = infinite cache). Embeds (link previews) are generated server-side: fetch Open Graph tags from URL, cache the embed data. User avatars stored as `{user_id}/{avatar_hash}.webp` — hash changes on update, naturally invalidating CDN cache.


## 9. Scaling Considerations


### ScyllaDB Migration


Discord migrated from Cassandra to ScyllaDB in 2023 for messages. Reason: Cassandra's JVM-based GC pauses caused tail latency spikes (p99 up to 200ms). ScyllaDB (C++ rewrite of Cassandra) eliminated GC pauses → p99 dropped to <10ms. Same data model (wide-column, CQL compatible). Discord stores **trillions of messages** across the cluster. Partition key: channel_id. Clustering key: message_id DESC. This enables efficient "fetch latest N messages" queries.


### Gateway Scaling


- **Millions of concurrent WebSocket connections** across hundreds of Gateway servers

- Each Gateway server: ~1M connections (Elixir/Erlang BEAM VM excels at lightweight processes per connection)

- **Bot sharding:** Large bots (in 100K+ guilds) receive events across multiple shards. Discord enforces shard count based on guild count

- **Horizontal scaling:** New Gateway servers auto-registered; clients re-balance on reconnect


### Load Balancing


- **DNS:** GeoDNS for region routing (us-east, eu-west, singapore, etc.)

- **L4:** Google Cloud L4 LB for TCP/UDP distribution

- **L7:** Cloudflare for HTTP API traffic + DDoS protection

- **Voice region:** Users can select voice region; SFU servers deployed per region for lowest latency


## 10. Tradeoffs


> **

> **
✅ SFU for Voice


Low server CPU (no transcoding), per-user volume control, supports simultaneous screen share + audio. Scales to 5000 users in a voice channel. Sub-50ms latency for audio.


> **
❌ SFU Bandwidth


Each participant receives N-1 audio streams instead of 1 mixed stream. For 25-person call: 24 streams × 64kbps = 1.5Mbps download per user. For video, bandwidth cost is even higher. Mitigated by voice activity detection (only forward active speakers).


> **
✅ ScyllaDB for Messages


Eliminated JVM GC pauses that plagued Cassandra. p99 latency from 200ms → <10ms. Same CQL API = minimal migration effort. Time-series-like access pattern (latest messages) maps perfectly to clustering key ordering.


> **
❌ ScyllaDB Operational Complexity


Smaller community than Cassandra. Fewer managed service options. Compaction tuning critical for write-heavy workloads. Hot partitions (extremely active channels) require careful monitoring and possible splitting.


> **
✅ Elixir/Erlang for Gateway


BEAM VM handles millions of lightweight processes (one per WebSocket connection). Built-in fault tolerance (let-it-crash). Hot code upgrades possible. Discord's Gateway handles 1M+ connections per server thanks to BEAM's concurrency model.


> **
❌ Elixir Ecosystem


Smaller hiring pool than Go/Java/Python. Fewer third-party libraries. Performance ceiling for CPU-intensive work (message processing offloaded to Rust services). Discord has increasingly moved hot paths to Rust while keeping Elixir for connection management.


## 11. Alternative Approaches


Matrix Protocol (Decentralized)


Open-standard, federated communication protocol. Any server can host and federate messages. Used by Element. Provides E2E encryption by default. Tradeoffs: higher latency due to federation, complex conflict resolution, harder to enforce content moderation across federated servers.


WebRTC Mesh for Voice


Direct peer-to-peer connections between all participants (no server). Lower latency for small groups (<5 users). Zero server media cost. Discord uses this for 1:1 calls. Doesn't scale beyond ~5 users (N² connections). No server-side recording capability.


NATS/Redis Streams for Pub/Sub


Instead of custom pub/sub, use NATS JetStream or Redis Streams. Lower operational overhead. NATS: ~10M msg/sec per server, lightweight. Redis Streams: consumer groups, persistence. Discord's custom pub/sub is optimized for their specific guild-scoped subscription patterns.


CockroachDB Instead of PostgreSQL


Distributed SQL with automatic sharding. Would eliminate Discord's manual PostgreSQL sharding. Supports serializable isolation. Tradeoffs: higher write latency (Raft consensus), less mature ecosystem, higher operational cost. Discord's PostgreSQL sharding (4096 shards) works well for their access patterns.


## 12. Additional Information


### Discord's Tech Stack


- **Elixir/Erlang:** Gateway (WebSocket management) — BEAM VM for massive concurrency

- **Rust:** Performance-critical services (message fanout, presence, read states) — moved from Go for lower latency

- **Python:** API services, ML/safety (originally Flask, now FastAPI)

- **React:** Web client (desktop app via Electron)

- **React Native:** Mobile clients (iOS/Android)

- **C++:** Voice/video engine, native module for desktop


### Key Numbers


| Monthly Active Users          | 200M+               |
|-------------------------------|---------------------|
| Active servers (guilds)       | 19M+                |
| Messages per day              | 4B+                 |
| Concurrent voice users (peak) | 15M+                |
| WebSocket connections (peak)  | Millions concurrent |
| ScyllaDB messages stored      | Trillions           |
| Average voice latency         | ~50ms               |


### Protocols Summary


- **Client → Gateway:** WebSocket (wss://) with zlib compression, ETF or JSON encoding, heartbeat every 41.25s

- **Client → API:** HTTPS REST (JSON) for non-real-time operations (history fetch, profile update, guild management)

- **Client → Voice:** WebSocket for signaling + UDP (RTP + XSalsa20-Poly1305) for media

- **Service → Service:** gRPC (Protobuf) for internal RPC

- **Pub/Sub:** Custom pub/sub layer for guild-scoped event distribution

- **Voice Codec:** Opus (48kHz, 64kbps) with DTX for silence suppression

- **Video Codec:** H.264 (720p30 free, 1080p60 Nitro) via SFU forwarding