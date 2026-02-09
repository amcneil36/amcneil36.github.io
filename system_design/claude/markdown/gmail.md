# 📧 Gmail - System Design


## Functional Requirements


1. **Send & Receive Email:** Compose, send, receive, and deliver emails with attachments (up to 25MB inline, 50MB receive)

2. **Inbox Management:** Labels, folders, starring, archiving, snoozing, threaded conversations, tabs (Primary/Social/Promotions)

3. **Search:** Full-text search across all mail, labels, attachments, date ranges — sub-second results over years of history


## Non-Functional Requirements


- **Scale:** 1.8B+ users, ~333B emails sent/day globally, 15GB free storage/user

- **Latency:** Inbox load <500ms, search <1s, send <2s perceived

- **Availability:** 99.99% uptime (Google Workspace SLA)

- **Durability:** Zero data loss — multi-region replication, 99.999999999% (11 nines) durability

- **Security:** TLS in transit, at-rest encryption, SPF/DKIM/DMARC, phishing/spam detection


## Flow 1: Sending an Email


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 370" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#4285F4">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="80" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Client (Web/App)
</text>


<rect x="190" y="50" width="120" height="50" rx="8" fill="#4285F4">
</rect>
<text x="250" y="80" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
API Gateway
</text>


<rect x="360" y="50" width="120" height="50" rx="8" fill="#4285F4">
</rect>
<text x="420" y="80" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Compose Service
</text>


<rect x="530" y="20" width="120" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="590" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Spam/Phishing
</text>
<text x="590" y="57" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Filter
</text>


<rect x="530" y="90" width="120" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="590" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Attachment
</text>
<text x="590" y="127" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Service (GCS)
</text>


<rect x="700" y="50" width="120" height="50" rx="8" fill="#E53935">
</rect>
<text x="760" y="80" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
SMTP Relay
</text>


<rect x="700" y="150" width="120" height="50" rx="8" fill="#795548">
</rect>
<text x="760" y="172" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Sent Mail Store
</text>
<text x="760" y="187" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Bigtable)
</text>


<rect x="360" y="200" width="120" height="50" rx="8" fill="#00897B">
</rect>
<text x="420" y="222" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Outbound Queue
</text>
<text x="420" y="237" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Pub/Sub)
</text>


<rect x="530" y="200" width="120" height="50" rx="8" fill="#4285F4">
</rect>
<text x="590" y="222" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
DKIM Signer /
</text>
<text x="590" y="237" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
SPF Validator
</text>


<rect x="700" y="280" width="120" height="50" rx="8" fill="#607D8B">
</rect>
<text x="760" y="302" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Remote MTA
</text>
<text x="760" y="317" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Recipient ISP)
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#4285F4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="310" y1="75" x2="355" y2="75" stroke="#4285F4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="480" y1="65" x2="525" y2="50" stroke="#4285F4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="480" y1="85" x2="525" y2="110" stroke="#4285F4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="650" y1="45" x2="695" y2="65" stroke="#4285F4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="480" y1="95" x2="420" y2="195" stroke="#4285F4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="480" y1="225" x2="525" y2="225" stroke="#4285F4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="650" y1="225" x2="695" y2="175" stroke="#4285F4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="760" y1="100" x2="760" y2="145" stroke="#4285F4" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="760" y1="200" x2="760" y2="275" stroke="#4285F4" stroke-width="2" marker-end="url(#ah)">
</line>


</svg>

</details>


### Steps


1. Client sends composed email via HTTPS POST to API Gateway (or SMTP submission on port 587)

2. Compose Service validates headers, body, recipients, rate limits, and drafts (stored in Bigtable)

3. Attachments uploaded to Google Cloud Storage (GCS), referenced by blob ID in email metadata

4. Spam/phishing filter scans outbound email (TensorFlow models for abuse prevention)

5. Email enqueued to outbound Pub/Sub queue for asynchronous delivery

6. DKIM signer adds cryptographic signature header; SPF TXT record validated for sender domain

7. SMTP relay delivers to recipient MTA via STARTTLS (MX record lookup, retry with exponential backoff)

8. Copy saved to sender's "Sent" folder in Bigtable; threading ID linked


### Example


