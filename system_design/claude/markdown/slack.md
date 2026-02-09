# 💬 Slack - System Design


## Functional Requirements


1. **Real-Time Messaging:** Channels (public/private), DMs, threads, reactions, file sharing, mentions (@user/@channel/@here), message editing/deletion, rich formatting (mrkdwn), link unfurling

2. **Search:** Full-text search across all messages, files, and channels with filters (from:, in:, before:, has:link, has:emoji) — results within seconds across years of history

3. **Integration Platform:** Slack Apps (Bolt SDK), slash commands, webhooks, interactive messages (buttons/modals), Workflow Builder, 2,600+ app directory integrations


## Non-Functional Requirements


- **Scale:** 200K+ paying customers, 65M+ daily active users, 2.6B+ messages/day, 1.5B+ API calls/day

- **Latency:** Message delivery <500ms, search <1s, file upload <3s for typical files

- **Availability:** 99.99% (4 nines); real-time messaging cannot tolerate extended downtime

- **Consistency:** Message ordering must be consistent within a channel — causal ordering with Lamport-like timestamps

- **Multi-Tenancy:** Complete workspace isolation; Enterprise Grid: multiple workspaces under single org with shared channels


## Flow 1: Real-Time Messaging


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#36C5F0">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Client
</text>
<text x="80" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Desktop/Web/Mobile)
</text>


<rect x="190" y="50" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="255" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Edge Gateway
</text>
<text x="255" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Envoy / HAProxy)
</text>


<rect x="370" y="50" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="435" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Message Server
</text>
<text x="435" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Channel Router)
</text>


<rect x="550" y="20" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="615" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
WebSocket
</text>
<text x="615" y="57" text-anchor="middle" fill="#fff" font-size="10">
Gateway (Flannel)
</text>


<rect x="550" y="90" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="615" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Message Store
</text>
<text x="615" y="127" text-anchor="middle" fill="#fff" font-size="10">
(MySQL Vitess)
</text>


<rect x="740" y="20" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="805" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Push Service
</text>
<text x="805" y="57" text-anchor="middle" fill="#fff" font-size="10">
(APNs/FCM)
</text>


<rect x="740" y="90" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="805" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Search Index
</text>
<text x="805" y="127" text-anchor="middle" fill="#fff" font-size="10">
(Solr → ES)
</text>


<rect x="370" y="200" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="222" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Job Queue
</text>
<text x="435" y="237" text-anchor="middle" fill="#fff" font-size="10">
(Kafka)
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="320" y1="75" x2="365" y2="75" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="65" x2="545" y2="45" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="85" x2="545" y2="110" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="680" y1="45" x2="735" y2="45" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="680" y1="115" x2="735" y2="115" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="435" y1="100" x2="435" y2="195" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah)">
</line>


</svg>

</details>


### Steps


1. Client sends message via HTTPS POST to Edge Gateway (Envoy-based with TLS termination)

2. Message Server validates auth (OAuth2 token), checks channel membership, enforces rate limits (1 msg/sec per user)

3. Message assigned monotonic channel-scoped timestamp (ts) — format: `1707500000.000100` (Unix epoch + micro counter)

4. Persisted to MySQL (Vitess sharded) — primary source of truth for all messages

5. WebSocket Gateway (Flannel) fans out message to all online channel members via persistent WebSocket connections

6. Offline users: push notification queued via Kafka → delivered via APNs/FCM with badge count update

7. Message indexed asynchronously in Solr/Elasticsearch for full-text search

8. Kafka job queue triggers: link unfurling, @mention notifications, bot/webhook deliveries, analytics events


### Example


User posts in #engineering (500 members, 45 online). Message Server assigns ts=1707500000.000100, stores in MySQL shard for workspace_123. Flannel pushes to 45 WebSocket connections. 455 offline users: push notifications batched (channel notification setting: "mentions only" → only @mentioned users get push). Kafka fires: unfurl the GitHub PR link, notify @alice (mentioned), deliver to PagerDuty bot webhook.


