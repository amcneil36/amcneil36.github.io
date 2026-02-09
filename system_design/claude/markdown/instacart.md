# 🛒 Design Instacart


On-demand grocery delivery platform connecting customers with personal shoppers across 80K+ stores, real-time inventory, route optimization


## 1. Functional Requirements


- **Store & Product Catalog:** Browse 80K+ stores, search/filter products with real-time availability and pricing per store

- **Cart & Checkout:** Multi-store cart, coupon/promo application, delivery window selection, payment processing

- **Shopper Assignment:** Match orders to personal shoppers based on proximity, store familiarity, rating, capacity

- **Real-Time Shopping:** Shopper scans items, communicates substitutions, customer approves/rejects replacements in real-time

- **Delivery Tracking:** Live map tracking of shopper (shopping → checkout → driving to customer)

- **Delivery Windows:** Scheduled delivery (pick a time slot) or priority delivery (within 30-60 minutes)

- **Inventory Sync:** Real-time-ish inventory from retail partners (varies by integration level)

- **Tipping & Ratings:** Rate shopper, adjust tip post-delivery, report issues for refunds


## 2. Non-Functional Requirements


- **Scale:** 10M+ monthly active customers, 600K+ shoppers, 80K+ stores across 14K+ cities

- **Latency:** Product search <200ms, substitution notification <2 seconds, location tracking updates every 5-10 seconds

- **Availability:** 99.9%+ (downtime = lost perishable grocery orders, angry customers)

- **Inventory Accuracy:** Goal: 90%+ accuracy (grocery inventory is notoriously inaccurate)

- **Peak Load:** 5-10x spikes during holidays (Thanksgiving, Super Bowl Sunday), severe weather events

- **Delivery SLA:** Priority orders within 60 min, scheduled within 2-hour window


## 3. Flow 1 — Product Search & Order Placement


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 400" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#43B02A">
</polygon>
</marker>
</defs>


<rect x="20" y="160" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="190" text-anchor="middle" fill="#fff" font-size="13">
Customer
</text>


<rect x="180" y="160" width="120" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="240" y="190" text-anchor="middle" fill="#fff" font-size="13">
API Gateway
</text>


<rect x="350" y="60" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="420" y="90" text-anchor="middle" fill="#fff" font-size="13">
Catalog Service
</text>
<text x="420" y="105" text-anchor="middle" fill="#fff" font-size="11">
(store-specific)
</text>


<rect x="350" y="160" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="420" y="185" text-anchor="middle" fill="#fff" font-size="13">
Cart Service
</text>


<rect x="350" y="270" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="420" y="295" text-anchor="middle" fill="#fff" font-size="13">
Order Service
</text>


<rect x="560" y="60" width="140" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="630" y="90" text-anchor="middle" fill="#fff" font-size="13">
Elasticsearch
</text>
<text x="630" y="105" text-anchor="middle" fill="#fff" font-size="11">
(product search)
</text>


<rect x="560" y="160" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="630" y="185" text-anchor="middle" fill="#fff" font-size="13">
Inventory Service
</text>
<text x="630" y="200" text-anchor="middle" fill="#fff" font-size="11">
(availability)
</text>


<rect x="560" y="270" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="630" y="295" text-anchor="middle" fill="#fff" font-size="13">
Pricing Service
</text>
<text x="630" y="310" text-anchor="middle" fill="#fff" font-size="11">
(dynamic markups)
</text>


<rect x="770" y="160" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="840" y="185" text-anchor="middle" fill="#fff" font-size="13">
Payment Service
</text>
<text x="840" y="200" text-anchor="middle" fill="#fff" font-size="11">
(Stripe)
</text>


<rect x="770" y="270" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="840" y="295" text-anchor="middle" fill="#fff" font-size="13">
Delivery Window
</text>
<text x="840" y="310" text-anchor="middle" fill="#fff" font-size="11">
Calculator
</text>


