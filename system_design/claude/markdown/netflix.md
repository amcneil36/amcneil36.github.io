# 🎬 System Design: Netflix (Video Streaming Service)


## 📋 Functional Requirements


  - Users can browse a personalized catalog and stream movies/shows on demand

  - Content is available in multiple resolutions with adaptive bitrate streaming

  - Users can manage profiles, watchlists, and continue watching from any device

  - Content ingestion pipeline processes new titles through encoding and DRM packaging


## 🔒 Non-Functional Requirements


  - **Scale:** 260M+ subscribers, 15,000+ titles, peak ~15% of global internet traffic

  - **Availability:** 99.99% uptime — "Netflix is always on"

  - **Low latency:** Playback start <3s, zero rebuffering on stable connections

  - **Global distribution:** Serve content from edge (Open Connect Appliances) in 1,000+ ISP locations

  - **Personalization:** Every user sees a different homepage — ML-driven row selection, artwork, and ranking


## 🔄 Flow 1 — Content Ingestion & Encoding


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 380" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="160" width="120" height="60" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="80" y="188" text-anchor="middle" fill="white" font-size="12">
Content Studio
</text>

  
<text x="80" y="205" text-anchor="middle" fill="white" font-size="10">
(Mezzanine File)
</text>

  
<line x1="140" y1="190" x2="210" y2="190" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="220" y="160" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="290" y="185" text-anchor="middle" fill="white" font-size="12">
Ingestion Service
</text>

  
<text x="290" y="202" text-anchor="middle" fill="white" font-size="10">
(QC + Validation)
</text>

  
<line x1="360" y1="190" x2="430" y2="190" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="440" y="160" width="150" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="515" y="185" text-anchor="middle" fill="white" font-size="11">
Shot-Based Encoder
</text>

  
<text x="515" y="202" text-anchor="middle" fill="white" font-size="10">
(Per-Title Encoding)
</text>

  
<line x1="590" y1="175" x2="660" y2="120" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="590" y1="205" x2="660" y2="260" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="670" y="90" width="150" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="745" y="113" text-anchor="middle" fill="white" font-size="11">
DRM Packager
</text>

  
<text x="745" y="130" text-anchor="middle" fill="white" font-size="10">
(Widevine/FairPlay/PR)
</text>

  
<rect x="670" y="235" width="150" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="745" y="258" text-anchor="middle" fill="white" font-size="11">
Subtitle/Audio
</text>

  
<text x="745" y="275" text-anchor="middle" fill="white" font-size="10">
(30+ Languages)
</text>

  
<line x1="820" y1="118" x2="900" y2="180" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="820" y1="262" x2="900" y2="195" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="910" y="160" width="150" height="60" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="985" y="185" text-anchor="middle" fill="white" font-size="11">
S3 → Open Connect
</text>

  
<text x="985" y="202" text-anchor="middle" fill="white" font-size="10">
(CDN Distribution)
</text>


</svg>

</details>


### Step-by-Step


  1. **Mezzanine delivery:** Studios deliver high-quality master files (ProRes 4444, 4K/HDR, ~100GB for a movie) via Aspera or dedicated upload portal

  2. **Quality Control:** Automated checks — audio sync, color space validation, black frame detection, loudness normalization (EBU R128 standard). Issues flagged for manual review

  3. **Per-Title Encoding:** Netflix's innovation — analyzes video complexity per-shot. Simple scenes (dialogue) get lower bitrate; complex scenes (action, fire) get higher. Optimizes unique bitrate ladder per title. A cartoon needs different encoding profile than Game of Thrones

  4. **Shot-based encoding:** Video split at scene boundaries (shot detection ML). Each shot encoded independently at optimal quality, then concatenated. Results in 20-30% bandwidth savings vs fixed-ladder encoding

  5. **DRM packaging:** Same content encrypted with multiple DRM systems — CENC (Common Encryption) with Widevine (Android/Chrome), FairPlay (Apple), PlayReady (Windows/Xbox). Single encrypted file, multiple license servers

  6. **Multi-language:** 30+ subtitle languages, 20+ audio tracks, audio description tracks. Each a separate stream selectable by player

  7. **Open Connect distribution:** Encoded content pushed to S3 → replicated to Open Connect Appliances (OCAs) in ISP data centers worldwide during off-peak hours


