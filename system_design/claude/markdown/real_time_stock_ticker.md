# System Design: Real-Time Stock Ticker


## Functional Requirements


  1. **Real-time price updates** – Display live stock prices with sub-second updates.

  2. **Watchlist** – Users can add/remove stocks to their personal watchlist and see live prices.

  3. **Historical data** – View price charts (1D, 1W, 1M, 1Y, 5Y timeframes).

  4. **Search** – Search for stocks by ticker symbol or company name.

  5. **Market summary** – Display major indices (S&P 500, NASDAQ, DOW) in real time.

  6. **Price alerts** – Users set alerts when a stock crosses a price threshold.


## Non-Functional Requirements


  1. **Ultra-low latency** – Price updates delivered to clients within 100ms of exchange data.

  2. **High throughput** – Handle 100K+ price updates/second from exchanges during market hours.

  3. **High availability** – 99.99%; market data must be continuously available during trading hours.

  4. **Scalability** – Support 10M+ concurrent users during market hours.

  5. **Consistency** – All users should see the same price for the same stock at any given moment.


## Flow 1: Ingesting Real-Time Market Data


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 320" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a1" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="120" width="100" height="55" rx="8" fill="#795548" stroke="#4e342e" stroke-width="2">
</rect>

  
<text x="60" y="145" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Stock
</text>

  
<text x="60" y="160" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Exchanges
</text>

  
<line x1="110" y1="147" x2="180" y2="147" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<text x="145" y="138" text-anchor="middle" fill="#555" font-size="8">
TCP/FIX
</text>

  
<rect x="180" y="120" width="110" height="55" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="235" y="145" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Market Data
</text>

  
<text x="235" y="160" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Ingester
</text>

  
<line x1="290" y1="147" x2="370" y2="147" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="370" y="120" width="100" height="55" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="420" y="145" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Price
</text>

  
<text x="420" y="160" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Stream
</text>

  
<text x="420" y="187" text-anchor="middle" fill="#555" font-size="9">
(Kafka)
</text>

  

  
<line x1="470" y1="140" x2="550" y2="90" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="600" cy="80" rx="45" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="555" y="80" width="90" height="22" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="600" cy="102" rx="45" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="555" y1="80" x2="555" y2="102" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="645" y1="80" x2="645" y2="102" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="600" y="95" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Price Cache
</text>

  
<text x="600" y="117" text-anchor="middle" fill="#555" font-size="8">
(Redis)
</text>

  

  
<line x1="470" y1="155" x2="550" y2="200" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="600" cy="195" rx="50" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="550" y="195" width="100" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="600" cy="220" rx="50" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="550" y1="195" x2="550" y2="220" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="650" y1="195" x2="650" y2="220" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="600" y="212" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Time-Series DB
</text>

  
<text x="600" y="237" text-anchor="middle" fill="#555" font-size="8">
(InfluxDB)
</text>

  

  
<line x1="470" y1="147" x2="550" y2="147" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="550" y="128" width="110" height="45" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="605" y="148" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
WebSocket
</text>

  
<text x="605" y="163" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Hub
</text>

  

  
<line x1="660" y1="140" x2="740" y2="100" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<line x1="660" y1="150" x2="740" y2="150" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<line x1="660" y1="160" x2="740" y2="200" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="740" y="80" width="80" height="35" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="780" y="102" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Client 1
</text>

  
<rect x="740" y="133" width="80" height="35" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="780" y="155" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Client 2
</text>

  
<rect x="740" y="185" width="80" height="35" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="780" y="207" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Client N
</text>

  

  
<line x1="470" y1="165" x2="550" y2="270" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a1)">
</line>

  
<rect x="550" y="255" width="110" height="40" rx="6" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="605" y="279" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Alert Service
</text>


</svg>

</details>


> **
Example: Live AAPL price update


The NYSE executes a trade for AAPL at $178.52. The exchange sends a market data message via **TCP/FIX protocol** to the **Market Data Ingester**. The Ingester normalizes the data into `{symbol: "AAPL", price: 178.52, volume: 1500, timestamp: 1707500400123}` and publishes it to the **Price Stream** (Kafka topic: `prices`, partitioned by symbol). Three consumers process in parallel: (1) **Price Cache** (Redis) updates the latest price for AAPL. (2) **Time-Series DB** (InfluxDB) stores the tick for historical charts. (3) **WebSocket Hub** pushes the price to all clients subscribed to AAPL via WebSocket — users see the update in <100ms.


> **
Example: Price alert triggered


Alice set an alert: "Notify me when TSLA crosses $250." The **Alert Service** consumes price events from Kafka. When TSLA's price moves from $249.50 to $250.25, the Alert Service detects the threshold crossing and sends a push notification to Alice: "TSLA just crossed $250.00 — currently at $250.25."


### Deep Dive: Components


> **
WebSocket Hub


Manages millions of concurrent WebSocket connections. Each client subscribes to specific ticker symbols. The Hub maintains a **subscription map:** `symbol → [list of WebSocket connections]`. When a price update arrives for AAPL, the Hub looks up all connections subscribed to AAPL and pushes the update.


