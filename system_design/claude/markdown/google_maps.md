# 🗺️ Google Maps — Navigation & Geospatial Platform


Design a mapping platform supporting map rendering, place search, routing/navigation with real-time traffic, Street View, and location-based services for 1B+ monthly active users.


## 📋 Functional Requirements


- **Map rendering** — render vector/raster map tiles at 23 zoom levels with real-time updates

- **Place search** — search for businesses, addresses, landmarks with autocomplete

- **Routing & navigation** — turn-by-turn directions for driving, walking, transit, cycling with ETA

- **Real-time traffic** — live traffic conditions from GPS probes (anonymized phone data)

- **Street View** — 360° panoramic imagery navigable at street level

- **ETA prediction** — accurate arrival time considering traffic, road conditions, historical patterns

- **Offline maps** — download map regions for offline navigation


## 📋 Non-Functional Requirements


- **Scale** — 1B+ MAU, 1B+ km of roads mapped, 200M+ businesses

- **Tile latency** — <100ms to serve map tiles (CDN-cached)

- **Routing latency** — <500ms for route computation (continent-spanning routes)

- **Traffic freshness** — live traffic updated every 1-2 minutes from probe data

- **Global coverage** — 220+ countries, terrain, satellite, transit data

- **Storage** — petabytes of map data (vector, imagery, Street View, elevation)


## 🔄 Flow 1: Map Tile Rendering & Display


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 300" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#34A853">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="90" text-anchor="middle" fill="white" font-size="12">
Client App
</text>


<rect x="175" y="60" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="240" y="90" text-anchor="middle" fill="white" font-size="12">
CDN Edge
</text>


<rect x="350" y="60" width="140" height="50" rx="8" fill="#1565C0">
</rect>
<text x="420" y="80" text-anchor="middle" fill="white" font-size="12">
Tile Server
</text>


<rect x="540" y="30" width="140" height="50" rx="8" fill="#4E342E">
</rect>
<text x="610" y="50" text-anchor="middle" fill="white" font-size="12">
Vector Tile DB
</text>
<text x="610" y="65" text-anchor="middle" fill="white" font-size="10">
(pre-computed)
</text>


<rect x="540" y="100" width="140" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="610" y="120" text-anchor="middle" fill="white" font-size="12">
Real-Time Layer
</text>
<text x="610" y="135" text-anchor="middle" fill="white" font-size="10">
(traffic overlay)
</text>


<rect x="740" y="60" width="130" height="50" rx="8" fill="#00695C">
</rect>
<text x="805" y="80" text-anchor="middle" fill="white" font-size="12">
Map Data
</text>
<text x="805" y="95" text-anchor="middle" fill="white" font-size="10">
Pipeline
</text>


<line x1="130" y1="85" x2="170" y2="85" stroke="#34A853" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="305" y1="85" x2="345" y2="85" stroke="#34A853" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="70" x2="535" y2="55" stroke="#34A853" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="100" x2="535" y2="125" stroke="#FF9800" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="680" y1="55" x2="735" y2="75" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<text x="175" y="45" fill="#aaa" font-size="10">
cache hit → instant
</text>


<text x="355" y="45" fill="#aaa" font-size="10">
cache miss → generate
</text>


</svg>

</details>


**Step 1:** Client requests tiles for viewport: GET /tiles/{z}/{x}/{y}.pbf (zoom/col/row)
**Step 2:** CDN serves cached vector tiles (95%+ hit rate). Cache miss → Tile Server
**Step 3:** Tile Server retrieves pre-computed vector tile from Tile DB, overlays real-time traffic layer
**Step 4:** Client renders vector tiles using GPU (WebGL/Metal) with client-side styling (dark mode, terrain, etc.)

Deep Dive: Vector Tiles vs Raster Tiles

