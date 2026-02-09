# 🛋️ Wayfair — E-Commerce for Home Goods


Design an e-commerce platform specializing in furniture and home goods with visual search, AR room visualization, complex shipping logistics for oversized items, and supplier drop-ship model.


## 📋 Functional Requirements


- **Product catalog** — browse/search 30M+ SKUs across furniture, decor, appliances with rich attributes (dimensions, material, color, style)

- **Visual search & AR** — upload photo to find similar products; AR placement to visualize furniture in room

- **Faceted search & filtering** — filter by price, dimensions, color, material, style, rating, delivery speed

- **Shopping cart & checkout** — cart with multi-supplier items, split shipping, tax calculation

- **Supplier drop-ship** — 80% of orders shipped directly from 12K+ suppliers, not Wayfair warehouses

- **Delivery scheduling** — white-glove delivery for large items (room of choice, assembly), LTL freight tracking

- **Reviews & ratings** — verified purchase reviews with photo uploads


## 📋 Non-Functional Requirements


- **Search latency** — <200ms for search results including facet counts

- **Catalog scale** — 30M+ products, 100K+ daily catalog updates from suppliers

- **Availability** — 99.99% for browse/search, 99.999% for checkout

- **Image processing** — 3D model rendering, AR compatibility, 10+ images per product

- **Logistics complexity** — handle LTL freight, parcel, white-glove across 12K+ suppliers

- **Peak handling** — Way Day sales (10x normal traffic), Black Friday


## 🔄 Flow 1: Product Search & Discovery


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 300" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#7B2D8E">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="90" text-anchor="middle" fill="white" font-size="13">
Customer
</text>


<rect x="175" y="60" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="240" y="90" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>


<rect x="350" y="30" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="415" y="60" text-anchor="middle" fill="white" font-size="12">
Search Service
</text>


<rect x="350" y="100" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="415" y="120" text-anchor="middle" fill="white" font-size="12">
Visual Search
</text>
<text x="415" y="135" text-anchor="middle" fill="white" font-size="10">
(CNN Embedding)
</text>


<rect x="530" y="30" width="140" height="50" rx="8" fill="#4E342E">
</rect>
<text x="600" y="60" text-anchor="middle" fill="white" font-size="12">
Elasticsearch
</text>


<rect x="530" y="100" width="140" height="50" rx="8" fill="#1565C0">
</rect>
<text x="600" y="120" text-anchor="middle" fill="white" font-size="12">
Ranking Service
</text>
<text x="600" y="135" text-anchor="middle" fill="white" font-size="10">
(LambdaMART)
</text>


<rect x="720" y="60" width="130" height="50" rx="8" fill="#00695C">
</rect>
<text x="785" y="90" text-anchor="middle" fill="white" font-size="12">
Personalization
</text>


<line x1="130" y1="85" x2="170" y2="85" stroke="#7B2D8E" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="305" y1="75" x2="345" y2="55" stroke="#7B2D8E" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="305" y1="95" x2="345" y2="125" stroke="#7B2D8E" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="480" y1="55" x2="525" y2="55" stroke="#7B2D8E" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="670" y1="75" x2="715" y2="85" stroke="#7B2D8E" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="480" y1="125" x2="525" y2="115" stroke="#E67E22" stroke-width="2" marker-end="url(#a1)">
</line>


<text x="415" y="175" fill="#aaa" font-size="10">
image → embedding → ANN search
</text>


</svg>

</details>


**Step 1:** Customer enters text query or uploads image for visual search
**Step 2:** Text search: Elasticsearch with custom analyzers for furniture attributes (dimensions, materials, style synonyms)
**Step 3:** Visual search: CNN extracts 512-dim embedding from image, nearest-neighbor search in vector DB (FAISS/Milvus)
**Step 4:** Ranking Service re-ranks results using LambdaMART with features: relevance, margin, conversion rate, delivery speed, supplier reliability
**Step 5:** Personalization layer boosts based on user style preferences, browsing history, room context

Deep Dive: Visual Search & Style Matching

