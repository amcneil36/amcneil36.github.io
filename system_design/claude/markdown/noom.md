# 🥗 Noom - System Design


## Functional Requirements


1. **Food Logging & Calorie Tracking:** Log meals by search/barcode/photo, auto-calculate calories via food database (800K+ items), color-coded system (green/yellow/red caloric density), daily/weekly summaries

2. **Behavioral Coaching & Curriculum:** Daily psychology-based lessons (CBT articles), 1-on-1 coach messaging, group support, habit tracking, goal setting with personalized weight loss plan

3. **Weight & Activity Tracking:** Daily weigh-ins with trend graph, step tracking (HealthKit/Google Fit integration), exercise logging, progress analytics with ML-predicted trajectory


## Non-Functional Requirements


- **Scale:** 45M+ downloads, ~1M active subscribers, ~10M food logs/day, ~2M messages/day

- **Latency:** Food search <200ms, barcode lookup <100ms, photo recognition <3s, lesson load <1s

- **Availability:** 99.9% — logging interruptions break user habits and reduce retention

- **Data Privacy:** HIPAA-adjacent health data; GDPR compliant; weight/food data is sensitive PII

- **Engagement:** Push notifications must arrive within 5 minutes of trigger; lesson delivery timed to user habits


## Flow 1: Food Logging & Calorie Calculation


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#00B894">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Mobile App
</text>
<text x="80" y="87" text-anchor="middle" fill="#fff" font-size="10">
(iOS / Android)
</text>


<rect x="190" y="50" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="255" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API Gateway
</text>
<text x="255" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Kong / Nginx)
</text>


<rect x="370" y="20" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="435" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Food Search
</text>
<text x="435" y="57" text-anchor="middle" fill="#fff" font-size="10">
(Elasticsearch)
</text>


<rect x="370" y="90" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Photo Recognizer
</text>
<text x="435" y="127" text-anchor="middle" fill="#fff" font-size="10">
(CNN / Vision)
</text>


<rect x="370" y="160" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="435" y="182" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Barcode Scanner
</text>
<text x="435" y="197" text-anchor="middle" fill="#fff" font-size="10">
(UPC Lookup)
</text>


<rect x="560" y="50" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="625" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Food Database
</text>
<text x="625" y="87" text-anchor="middle" fill="#fff" font-size="10">
(800K+ Items)
</text>


<rect x="560" y="130" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="625" y="152" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Calorie Engine
</text>
<text x="625" y="167" text-anchor="middle" fill="#fff" font-size="10">
(Color Classifier)
</text>


<rect x="750" y="80" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="815" y="102" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
User Log DB
</text>
<text x="815" y="117" text-anchor="middle" fill="#fff" font-size="10">
(PostgreSQL)
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#00B894" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="320" y1="65" x2="365" y2="45" stroke="#00B894" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="320" y1="80" x2="365" y2="115" stroke="#00B894" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="320" y1="90" x2="365" y2="180" stroke="#00B894" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="45" x2="555" y2="65" stroke="#00B894" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="115" x2="555" y2="80" stroke="#00B894" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="185" x2="555" y2="80" stroke="#00B894" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="690" y1="75" x2="625" y2="125" stroke="#00B894" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="690" y1="155" x2="745" y2="105" stroke="#00B894" stroke-width="2" marker-end="url(#ah)">
</line>


</svg>

</details>


### Steps


1. User logs food via one of three methods: text search, barcode scan, or food photo

2. **Text Search:** Elasticsearch query against 800K+ food database (USDA + branded foods + user-created)

3. **Barcode:** UPC code looked up against OpenFoodFacts + proprietary barcode→food mapping (Redis cache)

4. **Photo:** Image sent to CNN-based food recognition model (MobileNet/EfficientNet) → top-5 predictions with confidence scores

5. User confirms food item and serving size; Calorie Engine calculates: calories, macros (protein/carbs/fat), caloric density

6. Color classification: Green (low caloric density, <1 cal/g), Yellow (1-2.4 cal/g), Red (>2.4 cal/g)

7. Log entry persisted to PostgreSQL with timestamp, meal type (breakfast/lunch/dinner/snack), servings

8. Daily summary updated: total calories, color distribution (% green/yellow/red), budget remaining


### Example


User scans barcode of Greek yogurt → UPC 049022870000 → Redis cache hit → "Chobani Greek Yogurt, Plain, 5.3oz". Calorie Engine: 80 cal, 15g protein, 0g fat, 6g carbs, caloric density 0.53 cal/g → GREEN. Logged as breakfast. Daily total: 320/1,500 cal budget, 60% green distribution.


