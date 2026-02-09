# 🔍 Splunk — System Design


Log Analytics, Search Processing Language, Machine Data Indexing & Real-Time Alerting Platform


## Functional Requirements


  1. **Data Ingestion** — Accept structured/unstructured machine data from syslog, files, HTTP Event Collector (HEC), forwarders, cloud APIs

  2. **Indexing** — Parse, timestamp-extract, and store events in time-series indexed buckets for sub-second search

  3. **Search (SPL)** — Execute Search Processing Language queries: filtering, transforming, statistical, join, subsearch commands

  4. **Dashboards & Visualization** — Real-time and scheduled dashboards with charts, tables, single-value panels, maps

  5. **Alerting** — Threshold-based, scheduled, and real-time alerts with webhook/email/PagerDuty actions

  6. **Knowledge Objects** — Field extractions, lookups, event types, tags, data models, saved searches

  7. **RBAC & Multi-Tenancy** — Role-based access to indexes, apps, and search capabilities


## Non-Functional Requirements


  - **Throughput** — 100s of TB/day ingestion across distributed indexer clusters

  - **Search Latency** — <10s for recent data, minutes for terabytes of historical data (MapReduce-style parallel search)

  - **Durability** — Replication factor 3 across indexer peers; no data loss during node failures

  - **Scalability** — Horizontal scaling: add indexers for throughput, add search heads for concurrent users

  - **Availability** — 99.99% uptime with search head clustering and indexer cluster auto-failover

  - **Retention** — Configurable per-index (hot → warm → cold → frozen → thawed lifecycle)

  - **Security** — TLS in transit, AES-256 at rest, audit logging, SAML/LDAP SSO integration


## Flow 1: Data Ingestion & Indexing Pipeline


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 420" xmlns="http://www.w3.org/2000/svg">

  
<defs>

    
<marker id="ah1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">

      
<polygon points="0 0, 10 3.5, 0 7" fill="#65a637">
</polygon>

    
</marker>

  
</defs>

  

  
<rect x="10" y="30" width="130" height="50" rx="8" fill="#2e7d32">
</rect>

  
<text x="75" y="60" text-anchor="middle" fill="white" font-size="12">
Servers / Apps
</text>

  
<rect x="10" y="100" width="130" height="50" rx="8" fill="#2e7d32">
</rect>

  
<text x="75" y="130" text-anchor="middle" fill="white" font-size="12">
Network Devices
</text>

  
<rect x="10" y="170" width="130" height="50" rx="8" fill="#2e7d32">
</rect>

  
<text x="75" y="200" text-anchor="middle" fill="white" font-size="12">
Cloud (AWS/GCP)
</text>

  
<rect x="10" y="240" width="130" height="50" rx="8" fill="#2e7d32">
</rect>

  
<text x="75" y="270" text-anchor="middle" fill="white" font-size="12">
Custom Apps (HEC)
</text>


  

  
<rect x="200" y="80" width="140" height="60" rx="8" fill="#f58220">
</rect>

  
<text x="270" y="105" text-anchor="middle" fill="white" font-size="11">
Universal Forwarder
</text>

  
<text x="270" y="122" text-anchor="middle" fill="white" font-size="10">
(UF Agent)
</text>


  

  
<rect x="200" y="180" width="140" height="60" rx="8" fill="#e65100">
</rect>

  
<text x="270" y="205" text-anchor="middle" fill="white" font-size="11">
Heavy Forwarder
</text>

  
<text x="270" y="222" text-anchor="middle" fill="white" font-size="10">
(Parsing/Filtering)
</text>


  

  
<rect x="420" y="50" width="160" height="70" rx="8" fill="#1565c0">
</rect>

  
<text x="500" y="80" text-anchor="middle" fill="white" font-size="12">
Indexer Peer 1
</text>

  
<text x="500" y="98" text-anchor="middle" fill="white" font-size="10">
Parse → Index → Store
</text>

  
<rect x="420" y="140" width="160" height="70" rx="8" fill="#1565c0">
</rect>

  
<text x="500" y="170" text-anchor="middle" fill="white" font-size="12">
Indexer Peer 2
</text>

  
<text x="500" y="188" text-anchor="middle" fill="white" font-size="10">
Parse → Index → Store
</text>

  
<rect x="420" y="230" width="160" height="70" rx="8" fill="#1565c0">
</rect>

  
<text x="500" y="260" text-anchor="middle" fill="white" font-size="12">
Indexer Peer N
</text>

  
<text x="500" y="278" text-anchor="middle" fill="white" font-size="10">
Parse → Index → Store
</text>


  

  
<rect x="430" y="330" width="140" height="50" rx="8" fill="#7b1fa2">
</rect>

  
<text x="500" y="352" text-anchor="middle" fill="white" font-size="11">
Cluster Manager
</text>

  
<text x="500" y="368" text-anchor="middle" fill="white" font-size="10">
(Replication Mgmt)
</text>


  

  
<rect x="660" y="50" width="140" height="55" rx="8" fill="#c62828">
</rect>

  
<text x="730" y="75" text-anchor="middle" fill="white" font-size="11">
Hot Buckets
</text>

  
<text x="730" y="92" text-anchor="middle" fill="white" font-size="10">
(Active Write)
</text>

  
<rect x="660" y="120" width="140" height="55" rx="8" fill="#e65100">
</rect>

  
<text x="730" y="145" text-anchor="middle" fill="white" font-size="11">
Warm Buckets
</text>

  
<text x="730" y="162" text-anchor="middle" fill="white" font-size="10">
(Read-only, local)
</text>

  
<rect x="660" y="190" width="140" height="55" rx="8" fill="#4a148c">
</rect>

  
<text x="730" y="215" text-anchor="middle" fill="white" font-size="11">
Cold Buckets
</text>

  
<text x="730" y="232" text-anchor="middle" fill="white" font-size="10">
(Separate Path)
</text>

  
<rect x="660" y="260" width="140" height="55" rx="8" fill="#37474f">
</rect>

  
<text x="730" y="285" text-anchor="middle" fill="white" font-size="11">
Frozen → S3/HDFS
</text>

  
<text x="730" y="302" text-anchor="middle" fill="white" font-size="10">
(SmartStore Archive)
</text>


  

  
<rect x="860" y="130" width="150" height="60" rx="8" fill="#006064">
</rect>

  
<text x="935" y="155" text-anchor="middle" fill="white" font-size="11">
TSIDX Files
</text>

  
<text x="935" y="172" text-anchor="middle" fill="white" font-size="10">
(Time-Series Index)
</text>


  

  
<line x1="140" y1="55" x2="198" y2="100" stroke="#65a637" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="140" y1="125" x2="198" y2="112" stroke="#65a637" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="140" y1="195" x2="198" y2="205" stroke="#65a637" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="140" y1="265" x2="198" y2="215" stroke="#65a637" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="340" y1="110" x2="418" y2="90" stroke="#65a637" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="340" y1="110" x2="418" y2="175" stroke="#65a637" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="340" y1="210" x2="418" y2="265" stroke="#65a637" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="580" y1="85" x2="658" y2="77" stroke="#65a637" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="580" y1="175" x2="658" y2="147" stroke="#65a637" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="580" y1="265" x2="658" y2="217" stroke="#65a637" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="800" y1="160" x2="858" y2="160" stroke="#65a637" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="500" y1="300" x2="500" y2="328" stroke="#f58220" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah1)">
</line>


