# System Design: Amazon (E-Commerce Platform)


## Functional Requirements


- Product catalog with search, filtering, and category browsing

- Shopping cart management (add/remove/update items)

- Checkout and payment processing (multiple payment methods)

- Order management (place, track, cancel, return orders)

- Inventory management with real-time stock tracking

- Product reviews and ratings

- Personalized recommendations ("Customers who bought this also bought...")

- Seller/marketplace support (third-party sellers)


## Non-Functional Requirements


- **High Availability:** 99.99% — every minute of downtime = millions in lost revenue

- **Low Latency:** Product pages in <200ms, search in <300ms

- **Scalability:** Hundreds of millions of products, millions of concurrent users, peak events like Prime Day (10x normal traffic)

- **Consistency:** Strong consistency for inventory and orders (prevent overselling). Eventual consistency OK for reviews, recommendations

- **Durability:** Order and payment data must never be lost


## Flow 1: Product Search & Browse


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 420" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ff9900">
</polygon>
</marker>
</defs>


<rect x="20" y="170" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="80" y="205" text-anchor="middle" fill="white" font-size="13">
User
</text>


<rect x="190" y="170" width="120" height="60" rx="10" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="250" y="205" text-anchor="middle" fill="white" font-size="13">
CDN
</text>


<rect x="360" y="170" width="140" height="60" rx="10" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="430" y="198" text-anchor="middle" fill="white" font-size="12">
API Gateway /
</text>
<text x="430" y="215" text-anchor="middle" fill="white" font-size="12">
Load Balancer
</text>


<rect x="560" y="90" width="140" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="630" y="120" text-anchor="middle" fill="white" font-size="12">
Search Service
</text>


<rect x="560" y="200" width="140" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="630" y="230" text-anchor="middle" fill="white" font-size="12">
Catalog Service
</text>


<rect x="560" y="310" width="140" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="630" y="340" text-anchor="middle" fill="white" font-size="12">
Recommendation
</text>


<rect x="780" y="90" width="130" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="845" y="120" text-anchor="middle" fill="white" font-size="12">
Elasticsearch
</text>


<rect x="780" y="200" width="130" height="50" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="845" y="228" text-anchor="middle" fill="white" font-size="11">
Product Cache
</text>
<text x="845" y="242" text-anchor="middle" fill="white" font-size="10">
(Redis)
</text>


<ellipse cx="845" cy="335" rx="65" ry="25" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="845" y="340" text-anchor="middle" fill="white" font-size="11">
Product DB
</text>


<line x1="140" y1="200" x2="188" y2="200" stroke="#ff9900" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="310" y1="200" x2="358" y2="200" stroke="#ff9900" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="500" y1="188" x2="558" y2="120" stroke="#ff9900" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="500" y1="200" x2="558" y2="225" stroke="#ff9900" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="500" y1="215" x2="558" y2="330" stroke="#ff9900" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<line x1="700" y1="115" x2="778" y2="115" stroke="#ff9900" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="700" y1="225" x2="778" y2="225" stroke="#ff9900" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="845" y1="250" x2="845" y2="308" stroke="#ff6b6b" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow1)">
</line>


<text x="550" y="30" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Amazon: Product Search & Browse Flow
</text>


</svg>

</details>


### Step-by-Step


1. **User searches "wireless earbuds"** → `GET /v1/search?q=wireless+earbuds&page=1&sort=relevance`

2. **CDN:** Static product images and assets cached. Search results NOT cached (personalized)

3. **Search Service:** Queries Elasticsearch with full-text search + filters (price range, brand, ratings, Prime eligible)

4. **Elasticsearch:** Returns ranked product IDs using BM25 + custom scoring (sales velocity, review rating, conversion rate)

5. **Catalog Service:** Hydrates product IDs with full details (title, price, images, availability). Checks Redis cache first, falls back to Product DB

6. **Recommendation Service:** Adds "Sponsored products" and "Customers also viewed" based on collaborative filtering


> **
**Example:** User searches "wireless earbuds under $50". Search Service sends to Elasticsearch: `{ query: "wireless earbuds", filter: { price: { lte: 5000 } }, sort: { relevance: "desc" } }`. Returns 500 matching products. Top 48 hydrated from Product Cache (Redis hit: 43, DB miss: 5). Recommendation engine adds 4 sponsored products. Response returned in ~180ms.


### Deep Dive: Search Service


- **Protocol:** HTTPS REST

- **Elasticsearch config:** 100s of shards, replicated across AZs. Each product document: ~2KB (title, description, attributes, category hierarchy)