`// Visual Search Pipeline:
// 1. User uploads photo of furniture they like
// 2. CNN (ResNet-50 fine-tuned on furniture) extracts feature embedding
// 3. ANN (Approximate Nearest Neighbor) finds similar products

// Embedding model: trained on Wayfair's product image corpus
// Triplet loss: anchor (product) → positive (same style) → negative
// Result: similar-looking furniture maps to nearby embeddings

// ANN search: FAISS with IVF-PQ index
// 30M products → 30M 512-dim vectors
// IVF (Inverted File): cluster vectors into 4096 cells
// PQ (Product Quantization): compress 512-dim to 64 bytes
// Total index size: ~2GB (fits in memory)
// Search: probe 32 nearest cells, return top-50 in ~5ms

// Style attributes extracted by CNN:
// - Material: wood, metal, fabric, leather, glass
// - Style: modern, traditional, farmhouse, mid-century
// - Color: dominant + accent colors (HSV histogram)
// - Shape: silhouette features for matching form factor

// AR Visualization:
// ARKit (iOS) / ARCore (Android) detects floor plane
// 3D model (USDZ/GLB) placed at user-selected position
// Real-time occlusion and lighting estimation`


Deep Dive: Faceted Search with Elasticsearch

`// Furniture-specific search challenges:
// 1. Dimensional search: "couch under 80 inches wide"
// 2. Material synonyms: "wood" = oak, walnut, pine, bamboo
// 3. Style matching: "modern farmhouse" (compound style)
// 4. Color search: "blue" should match navy, teal, cerulean

// Elasticsearch mapping:
{
  "product": {
    "properties": {
      "title": { "type": "text", "analyzer": "furniture_analyzer" },
      "category_path": { "type": "keyword" }, // "Furniture/Living Room/Sofas"
      "dimensions": {
        "width_inches": { "type": "float" },
        "height_inches": { "type": "float" },
        "depth_inches": { "type": "float" },
        "weight_lbs": { "type": "float" }
      },
      "price": { "type": "scaled_float", "scaling_factor": 100 },
      "materials": { "type": "keyword" }, // multi-valued
      "style_tags": { "type": "keyword" },
      "color_family": { "type": "keyword" },
      "supplier_id": { "type": "keyword" },
      "in_stock": { "type": "boolean" },
      "avg_rating": { "type": "float" },
      "image_embedding": { "type": "dense_vector", "dims": 512 }
    }
  }
}

// Aggregations for facet counts:
// "aggs": { "materials": { "terms": { "field": "materials" } } }
// Returns: { "wood": 1234, "metal": 567, "fabric": 890 }
// Fast because keyword fields use doc_values (columnar)`


## 🔄 Flow 2: Order & Drop-Ship Fulfillment


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 320" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#E67E22">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="80" text-anchor="middle" fill="white" font-size="12">
Customer
</text>


<rect x="175" y="50" width="120" height="50" rx="8" fill="#1565C0">
</rect>
<text x="235" y="80" text-anchor="middle" fill="white" font-size="12">
Order Service
</text>


<rect x="340" y="30" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="405" y="50" text-anchor="middle" fill="white" font-size="12">
Order Splitter
</text>
<text x="405" y="65" text-anchor="middle" fill="white" font-size="10">
(per supplier)
</text>


<rect x="520" y="30" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="585" y="50" text-anchor="middle" fill="white" font-size="12">
Supplier Portal
</text>
<text x="585" y="65" text-anchor="middle" fill="white" font-size="10">
(EDI / API)
</text>


<rect x="700" y="30" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="765" y="60" text-anchor="middle" fill="white" font-size="12">
Supplier
</text>


<rect x="340" y="120" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="405" y="140" text-anchor="middle" fill="white" font-size="12">
Shipping
</text>
<text x="405" y="155" text-anchor="middle" fill="white" font-size="10">
Orchestrator
</text>


<rect x="520" y="120" width="130" height="50" rx="8" fill="#00695C">
</rect>
<text x="585" y="140" text-anchor="middle" fill="white" font-size="12">
LTL Freight
</text>
<text x="585" y="155" text-anchor="middle" fill="white" font-size="10">
Broker
</text>


<rect x="700" y="120" width="130" height="50" rx="8" fill="#00695C">
</rect>
<text x="765" y="140" text-anchor="middle" fill="white" font-size="12">
White Glove
</text>
<text x="765" y="155" text-anchor="middle" fill="white" font-size="10">
Delivery
</text>


<rect x="340" y="230" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="405" y="260" text-anchor="middle" fill="white" font-size="12">
Tracking Service
</text>


<line x1="130" y1="75" x2="170" y2="75" stroke="#E67E22" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="295" y1="65" x2="335" y2="55" stroke="#E67E22" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="470" y1="55" x2="515" y2="55" stroke="#E67E22" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="650" y1="55" x2="695" y2="55" stroke="#E67E22" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="405" y1="80" x2="405" y2="115" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="470" y1="145" x2="515" y2="140" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="650" y1="140" x2="695" y2="140" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="405" y1="170" x2="405" y2="225" stroke="#FF9800" stroke-width="2" marker-end="url(#a2)">
</line>


