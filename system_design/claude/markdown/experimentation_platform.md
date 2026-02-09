# 🧪 Experimentation Platform (A/B Testing)


Design a system like Optimizely, LaunchDarkly, or Meta's PlanOut that enables product teams to run A/B tests and feature experiments at scale with statistical rigor, targeting flexibility, and real-time metric analysis.


## 📋 Functional Requirements


- **Experiment creation** — create experiments with variants, traffic allocation, targeting rules, and guardrail metrics

- **User assignment** — deterministically assign users to experiment variants with consistent hashing

- **Feature flags** — toggle features on/off with gradual rollouts and targeting rules

- **Metric collection** — track experiment metrics (conversion, engagement, revenue) in real-time and batch

- **Statistical analysis** — compute p-values, confidence intervals, and sequential testing with false-discovery-rate controls

- **Mutual exclusion** — prevent conflicting experiments from running on same users

- **Experiment lifecycle** — draft → running → paused → completed → archived with rollback


## 📋 Non-Functional Requirements


- **Low latency assignment** — variant lookup must be < 5ms at p99 (on critical path)

- **Consistency** — same user always gets same variant (deterministic hashing)

- **Scale** — support 10K+ concurrent experiments, billions of assignments/day

- **Statistical rigor** — control false positive rate at 5%, manage multiple comparisons

- **Availability** — 99.99% for assignment service (fail-open to control group)

- **Isolation** — experiments must not interfere with each other (interaction effects)


## 🔄 Flow 1: Experiment Creation & Configuration


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 320" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#4CAF50">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="130" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="85" y="80" text-anchor="middle" fill="white" font-size="13">
Product Team
</text>


<rect x="200" y="50" width="140" height="50" rx="8" fill="#E65100">
</rect>
<text x="270" y="80" text-anchor="middle" fill="white" font-size="13">
Experiment UI
</text>


<rect x="400" y="50" width="150" height="50" rx="8" fill="#1565C0">
</rect>
<text x="475" y="80" text-anchor="middle" fill="white" font-size="13">
Config Service
</text>


<rect x="620" y="50" width="140" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="690" y="80" text-anchor="middle" fill="white" font-size="13">
Validation Engine
</text>


<rect x="400" y="160" width="150" height="50" rx="8" fill="#4E342E">
</rect>
<text x="475" y="190" text-anchor="middle" fill="white" font-size="13">
Experiment DB
</text>


<rect x="620" y="160" width="140" height="50" rx="8" fill="#00695C">
</rect>
<text x="690" y="190" text-anchor="middle" fill="white" font-size="13">
Config CDN Cache
</text>


<rect x="200" y="250" width="140" height="50" rx="8" fill="#1565C0">
</rect>
<text x="270" y="280" text-anchor="middle" fill="white" font-size="13">
Assignment Service
</text>


<line x1="150" y1="75" x2="195" y2="75" stroke="#4CAF50" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="340" y1="75" x2="395" y2="75" stroke="#4CAF50" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="550" y1="75" x2="615" y2="75" stroke="#4CAF50" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="475" y1="100" x2="475" y2="155" stroke="#FF9800" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="550" y1="185" x2="615" y2="185" stroke="#FF9800" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="475" y1="210" x2="270" y2="245" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<text x="490" y="140" fill="#aaa" font-size="11">
persist config
</text>


<text x="560" y="175" fill="#aaa" font-size="11">
push to CDN
</text>


<text x="310" y="230" fill="#aaa" font-size="11">
propagate to SDK
</text>


</svg>

</details>


**Step 1:** Product team defines experiment: name, hypothesis, variants (control + treatments), traffic %, targeting rules (country, platform, user segment), guardrail metrics
**Step 2:** Validation Engine checks: no conflicting experiments on same layer, sample size sufficient for MDE (minimum detectable effect), metrics exist in catalog
**Step 3:** Config Service persists experiment definition to Experiment DB and pushes serialized config to CDN/edge caches
**Step 4:** Assignment Service receives updated config for local evaluation (SDK model) or serves API lookups

