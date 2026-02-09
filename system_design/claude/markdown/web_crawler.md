# 🕷️ Web Crawler - System Design


## Functional Requirements


1. **URL Discovery & Crawling:** Start from seed URLs, discover new URLs via link extraction, recursively crawl web pages respecting robots.txt, download HTML/JS-rendered content

2. **Content Storage & Processing:** Store raw HTML, extract text/metadata, detect duplicates (near-duplicate and exact), maintain freshness by re-crawling based on change frequency

3. **Politeness & Scheduling:** Rate limit per domain (respect Crawl-delay), URL prioritization (PageRank, freshness, domain authority), frontier queue management, DNS caching


## Non-Functional Requirements


- **Scale:** Crawl 5B+ pages, discover 100M+ new URLs/day, re-crawl changed pages within hours

- **Throughput:** 10,000+ pages/second across distributed crawler fleet

- **Politeness:** Max 1 request/second per domain (configurable); respect robots.txt and nofollow/noindex directives

- **Robustness:** Handle spider traps, infinite URL spaces, malformed HTML, timeouts, DNS failures gracefully

- **Freshness:** High-priority pages (news sites) re-crawled every 15 minutes; average pages daily-weekly


## Flow 1: URL Frontier & Page Fetching


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#4ECDC4">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#FF6B6B">
</rect>
<text x="80" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Seed URLs
</text>
<text x="80" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Initial Set)
</text>


<rect x="190" y="50" width="140" height="55" rx="8" fill="#9C27B0">
</rect>
<text x="260" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
URL Frontier
</text>
<text x="260" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Priority Queues)
</text>


<rect x="380" y="20" width="130" height="45" rx="8" fill="#4285F4">
</rect>
<text x="445" y="47" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
URL Deduplicator
</text>


<rect x="380" y="80" width="130" height="45" rx="8" fill="#00897B">
</rect>
<text x="445" y="100" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Politeness Queue
</text>
<text x="445" y="115" text-anchor="middle" fill="#fff" font-size="10">
(Per-Domain)
</text>


<rect x="560" y="50" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="625" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
robots.txt
</text>
<text x="625" y="87" text-anchor="middle" fill="#fff" font-size="10">
Cache & Parser
</text>


<rect x="740" y="50" width="130" height="50" rx="8" fill="#4ECDC4">
</rect>
<text x="805" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
HTTP Fetcher
</text>
<text x="805" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Async Workers)
</text>


<rect x="560" y="160" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="625" y="182" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
DNS Resolver
</text>
<text x="625" y="197" text-anchor="middle" fill="#fff" font-size="10">
(Cache + Prefetch)
</text>


<rect x="740" y="160" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="805" y="182" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Raw Store
</text>
<text x="805" y="197" text-anchor="middle" fill="#fff" font-size="10">
(S3 / HDFS)
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="330" y1="65" x2="375" y2="42" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="330" y1="85" x2="375" y2="100" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="510" y1="100" x2="555" y2="75" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="690" y1="75" x2="735" y2="75" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="625" y1="100" x2="625" y2="155" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="805" y1="100" x2="805" y2="155" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah)">
</line>


</svg>

</details>


### Steps


1. Seed URLs loaded into URL Frontier — initial set of high-authority domains (news, Wikipedia, popular sites)

2. URL Deduplicator checks Bloom filter (billions of URLs, ~1% false positive) — skips already-crawled URLs

3. Politeness Queue ensures per-domain rate limiting: separate FIFO queue per domain with configurable delay

4. robots.txt fetched and cached per domain (TTL: 24h); rules parsed to check if URL path is allowed

5. DNS Resolver resolves hostname → IP; results cached locally (TTL from DNS) with async prefetch for queued domains

6. HTTP Fetcher (async I/O workers) downloads page: HTTP/1.1 or HTTP/2, follows redirects (max 5), timeout 30s

7. Raw HTML stored in blob storage (S3/HDFS) keyed by URL hash; metadata (status code, headers, fetch time) stored separately

8. Discovered URLs from page extracted and fed back into URL Frontier (recursive loop)


### Example


