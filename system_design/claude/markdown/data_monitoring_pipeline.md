# System Design: Data Monitoring Pipeline


## Functional Requirements


- Collect metrics from thousands of services/hosts (CPU, memory, request latency, error rates, custom business metrics)

- Real-time anomaly detection and alerting (within 1-2 minutes of anomaly)

- Dashboard visualization with customizable charts and graphs

- Historical data querying for trend analysis (days, weeks, months)

- Alert configuration with thresholds, rate-of-change, and anomaly-based rules

- Alert routing and escalation (PagerDuty, Slack, email)


## Non-Functional Requirements


- **High Throughput:** Ingest millions of data points per second across all sources

- **Low Latency:** Anomaly detection within 60 seconds of metric emission

- **Scalability:** Horizontally scalable to handle 10x growth

- **Durability:** No metric data loss (critical for SLA calculations)

- **Query Performance:** Dashboard queries return in <2 seconds even for 30-day time ranges

- **Availability:** 99.99% — monitoring must be more reliable than what it monitors


## Flow 1: Metric Ingestion & Storage


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 420" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#00e676">
</polygon>
</marker>
</defs>


<rect x="20" y="80" width="110" height="40" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="75" y="105" text-anchor="middle" fill="white" font-size="11">
App Servers
</text>


<rect x="20" y="140" width="110" height="40" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="75" y="165" text-anchor="middle" fill="white" font-size="11">
Databases
</text>


<rect x="20" y="200" width="110" height="40" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="75" y="225" text-anchor="middle" fill="white" font-size="11">
Load Balancers
</text>


<rect x="20" y="260" width="110" height="40" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="75" y="285" text-anchor="middle" fill="white" font-size="11">
Custom Metrics
</text>


<rect x="180" y="160" width="130" height="60" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="245" y="188" text-anchor="middle" fill="white" font-size="12">
Metric Agents
</text>
<text x="245" y="205" text-anchor="middle" fill="white" font-size="10">
(StatsD/Telegraf)
</text>


<rect x="370" y="160" width="130" height="60" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="435" y="188" text-anchor="middle" fill="white" font-size="12">
Kafka
</text>
<text x="435" y="205" text-anchor="middle" fill="white" font-size="10">
(Metric Stream)
</text>


<rect x="560" y="80" width="140" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="630" y="108" text-anchor="middle" fill="white" font-size="11">
Ingestion Service
</text>
<text x="630" y="122" text-anchor="middle" fill="white" font-size="10">
(Pre-aggregation)
</text>


<rect x="560" y="200" width="140" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="630" y="228" text-anchor="middle" fill="white" font-size="11">
Anomaly Detector
</text>
<text x="630" y="242" text-anchor="middle" fill="white" font-size="10">
(Flink Streaming)
</text>


<rect x="560" y="310" width="140" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="630" y="340" text-anchor="middle" fill="white" font-size="12">
Downsampler
</text>


<ellipse cx="830" cy="105" rx="80" ry="30" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="830" y="100" text-anchor="middle" fill="white" font-size="11">
Time-Series DB
</text>
<text x="830" y="115" text-anchor="middle" fill="white" font-size="10">
(InfluxDB/VictoriaMetrics)
</text>


<rect x="790" y="200" width="130" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="855" y="230" text-anchor="middle" fill="white" font-size="12">
Alert Engine
</text>


<ellipse cx="830" cy="340" rx="70" ry="25" fill="#888" stroke="#aaa" stroke-width="2">
</ellipse>
<text x="830" y="345" text-anchor="middle" fill="white" font-size="11">
Cold Storage (S3)
</text>


<line x1="130" y1="100" x2="178" y2="180" stroke="#00e676" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="130" y1="160" x2="178" y2="185" stroke="#00e676" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="130" y1="220" x2="178" y2="200" stroke="#00e676" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="130" y1="280" x2="178" y2="210" stroke="#00e676" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="310" y1="190" x2="368" y2="190" stroke="#00e676" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="500" y1="178" x2="558" y2="108" stroke="#00e676" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="500" y1="195" x2="558" y2="225" stroke="#00e676" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="700" y1="105" x2="748" y2="105" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="700" y1="225" x2="788" y2="225" stroke="#ff6b6b" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="700" y1="120" x2="700" y2="308" stroke="#ff6b6b" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow1)">
</line>


