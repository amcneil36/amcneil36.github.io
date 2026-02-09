# 💼 Design LinkedIn


Professional networking platform with 1B+ members, social graph, feed, messaging, job search, and recruiter tools


## 1. Functional Requirements


- **Profile Management:** Create/edit professional profiles (experience, education, skills, endorsements, certifications)

- **Social Graph:** Connect with 1st/2nd/3rd-degree connections, follow influencers, manage pending invitations

- **News Feed:** Personalized feed of posts, articles, job changes, work anniversaries from network

- **Content Creation:** Post text, images, videos, articles, polls, documents; reactions and comments

- **Messaging:** 1:1 and group chat with read receipts; InMail for non-connections

- **Job Search:** Search/filter job listings, apply with profile, track applications, job alerts

- **Recruiter Tools:** Advanced people search with filters, InMail campaigns, candidate pipeline management

- **Notifications:** Connection requests, post interactions, job updates, profile views, mentions

- **Search:** People, companies, jobs, content search with faceted filters


## 2. Non-Functional Requirements


- **Scale:** 1B+ members, 310M+ monthly active users, 2B+ API calls/day

- **Latency:** Feed loading p50 <200ms, search p50 <150ms, messaging delivery <500ms

- **Availability:** 99.99% uptime for core features

- **Consistency:** Eventual consistency for feed; strong consistency for connections and messages

- **Data Privacy:** GDPR compliant, granular visibility settings (public, connections-only, private)

- **Graph Queries:** Efficient 2nd/3rd-degree traversal across 1B+ nodes


## 3. Flow 1 — News Feed Generation & Delivery


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 400" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#0A66C2">
</polygon>
</marker>
</defs>


<rect x="20" y="160" width="120" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="80" y="190" text-anchor="middle" fill="#fff" font-size="13">
Author
</text>
<text x="80" y="205" text-anchor="middle" fill="#fff" font-size="11">
creates post
</text>


<rect x="190" y="160" width="130" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="255" y="190" text-anchor="middle" fill="#fff" font-size="13">
API Gateway
</text>


<rect x="370" y="60" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="435" y="90" text-anchor="middle" fill="#fff" font-size="13">
Post Service
</text>


<rect x="370" y="160" width="130" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="435" y="190" text-anchor="middle" fill="#fff" font-size="13">
Kafka
</text>
<text x="435" y="205" text-anchor="middle" fill="#fff" font-size="11">
feed-events
</text>


<rect x="370" y="270" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="435" y="300" text-anchor="middle" fill="#fff" font-size="13">
Feed Mixer
</text>
<text x="435" y="315" text-anchor="middle" fill="#fff" font-size="11">
hybrid push/pull
</text>


<rect x="560" y="60" width="130" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="625" y="90" text-anchor="middle" fill="#fff" font-size="13">
Espresso DB
</text>
<text x="625" y="105" text-anchor="middle" fill="#fff" font-size="11">
(posts store)
</text>


<rect x="560" y="160" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="625" y="190" text-anchor="middle" fill="#fff" font-size="13">
Feed Fanout
</text>
<text x="625" y="205" text-anchor="middle" fill="#fff" font-size="11">
Worker
</text>


<rect x="560" y="270" width="130" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="625" y="300" text-anchor="middle" fill="#fff" font-size="13">
ML Ranker
</text>
<text x="625" y="315" text-anchor="middle" fill="#fff" font-size="11">
(relevance model)
</text>


<rect x="750" y="160" width="130" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="815" y="190" text-anchor="middle" fill="#fff" font-size="13">
Feed Cache
</text>
<text x="815" y="205" text-anchor="middle" fill="#fff" font-size="11">
(Couchbase)
</text>


<rect x="940" y="160" width="120" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="1000" y="190" text-anchor="middle" fill="#fff" font-size="13">
Reader
</text>
<text x="1000" y="205" text-anchor="middle" fill="#fff" font-size="11">
views feed
</text>


