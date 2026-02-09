# 📺 DirecTV App — Live TV & Streaming Platform


Design a TV streaming application that delivers live satellite/streaming channels, on-demand content, DVR recording, and multi-device viewing with a unified electronic program guide (EPG).


## 📋 Functional Requirements


- **Live TV streaming** — stream 300+ live channels with <3s channel switch time

- **Electronic Program Guide (EPG)** — browseable schedule grid with 14-day lookback and 7-day lookahead

- **Cloud DVR** — record live programs to cloud storage, manage recordings, series recording rules

- **On-demand library** — browse/search 50K+ movies and shows with personalized recommendations

- **Multi-device sync** — seamless handoff between TV, mobile, tablet, web (resume where you left off)

- **Parental controls** — content ratings filter, PIN-locked channels, viewing time limits

- **Authentication & entitlements** — verify subscription tier, regional blackout enforcement, concurrent stream limits


## 📋 Non-Functional Requirements


- **Low latency** — live TV latency <5s behind actual broadcast, channel switch <3s

- **Availability** — 99.99% for live streams, especially during major events (Super Bowl, Olympics)

- **Scale** — 15M+ subscribers, 5M+ concurrent viewers during peak (Sunday NFL)

- **Quality** — adaptive bitrate 480p to 4K HDR, Dolby Atmos audio

- **DRM** — content protection (Widevine, FairPlay, PlayReady) per studio licensing

- **Compliance** — FCC blackout rules, regional sports network restrictions, Ad insertion for live


## 🔄 Flow 1: Live TV Channel Streaming


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 320" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#00A3E0">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="80" text-anchor="middle" fill="white" font-size="12">
Satellite Feed
</text>


<rect x="170" y="50" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="235" y="70" text-anchor="middle" fill="white" font-size="12">
Ingest & Transcode
</text>
<text x="235" y="85" text-anchor="middle" fill="white" font-size="10">
(MPEG-TS → HLS)
</text>


<rect x="345" y="50" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="410" y="70" text-anchor="middle" fill="white" font-size="12">
Origin Server
</text>
<text x="410" y="85" text-anchor="middle" fill="white" font-size="10">
(manifest + segments)
</text>


<rect x="520" y="50" width="120" height="50" rx="8" fill="#607D8B">
</rect>
<text x="580" y="80" text-anchor="middle" fill="white" font-size="12">
CDN Edge
</text>


<rect x="690" y="50" width="120" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="750" y="80" text-anchor="middle" fill="white" font-size="12">
Client Player
</text>


<rect x="345" y="170" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="410" y="190" text-anchor="middle" fill="white" font-size="12">
SCTE-35 Ad
</text>
<text x="410" y="205" text-anchor="middle" fill="white" font-size="10">
Insertion (SSAI)
</text>


<rect x="520" y="170" width="120" height="50" rx="8" fill="#1565C0">
</rect>
<text x="580" y="190" text-anchor="middle" fill="white" font-size="12">
Entitlements
</text>
<text x="580" y="205" text-anchor="middle" fill="white" font-size="10">
Service
</text>


<rect x="690" y="170" width="120" height="50" rx="8" fill="#4E342E">
</rect>
<text x="750" y="190" text-anchor="middle" fill="white" font-size="12">
DRM License
</text>
<text x="750" y="205" text-anchor="middle" fill="white" font-size="10">
Server
</text>


<line x1="130" y1="75" x2="165" y2="75" stroke="#00A3E0" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="75" x2="340" y2="75" stroke="#00A3E0" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="475" y1="75" x2="515" y2="75" stroke="#00A3E0" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="640" y1="75" x2="685" y2="75" stroke="#00A3E0" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="410" y1="100" x2="410" y2="165" stroke="#FF6B00" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="750" y1="100" x2="750" y2="165" stroke="#FF6B00" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="640" y1="195" x2="685" y2="195" stroke="#FF6B00" stroke-width="2" marker-end="url(#a1)">
</line>