### Deep Dives


Food Database


**Sources:** USDA FoodData Central (300K+), Open Food Facts (2M+ barcoded), restaurant menus (scraped/partnered), user-submitted


**Schema:** Per food: name, brand, serving_size, calories, protein, carbs, fat, fiber, sodium, caloric_density, noom_color


**Search:** Elasticsearch with synonym expansion ("chicken breast" = "grilled chicken"), fuzzy matching, brand boosting


**Updates:** USDA data refreshed quarterly; barcode data updated daily from OpenFoodFacts API


Food Photo Recognition


**Model:** EfficientNet-B4 fine-tuned on 500K+ labeled food images → 250 food categories


**Inference:** Server-side GPU inference (~500ms); returns top-5 predictions with confidence


**Portion:** Depth estimation (LiDAR on iPhone Pro) for portion size estimation — experimental


**Accuracy:** ~85% top-1 for common foods; lower for mixed dishes; user correction feeds training loop


Caloric Density Color System


**Green:** <1 cal/gram — fruits, vegetables, non-fat dairy, broth-based soups (eat more freely)


**Yellow:** 1-2.4 cal/gram — grains, lean meats, eggs, beans (moderate portions)


**Red:** >2.4 cal/gram — nuts, oils, cheese, processed snacks, desserts (mindful portions)


**Psychology:** No "forbidden" foods — traffic light system reduces cognitive load vs. macro counting


## Flow 2: Behavioral Coaching & Daily Curriculum


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FDA085">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="90" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Mobile App
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Curriculum
</text>
<text x="255" y="97" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Engine
</text>


<rect x="370" y="30" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Content CMS
</text>
<text x="435" y="67" text-anchor="middle" fill="#fff" font-size="10">
(CBT Lessons)
</text>


<rect x="370" y="100" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="435" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Personalization
</text>
<text x="435" y="137" text-anchor="middle" fill="#fff" font-size="10">
(ML Sequencer)
</text>


<rect x="560" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="625" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Coach Messaging
</text>
<text x="625" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Async Chat)
</text>


<rect x="740" y="60" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="805" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Group Support
</text>
<text x="805" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Cohort Chat)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#FDA085" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#FDA085" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="95" x2="365" y2="120" stroke="#FDA085" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="500" y1="55" x2="555" y2="75" stroke="#FDA085" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="690" y1="85" x2="735" y2="85" stroke="#FDA085" stroke-width="2" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Steps


1. Each morning, Curriculum Engine selects daily lessons based on user's program week and progress

2. Content CMS stores 300+ CBT-based articles (cognitive behavioral therapy) organized into 16-week curriculum tracks

3. Personalization ML adjusts lesson sequence based on: engagement (article completion rate), logged behaviors, weight trend

4. User reads 1-3 articles per day with interactive quizzes; progress tracked for coach visibility

5. 1-on-1 coach messaging: asynchronous chat (not real-time); coaches manage 200-300 users each

6. Group support: cohort of ~30 users with shared goal coach; social accountability and peer support

7. Coach receives AI-generated insights: "User hasn't logged food in 3 days" → proactive outreach


### Example


User is in Week 4 of program. Curriculum Engine selects: "Understanding Emotional Eating" (CBT article) + "Trigger Tracking Exercise" (interactive). User hasn't logged food in 2 days → ML flags for coach. Coach sends: "Hey! I noticed you've been quiet — how are things going?" User responds, coach provides motivational support and re-engagement strategies.


### Deep Dives


Curriculum Personalization


**Model:** Multi-armed bandit for lesson selection — explore/exploit balance for engagement optimization


**Features:** Program week, completion rate, logging frequency, weight trend slope, time-of-day engagement pattern


**Tracks:** Weight loss, diabetes prevention (DPP-certified), stress management, healthy eating — program customized at onboarding


Coach AI Assistance


**Alerts:** ML generates coach alerts: missed logging, weight plateau, high-red-food days, missed weigh-ins


**Templates:** AI-suggested message templates based on user situation; coach personalizes before sending


**Caseload:** Each coach manages 200-300 users; AI prioritization queue surfaces users needing most attention


## Flow 3: Weight Tracking & Progress Analytics


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#6C5CE7">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Mobile App
</text>
<text x="80" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Weigh-in)
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Weight Service
</text>


