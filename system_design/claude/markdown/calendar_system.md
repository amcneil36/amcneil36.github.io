# 📅 Design Calendar System


Collaborative calendar with event scheduling, free/busy lookup, recurring events, reminders, multi-timezone, CalDAV — Google Calendar scale (500M+ users)


## 1. Functional Requirements


- **Event CRUD:** Create, read, update, delete events with title, time, location, description, attendees, color, attachments

- **Recurring Events:** Daily, weekly, monthly, yearly recurrence rules (RFC 5545 RRULE) with exceptions and modifications

- **Attendees & RSVPs:** Invite attendees, track RSVP (accepted/declined/tentative/pending), proposing new times

- **Free/Busy Lookup:** Check availability of attendees before scheduling; find meeting times that work for all

- **Reminders & Notifications:** Email/push reminders at configurable intervals (10 min, 30 min, 1 hour before)

- **Multiple Calendars:** Personal, work, shared team calendars, holiday calendars, subscribed calendars

- **Timezone Support:** Events in any timezone; floating events (all-day) that follow local time

- **CalDAV/iCal Sync:** Standard protocol support for third-party client synchronization

- **Smart Scheduling:** AI-suggested meeting times based on preferences, working hours, travel time


## 2. Non-Functional Requirements


- **Scale:** 500M+ users, billions of events, 100K+ events/second write peak (Monday morning scheduling rush)

- **Latency:** Calendar view loading p50 <200ms, free/busy query <100ms, event creation <300ms

- **Availability:** 99.99%+ (calendar downtime = missed meetings = business impact)

- **Consistency:** Strong consistency for event writes (double-booking prevention); eventual consistency for read views across devices

- **Sync:** Cross-device sync within 5 seconds (mobile, web, desktop)

- **Data Integrity:** Never lose an event, never create phantom events from sync conflicts


## 3. Flow 1 — Event Creation & Invitation


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 400" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#4285F4">
</polygon>
</marker>
</defs>


<rect x="20" y="160" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="190" text-anchor="middle" fill="#fff" font-size="13">
Organizer
</text>


<rect x="180" y="160" width="120" height="60" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="240" y="190" text-anchor="middle" fill="#fff" font-size="13">
API Gateway
</text>


<rect x="350" y="60" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="415" y="90" text-anchor="middle" fill="#fff" font-size="13">
Event Service
</text>


<rect x="350" y="160" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="415" y="185" text-anchor="middle" fill="#fff" font-size="13">
Free/Busy
</text>
<text x="415" y="202" text-anchor="middle" fill="#fff" font-size="11">
Service
</text>


<rect x="350" y="270" width="130" height="60" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="415" y="295" text-anchor="middle" fill="#fff" font-size="13">
Notification
</text>
<text x="415" y="312" text-anchor="middle" fill="#fff" font-size="11">
Queue (Kafka)
</text>


<rect x="550" y="60" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="620" y="90" text-anchor="middle" fill="#fff" font-size="13">
Event Store
</text>
<text x="620" y="105" text-anchor="middle" fill="#fff" font-size="11">
(Spanner/MySQL)
</text>


<rect x="550" y="160" width="140" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="620" y="190" text-anchor="middle" fill="#fff" font-size="13">
Free/Busy Cache
</text>
<text x="620" y="205" text-anchor="middle" fill="#fff" font-size="11">
(Redis bitmap)
</text>


<rect x="550" y="270" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="620" y="295" text-anchor="middle" fill="#fff" font-size="13">
Email / Push
</text>
<text x="620" y="312" text-anchor="middle" fill="#fff" font-size="11">
Sender
</text>


<rect x="760" y="160" width="130" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="825" y="190" text-anchor="middle" fill="#fff" font-size="13">
Attendees
</text>
<text x="825" y="205" text-anchor="middle" fill="#fff" font-size="11">
(receive invite)
</text>