<text x="420" y="140" fill="#aaa" font-size="10">
ad markers
</text>


<text x="760" y="140" fill="#aaa" font-size="10">
license request
</text>


</svg>

</details>


**Step 1:** Satellite/fiber feed received at ingest facility. MPEG-TS demuxed, transcoded to multiple ABR profiles (HLS/DASH)
**Step 2:** Live segments (2-6s each) pushed to origin server with rolling manifest updates
**Step 3:** SCTE-35 markers in stream trigger server-side ad insertion (SSAI) for personalized ads
**Step 4:** Client requests manifest from CDN edge, plays segments. DRM license fetched on first play
**Step 5:** Entitlements Service validates subscription tier and blackout rules before granting access

Deep Dive: HLS Live Streaming Architecture

`// HLS (HTTP Live Streaming) for live TV:
// Segments: 6-second CMAF (Common Media Application Format) chunks
// Low-latency HLS (LL-HLS): partial segments of ~200ms

// Manifest structure:
#EXTM3U
#EXT-X-STREAM-INF:BANDWIDTH=2000000,RESOLUTION=1280x720
  channel_123/720p/playlist.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=5000000,RESOLUTION=1920x1080
  channel_123/1080p/playlist.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=15000000,RESOLUTION=3840x2160
  channel_123/4k/playlist.m3u8

// Per-quality playlist (rolling window):
#EXT-X-TARGETDURATION:6
#EXT-X-MEDIA-SEQUENCE:48291
#EXTINF:6.006,
  segment_48291.ts
#EXTINF:6.006,
  segment_48292.ts
// ... rolling 3-5 segments for live edge

// Channel switch optimization:
// 1. Start with lowest quality (instant start)
// 2. ABR algorithm switches up within 2-3 segments
// 3. Pre-fetch neighboring channel manifests
// 4. Keep low-quality stream for recently-watched channels in buffer
// Goal: <2s perceived switch time`


Deep Dive: Server-Side Ad Insertion (SSAI)

`// SSAI replaces broadcast ads with personalized digital ads
// Advantages over client-side: no ad blockers, seamless stitching

// Flow:
// 1. Encoder detects SCTE-35 markers in MPEG-TS stream
//    (SCTE-35 = industry standard for ad signaling)
// 2. Ad decision server called with user profile + context
// 3. Ad creative pre-transcoded to match stream bitrate profiles
// 4. Manifest manipulated to splice ad segments inline
// 5. Client sees continuous stream (no buffering at ad breaks)

// Personalization signals:
{
  "user_id": "u_123",
  "geo": {"dma": "501", "zip": "10001"},  // Nielsen DMA
  "device": "roku_ultra",
  "channel": "ESPN",
  "program": "NFL_Sunday",
  "ad_pod_duration": 120,  // seconds
  "content_rating": "TV-PG"
}

// Revenue: targeted ads command 3-5x CPM vs broadcast ads
// Compliance: maintain ad load limits per FCC/contractual rules`


## 🔄 Flow 2: Cloud DVR Recording


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 280" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FF6B00">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="90" text-anchor="middle" fill="white" font-size="12">
User Sets Record
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="255" y="80" text-anchor="middle" fill="white" font-size="12">
DVR Scheduler
</text>
<text x="255" y="95" text-anchor="middle" fill="white" font-size="10">
Service
</text>


<rect x="370" y="60" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="435" y="80" text-anchor="middle" fill="white" font-size="12">
Live Ingest
</text>
<text x="435" y="95" text-anchor="middle" fill="white" font-size="10">
Capture
</text>


<rect x="550" y="60" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="615" y="80" text-anchor="middle" fill="white" font-size="12">
Object Storage
</text>
<text x="615" y="95" text-anchor="middle" fill="white" font-size="10">
(S3 / GCS)
</text>


