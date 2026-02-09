# 📖 Design Wikipedia


Collaborative encyclopedia with 60M+ articles in 300+ languages, edit history, version control, anti-vandalism — 16B+ pageviews/month


## 1. Functional Requirements


- **Article Viewing:** Render articles with wikitext markup (tables, infoboxes, citations, categories, inter-wiki links)

- **Editing:** Edit articles with wikitext or visual editor; save edits with edit summaries, diff generation

- **Version History:** Complete revision history for every article; diff between any two revisions; revert to any version

- **Anti-Vandalism:** Automated bots (ClueBot NG), ORES ML scoring, patrolled/flagged revisions, semi-protection

- **Search:** Full-text search across all articles in a language with autocomplete and suggestions

- **Multi-Language:** 300+ language editions, each independent wiki with cross-language links (interwiki via Wikidata)

- **Talk Pages:** Discussion pages for each article, user talk pages for collaboration

- **Media:** Images, audio, video via Wikimedia Commons (100M+ free media files)


## 2. Non-Functional Requirements


- **Scale:** 16B+ pageviews/month, 60M+ articles, 300+ language editions

- **Latency:** Article rendering p50 <200ms (from cache), <1s (cache miss with wikitext parse)

- **Availability:** 99.95%+ (read path); write path can tolerate brief degradation

- **Read:Write ratio:** ~1000:1 (reads vastly dominate)

- **Storage:** All revisions stored forever (never deleted); ~4PB total including media

- **Budget:** Non-profit — runs on ~$150M/year donated funds with ~800 servers total (extremely efficient)


## 3. Flow 1 — Article Viewing (Read Path)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#69c">
</polygon>
</marker>
</defs>


<rect x="20" y="140" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="170" text-anchor="middle" fill="#fff" font-size="13">
Reader
</text>
<text x="75" y="185" text-anchor="middle" fill="#fff" font-size="11">
16B views/mo
</text>


<rect x="180" y="40" width="130" height="60" rx="8" fill="#546e7a" stroke="#90a4ae" stroke-width="2">
</rect>
<text x="245" y="70" text-anchor="middle" fill="#fff" font-size="13">
CDN (Varnish)
</text>
<text x="245" y="85" text-anchor="middle" fill="#fff" font-size="11">
edge cache layer
</text>


<rect x="180" y="140" width="130" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="245" y="170" text-anchor="middle" fill="#fff" font-size="13">
Varnish Frontend
</text>
<text x="245" y="185" text-anchor="middle" fill="#fff" font-size="11">
(local DC cache)
</text>


<rect x="180" y="240" width="130" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="245" y="270" text-anchor="middle" fill="#fff" font-size="13">
Varnish Backend
</text>
<text x="245" y="285" text-anchor="middle" fill="#fff" font-size="11">
(2nd layer)
</text>


<rect x="380" y="140" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="445" y="170" text-anchor="middle" fill="#fff" font-size="13">
MediaWiki
</text>
<text x="445" y="185" text-anchor="middle" fill="#fff" font-size="11">
(PHP app servers)
</text>


<rect x="570" y="40" width="130" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="635" y="70" text-anchor="middle" fill="#fff" font-size="13">
Memcached
</text>
<text x="635" y="85" text-anchor="middle" fill="#fff" font-size="11">
(parsed objects)
</text>


<rect x="570" y="140" width="130" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="635" y="170" text-anchor="middle" fill="#fff" font-size="13">
MariaDB
</text>
<text x="635" y="185" text-anchor="middle" fill="#fff" font-size="11">
(article metadata)
</text>


<rect x="570" y="240" width="130" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="635" y="270" text-anchor="middle" fill="#fff" font-size="13">
Blob Store
</text>
<text x="635" y="285" text-anchor="middle" fill="#fff" font-size="11">
(revision text)
</text>


<rect x="760" y="140" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="830" y="170" text-anchor="middle" fill="#fff" font-size="13">
Parsoid / Parser
</text>
<text x="830" y="185" text-anchor="middle" fill="#fff" font-size="11">
(wikitext → HTML)
</text>


