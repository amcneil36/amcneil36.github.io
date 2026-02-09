# 💼 System Design: Indeed (Job Search Platform)


## 📋 Functional Requirements


  - Job seekers can search for jobs by title, company, location, salary range, and filters

  - Employers can post job listings and manage applicants

  - Job seekers can apply to jobs (upload resume, answer screening questions)

  - Job seekers receive personalized job recommendations and email alerts


## 🔒 Non-Functional Requirements


  - **Scale:** 300M+ monthly visitors, 20M+ job listings, 3B+ searches/month

  - **Freshness:** New jobs indexed within minutes of posting

  - **Low latency:** Search results <200ms

  - **Availability:** 99.9% uptime


## 🔄 Flow 1 — Job Search


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="150" width="110" height="55" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="182" text-anchor="middle" fill="white" font-size="13">
Job Seeker
</text>

  
<line x1="130" y1="177" x2="195" y2="177" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="205" y="150" width="130" height="55" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="270" y="175" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>

  
<text x="270" y="192" text-anchor="middle" fill="white" font-size="10">
(CDN + Auth)
</text>

  
<line x1="335" y1="177" x2="400" y2="177" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="410" y="150" width="130" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="475" y="175" text-anchor="middle" fill="white" font-size="12">
Search Service
</text>

  
<text x="475" y="192" text-anchor="middle" fill="white" font-size="10">
(Query + Rank)
</text>

  
<line x1="540" y1="165" x2="610" y2="115" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="540" y1="190" x2="610" y2="240" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="620" y="90" width="140" height="50" rx="10" fill="#8a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="690" y="120" text-anchor="middle" fill="white" font-size="12">
Elasticsearch (Jobs)
</text>

  
<rect x="620" y="220" width="140" height="50" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="690" y="250" text-anchor="middle" fill="white" font-size="12">
Redis Cache
</text>

  
<line x1="760" y1="115" x2="830" y2="170" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="840" y="150" width="140" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="910" y="175" text-anchor="middle" fill="white" font-size="12">
ML Ranking
</text>

  
<text x="910" y="192" text-anchor="middle" fill="white" font-size="10">
(Personalization)
</text>


</svg>

</details>


### Step-by-Step


  1. **Search request:** `GET /api/v1/jobs?q=software+engineer&location=New+York&radius=25mi&salary_min=100000&job_type=fulltime`

  2. **Query parsing:** Extract title, geocode location, parse filters. Synonym expansion: "software engineer" → also "software developer", "SDE"

  3. **Cache check:** Popular queries cached in Redis (TTL 5 min)

  4. **Elasticsearch:** `bool` query with `multi_match` on title/description, `geo_distance` filter, `range` on salary, `function_score` boost for freshness and sponsored jobs

  5. **ML Ranking:** Re-ranks top-200 candidates using user profile match (skills, experience), predicted apply rate, employer quality. Two-tower model: user tower + job tower

  6. **Return top 15** per page with title, company, location, salary, posted date


> **
**Example:** User searches "software engineer" in NYC → 8,500 matches → ML ranking boosts backend roles (user has Python/AWS on resume) → Top: "Senior Backend Engineer at Stripe — $180-220K — Remote — 2 hours ago" → Indeed earns CPC from sponsored listings


### 🔍 Component Deep Dives — Search


Elasticsearch Job Index

{
  "title": { "type": "text", "analyzer": "job_title_analyzer" },
  "description": { "type": "text" },
  "company_name": { "type": "text", "fields": { "keyword": { "type": "keyword" } } },
  "location": { "type": "geo_point" },
  "salary_min": { "type": "integer" },
  "salary_max": { "type": "integer" },
  "job_type": { "type": "keyword" },
  "is_remote": { "type": "boolean" },
  "is_sponsored": { "type": "boolean" },
  "posted_at": { "type": "date" },
  "skills": { "type": "keyword" },
  "employer_quality_score": { "type": "float" }
}


Job Crawling (Aggregation)


Indeed aggregates jobs from employer websites, other job boards, and ATS (Applicant Tracking Systems). Web crawlers discover job pages → NLP extracts structured data (title, salary, location) → deduplication (same job posted on multiple boards) → indexed in Elasticsearch. This is Indeed's core differentiation — the "Google of jobs."


## 🔄 Flow 2 — Job Application


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 300" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="120" width="110" height="55" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="152" text-anchor="middle" fill="white" font-size="13">
Applicant
</text>

  
<line x1="130" y1="147" x2="200" y2="147" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="210" y="120" width="130" height="55" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="275" y="145" text-anchor="middle" fill="white" font-size="12">
Apply Service
</text>

  
<text x="275" y="162" text-anchor="middle" fill="white" font-size="10">
(Validation)
</text>

  
<line x1="340" y1="135" x2="410" y2="95" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="340" y1="155" x2="410" y2="200" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="420" y="70" width="140" height="50" rx="10" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="490" y="100" text-anchor="middle" fill="white" font-size="12">
PostgreSQL + S3
</text>

  
<rect x="420" y="180" width="140" height="50" rx="10" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="490" y="210" text-anchor="middle" fill="white" font-size="12">
Kafka (Events)
</text>

  
<line x1="560" y1="205" x2="640" y2="170" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="560" y1="205" x2="640" y2="235" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="650" y="145" width="140" height="45" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="720" y="172" text-anchor="middle" fill="white" font-size="11">
Resume Parser (NLP)
</text>

  
<rect x="650" y="210" width="140" height="45" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="720" y="237" text-anchor="middle" fill="white" font-size="11">
Employer Notification
</text>


