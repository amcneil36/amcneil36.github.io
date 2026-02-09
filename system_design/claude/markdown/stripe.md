# System Design: Stripe (Payment Processing Platform)


## Functional Requirements


- Merchants can accept payments via credit/debit cards, bank transfers, and digital wallets

- Support one-time charges, subscriptions (recurring billing), and payment intents (multi-step checkout)

- Refund processing (full and partial)

- Webhook notifications for payment events (succeeded, failed, refunded)

- Multi-currency support with automatic currency conversion

- Fraud detection and prevention

- PCI-DSS compliant card data tokenization


## Non-Functional Requirements


- **Consistency:** Strong consistency — payment records must never be lost or double-charged (ACID transactions)

- **Availability:** 99.999% uptime (financial system — downtime = lost revenue for merchants)

- **Latency:** <2 seconds for payment processing end-to-end

- **Idempotency:** Every API call must be idempotent (retries must not cause double charges)

- **Security:** PCI-DSS Level 1 compliance, tokenization, encryption at rest and in transit

- **Scalability:** Handle millions of transactions per day across thousands of merchants


## Flow 1: Payment Processing (Charge a Card)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 500" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#635bff">
</polygon>
</marker>
</defs>


<rect x="20" y="200" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="80" y="228" text-anchor="middle" fill="white" font-size="12">
Customer
</text>
<text x="80" y="245" text-anchor="middle" fill="white" font-size="11">
Browser
</text>


<rect x="180" y="200" width="130" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="245" y="228" text-anchor="middle" fill="white" font-size="12">
Merchant
</text>
<text x="245" y="245" text-anchor="middle" fill="white" font-size="11">
Server
</text>


<rect x="360" y="200" width="130" height="60" rx="10" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="425" y="228" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>
<text x="425" y="245" text-anchor="middle" fill="white" font-size="11">
+ Rate Limiter
</text>


<rect x="540" y="120" width="140" height="50" rx="8" fill="#635bff" stroke="#8a85ff" stroke-width="2">
</rect>
<text x="610" y="150" text-anchor="middle" fill="white" font-size="12">
Payment Service
</text>


<rect x="540" y="250" width="140" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="610" y="280" text-anchor="middle" fill="white" font-size="12">
Fraud Engine
</text>


<rect x="540" y="360" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="610" y="390" text-anchor="middle" fill="white" font-size="12">
Token Vault
</text>


<rect x="740" y="120" width="150" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="815" y="142" text-anchor="middle" fill="white" font-size="11">
Payment Processor
</text>
<text x="815" y="158" text-anchor="middle" fill="white" font-size="10">
(Card Network)
</text>


<rect x="950" y="120" width="140" height="50" rx="8" fill="#8b4513" stroke="#cd853f" stroke-width="2">
</rect>
<text x="1020" y="142" text-anchor="middle" fill="white" font-size="11">
Issuing Bank
</text>
<text x="1020" y="158" text-anchor="middle" fill="white" font-size="10">
(Visa/MC)
</text>


<ellipse cx="815" cy="280" rx="70" ry="30" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="815" y="285" text-anchor="middle" fill="white" font-size="12">
Payment DB
</text>


<rect x="740" y="360" width="150" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="815" y="390" text-anchor="middle" fill="white" font-size="12">
Ledger Service
</text>


<line x1="140" y1="230" x2="178" y2="230" stroke="#635bff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="310" y1="230" x2="358" y2="230" stroke="#635bff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="490" y1="220" x2="538" y2="150" stroke="#635bff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="490" y1="240" x2="538" y2="270" stroke="#635bff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="610" y1="170" x2="610" y2="248" stroke="#ffaa00" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow1)">
</line>


<line x1="610" y1="300" x2="610" y2="358" stroke="#ffaa00" stroke-width="1.5" stroke-dasharray="5,5">
</line>


<line x1="680" y1="145" x2="738" y2="145" stroke="#635bff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="890" y1="145" x2="948" y2="145" stroke="#635bff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="680" y1="155" x2="743" y2="268" stroke="#ff6b6b" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow1)">
</line>


<line x1="815" y1="310" x2="815" y2="358" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow1)">
</line>


<text x="600" y="30" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Stripe: Payment Processing Flow
</text>


</svg>

