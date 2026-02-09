# 🔍 Design Google Search


Web-scale search engine processing 8.5B+ queries/day with sub-200ms latency, indexing 400B+ web pages


## 1. Functional Requirements


- **Web Search:** Return ranked results for keyword/natural-language queries within 200ms

- **Query Autocomplete:** Suggest completions and corrections as user types (prefetch within 50ms)

- **Crawl & Index:** Continuously discover, fetch, parse, and index web pages (400B+ pages)

- **Snippet Generation:** Extract and highlight relevant text snippets from matched documents

- **Ranking:** Order results by relevance using PageRank, content signals, user behavior, and ML re-ranking

- **Vertical Search:** Images, news, videos, shopping, maps integrated into universal results

- **Knowledge Graph:** Display entity cards, knowledge panels, and direct answers

- **Ads Serving:** Display auction-based sponsored results (CPC/CPM)


## 2. Non-Functional Requirements


- **Latency:** p50 <200ms, p99 <500ms end-to-end for organic results

- **Throughput:** 100K+ queries/second globally (8.5B queries/day)

- **Index Size:** 400B+ pages, 100PB+ compressed index

- **Freshness:** Breaking news indexed within minutes, regular crawl cycle hours-to-weeks

- **Availability:** 99.999% uptime (Google SRE target)

- **Scalability:** Horizontal scaling across dozens of data centers worldwide

- **Consistency:** Eventual consistency for index updates; strong consistency for user accounts


## 3. Flow 1 — Query Processing & Serving


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 420" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#4285F4">
</polygon>
</marker>
</defs>


<rect x="20" y="170" width="120" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="80" y="205" text-anchor="middle" fill="#fff" font-size="14">
User Browser
</text>


<rect x="190" y="170" width="120" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="250" y="200" text-anchor="middle" fill="#fff" font-size="13">
Google Front
</text>
<text x="250" y="215" text-anchor="middle" fill="#fff" font-size="13">
End (GFE)
</text>


<rect x="360" y="70" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="425" y="100" text-anchor="middle" fill="#fff" font-size="13">
Query Parser
</text>
<text x="425" y="115" text-anchor="middle" fill="#fff" font-size="11">
spell/synonyms
</text>


<rect x="360" y="170" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="425" y="200" text-anchor="middle" fill="#fff" font-size="13">
Web Server
</text>
<text x="425" y="215" text-anchor="middle" fill="#fff" font-size="11">
blending/mixing
</text>


<rect x="360" y="270" width="130" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="425" y="300" text-anchor="middle" fill="#fff" font-size="13">
Ads Server
</text>
<text x="425" y="315" text-anchor="middle" fill="#fff" font-size="11">
auction engine
</text>


<rect x="550" y="70" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="620" y="95" text-anchor="middle" fill="#fff" font-size="13">
Index Server
</text>
<text x="620" y="112" text-anchor="middle" fill="#fff" font-size="11">
scatter-gather
</text>


<rect x="550" y="170" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="620" y="195" text-anchor="middle" fill="#fff" font-size="13">
Doc Server
</text>
<text x="620" y="212" text-anchor="middle" fill="#fff" font-size="11">
snippets/metadata
</text>


<rect x="550" y="270" width="140" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="620" y="300" text-anchor="middle" fill="#fff" font-size="13">
ML Ranker
</text>
<text x="620" y="315" text-anchor="middle" fill="#fff" font-size="11">
BERT re-ranking
</text>


<rect x="750" y="120" width="150" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="825" y="150" text-anchor="middle" fill="#fff" font-size="13">
Inverted Index
</text>
<text x="825" y="165" text-anchor="middle" fill="#fff" font-size="11">
(SSTable/Bigtable)
</text>


<rect x="750" y="220" width="150" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="825" y="250" text-anchor="middle" fill="#fff" font-size="13">
Doc Store
</text>
<text x="825" y="265" text-anchor="middle" fill="#fff" font-size="11">
(Colossus/GFS)
</text>


<rect x="940" y="170" width="130" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="1005" y="200" text-anchor="middle" fill="#fff" font-size="13">
Knowledge
</text>
<text x="1005" y="215" text-anchor="middle" fill="#fff" font-size="13">
Graph
</text>


<line x1="140" y1="200" x2="188" y2="200" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="310" y1="190" x2="358" y2="105" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="310" y1="200" x2="358" y2="200" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="310" y1="210" x2="358" y2="295" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="100" x2="548" y2="100" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="200" x2="548" y2="200" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="200" x2="548" y2="295" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="690" y1="100" x2="748" y2="145" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="690" y1="200" x2="748" y2="245" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="690" y1="200" x2="938" y2="200" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


