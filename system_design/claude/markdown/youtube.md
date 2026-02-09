# ▶️ System Design: YouTube (Video Streaming Platform)


## 📋 Functional Requirements


  - Creators can upload videos in any format — system transcodes to multiple resolutions

  - Viewers can stream videos with adaptive bitrate (auto quality adjustment)

  - Users can search for videos by title, description, tags

  - Users can like, comment, subscribe, and view personalized recommendations


## 🔒 Non-Functional Requirements


  - **Scale:** 800M+ videos, 500 hours uploaded per minute, 1B hours watched per day

  - **Low latency streaming:** Video playback starts in <2s, rebuffering ratio <0.5%

  - **High availability:** 99.99% uptime globally

  - **Efficient storage:** ~1PB of new video per day after transcoding

  - **Global distribution:** CDN serving from edge locations worldwide


## 🔄 Flow 1 — Video Upload & Processing Pipeline


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 420" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="170" width="110" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="198" text-anchor="middle" fill="white" font-size="13">
Creator
</text>

  
<text x="75" y="215" text-anchor="middle" fill="white" font-size="10">
(Upload Client)
</text>

  
<line x1="130" y1="200" x2="190" y2="200" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="200" y="170" width="130" height="60" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="265" y="195" text-anchor="middle" fill="white" font-size="12">
Upload Service
</text>

  
<text x="265" y="212" text-anchor="middle" fill="white" font-size="10">
(Resumable / Chunked)
</text>

  
<line x1="330" y1="200" x2="390" y2="200" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="400" y="170" width="130" height="60" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="465" y="195" text-anchor="middle" fill="white" font-size="12">
Object Storage
</text>

  
<text x="465" y="212" text-anchor="middle" fill="white" font-size="10">
(GCS — Raw Video)
</text>

  
<line x1="530" y1="200" x2="590" y2="200" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="600" y="170" width="140" height="60" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="670" y="195" text-anchor="middle" fill="white" font-size="12">
Kafka / Pub-Sub
</text>

  
<text x="670" y="212" text-anchor="middle" fill="white" font-size="10">
(transcode-jobs)
</text>

  
<line x1="740" y1="180" x2="810" y2="110" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="740" y1="200" x2="810" y2="200" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="740" y1="220" x2="810" y2="290" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="820" y="80" width="160" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="900" y="103" text-anchor="middle" fill="white" font-size="11">
Transcoder Workers
</text>

  
<text x="900" y="120" text-anchor="middle" fill="white" font-size="10">
(FFmpeg — GPU Cluster)
</text>

  
<rect x="820" y="175" width="160" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="900" y="198" text-anchor="middle" fill="white" font-size="11">
Thumbnail Generator
</text>

  
<text x="900" y="215" text-anchor="middle" fill="white" font-size="10">
(ML Scene Detection)
</text>

  
<rect x="820" y="270" width="160" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="900" y="293" text-anchor="middle" fill="white" font-size="11">
Content Moderation
</text>

  
<text x="900" y="310" text-anchor="middle" fill="white" font-size="10">
(ML + Human Review)
</text>

  
<line x1="980" y1="108" x2="1040" y2="200" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="1040" y="175" width="130" height="55" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="1105" y="198" text-anchor="middle" fill="white" font-size="11">
CDN Origin
</text>

  
<text x="1105" y="215" text-anchor="middle" fill="white" font-size="10">
(Transcoded Segments)
</text>


</svg>

</details>