Frontier dequeues `https://www.nytimes.com/2024/news/article`. Bloom filter check: not seen. Politeness queue for nytimes.com: last request was 1.2s ago, Crawl-delay: 1 → allowed. robots.txt cached: path /2024/news/* is allowed. DNS cached: nytimes.com → 151.101.1.164. Fetcher downloads 45KB HTML in 180ms. Stored to S3. Link extractor finds 23 outgoing URLs → fed back to frontier.


### Deep Dives


URL Frontier Architecture


**Front Queues:** Priority-based — URLs scored by PageRank, domain authority, freshness signal; higher priority dequeued first


**Back Queues:** Politeness-based — one FIFO queue per domain; ensures no domain hit faster than allowed rate


**Routing:** URL enters front queue by priority → assigned to back queue by domain hash → dequeued when politeness timer allows


**Implementation:** RocksDB for disk-backed queues (can't fit billions of URLs in memory); Redis for active/hot domains


URL Deduplication (Bloom Filter)


**Type:** Counting Bloom filter or Cuckoo filter — ~10 bytes per URL, supports billions of URLs in ~10GB memory


**False Positive:** ~1% — some URLs skipped unnecessarily (acceptable tradeoff vs. memory)


**Canonical:** URLs canonicalized before hashing: lowercase host, remove fragments, sort query params, resolve relative paths


**Persistent:** Bloom filter checkpointed to disk periodically; rebuilt from URL database on cold start


DNS Caching


**Local Cache:** In-memory DNS cache with TTL from DNS response (min 5min, max 24hr)


**Prefetch:** When domain enters frontier queue, DNS prefetched asynchronously before fetch time


**Scale:** ~100M unique domains → cache hit rate ~95% after warm-up


**Fallback:** Multiple DNS resolvers (Google 8.8.8.8, Cloudflare 1.1.1.1) for redundancy


robots.txt Compliance


**Standard:** Robots Exclusion Protocol (REP) — parsed per User-agent, Disallow, Allow, Crawl-delay, Sitemap directives


**Cache:** robots.txt cached per domain (24h TTL); re-fetched on 4xx/5xx expiry


**Sitemap:** Sitemap URLs extracted from robots.txt → priority boost for listed URLs


**Edge Cases:** No robots.txt (allow all), 5xx (assume allow, retry later), very large robots.txt (truncate at 500KB)


## Flow 2: Content Processing & Deduplication


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#45B7D1">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#795548">
</rect>
<text x="80" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Raw HTML
</text>
<text x="80" y="97" text-anchor="middle" fill="#fff" font-size="10">
(From Store)
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
HTML Parser
</text>
<text x="255" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Extract + Clean)
</text>


<rect x="370" y="30" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="435" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
SimHash / MinHash
</text>
<text x="435" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Near-Duplicate)
</text>


<rect x="370" y="100" width="130" height="50" rx="8" fill="#4ECDC4">
</rect>
<text x="435" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Link Extractor
</text>
<text x="435" y="137" text-anchor="middle" fill="#fff" font-size="10">
(URL Discovery)
</text>


<rect x="560" y="30" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="625" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Content Index
</text>
<text x="625" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Inverted Index)
</text>


<rect x="560" y="100" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="625" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
URL Frontier
</text>
<text x="625" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Feedback Loop)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#45B7D1" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#45B7D1" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="95" x2="365" y2="120" stroke="#45B7D1" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="500" y1="55" x2="555" y2="55" stroke="#45B7D1" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="500" y1="125" x2="555" y2="125" stroke="#45B7D1" stroke-width="2" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Steps


1. Raw HTML consumed from blob store by processing pipeline (MapReduce / Spark / Flink)

2. HTML Parser extracts: title, meta description, body text (boilerplate removed), headings, images alt text, structured data (schema.org)

3. Near-duplicate detection: SimHash (64-bit fingerprint, Hamming distance ≤3 = duplicate) or MinHash + LSH for Jaccard similarity

4. Exact duplicate: MD5/SHA-256 of normalized text content — identical pages from different URLs

5. Link Extractor: parses all `<a href>` tags, resolves relative URLs, filters nofollow, discovered URLs fed back to Frontier

6. Clean text + metadata written to Content Index (inverted index) for search engine consumption


### Example


Page `example.com/article` processed: 2,500 words extracted (nav/footer/ads removed via boilerplate detection). SimHash: 0xA3F1...B7 — compared against fingerprint store, Hamming distance 0 with previously crawled version → duplicate flagged (page hasn't changed, skip re-indexing). Link extractor finds 15 new outgoing URLs → 12 pass dedup filter → added to frontier.


### Deep Dives


SimHash (Near-Duplicate Detection)


**Algorithm:** Hash each word/shingle → weighted sum of bit positions → sign determines final 64-bit fingerprint


**Similarity:** Hamming distance ≤3 between two SimHashes → pages are near-duplicates (~95% similar)


**Advantage:** Single 64-bit comparison instead of full text diff — O(1) per comparison


**Scale:** 5 billion pages × 8 bytes = 40GB — fits in memory on single machine


Boilerplate Removal


**Algorithm:** DOM-based text density analysis — paragraphs with high text-to-HTML ratio = content; low ratio = navigation/ads


**Tools:** Readability algorithm (Mozilla), Dragnet, or ML-based content extraction


**Signal:** Text block length, link density, DOM depth, semantic tags (article, main, aside)


## Flow 3: Re-Crawl Scheduling & Freshness


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#96CEB4">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="85" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Crawl History DB
</text>
<text x="85" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Change Freq)
</text>


<rect x="200" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="265" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Scheduler
</text>
<text x="265" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Freshness Model)
</text>


<rect x="380" y="30" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="445" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Priority Scorer
</text>
<text x="445" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Importance × Age)
</text>


<rect x="380" y="100" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="445" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Sitemap Parser
</text>
<text x="445" y="137" text-anchor="middle" fill="#fff" font-size="10">
(changefreq Hints)
</text>


<rect x="570" y="60" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="635" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
URL Frontier
</text>
<text x="635" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Re-Crawl Queue)
</text>


<line x1="150" y1="85" x2="195" y2="85" stroke="#96CEB4" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="330" y1="75" x2="375" y2="55" stroke="#96CEB4" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="330" y1="95" x2="375" y2="120" stroke="#96CEB4" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="510" y1="55" x2="565" y2="75" stroke="#96CEB4" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="510" y1="125" x2="565" y2="95" stroke="#96CEB4" stroke-width="2" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Steps


1. Crawl History DB stores per-URL: last_crawled, change_frequency (estimated from past crawls), importance score

2. Scheduler runs periodically — scans URLs whose re-crawl time has passed based on estimated change frequency

3. Priority Scorer computes: `priority = importance × staleness` where staleness = (now - last_crawled) / expected_change_interval

4. Sitemap changefreq hints (always, hourly, daily, weekly, monthly, yearly) used as initial estimate; refined by observed changes

5. Exponential backoff for unchanged pages: if page hasn't changed in 3 consecutive crawls, double the re-crawl interval (max: 30 days)

6. Prioritized URLs enqueued into URL Frontier for re-crawling; high-priority (news homepages) bypass normal queue


### Example


`nytimes.com` homepage: estimated change frequency = 15 minutes (changes constantly). Last crawled 20min ago → staleness = 1.33 × importance 0.99 = priority 1.32 → HIGH. `example.com/about`: hasn't changed in 3 crawls, change_freq doubled from 7d to 14d. Last crawled 10d ago → staleness 0.71 × importance 0.2 = priority 0.14 → LOW.


### Deep Dives


Change Detection


**Method:** Compare SimHash of new page vs. stored SimHash from previous crawl


**Conditional:** HTTP If-Modified-Since / If-None-Match (ETag) headers — server returns 304 Not Modified if unchanged (saves bandwidth)


**Estimation:** Poisson process model for change frequency: λ estimated from observed change history using MLE


Adaptive Crawl Rate


**Budget:** Total crawl capacity (10K pages/sec) allocated across domains by importance


**Allocation:** High-authority domains (news, Wikipedia) get 60% of budget; long-tail gets 40%


**Feedback:** If server returns 429 (Too Many Requests) or slow response, back off crawl rate for that domain


## Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 450" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#4ECDC4">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="130" height="50" rx="8" fill="#FF6B6B">
</rect>
<text x="85" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Seed URLs +
</text>
<text x="85" y="97" text-anchor="middle" fill="#fff" font-size="10">
Sitemaps
</text>


<rect x="200" y="60" width="140" height="55" rx="8" fill="#9C27B0">
</rect>
<text x="270" y="82" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
URL Frontier
</text>
<text x="270" y="100" text-anchor="middle" fill="#fff" font-size="10">
(Priority + Politeness)
</text>


<rect x="400" y="30" width="130" height="45" rx="8" fill="#4285F4">
</rect>
<text x="465" y="57" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
URL Dedup (Bloom)
</text>


<rect x="400" y="90" width="130" height="45" rx="8" fill="#FB8C00">
</rect>
<text x="465" y="117" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
robots.txt Cache
</text>


<rect x="590" y="60" width="130" height="50" rx="8" fill="#4ECDC4">
</rect>
<text x="655" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
HTTP Fetchers
</text>
<text x="655" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Async Pool)
</text>


<rect x="780" y="60" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="845" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
DNS Cache
</text>


<rect x="400" y="200" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="465" y="222" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
HTML Parser
</text>
<text x="465" y="237" text-anchor="middle" fill="#fff" font-size="10">
(Content Extract)
</text>


<rect x="590" y="200" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="655" y="222" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Dedup Engine
</text>
<text x="655" y="237" text-anchor="middle" fill="#fff" font-size="10">
(SimHash/MinHash)
</text>


<rect x="780" y="200" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="845" y="222" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Link Extractor
</text>


<rect x="200" y="330" width="140" height="55" rx="8" fill="#795548">
</rect>
<text x="270" y="352" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Raw Store
</text>
<text x="270" y="370" text-anchor="middle" fill="#fff" font-size="10">
(S3 / HDFS)
</text>


<rect x="400" y="330" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="465" y="352" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Crawl DB
</text>
<text x="465" y="370" text-anchor="middle" fill="#fff" font-size="10">
(HBase / Cassandra)
</text>


<rect x="590" y="330" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="655" y="352" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Content Index
</text>
<text x="655" y="370" text-anchor="middle" fill="#fff" font-size="10">
(Search Engine)
</text>


<rect x="780" y="330" width="130" height="55" rx="8" fill="#00897B">
</rect>
<text x="845" y="352" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Kafka
</text>
<text x="845" y="370" text-anchor="middle" fill="#fff" font-size="10">
(URL Pipeline)
</text>


<line x1="150" y1="85" x2="195" y2="85" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="340" y1="75" x2="395" y2="52" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="340" y1="95" x2="395" y2="110" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="530" y1="75" x2="585" y2="80" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="720" y1="85" x2="775" y2="85" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="655" y1="110" x2="465" y2="195" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="530" y1="225" x2="585" y2="225" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="720" y1="225" x2="775" y2="225" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="845" y1="250" x2="845" y2="325" stroke="#4ECDC4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="655" y1="110" x2="270" y2="325" stroke="#4ECDC4" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="465" y1="250" x2="465" y2="325" stroke="#4ECDC4" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="655" y1="250" x2="655" y2="325" stroke="#4ECDC4" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="910" y1="355" x2="340" y2="95" stroke="#4ECDC4" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah4)">
</line>


</svg>

</details>


## Database Schema


### HBase / Cassandra — Crawl Database

`Row Key: reverse_domain + url_hash
  e.g., "com.nytimes.www/article/123" → SHA-256 prefix

Column Families:
  fetch:
    - status_code     (int)
    - content_type    (string)
    - content_length  (int)
    - response_time_ms (int)
    - redirect_url    (string)
    - last_fetched_at (timestamp)   `IDX`
    - fetch_count     (int)
  content:
    - raw_html_ref    (string → S3/HDFS key)
    - text_hash       (char64, SHA-256 of extracted text)
    - simhash         (bigint, 64-bit fingerprint)
    - title           (string)
    - language        (string, ISO 639-1)
    - word_count      (int)
  schedule:
    - priority        (float)
    - change_freq_hrs (float, estimated)
    - next_crawl_at   (timestamp)   `IDX`
    - consecutive_unchanged (int)
  links:
    - outgoing_urls   (list of strings)
    - incoming_count  (int, backlink count)`


 `SHARD` Sharded by reverse domain prefix — all URLs from same domain co-located for efficient per-domain queries


### Frontier Queue (RocksDB + Redis)

`# Priority Front Queue (Redis Sorted Set)
ZADD frontier:priority {score} {url_hash}
# Score = importance × staleness

# Politeness Back Queue (per-domain FIFO)
LPUSH domain:{domain_hash}:queue {url_hash}
# One list per domain

# Domain Rate Limiter (Redis)
SET domain:{domain_hash}:last_fetch {timestamp}
SET domain:{domain_hash}:crawl_delay {seconds}

# Bloom Filter (Redis / RocksDB)
BF.EXISTS seen_urls {url_hash}
BF.ADD seen_urls {url_hash}

# robots.txt Cache (Redis Hash)
HSET robots:{domain_hash} rules {parsed_rules_json}
HSET robots:{domain_hash} fetched_at {timestamp}
EXPIRE robots:{domain_hash} 86400  # 24hr TTL

# DNS Cache (Redis)
SET dns:{hostname} {ip_address}
EXPIRE dns:{hostname} {ttl_from_dns}`


## Cache Deep Dive


DNS Cache


**Strategy:** Local in-memory cache (100M+ entries) + Redis shared cache across crawler fleet


**Hit Rate:** ~95% after warm-up — most URLs resolve to previously seen domains


**Prefetch:** When URL enters frontier, DNS prefetched asynchronously in background


robots.txt Cache


**Strategy:** Cache-aside — fetch on first access per domain, cache in Redis with 24hr TTL


**Refresh:** Re-fetched when TTL expires or on 4xx/5xx response


**Size:** ~100M domains × ~2KB avg = ~200GB (mostly in Redis cluster)


Content Hash Store


**Purpose:** Store SimHash fingerprints for dedup — 5B pages × 8 bytes = 40GB


**Strategy:** In-memory hashtable (SimHash → URL list) for exact-match dedup


**Near-Dup:** Bit-sampling technique: split 64-bit SimHash into 4×16-bit blocks, index by each block for fast Hamming neighbor lookup


## Scaling & Load Balancing


- **Crawler Fleet:** Hundreds of machines, each running 1000+ async HTTP connections; geographically distributed for lower latency to target servers

- **URL Partitioning:** URL Frontier partitioned by domain hash — each crawler instance responsible for set of domains (avoids duplicate fetches and ensures politeness)

- **Processing Pipeline:** Spark/Flink cluster for HTML parsing, content extraction, dedup — horizontally scaled with data parallelism

- **Storage:** HDFS/S3 for raw HTML (5B pages × ~50KB = ~250TB); HBase for crawl metadata (sharded by reverse domain)

- **Kafka:** URL discovery pipeline — newly discovered URLs published to Kafka topic, consumed by URL dedup + frontier workers

- **Monitoring:** Crawl rate per domain, error rates, queue depth, duplicate rate, content quality metrics tracked in real-time dashboard


## Tradeoffs


> **

✅ BFS (Breadth-First) Crawl


- Discovers high-quality pages first (closer to seed URLs)

- Natural politeness — spreads requests across many domains

- Better coverage of the web graph at each depth level


❌ BFS (Breadth-First) Crawl


- Frontier queue grows rapidly (billions of URLs in queue)

- May crawl many low-value pages at same depth level

- Priority inversion — important deep pages discovered late


✅ Bloom Filter (vs. Hash Set)


- 10x less memory than exact hash set (10 bytes vs. 100 bytes per URL)

- O(1) lookup and insertion — constant time regardless of size

- No false negatives — if URL is seen, Bloom filter guarantees detection


❌ Bloom Filter


- False positives (~1%) — some new URLs skipped (lost coverage)

- Cannot delete entries (standard Bloom) — counting Bloom filter is 4x larger

- Fixed capacity — must be sized for maximum expected URLs


## Alternative Approaches


Headless Browser Rendering (Googlebot WRS)


Use headless Chrome to render JavaScript-heavy pages (SPAs, React apps). Captures dynamically-loaded content invisible to simple HTTP fetch. But 10-100x more resource-intensive per page, requires managing browser instances at scale, and introduces rendering latency (2-5s per page).


Focused Crawling


Crawl only pages relevant to a specific topic (e.g., medical papers, legal documents). Use classifier to score page relevance, only follow links from relevant pages. Much more efficient but requires topic-specific training data and risks missing relevant pages in unexpected locations.


Distributed Hash Table (DHT) Frontier


Use DHT (like Chord/Kademlia) for URL frontier distribution across crawler nodes. Each node responsible for domain hash range. Self-organizing, fault-tolerant, but added complexity and potential hotspots for popular domains.


## Additional Information


- **Spider Traps:** Infinite URL generators (calendars, session IDs in URLs, query parameter permutations). Mitigation: max URL length (2048), max depth per domain (15), max pages per domain (100K), URL pattern detection (regex dedup).

- **Ethical Crawling:** Beyond robots.txt — respect meta noindex/nofollow, X-Robots-Tag headers, avoid crawling during business hours for small sites, identify crawler via User-Agent string with contact info.

- **Legal Considerations:** hiQ Labs v. LinkedIn (2022) — scraping publicly available data generally legal under CFAA, but Terms of Service violations may create civil liability. GDPR considerations for personal data in crawled content.

- **Mercator Crawler:** Foundational academic crawler architecture (Heydon & Najork, 1999) — introduced the front/back queue politeness model used by most modern crawlers including Googlebot.

- **Common Crawl:** Non-profit providing free monthly crawl dataset — 3.6B pages, 380TB compressed. Alternative to running your own crawler for many use cases (ML training data, research).