<line x1="130" y1="190" x2="178" y2="190" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="175" x2="348" y2="95" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="190" x2="348" y2="190" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="300" y1="205" x2="348" y2="295" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="480" y1="90" x2="548" y2="90" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="480" y1="190" x2="548" y2="190" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="480" y1="300" x2="548" y2="300" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="690" y1="300" x2="758" y2="200" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


</svg>

</details>


### Flow Steps


1. **Organizer creates event** → POST `/calendars/{calId}/events` with title, start/end time (ISO 8601 with timezone), attendees, recurrence rule, reminders

2. **Free/Busy check (optional):** Before creating, organizer may query `GET /freebusy?attendees=a,b,c&timeMin=...&timeMax=...` to find available slots

3. **Event Service:** Validates event (time range, permissions, max attendees). Generates event_id (UUID). Handles timezone conversion to UTC for storage.

4. **Event Store:** Persist event in primary database. For recurring events, store the RRULE (not expanded instances). Write event to each attendee's calendar (cross-calendar reference).

5. **Free/Busy Cache update:** Update Redis bitmap for organizer's free/busy status for the event's time range

6. **Notification Queue:** Enqueue invitation emails to all attendees. Each attendee receives email with accept/decline/tentative buttons (one-click RSVP links)

7. **Sync:** Push notification to organizer's other devices (web, mobile) via WebSocket/SSE to update calendar view in real-time

8. **Attendee RSVP:** Attendee clicks "Accept" → updates their participation status, updates organizer's view, updates attendee's free/busy


### Example


**Creating a recurring team standup:**

{
  "summary": "Engineering Standup",
  "start": {"dateTime": "2024-01-15T09:00:00", "timeZone": "America/Los_Angeles"},
  "end": {"dateTime": "2024-01-15T09:15:00", "timeZone": "America/Los_Angeles"},
  "recurrence": ["RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR;UNTIL=20241231"],
  "attendees": [
    {"email": "alice@company.com"},
    {"email": "bob@company.com"},
    {"email": "carol@company.com"}
  ],
  "reminders": {"overrides": [{"method": "popup", "minutes": 5}]},
  "conferenceData": {"createRequest": {"requestId": "abc123"}},  // auto-create Meet link
  "location": "Conference Room Maple"
}


**Storage:** One event record with RRULE stored. When user views February calendar, instances are expanded on-the-fly: Feb 5,6,7,8,9 (Mon-Fri) at 9:00 AM PST.


**Exception:** If organizer moves the Wednesday Feb 7 instance to 10:00 AM → exception record stored: `{recurringEventId: "evt123", originalStartTime: "2024-02-07T09:00", start: "2024-02-07T10:00"}`


### Deep Dives


Recurring Events (RFC 5545 RRULE)


Recurring events are stored as a single record with an RRULE string: `RRULE:FREQ=WEEKLY;BYDAY=MO,WE,FR;COUNT=52`. On calendar view, instances are **expanded on-the-fly** for the requested time range. This avoids storing millions of individual event instances (a weekly meeting for 5 years = 260 instances). **Exceptions**: Individual instance modifications (moved, cancelled, different attendees) stored as separate "exception" records linked to the parent. **EXDATE**: specific dates excluded from recurrence. The expansion logic follows the iCalendar RFC 5545 specification. Libraries like `rrule.js` or `python-dateutil` handle expansion. Edge cases: timezone changes (DST transitions), BYSETPOS, INTERVAL, combinations of BYDAY+BYMONTHDAY.


Free/Busy Bitmap (Redis)


For fast availability checking, maintain a **bitmap** in Redis per user. Each bit represents a 15-minute slot. One week = 672 bits = 84 bytes per user. `SETBIT freebusy:alice 168 1` (slot 168 = Monday 6pm, 1=busy). Free/busy query for 5 attendees: AND/OR bitmap operations across 5 keys → O(1) per slot. Total for 500M users × 1 year: ~500M × 84 bytes × 52 weeks ≈ 2.2TB in Redis cluster. Alternative: store only next 30 days in Redis, query database for older ranges. The bitmap is updated on every event create/update/delete and RSVP change.


