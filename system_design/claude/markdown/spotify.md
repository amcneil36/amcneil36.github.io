# 🎵 System Design: Spotify (Music Streaming)


## 📋 Functional Requirements


  - Users can search for and stream songs, albums, artists, and podcasts

  - Users can create playlists, follow artists, and save songs to library

  - Personalized recommendations: Discover Weekly, Daily Mix, Release Radar

  - Support offline downloads, cross-device sync, and social features (collaborative playlists)


## 🔒 Non-Functional Requirements


  - **Scale:** 600M+ users, 100M+ tracks, 5M+ podcasts, billions of streams/day

  - **Low latency:** Song playback starts in <200ms, gapless playback between tracks

  - **High availability:** 99.99% — music must always play

  - **Audio quality:** Multiple bitrates (96/160/320 kbps for Ogg Vorbis, lossless for Premium)

  - **Global CDN:** Low-latency audio delivery worldwide


## 🔄 Flow 1 — Music Playback (Streaming)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 380" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="160" width="110" height="55" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="192" text-anchor="middle" fill="white" font-size="13">
Spotify App
</text>

  
<line x1="130" y1="187" x2="195" y2="187" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="205" y="160" width="130" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="270" y="183" text-anchor="middle" fill="white" font-size="12">
Playback Service
</text>

  
<text x="270" y="200" text-anchor="middle" fill="white" font-size="10">
(Track Resolve)
</text>

  
<line x1="335" y1="175" x2="400" y2="120" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="335" y1="195" x2="400" y2="250" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="410" y="95" width="140" height="50" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="480" y="118" text-anchor="middle" fill="white" font-size="11">
DRM License
</text>

  
<text x="480" y="133" text-anchor="middle" fill="white" font-size="10">
(Widevine)
</text>

  
<rect x="410" y="230" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="480" y="253" text-anchor="middle" fill="white" font-size="11">
Audio CDN Router
</text>

  
<text x="480" y="268" text-anchor="middle" fill="white" font-size="10">
(Nearest Edge)
</text>

  
<line x1="550" y1="255" x2="620" y2="255" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="630" y="230" width="140" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="700" y="253" text-anchor="middle" fill="white" font-size="11">
CDN Edge (GCS/S3)
</text>

  
<text x="700" y="268" text-anchor="middle" fill="white" font-size="10">
(Audio Files)
</text>

  
<line x1="770" y1="255" x2="840" y2="255" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="850" y="230" width="130" height="50" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="915" y="253" text-anchor="middle" fill="white" font-size="11">
Audio Playback
</text>

  
<text x="915" y="268" text-anchor="middle" fill="white" font-size="10">
(Ogg Vorbis/AAC)
</text>


</svg>

</details>


