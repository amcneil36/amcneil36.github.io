# System Design: WhatsApp


## Functional Requirements


  1. **One-on-one messaging** – Users can send and receive text messages in real time with another user.

  2. **Group messaging** – Users can create groups (up to 1024 members) and send/receive messages within the group.

  3. **Media sharing** – Users can send images, videos, audio files, and documents.

  4. **Online/offline & last-seen status** – Users can see whether contacts are online and when they were last active.

  5. **Read receipts** – Sent, delivered, and read indicators (single tick, double tick, blue tick).

  6. **End-to-end encryption** – Messages are encrypted on the sender's device and decrypted only on the receiver's device.

  7. **Push notifications** – Offline users receive push notifications for new messages.

  8. **Message persistence** – Messages are stored until delivered; chat history is available on the device.


## Non-Functional Requirements


  1. **Low latency** – Messages should be delivered in <200ms when both users are online.

  2. **High availability** – 99.99% uptime; the service must always be reachable.

  3. **Scalability** – Support 2 billion+ users and 100 billion+ messages per day.

  4. **Reliability** – No message loss; at-least-once delivery guarantee.

  5. **Consistency** – Eventual consistency is acceptable for status/presence, but message ordering must be maintained per conversation.

  6. **Security** – End-to-end encryption for all messages; secure key exchange.

  7. **Bandwidth efficiency** – Minimize data usage, especially for mobile networks.