User sends email to colleague@company.com. Compose service validates, attachment (3MB PDF) uploaded to GCS, spam filter passes, DKIM signed with d=gmail.com selector, queued, SMTP relay resolves MX for company.com → delivers to corporate Exchange server over TLS 1.3.


### Deep Dives


SMTP Protocol


**Protocol:** SMTP (RFC 5321) over TCP port 25 (server-to-server) or 587 (submission with AUTH)


**Commands:** EHLO → MAIL FROM → RCPT TO → DATA → QUIT


**STARTTLS:** Opportunistic encryption upgrade; Gmail enforces TLS for 90%+ of outbound


**Retry:** Exponential backoff (1min → 5min → 15min → 1hr → 4hr) for up to 5 days per RFC 5321


DKIM / SPF / DMARC


**DKIM:** RSA-2048 signature over headers + body hash; selector rotated quarterly


**SPF:** DNS TXT record listing authorized sending IPs; soft fail (~all) vs hard fail (-all)


**DMARC:** Policy alignment — both DKIM and SPF must pass with From: domain alignment; p=reject for Google domains


Attachment Storage


**Service:** Google Cloud Storage (multi-region, Standard class)


**Limit:** 25MB per email (outbound), 50MB receive; larger files auto-redirect to Drive link


**Dedup:** Content-addressable storage — SHA-256 hash → blob; same attachment shared across copies


**Virus Scan:** Every attachment scanned by ClamAV + proprietary ML before delivery


Outbound Queue


**System:** Google Pub/Sub — at-least-once delivery, exactly-once processing via dedup ID


**Partitioning:** By recipient domain for batching — multiple emails to same domain sent in single SMTP session


**Rate Limiting:** Per-domain throttling to avoid being flagged as spam by recipient ISPs


## Flow 2: Receiving Email & Inbox Delivery


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 340" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#34A853">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#607D8B">
</rect>
<text x="80" y="80" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Sender MTA
</text>


<rect x="190" y="50" width="120" height="50" rx="8" fill="#E53935">
</rect>
<text x="250" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Inbound MX
</text>
<text x="250" y="87" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Gateway
</text>


<rect x="360" y="20" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="425" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Spam Classifier
</text>
<text x="425" y="57" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(TensorFlow)
</text>


<rect x="360" y="90" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="425" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Phishing / Malware
</text>
<text x="425" y="127" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Scanner
</text>


<rect x="540" y="50" width="120" height="50" rx="8" fill="#4285F4">
</rect>
<text x="600" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Delivery Service
</text>
<text x="600" y="87" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Categorizer)
</text>


<rect x="710" y="20" width="120" height="50" rx="8" fill="#795548">
</rect>
<text x="770" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Mailbox Store
</text>
<text x="770" y="57" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Bigtable)
</text>


<rect x="710" y="90" width="120" height="50" rx="8" fill="#00897B">
</rect>
<text x="770" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Search Index
</text>
<text x="770" y="127" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Custom)
</text>


<rect x="540" y="180" width="120" height="50" rx="8" fill="#4285F4">
</rect>
<text x="600" y="202" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Push Notifier
</text>
<text x="600" y="217" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(FCM / APNs)
</text>


<rect x="710" y="180" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="770" y="202" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Client Sync
</text>
<text x="770" y="217" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(IMAP / API)
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#34A853" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="310" y1="65" x2="355" y2="50" stroke="#34A853" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="310" y1="85" x2="355" y2="110" stroke="#34A853" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="490" y1="45" x2="535" y2="65" stroke="#34A853" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="490" y1="115" x2="535" y2="85" stroke="#34A853" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="660" y1="65" x2="705" y2="45" stroke="#34A853" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="660" y1="85" x2="705" y2="110" stroke="#34A853" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="600" y1="100" x2="600" y2="175" stroke="#34A853" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="660" y1="205" x2="705" y2="205" stroke="#34A853" stroke-width="2" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Steps


1. Sender MTA connects to Gmail MX (aspmx.l.google.com) on port 25 over STARTTLS

2. Inbound gateway validates SPF, DKIM signature, DMARC alignment; rejects on hard fail

3. TensorFlow spam classifier scores email (0-1.0); >0.7 → Spam folder, >0.95 → reject at SMTP level

4. Phishing scanner checks URLs against Safe Browsing DB, attachment sandboxing via gVisor

5. Delivery Service categorizes: Primary / Social / Promotions / Updates using ML classifier trained on user behavior

