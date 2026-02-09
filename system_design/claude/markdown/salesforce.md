# ☁️ Salesforce — System Design


Multi-Tenant CRM Platform, Metadata-Driven Architecture, Force.com & Lightning Platform


## Functional Requirements


  1. **CRM Core** — Manage accounts, contacts, opportunities, leads, cases with full CRUD, workflow automation, and relationship tracking

  2. **Multi-Tenant Customization** — Per-tenant custom objects, fields, page layouts, validation rules, and business logic without code deployment

  3. **Declarative Automation** — Flow Builder, Process Builder, Workflow Rules, Approval Processes for no-code business automation

  4. **Programmatic Platform** — Apex (server-side), Lightning Web Components (client-side), Visualforce, SOQL/SOSL query languages

  5. **APIs & Integration** — REST API, SOAP API, Bulk API, Streaming API, Pub/Sub API for external system integration

  6. **Analytics & Reporting** — Report Builder, dashboards, Einstein Analytics (Tableau CRM), predictive scoring

  7. **AppExchange Ecosystem** — Marketplace for managed/unmanaged packages; ISV platform with packaging and licensing


## Non-Functional Requirements


  - **Multi-Tenancy** — 100K+ orgs share same infrastructure; strict tenant isolation with zero data leakage

  - **Availability** — 99.99%+ uptime (trust.salesforce.com SLA); active-active data centers per instance

  - **Scalability** — Handle orgs from 5 users to 100K+ users; billions of records across tenants

  - **Governor Limits** — Per-transaction resource limits (CPU, SOQL queries, DML rows) to prevent tenant "noisy neighbor" problems

  - **Security** — Field-Level Security (FLS), Record-Level Security (sharing rules, OWD), Shield Platform Encryption (AES-256), HIPAA/SOC2/FedRAMP compliance

  - **Performance** — <300ms API response time (p95), real-time streaming for change events

  - **Global** — 60+ instances across NA, EU, APAC; data residency compliance (Hyperforce)


## Flow 1: Multi-Tenant Data Architecture & Metadata Engine


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 430" xmlns="http://www.w3.org/2000/svg">

  
<defs>

    
<marker id="ah1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">

      
<polygon points="0 0, 10 3.5, 0 7" fill="#1b96ff">
</polygon>

    
</marker>

  
</defs>

  

  
<rect x="10" y="40" width="120" height="50" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="70" text-anchor="middle" fill="white" font-size="11">
Org A (50 users)
</text>

  
<rect x="10" y="110" width="120" height="50" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="140" text-anchor="middle" fill="white" font-size="11">
Org B (5K users)
</text>

  
<rect x="10" y="180" width="120" height="50" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="210" text-anchor="middle" fill="white" font-size="11">
Org C (50K users)
</text>


  

  
<rect x="190" y="90" width="150" height="70" rx="8" fill="#1565c0">
</rect>

  
<text x="265" y="118" text-anchor="middle" fill="white" font-size="12">
App Server Pod
</text>

  
<text x="265" y="138" text-anchor="middle" fill="white" font-size="10">
Metadata Runtime Engine
</text>


  

  
<rect x="190" y="200" width="150" height="55" rx="8" fill="#006064">
</rect>

  
<text x="265" y="225" text-anchor="middle" fill="white" font-size="11">
Metadata Cache
</text>

  
<text x="265" y="242" text-anchor="middle" fill="white" font-size="10">
(In-Memory per Pod)
</text>


  

  
<rect x="410" y="30" width="160" height="60" rx="8" fill="#7b1fa2">
</rect>

  
<text x="490" y="55" text-anchor="middle" fill="white" font-size="11">
Metadata Tables
</text>

  
<text x="490" y="72" text-anchor="middle" fill="white" font-size="10">
(CustomObject, CustomField)
</text>


  

  
<rect x="410" y="120" width="160" height="60" rx="8" fill="#c62828">
</rect>

  
<text x="490" y="145" text-anchor="middle" fill="white" font-size="11">
Data Tables
</text>

  
<text x="490" y="162" text-anchor="middle" fill="white" font-size="10">
(MT_Data, MT_Data_Extra)
</text>


  

  
<rect x="410" y="210" width="160" height="60" rx="8" fill="#e65100">
</rect>

  
<text x="490" y="235" text-anchor="middle" fill="white" font-size="11">
Pivot Tables
</text>

  
<text x="490" y="252" text-anchor="middle" fill="white" font-size="10">
(MT_Indexes, Relationships)
</text>


  

  
<rect x="650" y="100" width="140" height="65" rx="10" fill="#5d4037">
</rect>

  
<text x="720" y="127" text-anchor="middle" fill="white" font-size="12">
Oracle RAC
</text>

  
<text x="720" y="147" text-anchor="middle" fill="white" font-size="10">
(Shared DB Instance)
</text>


  

  
<rect x="650" y="210" width="140" height="55" rx="8" fill="#006064">
</rect>

  
<text x="720" y="232" text-anchor="middle" fill="white" font-size="11">
Search Index
</text>

  
<text x="720" y="250" text-anchor="middle" fill="white" font-size="10">
(Solr / Custom)
</text>


  

  
<rect x="650" y="300" width="140" height="55" rx="8" fill="#37474f">
</rect>

  
<text x="720" y="322" text-anchor="middle" fill="white" font-size="11">
File Storage
</text>

  
<text x="720" y="340" text-anchor="middle" fill="white" font-size="10">
(Content Delivery)
</text>


  

  
<rect x="860" y="120" width="150" height="55" rx="8" fill="#6a2a6a">
</rect>

  
<text x="935" y="142" text-anchor="middle" fill="white" font-size="11">
OrgID Partition
</text>

  
<text x="935" y="158" text-anchor="middle" fill="white" font-size="10">
(Tenant Isolation Key)
</text>


  

  
<line x1="130" y1="65" x2="188" y2="110" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="130" y1="135" x2="188" y2="125" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="130" y1="205" x2="188" y2="145" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="340" y1="110" x2="408" y2="60" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="340" y1="125" x2="408" y2="150" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="340" y1="140" x2="408" y2="240" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="570" y1="60" x2="648" y2="120" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="570" y1="150" x2="648" y2="132" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="570" y1="240" x2="648" y2="237" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="790" y1="132" x2="858" y2="140" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah1)">
</line>

  
<line x1="265" y1="160" x2="265" y2="198" stroke="#1b96ff" stroke-width="1.5" stroke-dasharray="5,3" marker-end="url(#ah1)">
</line>


