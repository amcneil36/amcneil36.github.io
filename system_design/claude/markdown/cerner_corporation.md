# 🏥 Cerner Corporation (Healthcare EHR) - System Design


## Functional Requirements


1. **Electronic Health Records (EHR):** Store, retrieve, and update patient medical records — demographics, diagnoses (ICD-10), medications, lab results, clinical notes, vitals, allergies, immunizations

2. **Clinical Decision Support & Orders (CPOE):** Computerized Provider Order Entry — physicians order labs, medications, imaging with drug-drug/drug-allergy interaction alerts, evidence-based clinical rules

3. **Health Information Exchange (HIE):** FHIR/HL7-based interoperability — exchange patient data across hospitals, labs, pharmacies, insurance; patient consent management


## Non-Functional Requirements


- **Availability:** 99.99% uptime — EHR downtime directly impacts patient care; disaster recovery with RTO <15 min

- **Compliance:** HIPAA (US), GDPR (EU), HITECH Act — full audit trail, encryption at rest and in transit, BAA with all vendors

- **Latency:** Chart load <2s, order entry <1s, lab result display <500ms; alert firing within 100ms of order submission

- **Scale:** 25K+ healthcare facilities, 250M+ patient records, 1B+ clinical documents

- **Data Integrity:** ACID transactions for clinical data — no partial updates to patient records; strong consistency


## Flow 1: Patient Chart Retrieval & Clinical Documentation


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 350" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#00A4E4">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Clinician
</text>
<text x="80" y="87" text-anchor="middle" fill="#fff" font-size="10">
(PowerChart)
</text>


<rect x="190" y="50" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="255" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API Gateway
</text>
<text x="255" y="87" text-anchor="middle" fill="#fff" font-size="10">
(OAuth2 + RBAC)
</text>


<rect x="370" y="50" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="435" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Chart Service
</text>
<text x="435" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Aggregator)
</text>


<rect x="550" y="20" width="130" height="45" rx="8" fill="#795548">
</rect>
<text x="615" y="47" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Patient DB (Oracle)
</text>


<rect x="550" y="80" width="130" height="45" rx="8" fill="#795548">
</rect>
<text x="615" y="107" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Clinical Doc Store
</text>


<rect x="550" y="140" width="130" height="45" rx="8" fill="#00897B">
</rect>
<text x="615" y="167" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Lab Results Cache
</text>


<rect x="740" y="50" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="805" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Audit Log
</text>
<text x="805" y="87" text-anchor="middle" fill="#fff" font-size="10">
(HIPAA Trail)
</text>


<rect x="370" y="200" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="435" y="222" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
CDS Engine
</text>
<text x="435" y="237" text-anchor="middle" fill="#fff" font-size="10">
(Alert Rules)
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="320" y1="75" x2="365" y2="75" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="65" x2="545" y2="42" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="75" x2="545" y2="100" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="90" x2="545" y2="155" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="100" x2="435" y2="195" stroke="#00A4E4" stroke-width="1.5" marker-end="url(#ah)">
</line>


<line x1="435" y1="100" x2="740" y2="65" stroke="#00A4E4" stroke-width="1.5" marker-end="url(#ah)">
</line>


</svg>

</details>


### Steps


1. Clinician authenticates via SSO (SAML 2.0) with role-based access — physician, nurse, pharmacist get different views

2. Searches patient by MRN (Medical Record Number) or name; API Gateway validates OAuth2 token + RBAC permissions

3. Chart Service aggregates data from multiple sources: demographics, problems list, medications, allergies, vitals, labs, notes

4. Patient DB (Oracle) queried for demographics and care plan; Clinical Doc Store returns structured notes (CDA/C-CDA format)

5. Lab Results Cache (Redis) serves recent results; older results fetched from lab system via HL7v2 interface

6. CDS Engine evaluates rules against patient data — displays alerts (drug allergies, overdue screenings, abnormal values)

7. Every chart access logged to HIPAA Audit Log with user_id, patient_id, timestamp, action, reason for access