### Deep Dives


WebSocket Gateway (Flannel)


**Architecture:** Slack's custom WebSocket layer — stateful, long-lived connections; each server handles ~100K connections


**Protocol:** WebSocket with JSON-framed messages; client maintains single connection for all channels


**Subscription:** Server maintains in-memory map: channel_id → [connection_ids]; broadcast = iterate subscribers


**Reconnect:** Client reconnects with last_event_id → server replays missed events from event log (Redis Streams)


Message Timestamp (ts)


**Format:** `{epoch}.{counter}` — e.g., `1707500000.000100`


**Uniqueness:** Globally unique within a channel — serves as both timestamp AND message ID


**Ordering:** String-sortable — lexicographic sort = chronological order


**Counter:** Per-channel monotonic counter prevents collisions for messages in same second


MySQL + Vitess (Sharding)


**Vitess:** MySQL sharding middleware developed at YouTube; provides horizontal sharding transparent to application


**Shard Key:** workspace_id — all data for a workspace on same shard (channels, messages, members)


**Scale:** Thousands of MySQL shards; Vitess handles routing, connection pooling, schema migration


**Migration:** Slack migrated from single MySQL to Vitess to handle growth; later exploring CockroachDB for some workloads


Link Unfurling


**Process:** Kafka consumer extracts URLs from message → HTTP HEAD/GET to fetch Open Graph metadata (og:title, og:image, og:description)


**Cache:** Unfurl results cached in Redis by URL (TTL 24h) — same URL shared across messages


**Special:** First-party unfurls for known services (Google Docs, GitHub, Jira) fetch richer metadata via API integration


## Flow 2: Message Search


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 260" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#2EB67D">
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
Search Service
</text>
<text x="255" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Query Parser)
</text>


<rect x="370" y="30" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="435" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Search Index
</text>
<text x="435" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Elasticsearch)
</text>


<rect x="370" y="100" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
ACL Filter
</text>
<text x="435" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Channel Perms)
</text>


<rect x="560" y="60" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="625" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
MySQL (Vitess)
</text>
<text x="625" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Message Hydration)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#2EB67D" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#2EB67D" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="95" x2="365" y2="120" stroke="#2EB67D" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="500" y1="75" x2="555" y2="80" stroke="#2EB67D" stroke-width="2" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Steps


1. User types search query with optional operators: `from:@alice in:#engineering has:link before:2024-02-01 deploy`

2. Search Service parses query into structured filters + free-text terms

3. Elasticsearch queried within workspace scope — BM25 ranking with recency boost

4. ACL Filter removes results from channels user doesn't have access to (post-filter on ES results)

5. Matching message IDs hydrated from MySQL to get full message content, user profiles, channel names

6. Results returned with highlighted snippets and pagination


### Deep Dives


Search Architecture


**Migration:** Slack migrated from Solr to Elasticsearch (2017-2019) for better scaling and operational tooling


**Index:** Per-workspace Elasticsearch index; large workspaces split across multiple shards


**Fields:** message_text (analyzed), sender_id, channel_id, timestamp, has_attachment, has_link, reactions


**Near-Real-Time:** New messages searchable within ~5 seconds of posting (ES refresh interval)


ACL Enforcement


**Problem:** User shouldn't see messages from private channels they're not in


**Solution:** Post-filter — ES returns top-N candidates, ACL filter removes unauthorized, fetch more if needed


**Optimization:** For most users, majority of channels are accessible → filter removes few results


## Flow 3: Slack Apps & Integrations


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 260" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#ECB22E">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
User Action
</text>
<text x="80" y="97" text-anchor="middle" fill="#fff" font-size="10">
(/command, button)
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Events API
</text>
<text x="255" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Dispatch)
</text>


<rect x="370" y="60" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="435" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
App Server
</text>
<text x="435" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Developer's)
</text>


