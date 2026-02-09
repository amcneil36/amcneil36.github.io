# 💳 Credit Karma — Credit Score & Financial Recommendations


Design a platform that provides free credit scores, credit monitoring, and personalized financial product recommendations (credit cards, loans, insurance) using a marketplace model where financial partners pay for qualified leads.


## 📋 Functional Requirements


- **Credit score & report** — display VantageScore 3.0 from TransUnion/Equifax, updated weekly, with full credit report details

- **Credit monitoring** — real-time alerts for new accounts, hard inquiries, delinquencies, balance changes

- **Product recommendations** — personalized credit cards, loans, insurance based on credit profile and approval odds

- **Approval odds** — ML-predicted likelihood of approval for each financial product before user applies

- **Credit score simulator** — "what if" scenarios: what happens if I pay off $5K balance? Open a new card?

- **Credit factors** — breakdown of what's helping/hurting score (utilization, payment history, age, etc.)

- **Identity monitoring** — dark web monitoring, SSN exposure alerts, identity theft protection


## 📋 Non-Functional Requirements


- **Scale** — 130M+ members, 100M+ credit score pulls/week

- **Data security** — PII/SSN encryption (AES-256), SOC 2, FCRA compliance

- **Freshness** — credit score updated weekly; monitoring alerts within minutes of bureau updates

- **Personalization accuracy** — approval odds predictions within ±5% of actual approval rates

- **Availability** — 99.99% for core score display and monitoring

- **Revenue optimization** — maximize match rate (user gets approved for recommended product) to increase partner satisfaction and CPA revenue


## 🔄 Flow 1: Credit Score Retrieval & Display


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 300" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#00B140">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="90" text-anchor="middle" fill="white" font-size="13">
Member
</text>


<rect x="175" y="60" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="240" y="90" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>


<rect x="350" y="60" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="415" y="80" text-anchor="middle" fill="white" font-size="12">
Score Service
</text>


<rect x="530" y="30" width="140" height="50" rx="8" fill="#00695C">
</rect>
<text x="600" y="60" text-anchor="middle" fill="white" font-size="12">
Score Cache
</text>


<rect x="530" y="100" width="140" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="600" y="120" text-anchor="middle" fill="white" font-size="12">
Bureau Adapter
</text>
<text x="600" y="135" text-anchor="middle" fill="white" font-size="10">
(TransUnion/Equifax)
</text>


<rect x="720" y="100" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="785" y="120" text-anchor="middle" fill="white" font-size="12">
Credit Bureau
</text>
<text x="785" y="135" text-anchor="middle" fill="white" font-size="10">
API
</text>


<rect x="350" y="200" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="415" y="220" text-anchor="middle" fill="white" font-size="12">
Factor Analysis
</text>
<text x="415" y="235" text-anchor="middle" fill="white" font-size="10">
Engine
</text>


<line x1="130" y1="85" x2="170" y2="85" stroke="#00B140" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="305" y1="85" x2="345" y2="85" stroke="#00B140" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="480" y1="70" x2="525" y2="55" stroke="#00B140" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="480" y1="100" x2="525" y2="125" stroke="#00B140" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="670" y1="125" x2="715" y2="125" stroke="#FF9800" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="415" y1="110" x2="415" y2="195" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<text x="485" y="25" fill="#aaa" font-size="10">
cache hit (weekly refresh)
</text>


<text x="485" y="165" fill="#aaa" font-size="10">
cache miss → pull from bureau
</text>


</svg>

</details>


**Step 1:** Member opens app. Score Service checks cache for latest score (refreshed weekly on schedule)
**Step 2:** If cache hit, return cached score + credit report summary. If stale, Bureau Adapter pulls fresh data
**Step 3:** Bureau API call (TransUnion/Equifax) via HTTPS with mutual TLS. Soft pull — no impact on credit score
**Step 4:** Factor Analysis Engine computes score factors: utilization ratio, payment history %, avg account age, new accounts, mix
**Step 5:** Response assembled: score (300-850), trend chart, factor breakdown, personalized tips

Deep Dive: Credit Bureau Integration

