# 🏢 Glassdoor - System Design


## Functional Requirements


1. **Company Reviews & Ratings:** Anonymous employee reviews (overall, culture, management, compensation, work-life balance), CEO approval rating, "recommend to friend" metric, pros/cons structure

2. **Salary Data & Job Search:** Crowdsourced salary reports by role/location/experience, salary estimator, job listings with employer branding, application tracking

3. **Interview Experiences:** Anonymous interview question sharing, difficulty rating, offer/no-offer outcome, interview process timeline, company-specific question database


## Non-Functional Requirements


- **Scale:** 55M+ monthly visitors, 100M+ reviews/salary reports, 2M+ employer profiles, 10M+ job listings

- **Anonymity:** Reviewer identity must be cryptographically protected — even Glassdoor staff cannot link review to individual

- **Integrity:** Fake review detection >95% accuracy; employer cannot manipulate ratings or suppress negative reviews

- **SEO:** Company pages must rank #1 for "{company} reviews" — 60%+ traffic from organic search

- **Latency:** Company page load <1.5s, salary search <500ms, job search <800ms


## Flow 1: Anonymous Review Submission & Moderation


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 320" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#0CAA41">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Employee
</text>
<text x="80" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Anonymous)
</text>


<rect x="190" y="50" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="255" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Review Service
</text>
<text x="255" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Anonymizer)
</text>


<rect x="370" y="20" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="435" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Fraud Detection
</text>
<text x="435" y="57" text-anchor="middle" fill="#fff" font-size="10">
(ML Classifier)
</text>


<rect x="370" y="90" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Content Mod
</text>
<text x="435" y="127" text-anchor="middle" fill="#fff" font-size="10">
(Policy Check)
</text>


<rect x="560" y="50" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="625" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Review DB
</text>
<text x="625" y="87" text-anchor="middle" fill="#fff" font-size="10">
(PostgreSQL)
</text>


<rect x="740" y="50" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="805" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Rating Aggregator
</text>
<text x="805" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Real-Time Calc)
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#0CAA41" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="320" y1="65" x2="365" y2="45" stroke="#0CAA41" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="320" y1="85" x2="365" y2="110" stroke="#0CAA41" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="75" x2="555" y2="75" stroke="#0CAA41" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="690" y1="75" x2="735" y2="75" stroke="#0CAA41" stroke-width="2" marker-end="url(#ah)">
</line>


</svg>

</details>


### Steps


1. Employee submits review: overall rating (1-5), pros, cons, advice to management, employment status, job title, location

2. Review Service anonymizes: strips PII, assigns anonymous review_id, stores user→review mapping in separate encrypted table (inaccessible to content systems)

3. Fraud Detection ML classifier scores review: fake probability based on IP patterns, submission velocity, text analysis, account age, email domain verification

4. Content Moderation checks policy: no naming individuals, no confidential business info, no threats/harassment, meets minimum length

5. Approved reviews persisted to PostgreSQL; rejected reviews sent to human moderation queue

6. Rating Aggregator recalculates company scores: weighted average (recent reviews weighted more), Bayesian smoothing for companies with few reviews

7. "Give to Get" model: user must contribute (review or salary) to unlock full access to others' contributions


### Example


Software engineer at TechCorp submits review: 4/5 overall, "Great engineering culture, competitive pay" (pro), "Long hours during launches" (con). Fraud ML scores 0.05 (legitimate). Content mod passes (no PII, meets policy). Published within 24 hours. TechCorp's rating recalculated: 4.1 → 4.12 (from 3,200 reviews). Review visible on TechCorp's Glassdoor page, indexed by Google within 4 hours.


### Deep Dives


Anonymity Architecture


**Separation:** User identity table and review content table in separate databases with different access controls


**Encryption:** User→review mapping encrypted with HSM-stored keys; requires multi-party approval to decrypt (legal subpoena only)


**No Fingerprinting:** Review metadata stripped of identifiable signals: exact submission time randomized (±hours), IP not stored with review


**Legal:** First Amendment protection for anonymous speech; Glassdoor has fought subpoenas to protect reviewer identity


Fake Review Detection


**Features:** Account age, email domain (company email vs. disposable), submission patterns, text sentiment vs. rating, IP clustering