<rect x="730" y="60" width="130" height="50" rx="8" fill="#00695C">
</rect>
<text x="795" y="80" text-anchor="middle" fill="white" font-size="12">
Recording
</text>
<text x="795" y="95" text-anchor="middle" fill="white" font-size="10">
Metadata DB
</text>


<rect x="370" y="180" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="435" y="200" text-anchor="middle" fill="white" font-size="12">
Copy-on-Write
</text>
<text x="435" y="215" text-anchor="middle" fill="white" font-size="10">
Deduplication
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#FF6B00" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="320" y1="85" x2="365" y2="85" stroke="#FF6B00" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="500" y1="85" x2="545" y2="85" stroke="#FF6B00" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="680" y1="85" x2="725" y2="85" stroke="#FF6B00" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="435" y1="110" x2="435" y2="175" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<text x="450" y="150" fill="#aaa" font-size="10">
shared segments
</text>


</svg>

</details>


**Step 1:** User schedules recording (single episode or series rule). DVR Scheduler persists rule
**Step 2:** At scheduled time, Live Ingest Capture writes encrypted segments to object storage
**Step 3:** Copy-on-write deduplication: if 10K users record same show, store ONE copy, create per-user metadata pointers
**Step 4:** Recording metadata (start/end times, segment manifest, user_id) saved to Recording DB
**Step 5:** Playback: generate user-specific manifest with their ad insertions, serve from CDN

Deep Dive: Cloud DVR Deduplication

`// Cloud DVR economics depend on deduplication:
// Without dedup: 1M users × 50 recordings × 2GB each = 100 PB!
// With dedup: store unique content ONCE = ~500TB

// Implementation: Copy-on-Write references
// Physical storage: one copy per (channel, timeslot)
// Per-user: metadata pointer + custom ad manifest

recordings_physical: {
  id: "rec_phys_espn_20250209_1300",
  channel_id: "espn",
  start_time: "2025-02-09T13:00:00Z",
  end_time: "2025-02-09T16:30:00Z",
  segments: ["s3://dvr/espn/20250209/seg_0001.ts", ...],
  total_size_gb: 8.5
}

recordings_user: {
  user_id: "u_123",
  physical_recording_id: "rec_phys_espn_20250209_1300",
  user_start_offset: 0, // user might set to record from beginning
  user_end_offset: 0,
  ad_manifest_id: "adm_u123_espn_sb", // personalized ads
  watched_position: 3600, // resume point (seconds)
  expires_at: "2025-05-09T16:30:00Z" // 90-day retention
}

// Storage savings: 95-99% reduction via deduplication
// Cost: ~$0.02/GB/month for S3 Standard`


## 🔄 Flow 3: EPG (Electronic Program Guide) & Search


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 280" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#00A3E0">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="90" text-anchor="middle" fill="white" font-size="12">
Client App
</text>


<rect x="180" y="60" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="245" y="80" text-anchor="middle" fill="white" font-size="12">
EPG Service
</text>
<text x="245" y="95" text-anchor="middle" fill="white" font-size="10">
(schedule API)
</text>


<rect x="360" y="30" width="130" height="50" rx="8" fill="#00695C">
</rect>
<text x="425" y="60" text-anchor="middle" fill="white" font-size="12">
Redis EPG Cache
</text>


<rect x="360" y="100" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="425" y="130" text-anchor="middle" fill="white" font-size="12">
Schedule DB
</text>


<rect x="550" y="60" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="615" y="80" text-anchor="middle" fill="white" font-size="12">
Gracenote /
</text>
<text x="615" y="95" text-anchor="middle" fill="white" font-size="10">
TMS Data Feed
</text>


<rect x="180" y="190" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="245" y="220" text-anchor="middle" fill="white" font-size="12">
Search Service
</text>


<rect x="360" y="190" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="425" y="220" text-anchor="middle" fill="white" font-size="12">
Elasticsearch
</text>


