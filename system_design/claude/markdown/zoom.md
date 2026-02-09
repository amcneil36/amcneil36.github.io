# System Design: Zoom (Video Conferencing)


## Functional Requirements


- Users can create and schedule video meetings with unique meeting IDs

- Users can join meetings via link or meeting ID + passcode

- Real-time video and audio streaming between participants (up to 1000 in a meeting)

- Screen sharing capability

- Real-time text chat within meetings

- Meeting recording and cloud storage

- Virtual waiting room and host controls (mute, remove, admit)


## Non-Functional Requirements


- **Low Latency:** <150ms end-to-end for audio/video (real-time requirement)

- **High Availability:** 99.99% uptime for signaling servers

- **Scalability:** Support millions of concurrent meetings, each up to 1000 participants

- **Quality Adaptation:** Adaptive bitrate based on network conditions

- **Security:** End-to-end encryption option, AES-256-GCM for media

- **Reliability:** Graceful degradation (audio priority over video on bad networks)


## Flow 1: Creating & Joining a Meeting (Signaling)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 500" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#00aaff">
</polygon>
</marker>
</defs>


<rect x="20" y="200" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="80" y="235" text-anchor="middle" fill="white" font-size="14">
Host Client
</text>


<rect x="200" y="200" width="140" height="60" rx="10" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="270" y="228" text-anchor="middle" fill="white" font-size="13">
API Gateway /
</text>
<text x="270" y="245" text-anchor="middle" fill="white" font-size="13">
Load Balancer
</text>


<rect x="400" y="120" width="140" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="470" y="155" text-anchor="middle" fill="white" font-size="13">
Meeting Service
</text>


<rect x="400" y="280" width="140" height="60" rx="10" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="470" y="308" text-anchor="middle" fill="white" font-size="12">
Signaling Service
</text>
<text x="470" y="325" text-anchor="middle" fill="white" font-size="11">
(WebSocket)
</text>


<rect x="620" y="120" width="130" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="685" y="150" text-anchor="middle" fill="white" font-size="13">
SFU Router
</text>


<path d="M 620 310 Q 685 310 685 190 Q 685 170 685 170" stroke="#00aaff" stroke-width="2" fill="none" marker-end="url(#arrow1)">
</path>


<ellipse cx="470" cy="430" rx="70" ry="30" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="470" y="435" text-anchor="middle" fill="white" font-size="12">
Meeting DB
</text>


<rect x="640" y="280" width="120" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="700" y="310" text-anchor="middle" fill="white" font-size="12">
TURN/STUN
</text>


<rect x="820" y="200" width="120" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="880" y="228" text-anchor="middle" fill="white" font-size="13">
Participant
</text>
<text x="880" y="245" text-anchor="middle" fill="white" font-size="13">
Clients
</text>


<rect x="820" y="80" width="140" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="890" y="110" text-anchor="middle" fill="white" font-size="12">
Media Server (SFU)
</text>


<line x1="140" y1="230" x2="198" y2="230" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="340" y1="215" x2="398" y2="155" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="340" y1="245" x2="398" y2="305" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="470" y1="180" x2="470" y2="398" stroke="#ff6b6b" stroke-width="1.5" stroke-dasharray="5,5" marker-end="url(#arrow1)">
</line>


<line x1="540" y1="310" x2="638" y2="305" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="750" y1="145" x2="818" y2="115" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="760" y1="305" x2="818" y2="240" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow1)">
</line>


<line x1="890" y1="130" x2="890" y2="198" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow1)">
</line>


<text x="550" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Zoom: Meeting Creation & Join Flow
</text>


</svg>

</details>


### Step-by-Step


1. **Host creates meeting** → POST to Meeting Service → generates unique meeting ID (11-digit numeric) + passcode → stores in Meeting DB

2. **Participant joins** → enters meeting ID/passcode → Signaling Service authenticates via WebSocket

3. **Signaling exchange** → SDP offer/answer + ICE candidates exchanged between client and SFU via Signaling Service

4. **NAT traversal** → STUN discovers public IP; if symmetric NAT, falls back to TURN relay

5. **SFU Router** assigns participant to optimal Media Server (SFU) based on geographic proximity and load

