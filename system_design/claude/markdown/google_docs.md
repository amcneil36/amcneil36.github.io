# 📝 Google Docs — Real-Time Collaborative Document Editor


Design a real-time collaborative document editing system where multiple users can simultaneously edit the same document with instant synchronization, conflict resolution, and full revision history.


## 📋 Functional Requirements


- **Real-time collaboration** — multiple users edit simultaneously with cursors/selections visible to all

- **Conflict resolution** — concurrent edits on same text resolved automatically without data loss

- **Rich text editing** — bold, italic, headings, lists, tables, images, comments, suggestions

- **Version history** — full revision history with ability to restore any previous version

- **Offline editing** — edit without internet, sync changes when reconnected

- **Sharing & permissions** — owner, editor, commenter, viewer roles with link sharing

- **Comments & suggestions** — inline comments, suggested edits with accept/reject workflow


## 📋 Non-Functional Requirements


- **Latency** — edits visible to collaborators within 100-200ms

- **Consistency** — all clients converge to same document state (strong eventual consistency)

- **Scale** — billions of documents, up to 200 concurrent editors per document

- **Durability** — zero data loss; every keystroke persisted

- **Availability** — 99.99% uptime globally

- **Storage efficiency** — billions of documents with full revision history stored compactly


## 🔄 Flow 1: Real-Time Collaborative Editing (OT)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 340" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#4285F4">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="70" text-anchor="middle" fill="white" font-size="12">
Client A
</text>
<text x="75" y="85" text-anchor="middle" fill="white" font-size="10">
(types "Hello")
</text>


<rect x="20" y="160" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="180" text-anchor="middle" fill="white" font-size="12">
Client B
</text>
<text x="75" y="195" text-anchor="middle" fill="white" font-size="10">
(types "World")
</text>


<rect x="220" y="100" width="140" height="60" rx="8" fill="#6A1B9A">
</rect>
<text x="290" y="125" text-anchor="middle" fill="white" font-size="12">
WebSocket
</text>
<text x="290" y="140" text-anchor="middle" fill="white" font-size="10">
Connection
</text>


<rect x="440" y="100" width="160" height="60" rx="8" fill="#1565C0">
</rect>
<text x="520" y="120" text-anchor="middle" fill="white" font-size="12">
Collaboration Server
</text>
<text x="520" y="140" text-anchor="middle" fill="white" font-size="10">
(OT Transform Engine)
</text>


<rect x="680" y="70" width="140" height="50" rx="8" fill="#4E342E">
</rect>
<text x="750" y="100" text-anchor="middle" fill="white" font-size="12">
Document Store
</text>


<rect x="680" y="160" width="140" height="50" rx="8" fill="#00695C">
</rect>
<text x="750" y="180" text-anchor="middle" fill="white" font-size="12">
Operation Log
</text>
<text x="750" y="195" text-anchor="middle" fill="white" font-size="10">
(append-only)
</text>


<rect x="440" y="250" width="160" height="50" rx="8" fill="#E65100">
</rect>
<text x="520" y="280" text-anchor="middle" fill="white" font-size="12">
Presence Service
</text>


<line x1="130" y1="75" x2="215" y2="115" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="130" y1="185" x2="215" y2="145" stroke="#34A853" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="360" y1="130" x2="435" y2="130" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="600" y1="115" x2="675" y2="95" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="600" y1="145" x2="675" y2="185" stroke="#FF9800" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="520" y1="160" x2="520" y2="245" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<text x="420" y="95" fill="#aaa" font-size="10">
ops stream
</text>


<text x="640" y="90" fill="#aaa" font-size="10">
persist snapshot
</text>


<text x="640" y="175" fill="#aaa" font-size="10">
append ops
</text>


</svg>

</details>


**Step 1:** Clients A and B connect via WebSocket to Collaboration Server for the same document
**Step 2:** Each keystroke generates an operation: {type: "insert", position: 5, chars: "H", revision: 42}
**Step 3:** Server receives ops, applies OT (Operational Transformation) to resolve conflicts
**Step 4:** Transformed ops broadcast to all other clients; each client applies transformed op locally
**Step 5:** Operations appended to immutable Operation Log; document snapshot periodically saved

Deep Dive: Operational Transformation (OT)

