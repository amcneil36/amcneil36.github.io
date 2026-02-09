# 💬 Microsoft Teams - System Design


## Functional Requirements


1. **Real-Time Messaging:** 1:1 and group chat with @mentions, reactions, threaded replies, file sharing, message editing/deletion, typing indicators, read receipts

2. **Video/Audio Conferencing:** HD video meetings (up to 10K participants), screen sharing, breakout rooms, recording, live captions, Together Mode (AI background)

3. **Channels & Teams:** Organized workspaces with channels, tabs (apps integration), wiki, file storage (SharePoint), permissions (owner/member/guest), cross-org federation


## Non-Functional Requirements


- **Scale:** 320M+ monthly active users, 2B+ meeting minutes/day, millions of concurrent meetings

- **Latency:** Message delivery <500ms, video/audio <200ms end-to-end, screen share <300ms

- **Availability:** 99.99% (Microsoft 365 SLA), multi-region active-active with automatic failover

- **Compliance:** GDPR, HIPAA, FedRAMP High, SOC 2, eDiscovery, legal hold, DLP (Data Loss Prevention), information barriers

- **Integration:** Deep M365 integration — SharePoint, OneDrive, Outlook, Power Platform, 1,800+ third-party apps


## Flow 1: Real-Time Chat Messaging


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#6264A7">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Teams Client
</text>
<text x="80" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Desktop/Web/Mobile)
</text>


<rect x="190" y="50" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="255" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Azure Front Door
</text>
<text x="255" y="87" text-anchor="middle" fill="#fff" font-size="10">
(L7 LB + WAF)
</text>


<rect x="370" y="50" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="435" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Chat Service
</text>
<text x="435" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Message Router)
</text>


<rect x="550" y="20" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="615" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
SignalR Hub
</text>
<text x="615" y="57" text-anchor="middle" fill="#fff" font-size="10">
(WebSocket/SSE)
</text>


<rect x="550" y="90" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="615" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Azure Cosmos DB
</text>
<text x="615" y="127" text-anchor="middle" fill="#fff" font-size="10">
(Message Store)
</text>


<rect x="740" y="20" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="805" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Push Notifier
</text>
<text x="805" y="57" text-anchor="middle" fill="#fff" font-size="10">
(WNS/FCM/APNs)
</text>


<rect x="740" y="90" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="805" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Compliance
</text>
<text x="805" y="127" text-anchor="middle" fill="#fff" font-size="10">
(DLP + eDiscovery)
</text>


<rect x="370" y="200" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="435" y="222" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Azure Search
</text>
<text x="435" y="237" text-anchor="middle" fill="#fff" font-size="10">
(Message Index)
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#6264A7" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="320" y1="75" x2="365" y2="75" stroke="#6264A7" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="65" x2="545" y2="45" stroke="#6264A7" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="85" x2="545" y2="110" stroke="#6264A7" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="680" y1="45" x2="735" y2="45" stroke="#6264A7" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="680" y1="115" x2="735" y2="115" stroke="#6264A7" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="435" y1="100" x2="435" y2="195" stroke="#6264A7" stroke-width="2" marker-end="url(#ah)">
</line>


</svg>

</details>


### Steps


1. Client sends message via HTTPS POST through Azure Front Door (global L7 load balancer with WAF)

2. Chat Service authenticates via Azure AD (OAuth2 + tenant validation), validates permissions (is user in this chat/channel?)

3. Message persisted to Azure Cosmos DB (multi-region write, automatic failover) — partitioned by chat/channel ID

4. SignalR Hub pushes message to all online chat participants via persistent WebSocket connections (fallback: SSE → long polling)

5. Offline users receive push notification via Windows Notification Service (WNS), FCM, or APNs

6. Compliance pipeline: DLP policy check (credit card numbers, SSN detection), message retained per retention policy, eDiscovery indexed

7. Message indexed in Azure Cognitive Search for cross-chat search functionality


### Example


User in "Engineering" channel types "@alice can you review the PR?" Chat Service persists to Cosmos DB (partition key: channel_12345). SignalR pushes to all 50 online channel members. @alice receives push notification (mobile). DLP scans message (clean). Message indexed for search. Compliance: retained for 7 years per org policy.