6. **Media flows** → Each client sends 1 stream to SFU, SFU forwards N-1 streams to each participant (Selective Forwarding Unit)


> **
**Example:** Alice creates meeting ID 123-456-7890 with passcode "abc123". She shares the link. Bob clicks the link → WebSocket connects to Signaling Service → SDP exchange → STUN resolves Bob's public IP → Bob connects to SFU in us-east-1 → SFU starts forwarding Alice's video to Bob and Bob's video to Alice. Latency: ~80ms (same region).


### Deep Dive: Signaling Service


- **Protocol:** WebSocket (WSS) for persistent bidirectional signaling

- **Purpose:** Exchange SDP offers/answers, ICE candidates, join/leave events, mute/unmute signals

- **Input:** `{ type: "join", meeting_id: "123-456-7890", sdp_offer: "..." }`

- **Output:** `{ type: "sdp_answer", sdp: "...", ice_candidates: [...] }`

- **Scale:** Stateless signaling servers behind L4 load balancer with sticky sessions (WebSocket affinity)


### Deep Dive: SFU (Selective Forwarding Unit)


- **Protocol:** WebRTC (SRTP over UDP for media, DTLS for key exchange)

- **Why SFU over Mesh:** Mesh = O(N²) connections; SFU = O(N). For 10 participants, mesh needs 90 streams vs SFU needs 10 upload + 10×9 download handled server-side

- **Simulcast:** Each client sends 3 quality layers (high/medium/low); SFU selects appropriate layer per receiver based on their bandwidth

- **Why not MCU:** MCU decodes + re-encodes (CPU-intensive, adds latency). SFU just forwards packets — lower latency, horizontally scalable


### Deep Dive: STUN/TURN


- **STUN (Session Traversal Utilities for NAT):** UDP-based, discovers public IP:port mapping. Works for ~80% of NAT types

- **TURN (Traversal Using Relays around NAT):** TCP/UDP relay for symmetric NAT cases (~20%). All media relayed through TURN server — adds latency but ensures connectivity

- **ICE (Interactive Connectivity Establishment):** Framework that tries STUN first, falls back to TURN


## Flow 2: Real-time Audio/Video Streaming


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 420" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#ffaa00">
</polygon>
</marker>
</defs>


<rect x="30" y="170" width="130" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="95" y="195" text-anchor="middle" fill="white" font-size="12">
Sender Client
</text>
<text x="95" y="212" text-anchor="middle" fill="white" font-size="11">
(Camera+Mic)
</text>


<rect x="220" y="50" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="290" y="80" text-anchor="middle" fill="white" font-size="12">
Codec Encoder
</text>


<text x="290" y="120" text-anchor="middle" fill="#aaa" font-size="11">
VP8/VP9/H.264 (video)
</text>


<text x="290" y="135" text-anchor="middle" fill="#aaa" font-size="11">
Opus (audio)
</text>


<rect x="220" y="170" width="140" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="290" y="195" text-anchor="middle" fill="white" font-size="12">
Jitter Buffer
</text>
<text x="290" y="212" text-anchor="middle" fill="white" font-size="11">
+ NACK/FEC
</text>


<rect x="220" y="290" width="140" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="290" y="320" text-anchor="middle" fill="white" font-size="12">
SRTP Packetizer
</text>


<rect x="450" y="170" width="140" height="60" rx="10" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="520" y="195" text-anchor="middle" fill="white" font-size="13">
SFU Media
</text>
<text x="520" y="212" text-anchor="middle" fill="white" font-size="13">
Server
</text>


<rect x="680" y="80" width="130" height="50" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="745" y="110" text-anchor="middle" fill="white" font-size="12">
Receiver A
</text>


<rect x="680" y="170" width="130" height="50" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="745" y="200" text-anchor="middle" fill="white" font-size="12">
Receiver B
</text>


<rect x="680" y="260" width="130" height="50" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="745" y="290" text-anchor="middle" fill="white" font-size="12">
Receiver C
</text>


<rect x="680" y="350" width="130" height="50" rx="10" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="745" y="375" text-anchor="middle" fill="white" font-size="11">
Recording Svc
</text>
<text x="745" y="390" text-anchor="middle" fill="white" font-size="10">
(if enabled)
</text>