</svg>

</details>


### Multi-Tenant Data Steps


  1. **Shared Database, Shared Schema** — All tenants (orgs) share the same Oracle RAC database. Tenant isolation is enforced by `OrgID` column present on every data row. Every SQL query includes `WHERE OrgID = :orgId` — guaranteed by the platform, never by developer code.

  2. **Metadata-Driven Architecture** — Custom objects and fields are NOT physical database columns. Instead, metadata tables store the "virtual" schema: `CustomObject` (object definitions), `CustomField` (field definitions with data type, length, required, unique constraints). The runtime engine translates SOQL to SQL against generic data tables.

  3. **Universal Data Dictionary (UDD)** — Physical data table has generic columns: `Value0` through `Value500` (varchar), with the metadata engine mapping virtual field → physical column slot. Custom fields like `Revenue__c` might map to `Value47` for Org A but `Value12` for Org B.

  4. **Pivot Tables for Indexes** — Since data is in generic varchar columns, standard B-tree indexes can't efficiently serve custom field queries. Pivot tables (MT_Indexes) denormalize: `(OrgID, KeyPrefix, FieldNum, StringValue, DataID)`. Custom indexes create entries in pivot tables for WHERE clause optimization.

  5. **Metadata Cache** — Each app server pod caches compiled metadata (object definitions, field maps, validation rules, triggers) in-memory. Cache is invalidated on metadata changes via a global invalidation bus. Cold start: ~2-5 seconds to load org metadata. Warm: sub-ms lookups.


> **
  **Example — Custom object query:**  

  Admin creates custom object `Invoice__c` with fields: `Amount__c` (Currency), `Due_Date__c` (Date), `Account__c` (Lookup).  

  Developer writes SOQL: `SELECT Id, Amount__c, Due_Date__c FROM Invoice__c WHERE Amount__c > 10000 AND Account__c = '001xxx'`  

  Runtime engine translates to SQL: `SELECT DataID, Value12, Value13 FROM MT_Data d JOIN MT_Indexes i ON d.DataID = i.DataID WHERE d.OrgID = 'ORG123' AND d.KeyPrefix = 'a0B' AND i.FieldNum = 12 AND CAST(i.StringValue AS NUMBER) > 10000 AND d.Value14 = '001xxx'`


> **
  **Deep Dive — Polymorphic Data Storage:**  

  The `MT_Data` table stores all custom object data across all tenants. Physical schema:  

  • `OrgID`  `SHARD` — 15-char tenant identifier  

  • `KeyPrefix` — 3-char object type identifier (e.g., "001" for Account, "a0B" for custom object)  

  • `DataID`  `PK` — 18-char globally unique record ID  

  • `Value0..Value500` — Generic VARCHAR2(4000) columns for custom field data  

  • `Name` — Record name (auto-number or text)  

  • `CreatedDate`, `LastModifiedDate`, `CreatedById`, `LastModifiedById` — Audit fields  

  • `IsDeleted` — Soft delete flag (Recycle Bin support, 15-day retention)  

  All data types (Number, Date, Boolean, Picklist) are stored as VARCHAR and cast at query time by the metadata engine. This is the fundamental tradeoff: flexibility over raw query performance.


> **
  **Deep Dive — Governor Limits (Noisy Neighbor Prevention):**  

  Per-transaction limits in Apex execution context:  

  • **SOQL Queries**: 100 (synchronous), 200 (asynchronous)  

  • **SOQL Rows Retrieved**: 50,000  

  • **DML Statements**: 150  

  • **DML Rows**: 10,000  

  • **CPU Time**: 10,000ms (sync), 60,000ms (async)  

  • **Heap Size**: 6MB (sync), 12MB (async)  

  • **Callouts**: 100 per transaction, 120s total timeout  

  • **Future Calls**: 50 per transaction  

  These limits ensure no single tenant's code execution can monopolize shared app server resources. Violations throw `System.LimitException`.