`// Credit bureau data pull:
// Two types: Hard Pull (affects score, requires consent)
//            Soft Pull (no impact, used by Credit Karma)

// Credit Karma uses SOFT INQUIRY via:
// - TransUnion TrueVision API
// - Equifax ConsumerConnect API

// Data returned (simplified):
{
  "consumer": {
    "ssn_last4": "1234",
    "name": "John Doe"
  },
  "score": {
    "model": "VantageScore3.0",
    "value": 742,
    "factors": [
      {"code": "01", "description": "Payment history", "impact": "high_positive"},
      {"code": "14", "description": "Credit utilization", "impact": "medium_negative"},
      {"code": "07", "description": "Account age", "impact": "low_positive"}
    ]
  },
  "tradelines": [
    {
      "creditor": "Chase Bank",
      "account_type": "credit_card",
      "credit_limit": 15000,
      "current_balance": 3200,
      "payment_status": "current",
      "opened_date": "2019-03-15",
      "utilization": 0.213
    }
    // ... all accounts
  ],
  "inquiries": [ /* hard inquiries last 2 years */ ],
  "public_records": [ /* bankruptcies, liens, etc. */ ]
}

// Pull frequency: weekly per member (contractual with bureaus)
// Batch pull: overnight job processes ~18M scores/night
// Real-time pull: on-demand for new signups or force refresh`


## 🔄 Flow 2: Personalized Product Recommendations


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 300" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#0077C5">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="90" text-anchor="middle" fill="white" font-size="12">
Member Profile
</text>


<rect x="175" y="60" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="240" y="80" text-anchor="middle" fill="white" font-size="12">
Recommendation
</text>
<text x="240" y="95" text-anchor="middle" fill="white" font-size="10">
Engine
</text>


<rect x="350" y="30" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="415" y="50" text-anchor="middle" fill="white" font-size="12">
Approval Odds
</text>
<text x="415" y="65" text-anchor="middle" fill="white" font-size="10">
ML Model
</text>


<rect x="350" y="100" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="415" y="120" text-anchor="middle" fill="white" font-size="12">
Revenue
</text>
<text x="415" y="135" text-anchor="middle" fill="white" font-size="10">
Optimization
</text>


<rect x="530" y="60" width="140" height="50" rx="8" fill="#E65100">
</rect>
<text x="600" y="80" text-anchor="middle" fill="white" font-size="12">
Ranked Product
</text>
<text x="600" y="95" text-anchor="middle" fill="white" font-size="10">
Feed
</text>


<rect x="720" y="30" width="140" height="50" rx="8" fill="#4E342E">
</rect>
<text x="790" y="60" text-anchor="middle" fill="white" font-size="12">
Partner Product DB
</text>


<rect x="720" y="100" width="140" height="50" rx="8" fill="#00695C">
</rect>
<text x="790" y="120" text-anchor="middle" fill="white" font-size="12">
Click → Apply
</text>
<text x="790" y="135" text-anchor="middle" fill="white" font-size="10">
Tracking
</text>


<line x1="130" y1="85" x2="170" y2="85" stroke="#0077C5" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="305" y1="75" x2="345" y2="55" stroke="#0077C5" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="305" y1="95" x2="345" y2="125" stroke="#0077C5" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="480" y1="55" x2="525" y2="75" stroke="#0077C5" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="480" y1="125" x2="525" y2="95" stroke="#0077C5" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="670" y1="80" x2="715" y2="55" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="670" y1="90" x2="715" y2="125" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


</svg>

</details>


**Step 1:** Member's credit profile + demographics fed to Recommendation Engine
**Step 2:** Approval Odds ML model predicts P(approval) for each product in partner catalog using member's credit features
**Step 3:** Revenue Optimization balances member value (best product for user) with business value (CPA from partner)
**Step 4:** Ranked product feed displayed with approval odds badges ("Excellent", "Very Good", "Good", "Fair")
**Step 5:** Click-through tracked. When member applies and is approved, partner pays CPA ($50-$200 per credit card, $20-$100 per loan)

Deep Dive: Approval Odds ML Model

`// Approval Odds = Credit Karma's core differentiator
// Predict P(approval | user_profile, product_criteria) before user applies

// Training data:
// - Historical applications through Credit Karma (millions)
// - Outcome: approved / denied (feedback from partners)
// - Features: credit score, utilization, payment history, income, etc.

// Model architecture: Gradient Boosted Trees (XGBoost/LightGBM)
// Why not deep learning? Tabular data + need for interpretability

// Features (per user × product pair):
{
  "user_features": {
    "vantage_score": 742,
    "total_utilization": 0.28,
    "num_accounts": 8,
    "avg_account_age_months": 54,
    "num_hard_inquiries_12m": 2,
    "has_derogatory": false,
    "estimated_income": 85000
  },
  "product_features": {
    "product_type": "credit_card",
    "min_credit_score": 670,
    "requires_no_bankruptcy": true,
    "max_utilization": 0.50,
    "issuer": "Chase"
  },
  "interaction_features": {
    "score_margin": 72,  // user_score - min_score
    "utilization_margin": 0.22  // max_util - user_util
  }
}

// Output: P(approval) = 0.87 → displayed as "Excellent odds"
// Thresholds: Excellent (>80%), Very Good (60-80%), Good (40-60%), Fair (<40%)

// Calibration: Platt scaling to ensure predicted probabilities match actual rates
// Metrics: AUC-ROC > 0.85, calibration error < 3%`