<line x1="160" y1="185" x2="218" y2="80" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="160" y1="200" x2="218" y2="195" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="160" y1="215" x2="218" y2="310" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="360" y1="195" x2="448" y2="195" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="590" y1="180" x2="678" y2="105" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="590" y1="200" x2="678" y2="195" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="590" y1="220" x2="678" y2="280" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<line x1="590" y1="230" x2="678" y2="370" stroke="#ffaa00" stroke-width="2" marker-end="url(#arrow2)">
</line>


<text x="520" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Zoom: Real-time Media Streaming via SFU
</text>


</svg>

</details>


### Step-by-Step


1. **Capture:** Camera captures frames at 30fps, mic captures audio at 48kHz

2. **Encode:** VP9/H.264 encodes video with simulcast (3 spatial layers); Opus encodes audio

3. **Packetize:** RTP packets wrapped in SRTP (encrypted). Each packet ~1200 bytes (fits in MTU)

4. **Transmit (UDP):** SRTP packets sent over UDP to SFU. No TCP retransmission — speed over reliability

5. **SFU selects layer:** For each receiver, SFU picks appropriate simulcast layer based on receiver's bandwidth estimate

6. **Forward:** SFU forwards selected SRTP packets to each receiver (no transcoding)

7. **Receive + Decode:** Receiver's jitter buffer reorders packets, decoder renders frames

8. **Recording (optional):** SFU forks a copy of all streams to Recording Service → transcoded to MP4 → stored in S3


> **
**Example:** In a 5-person meeting, Alice sends her video at 3 quality layers (720p, 360p, 180p). SFU sends 720p to Bob (good WiFi), 360p to Charlie (4G), and 180p to Dave (poor connection). SFU only forwards — no CPU-intensive transcoding. Each participant uploads 1 stream, downloads 4 streams.


### Deep Dive: UDP vs TCP for Media


- **UDP chosen because:** No head-of-line blocking. If a packet is lost, the video continues with a brief artifact rather than stalling. For real-time video, a slightly corrupted frame is better than a delayed one

- **TCP would cause:** Retransmission delays (100-300ms RTT penalty per lost packet), bufferbloat, and stalls — unacceptable for real-time communication

- **Loss recovery:** NACK (Negative Acknowledgment) requests retransmission of critical packets (e.g., keyframes). FEC (Forward Error Correction) adds redundant packets so receiver can reconstruct lost data without retransmission


### Deep Dive: Simulcast + SVC


- **Simulcast:** Client encodes video at 3 resolutions simultaneously (e.g., 720p, 360p, 180p). SFU picks per-receiver

- **SVC (Scalable Video Coding):** Single encoded stream with temporal/spatial layers. SFU drops layers to reduce quality — more bandwidth efficient than simulcast but codec support is limited

- **Bandwidth estimation:** SFU uses REMB (Receiver Estimated Maximum Bitrate) and Transport-CC feedback to determine each receiver's available bandwidth in real-time


## Flow 3: Screen Sharing


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 300" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#66ff66">
</polygon>
</marker>
</defs>


<rect x="30" y="120" width="130" height="60" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="95" y="148" text-anchor="middle" fill="white" font-size="12">
Sharer Client
</text>
<text x="95" y="165" text-anchor="middle" fill="white" font-size="11">
(Screen Capture)
</text>


<rect x="220" y="120" width="140" height="60" rx="10" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="290" y="148" text-anchor="middle" fill="white" font-size="12">
VP9 Encoder
</text>
<text x="290" y="165" text-anchor="middle" fill="white" font-size="11">
(Content Mode)
</text>


<rect x="430" y="120" width="140" height="60" rx="10" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="500" y="155" text-anchor="middle" fill="white" font-size="13">
SFU
</text>


<rect x="640" y="60" width="130" height="50" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="705" y="90" text-anchor="middle" fill="white" font-size="12">
Viewer A
</text>


<rect x="640" y="150" width="130" height="50" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="705" y="180" text-anchor="middle" fill="white" font-size="12">
Viewer B
</text>