<line x1="130" y1="155" x2="178" y2="70" stroke="#69c" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="130" y1="170" x2="178" y2="170" stroke="#69c" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="245" y1="102" x2="245" y2="138" stroke="#69c" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="245" y1="202" x2="245" y2="238" stroke="#69c" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="310" y1="270" x2="378" y2="185" stroke="#69c" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="510" y1="155" x2="568" y2="70" stroke="#69c" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="510" y1="170" x2="568" y2="170" stroke="#69c" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="510" y1="185" x2="568" y2="265" stroke="#69c" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="700" y1="170" x2="758" y2="170" stroke="#69c" stroke-width="2" marker-end="url(#a1)">
</line>


</svg>

</details>


### Flow Steps


1. **Reader requests** `https://en.wikipedia.org/wiki/Quantum_computing`

2. **CDN (Varnish edge)**: Cache lookup by URL + language + device variant. Cache HIT → return immediately (serves ~85-90% of requests). Average cache hit ratio at edge: ~90%

3. **Cache MISS → Varnish Frontend** (local data center Varnish layer): secondary cache lookup. Additional ~5% hit rate

4. **Cache MISS → Varnish Backend** (third layer): final Varnish cache. ~3% additional hit rate

5. **Origin MISS → MediaWiki (PHP)**: Only ~2-3% of requests reach the application servers

6. **MediaWiki** checks Memcached for parsed HTML of the article. If cached → return. If not → fetch latest revision metadata from MariaDB

7. **Fetch revision text** from blob store (external storage for wikitext content)

8. **Parsoid/Parser** converts wikitext to HTML: resolves templates ({{cite web|...}}), transclusions, categories, generates table of contents, infoboxes, reference lists

9. **Parsed HTML cached** in Memcached (keyed by page_id + revision_id) and in Varnish (keyed by URL)


### Example


**Request:** `GET /wiki/Quantum_computing HTTP/2`


**Varnish edge:** Cache HIT (popular article, cached 5 minutes ago) → return 200 with cached HTML in 15ms


**Cache MISS scenario:** Article just edited (cache purged) → request falls through all 3 Varnish layers → hits MediaWiki


**MediaWiki processing:** MariaDB lookup: page_id=25336, latest_rev=1234567. Memcached check for parsed HTML of rev 1234567 → MISS. Fetch wikitext from blob store (48KB of wikitext). Parse: resolve 150 templates, 200 citations, 30 images → generates 180KB HTML. Total parse time: ~800ms. Cached in Memcached and all Varnish layers. Next reader gets cache hit in 15ms.


**Mobile variant:** Separate Varnish cache entry for mobile (m.wikipedia.org uses different HTML renderer via MobileFrontend extension)


### Deep Dives


Multi-Layer Varnish Caching


Wikipedia's caching architecture is the key to serving 16B+ pageviews/month with only ~800 servers. Three Varnish layers: (1) **Edge/CDN**: geographically distributed, serves ~90% of reads. (2) **Frontend**: per-DC Varnish, handles regional cache misses. (3) **Backend**: final cache before origin. Total cache hit ratio: ~97-98%. Cache invalidation: on article edit, a **PURGE** request is sent to all Varnish layers for that URL. Uses **HTCPurge** multicast protocol to efficiently invalidate across all cache nodes. Cache key: URL + Vary headers (Accept-Language, Cookie for logged-in users).


Wikitext Parsing (Parsoid)


**Parsoid** is Wikipedia's parser that converts wikitext (a custom markup language) to HTML5+RDFa and vice-versa. Wikitext is complex: templates (transclusions from other pages), parser functions (#if, #switch), Lua modules (Scribunto), references (ref/reflist), tables, categories, interwiki links. Parsing a complex article can take 500ms-2s. This is why caching is critical — parsing is expensive. Parsoid outputs semantic HTML that preserves the wikitext's meaning, enabling round-tripping (HTML → wikitext → HTML). Used by the Visual Editor for WYSIWYG editing.


Template Transclusion