Deep Dive: Revenue Model (Marketplace Economics)

`// Credit Karma's revenue: 100% from partner referrals (free for members)
// Revenue = Σ (clicks × conversion_rate × CPA_per_approval)

// CPA rates by product type:
// Credit cards: $50 - $200 per approved card
// Personal loans: $20 - $100 per funded loan
// Auto loans: $50 - $150 per funded loan
// Mortgage: $100 - $500 per funded mortgage
// Insurance: $5 - $50 per policy sold

// Ranking optimization: multi-objective
// Objective = α × P(approval) × user_value + β × CPA × P(click)
// α, β tuned to balance member experience and revenue

// Feedback loop:
// Better recommendations → higher approval rates
// Higher approval rates → partners pay more CPA
// Higher CPA → more revenue → invest in better models
// Better models → even better recommendations

// A/B testing: continuous experiments on ranking algorithms
// Guardrail metrics: member approval rate must not decrease
// Revenue is secondary metric (don't sacrifice member trust)`


## 🔄 Flow 3: Credit Monitoring & Alerts


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 280" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#00B140">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#4E342E">
</rect>
<text x="80" y="80" text-anchor="middle" fill="white" font-size="12">
Credit Bureau
</text>
<text x="80" y="95" text-anchor="middle" fill="white" font-size="10">
Change Feed
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="255" y="80" text-anchor="middle" fill="white" font-size="12">
Change Detection
</text>
<text x="255" y="95" text-anchor="middle" fill="white" font-size="10">
Pipeline
</text>


<rect x="370" y="60" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="435" y="80" text-anchor="middle" fill="white" font-size="12">
Alert Rules
</text>
<text x="435" y="95" text-anchor="middle" fill="white" font-size="10">
Engine
</text>


<rect x="550" y="30" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="615" y="60" text-anchor="middle" fill="white" font-size="12">
Push Notification
</text>


<rect x="550" y="100" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="615" y="130" text-anchor="middle" fill="white" font-size="12">
Email Service
</text>


<rect x="730" y="60" width="120" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="790" y="90" text-anchor="middle" fill="white" font-size="12">
Member
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#00B140" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="320" y1="85" x2="365" y2="85" stroke="#00B140" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="500" y1="70" x2="545" y2="55" stroke="#00B140" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="500" y1="100" x2="545" y2="125" stroke="#00B140" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="680" y1="55" x2="725" y2="75" stroke="#00B140" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="680" y1="125" x2="725" y2="95" stroke="#00B140" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


**Step 1:** Credit bureaus send change notifications via batch feeds or real-time webhook (TransUnion Alert API)
**Step 2:** Change Detection compares new credit data against previous snapshot to identify material changes
**Step 3:** Alert Rules Engine categorizes: new account, hard inquiry, balance change >$500, delinquency, score change >20pts
**Step 4:** Alerts dispatched via push notification (critical) and email (summary), respecting member preferences


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 480" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="ac" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#00B140">
</polygon>
</marker>
</defs>


<text x="550" y="25" text-anchor="middle" fill="#00B140" font-size="16" font-weight="bold">
Credit Karma Platform Architecture
</text>


<rect x="20" y="50" width="120" height="40" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="75" text-anchor="middle" fill="white" font-size="11">
Web / Mobile App
</text>


<rect x="200" y="50" width="130" height="40" rx="8" fill="#E65100">
</rect>
<text x="265" y="75" text-anchor="middle" fill="white" font-size="11">
API Gateway
</text>


<rect x="400" y="40" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="63" text-anchor="middle" fill="white" font-size="11">
Score Service
</text>


<rect x="400" y="85" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="108" text-anchor="middle" fill="white" font-size="11">
Recs Engine
</text>


<rect x="400" y="130" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="153" text-anchor="middle" fill="white" font-size="11">
Monitoring Service
</text>


<rect x="400" y="175" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="198" text-anchor="middle" fill="white" font-size="11">
Simulator Service
</text>


<rect x="400" y="220" width="120" height="35" rx="8" fill="#1565C0">
</rect>
<text x="460" y="243" text-anchor="middle" fill="white" font-size="11">
Identity Service
</text>


