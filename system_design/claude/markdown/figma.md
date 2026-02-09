# 🎨 Figma - System Design


## Functional Requirements


1. **Real-Time Collaborative Editing:** Multiple users simultaneously edit the same design file with live cursors, selections, and instant sync (<100ms update propagation)

2. **Canvas Rendering:** Render complex vector graphics (paths, gradients, blurs, masks) at 60fps on infinite canvas with GPU acceleration via WebGL/WebGPU

3. **Version History & Branching:** Auto-save versions, named checkpoints, branch/merge workflows for design files, comment threads on specific objects


## Non-Functional Requirements


- **Latency:** Operation propagation <100ms between collaborators (same region), <250ms cross-region

- **Scale:** 4M+ paying users, files with 100K+ objects, 100+ concurrent editors per file

- **Performance:** 60fps rendering for complex designs; canvas zoom/pan at 60fps with 500K+ objects

- **Reliability:** Zero data loss — every edit persisted; conflict-free multi-user editing

- **Offline:** Graceful offline support — queue operations locally, reconcile on reconnect


## Flow 1: Real-Time Collaborative Editing (CRDT-Based)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 370" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#1ABCFE">
</polygon>
</marker>
</defs>


<rect x="20" y="40" width="130" height="50" rx="8" fill="#34A853">
</rect>
<text x="85" y="62" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Editor A
</text>
<text x="85" y="77" text-anchor="middle" fill="#fff" font-size="10">
(Browser/WASM)
</text>


<rect x="20" y="130" width="130" height="50" rx="8" fill="#34A853">
</rect>
<text x="85" y="152" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Editor B
</text>
<text x="85" y="167" text-anchor="middle" fill="#fff" font-size="10">
(Browser/WASM)
</text>


<rect x="210" y="80" width="140" height="55" rx="8" fill="#607D8B">
</rect>
<text x="280" y="102" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
WebSocket
</text>
<text x="280" y="117" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Multiplexer
</text>


<rect x="410" y="80" width="140" height="55" rx="8" fill="#A259FF">
</rect>
<text x="480" y="102" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Collaboration
</text>
<text x="480" y="117" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Server (CRDT)
</text>


<rect x="610" y="50" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="675" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Document Store
</text>
<text x="675" y="87" text-anchor="middle" fill="#fff" font-size="10">
(S3 + Metadata)
</text>


<rect x="610" y="120" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="675" y="142" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Op Log
</text>
<text x="675" y="157" text-anchor="middle" fill="#fff" font-size="10">
(Append-Only)
</text>


<rect x="410" y="210" width="140" height="50" rx="8" fill="#4285F4">
</rect>
<text x="480" y="232" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Presence Service
</text>
<text x="480" y="247" text-anchor="middle" fill="#fff" font-size="10">
(Cursors/Selection)
</text>


<rect x="610" y="210" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="675" y="232" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Snapshot Service
</text>
<text x="675" y="247" text-anchor="middle" fill="#fff" font-size="10">
(Periodic Save)
</text>


<line x1="150" y1="65" x2="205" y2="95" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="150" y1="155" x2="205" y2="120" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="350" y1="107" x2="405" y2="107" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="550" y1="95" x2="605" y2="75" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="550" y1="120" x2="605" y2="140" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="480" y1="135" x2="480" y2="205" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="550" y1="235" x2="605" y2="235" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah)">
</line>


</svg>

</details>


### Steps


1. Editor A opens file → WebSocket connection established to Collaboration Server (sticky to file shard)

2. Server loads latest snapshot from S3 + replays ops from Op Log since snapshot → sends full state to client

3. Client renders document using WebAssembly (Rust-compiled) + WebGL for GPU-accelerated 2D rendering

4. Editor A moves a rectangle → local CRDT generates operation: `{op: "move", node_id: "abc", x: 150, y: 200, lamport: 42}`

5. Operation applied locally immediately (optimistic) and sent to server via WebSocket

6. Collaboration Server receives op, validates, applies to server CRDT state, appends to Op Log

7. Server broadcasts op to Editor B (and all other connected clients) via WebSocket multiplexer

8. Editor B receives op, applies to local CRDT state — no conflicts due to CRDT convergence guarantees

9. Presence Service tracks cursor positions, selections, and active user avatars — broadcast at 10Hz

10. Snapshot Service periodically (every 30s or 50 ops) writes full document state to S3 as checkpoint


### Example


Two designers work on a button component. Editor A changes the fill color to #A259FF while Editor B simultaneously changes the border radius to 8px. Both ops are independent (different properties on different or same node). CRDTs handle both as commutative operations — final state has both the color and border radius change regardless of operation order.