6. Email written to Bigtable (mailbox row) with thread linking via References/In-Reply-To headers

7. Search index updated asynchronously (body, headers, attachment text via OCR/Tika extraction)

8. Push notification sent via FCM (Android) / APNs (iOS); IMAP IDLE notify for desktop clients


### Example


Newsletter from service@shopify.com arrives. SPF passes (IP in Shopify's SPF record), DKIM valid. Spam score 0.15 — not spam. ML categorizer routes to "Promotions" tab. Stored in Bigtable under user's mailbox, indexed for search. No push notification (Promotions tab notifications disabled by user preference).


### Deep Dives


Spam Classification


**Model:** TensorFlow deep neural network — features include sender reputation, content analysis, URL patterns, header anomalies, user feedback signals


**Training:** Continuously retrained on billions of labeled examples from user "Report Spam" / "Not Spam" actions


**Accuracy:** 99.9% spam detection, <0.05% false positive rate


**RBL:** Real-time blackhole lists consulted (Spamhaus, Barracuda) for known bad IPs


Tab Categorization


**Categories:** Primary, Social, Promotions, Updates, Forums — ML classifier per user


**Features:** Sender domain, List-Unsubscribe header presence, content keywords, user interaction history


**Personalization:** User drag-and-drop re-categorization feeds back into per-user model fine-tuning


Thread Linking


**Algorithm:** Match by Message-ID, In-Reply-To, References headers (RFC 5322)


**Fallback:** Subject normalization (strip Re:/Fwd: prefixes) + sender/recipient overlap heuristic


**Storage:** Conversation ID (thread_id) in Bigtable — all messages in thread share same key prefix


IMAP Protocol


**Protocol:** IMAP4rev1 (RFC 3501) over TLS port 993


**IDLE:** Long-lived TCP connection for push-like real-time notification (RFC 2177)


**CONDSTORE:** Conditional STORE for efficient sync — mod-sequence numbers track mailbox changes


**Gmail Extensions:** X-GM-LABELS for label sync, X-GM-THRID for thread ID mapping


## Flow 3: Email Search


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FBBC05">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="90" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Client
</text>


<rect x="190" y="60" width="120" height="50" rx="8" fill="#4285F4">
</rect>
<text x="250" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Search Frontend
</text>
<text x="250" y="97" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Service
</text>


<rect x="360" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="425" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Query Parser
</text>
<text x="425" y="97" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Structured)
</text>


<rect x="540" y="30" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="605" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Per-User Index
</text>
<text x="605" y="67" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Inverted)
</text>


<rect x="540" y="100" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="605" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Mailbox Store
</text>
<text x="605" y="137" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Bigtable)
</text>


<rect x="720" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="785" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Ranking &
</text>
<text x="785" y="97" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Snippeting
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#FBBC05" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="310" y1="85" x2="355" y2="85" stroke="#FBBC05" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="490" y1="75" x2="535" y2="55" stroke="#FBBC05" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="490" y1="95" x2="535" y2="120" stroke="#FBBC05" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="670" y1="55" x2="715" y2="75" stroke="#FBBC05" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="670" y1="125" x2="715" y2="95" stroke="#FBBC05" stroke-width="2" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Steps


1. User types query (e.g., `from:boss@company.com has:attachment after:2024/01/01 budget`)

2. Query parser tokenizes into structured operators: from_filter, has_attachment, date_range, full_text_terms

3. Per-user inverted index consulted — each user has dedicated index partitions (not global web-scale index)

4. Posting lists intersected: matching message IDs retrieved, filtered by date range predicate

5. Full message metadata fetched from Bigtable for ranking (recency, sender importance, interaction history)

6. Snippets generated by highlighting matching terms in message body (KWIC — keyword in context)

7. Results returned ranked by relevance with pagination (cursor-based using message timestamp)


### Example


Search `from:hr@company.com subject:benefits`. Parser extracts from: and subject: operators. Per-user index lookups: from:hr@company.com → {msg_42, msg_187, msg_301}, subject:benefits → {msg_42, msg_899}. Intersection: {msg_42}. Bigtable fetch for msg_42 metadata, snippet generated, returned in ~200ms.


### Deep Dives


Per-User Index Architecture


**Design:** Each user's mailbox has its own inverted index — not a shared global index