<text x="405" y="200" fill="#aaa" font-size="10">
shipment events
</text>


</svg>

</details>


**Step 1:** Customer places order with items from multiple suppliers. Order Service validates inventory, processes payment
**Step 2:** Order Splitter breaks order into sub-orders per supplier. Each sub-order sent via EDI 850 (purchase order) or API
**Step 3:** Supplier ships directly to customer. Shipping Orchestrator selects carrier: parcel (FedEx/UPS) for small items, LTL freight for furniture, white-glove for premium delivery
**Step 4:** Tracking Service aggregates shipment events from multiple carriers into unified tracking for customer

Deep Dive: Drop-Ship Model & EDI Integration

`// Wayfair's drop-ship model:
// - Wayfair does NOT hold inventory for 80% of products
// - Supplier stores, picks, packs, and ships directly
// - Wayfair handles: discovery, checkout, customer service, returns

// EDI (Electronic Data Interchange) — B2B protocol:
// EDI 850 = Purchase Order (Wayfair → Supplier)
// EDI 855 = PO Acknowledgment (Supplier → Wayfair)
// EDI 856 = Advance Ship Notice (Supplier → Wayfair)
// EDI 810 = Invoice (Supplier → Wayfair)

// Modern alternative: REST API for smaller suppliers
POST /api/v1/supplier/orders
{
  "po_number": "WF-2025-8839201",
  "ship_to": { "name": "John Doe", "street": "123 Main St", ... },
  "items": [
    { "sku": "SUP-SOFA-8822", "qty": 1, "unit_price": 450.00 }
  ],
  "ship_method": "LTL_FREIGHT",
  "ship_by_date": "2025-02-14",
  "deliver_by_date": "2025-02-21"
}

// Inventory feed: suppliers push stock levels every 15 min
// Critical for preventing overselling on Way Day (10x traffic)`


## 🔄 Flow 3: AR Room Visualization


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 250" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#7B2D8E">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="80" text-anchor="middle" fill="white" font-size="12">
Mobile Camera
</text>
<text x="80" y="95" text-anchor="middle" fill="white" font-size="10">
(ARKit/ARCore)
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="255" y="80" text-anchor="middle" fill="white" font-size="12">
Plane Detection
</text>
<text x="255" y="95" text-anchor="middle" fill="white" font-size="10">
& Scene Understanding
</text>


<rect x="370" y="60" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="435" y="80" text-anchor="middle" fill="white" font-size="12">
3D Model Loader
</text>
<text x="435" y="95" text-anchor="middle" fill="white" font-size="10">
(USDZ / GLB)
</text>


<rect x="550" y="60" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="615" y="80" text-anchor="middle" fill="white" font-size="12">
Real-time Render
</text>
<text x="615" y="95" text-anchor="middle" fill="white" font-size="10">
(PBR + Lighting)
</text>


<rect x="730" y="60" width="130" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="795" y="90" text-anchor="middle" fill="white" font-size="12">
AR Display
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#7B2D8E" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="320" y1="85" x2="365" y2="85" stroke="#7B2D8E" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="500" y1="85" x2="545" y2="85" stroke="#7B2D8E" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="680" y1="85" x2="725" y2="85" stroke="#7B2D8E" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


**Step 1:** ARKit/ARCore detects floor plane and room geometry from camera feed
**Step 2:** User selects product; 3D model downloaded from CDN (USDZ for iOS, GLB for Android, ~5-15MB)
**Step 3:** Model placed on detected surface with real-world scale (actual product dimensions)
**Step 4:** PBR (Physically Based Rendering) with environment lighting estimation for realistic appearance


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 480" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="ac" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#7B2D8E">
</polygon>
</marker>
</defs>


<text x="550" y="25" text-anchor="middle" fill="#7B2D8E" font-size="16" font-weight="bold">
Wayfair E-Commerce Architecture
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
Catalog Service
</text>


<rect x="400" y="130" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="153" text-anchor="middle" fill="white" font-size="11">
Cart / Checkout
</text>


<rect x="400" y="175" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="198" text-anchor="middle" fill="white" font-size="11">
Order Service
</text>


<rect x="400" y="220" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="243" text-anchor="middle" fill="white" font-size="11">
Inventory Service
</text>