**Why WebSocket:** Stock prices update many times per second. WebSocket provides persistent, low-latency, bidirectional communication. Alternatives:


  - **SSE (Server-Sent Events):** Unidirectional (server→client), simpler than WebSocket. Viable for a read-only ticker but doesn't support client→server messages (subscribe/unsubscribe). Could work with a separate REST endpoint for subscriptions.

  - **Polling:** Far too slow and wasteful for real-time prices. A 1-second poll interval means up to 1 second of stale data.


**Connection management:** The WebSocket connection is established via HTTP Upgrade. The Hub stores connection state in memory. A **Redis Pub/Sub** layer distributes price updates across multiple Hub instances — when a price arrives, it's published to a Redis channel; all Hub instances subscribed to that channel receive it and push to their local connections.


> **
Market Data Ingester


Connects to stock exchanges via **TCP with FIX protocol** (Financial Information eXchange — the industry standard for market data). Runs as a dedicated, highly available service with failover. Normalizes data from different exchanges into a common schema and publishes to Kafka.


**Why TCP:** FIX runs over TCP for reliable, ordered delivery. UDP would be faster but risks packet loss — unacceptable for financial data.


> **
Kafka (Price Stream)


Partitioned by `symbol` to maintain ordering per stock. With ~10,000 ticker symbols, we use ~1,000 partitions (10 symbols per partition). Provides durability, replay capability, and decouples ingestion from consumers.


## Flow 2: Viewing Historical Charts


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 700 200" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a2" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="70" width="80" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="50" y="100" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Client
</text>

  
<line x1="90" y1="95" x2="160" y2="95" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<text x="125" y="85" text-anchor="middle" fill="#555" font-size="8">
GET /chart
</text>

  
<rect x="160" y="70" width="100" height="50" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="210" y="100" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Chart Svc
</text>

  
<line x1="260" y1="85" x2="340" y2="45" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<ellipse cx="390" cy="35" rx="40" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="350" y="35" width="80" height="20" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="390" cy="55" rx="40" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="350" y1="35" x2="350" y2="55" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="430" y1="35" x2="430" y2="55" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="390" y="49" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Cache
</text>

  
<line x1="260" y1="105" x2="340" y2="140" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<ellipse cx="390" cy="135" rx="50" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="340" y="135" width="100" height="22" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="390" cy="157" rx="50" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="340" y1="135" x2="340" y2="157" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="440" y1="135" x2="440" y2="157" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="390" y="150" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
InfluxDB
</text>


</svg>

</details>


> **
Example: Viewing AAPL 1-month chart


Bob selects AAPL and taps "1M" chart view. Client sends **GET /api/v1/chart?symbol=AAPL&range=1M&interval=1h**. The Chart Service checks Redis cache (key: `chart:AAPL:1M:1h`). On miss, queries InfluxDB for hourly OHLCV data for the past 30 days. Returns aggregated data points. Populates cache with TTL = 5 minutes (intraday charts update frequently).


## Overall Combined Flow


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a3" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="5" y="140" width="80" height="40" rx="6" fill="#795548" stroke="#4e342e" stroke-width="1.5">
</rect>

  
<text x="45" y="164" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Exchanges
</text>

  
<line x1="85" y1="160" x2="130" y2="160" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="130" y="140" width="80" height="40" rx="6" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="170" y="164" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Ingester
</text>

  
<line x1="210" y1="160" x2="260" y2="160" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="260" y="140" width="70" height="40" rx="6" fill="#9c27b0" stroke="#6a1b9a" stroke-width="1.5">
</rect>

  
<text x="295" y="164" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Kafka
</text>

  
<line x1="330" y1="150" x2="380" y2="85" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<line x1="330" y1="160" x2="380" y2="160" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<line x1="330" y1="170" x2="380" y2="230" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<line x1="330" y1="175" x2="380" y2="290" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a3)">
</line>

  
<ellipse cx="420" cy="75" rx="35" ry="8" fill="#009688" stroke="#00695c" stroke-width="1">
</ellipse>

  
<rect x="385" y="75" width="70" height="16" fill="#009688" stroke="#00695c" stroke-width="1">
</rect>

  
<ellipse cx="420" cy="91" rx="35" ry="8" fill="#00796b" stroke="#00695c" stroke-width="1">
</ellipse>

  
<line x1="385" y1="75" x2="385" y2="91" stroke="#00695c" stroke-width="1">
</line>

  
<line x1="455" y1="75" x2="455" y2="91" stroke="#00695c" stroke-width="1">
</line>

  
<text x="420" y="87" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
Redis
</text>

  
<rect x="380" y="140" width="90" height="40" rx="6" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="425" y="164" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
WS Hub
</text>

  
<line x1="470" y1="160" x2="530" y2="160" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="530" y="140" width="80" height="40" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="570" y="164" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Clients
</text>

  
<ellipse cx="420" cy="225" rx="40" ry="8" fill="#ff5722" stroke="#d84315" stroke-width="1">
</ellipse>

  
<rect x="380" y="225" width="80" height="16" fill="#ff5722" stroke="#d84315" stroke-width="1">
</rect>

  
<ellipse cx="420" cy="241" rx="40" ry="8" fill="#e64a19" stroke="#d84315" stroke-width="1">
</ellipse>

  
<line x1="380" y1="225" x2="380" y2="241" stroke="#d84315" stroke-width="1">
</line>

  
<line x1="460" y1="225" x2="460" y2="241" stroke="#d84315" stroke-width="1">
</line>

  
<text x="420" y="237" text-anchor="middle" fill="#fff" font-size="7" font-weight="bold">
InfluxDB
</text>

  
<rect x="380" y="280" width="90" height="35" rx="6" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="425" y="302" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Alert Svc
</text>