How OT Resolves Concurrent Edits
`// Problem: Client A inserts "X" at position 3
//          Client B deletes char at position 1
//          Both see initial document: "ABCDE"

// Without OT: applying both literally produces different results!
// Client A sees: "ABCXDE" → delete pos 1 → "BCXDE" ✗
// Client B sees: "ACDE"   → insert X at 3 → "ACDXE" ✗

// With OT: Transform(Op_A, Op_B) adjusts positions
// Op_A: insert("X", 3), Op_B: delete(1)
// Transform: since B deleted before A's insert position,
//   A' = insert("X", 2)  (position shifted left by 1)
//   B' = delete(1)        (unaffected by insert after it)
// Both converge to: "ACXDE" ✓

// OT Transform function (simplified):
function transform(op1, op2) {
  if (op1.type === 'insert' && op2.type === 'insert') {
    if (op1.pos < op2.pos || (op1.pos === op2.pos && op1.clientId < op2.clientId)) {
      return [op1, {...op2, pos: op2.pos + op1.chars.length}];
    } else {
      return [{...op1, pos: op1.pos + op2.chars.length}, op2];
    }
  }
  if (op1.type === 'insert' && op2.type === 'delete') {
    if (op1.pos <= op2.pos) {
      return [op1, {...op2, pos: op2.pos + op1.chars.length}];
    } else {
      return [{...op1, pos: op1.pos - 1}, op2];
    }
  }
  // ... handle all (insert, delete) × (insert, delete) combinations
}

// Google Docs uses centralized OT:
// Server is the single source of truth (total ordering)
// Server revision number prevents divergence
// Client buffers ops while awaiting server acknowledgment`


Deep Dive: OT vs CRDT


| Aspect             | OT (Google Docs)                | CRDT (Yjs, Automerge)           |
|--------------------|---------------------------------|---------------------------------|
| Server requirement | Central server required         | Peer-to-peer possible           |
| Complexity         | Transform functions are complex | Data structure is complex       |
| Consistency        | Server-ordered (total order)    | Mathematically convergent       |
| Offline            | Buffer ops, sync on reconnect   | Natively supports offline       |
| Storage            | Compact (ops are small)         | Larger (metadata per character) |
| Proven scale       | Google Docs (billions of docs)  | Figma, newer systems            |
| Latency            | Server round-trip needed        | Instant local apply             |


`// CRDT example: Each character has a unique ID
// Insert "H" → {id: "A:1", char: "H", after: "root"}
// Insert "i" → {id: "A:2", char: "i", after: "A:1"}
// Concurrent insert by B after same position:
// Insert "X" → {id: "B:1", char: "X", after: "root"}
// Resolution: compare IDs (deterministic ordering)
// Result: either "HXi" or "XHi" — same on all clients

// Google chose OT because:
// 1. Proven at scale (20+ years)
// 2. Central server simplifies access control
// 3. Smaller on-wire format than CRDT metadata`


## 🔄 Flow 2: Document Sharing & Access Control


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 280" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#EA4335">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="90" text-anchor="middle" fill="white" font-size="12">
Owner
</text>


<rect x="175" y="60" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="240" y="80" text-anchor="middle" fill="white" font-size="12">
Sharing Service
</text>


<rect x="350" y="30" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="415" y="60" text-anchor="middle" fill="white" font-size="12">
ACL Store
</text>


<rect x="350" y="100" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="415" y="130" text-anchor="middle" fill="white" font-size="12">
Link Token Service
</text>


<rect x="550" y="60" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="615" y="80" text-anchor="middle" fill="white" font-size="12">
Notification
</text>
<text x="615" y="95" text-anchor="middle" fill="white" font-size="10">
Service
</text>


<rect x="730" y="60" width="120" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="790" y="90" text-anchor="middle" fill="white" font-size="12">
Invitee
</text>


<line x1="130" y1="85" x2="170" y2="85" stroke="#EA4335" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="305" y1="70" x2="345" y2="55" stroke="#EA4335" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="305" y1="100" x2="345" y2="125" stroke="#EA4335" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="480" y1="55" x2="545" y2="75" stroke="#EA4335" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="680" y1="85" x2="725" y2="85" stroke="#EA4335" stroke-width="2" marker-end="url(#a2)">
</line>


</svg>

</details>


**Step 1:** Owner shares document by entering email addresses or generating shareable link
**Step 2:** Sharing Service creates ACL entries (user → role mapping) and optional link tokens
**Step 3:** Link sharing options: "Anyone with link" (viewer/commenter/editor) or restricted to specific users
**Step 4:** On every document access, ACL checked: owner > editor > commenter > viewer
**Step 5:** Notification Service sends email invite with direct link to document


