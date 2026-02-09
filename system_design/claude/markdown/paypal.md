# 💰 System Design: PayPal (Digital Payment Platform)


## 📋 Functional Requirements


  - Users can send money to other users (P2P payments) via email/phone

  - Users can pay merchants for goods/services (checkout integration)

  - Users can link bank accounts, cards, and manage a PayPal balance

  - Support for multi-currency transactions with real-time exchange rates


## 🔒 Non-Functional Requirements


  - **Strong consistency:** Financial transactions must be ACID — no double-spending, no lost money

  - **Idempotency:** Every payment request must be safely retryable without duplicate charges

  - **PCI-DSS compliance:** Card data tokenized, encrypted at rest (AES-256), HSM for key management

  - **High availability:** 99.99% uptime — payment failures directly impact revenue

  - **Low latency:** <2s end-to-end for payment processing

  - **Fraud prevention:** Real-time ML fraud detection on every transaction

  - **Scale:** ~40M transactions/day, $1.5T annual payment volume


## 🔄 Flow 1 — P2P Payment (Send Money)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 400" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="160" width="110" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="195" text-anchor="middle" fill="white" font-size="14">
Sender App
</text>

  
<line x1="130" y1="190" x2="190" y2="190" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="200" y="160" width="130" height="60" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="265" y="188" text-anchor="middle" fill="white" font-size="13">
API Gateway
</text>

  
<text x="265" y="205" text-anchor="middle" fill="white" font-size="10">
(Auth + Idempotency)
</text>

  
<line x1="330" y1="190" x2="390" y2="190" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="400" y="160" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="470" y="185" text-anchor="middle" fill="white" font-size="13">
Payment
</text>

  
<text x="470" y="202" text-anchor="middle" fill="white" font-size="10">
Orchestrator
</text>

  
<line x1="540" y1="175" x2="610" y2="110" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="540" y1="190" x2="610" y2="190" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="540" y1="205" x2="610" y2="270" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="620" y="80" width="150" height="55" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="695" y="105" text-anchor="middle" fill="white" font-size="12">
Risk / Fraud Engine
</text>

  
<text x="695" y="122" text-anchor="middle" fill="white" font-size="10">
(ML Real-time)
</text>

  
<rect x="620" y="165" width="150" height="55" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="695" y="188" text-anchor="middle" fill="white" font-size="12">
Ledger Service
</text>

  
<text x="695" y="205" text-anchor="middle" fill="white" font-size="10">
(Double-Entry)
</text>

  
<rect x="620" y="245" width="150" height="55" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="695" y="268" text-anchor="middle" fill="white" font-size="12">
Wallet Service
</text>

  
<text x="695" y="285" text-anchor="middle" fill="white" font-size="10">
(Balance Mgmt)
</text>

  
<line x1="770" y1="192" x2="840" y2="192" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="850" y="165" width="150" height="55" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="925" y="188" text-anchor="middle" fill="white" font-size="12">
PostgreSQL
</text>

  
<text x="925" y="205" text-anchor="middle" fill="white" font-size="10">
(Ledger DB)
</text>

  
<line x1="770" y1="272" x2="840" y2="330" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="850" y="305" width="150" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="925" y="328" text-anchor="middle" fill="white" font-size="12">
Funding Source
</text>

  
<text x="925" y="345" text-anchor="middle" fill="white" font-size="10">
(Bank/Card ACH)
</text>


</svg>

</details>


### Step-by-Step


  1. **Client initiates:** `POST /api/v2/payments/send` with `{ "recipient": "friend@email.com", "amount": 50.00, "currency": "USD", "idempotency_key": "uuid-abc123", "note": "Dinner split" }`

  2. **API Gateway:** Authenticates via OAuth2 token, checks `idempotency_key` in Redis — if exists, return cached response (prevents duplicate payments on retry)

  3. **Payment Orchestrator:** Creates a `payment_intent` record with status `initiated`. Resolves recipient (email → PayPal account lookup)

  4. **Risk Engine (sync):** Real-time fraud check — evaluates sender device fingerprint, transaction velocity, amount anomaly, recipient risk profile. Returns: approve / deny / step-up-auth

  5. **Wallet Service:** Determines funding source priority: (1) PayPal balance, (2) linked bank account (ACH), (3) linked card. If balance sufficient → debit sender balance immediately

  6. **Ledger Service (atomic):** Double-entry booking: `DEBIT sender_wallet $50.00` + `CREDIT receiver_wallet $50.00` in a single DB transaction. Payment status → `completed`

  7. **Notify recipient:** Push notification + email — "You received $50.00 from John"

  8. **Idempotency key cached:** Store response in Redis with TTL 24hr keyed by idempotency_key


