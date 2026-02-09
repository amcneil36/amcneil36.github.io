# 📊 System Design: Ad Click Aggregator


## 📋 Functional Requirements


  - Track ad impressions and clicks in real-time across billions of events per day

  - Aggregate click counts by ad_id, campaign_id, advertiser_id with time-window granularity (minute/hour/day)

  - Provide real-time dashboards for advertisers (click-through rate, spend, conversions)

  - Support multi-dimensional filtering (by region, device, time range, creative variant)


## 🔒 Non-Functional Requirements


  - **High throughput:** ~10 billion clicks/day (~115K events/sec), ~100 billion impressions/day

  - **Low latency aggregation:** Real-time counts available within 1-2 minutes of click event

  - **Exactly-once counting:** No duplicate or lost clicks — directly impacts billing accuracy

  - **Fault tolerance:** No data loss even during node failures — click data is money

  - **Query performance:** Dashboard queries return in <500ms for any time range

  - **Scalability:** Handle Black Friday / Super Bowl traffic spikes (10x normal)


## 🔄 Flow 1 — Click Event Ingestion & Real-Time Aggregation


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
User Click
</text>

  
<text x="75" y="215" text-anchor="middle" fill="white" font-size="10">
(Browser/App)
</text>

  
<line x1="130" y1="200" x2="190" y2="200" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="200" y="170" width="130" height="60" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="265" y="198" text-anchor="middle" fill="white" font-size="13">
Click Collector
</text>

  
<text x="265" y="215" text-anchor="middle" fill="white" font-size="10">
(Edge Server)
</text>

  
<line x1="330" y1="200" x2="400" y2="200" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="410" y="170" width="140" height="60" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="480" y="198" text-anchor="middle" fill="white" font-size="13">
Apache Kafka
</text>

  
<text x="480" y="215" text-anchor="middle" fill="white" font-size="10">
(click-events topic)
</text>

  
<line x1="550" y1="185" x2="620" y2="120" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="550" y1="215" x2="620" y2="280" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="630" y="90" width="160" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="710" y="115" text-anchor="middle" fill="white" font-size="12">
Apache Flink
</text>

  
<text x="710" y="133" text-anchor="middle" fill="white" font-size="10">
(Stream Aggregation)
</text>

  
<rect x="630" y="255" width="160" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="710" y="280" text-anchor="middle" fill="white" font-size="12">
Flink (Dedup)
</text>

  
<text x="710" y="298" text-anchor="middle" fill="white" font-size="10">
(Bloom Filter / Redis)
</text>

  
<line x1="790" y1="120" x2="870" y2="120" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="790" y1="285" x2="870" y2="285" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="880" y="90" width="150" height="60" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="955" y="115" text-anchor="middle" fill="white" font-size="12">
OLAP DB
</text>

  
<text x="955" y="133" text-anchor="middle" fill="white" font-size="10">
(ClickHouse/Druid)
</text>

  
<rect x="880" y="255" width="150" height="60" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="955" y="280" text-anchor="middle" fill="white" font-size="12">
S3 Data Lake
</text>

  
<text x="955" y="298" text-anchor="middle" fill="white" font-size="10">
(Raw Events — Parquet)
</text>

  
<line x1="1030" y1="120" x2="1090" y2="180" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="1050" y="175" width="130" height="50" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="1115" y="205" text-anchor="middle" fill="white" font-size="12">
Redis (Real-time)
</text>


</svg>

</details>


### Step-by-Step


  1. **User clicks ad:** Browser fires `GET /click?ad_id=ad_123&campaign_id=camp_456&creative=v2&user_id=hash&device=ios&geo=US-CA&ts=1707500000` — redirect to advertiser's landing page (HTTP 302)

  2. **Click Collector (Edge):** Lightweight HTTP server at CDN edge. Logs click event, generates unique `click_id` (UUID), responds with 302 redirect immediately. Publishes event to Kafka asynchronously (fire-and-forget with local WAL buffer)

  3. **Kafka (click-events):** Partitioned by `ad_id` for ordering within an ad. Retention: 7 days. Replication factor: 3. Multiple consumer groups: real-time aggregation, raw archival, fraud detection

  4. **Deduplication (Flink):** Bloom filter checks if `click_id` was already processed. If duplicate (user double-clicked, network retry) → discard. Bloom filter backed by Redis for distributed state

  5. **Stream Aggregation (Flink):** Tumbling windows aggregate click counts per (ad_id, campaign_id, minute). Emits aggregated counts to OLAP DB. Also computes CTR (clicks / impressions) by joining with impression stream

  6. **OLAP DB (ClickHouse):** Pre-aggregated minute-level data stored in columnar format. Supports fast analytical queries: "clicks per campaign in last 24 hours grouped by region"

  7. **Redis (real-time counters):** `INCR click:{ad_id}:{minute}` for sub-second freshness on dashboard. TTL 24 hours — older data served from ClickHouse

  8. **S3 Data Lake:** Raw click events archived in Parquet format for batch reprocessing, ML training (fraud models), and reconciliation