<rect x="370" y="30" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Trend Analyzer
</text>
<text x="435" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Smoothing + ML)
</text>


<rect x="370" y="100" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="435" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
HealthKit / Fit
</text>
<text x="435" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Steps + Activity)
</text>


<rect x="560" y="60" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="625" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Calorie Budget
</text>
<text x="625" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Dynamic Adjust)
</text>


<rect x="740" y="60" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="805" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Analytics DB
</text>
<text x="805" y="97" text-anchor="middle" fill="#fff" font-size="10">
(TimescaleDB)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#6C5CE7" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#6C5CE7" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="95" x2="365" y2="120" stroke="#6C5CE7" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="55" x2="555" y2="75" stroke="#6C5CE7" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="125" x2="555" y2="95" stroke="#6C5CE7" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="690" y1="85" x2="735" y2="85" stroke="#6C5CE7" stroke-width="2" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Steps


1. User logs daily weight (manual entry or Bluetooth smart scale sync)

2. Weight Service stores raw weigh-in and computes smoothed trend (exponential moving average to filter daily fluctuations)

3. Trend Analyzer ML predicts trajectory: "At current rate, you'll reach goal weight in 8 weeks"

4. HealthKit (iOS) / Google Fit (Android) integration syncs steps, active minutes, exercise sessions automatically

5. Calorie Budget dynamically adjusts based on: activity level (step count), weight trend, metabolic rate estimate (Mifflin-St Jeor equation)

6. All time-series data stored in TimescaleDB for efficient range queries and aggregations

7. Weekly progress reports generated: weight change, logging consistency, color distribution trends, streak tracking


### Example


User weighs in at 178.2 lbs (yesterday: 179.0). Raw weight fluctuates, but 7-day EMA trend shows 178.5 (down from 180.1 last week = 1.6 lbs/week). ML predicts goal of 165 lbs reachable in ~8 weeks. HealthKit reports 8,200 steps today → calorie budget increased by 120 to 1,620 cal. Weekly report: 6/7 days logged, 55% green distribution (up from 45% last week).


### Deep Dives


Weight Trend Smoothing


**Algorithm:** Exponential Moving Average (EMA) with α=0.1 — smooths daily water weight fluctuations


**Display:** Raw dots + smoothed trend line on graph; users see actual progress vs. noise


**Psychology:** Smoothed trend prevents discouragement from daily weight spikes (water retention, sodium intake)


Dynamic Calorie Budget


**Base:** Mifflin-St Jeor BMR × activity multiplier (1.2-1.9) — deficit based on target loss rate (0.5-1 lb/week)


**Adjustment:** Steps > 10K/day → +100 cal; exercise logged → calories added proportionally


**Floor:** Never below 1,200 cal (women) or 1,500 cal (men) for safety


## Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 450" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#00B894">
</polygon>
</marker>
</defs>


<rect x="20" y="80" width="130" height="50" rx="8" fill="#34A853">
</rect>
<text x="85" y="105" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Mobile Apps
</text>
<text x="85" y="120" text-anchor="middle" fill="#fff" font-size="10">
(iOS / Android)
</text>


<rect x="200" y="80" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="265" y="105" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API Gateway
</text>


<rect x="390" y="20" width="130" height="45" rx="8" fill="#4285F4">
</rect>
<text x="455" y="47" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Food Service
</text>


<rect x="390" y="80" width="130" height="45" rx="8" fill="#4285F4">
</rect>
<text x="455" y="107" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Weight Service
</text>


<rect x="390" y="140" width="130" height="45" rx="8" fill="#4285F4">
</rect>
<text x="455" y="167" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Curriculum Engine
</text>


<rect x="390" y="200" width="130" height="45" rx="8" fill="#4285F4">
</rect>
<text x="455" y="227" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Coach Service
</text>


<rect x="580" y="20" width="130" height="45" rx="8" fill="#9C27B0">
</rect>
<text x="645" y="47" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Food Photo ML
</text>


<rect x="580" y="80" width="130" height="45" rx="8" fill="#9C27B0">
</rect>
<text x="645" y="107" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Trend Predictor
</text>


<rect x="580" y="140" width="130" height="45" rx="8" fill="#E53935">
</rect>
<text x="645" y="167" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Push Notifications
</text>


<rect x="580" y="200" width="130" height="45" rx="8" fill="#00897B">
</rect>
<text x="645" y="222" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
HealthKit / Fit
</text>
<text x="645" y="237" text-anchor="middle" fill="#fff" font-size="10">
Integration
</text>