### Deep Dives


CRDT (Conflict-Free Replicated Data Types)


**Type:** Figma uses a custom CRDT optimized for tree-structured documents (not off-the-shelf Yjs/Automerge)


**Operations:** Set property, insert child, delete node, move node, reorder — all designed to be commutative and idempotent


**Conflict Resolution:** Last-Writer-Wins (LWW) for property conflicts using Lamport timestamps + client ID tiebreaker


**Tree CRDT:** Custom tree-move operation prevents cycles and orphans — challenging problem in CRDT literature (Martin Kleppmann's research)


**Compression:** Operations compressed via delta encoding — only changed properties transmitted


WebSocket Multiplexer


**Protocol:** WebSocket (wss://) with custom binary protocol (not JSON — too slow for 60fps updates)


**Multiplexing:** Single WS connection carries: ops, presence (cursors), comments, chat — tagged by channel type


**Compression:** Messages compressed with Brotli; binary serialization using FlatBuffers


**Sticky Sessions:** All editors of same file route to same collaboration server (consistent hashing by file_id)


**Heartbeat:** Ping/pong every 10s; reconnect with delta sync (replay missed ops from Op Log)


Op Log (Append-Only)


**Storage:** Ordered log of all operations per file — enables time-travel and version history


**Format:** Protobuf-encoded operations with Lamport timestamp, client_id, file_id


**Compaction:** Periodic compaction — merge consecutive ops on same node into single state op


**Retention:** Full op history retained for version history; compacted after 30 days for storage efficiency


Presence (Cursors/Selection)


**Data:** Cursor position (x, y), selection bounds, active tool, user avatar/name


**Update Rate:** Throttled to 10 updates/second per user (cursor smoothing on client via interpolation)


**Protocol:** UDP-like unreliable delivery (latest-wins) — stale cursor positions don't need guaranteed delivery


**Fan-out:** Presence updates broadcast to all connected editors of same file


## Flow 2: Canvas Rendering Pipeline


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#0ACF83">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="130" height="50" rx="8" fill="#A259FF">
</rect>
<text x="85" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Scene Graph
</text>
<text x="85" y="97" text-anchor="middle" fill="#fff" font-size="10">
(CRDT State)
</text>


<rect x="200" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="265" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Layout Engine
</text>
<text x="265" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Constraint Solver)
</text>


<rect x="380" y="60" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="445" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Viewport Culling
</text>
<text x="445" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Spatial Index)
</text>


<rect x="560" y="60" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="625" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Tessellation
</text>
<text x="625" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Vectors→Triangles)
</text>


<rect x="740" y="60" width="130" height="50" rx="8" fill="#0ACF83">
</rect>
<text x="805" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
WebGL Renderer
</text>
<text x="805" y="97" text-anchor="middle" fill="#fff" font-size="10">
(GPU Pipeline)
</text>


<line x1="150" y1="85" x2="195" y2="85" stroke="#0ACF83" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="330" y1="85" x2="375" y2="85" stroke="#0ACF83" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="510" y1="85" x2="555" y2="85" stroke="#0ACF83" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="690" y1="85" x2="735" y2="85" stroke="#0ACF83" stroke-width="2" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Steps


1. **Scene Graph:** CRDT state forms a tree of design nodes — frames, rectangles, text, vectors, images, groups, components

2. **Layout Engine:** Auto Layout constraints resolved (Cassowary constraint solver) — parent→child flow, padding, spacing

3. **Viewport Culling:** R-tree spatial index determines which objects are visible in current viewport — skips offscreen objects entirely

4. **Tessellation:** Vector paths (Bézier curves) tessellated into triangle meshes for GPU rendering; cached per zoom level

5. **WebGL Renderer:** Custom fragment/vertex shaders handle fills, strokes, gradients, blurs, masks, blend modes at 60fps

6. **Tile-Based Rendering:** Canvas divided into tiles; only dirty tiles re-rendered on changes (incremental rendering)


### Example


Designer zooms into a 50K-object Figma file. R-tree queries visible viewport → 200 objects visible. Layout engine resolves Auto Layout for 15 frames. Tessellator converts 200 vector objects to ~5K triangles. WebGL draws in single batch call with instanced rendering. Total frame time: ~8ms (120fps capable). Image fills use pre-loaded texture atlas.


### Deep Dives


WebAssembly (WASM) Engine


**Language:** Core rendering and CRDT engine written in Rust, compiled to WebAssembly


