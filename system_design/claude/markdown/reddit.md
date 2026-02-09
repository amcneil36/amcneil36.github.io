# 🤖 Design Reddit


Social news aggregation with nested comment trees, subreddit communities, voting system — 1.7B+ MAU, 100K+ active subreddits


## 1. Functional Requirements


- **Post Submission:** Submit text, link, image, video, poll posts to subreddits

- **Voting:** Upvote/downvote posts and comments; karma aggregation per user

- **Nested Comments:** Threaded comment trees with collapsible branches, sorted by best/top/new/controversial

- **Subreddits:** Create/join communities with custom rules, flairs, automod, moderator tools

- **Feed Generation:** Home feed (subscribed subs), Popular, All, per-subreddit feeds with sorting (hot/new/top/rising)

- **Search:** Full-text search across posts, comments, subreddits, users

- **Awards & Premium:** Give awards (Gold, Silver, etc.), Reddit Premium features

- **Moderation:** Subreddit moderator tools (remove, ban, AutoModerator rules, mod queue)

- **Real-Time:** Live comment updates, typing indicators (new Reddit), chat


## 2. Non-Functional Requirements


- **Scale:** 1.7B+ MAU, 13B+ posts+comments total, 50M+ daily active users

- **Latency:** Feed generation p50 <200ms, vote processing <50ms, comment tree rendering <300ms

- **Availability:** 99.9% uptime (Reddit has historically had uptime challenges)

- **Consistency:** Eventual consistency for vote counts and karma; strong for moderation actions

- **Viral Content:** Handle massive spikes (trending post can get 100K+ votes in hours)

- **Storage:** Petabytes of content including images/videos hosted on Reddit's media platform


## 3. Flow 1 — Post Submission & Feed Generation


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 400" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#FF4500">
</polygon>
</marker>
</defs>


<rect x="20" y="160" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="190" text-anchor="middle" fill="#fff" font-size="13">
Author
</text>
<text x="75" y="205" text-anchor="middle" fill="#fff" font-size="11">
submits post
</text>


<rect x="180" y="160" width="120" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="240" y="190" text-anchor="middle" fill="#fff" font-size="13">
API Gateway
</text>


<rect x="350" y="60" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="415" y="90" text-anchor="middle" fill="#fff" font-size="13">
Post Service
</text>


<rect x="350" y="160" width="130" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="415" y="185" text-anchor="middle" fill="#fff" font-size="13">
Kafka
</text>
<text x="415" y="200" text-anchor="middle" fill="#fff" font-size="11">
post-events
</text>


<rect x="350" y="270" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="415" y="295" text-anchor="middle" fill="#fff" font-size="13">
AutoModerator
</text>
<text x="415" y="312" text-anchor="middle" fill="#fff" font-size="11">
spam/rules check
</text>


<rect x="540" y="60" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="610" y="90" text-anchor="middle" fill="#fff" font-size="13">
PostgreSQL
</text>
<text x="610" y="105" text-anchor="middle" fill="#fff" font-size="11">
(posts table)
</text>


<rect x="540" y="160" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="610" y="185" text-anchor="middle" fill="#fff" font-size="13">
Feed Generator
</text>
<text x="610" y="202" text-anchor="middle" fill="#fff" font-size="11">
(hot/new/top rank)
</text>


<rect x="540" y="270" width="140" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="610" y="295" text-anchor="middle" fill="#fff" font-size="13">
Feed Cache
</text>
<text x="610" y="312" text-anchor="middle" fill="#fff" font-size="11">
(Memcached)
</text>


<rect x="740" y="160" width="130" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="805" y="185" text-anchor="middle" fill="#fff" font-size="13">
Redis
</text>
<text x="805" y="202" text-anchor="middle" fill="#fff" font-size="11">
(sorted sets)
</text>


<rect x="930" y="160" width="120" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="990" y="190" text-anchor="middle" fill="#fff" font-size="13">
Reader
</text>
<text x="990" y="205" text-anchor="middle" fill="#fff" font-size="11">
views feed
</text>


