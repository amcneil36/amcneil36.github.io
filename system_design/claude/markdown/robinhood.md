# System Design: Robinhood


## Functional Requirements


  1. **Buy/sell stocks** – Users can place market, limit, and stop orders for stocks, ETFs, options, and crypto.

  2. **Real-time portfolio** – Display portfolio value with live price updates.

  3. **Real-time stock prices** – Stream live prices for watchlist and portfolio stocks.

  4. **Order management** – View pending orders, order history, and order status (pending, filled, cancelled).

  5. **Account management** – Deposit/withdraw funds, view balance, buying power.

  6. **Fractional shares** – Buy a fraction of a share (e.g., $10 worth of AAPL).

  7. **News & research** – Show stock-related news, analyst ratings, and company info.

  8. **Notifications** – Price alerts, order fills, account activity.


## Non-Functional Requirements


  1. **Strong consistency** – Order execution and account balances require ACID guarantees. No double-spending.

  2. **Low latency** – Order placement <100ms; price updates <200ms.

  3. **High availability** – 99.99% during market hours; any downtime means missed trades.

  4. **Regulatory compliance** – SEC/FINRA compliance; audit trails for all transactions.

  5. **Scalability** – Handle millions of concurrent users during volatile market events.


## Flow 1: Placing a Stock Order


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 450" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a1" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="170" width="80" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="50" y="200" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
User
</text>

  
<line x1="90" y1="195" x2="150" y2="195" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<text x="120" y="185" text-anchor="middle" fill="#555" font-size="8">
POST /order
</text>

  
<rect x="150" y="170" width="80" height="50" rx="8" fill="#ff9800" stroke="#f57c00" stroke-width="2">
</rect>

  
<text x="190" y="193" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
API
</text>

  
<text x="190" y="205" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
GW
</text>

  
<line x1="230" y1="195" x2="300" y2="195" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="300" y="165" width="110" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="355" y="193" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Order
</text>

  
<text x="355" y="208" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Service
</text>

  

  
<line x1="355" y1="165" x2="355" y2="90" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<rect x="305" y="40" width="100" height="50" rx="8" fill="#e91e63" stroke="#ad1457" stroke-width="2">
</rect>

  
<text x="355" y="63" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Risk &
</text>

  
<text x="355" y="78" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Validation
</text>

  

  
<line x1="405" y1="65" x2="480" y2="65" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="530" cy="58" rx="40" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="490" y="58" width="80" height="22" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="530" cy="80" rx="40" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="490" y1="58" x2="490" y2="80" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="570" y1="58" x2="570" y2="80" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="530" y="73" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Account DB
</text>

  

  
<line x1="410" y1="195" x2="490" y2="195" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="490" y="170" width="100" height="50" rx="8" fill="#9c27b0" stroke="#6a1b9a" stroke-width="2">
</rect>

  
<text x="540" y="193" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Order
</text>

  
<text x="540" y="208" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Queue
</text>

  

  
<line x1="590" y1="195" x2="670" y2="195" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<rect x="670" y="165" width="120" height="60" rx="8" fill="#673ab7" stroke="#4527a0" stroke-width="2">
</rect>

  
<text x="730" y="193" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Order Router /
</text>

  
<text x="730" y="208" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Matching Engine
</text>

  

  
<line x1="790" y1="195" x2="870" y2="195" stroke="#333" stroke-width="2" marker-end="url(#a1)">
</line>

  
<text x="830" y="185" text-anchor="middle" fill="#555" font-size="8">
FIX/TCP
</text>

  
<rect x="870" y="170" width="100" height="50" rx="8" fill="#795548" stroke="#4e342e" stroke-width="2">
</rect>

  
<text x="920" y="200" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Exchange
</text>

  

  
<line x1="355" y1="225" x2="355" y2="300" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<ellipse cx="355" cy="310" rx="45" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="310" y="310" width="90" height="25" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="355" cy="335" rx="45" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="310" y1="310" x2="310" y2="335" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="400" y1="310" x2="400" y2="335" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="355" y="327" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Order DB
</text>

  

  
<line x1="920" y1="220" x2="920" y2="310" stroke="#333" stroke-width="1.5" marker-end="url(#a1)">
</line>

  
<text x="920" y="270" text-anchor="start" fill="#555" font-size="8">
 Fill report