**Reason:** Privacy isolation (no cross-user data leaks), simpler ACL model


**Storage:** Index shards co-located with mailbox data in Bigtable for locality


**Update:** Near-real-time — new emails indexed within seconds of delivery


Search Operators


**Supported:** from:, to:, subject:, has:attachment, filename:, label:, after:, before:, is:starred, is:unread, larger:, in:anywhere


**Boolean:** AND (implicit), OR, NOT/- (negation)


**Grouping:** Parentheses for complex queries: `(from:a OR from:b) subject:meeting`


## Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 520" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#4285F4">
</polygon>
</marker>
</defs>


<rect x="20" y="30" width="140" height="50" rx="8" fill="#34A853">
</rect>
<text x="90" y="55" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Web Client
</text>
<text x="90" y="70" text-anchor="middle" fill="#fff" font-size="10">
(React SPA)
</text>


<rect x="20" y="100" width="140" height="50" rx="8" fill="#34A853">
</rect>
<text x="90" y="125" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Mobile Apps
</text>
<text x="90" y="140" text-anchor="middle" fill="#fff" font-size="10">
(iOS / Android)
</text>


<rect x="20" y="170" width="140" height="50" rx="8" fill="#34A853">
</rect>
<text x="90" y="195" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
IMAP/SMTP
</text>
<text x="90" y="210" text-anchor="middle" fill="#fff" font-size="10">
(3rd Party Clients)
</text>


<rect x="220" y="80" width="140" height="70" rx="8" fill="#FB8C00">
</rect>
<text x="290" y="110" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Google Front End
</text>
<text x="290" y="127" text-anchor="middle" fill="#fff" font-size="10">
(GFE / L7 LB)
</text>


<rect x="420" y="20" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="485" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Compose Service
</text>
<text x="485" y="57" text-anchor="middle" fill="#fff" font-size="10">
(Send Pipeline)
</text>


<rect x="420" y="90" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="485" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Delivery Service
</text>
<text x="485" y="127" text-anchor="middle" fill="#fff" font-size="10">
(Receive Pipeline)
</text>


<rect x="420" y="160" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="485" y="182" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Search Service
</text>
<text x="485" y="197" text-anchor="middle" fill="#fff" font-size="10">
(Per-User Index)
</text>


<rect x="610" y="20" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="675" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Spam / Phishing
</text>
<text x="675" y="57" text-anchor="middle" fill="#fff" font-size="10">
(TensorFlow)
</text>


<rect x="610" y="90" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="675" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Tab Categorizer
</text>
<text x="675" y="127" text-anchor="middle" fill="#fff" font-size="10">
(ML Classifier)
</text>


<rect x="800" y="20" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="865" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
SMTP Relay
</text>
<text x="865" y="57" text-anchor="middle" fill="#fff" font-size="10">
(Outbound)
</text>


<rect x="800" y="90" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="865" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
MX Gateway
</text>
<text x="865" y="127" text-anchor="middle" fill="#fff" font-size="10">
(Inbound)
</text>


<rect x="420" y="290" width="130" height="60" rx="8" fill="#795548">
</rect>
<text x="485" y="315" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Bigtable
</text>
<text x="485" y="332" text-anchor="middle" fill="#fff" font-size="10">
(Mailbox + Index)
</text>


<rect x="610" y="290" width="130" height="60" rx="8" fill="#795548">
</rect>
<text x="675" y="315" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Google Cloud
</text>
<text x="675" y="332" text-anchor="middle" fill="#fff" font-size="10">
Storage (Blobs)
</text>


<rect x="800" y="290" width="130" height="60" rx="8" fill="#00897B">
</rect>
<text x="865" y="315" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Pub/Sub
</text>
<text x="865" y="332" text-anchor="middle" fill="#fff" font-size="10">
(Async Queues)
</text>


<rect x="220" y="400" width="140" height="50" rx="8" fill="#4285F4">
</rect>
<text x="290" y="425" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Push Notifier
</text>
<text x="290" y="440" text-anchor="middle" fill="#fff" font-size="10">
(FCM / APNs)
</text>


<rect x="420" y="400" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="485" y="425" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Memcached
</text>
<text x="485" y="440" text-anchor="middle" fill="#fff" font-size="10">
(Session/Labels)
</text>


