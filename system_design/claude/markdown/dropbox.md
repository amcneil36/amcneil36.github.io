# System Design: Dropbox (Cloud File Storage & Sync)


## Functional Requirements


  1. **Upload files** – Users upload files from desktop/mobile clients to the cloud.

  2. **Download files** – Users download files from the cloud to any device.

  3. **Sync across devices** – Changes to files on one device are automatically synced to all other linked devices.

  4. **File versioning** – Maintain version history; users can revert to previous versions.

  5. **File sharing** – Users can share files/folders with others via links or direct sharing.

  6. **Offline access** – Users can access cached files offline; changes sync when back online.

  7. **Conflict resolution** – Handle simultaneous edits to the same file from different devices.


## Non-Functional Requirements


  1. **High availability** – 99.99% for reads; eventual consistency for sync.

  2. **Durability** – 99.999999999% (11 9's) durability for stored files.

  3. **Low latency** – Sync within seconds for small files; minimal perceived delay.

  4. **Scalability** – 500M+ users, petabytes of storage.

  5. **Bandwidth efficiency** – Only transfer changed parts of files (delta sync).


## Flow 1: Uploading/Syncing a File


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 450" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a1" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="160" width="100" height="60" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="60" y="185" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Desktop Client
</text>

  
<text x="60" y="200" text-anchor="middle" fill="#fff" font-size="9">
(Watcher)
</text>

  

  
<line x1="110" y1="190" x2="180" y2="190" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<text x="145" y="180" text-anchor="middle" fill="#555" font-size="8">
Chunks
</text>

  
<rect x="180" y="160" width="110" height="60" rx="8" fill="#00bcd4" stroke="#00838f" stroke-width="2">
</rect>

  
<text x="235" y="185" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Chunking
</text>

  
<text x="235" y="200" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Service
</text>

  

  
<line x1="290" y1="180" x2="370" y2="120" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<text x="340" y="138" text-anchor="middle" fill="#555" font-size="8">
Upload chunks
</text>

  
<rect x="370" y="90" width="110" height="55" rx="8" fill="#795548" stroke="#4e342e" stroke-width="2">
</rect>

  
<text x="425" y="115" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Block Storage
</text>

  
<text x="425" y="130" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
(S3)
</text>

  

  
<line x1="290" y1="200" x2="370" y2="260" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<text x="340" y="245" text-anchor="middle" fill="#555" font-size="8">
Update metadata
</text>

  
<rect x="370" y="240" width="110" height="55" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="425" y="263" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Metadata
</text>

  
<text x="425" y="278" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Service
</text>

  

  
<line x1="480" y1="267" x2="560" y2="267" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="610" cy="260" rx="40" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="570" y="260" width="80" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="610" cy="285" rx="40" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="570" y1="260" x2="570" y2="285" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="650" y1="260" x2="650" y2="285" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="610" y="277" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Metadata DB
</text>

  

  
<line x1="425" y1="295" x2="425" y2="360" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="370" y="360" width="110" height="50" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="425" y="383" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Sync Service
</text>

  
<text x="425" y="398" text-anchor="middle" fill="#fff" font-size="9">
(Notify Queue)
</text>

  

  
<line x1="480" y1="385" x2="600" y2="385" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<text x="540" y="375" text-anchor="middle" fill="#555" font-size="8">
Push notification
</text>

  
<rect x="600" y="360" width="110" height="50" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="655" y="383" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Notification
</text>

  
<text x="655" y="398" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Service
</text>

  

  
<line x1="710" y1="385" x2="790" y2="385" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="790" y="360" width="100" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="840" y="383" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Other
</text>

  
<text x="840" y="398" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Devices
</text>


</svg>

</details>


> **
Example: Editing a document


Alice edits a 100MB PowerPoint file on her laptop. The **Desktop Client's file watcher** detects the change. The **Chunking Service** (running locally on Alice's machine) splits the file into 4MB chunks, computes SHA256 hashes for each chunk, and compares with previously uploaded chunks. Only 2 of 25 chunks have changed (delta sync). The client uploads these 2 chunks to **Block Storage (S3)** via HTTPS PUT. It then calls the **Metadata Service** to update the file's metadata: new version number, updated chunk list (replacing 2 chunk hashes), and new modification timestamp. The Metadata Service writes to the **Metadata DB** and publishes a `FILE_UPDATED` event to the **Sync Service**. The Sync Service notifies Alice's other devices (phone, tablet, work laptop) via the **Notification Service** (long polling / WebSocket). Those devices download only the 2 changed chunks and reconstruct the updated file locally.