### Deep Dives


SignalR (Real-Time)


**Protocol:** Azure SignalR Service — managed WebSocket hub; fallback to Server-Sent Events → long polling


**Scale:** Serverless mode supports millions of concurrent connections; auto-scales based on connection count


**Groups:** Each chat/channel is a SignalR group — broadcast to group delivers to all connected members


**Sticky:** Connection affinity to specific SignalR unit; reconnection logic handles server-side failover


Azure Cosmos DB


**API:** SQL API (JSON documents) with server-side JavaScript stored procedures for atomic operations


**Partition:** chat_id as partition key — all messages in a chat co-located for efficient range queries


**Consistency:** Session consistency (read-your-own-writes) within region; bounded staleness across regions


**RU Budget:** Auto-scale from 400 to 100K RU/s per container based on throughput demand


Compliance & DLP


**DLP:** Sensitive information types (credit cards, SSN, passport numbers) detected in real-time; messages blocked or redacted


**eDiscovery:** All messages searchable by compliance officers; legal hold prevents deletion


**Retention:** Configurable per org: 30 days to forever; messages stored in Azure Blob for long-term compliance


**Information Barriers:** Prevent communication between specific groups (e.g., investment banking and research)


## Flow 2: Video/Audio Conferencing


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 320" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#464EB8">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Client A
</text>
<text x="80" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Camera + Mic)
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Calling Service
</text>
<text x="255" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Signaling)
</text>


<rect x="370" y="30" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Media Processor
</text>
<text x="435" y="67" text-anchor="middle" fill="#fff" font-size="10">
(SFU / MCU)
</text>


<rect x="370" y="100" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="435" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
TURN/STUN
</text>
<text x="435" y="137" text-anchor="middle" fill="#fff" font-size="10">
(NAT Traversal)
</text>


<rect x="560" y="30" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="625" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Recording
</text>
<text x="625" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Azure Media)
</text>


<rect x="560" y="100" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="625" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Live Captions
</text>
<text x="625" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Azure Speech)
</text>


<rect x="740" y="60" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="805" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Stream Storage
</text>
<text x="805" y="97" text-anchor="middle" fill="#fff" font-size="10">
(OneDrive/Stream)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#464EB8" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#464EB8" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="95" x2="365" y2="120" stroke="#464EB8" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="500" y1="55" x2="555" y2="55" stroke="#464EB8" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="500" y1="55" x2="555" y2="120" stroke="#464EB8" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="690" y1="55" x2="735" y2="75" stroke="#464EB8" stroke-width="2" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Steps


1. User clicks "Meet Now" → Calling Service creates meeting, generates join URL, initiates signaling (SDP offer/answer)

2. ICE negotiation via STUN/TURN servers — determines optimal media path (direct P2P for 1:1, SFU relay for groups)

3. Media Processor (SFU architecture for most meetings) receives audio/video from speakers, selectively forwards to participants

4. For large meetings (100+ participants): MCU mode — server mixes video into gallery view, reduces client bandwidth

5. Screen sharing: encoded as separate video stream at 1080p/30fps with content-optimized codec (lower framerate, higher quality)

6. Live captions powered by Azure Cognitive Speech — real-time speech-to-text with speaker attribution

7. Recording: server-side recording (cloud recording) → transcoded and stored in OneDrive/Microsoft Stream


### Example


10-person team standup meeting. Calling Service allocates SFU in Azure US-East. Each participant sends 720p video (~1.5 Mbps) + Opus audio to SFU. SFU forwards only active speaker's video at full resolution; other participants at thumbnail quality (180p). Total downstream per client: ~3 Mbps (vs. 15 Mbps without SFU optimization). Meeting recorded → uploaded to team's SharePoint → transcript generated in 10 minutes.


### Deep Dives


Media Architecture


**Small Meetings (≤4):** P2P mesh or single SFU — each client sends/receives directly


**Medium (5-49):** SFU with simulcast — each sender produces 3 quality levels; SFU selects appropriate level per receiver based on viewport size and bandwidth