<line x1="130" y1="190" x2="178" y2="190" stroke="#43B02A" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="175" x2="348" y2="95" stroke="#43B02A" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="190" x2="348" y2="190" stroke="#43B02A" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="205" x2="348" y2="295" stroke="#43B02A" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="90" x2="558" y2="90" stroke="#43B02A" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="190" x2="558" y2="190" stroke="#43B02A" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="490" y1="295" x2="558" y2="295" stroke="#43B02A" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="700" y1="190" x2="768" y2="190" stroke="#43B02A" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="700" y1="295" x2="768" y2="295" stroke="#43B02A" stroke-width="2" marker-end="url(#a1)">
</line>


</svg>

</details>


### Flow Steps


1. **Customer searches "organic milk"** → Catalog Service queries Elasticsearch with store_id filter (selected store) and customer's location-based pricing zone

2. **Results enriched:** Each product enriched with: store-specific price (may differ from in-store), estimated availability (inventory signal), promotions/coupons, product images, nutrition info

3. **Customer adds items to cart** → Cart Service maintains per-store cart with item quantities, substitution preferences

4. **Checkout initiated:** Cart Service validates item availability (real-time inventory check), calculates totals: items + service fee + delivery fee + tip + tax

5. **Delivery Window Calculator:** Based on store location, customer location, current shopper capacity, and estimated shopping/driving time → offers available 2-hour windows or priority (30-60 min)

6. **Payment:** Auth hold placed via Stripe (not charged yet — final charge after shopping completes with actual items/weights)

7. **Order created** with status: `pending_assignment` → enters shopper matching queue


### Example


**Customer order from Costco in San Francisco:**

Items:
  - Organic Whole Milk 1gal      $6.49  (avail: high)
  - Bananas (bunch ~2lb)          $1.89  (avail: high, by weight)
  - Rotisserie Chicken            $4.99  (avail: medium)
  - Kirkland Water 40-pack        $4.29  (avail: high, heavy item)

Subtotal: $17.66
Service fee (5%): $0.88
Delivery fee: $3.99 (free with Instacart+)
Heavy item fee: $2.00 (for water)
Estimated tax: $1.45
Tip: $5.00
Auth hold: $31.00 (buffer for substitutions/weight variations)

Selected: Priority delivery (est. 45 min)
Payment: Auth hold on Visa ending 4242


### Deep Dives


Store-Specific Product Catalog


Each store has a different assortment, pricing, and inventory. Instacart maintains a **unified product graph**: global product entities (UPC-based) linked to store-specific listings (price, availability, aisle location). The same "Organic Whole Milk" product maps to different prices at Costco ($6.49) vs Whole Foods ($7.99). Catalog updated via: (1) **Retailer data feeds** (batch, daily/weekly), (2) **POS integration** (near-real-time for some partners), (3) **Shopper feedback** (item not found → decrement availability), (4) **ML predictions** (predict availability from historical patterns). Elasticsearch index: per-store sharding for fast store-specific search.


Inventory Accuracy Challenge


Grocery inventory is notoriously inaccurate: items sold in-store aren't reflected until end-of-day POS reconciliation. Instacart combats this with: **Probabilistic availability** — not binary in/out-of-stock, but a probability score. ML model trained on: time of day (milk more available in AM), day of week (bread sells out by Sunday), historical store replenishment patterns, shopper feedback (real-time "not found" signals). Instacart reports ~92% item-found rate across all orders. The gap is handled by the substitution flow.


Delivery Window Calculation


Windows calculated from: (1) Estimated shopping time: `base_time + per_item_time × num_items + checkout_wait`. Costco: 45 min base (large store), 30 sec/item. (2) Driving time: Google Maps API for store→customer ETA. (3) Shopper supply: available shoppers near this store vs pending orders. Priority delivery requires a shopper available NOW. Scheduled slots: capacity-planned (max N orders per slot per store). Dynamic pricing: delivery fee increases during peak (Thanksgiving morning: $9.99 delivery fee). Instacart+ members get free delivery over $35.