> **
Example: Conflict resolution


Alice edits `report.docx` on her laptop while simultaneously editing it on her phone (both offline). When both come online, the client that syncs first wins — its version becomes the canonical version. The second client detects a conflict (the server's version is newer than expected). It saves its version as `report (conflicted copy).docx` and downloads the first version. Alice can then manually merge the two.


### Deep Dive: Components


> **
Chunking (Client-side)


Files are split into 4MB chunks using a **rolling hash (Rabin fingerprinting)** for content-defined chunking. This ensures that inserting data at the beginning of a file doesn't cause all chunks to change — only the affected chunks change. Each chunk is identified by its SHA256 hash, enabling **deduplication**: if two users upload the same file, the chunks are stored only once.


> **
WebSocket/Long Polling for Sync Notifications


Desktop clients maintain a **long polling** connection to the Notification Service. When a file change event is published, the Notification Service pushes the update to all connected clients for that user. Long polling is chosen over WebSocket because: (1) clients may be behind corporate firewalls that block WebSocket, (2) reconnection is simpler with long polling. Mobile clients use **push notifications** (APNs/FCM) instead.


> **
Metadata Service


**Protocol:** HTTPS REST API.


  - `PUT /api/v1/files/{file_id}/metadata` – Update file metadata (new version, chunks). Input: `{file_id, version, chunks: [{hash, index, size}], modified_at}`.

  - `GET /api/v1/files/{file_id}/metadata` – Get current file metadata.

  - `GET /api/v1/sync?since={timestamp}` – Get all changes since last sync.


## Flow 2: Downloading/Syncing to Another Device


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 850 250" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a2" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="90" width="100" height="50" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="60" y="112" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Notification
</text>

  
<text x="60" y="127" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
"file changed"
</text>

  
<line x1="110" y1="115" x2="180" y2="115" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<rect x="180" y="90" width="100" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="230" y="112" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Client
</text>

  
<text x="230" y="127" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
(Device B)
</text>

  
<line x1="280" y1="105" x2="360" y2="55" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="330" y="68" text-anchor="middle" fill="#555" font-size="8">
1. Get metadata
</text>

  
<rect x="360" y="30" width="110" height="45" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="415" y="57" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Metadata Svc
</text>

  
<line x1="280" y1="125" x2="360" y2="170" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="330" y="162" text-anchor="middle" fill="#555" font-size="8">
2. Download chunks
</text>

  
<rect x="360" y="150" width="110" height="45" rx="8" fill="#795548" stroke="#4e342e" stroke-width="2">
</rect>

  
<text x="415" y="177" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Block Storage
</text>

  
<line x1="230" y1="140" x2="230" y2="200" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="230" y="195" text-anchor="start" fill="#555" font-size="8">
 3. Reconstruct file
</text>


</svg>

</details>


> **
Example: Syncing to phone


Alice's phone receives a notification that `report.docx` was updated. The client calls the Metadata Service to get the latest chunk list. It compares with the locally cached chunks — 2 chunks are new. It downloads only those 2 chunks from Block Storage, combines with the existing local chunks, and reconstructs the updated file. Total download: 8MB instead of 100MB.


## Overall Combined Flow


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 380" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a3" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="150" width="80" height="45" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="50" y="170" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Device A
</text>

  
<text x="50" y="182" text-anchor="middle" fill="#fff" font-size="8">
(upload)
</text>

  
<line x1="90" y1="172" x2="135" y2="172" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="135" y="150" width="85" height="45" rx="6" fill="#00bcd4" stroke="#00838f" stroke-width="1.5">
</rect>

  
<text x="177" y="177" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Chunker
</text>

  
<line x1="220" y1="162" x2="280" y2="100" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="280" y="75" width="90" height="45" rx="6" fill="#795548" stroke="#4e342e" stroke-width="1.5">
</rect>

  
<text x="325" y="102" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
S3 Blocks
</text>

  
<line x1="220" y1="180" x2="280" y2="230" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="280" y="210" width="100" height="45" rx="6" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="330" y="237" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Metadata Svc
</text>

  
<line x1="380" y1="232" x2="440" y2="232" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<ellipse cx="485" cy="225" rx="35" ry="8" fill="#ff5722" stroke="#d84315" stroke-width="1">
</ellipse>

  
<rect x="450" y="225" width="70" height="16" fill="#ff5722" stroke="#d84315" stroke-width="1">
</rect>

  
<ellipse cx="485" cy="241" rx="35" ry="8" fill="#e64a19" stroke="#d84315" stroke-width="1">
</ellipse>

  
<line x1="450" y1="225" x2="450" y2="241" stroke="#d84315" stroke-width="1">
</line>

  
<line x1="520" y1="225" x2="520" y2="241" stroke="#d84315" stroke-width="1">
</line>

  
<text x="485" y="237" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
Meta DB
</text>

  
<line x1="330" y1="255" x2="330" y2="300" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="280" y="300" width="100" height="40" rx="6" fill="#9c27b0" stroke="#6a1b9a" stroke-width="1.5">
</rect>

  
<text x="330" y="324" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Sync Queue
</text>

  
<line x1="380" y1="320" x2="440" y2="320" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="440" y="300" width="90" height="40" rx="6" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="485" y="324" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Notif Svc
</text>

  
<line x1="530" y1="320" x2="590" y2="320" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<text x="560" y="312" text-anchor="middle" fill="#555" font-size="7">
Long poll/WS
</text>

  
<rect x="590" y="300" width="80" height="40" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="630" y="318" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Device B
</text>

  
<text x="630" y="330" text-anchor="middle" fill="#fff" font-size="8">
(sync)
</text>

  
<line x1="630" y1="300" x2="630" y2="120" stroke="#333" stroke-width="1" stroke-dasharray="4,3">
</line>

  
<line x1="630" y1="120" x2="370" y2="100" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a3)">
</line>

  
<text x="500" y="112" text-anchor="middle" fill="#555" font-size="7">
Download changed chunks
</text>