</svg>

</details>


### Step-by-Step


  1. **Apply:** `POST /api/v1/jobs/{id}/apply` with resume (PDF), screening answers, contact info

  2. **Validate:** Auth check, duplicate check (user hasn't applied before), file size/type

  3. **Store:** Resume in S3 (encrypted), application record in PostgreSQL

  4. **Kafka event → consumers:** Resume Parser extracts skills/experience via NLP → enriches user profile. Employer gets notification email with match score


> **
**Example:** User applies to Stripe job → Resume uploaded to S3 → Parser extracts: Python(7yr), AWS(5yr), Stanford CS → Profile enriched → Recruiter gets email: "New applicant — 92% match" → User sees "Applied ✓"


## 🔄 Flow 3 — Job Alerts & Recommendations


### Step-by-Step


  1. **New job posted:** CDC from PostgreSQL → Kafka → Elasticsearch indexer + Alert Matcher

  2. **Alert Matcher:** Inverted index of saved search criteria → new job matched against user alerts → queued for email

  3. **Recommendation:** Collaborative filtering ("similar users applied to X") + content-based (job embeddings ↔ resume embeddings)

  4. **Daily digest:** 8 AM local time email with matched alerts + personalized recommendations


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 500" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ahc" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="50" width="110" height="40" rx="8" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="75" text-anchor="middle" fill="white" font-size="11">
Job Seekers
</text>

  
<rect x="20" y="100" width="110" height="40" rx="8" fill="#8a4a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="75" y="125" text-anchor="middle" fill="white" font-size="11">
Employers
</text>

  
<line x1="130" y1="70" x2="200" y2="100" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="130" y1="120" x2="200" y2="105" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="210" y="80" width="120" height="50" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="270" y="110" text-anchor="middle" fill="white" font-size="11">
API Gateway
</text>

  
<line x1="330" y1="95" x2="400" y2="55" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="330" y1="105" x2="400" y2="105" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="330" y1="115" x2="400" y2="155" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="330" y1="120" x2="400" y2="205" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="410" y="30" width="130" height="45" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="475" y="57" text-anchor="middle" fill="white" font-size="10">
Search Service
</text>

  
<rect x="410" y="85" width="130" height="45" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="475" y="112" text-anchor="middle" fill="white" font-size="10">
Job Posting Service
</text>

  
<rect x="410" y="140" width="130" height="45" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="475" y="167" text-anchor="middle" fill="white" font-size="10">
Apply Service
</text>

  
<rect x="410" y="195" width="130" height="45" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="475" y="222" text-anchor="middle" fill="white" font-size="10">
Alert + Recs
</text>

  
<line x1="540" y1="53" x2="620" y2="53" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="540" y1="108" x2="620" y2="108" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="540" y1="163" x2="620" y2="163" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="540" y1="218" x2="620" y2="218" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="630" y="30" width="140" height="45" rx="8" fill="#8a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="700" y="57" text-anchor="middle" fill="white" font-size="10">
Elasticsearch
</text>

  
<rect x="630" y="85" width="140" height="45" rx="8" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="700" y="112" text-anchor="middle" fill="white" font-size="10">
PostgreSQL
</text>

  
<rect x="630" y="140" width="140" height="45" rx="8" fill="#4a4a4a" stroke="#999" stroke-width="2">
</rect>

  
<text x="700" y="167" text-anchor="middle" fill="white" font-size="10">
S3 (Resumes)
</text>

  
<rect x="630" y="195" width="140" height="45" rx="8" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="700" y="222" text-anchor="middle" fill="white" font-size="10">
Redis + Kafka
</text>


</svg>

</details>


> **
**End-to-End:** Employer posts job → PostgreSQL → CDC → Kafka → Elasticsearch index + Alert Matcher → Job seeker searches → ES + ML ranking → Personalized results → User applies → Resume to S3 → NLP parser enriches profile → Employer notified → Indeed earns CPC/CPA


## 🗄️ Database Schema


### PostgreSQL

jobs


| Column           | Type          | Notes                                   |
|------------------|---------------|-----------------------------------------|
| id               | BIGINT        | `PK`                                    |
| employer_id      | BIGINT        | `FK` `IDX`                              |
| title            | VARCHAR(255)  | `IDX`                                   |
| description      | TEXT          |                                         |
| company_name     | VARCHAR(255)  |                                         |
| location_city    | VARCHAR(100)  |                                         |
| latitude         | DECIMAL(10,7) |                                         |
| longitude        | DECIMAL(10,7) |                                         |
| salary_min       | INTEGER       |                                         |
| salary_max       | INTEGER       |                                         |
| job_type         | VARCHAR(20)   | fulltime/parttime/contract/intern       |
| experience_level | VARCHAR(20)   |                                         |
| is_remote        | BOOLEAN       |                                         |
| is_sponsored     | BOOLEAN       |                                         |
| source_url       | TEXT          | Original posting URL (for crawled jobs) |
| status           | VARCHAR(20)   | active / expired / filled               |
| posted_at        | TIMESTAMPTZ   | `IDX`                                   |
| expires_at       | TIMESTAMPTZ   |                                         |


applications


| Column            | Type        | Notes                                                 |
|-------------------|-------------|-------------------------------------------------------|
| id                | BIGINT      | `PK`                                                  |
| job_id            | BIGINT      | `FK` `IDX`                                            |
| user_id           | BIGINT      | `FK` `IDX`                                            |
| resume_url        | TEXT        | S3 presigned URL                                      |
| cover_letter      | TEXT        |                                                       |
| screening_answers | JSONB       |                                                       |
| status            | VARCHAR(20) | submitted / reviewed / interviewed / rejected / hired |
| applied_at        | TIMESTAMPTZ |                                                       |


 `IDX` on (job_id, user_id) — one application per user per job


user_profiles


| Column             | Type         | Notes                    |
|--------------------|--------------|--------------------------|
| user_id            | BIGINT       | `PK` `FK`                |
| headline           | VARCHAR(255) |                          |
| skills             | TEXT[]       | `IDX`                    |
| experience_years   | INTEGER      |                          |
| education          | JSONB        |                          |
| preferred_location | POINT        |                          |
| salary_expectation | INTEGER      |                          |
| resume_text        | TEXT         | Parsed from uploaded PDF |


### Sharding


**jobs:** Shard by `job_id` hash. Time-based partitioning for expired jobs (archive partition)  `SHARD`


**applications:** Shard by `job_id` — employer views all applicants for a job on same shard  `SHARD`


## 💾 Cache & CDN Deep Dive


| Cache          | Key                   | TTL    | Strategy                         |
|----------------|-----------------------|--------|----------------------------------|
| Search results | `search:{query_hash}` | 5 min  | Read-through                     |
| Job detail     | `job:{id}`            | 15 min | Read-through, invalidate on edit |
| Company page   | `company:{id}`        | 1 hr   | Read-through                     |
| User session   | `session:{token}`     | 24 hr  | Write-through                    |


**CDN:** Job listing pages are heavily SEO-driven — server-side rendered HTML cached on CDN for search engine crawlers. Static assets (CSS, JS, company logos) on CDN with long TTL. API responses NOT cached on CDN (personalized).


## ⚖️ Tradeoffs


> **
  
> **
    ✅ Job Crawling (Aggregation)
    

Largest job inventory — crawls employer sites, other boards, ATS systems. Users get comprehensive results in one place

  
  
> **
    ❌ Job Crawling
    

Duplicate listings across sources, stale/expired jobs from crawled sites, legal/IP issues with scraping, NLP extraction errors (wrong salary parsing)

  
  
> **
    ✅ CPC Monetization
    

Pay-per-click model aligns incentives — employers only pay when candidates engage

  
  
> **
    ❌ CPC Monetization
    

Sponsored listings may not be most relevant — tension between relevance and revenue. Click fraud possible

  


## 🔄 Alternative Approaches


AI-First Matching (LinkedIn Approach)


Instead of keyword search, use deep learning to match candidate profiles to job requirements. Enables "Jobs for You" without explicit search. Better for passive job seekers but requires rich profile data (LinkedIn has it, Indeed has less).


Skills-Based Matching (vs Title-Based)


Standardize skills taxonomy across all jobs and resumes. Match on skills rather than job titles (which vary wildly across companies). Challenge: building and maintaining a comprehensive skills ontology.


## 📚 Additional Information


### SEO as Growth Engine


Indeed ranks #1 on Google for millions of "[job title] jobs in [city]" queries. Each job listing is a unique URL, server-side rendered with structured data (schema.org/JobPosting). This organic traffic is Indeed's primary acquisition channel — billions of monthly pageviews from Google.


### Resume Parsing NLP Pipeline


Uploaded resumes (PDF/DOCX) → text extraction (Apache Tika) → NER (Named Entity Recognition) for skills, companies, dates, education → structured profile. Models trained on millions of manually labeled resumes. Accuracy: ~90% for skill extraction, ~95% for education. Used for personalized search ranking and employer matching.


### Deduplication of Crawled Jobs


Same job appears on company website, LinkedIn, Glassdoor, and 5 other boards. Dedup algorithm: (1) exact URL match, (2) fuzzy match on (title + company + location) with SimHash, (3) description similarity (cosine similarity on TF-IDF vectors, threshold 0.9). Only unique listings shown to prevent redundant results.