> **
**Example:** Alice sends $50 to Bob → Risk Engine: score 0.02 (low risk, approved) → Alice has $120 PayPal balance → Debit Alice $50, Credit Bob $50 → Ledger: two entries in same TX → Bob sees $50 in balance instantly → If Alice retries (network timeout), idempotency_key returns cached "success" — no double charge


### 🔍 Component Deep Dives — P2P Payment


Double-Entry Ledger


**Principle:** Every financial movement creates exactly two entries — a DEBIT and a CREDIT — that sum to zero. This ensures money is never created or destroyed in the system.


-- Single atomic transaction
BEGIN;
INSERT INTO ledger_entries (account_id, amount, type, payment_id, created_at)
  VALUES ('alice_wallet', -50.00, 'DEBIT', 'pay_123', NOW());
INSERT INTO ledger_entries (account_id, amount, type, payment_id, created_at)
  VALUES ('bob_wallet', 50.00, 'CREDIT', 'pay_123', NOW());
UPDATE wallets SET balance = balance - 50.00 WHERE account_id = 'alice_wallet' AND balance >= 50.00;
UPDATE wallets SET balance = balance + 50.00 WHERE account_id = 'bob_wallet';
COMMIT;


**Key:** The `WHERE balance >= 50.00` check prevents overdraft. If UPDATE affects 0 rows → insufficient funds → ROLLBACK entire TX.


Idempotency Implementation


**Protocol:** Client generates UUID v4 idempotency_key, sends in header `Idempotency-Key: uuid-abc123`


**Storage:** Redis `SET idempotency:{key} {response_json} EX 86400 NX` — NX ensures only first request proceeds


**Flow:** (1) Check Redis for key → miss → proceed with payment → store result. (2) Check Redis → hit → return cached response immediately


**Edge case:** If payment is in-flight (key exists but no response yet), return 409 Conflict telling client to retry after delay


Risk / Fraud Engine


**Protocol:** gRPC sync call from Payment Orchestrator (must complete in <100ms)


**Features:** Device fingerprint, IP geolocation, transaction velocity (count/sum in last hour), recipient risk score, historical pattern, time-of-day anomaly


**Decisions:** APPROVE (score < 0.3), STEP_UP_AUTH (0.3-0.7 → require 2FA/SMS), DENY (score > 0.7)


**Model:** Gradient Boosted Trees (XGBoost) with ~500 features, retrained daily on labeled fraud data


## 🔄 Flow 2 — Merchant Checkout Payment


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 420" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="160" width="110" height="60" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="195" text-anchor="middle" fill="white" font-size="14">
Buyer
</text>

  
<line x1="130" y1="190" x2="190" y2="190" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="200" y="160" width="130" height="60" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="265" y="188" text-anchor="middle" fill="white" font-size="13">
Merchant Site
</text>

  
<text x="265" y="205" text-anchor="middle" fill="white" font-size="10">
(PayPal JS SDK)
</text>

  
<line x1="330" y1="190" x2="390" y2="190" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="400" y="160" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="470" y="185" text-anchor="middle" fill="white" font-size="12">
Checkout Service
</text>

  
<text x="470" y="202" text-anchor="middle" fill="white" font-size="10">
(Order Creation)
</text>

  
<line x1="540" y1="175" x2="610" y2="100" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="540" y1="190" x2="610" y2="190" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="540" y1="210" x2="610" y2="290" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="620" y="70" width="150" height="55" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="695" y="93" text-anchor="middle" fill="white" font-size="12">
Token Vault
</text>

  
<text x="695" y="110" text-anchor="middle" fill="white" font-size="10">
(PCI — Card Data)
</text>

  
<rect x="620" y="165" width="150" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="695" y="188" text-anchor="middle" fill="white" font-size="12">
Payment Processor
</text>

  
<text x="695" y="205" text-anchor="middle" fill="white" font-size="10">
(Card Networks)
</text>

  
<rect x="620" y="265" width="150" height="55" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="695" y="288" text-anchor="middle" fill="white" font-size="12">
Ledger + Escrow
</text>

  
<text x="695" y="305" text-anchor="middle" fill="white" font-size="10">
(Hold → Capture)
</text>

  
<line x1="770" y1="192" x2="850" y2="130" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="770" y1="192" x2="850" y2="250" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="860" y="100" width="150" height="55" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="935" y="123" text-anchor="middle" fill="white" font-size="12">
Visa / Mastercard
</text>

  
<text x="935" y="140" text-anchor="middle" fill="white" font-size="10">
(ISO 8583)
</text>

  
<rect x="860" y="225" width="150" height="55" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="935" y="248" text-anchor="middle" fill="white" font-size="12">
ACH / Bank
</text>

  
<text x="935" y="265" text-anchor="middle" fill="white" font-size="10">
(Wire Network)
</text>

  
<line x1="1010" y1="128" x2="1080" y2="180" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="1010" y1="252" x2="1080" y2="195" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="1080" y="165" width="100" height="50" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="1130" y="195" text-anchor="middle" fill="white" font-size="12">
Merchant
</text>