</svg>

</details>


> **
Combined Example


Device A edits file → Chunker splits into chunks, uploads only changed chunks to S3 → Metadata Svc updates file version in Meta DB → Sync Queue notifies → Notif Svc pushes to Device B via long poll → Device B fetches new metadata → downloads only changed chunks from S3 → reconstructs file locally.


## Database Schema


### SQL (PostgreSQL)

1. files

  
  
  
  
  
  
  
  
  
  
  


| Field           | Type         | Key         | Description                 |
|-----------------|--------------|-------------|-----------------------------|
| file_id         | UUID         | `PK`        | Unique file ID              |
| user_id         | BIGINT       | `FK`  `IDX` | Owner                       |
| file_name       | VARCHAR(255) |             | File name                   |
| file_path       | TEXT         | `IDX`       | Full path in user's Dropbox |
| size_bytes      | BIGINT       |             | File size                   |
| current_version | INT          |             | Latest version number       |
| content_hash    | VARCHAR(64)  |             | SHA256 of entire file       |
| is_directory    | BOOLEAN      |             | Whether this is a folder    |
| created_at      | TIMESTAMP    |             | Creation time               |
| modified_at     | TIMESTAMP    |             | Last modification time      |


**Why SQL:** File metadata is hierarchical (directories), requires ACID for version updates, and involves complex queries (list files in directory, search by name). PostgreSQL handles this well.