<line x1="830" y1="135" x2="830" y2="313" stroke="#ff6b6b" stroke-width="1" stroke-dasharray="5,5" marker-end="url(#arrow1)">
</line>


<text x="550" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Data Monitoring: Metric Ingestion Flow
</text>


</svg>

</details>


### Step-by-Step


1. **Metric Agents** (StatsD/Telegraf) running on each host collect system metrics (CPU, memory, disk, network) and custom application metrics every 10 seconds

2. **Agents → Kafka:** Metrics pushed to Kafka topics partitioned by metric_name (ensures same metric goes to same partition for ordering)

3. **Ingestion Service:** Consumes from Kafka, pre-aggregates (e.g., 100 raw data points in 10s window → 1 aggregated point with min/max/avg/p99), writes to Time-Series DB

4. **Anomaly Detector (Flink):** Processes raw metric stream in real-time. Applies statistical models (Z-score, EWMA, STL decomposition) to detect anomalies

5. **Downsampler:** Background job that compresses old data: raw (10s) → 1min → 5min → 1hr → 1day. Reduces storage 100x for data >30 days old

6. **Cold Storage:** Data older than 90 days archived to S3 (Parquet format) for long-term retention and compliance


> **
**Example:** 10,000 app servers each emit 50 metrics every 10 seconds = 50K data points/second. Agents batch and send to Kafka. Ingestion Service pre-aggregates: for "api.latency.p99" across 10K servers, computes regional aggregates (us-east: 45ms, eu-west: 120ms). Writes 2 aggregated data points instead of 10K raw ones. Anomaly Detector sees eu-west p99 jump from 120ms baseline to 850ms → triggers anomaly alert within 30 seconds.


### Deep Dive: Ingestion Service


- **Protocol:** Kafka consumer (pull-based). Alternative: Prometheus pull model (HTTP scrape)

- **Pre-aggregation:** Groups metrics by (metric_name, tags) in 10-second tumbling windows. Computes: count, sum, min, max, avg, p50, p95, p99

- **Write path:** Batch writes to Time-Series DB (1000 data points per write for efficiency)

- **Backpressure:** If TSDB is slow, Kafka acts as buffer (hours of data). Ingestion rate can temporarily exceed write rate


### Deep Dive: Time-Series Database


- **Choice:** InfluxDB, VictoriaMetrics, or Prometheus TSDB — optimized for time-series writes and range queries

- **Data model:** `metric_name{tag1=v1, tag2=v2} value timestamp`

- **Compression:** Gorilla encoding (XOR delta for timestamps, delta-of-delta for values). 12:1 compression ratio typical

- **Retention policies:** Raw (10s): 7 days, 1min: 30 days, 5min: 90 days, 1hr: 1 year, 1day: forever


## Flow 2: Alerting & Anomaly Detection


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ff6b6b">
</polygon>
</marker>
</defs>


<rect x="20" y="140" width="130" height="60" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="85" y="168" text-anchor="middle" fill="white" font-size="12">
Kafka Stream
</text>
<text x="85" y="185" text-anchor="middle" fill="white" font-size="10">
(Raw Metrics)
</text>


<rect x="200" y="140" width="140" height="60" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="270" y="165" text-anchor="middle" fill="white" font-size="11">
Anomaly Detector
</text>
<text x="270" y="182" text-anchor="middle" fill="white" font-size="10">
(Flink + ML)
</text>


<rect x="400" y="80" width="130" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="465" y="110" text-anchor="middle" fill="white" font-size="12">
Alert Engine
</text>


<rect x="400" y="200" width="130" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="465" y="228" text-anchor="middle" fill="white" font-size="11">
Deduplication
</text>
<text x="465" y="242" text-anchor="middle" fill="white" font-size="10">
& Grouping
</text>


<rect x="600" y="60" width="130" height="40" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="665" y="85" text-anchor="middle" fill="white" font-size="11">
PagerDuty
</text>


<rect x="600" y="120" width="130" height="40" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="665" y="145" text-anchor="middle" fill="white" font-size="11">
Slack
</text>


<rect x="600" y="180" width="130" height="40" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="665" y="205" text-anchor="middle" fill="white" font-size="11">
Email
</text>


<rect x="600" y="240" width="130" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="665" y="265" text-anchor="middle" fill="white" font-size="11">
Webhook
</text>