### Example


Dr. Smith opens patient John Doe (MRN: 12345678). Chart Service fetches: 3 active diagnoses (Diabetes T2, Hypertension, Asthma), 5 active medications, 12 recent lab results (HbA1c: 7.2%), 2 allergies (Penicillin, Sulfa). CDS fires alert: "HbA1c above target — consider medication adjustment." Audit log records Dr. Smith accessed chart at 14:32 UTC.


### Deep Dives


HIPAA Audit Trail


**Requirement:** Every access to PHI (Protected Health Information) must be logged — who, what, when, where, why


**Storage:** Append-only log in dedicated database; tamper-proof with cryptographic hashing (each entry references previous hash)


**Retention:** Minimum 6 years per HIPAA; typically 10 years for legal defensibility


**Monitoring:** Break-the-glass alerts — accessing patient records without treatment relationship triggers immediate review


Role-Based Access Control


**Model:** RBAC with attribute-based extensions (ABAC) — role + department + care team membership + patient consent


**Roles:** Physician (full chart), Nurse (care notes + vitals), Pharmacist (meds only), Billing (demographics + codes only)


**Consent:** Patient can restrict access to sensitive records (behavioral health, HIV, substance abuse — 42 CFR Part 2)


Clinical Document Architecture (CDA)


**Standard:** HL7 CDA R2 / C-CDA (Consolidated CDA) — XML-based clinical document format


**Sections:** Problems, Medications, Allergies, Procedures, Results, Vitals, Plan of Care


**Coded Data:** SNOMED CT (problems), RxNorm (medications), LOINC (lab tests), ICD-10 (diagnoses), CPT (procedures)


Clinical Decision Support


**Engine:** Rule-based system (Arden Syntax MLMs) + ML models for predictive alerts


**Rules:** Drug-drug interactions (3,000+ rules), drug-allergy checks, dosing limits, duplicate orders, care gaps


**Alert Fatigue:** Tiered severity (info → warning → hard stop); suppression logic for previously overridden alerts


## Flow 2: Computerized Provider Order Entry (CPOE)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 320" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#6CC24A">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Physician
</text>
<text x="80" y="97" text-anchor="middle" fill="#fff" font-size="10">
(PowerOrders)
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Order Service
</text>


<rect x="370" y="30" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="435" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
CDS Engine
</text>
<text x="435" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Interaction Check)
</text>


<rect x="370" y="100" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Pharmacy Queue
</text>
<text x="435" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Verification)
</text>


<rect x="560" y="60" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="625" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Orders DB
</text>
<text x="625" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Oracle + Event Log)
</text>


<rect x="740" y="30" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="805" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
eMAR
</text>
<text x="805" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Medication Admin)
</text>


<rect x="740" y="100" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="805" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Lab Interface
</text>
<text x="805" y="137" text-anchor="middle" fill="#fff" font-size="10">
(HL7 ORM→Lab)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#6CC24A" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#6CC24A" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="95" x2="365" y2="120" stroke="#6CC24A" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="85" x2="555" y2="85" stroke="#6CC24A" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="690" y1="85" x2="735" y2="55" stroke="#6CC24A" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="690" y1="85" x2="735" y2="120" stroke="#6CC24A" stroke-width="2" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Steps


1. Physician selects medication order: Drug (RxNorm code), dose, route, frequency, duration

2. Order Service sends to CDS Engine for real-time interaction checking (within 100ms)

3. CDS checks: drug-drug interactions, drug-allergy, duplicate therapy, renal/hepatic dose adjustment, weight-based dosing

4. If hard-stop alert → order blocked until override with documented reason; soft alert → displayed, can be acknowledged

5. Order persisted to Orders DB with status: Ordered → Verified → Dispensed → Administered → Completed

6. Medication order routed to Pharmacy Queue for pharmacist verification (required by law for most medications)

7. After pharmacist verification, order appears on eMAR (electronic Medication Administration Record) for nurse administration