`// Raster tiles (legacy): pre-rendered PNG images at each zoom level
// 23 zoom levels × 4^z tiles at level z = billions of images
// Cannot style client-side, huge storage, slow to update

// Vector tiles (modern, used by Google Maps):
// Encode map features as geometric primitives (points, lines, polygons)
// Format: Protocol Buffers (Mapbox Vector Tile / MVT spec)
// Client renders using GPU (WebGL on web, Metal/Vulkan on mobile)

// Advantages of vector tiles:
// 1. 10-100x smaller than raster (geometry vs pixels)
// 2. Client-side styling (dark mode, custom colors — no re-render)
// 3. Smooth zoom (interpolate between zoom levels)
// 4. Rotation and 3D tilt (impossible with raster)
// 5. Dynamic labels (render text client-side in user's language)

// Tile pyramid: Zoom 0 = 1 tile (whole world)
// Zoom 1 = 4 tiles, Zoom 2 = 16 tiles, ...
// Zoom 18 = 68B tiles (individual buildings visible)
// But vector tiles only store USED tiles (sparse representation)

// Tile content at zoom 15 (city block level):
{
  "roads": [
    { "geometry": [[lat,lng]...], "name": "Main St", "class": "primary" }
  ],
  "buildings": [
    { "geometry": [[lat,lng]...], "height": 15, "type": "commercial" }
  ],
  "water": [ { "geometry": [[lat,lng]...] } ],
  "labels": [ { "text": "Central Park", "point": [lat,lng] } ]
}`


## 🔄 Flow 2: Route Computation & Navigation


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 320" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#4285F4">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="80" text-anchor="middle" fill="white" font-size="12">
User: A → B
</text>


<rect x="175" y="60" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="240" y="80" text-anchor="middle" fill="white" font-size="12">
Routing Service
</text>


<rect x="350" y="30" width="140" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="420" y="50" text-anchor="middle" fill="white" font-size="12">
Road Graph
</text>
<text x="420" y="65" text-anchor="middle" fill="white" font-size="10">
(Contraction Hierarchy)
</text>


<rect x="350" y="100" width="140" height="50" rx="8" fill="#E65100">
</rect>
<text x="420" y="120" text-anchor="middle" fill="white" font-size="12">
Traffic Engine
</text>
<text x="420" y="135" text-anchor="middle" fill="white" font-size="10">
(live + predicted)
</text>


<rect x="550" y="60" width="140" height="50" rx="8" fill="#00695C">
</rect>
<text x="620" y="80" text-anchor="middle" fill="white" font-size="12">
Route Ranker
</text>
<text x="620" y="95" text-anchor="middle" fill="white" font-size="10">
(multiple routes)
</text>


<rect x="740" y="60" width="130" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="805" y="90" text-anchor="middle" fill="white" font-size="12">
Navigation UI
</text>


<rect x="350" y="200" width="140" height="50" rx="8" fill="#4E342E">
</rect>
<text x="420" y="220" text-anchor="middle" fill="white" font-size="12">
ETA Predictor
</text>
<text x="420" y="235" text-anchor="middle" fill="white" font-size="10">
(DeepMind ML)
</text>


<line x1="130" y1="85" x2="170" y2="85" stroke="#4285F4" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="305" y1="70" x2="345" y2="55" stroke="#4285F4" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="305" y1="100" x2="345" y2="125" stroke="#4285F4" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="490" y1="55" x2="545" y2="75" stroke="#4285F4" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="490" y1="125" x2="545" y2="95" stroke="#4285F4" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="690" y1="85" x2="735" y2="85" stroke="#4285F4" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="420" y1="150" x2="420" y2="195" stroke="#FF9800" stroke-width="2" marker-end="url(#a2)">
</line>


<text x="430" y="180" fill="#aaa" font-size="10">
traffic-aware ETA
</text>


</svg>

</details>


**Step 1:** User requests route from A to B. Routing Service snaps origin/destination to nearest road segments
**Step 2:** Road Graph queried using Contraction Hierarchies (pre-processed Dijkstra) — cross-country route in ~10ms
**Step 3:** Traffic Engine overlays live traffic speeds on road graph edges (from GPS probe data)
**Step 4:** Route Ranker generates 3-5 alternative routes, scores by ETA, distance, toll avoidance
**Step 5:** ETA Predictor (DeepMind GNN model) provides refined arrival time considering traffic patterns

Deep Dive: Contraction Hierarchies (Fast Shortest Path)