### Step-by-Step


  1. **Creator initiates upload:** `POST /api/v3/videos?uploadType=resumable` → returns upload URI. Client uploads video in 8MB chunks with progress tracking. Supports resume on network interruption

  2. **Upload Service:** Validates file type, size limit (256GB max), user quota. Stores raw video in Google Cloud Storage (GCS) with lifecycle policy. Generates video_id (Base62 encoded, 11 chars like `dQw4w9WgXcQ`)

  3. **Metadata saved:** Video record created in DB with status `processing`. Title, description, tags, channel_id stored

  4. **Pub/Sub message:** Transcode job message published: `{ video_id, raw_path, target_resolutions: [144p, 240p, 360p, 480p, 720p, 1080p, 1440p, 2160p] }`

  5. **Transcoder Workers (GPU):** Each worker pulls a job → downloads raw video → FFmpeg transcodes to target resolution with H.264/H.265/VP9/AV1 codecs → segments into 2-6 second chunks → generates MPD/M3U8 manifest for DASH/HLS

  6. **Thumbnail Generator:** ML model selects 3 key frames (scene changes, faces, text) → generates thumbnails at multiple sizes → creator can choose or upload custom

  7. **Content Moderation:** ML classifiers check for: nudity (NSFW), violence, copyright (Content ID fingerprinting against reference library of 100M+ assets), hate speech in audio (speech-to-text + NLP)

  8. **CDN distribution:** Transcoded segments pushed to CDN origin → replicated to edge PoPs. Video status → `published`


> **
**Example:** Creator uploads 4K 30-minute vlog (8GB raw) → Upload Service: resumable upload completes in ~12 min on 100Mbps → Transcoder: 8 resolution variants × 3 codecs = 24 renditions → Total output: ~15GB across all variants → 900 segments (2s each × 30 min) per rendition → Content ID: no copyright match → Published in ~20 minutes → First viewer in US streams 1080p → CDN edge in Virginia serves pre-cached segments


### 🔍 Component Deep Dives — Upload Flow


Transcoding Pipeline


**Codec ladder:**


| Resolution | H.264 Bitrate | VP9 Bitrate | AV1 Bitrate |
|------------|---------------|-------------|-------------|
| 2160p (4K) | 20 Mbps       | 12 Mbps     | 8 Mbps      |
| 1080p      | 8 Mbps        | 5 Mbps      | 3.5 Mbps    |
| 720p       | 5 Mbps        | 3 Mbps      | 2 Mbps      |
| 480p       | 2.5 Mbps      | 1.5 Mbps    | 1 Mbps      |
| 360p       | 1 Mbps        | 0.7 Mbps    | 0.5 Mbps    |


**Per-title encoding:** Netflix-pioneered technique — analyze video complexity first (animation vs live action vs sports), then optimize bitrate ladder per video. A cartoon needs fewer bits than a fast-action sports clip at the same resolution.


**Parallel transcoding:** Split video into GOP-aligned segments, transcode segments in parallel across workers, merge. Reduces wall-clock time from hours to minutes for long videos.


Content ID (Copyright Detection)


**How it works:** Reference content owners upload their media → system generates audio/visual fingerprint. Every uploaded video is fingerprinted and compared against the reference database (~100M+ assets).


**Fingerprinting:** Audio: chromagram-based spectral fingerprint. Visual: perceptual hash of key frames. Both are robust to transcoding, cropping, speed changes.


**Actions:** Match found → content owner policy applies: (1) Block video, (2) Monetize (run ads, revenue to rights holder), (3) Track (just monitor). Most choose monetize.


Resumable Upload Protocol


**Protocol:** Google's Resumable Upload protocol over HTTPS


**Flow:** (1) Client POSTs metadata → gets upload URI, (2) Client PUTs video data in chunks, (3) If interrupted, client queries upload status → server reports last received byte, (4) Client resumes from that byte offset


**Why:** Video files are large (GBs). Without resumability, a network hiccup at 99% would require re-uploading everything. Critical for mobile uploads on unreliable connections.