Pricing Model


Instacart typically marks up prices 5-15% above in-store prices (varies by retailer agreement). Some retailers (Costco, Target, Kroger) have negotiated **in-store pricing** on Instacart. Revenue sources: (1) Service fees (5-10% of order), (2) Delivery fees ($3.99-$9.99), (3) Instacart+ subscriptions ($99/year), (4) **Instacart Ads** (CPM/CPC from CPG brands — the largest revenue source), (5) Retailer technology licensing fees. The Ads platform is critical: brands pay for product placement in search results and banner positions.


## 4. Flow 2 — Shopper Assignment & Real-Time Shopping


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 400" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#0AAD0A">
</polygon>
</marker>
</defs>


<rect x="20" y="160" width="120" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="80" y="185" text-anchor="middle" fill="#fff" font-size="13">
Order Queue
</text>
<text x="80" y="200" text-anchor="middle" fill="#fff" font-size="11">
(priority heap)
</text>


<rect x="190" y="160" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="255" y="185" text-anchor="middle" fill="#fff" font-size="13">
Shopper Matcher
</text>
<text x="255" y="200" text-anchor="middle" fill="#fff" font-size="11">
(assignment algo)
</text>


<rect x="190" y="60" width="130" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="255" y="90" text-anchor="middle" fill="#fff" font-size="13">
Shopper Pool
</text>
<text x="255" y="105" text-anchor="middle" fill="#fff" font-size="11">
(available shoppers)
</text>


<rect x="370" y="160" width="120" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="430" y="190" text-anchor="middle" fill="#fff" font-size="13">
Shopper
</text>
<text x="430" y="205" text-anchor="middle" fill="#fff" font-size="11">
(accepts batch)
</text>


<rect x="540" y="60" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="605" y="90" text-anchor="middle" fill="#fff" font-size="13">
Shopping List
</text>
<text x="605" y="105" text-anchor="middle" fill="#fff" font-size="11">
(aisle-optimized)
</text>


<rect x="540" y="160" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="605" y="185" text-anchor="middle" fill="#fff" font-size="13">
Item Scan
</text>
<text x="605" y="200" text-anchor="middle" fill="#fff" font-size="11">
(barcode verify)
</text>


<rect x="540" y="270" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="605" y="295" text-anchor="middle" fill="#fff" font-size="13">
Substitution
</text>
<text x="605" y="310" text-anchor="middle" fill="#fff" font-size="11">
Engine
</text>


<rect x="730" y="160" width="130" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="795" y="185" text-anchor="middle" fill="#fff" font-size="13">
Customer
</text>
<text x="795" y="200" text-anchor="middle" fill="#fff" font-size="11">
(approves sub)
</text>


<rect x="730" y="270" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="795" y="295" text-anchor="middle" fill="#fff" font-size="13">
ML Substitution
</text>
<text x="795" y="310" text-anchor="middle" fill="#fff" font-size="11">
Recommender
</text>


<line x1="140" y1="190" x2="188" y2="190" stroke="#0AAD0A" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="255" y1="120" x2="255" y2="158" stroke="#0AAD0A" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="320" y1="190" x2="368" y2="190" stroke="#0AAD0A" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="490" y1="175" x2="538" y2="95" stroke="#0AAD0A" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="490" y1="190" x2="538" y2="190" stroke="#0AAD0A" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="605" y1="222" x2="605" y2="268" stroke="#0AAD0A" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="670" y1="185" x2="728" y2="185" stroke="#0AAD0A" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="670" y1="295" x2="728" y2="295" stroke="#0AAD0A" stroke-width="2" marker-end="url(#a2)">
</line>


</svg>

</details>


### Flow Steps


1. **Order enters assignment queue** → priority based on delivery window urgency, customer tier (Instacart+ prioritized), tip amount

2. **Batch optimization:** Multiple orders at the same store may be batched (up to 3 orders per shopper trip) to improve efficiency