<rect x="580" y="50" width="130" height="40" rx="8" fill="#4E342E">
</rect>
<text x="645" y="75" text-anchor="middle" fill="white" font-size="11">
Elasticsearch
</text>


<rect x="580" y="110" width="130" height="40" rx="8" fill="#4E342E">
</rect>
<text x="645" y="135" text-anchor="middle" fill="white" font-size="11">
PostgreSQL
</text>


<rect x="580" y="170" width="130" height="40" rx="8" fill="#00695C">
</rect>
<text x="645" y="195" text-anchor="middle" fill="white" font-size="11">
Redis Cluster
</text>


<rect x="580" y="230" width="130" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="645" y="255" text-anchor="middle" fill="white" font-size="11">
Kafka Event Bus
</text>


<rect x="780" y="50" width="140" height="40" rx="8" fill="#E65100">
</rect>
<text x="850" y="75" text-anchor="middle" fill="white" font-size="11">
Supplier Portal (EDI)
</text>


<rect x="780" y="110" width="140" height="40" rx="8" fill="#1565C0">
</rect>
<text x="850" y="135" text-anchor="middle" fill="white" font-size="11">
Shipping Orchestrator
</text>


<rect x="780" y="170" width="140" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="850" y="195" text-anchor="middle" fill="white" font-size="11">
ML Ranking & Recs
</text>


<rect x="780" y="230" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="850" y="255" text-anchor="middle" fill="white" font-size="11">
3D Model / Image CDN
</text>


<line x1="140" y1="70" x2="195" y2="70" stroke="#7B2D8E" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="330" y1="70" x2="395" y2="57" stroke="#7B2D8E" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="57" x2="575" y2="70" stroke="#7B2D8E" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="102" x2="575" y2="130" stroke="#7B2D8E" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="147" x2="575" y2="190" stroke="#E67E22" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="192" x2="575" y2="250" stroke="#E67E22" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="710" y1="70" x2="775" y2="70" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="710" y1="130" x2="775" y2="130" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="710" y1="195" x2="775" y2="195" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="710" y1="250" x2="775" y2="250" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


</svg>

</details>


## 💾 Database Schema


### PostgreSQL — Products & Orders

`CREATE TABLE products (
  id BIGSERIAL PRIMARY KEY, --  `PK`
  sku VARCHAR(50) UNIQUE NOT NULL, --  `IDX`
  supplier_id INT NOT NULL, --  `FK`  `SHARD`
  title TEXT NOT NULL,
  category_path TEXT NOT NULL, --  `IDX`
  price_cents BIGINT NOT NULL,
  width_inches DECIMAL(6,1),
  height_inches DECIMAL(6,1),
  depth_inches DECIMAL(6,1),
  weight_lbs DECIMAL(7,1),
  materials TEXT[], -- array: ['solid wood','metal']
  style_tags TEXT[], -- ['modern','farmhouse']
  color_family VARCHAR(30), --  `IDX`
  has_3d_model BOOLEAN DEFAULT false,
  avg_rating DECIMAL(3,2),
  review_count INT DEFAULT 0,
  in_stock BOOLEAN DEFAULT true, --  `IDX`
  created_at TIMESTAMPTZ DEFAULT now()
);
--  `IDX`: (category_path, in_stock, price_cents)
--  `IDX`: GIN on materials, style_tags for array containment

CREATE TABLE orders (
  id BIGSERIAL PRIMARY KEY, --  `PK`
  customer_id BIGINT NOT NULL, --  `FK`  `IDX`
  status VARCHAR(20) DEFAULT 'pending',
  total_cents BIGINT NOT NULL,
  tax_cents BIGINT NOT NULL,
  shipping_cents BIGINT NOT NULL,
  payment_intent_id VARCHAR(50),
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE order_items (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL REFERENCES orders(id), --  `FK`
  product_id BIGINT NOT NULL, --  `FK`
  supplier_id INT NOT NULL,
  quantity INT NOT NULL,
  unit_price_cents BIGINT NOT NULL,
  sub_order_id VARCHAR(50), -- split per supplier
  ship_method VARCHAR(30), -- 'parcel'|'ltl_freight'|'white_glove'
  tracking_number VARCHAR(50)
);`


### Inventory (Redis + PostgreSQL)