## Flow 2: API Layer & Integration Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>

    
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">

      
<polygon points="0 0, 10 3.5, 0 7" fill="#1b96ff">
</polygon>

    
</marker>

  
</defs>

  

  
<rect x="10" y="130" width="120" height="55" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="153" text-anchor="middle" fill="white" font-size="11">
External Client
</text>

  
<text x="70" y="170" text-anchor="middle" fill="white" font-size="10">
(REST/SOAP)
</text>


  

  
<rect x="180" y="60" width="140" height="55" rx="8" fill="#f58220">
</rect>

  
<text x="250" y="83" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>

  
<text x="250" y="100" text-anchor="middle" fill="white" font-size="10">
(OAuth2 + Rate Limit)
</text>


  

  
<rect x="180" y="140" width="140" height="45" rx="8" fill="#1565c0">
</rect>

  
<text x="250" y="167" text-anchor="middle" fill="white" font-size="11">
REST API (v59.0)
</text>

  
<rect x="180" y="200" width="140" height="45" rx="8" fill="#1565c0">
</rect>

  
<text x="250" y="227" text-anchor="middle" fill="white" font-size="11">
Bulk API 2.0
</text>

  
<rect x="180" y="260" width="140" height="45" rx="8" fill="#1565c0">
</rect>

  
<text x="250" y="287" text-anchor="middle" fill="white" font-size="11">
Streaming / Pub/Sub
</text>


  

  
<rect x="390" y="40" width="150" height="55" rx="8" fill="#7b1fa2">
</rect>

  
<text x="465" y="63" text-anchor="middle" fill="white" font-size="11">
OAuth 2.0 Provider
</text>

  
<text x="465" y="80" text-anchor="middle" fill="white" font-size="10">
(JWT Bearer, Web Flow)
</text>


  

  
<rect x="390" y="130" width="150" height="65" rx="8" fill="#c62828">
</rect>

  
<text x="465" y="155" text-anchor="middle" fill="white" font-size="12">
Platform Core
</text>

  
<text x="465" y="172" text-anchor="middle" fill="white" font-size="10">
(CRUD + Security + Triggers)
</text>


  

  
<rect x="390" y="230" width="150" height="55" rx="8" fill="#006064">
</rect>

  
<text x="465" y="253" text-anchor="middle" fill="white" font-size="11">
Platform Event Bus
</text>

  
<text x="465" y="270" text-anchor="middle" fill="white" font-size="10">
(Pub/Sub, CDC)
</text>


  

  
<rect x="610" y="60" width="140" height="55" rx="8" fill="#00695c">
</rect>

  
<text x="680" y="83" text-anchor="middle" fill="white" font-size="11">
MuleSoft Anypoint
</text>

  
<text x="680" y="100" text-anchor="middle" fill="white" font-size="10">
(Integration Hub)
</text>


  

  
<rect x="810" y="40" width="130" height="45" rx="8" fill="#37474f">
</rect>

  
<text x="875" y="67" text-anchor="middle" fill="white" font-size="11">
ERP (SAP/Oracle)
</text>

  
<rect x="810" y="100" width="130" height="45" rx="8" fill="#37474f">
</rect>

  
<text x="875" y="127" text-anchor="middle" fill="white" font-size="11">
Marketing Cloud
</text>

  
<rect x="810" y="160" width="130" height="45" rx="8" fill="#37474f">
</rect>

  
<text x="875" y="187" text-anchor="middle" fill="white" font-size="11">
Data Warehouse
</text>


  

  
<line x1="130" y1="155" x2="178" y2="88" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="130" y1="157" x2="178" y2="162" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="130" y1="165" x2="178" y2="222" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="130" y1="170" x2="178" y2="282" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="320" y1="87" x2="388" y2="67" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="320" y1="162" x2="388" y2="162" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="320" y1="282" x2="388" y2="257" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="540" y1="87" x2="608" y2="87" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="750" y1="80" x2="808" y2="62" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="750" y1="87" x2="808" y2="122" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah2)">
</line>

  
<line x1="750" y1="95" x2="808" y2="182" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah2)">
</line>


</svg>

</details>


### API & Integration Steps


  1. **Authentication** — OAuth 2.0 flows: Web Server Flow (authorization code + PKCE for web apps), JWT Bearer Flow (server-to-server, signed with X.509 cert), Username-Password Flow (legacy), Device Flow. Token endpoint: `POST /services/oauth2/token`. Access token: 2-hour TTL, refresh token: revocable.

  2. **REST API** — Resource-based: `GET /services/data/v59.0/sobjects/Account/001xxx` (read record), `PATCH` (update), `POST` (create), `DELETE` (soft delete). Composite API: `/services/data/v59.0/composite` for batching up to 25 subrequests in one call. SObject Tree API for hierarchical inserts.

  3. **Bulk API 2.0** — For large data loads (millions of records). Create job → upload CSV data → server processes in background → poll for status → download results. Uses chunked upload. Server processes 10K records/batch. Parallel processing across app server pods.

  4. **Streaming API & Platform Events** — CometD long-polling for PushTopics (SOQL-based change notifications) and Change Data Capture (CDC) events. Platform Events: custom pub/sub. gRPC-based Pub/Sub API (new): subscribe to event channels with replay support. Event Bus uses Apache Kafka internally.

  5. **MuleSoft Integration** — Anypoint Platform provides ESB (Enterprise Service Bus), API management, DataWeave transformation language, 400+ pre-built connectors (SAP, Workday, AWS, Slack). Runs on CloudHub (managed runtime) or on-premise Mule Runtime.


> **
  **Example — Bulk data sync from ERP:**  

  1. External ETL authenticates: `POST /services/oauth2/token` with JWT Bearer → receives access_token  

  2. Create Bulk Job: `POST /services/data/v59.0/jobs/ingest` body: `{"object": "Account", "operation": "upsert", "externalIdFieldName": "ERP_ID__c"}`  

  3. Upload CSV: `PUT /services/data/v59.0/jobs/ingest/{jobId}/batches` with 500K Account rows  

  4. Close job: `PATCH /services/data/v59.0/jobs/ingest/{jobId}` body: `{"state": "UploadComplete"}`  

  5. Platform processes in parallel chunks → triggers fire on upserted records → CDC events published for downstream consumers  

  6. Poll results: `GET /services/data/v59.0/jobs/ingest/{jobId}` → `{"state": "JobComplete", "numberRecordsProcessed": 500000, "numberRecordsFailed": 23}`