`// Problem: Dijkstra on full road graph = O(V log V + E)
// US road graph: ~25M nodes, ~60M edges → 2-5 seconds per query!

// Solution: Contraction Hierarchies (CH)
// Preprocessing (offline, hours):
// 1. Assign importance to each node (heuristic: centrality, road class)
// 2. Contract nodes in order of importance:
//    - Remove node, add "shortcut" edges between its neighbors
//    - Shortcut preserves shortest path distance

// Example: highway node connecting two cities
// Before: A -(5)→ X -(3)→ B
// After contracting X: A -(8)→ B (shortcut, distance = 5+3)

// Query (online, ~10ms):
// Bidirectional Dijkstra on hierarchical graph
// Forward search from source (only go UP in hierarchy)
// Backward search from target (only go UP in hierarchy)
// Meet in middle at high-importance nodes (highways)
// Result: ~1000 nodes visited vs 1M for plain Dijkstra

// With live traffic:
// CH shortcuts have pre-computed distances (static)
// Customizable CH: re-weight edges with live traffic
// ALT: use A* with landmarks for traffic-aware routing
// Google likely uses hybrid approach

// Turn-by-turn: polyline + maneuver instructions
// Encoded polyline: "anafCdxglU..." (compressed lat/lng sequence)
// Maneuvers: [{type:"turn_right", road:"Main St", distance:500m}]`


Deep Dive: Real-Time Traffic from GPS Probes

`// Traffic data source: anonymized GPS data from:
// - Android phones running Google Maps (~1B devices)
// - Waze user reports (owned by Google)
// - Connected vehicles, fleet tracking

// Pipeline:
// 1. Phone sends (lat, lng, speed, heading, timestamp) every 5-15 seconds
//    (only when Google Maps active OR background location enabled)
//
// 2. Map matching: snap GPS points to road segments
//    Hidden Markov Model: probabilistic matching considering:
//    - GPS accuracy (10-30m error)
//    - Road topology (which road at intersection?)
//    - Speed consistency with road type
//
// 3. Speed aggregation per road segment:
//    - Segment: each road between two intersections (~100m avg)
//    - Aggregate speed from all probes in last 2 minutes
//    - Median speed (robust to outliers)
//    - Minimum 3 probes for statistical confidence
//
// 4. Traffic coloring:
//    Green: speed >= 80% of free-flow speed
//    Orange: speed 40-80% of free-flow
//    Red: speed < 40% of free-flow
//    Dark red: speed < 20% (severe congestion)
//
// 5. Predictive traffic:
//    Historical patterns + current conditions → predict next 30-60 min
//    DeepMind GNN model on road graph: node features = current speed
//    Trained on years of historical traffic data`


## 🔄 Flow 3: Place Search & Autocomplete


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
<text x="75" y="80" text-anchor="middle" fill="white" font-size="12">
User Types
</text>
<text x="75" y="95" text-anchor="middle" fill="white" font-size="10">
"star..."
</text>


<rect x="175" y="60" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="240" y="80" text-anchor="middle" fill="white" font-size="12">
Autocomplete
</text>
<text x="240" y="95" text-anchor="middle" fill="white" font-size="10">
Service
</text>


<rect x="350" y="30" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="415" y="50" text-anchor="middle" fill="white" font-size="12">
Prefix Trie +
</text>
<text x="415" y="65" text-anchor="middle" fill="white" font-size="10">
Geo Context
</text>


<rect x="350" y="100" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="415" y="120" text-anchor="middle" fill="white" font-size="12">
Places Index
</text>
<text x="415" y="135" text-anchor="middle" fill="white" font-size="10">
(200M+ POIs)
</text>


<rect x="530" y="60" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="595" y="80" text-anchor="middle" fill="white" font-size="12">
Place Details
</text>
<text x="595" y="95" text-anchor="middle" fill="white" font-size="10">
(reviews, hours)
</text>


<line x1="130" y1="85" x2="170" y2="85" stroke="#34A853" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="305" y1="75" x2="345" y2="55" stroke="#34A853" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="305" y1="95" x2="345" y2="125" stroke="#34A853" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="480" y1="75" x2="525" y2="85" stroke="#34A853" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


**Step 1:** Each keystroke triggers autocomplete request (debounced 100ms)
**Step 2:** Prefix trie matches against place names, addresses, categories with geo-biasing (prefer nearby results)
**Step 3:** Results ranked by: relevance, proximity, popularity, recency, user history
**Step 4:** On selection, Place Details fetched: reviews, photos, hours, phone, menu, popular times


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 480" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="ac" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#34A853">
</polygon>
</marker>
</defs>


<text x="550" y="25" text-anchor="middle" fill="#34A853" font-size="16" font-weight="bold">
Google Maps Architecture
</text>


<rect x="20" y="50" width="120" height="40" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="75" text-anchor="middle" fill="white" font-size="11">
Web / Mobile / API
</text>


<rect x="200" y="50" width="130" height="40" rx="8" fill="#607D8B">
</rect>
<text x="265" y="75" text-anchor="middle" fill="white" font-size="11">
CDN Edge (Tiles)
</text>