Wikitext templates like `{{Infobox_country|name=Japan|...}}` are resolved by substituting content from the template page. Templates can be recursive (templates calling templates). A single article can transclude 100+ templates. Template changes affect ALL articles that use them — requiring cache purge of all dependent articles. Template dependency tracking via the `templatelinks` table in MariaDB. High-use templates (e.g., {{citation needed}}) are referenced by millions of articles — template edits trigger massive cache purge cascades.


Content Negotiation


Wikipedia serves different content based on: **Language**: each language wiki (en, de, ja, etc.) is a separate MediaWiki installation with its own database. **Device**: desktop vs mobile (different HTML, separate cache entries). **User state**: logged-in users get personalized elements (watchlist links, edit buttons with user-specific tools) — these are NOT cached in Varnish (served directly from MediaWiki). Anonymous users: fully cached. This is why ~97% cache hit rate is achievable — most readers are anonymous.


## 4. Flow 2 — Article Editing & Version Control


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 380" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#9cf">
</polygon>
</marker>
</defs>


<rect x="20" y="150" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="180" text-anchor="middle" fill="#fff" font-size="13">
Editor
</text>
<text x="75" y="195" text-anchor="middle" fill="#fff" font-size="11">
(human/bot)
</text>


<rect x="180" y="150" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="245" y="180" text-anchor="middle" fill="#fff" font-size="13">
MediaWiki
</text>
<text x="245" y="195" text-anchor="middle" fill="#fff" font-size="11">
Edit Handler
</text>


<rect x="370" y="50" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="435" y="80" text-anchor="middle" fill="#fff" font-size="13">
Edit Conflict
</text>
<text x="435" y="95" text-anchor="middle" fill="#fff" font-size="11">
Detector
</text>


<rect x="370" y="150" width="130" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="435" y="180" text-anchor="middle" fill="#fff" font-size="13">
MariaDB
</text>
<text x="435" y="195" text-anchor="middle" fill="#fff" font-size="11">
(revision table)
</text>


<rect x="370" y="250" width="130" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="435" y="280" text-anchor="middle" fill="#fff" font-size="13">
Blob Store
</text>
<text x="435" y="295" text-anchor="middle" fill="#fff" font-size="11">
(wikitext content)
</text>


<rect x="560" y="50" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="625" y="80" text-anchor="middle" fill="#fff" font-size="13">
AbuseFilter
</text>
<text x="625" y="95" text-anchor="middle" fill="#fff" font-size="11">
(rule engine)
</text>


<rect x="560" y="150" width="130" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="625" y="180" text-anchor="middle" fill="#fff" font-size="13">
Job Queue
</text>
<text x="625" y="195" text-anchor="middle" fill="#fff" font-size="11">
(async tasks)
</text>


<rect x="560" y="250" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="625" y="280" text-anchor="middle" fill="#fff" font-size="13">
ORES / ML
</text>
<text x="625" y="295" text-anchor="middle" fill="#fff" font-size="11">
(vandalism score)
</text>


<rect x="760" y="150" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="830" y="180" text-anchor="middle" fill="#fff" font-size="13">
Cache Purge
</text>
<text x="830" y="195" text-anchor="middle" fill="#fff" font-size="11">
(HTCP multicast)
</text>


<line x1="130" y1="180" x2="178" y2="180" stroke="#9cf" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="310" y1="165" x2="368" y2="85" stroke="#9cf" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="310" y1="180" x2="368" y2="180" stroke="#9cf" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="310" y1="195" x2="368" y2="275" stroke="#9cf" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="500" y1="80" x2="558" y2="80" stroke="#9cf" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="500" y1="180" x2="558" y2="180" stroke="#9cf" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="500" y1="280" x2="558" y2="280" stroke="#9cf" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="690" y1="180" x2="758" y2="180" stroke="#9cf" stroke-width="2" marker-end="url(#a2)">
</line>


</svg>

</details>


### Flow Steps


1. **Editor** loads edit form → receives current wikitext + base revision ID (e.g., rev 1234567)

2. **Editor submits** modified wikitext + edit summary + base_rev_id via POST to MediaWiki action API

3. **AbuseFilter** checks edit against configured rules (regex patterns for common vandalism, new user restrictions, link spam detection)