**Performance:** Near-native speed (~80-90% of native) — critical for real-time rendering pipeline


**Memory:** WASM linear memory for scene graph — avoids JavaScript GC pauses that would cause frame drops


**Size:** WASM binary ~4MB (compressed ~1.2MB with Brotli) — loaded once, cached via Service Worker


WebGL / WebGPU Rendering


**Current:** WebGL 2.0 — custom shaders for anti-aliased vector rendering (signed distance fields for text)


**Future:** WebGPU migration (compute shaders for blur/effects, better batching, lower driver overhead)


**Batching:** Draw calls batched by material (fill type, blend mode) — reduces GPU state changes


**Texture Atlas:** Image fills packed into texture atlases to minimize GPU texture binds


## Flow 3: Version History & Branching


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FF7262">
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
Version Service
</text>


<rect x="370" y="30" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="435" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Snapshot Store
</text>
<text x="435" y="67" text-anchor="middle" fill="#fff" font-size="10">
(S3)
</text>


<rect x="370" y="100" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="435" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Op Log DB
</text>
<text x="435" y="137" text-anchor="middle" fill="#fff" font-size="10">
(PostgreSQL)
</text>


<rect x="560" y="60" width="130" height="50" rx="8" fill="#A259FF">
</rect>
<text x="625" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Diff Engine
</text>
<text x="625" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Branch/Merge)
</text>


<rect x="740" y="60" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="805" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Thumbnail Gen
</text>
<text x="805" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Headless Render)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#FF7262" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#FF7262" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="95" x2="365" y2="120" stroke="#FF7262" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="55" x2="555" y2="75" stroke="#FF7262" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="125" x2="555" y2="95" stroke="#FF7262" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="690" y1="85" x2="735" y2="85" stroke="#FF7262" stroke-width="2" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Steps


1. Auto-save: Snapshot Service writes full file state to S3 every 30 seconds (or after 50 operations)

2. Named versions: User manually saves checkpoint → creates named snapshot with metadata (title, description)

3. Version history: Client requests timeline → Version Service returns list of snapshots with thumbnails

4. Restore: Loading a past version replays snapshot + ops up to that point; can create new branch from any version

5. Branching: Creates a fork — copies snapshot + op log pointer; parallel editing proceeds independently

6. Merge: Diff Engine compares two branches, identifies conflicts (same node modified in both), presents merge UI

7. Thumbnails generated asynchronously by headless renderer for each snapshot


### Example


Designer creates branch "v2-dark-mode" from main file. Makes extensive changes over 3 days. Team decides to merge back. Diff Engine identifies 45 modified objects — 3 have conflicts (same button modified in both branches). Merge UI shows side-by-side comparison for conflict resolution. After resolving, ops from branch replayed onto main.


### Deep Dives


Snapshot Format


**Format:** Custom binary format (Figma .fig) — scene graph serialized as flat array with parent pointers


**Compression:** Zstandard (zstd) compression — ~5:1 ratio for typical design files


**Storage:** S3 with versioning enabled; each snapshot is immutable blob with content-addressed key


**Size:** Typical file: 1-50MB compressed; complex files with images: 100MB+


Branch Merge Strategy


**Algorithm:** Three-way merge — common ancestor (branch point) vs. main vs. branch


**Conflict:** Same node + same property modified in both → manual resolution required


**Auto-merge:** Non-conflicting changes (different nodes, or different properties on same node) auto-merged


**Structure:** Node additions/deletions handled via tree CRDT semantics (no orphans/cycles)


## Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 480" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#1ABCFE">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="140" height="50" rx="8" fill="#34A853">
</rect>
<text x="90" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Browser Clients
</text>
<text x="90" y="97" text-anchor="middle" fill="#fff" font-size="10">
(WASM + WebGL)
</text>


<rect x="20" y="140" width="140" height="50" rx="8" fill="#34A853">
</rect>
<text x="90" y="162" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Desktop App
</text>
<text x="90" y="177" text-anchor="middle" fill="#fff" font-size="10">
(Electron)
</text>


<rect x="220" y="90" width="140" height="60" rx="8" fill="#FB8C00">
</rect>
<text x="290" y="115" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Edge CDN
</text>
<text x="290" y="132" text-anchor="middle" fill="#fff" font-size="10">
(Cloudflare/Fastly)
</text>


<rect x="420" y="30" width="140" height="50" rx="8" fill="#A259FF">
</rect>
<text x="490" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Collab Server
</text>
<text x="490" y="67" text-anchor="middle" fill="#fff" font-size="10">
(CRDT Engine)
</text>