<rect x="560" y="30" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="625" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Web API
</text>
<text x="625" y="67" text-anchor="middle" fill="#fff" font-size="10">
(chat.postMessage)
</text>


<rect x="560" y="100" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="625" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Block Kit
</text>
<text x="625" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Rich UI Blocks)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#ECB22E" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="85" x2="365" y2="85" stroke="#ECB22E" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="75" x2="555" y2="55" stroke="#ECB22E" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="95" x2="555" y2="120" stroke="#ECB22E" stroke-width="2" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Steps


1. User interaction (slash command, button click, message shortcut) triggers event in Slack

2. Events API dispatches HTTP POST webhook to registered app server URL with event payload (signed with HMAC)

3. App server processes event, calls Slack Web API methods (chat.postMessage, views.open, reactions.add)

4. Block Kit: JSON-based UI framework for rich interactive messages — sections, buttons, selects, date pickers, modals

5. Socket Mode (alternative): app connects via WebSocket to receive events instead of HTTP webhooks (no public URL needed)

6. Bolt SDK (Node.js, Python, Java) simplifies app development with middleware pattern for event routing


### Deep Dives


Events API


**Delivery:** HTTP POST to app's Request URL with JSON payload; 3-second response timeout (acknowledge immediately, process async)


**Retry:** If app returns non-200, Slack retries 3 times with exponential backoff (1min, 5min, 30min)


**Verification:** `x-slack-signature` header: HMAC-SHA256(signing_secret, timestamp + body) — prevents forgery


**Event Types:** message, reaction_added, member_joined_channel, app_mention, file_shared, etc.


Block Kit


**Format:** JSON array of block elements — section, divider, image, actions, input, context


**Interactive:** Buttons, overflow menus, date pickers, multi-select → generate interaction payloads sent to app


**Modals:** Full-screen forms via `views.open` — supports multi-step workflows with state management


**Builder:** Block Kit Builder (visual editor) at api.slack.com/tools/block-kit-builder


## Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 450" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#36C5F0">
</polygon>
</marker>
</defs>


<rect x="20" y="70" width="130" height="50" rx="8" fill="#34A853">
</rect>
<text x="85" y="92" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Clients
</text>
<text x="85" y="107" text-anchor="middle" fill="#fff" font-size="10">
(Electron/Web/Mobile)
</text>


<rect x="200" y="70" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="265" y="92" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Edge (Envoy)
</text>
<text x="265" y="107" text-anchor="middle" fill="#fff" font-size="10">
(TLS + Routing)
</text>


<rect x="390" y="20" width="130" height="45" rx="8" fill="#4285F4">
</rect>
<text x="455" y="47" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Message Server
</text>


<rect x="390" y="80" width="130" height="45" rx="8" fill="#4285F4">
</rect>
<text x="455" y="107" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Search Service
</text>


<rect x="390" y="140" width="130" height="45" rx="8" fill="#4285F4">
</rect>
<text x="455" y="167" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
File Service
</text>


<rect x="580" y="20" width="130" height="45" rx="8" fill="#607D8B">
</rect>
<text x="645" y="47" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Flannel (WS)
</text>


<rect x="580" y="80" width="130" height="45" rx="8" fill="#9C27B0">
</rect>
<text x="645" y="107" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Events API
</text>


<rect x="580" y="140" width="130" height="45" rx="8" fill="#E53935">
</rect>
<text x="645" y="167" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Push Service
</text>


<rect x="780" y="70" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="845" y="92" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Kafka
</text>
<text x="845" y="107" text-anchor="middle" fill="#fff" font-size="10">
(Event Bus)
</text>


<rect x="390" y="290" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="455" y="312" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
MySQL (Vitess)
</text>
<text x="455" y="329" text-anchor="middle" fill="#fff" font-size="10">
(Messages + Meta)
</text>


<rect x="580" y="290" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="645" y="312" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Elasticsearch
</text>
<text x="645" y="329" text-anchor="middle" fill="#fff" font-size="10">
(Search Index)
</text>