## 🔄 Flow 3: Version History & Restore


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 260" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#34A853">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="90" text-anchor="middle" fill="white" font-size="12">
User
</text>


<rect x="175" y="60" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="240" y="80" text-anchor="middle" fill="white" font-size="12">
Version Service
</text>


<rect x="350" y="30" width="140" height="50" rx="8" fill="#4E342E">
</rect>
<text x="420" y="50" text-anchor="middle" fill="white" font-size="12">
Operation Log
</text>
<text x="420" y="65" text-anchor="middle" fill="white" font-size="10">
(all edits since creation)
</text>


<rect x="350" y="100" width="140" height="50" rx="8" fill="#00695C">
</rect>
<text x="420" y="120" text-anchor="middle" fill="white" font-size="12">
Snapshot Store
</text>
<text x="420" y="135" text-anchor="middle" fill="white" font-size="10">
(periodic checkpoints)
</text>


<rect x="550" y="60" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="615" y="80" text-anchor="middle" fill="white" font-size="12">
Diff Engine
</text>
<text x="615" y="95" text-anchor="middle" fill="white" font-size="10">
(visual diff)
</text>


<line x1="130" y1="85" x2="170" y2="85" stroke="#34A853" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="305" y1="70" x2="345" y2="55" stroke="#34A853" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="305" y1="100" x2="345" y2="125" stroke="#34A853" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="490" y1="80" x2="545" y2="80" stroke="#34A853" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


**Step 1:** User opens version history panel. Version Service retrieves revision timeline
**Step 2:** For any point in time: load nearest snapshot + replay ops from Operation Log to reconstruct document
**Step 3:** Diff Engine highlights changes between versions with color-coded per-author annotations
**Step 4:** "Named versions" let users bookmark important states. Auto-grouping: batch edits within session into one revision
**Step 5:** Restore creates new version (non-destructive) — old history preserved

Deep Dive: Operation Log & Snapshot Strategy

`// Storing every keystroke for billions of documents:
// Operation Log: append-only, immutable, compressed

// Storage optimization:
// 1. Operation compaction: merge sequential inserts by same user
//    "H" + "e" + "l" + "l" + "o" → insert("Hello", pos: 0)
// 2. Periodic snapshots: full document state every N ops (e.g., 1000)
// 3. To reconstruct any version: find nearest snapshot, replay remaining ops

// Snapshot strategy:
// - Every 1000 operations OR every 30 minutes (whichever first)
// - Snapshot stored as compressed JSON/protobuf
// - Old snapshots: keep every 1000th, discard intermediate
// - Result: O(1) storage per time period, not O(edits)

// Example timeline:
// t=0:    Snapshot S0 (empty doc)
// t=1-999: Operations O1...O999
// t=1000:  Snapshot S1 (current state)
// t=1001-1999: Operations O1001...O1999
// t=2000:  Snapshot S2

// To reconstruct version at t=1500:
// Load S1, replay O1001...O1500 → document at t=1500
// Max 999 ops to replay (fast reconstruction)

// Garbage collection: after 30 days, compact ops into fewer snapshots
// Legal hold: some orgs require full edit history for compliance`


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 520" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="ac" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#4285F4">
</polygon>
</marker>
</defs>


<text x="550" y="25" text-anchor="middle" fill="#4285F4" font-size="16" font-weight="bold">
Google Docs Architecture
</text>


<rect x="20" y="50" width="120" height="40" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="75" text-anchor="middle" fill="white" font-size="11">
Web / Mobile Clients
</text>


<rect x="200" y="50" width="130" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="265" y="75" text-anchor="middle" fill="white" font-size="11">
WebSocket Gateway
</text>


<rect x="400" y="40" width="140" height="35" rx="8" fill="#1565C0">
</rect>
<text x="470" y="63" text-anchor="middle" fill="white" font-size="11">
Collaboration Server (OT)
</text>


<rect x="400" y="85" width="140" height="35" rx="8" fill="#1565C0">
</rect>
<text x="470" y="108" text-anchor="middle" fill="white" font-size="11">
Document Service
</text>


<rect x="400" y="130" width="140" height="35" rx="8" fill="#1565C0">
</rect>
<text x="470" y="153" text-anchor="middle" fill="white" font-size="11">
Version Service
</text>


<rect x="400" y="175" width="140" height="35" rx="8" fill="#1565C0">
</rect>
<text x="470" y="198" text-anchor="middle" fill="white" font-size="11">
Sharing / ACL Service
</text>


