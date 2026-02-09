# System Design: Weather.com


## Functional Requirements


  1. **Current weather** – Display current temperature, humidity, wind, visibility, UV index for any location.

  2. **Forecasts** – Hourly (48hr), daily (10-day), and extended forecasts.

  3. **Radar/maps** – Interactive weather radar, precipitation, temperature, and satellite maps.

  4. **Severe weather alerts** – Push notifications for tornadoes, hurricanes, flood warnings.

  5. **Location-based** – Auto-detect user location or search by city/zip code.

  6. **Historical data** – Past weather data for any date/location.

  7. **Air quality index (AQI)** – Display air quality and pollen counts.


## Non-Functional Requirements


  1. **High availability** – 99.99%; weather data is critical during severe events.

  2. **Low latency** – Weather page loads in <300ms.

  3. **High cache hit rate** – Weather data changes slowly (~15 min intervals); ideal for aggressive caching.

  4. **Scalability** – Handle traffic spikes during severe weather (10x normal).

  5. **Global coverage** – Serve weather for every location on Earth.


## Flow 1: Fetching Current Weather


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 300" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a1" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="110" width="80" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="50" y="140" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
User
</text>

  
<line x1="90" y1="135" x2="160" y2="135" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<text x="125" y="125" text-anchor="middle" fill="#555" font-size="8">
GET /weather
</text>

  
<rect x="160" y="110" width="70" height="50" rx="8" fill="#607d8b" stroke="#37474f" stroke-width="2">
</rect>

  
<text x="195" y="140" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
CDN
</text>

  
<line x1="230" y1="135" x2="300" y2="135" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<text x="265" y="125" text-anchor="middle" fill="#555" font-size="8">
Miss
</text>

  
<rect x="300" y="110" width="80" height="50" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="340" y="133" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
API
</text>

  
<text x="340" y="145" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
GW
</text>

  
<line x1="380" y1="135" x2="450" y2="135" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="450" y="108" width="110" height="55" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="505" y="133" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Weather
</text>

  
<text x="505" y="148" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Service
</text>

  

  
<line x1="560" y1="128" x2="630" y2="80" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<text x="610" y="93" text-anchor="middle" fill="#555" font-size="8">
1. Cache?
</text>

  
<ellipse cx="685" cy="70" rx="45" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="640" y="70" width="90" height="22" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="685" cy="92" rx="45" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="640" y1="70" x2="640" y2="92" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="730" y1="70" x2="730" y2="92" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="685" y="85" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Redis Cache
</text>

  

  
<line x1="560" y1="142" x2="630" y2="185" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<text x="610" y="175" text-anchor="middle" fill="#555" font-size="8">
2. DB
</text>

  
<ellipse cx="685" cy="180" rx="45" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="640" y="180" width="90" height="22" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="685" cy="202" rx="45" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="640" y1="180" x2="640" y2="202" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="730" y1="180" x2="730" y2="202" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="685" y="195" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Weather DB
</text>


</svg>

</details>


> **
Example: Checking weather for NYC


User visits weather.com/nyc. The request first hits the **CDN** (CloudFront/Akamai). If the CDN has a cached response for NYC weather (TTL = 10 minutes), it returns immediately. On CDN miss, the request goes to the **API Gateway** → **Weather Service**. The service checks **Redis Cache** (key: `weather:40.71:-74.01` — lat/lng grid). Cache hit → return. Cache miss → query **Weather DB** (PostgreSQL) for the latest weather data ingested for that grid cell. Populates cache with TTL = 10 min. Returns `{temp: 42°F, humidity: 65%, wind: "12 mph NW", condition: "Partly Cloudy", ...}`.


## Flow 2: Ingesting Weather Data from Sources


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a2" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="60" width="100" height="40" rx="6" fill="#795548" stroke="#4e342e" stroke-width="1.5">
</rect>

  
<text x="60" y="84" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
NOAA / NWS
</text>

  
<rect x="10" y="110" width="100" height="40" rx="6" fill="#795548" stroke="#4e342e" stroke-width="1.5">
</rect>

  
<text x="60" y="134" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Weather Stns
</text>

  
<rect x="10" y="160" width="100" height="40" rx="6" fill="#795548" stroke="#4e342e" stroke-width="1.5">
</rect>

  
<text x="60" y="184" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Satellites
</text>

  
<line x1="110" y1="80" x2="180" y2="130" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<line x1="110" y1="130" x2="180" y2="130" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<line x1="110" y1="180" x2="180" y2="135" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<rect x="180" y="108" width="110" height="50" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="235" y="130" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Data Ingestion
</text>

  
<text x="235" y="145" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Pipeline
</text>

  
<line x1="290" y1="133" x2="360" y2="133" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<rect x="360" y="110" width="100" height="50" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="410" y="133" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Processing
</text>

  
<text x="410" y="148" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
& Forecast
</text>

  
<line x1="460" y1="125" x2="530" y2="80" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<ellipse cx="580" cy="70" rx="40" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="540" y="70" width="80" height="22" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="580" cy="92" rx="40" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="540" y1="70" x2="540" y2="92" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="620" y1="70" x2="620" y2="92" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="580" y="85" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Weather DB
</text>

  
<line x1="460" y1="140" x2="530" y2="180" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<ellipse cx="580" cy="175" rx="40" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="540" y="175" width="80" height="20" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="580" cy="195" rx="40" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="540" y1="175" x2="540" y2="195" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="620" y1="175" x2="620" y2="195" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="580" y="189" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Cache
</text>

  

  
<line x1="460" y1="145" x2="530" y2="240" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a2)">
</line>

  
<rect x="530" y="225" width="100" height="35" rx="6" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="580" y="247" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Alert Service
</text>