Conflict Detection


When creating an event, check if it conflicts with existing events. Query: all events for this user where `start < new_event.end AND end > new_event.start` (overlapping range query). For recurring events, must expand instances in the conflicting range. Google Calendar shows conflicts visually but allows double-booking (it's informational, not blocking). Outlook can be configured to block double-booking. For room resources (conference rooms), conflicts ARE blocking — uses a **pessimistic lock**: first reservation wins, subsequent attempts rejected.


CalDAV/iCal Protocol


**CalDAV** (RFC 4791): WebDAV extension for calendar access. Uses HTTP methods: PUT (create/update event as .ics file), DELETE, REPORT (query events in time range), PROPFIND (list calendars). Events stored in **iCalendar (.ics)** format (RFC 5545). Sync via: `REPORT` with `calendar-query` filter. Change detection via ctag (collection tag) — if ctag changed, client re-syncs. Google Calendar supports CalDAV for third-party clients (Apple Calendar, Thunderbird). The iCalendar format encodes: VEVENT (events), VTODO (tasks), VFREEBUSY, VALARM (reminders), RRULE (recurrence).


## 4. Flow 2 — Calendar View Rendering (Week View)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 300" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#0F9D58">
</polygon>
</marker>
</defs>


<rect x="20" y="120" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="150" text-anchor="middle" fill="#fff" font-size="13">
User
</text>
<text x="75" y="165" text-anchor="middle" fill="#fff" font-size="11">
opens week view
</text>


<rect x="180" y="120" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="245" y="150" text-anchor="middle" fill="#fff" font-size="13">
Calendar Service
</text>


<rect x="370" y="30" width="140" height="60" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="440" y="60" text-anchor="middle" fill="#fff" font-size="13">
Event Store
</text>
<text x="440" y="75" text-anchor="middle" fill="#fff" font-size="11">
(single events)
</text>


<rect x="370" y="120" width="140" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="440" y="150" text-anchor="middle" fill="#fff" font-size="13">
RRULE Expander
</text>
<text x="440" y="165" text-anchor="middle" fill="#fff" font-size="11">
(recurring → instances)
</text>


<rect x="370" y="210" width="140" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="440" y="240" text-anchor="middle" fill="#fff" font-size="13">
View Cache
</text>
<text x="440" y="255" text-anchor="middle" fill="#fff" font-size="11">
(per-user, per-week)
</text>


<rect x="580" y="120" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="645" y="150" text-anchor="middle" fill="#fff" font-size="13">
TZ Converter
</text>
<text x="645" y="165" text-anchor="middle" fill="#fff" font-size="11">
(UTC → local)
</text>


<rect x="780" y="120" width="120" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="840" y="150" text-anchor="middle" fill="#fff" font-size="13">
Week View
</text>
<text x="840" y="165" text-anchor="middle" fill="#fff" font-size="11">
(rendered)
</text>


<line x1="130" y1="150" x2="178" y2="150" stroke="#0F9D58" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="310" y1="135" x2="368" y2="65" stroke="#0F9D58" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="310" y1="150" x2="368" y2="150" stroke="#0F9D58" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="310" y1="165" x2="368" y2="240" stroke="#0F9D58" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="510" y1="150" x2="578" y2="150" stroke="#0F9D58" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="710" y1="150" x2="778" y2="150" stroke="#0F9D58" stroke-width="2" marker-end="url(#a2)">
</line>


</svg>

</details>


### Flow Steps


1. **User opens week view** for Jan 15-21, 2024 → GET `/calendars/{calId}/events?timeMin=2024-01-15T00:00Z&timeMax=2024-01-22T00:00Z`

2. **Cache check:** Look up pre-computed view for this user + week in Redis. Cache HIT → return immediately.

3. **Cache MISS → Event Store query:** Fetch all single (non-recurring) events in the time range for all user's visible calendars

4. **RRULE Expansion:** For each recurring event whose RRULE potentially generates instances in this week, expand to concrete instances. Apply EXDATE exclusions and exception modifications.

5. **Merge:** Combine single events + expanded recurring instances + shared calendar events + subscribed calendar events

6. **Timezone conversion:** Convert all event times from UTC (storage) to user's display timezone

7. **Cache store:** Cache the assembled week view with short TTL (5 min). Invalidated on any event change.

8. **Return:** JSON array of events with computed fields (conflict indicators, RSVP status, conference links)


### Example


**User: Alice (timezone: America/Los_Angeles), viewing Jan 15-21, 2024**


**Events fetched from DB:** 3 single events + 2 recurring event records


**RRULE expansion:**

Recurring #1: "Engineering Standup" RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR
  → Expands to: Mon 9am, Tue 9am, Wed 9am, Thu 9am, Fri 9am (5 instances)
  → Exception: Wed moved to 10am (exception record applied)

Recurring #2: "Biweekly 1:1 with Manager" RRULE:FREQ=WEEKLY;INTERVAL=2;BYDAY=TH
  → This week IS an occurrence week → Thu 2pm (1 instance)
  → Next week would be skipped (INTERVAL=2)


**Total events rendered:** 3 single + 5 standup instances + 1 biweekly = 9 events in week view


**Conflict detected:** Thursday 2pm biweekly 1:1 overlaps with a single event "Dentist 2-3pm" → shown with visual conflict indicator


### Deep Dives


Timezone Handling


Calendar systems must handle timezones meticulously. Events stored in **UTC** in the database. Each event has an associated timezone (e.g., "America/Los_Angeles"). On display, converted to viewer's timezone. **All-day events** are special: stored as "floating" (no timezone) — they display on the same calendar date regardless of viewer's timezone. **DST transitions:** A 9 AM meeting in PST becomes 9 AM PDT after spring forward — the UTC time shifts. IANA timezone database (tzdata) must be kept updated. **Timezone changes:** If a timezone's rules change (government decision), events created before the change must be re-evaluated.


Sync Protocol (Push + Pull)


Calendar sync across devices uses: (1) **Push:** When event changes, server sends push notification / WebSocket event to all of user's connected clients. Client applies delta update to local cache. (2) **Pull (sync token):** Client sends `GET /events?syncToken=xyz` → server returns only events changed since that token. Sync token is an opaque cursor into the event changelog. (3) **Full sync:** If sync token is expired or first sync → full event list for a time range. Google Calendar uses **incremental sync** with sync tokens (nextSyncToken/nextPageToken). The changelog is maintained as an append-only log of event mutations per user.


## 5. Flow 3 — Smart Scheduling (Find a Meeting Time)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 950 300" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#F4B400">
</polygon>
</marker>
</defs>


<rect x="20" y="120" width="110" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="75" y="150" text-anchor="middle" fill="#fff" font-size="13">
Organizer
</text>
<text x="75" y="165" text-anchor="middle" fill="#fff" font-size="11">
find time for 5
</text>


<rect x="180" y="120" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="245" y="150" text-anchor="middle" fill="#fff" font-size="13">
Scheduling
</text>
<text x="245" y="165" text-anchor="middle" fill="#fff" font-size="13">
Engine
</text>


<rect x="370" y="30" width="130" height="60" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="435" y="60" text-anchor="middle" fill="#fff" font-size="13">
Free/Busy
</text>
<text x="435" y="75" text-anchor="middle" fill="#fff" font-size="11">
Aggregator
</text>


<rect x="370" y="120" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="435" y="150" text-anchor="middle" fill="#fff" font-size="13">
Working Hours
</text>
<text x="435" y="165" text-anchor="middle" fill="#fff" font-size="11">
Filter
</text>


<rect x="370" y="210" width="130" height="60" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="435" y="240" text-anchor="middle" fill="#fff" font-size="13">
Preference
</text>
<text x="435" y="255" text-anchor="middle" fill="#fff" font-size="11">
Ranker (ML)
</text>


<rect x="570" y="120" width="140" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="640" y="150" text-anchor="middle" fill="#fff" font-size="13">
Suggested Slots
</text>
<text x="640" y="165" text-anchor="middle" fill="#fff" font-size="11">
(ranked options)
</text>


<line x1="130" y1="150" x2="178" y2="150" stroke="#F4B400" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="310" y1="135" x2="368" y2="65" stroke="#F4B400" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="310" y1="150" x2="368" y2="150" stroke="#F4B400" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="310" y1="165" x2="368" y2="235" stroke="#F4B400" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="500" y1="65" x2="568" y2="140" stroke="#F4B400" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="500" y1="150" x2="568" y2="150" stroke="#F4B400" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="500" y1="235" x2="568" y2="165" stroke="#F4B400" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


### Flow Steps


1. **Organizer requests:** "Find 30-minute slot for Alice, Bob, Carol, Dave next week"

2. **Free/Busy Aggregator:** Fetch free/busy bitmaps for all 4 attendees + organizer for the next week (7 days × 96 slots/day = 672 slots per person)

3. **Bitmap intersection:** AND all 5 bitmaps (inverted — free slots) → result bitmap shows slots where ALL attendees are free

4. **Working Hours Filter:** Remove slots outside each attendee's configured working hours and timezone. Alice: 9-5 PST, Bob: 9-5 EST, Carol: 9-5 CET → overlap window: 9am-12pm PST (6pm-9pm CET)

5. **Preference Ranker:** Among available slots, rank by: morning vs afternoon preference, buffer time between meetings (avoid back-to-back), minimize fragmentation (prefer slots adjacent to existing meetings), meeting room availability

6. **Return:** Top 5 suggested time slots, ranked by preference score. Organizer selects one and creates the event.


### Example


**5 attendees across 3 timezones:** Alice (PST), Bob (EST), Carol (CET), Dave (PST), Organizer (PST)


**Working hours overlap:** 9:00-12:00 PST = 12:00-15:00 EST = 18:00-21:00 CET


**Free slots after bitmap intersection:** Mon 9:00, Mon 10:30, Tue 9:00, Wed 11:00, Thu 9:30, Fri 10:00


**Ranked suggestions:**

1. Tuesday 9:00-9:30 AM PST  (score: 0.95 — morning, all prefer AM, room available)
2. Thursday 9:30-10:00 AM PST (score: 0.88 — good buffer after Alice's 9am standup)
3. Monday 10:30-11:00 AM PST  (score: 0.82 — Monday mornings often have conflicts)
4. Wednesday 11:00-11:30 AM PST (score: 0.75 — close to lunch for CET attendees)
5. Friday 10:00-10:30 AM PST  (score: 0.60 — Friday meetings often cancelled)


## 6. Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 500" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="a4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#4285F4">
</polygon>
</marker>
</defs>


<rect x="10" y="210" width="100" height="60" rx="8" fill="#2e7d32" stroke="#4caf50" stroke-width="2">
</rect>
<text x="60" y="240" text-anchor="middle" fill="#fff" font-size="12">
Clients
</text>
<text x="60" y="255" text-anchor="middle" fill="#fff" font-size="10">
Web/iOS/Android
</text>


<rect x="150" y="150" width="110" height="50" rx="8" fill="#546e7a" stroke="#90a4ae" stroke-width="2">
</rect>
<text x="205" y="180" text-anchor="middle" fill="#fff" font-size="12">
CDN
</text>


<rect x="150" y="215" width="110" height="50" rx="8" fill="#e65100" stroke="#ff9800" stroke-width="2">
</rect>
<text x="205" y="245" text-anchor="middle" fill="#fff" font-size="12">
API Gateway
</text>


<rect x="150" y="280" width="110" height="50" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="205" y="310" text-anchor="middle" fill="#fff" font-size="12">
CalDAV Server
</text>


<rect x="310" y="60" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="86" text-anchor="middle" fill="#fff" font-size="11">
Event Service
</text>


<rect x="310" y="115" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="141" text-anchor="middle" fill="#fff" font-size="11">
Calendar Service
</text>


<rect x="310" y="170" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="196" text-anchor="middle" fill="#fff" font-size="11">
Free/Busy
</text>


<rect x="310" y="225" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="251" text-anchor="middle" fill="#fff" font-size="11">
Scheduling Engine
</text>


<rect x="310" y="280" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="306" text-anchor="middle" fill="#fff" font-size="11">
Reminder Service
</text>


<rect x="310" y="335" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="361" text-anchor="middle" fill="#fff" font-size="11">
Notification Svc
</text>


<rect x="310" y="390" width="120" height="42" rx="8" fill="#1565c0" stroke="#42a5f5" stroke-width="2">
</rect>
<text x="370" y="416" text-anchor="middle" fill="#fff" font-size="11">
Sync Service
</text>


<rect x="490" y="115" width="100" height="42" rx="8" fill="#6a1b9a" stroke="#ab47bc" stroke-width="2">
</rect>
<text x="540" y="141" text-anchor="middle" fill="#fff" font-size="11">
Kafka
</text>


<rect x="650" y="60" width="120" height="42" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="710" y="86" text-anchor="middle" fill="#fff" font-size="11">
Spanner/MySQL
</text>


<rect x="650" y="115" width="120" height="42" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="710" y="141" text-anchor="middle" fill="#fff" font-size="11">
Redis (F/B cache)
</text>


<rect x="650" y="170" width="120" height="42" rx="8" fill="#4e342e" stroke="#8d6e63" stroke-width="2">
</rect>
<text x="710" y="196" text-anchor="middle" fill="#fff" font-size="11">
Changelog (log)
</text>


<rect x="650" y="225" width="120" height="42" rx="8" fill="#00838f" stroke="#26c6da" stroke-width="2">
</rect>
<text x="710" y="251" text-anchor="middle" fill="#fff" font-size="11">
Memcached
</text>


<line x1="110" y1="230" x2="148" y2="175" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="110" y1="240" x2="148" y2="240" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="110" y1="250" x2="148" y2="305" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="260" y1="240" x2="308" y2="86" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="86" x2="648" y2="80" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="140" x2="488" y2="140" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="590" y1="135" x2="648" y2="135" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


<line x1="430" y1="196" x2="648" y2="140" stroke="#4285F4" stroke-width="2" marker-end="url(#a4)">
</line>


</svg>

</details>


## 7. Database Schema


### Events (Spanner / MySQL — sharded by calendar_id)


Table: events   `SHARD`
- event_id: varchar(64)         `PK` (UUID)
- calendar_id: varchar(64)      `FK`  `IDX`
- organizer_id: varchar(64)     `FK`
- summary: varchar(500)
- description: text
- location: varchar(500)
- start_time: timestamp         `IDX`
- end_time: timestamp           `IDX`
- start_timezone: varchar(50)  // IANA timezone (e.g., "America/Los_Angeles")
- end_timezone: varchar(50)
- all_day: boolean
- status: enum('confirmed','tentative','cancelled')
- visibility: enum('default','public','private','confidential')
- recurrence: text             // RRULE string (NULL for single events)
- recurring_event_id: varchar(64)  // parent ID for exception instances
- original_start_time: timestamp   // for exception: original scheduled time
- conference_data: jsonb       // video conf link
- color_id: varchar(10)
- created_at: timestamp
- updated_at: timestamp
- sequence: int                // version counter (iCal SEQUENCE)
- etag: varchar(64)           // for sync conflict detection


### Attendees (many-to-many)


Table: event_attendees
- event_id: varchar(64)         `FK`  `IDX`
- attendee_email: varchar(255)  `IDX`
- attendee_id: varchar(64)      `FK`
- response_status: enum('needsAction','declined','tentative','accepted')
- is_organizer: boolean
- is_optional: boolean
- comment: varchar(500)
- responded_at: timestamp
Composite PK: (event_id, attendee_email)


### Calendars


Table: calendars
- calendar_id: varchar(64)      `PK`
- owner_id: varchar(64)         `FK`  `IDX`
- summary: varchar(255)        // "Work", "Personal", "Team Calendar"
- description: text
- timezone: varchar(50)
- color: varchar(7)           // hex color
- access_role: enum('owner','writer','reader','freeBusyReader')
- selected: boolean            // visible in UI by default
- ctag: varchar(64)           // CalDAV collection tag (changes on any event mutation)
- created_at: timestamp


### Reminders


Table: reminders
- reminder_id: varchar(64)      `PK`
- event_id: varchar(64)         `FK`  `IDX`
- user_id: varchar(64)          `FK`
- method: enum('email','popup','sms')
- minutes_before: int          // 5, 10, 30, 60, 1440 (1 day)
- trigger_time: timestamp       `IDX` // pre-computed: event.start - minutes_before
- sent: boolean


### Event Changelog (for sync)


Table: event_changelog   `SHARD`
- changelog_id: bigint auto_increment   `PK`
- calendar_id: varchar(64)      `IDX`
- event_id: varchar(64)
- mutation_type: enum('created','updated','deleted')
- mutated_at: timestamp         `IDX`
- sync_token: varchar(64)      `IDX` // opaque cursor for incremental sync


## 8. Cache & CDN Deep Dive


| Layer            | Technology              | What's Cached                                             | TTL          | Invalidation                                        |
|------------------|-------------------------|-----------------------------------------------------------|--------------|-----------------------------------------------------|
| Free/Busy Bitmap | Redis                   | Per-user busy slots (15-min granularity)                  | Real-time    | Updated on every event create/update/delete/RSVP    |
| Calendar View    | Memcached               | Pre-assembled week/month view per user                    | 5 min        | Invalidated on any event change in user's calendars |
| Event Cache      | Memcached               | Individual event details (hot events with many attendees) | 10 min       | Write-through on update                             |
| RRULE Expansion  | In-memory (per-request) | Expanded recurring event instances for a time range       | Per-request  | N/A (computed on demand)                            |
| Static Assets    | CDN                     | JS/CSS bundles, icons                                     | 1yr (hashed) | Deploy-time versioning                              |


### Reminder Scheduling


Reminders are time-sensitive and must fire precisely. Architecture: a **Reminder Scheduler** service periodically scans the reminders table for upcoming triggers (next 5 minutes). Uses a **delay queue** pattern: reminders loaded into a priority queue (min-heap by trigger_time). At trigger time, send email or push notification. For reliability: idempotent delivery (dedup by reminder_id), at-least-once semantics with dedup. Scale: 500M users × average 3 events/day × 1 reminder each = 1.5B reminders/day ≈ 17K reminders/second. Handled by a sharded reminder service with Redis sorted sets as the time-based index.


## 9. Scaling Considerations


### Monday Morning Peak


- Peak scheduling happens Monday 8-10 AM across timezones (as the wave moves west)

- Write spike: 100K+ events/second during peak (meetings being created for the week)

- Read spike: Calendar view loads as everyone checks their schedule

- Auto-scaling with pre-warming for known peak patterns


### Google Spanner for Calendar


Google Calendar uses Spanner (globally distributed, strongly consistent database). Enables: strong consistency for event writes (no double-booking of rooms), global distribution (user in Tokyo sees same data as user in NYC), automatic sharding and rebalancing. TrueTime API provides globally consistent timestamps. Alternative for non-Google: CockroachDB or Vitess-sharded MySQL.


### Load Balancing


- **DNS:** Anycast / GeoDNS for nearest region

- **L7:** Path-based routing (CalDAV vs REST API vs WebSocket)

- **Database:** Read replicas for calendar view queries; primary for writes

- **Reminder service:** Sharded by user_id hash — each shard handles reminders for a subset of users


## 10. Tradeoffs


> **

> **
✅ RRULE Storage (Compact)


Store one record for an infinite recurring event. "Every Monday forever" = 1 row, not 52/year × infinite years. Compact, easy to modify the pattern. Standard format (RFC 5545).


> **
❌ RRULE Expansion Complexity


Must expand on every read. Complex edge cases: DST transitions, timezone changes, BYSETPOS+BYDAY combinations. Exception management (individual instance edits) adds significant complexity. Performance: expanding "every day for 10 years" requires iterating 3650 dates.


> **
✅ Free/Busy Bitmap (Fast Scheduling)


O(1) availability check per slot. Bitmap AND/OR across multiple attendees is extremely fast. 672 bits per user per week = minimal memory. Enables sub-100ms scheduling suggestions.


> **
❌ Bitmap Maintenance


Must be kept perfectly in sync with actual events. Any event create/update/delete/RSVP must update bitmap. Recurring event expansion must be reflected. Bitmap doesn't store event details (just busy/free) — follow-up query needed for conflict details.


> **
✅ CalDAV Standard Protocol


Enables interoperability with any CalDAV client (Apple Calendar, Thunderbird, DAVx5). Users aren't locked into one client. Enterprise environments require standards compliance.


> **
❌ CalDAV Limitations


CalDAV is complex and poorly implemented by many clients. Some features (smart scheduling, video conferencing) require proprietary API extensions beyond CalDAV. Sync efficiency is lower than proprietary push-based protocols.


## 11. Alternative Approaches


Pre-Expanded Recurring Events


Instead of RRULE, expand and store every instance as a separate event (e.g., 52 rows for weekly meeting). Simpler read path (no expansion needed). But: "change all future instances" requires updating 100s of rows. Infinite recurrence impossible. Higher storage cost. Used by some simpler calendar implementations.


Event Sourcing for Calendar


Store all mutations as immutable events (created, moved, attendee_added, cancelled). Current state derived by replaying events. Benefits: complete audit trail, easy undo, conflict resolution. Used by Calendly internally. Downside: read path requires projection from event stream → need materialized views for performance.


CRDT-Based Calendar Sync


Use CRDTs for offline-first calendar editing (mobile with poor connectivity). Changes merge automatically without conflicts. Google has researched this for Docs; could apply to Calendar. Challenge: calendar events have time-based semantics that CRDTs don't naturally handle (e.g., "move meeting to 3pm" vs "move meeting to 4pm" — which wins?).


Microsoft Exchange / Graph API


Alternative architecture: Exchange-based (Outlook Calendar). Uses MAPI/EWS protocols (proprietary). Graph API for modern access. Room booking via Exchange resource mailboxes. Tighter integration with email (meeting invites as email). Enterprise-dominant but less open than CalDAV.


## 12. Additional Information


### Key Standards


- **RFC 5545 (iCalendar):** Data format for calendar events (.ics files). Defines VEVENT, RRULE, VTIMEZONE, VALARM

- **RFC 4791 (CalDAV):** Protocol for calendar access over WebDAV/HTTP

- **RFC 6638 (Scheduling):** CalDAV extension for invitations and free/busy

- **RFC 7265 (jCal):** JSON representation of iCalendar data

- **IANA Timezone Database (tzdata):** Authoritative timezone definitions, updated ~4x/year


### Key Numbers (Google Calendar scale)


| Users                            | 500M+  |
|----------------------------------|--------|
| Events per day (created)         | ~500M  |
| Calendar view loads per day      | ~2B    |
| Reminders fired per day          | ~1.5B  |
| Free/busy queries per day        | ~200M  |
| Average events per user per week | ~15-25 |