</svg>

</details>


### Flow Steps


1. **User submits query** → HTTPS GET to `www.google.com/search?q=...`

2. **GFE (Google Front End)** terminates TLS, applies rate limiting, routes to nearest serving cluster via anycast

3. **Query Parser** tokenizes query, applies spell correction (did-you-mean), synonym expansion, entity detection, and locale/language normalization

4. **Web Server (Superroot)** fans out parsed query to Index Servers, Ads Server, Knowledge Graph, verticals (Images, News, etc.) in parallel

5. **Index Server (scatter-gather)** — query is sent to thousands of index shards in parallel; each shard performs inverted-index lookup + initial scoring (BM25 + static rank); top-K results returned per shard

6. **Doc Server** fetches document metadata, generates snippets with query-term highlighting from cached page content

7. **ML Ranker** applies BERT-based re-ranking on top candidates (typically top 1000 → re-rank to final top ~10-20)

8. **Superroot blends** organic results, ads, knowledge panel, featured snippets, "People Also Ask," images, news into final SERP

9. **Response** rendered as HTML (server-side) and returned within ~200ms


### Example


**Query:** `"best italian restaurants san francisco"`


**Query Parser Output:**

{
  "tokens": ["best", "italian", "restaurants", "san francisco"],
  "entity": {"type": "cuisine", "value": "italian"},
  "location": {"city": "San Francisco", "lat": 37.77, "lng": -122.42},
  "intent": "local_business_search",
  "spell_correction": null,
  "synonyms": {"restaurants": ["eateries", "dining"]}
}


**Scatter-Gather:** Query sent to 10,000+ index shards → each returns top-50 doc IDs with BM25 scores → merged into global top-1000 → BERT re-ranks → top-10 organic results + local pack + ads


**Latency breakdown:** GFE 5ms + parse 10ms + index scatter-gather 80ms + doc fetch 30ms + ML re-rank 40ms + blending 15ms + render 20ms = ~200ms total


### Deep Dives


Inverted Index


Maps every token to a posting list: `token → [(docID, tf, positions), ...]`. Posting lists are sorted by docID for efficient intersection (zipper merge). Google stores in **SSTable** format on **Colossus** (GFS successor). Index is split into **tiers**: base index (high-quality pages, in-memory), extended index (long-tail, on SSD), real-time index (fresh content, in-memory). Compression: variable-byte encoding for docIDs, delta encoding for positions. Total index: ~100PB compressed across all data centers.


Scatter-Gather Architecture