</svg>

</details>


> **
Example: Data ingestion cycle


Every 15 minutes, the **Data Ingestion Pipeline** pulls data from NOAA/NWS APIs, weather station feeds, and satellite imagery. The **Processing & Forecast** service normalizes data into a grid (0.1° lat/lng cells), runs forecast models, and writes updated weather data to the **Weather DB**. It also invalidates/updates the **Redis Cache** for affected grid cells. If severe weather is detected (tornado warning for Oklahoma City), the **Alert Service** sends push notifications to users in the affected area.


## Overall Combined Flow


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 300" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a3" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<text x="10" y="20" fill="#1a73e8" font-size="12" font-weight="bold">
INGESTION (every 15 min)
</text>

  
<rect x="10" y="30" width="80" height="30" rx="5" fill="#795548" stroke="#4e342e" stroke-width="1">
</rect>

  
<text x="50" y="49" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Sources
</text>

  
<line x1="90" y1="45" x2="130" y2="45" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="130" y="30" width="80" height="30" rx="5" fill="#2196f3" stroke="#1565c0" stroke-width="1">
</rect>

  
<text x="170" y="49" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Ingest
</text>

  
<line x1="210" y1="45" x2="250" y2="45" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="250" y="30" width="80" height="30" rx="5" fill="#673ab7" stroke="#4527a0" stroke-width="1">
</rect>

  
<text x="290" y="49" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Process
</text>

  
<line x1="330" y1="40" x2="370" y2="40" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<ellipse cx="400" cy="36" rx="25" ry="7" fill="#ff5722" stroke="#d84315" stroke-width="1">
</ellipse>

  
<rect x="375" y="36" width="50" height="12" fill="#ff5722" stroke="#d84315" stroke-width="1">
</rect>

  
<ellipse cx="400" cy="48" rx="25" ry="7" fill="#e64a19" stroke="#d84315" stroke-width="1">
</ellipse>

  
<line x1="375" y1="36" x2="375" y2="48" stroke="#d84315" stroke-width="1">
</line>

  
<line x1="425" y1="36" x2="425" y2="48" stroke="#d84315" stroke-width="1">
</line>

  
<text x="400" y="46" text-anchor="middle" fill="#fff" font-size="6" font-weight="bold">
DB
</text>

  
<line x1="330" y1="50" x2="370" y2="75" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<ellipse cx="400" cy="72" rx="25" ry="7" fill="#009688" stroke="#00695c" stroke-width="1">
</ellipse>

  
<rect x="375" y="72" width="50" height="11" fill="#009688" stroke="#00695c" stroke-width="1">
</rect>

  
<ellipse cx="400" cy="83" rx="25" ry="7" fill="#00796b" stroke="#00695c" stroke-width="1">
</ellipse>

  
<line x1="375" y1="72" x2="375" y2="83" stroke="#00695c" stroke-width="1">
</line>

  
<line x1="425" y1="72" x2="425" y2="83" stroke="#00695c" stroke-width="1">
</line>

  
<text x="400" y="81" text-anchor="middle" fill="#fff" font-size="6" font-weight="bold">
Cache
</text>

  
<text x="10" y="130" fill="#1a73e8" font-size="12" font-weight="bold">
READ PATH
</text>

  
<rect x="10" y="145" width="65" height="30" rx="5" fill="#4caf50" stroke="#388e3c" stroke-width="1">
</rect>

  
<text x="42" y="164" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
User
</text>

  
<line x1="75" y1="160" x2="110" y2="160" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="110" y="145" width="55" height="30" rx="5" fill="#607d8b" stroke="#37474f" stroke-width="1">
</rect>

  
<text x="137" y="164" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
CDN
</text>

  
<line x1="165" y1="160" x2="195" y2="160" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="195" y="145" width="55" height="30" rx="5" fill="#ff9800" stroke="#f57c00" stroke-width="1">
</rect>

  
<text x="222" y="164" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
GW
</text>

  
<line x1="250" y1="160" x2="290" y2="160" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="290" y="145" width="80" height="30" rx="5" fill="#2196f3" stroke="#1565c0" stroke-width="1">
</rect>

  
<text x="330" y="164" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Weather Svc
</text>

  
<line x1="370" y1="155" x2="375" y2="83" stroke="#333" stroke-width="1" stroke-dasharray="3,2">
</line>

  
<line x1="370" y1="165" x2="375" y2="44" stroke="#333" stroke-width="1" stroke-dasharray="3,2">
</line>