- **Custom scoring:** `score = BM25_relevance * 0.4 + sales_velocity * 0.25 + avg_rating * 0.15 + conversion_rate * 0.1 + prime_eligible * 0.1`

- **Faceted search:** Elasticsearch aggregations for filters (brand counts, price ranges, star ratings). Computed in same query


## Flow 2: Checkout & Order Placement


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1150 500" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ffaa00">
</polygon>
</marker>
</defs>


<rect x="20" y="200" width="110" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="75" y="235" text-anchor="middle" fill="white" font-size="13">
User
</text>


<rect x="170" y="200" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="235" y="235" text-anchor="middle" fill="white" font-size="13">
Order Service
</text>


<rect x="350" y="100" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="420" y="130" text-anchor="middle" fill="white" font-size="12">
Inventory Svc
</text>


<rect x="350" y="200" width="140" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="420" y="230" text-anchor="middle" fill="white" font-size="12">
Payment Svc
</text>


<rect x="350" y="300" width="140" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="420" y="330" text-anchor="middle" fill="white" font-size="12">
Pricing/Tax Svc
</text>


<rect x="350" y="400" width="140" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="420" y="430" text-anchor="middle" fill="white" font-size="12">
Shipping Svc
</text>


<rect x="560" y="100" width="130" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="625" y="130" text-anchor="middle" fill="white" font-size="12">
Warehouse DB
</text>


<rect x="560" y="200" width="130" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="625" y="228" text-anchor="middle" fill="white" font-size="11">
Payment Gateway
</text>
<text x="625" y="243" text-anchor="middle" fill="white" font-size="10">
(Stripe/Internal)
</text>


<rect x="560" y="300" width="130" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="625" y="330" text-anchor="middle" fill="white" font-size="12">
Kafka
</text>


<rect x="770" y="200" width="140" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="840" y="230" text-anchor="middle" fill="white" font-size="12">
Notification Svc
</text>


<rect x="770" y="300" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="840" y="330" text-anchor="middle" fill="white" font-size="12">
Fulfillment Svc
</text>


<ellipse cx="840" cy="125" rx="65" ry="25" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="840" y="130" text-anchor="middle" fill="white" font-size="11">
Order DB
</text>


<line x1="130" y1="230" x2="168" y2="230" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="300" y1="215" x2="348" y2="130" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="300" y1="230" x2="348" y2="225" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="300" y1="240" x2="348" y2="320" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="300" y1="250" x2="348" y2="420" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="490" y1="125" x2="558" y2="125" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="490" y1="225" x2="558" y2="225" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="490" y1="330" x2="558" y2="325" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="690" y1="325" x2="768" y2="225" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<line x1="690" y1="325" x2="768" y2="325" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="690" y1="115" x2="773" y2="118" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow2)">
</line>


<text x="560" y="30" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Amazon: Checkout & Order Placement Flow
</text>


</svg>

</details>


### Step-by-Step (Saga Pattern)


1. **User clicks "Place Order"** → Order Service creates order with status "pending"

2. **Step 1 — Reserve Inventory:** Inventory Service does `UPDATE inventory SET reserved = reserved + qty WHERE product_id = X AND available - reserved >= qty` (optimistic lock)

3. **Step 2 — Calculate Price + Tax:** Pricing Service calculates subtotal, applicable discounts/coupons, sales tax (varies by state/country)

4. **Step 3 — Process Payment:** Payment Service charges customer's card via payment gateway. Idempotency key = order_id

5. **Step 4 — Confirm Order:** If payment succeeds → order status "confirmed". If fails → compensating action: release inventory reservation

6. **Step 5 — Publish event:** "order.confirmed" to Kafka → Notification Service emails customer, Fulfillment Service begins warehouse picking

7. **Shipping Service:** Calculates delivery date based on warehouse proximity, shipping method (Prime 1-day, 2-day, standard)


> **
**Example:** User orders 2x "Sony WH-1000XM5" ($348 each). Order Service creates order #ORD-789. Inventory Service reserves 2 units at warehouse SEA-3 (closest to user in Seattle). Price: $696 + $62.64 tax = $758.64. Payment Service charges Visa ending 4242 via internal payment gateway. Payment succeeds → order status "confirmed" → Kafka event → email confirmation → Fulfillment Service starts pick/pack at SEA-3 → estimated delivery: tomorrow by 9PM (Prime member). If payment had failed → inventory reservation released within 5 seconds (compensating transaction).


### Deep Dive: Inventory Service


- **Challenge:** Prevent overselling during flash sales (100K users buying same product simultaneously)