<rect x="400" y="220" width="140" height="35" rx="8" fill="#1565C0">
</rect>
<text x="470" y="243" text-anchor="middle" fill="white" font-size="11">
Presence Service
</text>


<rect x="400" y="265" width="140" height="35" rx="8" fill="#1565C0">
</rect>
<text x="470" y="288" text-anchor="middle" fill="white" font-size="11">
Comment Service
</text>


<rect x="620" y="50" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="690" y="75" text-anchor="middle" fill="white" font-size="11">
Spanner (Metadata)
</text>


<rect x="620" y="110" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="690" y="135" text-anchor="middle" fill="white" font-size="11">
Colossus (Operation Log)
</text>


<rect x="620" y="170" width="140" height="40" rx="8" fill="#00695C">
</rect>
<text x="690" y="195" text-anchor="middle" fill="white" font-size="11">
Bigtable (Snapshots)
</text>


<rect x="620" y="230" width="140" height="40" rx="8" fill="#00695C">
</rect>
<text x="690" y="255" text-anchor="middle" fill="white" font-size="11">
Redis (Presence Cache)
</text>


<rect x="620" y="290" width="140" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="690" y="315" text-anchor="middle" fill="white" font-size="11">
Pub/Sub (Real-Time)
</text>


<rect x="850" y="50" width="140" height="40" rx="8" fill="#607D8B">
</rect>
<text x="920" y="75" text-anchor="middle" fill="white" font-size="11">
Google Drive (Storage)
</text>


<rect x="850" y="110" width="140" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="920" y="135" text-anchor="middle" fill="white" font-size="11">
Search Indexer
</text>


<rect x="850" y="170" width="140" height="40" rx="8" fill="#E65100">
</rect>
<text x="920" y="195" text-anchor="middle" fill="white" font-size="11">
Export Service (PDF/DOCX)
</text>


<line x1="140" y1="70" x2="195" y2="70" stroke="#4285F4" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="330" y1="70" x2="395" y2="57" stroke="#4285F4" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="540" y1="57" x2="615" y2="70" stroke="#4285F4" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="540" y1="102" x2="615" y2="130" stroke="#4285F4" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="540" y1="147" x2="615" y2="130" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="540" y1="192" x2="615" y2="70" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="540" y1="237" x2="615" y2="255" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="760" y1="70" x2="845" y2="70" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="760" y1="130" x2="845" y2="130" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="760" y1="195" x2="845" y2="195" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


</svg>

</details>


## 💾 Database Schema


### Spanner — Document Metadata

`CREATE TABLE documents (
  doc_id STRING(36) NOT NULL, --  `PK`
  title STRING(500),
  owner_id STRING(36) NOT NULL, --  `IDX`
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  current_revision INT64 NOT NULL DEFAULT 0,
  last_snapshot_revision INT64,
  is_deleted BOOL DEFAULT false,
  storage_bucket STRING(50)
) PRIMARY KEY (doc_id);

CREATE TABLE document_acl (
  doc_id STRING(36) NOT NULL, --  `FK`
  principal_id STRING(100) NOT NULL, -- user email or group
  role STRING(20) NOT NULL, -- 'owner'|'editor'|'commenter'|'viewer'
  granted_by STRING(36),
  granted_at TIMESTAMP,
) PRIMARY KEY (doc_id, principal_id),
  INTERLEAVE IN PARENT documents ON DELETE CASCADE;

CREATE TABLE document_links (
  doc_id STRING(36) NOT NULL,
  link_token STRING(64) NOT NULL, --  `PK`
  access_level STRING(20) NOT NULL,
  expires_at TIMESTAMP,
) PRIMARY KEY (link_token);
--  `IDX`: (doc_id) for listing links per doc`


### Bigtable — Operations & Snapshots

`// Operation Log (append-only, per document)
Row Key: {doc_id}#{revision_number (zero-padded)}
Column Family "op": {
  "type": "insert" | "delete" | "format",
  "position": 1234,
  "content": "Hello",
  "author_id": "user_abc",
  "timestamp": 1707500000000,
  "client_revision": 41
}

// Example rows for doc "doc_123":
// doc_123#000000001 → insert("H", 0, user_a)
// doc_123#000000002 → insert("i", 1, user_a)
// doc_123#000000003 → insert("!", 0, user_b)
// doc_123#000000004 → delete(2, 1, user_a)

// Snapshots (periodic checkpoints)
Row Key: {doc_id}#snapshot#{revision_number}
Column Family "state": {
  "content": compressed_document_json,
  "revision": 1000,
  "timestamp": 1707400000000,
  "size_bytes": 45230
}

// Efficient range scan: get ops 1001-1500 for doc_123
// Scan: start="doc_123#000001001", end="doc_123#000001500"
// Bigtable is perfect: sorted by row key, fast range scans`