4. **Edit Conflict Detector:** Compare base_rev_id with current latest_rev. If they differ → edit conflict (another editor saved a revision in between). User must manually merge changes.

5. **If no conflict:** Insert new row in `revision` table (rev_id, page_id, user_id, timestamp, comment, SHA-1 of content). Store wikitext content in blob store (deduplicated by content hash).

6. **Update `page` table:** Set `page_latest` to new rev_id

7. **Job Queue (async):** Parse new wikitext → update Memcached with parsed HTML; update search index (Elasticsearch/CirrusSearch); update links tables (pagelinks, categorylinks, templatelinks); update recent changes feed

8. **Cache Purge:** HTCP multicast to all Varnish nodes to purge the article URL

9. **ORES ML scoring:** Score the edit for quality/vandalism probability. If high vandalism score → flagged for review in patrol queue


### Example


**Scenario:** User fixes a typo in the "History" section of the Quantum_computing article


**Edit:** Changed "computaiton" → "computation" (1 character edit)


**AbuseFilter:** No rules triggered (not a new account, not adding external links, not blanking content)


**Edit conflict check:** base_rev=1234567, current latest=1234567 → no conflict → proceed


**Revision created:** rev_id=1234568, parent_rev=1234567, size_diff=+0 (same length), sha1=abc...


**Content dedup:** New wikitext stored in blob store. Old revision's text preserved (complete text, not delta)


**ORES score:** goodfaith=0.98, damaging=0.02 → classified as good edit, auto-patrolled


**Diff generation:** Line-based diff computed between rev 1234567 and 1234568 → stored for display on revision history page


### Deep Dives


Edit Conflict Resolution


Wikipedia uses **optimistic concurrency control**. When an editor opens the edit form, they receive the current revision ID. On submit, if another edit occurred in between, the edit is rejected with a conflict. The editor sees a three-way merge view: their version, the other editor's version, and the common ancestor. They must manually merge. For high-traffic articles, edit conflicts are common. The **TwoColConflict** extension provides a better UX with side-by-side diff. Bots handle this by fetching the latest revision and re-applying their change programmatically (retry loop).


ORES (Objective Revision Evaluation Service)


ML service that scores each edit in real-time. Models: **damaging** (probability edit is vandalism), **goodfaith** (probability editor intended well), **articlequality** (stub/start/C/B/GA/FA), **draftquality** (for new article drafts). Features: edit size, user tenure, time of day, regex pattern matches, word changes, reference additions/removals. Used by: patrollers (sort edits by damaging probability), ClueBot NG (auto-revert highly damaging edits), and new page patrol. Model: gradient-boosted trees trained on labeled edit datasets.


## 5. Flow 3 — Search (CirrusSearch)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 300" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#cdf">
</polygon>
</marker>
</defs>


<rect x="20" y="120" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="150" text-anchor="middle" fill="#fff" font-size="13">
User
</text>
<text x="75" y="165" text-anchor="middle" fill="#fff" font-size="11">
search query
</text>


<rect x="180" y="120" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="245" y="150" text-anchor="middle" fill="#fff" font-size="13">
MediaWiki
</text>
<text x="245" y="165" text-anchor="middle" fill="#fff" font-size="11">
+ CirrusSearch
</text>


<rect x="370" y="40" width="140" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="440" y="70" text-anchor="middle" fill="#fff" font-size="13">
Elasticsearch
</text>
<text x="440" y="85" text-anchor="middle" fill="#fff" font-size="11">
Cluster
</text>


<rect x="370" y="120" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="440" y="150" text-anchor="middle" fill="#fff" font-size="13">
Completion
</text>
<text x="440" y="165" text-anchor="middle" fill="#fff" font-size="11">
Suggester
</text>


<rect x="370" y="200" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="440" y="230" text-anchor="middle" fill="#fff" font-size="13">
Rescore (MLR)
</text>
<text x="440" y="245" text-anchor="middle" fill="#fff" font-size="11">
learning-to-rank
</text>


<rect x="580" y="120" width="120" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="640" y="150" text-anchor="middle" fill="#fff" font-size="13">
Search Results
</text>