3. **Shopper Matcher:** Scores available shoppers: `score = proximity_to_store × store_familiarity × rating × capacity × speed_rating`

4. **Shopper receives batch offer** on app: shows store, item count, estimated earnings, estimated time → accepts or declines (timeout: 60 seconds)

5. **Shopping phase:** Shopper sees aisle-optimized shopping list (items ordered by store aisle layout). Scans each item barcode to verify correct product.

6. **Item not found → Substitution Engine:** ML recommender suggests alternatives (same brand different size, or same category similar product). Customer preference checked first (pre-set: "get my best match" or "don't replace").

7. **Real-time chat:** Shopper can message customer about substitutions; customer approves/rejects via push notification

8. **Checkout:** Shopper pays with Instacart payment card at register → receipt scanned for reconciliation


### Example


**Batch assignment:** 3 orders at Costco SF bundled: Order A (12 items), Order B (8 items), Order C (5 items) = 25 total items


**Shopper: Maria** — 4.95 rating, 2km from Costco, "Costco Expert" badge (50+ shops at this location), available now


**Earnings offer:** Base pay $7 + $2 heavy item bonus + $4.50 batch bonus = $13.50 base + $15 combined tips = $28.50 estimated for ~75 min work


**Substitution scenario:**

Item: Kirkland Organic Whole Milk 1gal — NOT FOUND
ML suggestions (ranked):
  1. Kirkland Organic 2% Milk 1gal (same brand, different fat%) — 0.87 confidence
  2. Horizon Organic Whole Milk 1gal (diff brand, same type) — 0.82 confidence
  3. Kirkland Organic Whole Milk 0.5gal x2 (same product, different size) — 0.76 confidence
Customer preference: "Get my best match" → auto-select #1
Push notification: "Maria replaced Organic Whole Milk → Organic 2% Milk. OK?"
Customer: [Approve ✓]


### Deep Dives


Order Batching Algorithm


Batching multiple orders per shopper trip improves economics but risks quality. Algorithm: for each store, group pending orders by delivery window compatibility (can they all be delivered on time?). Batch score: `Σ(order_value) × item_overlap_bonus / max_delivery_time_penalty`. Item overlap: if multiple orders want bananas, shopper grabs all at once. Max batch size: 3 orders (Instacart cap). Priority delivery orders are NEVER batched (dedicated shopper). The batching problem is a variant of the **bin packing / vehicle routing problem** — NP-hard, solved with greedy heuristics + simulated annealing.


ML Substitution Recommender