<rect x="780" y="290" width="130" height="55" rx="8" fill="#00897B">
</rect>
<text x="845" y="312" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Redis / Memcached
</text>
<text x="845" y="329" text-anchor="middle" fill="#fff" font-size="10">
(Cache)
</text>


<rect x="200" y="290" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="265" y="312" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
S3
</text>
<text x="265" y="329" text-anchor="middle" fill="#fff" font-size="10">
(Files + Uploads)
</text>


<line x1="150" y1="95" x2="195" y2="95" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="330" y1="85" x2="385" y2="42" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="330" y1="95" x2="385" y2="102" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="330" y1="105" x2="385" y2="162" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="520" y1="42" x2="575" y2="42" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="520" y1="102" x2="575" y2="102" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="520" y1="42" x2="575" y2="162" stroke="#36C5F0" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="710" y1="95" x2="775" y2="95" stroke="#36C5F0" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="455" y1="65" x2="455" y2="285" stroke="#36C5F0" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="520" y1="125" x2="580" y2="300" stroke="#36C5F0" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="710" y1="107" x2="780" y2="305" stroke="#36C5F0" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="390" y1="185" x2="265" y2="285" stroke="#36C5F0" stroke-width="1.5" marker-end="url(#ah4)">
</line>


</svg>

</details>


## Database Schema


### MySQL (Vitess) — Messages

`CREATE TABLE messages (
  workspace_id  BIGINT,            `SHARD`
  channel_id    VARCHAR(20),       `IDX`
  ts            VARCHAR(20),       `PK` -- "1707500000.000100"
  user_id       VARCHAR(20),       `FK`
  text          TEXT,
  type          VARCHAR(20) DEFAULT 'message',
  subtype       VARCHAR(30),      -- bot_message, channel_join, etc.
  thread_ts     VARCHAR(20),      -- parent message ts (for threads)
  reply_count   INT DEFAULT 0,
  reactions     JSON,             -- [{"name":"thumbsup","users":["U123"]}]
  attachments   JSON,             -- legacy attachments
  blocks        JSON,             -- Block Kit blocks
  files         JSON,             -- [{id, name, url_private, mimetype}]
  edited_ts     VARCHAR(20),
  is_deleted    BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (channel_id, ts)
) ENGINE=InnoDB;
CREATE INDEX idx_msg_thread ON messages(channel_id, thread_ts);
CREATE INDEX idx_msg_user ON messages(workspace_id, user_id, ts);`


 `SHARD` Vitess shards by workspace_id; channels + messages co-located per workspace


### MySQL — Channels & Members

`CREATE TABLE channels (
  workspace_id  BIGINT,            `SHARD`
  channel_id    VARCHAR(20),       `PK`
  name          VARCHAR(80),       `IDX`
  topic         TEXT,
  purpose       TEXT,
  is_private    BOOLEAN DEFAULT FALSE,
  is_archived   BOOLEAN DEFAULT FALSE,
  creator_id    VARCHAR(20),       `FK`
  member_count  INT DEFAULT 0,
  created_at    BIGINT
);

CREATE TABLE channel_members (
  workspace_id  BIGINT,
  channel_id    VARCHAR(20),       `FK`
  user_id       VARCHAR(20),       `FK`
  role          VARCHAR(20) DEFAULT 'member',
  notification_pref VARCHAR(20) DEFAULT 'default',
  last_read_ts  VARCHAR(20),      -- for unread tracking
  joined_at     BIGINT,
  PRIMARY KEY (channel_id, user_id)
);
CREATE INDEX idx_member_user ON channel_members(workspace_id, user_id);`


## Cache & CDN Deep Dive


Memcached (Hot Data)


**Cached:** Channel membership lists, user profiles, workspace settings, recent messages (last 50 per channel)


**Strategy:** Cache-aside with invalidation via Kafka events on write


**Scale:** ~6TB aggregate Memcached cluster; 99%+ hit rate for channel membership lookups