<line x1="130" y1="150" x2="178" y2="150" stroke="#cdf" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="310" y1="135" x2="368" y2="75" stroke="#cdf" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="310" y1="150" x2="368" y2="150" stroke="#cdf" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="310" y1="165" x2="368" y2="225" stroke="#cdf" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="510" y1="75" x2="578" y2="140" stroke="#cdf" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="510" y1="150" x2="578" y2="150" stroke="#cdf" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="510" y1="225" x2="578" y2="165" stroke="#cdf" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


### Flow Steps


1. **User types in search bar** → CirrusSearch extension sends prefix completion query to Elasticsearch

2. **Completion Suggester:** Returns title suggestions using FST (Finite State Transducer) — sub-millisecond prefix matching across 60M+ article titles

3. **Full search:** Elasticsearch query with BM25 scoring + field boosting (title > headings > content). Supports advanced operators: intitle:, incategory:, hastemplate:, insource:

4. **MLR (Machine Learning Ranking):** Rescore top results using learning-to-rank model (features: page popularity, incoming link count, text relevance, recency)

5. **Snippet generation:** Highlight matching terms in article excerpt

6. **Did-you-mean:** Spell correction via Elasticsearch suggest API (phrase suggester with bigram/trigram analysis)


### Example


**Query:** "quantim computing" (typo)


**Autocomplete (as user types "quant"):** ["Quantum computing", "Quantum mechanics", "Quantitative easing", "Quantum entanglement"]


**Full search:** Did-you-mean: "quantum computing" → redirects. BM25 results + MLR rescore: Quantum_computing (score: 95), Quantum_supremacy (score: 78), Qubit (score: 72)...


**MLR features for top result:** incoming_links=15000, pageviews_30d=2M, text_match=0.95, title_match=0.99


## 6. Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 500" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#69c">
</polygon>
</marker>
</defs>


<rect x="10" y="220" width="100" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="60" y="250" text-anchor="middle" fill="#fff" font-size="12">
Readers
</text>
<text x="60" y="265" text-anchor="middle" fill="#fff" font-size="10">
16B views/mo
</text>


<rect x="150" y="130" width="110" height="50" rx="8" fill="#546e7a" stroke="#90a4ae" stroke-width="2">
</rect>
<text x="205" y="160" text-anchor="middle" fill="#fff" font-size="12">
Varnish CDN
</text>


<rect x="150" y="200" width="110" height="50" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="205" y="225" text-anchor="middle" fill="#fff" font-size="12">
Varnish FE
</text>
<text x="205" y="240" text-anchor="middle" fill="#fff" font-size="10">
(per-DC)
</text>


<rect x="150" y="270" width="110" height="50" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="205" y="295" text-anchor="middle" fill="#fff" font-size="12">
Varnish BE
</text>


<rect x="310" y="200" width="130" height="50" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="375" y="225" text-anchor="middle" fill="#fff" font-size="12">
MediaWiki (PHP)
</text>
<text x="375" y="240" text-anchor="middle" fill="#fff" font-size="10">
~300 app servers
</text>


<rect x="310" y="280" width="130" height="50" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="375" y="305" text-anchor="middle" fill="#fff" font-size="12">
Parsoid (Node.js)
</text>


<rect x="310" y="360" width="130" height="50" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="375" y="385" text-anchor="middle" fill="#fff" font-size="12">
Job Queue
</text>
<text x="375" y="400" text-anchor="middle" fill="#fff" font-size="10">
(Redis-backed)
</text>


<rect x="500" y="80" width="120" height="45" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="560" y="108" text-anchor="middle" fill="#fff" font-size="11">
Memcached
</text>


<rect x="500" y="145" width="120" height="45" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="560" y="173" text-anchor="middle" fill="#fff" font-size="11">
MariaDB Primary
</text>


<rect x="500" y="210" width="120" height="45" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="560" y="238" text-anchor="middle" fill="#fff" font-size="11">
MariaDB Replicas
</text>


<rect x="500" y="275" width="120" height="45" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="560" y="303" text-anchor="middle" fill="#fff" font-size="11">
Blob Store
</text>