<rect x="640" y="240" width="130" height="50" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="705" y="270" text-anchor="middle" fill="white" font-size="12">
Viewer C
</text>


<line x1="160" y1="150" x2="218" y2="150" stroke="#66ff66" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="360" y1="150" x2="428" y2="150" stroke="#66ff66" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="570" y1="140" x2="638" y2="90" stroke="#66ff66" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="570" y1="150" x2="638" y2="175" stroke="#66ff66" stroke-width="2" marker-end="url(#arrow3)">
</line>


<line x1="570" y1="170" x2="638" y2="260" stroke="#66ff66" stroke-width="2" marker-end="url(#arrow3)">
</line>


<text x="450" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Zoom: Screen Sharing Flow
</text>


</svg>

</details>


- **Content mode encoding:** VP9 with high-resolution, lower frame rate (5-15 fps), optimized for text/slides (sharper than video mode). Uses `content_hint: "detail"`

- **Separate stream:** Screen share is a separate media track from camera — SFU treats it as an additional stream. Participants can pin or view gallery+screen simultaneously

- **Annotation:** Annotations are sent as overlay data via the signaling channel (DataChannel), not embedded in the video stream


> **
**Example:** Alice shares her screen while presenting slides. Encoder switches to content mode (high resolution 1080p, 10fps). VP9 is particularly efficient for screen content (large static areas compress well). Each viewer receives the high-res stream. Bob with slow connection gets a lower-resolution layer via simulcast.


## Combined Overall Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1200 650" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="arrow4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#00aaff">
</polygon>
</marker>
</defs>


<rect x="30" y="250" width="120" height="80" rx="10" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="90" y="283" text-anchor="middle" fill="white" font-size="12">
Client Apps
</text>
<text x="90" y="300" text-anchor="middle" fill="white" font-size="10">
(Desktop/Mobile/
</text>
<text x="90" y="315" text-anchor="middle" fill="white" font-size="10">
Web)
</text>


<rect x="200" y="200" width="130" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="265" y="230" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>


<rect x="200" y="310" width="130" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="265" y="335" text-anchor="middle" fill="white" font-size="11">
Signaling (WSS)
</text>
<text x="265" y="350" text-anchor="middle" fill="white" font-size="10">
SDP/ICE
</text>


<rect x="400" y="80" width="140" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="470" y="110" text-anchor="middle" fill="white" font-size="12">
Meeting Service
</text>


<rect x="400" y="170" width="140" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="470" y="200" text-anchor="middle" fill="white" font-size="12">
Auth Service
</text>


<rect x="400" y="260" width="140" height="50" rx="8" fill="#457b9d" stroke="#6db5d9" stroke-width="2">
</rect>
<text x="470" y="290" text-anchor="middle" fill="white" font-size="12">
Chat Service
</text>


<rect x="400" y="350" width="140" height="50" rx="8" fill="#8338ec" stroke="#b06efd" stroke-width="2">
</rect>
<text x="470" y="380" text-anchor="middle" fill="white" font-size="12">
SFU Router
</text>


<rect x="640" y="100" width="140" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="710" y="125" text-anchor="middle" fill="white" font-size="11">
Media Server (SFU)
</text>
<text x="710" y="140" text-anchor="middle" fill="white" font-size="10">
us-east-1
</text>


<rect x="640" y="190" width="140" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="710" y="215" text-anchor="middle" fill="white" font-size="11">
Media Server (SFU)
</text>
<text x="710" y="230" text-anchor="middle" fill="white" font-size="10">
eu-west-1
</text>


<rect x="640" y="280" width="140" height="50" rx="8" fill="#1e6091" stroke="#3a8cbf" stroke-width="2">
</rect>
<text x="710" y="305" text-anchor="middle" fill="white" font-size="11">
Media Server (SFU)
</text>
<text x="710" y="320" text-anchor="middle" fill="white" font-size="10">
ap-southeast-1
</text>


<rect x="640" y="380" width="130" height="50" rx="8" fill="#c77dba" stroke="#e0a5d8" stroke-width="2">
</rect>
<text x="705" y="410" text-anchor="middle" fill="white" font-size="12">
TURN/STUN
</text>