**Employer Manipulation:** Detect when employer encourages mass positive reviews — burst detection, text similarity within company


**Competitor Attacks:** Detect coordinated negative reviews — IP correlation, timing patterns, reviewer profile analysis


**Model:** Gradient boosted trees with SMOTE oversampling (fake reviews are minority class)


## Flow 2: Salary Data & Estimation


### Steps


1. User submits salary report: base salary, bonus, stock, total comp, job title, experience level, location, company

2. Salary data anonymized and aggregated — individual salaries never displayed; minimum 5 reports per role/company to show data

3. Salary Estimator ML model: predicts expected salary range for role × location × experience × company size × industry

4. Data displayed as ranges (25th, 50th, 75th percentiles) with confidence intervals based on sample size

5. Pay transparency: comparison tools show "You're paid X% below/above market" relative to similar roles


### Deep Dives


Salary Estimation Model


**Model:** Quantile regression (XGBoost) predicting 25th/50th/75th percentile compensation


**Features:** Job title (normalized via NLP), location (cost-of-living adjusted), years of experience, company size, industry, education


**Title Normalization:** NLP model maps 100K+ unique job titles to ~5,000 canonical titles (e.g., "SWE III" → "Senior Software Engineer")


**Refresh:** Model retrained monthly with new salary reports; stale data (>3 years) down-weighted


K-Anonymity for Privacy


**Threshold:** Salary data only shown when k≥5 reports exist for a specific combination (company × role × location)


**Generalization:** If insufficient data, broaden scope: specific city → metro area → state → national


**Suppression:** Outlier salaries (>3σ from mean) suppressed to prevent individual identification


## Flow 3: Job Search & Application


### Steps


1. User searches jobs by keyword, location, salary range, company rating, remote/hybrid/onsite

2. Elasticsearch query with BM25 + personalization boost (past search behavior, saved jobs, profile data)

3. Job listings enriched with Glassdoor-specific data: company rating, salary range, interview difficulty, CEO approval

4. Application: Easy Apply (hosted form) or redirect to employer ATS (Workday, Greenhouse, Lever)

5. Application tracking: status updates pulled from ATS integrations where available

6. Job ads: sponsored listings from employers (CPC auction model) ranked alongside organic results


### Deep Dives


Job Search Ranking


**Organic:** BM25 text relevance × recency × company rating × salary match × location proximity


**Sponsored:** CPC bid × relevance × expected CTR (ML predicted) — blended into organic results with "Sponsored" label


**Personalization:** User's profile (skills, experience, industry) → collaborative filtering for "Jobs you might like" recommendations


Employer Branding


**Enhanced Profile:** Paid employers get: custom photos/videos, "Why Join Us" section, featured reviews, response to reviews


**Analytics:** Employer dashboard: page views, review trends, competitor benchmarking, candidate pipeline metrics


**Revenue:** Employer branding + job ads = primary revenue model (~$300M+ ARR)


## Database Schema


### PostgreSQL — Reviews & Companies

`CREATE TABLE companies (
  company_id     BIGINT PRIMARY KEY,     `PK`
  name           VARCHAR(255),           `IDX`
  slug           VARCHAR(255) UNIQUE,    `IDX`
  industry       VARCHAR(100),
  size_range     VARCHAR(30),  -- 1-50, 51-200, 201-500, ...
  headquarters   VARCHAR(200),
  website        TEXT,
  overall_rating DECIMAL(2,1),
  culture_rating DECIMAL(2,1),
  comp_rating    DECIMAL(2,1),
  wlb_rating     DECIMAL(2,1),
  mgmt_rating    DECIMAL(2,1),
  ceo_approval   DECIMAL(4,1),  -- percentage
  recommend_pct  DECIMAL(4,1),
  review_count   INT DEFAULT 0,
  salary_count   INT DEFAULT 0,
  interview_count INT DEFAULT 0,
  is_premium     BOOLEAN DEFAULT FALSE,
  updated_at     TIMESTAMP
);

CREATE TABLE reviews (
  review_id      BIGINT PRIMARY KEY,     `PK`
  company_id     BIGINT,                 `FK`  `IDX`
  anonymous_id   UUID,  -- NOT linked to user table
  overall_rating TINYINT,
  culture_rating TINYINT,
  comp_rating    TINYINT,
  wlb_rating     TINYINT,
  mgmt_rating    TINYINT,
  job_title      VARCHAR(200),
  location       VARCHAR(200),
  employment_status VARCHAR(20),  -- current, former
  pros           TEXT,
  cons           TEXT,
  advice         TEXT,
  recommend      BOOLEAN,
  ceo_opinion    VARCHAR(20),  -- approve, disapprove, no_opinion
  helpful_count  INT DEFAULT 0,
  status         VARCHAR(20),  -- pending, approved, rejected
  created_at     TIMESTAMP              `IDX`
);`


