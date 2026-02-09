# 🎬 Video Recommendation Platform


Design a recommendation system that personalizes video content for billions of users across a feed, handling the candidate generation → ranking → re-ranking pipeline with real-time feature serving and feedback loops.


## 📋 Functional Requirements


- **Personalized feed** — generate ranked list of video recommendations for each user's homepage and "Up Next"

- **Candidate generation** — retrieve 100s of candidates from millions of videos using multiple retrieval strategies

- **Ranking** — score and rank candidates using deep learning model with user/video/context features

- **Real-time signals** — incorporate live signals (just-watched, trending, session context) into recommendations

- **Multi-objective optimization** — balance engagement (watch time), satisfaction (likes), and diversity (not all same genre)

- **Exploration vs exploitation** — surface new/niche content alongside high-confidence recommendations

- **Content safety filtering** — remove policy-violating content, apply age restrictions


## 📋 Non-Functional Requirements


- **Latency** — full recommendation pipeline <200ms p99 (candidate gen + ranking + re-ranking)

- **Scale** — 2B+ users, 800M+ videos, 1B+ recommendations served/day

- **Freshness** — new videos eligible for recommendation within minutes of upload

- **Feature serving** — 1000+ features per (user, video) pair served in <10ms

- **Model freshness** — ranking model updated daily; feature store updated in near-real-time

- **Availability** — 99.99%; graceful degradation (serve popular videos if personalization fails)


## 🔄 Flow 1: Candidate Generation (Retrieval)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 320" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FF0000">
</polygon>
</marker>
</defs>


<rect x="20" y="130" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="160" text-anchor="middle" fill="white" font-size="12">
User Request
</text>


<rect x="190" y="130" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="255" y="160" text-anchor="middle" fill="white" font-size="12">
Retrieval Mixer
</text>


<rect x="380" y="30" width="140" height="45" rx="8" fill="#1565C0">
</rect>
<text x="450" y="57" text-anchor="middle" fill="white" font-size="11">
Collaborative Filter
</text>


<rect x="380" y="90" width="140" height="45" rx="8" fill="#1565C0">
</rect>
<text x="450" y="117" text-anchor="middle" fill="white" font-size="11">
Two-Tower DNN
</text>


<rect x="380" y="150" width="140" height="45" rx="8" fill="#1565C0">
</rect>
<text x="450" y="177" text-anchor="middle" fill="white" font-size="11">
Content-Based
</text>


<rect x="380" y="210" width="140" height="45" rx="8" fill="#1565C0">
</rect>
<text x="450" y="237" text-anchor="middle" fill="white" font-size="11">
Trending / Popular
</text>


<rect x="380" y="270" width="140" height="45" rx="8" fill="#1565C0">
</rect>
<text x="450" y="297" text-anchor="middle" fill="white" font-size="11">
Graph-Based (Social)
</text>


<rect x="590" y="130" width="140" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="660" y="150" text-anchor="middle" fill="white" font-size="12">
Candidate Pool
</text>
<text x="660" y="165" text-anchor="middle" fill="white" font-size="10">
(~1000 videos)
</text>


<rect x="790" y="130" width="130" height="50" rx="8" fill="#00695C">
</rect>
<text x="855" y="160" text-anchor="middle" fill="white" font-size="12">
Dedup & Filter
</text>


<line x1="130" y1="155" x2="185" y2="155" stroke="#FF0000" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="320" y1="140" x2="375" y2="52" stroke="#FF0000" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="320" y1="145" x2="375" y2="112" stroke="#FF0000" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="320" y1="155" x2="375" y2="172" stroke="#FF0000" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="320" y1="165" x2="375" y2="232" stroke="#FF0000" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="320" y1="170" x2="375" y2="292" stroke="#FF0000" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="520" y1="52" x2="585" y2="140" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="520" y1="112" x2="585" y2="145" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="520" y1="172" x2="585" y2="150" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="520" y1="232" x2="585" y2="160" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="520" y1="292" x2="585" y2="165" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="730" y1="155" x2="785" y2="155" stroke="#FF6F00" stroke-width="2" marker-end="url(#a1)">
</line>


</svg>

</details>


**Step 1:** Retrieval Mixer fans out to 5+ candidate generators in parallel (each returns ~200 candidates)
**Step 2:** Collaborative Filtering: "users like you watched X" via ALS matrix factorization embeddings
**Step 3:** Two-Tower DNN: user embedding ↔ video embedding dot product via ANN search (FAISS/ScaNN)
**Step 4:** Content-Based: similar to recently watched videos (topic, creator, audio features)
**Step 5:** Trending + Social: popular videos, friends' recent watches, subscription uploads
**Step 6:** Candidate Pool (~1000 unique videos) after dedup and safety filtering