<rect x="420" y="100" width="140" height="50" rx="8" fill="#4285F4">
</rect>
<text x="490" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
REST API
</text>
<text x="490" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Files, Teams, Auth)
</text>


<rect x="420" y="170" width="140" height="50" rx="8" fill="#4285F4">
</rect>
<text x="490" y="192" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Plugin API
</text>
<text x="490" y="207" text-anchor="middle" fill="#fff" font-size="10">
(Sandboxed Runtime)
</text>


<rect x="620" y="30" width="140" height="50" rx="8" fill="#607D8B">
</rect>
<text x="690" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
WebSocket Hub
</text>
<text x="690" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Presence + Ops)
</text>


<rect x="620" y="100" width="140" height="50" rx="8" fill="#E53935">
</rect>
<text x="690" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Export Service
</text>
<text x="690" y="137" text-anchor="middle" fill="#fff" font-size="10">
(SVG/PNG/PDF)
</text>


<rect x="820" y="30" width="140" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="890" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Thumbnail Gen
</text>
<text x="890" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Headless Render)
</text>


<rect x="420" y="310" width="140" height="55" rx="8" fill="#795548">
</rect>
<text x="490" y="332" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
PostgreSQL
</text>
<text x="490" y="347" text-anchor="middle" fill="#fff" font-size="10">
(Metadata + Auth)
</text>


<rect x="620" y="310" width="140" height="55" rx="8" fill="#795548">
</rect>
<text x="690" y="332" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Amazon S3
</text>
<text x="690" y="347" text-anchor="middle" fill="#fff" font-size="10">
(Snapshots + Images)
</text>


<rect x="820" y="310" width="140" height="55" rx="8" fill="#00897B">
</rect>
<text x="890" y="332" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Redis
</text>
<text x="890" y="347" text-anchor="middle" fill="#fff" font-size="10">
(Sessions + Cache)
</text>


<line x1="160" y1="85" x2="215" y2="110" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="160" y1="165" x2="215" y2="130" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="360" y1="110" x2="415" y2="55" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="360" y1="125" x2="415" y2="125" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="560" y1="55" x2="615" y2="55" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="560" y1="125" x2="615" y2="125" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="760" y1="55" x2="815" y2="55" stroke="#1ABCFE" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="490" y1="150" x2="490" y2="305" stroke="#1ABCFE" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="560" y1="80" x2="620" y2="310" stroke="#1ABCFE" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="690" y1="150" x2="690" y2="305" stroke="#1ABCFE" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="560" y1="140" x2="815" y2="330" stroke="#1ABCFE" stroke-width="1.5" marker-end="url(#ah4)">
</line>


</svg>

</details>


## Database Schema


### PostgreSQL — Metadata

`CREATE TABLE files (
  file_id      UUID PRIMARY KEY,        `PK`
  name         VARCHAR(255),
  team_id      UUID REFERENCES teams,   `FK`
  project_id   UUID,                    `FK`
  created_by   UUID REFERENCES users,   `FK`
  s3_snapshot  TEXT,     -- latest snapshot S3 key
  op_log_seq   BIGINT,  -- latest op sequence number
  thumbnail_url TEXT,
  node_count   INT DEFAULT 0,
  created_at   TIMESTAMPTZ,
  updated_at   TIMESTAMPTZ              `IDX`
);

CREATE TABLE file_versions (
  version_id   UUID PRIMARY KEY,        `PK`
  file_id      UUID REFERENCES files,   `FK`
  name         VARCHAR(255),
  s3_snapshot  TEXT,     -- snapshot S3 key at this version
  op_log_seq   BIGINT,  -- op sequence at this version
  thumbnail_url TEXT,
  created_by   UUID REFERENCES users,   `FK`
  created_at   TIMESTAMPTZ
);
CREATE INDEX idx_versions_file ON file_versions(file_id, created_at DESC);

CREATE TABLE comments (
  comment_id   UUID PRIMARY KEY,        `PK`
  file_id      UUID REFERENCES files,   `FK`
  node_id      VARCHAR(64),   -- design object the comment is attached to
  author_id    UUID REFERENCES users,   `FK`
  body         TEXT,
  resolved     BOOLEAN DEFAULT FALSE,
  x            FLOAT,  y FLOAT,  -- pin position on canvas
  created_at   TIMESTAMPTZ
);
CREATE INDEX idx_comments_file ON comments(file_id, resolved);`


### Op Log (PostgreSQL / DynamoDB)