**Large (50-10K):** Hierarchical SFU + MCU hybrid — server-side composition for gallery view; cascaded SFUs across regions


**Codec:** H.264/VP8 for video; Opus for audio; SRTP encryption (DTLS-SRTP key exchange)


Together Mode (AI)


**Background:** AI body segmentation model (real-time on client GPU/CPU) separates person from background


**Composition:** Segmented participants placed into shared virtual scene (auditorium, coffee shop)


**Processing:** Client-side ML inference (~20ms per frame on modern hardware); no server-side processing needed


## Flow 3: Teams, Channels & App Integration


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#9EA2FF">
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
Graph API
</text>
<text x="255" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Microsoft Graph)
</text>


<rect x="370" y="30" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="435" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
SharePoint
</text>
<text x="435" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Files + Wiki)
</text>


<rect x="370" y="100" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Azure AD
</text>
<text x="435" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Identity + Groups)
</text>


<rect x="560" y="30" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="625" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
App Platform
</text>
<text x="625" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Tabs + Bots)
</text>


<rect x="560" y="100" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="625" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Connectors
</text>
<text x="625" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Webhook + O365)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#9EA2FF" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#9EA2FF" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="95" x2="365" y2="120" stroke="#9EA2FF" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="55" x2="555" y2="55" stroke="#9EA2FF" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="125" x2="555" y2="125" stroke="#9EA2FF" stroke-width="2" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Steps


1. Creating a Team provisions: Azure AD security group, SharePoint site (for files), Exchange mailbox (for email integration), Planner plan

2. Channels are sub-divisions within a Team — Standard (visible to all members), Private (subset), Shared (cross-org)

3. Each channel gets a SharePoint folder; files shared in chat uploaded to channel's SharePoint document library

4. Microsoft Graph API provides unified endpoint for all M365 data: users, teams, channels, messages, files, calendar

5. App Platform: Tabs (embedded web apps via iframe), Bots (conversational AI), Messaging Extensions (compose actions), Adaptive Cards

6. Connectors: incoming webhooks (external services post to channel) and O365 connectors (RSS, JIRA, GitHub notifications)


### Deep Dives


Microsoft Graph API


**Endpoint:** `https://graph.microsoft.com/v1.0/` — RESTful API for all M365 services


**Teams:** `GET /teams/{id}/channels/{id}/messages` — channel messages with OData pagination


**Auth:** OAuth 2.0 with delegated (user) or application (service) permissions; Azure AD consent model


**Webhooks:** Change notifications via `/subscriptions` — real-time push when resources change


Teams App Platform


**Tabs:** Embedded web pages (React/Angular) rendered in iframe; Teams SDK provides context (user, team, theme)


**Bots:** Azure Bot Framework — receive messages via Activity Protocol, respond with Adaptive Cards


**Adaptive Cards:** JSON-based card format for rich interactive content — forms, approvals, polls rendered natively


**Store:** Teams App Store — 1,800+ apps; IT admin can control which apps are allowed per org


## Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 460" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#6264A7">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="130" height="50" rx="8" fill="#34A853">
</rect>
<text x="85" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Desktop / Web
</text>
<text x="85" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Electron / React)
</text>


<rect x="20" y="130" width="130" height="50" rx="8" fill="#34A853">
</rect>
<text x="85" y="152" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Mobile Apps
</text>
<text x="85" y="167" text-anchor="middle" fill="#fff" font-size="10">
(iOS / Android)
</text>


<rect x="210" y="90" width="130" height="55" rx="8" fill="#FB8C00">
</rect>
<text x="275" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Azure Front Door
</text>
<text x="275" y="129" text-anchor="middle" fill="#fff" font-size="10">
(CDN + WAF)
</text>


<rect x="400" y="30" width="130" height="45" rx="8" fill="#4285F4">
</rect>
<text x="465" y="57" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Chat Service
</text>


<rect x="400" y="90" width="130" height="45" rx="8" fill="#4285F4">
</rect>
<text x="465" y="117" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Calling Service
</text>


<rect x="400" y="150" width="130" height="45" rx="8" fill="#4285F4">
</rect>
<text x="465" y="177" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Teams/Channel Svc
</text>