</text>

  
<rect x="870" y="310" width="100" height="40" rx="6" fill="#00bcd4" stroke="#00838f" stroke-width="1.5">
</rect>

  
<text x="920" y="335" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Settlement Svc
</text>

  

  
<line x1="870" y1="330" x2="570" y2="80" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a1)">
</line>

  
<text x="720" y="200" text-anchor="middle" fill="#555" font-size="7">
</text>

  

  
<line x1="730" y1="225" x2="730" y2="300" stroke="#333" stroke-width="1" stroke-dasharray="4,3" marker-end="url(#a1)">
</line>

  
<rect x="685" y="300" width="90" height="35" rx="6" fill="#e91e63" stroke="#ad1457" stroke-width="1.5">
</rect>

  
<text x="730" y="322" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Notif Svc
</text>


</svg>

</details>


> **
Example 1: Market buy order


Alice taps "Buy" on AAPL, enters 10 shares, selects "Market Order", and taps "Submit". Client sends **POST /api/v1/orders** with `{symbol: "AAPL", side: "BUY", type: "MARKET", quantity: 10}`. API Gateway → **Order Service**. The **Risk & Validation** module checks: (1) Alice has sufficient buying power ($1,785.20 needed at $178.52/share — checked against **Account DB**), (2) order is within daily limits, (3) market is open. Validation passes. Order is saved to **Order DB** with status=PENDING and buying power is held (reserved). The order is queued in the **Order Queue** (Kafka). The **Order Router** sends the order to the **NYSE/NASDAQ** via FIX protocol over TCP. The exchange fills the order at $178.50. A fill report comes back → **Settlement Service** updates Alice's account: deducts $1,785.00, credits 10 shares of AAPL. Order status → FILLED. **Notification Service** sends push: "Your order for 10 shares of AAPL was filled at $178.50."


> **
Example 2: Limit order (not immediately filled)


Bob places a limit order: Buy TSLA at $240 (current price: $248). The order passes validation (buying power reserved). It's sent to the exchange, which places it on the order book. The order remains PENDING. Hours later, TSLA drops to $240 and the order fills. The exchange sends a fill report → Settlement updates Bob's account. Push notification sent.


### Deep Dive: Components


> **
Order Service


**Protocol:** HTTPS POST (REST). Input: `{symbol, side, type, quantity, limit_price?}`. Output: `{order_id, status, estimated_price}`.


Implements **idempotency** via client-provided idempotency keys to prevent duplicate orders from network retries.


> **
Risk & Validation


Pre-trade risk checks: buying power verification (with reserved funds), pattern day trader rules, position limits, market hours check, symbol trading halt check. Uses **strong consistency** — reads the latest account balance from the primary DB (not a replica) to prevent double-spending.


> **
Order Router / Matching Engine


Routes orders to the best exchange (NYSE, NASDAQ, dark pools) based on price improvement and execution quality (NBBO — National Best Bid and Offer). Communicates via **FIX protocol over TCP** — the standard for electronic trading. Uses persistent TCP connections with heartbeats for reliability.


> **
Settlement Service


Processes fill reports from exchanges. In an atomic transaction: deducts cash from buyer's account, credits shares; deducts shares from seller's account, credits cash. Writes to the **Account DB** and **Transaction Ledger** (append-only audit log). Standard settlement is T+1 (trade date + 1 business day), but Robinhood provides instant access to funds.