<line x1="130" y1="85" x2="175" y2="85" stroke="#00A3E0" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="310" y1="70" x2="355" y2="55" stroke="#00A3E0" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="310" y1="100" x2="355" y2="125" stroke="#00A3E0" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="490" y1="55" x2="545" y2="75" stroke="#81D4FA" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="490" y1="125" x2="545" y2="95" stroke="#81D4FA" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="130" y1="95" x2="175" y2="215" stroke="#FF6B00" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="310" y1="215" x2="355" y2="215" stroke="#FF6B00" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


**Step 1:** Gracenote/TMS provides program schedule data (updated every 15 min). Ingested into Schedule DB
**Step 2:** EPG Service caches schedule data in Redis (key: channel_id + date, TTL: 1 hour)
**Step 3:** Client requests EPG grid for visible channels + time window. Server returns paginated schedule
**Step 4:** Search queries hit Elasticsearch (title, actor, genre, keyword across live + on-demand catalog)


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 500" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="ac" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#00A3E0">
</polygon>
</marker>
</defs>


<text x="550" y="25" text-anchor="middle" fill="#00A3E0" font-size="16" font-weight="bold">
DirecTV Streaming Architecture
</text>


<rect x="20" y="50" width="120" height="40" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="75" text-anchor="middle" fill="white" font-size="11">
Client Devices
</text>


<rect x="200" y="50" width="130" height="40" rx="8" fill="#E65100">
</rect>
<text x="265" y="75" text-anchor="middle" fill="white" font-size="11">
API Gateway / LB
</text>


<rect x="400" y="40" width="130" height="35" rx="8" fill="#1565C0">
</rect>
<text x="465" y="63" text-anchor="middle" fill="white" font-size="11">
Auth & Entitlements
</text>


<rect x="400" y="85" width="130" height="35" rx="8" fill="#1565C0">
</rect>
<text x="465" y="108" text-anchor="middle" fill="white" font-size="11">
EPG Service
</text>


<rect x="400" y="130" width="130" height="35" rx="8" fill="#1565C0">
</rect>
<text x="465" y="153" text-anchor="middle" fill="white" font-size="11">
DVR Service
</text>


<rect x="400" y="175" width="130" height="35" rx="8" fill="#1565C0">
</rect>
<text x="465" y="198" text-anchor="middle" fill="white" font-size="11">
Catalog / Search
</text>


<rect x="400" y="220" width="130" height="35" rx="8" fill="#1565C0">
</rect>
<text x="465" y="243" text-anchor="middle" fill="white" font-size="11">
Playback Service
</text>


<rect x="620" y="50" width="130" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="685" y="75" text-anchor="middle" fill="white" font-size="11">
Live Ingest Pipeline
</text>


<rect x="620" y="110" width="130" height="40" rx="8" fill="#607D8B">
</rect>
<text x="685" y="135" text-anchor="middle" fill="white" font-size="11">
CDN (Akamai)
</text>


<rect x="620" y="170" width="130" height="40" rx="8" fill="#4E342E">
</rect>
<text x="685" y="195" text-anchor="middle" fill="white" font-size="11">
DRM License Server
</text>


<rect x="620" y="230" width="130" height="40" rx="8" fill="#E65100">
</rect>
<text x="685" y="255" text-anchor="middle" fill="white" font-size="11">
SSAI Ad Server
</text>


<rect x="830" y="50" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="900" y="75" text-anchor="middle" fill="white" font-size="11">
Object Storage (DVR)
</text>


<rect x="830" y="110" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="900" y="135" text-anchor="middle" fill="white" font-size="11">
PostgreSQL / Cassandra
</text>


<rect x="830" y="170" width="140" height="40" rx="8" fill="#00695C">
</rect>
<text x="900" y="195" text-anchor="middle" fill="white" font-size="11">
Redis Cluster
</text>


<rect x="830" y="230" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="900" y="255" text-anchor="middle" fill="white" font-size="11">
Elasticsearch
</text>


<rect x="200" y="370" width="130" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="265" y="395" text-anchor="middle" fill="white" font-size="11">
Recommendation ML
</text>