> **
**Example:** User clicks ad_123 on CNN.com at 14:05:32 → Click Collector logs event, redirects to nike.com → Kafka receives event (partition = hash(ad_123) % 32) → Flink dedup: click_id is new → Aggregator: ad_123 minute-bucket 14:05 count incremented → ClickHouse receives: {ad_id: ad_123, campaign: camp_456, minute: "2024-02-09T14:05:00", clicks: 1, region: "US-CA", device: "ios"} → Dashboard shows campaign has 12,450 clicks in last hour, CTR 2.3%


### 🔍 Component Deep Dives — Ingestion Flow


Click Collector (Edge Server)


**Protocol:** HTTP GET (not POST) — allows browser-native redirect chain. Query parameters carry all metadata.


**Why edge?** Deployed at CDN PoPs (e.g., CloudFlare Workers) to minimize latency — user should be redirected to advertiser landing page in <100ms


**Local WAL:** Before publishing to Kafka, event is written to local Write-Ahead Log (disk). If Kafka is temporarily unavailable, events are buffered and replayed when Kafka recovers. Prevents data loss.


**Click validation:** Basic checks — is ad_id valid? Is campaign active? Is user-agent a known bot? IP rate limiting (100 clicks/min per IP to prevent click fraud)


Apache Flink — Stream Processing


**Windowing:** Tumbling windows of 1 minute for real-time aggregation. Sliding windows of 1 hour for rolling CTR calculation.


**Watermarks:** Event-time processing with watermarks — handles late-arriving events (e.g., mobile user with delayed network). Allowed lateness: 5 minutes. Events arriving after window closes are sent to side output for batch correction.


**Checkpointing:** Flink checkpoints state to S3 every 30 seconds (exactly-once semantics with Kafka source). On failure, Flink restarts from last checkpoint — reprocesses events from Kafka offset.


DataStream<ClickEvent> clicks = env.addSource(kafkaSource);
clicks
  .filter(event -> !bloomFilter.mightContain(event.clickId))  // dedup
  .keyBy(event -> event.adId + ":" + event.campaignId)
  .window(TumblingEventTimeWindows.of(Time.minutes(1)))
  .allowedLateness(Time.minutes(5))
  .aggregate(new ClickCountAggregator())
  .addSink(clickhouseSink);


Deduplication — Bloom Filter + Redis


**Bloom Filter:** Probabilistic data structure — false positives possible (~1%) but NO false negatives. If Bloom says "not seen" → definitely new click. If "maybe seen" → check Redis for exact lookup.


**Redis HyperLogLog:** Alternative for approximate distinct count per ad. `PFADD clicks:{ad_id} {user_id}` gives approximate unique clickers with 0.81% error and only 12 KB memory per counter.


**Why dedup matters:** Advertisers pay per click. Duplicate clicks = overcharging = lost trust = lawsuits. Must be exactly-once.


ClickHouse (OLAP)


**Why ClickHouse:** Columnar storage, vectorized query engine, compression (LZ4/ZSTD) — 10-100x faster than PostgreSQL for analytical queries. MergeTree engine with automatic data partitioning by date.


**Table engine:** `ReplicatedMergeTree ORDER BY (campaign_id, ad_id, event_time)` — data sorted on disk for efficient range scans


**Materialized Views:** Pre-aggregate by hour/day automatically as data arrives — dashboard queries hit materialized view, not raw data