<rect x="590" y="30" width="130" height="45" rx="8" fill="#607D8B">
</rect>
<text x="655" y="57" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
SignalR Hub
</text>


<rect x="590" y="90" width="130" height="45" rx="8" fill="#9C27B0">
</rect>
<text x="655" y="117" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Media Processor
</text>


<rect x="590" y="150" width="130" height="45" rx="8" fill="#9C27B0">
</rect>
<text x="655" y="172" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Azure AD
</text>
<text x="655" y="187" text-anchor="middle" fill="#fff" font-size="10">
(Identity)
</text>


<rect x="780" y="60" width="130" height="45" rx="8" fill="#E53935">
</rect>
<text x="845" y="87" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Compliance Svc
</text>


<rect x="780" y="120" width="130" height="45" rx="8" fill="#FB8C00">
</rect>
<text x="845" y="147" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Bot Framework
</text>


<rect x="400" y="300" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="465" y="322" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Cosmos DB
</text>
<text x="465" y="339" text-anchor="middle" fill="#fff" font-size="10">
(Messages)
</text>


<rect x="590" y="300" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="655" y="322" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
SharePoint
</text>
<text x="655" y="339" text-anchor="middle" fill="#fff" font-size="10">
(Files + Sites)
</text>


<rect x="780" y="300" width="130" height="55" rx="8" fill="#00897B">
</rect>
<text x="845" y="322" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Azure Search
</text>
<text x="845" y="339" text-anchor="middle" fill="#fff" font-size="10">
(Message Index)
</text>


<line x1="150" y1="85" x2="205" y2="105" stroke="#6264A7" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="150" y1="155" x2="205" y2="130" stroke="#6264A7" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="340" y1="105" x2="395" y2="52" stroke="#6264A7" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="340" y1="118" x2="395" y2="112" stroke="#6264A7" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="340" y1="130" x2="395" y2="172" stroke="#6264A7" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="530" y1="52" x2="585" y2="52" stroke="#6264A7" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="530" y1="112" x2="585" y2="112" stroke="#6264A7" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="530" y1="172" x2="585" y2="172" stroke="#6264A7" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="720" y1="52" x2="775" y2="75" stroke="#6264A7" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="720" y1="172" x2="775" y2="142" stroke="#6264A7" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="465" y1="75" x2="465" y2="295" stroke="#6264A7" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="530" y1="195" x2="590" y2="310" stroke="#6264A7" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="720" y1="85" x2="780" y2="315" stroke="#6264A7" stroke-width="1.5" marker-end="url(#ah4)">
</line>


</svg>

</details>


## Database Schema


### Cosmos DB — Messages

`{
  "id": "msg_uuid",
  "chatId": "chat_12345",       // partition key
  "threadId": "thread_abc",
  "senderId": "user_uuid",
  "senderName": "Alice Smith",
  "body": {
    "contentType": "html",
    "content": "<p>@bob review the PR</p>"
  },
  "attachments": [{
    "id": "att_uuid",
    "name": "design.pdf",
    "contentUrl": "https://sharepoint.com/...",
    "contentType": "application/pdf"
  }],
  "mentions": [{"id": "user_bob", "text": "@bob"}],
  "reactions": [{"type": "like", "userId": "user_carol"}],
  "importance": "normal",
  "deleted": false,
  "editedAt": null,
  "createdAt": "2024-02-09T10:30:00Z",
  "_ts": 1707471000
}`


 `SHARD` Partitioned by chatId — all messages in same chat co-located in same logical partition


### Azure AD — Teams & Channels

`Team {
  id: UUID,                   `PK`
  displayName: "Engineering",
  description: "Engineering team",
  visibility: "private",     // private | public
  groupId: UUID,             // Azure AD group   `FK`
  sharepointSiteUrl: "https://org.sharepoint.com/sites/eng",
  createdAt: DateTime
}

Channel {
  id: UUID,                   `PK`
  teamId: UUID,               `FK`
  displayName: "backend",
  membershipType: "standard", // standard | private | shared
  sharepointFolderId: UUID,
  email: "eng-backend@org.com",
  createdAt: DateTime
}

TeamMember {
  teamId: UUID,               `FK`
  userId: UUID,               `FK`
  role: "owner" | "member" | "guest",
  addedAt: DateTime
}`