<line x1="140" y1="190" x2="188" y2="190" stroke="#0A66C2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="320" y1="180" x2="368" y2="95" stroke="#0A66C2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="320" y1="190" x2="368" y2="190" stroke="#0A66C2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="500" y1="90" x2="558" y2="90" stroke="#0A66C2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="500" y1="190" x2="558" y2="190" stroke="#0A66C2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="690" y1="190" x2="748" y2="190" stroke="#0A66C2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="625" y1="222" x2="625" y2="268" stroke="#0A66C2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="435" y1="222" x2="435" y2="268" stroke="#0A66C2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="500" y1="300" x2="558" y2="300" stroke="#0A66C2" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="880" y1="190" x2="938" y2="190" stroke="#0A66C2" stroke-width="2" marker-end="url(#a1)">
</line>


</svg>

</details>


### Flow Steps


1. **Author creates post** → POST to `/v2/posts` with text, media URNs, visibility settings

2. **Post Service** validates content (spam detection, profanity filter), stores in Espresso (LinkedIn's document store), returns postID

3. **Kafka event** published: `{postId, authorId, timestamp, visibility}` to `feed-events` topic

4. **Feed Fanout Worker** consumes event; for authors with <10K connections → **push model** (fan-out-on-write): write postID to each follower's feed mailbox in Couchbase

5. For authors with >10K followers (influencers/celebrities) → **pull model** (fan-out-on-read): skip fanout, fetch at read time

6. **Feed Mixer** on read request: merges pre-materialized feed (push) + real-time pull from followed influencers + sponsored content

7. **ML Ranker** scores and re-ranks merged candidates using features: author affinity, content quality, engagement predictions, recency, content type

8. **Feed Cache** stores ranked feed per user; paginated response returned to client


### Example


**Scenario:** User A (500 connections) posts "Excited to start my new role at Meta!"


**Write path:** Post stored in Espresso → Kafka event → Fanout worker writes postID to 500 feed mailboxes in Couchbase (~50ms total, batched writes)


**Read path:** User B opens LinkedIn → Feed Mixer pulls B's mailbox (50 recent postIDs) + pull-based influencer posts (5 influencers B follows) + 2 sponsored posts → ML Ranker scores 57 candidates → returns top 10 with pagination cursor


**Ranking features:** Author A is B's close connection (messaged 3x this week) → high affinity score. Post has "new role" keyword → job-change signal boosted. Recent (2 hours old) → recency boost. Final score: 0.87 → ranked #2 in B's feed.


### Deep Dives


Hybrid Push/Pull Feed Model


LinkedIn uses a hybrid approach: **Fan-out-on-write** for normal users (pre-materialize feed into per-user mailboxes in Couchbase). **Fan-out-on-read** for mega-influencers (Bill Gates, 35M followers — fanout would write to 35M mailboxes). The threshold is configurable (~10K followers). At read time, the Feed Mixer merges both sources. This is the same approach used by Twitter (pre-Elon). The key advantage: most users (99%+) have <10K connections, so push handles 99% of write-side load efficiently.


Feed Ranking ML Model


LinkedIn's feed ranking uses a multi-objective model trained on implicit signals (dwell time, clicks, reactions, comments, shares, hides). Architecture: **two-tower model** for candidate retrieval + **deep neural network** for ranking. Key features: member-to-member affinity (interaction history), content quality score (anti-viral: LinkedIn explicitly deprioritizes clickbait), creator authority, content freshness, diversity penalty (avoid same-author domination). The ranking objective is: `Score = P(click) × E[dwell_time] + w1×P(like) + w2×P(comment) + w3×P(share) - w4×P(hide)`


Espresso (LinkedIn's Document Store)


Custom-built distributed document store. Key features: **router-based** architecture (stateless routers → storage nodes), **MySQL** as storage engine per partition (proven durability), online schema evolution, change capture streams (for Kafka integration). Supports secondary indexes, transactions within a partition. Used for: profiles, posts, connections, messages. Sharded by entity ID (e.g., memberID for profiles, postID for posts).


Kafka at LinkedIn Scale


LinkedIn invented Apache Kafka. Their deployment: **7+ trillion messages/day**, 100K+ topics, multi-DC replication via MirrorMaker 2.0. Feed events topic handles ~50K messages/sec. Key patterns: consumer groups for parallel processing, exactly-once semantics for critical paths (connection events), at-least-once for feed (idempotent consumers). Compacted topics for latest-state data (e.g., latest profile version). LinkedIn uses Kafka for: feed fanout, search indexing, analytics, change data capture from Espresso.


## 4. Flow 2 — Connection Graph & People Search


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 380" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#00a0dc">
</polygon>
</marker>
</defs>


<rect x="20" y="150" width="120" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="80" y="180" text-anchor="middle" fill="#fff" font-size="13">
Recruiter
</text>
<text x="80" y="195" text-anchor="middle" fill="#fff" font-size="11">
searches people
</text>


<rect x="190" y="150" width="130" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="255" y="180" text-anchor="middle" fill="#fff" font-size="13">
API Gateway
</text>


<rect x="370" y="50" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="435" y="80" text-anchor="middle" fill="#fff" font-size="13">
Search Service
</text>
<text x="435" y="95" text-anchor="middle" fill="#fff" font-size="11">
(Galene)
</text>


<rect x="370" y="150" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="435" y="180" text-anchor="middle" fill="#fff" font-size="13">
Graph Service
</text>
<text x="435" y="195" text-anchor="middle" fill="#fff" font-size="11">
(connection check)
</text>


<rect x="370" y="260" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="435" y="290" text-anchor="middle" fill="#fff" font-size="13">
PYMK Service
</text>
<text x="435" y="305" text-anchor="middle" fill="#fff" font-size="11">
People You May Know
</text>


<rect x="560" y="50" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="630" y="80" text-anchor="middle" fill="#fff" font-size="13">
Galene Index
</text>
<text x="630" y="95" text-anchor="middle" fill="#fff" font-size="11">
(custom Lucene)
</text>


<rect x="560" y="150" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="630" y="180" text-anchor="middle" fill="#fff" font-size="13">
Graph DB
</text>
<text x="630" y="195" text-anchor="middle" fill="#fff" font-size="11">
(adj. list in memory)
</text>


<rect x="560" y="260" width="140" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="630" y="290" text-anchor="middle" fill="#fff" font-size="13">
ML Model
</text>
<text x="630" y="305" text-anchor="middle" fill="#fff" font-size="11">
(connection pred.)
</text>


<rect x="760" y="150" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="825" y="180" text-anchor="middle" fill="#fff" font-size="13">
Result Blender
</text>


<rect x="930" y="150" width="100" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="980" y="185" text-anchor="middle" fill="#fff" font-size="13">
Results
</text>


<line x1="140" y1="180" x2="188" y2="180" stroke="#00a0dc" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="320" y1="170" x2="368" y2="85" stroke="#00a0dc" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="320" y1="180" x2="368" y2="180" stroke="#00a0dc" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="500" y1="80" x2="558" y2="80" stroke="#00a0dc" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="500" y1="180" x2="558" y2="180" stroke="#00a0dc" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="500" y1="290" x2="558" y2="290" stroke="#00a0dc" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="700" y1="80" x2="758" y2="170" stroke="#00a0dc" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="700" y1="180" x2="758" y2="180" stroke="#00a0dc" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="700" y1="290" x2="758" y2="195" stroke="#00a0dc" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="890" y1="180" x2="928" y2="180" stroke="#00a0dc" stroke-width="2" marker-end="url(#a2)">
</line>


</svg>

</details>


### Flow Steps


1. **Recruiter queries:** "software engineer machine learning san francisco" with filters (years of experience, current company, skills)

2. **Galene Search** (LinkedIn's custom search engine built on Lucene) performs faceted search across member profiles, returns candidate set

3. **Graph Service** annotates each result with connection degree (1st, 2nd, 3rd, out-of-network) relative to the searcher

4. **PYMK (People You May Know)** service augments results with connection suggestions based on mutual connections, shared companies, shared schools

5. **Result Blender** merges search results with graph annotations, applies recruiter-specific ranking (open-to-work signal, InMail response rate, profile completeness)

6. **Privacy enforcement:** Profiles with restricted visibility filtered out; anonymous profile view setting respected


### Example


**Query:** Recruiter at Meta searches: "ML engineer, 5+ years, San Francisco, open to work"


**Galene returns:** 12,000 matching profiles from index


**Graph annotation:** 45 are 1st-degree, 2,300 are 2nd-degree, rest are 3rd+


**Ranking signals:** "Open to Work" badge (signal sent privately to recruiters) → huge boost. Recent profile update (active user) → boost. Skills endorsement count for "Machine Learning" → boost. Shared connections with recruiter → higher trust. InMail response rate history → prioritize responsive candidates.


**Top result:** Jane Doe — ML Engineer at Stripe, 7 years exp, 2nd-degree connection (3 mutual), "Open to Work" enabled, 15 endorsements for PyTorch


### Deep Dives


Social Graph Storage


LinkedIn's connection graph: 1B+ nodes (members), 50B+ edges (connections). Stored as **adjacency lists** in memory across a distributed graph service. Each member's connections stored as a sorted array of memberIDs. 2nd-degree lookup: intersect connection lists of two members (sorted merge join). Entire graph stored in ~500GB compressed memory across a cluster. For 3rd-degree, use BFS with depth limit = 3. Connection degree annotated on every search result, feed item, and profile view. The graph is also used for: PYMK recommendations, shared connection counts, and "who viewed your profile" features.


Galene: LinkedIn's Search Engine


Custom search engine built on top of Lucene with LinkedIn-specific extensions. Key features: **early termination** (stop scoring after finding enough good results), **join queries** (join member index with company index), **real-time indexing** (profile updates searchable within seconds via nearline indexing pipeline). Index sharded by memberID. Supports: full-text search, faceted filters (location, company, skills, industry, seniority), type-ahead completion. Each shard: ~10M profiles. Total: ~100 shards with 3x replication. Custom scoring: BM25 + network proximity + profile completeness + activity recency.


## 5. Flow 3 — Messaging (Real-Time Chat)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#57c4e5">
</polygon>
</marker>
</defs>


<rect x="20" y="140" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="170" text-anchor="middle" fill="#fff" font-size="13">
Sender
</text>


<rect x="180" y="140" width="120" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="240" y="170" text-anchor="middle" fill="#fff" font-size="13">
API Gateway
</text>


<rect x="350" y="40" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="415" y="70" text-anchor="middle" fill="#fff" font-size="13">
Message Service
</text>


<rect x="350" y="140" width="130" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="415" y="170" text-anchor="middle" fill="#fff" font-size="13">
Kafka
</text>
<text x="415" y="185" text-anchor="middle" fill="#fff" font-size="11">
msg-events
</text>


<rect x="350" y="240" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="415" y="270" text-anchor="middle" fill="#fff" font-size="13">
Presence Service
</text>


<rect x="540" y="40" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="610" y="70" text-anchor="middle" fill="#fff" font-size="13">
Espresso
</text>
<text x="610" y="85" text-anchor="middle" fill="#fff" font-size="11">
(messages store)
</text>


<rect x="540" y="140" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="610" y="170" text-anchor="middle" fill="#fff" font-size="13">
Real-Time
</text>
<text x="610" y="185" text-anchor="middle" fill="#fff" font-size="11">
Delivery (Akka)
</text>


<rect x="540" y="240" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="610" y="270" text-anchor="middle" fill="#fff" font-size="13">
Push Notification
</text>
<text x="610" y="285" text-anchor="middle" fill="#fff" font-size="11">
Service
</text>


<rect x="740" y="140" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="795" y="170" text-anchor="middle" fill="#fff" font-size="13">
Recipient
</text>


<line x1="130" y1="170" x2="178" y2="170" stroke="#57c4e5" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="300" y1="155" x2="348" y2="75" stroke="#57c4e5" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="300" y1="170" x2="348" y2="170" stroke="#57c4e5" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="480" y1="70" x2="538" y2="70" stroke="#57c4e5" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="480" y1="170" x2="538" y2="170" stroke="#57c4e5" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="480" y1="170" x2="538" y2="265" stroke="#57c4e5" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="680" y1="170" x2="738" y2="170" stroke="#57c4e5" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="415" y1="240" x2="415" y2="202" stroke="#57c4e5" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


### Flow Steps


1. **Sender** sends message via REST API: POST `/messaging/conversations/{convId}/messages` with body, attachments

2. **Message Service** validates (spam check, InMail credits if non-connection), persists to Espresso, assigns message sequence number

3. **Kafka event** published for async fanout: `{convId, msgId, senderId, recipientIds, timestamp}`

4. **Real-Time Delivery (Akka)** maintains persistent connections (long-polling / SSE) to online recipients; pushes message instantly via the open connection

5. **Presence Service** tracks online/offline status; if recipient is offline → route to Push Notification Service (APNs/FCM)

6. **Read receipts:** Recipient's client sends acknowledgment → updates message status (sent → delivered → read)

7. **Typing indicators:** Ephemeral WebSocket messages, not persisted


### Example


**Scenario:** Recruiter sends InMail to candidate Jane (not connected)


**InMail validation:** Check recruiter has InMail credits (Recruiter Lite: 5/month, Recruiter: 150/month) → deduct 1 credit


**Persist:** Message stored in Espresso under conversation `conv_r123_j456`, sequence_num: 1


**Delivery:** Jane is on mobile (long-polling connection active) → message pushed via Akka actor for user j456 → appears in inbox within 200ms


**Notification:** Also triggers push notification: "New InMail from [Recruiter Name] at Meta: We have an exciting ML role..."


**If Jane is offline:** APNs push → badge count updated → message retrieved on next app open via pull


### Deep Dives


Real-Time Delivery with Akka


LinkedIn's real-time messaging uses **Akka actors** (Play Framework). Each online user has a dedicated actor that maintains their persistent connection (long-poll or SSE). When a message arrives, the system looks up the recipient's actor (via consistent hashing of memberID → server) and delivers the message. If the user's connection is on a different server, the message is forwarded via the actor system's remoting. Connection migration handled via cluster membership protocol. LinkedIn processes ~20B real-time events/day through this system.


Message Ordering & Delivery Guarantees


Each conversation has a monotonically increasing **sequence number** (generated per-partition in Espresso). This ensures total ordering within a conversation even under concurrent sends. Delivery guarantee: **at-least-once** from Kafka to delivery layer; clients deduplicate by sequence number. Read receipts are aggregated and batched (every 5 seconds) to avoid excessive writes. Offline message sync: client sends last-seen sequence number, server returns all messages with higher sequence numbers.


## 6. Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 580" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#0A66C2">
</polygon>
</marker>
</defs>


<rect x="10" y="250" width="100" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="60" y="280" text-anchor="middle" fill="#fff" font-size="12">
Clients
</text>
<text x="60" y="295" text-anchor="middle" fill="#fff" font-size="10">
Web/iOS/Android
</text>


<rect x="150" y="190" width="110" height="50" rx="8" fill="#546e7a" stroke="#90a4ae" stroke-width="2">
</rect>
<text x="205" y="220" text-anchor="middle" fill="#fff" font-size="12">
CDN (Akamai)
</text>


<rect x="150" y="260" width="110" height="50" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="205" y="290" text-anchor="middle" fill="#fff" font-size="12">
API Gateway
</text>


<rect x="300" y="50" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="360" y="78" text-anchor="middle" fill="#fff" font-size="11">
Profile Service
</text>


<rect x="300" y="110" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="360" y="138" text-anchor="middle" fill="#fff" font-size="11">
Feed Service
</text>


<rect x="300" y="170" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="360" y="198" text-anchor="middle" fill="#fff" font-size="11">
Post Service
</text>


<rect x="300" y="230" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="360" y="258" text-anchor="middle" fill="#fff" font-size="11">
Search (Galene)
</text>


<rect x="300" y="290" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="360" y="318" text-anchor="middle" fill="#fff" font-size="11">
Messaging
</text>


<rect x="300" y="350" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="360" y="378" text-anchor="middle" fill="#fff" font-size="11">
Jobs Service
</text>


<rect x="300" y="410" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="360" y="438" text-anchor="middle" fill="#fff" font-size="11">
Graph Service
</text>


<rect x="300" y="470" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="360" y="498" text-anchor="middle" fill="#fff" font-size="11">
Notifications
</text>


<rect x="480" y="120" width="120" height="45" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="540" y="148" text-anchor="middle" fill="#fff" font-size="11">
Kafka
</text>


<rect x="480" y="250" width="120" height="45" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="540" y="278" text-anchor="middle" fill="#fff" font-size="11">
ML Platform
</text>


<rect x="650" y="60" width="120" height="45" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="710" y="88" text-anchor="middle" fill="#fff" font-size="11">
Espresso
</text>


<rect x="650" y="120" width="120" height="45" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="710" y="148" text-anchor="middle" fill="#fff" font-size="11">
Venice (derived)
</text>


<rect x="650" y="185" width="120" height="45" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="710" y="213" text-anchor="middle" fill="#fff" font-size="11">
Couchbase Cache
</text>


<rect x="650" y="250" width="120" height="45" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="710" y="278" text-anchor="middle" fill="#fff" font-size="11">
Galene Index
</text>


<rect x="650" y="320" width="120" height="45" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="710" y="348" text-anchor="middle" fill="#fff" font-size="11">
HDFS / Spark
</text>


<line x1="110" y1="275" x2="148" y2="285" stroke="#0A66C2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="110" y1="265" x2="148" y2="215" stroke="#0A66C2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="260" y1="285" x2="298" y2="200" stroke="#0A66C2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="420" y1="140" x2="478" y2="140" stroke="#0A66C2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="600" y1="140" x2="648" y2="85" stroke="#0A66C2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="600" y1="140" x2="648" y2="140" stroke="#0A66C2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="420" y1="260" x2="478" y2="270" stroke="#0A66C2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="420" y1="75" x2="648" y2="80" stroke="#0A66C2" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="600" y1="270" x2="648" y2="270" stroke="#0A66C2" stroke-width="2" marker-end="url(#a4)">
</line>


</svg>

</details>


## 7. Database Schema


### Members (Espresso — partitioned by member_id)


Table: members   `SHARD`
- member_id: bigint             `PK`
- email: varchar(255)           `IDX`
- password_hash: varchar(255)
- first_name: varchar(100)
- last_name: varchar(100)       `IDX`
- headline: varchar(300)
- summary: text
- location: varchar(100)        `IDX`
- industry: varchar(100)        `IDX`
- profile_photo_url: varchar(500)
- vanity_name: varchar(100)     `IDX`
- visibility: enum('public','connections','private')
- open_to_work: boolean
- created_at: timestamp
- updated_at: timestamp


### Connections (Graph — adjacency list)


Table: connections   `SHARD`
- member_id: bigint             `PK`
- connected_member_id: bigint   `PK`
- status: enum('pending','accepted','blocked')
- connected_at: timestamp
- relationship: varchar(50)
Index: (connected_member_id, member_id) for reverse lookups   `IDX`
Note: Bidirectional — both (A→B) and (B→A) rows stored for fast adjacency lookup


### Posts (Espresso — partitioned by post_id)


Table: posts   `SHARD`
- post_id: bigint               `PK`
- author_id: bigint             `FK`  `IDX`
- content: text
- media_urls: json
- post_type: enum('text','image','video','article','poll','document')
- visibility: enum('public','connections','group')
- like_count: int (denormalized, async updated)
- comment_count: int (denormalized)
- share_count: int (denormalized)
- created_at: timestamp         `IDX`
- updated_at: timestamp


### Messages (Espresso — partitioned by conversation_id)


Table: messages   `SHARD`
- conversation_id: bigint       `PK`
- sequence_num: bigint          `PK`
- sender_id: bigint             `FK`
- body: text (encrypted at rest)
- attachments: json
- msg_type: enum('text','inmail','system')
- created_at: timestamp
- delivery_status: map<bigint, enum('sent','delivered','read')>


### Jobs (Espresso — partitioned by job_id)


Table: jobs   `SHARD`
- job_id: bigint                `PK`
- company_id: bigint            `FK`  `IDX`
- title: varchar(200)           `IDX`
- description: text
- location: varchar(200)        `IDX`
- remote_type: enum('onsite','remote','hybrid')
- seniority: enum('intern','entry','mid','senior','director','vp','cxo')
- salary_range: json
- skills_required: json         `IDX`
- posted_at: timestamp
- expires_at: timestamp
- status: enum('active','closed','paused')
- easy_apply: boolean
- applicant_count: int (denormalized)


## 8. Cache & CDN Deep Dive


### Caching Strategy


| Layer        | Technology             | What's Cached                                    | TTL                                   | Strategy                                         |
|--------------|------------------------|--------------------------------------------------|---------------------------------------|--------------------------------------------------|
| CDN          | Akamai / LinkedIn Edge | Static assets, profile images, JS/CSS bundles    | Hours to months                       | Cache-Control immutable for hashed assets        |
| App Cache    | Couchbase              | Feed mailboxes, session data, profile summaries  | Minutes to hours                      | Write-through on update, LRU eviction            |
| Graph Cache  | In-memory (custom)     | Connection adjacency lists (entire social graph) | Real-time (event-driven invalidation) | Graph stored entirely in memory (~500GB cluster) |
| Search Cache | Galene internal        | Popular query results, posting lists             | Minutes                               | LRU, invalidated on index update                 |
| Client Cache | Browser/App            | Feed items, profile data, conversation history   | Session-based                         | ETag / If-None-Match for conditional requests    |


### Couchbase at LinkedIn


LinkedIn uses Couchbase as a general-purpose caching and session store layer. Feed mailboxes: each user has a Couchbase document with their pre-materialized feed (list of postIDs). On feed read, fetch mailbox → resolve postIDs to full post data from Espresso (parallel batch fetch). Eviction: LRU with TTL. Replication: 2 replicas per vBucket. Cross-datacenter replication via XDCR for DR.


### Venice: Derived Data Store


**Venice** is LinkedIn's custom derived data platform. It pre-computes and serves read-optimized views of data (e.g., pre-computed member features for ML models, denormalized company pages). Built on RocksDB with a custom serving layer. Data is computed via Spark batch jobs → pushed to Venice → served with sub-ms latency. Used for: PYMK features, job recommendation features, ad targeting features.


## 9. Scaling Considerations


### Service Architecture


- **Microservices:** 2,000+ services running on LinkedIn's custom service mesh. Service discovery via D2 (dynamic discovery). RPC framework: Rest.li (LinkedIn's open-source REST framework with IDL)

- **Multi-DC:** Active-active across 3+ data centers. Each DC serves full read/write traffic. Espresso supports cross-DC replication with conflict resolution (last-writer-wins for most entities)

- **Data partitioning:** All primary stores (Espresso, Couchbase) sharded by entity ID. Graph service co-locates a member's connections on the same shard


### Load Balancing


- **DNS-level:** GeoDNS routes users to nearest data center

- **L4:** Hardware LBs (A10 Networks) for TCP distribution

- **L7:** Software LBs (custom Indis / Envoy-based) with header-based routing, service-mesh integration

- **D2:** Client-side load balancing within services — hash ring-based for stateful services, round-robin for stateless


### Key Scale Numbers


| Members                | 1B+         |
|------------------------|-------------|
| Monthly Active Users   | 310M+       |
| Connections (edges)    | 50B+        |
| Posts per day          | ~2M         |
| Messages per day       | ~100M       |
| Job listings           | ~20M active |
| API calls per day      | 2B+         |
| Kafka messages per day | 7T+         |


## 10. Tradeoffs


> **

> **
✅ Hybrid Push/Pull Feed


Push model provides fast reads for 99% of users. Pull for influencers avoids write amplification (35M fan-out per post). Best of both worlds — low read latency + manageable write cost.


> **
❌ Feed Staleness


Pre-materialized feeds can be slightly stale (fanout takes seconds). Influencer posts fetched at read time add latency. Feed mixing at read time is complex — must handle missing/duplicate items, ordering across sources.


> **
✅ Espresso (Custom DB)


Purpose-built for LinkedIn's access patterns. MySQL storage engine gives proven durability. Router-based architecture allows independent scaling of routing and storage. Online schema evolution without downtime.


> **
❌ Custom Infrastructure Burden


Building and maintaining Espresso, Venice, Galene, D2, Rest.li requires massive engineering investment. Cannot leverage commodity cloud databases. Hard to hire for custom tech stack. Migration path to cloud is complex.


> **
✅ In-Memory Graph


Sub-millisecond connection degree lookups. Enables real-time 2nd/3rd-degree annotations on every search result and feed item. Critical for LinkedIn's core value prop (professional network proximity).


> **
❌ Memory Cost


500GB+ memory cluster just for the social graph. Must be replicated for availability. Graph updates must propagate across all replicas. Adding new graph features (e.g., skill endorsement graph) requires additional memory capacity.


## 11. Alternative Approaches


Neo4j Graph Database


Native graph DB for social network queries. Supports Cypher query language for complex traversals. Better for ad-hoc graph queries. LinkedIn chose custom in-memory graph for lower latency at their specific scale. Neo4j could work for smaller professional networks (<100M users).


Elasticsearch Instead of Galene


Open-source, widely adopted, strong faceted search. LinkedIn built Galene for tighter integration with their data pipeline and custom ranking features. For a new professional network, Elasticsearch would be a more practical starting point with lower operational cost.


Redis Streams for Messaging


Simpler real-time messaging using Redis Streams + pub/sub instead of Akka actors. Lower complexity, good for <10M concurrent users. LinkedIn's Akka-based system handles their specific scale (100M+ msgs/day) with fine-grained actor-per-user model.


GraphQL Federation


Instead of Rest.li, use GraphQL with schema federation across services. Reduces over-fetching (client specifies exact fields needed). LinkedIn's Rest.li predates GraphQL; new social platforms (e.g., Threads) use GraphQL natively.


## 12. Additional Information


### LinkedIn's Tech Stack (Open Source Contributions)


- **Apache Kafka** — distributed event streaming (invented at LinkedIn)

- **Apache Samza** — stream processing framework (built at LinkedIn)

- **Rest.li** — RESTful service framework with IDL and code generation

- **Espresso** — distributed document store (MySQL-backed)

- **Venice** — derived data serving platform (batch → online serving)

- **Galene** — custom search engine (Lucene-based)

- **D2** — dynamic service discovery and client-side load balancing

- **Brooklin** — change data capture and streaming bridge


### Key Protocols


- **Client → Server:** HTTPS/2 REST (Rest.li) with JSON payloads; Protobuf for mobile (bandwidth optimization)

- **Service → Service:** Rest.li over HTTP/2 (internal), gRPC for newer services

- **Real-time:** Long-polling / Server-Sent Events for messaging and notifications (Akka-based)

- **Event Bus:** Kafka (Avro-encoded events with Schema Registry)

- **Cross-DC Replication:** Kafka MirrorMaker 2.0 + Espresso XDCR + Couchbase XDCR