</details>


### Step-by-Step


1. **Tokenize card:** Customer enters card in Stripe.js (iframe) → card sent directly to Stripe (never touches merchant server) → Token Vault returns `tok_xxx`

2. **Create PaymentIntent:** Merchant server calls `POST /v1/payment_intents` with token, amount, currency, idempotency_key

3. **API Gateway:** Validates API key, rate limits, routes to Payment Service

4. **Fraud check:** Payment Service sends transaction to Fraud Engine (Radar) → ML model scores risk (0-100). If score >80, block; 50-80 review; <50 approve

5. **Detokenize + Authorize:** Payment Service retrieves real card from Token Vault → sends authorization request to card network (Visa/MC) via ISO 8583 protocol

6. **Issuing bank responds:** Approve/decline → response flows back through card network → Payment Service

7. **Record:** Payment Service writes to Payment DB (status: succeeded/failed) and Ledger Service (double-entry bookkeeping)

8. **Webhook:** Async notification sent to merchant's webhook URL with `payment_intent.succeeded` event


> **
**Example:** Customer buys $99.99 headphones on merchant site. Stripe.js tokenizes card → `tok_abc123`. Merchant calls `POST /v1/payment_intents { amount: 9999, currency: "usd", payment_method: "tok_abc123", idempotency_key: "order_456" }`. Fraud score: 12 (low risk). Authorization sent to Visa network → Chase (issuing bank) approves. PaymentIntent status → "succeeded". Webhook fires to merchant. Merchant ships headphones.


### Deep Dive: Payment Service


- **Protocol:** HTTPS REST API (TLS 1.3)

- **HTTP Method:** `POST /v1/payment_intents`

- **Input:** `{ amount: 9999, currency: "usd", payment_method: "tok_abc", confirm: true, idempotency_key: "..." }`

- **Output:** `{ id: "pi_xxx", status: "succeeded", amount: 9999, charges: { data: [...] } }`

- **Idempotency:** idempotency_key stored with request hash. If same key + same params → return cached response. Different params → 409 Conflict


### Deep Dive: Token Vault (PCI-DSS)


- **Isolation:** Runs in a separate PCI-compliant environment (hardened network, restricted access, audit logging)

- **Encryption:** Card numbers encrypted with AES-256 at rest, per-merchant encryption keys stored in HSM (Hardware Security Module)

- **Tokenization:** `4242424242424242` → `tok_1Hk5xx2eZv`. Token is useless outside Stripe's systems

- **Why tokenization:** Merchants never handle raw card data → reduces their PCI compliance burden from SAQ D (300+ requirements) to SAQ A (22 requirements)


### Deep Dive: Fraud Engine (Radar)


- **ML Model:** Gradient Boosted Trees trained on billions of historical transactions

- **Features:** IP geolocation, device fingerprint, card velocity, billing/shipping address match, transaction amount vs historical average

- **Latency:** <100ms inference time (model runs in-memory)

- **Rules engine:** Merchants can add custom rules: "block if country != US", "review if amount > $500"


## Flow 2: Subscription / Recurring Billing


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 400" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ffaa00">
</polygon>
</marker>
</defs>


<rect x="30" y="160" width="130" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="95" y="195" text-anchor="middle" fill="white" font-size="13">
Cron Scheduler
</text>


<rect x="210" y="160" width="140" height="60" rx="10" fill="#635bff" stroke="#8a85ff" stroke-width="2">
</rect>
<text x="280" y="188" text-anchor="middle" fill="white" font-size="12">
Billing Service
</text>
<text x="280" y="205" text-anchor="middle" fill="white" font-size="11">
(Invoice Gen)
</text>


<rect x="410" y="100" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="480" y="130" text-anchor="middle" fill="white" font-size="12">
Proration Engine
</text>


<rect x="410" y="200" width="140" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="480" y="230" text-anchor="middle" fill="white" font-size="12">
Payment Service
</text>


<rect x="410" y="300" width="140" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="480" y="330" text-anchor="middle" fill="white" font-size="12">
Retry Queue
</text>


<rect x="620" y="200" width="140" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="690" y="230" text-anchor="middle" fill="white" font-size="12">
Card Network
</text>