<rect x="390" y="320" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="455" y="342" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
PostgreSQL
</text>
<text x="455" y="357" text-anchor="middle" fill="#fff" font-size="10">
(Users + Logs)
</text>


<rect x="580" y="320" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="645" y="342" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Elasticsearch
</text>
<text x="645" y="357" text-anchor="middle" fill="#fff" font-size="10">
(Food Search)
</text>


<rect x="770" y="320" width="130" height="55" rx="8" fill="#00897B">
</rect>
<text x="835" y="342" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Redis
</text>
<text x="835" y="357" text-anchor="middle" fill="#fff" font-size="10">
(Cache + Barcodes)
</text>


<rect x="770" y="80" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="835" y="102" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
TimescaleDB
</text>
<text x="835" y="117" text-anchor="middle" fill="#fff" font-size="10">
(Time-Series)
</text>


<line x1="150" y1="105" x2="195" y2="105" stroke="#00B894" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="330" y1="95" x2="385" y2="42" stroke="#00B894" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="330" y1="105" x2="385" y2="102" stroke="#00B894" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="330" y1="110" x2="385" y2="162" stroke="#00B894" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="330" y1="120" x2="385" y2="222" stroke="#00B894" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="520" y1="42" x2="575" y2="42" stroke="#00B894" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="520" y1="102" x2="575" y2="102" stroke="#00B894" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="520" y1="162" x2="575" y2="162" stroke="#00B894" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="520" y1="222" x2="575" y2="222" stroke="#00B894" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="710" y1="102" x2="765" y2="102" stroke="#00B894" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="455" y1="245" x2="455" y2="315" stroke="#00B894" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="520" y1="65" x2="580" y2="330" stroke="#00B894" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="650" y1="65" x2="770" y2="340" stroke="#00B894" stroke-width="1.5" marker-end="url(#ah4)">
</line>


</svg>

</details>


## Database Schema


### PostgreSQL — Users & Food Logs

`CREATE TABLE users (
  user_id       UUID PRIMARY KEY,         `PK`
  email         VARCHAR(255) UNIQUE,      `IDX`
  name          VARCHAR(100),
  goal_weight   DECIMAL(5,1),
  start_weight  DECIMAL(5,1),
  height_cm     INT,
  dob           DATE,
  gender        VARCHAR(10),
  activity_level VARCHAR(20),
  program_type  VARCHAR(30),  -- weight_loss, dpp, healthy_eating
  program_week  INT DEFAULT 1,
  coach_id      UUID,                     `FK`
  group_id      UUID,                     `FK`
  calorie_budget INT,
  subscription_status VARCHAR(20),
  created_at    TIMESTAMPTZ
);

CREATE TABLE food_logs (
  log_id        UUID PRIMARY KEY,         `PK`
  user_id       UUID REFERENCES users,    `FK`
  food_id       UUID,                     `FK`
  food_name     VARCHAR(255),
  meal_type     VARCHAR(20),  -- breakfast, lunch, dinner, snack
  servings      DECIMAL(4,2),
  calories      INT,
  protein_g     DECIMAL(5,1),
  carbs_g       DECIMAL(5,1),
  fat_g         DECIMAL(5,1),
  noom_color    VARCHAR(10),  -- green, yellow, red
  input_method  VARCHAR(20),  -- search, barcode, photo, manual
  logged_at     TIMESTAMPTZ               `IDX`
);
CREATE INDEX idx_food_logs_user_date ON food_logs(user_id, logged_at DESC);`


 `SHARD` Sharded by user_id for data locality — all user's logs on same shard


### TimescaleDB — Weight & Activity

`CREATE TABLE weight_entries (
  user_id       UUID,                     `FK`
  weight_lbs    DECIMAL(5,1),
  ema_trend     DECIMAL(5,1),  -- exponential moving average
  recorded_at   TIMESTAMPTZ
);
SELECT create_hypertable('weight_entries', 'recorded_at');
CREATE INDEX idx_weight_user ON weight_entries(user_id, recorded_at DESC);

CREATE TABLE activity_entries (
  user_id       UUID,                     `FK`
  steps         INT,
  active_mins   INT,
  calories_burned INT,
  source        VARCHAR(20),  -- healthkit, google_fit, manual
  recorded_at   TIMESTAMPTZ
);
SELECT create_hypertable('activity_entries', 'recorded_at');

CREATE TABLE lesson_progress (
  user_id       UUID,                     `FK`
  lesson_id     UUID,                     `FK`
  status        VARCHAR(20),  -- not_started, in_progress, completed
  quiz_score    INT,
  time_spent_s  INT,
  completed_at  TIMESTAMPTZ
);`