> **
  **Deep Dive — Platform Events (Kafka-Backed):**  

  Platform Events are custom event definitions (like custom objects but for events). Schema defined declaratively: `Order_Event__e` with fields `Order_Id__c`, `Status__c`, `Amount__c`. Published via Apex: `EventBus.publish(new Order_Event__e(...))` or REST API: `POST /services/data/v59.0/sobjects/Order_Event__e`. Subscribers: Apex triggers, Flows, CometD clients, gRPC Pub/Sub clients. Internally backed by Apache Kafka with 72-hour retention. `ReplayId` allows consumers to replay from any point. Delivery guarantee: at-least-once. Rate limits: 250K events/hour (standard), 25M/hour (high-volume).


> **
  **Deep Dive — API Rate Limiting:**  

  Salesforce enforces per-org API call limits based on edition and licenses:  

  • Enterprise Edition: 1,000 calls per user license per 24 hours (min 15,000 per org)  

  • Unlimited Edition: 5,000 calls per user license per 24 hours  

  • Bulk API: separate limit — 15,000 batches per 24 hours  

  • Streaming API: 2,000 concurrent CometD clients  

  Response headers include: `Sforce-Limit-Info: api-usage=1234/100000`. When approaching limits: use Composite API (1 call = 25 operations), Bulk API for large operations, caching on client side. HTTP 403 with `REQUEST_LIMIT_EXCEEDED` when exceeded.


## Flow 3: Automation & Trigger Execution Engine


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 340" xmlns="http://www.w3.org/2000/svg">

  
<defs>

    
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">

      
<polygon points="0 0, 10 3.5, 0 7" fill="#1b96ff">
</polygon>

    
</marker>

  
</defs>

  

  
<rect x="10" y="130" width="120" height="55" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="153" text-anchor="middle" fill="white" font-size="12">
DML Operation
</text>

  
<text x="70" y="170" text-anchor="middle" fill="white" font-size="10">
(Insert/Update)
</text>


  

  
<rect x="170" y="50" width="130" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="235" y="72" text-anchor="middle" fill="white" font-size="11">
Before Triggers
</text>

  
<text x="235" y="88" text-anchor="middle" fill="white" font-size="10">
(Apex, Validation)
</text>


  

  
<rect x="170" y="120" width="130" height="50" rx="8" fill="#7b1fa2">
</rect>

  
<text x="235" y="142" text-anchor="middle" fill="white" font-size="11">
Validation Rules
</text>

  
<text x="235" y="158" text-anchor="middle" fill="white" font-size="10">
(Formula-based)
</text>


  

  
<rect x="170" y="190" width="130" height="50" rx="8" fill="#7b1fa2">
</rect>

  
<text x="235" y="212" text-anchor="middle" fill="white" font-size="11">
Duplicate Rules
</text>

  
<text x="235" y="228" text-anchor="middle" fill="white" font-size="10">
(Matching Rules)
</text>


  

  
<rect x="350" y="130" width="130" height="55" rx="8" fill="#c62828">
</rect>

  
<text x="415" y="153" text-anchor="middle" fill="white" font-size="12">
Database Save
</text>

  
<text x="415" y="170" text-anchor="middle" fill="white" font-size="10">
(Commit Point)
</text>


  

  
<rect x="530" y="50" width="130" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="595" y="72" text-anchor="middle" fill="white" font-size="11">
After Triggers
</text>

  
<text x="595" y="88" text-anchor="middle" fill="white" font-size="10">
(Apex Logic)
</text>


  

  
<rect x="530" y="120" width="130" height="50" rx="8" fill="#e65100">
</rect>

  
<text x="595" y="142" text-anchor="middle" fill="white" font-size="11">
Record-Triggered
</text>

  
<text x="595" y="158" text-anchor="middle" fill="white" font-size="10">
Flows
</text>


  

  
<rect x="530" y="190" width="130" height="50" rx="8" fill="#e65100">
</rect>

  
<text x="595" y="212" text-anchor="middle" fill="white" font-size="11">
Rollup Summary
</text>

  
<text x="595" y="228" text-anchor="middle" fill="white" font-size="10">
Field Updates
</text>


  

  
<rect x="710" y="50" width="140" height="50" rx="8" fill="#006064">
</rect>

  
<text x="780" y="72" text-anchor="middle" fill="white" font-size="11">
@future / Queueable
</text>

  
<text x="780" y="88" text-anchor="middle" fill="white" font-size="10">
(Async Apex)
</text>


  

  
<rect x="710" y="120" width="140" height="50" rx="8" fill="#006064">
</rect>

  
<text x="780" y="142" text-anchor="middle" fill="white" font-size="11">
Platform Events
</text>

  
<text x="780" y="158" text-anchor="middle" fill="white" font-size="10">
(Publish CDC)
</text>


  

  
<rect x="710" y="190" width="140" height="50" rx="8" fill="#37474f">
</rect>

  
<text x="780" y="212" text-anchor="middle" fill="white" font-size="11">
Outbound Message
</text>

  
<text x="780" y="228" text-anchor="middle" fill="white" font-size="10">
(SOAP Callback)
</text>


  

  
<line x1="130" y1="145" x2="168" y2="80" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="130" y1="155" x2="168" y2="145" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="130" y1="165" x2="168" y2="215" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="300" y1="80" x2="348" y2="150" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="300" y1="145" x2="348" y2="155" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="300" y1="215" x2="348" y2="165" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="480" y1="150" x2="528" y2="75" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="480" y1="157" x2="528" y2="145" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="480" y1="165" x2="528" y2="215" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="660" y1="75" x2="708" y2="75" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="660" y1="145" x2="708" y2="145" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah3)">
</line>

  
<line x1="660" y1="215" x2="708" y2="215" stroke="#1b96ff" stroke-width="2" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Trigger Execution Order


  1. **System Validation** — Required fields, field formats, field length, unique constraints

  2. **Before Triggers** — Apex triggers fire. Can modify field values on `Trigger.new` (no DML needed). Used for: default values, field normalization, cross-record validation.

  3. **Custom Validation Rules** — Formula-based rules evaluated. If any rule fails → `FIELD_CUSTOM_VALIDATION_EXCEPTION` → entire transaction rolls back.

  4. **Duplicate Rules** — Matching rules check for duplicates (fuzzy match on Name, Email, etc). Block or alert based on rule configuration.

  5. **Database Save** — Record committed to database (but transaction not yet committed to user).

  6. **After Triggers** — Apex triggers fire with record IDs now available. Used for: creating related records, sending notifications, complex business logic. Can cause re-entry (cascading triggers up to 16 levels).

  7. **Record-Triggered Flows** — Declarative automation runs. Before-save flows (fast field updates), after-save flows (create records, send emails, call subflows).

  8. **Assignment Rules, Auto-Response, Escalation Rules** — Lead/Case routing automation.

  9. **Rollup Summary Fields** — Parent record aggregate fields recalculated (SUM, COUNT, MIN, MAX of child records).

  10. **Post-Commit** — Platform Events published, @future methods enqueued, outbound messages sent.