When an item is unavailable, the ML model suggests substitutions. Features: product category similarity (hierarchical taxonomy), brand affinity (customer's brand preference history), size/quantity similarity, price similarity (don't substitute up significantly), dietary compatibility (vegan customer → don't suggest non-vegan), historical acceptance rate (what substitutions do customers generally approve). Model: gradient-boosted trees (LightGBM) trained on millions of historical substitution accept/reject decisions. Instacart reports 90%+ substitution acceptance when ML suggestions are used.


Shopper Payment Card


Instacart shoppers use a **virtual payment card** (Instacart-issued debit card) to pay at checkout. The card is pre-loaded with the exact order amount + buffer for weight variations and substitutions. At checkout, the POS transaction is captured and reconciled against the order. If a substitution changed the total, the charge is adjusted. This eliminates the need for shoppers to use personal funds. Card is issued via **Stripe Issuing** — programmatically created virtual cards with spending controls.


Aisle-Optimized Shopping List


Instacart knows store layouts (aisle maps) for many retailers. The shopping list is sorted by aisle order to minimize backtracking (a variant of the **Traveling Salesman Problem** within a store). Data source: retailer-provided planograms, shopper crowdsourced aisle data (shoppers confirm aisle numbers), and historical scan location data. For stores without aisle maps, items are grouped by department (produce, dairy, frozen, etc.). This optimization saves 10-20% shopping time per order.


## 5. Flow 3 — Delivery & Real-Time Tracking


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 300" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#8BC34A">
</polygon>
</marker>
</defs>


<rect x="20" y="120" width="120" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="80" y="150" text-anchor="middle" fill="#fff" font-size="13">
Shopper
</text>
<text x="80" y="165" text-anchor="middle" fill="#fff" font-size="11">
(leaves store)
</text>


<rect x="190" y="120" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="255" y="150" text-anchor="middle" fill="#fff" font-size="13">
Location Service
</text>
<text x="255" y="165" text-anchor="middle" fill="#fff" font-size="11">
(GPS every 5s)
</text>


<rect x="370" y="30" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="435" y="60" text-anchor="middle" fill="#fff" font-size="13">
Route Optimizer
</text>
<text x="435" y="75" text-anchor="middle" fill="#fff" font-size="11">
(multi-drop)
</text>


<rect x="370" y="120" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="435" y="150" text-anchor="middle" fill="#fff" font-size="13">
ETA Service
</text>


<rect x="370" y="210" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="435" y="240" text-anchor="middle" fill="#fff" font-size="13">
Notification
</text>
<text x="435" y="255" text-anchor="middle" fill="#fff" font-size="11">
Service
</text>


<rect x="560" y="120" width="130" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="625" y="150" text-anchor="middle" fill="#fff" font-size="13">
Customer
</text>
<text x="625" y="165" text-anchor="middle" fill="#fff" font-size="11">
(live tracking)
</text>


<rect x="760" y="120" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="825" y="150" text-anchor="middle" fill="#fff" font-size="13">
Delivery Proof
</text>
<text x="825" y="165" text-anchor="middle" fill="#fff" font-size="11">
(photo + GPS)
</text>


<line x1="140" y1="150" x2="188" y2="150" stroke="#8BC34A" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="320" y1="135" x2="368" y2="65" stroke="#8BC34A" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="320" y1="150" x2="368" y2="150" stroke="#8BC34A" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="320" y1="165" x2="368" y2="235" stroke="#8BC34A" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="500" y1="150" x2="558" y2="150" stroke="#8BC34A" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="690" y1="150" x2="758" y2="150" stroke="#8BC34A" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


### Flow Steps


1. **Shopper completes shopping** → status changes to `delivering`. If batch has multiple orders, Route Optimizer calculates optimal delivery sequence.

2. **Route Optimizer:** Multi-drop routing (store → customer A → customer B → customer C). Considers: delivery window deadlines, traffic conditions, distance. For 3-order batches: solves TSP variant with time windows.

3. **Location tracking:** Shopper app sends GPS every 5 seconds → Location Service ingests via UDP/WebSocket → stored in Redis for real-time access

4. **ETA Service:** Computes dynamic ETA using: current location, traffic (Google Maps API), remaining stops. Updates customer's tracking screen in real-time.

5. **Customer notifications:** "Your shopper is on the way!" → "5 minutes away" → "Your order has arrived!"

6. **Delivery proof:** Shopper takes photo of delivered bags at door + GPS coordinates logged → stored for dispute resolution

7. **Final charge:** Payment adjusted for actual items (substitutions, weight-based items). Customer can adjust tip for 24 hours post-delivery.


### Example


**Multi-drop delivery for 3-order batch:**

Costco SF → Customer A (2.1 miles, ETA 8 min) → Customer B (1.3 miles, ETA 14 min) → Customer C (0.8 miles, ETA 18 min)
Route optimized: A first (closest + tightest delivery window), then B, then C
Total driving: 4.2 miles, 18 min
Customer A sees: "Maria is shopping" → "Maria is checking out" → "Maria is on the way — ETA 8 min" → "Delivered! [photo]"
Final charge for Customer A: $18.52 (original auth: $31.00 — difference refunded)
  - Milk substituted (2% instead of whole): -$0.50
  - Bananas actual weight 1.8lb vs estimated 2lb: -$0.19
Tip adjustment window: Customer A increases tip from $5 to $8 (great service)


## 6. Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 500" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#43B02A">
</polygon>
</marker>
</defs>


<rect x="10" y="180" width="90" height="50" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="55" y="210" text-anchor="middle" fill="#fff" font-size="11">
Customer
</text>


<rect x="10" y="250" width="90" height="50" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="55" y="280" text-anchor="middle" fill="#fff" font-size="11">
Shopper
</text>


<rect x="140" y="215" width="100" height="45" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="190" y="243" text-anchor="middle" fill="#fff" font-size="11">
API Gateway
</text>


<rect x="290" y="50" width="110" height="40" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="345" y="75" text-anchor="middle" fill="#fff" font-size="10">
Catalog
</text>


<rect x="290" y="100" width="110" height="40" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="345" y="125" text-anchor="middle" fill="#fff" font-size="10">
Cart
</text>


<rect x="290" y="150" width="110" height="40" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="345" y="175" text-anchor="middle" fill="#fff" font-size="10">
Order
</text>


<rect x="290" y="200" width="110" height="40" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="345" y="225" text-anchor="middle" fill="#fff" font-size="10">
Shopper Matcher
</text>


<rect x="290" y="250" width="110" height="40" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="345" y="275" text-anchor="middle" fill="#fff" font-size="10">
Inventory
</text>


<rect x="290" y="300" width="110" height="40" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="345" y="325" text-anchor="middle" fill="#fff" font-size="10">
Delivery/Route
</text>


<rect x="290" y="350" width="110" height="40" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="345" y="375" text-anchor="middle" fill="#fff" font-size="10">
Ads Platform
</text>


<rect x="290" y="400" width="110" height="40" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="345" y="425" text-anchor="middle" fill="#fff" font-size="10">
Payment
</text>


<rect x="460" y="100" width="100" height="40" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="510" y="125" text-anchor="middle" fill="#fff" font-size="10">
Kafka
</text>


<rect x="620" y="50" width="110" height="40" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="675" y="75" text-anchor="middle" fill="#fff" font-size="10">
PostgreSQL
</text>


<rect x="620" y="100" width="110" height="40" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="675" y="125" text-anchor="middle" fill="#fff" font-size="10">
Redis
</text>


<rect x="620" y="150" width="110" height="40" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="675" y="175" text-anchor="middle" fill="#fff" font-size="10">
Elasticsearch
</text>


<rect x="620" y="200" width="110" height="40" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="675" y="225" text-anchor="middle" fill="#fff" font-size="10">
DynamoDB
</text>


<rect x="620" y="250" width="110" height="40" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="675" y="275" text-anchor="middle" fill="#fff" font-size="10">
S3 (images)
</text>


<rect x="620" y="300" width="110" height="40" rx="8" fill="#546e7a" stroke="#90a4ae" stroke-width="2">
</rect>
<text x="675" y="325" text-anchor="middle" fill="#fff" font-size="10">
CDN (CF)
</text>


<line x1="100" y1="205" x2="138" y2="235" stroke="#43B02A" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="100" y1="275" x2="138" y2="245" stroke="#43B02A" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="240" y1="235" x2="288" y2="175" stroke="#43B02A" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="400" y1="120" x2="458" y2="120" stroke="#43B02A" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="560" y1="120" x2="618" y2="75" stroke="#43B02A" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="560" y1="120" x2="618" y2="120" stroke="#43B02A" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="400" y1="70" x2="618" y2="170" stroke="#43B02A" stroke-width="2" marker-end="url(#a4)">
</line>


</svg>

</details>


## 7. Database Schema


### Orders (PostgreSQL — sharded by order_id)


Table: orders   `SHARD`
- order_id: uuid                `PK`
- customer_id: uuid             `FK`  `IDX`
- store_id: uuid                `FK`  `IDX`
- shopper_id: uuid              `FK`
- batch_id: uuid                `IDX`
- status: enum('pending','assigned','shopping','checkout','delivering','delivered','cancelled')
- delivery_window_start: timestamp
- delivery_window_end: timestamp
- delivery_type: enum('priority','scheduled')
- subtotal: decimal(10,2)
- service_fee: decimal(10,2)
- delivery_fee: decimal(10,2)
- tax: decimal(10,2)
- tip: decimal(10,2)
- final_total: decimal(10,2)
- payment_intent_id: varchar   // Stripe
- delivery_address: jsonb
- delivery_proof_photo_url: varchar
- created_at: timestamp         `IDX`
- delivered_at: timestamp


### Order Items (PostgreSQL)


Table: order_items
- order_item_id: uuid           `PK`
- order_id: uuid                `FK`  `IDX`
- product_id: uuid              `FK`
- store_product_id: uuid       // store-specific listing
- quantity: decimal(10,3)      // supports weight-based (2.5 lbs)
- unit_price: decimal(10,2)
- status: enum('pending','found','substituted','refunded','not_found')
- substitution_product_id: uuid
- substitution_preference: enum('best_match','specific','dont_replace')
- scanned_barcode: varchar
- actual_quantity: decimal(10,3)
- actual_price: decimal(10,2)


### Products (Elasticsearch + PostgreSQL)


// Elasticsearch index: products_by_store
{
  "product_id": "uuid",
  "store_id": "uuid",
  "name": "Organic Whole Milk",           //  `IDX`
  "brand": "Kirkland",
  "category": ["Dairy", "Milk", "Organic"],
  "upc": "012345678901",
  "price": 6.49,
  "unit": "gallon",
  "availability_score": 0.92,
  "aisle": "A12",
  "image_url": "cdn.instacart.com/...",
  "nutrition": {"calories": 150, ...},
  "tags": ["organic", "whole", "non-gmo"],
  "popularity_score": 0.87
}


### Shoppers (PostgreSQL)


Table: shoppers
- shopper_id: uuid              `PK`
- name: varchar
- phone: varchar
- email: varchar
- rating: decimal(3,2)         `IDX`
- total_orders: int
- status: enum('available','shopping','delivering','offline')
- current_location: point       `IDX`
- preferred_stores: uuid[]
- speed_rating: decimal(3,2)   // items per minute
- payment_card_id: varchar     // Stripe Issuing virtual card
- created_at: timestamp


## 8. Cache & CDN Deep Dive


| Layer            | Technology    | What's Cached                          | TTL                                   | Notes                                        |
|------------------|---------------|----------------------------------------|---------------------------------------|----------------------------------------------|
| CDN              | CloudFront    | Product images, store logos            | 1yr (versioned)                       | Multiple resolutions per product image       |
| Search Cache     | Elasticsearch | Popular searches, category pages       | 5-15 min                              | Per-store results                            |
| Inventory Cache  | Redis         | Product availability scores            | 5-30 min (varies by signal freshness) | Updated by shopper feedback in real-time     |
| Cart Cache       | Redis         | Active shopping carts                  | 7 days                                | Persisted to DB on checkout                  |
| Shopper Location | Redis GEO     | Real-time shopper positions            | 30 seconds                            | Updated every 5 seconds from app             |
| Delivery Window  | Redis         | Pre-computed available slots per store | 1-5 min                               | Recalculated on order placement/cancellation |


### Inventory Cache Strategy


Inventory data comes from multiple sources with different freshness: retailer POS feed (1-24h delay), shopper real-time feedback (instant), ML predictions (hourly). The cache implements a **layered freshness model**: start with retailer feed as base → overlay shopper feedback (item not found signals) → overlay ML prediction. The combined score is the availability probability shown to customers. Redis key: `inv:{store_id}:{product_id}` → `{score: 0.92, last_shopper_signal: "found", ml_prediction: 0.88}`.


## 9. Scaling Considerations


### Peak Traffic (Holidays)


- **Thanksgiving:** 5-10x normal volume. Pre-scaling starts 2 weeks before. Extra shoppers recruited via bonuses.

- **COVID peaks:** 2020 saw 500% traffic increase in weeks. Instacart added 300K shoppers in 3 months.

- **Weather events:** Snowstorms cause local 3-5x spikes with 2 hours notice.

- **Auto-scaling:** Kubernetes HPA on all services. Pre-warm capacity for known events (holidays).


### Multi-Region Deployment


- Primary: US regions (us-east-1, us-west-2). Also: Canada, expanding internationally.

- Store data localized: each metro area's stores indexed and served from nearest region

- Shopper matching: must be geographically local (shopper must be near the store)


### Load Balancing


- **DNS:** Route53 latency-based routing

- **L7:** AWS ALB with path routing to service mesh (Envoy/Istio)

- **WebSocket:** Sticky sessions for real-time shopper-customer communication


## 10. Tradeoffs


> **

> **
✅ Order Batching


2-3 orders per trip significantly improves shopper earnings and Instacart economics. Reduces delivery cost per order by 30-50%. Higher shopper utilization.


> **
❌ Batching Quality Tradeoff


Later deliveries in a batch may receive slightly warmer frozen items. Longer wait for batched customers. Complex multi-drop routing adds delivery time. Customer perception: "I'm paying for delivery but sharing a shopper."


> **
✅ ML Substitution Suggestions


90%+ acceptance rate when ML suggests substitutions. Reduces order cancellations. Faster than manual shopper decisions. Learns from customer preferences over time.


> **
❌ Substitution Friction


Customers may not respond to substitution requests in time (shopper waiting). Auto-substitutions can occasionally miss dietary restrictions. Some customers prefer no substitutions at all.


> **
✅ Gig Worker Model


Flexible supply — scales up/down with demand. No fixed labor cost during low-demand hours. Shoppers choose their own schedules.


> **
❌ Quality Control


Inconsistent shopping quality across gig workers. Training and retention challenges. Regulatory risk (worker classification lawsuits). Tips create incentive misalignment (shoppers may prioritize high-tip orders).


## 11. Alternative Approaches


Dark Store Model (Gopuff, Gorillas)


Operate dedicated micro-fulfillment centers (not retail stores). 100% inventory control, faster picking (purpose-built layout), guaranteed availability. But: high fixed cost, limited SKU count (~3000 vs 30,000+ at a supermarket), requires dense urban areas to be economical.


Retailer-Owned Pickup (Walmart, Kroger)


Retailer employees pick orders in-store and prepare for customer pickup or last-mile delivery. Better inventory accuracy (employee knows store), lower cost (existing workforce). But: no gig economy flexibility, limited delivery radius, retailer must invest in tech.


Autonomous Delivery


Nuro, Starship, Waymo delivery bots for last-mile. Eliminates delivery labor cost. But: limited to certain geographies, weather dependent, can't navigate apartments/stairs, regulatory hurdles, customer must come to curb.


Voice Commerce (Alexa/Google)


"Alexa, reorder my groceries." Simplifies repeat orders. Instacart integrates with voice assistants. Limitation: browsing/discovery is poor via voice. Works best for routine reorders of known items.


## 12. Additional Information


### Instacart Ads Platform


Instacart's largest revenue driver. CPG brands (P&G, Coca-Cola, General Mills) bid for product placement in search results and banner ads. Similar to Amazon Sponsored Products. Auction-based (second-price CPC). Targeting: by category, keyword, competitor conquesting. Measurement: closed-loop attribution (ad view → purchase within session). The Ads platform processes millions of bid requests per second, using a real-time auction engine similar to Google Ads.


### Key Numbers


| Monthly Active Customers | 10M+      |
|--------------------------|-----------|
| Shoppers (gig workers)   | 600K+     |
| Retail partner stores    | 80K+      |
| Cities served            | 14K+      |
| Average order value      | ~$110     |
| Item-found rate          | ~92%      |
| Annual GTV               | ~$30B     |
| Revenue                  | ~$3B/year |