<rect x="590" y="50" width="140" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="660" y="75" text-anchor="middle" fill="white" font-size="11">
ML Model Serving
</text>


<rect x="590" y="110" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="660" y="135" text-anchor="middle" fill="white" font-size="11">
PostgreSQL (Encrypted)
</text>


<rect x="590" y="170" width="140" height="40" rx="8" fill="#00695C">
</rect>
<text x="660" y="195" text-anchor="middle" fill="white" font-size="11">
Redis Cache
</text>


<rect x="590" y="230" width="140" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="660" y="255" text-anchor="middle" fill="white" font-size="11">
Kafka Event Bus
</text>


<rect x="800" y="50" width="150" height="40" rx="8" fill="#4E342E">
</rect>
<text x="875" y="75" text-anchor="middle" fill="white" font-size="11">
TransUnion / Equifax
</text>


<rect x="800" y="110" width="150" height="40" rx="8" fill="#E65100">
</rect>
<text x="875" y="135" text-anchor="middle" fill="white" font-size="11">
Partner APIs (Banks)
</text>


<rect x="800" y="170" width="150" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="875" y="195" text-anchor="middle" fill="white" font-size="11">
Dark Web Monitor
</text>


<rect x="200" y="350" width="130" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="265" y="375" text-anchor="middle" fill="white" font-size="11">
Analytics Pipeline
</text>


<rect x="400" y="350" width="120" height="40" rx="8" fill="#1565C0">
</rect>
<text x="460" y="375" text-anchor="middle" fill="white" font-size="11">
Notification Svc
</text>


<line x1="140" y1="70" x2="195" y2="70" stroke="#00B140" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="330" y1="70" x2="395" y2="57" stroke="#00B140" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="57" x2="585" y2="70" stroke="#00B140" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="102" x2="585" y2="70" stroke="#00B140" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="520" y1="147" x2="585" y2="130" stroke="#0077C5" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="70" x2="795" y2="70" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="130" x2="795" y2="130" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="195" x2="795" y2="195" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


</svg>

</details>


## 💾 Database Schema


### PostgreSQL — Member & Credit Data

`CREATE TABLE members (
  id UUID PRIMARY KEY, --  `PK`
  email_encrypted BYTEA NOT NULL,
  ssn_encrypted BYTEA NOT NULL, -- AES-256-GCM
  ssn_hash VARCHAR(64) NOT NULL UNIQUE, --  `IDX` for lookup
  full_name_encrypted BYTEA NOT NULL,
  dob_encrypted BYTEA,
  estimated_income INT,
  zip_code VARCHAR(10),
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE credit_scores (
  id BIGSERIAL PRIMARY KEY,
  member_id UUID NOT NULL REFERENCES members(id), --  `FK`
  bureau VARCHAR(15) NOT NULL, -- 'transunion'|'equifax'
  score_model VARCHAR(20) NOT NULL, -- 'VantageScore3.0'
  score INT NOT NULL, -- 300-850
  factors JSONB NOT NULL, -- score factor codes
  pulled_at TIMESTAMPTZ NOT NULL, --  `IDX`
  report_data_encrypted BYTEA NOT NULL
);
--  `IDX`: (member_id, bureau, pulled_at DESC) for latest score
-- Partitioned by pulled_at (monthly) for retention management

CREATE TABLE credit_alerts (
  id BIGSERIAL PRIMARY KEY,
  member_id UUID NOT NULL, --  `FK`  `IDX`
  alert_type VARCHAR(30) NOT NULL, -- 'new_account'|'hard_inquiry'|'balance_change'
  severity VARCHAR(10) NOT NULL, -- 'high'|'medium'|'low'
  details JSONB NOT NULL,
  is_read BOOLEAN DEFAULT false,
  created_at TIMESTAMPTZ DEFAULT now()
);`


### Product Recommendations