<ellipse cx="465" cy="320" rx="70" ry="25" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="465" y="325" text-anchor="middle" fill="white" font-size="11">
Alert State DB
</text>


<line x1="150" y1="170" x2="198" y2="170" stroke="#ff6b6b" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="340" y1="160" x2="398" y2="110" stroke="#ff6b6b" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="530" y1="105" x2="530" y2="200" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="530" y1="220" x2="598" y2="80" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="530" y1="225" x2="598" y2="140" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="530" y1="230" x2="598" y2="200" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="530" y1="235" x2="598" y2="260" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="465" y1="130" x2="465" y2="293" stroke="#ff6b6b" stroke-width="1" stroke-dasharray="5,5" marker-end="url(#arrow2)">
</line>


<text x="480" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Data Monitoring: Alerting Flow
</text>


</svg>

</details>


### Alert Rule Types


- **Threshold:** `IF api.error_rate > 5% FOR 5 minutes THEN alert`

- **Rate of change:** `IF cpu_usage increases > 30% in 10 minutes THEN alert`

- **Anomaly-based:** ML model detects deviation from historical pattern (seasonal decomposition)

- **Composite:** `IF error_rate > 3% AND latency_p99 > 500ms THEN alert`


### Deep Dive: Deduplication & Grouping


- **Problem:** Same issue may trigger 1000 alerts from 1000 hosts simultaneously

- **Solution:** Group by (alert_rule, service_name, region). If 50 hosts trigger same alert → 1 grouped notification: "api-service: error_rate > 5% on 50/100 hosts in us-east-1"

- **Dedup window:** Same alert not re-sent within 15 minutes (configurable silence window)

- **Escalation:** If not acknowledged in 10 min → escalate to team lead. 30 min → escalate to VP. Configurable per alert severity


> **
**Example:** Database connection pool exhausted on 20 app servers. Each server emits `db.connections.available = 0`. Anomaly Detector flags all 20. Alert Engine groups them: "Critical: db.connections.available = 0 on 20/50 hosts in api-service (us-east-1)". Dedup suppresses for 15min. Alert routed: P1 → PagerDuty (on-call engineer) + Slack #incidents. Not acknowledged in 10min → escalated to team lead via PagerDuty.


## Combined Overall Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 550" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#00e676">
</polygon>
</marker>
</defs>


<text x="600" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Data Monitoring Pipeline: Combined Architecture
</text>


<rect x="20" y="200" width="100" height="130" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="70" y="240" text-anchor="middle" fill="white" font-size="10">
Metric
</text>
<text x="70" y="255" text-anchor="middle" fill="white" font-size="10">
Sources
</text>
<text x="70" y="280" text-anchor="middle" fill="white" font-size="9">
(Hosts, DBs,
</text>
<text x="70" y="295" text-anchor="middle" fill="white" font-size="9">
LBs, Apps)
</text>


<rect x="160" y="230" width="110" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="215" y="258" text-anchor="middle" fill="white" font-size="11">
Agents
</text>


<rect x="320" y="230" width="120" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="380" y="258" text-anchor="middle" fill="white" font-size="11">
Kafka
</text>


<rect x="500" y="100" width="130" height="45" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="565" y="127" text-anchor="middle" fill="white" font-size="11">
Ingestion Svc
</text>


<rect x="500" y="180" width="130" height="45" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="565" y="207" text-anchor="middle" fill="white" font-size="11">
Anomaly (Flink)
</text>


<rect x="500" y="260" width="130" height="45" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="565" y="287" text-anchor="middle" fill="white" font-size="11">
Alert Engine
</text>


<rect x="500" y="340" width="130" height="45" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="565" y="367" text-anchor="middle" fill="white" font-size="11">
Downsampler
</text>


<rect x="500" y="430" width="130" height="45" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="565" y="457" text-anchor="middle" fill="white" font-size="11">
Query Engine
</text>


<ellipse cx="760" cy="120" rx="75" ry="28" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="760" y="118" text-anchor="middle" fill="white" font-size="10">
Time-Series DB
</text>
<text x="760" y="132" text-anchor="middle" fill="white" font-size="9">
(Hot: 7 days)
</text>