</svg>

</details>


### Step-by-Step


  1. **Merchant creates order:** `POST /v2/checkout/orders` with `{ "intent": "CAPTURE", "purchase_units": [{ "amount": { "currency_code": "USD", "value": "99.99" } }] }`

  2. **Buyer approves:** PayPal modal/redirect → buyer logs in, selects funding source (balance, card, bank), confirms

  3. **Authorization:** Payment Processor performs authorization — for cards: sends ISO 8583 authorization request to card network → issuing bank approves → auth_code returned. Funds are held (not captured yet)

  4. **Merchant captures:** `POST /v2/checkout/orders/{id}/capture` — triggers actual fund movement

  5. **Capture execution:** Card network capture message sent, or ACH debit initiated. Ledger records double-entry: DEBIT buyer → CREDIT PayPal escrow → CREDIT merchant (minus fees)

  6. **Settlement:** Funds available in merchant's PayPal account. Merchant can withdraw to bank (1-3 business days for ACH)


> **
**Example:** Buyer on e-commerce site clicks "Pay with PayPal" for $99.99 order → PayPal modal opens → Buyer selects Visa card → Authorization: Visa approves hold of $99.99 → Merchant ships item → Merchant calls Capture API → PayPal captures $99.99 from Visa → Ledger: buyer -$99.99, merchant +$97.10 (after 2.9% + $0.30 fee), PayPal revenue +$2.89


### 🔍 Component Deep Dives — Checkout Payment


Two-Phase Payment (Auth + Capture)


**Why separate?** Merchant may not want to charge until shipment. Authorization holds funds for 3-29 days (varies by card network). If merchant doesn't capture, auth expires and hold is released.


**Void:** If order is cancelled before capture, send void request to release the hold immediately


**Partial Capture:** Merchant can capture less than authorized amount (e.g., item out of stock → partial shipment)


PCI-DSS Token Vault


**Card data never touches PayPal application servers.** Flow: buyer enters card in secure iframe (hosted field) → card data sent directly to Token Vault (isolated PCI-certified environment) → returns opaque token `tok_abc123` → all subsequent API calls use token, not card numbers


**Encryption:** AES-256-GCM at rest, HSM (Hardware Security Module) for key management. Card numbers stored encrypted with per-card unique DEK (Data Encryption Key), DEK encrypted with KEK (Key Encryption Key) in HSM


ISO 8583 — Card Network Protocol


**Protocol:** Binary message format used by Visa/Mastercard networks over TCP/IP


**Message types:** 0100 (Authorization Request), 0110 (Authorization Response), 0200 (Financial Request/Capture), 0400 (Reversal)


**Fields:** PAN (Primary Account Number), expiry date, amount, currency code, merchant category code (MCC), terminal ID


**Response codes:** 00 (Approved), 05 (Do Not Honor), 51 (Insufficient Funds), 54 (Expired Card)