## 🔄 Flow 2 — Dashboard Query (Advertiser Analytics)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="140" width="130" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="85" y="168" text-anchor="middle" fill="white" font-size="13">
Advertiser
</text>

  
<text x="85" y="185" text-anchor="middle" fill="white" font-size="10">
Dashboard
</text>

  
<line x1="150" y1="170" x2="220" y2="170" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="230" y="140" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="300" y="168" text-anchor="middle" fill="white" font-size="13">
Query Service
</text>

  
<text x="300" y="185" text-anchor="middle" fill="white" font-size="10">
(API + Router)
</text>

  
<line x1="370" y1="155" x2="440" y2="105" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="370" y1="170" x2="440" y2="170" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="370" y1="185" x2="440" y2="240" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="450" y="75" width="150" height="55" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="525" y="100" text-anchor="middle" fill="white" font-size="12">
Redis (Real-time)
</text>

  
<text x="525" y="117" text-anchor="middle" fill="white" font-size="10">
(Last ~1 hour)
</text>

  
<rect x="450" y="145" width="150" height="55" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="525" y="168" text-anchor="middle" fill="white" font-size="12">
ClickHouse
</text>

  
<text x="525" y="185" text-anchor="middle" fill="white" font-size="10">
(Minutes → Months)
</text>

  
<rect x="450" y="215" width="150" height="55" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="525" y="238" text-anchor="middle" fill="white" font-size="12">
S3 + Spark
</text>

  
<text x="525" y="255" text-anchor="middle" fill="white" font-size="10">
(Months → Years)
</text>

  
<line x1="600" y1="103" x2="680" y2="160" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="600" y1="172" x2="680" y2="165" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="690" y="140" width="150" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="765" y="163" text-anchor="middle" fill="white" font-size="12">
Result Merger
</text>

  
<text x="765" y="180" text-anchor="middle" fill="white" font-size="10">
(Combine Sources)
</text>


</svg>

</details>


### Step-by-Step


  1. **Advertiser requests:** `GET /api/v1/analytics?campaign_id=camp_456&start=2024-02-09T14:00&end=2024-02-09T15:00&granularity=minute&group_by=region,device`

  2. **Query Service routes:** Based on time range: last hour → Redis for real-time counters + ClickHouse for minute aggregates. Last 30 days → ClickHouse pre-aggregated tables. Last year+ → S3/Spark batch query

  3. **ClickHouse query:** `SELECT region, device, toStartOfMinute(event_time) as min, sum(clicks), sum(impressions), clicks/impressions as ctr FROM ad_clicks_1m WHERE campaign_id='camp_456' AND event_time BETWEEN ... GROUP BY region, device, min ORDER BY min`

  4. **Result Merger:** Combines real-time Redis counters (last few minutes) with ClickHouse historical data, deduplicates overlapping windows, fills gaps

  5. **Return:** Time-series data with multi-dimensional breakdowns → rendered as charts in dashboard


> **
**Example:** Advertiser opens campaign dashboard for "Summer Sale 2024" → Requests last 24 hours by hour, grouped by device → Query Service: last 1 hour from Redis real-time + last 23 hours from ClickHouse hourly table → Returns: [{"hour": "14:00", "clicks": 12450, "impressions": 542000, "ctr": 2.3%, "mobile": 8200, "desktop": 4250}] → Dashboard renders line chart of clicks over time with device breakdown


### 🔍 Component Deep Dives — Query Flow


Tiered Storage Architecture (Lambda Architecture)


This system uses a Lambda-like architecture with three tiers:


| Tier          | Data Source           | Latency       | Retention | Use Case                                 |
|---------------|-----------------------|---------------|-----------|------------------------------------------|
| Speed Layer   | Redis counters        | <1 second     | 24 hours  | Live dashboard, real-time spend tracking |
| Serving Layer | ClickHouse aggregates | <500ms        | 13 months | Campaign analytics, reporting            |
| Batch Layer   | S3 Parquet + Spark    | Minutes-hours | Unlimited | Historical analysis, ML training, audit  |


ClickHouse Materialized Views (Auto-Aggregation)

-- Raw minute-level data (written by Flink)
CREATE TABLE ad_clicks_raw (
    ad_id UInt64, campaign_id UInt64, event_time DateTime,
    region String, device String, clicks UInt64, impressions UInt64
) ENGINE = ReplicatedMergeTree()
PARTITION BY toYYYYMMDD(event_time) ORDER BY (campaign_id, ad_id, event_time);