Example: Creating a checkout flow A/B test
`POST /api/v1/experiments
{
  "name": "checkout_redesign_q1_2025",
  "hypothesis": "New checkout reduces cart abandonment by 5%",
  "layer": "checkout_layer",
  "variants": [
    {"name": "control", "weight": 50, "config": {"checkout_version": "v1"}},
    {"name": "treatment_a", "weight": 25, "config": {"checkout_version": "v2_single_page"}},
    {"name": "treatment_b", "weight": 25, "config": {"checkout_version": "v2_accordion"}}
  ],
  "targeting": {
    "countries": ["US", "CA"],
    "platforms": ["web", "ios"],
    "user_segment": "returning_users"
  },
  "primary_metric": "checkout_conversion_rate",
  "guardrail_metrics": ["p99_page_load", "revenue_per_user"],
  "min_detectable_effect": 0.02,
  "significance_level": 0.05,
  "power": 0.8,
  "estimated_duration_days": 14
}`


Deep Dive: Experiment Layers & Mutual Exclusion

Layer-Based Traffic Isolation (Google's Overlapping Experiments)


Traffic is divided into **layers** (orthogonal dimensions) and **domains** (traffic slices):

`// Layer architecture (inspired by Google's paper)
// Each layer is an independent randomization unit
Layer: "UI" → experiments affecting UI rendering
Layer: "Backend" → experiments affecting backend logic
Layer: "ML Ranking" → experiments affecting ML models

// Within a layer, experiments are mutually exclusive
// Across layers, experiments are orthogonal (can overlap)

// Mutual exclusion within a layer:
hash(user_id + layer_id) % 10000 → traffic_bucket
experiment_A: buckets 0-4999 (50%)
experiment_B: buckets 5000-7499 (25%)
experiment_C: buckets 7500-9999 (25%)

// Cross-layer independence:
// User in experiment_A (UI layer) can also be in experiment_X (ML layer)
// because different layer_id produces different hash → independent assignment`


**Why layers?** Without layers, you'd need N! combinations for N experiments. Layers give O(N) complexity by ensuring independence. Google runs 10K+ concurrent experiments using this model.


## 🔄 Flow 2: User Assignment (Variant Lookup)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 300" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FF9800">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="90" text-anchor="middle" fill="white" font-size="13">
Client App
</text>


<rect x="190" y="60" width="140" height="50" rx="8" fill="#00695C">
</rect>
<text x="260" y="80" text-anchor="middle" fill="white" font-size="12">
Local SDK Cache
</text>
<text x="260" y="95" text-anchor="middle" fill="white" font-size="10">
(config snapshot)
</text>


<rect x="380" y="30" width="150" height="50" rx="8" fill="#1565C0">
</rect>
<text x="455" y="60" text-anchor="middle" fill="white" font-size="13">
Hashing Function
</text>


<rect x="380" y="100" width="150" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="455" y="130" text-anchor="middle" fill="white" font-size="13">
Targeting Engine
</text>


<rect x="600" y="60" width="140" height="50" rx="8" fill="#E65100">
</rect>
<text x="670" y="90" text-anchor="middle" fill="white" font-size="13">
Variant Config
</text>


<rect x="800" y="60" width="130" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="865" y="90" text-anchor="middle" fill="white" font-size="13">
Feature Code
</text>


<rect x="380" y="200" width="150" height="50" rx="8" fill="#4E342E">
</rect>
<text x="455" y="230" text-anchor="middle" fill="white" font-size="13">
Exposure Log
</text>


<rect x="600" y="200" width="140" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="670" y="230" text-anchor="middle" fill="white" font-size="13">
Event Pipeline
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#FF9800" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="330" y1="75" x2="375" y2="55" stroke="#FF9800" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="330" y1="95" x2="375" y2="125" stroke="#FF9800" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="530" y1="55" x2="595" y2="75" stroke="#FF9800" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="530" y1="125" x2="595" y2="95" stroke="#FF9800" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="740" y1="85" x2="795" y2="85" stroke="#FF9800" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="455" y1="150" x2="455" y2="195" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="530" y1="225" x2="595" y2="225" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<text x="455" y="180" fill="#aaa" font-size="11" text-anchor="middle">
log exposure
</text>


</svg>

</details>


**Step 1:** Client SDK loads experiment config snapshot (pushed via CDN or streamed via SSE)
**Step 2:** For each experiment, SDK evaluates targeting rules (country, platform, segment)
**Step 3:** Deterministic hashing: variant = hash(user_id + experiment_salt) % 10000; map bucket to variant
**Step 4:** SDK returns variant config to feature code and logs exposure event asynchronously

Deep Dive: Deterministic Hashing for Consistent Assignment

Why Deterministic Hashing?


Users must always see the same variant. Storing assignments in a DB would add latency and a single point of failure. Instead, we use deterministic hashing:

`// Assignment algorithm (no DB needed!)
function assignVariant(userId, experiment) {
  // Salt ensures different experiments get independent assignments
  const hash = murmur3_32(userId + experiment.salt);
  const bucket = hash % 10000; // 0.01% granularity

  // Check targeting rules first
  if (!matchesTargeting(userId, experiment.targeting)) {
    return null; // not in experiment
  }

  // Map bucket to variant based on traffic allocation
  let cumulative = 0;
  for (const variant of experiment.variants) {
    cumulative += variant.weight * 100; // weight is percentage
    if (bucket < cumulative) {
      return variant;
    }
  }
  return experiment.variants[0]; // fallback to control
}

// Properties of murmur3:
// - Deterministic: same input → same output (consistent assignment)
// - Uniform: buckets are evenly distributed (balanced groups)
// - Fast: ~3ns per hash (negligible latency)
// - Avalanche: small input change → completely different output`


**Salt per experiment** ensures that being in variant A of experiment 1 doesn't correlate with variant assignment in experiment 2 (independence).


Deep Dive: Client SDK Architecture

Two Models: Server-Side vs Client-Side Evaluation


| Aspect           | Server-Side (API)      | Client-Side (SDK)          |
|------------------|------------------------|----------------------------|
| Latency          | Network RTT (~20-50ms) | Local (~0.1ms)             |
| Config freshness | Always latest          | Eventual (polling/SSE)     |
| Targeting data   | Rich server context    | Limited to client context  |
| Offline support  | No                     | Yes (cached config)        |
| SDK complexity   | Thin client            | Thick client (eval engine) |


**Hybrid approach (LaunchDarkly model):** SDK connects via streaming (SSE/WebSocket) for real-time config updates, evaluates locally for speed, falls back to cached config if connection lost.

`// SDK initialization flow
const client = ExperimentSDK.init({
  sdkKey: "sdk-xxx",
  user: { key: "user_123", country: "US", plan: "premium" },
  bootstrap: localStorageCache, // instant first render
  streaming: true, // SSE for real-time updates
  flushInterval: 30000, // batch exposure events every 30s
});`


## 🔄 Flow 3: Metric Analysis & Statistical Testing


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#81D4FA">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="85" y="80" text-anchor="middle" fill="white" font-size="12">
Exposure Events
</text>


<rect x="20" y="130" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="85" y="160" text-anchor="middle" fill="white" font-size="12">
Metric Events
</text>


<rect x="200" y="90" width="140" height="50" rx="8" fill="#4E342E">
</rect>
<text x="270" y="120" text-anchor="middle" fill="white" font-size="13">
Event Pipeline
</text>


<rect x="400" y="50" width="150" height="50" rx="8" fill="#1565C0">
</rect>
<text x="475" y="75" text-anchor="middle" fill="white" font-size="12">
Exposure-Metric
</text>
<text x="475" y="90" text-anchor="middle" fill="white" font-size="12">
Join (Spark)
</text>


<rect x="400" y="140" width="150" height="50" rx="8" fill="#00695C">
</rect>
<text x="475" y="170" text-anchor="middle" fill="white" font-size="12">
Stats Engine
</text>


<rect x="620" y="50" width="160" height="50" rx="8" fill="#E65100">
</rect>
<text x="700" y="80" text-anchor="middle" fill="white" font-size="12">
Results Dashboard
</text>


<rect x="620" y="140" width="160" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="700" y="160" text-anchor="middle" fill="white" font-size="12">
Auto-Stop /
</text>
<text x="700" y="175" text-anchor="middle" fill="white" font-size="12">
Guardrail Alerts
</text>


<rect x="400" y="260" width="150" height="50" rx="8" fill="#1565C0">
</rect>
<text x="475" y="280" text-anchor="middle" fill="white" font-size="12">
Variance Reduction
</text>
<text x="475" y="295" text-anchor="middle" fill="white" font-size="12">
(CUPED)
</text>


<line x1="150" y1="75" x2="195" y2="105" stroke="#81D4FA" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="150" y1="155" x2="195" y2="125" stroke="#81D4FA" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="340" y1="115" x2="395" y2="80" stroke="#81D4FA" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="550" y1="75" x2="615" y2="75" stroke="#81D4FA" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="475" y1="100" x2="475" y2="135" stroke="#FF9800" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="550" y1="165" x2="615" y2="165" stroke="#FF9800" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="475" y1="190" x2="475" y2="255" stroke="#81D4FA" stroke-width="2" marker-end="url(#a3)">
</line>


<text x="475" y="240" fill="#aaa" font-size="11" text-anchor="middle">
pre-experiment covariate
</text>


</svg>

</details>


**Step 1:** Exposure events (who saw what variant) and metric events (conversions, clicks, revenue) flow into event pipeline (Kafka → Spark)
**Step 2:** Exposure-Metric Join: match metric events to the variant the user was assigned to via user_id + timestamp
**Step 3:** Stats Engine computes per-variant aggregates, applies CUPED variance reduction, runs hypothesis tests
**Step 4:** Dashboard shows lift, p-value, confidence intervals; guardrail alerts fire if degradation detected

Deep Dive: Statistical Methods

Fixed-Horizon vs Sequential Testing
`// Fixed-Horizon (Classical A/B Test)
// - Set sample size upfront based on MDE, power, significance
// - Wait until sample collected, then analyze ONCE
// - Problem: "peeking" inflates false positive rate

n = (Z_α/2 + Z_β)² × (σ₁² + σ₂²) / (μ₁ - μ₂)²
// For 5% significance, 80% power, 2% MDE on 10% baseline conversion:
// n ≈ 39,200 per variant

// Sequential Testing (Always-Valid p-values)
// - Can peek at results at any time without inflating false positives
// - Uses confidence sequences instead of confidence intervals
// - Methods: mSPRT (mixture Sequential Probability Ratio Test)

// CUPED (Controlled-experiment Using Pre-Experiment Data)
// Reduces variance by 20-50%, cutting experiment duration significantly
Ŷ_cuped = Y - θ × (X - X̄)
// where X = pre-experiment metric (covariate)
// θ = Cov(X,Y) / Var(X)
// This adjusts for pre-existing user behavior differences`

Multiple Testing Correction
`// Problem: Testing 20 metrics at α=0.05 → expect 1 false positive!
// Solutions:
// 1. Bonferroni: α_adj = α / m (too conservative)
// 2. Benjamini-Hochberg (FDR): controls false discovery rate
//    - Sort p-values: p(1) ≤ p(2) ≤ ... ≤ p(m)
//    - Find largest k where p(k) ≤ k/m × α
//    - Reject all H0 for i ≤ k

// 3. Primary vs secondary metrics:
//    - Apply correction only to primary metrics
//    - Secondary metrics are exploratory (flag but don't decide)`


Deep Dive: CUPED Variance Reduction


**CUPED** (Microsoft Research, 2013) uses pre-experiment data to reduce noise:

`// Without CUPED: need 40K users for 2% MDE detection
// With CUPED: need ~20K users (50% variance reduction!)

// Algorithm:
// 1. For each user, get pre-experiment metric X (e.g., last 7 days revenue)
// 2. Compute θ = Cov(X, Y) / Var(X)
// 3. Adjusted metric: Ŷ = Y - θ(X - X̄)
// 4. Run t-test on Ŷ instead of Y

// Why it works:
// - Users who spent a lot before experiment will likely spend a lot during
// - Subtracting this "expected" behavior isolates the treatment effect
// - Mathematically: Var(Ŷ) = Var(Y)(1 - ρ²) where ρ = correlation(X,Y)
// - Higher correlation → more variance reduction`


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 600" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="ac" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#4CAF50">
</polygon>
</marker>
</defs>


<text x="550" y="25" text-anchor="middle" fill="#4CAF50" font-size="16" font-weight="bold">
Experimentation Platform Architecture
</text>


<rect x="20" y="45" width="120" height="45" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="73" text-anchor="middle" fill="white" font-size="12">
Experiment UI
</text>


<rect x="20" y="110" width="120" height="45" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="138" text-anchor="middle" fill="white" font-size="12">
Client Apps
</text>


<rect x="200" y="45" width="140" height="45" rx="8" fill="#E65100">
</rect>
<text x="270" y="73" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>


<rect x="400" y="45" width="150" height="45" rx="8" fill="#1565C0">
</rect>
<text x="475" y="73" text-anchor="middle" fill="white" font-size="12">
Config Service
</text>


<rect x="400" y="110" width="150" height="45" rx="8" fill="#1565C0">
</rect>
<text x="475" y="138" text-anchor="middle" fill="white" font-size="12">
Assignment Service
</text>


<rect x="400" y="175" width="150" height="45" rx="8" fill="#1565C0">
</rect>
<text x="475" y="203" text-anchor="middle" fill="white" font-size="12">
Analysis Service
</text>


<rect x="620" y="45" width="140" height="45" rx="8" fill="#4E342E">
</rect>
<text x="690" y="73" text-anchor="middle" fill="white" font-size="12">
Experiment DB
</text>


<rect x="620" y="110" width="140" height="45" rx="8" fill="#00695C">
</rect>
<text x="690" y="138" text-anchor="middle" fill="white" font-size="12">
Config Cache
</text>


<rect x="200" y="175" width="140" height="45" rx="8" fill="#6A1B9A">
</rect>
<text x="270" y="203" text-anchor="middle" fill="white" font-size="12">
Client SDK
</text>


<rect x="200" y="290" width="140" height="45" rx="8" fill="#6A1B9A">
</rect>
<text x="270" y="318" text-anchor="middle" fill="white" font-size="12">
Kafka Events
</text>


<rect x="400" y="290" width="150" height="45" rx="8" fill="#1565C0">
</rect>
<text x="475" y="318" text-anchor="middle" fill="white" font-size="12">
Flink Streaming
</text>


<rect x="620" y="290" width="140" height="45" rx="8" fill="#4E342E">
</rect>
<text x="690" y="318" text-anchor="middle" fill="white" font-size="12">
ClickHouse
</text>


<rect x="400" y="370" width="150" height="45" rx="8" fill="#1565C0">
</rect>
<text x="475" y="398" text-anchor="middle" fill="white" font-size="12">
Spark Batch
</text>


<rect x="620" y="370" width="140" height="45" rx="8" fill="#1565C0">
</rect>
<text x="690" y="398" text-anchor="middle" fill="white" font-size="12">
Stats Engine
</text>


<rect x="830" y="290" width="140" height="45" rx="8" fill="#E65100">
</rect>
<text x="900" y="318" text-anchor="middle" fill="white" font-size="12">
Results Dashboard
</text>


<rect x="830" y="370" width="140" height="45" rx="8" fill="#2E7D32">
</rect>
<text x="900" y="398" text-anchor="middle" fill="white" font-size="12">
Alerting System
</text>


<rect x="830" y="45" width="140" height="45" rx="8" fill="#607D8B">
</rect>
<text x="900" y="73" text-anchor="middle" fill="white" font-size="12">
CDN Edge
</text>


<line x1="140" y1="67" x2="195" y2="67" stroke="#4CAF50" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="140" y1="132" x2="195" y2="197" stroke="#4CAF50" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="340" y1="67" x2="395" y2="67" stroke="#4CAF50" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="550" y1="67" x2="615" y2="67" stroke="#4CAF50" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="550" y1="132" x2="615" y2="132" stroke="#4CAF50" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="760" y1="67" x2="825" y2="67" stroke="#4CAF50" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="340" y1="197" x2="395" y2="197" stroke="#4CAF50" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="270" y1="220" x2="270" y2="285" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="340" y1="312" x2="395" y2="312" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="550" y1="312" x2="615" y2="312" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="760" y1="312" x2="825" y2="312" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="475" y1="335" x2="475" y2="365" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="550" y1="392" x2="615" y2="392" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="760" y1="392" x2="825" y2="392" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


</svg>

</details>


## 💾 Database Schema


### PostgreSQL — Experiment Config

`CREATE TABLE experiments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), --  `PK`
  name VARCHAR(255) NOT NULL UNIQUE, --  `IDX`
  layer_id UUID NOT NULL REFERENCES layers(id), --  `FK`
  status VARCHAR(20) DEFAULT 'draft', --  `IDX`
  hypothesis TEXT,
  targeting_rules JSONB NOT NULL DEFAULT '{}',
  salt VARCHAR(64) NOT NULL DEFAULT gen_random_uuid(),
  traffic_percentage DECIMAL(5,2) DEFAULT 100.00,
  primary_metric_id UUID REFERENCES metrics(id),
  significance_level DECIMAL(4,3) DEFAULT 0.050,
  power DECIMAL(4,3) DEFAULT 0.800,
  min_detectable_effect DECIMAL(6,4),
  started_at TIMESTAMPTZ,
  ended_at TIMESTAMPTZ,
  created_by UUID NOT NULL,
  created_at TIMESTAMPTZ DEFAULT now(),
  version INT DEFAULT 1
);
--  `IDX`: (layer_id, status) for finding active experiments per layer
--  `IDX`: (status, started_at) for lifecycle management

CREATE TABLE variants (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  experiment_id UUID NOT NULL REFERENCES experiments(id), --  `FK`
  name VARCHAR(100) NOT NULL,
  weight INT NOT NULL, -- percentage × 100 for precision
  config JSONB NOT NULL DEFAULT '{}',
  is_control BOOLEAN DEFAULT false,
  UNIQUE(experiment_id, name)
);

CREATE TABLE layers (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255) NOT NULL UNIQUE,
  description TEXT,
  domain_start INT DEFAULT 0,
  domain_end INT DEFAULT 9999
);

CREATE TABLE experiment_guardrails (
  experiment_id UUID REFERENCES experiments(id),
  metric_id UUID REFERENCES metrics(id),
  direction VARCHAR(10) NOT NULL, -- 'increase' | 'decrease'
  threshold DECIMAL(8,4), -- alert if metric moves > threshold
  PRIMARY KEY (experiment_id, metric_id)
);`


### ClickHouse — Event Analytics

`-- Exposure events (who saw which variant)
CREATE TABLE exposures (
  event_id UUID,
  user_id UInt64, --  `SHARD`
  experiment_id UUID,
  variant_id UUID,
  variant_name String,
  timestamp DateTime64(3),
  context Map(String, String), -- device, country, etc.
  sdk_version String
) ENGINE = MergeTree()
PARTITION BY toYYYYMMDD(timestamp)
ORDER BY (experiment_id, user_id, timestamp);

-- Metric events (what users did)
CREATE TABLE metric_events (
  event_id UUID,
  user_id UInt64,
  metric_name String,
  metric_value Float64,
  timestamp DateTime64(3),
  properties Map(String, String)
) ENGINE = MergeTree()
PARTITION BY toYYYYMMDD(timestamp)
ORDER BY (metric_name, user_id, timestamp);

-- Pre-aggregated experiment results (materialized view)
CREATE MATERIALIZED VIEW experiment_results_mv
ENGINE = AggregatingMergeTree()
ORDER BY (experiment_id, variant_id, metric_name, date)
AS SELECT
  e.experiment_id,
  e.variant_id,
  m.metric_name,
  toDate(m.timestamp) AS date,
  countState() AS sample_size,
  sumState(m.metric_value) AS total_value,
  avgState(m.metric_value) AS avg_value,
  varSampState(m.metric_value) AS variance
FROM exposures e
JOIN metric_events m ON e.user_id = m.user_id
GROUP BY experiment_id, variant_id, metric_name, date;`


## ⚡ Cache & CDN Deep Dive


### Experiment Config Distribution


| Layer            | Strategy                 | TTL                 | Purpose                          |
|------------------|--------------------------|---------------------|----------------------------------|
| CDN Edge         | Push on change           | 5 min max           | Serve SDK config downloads       |
| SDK Local Cache  | In-memory + localStorage | Until SSE update    | Zero-latency variant lookup      |
| Assignment Redis | Write-through            | Experiment duration | Override assignments (debugging) |
| Results Cache    | LRU + TTL                | 5 min               | Dashboard query results          |


Config Propagation Protocol
`// When experiment config changes:
1. Config Service updates Experiment DB (source of truth)
2. Publishes config change event to Kafka topic "experiment-config-changes"
3. CDN invalidation triggered (purge edge caches)
4. SSE fanout service pushes update to all connected SDKs
5. SDK receives update, atomically swaps local config
   Average propagation time: ~2-5 seconds globally

// Fail-safe: SDKs poll every 5 minutes as backup
// On SDK init with no cached config: synchronous HTTP fetch`


## 📈 Scaling Considerations


### Assignment Service Scaling


- **Stateless computation:** assignment is pure function of (user_id, experiment_config) → horizontally scalable

- **Client-side evaluation:** push config to SDKs, eliminate server calls entirely; this is how 10K+ experiments scale

- **Config snapshot size:** serialize active experiments as protobuf (~50KB for 1000 experiments); fits in single CDN fetch

- **Event ingestion:** Kafka with 100+ partitions for exposure/metric events; 1M+ events/sec


### Analysis Pipeline Scaling


- **Incremental computation:** don't recompute all stats from scratch; use streaming aggregates in Flink for real-time and Spark batch for full precision

- **Pre-aggregation:** ClickHouse materialized views maintain running sums/counts per experiment×variant×metric

- **Parallel experiment analysis:** each experiment analyzed independently → embarrassingly parallel

- **CUPED at scale:** pre-compute covariates in nightly batch job; store in feature store for fast access


## ⚖️ Tradeoffs


> **

Client-Side Evaluation


- Zero-latency variant lookup

- Works offline

- No server dependency


Client-Side Evaluation


- Config size grows with experiments

- SDK complexity increases

- Config propagation delay


Sequential Testing


- Can stop experiments early

- Peek at results safely

- Faster iteration


Sequential Testing


- Wider confidence intervals

- More complex to implement

- Requires larger sample for same power


## 🔄 Alternative Approaches


Multi-Armed Bandit (MAB)


Instead of fixed allocation, dynamically shift traffic to winning variant. Thompson Sampling or UCB algorithm. Maximizes reward during experiment but trades off statistical rigor for practical optimization. Best for short-lived optimizations (ad copy, button color).


Bayesian A/B Testing


Replace p-values with posterior probability distributions. Output: "92% probability that treatment is better" (more intuitive). Use Beta-Binomial for conversion metrics, Normal-Normal for continuous. No fixed sample size needed. Popular in industry (VWO, Dynamic Yield).


Interleaving (Ranking Experiments)


For search/recommendation, interleave results from control and treatment in same result page. Each user sees both. Much more sensitive than A/B test (10x fewer users needed). Used by Netflix, Spotify for ranking model evaluation.


## 📚 Additional Information


- **Novelty & primacy effects:** new treatments often show initial lift that fades. Run experiments for at least 2 full business cycles (weekdays + weekends).

- **Network effects / SUTVA violation:** if users interact (social networks), treatment on user A affects control user B. Use cluster randomization (assign entire friend groups together) or switchback experiments.

- **Simpson's Paradox:** overall metric improves but subgroups worsen. Always do heterogeneous treatment effect analysis (slice by segments).

- **Triggering / dilution:** only analyze users who actually "triggered" the experiment (saw the feature), not all assigned users. Reduces dilution and increases sensitivity.

- **Key papers:** Google (Overlapping Experiment Infrastructure, 2010), Microsoft (Trustworthy Online Controlled Experiments, 2020), Netflix (Interleaving, 2018), Meta (PlanOut, 2014)