## 🔄 Flow 2 — Video Streaming (Playback)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 380" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="160" width="110" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="188" text-anchor="middle" fill="white" font-size="13">
Viewer
</text>

  
<text x="75" y="205" text-anchor="middle" fill="white" font-size="10">
(Video Player)
</text>

  
<line x1="130" y1="190" x2="200" y2="190" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="210" y="160" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="280" y="185" text-anchor="middle" fill="white" font-size="12">
Video API
</text>

  
<text x="280" y="202" text-anchor="middle" fill="white" font-size="10">
(Metadata + Manifest)
</text>

  
<line x1="350" y1="190" x2="420" y2="190" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="430" y="160" width="140" height="60" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="500" y="185" text-anchor="middle" fill="white" font-size="12">
CDN Edge PoP
</text>

  
<text x="500" y="202" text-anchor="middle" fill="white" font-size="10">
(Nearest Location)
</text>

  
<line x1="570" y1="175" x2="640" y2="120" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="570" y1="205" x2="640" y2="260" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="650" y="90" width="160" height="55" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="730" y="115" text-anchor="middle" fill="white" font-size="12">
Cache HIT
</text>

  
<text x="730" y="132" text-anchor="middle" fill="white" font-size="10">
(Serve from Edge)
</text>

  
<rect x="650" y="235" width="160" height="55" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="730" y="258" text-anchor="middle" fill="white" font-size="12">
Cache MISS
</text>

  
<text x="730" y="275" text-anchor="middle" fill="white" font-size="10">
(Fetch from Origin)
</text>

  
<line x1="810" y1="262" x2="880" y2="262" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="890" y="235" width="140" height="55" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="960" y="258" text-anchor="middle" fill="white" font-size="12">
Origin (GCS)
</text>

  
<text x="960" y="275" text-anchor="middle" fill="white" font-size="10">
(Fill → Edge Cache)
</text>


</svg>

</details>


### Step-by-Step


  1. **Player requests video:** `GET /api/v3/videos/dQw4w9WgXcQ` → returns metadata (title, description, channel) + streaming manifest URL

  2. **Manifest fetch:** Player fetches DASH MPD or HLS M3U8 manifest from CDN. Manifest lists all available resolutions + segment URLs

  3. **Adaptive Bitrate (ABR):** Player's ABR algorithm selects initial resolution based on connection speed estimate. Starts with lower quality for fast start, ramps up

  4. **Segment requests:** Player requests 2-6 second video segments sequentially from CDN: `GET /v/dQw4w9WgXcQ/seg-0001-1080p.m4s`

  5. **CDN edge serve:** If segment is cached at edge (cache HIT ~95% for popular videos) → serve directly. If MISS → fetch from regional cache → origin (GCS) → cache at edge for subsequent requests

  6. **Quality switching:** During playback, ABR continuously monitors buffer level + throughput. If bandwidth drops → switch to lower resolution on next segment boundary (seamless switch)

  7. **View counted:** After ~30 seconds of playback, view count event fired → Kafka → async increment


> **
**Example:** Viewer clicks video → Player fetches manifest (50KB, cached at edge) → ABR: estimated 15Mbps connection → starts at 720p → first segment in 0.8s → buffer fills, switches to 1080p at segment 3 → viewer on subway: bandwidth drops to 2Mbps → ABR detects buffer draining → switches to 360p at segment 45 → bandwidth recovers → back to 720p seamlessly


### 🔍 Component Deep Dives — Streaming Flow


DASH vs HLS (Adaptive Streaming Protocols)


| Feature         | DASH (Dynamic Adaptive Streaming over HTTP) | HLS (HTTP Live Streaming) |
|-----------------|---------------------------------------------|---------------------------|
| Manifest        | MPD (XML)                                   | M3U8 (plaintext playlist) |
| Segment format  | fMP4, WebM                                  | fMP4, MPEG-TS             |
| Browser support | MSE-based (most browsers)                   | Native Safari, MSE others |
| Codec support   | Any (VP9, AV1, H.264, H.265)                | H.264, H.265, limited AV1 |
| DRM             | CENC (Widevine, PlayReady)                  | FairPlay (Apple)          |