<rect x="880" y="100" width="140" height="50" rx="8" fill="#e76f51" stroke="#ff9a76" stroke-width="2">
</rect>
<text x="950" y="125" text-anchor="middle" fill="white" font-size="11">
Recording Service
</text>
<text x="950" y="140" text-anchor="middle" fill="white" font-size="10">
→ S3
</text>


<rect x="880" y="190" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="950" y="215" text-anchor="middle" fill="white" font-size="11">
Transcription Svc
</text>
<text x="950" y="230" text-anchor="middle" fill="white" font-size="10">
(AI/ML)
</text>


<ellipse cx="950" cy="320" rx="70" ry="30" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="950" y="325" text-anchor="middle" fill="white" font-size="12">
Meeting DB
</text>


<ellipse cx="950" cy="410" rx="70" ry="30" fill="#6b2fa0" stroke="#9d5fd0" stroke-width="2">
</ellipse>
<text x="950" y="415" text-anchor="middle" fill="white" font-size="12">
Chat DB
</text>


<rect x="400" y="470" width="140" height="50" rx="8" fill="#2a9d8f" stroke="#4ad4c0" stroke-width="2">
</rect>
<text x="470" y="500" text-anchor="middle" fill="white" font-size="12">
Waiting Room Svc
</text>


<rect x="640" y="470" width="140" height="50" rx="8" fill="#d4a017" stroke="#f0c040" stroke-width="2">
</rect>
<text x="710" y="500" text-anchor="middle" fill="white" font-size="12">
Notification Svc
</text>


<line x1="150" y1="275" x2="198" y2="230" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow4)">
</line>


<line x1="150" y1="305" x2="198" y2="335" stroke="#00aaff" stroke-width="2" marker-end="url(#arrow4)">
</line>


<line x1="330" y1="215" x2="398" y2="105" stroke="#00aaff" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="330" y1="225" x2="398" y2="195" stroke="#00aaff" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="330" y1="340" x2="398" y2="285" stroke="#00aaff" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="330" y1="345" x2="398" y2="375" stroke="#00aaff" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="540" y1="375" x2="638" y2="130" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="540" y1="375" x2="638" y2="215" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="540" y1="375" x2="638" y2="300" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="540" y1="385" x2="638" y2="405" stroke="#ffaa00" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="780" y1="120" x2="878" y2="120" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="780" y1="135" x2="878" y2="210" stroke="#ff6b6b" stroke-width="1.5" marker-end="url(#arrow4)">
</line>


<line x1="540" y1="105" x2="878" y2="310" stroke="#ff6b6b" stroke-width="1" stroke-dasharray="5,5" marker-end="url(#arrow4)">
</line>


<line x1="540" y1="285" x2="878" y2="400" stroke="#ff6b6b" stroke-width="1" stroke-dasharray="5,5" marker-end="url(#arrow4)">
</line>


<text x="600" y="25" text-anchor="middle" fill="#aaa" font-size="16" font-weight="bold">
Zoom: Combined Overall Architecture
</text>


</svg>

</details>


> **
**Example — Full meeting lifecycle:** Alice creates meeting → Meeting Service generates ID, stores in DB → Alice joins via WebSocket signaling → SFU Router assigns us-east-1 SFU → Bob (in Europe) joins → SFU Router assigns eu-west-1 SFU → Two SFUs cascade (forward streams between regions) → Charlie shares screen → separate screen share stream flows through SFU → Recording enabled → SFU forks all streams to Recording Service → After meeting, recording transcoded to MP4 → stored in S3 → Transcription Service generates captions from audio → Chat messages persisted to Chat DB.


## Database Schema


### SQL — PostgreSQL (Users, Accounts, Meeting Config)