## Cache & CDN Deep Dive


Azure Front Door (CDN)


**Static:** Teams web app bundles, icons, fonts cached globally at 180+ edge PoPs


**WAF:** Web Application Firewall with DDoS protection, rate limiting, bot detection


**Routing:** Anycast + latency-based routing to nearest Azure region


Azure Redis Cache


**Cached:** User presence (online/away/busy), team membership lists, permission checks, recent conversations list


**Strategy:** Cache-aside with TTL; invalidated on presence change / membership update events


**TTL:** Presence: 60s; membership: 5min; conversation list: 2min


Client-Side Cache


**Desktop:** IndexedDB for message cache (last 7 days), user profiles, team structure — enables offline viewing


**Sync:** Delta sync protocol — client sends last sync token, server returns only new/modified messages


## Scaling & Load Balancing


- **Azure Front Door:** Global L7 load balancer with anycast; routes to nearest healthy Azure region; auto-failover on region outage

- **Cosmos DB:** Multi-region write with automatic conflict resolution (last-writer-wins); auto-scale RU/s per partition

- **Media Processors:** Auto-scaled VM pools per region; GPU-accelerated for video transcoding; cascaded SFUs for cross-region meetings

- **SignalR:** Azure SignalR Service scales to millions of connections per unit; multiple units per region; sharded by chat group

- **Multi-Tenant:** Each M365 tenant is logically isolated — data residency controls (EU data stays in EU Azure regions)


## Tradeoffs


> **

✅ M365 Integration


- Seamless file sharing (SharePoint), calendar (Outlook), identity (Azure AD)

- Single pane of glass for enterprise communication + collaboration

- IT admin can manage everything from M365 admin center


❌ M365 Integration


- Tightly coupled — switching away from M365 ecosystem is very difficult

- Complexity: provisioning a Team creates SharePoint site + AD group + mailbox

- Performance depends on multiple Azure services (cascading failures)


✅ Cosmos DB (Global Distribution)


- Multi-region write with 99.999% availability SLA

- Automatic failover — regional outage is transparent to users

- Single-digit ms reads globally with session consistency


❌ Cosmos DB


- Expensive at scale (RU-based pricing model)

- Partition key design is critical — bad partitioning = hot partitions

- Limited query capabilities vs. traditional SQL databases


## Alternative Approaches


Matrix Protocol (Decentralized)


Open standard for decentralized communication (Element/Matrix). Each org runs own server, federated messaging across servers. Better for data sovereignty and avoiding vendor lock-in. But harder to achieve consistent UX, slower feature development, and no native M365 integration.


XMPP-Based (Legacy)


Extensible Messaging and Presence Protocol — used by earlier enterprise messaging (Jabber, Lync). Proven, standards-based, extensible. But XML-heavy protocol, poor mobile support, and lacks modern features (threaded replies, adaptive cards, app platform).


## Additional Information


- **Teams 2.0 (New Teams):** Rewritten from Electron+Angular to Electron+React (WebView2 + Edge Chromium). 50% less memory, 2x faster startup. Uses IndexedDB for local caching, Web Workers for background processing.

- **Copilot in Teams:** GPT-4 powered meeting summarization, action item extraction, catch-up ("What did I miss?"), intelligent recap. Processes meeting transcripts in real-time via Azure OpenAI Service.

- **PSTN Integration:** Teams can replace traditional phone systems — Direct Routing (SBC gateway to PSTN), Calling Plans (Microsoft as carrier), Operator Connect (telco partnership). SIP trunking for enterprise PBX replacement.

- **Guest Access & Federation:** External users can join teams as guests (Azure AD B2B); federation allows chat/calling with other Teams/Skype orgs without guest accounts. Cross-cloud federation supports GCC/DoD environments.

- **Live Events:** Broadcast meetings to 20K+ attendees; uses Azure Media Services for live encoding + CDN distribution. Attendees receive stream (not interactive media); Q&A via moderated chat.