## ⚡ Cache & CDN Deep Dive


| Layer             | What                                     | Strategy                                  | TTL               |
|-------------------|------------------------------------------|-------------------------------------------|-------------------|
| Client Memory     | Current document state + pending ops     | In-memory, updated via WebSocket          | Until tab closed  |
| Redis — Presence  | Active cursors, selections, online users | Pub/Sub + ephemeral keys                  | 30s heartbeat TTL |
| Redis — Doc Cache | Hot document snapshots                   | LRU, write-through on snapshot            | 1 hour            |
| Memcache — ACL    | Permission lookups                       | Write-through, invalidate on share change | 5 minutes         |
| CDN               | Static editor JS/CSS, fonts, images      | Immutable versioned URLs                  | 1 year            |


WebSocket Connection Management
`// Each document session maintains WebSocket to Collaboration Server
// Connection protocol:
// 1. Client connects with doc_id + auth token
// 2. Server sends current document state (or last known revision)
// 3. Bidirectional op streaming begins

// Presence protocol (lightweight):
// Client sends cursor position every 50ms (throttled)
// Server fans out to all other clients via Pub/Sub
// Presence data NOT persisted (ephemeral Redis keys with 30s TTL)
// Heartbeat: client pings every 10s, server removes on timeout

// Reconnection: client stores last confirmed revision
// On reconnect: request ops since last_revision
// If gap > threshold: full document re-fetch`


## 📈 Scaling Considerations


- **Document partitioning:** each document assigned to one Collaboration Server instance (consistent hashing on doc_id). All ops for a doc processed by single server (avoids distributed OT). If server dies, another takes over from operation log.

- **Hot documents:** a viral document with 200 editors generates 1000s of ops/sec. Solution: batch ops into micro-batches (every 50ms), transform batch together. Presence updates throttled to 5Hz.

- **WebSocket scaling:** each server holds ~100K WebSocket connections. Horizontally scale connection servers. Pub/Sub (Google Cloud Pub/Sub) fans out ops to all connection servers for a document.

- **Storage:** billions of documents × full revision history. Compression: ops are small (avg ~50 bytes). Snapshots compressed with zstd. Cold documents (not edited in 30 days): compact ops → single snapshot.


## ⚖️ Tradeoffs


> **
Centralized OT (Server-Ordered)
- Strong consistency guarantee
- Simpler client implementation
- Server controls access + ordering


Centralized OT (Server-Ordered)
- Server is bottleneck per document
- Network latency on every edit
- Complex transform function maintenance


Append-Only Operation Log
- Perfect audit trail (every edit preserved)
- Easy version reconstruction
- Natural event sourcing pattern


Append-Only Operation Log
- Grows unboundedly without compaction
- Reconstruction from ops is slow without snapshots
- Storage cost for rarely-accessed docs


## 🔄 Alternative Approaches


CRDT-Based (Yjs / Automerge)

CRDTs (Conflict-free Replicated Data Types) mathematically guarantee convergence without a central server. Each character has a unique ID and position in a partial order. Better for offline-first (Figma uses CRDTs). Trade-off: higher metadata overhead per character (~2-4x storage).

Differential Sync (Neil Fraser)

Periodically diff client state vs server state (using Myers diff algorithm). Simpler than OT but less granular — batches changes between sync cycles. Used by earlier collaborative tools. Not suitable for real-time character-by-character sync.


## 📚 Additional Information


- **Jupiter protocol:** Google's internal OT implementation (used in Docs). Uses a state-space graph where each axis represents one client's operations. Traversal from any point to any other always produces the same result (convergence proof).

- **Rich text as operations:** formatting is also an operation: format(range: [5,10], bold: true). OT must transform formatting ops against insert/delete ops (e.g., if text inserted within formatted range, extend format range).

- **Offline editing:** client queues ops locally (IndexedDB). On reconnect, sends all buffered ops. Server transforms against any concurrent ops that arrived from other clients. Can result in surprising merges for long offline periods.

- **Smart Compose / AI:** predictive text suggestions while typing (using LLM). Suggestions are "phantom ops" — not committed until user accepts. Must not interfere with OT stream from other collaborators.