<line x1="130" y1="190" x2="178" y2="190" stroke="#FF4500" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="175" x2="348" y2="95" stroke="#FF4500" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="190" x2="348" y2="190" stroke="#FF4500" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="205" x2="348" y2="295" stroke="#FF4500" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="480" y1="90" x2="538" y2="90" stroke="#FF4500" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="480" y1="190" x2="538" y2="190" stroke="#FF4500" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="680" y1="190" x2="738" y2="190" stroke="#FF4500" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="680" y1="295" x2="738" y2="200" stroke="#FF4500" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="870" y1="190" x2="928" y2="190" stroke="#FF4500" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="610" y1="222" x2="610" y2="268" stroke="#FF4500" stroke-width="2" marker-end="url(#a1)">
</line>


</svg>

</details>


### Flow Steps


1. **Author submits post** → POST `/api/v1/subreddits/{sub}/submit` with title, body/URL, flair, NSFW flag

2. **AutoModerator** checks subreddit rules (regex patterns, account age requirements, karma threshold, banned domains)

3. **Spam detection:** ML model scores post for spam probability; high-confidence spam auto-removed, borderline sent to mod queue

4. **Post Service** persists to PostgreSQL, generates post ID (base36 encoded, e.g., `t3_abc123`)

5. **Kafka event** triggers async processing: media transcoding, link preview extraction, search indexing, notification to subscribers

6. **Feed Generator** computes scores and inserts post into relevant sorted sets in Redis (per-subreddit: hot, new, top; global: r/all, r/popular)

7. **Hot ranking formula:** `score = log10(max(|ups - downs|, 1)) + sign(ups - downs) × (post_age / 45000)` — Wilson score interval variant. Newer posts with same votes rank higher.

8. **Feed Cache** (Memcached) stores pre-rendered feed pages; invalidated on score changes or new posts


### Example


**Scenario:** User posts "TIL the Sun is actually white, not yellow" to r/todayilearned (30M subscribers)


**AutoMod:** Checks: account age >30 days ✅, karma >100 ✅, title starts with "TIL" ✅, not a repost (URL not seen in last 6 months) ✅ → approved