8. Lab orders sent via HL7 ORM message to laboratory information system (LIS) for processing


### Example


Dr. Smith orders Amoxicillin 500mg PO TID for 10 days. CDS Engine checks: Patient has Penicillin allergy → HARD STOP alert "Cross-reactivity: Amoxicillin is a penicillin-type antibiotic." Dr. Smith cancels order, selects Azithromycin instead. CDS passes. Order sent to pharmacy queue. Pharmacist verifies dose is appropriate. Order appears on nurse's eMAR with barcode for bedside scanning.


### Deep Dives


HL7v2 Messaging


**Protocol:** HL7v2 (pipe-delimited) over MLLP (Minimum Lower Layer Protocol) — TCP with header/trailer bytes


**Messages:** ORM (Order), ORU (Result), ADT (Admit/Discharge/Transfer), SIU (Scheduling)


**Example:** `MSH|^~\&|CERNER|HOSPITAL|LAB|LABSYS|202402...|ORM^O01`


**Ack:** Every message requires ACK response; retry queue with exponential backoff on NACK


Order State Machine


**States:** Draft → Signed → Verified → Dispensed → Administered → Completed / Discontinued


**Events:** Each transition logged with timestamp, user_id, reason; full audit trail per order


**Idempotency:** Order IDs are globally unique (UUID); duplicate submission detection via order_id dedup


## Flow 3: Health Information Exchange (FHIR API)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FF6B35">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#607D8B">
</rect>
<text x="80" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
External EHR
</text>
<text x="80" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Epic, Allscripts)
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
FHIR Gateway
</text>
<text x="255" y="97" text-anchor="middle" fill="#fff" font-size="10">
(R4 / SMART)
</text>


<rect x="370" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="435" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Integration Engine
</text>
<text x="435" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Transform/Route)
</text>


<rect x="550" y="30" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="615" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Consent Service
</text>
<text x="615" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Patient Prefs)
</text>


<rect x="550" y="100" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="615" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Master Patient
</text>
<text x="615" y="137" text-anchor="middle" fill="#fff" font-size="10">
Index (MPI)
</text>


<rect x="740" y="60" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="805" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Patient DB
</text>
<text x="805" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Local EHR)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#FF6B35" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="85" x2="365" y2="85" stroke="#FF6B35" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="75" x2="545" y2="55" stroke="#FF6B35" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="95" x2="545" y2="120" stroke="#FF6B35" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="680" y1="85" x2="735" y2="85" stroke="#FF6B35" stroke-width="2" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Steps


1. External EHR requests patient data via FHIR R4 API: `GET /Patient/{id}/everything` with SMART on FHIR OAuth2 token