## Flow 2: Real-Time Portfolio View


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 850 250" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a2" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="10" y="90" width="80" height="50" rx="8" fill="#4caf50" stroke="#388e3c" stroke-width="2">
</rect>

  
<text x="50" y="120" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
User
</text>

  
<line x1="90" y1="115" x2="160" y2="115" stroke="#333" stroke-width="2" marker-end="url(#a2)">
</line>

  
<text x="125" y="105" text-anchor="middle" fill="#555" font-size="8">
WebSocket
</text>

  
<rect x="160" y="85" width="100" height="60" rx="8" fill="#2196f3" stroke="#1565c0" stroke-width="2">
</rect>

  
<text x="210" y="110" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Portfolio
</text>

  
<text x="210" y="125" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">
Service
</text>

  
<line x1="260" y1="105" x2="340" y2="60" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="310" y="70" text-anchor="middle" fill="#555" font-size="8">
Holdings
</text>

  
<ellipse cx="390" cy="50" rx="40" ry="10" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<rect x="350" y="50" width="80" height="20" fill="#ff5722" stroke="#d84315" stroke-width="1.5">
</rect>

  
<ellipse cx="390" cy="70" rx="40" ry="10" fill="#e64a19" stroke="#d84315" stroke-width="1.5">
</ellipse>

  
<line x1="350" y1="50" x2="350" y2="70" stroke="#d84315" stroke-width="1.5">
</line>

  
<line x1="430" y1="50" x2="430" y2="70" stroke="#d84315" stroke-width="1.5">
</line>

  
<text x="390" y="64" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Account DB
</text>

  
<line x1="260" y1="125" x2="340" y2="165" stroke="#333" stroke-width="1.5" marker-end="url(#a2)">
</line>

  
<text x="310" y="160" text-anchor="middle" fill="#555" font-size="8">
Live prices
</text>

  
<ellipse cx="390" cy="160" rx="40" ry="10" fill="#009688" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<rect x="350" y="160" width="80" height="20" fill="#009688" stroke="#00695c" stroke-width="1.5">
</rect>

  
<ellipse cx="390" cy="180" rx="40" ry="10" fill="#00796b" stroke="#00695c" stroke-width="1.5">
</ellipse>

  
<line x1="350" y1="160" x2="350" y2="180" stroke="#00695c" stroke-width="1.5">
</line>

  
<line x1="430" y1="160" x2="430" y2="180" stroke="#00695c" stroke-width="1.5">
</line>

  
<text x="390" y="174" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Price Cache
</text>


</svg>

</details>


> **
Example: Portfolio dashboard


Alice opens Robinhood. The app establishes a WebSocket connection. The **Portfolio Service** fetches Alice's holdings from the **Account DB** (AAPL: 10 shares, TSLA: 5 shares) and current prices from the **Price Cache** (Redis). Calculates total portfolio value: (10 × $178.50) + (5 × $248.00) = $3,025.00. Streams live price updates via WebSocket — as AAPL changes to $179.00, the portfolio value updates to $3,030.00 in real time.