<ellipse cx="760" cy="210" rx="65" ry="25" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="760" y="215" text-anchor="middle" fill="white" font-size="10">
Warm (30 days)
</text>


<ellipse cx="760" cy="300" rx="60" ry="25" fill="#888" stroke="#aaa" stroke-width="2">
</ellipse>
<text x="760" y="305" text-anchor="middle" fill="white" font-size="10">
Cold (S3)
</text>


<rect x="760" cy="350" y="380" width="120" height="40" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="820" y="405" text-anchor="middle" fill="white" font-size="11">
Alert State DB
</text>


<rect x="920" y="100" width="120" height="45" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="980" y="127" text-anchor="middle" fill="white" font-size="11">
PagerDuty
</text>


<rect x="920" y="180" width="120" height="45" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="980" y="207" text-anchor="middle" fill="white" font-size="11">
Slack
</text>


<rect x="920" y="260" width="120" height="45" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="980" y="287" text-anchor="middle" fill="white" font-size="11">
Dashboard UI
</text>


<line x1="120" y1="255" x2="158" y2="255" stroke="#00e676" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="255" x2="318" y2="255" stroke="#00e676" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="440" y1="242" x2="498" y2="122" stroke="#00e676" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="440" y1="255" x2="498" y2="202" stroke="#00e676" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="122" x2="683" y2="118" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="202" x2="498" y2="282" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="362" x2="683" y2="210" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="760" y1="235" x2="760" y2="273" stroke="#ffaa00" stroke-width="1" stroke-dasharray="4,4" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="282" x2="758" y2="380" stroke="#ff6b6b" stroke-width="1" stroke-dasharray="4,4" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="270" x2="918" y2="122" stroke="#ff6b6b" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="282" x2="918" y2="200" stroke="#ff6b6b" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="630" y1="452" x2="918" y2="282" stroke="#00e676" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


</svg>

</details>


> **
**Example — Full flow:** 10K hosts emit CPU, memory, latency metrics → Agents batch → Kafka → Ingestion Service pre-aggregates into 1-min windows → writes to TSDB (hot tier). Flink detects api.latency.p99 anomaly in eu-west → Alert Engine evaluates rules → groups 50 host alerts → dedup check (not recently alerted) → routes to PagerDuty (P1) + Slack. On-call engineer opens Dashboard UI → Query Engine reads from TSDB → shows latency spike graph → engineer identifies root cause (database connection pool exhaustion) → deploys fix → metrics return to normal → alert auto-resolves.


## Database Schema


### Time-Series DB — InfluxDB / VictoriaMetrics


| Measurement | Tags                                                      | Fields                              | Timestamp            |
|-------------|-----------------------------------------------------------|-------------------------------------|----------------------|
| api_latency | service=api, host=web-01, region=us-east, endpoint=/users | p50=12, p95=45, p99=120, count=1500 | 2024-01-15T10:00:00Z |
| cpu_usage   | host=web-01, core=all                                     | percent=78.5                        | 2024-01-15T10:00:00Z |
| error_rate  | service=api, error_code=500                               | count=23, rate=0.015                | 2024-01-15T10:00:00Z |


### SQL — PostgreSQL (Alert Configuration, State)


| Table                 | Column       | Type                                          | Details |
|-----------------------|--------------|-----------------------------------------------|---------|
| **alert_rules**       | rule_id      | SERIAL                                        | `PK`    |
| name                  | VARCHAR(255) |                                               |         |
| metric_query          | TEXT         | PromQL-like expression                        |         |
| condition             | JSONB        | {operator: ">", threshold: 5, duration: "5m"} |         |
| severity              | ENUM         | P1, P2, P3, P4                                |         |
| notification_channels | JSONB        | ["pagerduty:team-a", "slack:#alerts"]         |         |
| **alert_incidents**   | incident_id  | BIGSERIAL                                     | `PK`    |
| rule_id               | INT          | `IDX`                                         |         |
| status                | ENUM         | firing, acknowledged, resolved                |         |
| started_at            | TIMESTAMP    | `IDX`                                         |         |
| resolved_at           | TIMESTAMP    |                                               |         |


Sharding Strategy


- **TSDB:** Sharded by metric_name hash. All data points for "api_latency" on same shard. Enables efficient time-range queries per metric

- **Alternative: shard by time range.** Each shard holds 1 day of data. Enables efficient data lifecycle management (drop entire shard when expired). Used by VictoriaMetrics