<rect x="400" y="370" width="130" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="465" y="395" text-anchor="middle" fill="white" font-size="11">
Analytics Pipeline
</text>


<rect x="620" y="370" width="130" height="40" rx="8" fill="#1565C0">
</rect>
<text x="685" y="395" text-anchor="middle" fill="white" font-size="11">
Concurrency Mgr
</text>


<line x1="140" y1="70" x2="195" y2="70" stroke="#00A3E0" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="330" y1="70" x2="395" y2="57" stroke="#00A3E0" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="330" y1="70" x2="395" y2="102" stroke="#00A3E0" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="530" y1="57" x2="615" y2="70" stroke="#00A3E0" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="530" y1="243" x2="615" y2="135" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="750" y1="70" x2="825" y2="70" stroke="#FF6B00" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="750" y1="135" x2="825" y2="135" stroke="#FF6B00" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="750" y1="195" x2="825" y2="195" stroke="#FF6B00" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="750" y1="255" x2="825" y2="255" stroke="#FF6B00" stroke-width="2" marker-end="url(#ac)">
</line>


</svg>

</details>


## 💾 Database Schema


### PostgreSQL — Core Entities

`CREATE TABLE users (
  id UUID PRIMARY KEY, --  `PK`
  account_id UUID NOT NULL, --  `FK` household account
  email VARCHAR(255) UNIQUE,
  subscription_tier VARCHAR(20) NOT NULL, -- 'entertainment','choice','ultimate'
  max_concurrent_streams INT DEFAULT 3,
  parental_pin_hash VARCHAR(64),
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE channels (
  id SERIAL PRIMARY KEY, --  `PK`
  call_sign VARCHAR(10) NOT NULL, -- 'ESPN', 'HBO'
  name VARCHAR(100) NOT NULL,
  tier_required VARCHAR(20) NOT NULL, --  `IDX`
  category VARCHAR(30), -- 'sports','movies','news'
  hls_origin_url TEXT NOT NULL,
  logo_url TEXT
);

CREATE TABLE schedule (
  id BIGSERIAL PRIMARY KEY,
  channel_id INT NOT NULL REFERENCES channels(id), --  `FK`
  program_title VARCHAR(255) NOT NULL,
  start_time TIMESTAMPTZ NOT NULL, --  `IDX`
  end_time TIMESTAMPTZ NOT NULL,
  rating VARCHAR(10), -- 'TV-PG', 'TV-MA'
  description TEXT,
  series_id VARCHAR(50), --  `IDX`
  episode_num VARCHAR(20),
  gracenote_id VARCHAR(30) --  `IDX`
);
--  `IDX`: (channel_id, start_time) for EPG queries`


### Cassandra — DVR & Playback

`-- DVR Recordings (Cassandra for write-heavy, time-series)
CREATE TABLE dvr_recordings (
  user_id UUID,        --  `SHARD`
  recording_id UUID,
  channel_id INT,
  program_title TEXT,
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  physical_recording_id UUID, -- dedup reference
  ad_manifest_url TEXT,
  watched_position INT,  -- seconds, for resume
  expires_at TIMESTAMP,
  PRIMARY KEY (user_id, recording_id)
) WITH default_time_to_live = 7776000; -- 90 days

-- Watch History / Resume Points
CREATE TABLE watch_progress (
  user_id UUID,         --  `SHARD`
  content_id TEXT,      -- channel_id or vod_asset_id
  content_type TEXT,    -- 'live' | 'vod' | 'dvr'
  position_seconds INT,
  duration_seconds INT,
  last_updated TIMESTAMP,
  device_id TEXT,
  PRIMARY KEY (user_id, content_id)
);

-- Concurrent stream tracking (Redis better for this)
-- Key: account:{account_id}:streams
-- Value: SET of {device_id, stream_url, started_at}
-- SCARD for count check, SADD/SREM for manage`