## Overall Combined Flow


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="a3" viewbox="0 0 10 10" refx="9" refy="5" markerwidth="6" markerheight="6" orient="auto">
<path d="M0 0L10 5L0 10z" fill="#333">
</path>
</marker>
</defs>

  
<rect x="5" y="140" width="70" height="40" rx="6" fill="#4caf50" stroke="#388e3c" stroke-width="1.5">
</rect>

  
<text x="40" y="164" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">
Client
</text>

  
<line x1="75" y1="160" x2="120" y2="160" stroke="#333" stroke-width="1.5" marker-end="url(#a3)">
</line>

  
<rect x="120" y="140" width="65" height="40" rx="6" fill="#ff9800" stroke="#f57c00" stroke-width="1.5">
</rect>

  
<text x="152" y="164" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
API GW
</text>

  
<line x1="185" y1="155" x2="230" y2="100" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<line x1="185" y1="165" x2="230" y2="215" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="230" y="80" width="85" height="40" rx="6" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="272" y="104" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Order Svc
</text>

  
<rect x="230" y="200" width="85" height="40" rx="6" fill="#2196f3" stroke="#1565c0" stroke-width="1.5">
</rect>

  
<text x="272" y="224" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Portfolio Svc
</text>

  
<line x1="315" y1="100" x2="360" y2="55" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="360" y="35" width="75" height="35" rx="5" fill="#e91e63" stroke="#ad1457" stroke-width="1">
</rect>

  
<text x="397" y="57" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Risk Chk
</text>

  
<line x1="315" y1="100" x2="360" y2="100" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="360" y="80" width="80" height="40" rx="6" fill="#9c27b0" stroke="#6a1b9a" stroke-width="1.5">
</rect>

  
<text x="400" y="104" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Order Q
</text>

  
<line x1="440" y1="100" x2="490" y2="100" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="490" y="80" width="80" height="40" rx="6" fill="#673ab7" stroke="#4527a0" stroke-width="1.5">
</rect>

  
<text x="530" y="104" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Router
</text>

  
<line x1="570" y1="100" x2="620" y2="100" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="620" y="80" width="80" height="40" rx="6" fill="#795548" stroke="#4e342e" stroke-width="1.5">
</rect>

  
<text x="660" y="104" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Exchange
</text>

  
<line x1="660" y1="120" x2="660" y2="165" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="620" y="165" width="80" height="35" rx="5" fill="#00bcd4" stroke="#00838f" stroke-width="1">
</rect>

  
<text x="660" y="187" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Settlement
</text>

  
<line x1="620" y1="182" x2="540" y2="182" stroke="#333" stroke-width="1" stroke-dasharray="3,2" marker-end="url(#a3)">
</line>

  
<ellipse cx="500" cy="178" rx="35" ry="8" fill="#ff5722" stroke="#d84315" stroke-width="1">
</ellipse>

  
<rect x="465" y="178" width="70" height="14" fill="#ff5722" stroke="#d84315" stroke-width="1">
</rect>

  
<ellipse cx="500" cy="192" rx="35" ry="8" fill="#e64a19" stroke="#d84315" stroke-width="1">
</ellipse>

  
<line x1="465" y1="178" x2="465" y2="192" stroke="#d84315" stroke-width="1">
</line>

  
<line x1="535" y1="178" x2="535" y2="192" stroke="#d84315" stroke-width="1">
</line>

  
<text x="500" y="189" text-anchor="middle" fill="#fff" font-size="6" font-weight="bold">
Account DB
</text>

  

  
<rect x="5" y="280" width="80" height="35" rx="5" fill="#795548" stroke="#4e342e" stroke-width="1">
</rect>

  
<text x="45" y="302" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Exchanges
</text>

  
<line x1="85" y1="297" x2="130" y2="297" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="130" y="280" width="80" height="35" rx="5" fill="#2196f3" stroke="#1565c0" stroke-width="1">
</rect>

  
<text x="170" y="302" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Ingester
</text>

  
<line x1="210" y1="297" x2="260" y2="297" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="260" y="280" width="70" height="35" rx="5" fill="#9c27b0" stroke="#6a1b9a" stroke-width="1">
</rect>

  
<text x="295" y="302" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
Kafka
</text>

  
<line x1="330" y1="290" x2="380" y2="260" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<ellipse cx="415" cy="253" rx="30" ry="7" fill="#009688" stroke="#00695c" stroke-width="1">
</ellipse>

  
<rect x="385" y="253" width="60" height="13" fill="#009688" stroke="#00695c" stroke-width="1">
</rect>

  
<ellipse cx="415" cy="266" rx="30" ry="7" fill="#00796b" stroke="#00695c" stroke-width="1">
</ellipse>

  
<line x1="385" y1="253" x2="385" y2="266" stroke="#00695c" stroke-width="1">
</line>

  
<line x1="445" y1="253" x2="445" y2="266" stroke="#00695c" stroke-width="1">
</line>

  
<text x="415" y="263" text-anchor="middle" fill="#fff" font-size="6" font-weight="bold">
Price$
</text>

  
<line x1="330" y1="297" x2="380" y2="297" stroke="#333" stroke-width="1" marker-end="url(#a3)">
</line>

  
<rect x="380" y="283" width="80" height="30" rx="5" fill="#e91e63" stroke="#ad1457" stroke-width="1">
</rect>

  
<text x="420" y="302" text-anchor="middle" fill="#fff" font-size="8" font-weight="bold">
WS Hub
</text>

  
<line x1="420" y1="283" x2="272" y2="240" stroke="#333" stroke-width="1" stroke-dasharray="3,2">
</line>


</svg>

</details>


> **
Combined Example


**Trading:** Client → POST /order → API GW → Order Svc → Risk Check (buying power in Account DB) → Order Queue → Router → FIX/TCP → Exchange → Fill → Settlement → Update Account DB → Push Notification.
  
**Market Data:** Exchanges → Ingester → Kafka → Price Cache (Redis) + WebSocket Hub → streams to Portfolio Svc and directly to clients.


## Database Schema


### SQL (PostgreSQL — ACID required for financial data)

1. accounts

  
  
  
  
  
  
  


| Field        | Type          | Key         | Description                        |
|--------------|---------------|-------------|------------------------------------|
| account_id   | BIGINT        | `PK`        | Unique account ID                  |
| user_id      | BIGINT        | `FK`  `IDX` | Account owner                      |
| cash_balance | DECIMAL(15,2) |             | Available cash                     |
| buying_power | DECIMAL(15,2) |             | Cash - reserved for pending orders |
| account_type | ENUM          |             | INDIVIDUAL, MARGIN, IRA            |
| status       | ENUM          |             | ACTIVE, FROZEN, CLOSED             |


**Why SQL:** Financial data requires ACID. cash_balance and buying_power must be updated atomically to prevent double-spending. Uses `SELECT ... FOR UPDATE` for row-level locking during order placement.


2. orders

  
  
  
  
  
  
  
  
  
  
  
  
  