<line x1="160" y1="55" x2="215" y2="100" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="160" y1="125" x2="215" y2="115" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="160" y1="195" x2="215" y2="140" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="360" y1="100" x2="415" y2="45" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="360" y1="115" x2="415" y2="115" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="360" y1="130" x2="415" y2="185" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="550" y1="45" x2="605" y2="45" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="550" y1="115" x2="605" y2="115" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="740" y1="45" x2="795" y2="45" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="740" y1="115" x2="795" y2="115" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="485" y1="210" x2="485" y2="285" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="520" y1="140" x2="610" y2="290" stroke="#4285F4" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="865" y1="140" x2="865" y2="285" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="485" y1="350" x2="485" y2="395" stroke="#4285F4" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="420" y1="350" x2="350" y2="400" stroke="#4285F4" stroke-width="1.5" marker-end="url(#ah4)">
</line>


</svg>

</details>


### Architecture Notes


- **Google Front End (GFE):** L7 load balancer handling TLS termination, DDoS mitigation, rate limiting — all Gmail traffic enters here

- **Bigtable:** Primary storage for mailbox data — row key: `user_id#timestamp#msg_id`; column families for headers, body, labels, flags

- **Per-User Index:** Co-located with mailbox in Bigtable — inverted index mapping terms → message IDs within a single user's data

- **Pub/Sub:** Decouples send/receive pipelines — delivery retries, webhook notifications, analytics all consume from shared topics

- **Colossus (GFS v2):** Underlying distributed filesystem for Bigtable and GCS — handles replication and erasure coding


## Database Schema


### Bigtable — Mailbox Table

`Row Key: {user_id}#{reverse_timestamp}#{message_id}
Column Families:
  headers:
    - from         (string)
    - to           (string[])
    - cc           (string[])
    - subject      (string)
    - date         (timestamp)
    - message_id   (string, RFC 5322 Message-ID)
    - in_reply_to  (string)
    - references   (string[])
  body:
    - text_plain   (bytes, compressed)
    - text_html    (bytes, compressed)
    - snippet      (string, first 200 chars)
  meta:
    - thread_id    (string)   `IDX`
    - labels       (string[], e.g., ["INBOX","UNREAD","IMPORTANT"])
    - category     (enum: primary|social|promotions|updates|forums)
    - is_read      (bool)
    - is_starred   (bool)
    - spam_score   (float)
  attachments:
    - att_0_name   (string)
    - att_0_mime   (string)
    - att_0_size   (int64)
    - att_0_blob   (string → GCS reference)`


 `SHARD` Sharded by user_id — each user's data co-located on same Bigtable tablet


 `PK` Reverse timestamp ensures newest messages sort first in scan


### Bigtable — Per-User Search Index

`Row Key: {user_id}#idx#{term}
Column Family:
  postings:
    - msg_ids     (sorted list of message_ids)
    - positions   (term positions for snippet generation)
    - doc_freq    (count of messages containing term)

Terms indexed:
  - Body tokens (stemmed, lowercased)
  - Header values (from:, to:, subject:)
  - Attachment filenames
  - Label names
  - Date ranges (bucketed by month)`


 `IDX` One inverted index per user — privacy isolated, no cross-user leakage


### Labels & Filters (Spanner)

`CREATE TABLE user_labels (
  user_id     STRING(36),    `PK`
  label_id    STRING(36),    `PK`
  name        STRING(256),
  color       STRING(7),
  type        STRING(20),   -- system | user
  visibility  STRING(20),   -- show | hide
  created_at  TIMESTAMP
) PRIMARY KEY (user_id, label_id);

CREATE TABLE user_filters (
  user_id     STRING(36),    `PK`
  filter_id   STRING(36),    `PK`
  criteria    JSON,         -- {from, to, subject, has_words, size_gt}
  actions     JSON,         -- {add_label, archive, delete, mark_read, forward}
  created_at  TIMESTAMP
) PRIMARY KEY (user_id, filter_id);`


## Cache & CDN Deep Dive


Session & Label Cache


**System:** Memcached (Google internal Memcache service)


**Cached:** User session, label list, unread counts, draft state, contact autocomplete


**Strategy:** Write-through for label changes; invalidate on new email delivery


**TTL:** 15 minutes for unread counts; session cache: tied to auth token expiry


Attachment CDN


**System:** Google's global edge network (same infra as YouTube/Drive)