**YouTube uses DASH** primarily (with HLS fallback for Apple devices). DASH allows more codec flexibility (YouTube is pushing AV1 adoption for bandwidth savings).


ABR (Adaptive Bitrate) Algorithm


**Buffer-Based (BBA):** YouTube uses a buffer-based approach — decisions based on current buffer level, not just throughput:


  - Buffer < 5s → drop to lowest quality (avoid rebuffer at all costs)

  - Buffer 5-15s → choose quality matching recent throughput

  - Buffer > 15s → try next higher quality


**Why not throughput-based?** Throughput estimation is noisy (varies wildly on mobile). Buffer level is a more stable signal of whether the player can sustain current quality.


CDN Architecture (Multi-Tier)


**Tier 1 — Edge PoPs:** Hundreds of locations worldwide. Cache hot content (videos viewed in last hour). ~95% cache hit rate for popular videos. SSD-backed


**Tier 2 — Regional Caches:** ~20 locations. Broader cache for less popular content. Edge misses go here before origin


**Tier 3 — Origin (GCS):** All transcoded segments stored permanently. Only accessed on cold content or initial cache fill


**Long tail problem:** ~80% of YouTube's videos are watched <100 times total. These "long tail" videos are not cached at edge — served from regional/origin. Google's Peering CDN (Google Global Cache) is installed directly in ISP data centers for better performance.


## 🔄 Flow 3 — Video Search


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 300" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="120" width="110" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="155" text-anchor="middle" fill="white" font-size="14">
User
</text>

  
<line x1="130" y1="150" x2="200" y2="150" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="210" y="120" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="280" y="148" text-anchor="middle" fill="white" font-size="12">
Search Service
</text>

  
<text x="280" y="165" text-anchor="middle" fill="white" font-size="10">
(Query Understanding)
</text>

  
<line x1="350" y1="150" x2="420" y2="150" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="430" y="120" width="150" height="60" rx="10" fill="#8a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="505" y="148" text-anchor="middle" fill="white" font-size="12">
Video Search Index
</text>

  
<text x="505" y="165" text-anchor="middle" fill="white" font-size="10">
(Inverted Index)
</text>

  
<line x1="580" y1="150" x2="650" y2="150" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="660" y="120" width="150" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="735" y="148" text-anchor="middle" fill="white" font-size="12">
Ranking Service
</text>

  
<text x="735" y="165" text-anchor="middle" fill="white" font-size="10">
(ML — CTR + Watch Time)
</text>


</svg>

</details>


### Step-by-Step


  1. **User searches:** `GET /api/v3/search?q=python+tutorial+beginners&type=video`

  2. **Query Understanding:** Spell correction, synonym expansion, entity detection ("python" → programming language, not snake), intent classification (tutorial-seeking)

  3. **Index query:** Inverted index lookup on tokenized title, description, tags, auto-generated captions. Initial retrieval: ~10K candidates

  4. **Ranking:** ML model scores candidates based on: query relevance (BM25 text match), predicted CTR (click-through rate), predicted watch time, video freshness, channel authority, user personalization (watch history, subscriptions)

  5. **Return top 20:** Paginated results with thumbnails, title, channel, view count, upload date, duration