2. FHIR Gateway validates token, scopes (patient/*.read), and enforces rate limiting

3. Integration Engine maps internal data model to FHIR Resources (Patient, Condition, MedicationRequest, Observation)

4. Consent Service checks patient's data sharing preferences — restricts sensitive categories per consent directives

5. Master Patient Index (MPI) resolves patient identity across systems using probabilistic matching (name, DOB, SSN, address)

6. Data returned as FHIR Bundle (JSON) with standardized coding (SNOMED, LOINC, ICD-10, RxNorm)


### Example


Patient transfers from Hospital A (Epic) to Hospital B (Cerner). Hospital A's system calls Cerner's FHIR endpoint for patient records. MPI matches patient across systems (John Doe, DOB 1985-03-15, probabilistic match score 0.98). Consent service confirms patient opted in to data sharing. Returns FHIR Bundle: 5 Conditions, 8 MedicationRequests, 20 Observations, 2 AllergyIntolerances.


### Deep Dives


FHIR R4 API


**Standard:** HL7 FHIR (Fast Healthcare Interoperability Resources) R4 — RESTful API


**Resources:** Patient, Condition, Observation, MedicationRequest, Procedure, DiagnosticReport, AllergyIntolerance


**Auth:** SMART on FHIR — OAuth2 with clinical scopes (patient/Observation.read, user/MedicationRequest.write)


**Mandate:** CMS Interoperability Rule (2020) requires FHIR API for patient data access


Master Patient Index (MPI)


**Algorithm:** Probabilistic matching using weighted scores: last name (0.3), first name (0.2), DOB (0.25), SSN (0.15), address (0.1)


**Threshold:** Score >0.95 = auto-link; 0.80-0.95 = manual review queue; <0.80 = no match


**Dedup:** Golden record concept — merge duplicate patient records into single longitudinal record


## Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 500" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#00A4E4">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="130" height="50" rx="8" fill="#34A853">
</rect>
<text x="85" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
PowerChart
</text>
<text x="85" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Clinician UI)
</text>


<rect x="20" y="140" width="130" height="50" rx="8" fill="#34A853">
</rect>
<text x="85" y="162" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Patient Portal
</text>
<text x="85" y="177" text-anchor="middle" fill="#fff" font-size="10">
(HealtheLife)
</text>


<rect x="210" y="90" width="130" height="60" rx="8" fill="#FB8C00">
</rect>
<text x="275" y="115" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API Gateway
</text>
<text x="275" y="132" text-anchor="middle" fill="#fff" font-size="10">
(OAuth2 + HIPAA)
</text>


<rect x="400" y="30" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="465" y="55" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Chart Service
</text>


<rect x="400" y="100" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="465" y="125" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Order Service
</text>


<rect x="400" y="170" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="465" y="195" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
FHIR Gateway
</text>


<rect x="590" y="30" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="655" y="55" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
CDS Engine
</text>


<rect x="590" y="100" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="655" y="125" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Integration Engine
</text>


<rect x="590" y="170" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="655" y="195" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
MPI Service
</text>


<rect x="790" y="60" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="855" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
HL7/FHIR Bus
</text>
<text x="855" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Message Broker)
</text>


<rect x="790" y="140" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="855" y="162" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
External Systems
</text>
<text x="855" y="177" text-anchor="middle" fill="#fff" font-size="10">
(Labs, Pharmacy)
</text>


<rect x="400" y="310" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="465" y="332" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Oracle RAC
</text>
<text x="465" y="347" text-anchor="middle" fill="#fff" font-size="10">
(Patient Data)
</text>


<rect x="590" y="310" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="655" y="332" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Document Store
</text>
<text x="655" y="347" text-anchor="middle" fill="#fff" font-size="10">
(CDA/C-CDA)
</text>


<rect x="790" y="310" width="130" height="55" rx="8" fill="#9C27B0">
</rect>
<text x="855" y="332" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Audit DB
</text>
<text x="855" y="347" text-anchor="middle" fill="#fff" font-size="10">
(HIPAA Log)
</text>


<rect x="400" y="410" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="465" y="432" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Redis Cache
</text>
<text x="465" y="447" text-anchor="middle" fill="#fff" font-size="10">
(Sessions + Labs)
</text>


<line x1="150" y1="85" x2="205" y2="110" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="150" y1="165" x2="205" y2="130" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="340" y1="105" x2="395" y2="55" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="340" y1="120" x2="395" y2="125" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="340" y1="135" x2="395" y2="195" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="530" y1="55" x2="585" y2="55" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="530" y1="125" x2="585" y2="125" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="530" y1="195" x2="585" y2="195" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="720" y1="125" x2="785" y2="85" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="920" y1="85" x2="920" y2="135" stroke="#00A4E4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="465" y1="150" x2="465" y2="305" stroke="#00A4E4" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="530" y1="80" x2="590" y2="310" stroke="#00A4E4" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="720" y1="55" x2="790" y2="320" stroke="#00A4E4" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="465" y1="365" x2="465" y2="405" stroke="#00A4E4" stroke-width="1.5" marker-end="url(#ah4)">
</line>


</svg>

</details>


## Database Schema


### Oracle RAC — Patient & Orders

`CREATE TABLE patients (
  patient_id    NUMBER PRIMARY KEY,      `PK`
  mrn           VARCHAR2(20) UNIQUE,     `IDX`
  first_name    VARCHAR2(100),
  last_name     VARCHAR2(100),           `IDX`
  dob           DATE,                    `IDX`
  gender        CHAR(1),
  ssn_hash      CHAR(64),              -- SHA-256 for matching
  address       CLOB,
  phone         VARCHAR2(20),
  insurance_id  NUMBER REFERENCES insurance_plans,  `FK`
  created_at    TIMESTAMP WITH TIME ZONE
);

CREATE TABLE encounters (
  encounter_id  NUMBER PRIMARY KEY,      `PK`
  patient_id    NUMBER REFERENCES patients,  `FK`
  facility_id   NUMBER,                  `FK`
  encounter_type VARCHAR2(20),  -- inpatient, outpatient, ED
  admit_dt      TIMESTAMP,
  discharge_dt  TIMESTAMP,
  attending_id  NUMBER REFERENCES providers,  `FK`
  status        VARCHAR2(20)
);
CREATE INDEX idx_enc_patient ON encounters(patient_id, admit_dt DESC);

CREATE TABLE orders (
  order_id      NUMBER PRIMARY KEY,      `PK`
  encounter_id  NUMBER REFERENCES encounters,  `FK`
  patient_id    NUMBER,                  `FK`
  order_type    VARCHAR2(20),  -- medication, lab, imaging, consult
  code          VARCHAR2(20),  -- RxNorm / LOINC / CPT
  code_system   VARCHAR2(30),
  display_name  VARCHAR2(255),
  status        VARCHAR2(20),  -- ordered, verified, completed, cancelled
  ordered_by    NUMBER REFERENCES providers,  `FK`
  ordered_at    TIMESTAMP,
  details       CLOB  -- JSON: dose, route, frequency, duration
);
CREATE INDEX idx_orders_patient ON orders(patient_id, ordered_at DESC);`


 `SHARD` Oracle RAC (Real Application Clusters) — shared-disk architecture with active-active nodes; no application-level sharding needed


### Clinical Results & Audit

`CREATE TABLE observations (
  obs_id        NUMBER PRIMARY KEY,      `PK`
  patient_id    NUMBER,                  `FK`
  encounter_id  NUMBER,                  `FK`
  loinc_code    VARCHAR2(10),           `IDX`
  value_numeric NUMBER(10,3),
  value_string  VARCHAR2(500),
  unit          VARCHAR2(20),
  reference_low NUMBER(10,3),
  reference_high NUMBER(10,3),
  status        VARCHAR2(20),
  performed_at  TIMESTAMP,
  reported_at   TIMESTAMP
);
CREATE INDEX idx_obs_patient ON observations(patient_id, loinc_code, performed_at DESC);

CREATE TABLE audit_log (
  log_id        NUMBER PRIMARY KEY,      `PK`
  user_id       NUMBER,
  patient_id    NUMBER,                  `IDX`
  action        VARCHAR2(50),  -- view_chart, modify_order, print_record
  resource_type VARCHAR2(50),
  resource_id   NUMBER,
  ip_address    VARCHAR2(45),
  user_agent    VARCHAR2(500),
  reason        VARCHAR2(255),  -- treatment, payment, operations, emergency
  prev_hash     CHAR(64),      -- chain for tamper detection
  created_at    TIMESTAMP        `IDX`
);`


## Cache & CDN Deep Dive


Clinical Data Cache (Redis)


**Cached:** Recent lab results, active medication list, allergy list — high-frequency reads during chart review


**Strategy:** Write-through — every new result updates cache and DB simultaneously


**TTL:** 15 minutes for lab results; medication list cached until next order change event


**Encryption:** Redis TLS + application-layer AES-256 encryption for PHI in cache (HIPAA requirement)


Session Cache


**Storage:** Redis with TLS — stores OAuth2 tokens, user context, active patient context


**TTL:** 15-minute session timeout (HIPAA auto-logoff requirement); refreshed on activity


**Context Switch:** When clinician switches patients, previous patient context evicted immediately


No Public CDN


**Reason:** HIPAA prohibits caching PHI on public CDN edges — all clinical data served from datacenter


**Static Assets:** Application JS/CSS/images can use CDN with no-store for API responses


**Private CDN:** Internal content delivery within hospital network (Akamai Private Peering or similar)


## Scaling & Load Balancing


- **Oracle RAC:** Active-active cluster (2-4 nodes) with shared storage — automatic failover; transparent application routing (TAR) redistributes connections on node failure

- **Application Tier:** Stateless microservices behind F5 BIG-IP load balancers; health-based routing; SSL offloading

- **Multi-Tenant:** Each healthcare organization gets logical tenant isolation (separate schemas or databases) — data never co-mingled across organizations

- **Disaster Recovery:** Active-passive across datacenters with Oracle Data Guard (synchronous redo log shipping); RTO <15 minutes, RPO near-zero

- **Integration Bus:** HL7 interface engine (Rhapsody / Mirth Connect) scaled horizontally; message queues (RabbitMQ) buffer during downstream outages

- **Peak Handling:** Morning rounding (7-9 AM) creates 5x normal load — pre-warmed caches and auto-scaled compute


## Tradeoffs


> **

✅ Oracle RAC


- Proven in healthcare for 20+ years — regulatory acceptance

- Strong ACID guarantees — critical for clinical data integrity

- Active-active HA without application-level sharding


❌ Oracle RAC


- Extremely expensive licensing ($47K/core)

- Vertical scaling limitations — shared-disk architecture

- Vendor lock-in; migration to cloud-native databases is painful


✅ HL7v2 (Legacy Protocol)


- Universal adoption — every healthcare system supports it

- Battle-tested over 30+ years; well-understood failure modes

- Simple pipe-delimited format — easy to debug and parse


❌ HL7v2 (Legacy Protocol)


- Optionality nightmare — same message interpreted differently by vendors

- No standardized transport (MLLP is fragile, no built-in encryption)

- Point-to-point integration doesn't scale (N×M interface problem)


## Alternative Approaches


Cloud-Native (AWS HealthLake)


FHIR-native data store on AWS with built-in HIPAA compliance. Eliminates Oracle licensing, enables elastic scaling. But nascent technology with less proven track record in critical care settings and potential data sovereignty concerns.


OpenEHR Architecture


Archetype-based data modeling — separates clinical knowledge from software. Extremely flexible for evolving clinical requirements. Used by NHS in UK. More complex than FHIR but richer clinical modeling capability.


Event-Sourced EHR


Store all clinical events as immutable log (patient_admitted, medication_ordered, vitals_recorded). Current state computed from events. Perfect audit trail, temporal queries ("what was the med list at time T?"), but complex read path and regulatory acceptance uncertain.


## Additional Information


- **Meaningful Use / Promoting Interoperability:** CMS incentive program requiring EHRs to meet specific functionality criteria — CPOE, CDS, e-prescribing, care coordination. Cerner is ONC-certified.

- **Oracle Acquisition (2022):** Oracle acquired Cerner for $28.3B — integrating with Oracle Cloud Infrastructure (OCI) and Oracle Autonomous Database; moving from on-premise to cloud-hosted model.

- **Millennium vs Oracle Health:** Cerner Millennium is the legacy platform; Oracle Health (renamed 2023) is the cloud-native successor leveraging Oracle DB, Kubernetes, and FHIR-first architecture.

- **Barcode Medication Administration (BCMA):** Nurses scan patient wristband barcode + medication barcode before administration — 5 Rights verification (right patient, drug, dose, route, time). Reduces medication errors by ~50%.

- **Downtime Procedures:** Every hospital must have paper-based fallback procedures when EHR is unavailable — pre-printed order sheets, medication administration logs. EHR downtime directly threatens patient safety.