- **Solution:** Pessimistic locking on hot items (SELECT FOR UPDATE). For normal items, optimistic locking (version column with CAS)

- **Distributed inventory:** Stock tracked per-warehouse. "Reserve closest warehouse" strategy minimizes shipping distance/cost

- **Redis counter:** For hot items, maintain real-time available count in Redis. Decrement atomically. If Redis says 0, reject immediately without hitting DB


### Deep Dive: Saga Pattern (Distributed Transaction)


- **Why Saga:** Order placement spans 4 services (inventory, pricing, payment, fulfillment). Traditional 2PC too slow and locks resources

- **Orchestrator:** Order Service acts as saga orchestrator. Calls each service sequentially. On failure, executes compensating actions in reverse order

- **Compensating actions:** Payment failed → release inventory. Inventory failed → cancel order. Each step is idempotent


## Combined Overall Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 650" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ff9900">
</polygon>
</marker>
</defs>


<rect x="20" y="280" width="100" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="70" y="315" text-anchor="middle" fill="white" font-size="12">
Clients
</text>


<rect x="160" y="280" width="110" height="50" rx="8" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="215" y="310" text-anchor="middle" fill="white" font-size="12">
CDN
</text>


<rect x="160" y="370" width="110" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="215" y="400" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>


<rect x="330" y="60" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="390" y="85" text-anchor="middle" fill="white" font-size="11">
Search Svc
</text>


<rect x="330" y="130" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="390" y="155" text-anchor="middle" fill="white" font-size="11">
Catalog Svc
</text>


<rect x="330" y="200" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="390" y="225" text-anchor="middle" fill="white" font-size="11">
Cart Svc
</text>


<rect x="330" y="270" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="390" y="295" text-anchor="middle" fill="white" font-size="11">
Order Svc
</text>


<rect x="330" y="340" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="390" y="365" text-anchor="middle" fill="white" font-size="11">
Inventory Svc
</text>


<rect x="330" y="410" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="390" y="435" text-anchor="middle" fill="white" font-size="11">
Payment Svc
</text>


<rect x="330" y="480" width="120" height="40" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="390" y="505" text-anchor="middle" fill="white" font-size="11">
Review Svc
</text>


<rect x="330" y="550" width="120" height="40" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="390" y="575" text-anchor="middle" fill="white" font-size="11">
Recommend Svc
</text>


<rect x="530" y="60" width="120" height="40" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="590" y="85" text-anchor="middle" fill="white" font-size="11">
Elasticsearch
</text>


<rect x="530" y="130" width="120" height="40" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="590" y="155" text-anchor="middle" fill="white" font-size="11">
Product Cache
</text>


<rect x="530" y="200" width="120" height="40" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="590" y="225" text-anchor="middle" fill="white" font-size="11">
Cart Cache
</text>


<rect x="530" y="340" width="120" height="40" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="590" y="365" text-anchor="middle" fill="white" font-size="11">
Kafka Bus
</text>


<ellipse cx="740" cy="80" rx="60" ry="22" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="740" y="85" text-anchor="middle" fill="white" font-size="10">
Product DB
</text>


<ellipse cx="740" cy="155" rx="60" ry="22" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="740" y="160" text-anchor="middle" fill="white" font-size="10">
Order DB
</text>


<ellipse cx="740" cy="230" rx="60" ry="22" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="740" y="235" text-anchor="middle" fill="white" font-size="10">
Inventory DB
</text>


<ellipse cx="740" cy="305" rx="60" ry="22" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="740" y="310" text-anchor="middle" fill="white" font-size="10">
Review DB
</text>


<rect x="530" y="430" width="120" height="40" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="590" y="455" text-anchor="middle" fill="white" font-size="11">
Fulfillment
</text>


<rect x="530" y="500" width="120" height="40" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="590" y="525" text-anchor="middle" fill="white" font-size="11">
Notification
</text>


<rect x="530" y="570" width="120" height="40" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="590" y="595" text-anchor="middle" fill="white" font-size="11">
ML Features
</text>


<rect x="870" y="70" width="100" height="40" rx="8" fill="#888" stroke="#aaa" stroke-width="2">
</rect>
<text x="920" y="95" text-anchor="middle" fill="white" font-size="11">
S3 (Media)
</text>