</svg>

</details>


### Ingestion Steps


  1. **Data Collection** — Universal Forwarders (UFs) run as lightweight agents on source machines, tailing log files and forwarding raw data over TCP/TLS (port 9997) to indexers. Heavy Forwarders (HFs) add parsing/filtering/routing before forwarding.

  2. **HTTP Event Collector (HEC)** — RESTful endpoint (port 8088) for application telemetry. Token-based auth. Accepts JSON/raw payloads. `POST /services/collector/event` with `{"event": {...}, "sourcetype": "json", "index": "main"}`.

  3. **Parsing Pipeline** — Indexer receives data → *character encoding* → *line breaking* (LINE_BREAKER regex) → *timestamp extraction* (TIME_FORMAT, MAX_TIMESTAMP_LOOKAHEAD) → *event assembly* → assign metadata (host, source, sourcetype, index).

  4. **Indexing** — Events are written to Hot Buckets (on local SSD). Each bucket is a directory containing: `rawdata/journal.gz` (compressed events), `*.tsidx` (inverted index), `.bucketmanifest`. TSIDX maps terms → event offsets for fast search.

  5. **Replication** — Cluster Manager coordinates replication factor (RF) and search factor (SF). RF=3 means 3 copies of rawdata across peers. SF=2 means 2 searchable copies with TSIDX.

  6. **Bucket Lifecycle** — Hot (active writes) → Warm (rolled, read-only, local) → Cold (older, separate mount) → Frozen (archived to S3/HDFS via SmartStore or deleted).


> **
  **Example — Application logs ingestion:**  

  A web server UF tails `/var/log/nginx/access.log`, forwards to indexer cluster. Indexer parses timestamps (`%d/%b/%Y:%H:%M:%S %z`), assigns `sourcetype=access_combined`, writes to index `web_logs`. Each event: `192.168.1.50 - - [09/Feb/2026:15:30:01 +0000] "GET /api/users HTTP/1.1" 200 1532 0.045`. TSIDX indexes terms: "192.168.1.50", "GET", "/api/users", "200".


> **
  **Deep Dive — TSIDX (Time-Series Index):**  

  TSIDX is Splunk's inverted index stored per-bucket. Structure: segmented terms → posting lists (event offsets in rawdata journal). Terms are *major segments* (full words) and *minor segments* (sub-tokens from segmentation). Default segmentation: `SEGMENTATION-regex` splits on non-alphanumeric characters. Index-time field extraction creates `field::value` entries in TSIDX. *Bloom filters* (.bloomfilter) per bucket allow fast negative lookups — if a term definitely isn't in a bucket, skip it entirely during search. This reduces I/O by 90%+ for sparse queries.


> **
  **Deep Dive — SmartStore (Remote Storage):**  

  SmartStore separates compute (indexers) from storage (S3/GCS/Azure Blob). Hot buckets are local. When rolled to warm, data is uploaded to remote store and local copy is cached. Indexers maintain a local cache manager (LRU eviction, configurable max size). On search, if bucket not cached locally, indexer fetches from remote store on-demand. Benefits: elastic storage costs, indexer nodes are stateless (can be auto-scaled). Tradeoff: cold search latency increases by 1-5s for cache misses.