-- Auto-aggregated hourly view
CREATE MATERIALIZED VIEW ad_clicks_hourly
ENGINE = SummingMergeTree() ORDER BY (campaign_id, ad_id, hour, region, device)
AS SELECT campaign_id, ad_id, toStartOfHour(event_time) as hour,
   region, device, sum(clicks) as clicks, sum(impressions) as impressions
FROM ad_clicks_raw GROUP BY campaign_id, ad_id, hour, region, device;

-- Auto-aggregated daily view
CREATE MATERIALIZED VIEW ad_clicks_daily
ENGINE = SummingMergeTree() ORDER BY (campaign_id, ad_id, day, region, device)
AS SELECT campaign_id, ad_id, toDate(event_time) as day,
   region, device, sum(clicks) as clicks, sum(impressions) as impressions
FROM ad_clicks_raw GROUP BY campaign_id, ad_id, day, region, device;


**Key:** SummingMergeTree automatically sums numeric columns when merging — no manual rollup needed. Data is automatically aggregated at multiple granularities.


## 🔄 Flow 3 — Click Fraud Detection


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 320" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="130" width="130" height="60" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="85" y="158" text-anchor="middle" fill="white" font-size="13">
Kafka
</text>

  
<text x="85" y="175" text-anchor="middle" fill="white" font-size="10">
(click-events)
</text>

  
<line x1="150" y1="160" x2="220" y2="160" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="230" y="130" width="150" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="305" y="155" text-anchor="middle" fill="white" font-size="12">
Fraud Detection
</text>

  
<text x="305" y="172" text-anchor="middle" fill="white" font-size="10">
Flink Job (ML)
</text>

  
<line x1="380" y1="145" x2="450" y2="100" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<line x1="380" y1="175" x2="450" y2="220" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="460" y="70" width="160" height="55" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="540" y="93" text-anchor="middle" fill="white" font-size="12">
Redis (IP Counters)
</text>

  
<text x="540" y="110" text-anchor="middle" fill="white" font-size="10">
(Sliding Window)
</text>

  
<rect x="460" y="195" width="160" height="55" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="540" y="218" text-anchor="middle" fill="white" font-size="12">
ML Fraud Model
</text>

  
<text x="540" y="235" text-anchor="middle" fill="white" font-size="10">
(Feature Store)
</text>

  
<line x1="620" y1="98" x2="700" y2="155" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<line x1="620" y1="222" x2="700" y2="160" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="710" y="130" width="150" height="60" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="785" y="155" text-anchor="middle" fill="white" font-size="12">
Fraud Verdicts
</text>

  
<text x="785" y="172" text-anchor="middle" fill="white" font-size="10">
(Flag + Deduct)
</text>


</svg>

</details>


### Step-by-Step


  1. **Separate Kafka consumer group:** Fraud detection runs as independent Flink job consuming same click-events topic

  2. **Rule-based checks:** IP rate limiting (>50 clicks/min from same IP), user-agent is known bot, click timestamp within ad display window (click without impression = suspicious)

  3. **ML model inference:** Features: click velocity, IP reputation score, device fingerprint uniqueness, click-to-conversion ratio, geographic plausibility. Model: isolation forest for anomaly detection

  4. **Verdict:** Clicks flagged as fraudulent are: (1) marked in OLAP DB so they don't count toward billing, (2) deducted from advertiser's running spend total, (3) aggregated into fraud reports

  5. **Feedback loop:** Advertisers can dispute charges → human review → labeled data feeds back into ML model retraining