`-- Real-time inventory in Redis (for checkout validation)
-- Key: inventory:{sku}
-- Value: { "available": 45, "reserved": 3, "supplier_id": 8821 }
-- Atomic decrement on order placement (DECRBY)

-- Inventory history in PostgreSQL
CREATE TABLE inventory_feeds (
  id BIGSERIAL PRIMARY KEY,
  supplier_id INT NOT NULL, --  `FK`
  sku VARCHAR(50) NOT NULL, --  `IDX`
  available_qty INT NOT NULL,
  warehouse_location VARCHAR(50),
  lead_time_days INT,
  received_at TIMESTAMPTZ DEFAULT now()
);
-- Suppliers push feed every 15 min via EDI 846 or API
-- Stale inventory (>30 min) triggers "limited stock" badge

-- 3D Model Assets (metadata in DB, files on CDN)
CREATE TABLE product_3d_models (
  product_id BIGINT PRIMARY KEY REFERENCES products(id),
  usdz_url TEXT, -- iOS AR format
  glb_url TEXT,  -- Android/web AR format
  thumbnail_url TEXT,
  poly_count INT,
  file_size_bytes BIGINT,
  status VARCHAR(20) DEFAULT 'processing'
);`


## ⚡ Cache & CDN Deep Dive


| Layer             | What                                     | Strategy                                | TTL        |
|-------------------|------------------------------------------|-----------------------------------------|------------|
| CDN (CloudFront)  | Product images, 3D models, static assets | Long-lived with versioned URLs          | 30 days    |
| Redis — Inventory | Real-time stock levels                   | Write-through from supplier feed        | 30 min max |
| Redis — Search    | Popular search results                   | LRU with invalidation on catalog change | 5 min      |
| Redis — Sessions  | Cart, user preferences                   | Write-through                           | 24 hours   |
| Varnish           | Category pages, PDP (product detail)     | Stale-while-revalidate                  | 60 seconds |


`// Image processing pipeline:
// Supplier uploads high-res image (4000x4000 JPEG)
// → Background removal (U-Net segmentation)
// → Generate 6 renditions: thumbnail, listing, detail, zoom, social, AR-preview
// → WebP/AVIF conversion for modern browsers
// → Push to CDN with immutable URLs
// → Product page references CDN URLs with responsive srcset`


## 📈 Scaling Considerations


- **Way Day preparation:** pre-warm CDN caches with deal products, pre-scale search cluster 3x, freeze catalog changes during event, dedicated checkout capacity.

- **Supplier integration scale:** 12K+ suppliers with varying tech capabilities. EDI for large suppliers, REST API for mid-size, CSV upload for small. All normalized to internal event format.

- **Search cluster:** Elasticsearch with 30M products across 20+ shards. Shard by category for co-located search. Replica shards for read scaling (3 replicas during Way Day).

- **3D model generation:** automated pipeline from product photos → 3D mesh using photogrammetry + AI. Scale bottleneck: GPU rendering farm. Prioritize top-selling products for 3D models.


## ⚖️ Tradeoffs


> **
Drop-Ship Model
- Zero inventory holding cost
- Infinite catalog scalability
- Low capital requirements


Drop-Ship Model
- Less control over shipping quality
- Complex returns across suppliers
- Inventory accuracy depends on supplier feeds


Visual Search (CNN)
- Discover products without words
- Cross-sell from inspiration photos
- Higher engagement, +15% conversion


Visual Search (CNN)
- GPU inference cost per query
- Requires large training dataset
- Style subjective — not always accurate


## 🔄 Alternative Approaches


First-Party Fulfillment (Amazon FBA model)

Hold inventory in Wayfair warehouses for faster, more reliable delivery. Higher capital expenditure but better customer experience. CastleGate (Wayfair's forward warehouse program) is a hybrid — suppliers pre-position inventory in Wayfair facilities.

Generative AI for Room Design

Instead of AR placement, use diffusion models to generate complete room renders with selected products. Customer uploads room photo, AI redesigns with Wayfair products. Requires style-transfer and product-faithful generation.


## 📚 Additional Information


- **CastleGate warehousing:** Wayfair's forward-deployment network. Suppliers pre-ship popular SKUs to regional Wayfair facilities. Enables 2-day delivery while keeping drop-ship cost structure.

- **LTL freight:** Less-Than-Truckload. For items 150-10,000 lbs. Requires freight class determination based on dimensions, weight, density. White-glove adds room placement + assembly (+$100-300).

- **Tax calculation:** complex for furniture (varies by state, item type — some states exempt certain home goods). Use tax engine (Avalara/Vertex) with product tax codes.

- **Return logistics:** furniture returns are expensive ($50-200 per return in shipping). Wayfair often issues refund without return for items under $50 (cheaper than reverse logistics).