**TTL:** Channel membership: 10min; user profiles: 5min; settings: 1hr


Redis (Real-Time State)


**Presence:** Online/away/DND status per user — Redis SET with heartbeat TTL (60s)


**Typing:** Typing indicators stored in Redis with 5-second TTL — auto-expire when user stops typing


**Unread:** Per-user per-channel last_read_ts — compared against channel's latest message ts


**Event Log:** Redis Streams for WebSocket reconnection replay (last 1000 events per channel)


CDN (Cloudfront)


**Files:** Uploaded files stored in S3; served via CloudFront CDN with signed URLs (expiring tokens for access control)


**Static:** App bundles, emoji sprites, default avatars cached at edge


**Unfurl Cache:** Link preview images/metadata cached at CDN; 24hr TTL


## Scaling & Load Balancing


- **Edge:** Envoy-based edge proxies in multiple AWS regions; anycast routing to nearest edge; TLS 1.3 termination

- **Vitess:** Thousands of MySQL shards; Vitess vtgate handles query routing; online resharding for hot workspaces (e.g., Salesforce's 100K+ user workspace)

- **WebSocket (Flannel):** Horizontally scaled; consistent hashing by workspace_id → specific Flannel instances; connection migration on server restart

- **Kafka:** Central event bus — 100+ topics; partitioned by workspace_id; used for async processing (search indexing, push notifications, analytics, compliance)

- **Enterprise Grid:** Multi-workspace organizations; shared channels span workspaces via federation protocol (cross-shard message routing)


## Tradeoffs


> **

✅ MySQL + Vitess


- Proven reliability — MySQL is battle-tested for ACID transactions

- Rich query capabilities — JOINs, subqueries, complex predicates

- Vitess provides horizontal scaling without application changes


❌ MySQL + Vitess


- Cross-shard queries are expensive (scatter-gather through vtgate)

- Schema changes require careful online DDL management

- Write amplification from JSON columns (reactions, blocks, files)


✅ Channel-Based Model (vs. DM-First)


- Transparent communication — reduces information silos

- Searchable organizational knowledge base

- Easier onboarding — new members can read channel history


❌ Channel-Based Model


- Channel sprawl — large orgs have thousands of channels (discovery problem)

- Information overload — users in many channels face notification fatigue

- Context switching cost — managing multiple concurrent conversations


## Alternative Approaches


NoSQL (Cassandra/DynamoDB)


Wide-column store for messages — partition by channel_id, sort by timestamp. Infinite horizontal scale, no cross-partition queries needed for messaging. But loses JOIN capability, complex consistency model, and Slack's existing MySQL expertise would be wasted.


CRDT-Based (Linear Chat)


Conflict-free data types for offline-first messaging. Each client maintains local state, syncs via CRDT merge. Enables true offline mode and reduces server dependency. But adds complexity, larger message metadata, and challenges with message ordering guarantees.


## Additional Information


- **Slack Connect:** Cross-organization channels — two companies share a channel without guest accounts. Each org's data stays on their shard; messages routed cross-org via federation layer. Requires admin approval on both sides.

- **Huddles:** Lightweight audio (and optional video) calls within channels/DMs — WebRTC-based with Janus SFU. Always-on ambient audio for remote pair programming. Lower ceremony than scheduled meetings.

- **Canvas:** Rich document editor within Slack — collaborative notes with formatting, checklists, embedded messages. Backed by Quill.js editor with custom OT (operational transformation) for real-time co-editing.

- **Slack AI:** Search answers (summarize search results), channel recaps (catch up on missed messages), thread summaries. Powered by Anthropic Claude and OpenAI models; data stays within Slack's infrastructure (not used for training).

- **Rate Limiting:** Web API: tier-based — Tier 1: 1 req/sec, Tier 2: 20 req/min, Tier 3: 50 req/min, Tier 4: 100 req/min. Events API: 30K events/hour per app. Rate limit headers (X-RateLimit-Remaining) enable client-side throttling.