> **
**Example:** Bot farm generates 10,000 clicks on competitor's ad from 50 IPs in 2 minutes → Rule engine flags: IP 1.2.3.4 has 200 clicks/min (threshold: 50) → ML model: click pattern has zero conversion correlation, device fingerprints are identical → All 10,000 clicks flagged as fraud → Advertiser not billed → Fraud report generated for campaign owner


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

  

  
<rect x="20" y="50" width="120" height="50" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="80" y="80" text-anchor="middle" fill="white" font-size="13">
Ad Click (User)
</text>

  
<rect x="20" y="120" width="120" height="50" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="80" y="150" text-anchor="middle" fill="white" font-size="13">
Ad Impression
</text>

  

  
<line x1="140" y1="75" x2="220" y2="110" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="140" y1="145" x2="220" y2="120" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="230" y="90" width="140" height="55" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="300" y="115" text-anchor="middle" fill="white" font-size="12">
Click/Impression
</text>

  
<text x="300" y="132" text-anchor="middle" fill="white" font-size="10">
Collectors (Edge)
</text>

  

  
<line x1="370" y1="118" x2="450" y2="118" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="460" y="90" width="150" height="55" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="535" y="115" text-anchor="middle" fill="white" font-size="13">
Apache Kafka
</text>

  
<text x="535" y="132" text-anchor="middle" fill="white" font-size="10">
(Partitioned by ad_id)
</text>

  

  
<line x1="610" y1="100" x2="690" y2="55" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="610" y1="118" x2="690" y2="135" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="610" y1="135" x2="690" y2="215" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="700" y="30" width="160" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="780" y="50" text-anchor="middle" fill="white" font-size="11">
Flink: Dedup + Aggregate
</text>

  
<text x="780" y="67" text-anchor="middle" fill="white" font-size="10">
(1-min tumbling window)
</text>

  
<rect x="700" y="110" width="160" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="780" y="130" text-anchor="middle" fill="white" font-size="11">
Flink: Fraud Detection
</text>

  
<text x="780" y="147" text-anchor="middle" fill="white" font-size="10">
(ML + Rules)
</text>

  
<rect x="700" y="190" width="160" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="780" y="210" text-anchor="middle" fill="white" font-size="11">
Flink: Raw Archival
</text>

  
<text x="780" y="227" text-anchor="middle" fill="white" font-size="10">
(S3 Sink — Parquet)
</text>

  

  
<line x1="860" y1="55" x2="940" y2="55" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="860" y1="55" x2="940" y2="135" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="860" y1="215" x2="940" y2="215" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="950" y="30" width="160" height="50" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="1030" y="50" text-anchor="middle" fill="white" font-size="11">
ClickHouse (OLAP)
</text>

  
<text x="1030" y="67" text-anchor="middle" fill="white" font-size="10">
(Serving Layer)
</text>

  
<rect x="950" y="110" width="160" height="50" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="1030" y="130" text-anchor="middle" fill="white" font-size="11">
Redis (Real-time)
</text>

  
<text x="1030" y="147" text-anchor="middle" fill="white" font-size="10">
(Speed Layer)
</text>

  
<rect x="950" y="190" width="160" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="1030" y="210" text-anchor="middle" fill="white" font-size="11">
S3 Data Lake
</text>

  
<text x="1030" y="227" text-anchor="middle" fill="white" font-size="10">
(Batch Layer)
</text>

  

  
<rect x="20" y="300" width="130" height="50" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="85" y="330" text-anchor="middle" fill="white" font-size="13">
Advertiser UI
</text>

  
<line x1="150" y1="325" x2="230" y2="325" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="240" y="300" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="310" y="330" text-anchor="middle" fill="white" font-size="12">
Query Service
</text>

  
<line x1="380" y1="320" x2="940" y2="135" stroke="#aaa" stroke-dasharray="5,5" marker-end="url(#ahc)">
</line>

  
<line x1="380" y1="330" x2="940" y2="55" stroke="#aaa" stroke-dasharray="5,5" marker-end="url(#ahc)">
</line>


</svg>

</details>


> **
**End-to-End Example:** 10B ad impressions served daily → Users click ~100M times → Click Collectors at CDN edge capture events → Kafka buffers with 3x replication → Three Flink jobs consume in parallel: (1) Dedup + aggregate into ClickHouse minute/hour/day tables + Redis real-time counters, (2) Fraud detection flags 3% of clicks, (3) Raw archival to S3 Parquet → Advertiser opens dashboard → Query Service pulls last hour from Redis + older data from ClickHouse → Shows campaign spent $45,230 today with 2.1% CTR, $12.40 CPC, 15K conversions


## 🗄️ Database Schema


### ClickHouse — OLAP (Analytical Queries)


ad_clicks_raw (minute-level)