Deep Dive: Two-Tower Model (YouTube DNN Paper)

`// Two-Tower Architecture (Google, 2019):
// Separate user and item towers, trained jointly
// At serving time: precompute all video embeddings, ANN search for user

// User Tower (computed at request time):
user_features = [
  watch_history_embeddings,     // avg of last 50 watched video embeddings
  search_query_embeddings,      // recent search terms
  demographic_features,          // age, gender, country
  context_features,              // time of day, device, day of week
  engagement_history             // avg watch %, like rate
]
user_embedding = DNN(user_features) → 256-dim vector

// Video Tower (precomputed offline for all videos):
video_features = [
  title_embedding,              // BERT encoding of title
  visual_embedding,             // CNN features from thumbnail/frames
  audio_embedding,              // audio content features
  creator_embedding,            // creator's channel features
  metadata                      // category, tags, duration, upload_time
]
video_embedding = DNN(video_features) → 256-dim vector

// Retrieval: find top-K videos where dot(user_emb, video_emb) is highest
// ANN search with ScaNN: search 800M videos in ~5ms
// ScaNN partitions: 4000 clusters, search top-100 clusters
// Asymmetric hashing: compress video embeddings to 32 bytes each
// Total index: 800M × 32B = ~25GB (fits in memory per replica)

// Training: in-batch sampled softmax
// Positive: (user, video_watched)
// Negatives: other videos in batch (efficient sampling)`


## 🔄 Flow 2: Ranking (Scoring & Ordering)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 300" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FF6F00">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="85" y="80" text-anchor="middle" fill="white" font-size="12">
~1000 Candidates
</text>


<rect x="200" y="60" width="140" height="50" rx="8" fill="#00695C">
</rect>
<text x="270" y="80" text-anchor="middle" fill="white" font-size="12">
Feature Store
</text>
<text x="270" y="95" text-anchor="middle" fill="white" font-size="10">
(user+video+cross)
</text>


<rect x="400" y="60" width="150" height="50" rx="8" fill="#1565C0">
</rect>
<text x="475" y="80" text-anchor="middle" fill="white" font-size="12">
Deep Ranking Model
</text>
<text x="475" y="95" text-anchor="middle" fill="white" font-size="10">
(Multi-Task DNN)
</text>


<rect x="610" y="60" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="675" y="80" text-anchor="middle" fill="white" font-size="12">
Multi-Objective
</text>
<text x="675" y="95" text-anchor="middle" fill="white" font-size="10">
Combiner
</text>


<rect x="800" y="60" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="865" y="80" text-anchor="middle" fill="white" font-size="12">
Ranked List
</text>
<text x="865" y="95" text-anchor="middle" fill="white" font-size="10">
(top ~50)
</text>


<rect x="200" y="190" width="140" height="50" rx="8" fill="#4E342E">
</rect>
<text x="270" y="210" text-anchor="middle" fill="white" font-size="12">
Online Feature
</text>
<text x="270" y="225" text-anchor="middle" fill="white" font-size="10">
Pipeline (Flink)
</text>


<rect x="400" y="190" width="150" height="50" rx="8" fill="#4E342E">
</rect>
<text x="475" y="210" text-anchor="middle" fill="white" font-size="12">
Batch Feature
</text>
<text x="475" y="225" text-anchor="middle" fill="white" font-size="10">
Pipeline (Spark)
</text>


<line x1="150" y1="85" x2="195" y2="85" stroke="#FF6F00" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="340" y1="85" x2="395" y2="85" stroke="#FF6F00" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="550" y1="85" x2="605" y2="85" stroke="#FF6F00" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="740" y1="85" x2="795" y2="85" stroke="#FF6F00" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="270" y1="190" x2="270" y2="115" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="475" y1="190" x2="350" y2="115" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<text x="290" y="170" fill="#aaa" font-size="10">
real-time features
</text>


<text x="420" y="170" fill="#aaa" font-size="10">
batch features
</text>


</svg>

</details>


**Step 1:** For each of ~1000 candidates, Feature Store assembles feature vector (1000+ features per user×video pair)
**Step 2:** Deep Ranking Model (multi-task DNN) predicts multiple engagement signals simultaneously
**Step 3:** Multi-Objective Combiner produces final score: weighted combination of predicted watch time, P(like), P(share), P(subscribe)
**Step 4:** Top ~50 videos selected as ranked list for the feed page