| Table            | Column                             | Type                                | Details                 |
|------------------|------------------------------------|-------------------------------------|-------------------------|
| **users**        | user_id                            | BIGINT                              | `PK`                    |
| email            | VARCHAR(255)                       | `IDX`                               |                         |
| display_name     | VARCHAR(100)                       |                                     |                         |
| password_hash    | VARCHAR(255)                       |                                     |                         |
| plan_type        | ENUM(free,pro,business,enterprise) |                                     |                         |
| created_at       | TIMESTAMP                          |                                     |                         |
| **meetings**     | meeting_id                         | BIGINT                              | `PK` (11-digit numeric) |
| host_user_id     | BIGINT                             | `FK`  `IDX`                         |                         |
| title            | VARCHAR(255)                       |                                     |                         |
| passcode         | VARCHAR(10)                        |                                     |                         |
| scheduled_start  | TIMESTAMP                          | `IDX`                               |                         |
| duration_minutes | INT                                |                                     |                         |
| is_recurring     | BOOLEAN                            |                                     |                         |
| settings         | JSONB                              | waiting_room, e2ee, recording, etc. |                         |


Sharding Strategy (SQL)


- **users:** Shard by `user_id` (hash-based). Users are the primary access pattern for login/profile

- **meetings:** Shard by `meeting_id` (hash-based). Join operations look up by meeting_id, so this is the primary key