> **
**Example:** "Stranger Things S5E1" delivered as 100GB ProRes 4K HDR mezzanine → QC passes → Per-title analysis: episode has dark scenes (Upside Down) needing higher bitrate + bright dialogue scenes → Custom bitrate ladder generated → Encoded into 12 renditions (from 235kbps mobile to 16Mbps 4K HDR) → DRM encrypted → 34 subtitle tracks + 24 audio tracks → Pushed to 17,000+ OCA servers in 6,000+ ISP locations → Available globally at midnight local time for each region


### 🔍 Component Deep Dives — Ingestion


Per-Title Encoding (Netflix Innovation)


**Problem:** Fixed bitrate ladder wastes bandwidth. A simple animation at 4K needs ~5Mbps; a fast-action sports scene needs 15Mbps at the same quality (VMAF score)


**Solution:** For each title, run multiple encodes at different bitrate/resolution combinations. Plot quality (VMAF) vs bitrate for each resolution. Select the Pareto-optimal ladder — convex hull of quality-bitrate tradeoff


**VMAF (Video Multi-Method Assessment Fusion):** Netflix's perceptual quality metric. Scores 0-100 (target: VMAF ≥ 93 for HD, ≥ 80 for mobile). Combines spatial detail, temporal complexity, and human-calibrated models


Open Connect CDN


**Architecture:** Netflix builds custom hardware appliances (OCA boxes — each with 100-200TB storage, 40-100 Gbps NICs) and deploys them inside ISP data centers for free. ISPs benefit from reduced transit traffic; Netflix benefits from lower latency and bandwidth costs


**Content placement algorithm:** Popular titles cached on all OCAs. Regional content (Bollywood in India, anime in Japan) cached on regional OCAs. Long-tail content served from regional hubs or AWS origin


**Scale:** 17,000+ OCA servers across 6,000+ ISP locations in 175+ countries. Serves ~95% of Netflix traffic directly from ISP networks


## 🔄 Flow 2 — Video Playback (Streaming)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 400" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="170" width="110" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="198" text-anchor="middle" fill="white" font-size="13">
Device
</text>

  
<text x="75" y="215" text-anchor="middle" fill="white" font-size="10">
(Netflix App)
</text>

  
<line x1="130" y1="190" x2="200" y2="190" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="210" y="170" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="280" y="195" text-anchor="middle" fill="white" font-size="12">
Playback Service
</text>

  
<text x="280" y="212" text-anchor="middle" fill="white" font-size="10">
(License + Manifest)
</text>

  
<line x1="350" y1="180" x2="420" y2="120" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="350" y1="200" x2="420" y2="260" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="430" y="90" width="150" height="55" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="505" y="113" text-anchor="middle" fill="white" font-size="11">
DRM License Server
</text>

  
<text x="505" y="130" text-anchor="middle" fill="white" font-size="10">
(Decrypt Keys)
</text>

  
<rect x="430" y="235" width="150" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="505" y="258" text-anchor="middle" fill="white" font-size="11">
Steering Service
</text>

  
<text x="505" y="275" text-anchor="middle" fill="white" font-size="10">
(Best OCA Selection)
</text>

  
<line x1="580" y1="118" x2="650" y2="188" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="580" y1="262" x2="650" y2="195" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="660" y="170" width="140" height="60" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="730" y="195" text-anchor="middle" fill="white" font-size="12">
Open Connect OCA
</text>

  
<text x="730" y="212" text-anchor="middle" fill="white" font-size="10">
(ISP Edge Cache)
</text>

  
<line x1="800" y1="200" x2="870" y2="200" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="880" y="170" width="120" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="940" y="195" text-anchor="middle" fill="white" font-size="12">
Playback
</text>

  
<text x="940" y="212" text-anchor="middle" fill="white" font-size="10">
(Adaptive ABR)
</text>


</svg>

</details>