<line x1="120" y1="300" x2="158" y2="300" stroke="#ff9900" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="120" y1="320" x2="158" y2="390" stroke="#ff9900" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="395" x2="328" y2="80" stroke="#ff9900" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="395" x2="328" y2="150" stroke="#ff9900" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="395" x2="328" y2="220" stroke="#ff9900" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="270" y1="395" x2="328" y2="290" stroke="#ff9900" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="450" y1="80" x2="528" y2="80" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="450" y1="150" x2="528" y2="150" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="450" y1="220" x2="528" y2="220" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="450" y1="290" x2="528" y2="355" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="650" y1="80" x2="678" y2="80" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow3)">
</line>


<line x1="650" y1="360" x2="678" y2="155" stroke="#ff6b6b" stroke-width="1" marker-end="url(#arrow3)">
</line>


<line x1="650" y1="360" x2="678" y2="230" stroke="#ff6b6b" stroke-width="1" marker-end="url(#arrow3)">
</line>


<text x="600" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Amazon: Combined Overall Architecture
</text>


</svg>

</details>


> **
**Example — Full purchase journey:** User searches "laptop stand" → Elasticsearch returns 200 results → Product Cache hydrates top 48 → Recommendation Service adds "frequently bought together: USB hub" → User adds laptop stand to cart (Cart Service → Redis) → Checkout → Order Service orchestrates saga: Inventory reserves 1 unit at warehouse ORD-1 → Payment charges $29.99 + $2.40 tax → Order confirmed → Kafka event → email sent → Fulfillment starts pick/pack → UPS label generated → Tracking number emailed → Delivered next day.


## Database Schema


### SQL — PostgreSQL/Aurora (Orders, Users — ACID required)


| Table               | Column        | Type                                                       | Details |
|---------------------|---------------|------------------------------------------------------------|---------|
| **orders**          | order_id      | VARCHAR(20)                                                | `PK`    |
| user_id             | BIGINT        | `FK`  `IDX`                                                |         |
| status              | ENUM          | pending, confirmed, shipped, delivered, canceled, returned |         |
| total_amount        | BIGINT        | In cents                                                   |         |
| shipping_address_id | BIGINT        | `FK`                                                       |         |
| idempotency_key     | VARCHAR(64)   | `IDX`                                                      |         |
| created_at          | TIMESTAMP     | `IDX`                                                      |         |
| **order_items**     | order_item_id | BIGSERIAL                                                  | `PK`    |
| order_id            | VARCHAR(20)   | `FK`  `IDX`                                                |         |
| product_id          | BIGINT        | `FK`                                                       |         |
| quantity            | INT           |                                                            |         |
| unit_price          | BIGINT        | Price at time of purchase (snapshot)                       |         |
| **inventory**       | product_id    | BIGINT                                                     | `PK`    |
| warehouse_id        | VARCHAR(10)   | `PK`                                                       |         |
| available_qty       | INT           |                                                            |         |
| reserved_qty        | INT           |                                                            |         |
| version             | BIGINT        | Optimistic locking                                         |         |


### NoSQL — DynamoDB (Products, Reviews — high read throughput)


| Table         | Column     | Type                               | Details |
|---------------|------------|------------------------------------|---------|
| **products**  | product_id | BIGINT                             | `PK`    |
| title         | STRING     |                                    |         |
| description   | STRING     |                                    |         |
| price         | NUMBER     | In cents                           |         |
| category_path | STRING     | "Electronics > Audio > Headphones" |         |
| seller_id     | BIGINT     | `IDX`                              |         |
| avg_rating    | NUMBER     | Denormalized (1.0-5.0)             |         |
| image_urls    | LIST       | CDN URLs                           |         |
| **reviews**   | product_id | BIGINT                             | `PK`    |
| review_id     | STRING     | `PK`                               |         |
| user_id       | BIGINT     |                                    |         |
| rating        | NUMBER     | 1-5                                |         |
| review_text   | STRING     |                                    |         |
| helpful_votes | NUMBER     |                                    |         |


### Cart — Redis


| Key              | Type | Value                                |
|------------------|------|--------------------------------------|
| `cart:{user_id}` | Hash | product_id → {qty, price, seller_id} |


Sharding Strategy


- **orders:** Shard by `user_id` — "my orders" query is shard-local. order_id uses Snowflake (time-sortable)

- **inventory:** Shard by `product_id` — inventory checks for a specific product hit one shard

- **products (DynamoDB):** Partition by `product_id` — even distribution. GSI on `seller_id` for seller dashboard

- **reviews:** Partition by `product_id` — all reviews for a product co-located (optimal for product page display)


### Denormalization


- `products.avg_rating` — denormalized from reviews. Updated async via Kafka consumer when new review submitted

- `order_items.unit_price` — snapshot of price at purchase time (price may change later, order must reflect original price)