| Column        | Type                   | Notes                               |
|---------------|------------------------|-------------------------------------|
| event_time    | DateTime               | `PK` Partition by date              |
| ad_id         | UInt64                 | `PK`                                |
| campaign_id   | UInt64                 | `PK`                                |
| advertiser_id | UInt64                 | `IDX`                               |
| region        | LowCardinality(String) | US-CA, EU-DE, etc.                  |
| device        | LowCardinality(String) | ios, android, desktop               |
| creative_id   | UInt64                 |                                     |
| clicks        | UInt64                 |                                     |
| impressions   | UInt64                 |                                     |
| spend         | Decimal64(4)           | Cost to advertiser for these clicks |
| conversions   | UInt32                 | Attributed conversions              |
| fraud_clicks  | UInt64                 | Flagged as fraudulent               |


**Engine:** `ReplicatedMergeTree() PARTITION BY toYYYYMMDD(event_time) ORDER BY (campaign_id, ad_id, event_time)`


**TTL:** Raw minute data: 90 days. Hourly aggregates: 2 years. Daily aggregates: forever.


### Redis — Real-Time Counters


# Per-minute click counter (auto-expires)
INCR   click:{ad_id}:{YYYYMMDDHHmm}          # click count
EXPIRE click:{ad_id}:{YYYYMMDDHHmm} 86400    # TTL 24 hours

# Per-campaign spend tracking (sorted set for top-N)
ZINCRBY campaign_spend:{campaign_id}:{date} {cost} {ad_id}

# HyperLogLog for unique users per campaign
PFADD unique_clickers:{campaign_id}:{date} {user_hash}
PFCOUNT unique_clickers:{campaign_id}:{date}   # ~0.81% error


### S3 Data Lake — Raw Events


s3://ad-clicks-lake/
  year=2024/month=02/day=09/hour=14/
    clicks_part-00000.parquet
    clicks_part-00001.parquet
    impressions_part-00000.parquet

Schema (Parquet):
- click_id: STRING (UUID)
- ad_id: INT64
- campaign_id: INT64
- user_id_hash: STRING
- ip_hash: STRING
- user_agent: STRING
- device_type: STRING
- geo_country: STRING
- geo_region: STRING
- referrer_url: STRING
- landing_url: STRING
- event_timestamp: TIMESTAMP
- fraud_score: FLOAT
- is_fraudulent: BOOLEAN


### Sharding Strategy


**ClickHouse:** Distributed table sharded by `campaign_id` hash across cluster nodes. Each shard has local MergeTree + replicas. Queries fan out to all shards (scatter-gather)  `SHARD`


**Kafka:** Partitioned by `ad_id` — ensures all clicks for same ad land on same partition → ordered processing  `SHARD`


**S3:** Partitioned by date/hour (Hive-style partitioning) — enables efficient time-range scans with partition pruning  `SHARD`


## 💾 Cache & CDN Deep Dive


### Redis as Speed Layer


  - **INCR counters:** Atomic increment for click counting — no race conditions, O(1) per operation

  - **Pipeline batching:** Flink batches Redis commands (100 INCR in single pipeline) to reduce round-trips

  - **Eviction:** `volatile-ttl` — all keys have TTL (24hr), Redis evicts closest-to-expire keys first when memory full

  - **Cluster:** Redis Cluster with hash slots — distributes counters across nodes. Counter key includes ad_id → even distribution


### CDN for Click Redirects


  - **Click endpoint at CDN edge:** `https://click.adnetwork.com/c/{ad_id}` served by Cloudflare Workers or AWS Lambda@Edge

  - **NOT cacheable:** Each click is unique event that must be logged. CDN provides geographic proximity, not caching

  - **Impression pixel:** 1x1 tracking pixel served from CDN. `<img src="https://t.adnetwork.com/i?ad_id=123&...">` — fires GET request, logged, returns transparent 1x1 GIF (43 bytes, heavily cached on CDN)