| Field           | Type                          | Key         | Description                                  |
|-----------------|-------------------------------|-------------|----------------------------------------------|
| order_id        | BIGINT                        | `PK`        | Unique order ID                              |
| account_id      | BIGINT                        | `FK`  `IDX` | Account placing the order                    |
| symbol          | VARCHAR(10)                   | `IDX`       | Ticker symbol                                |
| side            | ENUM('BUY','SELL')            |             | Order side                                   |
| type            | ENUM('MARKET','LIMIT','STOP') |             | Order type                                   |
| quantity        | DECIMAL(15,6)                 |             | Shares (supports fractional)                 |
| limit_price     | DECIMAL(10,2)                 |             | Limit price (nullable for market orders)     |
| filled_price    | DECIMAL(10,2)                 |             | Actual execution price                       |
| status          | ENUM                          | `IDX`       | PENDING, FILLED, PARTIALLY_FILLED, CANCELLED |
| idempotency_key | VARCHAR(64)                   | `IDX`       | Prevents duplicate orders                    |
| created_at      | TIMESTAMP                     |             | Order creation time                          |
| filled_at       | TIMESTAMP                     |             | Execution time                               |


**Index:** Hash index on `idempotency_key` for O(1) duplicate detection. B-tree on `(account_id, created_at DESC)` for order history queries. B-tree on `status` for finding pending orders.


**Sharding:** By `account_id` — all orders for an account are on the same shard, enabling efficient history queries and atomic balance updates in the same transaction.


3. holdings

  
  
  
  
  


| Field      | Type          | Key        | Description                  |
|------------|---------------|------------|------------------------------|
| account_id | BIGINT        | `PK`  `FK` | Account                      |
| symbol     | VARCHAR(10)   | `PK`       | Ticker                       |
| quantity   | DECIMAL(15,6) |            | Shares held                  |
| avg_cost   | DECIMAL(10,2) |            | Average cost basis per share |


4. transaction_ledger (append-only audit log)

  
  
  
  
  
  
  
  


| Field            | Type          | Key  | Description                              |
|------------------|---------------|------|------------------------------------------|
| ledger_id        | BIGINT        | `PK` | Auto-increment                           |
| account_id       | BIGINT        | `FK` | Account                                  |
| transaction_type | ENUM          |      | BUY, SELL, DEPOSIT, WITHDRAWAL, DIVIDEND |
| amount           | DECIMAL(15,2) |      | Dollar amount                            |
| symbol           | VARCHAR(10)   |      | Ticker (nullable for deposits)           |
| quantity         | DECIMAL(15,6) |      | Shares (nullable for deposits)           |
| timestamp        | TIMESTAMP     |      | Transaction time                         |


**Append-only:** Never updated or deleted. Required for regulatory compliance (SEC audit trail). Immutable ledger.


## Cache Deep Dive


> **
Price Cache (Redis)


Same as stock ticker design: updated on every price tick from Kafka. Used for portfolio value calculation.


**Strategy:** Write-through. No TTL during market hours.


> **
CDN


**Not appropriate for trading data** (real-time, personalized). **Appropriate for**: static content (company logos, news articles), app bundles.


## Scaling Considerations


  - **Load Balancer:** L7 LB with round-robin for API GW. L4 LB for WebSocket connections.

  - **Order Service:** Stateless, horizontally scalable. During volatile events (GME squeeze), auto-scale to 10x normal capacity.

  - **Database:** Primary-replica for reads. Sharding by account_id for writes. Read replicas for portfolio queries (eventual consistency acceptable for display, not for trading).


## Tradeoffs and Deep Dives


> **
Strong Consistency vs. Availability for Orders


Order placement uses strong consistency (read from primary, row-level locks). This means slightly higher latency (~10ms) but prevents double-spending. We can't use eventual consistency here — a user could place two orders exceeding their balance if we read from a stale replica.


## Alternative Approaches


Event Sourcing for Account Balance


Instead of updating the balance directly, record all events (deposits, fills, withdrawals) and compute the balance by replaying events. More complex but provides a complete audit trail. Hybrid approach: use event sourcing for the ledger but maintain a materialized view of the current balance for fast queries.


## Additional Information


### Circuit Breakers


If the exchange connection drops, the Order Router activates a circuit breaker — new orders are queued and retried when the connection recovers. Prevents cascading failures.


### Fractional Shares


Robinhood handles fractional shares internally. When Alice buys $10 of AAPL ($178/share), Robinhood buys whole shares in bulk and allocates fractional ownership. Stored as `quantity: 0.056180` in the holdings table.