`CREATE TABLE op_log (
  file_id      UUID,               `PK`
  seq_num      BIGINT,             `PK`
  client_id    UUID,
  lamport_ts   BIGINT,
  op_type      VARCHAR(20),  -- set_prop, insert_child, delete, move
  node_id      VARCHAR(64),
  payload      JSONB,        -- operation-specific data
  created_at   TIMESTAMPTZ,
  PRIMARY KEY (file_id, seq_num)
);`


 `SHARD` Partitioned by file_id — all ops for a file co-located


**Alternative:** DynamoDB with file_id as partition key, seq_num as sort key for infinite scalability


## Cache & CDN Deep Dive


CDN (Static Assets)


**Provider:** Cloudflare / Fastly


**Cached:** WASM binary, JS bundles, fonts, static images, shared component libraries


**Policy:** Immutable hashed filenames with max-age=31536000 (1 year); bust via filename hash change


Image/Asset Cache


**Source:** Design file images stored in S3; thumbnails pre-generated


**Client Cache:** IndexedDB for offline access — stores recently viewed file snapshots + image assets


**Server Cache:** Redis cache for file metadata, team/project structures (TTL: 5min, invalidate on mutation)


Collaboration Cache


**In-Memory:** Active files' CRDT state kept in Collaboration Server memory (~50MB per complex file)


**Eviction:** LRU eviction when no editors connected for 5 minutes; state persisted to S3 before eviction


**Warm-Up:** On first editor connect, load snapshot from S3 + replay ops (typically <500ms)


## Scaling & Load Balancing


- **Collaboration Server:** Horizontally scaled with consistent hashing by file_id — each file pinned to one server (single writer for ordering). If server dies, file reopened on another server and state reconstructed from S3 + Op Log.

- **WebSocket:** L4 load balancer with sticky sessions (IP hash or connection ID) — ensures all ops for a file reach same collab server

- **REST API:** Stateless services behind L7 LB (Envoy); auto-scaled based on request rate

- **Database:** PostgreSQL with read replicas; op log tables partitioned by file_id; S3 for blob storage (effectively unlimited)

- **Multi-Region:** Collaboration servers in US, EU, APAC — file assigned to region of majority of editors; cross-region ops have higher latency


## Tradeoffs


> **

✅ CRDT vs OT (Operational Transformation)


- No central ordering server required — CRDTs converge without coordination

- Better offline support — ops can be applied independently

- Mathematically proven convergence — no edge case bugs


❌ CRDT vs OT


- Higher metadata overhead (vector clocks, tombstones)

- Tombstone accumulation requires periodic garbage collection

- Tree move operations are notoriously complex in CRDT literature


✅ WASM + WebGL (Browser)


- Zero install — runs in any browser, no desktop app required

- Instant updates — deploy new version server-side

- Cross-platform by default (macOS, Windows, Linux, ChromeOS)


❌ WASM + WebGL (Browser)


- Browser sandboxing limits GPU access (no Vulkan/Metal directly)

- 4GB WASM memory limit (until Memory64 proposal ships)

- Font rendering inconsistencies across browsers/OSes


## Alternative Approaches


OT (Google Docs Model)


Operational Transformation with central server for operation ordering. Proven at Google scale (Docs, Sheets). Simpler per-operation, but requires ordered delivery and central coordinator — single point of contention for high-throughput editing.


Native Desktop (Sketch Model)


Native macOS/Windows app using Metal/DirectX directly. 10x rendering performance for complex scenes, full GPU access, no browser limitations. But platform-specific codebases, no browser access, complex update distribution via App Store.


Server-Side Rendering (Miro Model)


Render canvas on server, stream viewport as video/tiles to client. Enables thin clients (any device) and protects IP (design never leaves server). But high server cost (GPU per session), latency-sensitive, poor offline support.


## Additional Information


- **Plugin Sandbox:** Figma plugins run in a sandboxed JavaScript iframe (no DOM access) — communicate with main thread via postMessage API. Prevents plugins from accessing raw design data or crashing the editor.

- **Dev Mode:** Inspection mode generates CSS/Swift/Kotlin code from design objects — layout constraints, colors, typography extracted programmatically from scene graph

- **Component System:** Master component + instances form a prototype chain — instance overrides stored as sparse property deltas on top of master component

- **Variables & Design Tokens:** Variables (colors, spacing, strings) stored as first-class entities in scene graph — referenced by alias, enabling theme switching and multi-mode design tokens

- **Font Loading:** Fonts loaded from Google Fonts CDN or user-uploaded; local fonts accessed via Figma Font Helper (small native daemon that reads system fonts and serves them to the browser)