## 🔄 Flow 3 — Refund Processing


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 320" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="130" width="120" height="60" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="80" y="165" text-anchor="middle" fill="white" font-size="13">
Merchant /
</text>

  
<text x="80" y="180" text-anchor="middle" fill="white" font-size="13">
Buyer Dispute
</text>

  
<line x1="140" y1="160" x2="210" y2="160" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="220" y="130" width="140" height="60" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="290" y="158" text-anchor="middle" fill="white" font-size="13">
Refund Service
</text>

  
<text x="290" y="175" text-anchor="middle" fill="white" font-size="10">
(Validation)
</text>

  
<line x1="360" y1="160" x2="430" y2="110" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<line x1="360" y1="160" x2="430" y2="210" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="440" y="85" width="150" height="55" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="515" y="108" text-anchor="middle" fill="white" font-size="12">
Ledger (Reverse)
</text>

  
<text x="515" y="125" text-anchor="middle" fill="white" font-size="10">
(Compensating TX)
</text>

  
<rect x="440" y="185" width="150" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="515" y="208" text-anchor="middle" fill="white" font-size="12">
Payment Processor
</text>

  
<text x="515" y="225" text-anchor="middle" fill="white" font-size="10">
(Reverse Charge)
</text>

  
<line x1="590" y1="112" x2="660" y2="160" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<line x1="590" y1="212" x2="660" y2="165" stroke="#aaa" marker-end="url(#ah3)">
</line>

  
<rect x="670" y="135" width="140" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="740" y="158" text-anchor="middle" fill="white" font-size="12">
Notification
</text>

  
<text x="740" y="175" text-anchor="middle" fill="white" font-size="10">
(Buyer + Merchant)
</text>


</svg>

</details>


### Step-by-Step


  1. **Refund initiated:** Merchant calls `POST /v2/payments/captures/{capture_id}/refund` with `{ "amount": { "value": "99.99", "currency_code": "USD" } }`

  2. **Refund Service validates:** Original payment exists, is captured, refund amount ≤ remaining capturable amount, refund window not expired (180 days)

  3. **Ledger reverse entries:** Compensating transaction — DEBIT merchant account, CREDIT buyer account. This doesn't delete original entries — it creates new reversal entries for audit trail

  4. **Payment Processor:** If funded by card → send ISO 8583 reversal (0400) to card network → issuing bank credits buyer's card (3-10 business days). If funded by balance → instant credit to buyer PayPal balance

  5. **Notifications:** Buyer gets email "Refund of $99.99 processed" + merchant gets confirmation