> **
**Example:** User searches "python tutorial beginners" → 15K candidate videos match text → Ranking model: "Automate the Boring Stuff" (high watch time completion), "Python for Beginners" (most views), "Learn Python in 1 Hour" (recent, high CTR) → User has Java tutorials in watch history → personalization boosts "Python for Java Developers" → Results returned in 150ms


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 700" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ahc" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  

  
<rect x="20" y="60" width="120" height="45" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="80" y="87" text-anchor="middle" fill="white" font-size="12">
Web Player
</text>

  
<rect x="20" y="120" width="120" height="45" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="80" y="147" text-anchor="middle" fill="white" font-size="12">
Mobile App
</text>

  
<rect x="20" y="180" width="120" height="45" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="80" y="207" text-anchor="middle" fill="white" font-size="12">
Smart TV
</text>

  

  
<line x1="140" y1="90" x2="220" y2="140" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="140" y1="142" x2="220" y2="142" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="140" y1="200" x2="220" y2="150" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="230" y="115" width="140" height="55" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="300" y="138" text-anchor="middle" fill="white" font-size="12">
API Gateway / LB
</text>

  
<text x="300" y="155" text-anchor="middle" fill="white" font-size="10">
(Global L7)
</text>

  

  
<line x1="370" y1="125" x2="450" y2="60" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="370" y1="135" x2="450" y2="135" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="370" y1="145" x2="450" y2="210" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="370" y1="155" x2="450" y2="285" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="370" y1="160" x2="450" y2="360" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="460" y="35" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="530" y="65" text-anchor="middle" fill="white" font-size="11">
Upload Service
</text>

  
<rect x="460" y="110" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="530" y="140" text-anchor="middle" fill="white" font-size="11">
Video API
</text>

  
<rect x="460" y="185" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="530" y="215" text-anchor="middle" fill="white" font-size="11">
Search Service
</text>

  
<rect x="460" y="260" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="530" y="290" text-anchor="middle" fill="white" font-size="11">
Recommendation
</text>

  
<rect x="460" y="335" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="530" y="365" text-anchor="middle" fill="white" font-size="11">
Comment Service
</text>

  

  
<line x1="600" y1="60" x2="680" y2="60" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="690" y="35" width="150" height="50" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="765" y="55" text-anchor="middle" fill="white" font-size="11">
Transcode Pipeline
</text>

  
<text x="765" y="72" text-anchor="middle" fill="white" font-size="10">
(Kafka + GPU Workers)
</text>

  

  
<line x1="600" y1="135" x2="680" y2="135" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="600" y1="210" x2="680" y2="210" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="690" y="110" width="150" height="50" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="765" y="130" text-anchor="middle" fill="white" font-size="11">
Vitess / MySQL
</text>

  
<text x="765" y="147" text-anchor="middle" fill="white" font-size="10">
(Video Metadata)
</text>

  
<rect x="690" y="185" width="150" height="50" rx="10" fill="#8a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="765" y="205" text-anchor="middle" fill="white" font-size="11">
Search Index
</text>

  
<text x="765" y="222" text-anchor="middle" fill="white" font-size="10">
(Inverted Index)
</text>

  
<line x1="600" y1="285" x2="680" y2="280" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="690" y="260" width="150" height="50" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="765" y="280" text-anchor="middle" fill="white" font-size="11">
Bigtable
</text>

  
<text x="765" y="297" text-anchor="middle" fill="white" font-size="10">
(Watch History, Recs)
</text>

  
<line x1="600" y1="360" x2="680" y2="340" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="690" y="335" width="150" height="50" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="765" y="355" text-anchor="middle" fill="white" font-size="11">
Redis
</text>

  
<text x="765" y="372" text-anchor="middle" fill="white" font-size="10">
(View Counts, Sessions)
</text>

  

  
<line x1="840" y1="60" x2="920" y2="60" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="930" y="35" width="160" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="1010" y="55" text-anchor="middle" fill="white" font-size="11">
GCS (Object Storage)
</text>

  
<text x="1010" y="72" text-anchor="middle" fill="white" font-size="10">
(Raw + Transcoded)
</text>

  
<rect x="930" y="110" width="160" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="1010" y="130" text-anchor="middle" fill="white" font-size="11">
CDN (Global Edge)
</text>

  
<text x="1010" y="147" text-anchor="middle" fill="white" font-size="10">
(Video Segments)
</text>

  
<line x1="1010" y1="85" x2="1010" y2="105" stroke="#aaa" marker-end="url(#ahc)">
</line>


</svg>

</details>