## Flow 2: Search Processing (SPL Execution)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 340" xmlns="http://www.w3.org/2000/svg">

  
<defs>

    
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">

      
<polygon points="0 0, 10 3.5, 0 7" fill="#65a637">
</polygon>

    
</marker>

  
</defs>

  

  
<rect x="10" y="130" width="110" height="55" rx="8" fill="#2e7d32">
</rect>

  
<text x="65" y="155" text-anchor="middle" fill="white" font-size="12">
User / API
</text>

  
<text x="65" y="172" text-anchor="middle" fill="white" font-size="10">
SPL Query
</text>


  

  
<rect x="170" y="60" width="150" height="60" rx="8" fill="#f58220">
</rect>

  
<text x="245" y="85" text-anchor="middle" fill="white" font-size="12">
Search Head
</text>

  
<text x="245" y="100" text-anchor="middle" fill="white" font-size="10">
(Parse + Coordinate)
</text>


  

  
<rect x="170" y="180" width="150" height="60" rx="8" fill="#e65100">
</rect>

  
<text x="245" y="205" text-anchor="middle" fill="white" font-size="11">
SH Cluster Captain
</text>

  
<text x="245" y="222" text-anchor="middle" fill="white" font-size="10">
(Job Scheduling)
</text>


  

  
<rect x="390" y="30" width="160" height="55" rx="8" fill="#1565c0">
</rect>

  
<text x="470" y="52" text-anchor="middle" fill="white" font-size="11">
Indexer 1 (Map)
</text>

  
<text x="470" y="70" text-anchor="middle" fill="white" font-size="10">
Filter + Extract
</text>

  
<rect x="390" y="100" width="160" height="55" rx="8" fill="#1565c0">
</rect>

  
<text x="470" y="122" text-anchor="middle" fill="white" font-size="11">
Indexer 2 (Map)
</text>

  
<text x="470" y="140" text-anchor="middle" fill="white" font-size="10">
Filter + Extract
</text>

  
<rect x="390" y="170" width="160" height="55" rx="8" fill="#1565c0">
</rect>

  
<text x="470" y="192" text-anchor="middle" fill="white" font-size="11">
Indexer N (Map)
</text>

  
<text x="470" y="210" text-anchor="middle" fill="white" font-size="10">
Filter + Extract
</text>


  

  
<rect x="620" y="100" width="150" height="60" rx="8" fill="#7b1fa2">
</rect>

  
<text x="695" y="125" text-anchor="middle" fill="white" font-size="12">
Search Head
</text>

  
<text x="695" y="142" text-anchor="middle" fill="white" font-size="10">
(Reduce / Merge)
</text>


  

  
<rect x="840" y="100" width="130" height="60" rx="8" fill="#006064">
</rect>

  
<text x="905" y="125" text-anchor="middle" fill="white" font-size="12">
Results
</text>

  
<text x="905" y="142" text-anchor="middle" fill="white" font-size="10">
Events / Stats
</text>


  

  
<line x1="120" y1="155" x2="168" y2="95" stroke="#65a637" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="320" y1="90" x2="388" y2="60" stroke="#65a637" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="320" y1="90" x2="388" y2="127" stroke="#65a637" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="320" y1="90" x2="388" y2="195" stroke="#65a637" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="550" y1="57" x2="618" y2="120" stroke="#65a637" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="550" y1="127" x2="618" y2="130" stroke="#65a637" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="550" y1="197" x2="618" y2="140" stroke="#65a637" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="770" y1="130" x2="838" y2="130" stroke="#65a637" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="245" y1="120" x2="245" y2="178" stroke="#f58220" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Search Execution Steps


  1. **SPL Parsing** — Search Head parses the SPL query into an AST. Commands are classified: *streaming* (per-event: search, eval, rex, where), *transforming* (aggregate: stats, chart, timechart), *generating* (create events: inputlookup, makeresults), *orchestrating* (join, append, subsearch).

  2. **Search Optimization** — The Splunk query planner optimizes by: pushing streaming commands to indexers (run in parallel), evaluating `tstats` against accelerated data models (skip rawdata), identifying bloom filter eligible terms to prune buckets.

  3. **Map Phase (Distributed)** — Search Head dispatches the "streaming" portion to all relevant indexers. Each indexer: scans TSIDX → bloom filter check → read matching events from rawdata journal → apply field extractions (search-time regex) → filter → stream results back.

  4. **Reduce Phase (Search Head)** — Search Head receives partial results from all indexers, merges them, applies transforming commands (stats, dedup, sort, head/tail), evaluates subsearches, and produces final result set.

  5. **Result Delivery** — Results served to user via Splunk Web (SplunkJS framework), REST API (`GET /services/search/jobs/{sid}/results`), or forwarded to scheduled alert actions.


> **
  **Example — Error rate search:**  

  `index=web_logs sourcetype=access_combined status>=500 | timechart span=5m count AS errors | where errors > 100`  

  Search Head parses → pushes `index=web_logs sourcetype=access_combined status>=500` to indexers (Map phase). Each indexer: bloom filter checks for "500", "501", "502"... → reads matching events → extracts `status` field → filters status≥500 → streams events back. Search Head: aggregates into 5-minute bins → applies `where errors > 100` threshold. ~10 indexers scan 500GB in parallel → results in ~8 seconds.