`CREATE TABLE partner_products (
  id SERIAL PRIMARY KEY, --  `PK`
  partner_id INT NOT NULL, --  `FK`
  product_type VARCHAR(20) NOT NULL, --  `IDX`
  product_name VARCHAR(100) NOT NULL,
  min_credit_score INT,
  max_credit_score INT,
  apr_range VARCHAR(20), -- '15.99%-24.99%'
  annual_fee_cents INT,
  signup_bonus TEXT,
  cpa_cents INT NOT NULL, -- what partner pays per approval
  is_active BOOLEAN DEFAULT true, --  `IDX`
  targeting_rules JSONB, -- additional criteria
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE recommendations (
  id BIGSERIAL PRIMARY KEY,
  member_id UUID NOT NULL, --  `FK`  `SHARD`
  product_id INT NOT NULL, --  `FK`
  approval_odds DECIMAL(4,3) NOT NULL,
  rank_position INT NOT NULL,
  score_at_recommendation INT, -- member's score when recommended
  generated_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE referral_events (
  id BIGSERIAL PRIMARY KEY,
  member_id UUID NOT NULL, --  `FK`
  product_id INT NOT NULL, --  `FK`
  event_type VARCHAR(20) NOT NULL, -- 'impression'|'click'|'apply'|'approved'
  revenue_cents INT, -- populated on 'approved' event
  created_at TIMESTAMPTZ DEFAULT now()
);
--  `IDX`: (member_id, event_type, created_at) for funnel analysis`


## ⚡ Cache & CDN Deep Dive


| Layer           | What                                          | Strategy                           | TTL                      |
|-----------------|-----------------------------------------------|------------------------------------|--------------------------|
| Redis — Score   | Latest credit score per member                | Write-through on weekly pull       | 7 days (until next pull) |
| Redis — Recs    | Pre-computed product recommendations          | Batch generated nightly            | 24 hours                 |
| Redis — Session | Auth tokens, member context                   | Write-through on login             | 30 minutes               |
| CDN             | Product images, partner logos, static content | Immutable versioned URLs           | 30 days                  |
| Local Cache     | Partner product catalog                       | In-memory, refresh on change event | 5 minutes                |


`// Score caching strategy:
// Members check score frequently but it only updates weekly
// Cache key: score:{member_id}:{bureau}
// Value: { score, factors, pulled_at, next_pull_date }
// Hit rate: ~95% (most checks between weekly pulls)

// Recommendation pre-computation:
// Nightly batch job: for each member, compute top-20 products
// Store in Redis sorted set: recs:{member_id} → product_ids with scores
// On request: read pre-computed list, filter by real-time availability`


## 📈 Scaling Considerations


- **Batch credit pulls:** 130M members × weekly = ~18M pulls/night. Bureau APIs have rate limits. Stagger pulls across the week. Prioritize active members (logged in last 7 days).

- **Recommendation batch:** 130M members × 200 products × approval odds = 26B predictions/night. Use GPU inference cluster. Batch by credit score bands for efficient model serving.

- **Monitoring pipeline:** TransUnion Alert API sends ~5M change notifications/day. Kafka-based stream processing with Flink. Alert deduplication via member_id windowing.

- **Data sharding:** shard by member_id (UUID) for even distribution. All member data co-located on same shard for efficient joins.


## ⚖️ Tradeoffs


> **
Free Model (Ad-Supported)
- Massive user base (130M+)
- Network effects: more users → better ML models
- Trust: users don't feel sold to


Free Model (Ad-Supported)
- Revenue depends on partner CPA rates
- Must balance user value vs partner revenue
- Regulatory scrutiny (is recommendation or advertisement?)


Pre-Computed Recommendations
- Sub-millisecond serving from cache
- Consistent experience (no cold-start per request)
- Batch inference is GPU-efficient


Pre-Computed Recommendations
- Stale for 24h (product changes not reflected instantly)
- Massive storage for 130M × 20 recs
- Can't react to real-time context (browsing behavior)


## 🔄 Alternative Approaches


Real-Time Approval (Pre-Qualified Offers)

Instead of predicting odds, partners pre-approve members in real-time. Partner's underwriting API called with member's consent. "You're pre-approved for Chase Sapphire" — much higher conversion. Requires deeper partner integration and data sharing agreements.

Open Banking Integration

With member consent, access bank transaction data (via Plaid/MX). Richer financial profile: income verification, spending patterns, cash flow. Better approval predictions. Enables budgeting/savings recommendations beyond credit products.


## 📚 Additional Information


- **FCRA compliance:** Fair Credit Reporting Act governs how credit data can be used. Credit Karma must have "permissible purpose" for soft pulls. Cannot share raw credit data with partners (only aggregated features).

- **VantageScore vs FICO:** Credit Karma uses VantageScore 3.0 (free for soft pulls). Most lenders use FICO. Scores can differ by 20-40 points. This causes user confusion ("my Credit Karma score is 750 but lender says 710").

- **Score simulator:** uses a simplified version of the VantageScore model to predict score changes. Not exact (scoring models are proprietary), but directionally accurate. "Pay down $2K → score likely increases 15-25 points."

- **Acquired by Intuit (2020):** $7.1B acquisition. Integration with TurboTax creates financial platform: file taxes → see refund impact on credit → get recommended products.