## ⚡ Cache & CDN Deep Dive


| Layer             | What                      | Strategy                                     | TTL                     |
|-------------------|---------------------------|----------------------------------------------|-------------------------|
| CDN Edge (Akamai) | HLS segments + manifests  | Pull-through, segment TTL = segment duration | 6-10s (live), 24h (VOD) |
| Redis EPG Cache   | Schedule grid data        | Pre-computed per channel+day                 | 1 hour                  |
| Redis Session     | Auth tokens, entitlements | Write-through on login                       | 30 min                  |
| Client Buffer     | Video segments            | ABR buffer: 30s ahead for live               | Until played            |
| Catalog Cache     | VOD metadata, images      | LRU, invalidate on catalog update            | 6 hours                 |


CDN for Live Streaming
`// Live HLS segment caching at CDN edge:
// Challenge: 300 channels × 5 quality levels × 6s segments
// = new segment every 6s per rendition = massive origin load

// Solution: CDN shield layer
// Edge → Shield (regional) → Origin
// Shield absorbs concurrent requests for same segment
// Request coalescing: 10K edge requests → 1 origin request

// Manifest caching: very short TTL (1-2s) or server push
// Segment caching: TTL = segment duration (6s)
// Stale-while-revalidate for smooth playback`


## 📈 Scaling Considerations


- **Super Bowl problem:** 5M+ concurrent viewers on one channel. CDN pre-warms edge caches, dedicated origin capacity, regional failover. Predictive scaling based on event schedule.

- **Concurrent stream enforcement:** Redis SET per account tracks active streams. SCARD check before granting new stream. Challenge: device crash doesn't send stop → heartbeat timeout (30s) cleans stale entries.

- **EPG data volume:** 300 channels × 24h × 14 days lookback = ~100K schedule entries. Redis can hold entire EPG in memory (~500MB). Shard by channel range.

- **Regional blackout:** Geo-IP lookup + blackout rules engine. Override CDN routing for blacked-out regions. Must handle VPN detection for sports blackouts.


## ⚖️ Tradeoffs


> **
SSAI (Server-Side Ads)
- Ad-blocker resistant
- Seamless stitching (no buffering)
- Unified QoS metrics


SSAI (Server-Side Ads)
- Per-viewer manifest = higher origin load
- Less interactive than CSAI
- Complex CDN caching (personalized manifests uncacheable)


Cloud DVR (Copy-on-Write)
- 95%+ storage savings via dedup
- Instant "recording" (just metadata)
- No per-user storage limits needed


Cloud DVR (Copy-on-Write)
- Complex rights management per user
- Ad replacement rules vary per user
- Regulatory: some markets require unique copies


## 🔄 Alternative Approaches


ATSC 3.0 (NextGen TV)

IP-based broadcast standard replacing satellite. Combines over-the-air broadcast with internet delivery. Supports 4K HDR, immersive audio, targeted OTA ads. Hybrid approach: broadcast popular channels OTA, niche channels via IP.

WebRTC for Ultra-Low Latency

Sub-second latency for sports betting / interactive use cases. Replace HLS (3-30s latency) with WebRTC (<500ms). Trade scalability (WebRTC needs media server per viewer) for interactivity. Use selectively for specific premium features.


## 📚 Additional Information


- **CMAF (Common Media Application Format):** unified segment format for both HLS and DASH. Single encoding pipeline serves Apple and non-Apple devices.

- **Widevine L1/L3:** L1 = hardware-backed DRM (required for HD/4K on Android/Chrome). L3 = software only (SD max). FairPlay for Apple, PlayReady for Windows.

- **QoE metrics:** time-to-first-frame, buffering ratio, bitrate stability, channel switch time. Tracked via client beacons to analytics pipeline (Conviva/NPAW).

- **Failover:** multi-CDN strategy (Akamai + CloudFront). Real-time CDN health monitoring. Client-side CDN switching on error (manifest contains alternate CDN URLs).