<rect x="500" y="340" width="120" height="45" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="560" y="368" text-anchor="middle" fill="#fff" font-size="11">
Elasticsearch
</text>


<rect x="680" y="145" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="740" y="173" text-anchor="middle" fill="#fff" font-size="11">
ORES (ML)
</text>


<rect x="680" y="210" width="120" height="45" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="740" y="238" text-anchor="middle" fill="#fff" font-size="11">
Wikidata (WDQS)
</text>


<rect x="680" y="275" width="120" height="45" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="740" y="303" text-anchor="middle" fill="#fff" font-size="11">
Commons (media)
</text>


<line x1="110" y1="240" x2="148" y2="155" stroke="#69c" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="110" y1="250" x2="148" y2="225" stroke="#69c" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="205" y1="252" x2="205" y2="268" stroke="#69c" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="260" y1="295" x2="308" y2="225" stroke="#69c" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="440" y1="215" x2="498" y2="105" stroke="#69c" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="440" y1="225" x2="498" y2="165" stroke="#69c" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="440" y1="230" x2="498" y2="295" stroke="#69c" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="440" y1="225" x2="498" y2="230" stroke="#69c" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="440" y1="310" x2="498" y2="360" stroke="#69c" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="620" y1="165" x2="678" y2="165" stroke="#69c" stroke-width="2" marker-end="url(#a4)">
</line>


</svg>

</details>


## 7. Database Schema


### MariaDB — Core Tables (per-wiki database, e.g., enwiki)


Table: page
- page_id: int unsigned         `PK`
- page_namespace: int           `IDX` (0=article, 1=talk, 2=user, etc.)
- page_title: varbinary(255)    `IDX`
- page_is_redirect: tinyint
- page_is_new: tinyint
- page_len: int unsigned       // byte length of latest wikitext
- page_latest: int unsigned     `FK` (latest revision ID)
- page_content_model: varbinary(32)  // 'wikitext', 'json', etc.
- page_touched: varbinary(14)  // yyyymmddhhmmss (cache invalidation timestamp)

Table: revision   `SHARD`
- rev_id: int unsigned          `PK`
- rev_page: int unsigned        `FK`  `IDX`
- rev_actor: bigint unsigned    `FK`
- rev_timestamp: varbinary(14)  `IDX`
- rev_minor_edit: tinyint
- rev_deleted: tinyint         // suppression flags
- rev_len: int unsigned        // content length
- rev_parent_id: int unsigned   `FK` (previous revision)
- rev_sha1: varbinary(32)     // SHA-1 of content (dedup key)
- rev_comment_id: bigint       `FK`

Table: text (blob store — external storage)
- old_id: int unsigned          `PK`
- old_text: mediumblob         // raw wikitext content (gzip compressed)
- old_flags: tinyblob          // 'utf-8,gzip,external' — storage flags
// In practice, large wikis use external blob stores (not MySQL TEXT column)


### Link Tables


Table: pagelinks (which pages link to which)
- pl_from: int unsigned         `FK`  `IDX`
- pl_namespace: int
- pl_title: varbinary(255)      `IDX`
// Used for: "What links here", backlink updates, link graph analysis

Table: categorylinks
- cl_from: int unsigned         `FK`
- cl_to: varbinary(255)         `IDX` (category name)
- cl_sortkey: varbinary(230)
- cl_timestamp: timestamp

Table: templatelinks
- tl_from: int unsigned         `FK`
- tl_namespace: int
- tl_title: varbinary(255)      `IDX`
// Used for: cache invalidation when template is edited


### Recent Changes & Watchlist


Table: recentchanges
- rc_id: int unsigned           `PK`
- rc_timestamp: varbinary(14)   `IDX`
- rc_actor: bigint unsigned
- rc_namespace: int
- rc_title: varbinary(255)
- rc_type: tinyint             // 0=edit, 1=new, 3=log, 5=categorize
- rc_cur_id: int unsigned       `FK`
- rc_this_oldid: int unsigned   `FK`
- rc_last_oldid: int unsigned   `FK`
- rc_patrolled: tinyint        // 0=unpatrolled, 1=patrolled