The inverted index is horizontally sharded across thousands of machines. A query is broadcast to ALL shards in parallel (scatter). Each shard performs local BM25 scoring and returns its top-K results. The coordinator merges (gathers) all shard results using a priority queue. **Hedged requests**: send duplicate requests to reduce tail latency (if one shard is slow, use the backup's response). **Canary requests**: test query on a few shards before full fanout to detect crashes. Timeout at p99 + buffer; return partial results if some shards don't respond.


BERT Re-Ranking


Google applies BERT (Bidirectional Encoder Representations from Transformers) to understand query-document relevance at a semantic level. Applied to the top ~1000 candidates (too expensive for all 400B pages). Uses a cross-encoder: `[CLS] query [SEP] document [SEP]` → relevance score. Distilled to smaller models for serving latency. **MUM** (Multitask Unified Model) extends this to be multimodal and multilingual. Running on TPU v4 pods in serving path.


PageRank & Static Rank


PageRank computes a static quality score for each page based on the link graph: `PR(A) = (1-d) + d × Σ(PR(Ti)/C(Ti))` where d=0.85 damping factor, Ti = pages linking to A, C(Ti) = outbound links from Ti. Computed offline via iterative MapReduce (converges in ~50-100 iterations). Combined with 200+ other signals: domain authority, content freshness, HTTPS, mobile-friendliness, Core Web Vitals, E-E-A-T signals.


## 4. Flow 2 — Web Crawling & Indexing Pipeline


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#34A853">
</polygon>
</marker>
</defs>


<rect x="20" y="140" width="120" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="80" y="170" text-anchor="middle" fill="#fff" font-size="13">
URL Frontier
</text>
<text x="80" y="185" text-anchor="middle" fill="#fff" font-size="11">
priority queue
</text>


<rect x="190" y="140" width="120" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="250" y="170" text-anchor="middle" fill="#fff" font-size="13">
Crawler
</text>
<text x="250" y="185" text-anchor="middle" fill="#fff" font-size="11">
(Googlebot)
</text>


<rect x="190" y="50" width="120" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="250" y="80" text-anchor="middle" fill="#fff" font-size="13">
DNS Resolver
</text>
<text x="250" y="95" text-anchor="middle" fill="#fff" font-size="11">
+ robots.txt
</text>


<rect x="360" y="140" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="425" y="170" text-anchor="middle" fill="#fff" font-size="13">
Parser / Renderer
</text>
<text x="425" y="185" text-anchor="middle" fill="#fff" font-size="11">
headless Chrome
</text>


<rect x="540" y="50" width="130" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="605" y="80" text-anchor="middle" fill="#fff" font-size="13">
Duplicate
</text>
<text x="605" y="95" text-anchor="middle" fill="#fff" font-size="11">
Detector (SimHash)
</text>


<rect x="540" y="140" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="605" y="170" text-anchor="middle" fill="#fff" font-size="13">
Indexer
</text>
<text x="605" y="185" text-anchor="middle" fill="#fff" font-size="11">
token→posting
</text>


<rect x="540" y="230" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="605" y="260" text-anchor="middle" fill="#fff" font-size="13">
Link Extractor
</text>
<text x="605" y="275" text-anchor="middle" fill="#fff" font-size="11">
→ URL Frontier
</text>


<rect x="720" y="140" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="790" y="170" text-anchor="middle" fill="#fff" font-size="13">
Index Shards
</text>
<text x="790" y="185" text-anchor="middle" fill="#fff" font-size="11">
(SSTable/Colossus)
</text>


<rect x="910" y="140" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="980" y="170" text-anchor="middle" fill="#fff" font-size="13">
PageRank
</text>
<text x="980" y="185" text-anchor="middle" fill="#fff" font-size="11">
(offline MapReduce)
</text>


<line x1="140" y1="170" x2="188" y2="170" stroke="#34A853" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="250" y1="140" x2="250" y2="112" stroke="#34A853" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="310" y1="170" x2="358" y2="170" stroke="#34A853" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="490" y1="155" x2="538" y2="85" stroke="#34A853" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="490" y1="170" x2="538" y2="170" stroke="#34A853" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="490" y1="185" x2="538" y2="255" stroke="#34A853" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="670" y1="170" x2="718" y2="170" stroke="#34A853" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="860" y1="170" x2="908" y2="170" stroke="#34A853" stroke-width="2" marker-end="url(#a2)">
</line>


<path d="M605 290 L605 320 L50 320 L50 170 L18 170" stroke="#34A853" stroke-width="2" marker-end="url(#a2)" fill="none" stroke-dasharray="5,5">
</path>


</svg>

</details>


### Flow Steps


1. **URL Frontier** maintains a priority queue of URLs to crawl, ordered by importance (PageRank, freshness), with politeness constraints (max crawl rate per domain via per-host queues)

2. **Googlebot** fetches pages via HTTP/2, respecting `robots.txt` (cached per domain), `Crawl-delay`, and `noindex`/`nofollow` directives

3. **Renderer** runs headless Chromium (WRS — Web Rendering Service) to execute JavaScript SPAs, producing final DOM

4. **Duplicate Detector** computes SimHash/MinHash fingerprint; near-duplicates are clustered, canonical selected

5. **Indexer** tokenizes document content, builds inverted index entries (term → posting list with positions, frequencies)

6. **Link Extractor** discovers new URLs from anchor tags, feeds back into URL Frontier (the crawl cycle)

7. **Index Shards** receive new/updated entries via a streaming pipeline; shards are served from SSTable files on Colossus

8. **PageRank** runs as offline batch job (MapReduce/Pregel) over the entire link graph, produces static quality scores


### Example


**Scenario:** New blog post published at `https://example.com/blog/new-post`


**Discovery:** Googlebot finds URL via sitemap.xml submission (Google Search Console) or from a link on the homepage


**Crawl:** HTTP GET → 200 OK, Content-Type: text/html, 45KB body


**Render:** Page uses React SSR → WRS skips JS rendering (DOM already present). If client-side only, WRS queues for headless Chrome rendering (may take hours)


**Index:** Tokenized into ~3000 terms. Title, H1, meta description get boosted weight. Added to real-time index tier → searchable within minutes. Later merged to base index tier during compaction.


**Recrawl schedule:** High-authority sites (CNN, Wikipedia): every few minutes. Low-authority blogs: every few weeks.


### Deep Dives


URL Frontier Design


Two-level queue: **front queues** (prioritized by importance: PageRank score, freshness, crawl budget) and **back queues** (one per host for politeness). The front queues feed into a router that assigns URLs to back queues based on hostname. Each back queue has a heap-based timer ensuring minimum crawl interval per host (typically 1 request per second per domain). URL deduplication via Bloom filter (billions of URLs seen). The frontier uses **Mercator-style** architecture. Crawl budget allocation uses a formula: `budget(site) = f(PageRank, freshness_decay, update_frequency)`.


Web Rendering Service (WRS)


Many modern sites rely on client-side JavaScript (React, Angular, Vue). Googlebot's WRS runs headless Chromium to execute JS and produce the final DOM. This is a **two-phase indexing**: first index the raw HTML (fast), then render JS and re-index (delayed, can take hours/days). WRS resource cost: rendering 1 page ≈ 5-10x the cost of a simple HTTP fetch. Google encourages SSR/SSG for SEO. WRS handles: dynamic imports, IntersectionObserver lazy loading, Web Components, service workers (skipped).


SimHash Duplicate Detection


Computing exact duplicates across 400B pages is infeasible. **SimHash**: produce a 64-bit fingerprint from document features (shingles of n-grams). Two documents are near-duplicates if their SimHash values differ in ≤3 bits (Hamming distance). Stored in a hash table with multi-probe lookups. Also use **MinHash** with LSH (Locality-Sensitive Hashing) for set similarity (Jaccard). Canonical URL selection: highest PageRank page in a duplicate cluster becomes the canonical.


Freshness: Caffeine Architecture


Google's **Caffeine** indexing system (2010+) moved from batch-based full index rebuilds to an **incremental, continuously-updated** index. New content flows through a streaming pipeline and becomes searchable within minutes (real-time tier). The system uses a **multi-tier index**: real-time (in-memory, seconds-fresh), base (SSTable, hours-fresh), and archive (rarely-accessed long-tail). Compaction merges real-time entries into base tier periodically. This gives Google a significant freshness advantage.


## 5. Flow 3 — Query Autocomplete & Spell Correction


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 300" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#FBBC05">
</polygon>
</marker>
</defs>


<rect x="20" y="120" width="130" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="85" y="150" text-anchor="middle" fill="#fff" font-size="13">
User Input
</text>
<text x="85" y="165" text-anchor="middle" fill="#fff" font-size="11">
keystroke stream
</text>


<rect x="200" y="120" width="130" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="265" y="150" text-anchor="middle" fill="#fff" font-size="13">
Debounce
</text>
<text x="265" y="165" text-anchor="middle" fill="#fff" font-size="11">
150ms client-side
</text>


<rect x="380" y="30" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="450" y="55" text-anchor="middle" fill="#fff" font-size="13">
Trie Lookup
</text>
<text x="450" y="72" text-anchor="middle" fill="#fff" font-size="11">
prefix → top-K
</text>


<rect x="380" y="120" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="450" y="150" text-anchor="middle" fill="#fff" font-size="13">
Personalization
</text>
<text x="450" y="165" text-anchor="middle" fill="#fff" font-size="11">
user history
</text>


<rect x="380" y="210" width="140" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="450" y="240" text-anchor="middle" fill="#fff" font-size="13">
Spell Corrector
</text>
<text x="450" y="255" text-anchor="middle" fill="#fff" font-size="11">
noisy channel
</text>


<rect x="580" y="120" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="650" y="150" text-anchor="middle" fill="#fff" font-size="13">
Ranking /
</text>
<text x="650" y="165" text-anchor="middle" fill="#fff" font-size="13">
Blending
</text>


<rect x="780" y="120" width="130" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="845" y="150" text-anchor="middle" fill="#fff" font-size="13">
Suggestions
</text>
<text x="845" y="165" text-anchor="middle" fill="#fff" font-size="11">
dropdown UI
</text>


<line x1="150" y1="150" x2="198" y2="150" stroke="#FBBC05" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="330" y1="140" x2="378" y2="65" stroke="#FBBC05" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="330" y1="150" x2="378" y2="150" stroke="#FBBC05" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="330" y1="160" x2="378" y2="235" stroke="#FBBC05" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="520" y1="65" x2="578" y2="140" stroke="#FBBC05" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="520" y1="150" x2="578" y2="150" stroke="#FBBC05" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="520" y1="235" x2="578" y2="165" stroke="#FBBC05" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="720" y1="150" x2="778" y2="150" stroke="#FBBC05" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


### Flow Steps


1. **Keystroke stream** from user input is debounced on the client (150ms delay to avoid excessive RPCs)

2. **Prefix sent** to autocomplete service via HTTPS GET: `/complete/search?q=ita&client=gws`

3. **Trie Lookup** performs prefix match on a compressed trie built from billions of past queries, weighted by frequency + recency

4. **Personalization** boosts suggestions based on user's search history and location (e.g., user in SF → local results boosted)

5. **Spell Corrector** applies noisy channel model: `P(correction|query) ∝ P(query|correction) × P(correction)` — language model probability × edit distance probability

6. **Ranking** blends trie results, personalized results, trending queries, and spell corrections into final top-10 suggestions

7. **Response** returned as JSON within ~50ms, rendered in dropdown


### Example


**User types:** `"ita"`


**Trie results:** ["italian restaurants near me", "italy travel", "italian flag", "itachi", "itar regulations"]


**Personalization (user in SF):** Boost "italian restaurants near me" + "italian restaurants san francisco"


**Trending:** "italy election results" (trending this week)


**Final suggestions:** ["italian restaurants near me", "italian restaurants san francisco", "italy election results", "italy travel", "itachi"]


**Latency:** Client debounce 150ms + network RTT 20ms + server 15ms = suggestion appears ~185ms after keystroke pause


### Deep Dives


Compressed Trie Data Structure


A standard trie for billions of queries would require petabytes. Google uses a **compressed (Patricia) trie** that merges single-child chains. Further optimization: **succinct trie** representation using LOUDS (Level-Order Unary Degree Sequence) encoding — stores the trie structure in ~2 bits per node. Each leaf/node stores the top-K query completions pre-computed. The trie is sharded by first 2-3 characters of the prefix. Stored entirely in memory across the autocomplete cluster for sub-millisecond lookups.


Noisy Channel Spell Correction


Google's spell corrector uses Peter Norvig's noisy channel model at massive scale. **Language model P(correction)**: trained on billions of web documents (n-gram frequencies). **Error model P(query|correction)**: probability of the typo given the intended word (keyboard proximity, common substitutions like "teh"→"the"). For each query term, generate candidates within edit distance 1-2 (insertions, deletions, substitutions, transpositions). Rank by: `argmax P(c) × P(q|c)`. Also uses **context-dependent correction**: "italaian food" → both words jointly corrected to "italian food".


## 6. Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 600" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#4285F4">
</polygon>
</marker>
</defs>


<rect x="10" y="260" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="65" y="290" text-anchor="middle" fill="#fff" font-size="12">
Users
</text>
<text x="65" y="305" text-anchor="middle" fill="#fff" font-size="10">
8.5B queries/day
</text>


<rect x="160" y="200" width="120" height="50" rx="8" fill="#546e7a" stroke="#90a4ae" stroke-width="2">
</rect>
<text x="220" y="230" text-anchor="middle" fill="#fff" font-size="12">
CDN / Edge
</text>


<rect x="160" y="270" width="120" height="50" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="220" y="295" text-anchor="middle" fill="#fff" font-size="12">
GFE + LB
</text>
<text x="220" y="310" text-anchor="middle" fill="#fff" font-size="10">
anycast routing
</text>


<rect x="160" y="340" width="120" height="50" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="220" y="365" text-anchor="middle" fill="#fff" font-size="12">
Autocomplete
</text>


<rect x="330" y="60" width="130" height="50" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="395" y="90" text-anchor="middle" fill="#fff" font-size="12">
Query Parser
</text>


<rect x="330" y="140" width="130" height="50" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="395" y="165" text-anchor="middle" fill="#fff" font-size="12">
Superroot (WS)
</text>


<rect x="330" y="220" width="130" height="50" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="395" y="245" text-anchor="middle" fill="#fff" font-size="12">
Ads Auction
</text>


<rect x="330" y="300" width="130" height="50" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="395" y="325" text-anchor="middle" fill="#fff" font-size="12">
Knowledge Graph
</text>


<rect x="330" y="380" width="130" height="50" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="395" y="405" text-anchor="middle" fill="#fff" font-size="12">
Verticals
</text>
<text x="395" y="418" text-anchor="middle" fill="#fff" font-size="10">
img/news/video
</text>


<rect x="520" y="80" width="130" height="50" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="585" y="105" text-anchor="middle" fill="#fff" font-size="12">
Index Servers
</text>
<text x="585" y="118" text-anchor="middle" fill="#fff" font-size="10">
10K+ shards
</text>


<rect x="520" y="160" width="130" height="50" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="585" y="185" text-anchor="middle" fill="#fff" font-size="12">
Doc Servers
</text>


<rect x="520" y="240" width="130" height="50" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="585" y="265" text-anchor="middle" fill="#fff" font-size="12">
ML Ranker
</text>
<text x="585" y="278" text-anchor="middle" fill="#fff" font-size="10">
BERT/MUM on TPU
</text>


<rect x="700" y="80" width="130" height="50" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="765" y="105" text-anchor="middle" fill="#fff" font-size="12">
Inverted Index
</text>
<text x="765" y="118" text-anchor="middle" fill="#fff" font-size="10">
100PB+ SSTable
</text>


<rect x="700" y="160" width="130" height="50" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="765" y="185" text-anchor="middle" fill="#fff" font-size="12">
Doc Store
</text>
<text x="765" y="198" text-anchor="middle" fill="#fff" font-size="10">
Colossus/GFS
</text>


<rect x="700" y="240" width="130" height="50" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="765" y="265" text-anchor="middle" fill="#fff" font-size="12">
Bigtable
</text>
<text x="765" y="278" text-anchor="middle" fill="#fff" font-size="10">
user data
</text>


<rect x="520" y="400" width="130" height="50" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="585" y="425" text-anchor="middle" fill="#fff" font-size="12">
Crawler
</text>
<text x="585" y="438" text-anchor="middle" fill="#fff" font-size="10">
(Googlebot)
</text>


<rect x="700" y="400" width="130" height="50" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="765" y="425" text-anchor="middle" fill="#fff" font-size="12">
Indexing Pipeline
</text>
<text x="765" y="438" text-anchor="middle" fill="#fff" font-size="10">
Caffeine
</text>


<rect x="880" y="400" width="130" height="50" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="945" y="425" text-anchor="middle" fill="#fff" font-size="12">
PageRank
</text>
<text x="945" y="438" text-anchor="middle" fill="#fff" font-size="10">
MapReduce
</text>


<rect x="700" y="320" width="130" height="50" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="765" y="345" text-anchor="middle" fill="#fff" font-size="12">
Memcache
</text>
<text x="765" y="358" text-anchor="middle" fill="#fff" font-size="10">
result cache
</text>


<line x1="120" y1="290" x2="158" y2="290" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="120" y1="280" x2="158" y2="225" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="120" y1="300" x2="158" y2="360" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="280" y1="290" x2="328" y2="165" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="395" y1="110" x2="395" y2="138" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="460" y1="160" x2="518" y2="105" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="460" y1="170" x2="518" y2="185" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="460" y1="245" x2="518" y2="265" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="650" y1="105" x2="698" y2="105" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="650" y1="185" x2="698" y2="185" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="650" y1="425" x2="698" y2="425" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="830" y1="425" x2="878" y2="425" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="765" y1="400" x2="765" y2="372" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


</svg>

</details>


### Architecture Example Walkthrough


**Full query lifecycle for:** `"how to make sourdough bread"`


1. **Autocomplete:** As user types "how to make sour", suggestions appear: ["how to make sourdough bread", "how to make sourdough starter", ...]

2. **GFE:** Routes HTTPS request to nearest DC (e.g., us-west1), terminates TLS with BoringSSL

3. **Result Cache Check:** Memcache lookup for this exact query → cache HIT (popular query) → return cached SERP in 30ms

4. **Cache MISS path:** Query Parser → tokens: [sourdough, bread, make], intent: "how-to/recipe"

5. **Superroot fans out:** Index Servers (organic), Ads Auction (cooking ads), Knowledge Graph (sourdough entity), Recipe vertical, Video vertical (YouTube), "People Also Ask"

6. **Index Servers:** Scatter to 10K+ shards → BM25 + PageRank scoring → top-1000 candidates in 80ms

7. **ML Ranker:** BERT cross-encoder re-ranks top-1000 → recipe pages with structured data (schema.org/Recipe) boosted

8. **Blending:** Featured snippet (extracted recipe summary), organic results, recipe carousel, YouTube videos, "People Also Ask", ads → assembled into SERP HTML

9. **Total latency:** ~220ms (cache miss) or ~30ms (cache hit)


## 7. Database Schema


### Inverted Index (SSTable on Colossus)


Key: term (string, e.g., "sourdough")
Value: PostingList {
  doc_freq: uint32,                    // number of documents containing this term
  postings: [                          // sorted by docID
    {
      doc_id: uint64,                  // globally unique document identifier
      term_freq: uint16,              // occurrences of term in this document
      positions: [uint32],            // word positions for phrase queries
      field_flags: uint8              // bit flags: title|body|url|anchor|h1
    }, ...
  ]
}
Compression: delta-encoded docIDs + variable-byte encoding
Sharding: hash(term) % num_shards (term-based sharding)


### Document Store (Bigtable/Colossus)


Row Key: doc_id (uint64)   `PK`  `SHARD`
Column Families:
  "meta": {
    url: string,
    title: string,
    description: string,
    language: string,
    content_type: string,
    last_crawled: timestamp,
    last_modified: timestamp,
    page_rank: float32,
    canonical_url: string
  }
  "content": {
    raw_html: bytes (compressed),
    rendered_text: string,
    extracted_links: [string],
    structured_data: json (schema.org)
  }
  "signals": {
    mobile_friendly: bool,
    https: bool,
    core_web_vitals: {lcp: float, fid: float, cls: float},
    spam_score: float32
  }


### Query Logs (for autocomplete & analytics)


Table: query_log   `SHARD`
- query_hash: uint64       `PK`
- query_text: string        `IDX`
- frequency: uint64         `IDX`
- last_seen: timestamp
- region_counts: map<string, uint64>  // per-region frequency
- trend_score: float32     // recency-weighted score


### Web Graph (for PageRank)


// Adjacency list stored in Colossus
Key: source_doc_id (uint64)   `PK`
Value: {
  outbound_links: [
    {target_doc_id: uint64, anchor_text: string}, ...
  ],
  inbound_count: uint32
}
// Processing: Pregel/MapReduce iterative computation
// ~100B edges in the web graph


## 8. Cache & CDN Deep Dive


### Multi-Layer Caching


| Layer                   | What's Cached                                         | TTL                                         | Strategy                    | Hit Rate            |
|-------------------------|-------------------------------------------------------|---------------------------------------------|-----------------------------|---------------------|
| Browser Cache           | Static assets (JS, CSS, images), SERP for back button | 1h-1yr (immutable hashed assets)            | Cache-Control headers       | High for assets     |
| CDN / Edge              | Static assets, autocomplete for popular prefixes      | Minutes to hours                            | Anycast, edge POP           | ~90% for static     |
| Result Cache (Memcache) | Full SERP for exact query+locale                      | Minutes (popular) to seconds (fresh topics) | LRU eviction, write-through | ~30% of queries     |
| Posting List Cache      | Hot posting lists (frequently queried terms)          | Until index update                          | In-memory on index servers  | ~80% for head terms |
| Document Cache          | Snippets and metadata for top documents               | Hours                                       | LRU in doc server memory    | ~70%                |


### Cache Invalidation Strategy


**Result cache** uses short TTLs (minutes) since rankings change frequently. For breaking news queries, TTL drops to seconds. **Posting list cache** is invalidated when the index shard receives updates from the Caffeine pipeline. **Document cache** invalidated on recrawl. Google prefers **TTL-based expiration** over explicit invalidation for most layers due to the massive scale (explicit invalidation across 10K+ servers is impractical).


### CDN Architecture


Google operates its own CDN via **Google Edge Network** with 200+ edge PoPs worldwide. Uses **QUIC** (HTTP/3) for reduced connection setup latency (0-RTT). Anycast routing directs users to nearest edge. Search results themselves are NOT cached at CDN (too dynamic/personalized), but all static assets (search page JS/CSS, logo, fonts) are aggressively cached. Autocomplete responses for the top ~10K prefixes may be edge-cached.


## 9. Scaling Considerations


### Horizontal Scaling


- **Index sharding:** 10,000+ shards across dozens of data centers, each shard holding ~40M documents. Every query fans out to ALL shards (scatter-gather). Each shard is replicated 3-5x for redundancy and load distribution.

- **Geographic distribution:** Full index replica in each major region (US, Europe, Asia, etc.) — user queries served from nearest region. Index synchronization via streaming replication.

- **Crawler scaling:** Thousands of Googlebot instances across multiple IPs and data centers. Distributed URL frontier prevents duplicate crawling.


### Load Balancing


- **DNS-level:** Anycast routing to nearest Google PoP

- **GFE layer:** Maglev (Google's network load balancer) distributes connections using consistent hashing — handles 1M+ connections per server

- **Cluster-level:** BorgMaster assigns query workloads across machines based on CPU/memory utilization

- **Shard-level:** Within a shard's replicas, round-robin with health checking and load-aware routing


### Tail Latency Optimization


- **Hedged requests:** Send query to 2 replicas of each shard; use whichever responds first. Reduces p99 by 50%+

- **Tied requests:** Cross-server cancellation — once one replica starts processing, the other cancels

- **Partial results:** If some shards time out (>100ms), return results from responding shards (slight quality degradation, major latency improvement)

- **Canary queries:** Test query on a subset of shards first to detect poison-pill queries that crash servers


## 10. Tradeoffs


> **

> **
✅ Scatter-Gather (All Shards)


Every query goes to ALL shards ensuring no results are missed. Guarantees global best results. Simple conceptually — no need to determine which shards to query.


> **
❌ Scatter-Gather Cost


Fan-out to 10K+ shards per query is extremely expensive in network and compute. Tail latency dominated by slowest shard. Mitigated by hedged requests, partial results, and heavy caching.


> **
✅ Multi-Tier Index (Caffeine)


Real-time tier gives seconds-fresh results for breaking news. Base tier gives comprehensive coverage. Separating tiers allows optimizing each for its use case (freshness vs. completeness).


> **
❌ Multi-Tier Complexity


Must query multiple tiers and merge results. Duplicate handling across tiers. Index compaction from real-time to base tier requires careful orchestration. Consistency challenges during transitions.


> **
✅ BERT Re-Ranking (Quality)


Dramatically improves result relevance by understanding query-document semantics (not just keyword matching). Handles natural language queries, negations, prepositions correctly.


> **
❌ BERT Re-Ranking (Cost)


Cross-encoder BERT is ~1000x more expensive than BM25 per document. Can only re-rank top ~1000 candidates, not the full index. Requires dedicated TPU serving infrastructure.


> **
✅ Google-Owned Infrastructure


Custom hardware (TPUs, Titan chips), custom networking (Jupiter fabric, B4 WAN), custom storage (Colossus) — all optimized for search workloads. No cloud provider dependency.


> **
❌ Massive CapEx


Billions in infrastructure investment. Requires world-class hardware, networking, and SRE teams. Extremely high barrier to entry for competitors.


## 11. Alternative Approaches


Elasticsearch/Solr-Based Search


Open-source alternatives using Lucene. Suitable for enterprise search (millions of documents). Cannot scale to Google's 400B+ pages — lacks scatter-gather at that scale, no custom hardware optimization. Typically used for site search, e-commerce search, log search (ELK stack).


Vector Search / Neural Retrieval


Embed all documents as vectors (using sentence-transformers), retrieve via ANN search (FAISS, ScaNN). Google does this as one retrieval pathway alongside traditional inverted index. Pure vector search loses exact keyword matching capability and has higher latency at scale. Hybrid approach (BM25 + vector) is state-of-the-art.


LLM-Based Search (Perplexity Model)


Generate direct answers via LLM instead of returning links. Google's SGE (Search Generative Experience) moves in this direction. Tradeoffs: higher latency (1-3s for generation), higher compute cost per query, hallucination risk, publisher attribution challenges, ad placement disruption.


Federated/P2P Search (YaCy)


Decentralized search where participants contribute to crawling and indexing. No single point of control or censorship. Challenges: much lower quality (no PageRank equivalent), inconsistent index, higher latency, spam vulnerability. Not viable at consumer scale.


## 12. Additional Information


### Google's Infrastructure Stack


- **Colossus** (distributed file system, successor to GFS) — stores index files, document cache, web graph

- **Bigtable** (wide-column NoSQL) — document metadata, user data

- **Spanner** (globally distributed relational DB) — ads data, user accounts (needs strong consistency)

- **Borg** (cluster management, predecessor to Kubernetes) — schedules all search workloads

- **Maglev** (network load balancer) — distributes incoming queries

- **TPU v4** — custom ASIC for BERT/MUM inference in serving path

- **QUIC/HTTP3** — reduced latency connection protocol (0-RTT resumption)


### Key Numbers


| Queries per day        | ~8.5 billion              |
|------------------------|---------------------------|
| Queries per second     | ~100,000                  |
| Pages indexed          | 400+ billion              |
| Index size             | ~100 PB (compressed)      |
| Average result latency | ~200ms (p50)              |
| Crawl rate             | Billions of pages per day |
| Data centers           | 30+ worldwide             |
| Revenue (search ads)   | ~$175B/year               |


### Protocols & Connections


- **User → GFE:** HTTPS/3 (QUIC) over UDP, 0-RTT resumption, BoringSSL (Google's OpenSSL fork)

- **GFE → Backend:** gRPC over HTTP/2 (Stubby, Google's internal RPC framework)

- **Crawler → Web:** HTTP/1.1 and HTTP/2, respects robots.txt, user-agent: Googlebot/2.1

- **Inter-DC replication:** B4 (Google's software-defined WAN), traffic engineering for index sync

- **Internal storage:** Colossus chunks + Reed-Solomon erasure coding for durability