> **
  **Example — Opportunity closed-won automation:**  

  Sales rep updates Opportunity StageName to "Closed Won".  

  1. Before trigger: Apex sets `Close_Date__c = TODAY()` if null  

  2. Validation rule: `AND(ISPICKVAL(StageName, "Closed Won"), ISBLANK(Amount))` → blocks if no Amount  

  3. Save to DB  

  4. After trigger: creates Revenue_Recognition__c record, updates Account.Last_Won_Date__c  

  5. Flow: sends email to Finance team, creates Task for account manager follow-up  

  6. Rollup: Account.Total_Won_Opps__c recalculated  

  7. CDC event published → ERP system receives and creates invoice


> **
  **Deep Dive — Apex Execution Context & Bulkification:**  

  Apex triggers receive up to 200 records per execution (bulk DML). Developers must write "bulkified" code: no SOQL/DML inside loops. Pattern:  

  `trigger AccountTrigger on Account (before update) {`  

  `  Set<Id> accountIds = new Set<Id>();`  

  `  for (Account a : Trigger.new) { accountIds.add(a.Id); }`  

  `  Map<Id, List<Contact>> contactMap = ... // single SOQL for all`  

  `  for (Account a : Trigger.new) { /* process using map */ }`  

  `}`  

  Non-bulkified code hitting governor limits is the #1 cause of production errors on the platform.


## Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 530" xmlns="http://www.w3.org/2000/svg">

  
<defs>

    
<marker id="ah4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">

      
<polygon points="0 0, 10 3.5, 0 7" fill="#1b96ff">
</polygon>

    
</marker>

  
</defs>

  

  
<rect x="10" y="30" width="120" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="57" text-anchor="middle" fill="white" font-size="11">
Browser (LWC)
</text>

  
<rect x="10" y="90" width="120" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="117" text-anchor="middle" fill="white" font-size="11">
Mobile (SF App)
</text>

  
<rect x="10" y="150" width="120" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="177" text-anchor="middle" fill="white" font-size="11">
API Clients
</text>

  
<rect x="10" y="210" width="120" height="45" rx="8" fill="#2e7d32">
</rect>

  
<text x="70" y="237" text-anchor="middle" fill="white" font-size="11">
AppExchange
</text>


  

  
<rect x="180" y="30" width="130" height="50" rx="8" fill="#37474f">
</rect>

  
<text x="245" y="52" text-anchor="middle" fill="white" font-size="11">
Akamai CDN
</text>

  
<text x="245" y="68" text-anchor="middle" fill="white" font-size="10">
(Static Assets)
</text>


  

  
<rect x="180" y="100" width="130" height="50" rx="8" fill="#e65100">
</rect>

  
<text x="245" y="122" text-anchor="middle" fill="white" font-size="11">
Load Balancer
</text>

  
<text x="245" y="138" text-anchor="middle" fill="white" font-size="10">
(L7 + WAF)
</text>


  

  
<rect x="370" y="30" width="150" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="445" y="52" text-anchor="middle" fill="white" font-size="11">
App Server Pod 1
</text>

  
<text x="445" y="68" text-anchor="middle" fill="white" font-size="10">
(Java + Metadata RT)
</text>

  
<rect x="370" y="95" width="150" height="50" rx="8" fill="#1565c0">
</rect>

  
<text x="445" y="117" text-anchor="middle" fill="white" font-size="11">
App Server Pod N
</text>

  
<text x="445" y="133" text-anchor="middle" fill="white" font-size="10">
(Java + Metadata RT)
</text>


  

  
<rect x="370" y="170" width="150" height="45" rx="8" fill="#006064">
</rect>

  
<text x="445" y="197" text-anchor="middle" fill="white" font-size="11">
Metadata Cache Layer
</text>


  

  
<rect x="600" y="30" width="150" height="55" rx="10" fill="#5d4037">
</rect>

  
<text x="675" y="52" text-anchor="middle" fill="white" font-size="11">
Oracle RAC (Primary)
</text>

  
<text x="675" y="70" text-anchor="middle" fill="white" font-size="10">
MT_Data + Metadata
</text>

  
<rect x="600" y="100" width="150" height="45" rx="10" fill="#5d4037">
</rect>

  
<text x="675" y="127" text-anchor="middle" fill="white" font-size="10">
Oracle RAC (Standby)
</text>


  

  
<rect x="600" y="170" width="150" height="45" rx="8" fill="#006064">
</rect>

  
<text x="675" y="197" text-anchor="middle" fill="white" font-size="11">
Search (Solr Cluster)
</text>


  

  
<rect x="370" y="250" width="150" height="50" rx="8" fill="#7b1fa2">
</rect>

  
<text x="445" y="272" text-anchor="middle" fill="white" font-size="11">
Event Bus (Kafka)
</text>

  
<text x="445" y="288" text-anchor="middle" fill="white" font-size="10">
Platform Events + CDC
</text>


  

  
<rect x="600" y="250" width="150" height="50" rx="8" fill="#e65100">
</rect>

  
<text x="675" y="272" text-anchor="middle" fill="white" font-size="11">
Async Engine
</text>

  
<text x="675" y="288" text-anchor="middle" fill="white" font-size="10">
Batch/Queueable/Future
</text>


  

  
<rect x="370" y="330" width="150" height="45" rx="8" fill="#00695c">
</rect>

  
<text x="445" y="357" text-anchor="middle" fill="white" font-size="11">
Salesforce Functions
</text>


  

  
<rect x="600" y="330" width="150" height="45" rx="8" fill="#7b1fa2">
</rect>

  
<text x="675" y="357" text-anchor="middle" fill="white" font-size="11">
Einstein AI Platform
</text>


  

  
<rect x="830" y="30" width="150" height="55" rx="8" fill="#1565c0">
</rect>

  
<text x="905" y="52" text-anchor="middle" fill="white" font-size="11">
Hyperforce (AWS)
</text>

  
<text x="905" y="70" text-anchor="middle" fill="white" font-size="10">
Public Cloud Infra
</text>


  

  
<rect x="830" y="110" width="150" height="55" rx="8" fill="#4a148c">
</rect>

  
<text x="905" y="132" text-anchor="middle" fill="white" font-size="11">
Data Cloud (CDP)
</text>

  
<text x="905" y="150" text-anchor="middle" fill="white" font-size="10">
Unified Customer Data
</text>


  

  
<rect x="830" y="195" width="150" height="50" rx="8" fill="#c62828">
</rect>

  
<text x="905" y="217" text-anchor="middle" fill="white" font-size="11">
Shield Encryption
</text>

  
<text x="905" y="233" text-anchor="middle" fill="white" font-size="10">
AES-256, Event Monitoring
</text>


  

  
<rect x="830" y="270" width="150" height="50" rx="8" fill="#00695c">
</rect>

  
<text x="905" y="292" text-anchor="middle" fill="white" font-size="11">
MuleSoft Anypoint
</text>

  
<text x="905" y="308" text-anchor="middle" fill="white" font-size="10">
Integration Platform
</text>


  

  
<line x1="130" y1="52" x2="178" y2="52" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="130" y1="112" x2="178" y2="120" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="130" y1="172" x2="178" y2="130" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="310" y1="55" x2="368" y2="55" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="310" y1="125" x2="368" y2="120" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="520" y1="55" x2="598" y2="55" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="520" y1="120" x2="598" y2="120" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="520" y1="192" x2="598" y2="192" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="520" y1="275" x2="598" y2="275" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="750" y1="57" x2="828" y2="57" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="750" y1="120" x2="828" y2="137" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="750" y1="192" x2="828" y2="220" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>

  
<line x1="750" y1="275" x2="828" y2="290" stroke="#1b96ff" stroke-width="1.5" marker-end="url(#ah4)">
</line>


</svg>

</details>


> **
  **Example — Full CRM flow (lead-to-cash):**  

  1. Marketing Cloud captures web form → creates Lead in Salesforce via REST API  

  2. Lead Assignment Rule routes to sales rep based on territory (zip code matching)  

  3. Rep qualifies → converts Lead → creates Account + Contact + Opportunity  

  4. Einstein Lead Scoring (logistic regression on 50+ features) predicts conversion probability: 78%  

  5. Opportunity moves through stages → CPQ generates quote → DocuSign for e-signature  

  6. Closed Won → after trigger creates Order, Revenue Schedule → Platform Event published  

  7. MuleSoft integration syncs to NetSuite ERP for invoicing → Data Cloud unifies customer profile  

  8. Dashboard shows pipeline: $2.3M weighted, 42 open opps, 28-day avg sales cycle


## Database Schema


### Core CRM Objects (Standard SObjects)


  
  
    
    
    
  
  
    
    
    
  
  
    
    
    
  
  
    
    
    
  
  
    
    
    
  


| Object      | Key Fields                                                                                                                                                   | Relationships                                                                             |
|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| Account     | `Id`  `PK`, `Name`, `Industry`, `AnnualRevenue`, `BillingCity/State/Country`, `OwnerId`  `FK`, `ParentId`  `FK`, `RecordTypeId`                              | Parent Account (self-ref), Owner (User), Contacts (1:M), Opportunities (1:M), Cases (1:M) |
| Contact     | `Id`  `PK`, `FirstName`, `LastName`, `Email`  `IDX`, `Phone`, `AccountId`  `FK`, `OwnerId`  `FK`                                                             | Account (M:1), Opportunities (M:M via OpportunityContactRole), Cases (1:M)                |
| Opportunity | `Id`  `PK`, `Name`, `Amount`, `StageName`  `IDX`, `CloseDate`  `IDX`, `Probability`, `AccountId`  `FK`, `OwnerId`  `FK`                                      | Account (M:1), Products (M:M via OpportunityLineItem), Contacts (M:M)                     |
| Lead        | `Id`  `PK`, `FirstName`, `LastName`, `Company`, `Email`  `IDX`, `Status`, `LeadSource`, `ConvertedAccountId`, `ConvertedContactId`, `ConvertedOpportunityId` | Converted to Account+Contact+Opportunity on conversion                                    |
| Case        | `Id`  `PK`, `Subject`, `Description`, `Status`, `Priority`, `ContactId`  `FK`, `AccountId`  `FK`, `OwnerId`                                                  | Account (M:1), Contact (M:1), CaseComments (1:M), Solutions (M:M)                         |