- Elasticsearch documents duplicate product data (title, price, rating) for search — avoids joins during search queries


### Indexes


- `orders.user_id` — B-tree index for "my orders" listing (ORDER BY created_at DESC)

- `orders.idempotency_key` — Unique index to prevent duplicate order placement on retry

- `inventory(product_id, warehouse_id)` — Composite PK for per-warehouse stock lookup

- `products.seller_id` — DynamoDB GSI for seller product management dashboard


## Cache Deep Dive


| Cache                 | Strategy      | Eviction | TTL            | Purpose                            |
|-----------------------|---------------|----------|----------------|------------------------------------|
| Product Cache (Redis) | Read-through  | LRU      | 1 hour         | Product details for catalog pages  |
| Cart (Redis)          | Write-through | TTL      | 30 days        | Shopping cart persistence          |
| Session (Redis)       | Write-through | TTL      | 24 hours       | User auth sessions                 |
| Inventory Hot (Redis) | Write-through | None     | Real-time sync | Fast stock check for popular items |
| Search Cache (Redis)  | Read-through  | LRU      | 5 min          | Common search query results        |


### CDN Deep Dive


- **What's cached:** Product images (multiple sizes: thumbnail, gallery, zoom), static assets, JS/CSS bundles

- **Strategy:** Push-based for popular products. Pull-based for long-tail

- **Image optimization:** WebP format for supported browsers (30% smaller). Lazy loading for below-fold images

- **TTL:** Product images: 365 days (immutable, URL includes hash). HTML pages: NOT cached (dynamic pricing, personalization)

- **Edge computing:** Some personalization logic (A/B tests, geo-pricing) runs at CDN edge via Lambda@Edge


## Scaling Considerations


- **Prime Day handling:** Pre-warm all caches 24 hours before. Auto-scale all services to 3-5x normal capacity. Inventory pre-allocated to multiple warehouses. Circuit breakers on non-essential services (recommendations can degrade gracefully)

- **Database scaling:** Aurora with read replicas for order queries. DynamoDB auto-scaling for product reads. Elasticsearch horizontal scaling (add shards)

- **Cart durability:** Redis with AOF persistence + async replication. If Redis node fails, cart data recovered from AOF log. For logged-in users, cart also periodically snapshot to DynamoDB

- **Microservices independence:** Each service owns its database (database-per-service pattern). No shared DB. Communication via REST/gRPC + Kafka events


## Tradeoffs & Deep Dives


> **
✅ Saga Pattern
- No distributed locks — each service manages its own transactions
- Better availability — partial failures don't block entire system
- Compensating transactions handle rollback


❌ Saga Downsides
- Complexity: must implement compensating transaction for every step
- Eventual consistency: brief window where inventory reserved but payment pending
- Debugging distributed failures is harder than single DB transaction


### Message Queue (Kafka) Deep Dive


- **Topics:** order-events, inventory-events, payment-events, review-events, user-activity

- **Partitioning:** order-events by order_id, inventory-events by product_id

- **Consumers:** Notification Service, Fulfillment Service, Analytics, Search Indexer (for real-time Elasticsearch updates)

- **Exactly-once:** Kafka transactions + idempotent producers to prevent duplicate order processing


## Alternative Approaches


- **2-Phase Commit (2PC):** Instead of Saga. Stronger consistency guarantee but: coordinator is single point of failure, locks held during entire transaction (reduces throughput), doesn't scale well across services

- **CQRS for Orders:** Separate write model (PostgreSQL for order placement) from read model (Elasticsearch for order search/analytics). Write path optimized for consistency, read path optimized for query flexibility

- **GraphQL instead of REST:** Single query can fetch product + reviews + recommendations. Reduces number of API calls from client. Tradeoff: more complex caching (each query is unique)

- **Event Sourcing for Inventory:** Store every inventory change as event (received, reserved, shipped, returned). Derive current stock from event replay. Better audit trail, easier debugging, but requires snapshotting for read performance


## Additional Information


- **Price consistency:** Product price cached aggressively but cart always re-validates prices at checkout time. If price changed between add-to-cart and checkout, customer is notified

- **Search ranking personalization:** Same query returns different results per user. Purchase history, browse history, and demographic data influence Elasticsearch boost factors

- **Warehouse selection algorithm:** Minimize: shipping cost + delivery time. Consider: warehouse proximity to customer, item availability, current warehouse load, carrier rates

- **Review authenticity:** ML model detects fake reviews (linguistic patterns, reviewer behavior, purchase verification). Verified Purchase badge requires actual order for that product