Deep Dive: Multi-Task Ranking Model

`// Multi-task DNN predicts several objectives simultaneously:
// Shared bottom layers → task-specific heads

// Architecture (MMoE - Multi-gate Mixture of Experts):
//
// Input features (1000+):
// ├── User: watch history, demographics, device, time
// ├── Video: category, duration, creator, upload age, quality score
// ├── Cross: user-video affinity, topic overlap, social signal
// └── Context: time of day, session position, previous watch
//
// Shared Experts (8 expert networks):
// Each expert: 3-layer DNN (512→256→128)
//
// Task-specific gates:
// Gate_i = softmax(W_i × input) → weights over experts
//
// Task heads:
// ├── P(click) — logistic regression head
// ├── E[watch_time | click] — regression head (minutes)
// ├── P(like | watch) — logistic regression head
// ├── P(share | watch) — logistic regression head
// ├── P(subscribe | watch) — logistic regression head
// └── P(not_interested) — negative signal head

// Final score = weighted combination:
score = w1 × P(click) × E[watch_time]
      + w2 × P(like)
      + w3 × P(share)
      + w4 × P(subscribe)
      - w5 × P(not_interested)

// Weights tuned via A/B testing to optimize platform-level metrics
// Serving: TensorFlow Serving on GPU, batched inference
// ~1000 candidates × 1000 features → ~50ms on GPU`


## 🔄 Flow 3: Re-Ranking & Serving


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 260" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FF0000">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#4E342E">
</rect>
<text x="80" y="90" text-anchor="middle" fill="white" font-size="12">
Ranked List (50)
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="255" y="80" text-anchor="middle" fill="white" font-size="12">
Diversity
</text>
<text x="255" y="95" text-anchor="middle" fill="white" font-size="10">
Re-Ranker
</text>


<rect x="370" y="60" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="435" y="80" text-anchor="middle" fill="white" font-size="12">
Business Rules
</text>
<text x="435" y="95" text-anchor="middle" fill="white" font-size="10">
(boost, suppress)
</text>


<rect x="550" y="60" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="615" y="80" text-anchor="middle" fill="white" font-size="12">
Ad Insertion
</text>
<text x="615" y="95" text-anchor="middle" fill="white" font-size="10">
(blended feed)
</text>


<rect x="730" y="60" width="130" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="795" y="90" text-anchor="middle" fill="white" font-size="12">
User Feed
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#FF0000" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="320" y1="85" x2="365" y2="85" stroke="#FF0000" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="500" y1="85" x2="545" y2="85" stroke="#FF0000" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="680" y1="85" x2="725" y2="85" stroke="#FF0000" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


**Step 1:** Diversity Re-Ranker ensures variety (MMR algorithm): avoid 5 cooking videos in a row; mix genres, creators, video lengths
**Step 2:** Business Rules: boost creator content for subscribers, suppress recently-dismissed, apply content policies
**Step 3:** Ad Insertion: blend sponsored content at designated positions (every 5th slot)
**Step 4:** Final feed rendered to user with thumbnails, titles, and engagement previews

Deep Dive: Feature Store Architecture

`// Feature Store: central system for managing ML features
// Two data paths: batch (Spark) and streaming (Flink)

// Batch features (updated daily):
// - User: total watch hours, genre preferences, avg session length
// - Video: total views, avg watch %, like ratio, category embeddings
// Storage: Bigtable / HBase with row key = entity_id

// Streaming features (updated in real-time):
// - User: last 10 videos watched (session), current device, trending interest
// - Video: view count last hour, real-time like velocity
// Storage: Redis with TTL (24h)

// Feature serving at ranking time:
// 1. Batch lookup: Bigtable.get(user_id) → 500 user features (~2ms)
// 2. Batch lookup: Bigtable.multiGet(video_ids) → 500 video features (~5ms)
// 3. Real-time lookup: Redis.mget(user_id, video_ids) → session features (~1ms)
// 4. Cross features computed on-the-fly: topic_overlap, time_since_last_watch
// Total feature assembly: ~8ms for 1000 candidates

// Feature logging: every served recommendation logs features
// This creates training data for next model iteration
// "Log and learn" pattern: production features → offline training`


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 500" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="ac" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FF0000">
</polygon>
</marker>
</defs>


<text x="550" y="25" text-anchor="middle" fill="#FF0000" font-size="16" font-weight="bold">
Video Recommendation Pipeline
</text>