<rect x="400" y="40" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="63" text-anchor="middle" fill="white" font-size="11">
Tile Server
</text>


<rect x="400" y="85" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="108" text-anchor="middle" fill="white" font-size="11">
Routing Engine
</text>


<rect x="400" y="130" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="153" text-anchor="middle" fill="white" font-size="11">
Places Service
</text>


<rect x="400" y="175" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="198" text-anchor="middle" fill="white" font-size="11">
Traffic Engine
</text>


<rect x="400" y="220" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="243" text-anchor="middle" fill="white" font-size="11">
Geocoding Service
</text>


<rect x="400" y="265" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="288" text-anchor="middle" fill="white" font-size="11">
Street View Svc
</text>


<rect x="590" y="50" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="660" y="75" text-anchor="middle" fill="white" font-size="11">
Bigtable (Map Data)
</text>


<rect x="590" y="110" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="660" y="135" text-anchor="middle" fill="white" font-size="11">
Road Graph (Memory)
</text>


<rect x="590" y="170" width="140" height="40" rx="8" fill="#00695C">
</rect>
<text x="660" y="195" text-anchor="middle" fill="white" font-size="11">
Spanner (Places)
</text>


<rect x="590" y="230" width="140" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="660" y="255" text-anchor="middle" fill="white" font-size="11">
Probe Pipeline (Kafka)
</text>


<rect x="800" y="50" width="150" height="40" rx="8" fill="#4E342E">
</rect>
<text x="875" y="75" text-anchor="middle" fill="white" font-size="11">
Colossus (Imagery)
</text>


<rect x="800" y="110" width="150" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="875" y="135" text-anchor="middle" fill="white" font-size="11">
Map Compilation Pipeline
</text>


<rect x="800" y="170" width="150" height="40" rx="8" fill="#E65100">
</rect>
<text x="875" y="195" text-anchor="middle" fill="white" font-size="11">
ETA Model (DeepMind)
</text>


<line x1="140" y1="70" x2="195" y2="70" stroke="#34A853" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="330" y1="70" x2="395" y2="57" stroke="#34A853" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="57" x2="585" y2="70" stroke="#34A853" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="102" x2="585" y2="130" stroke="#34A853" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="147" x2="585" y2="190" stroke="#4285F4" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="192" x2="585" y2="250" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="70" x2="795" y2="70" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="135" x2="795" y2="135" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="195" x2="795" y2="195" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


</svg>

</details>


## 💾 Database Schema


### Bigtable — Map Data & Tiles

`// Vector Tiles (pre-computed at all zoom levels)
Row Key: tile:{z}:{x}:{y}
Column Family "data": {
  "proto": compressed_vector_tile_protobuf,
  "version": tile_version_hash,
  "size_bytes": 12345,
  "generated_at": timestamp
}
// Total: ~50B tiles across all zoom levels
// Only populated tiles stored (sparse: ocean = no tiles)

// Road Graph (in-memory for routing)
// Stored in custom binary format optimized for CH queries
// Nodes: {id, lat, lng, ch_level, edges[]}
// Edges: {target_node, distance_m, travel_time_s, road_class, one_way}
// Shortcuts: {target_node, distance_m, contracted_nodes[]}
// Size: ~25M nodes × 60M edges → ~10GB compressed per region
// Partitioned by continent/region for distributed routing

// Traffic overlay
Row Key: segment:{road_segment_id}
Column Family "speed": {
  "current_speed_kph": 45,
  "free_flow_speed_kph": 65,
  "probe_count": 12,
  "confidence": 0.85,
  "updated_at": timestamp
}
// Updated every 1-2 minutes from probe pipeline`


### Spanner — Places

`CREATE TABLE places (
  place_id STRING(50) NOT NULL, --  `PK`
  name STRING(200) NOT NULL,
  lat FLOAT64 NOT NULL,
  lng FLOAT64 NOT NULL,
  s2_cell_id INT64 NOT NULL, --  `IDX` for geo queries
  category STRING(50), -- restaurant|hotel|gas_station
  address STRING(500),
  phone STRING(20),
  website STRING(500),
  rating FLOAT64, -- 1.0-5.0
  review_count INT64,
  price_level INT64, -- 1-4
  hours BYTES, -- structured open/close times
  photos ARRAY, -- GCS URLs
  updated_at TIMESTAMP
) PRIMARY KEY (place_id);

-- S2 Geometry cells for spatial indexing:
-- S2 divides Earth into hierarchical cells (like geohash but better)
-- Level 14 S2 cell ≈ 300m × 300m
-- Query: find all places in cells overlapping viewport
-- Efficient range scan on s2_cell_id

CREATE TABLE place_reviews (
  place_id STRING(50) NOT NULL,
  review_id STRING(30) NOT NULL,
  author_id STRING(30),
  rating INT64,
  text STRING(5000),
  language STRING(5),
  photos ARRAY,
  created_at TIMESTAMP,
) PRIMARY KEY (place_id, review_id),
  INTERLEAVE IN PARENT places;`