</svg>

</details>


> **
Combined Example


Exchanges → TCP/FIX → Ingester normalizes → Kafka (per-symbol partitions) → three parallel consumers: (1) Redis updates latest price, (2) InfluxDB stores tick for charts, (3) WebSocket Hub pushes to subscribed clients in real time, (4) Alert Service checks price thresholds. Client requests historical chart → Chart Svc queries cache/InfluxDB → returns OHLCV data.


## Database Schema


### Time-Series DB (InfluxDB)

1. price_ticks

  
  
  
  
  
  
  
  


| Field    | Type           | Description                          |
|----------|----------------|--------------------------------------|
| time     | TIMESTAMP (ns) | Nanosecond-precision timestamp  `PK` |
| symbol   | TAG            | Ticker symbol (indexed)  `PK`        |
| exchange | TAG            | Exchange (NYSE, NASDAQ)              |
| price    | FLOAT          | Trade price                          |
| volume   | INT            | Trade volume                         |
| bid      | FLOAT          | Best bid price                       |
| ask      | FLOAT          | Best ask price                       |


**Why Time-Series DB:** Stock tick data is append-only, time-indexed, and requires time-range aggregations (OHLCV candles). InfluxDB is purpose-built for this: columnar storage, built-in downsampling (continuous queries), and time-based retention policies (keep tick data for 30 days, daily aggregates forever).


### SQL (PostgreSQL)

2. watchlists

  
  
  
  


| Field    | Type        | Key  | Description   |
|----------|-------------|------|---------------|
| user_id  | BIGINT      | `PK` | User          |
| symbol   | VARCHAR(10) | `PK` | Ticker symbol |
| added_at | TIMESTAMP   |      | When added    |


3. price_alerts

  
  
  
  
  
  
  


| Field        | Type                  | Key   | Description             |
|--------------|-----------------------|-------|-------------------------|
| alert_id     | BIGINT                | `PK`  | Alert ID                |
| user_id      | BIGINT                | `IDX` | User                    |
| symbol       | VARCHAR(10)           | `IDX` | Ticker                  |
| target_price | DECIMAL               |       | Alert threshold         |
| direction    | ENUM('above','below') |       | Trigger direction       |
| is_active    | BOOLEAN               |       | Whether alert is active |


## Cache Deep Dive


> **
Price Cache (Redis)


**Purpose:** Store the latest price for every active symbol. Key: `price:{symbol}`. Value: JSON `{price, volume, change, changePercent, timestamp}`.


**Strategy:** **Write-through** — updated on every price tick from Kafka.


**Eviction:** None needed (only ~10K symbols, ~1MB total). **Expiration:** No TTL during market hours. After hours, TTL = 12 hours (stale data is acceptable).


> **
CDN


CDN is **not appropriate for real-time price data** (it changes every second). CDN **is appropriate** for: static assets (JS, CSS, company logos), historical chart data (cacheable for 5 min), and company info pages.


## Scaling Considerations


  - **WebSocket Hub:** Each instance holds ~500K connections. With 10M concurrent users, need ~20 instances behind an L4 load balancer (TCP sticky sessions).

  - **Redis Pub/Sub:** Distributes price updates across all WebSocket Hub instances. ~10K messages/sec (one per symbol per tick).

  - **Kafka:** Handles 100K+ messages/sec with appropriate partitioning.

  - **InfluxDB:** Clustered with sharding by symbol range for write distribution.


## Tradeoffs and Deep Dives


> **
WebSocket vs. SSE


WebSocket is chosen for bidirectional communication (client sends subscribe/unsubscribe). SSE would work for one-way price push but requires a separate channel for subscriptions. WebSocket is more efficient for this use case.


> **
Kafka vs. Direct Push


Kafka adds ~5ms latency compared to direct push from ingester to WebSocket Hub. The benefit is decoupling, durability, and ability to replay data. For ultra-low-latency trading systems (not a ticker app), direct UDP multicast would be used instead.


## Alternative Approaches


UDP Multicast (for HFT systems)


Financial exchanges use UDP multicast for the lowest latency (~microseconds). Not chosen for a consumer app because: (1) requires specialized network infrastructure, (2) no delivery guarantees, (3) our target latency (<100ms) is achievable with TCP/Kafka/WebSocket.


## Additional Information


### Data Downsampling


Tick-by-tick data generates TB/day. InfluxDB continuous queries aggregate ticks into 1-minute, 5-minute, 1-hour, and 1-day OHLCV candles. Tick data is retained for 30 days; minute data for 1 year; daily data forever.