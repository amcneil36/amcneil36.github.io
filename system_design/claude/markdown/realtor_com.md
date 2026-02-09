# 🏠 Realtor.com — Real Estate Listing Platform


Design a real estate platform for searching property listings, viewing details with maps/photos/3D tours, mortgage estimation, lead generation to agents, and MLS data integration across 800+ local MLS databases.


## 📋 Functional Requirements


- **Property search** — search by location (city, zip, map bounds), filters (price, beds, baths, sqft, type, year built)

- **Map-based browsing** — interactive map with property pins, polygon drawing for custom areas, school district overlay

- **Listing details** — photos, 3D tours (Matterport), floor plans, property history, tax records, estimated value (Zestimate-like)

- **Saved searches & alerts** — notify users when new listings match saved criteria

- **Lead generation** — connect buyers with agents; agents pay for leads (CPA model)

- **Mortgage calculator** — estimated monthly payment with current rates, down payment, taxes, insurance

- **Neighborhood insights** — school ratings, crime stats, walkability, commute times, demographics


## 📋 Non-Functional Requirements


- **Data freshness** — new MLS listings live within 5 minutes (Realtor.com's key differentiator: "most listings updated every 5 min")

- **Search latency** — <200ms for geo+filter queries including map rendering

- **Scale** — 5M+ active listings, 100M+ monthly visitors, 50K+ concurrent map sessions

- **MLS coverage** — aggregate from 800+ MLS databases with different schemas and protocols (RETS/RESO)

- **Availability** — 99.99% uptime, especially during peak (spring buying season)

- **SEO** — property pages must be SEO-optimized (major organic traffic source)


## 🔄 Flow 1: Property Search (Geo + Filters)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 300" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#D92228">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="90" text-anchor="middle" fill="white" font-size="12">
User
</text>


<rect x="175" y="60" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="240" y="90" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>


<rect x="350" y="60" width="140" height="50" rx="8" fill="#1565C0">
</rect>
<text x="420" y="80" text-anchor="middle" fill="white" font-size="12">
Search Service
</text>


<rect x="540" y="30" width="140" height="50" rx="8" fill="#4E342E">
</rect>
<text x="610" y="50" text-anchor="middle" fill="white" font-size="12">
Elasticsearch
</text>
<text x="610" y="65" text-anchor="middle" fill="white" font-size="10">
(geo_bounding_box)
</text>


<rect x="540" y="100" width="140" height="50" rx="8" fill="#00695C">
</rect>
<text x="610" y="120" text-anchor="middle" fill="white" font-size="12">
Map Tile Service
</text>
<text x="610" y="135" text-anchor="middle" fill="white" font-size="10">
(vector tiles)
</text>


<rect x="740" y="60" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="805" y="80" text-anchor="middle" fill="white" font-size="12">
Ranking /
</text>
<text x="805" y="95" text-anchor="middle" fill="white" font-size="10">
Personalization
</text>


<line x1="130" y1="85" x2="170" y2="85" stroke="#D92228" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="305" y1="85" x2="345" y2="85" stroke="#D92228" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="75" x2="535" y2="55" stroke="#D92228" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="95" x2="535" y2="125" stroke="#D92228" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="680" y1="55" x2="735" y2="75" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


</svg>

</details>


**Step 1:** User searches "3 bed, 2 bath, $300K-$500K in Austin TX" or drags map to define viewport
**Step 2:** Search Service translates to Elasticsearch query with geo_bounding_box + filter clauses
**Step 3:** Elasticsearch returns matching listings with lat/lng for map pins + facet counts for filter sidebar
**Step 4:** Map Tile Service renders vector tiles with property pin clusters at current zoom level
**Step 5:** Ranking personalizes order: promoted listings (paid), match strength, recency, user history

Deep Dive: Geospatial Search with Elasticsearch

`// Elasticsearch mapping for property listings:
{
  "listing": {
    "properties": {
      "location": { "type": "geo_point" },  // lat/lng
      "polygon": { "type": "geo_shape" },    // property boundaries
      "price": { "type": "integer" },
      "bedrooms": { "type": "byte" },
      "bathrooms": { "type": "half_float" },
      "sqft": { "type": "integer" },
      "property_type": { "type": "keyword" }, // house|condo|townhouse
      "status": { "type": "keyword" },        // active|pending|sold
      "list_date": { "type": "date" },
      "mls_id": { "type": "keyword" },
      "school_district": { "type": "keyword" },
      "hoa_fee": { "type": "integer" },
      "year_built": { "type": "short" },
      "features": { "type": "keyword" }       // pool|garage|fireplace
    }
  }
}

// Query: map viewport search
{
  "query": {
    "bool": {
      "filter": [
        { "geo_bounding_box": {
            "location": {
              "top_left": { "lat": 30.35, "lon": -97.85 },
              "bottom_right": { "lat": 30.20, "lon": -97.65 }
            }
        }},
        { "range": { "price": { "gte": 300000, "lte": 500000 }}},
        { "term": { "bedrooms": 3 }},
        { "term": { "status": "active" }}
      ]
    }
  },
  "aggs": {
    "price_histogram": { "histogram": { "field": "price", "interval": 50000 }},
    "property_types": { "terms": { "field": "property_type" }}
  }
}

// Map pin clustering: use geo_grid aggregation at zoom levels
// Zoom < 12: cluster pins into geohash cells
// Zoom >= 12: show individual property pins`


## 🔄 Flow 2: MLS Data Ingestion Pipeline


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 300" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#1976D2">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#4E342E">
</rect>
<text x="80" y="70" text-anchor="middle" fill="white" font-size="12">
800+ MLS
</text>
<text x="80" y="85" text-anchor="middle" fill="white" font-size="10">
Databases
</text>


<rect x="190" y="50" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="255" y="70" text-anchor="middle" fill="white" font-size="12">
MLS Adapter
</text>
<text x="255" y="85" text-anchor="middle" fill="white" font-size="10">
(RETS/RESO/API)
</text>


<rect x="370" y="50" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="435" y="70" text-anchor="middle" fill="white" font-size="12">
Schema
</text>
<text x="435" y="85" text-anchor="middle" fill="white" font-size="10">
Normalizer
</text>


<rect x="550" y="50" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="615" y="70" text-anchor="middle" fill="white" font-size="12">
Enrichment
</text>
<text x="615" y="85" text-anchor="middle" fill="white" font-size="10">
(geocode, photos)
</text>


<rect x="730" y="50" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="795" y="75" text-anchor="middle" fill="white" font-size="12">
Listing DB + ES
</text>


<rect x="370" y="170" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="435" y="190" text-anchor="middle" fill="white" font-size="12">
Dedup Engine
</text>
<text x="435" y="205" text-anchor="middle" fill="white" font-size="10">
(cross-MLS)
</text>


<rect x="550" y="170" width="130" height="50" rx="8" fill="#00695C">
</rect>
<text x="615" y="190" text-anchor="middle" fill="white" font-size="12">
Alert Matcher
</text>
<text x="615" y="205" text-anchor="middle" fill="white" font-size="10">
(saved searches)
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#1976D2" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="320" y1="75" x2="365" y2="75" stroke="#1976D2" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="500" y1="75" x2="545" y2="75" stroke="#1976D2" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="680" y1="75" x2="725" y2="75" stroke="#1976D2" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="435" y1="100" x2="435" y2="165" stroke="#FF9800" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="500" y1="195" x2="545" y2="195" stroke="#FF9800" stroke-width="2" marker-end="url(#a2)">
</line>


<text x="80" y="120" fill="#aaa" font-size="10">
RETS pull every 5 min
</text>


</svg>

</details>


**Step 1:** MLS Adapters connect to 800+ MLS systems using RETS (Real Estate Transaction Standard) or RESO Web API, polling every 5 minutes
**Step 2:** Schema Normalizer maps 800 different field naming conventions to unified schema
**Step 3:** Dedup Engine detects same property listed on multiple MLSs (address matching + fuzzy dedup)
**Step 4:** Enrichment adds geocoding (lat/lng from address), photo processing, school district assignment
**Step 5:** Alert Matcher checks new/updated listings against saved searches and sends push/email notifications

Deep Dive: MLS Integration Challenges

`// The US has 800+ MLS databases — each is a local monopoly
// No single national MLS (unlike most countries)

// Protocols:
// RETS (legacy): XML-based query language, proprietary per MLS
//   GET /search?type=Property&query=(ListPrice=300000+)&limit=100
// RESO Web API (modern): RESTful, OData-based, standardized
//   GET /odata/Property?$filter=ListPrice ge 300000&$top=100

// Schema nightmare: each MLS names fields differently
// MLS A: "ListPrice", MLS B: "AskingPrice", MLS C: "LP"
// MLS A: "Bedrooms", MLS B: "BedroomCount", MLS C: "BR"

// Solution: field mapping table per MLS (maintained manually!)
{
  "mls_austin": {
    "listing_price": "ListPrice",
    "bedrooms": "BedroomCount",
    "status": "StandardStatus",
    "photo_url_template": "https://photos.mls.com/{ListingKey}/{seq}.jpg"
  },
  "mls_dallas": {
    "listing_price": "AskingPrice",
    "bedrooms": "BR",
    // ... different for every MLS
  }
}

// Data freshness: Realtor.com pulls every 5 min (contractual requirement)
// Some MLS provide push (webhooks) for instant updates
// Total: ~50K listing updates/hour across all MLS sources`


## 🔄 Flow 3: Property Valuation & Mortgage Estimation


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 260" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#D92228">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="90" text-anchor="middle" fill="white" font-size="12">
User Views Listing
</text>


<rect x="175" y="60" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="240" y="80" text-anchor="middle" fill="white" font-size="12">
AVM Service
</text>
<text x="240" y="95" text-anchor="middle" fill="white" font-size="10">
(Auto Valuation)
</text>


<rect x="350" y="60" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="415" y="80" text-anchor="middle" fill="white" font-size="12">
Comparable Sales
</text>
<text x="415" y="95" text-anchor="middle" fill="white" font-size="10">
Engine
</text>


<rect x="530" y="60" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="595" y="80" text-anchor="middle" fill="white" font-size="12">
Mortgage Calc
</text>
<text x="595" y="95" text-anchor="middle" fill="white" font-size="10">
Service
</text>


<rect x="710" y="60" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="775" y="80" text-anchor="middle" fill="white" font-size="12">
Rate API
</text>
<text x="775" y="95" text-anchor="middle" fill="white" font-size="10">
(Freddie Mac)
</text>


<line x1="130" y1="85" x2="170" y2="85" stroke="#D92228" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="305" y1="85" x2="345" y2="85" stroke="#D92228" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="480" y1="85" x2="525" y2="85" stroke="#D92228" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="660" y1="85" x2="705" y2="85" stroke="#81D4FA" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


**Step 1:** AVM (Automated Valuation Model) estimates property value using ML on comparable sales, tax assessments, market trends
**Step 2:** Comparable Sales Engine finds recent sales of similar properties within radius (beds, baths, sqft, age, condition)
**Step 3:** Mortgage Calculator: monthly payment = P&I + property tax + insurance + PMI (if < 20% down) + HOA
**Step 4:** Current mortgage rates pulled from Freddie Mac API or partner lenders


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 480" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="ac" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#D92228">
</polygon>
</marker>
</defs>


<text x="550" y="25" text-anchor="middle" fill="#D92228" font-size="16" font-weight="bold">
Realtor.com Architecture
</text>


<rect x="20" y="50" width="120" height="40" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="75" text-anchor="middle" fill="white" font-size="11">
Web / Mobile
</text>


<rect x="200" y="50" width="130" height="40" rx="8" fill="#E65100">
</rect>
<text x="265" y="75" text-anchor="middle" fill="white" font-size="11">
API Gateway / CDN
</text>


<rect x="400" y="40" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="63" text-anchor="middle" fill="white" font-size="11">
Search Service
</text>


<rect x="400" y="85" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="108" text-anchor="middle" fill="white" font-size="11">
Listing Service
</text>


<rect x="400" y="130" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="153" text-anchor="middle" fill="white" font-size="11">
Map Tile Service
</text>


<rect x="400" y="175" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="198" text-anchor="middle" fill="white" font-size="11">
AVM / Mortgage
</text>


<rect x="400" y="220" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="243" text-anchor="middle" fill="white" font-size="11">
Lead Service
</text>


<rect x="400" y="265" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="288" text-anchor="middle" fill="white" font-size="11">
Alert Service
</text>


<rect x="590" y="50" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="660" y="75" text-anchor="middle" fill="white" font-size="11">
Elasticsearch
</text>


<rect x="590" y="110" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="660" y="135" text-anchor="middle" fill="white" font-size="11">
PostgreSQL
</text>


<rect x="590" y="170" width="140" height="40" rx="8" fill="#00695C">
</rect>
<text x="660" y="195" text-anchor="middle" fill="white" font-size="11">
Redis Cache
</text>


<rect x="590" y="230" width="140" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="660" y="255" text-anchor="middle" fill="white" font-size="11">
Kafka
</text>


<rect x="800" y="50" width="150" height="40" rx="8" fill="#4E342E">
</rect>
<text x="875" y="75" text-anchor="middle" fill="white" font-size="11">
MLS Ingestion Pipeline
</text>


<rect x="800" y="110" width="150" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="875" y="135" text-anchor="middle" fill="white" font-size="11">
Photo/3D CDN (S3)
</text>


<rect x="800" y="170" width="150" height="40" rx="8" fill="#E65100">
</rect>
<text x="875" y="195" text-anchor="middle" fill="white" font-size="11">
Agent CRM / Leads
</text>


<line x1="140" y1="70" x2="195" y2="70" stroke="#D92228" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="330" y1="70" x2="395" y2="57" stroke="#D92228" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="57" x2="585" y2="70" stroke="#D92228" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="102" x2="585" y2="130" stroke="#D92228" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="147" x2="585" y2="195" stroke="#1976D2" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="70" x2="795" y2="70" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="130" x2="795" y2="130" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="255" x2="795" y2="195" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


</svg>

</details>


## 💾 Database Schema


### PostgreSQL — Listings & Properties

`CREATE TABLE properties (
  id BIGSERIAL PRIMARY KEY, --  `PK`
  address TEXT NOT NULL,
  city VARCHAR(100) NOT NULL, --  `IDX`
  state VARCHAR(2) NOT NULL,
  zip VARCHAR(10) NOT NULL, --  `IDX`
  lat DECIMAL(9,6) NOT NULL,
  lng DECIMAL(9,6) NOT NULL,
  property_type VARCHAR(20), -- house|condo|townhouse|land
  sqft INT,
  lot_sqft INT,
  year_built SMALLINT,
  bedrooms SMALLINT,
  bathrooms DECIMAL(3,1),
  stories SMALLINT,
  garage_spaces SMALLINT,
  features TEXT[], -- pool, fireplace, etc.
  school_district VARCHAR(100),
  tax_assessed_value INT,
  estimated_value INT, -- AVM output
  created_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_geo ON properties USING GIST (
  ST_SetSRID(ST_MakePoint(lng, lat), 4326)
);

CREATE TABLE listings (
  id BIGSERIAL PRIMARY KEY, --  `PK`
  property_id BIGINT NOT NULL REFERENCES properties(id), --  `FK`
  mls_id VARCHAR(20) NOT NULL,
  mls_number VARCHAR(30) NOT NULL, --  `IDX`
  status VARCHAR(15) NOT NULL, -- active|pending|sold|withdrawn
  list_price INT NOT NULL, --  `IDX`
  sold_price INT,
  list_date DATE NOT NULL, --  `IDX`
  sold_date DATE,
  agent_id BIGINT, --  `FK`
  description TEXT,
  virtual_tour_url TEXT,
  days_on_market INT,
  updated_at TIMESTAMPTZ,
  UNIQUE(mls_id, mls_number)
);
--  `IDX`: (status, list_date) for active listing queries`


### Saved Searches & Leads

`CREATE TABLE saved_searches (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL, --  `FK`  `IDX`
  name VARCHAR(100),
  search_criteria JSONB NOT NULL,
  -- Example: {"city":"Austin","min_price":300000,
  --           "max_price":500000,"beds_min":3}
  alert_frequency VARCHAR(10) DEFAULT 'instant',
  last_alerted_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ DEFAULT now()
);

-- Alert matching: new listing ingested → check against
-- all saved searches (inverted index approach)
-- Index saved searches by (city, price_range, beds)
-- On new listing: find matching searches in O(1)

CREATE TABLE leads (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL, --  `FK`
  listing_id BIGINT NOT NULL, --  `FK`
  agent_id BIGINT NOT NULL, --  `FK`
  lead_type VARCHAR(20), -- 'tour_request'|'question'|'contact'
  message TEXT,
  status VARCHAR(15) DEFAULT 'new', --  `IDX`
  revenue_cents INT, -- CPA charge to agent
  created_at TIMESTAMPTZ DEFAULT now()
);
-- Agent pays per qualified lead ($20-$100+)
-- Revenue model: lead gen is primary income source`


## ⚡ Cache & CDN Deep Dive


| Layer            | What                                           | Strategy                               | TTL                      |
|------------------|------------------------------------------------|----------------------------------------|--------------------------|
| CDN (CloudFront) | Property photos, map tiles, static pages       | Long-lived with invalidation on update | 24h (photos), 1h (tiles) |
| Redis — Search   | Popular search results (city + common filters) | LRU, invalidate on listing update      | 5 minutes                |
| Redis — Listing  | Hot listing detail pages                       | Write-through on MLS update            | 5 minutes                |
| Map Tile Cache   | Pre-rendered vector tiles per zoom level       | Generated on listing change in area    | 1 hour                   |
| Server SSR Cache | Pre-rendered HTML for SEO (listing pages)      | Stale-while-revalidate                 | 5 minutes                |


`// Photo CDN pipeline:
// MLS provides photo URLs (often low-quality, watermarked)
// Pipeline: download → remove watermark → generate sizes:
//   - Thumbnail: 200x150 (search results)
//   - Medium: 800x600 (listing page)
//   - Large: 1600x1200 (lightbox/zoom)
//   - WebP/AVIF versions for modern browsers
// Store in S3 → CloudFront CDN
// ~40 photos per listing × 5M listings = 200M images`


## 📈 Scaling Considerations


- **MLS ingestion at scale:** 800 MLS × pull every 5 min = 9,600 poll jobs/hour. Distributed job scheduler (Celery/Airflow). Each MLS adapter runs independently. Rate limit per MLS (respect their API limits).

- **Elasticsearch scaling:** 5M active listings + 50M sold (historical). Separate indices: active_listings (hot, 10 shards), sold_listings (warm, 20 shards). Active index replicas: 3 for read throughput.

- **Map rendering:** vector tiles pre-computed at zoom levels 1-18. On listing change, invalidate affected tiles. Mapbox GL JS on client renders tiles. At low zoom: cluster pins server-side to avoid sending 10K pins.

- **Spring buying season:** Feb-Jun traffic 2-3x winter. Pre-scale Elasticsearch replicas, CDN capacity, and search service instances. Predictive auto-scaling based on prior year patterns.


## ⚖️ Tradeoffs


> **
5-Minute MLS Freshness
- Competitive advantage (other sites: 15-60 min)
- Reduces "already sold" frustration
- Better user trust


5-Minute MLS Freshness
- 800 adapters to maintain (each MLS is unique)
- High polling load on MLS servers
- Schema changes require manual adapter updates


Lead Gen Revenue Model
- Free for consumers (maximizes traffic)
- High-value leads ($20-$100+ per lead)
- Aligned incentives (better search = more leads)


Lead Gen Revenue Model
- Agents may get low-quality leads
- User experience vs monetization tension
- Dependent on real estate market health


## 🔄 Alternative Approaches


iBuying Model (Opendoor/Zillow Offers)

Platform buys homes directly, renovates, and resells. Eliminates traditional agent model. Higher revenue per transaction but massive capital risk. Zillow lost $881M on iBuying (shut down in 2021). AVM accuracy critical — 1% error on $500K home = $5K loss.

Blockchain Title / Smart Contract

Replace traditional title search with blockchain-verified property ownership. Smart contracts for escrow and closing. Would eliminate title insurance (~$1K per transaction). Challenge: requires government adoption of on-chain deed recording.


## 📚 Additional Information


- **RESO standards:** Real Estate Standards Organization defines data dictionary (1,800+ fields) and Web API. Adoption growing but not universal — many MLS still use legacy RETS or proprietary protocols.

- **IDX (Internet Data Exchange):** agreement that allows brokers to display each other's listings. Realtor.com has direct MLS data (not IDX) — gives access to more data fields and faster updates.

- **AVM accuracy:** Automated Valuation Models use comparable sales, tax assessments, and ML. Median error: ~2-4% in data-rich areas, ~7-10% in rural areas. Not a substitute for appraisal but useful for estimation.

- **SEO is critical:** ~40% of traffic comes from organic search ("houses for sale in [city]"). Server-side rendering (SSR) for property pages. Rich snippets with structured data (JSON-LD for RealEstateListing schema).