> **
**Example:** Buyer received wrong item → Merchant issues full refund of $99.99 → Ledger creates reverse entries: merchant -$99.99, buyer +$99.99. PayPal's fee ($2.89) is NOT refunded to merchant (PayPal keeps processing fee). Card network processes reversal → buyer sees credit on Visa statement in 5-7 days.


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 700" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ahc" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="80" width="130" height="50" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="85" y="110" text-anchor="middle" fill="white" font-size="13">
Buyer App
</text>

  
<rect x="20" y="150" width="130" height="50" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="85" y="180" text-anchor="middle" fill="white" font-size="13">
Merchant SDK
</text>

  
<line x1="150" y1="105" x2="230" y2="155" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="150" y1="175" x2="230" y2="160" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="240" y="130" width="140" height="60" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="310" y="158" text-anchor="middle" fill="white" font-size="13">
API Gateway
</text>

  
<text x="310" y="175" text-anchor="middle" fill="white" font-size="10">
(L7 LB + Auth)
</text>

  

  
<line x1="380" y1="145" x2="460" y2="80" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="380" y1="155" x2="460" y2="155" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="380" y1="165" x2="460" y2="230" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="380" y1="175" x2="460" y2="310" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="470" y="55" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="540" y="85" text-anchor="middle" fill="white" font-size="12">
Payment Orchestrator
</text>

  
<rect x="470" y="130" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="540" y="160" text-anchor="middle" fill="white" font-size="12">
Checkout Service
</text>

  
<rect x="470" y="210" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="540" y="240" text-anchor="middle" fill="white" font-size="12">
Wallet Service
</text>

  
<rect x="470" y="290" width="140" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="540" y="320" text-anchor="middle" fill="white" font-size="12">
Refund Service
</text>

  

  
<line x1="610" y1="80" x2="700" y2="80" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="610" y1="155" x2="700" y2="155" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="610" y1="235" x2="700" y2="235" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="710" y="55" width="150" height="50" rx="10" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="785" y="85" text-anchor="middle" fill="white" font-size="12">
Risk Engine (ML)
</text>

  
<rect x="710" y="130" width="150" height="50" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="785" y="160" text-anchor="middle" fill="white" font-size="12">
Ledger Service
</text>

  
<rect x="710" y="210" width="150" height="50" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="785" y="240" text-anchor="middle" fill="white" font-size="12">
Token Vault (PCI)
</text>

  

  
<line x1="860" y1="155" x2="940" y2="155" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="950" y="55" width="160" height="50" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="1030" y="85" text-anchor="middle" fill="white" font-size="11">
PostgreSQL (Ledger)
</text>

  
<rect x="950" y="130" width="160" height="50" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="1030" y="160" text-anchor="middle" fill="white" font-size="11">
PostgreSQL (Accounts)
</text>

  
<rect x="950" y="210" width="160" height="50" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="1030" y="240" text-anchor="middle" fill="white" font-size="11">
Redis (Idempotency)
</text>

  
<line x1="860" y1="80" x2="940" y2="80" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="860" y1="235" x2="940" y2="235" stroke="#aaa" marker-end="url(#ahc)">
</line>

  

  
<rect x="470" y="400" width="140" height="50" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="540" y="430" text-anchor="middle" fill="white" font-size="12">
Apache Kafka
</text>

  
<line x1="540" y1="340" x2="540" y2="395" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="610" y1="425" x2="700" y2="400" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="610" y1="425" x2="700" y2="455" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="710" y="375" width="150" height="45" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="785" y="402" text-anchor="middle" fill="white" font-size="11">
Notification Service
</text>

  
<rect x="710" y="435" width="150" height="45" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="785" y="462" text-anchor="middle" fill="white" font-size="11">
Analytics / Reporting
</text>

  

  
<rect x="950" y="375" width="160" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="1030" y="405" text-anchor="middle" fill="white" font-size="11">
Card Networks (Visa/MC)
</text>

  
<rect x="950" y="440" width="160" height="50" rx="10" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="1030" y="470" text-anchor="middle" fill="white" font-size="11">
ACH / Bank Network
</text>

  
<line x1="860" y1="235" x2="940" y2="400" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="860" y1="235" x2="940" y2="465" stroke="#aaa" marker-end="url(#ahc)">
</line>


</svg>

</details>


> **
**End-to-End Example:** Buyer on merchant site clicks "Pay with PayPal" → PayPal JS SDK opens checkout modal → Buyer selects saved Visa → Token Vault retrieves encrypted card → Risk Engine approves (score 0.05) → Auth request to Visa via ISO 8583 → Approved → Merchant captures payment → Ledger: buyer debited, PayPal escrow, merchant credited (minus 2.9% + $0.30 fee) → Kafka event → Notification Service emails buyer receipt → Analytics records transaction metrics


## 🗄️ Database Schema


### PostgreSQL — Ledger Database (ACID Critical)


accounts


| Column            | Type          | Notes                                          |
|-------------------|---------------|------------------------------------------------|
| id                | UUID          | `PK`                                           |
| user_id           | UUID          | `FK` `IDX`                                     |
| account_type      | VARCHAR(20)   | wallet / merchant / escrow / fee_revenue       |
| currency          | VARCHAR(3)    | USD, EUR, GBP, etc.                            |
| balance           | DECIMAL(19,4) | Current balance (denormalized from ledger sum) |
| available_balance | DECIMAL(19,4) | Balance minus holds/pending                    |
| status            | VARCHAR(20)   | active / frozen / closed                       |
| created_at        | TIMESTAMPTZ   |                                                |


ledger_entries


| Column      | Type          | Notes                                     |
|-------------|---------------|-------------------------------------------|
| id          | BIGINT        | `PK` (Snowflake ID — time-ordered)        |
| account_id  | UUID          | `FK` `IDX`                                |
| payment_id  | UUID          | `FK` `IDX`                                |
| type        | VARCHAR(10)   | DEBIT / CREDIT                            |
| amount      | DECIMAL(19,4) | Always positive; type indicates direction |
| currency    | VARCHAR(3)    |                                           |
| description | TEXT          |                                           |
| created_at  | TIMESTAMPTZ   | `IDX`                                     |


**Constraint:** For any payment_id, SUM(CREDIT amounts) = SUM(DEBIT amounts). Enforced by application-level invariant + periodic reconciliation job.


payments