### Physical Multi-Tenant Table (Internal)


  
  
  
  
  
  
  
  
  
  


| Column             | Type                 | Description                                                 |
|--------------------|----------------------|-------------------------------------------------------------|
| `OrgID`            | VARCHAR(15)  `SHARD` | Tenant identifier — partition key for all queries           |
| `KeyPrefix`        | VARCHAR(3)           | Object type identifier (e.g., "001"=Account, "003"=Contact) |
| `DataID`           | VARCHAR(18)  `PK`    | Globally unique record ID (includes KeyPrefix + 15-char ID) |
| `Name`             | VARCHAR(255)         | Record Name field                                           |
| `Value0..Value500` | VARCHAR(4000)        | Generic columns for custom/standard field data              |
| `Clob0..Clob5`     | CLOB                 | Large text/rich text fields (up to 131,072 characters)      |
| `CreatedDate`      | TIMESTAMP            | Record creation timestamp                                   |
| `LastModifiedDate` | TIMESTAMP  `IDX`     | Last modification timestamp                                 |
| `IsDeleted`        | BOOLEAN              | Soft delete (Recycle Bin)                                   |


> **
  **Deep Dive — Custom Indexes (Pivot Tables):**  

  Standard B-tree indexes on `Value0` through `Value500` would be inefficient (all orgs' data mixed). Instead, Salesforce uses **pivot tables** for custom indexes:  

  `MT_Indexes(OrgID, KeyPrefix, FieldNum, StringValue, NumValue, DateValue, DataID)`  

  When admin creates a custom index on `Invoice__c.Amount__c`, platform creates pivot table entries: one row per record with `NumValue = amount`. SOQL `WHERE Amount__c > 10000` becomes a range scan on pivot table's `NumValue` column with standard B-tree index. Skinny tables: for extreme performance, Salesforce support can create "skinny tables" — physical denormalized tables with org's specific columns, bypassing the generic data table for critical queries.


## Cache & Performance Deep Dive


### Caching Layers


  
  
  
  
  
  


| Cache Layer              | Strategy                        | Eviction                         | Details                                                                                                                                                         |
|--------------------------|---------------------------------|----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Metadata Cache (per-pod) | Compiled definitions            | Invalidation-based (event bus)   | Object/field definitions, validation rules, triggers compiled and cached per app server. Cold load: 2-5s. Invalidated on metadata deploy.                       |
| Platform Cache (Org)     | Programmatic (Apex `Cache.Org`) | TTL (up to 48 hours) + LRU       | Developer-controlled cache. Session cache (per-user) and Org cache (shared). 10MB default, up to 100MB purchased. Backed by distributed cache (memcached-like). |
| Query Result Cache       | Auto-cached repeated SOQL       | Record modification invalidation | Identical SOQL queries return cached results if underlying data unchanged. Cache key: org + query hash + field security context.                                |
| CDN (Akamai)             | Static assets                   | Version-based                    | Lightning component bundles, images, static resources. URL-versioned for cache busting.                                                                         |
| Browser Cache (LDS)      | Lightning Data Service          | Record change notification       | Client-side record cache with optimistic UI updates. Shared across components. Wire service invalidation.                                                       |


### Performance Best Practices


  - **Selective Queries** — SOQL queries must be selective (return <10% of object's records). Non-selective queries on objects with >200K records are blocked.

  - **Compound Indexes** — Standard indexes on Id, Name, RecordTypeId, foreign key fields. Custom indexes added by admin. Two-column skinny indexes for frequent filter combinations.

  - **Async Processing** — Batch Apex for processing millions of records (up to 50M). Queueable Apex for chained async jobs. Scheduled Apex for cron-based execution.

  - **Big Objects** — For billion-record storage (e.g., telemetry, audit logs). Stored outside MT_Data in dedicated Hadoop/HBase storage. Queryable via Async SOQL.


## Scaling Considerations


### Multi-Tenant Scaling Architecture


  
  
  
  
  
  
  


| Component             | Scaling Strategy                              | Key Metric                                 |
|-----------------------|-----------------------------------------------|--------------------------------------------|
| App Servers           | Horizontal (add pods per instance)            | ~100-200 concurrent transactions per pod   |
| Database (Oracle RAC) | Vertical + Active-Active nodes                | Thousands of orgs per DB instance          |
| Instance Segmentation | Orgs assigned to instances (NA44, EU25, etc.) | ~10K-70K orgs per instance                 |
| Hyperforce            | Public cloud (AWS) infrastructure             | Independent K8s clusters per region        |
| Search (Solr)         | Per-org sharding, replicated clusters         | ~500ms SOSL query (p95)                    |
| Event Bus             | Kafka clusters per data center                | 250K events/hour standard, 25M high-volume |


### Large Org Handling


  - **Data Skew** — Orgs with very large record counts (>10M) may cause lock contention. Mitigation: defer sharing calculations, async triggers, optimistic locking.

  - **Dedicated Infrastructure** — Very large customers get dedicated database instances or Hyperforce deployment.

  - **Org Split** — Extreme cases: migrate org to less-loaded instance during maintenance window. Instance-to-instance migration with minimal downtime.

  - **Hyperforce** — Next-gen infrastructure running Salesforce on public cloud (AWS). Benefits: data residency compliance, elastic scaling, modernized deployment. Each org runs in isolated Kubernetes namespace.


## Tradeoffs & Alternative Approaches


> **
  **Tradeoff — Multi-Tenant Shared DB vs. Database-per-Tenant:**  

  *Shared DB (Salesforce model)*: All orgs in same Oracle instance. Advantage: operational simplicity (one DB to manage), efficient resource sharing. Disadvantage: noisy neighbor risk (mitigated by governor limits), no per-tenant tuning. *Database-per-tenant (e.g., AWS RDS per customer)*: Perfect isolation, per-tenant scaling. Disadvantage: operational overhead scales with tenant count, expensive for small tenants. Salesforce's approach works because governor limits are extremely effective at preventing noisy neighbors.


> **
  **Tradeoff — Metadata-Driven vs. Physical Schema:**  

  *Metadata-driven (Salesforce)*: Custom objects stored in generic varchar columns. Advantage: instant schema changes (no ALTER TABLE), unlimited customization per tenant. Disadvantage: all data is VARCHAR → no native type enforcement by DB, query plans are complex, JOIN performance limited. *Physical schema*: real columns per field. Advantage: native type enforcement, optimal query plans, standard indexing. Disadvantage: schema changes require DDL, can't scale to 100K tenants with different schemas.


> **
  **Tradeoff — Governor Limits vs. Unlimited Resources:**  

  Governor limits constrain what developers can do per transaction. They prevent: N+1 SOQL patterns, infinite loops, excessive DML. Advantage: platform stability guaranteed, fair resource sharing. Disadvantage: steep learning curve, limits complex business logic, forces awkward patterns (bulkification). Alternative: quota-based billing (charge per CPU-second like AWS Lambda) — more flexible but harder to predict costs.


### Alternative Approaches


  
  
  
  
  
  


| Approach          | Technology                      | When to Prefer                                                                    |
|-------------------|---------------------------------|-----------------------------------------------------------------------------------|
| Open-Source CRM   | SuiteCRM, Odoo, ERPNext         | Budget-constrained, need full customization control, self-hosted                  |
| Modern CRM        | HubSpot, Pipedrive, Freshsales  | SMB focus, simpler UI, lower cost, marketing-first                                |
| Custom Build      | PostgreSQL + React + Node.js    | Highly unique workflows, no multi-tenancy needed, engineering resources available |
| Enterprise CRM    | Microsoft Dynamics 365, SAP CRM | Existing Microsoft/SAP ecosystem, tight ERP integration needed                    |
| Low-Code Platform | OutSystems, Mendix, Appian      | Custom apps beyond CRM, broader low-code requirements                             |


## Additional Information


### SOQL (Salesforce Object Query Language)


  
  
  
  
  
  
  


| Feature            | Syntax                                                       | Notes                                                   |
|--------------------|--------------------------------------------------------------|---------------------------------------------------------|
| Basic Query        | `SELECT Id, Name FROM Account WHERE Industry = 'Tech'`       | No SELECT * — must enumerate fields. Respects FLS.      |
| Relationship Query | `SELECT Name, (SELECT LastName FROM Contacts) FROM Account`  | Parent-to-child subquery. Up to 20 child relationships. |
| Cross-Object       | `SELECT Account.Name, Amount FROM Opportunity`               | Child-to-parent dot notation. Up to 5 levels.           |
| Aggregate          | `SELECT Industry, COUNT(Id) FROM Account GROUP BY Industry`  | SUM, AVG, MIN, MAX, COUNT. HAVING clause supported.     |
| Date Literals      | `WHERE CreatedDate = LAST_N_DAYS:30`                         | TODAY, YESTERDAY, LAST_WEEK, THIS_QUARTER, etc.         |
| Polymorphic        | `SELECT Id, What.Type FROM Task WHERE What.Type = 'Account'` | TYPEOF for polymorphic lookups (WhoId, WhatId).         |


### Security Model (Defense in Depth)


  - **Org-Level** — Login IP ranges, login hours, session timeout, MFA enforcement

  - **Object-Level (Profiles/Permission Sets)** — CRUD permissions per object. Profile: baseline. Permission Set: additive grants.

  - **Field-Level Security (FLS)** — Per-field read/edit visibility. Enforced in UI, API, reports. Developers must explicitly check with `Schema.sObjectType.Account.fields.Revenue__c.isAccessible()`

  - **Record-Level** — Organization-Wide Defaults (OWD): Private/Public Read/Public Read-Write per object. Sharing Rules: criteria-based or owner-based grants. Manual Sharing. Role Hierarchy: upward visibility. Territory Management for geographic access.

  - **Shield Platform Encryption** — AES-256 encryption at rest. Tenant-specific keys (BYOK supported via key management service). Deterministic encryption for filtered queries on encrypted fields. Event Monitoring: API call logging, Login Forensics, Transaction Security policies.


### Einstein AI Platform


  - **Einstein Prediction Builder** — No-code ML: predict any custom field value. Logistic regression for binary outcomes, gradient boosted trees for scoring. Trained on org's own data.

  - **Einstein Bots** — NLU-powered chatbots for Service Cloud. Intent classification + entity extraction. Seamless escalation to human agent.

  - **Einstein GPT** — Generative AI integration. Draft emails, summarize cases, generate knowledge articles. Trust Layer: prompt injection defense, PII masking, toxicity detection, audit logging.

  - **Einstein Discovery** — Automated analytics: finds patterns, predicts outcomes, recommends actions. Built on Tableau/CRM Analytics engine.