> **
**End-to-End:** Creator uploads 4K video → Upload Service stores raw in GCS → Kafka triggers transcode pipeline → GPU workers produce 8 resolutions × 3 codecs → Content ID checks copyright → Published → CDN distributes → Viewer searches "tutorial" → Search Service ranks by relevance + personalization → Viewer clicks → Player fetches DASH manifest → ABR selects 1080p → CDN edge serves segments → View event → Kafka → async view count increment + recommendation update → Next viewer sees video in recommendations


## 🗄️ Database Schema


### Vitess/MySQL — Video Metadata


videos


| Column           | Type         | Notes                                                 |
|------------------|--------------|-------------------------------------------------------|
| id               | VARCHAR(11)  | `PK` Base62 encoded (dQw4w9WgXcQ)                     |
| channel_id       | VARCHAR(24)  | `FK` `IDX`                                            |
| title            | VARCHAR(100) |                                                       |
| description      | TEXT         |                                                       |
| duration_seconds | INTEGER      |                                                       |
| view_count       | BIGINT       | Denormalized, updated async                           |
| like_count       | BIGINT       | Denormalized                                          |
| category_id      | INTEGER      |                                                       |
| tags             | JSON         |                                                       |
| thumbnail_url    | TEXT         |                                                       |
| manifest_url     | TEXT         | DASH MPD path                                         |
| status           | VARCHAR(20)  | processing / published / blocked / private / unlisted |
| privacy          | VARCHAR(20)  | public / unlisted / private                           |
| upload_date      | TIMESTAMPTZ  | `IDX`                                                 |


channels


| Column           | Type         | Notes        |
|------------------|--------------|--------------|
| id               | VARCHAR(24)  | `PK`         |
| name             | VARCHAR(100) |              |
| owner_user_id    | BIGINT       | `FK`         |
| subscriber_count | BIGINT       | Denormalized |
| total_views      | BIGINT       | Denormalized |
| created_at       | TIMESTAMPTZ  |              |


### Bigtable — User Data (NoSQL)


Table: user_watch_history
Row Key: {user_id}#{reverse_timestamp}
Column Family: video
  - video:id = "dQw4w9WgXcQ"
  - video:watch_pct = 0.85
  - video:timestamp = 1707500000

Table: video_comments
Row Key: {video_id}#{timestamp}#{comment_id}
Column Family: comment
  - comment:user_id = "12345"
  - comment:text = "Great tutorial!"
  - comment:likes = 42
  - comment:reply_to = null


### Sharding Strategy


**videos:** Vitess (sharded MySQL) — shard by `video_id` hash. YouTube uses Vitess (which they created) for horizontal MySQL scaling  `SHARD`