## Cache & CDN Deep Dive


Food Search Cache (Redis)


**Strategy:** LRU cache for popular food searches — top 10K queries cached (covers 80% of lookups)


**Barcode:** Redis hash map: UPC → food_id (100% cache hit for previously scanned items)


**TTL:** Search cache: 1 hour; barcode cache: 7 days (food nutrition data rarely changes)


CDN (Cloudflare)


**Cached:** Lesson content (articles, images, quizzes), food images, app assets


**Policy:** Lesson content: max-age=86400 (24h), food images: max-age=604800 (7d)


**Personalized:** No CDN for personalized data (food logs, weight) — served directly from API


Client-Side Cache


**SQLite:** Local SQLite database on device for offline food logging (sync on reconnect)


**Frequent Foods:** User's top 50 recently-logged foods cached locally for quick re-logging


**Lessons:** Pre-fetched next 3 lessons for offline reading


## Scaling & Load Balancing


- **API Gateway:** Kong / Nginx with rate limiting per user (100 req/min); JWT-based auth

- **Food Service:** Stateless, horizontally scaled; Elasticsearch cluster with 5 shards, 2 replicas

- **ML Inference:** Food photo recognition on GPU instances (NVIDIA T4); auto-scaled 2-10 instances based on upload queue depth

- **Database:** PostgreSQL primary with 2 read replicas; TimescaleDB for time-series with automatic partitioning by month

- **Push Notifications:** Firebase Cloud Messaging (FCM) + APNs; notification scheduler processes 1M+ notifications/day via SQS queue

- **Coach Platform:** Separate admin service with its own scaling — coach-facing dashboard reads from read replicas


## Tradeoffs


> **

✅ Caloric Density Colors (vs. Macros)


- Simpler for users — no macro counting, just color awareness

- Backed by volumetrics research (Barbara Rolls, Penn State)

- Reduces cognitive load → higher adherence rates


❌ Caloric Density Colors


- Less precise than macro tracking for athletes/bodybuilders

- Some healthy high-calorie foods unfairly "red" (nuts, avocado, olive oil)

- Oversimplification may miss micronutrient deficiencies


✅ Human Coaches (vs. AI-Only)


- Empathetic support for emotional/behavioral challenges

- Accountability — real human checking in increases adherence

- Handles nuanced situations AI can't (grief, stress eating, medical issues)


❌ Human Coaches


- Expensive — coaches are the largest COGS (limits margins)

- Scaling bottleneck — hiring and training coaches takes time

- Inconsistent quality across 3,000+ coaches


## Alternative Approaches


AI-First Coaching (ChatGPT-style)


Replace human coaches with LLM-powered conversational AI. Infinitely scalable, 24/7 availability, consistent quality. But lacks genuine empathy, may give harmful advice, and regulatory concerns around AI providing health guidance without human oversight.


Macro Tracking (MyFitnessPal Model)


Full macro counting (protein/carbs/fat targets) instead of color system. More precise for advanced users but higher cognitive load, lower adherence for general population, and research shows calorie counting alone without behavioral change doesn't sustain long-term weight loss.


Wearable-First (Continuous Monitoring)


CGM (continuous glucose monitor) + smartwatch for passive data collection — no manual food logging. Emerging approach (Levels, Signos). More accurate but expensive hardware, invasive (CGM sensor), and food logging itself serves as a mindfulness intervention.


## Additional Information


- **DPP Certification:** Noom is CDC-recognized as a Diabetes Prevention Program (DPP) — clinically validated 16-week curriculum shown to reduce Type 2 diabetes risk by 58%

- **Retention Mechanics:** Streaks (consecutive logging days), progress milestones with celebrations, weekly weigh-in reminders, "Noom Master" lessons for completed users

- **Subscription Model:** $59/month (annual) to $199/month (monthly) — high price justified by human coaching; 70%+ of revenue from US market

- **A/B Testing Platform:** Extensive experimentation on lesson content, notification timing, UI flows, pricing — Noom runs 50+ concurrent experiments; Optimizely + internal platform

- **Data Science:** Research team publishes peer-reviewed papers on behavioral weight loss; data from millions of users creates massive observational dataset for health outcomes research