## ⚖️ Scaling Considerations


  - **Kafka scaling:** Add partitions for horizontal throughput. At 10B events/day, ~32-64 partitions per topic. Enable compression (lz4) to reduce network bandwidth 70%

  - **Flink scaling:** Parallelism matches Kafka partitions. Checkpoint interval tuned: shorter = less data reprocessed on failure, longer = better throughput. Backpressure monitoring to detect slow sinks

  - **ClickHouse scaling:** Add shards for write throughput, replicas for read throughput. ZooKeeper (or ClickHouse Keeper) for distributed coordination

  - **Hot campaigns:** Viral campaigns generate disproportionate traffic. Key-based partitioning can cause hot partitions. Mitigation: add random suffix to partition key for hot campaigns, aggregate at merge

  - **Super Bowl spike:** 10x normal traffic. Kafka partitions pre-scaled, Flink auto-scaling on Kubernetes, ClickHouse handles bursty writes via buffer tables

  - **Late-arriving events:** Mobile devices may send clicks with significant delay. Flink allowed lateness + side output for batch correction. Daily reconciliation job compares batch (S3) vs streaming (ClickHouse) counts


## ⚖️ Tradeoffs


> **
  
> **
    ✅ Lambda Architecture (Speed + Batch)
    

Real-time freshness for dashboards + batch accuracy for billing. Self-healing: batch layer corrects any streaming errors

  
  
> **
    ❌ Lambda Architecture
    

Dual pipeline complexity — maintaining Flink streaming + Spark batch with same logic is expensive. Kappa architecture (stream-only) is simpler alternative

  
  
> **
    ✅ ClickHouse for OLAP
    

Extremely fast analytical queries on columnar data, built-in materialized views, 10x compression, distributed queries

  
  
> **
    ❌ ClickHouse for OLAP
    

Not great for point lookups (single row), limited JOIN performance, UPDATE/DELETE expensive (async mutations), requires careful schema design

  
  
> **
    ✅ Bloom Filter Dedup
    

O(1) space-efficient dedup — 1% false positive rate with ~10 bits per element. Handles billions of click_ids with GB of memory

  
  
> **
    ❌ Bloom Filter Dedup
    

False positives mean ~1% of legitimate clicks discarded. Acceptable for aggregation but not for billing — billing uses exact dedup (Redis set or DB unique constraint)

  


## 🔄 Alternative Approaches


Kappa Architecture (Stream-Only)


Eliminate batch layer entirely. Use Kafka as the source of truth (with infinite retention) + Flink for all processing. Reprocess historical data by replaying Kafka topics. Simpler than Lambda, but requires Kafka to store potentially petabytes of raw events. Used by LinkedIn for metrics processing.


Apache Druid Instead of ClickHouse


Druid is designed for real-time analytics on event data. Built-in ingestion from Kafka, automatic segment management, bitmap indexes for fast filtering. Better at real-time ingestion; ClickHouse is better at ad-hoc SQL queries. Facebook uses Druid-based system for ads analytics.


Count-Min Sketch for Approximate Counting


Instead of exact counters, use Count-Min Sketch — probabilistic data structure that over-counts by ε with probability 1-δ. Dramatically reduces memory for high-cardinality dimensions. Suitable for non-billing metrics (trending, anomaly detection) but not for billing.


## 📚 Additional Information


### Exactly-Once Semantics


End-to-end exactly-once in streaming is achieved by: (1) Kafka idempotent producer (dedup at broker), (2) Flink checkpointing with Kafka consumer offsets (no reprocessing of already-committed data), (3) ClickHouse dedup via ReplacingMergeTree (if same primary key inserted twice, merge keeps latest). This chain ensures each click is counted exactly once.


### Attribution & Conversion Tracking


Click → conversion attribution: when user purchases after clicking ad, conversion pixel fires → attributed to last click within attribution window (7 or 30 days). Multi-touch attribution models (linear, time-decay, position-based) distribute credit across multiple ad touchpoints. Conversion data joins with click data in Flink via user_id.


### Budget Pacing


Advertisers set daily budgets (e.g., $1,000/day). Real-time spend tracked in Redis. When spend approaches budget, ad serving rate is throttled (pacing). Naive approach: stop serving when budget hit. Better: pace evenly throughout the day to avoid spending all budget at midnight. Implemented as feedback loop: check spend every minute, adjust bid multiplier to target uniform spend rate.


### MapReduce for Batch Reconciliation


Daily Spark job reads raw events from S3, computes exact aggregates, compares with ClickHouse streaming aggregates. Differences logged and corrected. This catches late events, failed checkpoints, or any streaming bugs. Billing is always based on reconciled batch numbers, not streaming approximations.