<rect x="20" y="50" width="120" height="40" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="75" text-anchor="middle" fill="white" font-size="11">
User Request
</text>


<rect x="200" y="50" width="130" height="40" rx="8" fill="#E65100">
</rect>
<text x="265" y="75" text-anchor="middle" fill="white" font-size="11">
Retrieval Service
</text>


<rect x="400" y="50" width="130" height="40" rx="8" fill="#1565C0">
</rect>
<text x="465" y="75" text-anchor="middle" fill="white" font-size="11">
Ranking Service
</text>


<rect x="600" y="50" width="130" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="665" y="75" text-anchor="middle" fill="white" font-size="11">
Re-Ranking Service
</text>


<rect x="800" y="50" width="130" height="40" rx="8" fill="#2E7D32">
</rect>
<text x="865" y="75" text-anchor="middle" fill="white" font-size="11">
User Feed
</text>


<rect x="200" y="130" width="130" height="40" rx="8" fill="#4E342E">
</rect>
<text x="265" y="155" text-anchor="middle" fill="white" font-size="11">
ANN Index (ScaNN)
</text>


<rect x="400" y="130" width="130" height="40" rx="8" fill="#00695C">
</rect>
<text x="465" y="155" text-anchor="middle" fill="white" font-size="11">
Feature Store
</text>


<rect x="600" y="130" width="130" height="40" rx="8" fill="#607D8B">
</rect>
<text x="665" y="155" text-anchor="middle" fill="white" font-size="11">
GPU Model Serving
</text>


<rect x="200" y="210" width="130" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="265" y="235" text-anchor="middle" fill="white" font-size="11">
Kafka Event Stream
</text>


<rect x="400" y="210" width="130" height="40" rx="8" fill="#1565C0">
</rect>
<text x="465" y="235" text-anchor="middle" fill="white" font-size="11">
Flink (Real-Time)
</text>


<rect x="600" y="210" width="130" height="40" rx="8" fill="#1565C0">
</rect>
<text x="665" y="235" text-anchor="middle" fill="white" font-size="11">
Spark (Batch)
</text>


<rect x="800" y="210" width="130" height="40" rx="8" fill="#4E342E">
</rect>
<text x="865" y="235" text-anchor="middle" fill="white" font-size="11">
Training Pipeline
</text>


<rect x="400" y="310" width="130" height="40" rx="8" fill="#4E342E">
</rect>
<text x="465" y="335" text-anchor="middle" fill="white" font-size="11">
Bigtable (Features)
</text>


<rect x="600" y="310" width="130" height="40" rx="8" fill="#00695C">
</rect>
<text x="665" y="335" text-anchor="middle" fill="white" font-size="11">
Redis (Real-Time)
</text>


<rect x="200" y="310" width="130" height="40" rx="8" fill="#4E342E">
</rect>
<text x="265" y="335" text-anchor="middle" fill="white" font-size="11">
GCS (Model Store)
</text>


<line x1="140" y1="70" x2="195" y2="70" stroke="#FF0000" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="330" y1="70" x2="395" y2="70" stroke="#FF0000" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="530" y1="70" x2="595" y2="70" stroke="#FF0000" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="70" x2="795" y2="70" stroke="#FF0000" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="265" y1="90" x2="265" y2="125" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="465" y1="90" x2="465" y2="125" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="665" y1="90" x2="665" y2="125" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="265" y1="170" x2="265" y2="205" stroke="#FF6F00" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="330" y1="230" x2="395" y2="230" stroke="#FF6F00" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="530" y1="230" x2="595" y2="230" stroke="#FF6F00" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="230" x2="795" y2="230" stroke="#FF6F00" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="465" y1="250" x2="465" y2="305" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="665" y1="250" x2="665" y2="305" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


</svg>

</details>


## 💾 Database Schema


### Bigtable — Feature Store

`// User features (batch + streaming)
Row Key: user:{user_id}
Column Families:
  "profile": { age, gender, country, language, device_type }
  "history": { last_50_watched (video_ids), genre_dist, avg_watch_pct }
  "embeddings": { user_embedding_256d (float[]) }
  "realtime": { session_videos, current_device, last_active }

// Video features
Row Key: video:{video_id}
Column Families:
  "metadata": { title, category, creator_id, duration, upload_time }
  "stats": { total_views, avg_watch_pct, like_ratio, ctr }
  "embeddings": { video_embedding_256d (float[]) }
  "quality": { quality_score, thumbnail_score, content_rating }

// Interaction features (for training data)
Row Key: interaction:{user_id}#{timestamp}
Column Families:
  "event": { video_id, event_type, watch_duration, liked, shared }
  "context": { device, time_of_day, session_position }
  "features_logged": { feature_vector_snapshot (for offline training) }`