## Flow 1: Sending a 1-on-1 Message


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 420" xmlns="http://www.w3.org/2000/svg">

  

  
<rect x="10" y="170" width="100" height="60" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="60" y="205" text-anchor="middle" fill="white" font-size="13" font-weight="bold">
Sender
</text>

  

  
<line x1="110" y1="200" x2="180" y2="200" stroke="#333" stroke-width="2" marker-end="url(#arrow)">
</line>

  
<text x="145" y="190" text-anchor="middle" fill="#555" font-size="10">
WebSocket
</text>

  

  
<rect x="180" y="170" width="100" height="60" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="230" y="200" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Load
</text>

  
<text x="230" y="215" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Balancer
</text>

  

  
<line x1="280" y1="200" x2="350" y2="200" stroke="#333" stroke-width="2" marker-end="url(#arrow)">
</line>

  

  
<rect x="350" y="170" width="120" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="410" y="205" text-anchor="middle" fill="white" font-size="13" font-weight="bold">
Chat Service
</text>

  

  
<line x1="470" y1="200" x2="540" y2="200" stroke="#333" stroke-width="2" marker-end="url(#arrow)">
</line>

  

  
<rect x="540" y="170" width="120" height="60" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="600" y="200" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Message
</text>

  
<text x="600" y="215" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Queue
</text>

  

  
<line x1="660" y1="200" x2="730" y2="200" stroke="#333" stroke-width="2" marker-end="url(#arrow)">
</line>

  

  
<rect x="730" y="170" width="120" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="790" y="195" text-anchor="middle" fill="white" font-size="11" font-weight="bold">
Chat Service
</text>

  
<text x="790" y="210" text-anchor="middle" fill="white" font-size="11" font-weight="bold">
(Receiver's)
</text>

  

  
<line x1="850" y1="200" x2="920" y2="200" stroke="#333" stroke-width="2" marker-end="url(#arrow)">
</line>

  
<text x="885" y="190" text-anchor="middle" fill="#555" font-size="10">
WebSocket
</text>

  

  
<rect x="920" y="170" width="100" height="60" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="970" y="205" text-anchor="middle" fill="white" font-size="13" font-weight="bold">
Receiver
</text>


  

  
<line x1="410" y1="230" x2="410" y2="300" stroke="#333" stroke-width="2" marker-end="url(#arrow)">
</line>

  

  
<ellipse cx="410" cy="310" rx="55" ry="12" fill="#ff5722" stroke="#d84315" stroke-width="2">
</ellipse>

  
<rect x="355" y="310" width="110" height="40" fill="#ff5722" stroke="#d84315" stroke-width="2">
</rect>

  
<ellipse cx="410" cy="350" rx="55" ry="12" fill="#e64a19" stroke="#d84315" stroke-width="2">
</ellipse>

  
<line x1="355" y1="310" x2="355" y2="350" stroke="#d84315" stroke-width="2">
</line>

  
<line x1="465" y1="310" x2="465" y2="350" stroke="#d84315" stroke-width="2">
</line>

  
<text x="410" y="335" text-anchor="middle" fill="white" font-size="11" font-weight="bold">
Message DB
</text>


  

  
<line x1="600" y1="230" x2="600" y2="290" stroke="#333" stroke-width="2" marker-end="url(#arrow)">
</line>

  
<ellipse cx="600" cy="300" rx="60" ry="12" fill="#009688" stroke="#00695c" stroke-width="2">
</ellipse>

  
<rect x="540" y="300" width="120" height="40" fill="#009688" stroke="#00695c" stroke-width="2">
</rect>

  
<ellipse cx="600" cy="340" rx="60" ry="12" fill="#00796b" stroke="#00695c" stroke-width="2">
</ellipse>

  
<line x1="540" y1="300" x2="540" y2="340" stroke="#00695c" stroke-width="2">
</line>

  
<line x1="660" y1="300" x2="660" y2="340" stroke="#00695c" stroke-width="2">
</line>

  
<text x="600" y="323" text-anchor="middle" fill="white" font-size="10" font-weight="bold">
Session Store
</text>

  
<text x="600" y="336" text-anchor="middle" fill="white" font-size="9">
(Redis)
</text>


  

  
<line x1="790" y1="230" x2="790" y2="300" stroke="#333" stroke-width="2" marker-end="url(#arrow)">
</line>

  
<rect x="730" y="300" width="120" height="50" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="790" y="322" text-anchor="middle" fill="white" font-size="11" font-weight="bold">
Push Notif
</text>

  
<text x="790" y="337" text-anchor="middle" fill="white" font-size="11" font-weight="bold">
Service
</text>

  
<text x="790" y="290" text-anchor="middle" fill="#555" font-size="9">
If offline
</text>


  

  
<defs>

    
<marker id="arrow" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto-start-auto">

      
<path d="M 0 0 L 10 5 L 0 10 z" fill="#333">
</path>

    
</marker>

  
</defs>


</svg>

</details>


> **
Example 1: Both users online


Alice opens her chat with Bob and types "Hey, are you free tonight?" and hits send. The WhatsApp client encrypts the message using Bob's public key (E2E encryption) and sends it over the existing **WebSocket connection** to the **Load Balancer**, which routes it to the **Chat Service** instance handling Alice's connection. The Chat Service persists the encrypted message to the **Message DB** (Cassandra) with status=SENT. It then looks up Bob's connection in the **Session Store** (Redis) to find which Chat Service instance Bob is connected to, and publishes the message to the **Message Queue** (Kafka). The Chat Service instance handling Bob's connection consumes the message from the queue and pushes it to Bob over Bob's WebSocket. Bob's client decrypts the message and displays it. A delivery ACK is sent back, updating the message status to DELIVERED in the Message DB. Alice sees two grey ticks.


> **
Example 2: Receiver is offline


Alice sends "Happy Birthday!" to Carol. The Chat Service persists the message to the Message DB and looks up Carol's session in Redis — Carol has no active WebSocket connection. The Chat Service forwards the message to the **Push Notification Service**, which sends a push notification via APNs (iOS) or FCM (Android) to Carol's device. The message remains in the Message DB with status=SENT. When Carol comes back online and re-establishes her WebSocket connection, the Chat Service queries for all undelivered messages for Carol and pushes them over the WebSocket. Carol receives the message, and a DELIVERED ACK updates the DB.


### Deep Dive: Flow 1 Components


> **
WebSocket Connection


WhatsApp uses **WebSocket (WSS)** for persistent, full-duplex communication between clients and servers. The connection is established via an HTTP/1.1 Upgrade request. Once established, both the client and server can push data at any time without polling overhead. Each client maintains exactly one long-lived WebSocket connection. If the connection drops, the client reconnects with exponential backoff.


**Why WebSocket over alternatives:**


  - **Long polling** – Too much overhead for a chat system with billions of messages; each poll is a new HTTP connection.

  - **Server-Sent Events (SSE)** – Unidirectional (server→client only); doesn't support client→server push.

  - **HTTP polling** – Extremely wasteful; most polls return empty responses.


> **
Load Balancer


An L4 (TCP-level) load balancer sits in front of Chat Service instances. Since WebSocket is a stateful protocol, the LB uses **consistent hashing** or **sticky sessions** based on the connection ID to ensure a client's WebSocket is always routed to the same Chat Service instance. Health checks remove unhealthy instances. For scalability, multiple LB layers (DNS-level → L4 → Chat Service) are used.


> **
Chat Service


A stateful service that manages WebSocket connections. Each instance holds up to ~1 million concurrent connections. Responsibilities:


  - Receive messages from senders over WebSocket.

  - Persist messages to Message DB.

  - Route messages to the correct receiver (via Message Queue).

  - Handle ACKs (sent, delivered, read).


**Protocol:** Incoming = WebSocket (WSS); inter-service = gRPC/TCP; DB writes = Cassandra driver over TCP.


> **
Message Queue (Apache Kafka)


Used for decoupling sender-side and receiver-side Chat Service instances. Each user has a dedicated Kafka topic (or partition keyed by user_id). When a message arrives for a user, it's produced to that user's partition. The Chat Service instance holding that user's WebSocket connection is the consumer for that partition. This provides:


  - **Durability** – Messages survive Chat Service crashes.

  - **Ordering** – Per-partition ordering guarantees message order within a conversation.

  - **Decoupling** – Sender's Chat Service doesn't need to know which instance the receiver is on.


> **
Session Store (Redis)


An in-memory key-value store mapping `user_id → {chat_service_instance_id, connection_status, last_seen}`. Used for:


  - Determining whether a user is online.

  - Finding which Chat Service instance holds a user's WebSocket.

  - Storing last-seen timestamps.


**Why Redis:** Sub-millisecond reads, supports TTL for auto-expiry of stale sessions, and cluster mode for horizontal scaling.


> **
Message DB (Apache Cassandra)


A wide-column NoSQL store optimized for high write throughput. Messages are partitioned by `conversation_id` and sorted by `timestamp` within each partition. This allows efficient retrieval of recent messages for a conversation. Cassandra's tunable consistency (typically `QUORUM` writes) ensures durability without sacrificing availability.


> **
Push Notification Service


When the receiver is offline, the Push Notification Service sends a notification via **APNs** (Apple Push Notification service) for iOS or **FCM** (Firebase Cloud Messaging) for Android. The service stores device tokens in a separate DB. The notification payload is minimal (just sender name + "new message") since the actual message content is E2E encrypted and cannot be read by the server.


## Flow 2: Group Messaging


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 500" xmlns="http://www.w3.org/2000/svg">

  

  
<rect x="10" y="200" width="100" height="60" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="60" y="235" text-anchor="middle" fill="white" font-size="13" font-weight="bold">
Sender
</text>

  

  
<line x1="110" y1="230" x2="170" y2="230" stroke="#333" stroke-width="2" marker-end="url(#arrow2)">
</line>

  

  
<rect x="170" y="200" width="120" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="230" y="235" text-anchor="middle" fill="white" font-size="13" font-weight="bold">
Chat Service
</text>

  

  
<line x1="290" y1="230" x2="370" y2="230" stroke="#333" stroke-width="2" marker-end="url(#arrow2)">
</line>

  

  
<rect x="370" y="200" width="120" height="60" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="430" y="228" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Group
</text>

  
<text x="430" y="243" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Service
</text>

  

  
<line x1="490" y1="230" x2="570" y2="230" stroke="#333" stroke-width="2" marker-end="url(#arrow2)">
</line>

  
<text x="530" y="220" text-anchor="middle" fill="#555" font-size="9">
Fan-out
</text>

  

  
<rect x="570" y="200" width="120" height="60" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="630" y="228" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Message
</text>

  
<text x="630" y="243" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Queue
</text>

  

  
<line x1="690" y1="210" x2="780" y2="130" stroke="#333" stroke-width="2" marker-end="url(#arrow2)">
</line>

  
<line x1="690" y1="230" x2="780" y2="230" stroke="#333" stroke-width="2" marker-end="url(#arrow2)">
</line>

  
<line x1="690" y1="250" x2="780" y2="330" stroke="#333" stroke-width="2" marker-end="url(#arrow2)">
</line>

  

  
<rect x="780" y="100" width="100" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="830" y="130" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Member 1
</text>

  
<rect x="780" y="205" width="100" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="830" y="235" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Member 2
</text>

  
<rect x="780" y="310" width="100" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="830" y="340" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Member N
</text>


  

  
<line x1="430" y1="260" x2="430" y2="340" stroke="#333" stroke-width="2" marker-end="url(#arrow2)">
</line>

  
<ellipse cx="430" cy="350" rx="55" ry="12" fill="#ff5722" stroke="#d84315" stroke-width="2">
</ellipse>

  
<rect x="375" y="350" width="110" height="40" fill="#ff5722" stroke="#d84315" stroke-width="2">
</rect>

  
<ellipse cx="430" cy="390" rx="55" ry="12" fill="#e64a19" stroke="#d84315" stroke-width="2">
</ellipse>

  
<line x1="375" y1="350" x2="375" y2="390" stroke="#d84315" stroke-width="2">
</line>

  
<line x1="485" y1="350" x2="485" y2="390" stroke="#d84315" stroke-width="2">
</line>

  
<text x="430" y="375" text-anchor="middle" fill="white" font-size="11" font-weight="bold">
Group DB
</text>


  
<defs>

    
<marker id="arrow2" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto-start-auto">

      
<path d="M 0 0 L 10 5 L 0 10 z" fill="#333">
</path>

    
</marker>

  
</defs>


</svg>

</details>


> **
Example: Group message to "Family" group


Alice sends "Dinner at 7pm" to the "Family" group (5 members). The message goes through her WebSocket to the Chat Service, which forwards it to the **Group Service**. The Group Service looks up the group membership from the **Group DB** (returns user IDs: Bob, Carol, Dave, Eve). It then performs a **fan-out**: for each member, it publishes a copy of the message to the Message Queue on each member's partition. Each member's Chat Service instance consumes the message and pushes it to the member via WebSocket (or triggers a push notification if offline). The message is stored once per group conversation in the Message DB (not duplicated per member).


> **
Group Service


Manages group metadata: creation, member addition/removal, admin roles, group name/avatar. On receiving a group message, it fetches the member list and performs fan-out to the Message Queue. For large groups, fan-out is done asynchronously to avoid blocking.


**Protocol:** Internal gRPC calls from Chat Service. Group DB queries over TCP.


## Flow 3: Media Sharing


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">

  

  
<rect x="10" y="140" width="100" height="60" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="60" y="175" text-anchor="middle" fill="white" font-size="13" font-weight="bold">
Sender
</text>

  

  
<line x1="110" y1="170" x2="200" y2="170" stroke="#333" stroke-width="2" marker-end="url(#arrow3)">
</line>

  
<text x="155" y="160" text-anchor="middle" fill="#555" font-size="10">
HTTPS PUT
</text>

  

  
<rect x="200" y="140" width="120" height="60" rx="8" fill="#00bcd4" stroke="#00838f" stroke-width="2">
</rect>

  
<text x="260" y="168" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Media
</text>

  
<text x="260" y="183" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Service
</text>

  

  
<line x1="320" y1="170" x2="420" y2="170" stroke="#333" stroke-width="2" marker-end="url(#arrow3)">
</line>

  

  
<rect x="420" y="140" width="130" height="60" rx="8" fill="#795548" stroke="#4e342e" stroke-width="2">
</rect>

  
<text x="485" y="168" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Object Storage
</text>

  
<text x="485" y="183" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
(S3)
</text>

  

  
<line x1="550" y1="170" x2="640" y2="170" stroke="#333" stroke-width="2" marker-end="url(#arrow3)">
</line>

  

  
<rect x="640" y="140" width="100" height="60" rx="8" fill="#607d8b" stroke="#37474f" stroke-width="2">
</rect>

  
<text x="690" y="175" text-anchor="middle" fill="white" font-size="13" font-weight="bold">
CDN
</text>


  

  
<line x1="260" y1="200" x2="260" y2="280" stroke="#333" stroke-width="2" marker-end="url(#arrow3)">
</line>

  
<rect x="200" y="280" width="120" height="50" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="260" y="310" text-anchor="middle" fill="white" font-size="13" font-weight="bold">
Chat Service
</text>

  
<text x="260" y="270" text-anchor="middle" fill="#555" font-size="10">
media_url
</text>


  

  
<line x1="690" y1="200" x2="690" y2="280" stroke="#333" stroke-width="2" marker-end="url(#arrow3)">
</line>

  
<rect x="640" y="280" width="100" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="690" y="310" text-anchor="middle" fill="white" font-size="13" font-weight="bold">
Receiver
</text>

  
<text x="690" y="270" text-anchor="middle" fill="#555" font-size="10">
HTTPS GET
</text>


  
<defs>

    
<marker id="arrow3" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto-start-auto">

      
<path d="M 0 0 L 10 5 L 0 10 z" fill="#333">
</path>

    
</marker>

  
</defs>


</svg>

</details>


> **
Example: Sending a photo


Alice takes a photo and sends it to Bob. The WhatsApp client compresses the image, encrypts it with a random AES key (then encrypts that key with Bob's public key), and uploads the encrypted blob to the **Media Service** via **HTTPS PUT**. The Media Service stores it in **S3** (object storage) and returns a media_url. Alice's client then sends a text message via the Chat Service (WebSocket) that contains the media_url and the encrypted AES key. Bob's client receives the message, downloads the encrypted file from the **CDN** (which caches from S3) via HTTPS GET, decrypts it with the AES key (decrypted using Bob's private key), and renders the photo.


> **
Media Service


**Protocol:** HTTPS PUT for uploads, HTTPS GET for downloads (served via CDN).


**Input:** Encrypted binary blob + metadata (file type, size).


**Output:** media_url (CDN-backed URL).


Handles thumbnail generation (for images), video transcoding, and virus scanning. Files are stored in S3 with a 30-day TTL (auto-deleted after delivery confirmation).


> **
CDN (Content Delivery Network)


**Why CDN is appropriate:** Media files (images, videos) are large, static blobs that benefit enormously from edge caching. The CDN reduces latency by serving files from edge nodes close to the receiver. It also offloads bandwidth from origin servers (S3).


**Caching strategy:** Pull-based (lazy loading). The CDN fetches from S3 on the first request for a media_url and caches it at the edge.


**Eviction policy:** LRU (Least Recently Used) – media files not accessed recently are evicted first.


**Expiration policy:** TTL of 7 days at the CDN layer. After 30 days, the origin file is deleted from S3.


**Why pull-based:** Not all media will be downloaded immediately; push-based would waste CDN storage for messages to offline users.


## Overall Combined Flow


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 650" xmlns="http://www.w3.org/2000/svg">

  

  
<rect x="10" y="270" width="90" height="60" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="55" y="305" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Client
</text>


  

  
<line x1="100" y1="300" x2="160" y2="300" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<rect x="160" y="270" width="80" height="60" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="200" y="298" text-anchor="middle" fill="white" font-size="11" font-weight="bold">
Load
</text>

  
<text x="200" y="312" text-anchor="middle" fill="white" font-size="11" font-weight="bold">
Balancer
</text>


  

  
<line x1="240" y1="300" x2="300" y2="300" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<rect x="300" y="270" width="110" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="355" y="305" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Chat Service
</text>


  

  
<line x1="355" y1="270" x2="355" y2="100" stroke="#333" stroke-width="1.5" stroke-dasharray="5,3">
</line>

  
<line x1="355" y1="100" x2="460" y2="100" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<rect x="460" y="75" width="110" height="50" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="515" y="105" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Group Svc
</text>


  

  
<line x1="570" y1="100" x2="620" y2="100" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<ellipse cx="670" cy="90" rx="40" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="630" y="90" width="80" height="30" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="670" cy="120" rx="40" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="630" y1="90" x2="630" y2="120" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="710" y1="90" x2="710" y2="120" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="670" y="110" text-anchor="middle" fill="white" font-size="9" font-weight="bold">
Group DB
</text>


  

  
<line x1="410" y1="300" x2="480" y2="300" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<rect x="480" y="270" width="110" height="60" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="535" y="298" text-anchor="middle" fill="white" font-size="11" font-weight="bold">
Message
</text>

  
<text x="535" y="312" text-anchor="middle" fill="white" font-size="11" font-weight="bold">
Queue (Kafka)
</text>


  

  
<line x1="590" y1="300" x2="660" y2="300" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<rect x="660" y="270" width="110" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="715" y="298" text-anchor="middle" fill="white" font-size="11" font-weight="bold">
Chat Svc
</text>

  
<text x="715" y="312" text-anchor="middle" fill="white" font-size="11" font-weight="bold">
(Receiver)
</text>


  

  
<line x1="770" y1="300" x2="840" y2="300" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<rect x="840" y="270" width="90" height="60" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="885" y="305" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Receiver
</text>


  

  
<line x1="355" y1="330" x2="355" y2="400" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<ellipse cx="355" cy="410" rx="50" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="305" y="410" width="100" height="30" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="355" cy="440" rx="50" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="305" y1="410" x2="305" y2="440" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="405" y1="410" x2="405" y2="440" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="355" y="430" text-anchor="middle" fill="white" font-size="9" font-weight="bold">
Message DB
</text>


  

  
<line x1="535" y1="330" x2="535" y2="400" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<ellipse cx="535" cy="410" rx="50" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="485" y="410" width="100" height="30" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="535" cy="440" rx="50" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="485" y1="410" x2="485" y2="440" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="585" y1="410" x2="585" y2="440" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="535" y="430" text-anchor="middle" fill="white" font-size="9" font-weight="bold">
Session (Redis)
</text>


  

  
<line x1="715" y1="330" x2="715" y2="400" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<rect x="660" y="400" width="110" height="50" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="715" y="425" text-anchor="middle" fill="white" font-size="10" font-weight="bold">
Push Notif Svc
</text>

  
<text x="715" y="388" text-anchor="middle" fill="#555" font-size="9">
If offline
</text>


  

  
<line x1="55" y1="270" x2="55" y2="100" stroke="#333" stroke-width="1.5" stroke-dasharray="5,3">
</line>

  
<line x1="55" y1="100" x2="130" y2="100" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<rect x="130" y="75" width="100" height="50" rx="8" fill="#00bcd4" stroke="#00838f" stroke-width="2">
</rect>

  
<text x="180" y="105" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
Media Svc
</text>

  
<text x="55" y="90" text-anchor="middle" fill="#555" font-size="9">
HTTPS PUT
</text>


  

  
<line x1="180" y1="75" x2="180" y2="30" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<rect x="140" y="0" width="80" height="30" rx="6" fill="#795548" stroke="#4e342e" stroke-width="1.5">
</rect>

  
<text x="180" y="20" text-anchor="middle" fill="white" font-size="10" font-weight="bold">
S3
</text>


  

  
<line x1="220" y1="15" x2="310" y2="15" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<rect x="310" y="0" width="80" height="30" rx="6" fill="#607d8b" stroke="#37474f" stroke-width="1.5">
</rect>

  
<text x="350" y="20" text-anchor="middle" fill="white" font-size="10" font-weight="bold">
CDN
</text>


  

  
<line x1="355" y1="440" x2="355" y2="510" stroke="#333" stroke-width="2" marker-end="url(#arrow4)">
</line>

  
<ellipse cx="355" cy="520" rx="50" ry="10" fill="#3f51b5" stroke="#1a237e" stroke-width="1.5">
</ellipse>

  
<rect x="305" y="520" width="100" height="30" fill="#3f51b5" stroke="#1a237e" stroke-width="1.5">
</rect>

  
<ellipse cx="355" cy="550" rx="50" ry="10" fill="#283593" stroke="#1a237e" stroke-width="1.5">
</ellipse>

  
<line x1="305" y1="520" x2="305" y2="550" stroke="#1a237e" stroke-width="1.5">
</line>

  
<line x1="405" y1="520" x2="405" y2="550" stroke="#1a237e" stroke-width="1.5">
</line>

  
<text x="355" y="540" text-anchor="middle" fill="white" font-size="9" font-weight="bold">
User DB
</text>


  
<defs>

    
<marker id="arrow4" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto-start-auto">

      
<path d="M 0 0 L 10 5 L 0 10 z" fill="#333">
</path>

    
</marker>

  
</defs>


</svg>

</details>


> **
Combined Example 1: Text message in a 1-on-1 chat


Alice types "Are you coming?" and sends it to Bob. Client → WebSocket → Load Balancer → Chat Service → persists to Message DB → checks Session Store (Redis) for Bob → Bob is online → publishes to Kafka (Bob's partition) → Bob's Chat Service instance consumes → pushes to Bob via WebSocket → Bob sees message. ACK flow: Bob's client sends DELIVERED ACK → Chat Service updates Message DB.


> **
Combined Example 2: Photo in a group chat, one member offline


Alice sends a photo to "Work Team" group (3 members: Bob online, Carol offline, Dave online). Client encrypts photo → HTTPS PUT to Media Service → stored in S3 → returns media_url. Client sends message (with media_url + encrypted key) via WebSocket → Chat Service → Group Service fetches member list from Group DB → fan-out to Kafka for Bob, Carol, Dave. Bob and Dave's Chat Service instances consume and push via WebSocket. Carol's instance finds no connection in Session Store → Push Notification Service sends push via FCM. Carol opens the app later, reconnects WebSocket, gets undelivered messages, downloads photo from CDN.


## Database Schema


### NoSQL Tables (Cassandra)


1. messages

  
  
  
  
  
  
  
  
  


| Field             | Type      | Key  | Description                                      |
|-------------------|-----------|------|--------------------------------------------------|
| conversation_id   | UUID      | `PK` | Unique ID for the conversation (1-on-1 or group) |
| message_id        | TimeUUID  | `PK` | Time-based UUID; sorts messages chronologically  |
| sender_id         | UUID      |      | User who sent the message                        |
| encrypted_content | BLOB      |      | E2E encrypted message body                       |
| media_url         | TEXT      |      | URL of media file (nullable)                     |
| message_type      | TEXT      |      | TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT              |
| status            | TEXT      |      | SENT, DELIVERED, READ                            |
| created_at        | TIMESTAMP |      | Server-side timestamp                            |


**Why NoSQL (Cassandra):** Messages are write-heavy (100B+ per day). Cassandra excels at high-throughput writes with linear horizontal scaling. Partition by `conversation_id` ensures all messages in a chat are co-located. Clustering by `message_id` (TimeUUID) provides chronological ordering within a partition, enabling efficient range queries for "load last 50 messages."


**Sharding:** Cassandra natively shards (partitions) data across nodes using consistent hashing on the partition key (`conversation_id`). This distributes load evenly and avoids hot partitions since conversations are naturally distributed.


**Index:** No secondary index needed — primary access pattern is by `conversation_id` + range on `message_id`.


2. conversations

  
  
  
  
  
  
  


| Field                | Type      | Key  | Description                        |
|----------------------|-----------|------|------------------------------------|
| user_id              | UUID      | `PK` | The user                           |
| conversation_id      | UUID      | `PK` | Conversation ID                    |
| last_message_preview | TEXT      |      | Truncated last message (encrypted) |
| last_message_time    | TIMESTAMP |      | Timestamp of last message          |
| unread_count         | INT       |      | Number of unread messages          |
| is_group             | BOOLEAN   |      | Whether this is a group chat       |


**Why NoSQL:** This table is **denormalized** — it duplicates `last_message_preview` and `last_message_time` from the messages table. This avoids an expensive join to display the chat list. Each user has their own row for each conversation, enabling fast "get all conversations for user X" queries. Denormalization is justified by the read-heavy nature of the chat list (opened every time the app launches) vs. the write cost (only updated when a new message arrives).


### SQL Tables (PostgreSQL)


3. users

  
  
  
  
  
  
  
  


| Field        | Type         | Key   | Description               |
|--------------|--------------|-------|---------------------------|
| user_id      | UUID         | `PK`  | Unique user ID            |
| phone_number | VARCHAR(20)  | `IDX` | Phone number (unique)     |
| display_name | VARCHAR(100) |       | User's display name       |
| avatar_url   | TEXT         |       | Profile picture URL       |
| public_key   | TEXT         |       | E2E encryption public key |
| created_at   | TIMESTAMP    |       | Account creation time     |
| last_seen    | TIMESTAMP    |       | Last online timestamp     |


**Why SQL:** User data is relational, relatively small (2B rows), and requires strong consistency (e.g., phone number uniqueness). PostgreSQL provides ACID guarantees for user registration and profile updates.


**Index:** `phone_number` has a **hash index** for O(1) lookups during user registration and contact matching. Hash index is chosen because phone number lookups are always equality checks (no range queries needed).


**Sharding:** Shard by `user_id` using hash-based sharding across PostgreSQL instances. This distributes users evenly.


**Read/Write triggers:** Written to on user registration and profile update. Read on login, contact sync, and message delivery (to look up recipient's public key).


4. groups

  
  
  
  
  
  
  


| Field       | Type         | Key  | Description                            |
|-------------|--------------|------|----------------------------------------|
| group_id    | UUID         | `PK` | Unique group ID                        |
| group_name  | VARCHAR(100) |      | Group display name                     |
| avatar_url  | TEXT         |      | Group avatar                           |
| created_by  | UUID         | `FK` | Creator of the group                   |
| created_at  | TIMESTAMP    |      | Creation time                          |
| max_members | INT          |      | Maximum allowed members (default 1024) |


5. group_members

  
  
  
  
  


| Field     | Type      | Key        | Description                |
|-----------|-----------|------------|----------------------------|
| group_id  | UUID      | `PK`  `FK` | Group reference            |
| user_id   | UUID      | `PK`  `FK` | User reference             |
| role      | ENUM      |            | ADMIN or MEMBER            |
| joined_at | TIMESTAMP |            | When user joined the group |


**Index:** Composite primary key (`group_id`, `user_id`). Additional **B-tree index** on `user_id` for "get all groups for a user" queries.


**Why SQL:** Group membership is inherently relational (many-to-many between users and groups). Requires consistency (can't have duplicate members, role enforcement).


## Cache Deep Dive


> **
Redis – Session/Presence Cache


**Purpose:** Stores user session data: `user_id → {ws_server_id, status: ONLINE/OFFLINE, last_seen}`.


**Caching strategy:** **Write-through** – When a user connects/disconnects, the Chat Service immediately updates Redis. This ensures session data is always fresh.


**Populated by:** WebSocket connect/disconnect events in the Chat Service.


**Eviction policy:** No eviction needed — entries are explicitly deleted on disconnect and have a TTL of 5 minutes as a safety net (in case the Chat Service crashes without cleanup).


**Expiration policy:** TTL = 5 minutes. If a user's WebSocket drops and the Chat Service doesn't clean up, the entry auto-expires. The client reconnects and creates a fresh entry.


**Why write-through:** Presence data must be accurate in real time — stale cache would cause messages to be routed to a dead connection.


> **
Redis – Recent Conversations Cache


**Purpose:** Caches a user's most recent conversations (top 20) to speed up the chat list screen.


**Caching strategy:** **Write-behind (write-back)** – When a new message arrives, the cache is updated asynchronously after the DB write.


**Eviction policy:** LRU (Least Recently Used) – users who haven't opened the app recently are evicted.


**Expiration policy:** TTL = 24 hours. Refreshed on each app open.


## Scaling Considerations


### Load Balancers


Multiple layers of load balancing:


  1. **DNS-level**: GeoDNS routes users to the nearest data center.

  2. **L4 Load Balancer (TCP)**: Distributes WebSocket connections across Chat Service instances using consistent hashing. L4 is chosen over L7 because WebSocket is a persistent TCP connection — L7 (HTTP) load balancing would terminate the connection at the LB, adding latency.

  3. **L7 Load Balancer (HTTPS)**: In front of Media Service and other REST APIs for HTTP-based traffic.


Each Chat Service instance handles ~1M concurrent connections. With 2B users and ~10% concurrency, we need ~200M connections = ~200 Chat Service instances.


### Horizontal Scaling


  - **Chat Service:** Stateless except for WebSocket connections. Add more instances behind the LB.

  - **Kafka:** Add partitions and brokers. Partition by user_id.

  - **Cassandra:** Add nodes; data rebalances automatically via consistent hashing.

  - **Redis:** Redis Cluster mode with sharding across nodes.

  - **PostgreSQL:** Read replicas for user lookups; sharding by user_id for writes.


## Tradeoffs and Deep Dives


> **
Push vs. Pull for message delivery


We chose **push** (server pushes to client via WebSocket) over pull (client polls). Push provides real-time delivery with minimal latency. Pull would require frequent polling intervals, wasting bandwidth and battery. The tradeoff is that push requires maintaining millions of persistent connections, increasing server resource usage.


> **
At-least-once vs. Exactly-once delivery


We use **at-least-once** delivery. The client deduplicates using `message_id`. Exactly-once delivery across distributed systems is extremely expensive (requires two-phase commit or similar). Since messages have unique IDs, client-side deduplication is simple and effective.


> **
Cassandra vs. HBase for messages


Both are viable wide-column stores. We chose Cassandra for its masterless architecture (higher availability), tunable consistency, and simpler operations. HBase offers stronger consistency but depends on HDFS/ZooKeeper, adding operational complexity.


> **
Per-user Kafka topic vs. Per-user partition


Using a per-user topic (2B topics) would overwhelm Kafka's metadata management. Instead, we use a fixed number of partitions (~100K) and hash user_id to a partition. The tradeoff is that messages for different users share a partition, so we need application-level filtering. But Kafka handles this efficiently with consumer groups.


## Alternative Approaches


Alternative 1: XMPP Protocol


WhatsApp originally used XMPP (Extensible Messaging and Presence Protocol). XMPP is an open standard with built-in presence, roster management, and message routing. However, XMPP is XML-based (verbose), and at WhatsApp's scale, a custom binary protocol over WebSocket is more bandwidth-efficient. We didn't choose XMPP because of its overhead and limited control over optimization.


Alternative 2: HTTP Long Polling


Clients would keep an HTTP connection open, and the server responds when a new message is available. This works but has higher overhead than WebSocket (new TCP connection per poll cycle), higher latency, and more server resources. Not chosen because WebSocket is superior for bidirectional real-time communication.


Alternative 3: gRPC bidirectional streaming


gRPC supports bidirectional streaming over HTTP/2, which could replace WebSocket. It provides built-in protobuf serialization (efficient), load balancing support, and TLS. However, gRPC has less mature browser support (relevant for WhatsApp Web) and the ecosystem for persistent chat connections is more proven with WebSocket. This is a viable alternative that could work at scale.


Alternative 4: Server-side message storage (no E2E encryption)


Storing messages in plaintext on the server simplifies search, backup, and moderation. However, privacy is a core value for WhatsApp. E2E encryption ensures even the server operator cannot read messages. The tradeoff is losing server-side search and easy backup, but this is acceptable given the privacy guarantee.


## Additional Information


### End-to-End Encryption (Signal Protocol)


WhatsApp uses the Signal Protocol for E2E encryption. Each user generates an identity key pair, a signed pre-key, and a set of one-time pre-keys. The server stores public keys for initial key exchange (X3DH). For group chats, the Sender Keys protocol is used: the sender encrypts the message once with a symmetric key shared with the group via pairwise encrypted channels.


### Message Ordering


Messages within a conversation are ordered by `message_id` (TimeUUID). In the rare case of clock skew, the server assigns a server-side timestamp. Kafka's per-partition ordering guarantees FIFO delivery within a user's message queue.


### Offline Message Queue


When a user is offline, messages accumulate in Kafka (retained for up to 30 days). When the user reconnects, the Chat Service replays all undelivered messages from Kafka in order. This is more efficient than storing offline messages in a separate database.


### Heartbeat Mechanism


The client sends a heartbeat (ping) every 30 seconds over the WebSocket. If the server doesn't receive a heartbeat within 90 seconds, it marks the user as offline in Redis and closes the connection. This handles ungraceful disconnects (e.g., network loss, app killed).