- Cross-shard query for "my meetings" uses `host_user_id` index → scatter-gather across shards (acceptable since it's a read-only dashboard query)


### NoSQL — Cassandra (Chat Messages, Participants, Recordings)


| Table                    | Column     | Type                       | Details |
|--------------------------|------------|----------------------------|---------|
| **chat_messages**        | meeting_id | BIGINT                     | `PK`    |
| message_id               | TIMEUUID   | `PK` (ASC)                 |         |
| sender_user_id           | BIGINT     |                            |         |
| content                  | TEXT       |                            |         |
| message_type             | TEXT       | text, file, reaction       |         |
| **meeting_participants** | meeting_id | BIGINT                     | `PK`    |
| user_id                  | BIGINT     | `PK`                       |         |
| join_time                | TIMESTAMP  |                            |         |
| leave_time               | TIMESTAMP  |                            |         |
| role                     | TEXT       | host, co-host, participant |         |
| **recordings**           | meeting_id | BIGINT                     | `PK`    |
| recording_id             | TIMEUUID   | `PK`                       |         |
| s3_url                   | TEXT       |                            |         |
| duration_seconds         | INT        |                            |         |
| status                   | TEXT       | processing, ready, expired |         |


Why Cassandra for Chat/Participants?


- Chat messages are write-heavy, append-only, and read by meeting_id (partition key = meeting_id ensures all messages for a meeting are co-located)

- meeting_participants is read when displaying participant list — bounded per meeting (<1000 rows per partition)

- Cassandra handles the write throughput of millions of concurrent meetings sending chat messages


### Denormalization


- **Active participant count** denormalized in Redis: `meeting:{meeting_id}:count` — INCR on join, DECR on leave. Avoids counting rows in Cassandra

- **Meeting status** (waiting, in-progress, ended) stored in both PostgreSQL (authoritative) and Redis (fast lookup for join flow)


### Indexes Explanation


- `meetings.host_user_id` — B-tree index for "list my meetings" dashboard query

- `meetings.scheduled_start` — B-tree index for upcoming meeting reminders and cleanup jobs

- `users.email` — Unique B-tree index for login lookups (email → user_id)


## Cache Deep Dive


### Redis Cache Layers


| Cache          | Key Pattern          | Value                                 | TTL                       | Strategy         |
|----------------|----------------------|---------------------------------------|---------------------------|------------------|
| Meeting State  | `meeting:{id}:state` | JSON (participants, settings, status) | Duration of meeting + 1hr | Write-through    |
| SFU Assignment | `meeting:{id}:sfu`   | SFU server address                    | Duration of meeting       | Write-through    |
| Auth Token     | `session:{token}`    | user_id, permissions                  | 24 hours                  | Write-through    |
| Active Count   | `meeting:{id}:count` | Integer                               | Duration of meeting       | Atomic INCR/DECR |


### Eviction Policy


- **Policy:** `volatile-ttl` — evict keys with nearest expiry first. Meeting caches naturally expire when meetings end

- **No CDN needed** for real-time media (it's live, not cacheable). CDN used only for static assets (JS/CSS, recording playback)


### CDN Usage


- **Static assets:** Web client JS bundle, mobile app assets served via CDN

- **Recording playback:** Completed recordings in S3 served via CloudFront CDN with signed URLs (time-limited access)

- **Strategy:** Pull-based CDN — first request pulls from S3 origin, subsequent requests served from edge cache

- **TTL:** 7 days for recordings, 30 days for static assets


## Scaling Considerations


- **Signaling servers:** Stateless, horizontally scalable behind L4 load balancer. WebSocket sessions use sticky sessions (IP hash)

- **SFU scaling:** Each SFU handles ~100-500 participants depending on hardware. For large meetings (>300), cascade multiple SFUs (SFU-to-SFU forwarding)

- **Geographic distribution:** SFU clusters in every major region. SFU Router picks closest cluster. For cross-region meetings, SFUs cascade with dedicated inter-DC links (lower latency than public internet)

- **Recording Service:** Scales independently. Uses Kafka queue — SFU publishes "record stream X" events, Recording workers consume and process. Can scale workers based on queue depth

- **Load balancing:** L4 (TCP/UDP) for media traffic to SFUs; L7 (HTTP) for REST APIs. Consistent hashing ensures meeting participants land on the same SFU

- **Auto-scaling:** SFU fleet auto-scales based on concurrent participant count. Pre-warms capacity before known peak times (9am Monday, school hours)


## Tradeoffs & Deep Dives


> **
✅ SFU Architecture
- O(N) bandwidth per participant (vs O(N²) mesh)
- No transcoding = low latency
- Simulcast enables per-receiver quality adaptation
- Horizontally scalable


❌ SFU Downsides
- Server bandwidth cost: each stream multiplied N times
- SFU is stateful (participant sessions) — harder to migrate
- Cascading SFUs for large meetings adds complexity


> **
✅ WebRTC + UDP
- Sub-100ms latency possible
- Built-in encryption (DTLS-SRTP)
- Adaptive bitrate via REMB/Transport-CC


❌ WebRTC Challenges
- NAT traversal complexity (need STUN/TURN infrastructure)
- UDP blocked on some corporate networks → need TCP fallback
- Browser implementation inconsistencies


### WebSocket Deep Dive (Signaling)


- **Why WebSocket for signaling:** Persistent connection for real-time SDP/ICE exchange. HTTP polling would add unacceptable latency to the join flow

- **Message types:** join, leave, offer, answer, ice-candidate, mute, unmute, screen-share-start, screen-share-stop, chat, reaction

- **Heartbeat:** Ping/pong every 10 seconds to detect disconnected clients


## Alternative Approaches


- **MCU (Multipoint Control Unit):** Server decodes all streams, composites into one mixed stream, re-encodes and sends to each participant. Pros: clients receive only 1 stream (low bandwidth). Cons: Very high server CPU, added latency (encode+decode), no individual video layout control. Used in older systems

- **Mesh (P2P):** Every participant connects directly to every other. Pros: No server infrastructure for media. Cons: O(N²) connections, doesn't scale beyond 4-5 participants, NAT traversal needed for every pair

- **HLS/DASH for large events:** For webinars with >10K viewers, switch to one-way HLS streaming (5-30s latency acceptable for view-only). Reduces SFU load dramatically

- **E2EE implementation:** Insertable Streams API (WebRTC) — encrypt media frames before they reach the SFU. SFU can still route packets (encrypted) but cannot see content. Tradeoff: server-side recording and transcription become impossible


## Additional Information


- **Breakout rooms:** Logically separate sub-meetings on the same SFU (or different SFU). Signaling server manages room-to-SFU mapping. Moving between rooms = SDP renegotiation

- **Virtual backgrounds:** Client-side ML model (MediaPipe / TensorFlow Lite) segments person from background at ~30fps. Processed before encoding — SFU sees normal video

- **Noise suppression:** Client-side RNNoise or proprietary ML model processes audio before encoding. Removes keyboard clicks, dog barks, etc.

- **End-to-end latency budget:** Capture (5ms) + Encode (10ms) + Network (30-80ms) + SFU routing (1ms) + Jitter buffer (20-40ms) + Decode (10ms) + Render (5ms) = ~80-150ms total

- **Bandwidth usage:** ~1.5 Mbps per 720p video stream. 10-person meeting: ~15 Mbps download for gallery view