### Step-by-Step


  1. **User taps play:** Client sends `POST /api/v1/play { track_id, context: "playlist:abc123", position: 0 }`

  2. **Playback Service:** Resolves track_id to audio file URL. Checks: user subscription (free → 160kbps max, Premium → 320kbps/lossless), geographic licensing (track available in user's country?), selects codec (Ogg Vorbis for desktop/Android, AAC for iOS)

  3. **DRM License:** Issues playback license — encrypted key bound to session. Prevents downloading for offline piracy. Free tier: mandatory ads between tracks

  4. **CDN routing:** Audio CDN Router selects nearest edge PoP. Audio files pre-segmented into chunks (~128KB each, ~10s of audio at 128kbps)

  5. **Prefetching:** Client fetches first chunk of NEXT track while current track is still playing → enables gapless playback (crossfade). Also prefetches 2-3 tracks ahead in playlist

  6. **Stream event:** After 30 seconds of playback, a "stream" event is recorded → counts toward royalty payment to artist. Kafka → analytics pipeline


> **
**Example:** Premium user plays "Shape of You" from Discover Weekly playlist → Playback Service: Premium → 320kbps Ogg Vorbis → DRM license issued → CDN edge in Chicago (user is in IL) → First chunk delivered in 80ms → Playback starts → Client prefetches next song "Blinding Lights" → Gapless transition → 30s stream event fires → Ed Sheeran earns ~$0.004 per stream


### 🔍 Component Deep Dives — Playback


Audio Encoding


| Tier               | Codec            | Bitrate     | Quality          |
|--------------------|------------------|-------------|------------------|
| Free (Low)         | Ogg Vorbis / AAC | 96 kbps     | ~3.5 MB per song |
| Free (Normal)      | Ogg Vorbis / AAC | 160 kbps    | ~5.5 MB per song |
| Premium (High)     | Ogg Vorbis / AAC | 320 kbps    | ~10 MB per song  |
| Premium (Lossless) | FLAC             | ~1,411 kbps | ~50 MB per song  |


Each track stored in multiple quality variants. Client selects based on subscription tier + network conditions (mobile data → auto-switch to lower quality)


Gapless Playback & Crossfade


**Gapless:** Client decodes last chunk of current track and first chunk of next track simultaneously, seamlessly concatenating audio buffers. Critical for live albums and classical music where silence between tracks ruins the experience.


**Crossfade:** Optional feature — fades out current track while fading in next over 1-12 seconds. Implemented in client's audio engine by mixing two audio streams with opposing volume ramps.


Offline Downloads


Premium users can download tracks for offline playback. Encrypted audio files stored on device with DRM license (expires after 30 days without connectivity). Device must connect to Spotify servers monthly to renew licenses — prevents account sharing abuse.


## 🔄 Flow 2 — Search


### Step-by-Step


  1. **User types query:** `GET /api/v1/search?q=taylor+swift+shake&type=track,artist,album,playlist`

  2. **Autocomplete:** As-you-type suggestions from prefix trie/n-gram index. Results appear after 2-3 characters

  3. **Search Service:** Elasticsearch multi-index query — searches tracks, artists, albums, playlists simultaneously. Custom scoring: exact title match > partial match > description match. Artist popularity boosts results

  4. **Personalization:** If user frequently listens to pop, boost pop results. If user has Taylor Swift in library, boost her results higher

  5. **Return categorized results:** Top Result (artist/track), Songs, Artists, Albums, Playlists, Podcasts


> **
**Example:** User types "taylor swift shake" → Autocomplete: "Shake It Off - Taylor Swift" → Search returns: Top Result: Taylor Swift (artist), Songs: "Shake It Off", "Shake It Off (Remix)", Albums: "1989", Playlists: "Taylor Swift Radio" → All in <150ms


## 🔄 Flow 3 — Discover Weekly (Recommendation)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 320" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="130" width="140" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="90" y="155" text-anchor="middle" fill="white" font-size="11">
Listening History
</text>

  
<text x="90" y="172" text-anchor="middle" fill="white" font-size="10">
(User Taste Profile)
</text>

  
<line x1="160" y1="157" x2="230" y2="157" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="240" y="130" width="150" height="55" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="315" y="155" text-anchor="middle" fill="white" font-size="11">
Collaborative Filter
</text>

  
<text x="315" y="172" text-anchor="middle" fill="white" font-size="10">
(Matrix Factorization)
</text>

  
<line x1="390" y1="157" x2="460" y2="157" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="470" y="130" width="150" height="55" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="545" y="155" text-anchor="middle" fill="white" font-size="11">
Content-Based Filter
</text>

  
<text x="545" y="172" text-anchor="middle" fill="white" font-size="10">
(Audio Features / NLP)
</text>

  
<line x1="620" y1="157" x2="690" y2="157" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="700" y="130" width="150" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="775" y="155" text-anchor="middle" fill="white" font-size="11">
Playlist Assembler
</text>

  
<text x="775" y="172" text-anchor="middle" fill="white" font-size="10">
(30 tracks / Monday)
</text>


</svg>

</details>


### Step-by-Step


  1. **User taste profile:** Built from listening history — weighted by recency, skip rate, save/like actions. Represents user as vector in embedding space

  2. **Collaborative Filtering:** "Users who listen to similar music to you also listen to X." Matrix factorization (ALS algorithm) on user-track interaction matrix → discovers latent taste factors

  3. **Content-Based Filtering:** Audio analysis of tracks user likes — tempo, energy, danceability, key, valence (happiness). Raw audio features extracted by CNN. Also NLP on blog posts, reviews for cultural context

  4. **Candidate generation:** ~1000 candidate tracks from both approaches. Filter: remove tracks user already knows (listened >3 times), remove explicit content if user has filter on

  5. **Ranking:** Predict probability user will save/like each track. Select top 30. Ensure diversity: genre variety, mix of familiar-adjacent and discovery, balance popular and niche

  6. **Delivery:** Computed batch (Sunday night) for all 400M+ users. Playlist updated every Monday morning. Stored in Cassandra, cached in Redis


> **
**Example:** User listens to indie rock and synthwave → Collaborative: "Users like you also enjoy Tame Impala, MGMT" → Content-based: audio features suggest user likes mid-tempo, high-energy, atmospheric → Candidate pool: 1000 tracks → Ranked by predicted save rate → Final 30: mix of indie rock deep cuts + synthwave newcomers + one jazz-adjacent surprise → "Discover Weekly" updated Monday 5 AM


### 🔍 Spotify's Three Recommendation Engines


1. Collaborative Filtering (Implicit Matrix Factorization)


User-track matrix (600M users × 100M tracks). Decompose into User matrix (600M × 128) and Track matrix (100M × 128). Each user/track represented as 128-dimensional vector. Similar users → similar vectors. Trained with ALS (Alternating Least Squares) on Hadoop cluster.


2. NLP (Cultural Context)


Crawl music blogs, reviews, social media for text about artists/tracks. Word2Vec-style embeddings → tracks mentioned in similar cultural contexts get similar embeddings. Captures "vibe" that audio analysis misses (e.g., "summer anthems", "study music").


3. Audio Analysis (CNN)


Raw audio spectrogram → Convolutional Neural Network → audio feature vector. Enables cold-start recommendations: new track with zero listens can be recommended based on how it sounds. Features: tempo, key, mode, energy, danceability, speechiness, acousticness, instrumentalness, liveness, valence.


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 500" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ahc" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="60" width="110" height="40" rx="8" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="85" text-anchor="middle" fill="white" font-size="11">
Mobile / Desktop
</text>

  
<line x1="130" y1="80" x2="200" y2="100" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="210" y="80" width="120" height="50" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="270" y="110" text-anchor="middle" fill="white" font-size="11">
API Gateway
</text>

  
<line x1="330" y1="95" x2="400" y2="55" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="330" y1="100" x2="400" y2="100" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="330" y1="110" x2="400" y2="145" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="330" y1="120" x2="400" y2="190" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="410" y="35" width="130" height="40" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="475" y="60" text-anchor="middle" fill="white" font-size="10">
Playback Service
</text>

  
<rect x="410" y="80" width="130" height="40" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="475" y="105" text-anchor="middle" fill="white" font-size="10">
Search Service
</text>

  
<rect x="410" y="125" width="130" height="40" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="475" y="150" text-anchor="middle" fill="white" font-size="10">
Playlist Service
</text>

  
<rect x="410" y="170" width="130" height="40" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="475" y="195" text-anchor="middle" fill="white" font-size="10">
Recommendation
</text>

  
<line x1="540" y1="55" x2="620" y2="55" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="540" y1="100" x2="620" y2="100" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="540" y1="145" x2="620" y2="145" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="540" y1="190" x2="620" y2="190" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="630" y="35" width="130" height="40" rx="8" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="695" y="60" text-anchor="middle" fill="white" font-size="10">
Audio CDN (GCS)
</text>

  
<rect x="630" y="80" width="130" height="40" rx="8" fill="#8a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="695" y="105" text-anchor="middle" fill="white" font-size="10">
Elasticsearch
</text>

  
<rect x="630" y="125" width="130" height="40" rx="8" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="695" y="150" text-anchor="middle" fill="white" font-size="10">
Cassandra
</text>

  
<rect x="630" y="170" width="130" height="40" rx="8" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="695" y="195" text-anchor="middle" fill="white" font-size="10">
Redis + Kafka
</text>

  

  
<rect x="410" y="260" width="350" height="45" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="585" y="287" text-anchor="middle" fill="white" font-size="11">
ML Pipeline (Hadoop/Spark — Batch Recommendations)
</text>


</svg>

</details>


> **
**End-to-End:** User opens Spotify → Home shows personalized playlists (from Cassandra/Redis) → User plays "Discover Weekly" → Playback Service resolves track → CDN edge delivers 320kbps Ogg Vorbis → Client prefetches next 3 tracks → User skips track 5 after 8s (not counted as stream) → Listens to track 6 for 3 min (stream event → Kafka → royalty calculation) → Sunday: ML pipeline runs on Hadoop → generates new Discover Weekly for 400M users → stored in Cassandra


## 🗄️ Database Schema


### Cassandra — Music Catalog + User Data


Table: tracks
Partition Key: track_id
  - track_id: UUID
  - title: text
  - artist_ids: list<UUID>
  - album_id: UUID
  - duration_ms: int
  - popularity: int (0-100)
  - audio_urls: map<text, text>  // {quality: cdn_url}
  - audio_features: map<text, float>  // {energy: 0.8, tempo: 120}

Table: playlists
Partition Key: playlist_id
  - playlist_id: UUID
  - owner_id: UUID
  - name: text
  - description: text
  - is_collaborative: boolean
  - follower_count: int
  - track_count: int

Table: playlist_tracks
Partition Key: playlist_id
Clustering: position ASC
  - playlist_id: UUID
  - position: int
  - track_id: UUID
  - added_by: UUID
  - added_at: timestamp

Table: user_library
Partition Key: user_id
Clustering: saved_at DESC
  - user_id: UUID
  - saved_at: timestamp
  - item_type: text (track / album / artist / playlist)
  - item_id: UUID


### PostgreSQL — Account/Billing


| Column        | Type         | Notes                                   |
|---------------|--------------|-----------------------------------------|
| user_id       | UUID         | `PK`                                    |
| email         | VARCHAR(255) | `IDX`                                   |
| plan          | VARCHAR(20)  | free / premium / family / duo / student |
| country       | VARCHAR(3)   | Content licensing                       |
| payment_token | VARCHAR(64)  |                                         |
| billing_date  | DATE         |                                         |


### Sharding


**Cassandra:** Consistent hashing on partition key. Playlist tracks partitioned by playlist_id — all tracks in a playlist on same node  `SHARD`


**Listening history:** Partitioned by user_id. Time-series data with TTL for older entries (keep 2 years)  `SHARD`


## 💾 Cache & CDN Deep Dive


| Cache            | Key                    | TTL     | Strategy                 |
|------------------|------------------------|---------|--------------------------|
| Track metadata   | `track:{id}`           | 1 hr    | Read-through             |
| Playlist content | `playlist:{id}:tracks` | 5 min   | Write-invalidate on edit |
| User home page   | `home:{user_id}`       | 10 min  | Pre-computed             |
| Discover Weekly  | `dw:{user_id}`         | 7 days  | Written by batch ML      |
| Audio segments   | CDN edge               | 30 days | Pull-based CDN           |


**Audio CDN:** Google Cloud CDN (Spotify migrated to GCP). Audio files stored in GCS, served from edge. Popular tracks (~top 1% = 1M tracks) account for ~80% of streams → very high cache hit rate. Long-tail tracks (99% of catalog) have lower hit rate → served from regional/origin.


**Eviction:** LRU at CDN edge. Audio files are small (~10MB) so edge caches hold millions of tracks.


## ⚖️ Tradeoffs


> **
  
> **
    ✅ Three-Engine Recommendation
    

Collaborative + Content + NLP captures user taste from multiple angles. Solves cold-start (audio analysis for new tracks)

  
  
> **
    ❌ Three-Engine Recommendation
    

Massive compute cost to generate personalized playlists for 600M users weekly. Complex pipeline with multiple ML models to maintain

  
  
> **
    ✅ Ogg Vorbis Codec
    

Open-source (no licensing fees), good quality-to-size ratio, widely supported

  
  
> **
    ❌ Ogg Vorbis Codec
    

Not natively supported on iOS (requires custom decoder). AAC used as fallback for Apple devices

  


## 🔄 Alternative Approaches


P2P-Assisted Streaming (Original Spotify)


Early Spotify (2008-2014) used P2P to reduce CDN costs — desktop clients cached and shared audio chunks with nearby peers. Eliminated in 2014 as CDN costs dropped and mobile (which can't do P2P efficiently) became dominant.


Real-Time ML Recommendations (vs Batch)


Instead of weekly batch, compute recommendations in real-time based on current session. "You just listened to 3 jazz tracks → here's a jazz recommendation." Spotify is moving toward session-based real-time recommendations (Daily Mix already adapts faster than Discover Weekly).


## 📚 Additional Information


### Royalty Payment System


A "stream" counts after 30 seconds of playback. Revenue from subscriptions + ads pooled into a global pot. Each stream = proportional share of monthly pot. Average payout: ~$0.003-0.005 per stream. Artists with 1M monthly listeners earn ~$3,000-5,000/month. Rights split: ~70% to rights holders (label + songwriter + publisher), ~30% to Spotify.


### Spotify Connect (Cross-Device)


Unique feature: control playback on any device from any other device. Architecture: all devices connected to Spotify's real-time service via WebSocket. When user switches playback device, command sent via server → target device starts streaming from current position. State synchronized in real-time across all logged-in devices.


### Audio Fingerprinting


Similar to YouTube's Content ID, Spotify uses audio fingerprinting to identify duplicate uploads, match cover versions, and detect copyright infringement. Chromaprint library generates compact fingerprint from audio spectrogram → compared against database of 100M+ tracks.