- **Alert State DB:** Single PostgreSQL instance (low write volume — only state changes). Read replica for dashboard


### Indexes


- TSDB inverted index: tag → time series mapping. E.g., `service=api` → [series_1, series_2, ...]. Enables fast filtering by any tag combination

- `alert_incidents.started_at` — B-tree for "recent incidents" dashboard queries

- `alert_incidents.rule_id` — For "history of this alert" queries


## Cache Deep Dive


| Cache                         | Strategy                  | Eviction              | TTL        | Purpose                                                            |
|-------------------------------|---------------------------|-----------------------|------------|--------------------------------------------------------------------|
| Dashboard Query Cache (Redis) | Read-through              | LRU                   | 30 seconds | Cache frequently-viewed dashboard queries. Short TTL for freshness |
| TSDB Block Cache              | Built-in (LRU page cache) | LRU                   | N/A        | Recently queried time-series blocks kept in memory                 |
| Alert Rule Cache (Redis)      | Write-through             | Invalidated on update | None       | Alert rules loaded into memory for Flink evaluation                |
| Tag Index Cache (Redis)       | Read-through              | LRU                   | 5 min      | Metric tag → series mapping for dashboard autocomplete             |


### No CDN


- Monitoring data is internal — no public CDN needed. Dashboard served from internal load balancer

- Static dashboard assets (JS/CSS) can be cached on internal CDN or served directly


## Scaling Considerations


- **Kafka:** Partition by metric_name for parallelism. Add partitions + consumers to scale ingestion throughput

- **TSDB horizontal scaling:** Add shards for more metrics. Each shard handles a subset of metric names. Query Engine does scatter-gather for cross-metric queries

- **Flink auto-scaling:** Scale anomaly detection parallelism based on Kafka lag. If lag > threshold, add Flink task slots

- **Query Engine:** Stateless — scale horizontally behind load balancer. Caches recently queried data in Redis to reduce TSDB load

- **Cardinality explosion:** High-cardinality tags (e.g., user_id) explode the number of time series. Prevent by limiting allowed tag values and using sampling for high-cardinality dimensions


## Tradeoffs & Deep Dives


> **
✅ Push-based (StatsD/Kafka)
- Applications push when ready — no coordination
- Works across firewalls (outbound only)
- Kafka buffers during ingestion spikes


❌ Alternative: Pull-based (Prometheus)
- Simpler — Prometheus scrapes targets via HTTP
- Easier to detect dead targets (scrape fails)
- But: doesn't scale as well for 100K+ targets, pull interval limits freshness


### Message Queue (Kafka) Deep Dive


- **Why Kafka:** Decouples metric producers (agents) from consumers (ingestion, anomaly detection, alerting). Handles burst traffic. Provides replay capability for reprocessing

- **Partitioning:** By metric_name ensures all data points for a metric arrive at the same consumer in order (important for anomaly detection)

- **Retention:** 24 hours (enough for reprocessing after ingestion failures)


## Alternative Approaches


- **Prometheus + Thanos:** Prometheus for short-term (2 weeks) with Thanos for long-term S3-backed storage. Pull-based model. Good for Kubernetes-native environments but pull model limits scale

- **OpenTelemetry + ClickHouse:** OTLP for collection, ClickHouse (columnar DB) for storage. Excellent query performance for wide time ranges. ClickHouse handles metrics + logs + traces in one system

- **Lambda architecture:** Batch layer (Spark on S3) + speed layer (Flink on Kafka). Batch provides accuracy, speed provides freshness. More complex but handles late-arriving data better


## Additional Information


- **Monitoring the monitor:** The monitoring system itself needs monitoring. Use a separate lightweight system (e.g., simple healthcheck pings + external service like Pingdom) to monitor the monitoring pipeline

- **SLI/SLO calculation:** Query Engine can compute SLIs (e.g., availability = 1 - error_rate) over time windows and compare against SLO targets (e.g., 99.9%). Automated SLO burn rate alerts

- **Log correlation:** Metric anomaly → link to relevant log entries by timestamp + service + host. Enables root cause analysis without manual log searching

- **Cost optimization:** Metric data is expensive at scale. Strategies: drop unused metrics after 30 days of no queries, aggregate early, sample high-frequency metrics