### Step-by-Step


  1. **Play button pressed:** Client calls Playback Service: `POST /api/play { title_id, profile_id, device_type, network_info }`

  2. **DRM License:** Playback Service issues DRM license — client must prove device integrity (Widevine L1 for HD, L3 for SD-only). License contains decryption keys bound to device, expires in 24-48 hours

  3. **Manifest generation:** Dynamic manifest created for this device — includes only supported resolutions/codecs (4K HDR only if device supports it, mobile gets lower bitrate tiers)

  4. **OCA steering:** Steering Service selects optimal OCA based on: (1) user's ISP, (2) OCA health/load, (3) content availability. Returns ranked list of OCA URLs. Uses BGP routing data + probe measurements

  5. **Content delivery:** Client requests segments from selected OCA via HTTPS. Adaptive bitrate algorithm (Netflix's custom ABR) adjusts quality based on buffer + throughput + device capabilities

  6. **Quality of Experience (QoE) reporting:** Client reports rebuffer events, bitrate switches, startup time back to Netflix → feeds into A/B tests for algorithm improvements


> **
**Example:** User plays "Stranger Things" on Samsung TV → Playback Service: device supports Widevine L1 (4K HDR), issues license → Steering selects OCA at Comcast San Francisco PoP → Manifest: 12 video renditions (235kbps–16Mbps) + H.265 codec for this TV → ABR starts at 5Mbps, ramps to 16Mbps 4K HDR within 15 seconds → All traffic stays within Comcast's network (OCA in their datacenter) → Zero buffering, startup in 1.2s


### 🔍 Component Deep Dives — Streaming


Netflix ABR Algorithm


Netflix developed custom ABR that outperforms standard throughput-based or buffer-based approaches:


  - **Throughput prediction:** Harmonic mean of recent segment download speeds (more conservative than arithmetic mean)

  - **Buffer reservoir:** Maintains target buffer of 30-60s. Quality changes are gradual — no jumping from 4K to 240p

  - **Startup optimization:** Start with lower quality for fast first-frame. Ramp up aggressively once buffer > 10s

  - **Device-aware:** Phone on cellular? Cap at 720p by default. 4K TV on fiber? Go to max immediately


DRM — Multi-DRM Strategy


**CENC (Common Encryption):** Content encrypted once, multiple DRM license servers can decrypt. Single file serves all platforms


| DRM System       | Platform                   | Max Quality |
|------------------|----------------------------|-------------|
| Widevine L1      | Android, Chrome (hardware) | 4K HDR      |
| Widevine L3      | Chrome (software)          | 720p        |
| FairPlay         | iOS, Safari, Apple TV      | 4K HDR      |
| PlayReady SL3000 | Windows, Xbox, Edge        | 4K HDR      |


**Why hardware DRM matters:** Studios mandate hardware-backed DRM (L1/SL3000) for 4K/HDR content — prevents screen capture tools from recording in high quality


## 🔄 Flow 3 — Personalized Homepage (Recommendation)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 320" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="130" width="110" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="165" text-anchor="middle" fill="white" font-size="14">
User
</text>

  
<line x1="130" y1="160" x2="200" y2="160" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="210" y="130" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="280" y="155" text-anchor="middle" fill="white" font-size="12">
Homepage Service
</text>

  
<text x="280" y="172" text-anchor="middle" fill="white" font-size="10">
(Row Assembly)
</text>

  
<line x1="350" y1="145" x2="420" y2="100" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<line x1="350" y1="160" x2="420" y2="160" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<line x1="350" y1="175" x2="420" y2="220" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="430" y="70" width="160" height="55" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="510" y="93" text-anchor="middle" fill="white" font-size="11">
Row Selection Model
</text>

  
<text x="510" y="110" text-anchor="middle" fill="white" font-size="10">
(Which rows to show)
</text>

  
<rect x="430" y="135" width="160" height="55" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="510" y="158" text-anchor="middle" fill="white" font-size="11">
Item Ranking Model
</text>

  
<text x="510" y="175" text-anchor="middle" fill="white" font-size="10">
(Order within row)
</text>

  
<rect x="430" y="200" width="160" height="55" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="510" y="223" text-anchor="middle" fill="white" font-size="11">
Artwork Selection
</text>

  
<text x="510" y="240" text-anchor="middle" fill="white" font-size="10">
(Personalized thumbnails)
</text>

  
<line x1="590" y1="97" x2="670" y2="155" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<line x1="590" y1="162" x2="670" y2="160" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<line x1="590" y1="227" x2="670" y2="165" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="680" y="130" width="150" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="755" y="155" text-anchor="middle" fill="white" font-size="12">
Assembled Page
</text>

  
<text x="755" y="172" text-anchor="middle" fill="white" font-size="10">
(~40 rows, ranked)
</text>


</svg>

</details>


### Step-by-Step


  1. **Homepage request:** User opens Netflix → `GET /api/homepage?profile_id=prof_123`

  2. **Row Selection:** From ~10K possible rows (genres, "Because you watched X", "Trending", "New Releases"), ML model selects ~40 most relevant rows for this user. Features: watch history, ratings, time of day, day of week, device type

  3. **Item Ranking:** Within each row, rank ~75 titles. Candidate generation (collaborative filtering) → ranking model (probability of play × predicted engagement). Two-tower architecture: user tower (history embeddings) + item tower (content features)

  4. **Artwork Selection:** For each title, Netflix has 9-15 different artwork variants. ML selects which image to show this user. If user watches a lot of comedies → show comedian's face. If user watches action → show explosion scene. Personalized per-user artwork increases CTR by ~20%

  5. **Page assembly:** Combine rows + ranked items + personalized artwork → single response. Pre-computed during off-peak, cached per profile with 5-min TTL. Real-time signals (continue watching, new episodes) injected at request time


> **
**Example:** User who watches Korean dramas + sci-fi → Row Selection: "Korean Dramas", "Because You Watched Squid Game", "Trending Now", "Sci-Fi Adventures", "Continue Watching" (top 5 of 40 rows) → Within "Sci-Fi": "3 Body Problem" ranked #1 (new, matches taste) → Artwork for "3 Body Problem" shows the VR headset scene (sci-fi interest) vs romance couple scene (for romance fans). Every user sees a different Netflix homepage.


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 650" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ahc" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  

  
<text x="500" y="25" fill="#ff3d47" font-size="14" font-weight="bold">
Control Plane (AWS)
</text>

  
<rect x="40" y="40" width="900" height="320" rx="15" fill="none" stroke="#ff3d4755" stroke-width="1" stroke-dasharray="5,5">
</rect>

  

  
<rect x="60" y="60" width="110" height="40" rx="8" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="115" y="85" text-anchor="middle" fill="white" font-size="11">
Netflix Apps
</text>

  
<line x1="170" y1="80" x2="240" y2="80" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="250" y="55" width="130" height="50" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="315" y="78" text-anchor="middle" fill="white" font-size="11">
Zuul (API Gateway)
</text>

  
<text x="315" y="92" text-anchor="middle" fill="white" font-size="9">
(Routing + Auth)
</text>

  

  
<line x1="380" y1="70" x2="440" y2="70" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="380" y1="80" x2="440" y2="140" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="380" y1="90" x2="440" y2="210" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="380" y1="95" x2="440" y2="280" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="450" y="45" width="140" height="45" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="520" y="72" text-anchor="middle" fill="white" font-size="10">
Homepage / Recommendation
</text>

  
<rect x="450" y="115" width="140" height="45" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="520" y="142" text-anchor="middle" fill="white" font-size="10">
Playback Service
</text>

  
<rect x="450" y="185" width="140" height="45" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="520" y="212" text-anchor="middle" fill="white" font-size="10">
Search Service
</text>

  
<rect x="450" y="255" width="140" height="45" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="520" y="282" text-anchor="middle" fill="white" font-size="10">
User / Profile Service
</text>

  

  
<line x1="590" y1="68" x2="660" y2="68" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="590" y1="138" x2="660" y2="138" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="590" y1="208" x2="660" y2="208" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="590" y1="278" x2="660" y2="278" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="670" y="45" width="130" height="45" rx="8" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="735" y="72" text-anchor="middle" fill="white" font-size="10">
Cassandra (Catalog)
</text>

  
<rect x="670" y="115" width="130" height="45" rx="8" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="735" y="142" text-anchor="middle" fill="white" font-size="10">
DRM License Store
</text>

  
<rect x="670" y="185" width="130" height="45" rx="8" fill="#8a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="735" y="212" text-anchor="middle" fill="white" font-size="10">
Elasticsearch
</text>

  
<rect x="670" y="255" width="130" height="45" rx="8" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="735" y="282" text-anchor="middle" fill="white" font-size="10">
EVCache (Memcached)
</text>

  

  
<text x="500" y="395" fill="#ff3d47" font-size="14" font-weight="bold">
Data Plane (Open Connect CDN)
</text>

  
<rect x="40" y="410" width="900" height="120" rx="15" fill="none" stroke="#ff3d4755" stroke-width="1" stroke-dasharray="5,5">
</rect>

  
<rect x="80" y="435" width="140" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="150" y="465" text-anchor="middle" fill="white" font-size="11">
OCA (ISP A)
</text>

  
<rect x="260" y="435" width="140" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="330" y="465" text-anchor="middle" fill="white" font-size="11">
OCA (ISP B)
</text>

  
<rect x="440" y="435" width="140" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="510" y="465" text-anchor="middle" fill="white" font-size="11">
OCA (ISP C)
</text>

  
<rect x="620" y="435" width="140" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="690" y="465" text-anchor="middle" fill="white" font-size="11">
OCA Hub (Region)
</text>

  
<rect x="800" y="435" width="130" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="865" y="465" text-anchor="middle" fill="white" font-size="11">
S3 Origin (AWS)
</text>

  

  
<line x1="520" y1="160" x2="200" y2="430" stroke="#4a9eff" stroke-dasharray="5,5" marker-end="url(#ahc)">
</line>

  
<text x="300" y="310" fill="#4a9eff" font-size="10">
Steering → Closest OCA
</text>


</svg>

</details>


> **
**End-to-End:** User opens Netflix on TV → Zuul routes to Homepage service → ML selects 40 personalized rows with ranked titles + personalized artwork → User selects "Stranger Things" → Playback Service: DRM license issued (Widevine L1), steering selects OCA in user's ISP → TV's ABR downloads encrypted segments from OCA 2ms away → DRM module decrypts in hardware → 4K HDR playback starts in 1.5s → Viewing event → Kafka → updates recommendation models + "Continue Watching" position. Control plane (AWS) handles all API calls; data plane (Open Connect) handles all video bytes.


## 🗄️ Database Schema


### Cassandra — Content Catalog + User Data


Table: titles
Partition Key: title_id
Clustering: locale
  - title_id: UUID
  - locale: text (en-US, ko-KR, etc.)
  - name: text
  - synopsis: text
  - release_year: int
  - maturity_rating: text
  - genres: set<text>
  - cast: list<text>
  - duration_minutes: int
  - type: text (movie / series)

Table: viewing_history
Partition Key: profile_id
Clustering: viewed_at DESC
  - profile_id: UUID
  - viewed_at: timestamp
  - title_id: UUID
  - watch_duration: int
  - position_seconds: int  -- resume position
  - device_type: text

Table: my_list (Watchlist)
Partition Key: profile_id
Clustering: added_at DESC
  - profile_id: UUID
  - added_at: timestamp
  - title_id: UUID


### MySQL — Account/Billing (ACID needed)


| Column               | Type         | Notes                               |
|----------------------|--------------|-------------------------------------|
| account_id           | UUID         | `PK`                                |
| email                | VARCHAR(255) | `IDX`                               |
| plan_type            | VARCHAR(20)  | basic / standard / premium          |
| billing_status       | VARCHAR(20)  | active / past_due / cancelled       |
| payment_method_token | VARCHAR(64)  | Tokenized card                      |
| next_billing_date    | DATE         |                                     |
| country              | VARCHAR(3)   | Content licensing varies by country |


### EVCache (Memcached) — Hot Data


# User's homepage (pre-computed)
Key: homepage:{profile_id}
Value: { rows: [...], ttl: 300 }

# Title metadata (high-traffic)
Key: title:{title_id}:{locale}
Value: { name, synopsis, artwork_urls, ratings }

# Viewing position (continue watching)
Key: position:{profile_id}:{title_id}
Value: { seconds: 1842, updated_at: ... }


### Sharding


**Cassandra:** Consistent hashing on partition key. Viewing history partitioned by profile_id — all history for one user on same node  `SHARD`


**MySQL:** Vitess-like sharding by account_id for billing data  `SHARD`


## 💾 Cache & CDN Deep Dive


### EVCache (Netflix's Memcached Layer)


Netflix's distributed caching layer — wraps Memcached with replication across availability zones. Every read goes to cache first; cache miss falls through to Cassandra.


| Cache            | Key Pattern             | TTL     | Strategy                                    |
|------------------|-------------------------|---------|---------------------------------------------|
| Homepage         | `homepage:{profile_id}` | 5 min   | Pre-computed, write-through                 |
| Title metadata   | `title:{id}:{locale}`   | 1 hr    | Read-through                                |
| Viewing position | `pos:{profile}:{title}` | 30 days | Write-through (updated on every pause/stop) |
| Recommendations  | `recs:{profile_id}`     | 24 hr   | Pre-computed offline, refreshed daily       |


### Open Connect CDN


**Content placement:** Popularity-based. Top 20% of content cached on all OCAs (covers ~80% of views). Regional content pushed to local OCAs. Cache fill happens during off-peak hours (2-6 AM local time) to avoid competing with viewing traffic.


**Cache eviction:** LRU-based. Each OCA has 100-200TB. Least-recently-viewed content evicted. Eviction is rare because Netflix's catalog is finite (~15K titles) and most are pre-cached.


## ⚖️ Tradeoffs


> **
  
> **
    ✅ Open Connect (Own CDN)
    

Total control over content delivery, lowest possible latency (ISP co-location), no CDN vendor costs, custom hardware optimized for streaming

  
  
> **
    ❌ Open Connect
    

Massive capital investment (~$100M+ in hardware), ISP relationship management in 175+ countries, hardware refresh cycles, maintenance logistics

  
  
> **
    ✅ Per-Title Encoding
    

20-30% bandwidth savings × billions of streams = massive cost savings + better quality for viewers

  
  
> **
    ❌ Per-Title Encoding
    

Compute-intensive analysis per title (multiple trial encodes), longer ingestion pipeline, complex infrastructure to maintain custom encoding profiles per title

  
  
> **
    ✅ Cassandra for User Data
    

Always-available (AP in CAP), linear horizontal scaling, great for write-heavy viewing history, multi-DC replication

  
  
> **
    ❌ Cassandra for User Data
    

Eventually consistent (user might not see last viewing position for a few seconds across devices), no JOINs, schema design requires upfront query planning

  


## 🔄 Alternative Approaches


Third-Party CDN (Akamai/CloudFront)


Easier to set up, no hardware investment. Akamai has 300K+ servers globally. Netflix actually migrated AWAY from CDNs to Open Connect in 2012 because CDN costs at Netflix's scale were astronomical and they needed more control over content placement algorithms.


Just-In-Time Transcoding (vs Pre-encoding)


Encode only when someone requests a specific resolution. Saves storage for rarely-watched content. Used by some smaller platforms. Not viable for Netflix because first-viewer experience would be degraded, and their catalog size (15K titles) makes pre-encoding feasible.


Microservices Chaos Engineering


Netflix pioneered chaos engineering (Chaos Monkey, Chaos Kong). Instead of preventing failures, they actively inject failures in production to ensure resilience. Alternative: traditional redundancy/failover. Netflix's approach creates confidence that any component failure is gracefully handled — critical at their scale.


## 📚 Additional Information


### Netflix OSS (Open Source Stack)


  - **Zuul:** API gateway — routing, authentication, rate limiting, load shedding

  - **Eureka:** Service discovery — microservices register/discover each other

  - **Hystrix:** Circuit breaker — prevents cascade failures when downstream service is down (now replaced by Resilience4j)

  - **Conductor:** Workflow orchestration — manages multi-step encoding/ingestion workflows

  - **EVCache:** Distributed caching (Memcached wrapper with cross-AZ replication)


### Multi-Region Active-Active


Netflix runs in 3 AWS regions (us-east-1, us-west-2, eu-west-1) in active-active configuration. All regions serve traffic simultaneously. If one region goes down, traffic automatically shifts to surviving regions via Route 53 DNS failover. Tested regularly via Chaos Kong (simulates entire region failure).


### Content Licensing & Geofencing


Content availability varies by country due to licensing agreements. Catalog service filters titles based on user's country (derived from IP geolocation). VPN detection employed to enforce geographic restrictions. Each title has a `country_availability` map in Cassandra.