| Column              | Type          | Notes                                                             |
|---------------------|---------------|-------------------------------------------------------------------|
| id                  | UUID          | `PK`                                                              |
| idempotency_key     | VARCHAR(64)   | `IDX`                                                             |
| sender_account_id   | UUID          | `FK`                                                              |
| receiver_account_id | UUID          | `FK`                                                              |
| amount              | DECIMAL(19,4) |                                                                   |
| currency            | VARCHAR(3)    |                                                                   |
| fee_amount          | DECIMAL(19,4) |                                                                   |
| payment_type        | VARCHAR(20)   | p2p / checkout / refund / withdrawal                              |
| status              | VARCHAR(20)   | initiated / authorized / captured / completed / failed / refunded |
| funding_source      | VARCHAR(20)   | balance / card / bank                                             |
| risk_score          | DECIMAL(5,4)  |                                                                   |
| auth_code           | VARCHAR(20)   | From card network (if card funded)                                |
| created_at          | TIMESTAMPTZ   | `IDX`                                                             |
| updated_at          | TIMESTAMPTZ   |                                                                   |


funding_sources


| Column       | Type        | Notes                                            |
|--------------|-------------|--------------------------------------------------|
| id           | UUID        | `PK`                                             |
| user_id      | UUID        | `FK` `IDX`                                       |
| type         | VARCHAR(20) | card / bank_account                              |
| token        | VARCHAR(64) | Reference to Token Vault (never raw card number) |
| last_four    | VARCHAR(4)  | Display purposes                                 |
| brand        | VARCHAR(20) | visa / mastercard / amex                         |
| expiry_month | SMALLINT    |                                                  |
| expiry_year  | SMALLINT    |                                                  |
| is_default   | BOOLEAN     |                                                  |
| status       | VARCHAR(20) | active / expired / removed                       |
| created_at   | TIMESTAMPTZ |                                                  |


### Sharding Strategy


**accounts + ledger_entries:** Shard by `account_id` — keeps all entries for an account on same shard. Cross-shard transactions (sender and receiver on different shards) handled by 2-Phase Commit or Saga pattern  `SHARD`


**payments:** Shard by `payment_id` hash — uniform distribution. Secondary index on sender/receiver for lookup  `SHARD`


### DECIMAL(19,4) for Money


**NEVER use FLOAT for money.** IEEE 754 floating point cannot represent 0.10 exactly (0.1000000000000000055511151231257827021181583404541015625). Use DECIMAL/NUMERIC with fixed precision. 19 digits allows values up to 999,999,999,999,999.9999 — sufficient for any single transaction.


### Reconciliation


Daily batch job sums all ledger_entries per account and compares to `accounts.balance`. Any discrepancy triggers an alert and investigation. This catches bugs in the double-entry system. Also reconciles PayPal's internal ledger against card network settlement files and bank statements.


## 💾 Cache & CDN Deep Dive


### Redis Caching Strategy


| Cache           | Key Pattern                 | TTL                        | Strategy                       |
|-----------------|-----------------------------|----------------------------|--------------------------------|
| Idempotency     | `idemp:{key}`               | 24 hr                      | Write-through (SET NX)         |
| Rate Limits     | `rate:{user_id}:{endpoint}` | 1 min window               | Sliding window counter         |
| Session / Auth  | `session:{token}`           | 30 min                     | Write-through                  |
| Exchange Rates  | `fx:{from}:{to}`            | 5 min                      | Pull from rate provider        |
| Account Balance | `balance:{account_id}`      | None (invalidate on write) | Read-through, write-invalidate |


**⚠️ No caching of financial data for reads that affect decisions.** Balance checks for payment processing ALWAYS read from PostgreSQL primary (not replica, not cache) to prevent stale-balance attacks. Cache is only for display purposes (e.g., showing balance on home screen).


### CDN Strategy


**Static only:** JS SDK, logos, marketing pages. API responses are NEVER cached on CDN — all payment APIs require real-time auth and are user-specific.


**SDK caching:** PayPal JS SDK (`paypal-js`) cached on CDN with versioned URLs. Merchants include `<script src="https://www.paypal.com/sdk/js?client-id=...">`