<rect x="820" y="160" width="140" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="890" y="188" text-anchor="middle" fill="white" font-size="11">
Webhook Service
</text>


<line x1="160" y1="190" x2="208" y2="190" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="350" y1="175" x2="408" y2="130" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="350" y1="200" x2="408" y2="225" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="550" y1="225" x2="618" y2="225" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="480" y1="250" x2="480" y2="298" stroke="#ff6b6b" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow2)">
</line>


<line x1="550" y1="330" x2="620" y2="230" stroke="#ff6b6b" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow2)">
</line>


<line x1="760" y1="210" x2="818" y2="185" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<text x="500" y="30" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Stripe: Subscription Billing Flow
</text>


</svg>

</details>


### Step-by-Step


1. **Cron Scheduler** runs every minute, queries subscriptions where `current_period_end <= NOW()`

2. **Billing Service** generates an invoice: line items, proration adjustments, tax calculation

3. **Proration Engine:** If plan changed mid-cycle, calculates credit for unused time + charge for new plan

4. **Payment Service** charges the customer's saved payment method (stored token)

5. **If payment fails:** Enters smart retry queue — retry after 1 day, 3 days, 5 days (exponential backoff). Updates subscription status to "past_due"

6. **Dunning:** After all retries fail, subscription → "unpaid" → eventually "canceled". Customer emailed at each stage

7. **Webhook:** `invoice.paid`, `invoice.payment_failed`, `customer.subscription.updated`


> **
**Example:** Netflix-like SaaS charges $14.99/month. On March 1st, Cron Scheduler triggers billing for subscriptions expiring today. Billing Service creates invoice for user_123: $14.99 basic plan. Payment Service charges saved Visa ending 4242. Visa approves. Invoice marked "paid". Subscription period updated to March 1 - April 1. Webhook `invoice.paid` fires. If card declined: retry March 2, March 4, March 6. After all retries fail: subscription canceled, `customer.subscription.deleted` webhook fires.


### Deep Dive: Smart Retry Logic


- **Retry timing:** ML model predicts optimal retry time based on historical success patterns. E.g., payday (1st/15th of month) has higher success rates

- **Card updater:** Before retry, check if card network has updated card details (e.g., new expiry date). Visa Account Updater (VAU) provides automatic card updates

- **Retry schedule:** 4 attempts over 7 days. Each attempt at different time of day. Final attempt before cancellation


## Flow 3: Refund Processing


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#66ff66">
</polygon>
</marker>
</defs>


<rect x="30" y="110" width="120" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="90" y="145" text-anchor="middle" fill="white" font-size="13">
Merchant
</text>


<rect x="200" y="110" width="130" height="60" rx="10" fill="#635bff" stroke="#8a85ff" stroke-width="2">
</rect>
<text x="265" y="138" text-anchor="middle" fill="white" font-size="12">
Refund Service
</text>
<text x="265" y="155" text-anchor="middle" fill="white" font-size="11">
(Idempotent)
</text>


<rect x="390" y="110" width="130" height="60" rx="10" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="455" y="145" text-anchor="middle" fill="white" font-size="12">
Ledger Service
</text>


<rect x="580" y="110" width="140" height="60" rx="10" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="650" y="145" text-anchor="middle" fill="white" font-size="12">
Card Network
</text>


<rect x="580" y="210" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="650" y="240" text-anchor="middle" fill="white" font-size="12">
Settlement Svc
</text>


<line x1="150" y1="140" x2="198" y2="140" stroke="#66ff66" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="330" y1="140" x2="388" y2="140" stroke="#66ff66" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="520" y1="140" x2="578" y2="140" stroke="#66ff66" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="650" y1="170" x2="650" y2="208" stroke="#66ff66" stroke-width="2" marker-end="url(#arrow3)">
</line>


<text x="450" y="30" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Stripe: Refund Processing Flow
</text>


</svg>

</details>


- **POST** `/v1/refunds { charge: "ch_xxx", amount: 5000 }` — partial refund of $50.00

- Refund Service validates: refund amount ≤ charge amount - existing refunds. Creates refund record with status "pending"

- Ledger Service creates reverse double-entry: credit merchant account, debit Stripe's receivable

- Card network processes reversal → funds return to customer's card in 5-10 business days

- Settlement Service adjusts next payout to merchant (deducts refund amount from upcoming transfer)