> **
  **Deep Dive — SPL Command Pipeline:**  

  SPL uses a Unix pipe model. Commands chain via `|`. Execution categories:  

  • **Streaming (distributable)**: `search`, `eval`, `rex`, `rename`, `where` — run on indexers in parallel  

  • **Streaming (centralized)**: `head`, `streamstats` — must run on search head (order-dependent)  

  • **Transforming**: `stats`, `timechart`, `top`, `rare` — partial on indexers, final merge on search head  

  • **Generating**: `inputlookup`, `makeresults`, `tstats` — generate events from non-index sources  

  • **Dataset**: `from`, `pivot` — operate on data models  

  For `stats` commands, indexers compute partial aggregates (sum, count, min, max) locally and send only summaries to search head. This reduces network transfer by 1000x compared to sending all raw events.


> **
  **Deep Dive — tstats (Accelerated Data Models):**  

  `tstats` searches directly against TSIDX summary data — no rawdata decompression needed. Data Model Acceleration (DMA) pre-builds summary TSIDX files at index time for defined data models. Example: `| tstats count WHERE index=web_logs BY status, host` returns in <1s for TB-scale data (vs. 30s+ for equivalent `stats` on raw events). Tradeoff: DMA consumes additional storage (5-15% overhead) and requires maintenance windows.


## Flow 3: Real-Time Alerting & Monitoring


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 300" xmlns="http://www.w3.org/2000/svg">

  
<defs>

    
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">

      
<polygon points="0 0, 10 3.5, 0 7" fill="#65a637">
</polygon>

    
</marker>

  
</defs>

  

  
<rect x="10" y="110" width="140" height="60" rx="8" fill="#7b1fa2">
</rect>

  
<text x="80" y="135" text-anchor="middle" fill="white" font-size="12">
Saved Search
</text>

  
<text x="80" y="152" text-anchor="middle" fill="white" font-size="10">
(Scheduled / RT)
</text>


  

  
<rect x="200" y="110" width="140" height="60" rx="8" fill="#f58220">
</rect>

  
<text x="270" y="135" text-anchor="middle" fill="white" font-size="12">
Search Head
</text>

  
<text x="270" y="152" text-anchor="middle" fill="white" font-size="10">
(Execute SPL)
</text>


  

  
<rect x="400" y="110" width="140" height="60" rx="8" fill="#1565c0">
</rect>

  
<text x="470" y="135" text-anchor="middle" fill="white" font-size="12">
Alert Evaluator
</text>

  
<text x="470" y="152" text-anchor="middle" fill="white" font-size="10">
(Threshold Check)
</text>


  

  
<rect x="600" y="20" width="140" height="50" rx="8" fill="#c62828">
</rect>

  
<text x="670" y="50" text-anchor="middle" fill="white" font-size="11">
Email Notification
</text>

  
<rect x="600" y="85" width="140" height="50" rx="8" fill="#c62828">
</rect>

  
<text x="670" y="115" text-anchor="middle" fill="white" font-size="11">
Webhook / REST
</text>

  
<rect x="600" y="150" width="140" height="50" rx="8" fill="#c62828">
</rect>

  
<text x="670" y="180" text-anchor="middle" fill="white" font-size="11">
PagerDuty / Slack
</text>

  
<rect x="600" y="215" width="140" height="50" rx="8" fill="#c62828">
</rect>

  
<text x="670" y="245" text-anchor="middle" fill="white" font-size="11">
Run Script / SOAR
</text>


  

  
<rect x="790" y="110" width="140" height="60" rx="8" fill="#006064">
</rect>

  
<text x="860" y="135" text-anchor="middle" fill="white" font-size="12">
Throttle / Suppress
</text>

  
<text x="860" y="152" text-anchor="middle" fill="white" font-size="10">
(Dedup Window)
</text>


  

  
<line x1="150" y1="140" x2="198" y2="140" stroke="#65a637" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="340" y1="140" x2="398" y2="140" stroke="#65a637" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="540" y1="130" x2="598" y2="47" stroke="#65a637" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="540" y1="135" x2="598" y2="110" stroke="#65a637" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="540" y1="145" x2="598" y2="175" stroke="#65a637" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="540" y1="150" x2="598" y2="240" stroke="#65a637" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="740" y1="140" x2="788" y2="140" stroke="#65a637" stroke-width="2" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Alerting Steps


  1. **Alert Definition** — Admin creates a saved search with alert conditions. *Scheduled alert*: runs SPL on cron schedule (e.g., every 5 min). *Real-time alert*: uses rolling time window, continuously evaluates as events arrive.

  2. **Trigger Conditions** — Number of results > threshold, custom condition (SPL expression), per-result trigger, throttle window (e.g., once per 10 minutes per `host`).

  3. **Alert Actions** — Triggered alerts fire one or more actions: send email, call webhook (HTTP POST with JSON payload), create PagerDuty incident, post to Slack channel, run custom script, trigger SOAR playbook (Splunk SOAR/Phantom).

  4. **Suppression & Throttle** — Prevent alert storms: suppress duplicate alerts within time window grouped by field values. E.g., suppress per `host` for 30 minutes after first trigger.

  5. **Notable Events (ES)** — In Splunk Enterprise Security, alerts create Notable Events in the Incident Review dashboard with severity, owner assignment, status tracking (New → In Progress → Resolved).