</svg>

</details>


> **Combined Example

Sources feed data every 15 min → Ingest → Process → Store in DB + invalidate Cache. User requests weather → CDN (10-min TTL hit) → or API GW → Weather Svc → Redis Cache → or DB → return.


## Database Schema


### NoSQL (Cassandra or DynamoDB)

1. current_weather

  
  
  
  
  
  
  
  
  
  
  


| Field          | Type      | Key  | Description                            |
|----------------|-----------|------|----------------------------------------|
| grid_cell_id   | VARCHAR   | `PK` | Lat/lng grid cell (e.g., "40.7:-74.0") |
| timestamp      | TIMESTAMP | `PK` | Observation time                       |
| temperature    | FLOAT     |      | Temperature (°F)                       |
| humidity       | FLOAT     |      | Relative humidity (%)                  |
| wind_speed     | FLOAT     |      | Wind speed (mph)                       |
| wind_direction | VARCHAR   |      | Wind direction (N, NE, etc.)           |
| condition      | VARCHAR   |      | Sunny, Cloudy, Rain, etc.              |
| visibility     | FLOAT     |      | Miles                                  |
| uv_index       | INT       |      | UV index (0-11+)                       |
| pressure       | FLOAT     |      | Barometric pressure                    |


**Why NoSQL:** Weather data is write-heavy (millions of grid cells updated every 15 min), append-only (historical data preserved), and accessed by key (grid cell). Cassandra handles this well with high write throughput.


2. forecasts

  
  
  
  
  
  
  
  


| Field                | Type      | Key  | Description                   |
|----------------------|-----------|------|-------------------------------|
| grid_cell_id         | VARCHAR   | `PK` | Location grid cell            |
| forecast_time        | TIMESTAMP | `PK` | Forecasted time               |
| forecast_type        | VARCHAR   |      | HOURLY, DAILY                 |
| high_temp            | FLOAT     |      | High temperature              |
| low_temp             | FLOAT     |      | Low temperature               |
| condition            | VARCHAR   |      | Expected condition            |
| precipitation_chance | FLOAT     |      | Precipitation probability (%) |


## Cache Deep Dive


> **
Multi-Layer Caching


**Layer 1 — CDN (CloudFront/Akamai):** Caches API responses at edge. TTL = 10 minutes. Handles 90%+ of traffic for popular locations. Pull-based. LRU eviction.


**Layer 2 — Redis Cache:** Application-level cache. Key = grid_cell_id. TTL = 15 minutes (aligned with data update cycle). Write-through (updated by ingestion pipeline). LRU eviction.


**Why aggressive caching works:** Weather data changes slowly (every 15 min). Thousands of users requesting NYC weather get the same data. Cache hit ratio: ~98%.


## Scaling Considerations


  - **CDN handles traffic spikes** — during Hurricane events, CDN absorbs 10x traffic at edge without hitting origin.

  - **Load Balancer:** L7 LB (round-robin) in front of Weather Service.

  - **Auto-scaling:** Weather Service scales based on CDN miss rate.

  - **Geo-distributed:** Deploy Weather Service in multiple regions; CDN routes to nearest.


## Tradeoffs and Deep Dives


> **Grid Cell Resolution

0.1° grid = ~11km cells. Finer grid (0.01° = 1.1km) provides more precise data but 100x more cells to store/cache. We use 0.1° for current weather and interpolate for specific coordinates.


## Alternative Approaches

On-demand API calls to weather sources

Instead of pre-ingesting, call NOAA/NWS APIs on each user request. Simpler but: (1) external API rate limits, (2) higher latency, (3) no control over data quality. Not chosen.


## Additional Information


### Radar Tile Server


Weather radar maps are served as map tiles (256×256 PNG images) at different zoom levels (Z/X/Y). Pre-rendered and stored in S3, served via CDN. Updated every 5 minutes. This is the same approach as Google Maps tiles.