### Redis — Real-Time Features

`// Session context (per active user)
SET session:{user_id} {
  "videos_watched": ["v1", "v2", "v3"],
  "genres_in_session": {"comedy": 2, "news": 1},
  "session_start": 1707500000,
  "device": "mobile_ios",
  "last_interaction": 1707500300
} EX 3600  // 1h session TTL

// Real-time video stats
HSET video_rt:{video_id} {
  "views_1h": 15000,
  "likes_1h": 890,
  "shares_1h": 120,
  "trending_score": 0.85
}
// Updated by Flink every 60 seconds

// ANN Index (ScaNN / FAISS)
// Not in Redis — separate service with in-memory index
// 800M video embeddings × 256 dims × 4 bytes = ~800GB
// Sharded across 32 machines (~25GB each)
// Approximate search: top-200 in ~5ms per shard`


## ⚡ Cache & CDN Deep Dive


| Layer         | What                                     | Strategy                                        | TTL                |
|---------------|------------------------------------------|-------------------------------------------------|--------------------|
| Client Cache  | Pre-fetched next page of recommendations | Predictive pre-fetch (2 pages ahead)            | Until scroll       |
| Redis         | Pre-computed recs for returning users    | Batch-generated, served if model latency spikes | 1 hour             |
| Feature Cache | Hot user/video features                  | LRU in-memory cache on feature serving nodes    | 5 minutes          |
| Model Cache   | TF SavedModel in GPU memory              | Loaded on startup, hot-swapped on new version   | Until model update |
| CDN           | Video thumbnails, metadata               | Pull-through caching                            | 24 hours           |


## 📈 Scaling Considerations


- **Retrieval parallelism:** 5 candidate generators run in parallel (fan-out). Total retrieval latency = max of individual generator latencies (~30ms).

- **GPU inference scaling:** batch requests for ranking model (batch size 256). Dynamic batching: wait up to 5ms to fill batch for better GPU utilization. 100 GPU serving instances for 1B requests/day.

- **ANN index updates:** new videos need to be searchable within minutes. Incremental index update every 5 minutes. Full rebuild nightly. Dual-index strategy: serve from old index while building new.

- **Cold start:** new users (no history) → use popularity + demographic-based recs. New videos (no engagement data) → boost via content-based features + exploration budget (ε-greedy or Thompson sampling).


## ⚖️ Tradeoffs


> **
Multi-Stage Pipeline
- Handles billions of videos efficiently
- Each stage optimized independently
- Complex model only scores ~1000 items


Multi-Stage Pipeline
- Items lost in retrieval can't be recovered
- Stage boundaries create information loss
- Complex system to maintain and debug


Multi-Objective Ranking
- Balances engagement + satisfaction
- Reduces clickbait (optimizes beyond just clicks)
- Business objectives directly in model


Multi-Objective Ranking
- Weight tuning is complex (many knobs)
- Objectives can conflict (watch time vs diversity)
- Harder to debug than single-objective


## 🔄 Alternative Approaches


End-to-End Retrieval + Ranking

Single model that directly retrieves and ranks (eliminates staged pipeline). Research direction: retrieval-augmented generation for recommendations. Challenge: scoring all 800M items with a complex model is computationally infeasible at serving time. Approximations like MIPS (Maximum Inner Product Search) help but still gap vs staged approach.

Reinforcement Learning (RL) for Sequencing

Treat recommendation as a sequential decision problem. State = user history, Action = next recommendation, Reward = long-term engagement. Optimizes for session-level satisfaction, not per-item clicks. Used at YouTube for "Up Next" sequencing. Challenge: delayed/sparse rewards, exploration risk.


## 📚 Additional Information


- **Position bias:** items at top of feed get more clicks regardless of quality. De-bias by training model with position as feature, then setting position=0 at serving time (inverse propensity weighting).

- **Feedback loops:** model recommends popular → popular gets more popular (rich-get-richer). Mitigation: exploration budget (5-10% of slots for new/diverse content), fairness constraints per creator.

- **Responsible AI:** avoid filter bubbles, reduce harmful content amplification, ensure creator fairness. Guardrail metrics: diversity score, misinformation exposure, watch time satisfaction ratio.

- **Key papers:** YouTube DNN (2016), Two-Tower (2019), MMoE (2018), YouTube RL (2019), ScaNN (2020)