> **
**Example:** Merchant refunds $50 of a $99.99 charge. `POST /v1/refunds { charge: "ch_abc", amount: 5000 }`. Remaining refundable: $49.99. Ledger records: DR Merchant Revenue $50, CR Customer Payable $50. Visa processes reversal. Customer sees $50 credit in 5-7 days. Merchant's next Friday payout is reduced by $50.


## Combined Overall Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 650" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#635bff">
</polygon>
</marker>
</defs>


<rect x="20" y="280" width="120" height="70" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="80" y="312" text-anchor="middle" fill="white" font-size="12">
Merchant
</text>
<text x="80" y="330" text-anchor="middle" fill="white" font-size="11">
+ Stripe.js
</text>


<rect x="190" y="280" width="130" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="255" y="310" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>


<rect x="380" y="80" width="130" height="50" rx="8" fill="#635bff" stroke="#8a85ff" stroke-width="2">
</rect>
<text x="445" y="110" text-anchor="middle" fill="white" font-size="12">
Payment Svc
</text>


<rect x="380" y="170" width="130" height="50" rx="8" fill="#635bff" stroke="#8a85ff" stroke-width="2">
</rect>
<text x="445" y="200" text-anchor="middle" fill="white" font-size="12">
Billing Svc
</text>


<rect x="380" y="260" width="130" height="50" rx="8" fill="#635bff" stroke="#8a85ff" stroke-width="2">
</rect>
<text x="445" y="290" text-anchor="middle" fill="white" font-size="12">
Refund Svc
</text>


<rect x="380" y="350" width="130" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="445" y="380" text-anchor="middle" fill="white" font-size="12">
Fraud Engine
</text>


<rect x="380" y="440" width="130" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="445" y="470" text-anchor="middle" fill="white" font-size="12">
Token Vault
</text>


<rect x="380" y="530" width="130" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="445" y="560" text-anchor="middle" fill="white" font-size="12">
Webhook Svc
</text>


<rect x="600" y="80" width="140" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="670" y="110" text-anchor="middle" fill="white" font-size="12">
Card Networks
</text>


<rect x="600" y="170" width="140" height="50" rx="8" fill="#8b4513" stroke="#cd853f" stroke-width="2">
</rect>
<text x="670" y="200" text-anchor="middle" fill="white" font-size="12">
Issuing Banks
</text>


<rect x="600" y="260" width="140" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="670" y="290" text-anchor="middle" fill="white" font-size="12">
Ledger Svc
</text>


<rect x="600" y="350" width="140" height="50" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="670" y="375" text-anchor="middle" fill="white" font-size="11">
Settlement/Payout
</text>


<rect x="600" y="440" width="140" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="670" y="465" text-anchor="middle" fill="white" font-size="11">
Kafka Event Bus
</text>


<ellipse cx="870" cy="170" rx="70" ry="30" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="870" y="175" text-anchor="middle" fill="white" font-size="12">
Payment DB
</text>


<ellipse cx="870" cy="280" rx="70" ry="30" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="870" y="285" text-anchor="middle" fill="white" font-size="12">
Ledger DB
</text>


<ellipse cx="870" cy="380" rx="70" ry="30" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="870" y="385" text-anchor="middle" fill="white" font-size="12">
Idempotency
</text>


<rect x="830" y="440" width="100" height="40" rx="8" fill="#c0392b" stroke="#e74c3c" stroke-width="2">
</rect>
<text x="880" y="465" text-anchor="middle" fill="white" font-size="11">
Redis Cache
</text>


<line x1="140" y1="305" x2="188" y2="305" stroke="#635bff" stroke-width="2" marker-end="url(#arrow4)">
</line>


<line x1="320" y1="290" x2="378" y2="105" stroke="#635bff" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="320" y1="300" x2="378" y2="195" stroke="#635bff" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="320" y1="310" x2="378" y2="285" stroke="#635bff" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="510" y1="105" x2="598" y2="105" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow4)">
</line>


<line x1="740" y1="105" x2="798" y2="170" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="740" y1="285" x2="798" y2="280" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="670" y1="310" x2="670" y2="348" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="670" y1="400" x2="670" y2="438" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="740" y1="460" x2="828" y2="460" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<text x="600" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Stripe: Combined Overall Architecture
</text>