**Caching:** Attachment thumbnails and previews cached at edge; full downloads proxied through GCS


**Policy:** Private cache-control (no public CDN caching for email content — privacy)


**Auth:** Signed URLs with 15-minute expiry for attachment download links


Inbox Prefetch


**Strategy:** First page of inbox (25 conversations) prefetched and cached on client


**Service Worker:** Web client uses service worker for offline inbox access


**Delta Sync:** Client sends last sync token → server returns only new/changed messages since


**Compression:** Protocol Buffers over gRPC — 60-80% smaller than JSON equivalent


DNS & Routing Cache


**MX Cache:** SMTP relay caches MX record lookups (TTL from DNS, min 5 minutes)


**TLS Cache:** SMTP TLS session tickets cached to avoid full handshake on repeated connections


**Reputation Cache:** Sender reputation scores cached per-domain (hourly refresh from scoring pipeline)


## Scaling & Load Balancing


- **Google Front End (GFE):** Anycast-based global load balancing — routes to nearest healthy datacenter; Maglev (kernel-bypass L4 LB) handles millions of connections per server

- **Bigtable Auto-Scaling:** Tablets auto-split when size exceeds threshold; hotspot detection moves heavy-usage tablets to dedicated nodes

- **SMTP Relay Scaling:** Horizontally scaled relay pool; per-destination-domain queues with configurable concurrency (avoid overwhelming small mail servers)

- **Multi-Region:** User data replicated across 3+ zones within a region (synchronous); cross-region async for disaster recovery (RPO < 1 minute)

- **Borg (Kubernetes predecessor):** All Gmail services run on Borg — auto-restart, bin-packing, priority-based scheduling

- **Batch Processing:** Spam model retraining, storage reclamation, and analytics run on Flume/MapReduce pipelines during off-peak


## Tradeoffs


> **

✅ Per-User Index


- Perfect privacy isolation — no cross-user data leakage possible

- Simpler ACL model (no per-document permission checks)

- Index size per user is small → fast queries


❌ Per-User Index


- Cannot do cross-user analytics or global search

- Index maintenance overhead — billions of small indexes vs. one large one

- Cold start for new users requires index build from scratch


✅ Bigtable (NoSQL)


- Handles massive write throughput (billions of emails/day)

- Flexible schema — easy to add new column families

- Horizontal scaling without application changes


❌ Bigtable (NoSQL)


- No SQL joins — application-level joins required for cross-entity queries

- Limited secondary indexing (must maintain manually)

- Eventual consistency for cross-region reads


## Alternative Approaches


Traditional RDBMS (PostgreSQL)


Could use sharded PostgreSQL (like Citus) for mailbox storage. Provides ACID transactions, rich querying, and built-in full-text search (tsvector). However, scaling to billions of users with billions of rows each is impractical without extreme sharding complexity.


Elasticsearch for Search


Dedicated Elasticsearch cluster per user partition instead of Bigtable-embedded index. Better full-text capabilities (BM25 ranking, fuzzy matching) but adds operational complexity, higher memory cost, and cross-cluster coordination overhead.


Event Sourcing Architecture


Store all mailbox mutations as immutable events (EmailReceived, LabelAdded, MessageDeleted). Rebuild current state from event log. Excellent for audit trails and undo, but read performance requires maintained projections (materialized views), adding complexity.


## Additional Information


- **Confidential Mode:** Time-expiring emails with revocable access — stored server-side, recipient gets a link (not full email body); SMS passcode for non-Gmail recipients

- **Google Workspace Vault:** Legal hold and eDiscovery — all emails retained in immutable audit store regardless of user deletion

- **AMP for Email:** Interactive emails (carousels, forms, real-time content) rendered via AMP runtime sandboxed within Gmail client

- **Smart Compose / Smart Reply:** On-device LSTM/Transformer models suggest completions; server-side models for Smart Reply (3 suggestions). Federated learning for personalization without sending raw email content to training pipeline.

- **Storage Quota:** 15GB shared across Gmail, Drive, Photos (free tier). Bigtable stores compressed — actual storage efficiency ~3-5x over raw size via Snappy compression + deduplication of repeated MIME parts.

- **Migration:** Gmail Import supports IMAP pull from external providers; Google Takeout exports all email as MBOX format for data portability (GDPR compliance).