**Index:** B-tree on (`user_id`, `file_path`) for efficient directory listings. B-tree is chosen for range queries (list all files under /Documents/).


**Sharding:** By `user_id` — all files for a user are on the same shard for efficient listing.


2. file_chunks

  
  
  
  
  
  


| Field       | Type        | Key        | Description               |
|-------------|-------------|------------|---------------------------|
| file_id     | UUID        | `PK`  `FK` | File reference            |
| version     | INT         | `PK`       | Version number            |
| chunk_index | INT         | `PK`       | Order of chunk in file    |
| chunk_hash  | VARCHAR(64) |            | SHA256 hash (also S3 key) |
| chunk_size  | INT         |            | Chunk size in bytes       |


**Why SQL:** Chunk ordering matters. Need to query all chunks for a file+version efficiently.


3. file_versions

  
  
  
  
  
  


| Field       | Type      | Key        | Description               |
|-------------|-----------|------------|---------------------------|
| file_id     | UUID      | `PK`  `FK` | File reference            |
| version     | INT       | `PK`       | Version number            |
| modified_by | BIGINT    | `FK`       | Who made this version     |
| size_bytes  | BIGINT    |            | File size at this version |
| modified_at | TIMESTAMP |            | Modification time         |


## Cache Deep Dive


> **
Metadata Cache (Redis)


**Purpose:** Cache file metadata for frequently accessed files. Key: `file:{file_id}:meta`.


**Strategy:** **Write-through** — every metadata update writes to both DB and Redis. Ensures clients always get fresh metadata during sync.


**Eviction:** LRU. **Expiration:** TTL = 1 hour.


> **
CDN


CDN is **appropriate for shared file downloads**. When a file is shared publicly (via link), the CDN caches the file at edge locations. For private files (most of Dropbox's use case), CDN is less useful because each user's files are unique and private. We use CDN selectively for shared links with high download counts.


## Scaling Considerations


  - **Load Balancer:** L7 LB for API traffic. Sticky sessions based on user_id for metadata caching efficiency.

  - **Block Storage (S3):** Scales infinitely. Deduplication reduces storage: if 100 users upload the same PDF, only one copy of each chunk is stored.

  - **Metadata DB:** Sharded by user_id. Read replicas for listing queries.

  - **Sync Service:** Each Notification Service instance holds long-poll connections for a subset of users. Scale horizontally with consistent hashing on user_id.


## Tradeoffs and Deep Dives


> **
Delta Sync vs. Full File Upload


Delta sync (only upload changed chunks) saves bandwidth dramatically — editing 1 byte of a 100MB file uploads only 4MB (1 chunk) instead of 100MB. The tradeoff is client-side complexity: the chunking algorithm (Rabin fingerprinting) uses CPU, and the client must maintain chunk hashes. Worth it for the bandwidth savings.


> **
Long Polling vs. WebSocket for Sync


Long polling is simpler and works through firewalls/proxies. WebSocket would be more efficient for high-frequency changes. Dropbox chose long polling historically because of firewall compatibility. A modern implementation might use WebSocket with long polling fallback.


## Alternative Approaches


Peer-to-Peer Sync (like Resilio Sync)


Sync files directly between devices without a cloud server. Lower latency on LAN, no storage costs. Not chosen because: (1) requires both devices to be online simultaneously, (2) no cloud backup, (3) harder to share with others.


CRDT-based Conflict Resolution


Use Conflict-free Replicated Data Types to automatically merge concurrent edits. Works great for text files but not for binary files (PowerPoint, images). Not chosen because most Dropbox files are binary.


## Additional Information


### Deduplication Savings


Content-defined chunking + SHA256 hashing enables cross-user deduplication. In practice, this saves 30-50% of storage. Popular files (OS installers, stock images) may have thousands of users but only one copy of each chunk.


### Bandwidth Throttling


The client limits upload/download bandwidth to avoid saturating the user's network. Default: 75% of available bandwidth. User-configurable.