</svg>

</details>


> **
**Example — Full lifecycle:** Merchant integrates Stripe.js → Customer enters card → tokenized to `tok_xxx` → Merchant creates PaymentIntent → API Gateway authenticates merchant API key → Payment Service checks Fraud Engine (score: 15, safe) → detokenizes card from Token Vault → sends to Visa via ISO 8583 → Chase approves → Ledger Service records double-entry (DR Stripe Receivable, CR Merchant Revenue) → Kafka publishes "payment.succeeded" event → Webhook Service delivers to merchant → Settlement Service batches and pays merchant on Friday via ACH bank transfer.


## Database Schema


### SQL — PostgreSQL (Core Financial Data — ACID Required)


| Table               | Column       | Type                                                     | Details              |
|---------------------|--------------|----------------------------------------------------------|----------------------|
| **payment_intents** | id           | VARCHAR(30)                                              | `PK` (pi_xxx format) |
| merchant_id         | VARCHAR(30)  | `FK`  `IDX`                                              |                      |
| amount              | BIGINT       | In smallest currency unit (cents)                        |                      |
| currency            | VARCHAR(3)   | ISO 4217 (usd, eur, gbp)                                 |                      |
| status              | ENUM         | requires_payment_method, processing, succeeded, canceled |                      |
| idempotency_key     | VARCHAR(255) | `IDX`                                                    |                      |
| created_at          | TIMESTAMP    | `IDX`                                                    |                      |
| **charges**         | id           | VARCHAR(30)                                              | `PK` (ch_xxx)        |
| payment_intent_id   | VARCHAR(30)  | `FK`  `IDX`                                              |                      |
| amount              | BIGINT       |                                                          |                      |
| status              | ENUM         | succeeded, pending, failed                               |                      |
| card_last4          | VARCHAR(4)   | Denormalized for display                                 |                      |
| failure_code        | VARCHAR(50)  | card_declined, insufficient_funds, etc.                  |                      |
| **subscriptions**   | id           | VARCHAR(30)                                              | `PK` (sub_xxx)       |
| customer_id         | VARCHAR(30)  | `FK`  `IDX`                                              |                      |
| plan_id             | VARCHAR(30)  | `FK`                                                     |                      |
| status              | ENUM         | active, past_due, canceled, trialing                     |                      |
| current_period_end  | TIMESTAMP    | `IDX` (for billing scheduler)                            |                      |
| **ledger_entries**  | id           | BIGSERIAL                                                | `PK`                 |
| account_id          | VARCHAR(30)  | `IDX`                                                    |                      |
| type                | ENUM         | debit, credit                                            |                      |
| amount              | BIGINT       |                                                          |                      |
| reference_id        | VARCHAR(30)  | Links to charge/refund.  `IDX`                           |                      |


Sharding Strategy


- **payment_intents, charges:** Shard by `merchant_id` — all payments for a merchant on the same shard. Merchant dashboard queries are shard-local

- **ledger_entries:** Shard by `account_id` — each merchant's ledger is co-located for balance calculations

- **subscriptions:** Shard by `customer_id` — customer's subscription management is shard-local

- **Why not shard by payment_intent_id?** Because "list all payments for merchant X" would require scatter-gather across all shards


### Idempotency Store (Redis)


| Key                               | Value                         | TTL      |
|-----------------------------------|-------------------------------|----------|
| `idempotency:{merchant_id}:{key}` | JSON: request hash + response | 24 hours |


### Denormalization


- `charges.card_last4` — denormalized from token vault to avoid cross-service lookup for display purposes

- `merchants.balance` — denormalized running balance, updated atomically with each charge/refund via database trigger. Avoids SUM() over all ledger entries


### Indexes


- `payment_intents.idempotency_key` — UNIQUE B-tree index for idempotency checks (critical: must be unique per merchant)

- `subscriptions.current_period_end` — B-tree index for billing scheduler range queries (WHERE current_period_end BETWEEN...)

- `charges.payment_intent_id` — B-tree for "list charges for a payment intent"


## Cache Deep Dive