**Stored:** post_id=t3_xyz789, score=1 (author's auto-upvote), created_utc=now


**Hot rank:** `log10(1) + 1 × (now/45000) = 0 + 37.5 = 37.5` → inserted into r/todayilearned sorted set in Redis


**After 1 hour (5000 upvotes):** `log10(5000) + 1 × ((now+3600)/45000) = 3.7 + 37.58 = 41.28` → climbs to page 1 of r/todayilearned, appears on r/all


**After 24 hours (50K upvotes):** `log10(50000) + 1 × ((now+86400)/45000) = 4.7 + 39.42 = 44.12` → still high but newer posts gaining


### Deep Dives


Hot Ranking Algorithm


Reddit's hot ranking is time-decay based: `hot(ups, downs, date) = log10(max(|ups-downs|, 1)) × sign + seconds_since_epoch/45000`. The `log10` means the first 10 votes matter as much as the next 100, which matter as much as the next 1000 — diminishing returns. The `/45000` time component means every ~12.5 hours, a post needs 10x more votes to maintain the same rank. **Controversial** sort: `min(ups, downs) / max(ups, downs) × max(ups, downs)` — posts with nearly equal up/down votes rank highest. **Best (Wilson)** sort for comments: lower bound of Wilson score interval (95% confidence) — better than simple ups/downs ratio for low-vote items.


Feed Architecture (Pull Model)


Reddit uses a **pull model** (fan-out-on-read) for feeds. No pre-materialized per-user feeds. On feed request: fetch user's subscribed subreddits → for each sub, pull top-N posts from Redis sorted set → merge-sort across all subs → return paginated results. This works because: (1) users subscribe to ~20-50 subs on average, (2) per-sub sorted sets are small and cached, (3) merge is cheap. **r/all** and **r/popular** are special: single global sorted set, no per-user merging needed. Pagination via `after=t3_xyz` cursor (score-based, not offset-based).


AutoModerator


Rule engine that runs before post/comment is visible. Written in YAML by subreddit moderators. Checks: regex on title/body, account age, karma thresholds, domain blacklists, flair requirements, rate limits. Actions: remove, report to mod queue, flair, lock, sticky comment. Reddit's **Safety team** adds platform-wide rules (sitewide ban evasion detection, CSAM detection, spam ML models). AutoMod processes ~50M+ actions/day across all subreddits.


Media Processing Pipeline


Reddit hosts images/videos on `i.redd.it` and `v.redd.it`. Upload flow: client → API → S3 → async transcoding via media pipeline. Images: stored in original + multiple resolutions (thumbnail, preview, full). Videos: transcoded to HLS (multiple bitrates: 240p, 360p, 480p, 720p, 1080p) using FFmpeg workers. GIFs: transcoded to MP4 (10-20x smaller). All media served via CDN (Fastly). NSFW content blurred by default, requires opt-in to view.


## 4. Flow 2 — Voting System


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 300" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#FF8717">
</polygon>
</marker>
</defs>


<rect x="20" y="120" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="150" text-anchor="middle" fill="#fff" font-size="13">
Voter
</text>


<rect x="180" y="120" width="120" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="240" y="150" text-anchor="middle" fill="#fff" font-size="13">
Vote Service
</text>


<rect x="350" y="30" width="130" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="415" y="60" text-anchor="middle" fill="#fff" font-size="13">
Redis
</text>
<text x="415" y="75" text-anchor="middle" fill="#fff" font-size="11">
(vote dedup)
</text>


<rect x="350" y="120" width="130" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="415" y="150" text-anchor="middle" fill="#fff" font-size="13">
PostgreSQL
</text>
<text x="415" y="165" text-anchor="middle" fill="#fff" font-size="11">
(votes table)
</text>


<rect x="350" y="210" width="130" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="415" y="240" text-anchor="middle" fill="#fff" font-size="13">
Kafka
</text>
<text x="415" y="255" text-anchor="middle" fill="#fff" font-size="11">
vote-events
</text>


<rect x="540" y="30" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="610" y="60" text-anchor="middle" fill="#fff" font-size="13">
Score Updater
</text>
<text x="610" y="75" text-anchor="middle" fill="#fff" font-size="11">
(async batch)
</text>


<rect x="540" y="120" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="610" y="150" text-anchor="middle" fill="#fff" font-size="13">
Karma Service
</text>


<rect x="540" y="210" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="610" y="240" text-anchor="middle" fill="#fff" font-size="13">
Rank Recalc
</text>
<text x="610" y="255" text-anchor="middle" fill="#fff" font-size="11">
(hot/top update)
</text>


<line x1="130" y1="150" x2="178" y2="150" stroke="#FF8717" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="300" y1="135" x2="348" y2="65" stroke="#FF8717" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="300" y1="150" x2="348" y2="150" stroke="#FF8717" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="300" y1="165" x2="348" y2="235" stroke="#FF8717" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="480" y1="55" x2="538" y2="55" stroke="#FF8717" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="480" y1="240" x2="538" y2="150" stroke="#FF8717" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="480" y1="240" x2="538" y2="240" stroke="#FF8717" stroke-width="2" marker-end="url(#a2)">
</line>


</svg>

</details>


### Flow Steps


1. **User votes** → POST `/api/vote` with `{thing_id: "t3_abc123", dir: 1}` (1=up, -1=down, 0=unvote)

2. **Vote dedup check:** Redis lookup `vote:{user_id}:{thing_id}` → if same direction already exists, no-op. If changing vote (up→down), net change = -2

3. **Persist vote:** UPSERT into PostgreSQL votes table with (user_id, thing_id, direction)

4. **Kafka event:** `{thing_id, voter_id, direction, net_change, timestamp}`

5. **Score Updater** (async consumer): batches vote events per post, atomically increments `posts.score` in PostgreSQL and Redis sorted set score

6. **Karma Service:** Updates author's karma (async, non-critical). Karma ≠ exact vote count (Reddit applies fuzzing + diminishing returns from single post)

7. **Rank Recalc:** Recomputes hot rank score in Redis sorted set for the post's subreddit and r/all


### Example


**Scenario:** Viral post receiving 1000 votes/minute during peak


**Vote dedup:** Redis SET `vote:u123:t3_abc` = 1 with TTL 30 days (prevents re-voting from the same account)


**Batching:** Score Updater consumes Kafka in microbatches (every 100ms). 1000 votes → single batch UPDATE `SET score = score + 980` (after netting ups and downs)


**Vote fuzzing:** Reddit displays "approximately" vote counts. For posts with 10K+ votes, displayed count varies by ±5%. Prevents vote manipulation detection. Actual score stored precisely for ranking; displayed score is fuzzed.


**Redis sorted set update:** `ZADD r/todayilearned:hot 44.12 t3_abc123` → post rank updated in sorted set


### Deep Dives


Vote Fuzzing & Anti-Manipulation


Reddit intentionally fuzzes displayed vote counts to prevent manipulation. True score stored for ranking. Fuzzing: add random noise proportional to total votes. Also: **vote weight** — votes from new/suspicious accounts count less. **Brigading detection**: if a post suddenly receives abnormal vote patterns (many votes from accounts that share subreddit overlap, or from same IP ranges), those votes are discounted. **Shadow banning**: banned users' votes appear to work from their perspective but don't actually count.


Karma Calculation


Karma ≠ total upvotes. Reddit applies diminishing returns: first ~10 upvotes give ~1 karma each; after that, the ratio decreases. A post with 50K upvotes might give ~5K karma. Comment karma and post karma are tracked separately. Karma serves as: spam prevention (minimum karma to post in many subs), social proof, and vanity metric. Karma is NOT recalculated if a post's votes change weeks later — it's mostly computed at the time of peak activity.


## 5. Flow 3 — Nested Comment Trees


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#FFD635">
</polygon>
</marker>
</defs>


<rect x="20" y="140" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="170" text-anchor="middle" fill="#fff" font-size="13">
User
</text>
<text x="75" y="185" text-anchor="middle" fill="#fff" font-size="11">
views post
</text>


<rect x="180" y="140" width="120" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="240" y="170" text-anchor="middle" fill="#fff" font-size="13">
Comment Service
</text>


<rect x="350" y="40" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="420" y="70" text-anchor="middle" fill="#fff" font-size="13">
PostgreSQL
</text>
<text x="420" y="85" text-anchor="middle" fill="#fff" font-size="11">
(comments table)
</text>


<rect x="350" y="140" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="420" y="165" text-anchor="middle" fill="#fff" font-size="13">
Tree Builder
</text>
<text x="420" y="182" text-anchor="middle" fill="#fff" font-size="11">
(adjacency → tree)
</text>


<rect x="350" y="240" width="140" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="420" y="265" text-anchor="middle" fill="#fff" font-size="13">
Comment Cache
</text>
<text x="420" y="282" text-anchor="middle" fill="#fff" font-size="11">
(Memcached)
</text>


<rect x="560" y="140" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="630" y="165" text-anchor="middle" fill="#fff" font-size="13">
Sorter
</text>
<text x="630" y="182" text-anchor="middle" fill="#fff" font-size="11">
(best/top/new)
</text>


<rect x="760" y="140" width="120" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="820" y="170" text-anchor="middle" fill="#fff" font-size="13">
Rendered
</text>
<text x="820" y="185" text-anchor="middle" fill="#fff" font-size="11">
Comment Tree
</text>


<line x1="130" y1="170" x2="178" y2="170" stroke="#FFD635" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="300" y1="155" x2="348" y2="75" stroke="#FFD635" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="300" y1="170" x2="348" y2="170" stroke="#FFD635" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="300" y1="185" x2="348" y2="265" stroke="#FFD635" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="490" y1="170" x2="558" y2="170" stroke="#FFD635" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="700" y1="170" x2="758" y2="170" stroke="#FFD635" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


### Flow Steps


1. **User opens post** → GET `/comments/t3_abc123?sort=best&limit=200&depth=10`

2. **Cache check:** Memcached lookup for this post's comment tree (keyed by post_id + sort + depth)

3. **Cache miss → DB query:** Fetch all comments for post from PostgreSQL using `parent_id` adjacency list model

4. **Tree Builder:** Reconstructs tree from flat list using parent_id references. Uses hash map: `{comment_id → comment_node}`, then link children to parents. O(n) construction.

5. **Sorter:** At each level of the tree, sort sibling comments by selected algorithm (best=Wilson, top=score, new=timestamp, controversial=min/max ratio)

6. **Truncation:** If comment tree exceeds limits (e.g., 200 comments, depth 10), insert "load more comments" placeholders with continuation tokens

7. **Cache store:** Cache the rendered tree in Memcached with short TTL (1-5 minutes) for hot posts


### Example


**Post with 5000 comments, sorted by "best":**


Root (post t3_abc123)
├── Comment A (score: 3500, Wilson: 0.95) ← top-level, best score
│   ├── Comment A1 (score: 800)
│   │   ├── Comment A1a (score: 200)
│   │   └── Comment A1b (score: 50)
│   └── Comment A2 (score: 1200) ← higher than A1 at this level
│       └── [load more: 15 children]
├── Comment B (score: 2100, Wilson: 0.92)
│   └── Comment B1 (score: 500)
├── Comment C (score: 1800, Wilson: 0.90)
│   └── [load more: 45 children]
└── [load more: 180 top-level comments]


**Wilson score for Comment A:** 3500 upvotes, 200 downvotes → Wilson lower bound at 95% CI = 0.945 → ranks higher than B (2100 up, 100 down, Wilson=0.942)


### Deep Dives


Comment Storage: Adjacency List


Reddit stores comments as a flat table with `parent_id` references (adjacency list model). Query: `SELECT * FROM comments WHERE post_id = ? ORDER BY created_at` → fetch all comments for a post in one query. Tree reconstruction done in application layer. Alternative models considered: **Nested Sets** (fast reads, slow writes — bad for Reddit's write-heavy pattern), **Materialized Path** (path string like "A/A1/A1a" — good for subtree queries but path length limits). Adjacency list wins for Reddit because: writes are simple INSERTs, reads fetch all at once and build tree in memory.


Wilson Score Confidence (Best Sort)


Reddit's "Best" sort uses the **lower bound of the Wilson score confidence interval**. For a comment with `p = upvotes / total_votes` and `n = total_votes`, the Wilson lower bound is: `(p + z²/2n - z√(p(1-p)/n + z²/4n²)) / (1 + z²/n)` where z=1.96 for 95% confidence. This naturally handles the "1 upvote / 1 total" vs "100 upvotes / 200 total" problem — the former ranks lower because low confidence. This was famously proposed by Randall Munroe (xkcd) and adopted by Reddit.


## 6. Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 520" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#FF4500">
</polygon>
</marker>
</defs>


<rect x="10" y="220" width="100" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="60" y="250" text-anchor="middle" fill="#fff" font-size="12">
Users
</text>
<text x="60" y="265" text-anchor="middle" fill="#fff" font-size="10">
1.7B+ MAU
</text>


<rect x="150" y="160" width="110" height="50" rx="8" fill="#546e7a" stroke="#90a4ae" stroke-width="2">
</rect>
<text x="205" y="190" text-anchor="middle" fill="#fff" font-size="12">
CDN (Fastly)
</text>


<rect x="150" y="225" width="110" height="50" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="205" y="255" text-anchor="middle" fill="#fff" font-size="12">
API / GraphQL
</text>


<rect x="310" y="50" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="76" text-anchor="middle" fill="#fff" font-size="11">
Post Service
</text>


<rect x="310" y="105" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="131" text-anchor="middle" fill="#fff" font-size="11">
Comment Service
</text>


<rect x="310" y="160" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="186" text-anchor="middle" fill="#fff" font-size="11">
Vote Service
</text>


<rect x="310" y="215" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="241" text-anchor="middle" fill="#fff" font-size="11">
Feed Service
</text>


<rect x="310" y="270" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="296" text-anchor="middle" fill="#fff" font-size="11">
User Service
</text>


<rect x="310" y="325" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="351" text-anchor="middle" fill="#fff" font-size="11">
Search Service
</text>


<rect x="310" y="380" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="406" text-anchor="middle" fill="#fff" font-size="11">
Moderation
</text>


<rect x="310" y="435" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="461" text-anchor="middle" fill="#fff" font-size="11">
Notifications
</text>


<rect x="490" y="120" width="120" height="42" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="550" y="146" text-anchor="middle" fill="#fff" font-size="11">
Kafka
</text>


<rect x="670" y="60" width="120" height="42" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="730" y="86" text-anchor="middle" fill="#fff" font-size="11">
PostgreSQL
</text>


<rect x="670" y="115" width="120" height="42" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="730" y="141" text-anchor="middle" fill="#fff" font-size="11">
Redis
</text>


<rect x="670" y="170" width="120" height="42" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="730" y="196" text-anchor="middle" fill="#fff" font-size="11">
Memcached
</text>


<rect x="670" y="225" width="120" height="42" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="730" y="251" text-anchor="middle" fill="#fff" font-size="11">
Elasticsearch
</text>


<rect x="670" y="280" width="120" height="42" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="730" y="306" text-anchor="middle" fill="#fff" font-size="11">
S3 (media)
</text>


<rect x="670" y="335" width="120" height="42" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="730" y="361" text-anchor="middle" fill="#fff" font-size="11">
Cassandra (legacy)
</text>


<line x1="110" y1="245" x2="148" y2="185" stroke="#FF4500" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="110" y1="250" x2="148" y2="250" stroke="#FF4500" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="260" y1="245" x2="308" y2="75" stroke="#FF4500" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="75" x2="668" y2="80" stroke="#FF4500" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="185" x2="668" y2="135" stroke="#FF4500" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="240" x2="668" y2="190" stroke="#FF4500" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="350" x2="668" y2="240" stroke="#FF4500" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="130" x2="488" y2="140" stroke="#FF4500" stroke-width="2" marker-end="url(#a4)">
</line>


</svg>

</details>


## 7. Database Schema


### Posts (PostgreSQL — sharded by subreddit_id)


Table: posts   `SHARD`
- post_id: varchar(10)          `PK` (base36, e.g., "abc123")
- fullname: varchar(15)         `IDX` ("t3_abc123")
- subreddit_id: int             `FK`  `IDX`
- author_id: int                `FK`  `IDX`
- title: varchar(300)           `IDX`
- body: text                   // self-text (markdown)
- url: varchar(2048)           // for link posts
- post_type: enum('self','link','image','video','poll','crosspost')
- score: int                    `IDX` (denormalized ups - downs)
- ups: int
- downs: int
- num_comments: int
- flair_id: int
- nsfw: boolean
- spoiler: boolean
- locked: boolean
- stickied: boolean
- removed: boolean
- created_utc: timestamp        `IDX`
- edited_utc: timestamp


### Comments (PostgreSQL — sharded by post_id)


Table: comments   `SHARD`
- comment_id: varchar(10)       `PK`
- fullname: varchar(15)        ("t1_xyz789")
- post_id: varchar(10)          `FK`  `IDX`
- parent_id: varchar(15)        `FK`  `IDX`
- author_id: int                `FK`
- body: text (markdown)
- score: int                    `IDX`
- ups: int
- downs: int
- depth: smallint              // 0 = top-level reply to post
- removed: boolean
- created_utc: timestamp
- edited_utc: timestamp


### Votes (PostgreSQL — sharded by thing_id)


Table: votes   `SHARD`
- user_id: int                  `PK`
- thing_id: varchar(15)         `PK` (post or comment fullname)
- direction: smallint          // 1=up, -1=down, 0=unvote
- created_utc: timestamp
Unique constraint: (user_id, thing_id)


### Subreddits (PostgreSQL)


Table: subreddits
- subreddit_id: int             `PK`
- name: varchar(21)             `IDX`
- title: varchar(100)
- description: text
- sidebar: text (markdown)
- subscribers: int             (denormalized count)
- type: enum('public','private','restricted','premium_only')
- nsfw: boolean
- created_utc: timestamp


### Redis Sorted Sets (Feed Rankings)


Key: feed:{subreddit_name}:{sort}  // e.g., feed:todayilearned:hot
Type: ZSET
Members: post fullnames (t3_abc123)
Scores: ranking score (hot formula output)

Key: feed:all:{sort}  // r/all
Key: feed:popular:{sort}  // r/popular

// Personal home feed computed on-the-fly by merging per-sub sorted sets


## 8. Cache & CDN Deep Dive


### Caching Layers


| Layer              | Technology       | What                                                | TTL                           | Notes                             |
|--------------------|------------------|-----------------------------------------------------|-------------------------------|-----------------------------------|
| CDN                | Fastly (Varnish) | Media (images, videos, thumbnails), static assets   | 1yr (content-addressed)       | ~200 edge PoPs, custom VCL rules  |
| Page Cache         | Memcached        | Pre-rendered subreddit listings (hot/new/top pages) | 60-300 seconds                | Keyed by sub + sort + page        |
| Comment Tree Cache | Memcached        | Rendered comment trees per post                     | 60 seconds (hot), 5min (cold) | Invalidated on new comment/vote   |
| Vote Dedup         | Redis            | User vote state per thing                           | 30 days                       | Prevents duplicate votes          |
| Feed Rankings      | Redis (ZSET)     | Sorted post IDs per subreddit per sort type         | Continuously updated          | Core data structure for feeds     |
| Session            | Redis            | Auth sessions, rate limit counters                  | Hours                         | Per-user rate limits tracked here |


### CDN (Fastly) Details


Reddit uses **Fastly** (Varnish-based CDN) for all static and media content. Custom VCL (Varnish Configuration Language) rules handle: cache keying (vary by logged-in state for personalized vs anonymous views), stale-while-revalidate (serve stale content while fetching fresh in background), geographic routing, NSFW content filtering at edge (for certain regions), and bot detection. Reddit's "Reddit is fun" moments (site goes down) have historically been caused by cache stampedes — many users requesting the same hot post simultaneously, all hitting origin when cache expires. Mitigated by: request coalescing, stale-while-revalidate, and origin shielding.


## 9. Scaling Considerations


### Reddit's Infrastructure Evolution


- **Original stack (2005):** Single Python/Pylons server, single PostgreSQL instance, memcached

- **2010s:** Moved to Cassandra for some data, rabbitmq for queues, still primarily Python

- **2020s:** Kubernetes on AWS, migrated to Go microservices, GraphQL federation, Kafka for event streaming

- **Current:** 500+ microservices, multi-region AWS deployment, ~1500 engineers


### Handling Viral Content


- **"Reddit hug of death":** When a linked site goes down from Reddit traffic. Reddit itself handles internal viral posts via Redis sorted sets (O(log n) score updates) + aggressive Memcached caching

- **Hot partition problem:** A viral post's comment thread can become a hot partition. Mitigation: comment cache with short TTL, write-behind for vote counts, async comment count updates

- **r/all:** Single global sorted set serving all users. Cached aggressively. Score updates batched to avoid Redis write contention


### Load Balancing


- **DNS:** Route53 with latency-based routing to nearest AWS region

- **L7:** AWS ALB → Kubernetes ingress (envoy) → service pods

- **Rate limiting:** Per-user (60 req/min API), per-IP (100 req/min), per-subreddit (posting limits)


## 10. Tradeoffs


> **

> **
✅ Pull-Based Feed (Fan-Out-On-Read)


No write amplification when posting. Works great for Reddit's model where users subscribe to subreddits, not individuals. Subreddit sorted sets are shared across all users — very cache-friendly.


> **
❌ Pull-Based Read Latency


Home feed requires merging sorted sets from ~20-50 subscribed subreddits at read time. Can be slow for users with many subscriptions. Mitigated by caching merged feed for short TTL.


> **
✅ Adjacency List for Comments


Simple INSERT for new comments (no tree rebalancing). All comments fetched in single query. Tree built in-memory cheaply (O(n)). Flexible: any sort algorithm can be applied at read time.


> **
❌ Full Comment Fetch


Must fetch ALL comments for a post to build the tree (even if only showing top 200). For posts with 50K+ comments, this is expensive. Partial tree loading requires "load more" with separate API calls.


> **
✅ Vote Fuzzing


Prevents manipulation detection (can't A/B test vote bots). Discourages obsessive vote tracking. Adds uncertainty that makes gaming harder.


> **
❌ User Trust


Users frustrated by inaccurate vote counts. "Why does my post show different numbers on refresh?" Undermines perceived transparency. Some power users distrust the platform due to opaque fuzzing.


## 11. Alternative Approaches


Materialized Path for Comments


Store path string (e.g., "001/003/007") instead of parent_id. Enables subtree queries with LIKE prefix. Used by some forum software. Tradeoff: path length limits depth, harder to reorder, but enables partial tree loading without fetching all comments.


Push-Based Home Feed


Pre-compute home feed per user (like Twitter/LinkedIn). Eliminates read-time merging. But Reddit has 100K+ active subreddits with frequent posts — write amplification would be enormous (post to r/funny with 50M subscribers → 50M feed writes).


CRDT-Based Voting


Use CRDTs (Conflict-free Replicated Data Types) for vote counters — enables multi-region writes without coordination. Eventually consistent but always converges. Would simplify multi-DC voting at the cost of temporary count inaccuracies (which Reddit already tolerates via fuzzing).


GraphQL Federation


Reddit has moved toward GraphQL for their API. Federation: each microservice exposes a GraphQL subgraph, gateway stitches them together. Reduces over-fetching (mobile client can request exactly needed fields). Tradeoff: complex query planning, potential N+1 problems, harder to cache than REST.


## 12. Additional Information


### Reddit's Thing System


Every entity in Reddit has a "fullname" with type prefix: `t1_` = comment, `t2_` = account, `t3_` = post (link), `t4_` = message, `t5_` = subreddit, `t6_` = award. This polymorphic ID system allows the voting API to work on both posts and comments with a single endpoint.


### Tech Stack


- **Backend:** Go (new services), Python (legacy, still significant), Thrift/gRPC for service communication

- **Frontend:** React (new Reddit), jQuery (old Reddit still maintained)

- **Infrastructure:** AWS (EC2, S3, ElastiCache), Kubernetes, Terraform

- **Data stores:** PostgreSQL (primary), Redis (caching/ranking), Memcached (page cache), Elasticsearch (search), Cassandra (some legacy data)

- **Streaming:** Kafka for event pipeline, Flink for stream processing

- **CDN:** Fastly (custom VCL, edge computing)


### Key Numbers


| Monthly Active Users   | 1.7B+ |
|------------------------|-------|
| Daily Active Users     | 50M+  |
| Total posts + comments | 13B+  |
| Active subreddits      | 100K+ |
| Page views per day     | 1.5B+ |
| Votes per day          | ~500M |
| Comments per day       | ~50M  |