**user_watch_history:** Bigtable — row key prefix is user_id, so all history for a user is on same tablet (Bigtable's shard unit)  `SHARD`


**comments:** Shard by `video_id` — all comments for a video co-located  `SHARD`


## 💾 Cache & CDN Deep Dive


### CDN Caching Strategy


| Content                 | Cache Location      | TTL       | Hit Rate |
|-------------------------|---------------------|-----------|----------|
| Popular video segments  | Edge PoP (L1)       | 24 hr     | ~95%     |
| Moderate video segments | Regional cache (L2) | 7 days    | ~85%     |
| Long-tail videos        | Origin (GCS)        | Permanent | N/A      |
| Thumbnails              | Edge PoP            | 30 days   | ~99%     |
| Manifests (MPD/M3U8)    | Edge PoP            | 1 hr      | ~98%     |


### Redis Caching


  - **View counts:** `INCR views:{video_id}` — buffered in Redis, flushed to MySQL every minute. Prevents write amplification from billions of view events

  - **Session/auth:** User session tokens cached in Redis (TTL 30 min)

  - **Rate limiting:** Upload rate limits per user in Redis sliding window


### Google Global Cache (GGC)


Google deploys cache servers directly inside ISP networks. ISP traffic to YouTube stays within their network — reduces transit costs for ISP, reduces latency for users. Over 1,000 ISPs worldwide have GGC deployments. This is why YouTube feels "fast" even on average internet connections.


## ⚖️ Scaling Considerations


  - **Transcoding:** GPU clusters auto-scale based on upload queue depth. Spot/preemptible instances for cost savings (transcode jobs are restartable)

  - **View count hot spots:** Viral videos get millions of INCR per second. Sharded Redis counters (split into 100 sub-counters, aggregate on read) prevent hot key problems

  - **Storage costs:** ~1 exabyte total. Cold storage (Coldline) for rarely-watched videos. Per-title encoding optimizes bitrate → reduces storage by ~20%

  - **CDN capacity:** Peak traffic during evening hours per timezone. CDN auto-scales at edge. Pre-warming: when a popular creator's video is detected (subscriber notification), pre-push segments to regional caches before traffic arrives

  - **Database:** Vitess provides transparent sharding for MySQL. Add shards as data grows without application changes


## ⚖️ Tradeoffs


> **
  
> **
    ✅ Pre-transcoding All Resolutions
    

Instant playback at any quality. No transcoding delay at view time. CDN can cache fixed segments

  
  
> **
    ❌ Pre-transcoding All Resolutions
    

Massive storage cost (8+ renditions per video). Most 144p/240p renditions rarely accessed. Alternative: just-in-time transcoding for low-demand resolutions

  
  
> **
    ✅ Segmented Streaming (DASH/HLS)
    

Adaptive bitrate, works over HTTP (CDN-friendly), seekable, no special streaming server needed

  
  
> **
    ❌ Segmented Streaming
    

Higher latency than WebRTC (2-30s delay). Segment boundaries limit quality switch speed. Manifest overhead for short videos

  
  
> **
    ✅ AV1 Codec
    

30-50% bitrate savings over H.264 at same quality → massive CDN bandwidth savings at YouTube's scale

  
  
> **
    ❌ AV1 Codec
    

10x slower encoding (higher compute cost), limited hardware decoding support (newer devices only), H.264 still needed as fallback

  


## 🔄 Alternative Approaches


Just-In-Time Transcoding


Only transcode on first view at that resolution. Saves storage for long-tail content that's rarely watched. Used by some smaller platforms. Tradeoff: first viewer experiences delay, requires fast transcoding infrastructure (GPU always warm).


WebRTC for Low-Latency (Live Streaming)


YouTube Live uses WebRTC for ultra-low-latency streams (<1s). Sub-second latency enables real-time interaction. But doesn't scale to millions of viewers without SFU cascading. DASH/HLS used for most live streams with 5-30s latency instead.


P2P-Assisted CDN


Viewers share video segments with nearby peers (like BitTorrent). Reduces CDN cost by offloading bandwidth to viewers. Used by some Chinese platforms (Bilibili). Drawback: reliability depends on peer availability, privacy concerns, NAT traversal complexity.


## 📚 Additional Information


### Recommendation Engine


YouTube's recommendation drives 70%+ of watch time. Two-stage architecture: (1) Candidate generation — collaborative filtering from watch history (deep neural network with embedding layers), retrieves ~1000 candidates. (2) Ranking — predicts expected watch time using features: user history, video features (title, tags, freshness, channel), context (time of day, device). Optimizes for engagement metrics (watch time, not just clicks).


### Video ID Design


YouTube video IDs are 11 characters, Base64url encoded: [A-Za-z0-9_-]. This gives 64^11 = 73.8 quadrillion possible IDs. IDs are not sequential (security through obscurity — can't enumerate all videos). Generated randomly with collision check.


### Monetization Infrastructure


Ad serving during video playback: pre-roll (before video), mid-roll (during, at natural breaks), post-roll. Ad decisions made in real-time by auction system (similar to Ad Click Aggregator design). Revenue split: 55% to creator, 45% to YouTube. Super Chat, Memberships, and Merchandise Shelf are additional revenue streams, each with their own microservices.