| Cache           | Strategy                | Eviction  | TTL      | Purpose                                   |
|-----------------|-------------------------|-----------|----------|-------------------------------------------|
| Idempotency     | Write-through           | TTL-based | 24hr     | Prevent duplicate API requests            |
| Merchant Config | Read-through            | LRU       | 5min     | API keys, webhook URLs, settings          |
| Fraud Features  | Write-through           | LRU       | 1hr      | Customer risk profiles, velocity counters |
| Rate Limiting   | Sliding window counters | TTL-based | 1min/1hr | API rate limits per merchant              |


### No CDN for Payment APIs


- Payment API responses are **never cached** at CDN layer — each request must be processed fresh (financial correctness)

- CDN used only for: Stripe.js bundle (static JS), documentation site, dashboard static assets

- Stripe.js CDN TTL: 1 hour (so merchants always get latest fraud detection updates)


## Scaling Considerations


- **API Gateway:** L7 load balancer, rate limiting per merchant (100 req/sec default, configurable). TLS termination at edge

- **Payment Service:** Stateless, horizontally scaled. Each instance can process any payment. Database provides consistency

- **Database:** Primary-replica setup with synchronous replication for payment data (no data loss). Read replicas for dashboard queries

- **Kafka Event Bus:** Payment events published to Kafka for async consumers (webhooks, analytics, fraud feature updates). Partitioned by merchant_id for ordering

- **Webhook delivery:** Separate worker pool. Retries with exponential backoff (1hr, 6hr, 24hr, up to 3 days). Signed with HMAC-SHA256 for verification

- **Multi-region:** Active-passive for payment processing (financial data must be consistent). Active-active for read-only endpoints (dashboard, analytics)


## Tradeoffs & Deep Dives


> **
✅ Strong Consistency (PostgreSQL)
- ACID guarantees — no double charges, no lost payments
- Serializable isolation for concurrent refunds
- Double-entry ledger always balances


❌ Consistency Cost
- Lower throughput than eventual consistency
- Cross-shard transactions require 2PC
- Primary-replica lag means read replicas slightly behind


> **
✅ Idempotency Keys
- Safe retries — network failures don't cause double charges
- Client-generated: merchant controls dedup scope


❌ Idempotency Complexity
- 24hr TTL means very old retries might not be caught
- Same key + different params = ambiguous (409 error)
- Storage cost for cached responses


### Message Queue Deep Dive (Kafka)


- **Why Kafka:** Durable event log for financial events. Every payment event persisted. Consumers (webhook delivery, analytics, fraud) process independently at their own pace

- **Partitioning:** By `merchant_id` — ensures events for a single merchant are ordered (important: webhook delivery order matters)

- **Consumer groups:** Webhook Service, Analytics Service, Fraud Feature Service each in separate consumer groups — same event processed by all three

- **Retention:** 30 days for replay/debugging. Compliance archive to S3 for 7 years


## Alternative Approaches


- **Event Sourcing for Ledger:** Instead of updating balances, store all events (charge, refund, payout) and derive balance from event replay. Pros: complete audit trail, easy debugging. Cons: read performance (must replay events or maintain projections)

- **CQRS:** Separate write model (PostgreSQL for transactions) from read model (Elasticsearch for merchant dashboard search/analytics). Write path optimized for consistency, read path optimized for query flexibility

- **Saga pattern for multi-step payments:** Instead of distributed transactions, use compensating transactions. E.g., if card charged but ledger write fails → compensating refund issued. More resilient to partial failures

- **gRPC for inter-service communication:** Instead of REST between microservices. Pros: Protocol Buffers (smaller payloads, type-safe), HTTP/2 multiplexing. Cons: harder to debug (binary protocol), less tooling


## Additional Information


- **PCI-DSS compliance:** Token Vault runs in isolated VPC. No SSH access. All access via hardware-backed API keys. Quarterly penetration testing. Annual audit by QSA (Qualified Security Assessor)

- **3D Secure (3DS):** For high-risk transactions, redirects customer to bank's authentication page (SMS code, biometric). Shifts fraud liability from merchant to bank

- **Connect platform:** Marketplace payments — Stripe splits funds between platform and connected merchants. Uses separate "connected accounts" with their own balance and payout schedule

- **Dispute handling:** When customer files chargeback with their bank → Stripe receives dispute notification → merchant has 7 days to submit evidence → bank decides. Automated evidence collection via ML