> **
  **Example — Security alert:**  

  Saved search: `index=auth sourcetype=sshd "Failed password" | stats count BY src_ip | where count > 50` runs every 5 minutes. When `src_ip=10.0.5.99` has 73 failed SSH logins → alert fires → webhook POSTs to SOAR → automated playbook blocks IP in firewall via API → PagerDuty page to SOC analyst → notable event created in Splunk ES.


> **
  **Deep Dive — Real-Time Search vs. Scheduled:**  

  *Real-time search* uses a sliding window. The search head opens a persistent connection to indexers and receives events as they are indexed. Window types: **real-time (windowed)** — e.g., last 30 seconds, re-evaluated continuously; **real-time (all-time)** — growing window from search start. Real-time is expensive: keeps search slots open, increases indexer CPU. *Indexed real-time* (introduced in Splunk 7.3) is a lighter alternative: uses periodic micro-batches (1-second granularity) instead of true streaming. Recommended for most RT alerting use cases with <1s additional latency but 80% less resource usage.


## Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 520" xmlns="http://www.w3.org/2000/svg">

  
<defs>

    
<marker id="ah4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">

      
<polygon points="0 0, 10 3.5, 0 7" fill="#65a637">
</polygon>

    
</marker>

  
</defs>

  

  
<rect x="10" y="20" width="120" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="47" text-anchor="middle" fill="white" font-size="11">
Servers (UF)
</text>

  
<rect x="10" y="80" width="120" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="107" text-anchor="middle" fill="white" font-size="11">
Network (Syslog)
</text>

  
<rect x="10" y="140" width="120" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="167" text-anchor="middle" fill="white" font-size="11">
Apps (HEC API)
</text>

  
<rect x="10" y="200" width="120" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="227" text-anchor="middle" fill="white" font-size="11">
Cloud (Modular)
</text>


  

  
<rect x="190" y="80" width="130" height="55" rx="8" fill="#e65100">
</rect>

  
<text x="255" y="102" text-anchor="middle" fill="white" font-size="11">
Forwarder Tier
</text>

  
<text x="255" y="120" text-anchor="middle" fill="white" font-size="10">
UF / HF / Syslog-ng
</text>


  

  
<rect x="190" y="170" width="130" height="45" rx="8" fill="#37474f">
</rect>

  
<text x="255" y="197" text-anchor="middle" fill="white" font-size="11">
LB (HAProxy/F5)
</text>


  

  
<rect x="380" y="40" width="150" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="455" y="70" text-anchor="middle" fill="white" font-size="11">
Indexer Peer 1
</text>

  
<rect x="380" y="105" width="150" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="455" y="135" text-anchor="middle" fill="white" font-size="11">
Indexer Peer 2
</text>

  
<rect x="380" y="170" width="150" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="455" y="200" text-anchor="middle" fill="white" font-size="11">
Indexer Peer N
</text>


  

  
<rect x="380" y="250" width="150" height="45" rx="8" fill="#7b1fa2">
</rect>

  
<text x="455" y="277" text-anchor="middle" fill="white" font-size="11">
Cluster Manager
</text>


  

  
<rect x="380" y="320" width="150" height="45" rx="8" fill="#4a148c">
</rect>

  
<text x="455" y="347" text-anchor="middle" fill="white" font-size="11">
S3 / SmartStore
</text>


  

  
<rect x="610" y="40" width="150" height="50" rx="8" fill="#f58220">
</rect>

  
<text x="685" y="70" text-anchor="middle" fill="white" font-size="11">
Search Head 1
</text>

  
<rect x="610" y="105" width="150" height="50" rx="8" fill="#f58220">
</rect>

  
<text x="685" y="135" text-anchor="middle" fill="white" font-size="11">
Search Head 2
</text>

  
<rect x="610" y="170" width="150" height="50" rx="8" fill="#f58220">
</rect>

  
<text x="685" y="200" text-anchor="middle" fill="white" font-size="11">
Search Head N
</text>


  

  
<rect x="610" y="250" width="150" height="45" rx="8" fill="#e65100">
</rect>

  
<text x="685" y="277" text-anchor="middle" fill="white" font-size="11">
SHC Captain (Raft)
</text>


  

  
<rect x="610" y="320" width="150" height="45" rx="8" fill="#37474f">
</rect>

  
<text x="685" y="347" text-anchor="middle" fill="white" font-size="11">
Deployer (Apps)
</text>


  

  
<rect x="840" y="60" width="130" height="50" rx="8" fill="#006064">
</rect>

  
<text x="905" y="82" text-anchor="middle" fill="white" font-size="11">
Splunk Web UI
</text>

  
<text x="905" y="98" text-anchor="middle" fill="white" font-size="10">
Dashboards
</text>

  
<rect x="840" y="130" width="130" height="50" rx="8" fill="#006064">
</rect>

  
<text x="905" y="152" text-anchor="middle" fill="white" font-size="11">
REST API
</text>

  
<text x="905" y="168" text-anchor="middle" fill="white" font-size="10">
/services/*
</text>

  
<rect x="840" y="200" width="130" height="50" rx="8" fill="#c62828">
</rect>

  
<text x="905" y="222" text-anchor="middle" fill="white" font-size="11">
Alert Actions
</text>

  
<text x="905" y="238" text-anchor="middle" fill="white" font-size="10">
Webhook/SOAR
</text>


  

  
<rect x="840" y="320" width="130" height="45" rx="8" fill="#4a148c">
</rect>

  
<text x="905" y="347" text-anchor="middle" fill="white" font-size="11">
KV Store (MongoDB)
</text>


  

  
<rect x="190" y="320" width="130" height="45" rx="8" fill="#37474f">
</rect>

  
<text x="255" y="347" text-anchor="middle" fill="white" font-size="11">
Deployment Server
</text>


  

  
<rect x="190" y="390" width="130" height="45" rx="8" fill="#37474f">
</rect>

  
<text x="255" y="417" text-anchor="middle" fill="white" font-size="11">
License Manager
</text>


  

  
<line x1="130" y1="42" x2="188" y2="100" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="130" y1="102" x2="188" y2="107" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="130" y1="162" x2="188" y2="190" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="130" y1="222" x2="188" y2="195" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="320" y1="107" x2="378" y2="65" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="320" y1="107" x2="378" y2="130" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="320" y1="192" x2="378" y2="195" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="530" y1="65" x2="608" y2="65" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="530" y1="130" x2="608" y2="130" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="530" y1="195" x2="608" y2="195" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="760" y1="65" x2="838" y2="80" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="760" y1="130" x2="838" y2="150" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="760" y1="195" x2="838" y2="220" stroke="#65a637" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="455" y1="220" x2="455" y2="248" stroke="#f58220" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah4)">
</line>

  
<line x1="455" y1="295" x2="455" y2="318" stroke="#f58220" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah4)">
</line>

  
<line x1="685" y1="220" x2="685" y2="248" stroke="#f58220" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah4)">
</line>

  
<line x1="685" y1="295" x2="685" y2="318" stroke="#f58220" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah4)">
</line>


</svg>

</details>


> **
  **Example — Full flow (security investigation):**  

  1. Firewall sends syslog to Heavy Forwarder → routes to `index=firewall` on indexer cluster  

  2. Scheduled correlation search in Splunk ES: `| tstats count from datamodel=Network_Traffic where dest_port=4444 BY src_ip, dest_ip | where count > 5`  

  3. Alert triggers → creates Notable Event (severity=Critical) → SOAR playbook auto-enriches with VirusTotal, GeoIP → analyst triages in Incident Review → executes response action (isolate host via EDR API)  

  4. Dashboard shows real-time: blocked connections, top attacker IPs, MITRE ATT&CK mapping, mean time to respond


## Database Schema & Storage Design


### Bucket Structure (Filesystem-Based)


  
  
  
  
  
  


| Component    | File                                             | Description                                                                                                                   |
|--------------|--------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| Raw Data     | `rawdata/journal.gz`                             | Compressed events (zlib/zstd), append-only, ~1:8 compression ratio. Each event has offset, length, metadata pointers.         |
| TSIDX        | `*.tsidx`                                        | Inverted index: term → posting list (offset in journal). Multiple TSIDX files per bucket as index grows. ~15-25% of raw size. |
| Bloom Filter | `.bloomfilter`                                   | Probabilistic set membership per bucket. 10 bits/term, 7 hash functions. FP rate ~0.8%. Used to skip irrelevant buckets.      |
| Metadata     | `.bucketmanifest`                                | Bucket time range (earliest/latest), event count, size, generation ID, primary/replicated flag.                               |
| Hot Metadata | `Hosts.data`, `Sources.data`, `Sourcetypes.data` | Per-bucket metadata catalogs for `| metadata` searches and typeahead.                                                         |


### KV Store (App State — MongoDB-backed)


  
  
    
    
    
  
  
    
    
    
  
  
    
    
    
  
  
    
    
    
  


| Collection         | Fields                                                                                                                                | Purpose                                   |
|--------------------|---------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------|
| saved_searches     | `_key`  `PK`, `name`, `search` (SPL), `cron_schedule`, `alert_type`, `dispatch.earliest_time`, `dispatch.latest_time`, `owner`, `app` | Persisted saved searches and alerts       |
| lookup_definitions | `_key`  `PK`, `filename`, `fields_list`, `collection`, `external_type`                                                                | Lookup table definitions for enrichment   |
| notable_events     | `_key`  `PK`, `rule_name`  `IDX`, `severity`, `src_ip`, `dest_ip`, `status`  `IDX`, `owner`, `time`  `IDX`, `disposition`             | ES notable events for incident management |
| asset_inventory    | `_key`  `PK`, `ip`  `IDX`, `hostname`, `mac`, `category`, `priority`, `bunit`, `owner`                                                | Asset and identity framework data         |


### Index Configuration (indexes.conf)


  
  
  
  
  
  
  
  


| Setting                  | Default                     | Description                                       |
|--------------------------|-----------------------------|---------------------------------------------------|
| `homePath`               | `$SPLUNK_DB/<index>/db`     | Hot/warm bucket location (fast SSD recommended)   |
| `coldPath`               | `$SPLUNK_DB/<index>/colddb` | Cold bucket location (can be slower storage)      |
| `frozenTimePeriodInSecs` | `188697600` (6 years)       | Max age before freezing                           |
| `maxTotalDataSizeMB`     | `500000`                    | Max total index size before oldest buckets frozen |
| `maxHotBuckets`          | `10`                        | Max concurrent hot buckets per index              |
| `maxDataSize`            | `auto_high_volume`          | Max single bucket size (10GB for high volume)     |
| `repFactor`              | `auto`                      | Inherit cluster replication factor                |


> **
  **Deep Dive — TSIDX Inverted Index Internals:**  

  TSIDX uses a **lexicon + postings** structure similar to Lucene but optimized for time-series data. The lexicon is a sorted dictionary of terms (both raw text segments and `field::value` pairs). Each term maps to a posting list containing: event offsets in journal, time range of matching events. Posting lists are delta-encoded and compressed (variable-byte encoding). Search process: 1) lookup term in lexicon (binary search), 2) read posting list, 3) intersect posting lists for AND queries, 4) seek to offsets in journal.gz, 5) decompress and return events. For `tstats`, only steps 1-2 are needed — no rawdata decompression.


## Cache & Performance Deep Dive


### SmartStore Cache Architecture


  
  
  
  
  
  


| Cache Layer           | Strategy                 | Eviction             | Details                                                                                                               |
|-----------------------|--------------------------|----------------------|-----------------------------------------------------------------------------------------------------------------------|
| Hot Bucket Cache      | Write-through            | N/A (always local)   | Active write buckets always on local SSD. Rolled to warm → uploaded to remote.                                        |
| Warm/Cold Cache       | LRU (demand-fetch)       | LRU by access time   | Configurable max cache size per index. Cache miss → fetch from S3 (~1-5s). Cache hit rate >90% for typical workloads. |
| TSIDX Summary Cache   | Pre-computed             | Age-based rollup     | Accelerated data model summaries. Pre-built at index time. Refreshed on schedule.                                     |
| Search Artifact Cache | Per-search-job           | TTL (10 min default) | Results of completed searches cached as dispatch artifacts. Reusable by other users with same query.                  |
| KV Store Cache        | MongoDB WiredTiger cache | LRU                  | In-memory cache for lookup tables and app state. Sized at 50% of RAM by default.                                      |


### Performance Optimization Techniques


  - **Bloom Filters** — Skip entire buckets when search terms definitely not present. Saves 80-95% I/O for selective searches.

  - **Parallel Streaming** — Search head opens concurrent TCP connections to all indexers. Results streamed back as produced (no wait for slowest).

  - **Search Affinity** — SHC Captain routes identical searches to the same search head to reuse cached artifacts.

  - **Summary Indexing** — `| collect` or `| sitimechart` commands create pre-aggregated summaries in dedicated summary indexes for expensive recurring reports.

  - **Report Acceleration** — Auto-builds summary for saved reports. First run: full search; subsequent runs: incremental update of summary.


## Scaling Considerations


### Horizontal Scaling Tiers


  
  
  
  
  


| Tier             | Scale Unit         | Scaling Metric                    | Notes                                                                               |
|------------------|--------------------|-----------------------------------|-------------------------------------------------------------------------------------|
| Forwarder Tier   | UF per source host | ~500 MB/day per UF typical        | Minimal overhead (<2% CPU). HFs for heavy parsing/routing.                          |
| Indexer Tier     | Indexer peer node  | ~200-300 GB/day per indexer       | Scale linearly. Add indexers for throughput. Rebalance buckets via Cluster Manager. |
| Search Head Tier | SH node in SHC     | ~20-50 concurrent searches per SH | Scale for concurrent users. SHC Captain (Raft consensus) distributes jobs.          |
| Storage Tier     | SmartStore remote  | Unlimited (S3/GCS)                | Local cache sized per indexer. Remote storage scales independently.                 |


### Deployment Topologies


  - **Single Server** — All-in-one for <50 GB/day. Forwarder + Indexer + Search Head on one machine. Lab/dev use.

  - **Distributed** — Separate forwarder → indexer → search head tiers. 50-500 GB/day. Most common production topology.

  - **Clustered** — Indexer Cluster (RF=3, SF=2) + Search Head Cluster (3+ SHs). 500 GB - 10 TB/day. High availability.

  - **Multi-Site** — site1/site2 indexer clusters with site-aware replication. site_replication_factor=origin:2,total:3. DR and data locality.

  - **Splunk Cloud** — SaaS: fully managed indexer/SH clusters. Customer manages forwarders only. Victoria/Noah architecture.


> **
  **Deep Dive — Indexer Cluster Replication:**  

  The Cluster Manager (CM) maintains a **generation** model. Each generation represents a consistent view of bucket distribution. When a peer joins/leaves, CM initiates generation roll: reassigns bucket primacy, triggers replication to maintain RF/SF. Replication is *bucket-level*: source peer streams rawdata + TSIDX to target peer. **Search Factor** (SF): number of searchable copies (with TSIDX). SF ≤ RF. Example: RF=3, SF=2 → 3 copies of raw data, 2 have searchable indexes, 1 is rawdata-only (cheaper). During peer failure: CM marks peer as down after heartbeat timeout (60s default) → promotes non-searchable copies to searchable (rebuild TSIDX) → initiates fix-up replication to restore RF. Recovery time: minutes for search restoration, hours for full replication fix-up on TB-scale clusters.


## Tradeoffs & Alternative Approaches


> **
  **Tradeoff — Schema-on-Read vs. Schema-on-Write:**  

  Splunk uses **schema-on-read**: data is ingested as raw text, fields extracted at search time via regex/delimiters. Advantage: no upfront schema definition, any data format accepted, retroactive field extraction. Disadvantage: search-time extraction is CPU-intensive (10x slower than pre-indexed fields). Mitigation: index-time field extraction for frequently searched fields (`INDEXED_EXTRACTIONS`), calculated fields, accelerated data models.


> **
  **Tradeoff — SmartStore vs. Local Storage:**  

  *Local storage*: all buckets on local SSD. Fastest search, no remote fetch latency. Cost: expensive SSD at scale, indexers are stateful (hard to replace). *SmartStore*: hot local + warm/cold on S3. Cost: 10x cheaper storage, stateless indexers (easy auto-scaling). Penalty: cache miss adds 1-5s per bucket fetch. Best for: large retention requirements, cloud-native deployments, elastic scaling.


> **
  **Tradeoff — Splunk vs. ELK (Elasticsearch + Logstash + Kibana):**  

  *Splunk*: Proprietary, SPL query language (more powerful for chained analytics), built-in SOAR/SIEM, easier for non-developers (GUI-driven). Cost: $$$, licensed by daily ingestion volume ($1-3K/GB/year). *ELK*: Open-source core (Elastic License), Lucene query + KQL, JSON document model, developer-oriented. Cost: infrastructure + operational expertise. Splunk excels at security ops (ES) and enterprise compliance. ELK excels at developer observability and cost-sensitive deployments.


### Alternative Approaches


  
  
  
  
  
  


| Approach                | Technology                                 | When to Prefer                                                |
|-------------------------|--------------------------------------------|---------------------------------------------------------------|
| Columnar Log Analytics  | ClickHouse, Apache Druid                   | High-cardinality metrics, SQL-native teams, cost optimization |
| Cloud-Native Logging    | AWS CloudWatch, GCP Cloud Logging, Datadog | Cloud-first organizations, managed services preference        |
| Open-Source SIEM        | Wazuh, OSSEC, SecurityOnion                | Budget-constrained security operations                        |
| OpenTelemetry + Backend | OTel Collector → Jaeger/Grafana Tempo/Loki | Vendor-neutral observability, traces + logs correlation       |
| Data Lake Analytics     | Apache Spark + Delta Lake + Databricks     | ML on log data, long-term analytics, data science workloads   |


## Additional Information


### Splunk Processing Language (SPL) — Key Commands


  
  
  
  
  
  
  


| Category       | Commands                                     | Example                                        |
|----------------|----------------------------------------------|------------------------------------------------|
| Filtering      | `search`, `where`, `dedup`                   | `index=web status=500 | dedup src_ip`          |
| Transformation | `stats`, `timechart`, `chart`, `top`         | `| stats avg(response_time) BY endpoint`       |
| Evaluation     | `eval`, `rex`, `spath`                       | `| eval duration_ms=duration*1000`             |
| Enrichment     | `lookup`, `inputlookup`, `geostats`          | `| lookup geo_ip src_ip OUTPUT country`        |
| Join           | `join`, `append`, `subsearch`                | `| join src_ip [search index=threat_intel]`    |
| Output         | `table`, `fields`, `outputlookup`, `collect` | `| table _time, src_ip, status, response_time` |


### Security (Splunk Enterprise Security)


  - **Correlation Searches** — Scheduled SPL that detect threats across multiple data sources. Map to MITRE ATT&CK techniques.

  - **Risk-Based Alerting (RBA)** — Assign risk scores to entities (users, hosts). Alert only when cumulative risk exceeds threshold. Reduces alert fatigue by 90%.

  - **Adaptive Response** — Automated response actions: block IP, disable user, quarantine host, create ticket. Integrated with SOAR playbooks.

  - **Threat Intelligence Framework** — Ingest IOCs from STIX/TAXII, MISP, OpenCTI. Auto-correlate against all incoming data.


### Observability (Splunk Observability Cloud)


  - **Splunk APM** — Distributed tracing (OpenTelemetry), full-fidelity trace storage (no sampling), AI-directed root cause analysis.

  - **Splunk Infrastructure Monitoring** — Real-time streaming analytics on metrics (SignalFlow language). 1-second resolution. 100K+ metrics/sec per node.

  - **Splunk RUM** — Real User Monitoring: browser/mobile performance, Core Web Vitals, session replay, error tracking.

  - **Splunk Synthetic Monitoring** — Proactive uptime checks, multi-step API tests, global checkpoint locations.


### Data Input Methods


  - **Universal Forwarder** — Lightweight agent (50MB), file monitoring + network inputs, output to indexers. No parsing.

  - **Heavy Forwarder** — Full Splunk instance configured for forwarding. Can parse, filter, route, mask data before forwarding.

  - **HTTP Event Collector (HEC)** — `POST /services/collector/event`, token auth, JSON/raw, batching support (`/services/collector/event` with array payload). 1000s events/request.

  - **Syslog (TCP/UDP)** — Direct syslog input on port 514. For network devices, firewalls. Often via syslog-ng/rsyslog intermediate.

  - **Modular Inputs** — Python/Node scripts that poll external APIs (AWS CloudTrail, O365, Salesforce). Checkpoint-based incremental collection.

  - **DB Connect** — JDBC connections to SQL databases. Scheduled queries import rows as events. Supports Oracle, MySQL, PostgreSQL, SQL Server.