Table: watchlist
- wl_user: int unsigned         `FK`
- wl_namespace: int
- wl_title: varbinary(255)
- wl_notificationtimestamp: varbinary(14)
// Composite PK: (wl_user, wl_namespace, wl_title)


## 8. Cache & CDN Deep Dive


### Wikipedia's Caching Philosophy


Wikipedia's entire architecture is optimized around caching because: (1) 1000:1 read:write ratio, (2) non-profit budget (~800 servers for 16B pageviews/month), (3) most content is stable (articles change infrequently relative to reads).


| Layer               | Technology                       | Hit Rate              | Purpose                              | TTL / Invalidation                |
|---------------------|----------------------------------|-----------------------|--------------------------------------|-----------------------------------|
| Edge CDN            | Varnish (custom, ~60 servers)    | ~90%                  | Full HTML pages for anonymous users  | 24h TTL, PURGE on edit            |
| Varnish Frontend    | Varnish (per-DC)                 | ~5%                   | Second-layer page cache              | Same as edge                      |
| Varnish Backend     | Varnish                          | ~3%                   | Third-layer, request coalescing      | Same as edge                      |
| Memcached           | Memcached (~100 servers, ~200GB) | ~95% (of origin hits) | Parsed HTML, metadata, user sessions | Invalidated on edit (version key) |
| MariaDB query cache | MariaDB built-in                 | Varies                | Repeated queries                     | Invalidated on table write        |


### Cache Invalidation: HTCP Purge


When an article is edited, MediaWiki sends a **PURGE** to all Varnish instances. Uses **HTCP (Hyper Text Caching Protocol)** multicast: a single multicast packet invalidates the URL across all Varnish servers simultaneously. This ensures readers see updated content within seconds of an edit. For high-edit-rate articles (e.g., ongoing events), the cache is constantly being purged and refilled — Varnish's **request coalescing** (grace mode) prevents thundering herd by serving stale content to concurrent requesters while one request refills the cache.


### Purge Cascade for Templates


Editing a template (e.g., {{citation needed}}) requires purging ALL articles that transclude it. For popular templates, this can mean purging millions of URLs. This is done via the **Job Queue** (Redis-backed): a "htmlCacheUpdate" job is created for each affected page, processed asynchronously. A template used by 5M articles might take hours to fully re-cache all dependent pages.


## 9. Scaling Considerations


### Infrastructure Efficiency


Wikipedia serves 16B+ pageviews/month with only ~800 servers (compared to comparable sites needing 10,000+). Key factors:


- **97-98% cache hit rate:** Only 2-3% of requests reach origin servers

- **Read-heavy workload:** ~300 edits/minute vs ~300,000 page views/minute

- **No user personalization for anonymous readers:** Same HTML served to all anonymous users (cache-friendly)

- **MediaWiki is PHP:** Simple, shared-nothing architecture — each request is independent


### Data Center Architecture


- **Primary DC:** eqiad (Ashburn, Virginia) — handles all writes (single-primary MariaDB)

- **Secondary DC:** codfw (Dallas, Texas) — warm standby, handles reads via MariaDB replicas

- **Edge PoPs:** esams (Amsterdam), ulsfo (San Francisco), eqsin (Singapore), drmrs (Marseille) — Varnish edge caches only, no app servers

- **DC switchover:** Can promote codfw to primary within minutes for disaster recovery


### MariaDB Scaling


- Each language wiki has its own database (enwiki_p, dewiki_p, etc.)

- Large wikis (en, de, fr) get dedicated MariaDB clusters with multiple read replicas

- Small wikis (300+ languages) share database servers

- Single-primary for writes (no multi-master) — edit conflicts handled at application level

- Read replicas serve all read queries via MediaWiki's `LoadBalancer` (lag-aware routing)


## 10. Tradeoffs


> **

> **
✅ Multi-Layer Varnish (Cost Efficiency)


97% cache hit rate means only ~800 servers needed for 16B pageviews/month. Incredibly cost-efficient for a non-profit. Varnish is open-source, no CDN vendor dependency.


> **
❌ Cache Purge Complexity