## ⚡ Cache & CDN Deep Dive


| Layer                  | What                              | Strategy                          | TTL                       |
|------------------------|-----------------------------------|-----------------------------------|---------------------------|
| CDN (Google Global)    | Vector tiles (static layers)      | Versioned URLs, 95%+ hit rate     | 1 week (until map update) |
| Client Tile Cache      | Recently viewed tiles             | LRU disk cache (100-500MB)        | 1 week                    |
| Redis — Traffic        | Live traffic speeds per segment   | Write-through from probe pipeline | 2 minutes                 |
| In-Memory — Road Graph | CH graph for routing              | Loaded at startup per region      | Until daily rebuild       |
| Client — Route Cache   | Recent routes and searched places | LRU local                         | 24 hours                  |


`// Offline maps: user downloads region
// Contents: vector tiles (all zoom levels for area) + road graph subset + place data
// Size: ~150MB for a city, ~1.5GB for a state
// Updated weekly via delta downloads (only changed tiles)
// Navigation works fully offline (routing done locally on road graph)`


## 📈 Scaling Considerations


- **Tile serving:** billions of tile requests/day. CDN absorbs 95%+. Tile Server handles cache misses and real-time overlay composition. Tiles pre-computed by Map Compilation Pipeline (daily batch).

- **Routing partitioning:** road graph too large for single machine. Partition by region (NA, EU, APAC). Cross-partition routes: find border crossing points, route within each partition, stitch. Multi-level routing: local roads cached locally, highways globally.

- **Traffic probe processing:** 1B+ GPS probes/minute globally. Kafka → Flink stream processing. Map matching and speed aggregation distributed by road segment. Output: per-segment speed map updated every 60-120 seconds.

- **Street View:** 200+ PB of 360° imagery. Served from Colossus (Google's distributed filesystem). CDN caches popular panoramas. Depth map + 3D mesh for smooth transitions between viewpoints.


## ⚖️ Tradeoffs


> **
Contraction Hierarchies
- 10ms routing (1000x faster than Dijkstra)
- Optimal shortest path guaranteed
- Works for continent-spanning routes


Contraction Hierarchies
- Hours of preprocessing on graph changes
- Live traffic integration requires re-customization
- Memory-intensive (full graph in RAM)


Vector Tiles
- 10-100x smaller than raster tiles
- Client-side styling (dark mode, custom)
- Smooth zoom and 3D rotation


Vector Tiles
- Requires GPU on client (older devices struggle)
- Complex rendering engine on client
- Label placement computed per frame


## 🔄 Alternative Approaches


HD Maps for Autonomous Vehicles

Centimeter-precision maps with lane markings, traffic signs, curb heights. LiDAR-scanned road surfaces. Real-time map updates from fleet sensor data. 100x more data than consumer maps. Google's Waymo uses separate HD map pipeline.

Crowd-Sourced Maps (OpenStreetMap)

Community-edited map data (Wikipedia model for maps). Free and open. Quality varies by region. Used by Mapbox, Apple Maps (partially). Advantage: faster updates in some areas. Challenge: vandalism, inconsistent quality.


## 📚 Additional Information


- **S2 Geometry:** Google's spatial indexing library. Maps Earth's surface onto a unit cube, then uses Hilbert curve for 1D ordering. Enables efficient spatial range queries in any database that supports range scans (Bigtable, Spanner).

- **ETA accuracy:** Google + DeepMind achieved <5% median error for ETA predictions using Graph Neural Networks on road network topology + real-time traffic. Published in Nature, 2020.

- **Map compilation:** satellite imagery → road detection (ML) → building footprints → address geocoding → speed limits → one-way streets → transit routes. Continuous pipeline processing petabytes of source data.

- **Waze integration:** live incident reports (accidents, police, road closures) from Waze community feed into Google Maps traffic layer. 140M+ Waze users provide ground truth.