## ⚖️ Scaling Considerations


  - **Ledger DB scaling:** Shard by account_id. Cross-shard P2P payments use Saga pattern with compensating transactions. Ledger is append-only (immutable entries), enabling high write throughput

  - **Connection pooling:** PgBouncer with transaction-mode pooling — critical for high-concurrency payment processing

  - **Geo-distributed:** Multi-region deployment (US, EU, APAC) with regional PostgreSQL primaries. Cross-region payments routed to sender's region's primary

  - **Rate limiting:** Token bucket per user per endpoint. Strict limits on payment initiation (100/hour), lenient on balance checks (1000/hour)

  - **Circuit breaker:** Card network connections have circuit breakers — if Visa is down, route to backup processor or fail gracefully with retry logic

  - **Hot account problem:** Popular merchants (e.g., Amazon's PayPal account) receive millions of credits/day → row-level lock contention on balance UPDATE. Solution: sub-accounts — split merchant balance into 100 sub-accounts, round-robin credits, aggregate balance for display


## ⚖️ Tradeoffs


> **
  
> **
    ✅ Double-Entry Ledger
    

Complete audit trail, mathematical invariant (debits = credits), regulatory compliance, easy reconciliation

  
  
> **
    ❌ Double-Entry Ledger
    

2x write amplification (every transaction = 2+ rows), cross-shard transactions complex, harder to scale than event-sourcing

  
  
> **
    ✅ Idempotency Keys
    

Safe retries — eliminates duplicate payment problem entirely, essential for unreliable networks

  
  
> **
    ❌ Idempotency Keys
    

Extra storage (Redis), client must generate unique keys, 24hr TTL means very late retries may create duplicates

  
  
> **
    ✅ Sync Risk Check
    

Blocks fraud before money moves — prevents financial loss in real-time

  
  
> **
    ❌ Sync Risk Check
    

Adds latency to every payment (~50-100ms), single point of failure if risk engine is down (must have fallback policy)

  
  
> **
    ✅ Auth + Capture (Two-Phase)
    

Merchants only charge when they ship, reduces chargebacks, supports partial captures

  
  
> **
    ❌ Auth + Capture (Two-Phase)
    

Complexity in tracking auth expiry (29 days for Visa, 7 for MC), held funds unavailable to buyer, failed captures after auth expiry

  


## 🔄 Alternative Approaches


Event Sourcing Instead of Double-Entry Ledger


Store all financial events as immutable event stream (Kafka). Current balance derived by replaying events. Advantages: natural audit trail, easy to replay/debug, scales better horizontally. Disadvantage: calculating current balance requires reading full event history (mitigated by snapshots). Used by some modern fintech (e.g., Nubank).


CQRS for Transaction History


Separate write model (ledger DB — optimized for ACID writes) from read model (Elasticsearch or materialized views — optimized for transaction history queries, search, filtering). Write path: PostgreSQL. Read path: CDC → Elasticsearch with rich search capabilities (date range, amount range, merchant name search).


Blockchain / DLT for Settlement


Use distributed ledger for cross-border settlements (like Ripple/XRP). Eliminates intermediary banks for international transfers, reduces settlement from days to seconds. Drawback: regulatory uncertainty, throughput limitations, energy cost for proof-of-work chains.


## 📚 Additional Information


### Multi-Currency & FX


PayPal supports 25+ currencies. When sender's currency differs from receiver's, FX conversion happens at PayPal's rate (which includes a ~3-4% markup over mid-market rate). Exchange rates cached in Redis (5-min TTL) from upstream FX providers. Rate locked at time of payment initiation for 10 minutes.


### Dispute Resolution (Chargebacks)


Buyer Protection: buyers can open disputes within 180 days. Process: (1) Buyer opens case, (2) Merchant has 10 days to respond with evidence, (3) PayPal arbitrates. If merchant loses → forced refund (chargeback). Merchant accounts with high chargeback rates get account restrictions or termination.


### ACH Processing


Bank transfers via ACH (Automated Clearing House) are batch-processed. PayPal submits ACH files to the Federal Reserve in batches (multiple times/day). Settlement takes 1-3 business days. Provisional credit given to recipient immediately (PayPal takes the risk). ACH returns (insufficient funds) handled as negative balance on sender's account.


### Compliance & KYC


Know Your Customer (KYC): users must verify identity for higher limits. Progressive KYC: basic account ($500/month limit) → verified (SSN/ID check → unlimited). Anti-Money Laundering (AML): suspicious transaction reporting to FinCEN. OFAC screening: every payment checked against sanctions list in real-time.