Template edits can cascade to millions of page purges. HTCP multicast is fragile at scale. Stale content can persist for seconds during purge propagation. Logged-in users bypass cache entirely (higher origin load).


> **
✅ Full Revision History (Never Delete)


Every edit preserved forever enables: accountability, vandalism recovery, scholarly research, legal compliance, transparency. Wikipedia's core philosophy: all knowledge should be traceable.


> **
❌ Storage Growth


Storing every revision's complete wikitext (not deltas) is storage-intensive. English Wikipedia alone: ~20TB of revision text. Content deduplication by SHA-1 helps (identical content stored once). But still, storage grows linearly with edit count.


> **
✅ Single-Primary DB (Simplicity)


No multi-master complexity, no write conflicts at DB level. Edit conflicts handled cleanly at application level (optimistic locking). Simple replication topology. Easy to reason about consistency.


> **
❌ Write Bottleneck


All writes go to single primary DC. If primary DC goes down, writes are unavailable until failover (~minutes). Write throughput limited by single MariaDB primary. Sufficient for Wikipedia's ~300 edits/minute but wouldn't scale for higher write volumes.


## 11. Alternative Approaches


CRDTs for Collaborative Editing


Instead of optimistic locking with edit conflicts, use CRDTs (like Google Docs) for real-time collaborative editing. Wikipedia considered this but rejected it: wikitext is not well-suited for character-level CRDTs, editorial review process requires discrete edits (diffs), and the community values the "edit → review → merge" workflow over real-time collaboration.


Delta Storage for Revisions


Store only diffs between revisions instead of full text. Saves significant storage (typical edit changes <1% of article). Used by some wikis (git uses delta compression). Wikipedia chose full-text storage for simplicity: any revision can be retrieved in O(1) without reconstructing from deltas. Blob store uses content-addressing (SHA-1) for deduplication of identical content.


Cloud CDN (CloudFlare/Fastly)


Instead of self-hosted Varnish, use a commercial CDN. Would reduce operational burden. Wikipedia chose self-hosted for: full control over cache behavior (custom VCL), no vendor lock-in, privacy (no third-party seeing all traffic), and cost (at their scale, self-hosted is cheaper than CDN pricing).


Static Site Generation


Pre-render all articles to static HTML (like a static site generator). Would eliminate Varnish/MediaWiki for reads entirely. Challenges: 60M articles × 300 languages = massive generation pipeline. Template changes would require re-rendering millions of pages. Real-time edits would have minutes of delay. Wikipedia needs sub-minute edit visibility.


## 12. Additional Information


### Tech Stack


- **MediaWiki:** PHP application (custom framework, not Laravel/Symfony). Powers all Wikimedia sites

- **Parsoid:** Node.js service for wikitext↔HTML conversion

- **MariaDB:** Primary database (forked from MySQL)

- **Memcached:** Object caching layer (~200GB total)

- **Varnish:** HTTP caching (self-hosted CDN)

- **Elasticsearch:** Full-text search (CirrusSearch extension)

- **Redis:** Job queue backend, session storage

- **Puppet:** Configuration management for all servers

- **Kubernetes:** Newer services (ORES, Parsoid) run on K8s


### Key Numbers


| Monthly pageviews              | 16B+     |
|--------------------------------|----------|
| Total articles (all languages) | 60M+     |
| English Wikipedia articles     | 6.8M+    |
| Language editions              | 300+     |
| Edits per minute               | ~300     |
| Active editors per month       | ~280,000 |
| Total servers                  | ~800     |
| Annual budget                  | ~$150M   |
| Cache hit rate                 | 97-98%   |


### Anti-Vandalism Stack


- **ClueBot NG:** ML bot that auto-reverts obvious vandalism within seconds (~40% of vandalism caught)

- **ORES:** ML scoring service (damaging probability) used by human patrollers

- **AbuseFilter:** Rule-based filter engine (regex/conditions) configured by admins

- **Page protection:** Semi-protect (only autoconfirmed users can edit), full-protect (only admins)

- **Flagged Revisions:** On some wikis (dewiki), edits must be reviewed before being shown to anonymous readers