### Salary & Interview Data

`CREATE TABLE salary_reports (
  report_id      BIGINT PRIMARY KEY,     `PK`
  company_id     BIGINT,                 `FK`  `IDX`
  job_title_raw  VARCHAR(200),
  job_title_norm VARCHAR(200),           `IDX`
  location       VARCHAR(200),
  experience_yrs SMALLINT,
  base_salary    INT,
  bonus          INT,
  stock_grants   INT,
  total_comp     INT,
  currency       CHAR(3),
  pay_period     VARCHAR(20),  -- annual, monthly, hourly
  created_at     TIMESTAMP
);

CREATE TABLE interview_reviews (
  interview_id   BIGINT PRIMARY KEY,     `PK`
  company_id     BIGINT,                 `FK`  `IDX`
  job_title      VARCHAR(200),
  difficulty     TINYINT,  -- 1-5
  experience     VARCHAR(20),  -- positive, neutral, negative
  outcome        VARCHAR(20),  -- offer, no_offer, declined
  application_source VARCHAR(30),
  process_description TEXT,
  questions      TEXT[],  -- array of interview questions
  duration_days  INT,
  created_at     TIMESTAMP
);`


## Cache & CDN Deep Dive


CDN (Cloudflare)


**Pages:** Company pages server-side rendered and cached at CDN edge (TTL: 1hr) — critical for SEO


**Static:** Images, JS bundles, CSS cached with immutable hashed URLs (max-age: 1yr)


**Purge:** New review approved → CDN purge for that company's page (instant refresh)


Redis Cache


**Company Ratings:** Aggregated ratings cached per company (TTL: 5min, invalidated on new review)


**Salary Aggregates:** Percentile salary data cached per company×role (TTL: 1hr)


**Hot Companies:** Top 1,000 companies' full page data pre-cached (covers 80% of traffic)


## Scaling & Load Balancing


- **SEO-Optimized SSR:** Company pages pre-rendered (Gatsby/Next.js ISR) for Google crawling; CDN-cached for performance

- **Database:** PostgreSQL primary + 3 read replicas; read-heavy workload (99% reads, 1% writes)

- **Elasticsearch:** Job search + review search on ES cluster; company-level sharding; reindexed hourly from PostgreSQL

- **Review Pipeline:** Kafka-based async processing — fraud detection, content moderation, rating aggregation all asynchronous

- **Auto-Scale:** Traffic spikes during "best places to work" announcements, layoff news, earnings seasons


## Tradeoffs


> **
✅ "Give to Get" Model
- Ensures constant fresh data supply (users must contribute to access)
- Self-sustaining content ecosystem
- Higher engagement and time-on-site


❌ "Give to Get" Model
- Creates friction — some users leave rather than contribute
- May incentivize low-quality "minimum effort" reviews
- Competitors with free access (Indeed, Blind) attract users unwilling to contribute


## Additional Information


- **Fishbowl Acquisition (2024):** Glassdoor acquired Fishbowl (professional social network) — adds community discussions alongside reviews, creating a more engaged user base.

- **Employer Response:** Premium employers can publicly respond to reviews (without knowing reviewer identity). Studies show employer responses increase applicant trust even for negative reviews.

- **Legal Framework:** Section 230 (Communications Decency Act) protects Glassdoor from liability for user-generated reviews. Companies cannot sue Glassdoor for hosting negative reviews.

- **Data Licensing:** Glassdoor licenses aggregated (anonymized) salary and review data to HR analytics firms, compensation benchmarking services, and academic researchers.