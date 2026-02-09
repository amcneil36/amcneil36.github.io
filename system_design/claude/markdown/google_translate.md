# 🌐 Google Translate — Machine Translation System


Design a system that translates text, documents, and speech across 130+ languages using neural machine translation, supporting 1B+ daily translations with low latency.


## 📋 Functional Requirements


- **Text translation** — translate text between 130+ language pairs with context-aware quality

- **Language detection** — auto-detect source language from input text

- **Document translation** — translate PDF, DOCX, PPTX while preserving formatting

- **Speech translation** — speech-to-text → translate → text-to-speech pipeline

- **Camera/image translation** — OCR on camera input, translate overlaid text in real-time

- **Conversation mode** — real-time bidirectional speech translation

- **Offline translation** — download language packs for offline use on mobile


## 📋 Non-Functional Requirements


- **Latency** — <200ms for text translation, <500ms for speech-to-text per utterance

- **Throughput** — 1B+ translations/day, 100K+ QPS at peak

- **Quality** — BLEU score >40 for major language pairs, continuous improvement via user feedback

- **Availability** — 99.99% uptime globally

- **Coverage** — 130+ languages including low-resource languages

- **Cost efficiency** — optimize GPU/TPU inference cost per translation


## 🔄 Flow 1: Text Translation (Neural Machine Translation)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 300" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#4285F4">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="90" text-anchor="middle" fill="white" font-size="13">
Client
</text>


<rect x="175" y="60" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="240" y="90" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>


<rect x="350" y="30" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="415" y="60" text-anchor="middle" fill="white" font-size="12">
Language Detect
</text>


<rect x="350" y="100" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="415" y="120" text-anchor="middle" fill="white" font-size="12">
Translation Cache
</text>
<text x="415" y="135" text-anchor="middle" fill="white" font-size="10">
(Redis)
</text>


<rect x="530" y="60" width="140" height="50" rx="8" fill="#1565C0">
</rect>
<text x="600" y="80" text-anchor="middle" fill="white" font-size="12">
NMT Model Server
</text>
<text x="600" y="95" text-anchor="middle" fill="white" font-size="10">
(TPU/GPU)
</text>


<rect x="720" y="30" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="785" y="60" text-anchor="middle" fill="white" font-size="12">
Model Registry
</text>


<rect x="720" y="100" width="130" height="50" rx="8" fill="#00695C">
</rect>
<text x="785" y="130" text-anchor="middle" fill="white" font-size="12">
Post-Processing
</text>


<rect x="530" y="200" width="140" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="600" y="230" text-anchor="middle" fill="white" font-size="12">
Feedback Logger
</text>


<line x1="130" y1="85" x2="170" y2="85" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="305" y1="75" x2="345" y2="55" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="305" y1="95" x2="345" y2="125" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="480" y1="125" x2="525" y2="95" stroke="#4285F4" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="670" y1="85" x2="715" y2="55" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="670" y1="95" x2="715" y2="125" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="600" y1="110" x2="600" y2="195" stroke="#FF9800" stroke-width="2" marker-end="url(#a1)">
</line>


<text x="415" y="170" fill="#aaa" font-size="10">
cache miss
</text>


<text x="620" y="160" fill="#aaa" font-size="10">
log for training
</text>


</svg>

</details>


**Step 1:** Client sends text with optional source/target language. API Gateway authenticates, rate-limits, routes
**Step 2:** Language Detection runs if source language not specified (fastText classifier, <1ms)
**Step 3:** Translation Cache checked — common phrases/sentences cached (Redis, ~40% hit rate)
**Step 4:** On cache miss, NMT Model Server runs Transformer inference on TPU. Text tokenized → encoded → decoded → detokenized
**Step 5:** Post-processing: fix capitalization, restore named entities, format numbers/dates for target locale

Deep Dive: Transformer Architecture for Translation

Encoder-Decoder Transformer (Google's NMT)
`// Evolution of Google Translate:
// 2006: Statistical MT (phrase-based, BLEU ~25)
// 2016: Neural MT (GNMT - LSTM encoder-decoder, BLEU ~35)
// 2017: Transformer (attention is all you need, BLEU ~40+)
// 2023: PaLM-based / large multilingual models

// Transformer architecture for translation:
Input: "The cat sat on the mat"
  ↓ Tokenization (SentencePiece - subword units)
  ["▁The", "▁cat", "▁sat", "▁on", "▁the", "▁mat"]
  ↓ Embedding + Positional Encoding
  ↓ Encoder (6 layers of self-attention + FFN)
  → Context-aware representation of source sentence
  ↓ Decoder (6 layers of masked self-attention + cross-attention + FFN)
  → Autoregressive generation, one token at a time
Output: "Le chat était assis sur le tapis"

// Key innovation: Multi-head attention
// Q, K, V = linear projections of input
// Attention(Q,K,V) = softmax(QK^T / √d_k) × V
// 8 heads → 8 different "perspectives" on relationships

// For 130+ languages: Multilingual model
// Single model handles ALL language pairs
// Zero-shot: can translate X→Y even if only trained on X→EN and EN→Y
// Massively multilingual: shared representations improve low-resource languages`


Deep Dive: Language Detection

`// Language detection must be fast (<1ms) and accurate
// Approach: Character n-gram classifier (fastText)

// 1. Extract character trigrams from input
// "Hello" → ["Hel", "ell", "llo"]
// Each language has distinctive character patterns

// 2. fastText linear classifier
// - Trained on Wikipedia text in 130+ languages
// - Hash n-grams to fixed vocabulary (buckets)
// - Single matrix multiply → softmax over languages
// - 99.5% accuracy for >20 character inputs

// 3. Challenges:
// - Short text ("ok", "taxi") — ambiguous across languages
// - Code-mixed text ("I want कॉफ़ी") — multilingual input
// - Script detection helps: Devanagari → Hindi/Marathi/Sanskrit
// - Fallback: user's locale as prior probability

// 4. CLD3 (Compact Language Detector 3)
// Google's production detector
// Neural network on character sequences
// Supports 107 languages, runs in ~0.5ms`


## 🔄 Flow 2: Speech Translation (Real-Time Conversation)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 280" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#34A853">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="80" text-anchor="middle" fill="white" font-size="12">
Microphone
</text>


<rect x="170" y="50" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="235" y="70" text-anchor="middle" fill="white" font-size="12">
Speech-to-Text
</text>
<text x="235" y="85" text-anchor="middle" fill="white" font-size="10">
(Streaming ASR)
</text>


<rect x="340" y="50" width="120" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="400" y="80" text-anchor="middle" fill="white" font-size="12">
NMT Engine
</text>


<rect x="500" y="50" width="130" height="50" rx="8" fill="#1565C0">
</rect>
<text x="565" y="70" text-anchor="middle" fill="white" font-size="12">
Text-to-Speech
</text>
<text x="565" y="85" text-anchor="middle" fill="white" font-size="10">
(WaveNet/Tacotron)
</text>


<rect x="670" y="50" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="725" y="80" text-anchor="middle" fill="white" font-size="12">
Speaker
</text>


<rect x="170" y="160" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="235" y="180" text-anchor="middle" fill="white" font-size="12">
WebSocket /
</text>
<text x="235" y="195" text-anchor="middle" fill="white" font-size="10">
gRPC Streaming
</text>


<line x1="130" y1="75" x2="165" y2="75" stroke="#34A853" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="300" y1="75" x2="335" y2="75" stroke="#34A853" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="460" y1="75" x2="495" y2="75" stroke="#34A853" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="630" y1="75" x2="665" y2="75" stroke="#34A853" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="235" y1="100" x2="235" y2="155" stroke="#FF9800" stroke-width="2" marker-end="url(#a2)">
</line>


<text x="130" y="45" fill="#aaa" font-size="10">
audio chunks
</text>


<text x="310" y="45" fill="#aaa" font-size="10">
partial text
</text>


<text x="470" y="45" fill="#aaa" font-size="10">
translated
</text>


<text x="640" y="45" fill="#aaa" font-size="10">
synthesized audio
</text>


</svg>

</details>


**Step 1:** Audio streamed via WebSocket/gRPC in 100ms chunks (16kHz PCM or Opus codec)
**Step 2:** Streaming ASR produces partial transcripts in real-time (Conformer model), finalizes on silence detection
**Step 3:** Finalized text segments sent to NMT engine for translation
**Step 4:** Translated text synthesized to speech via WaveNet/Tacotron TTS model
**Step 5:** Audio response streamed back to client for playback

Deep Dive: Streaming ASR & End-of-Utterance Detection

`// Streaming Speech Recognition Pipeline:
// Audio → Feature Extraction → Acoustic Model → Language Model → Text

// 1. Feature extraction: 25ms windows, 10ms hop
//    Mel-frequency cepstral coefficients (MFCC) or log-mel filterbanks
//    80 mel bins → 80-dim feature vector per frame

// 2. Acoustic model: Conformer (Conv + Transformer)
//    - Convolution captures local audio patterns
//    - Transformer captures long-range dependencies
//    - Streaming mode: causal attention (can't look ahead)
//    - Emits partial hypotheses every ~200ms

// 3. Endpointer: detect when user stops speaking
//    - Voice Activity Detection (VAD): classify frames as speech/silence
//    - End-of-query model: predict if user is done or just pausing
//    - Typical threshold: 700ms silence → finalize
//    - Challenge: premature cutoff vs excessive wait

// 4. Protocol: bidirectional gRPC streaming
//    Client → Server: audio chunks (StreamingRecognizeRequest)
//    Server → Client: partial + final results (StreamingRecognizeResponse)
//    { "is_final": false, "transcript": "how do you" }  // partial
//    { "is_final": true,  "transcript": "how do you say hello" }  // final`


## 🔄 Flow 3: Camera/Image Translation (Visual Translation)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 280" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FBBC04">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="110" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="75" y="90" text-anchor="middle" fill="white" font-size="12">
Camera Feed
</text>


<rect x="175" y="60" width="120" height="50" rx="8" fill="#1565C0">
</rect>
<text x="235" y="80" text-anchor="middle" fill="white" font-size="12">
Text Detection
</text>
<text x="235" y="95" text-anchor="middle" fill="white" font-size="10">
(CRAFT/EAST)
</text>


<rect x="340" y="60" width="120" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="400" y="80" text-anchor="middle" fill="white" font-size="12">
OCR Engine
</text>
<text x="400" y="95" text-anchor="middle" fill="white" font-size="10">
(Tesseract/CNN)
</text>


<rect x="505" y="60" width="120" height="50" rx="8" fill="#1565C0">
</rect>
<text x="565" y="90" text-anchor="middle" fill="white" font-size="12">
NMT Engine
</text>


<rect x="670" y="60" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="735" y="80" text-anchor="middle" fill="white" font-size="12">
AR Overlay
</text>
<text x="735" y="95" text-anchor="middle" fill="white" font-size="10">
(Inpainting+Render)
</text>


<line x1="130" y1="85" x2="170" y2="85" stroke="#FBBC04" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="295" y1="85" x2="335" y2="85" stroke="#FBBC04" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="460" y1="85" x2="500" y2="85" stroke="#FBBC04" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="625" y1="85" x2="665" y2="85" stroke="#FBBC04" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


**Step 1:** Camera captures frame. Text Detection model (CRAFT/EAST) locates text regions as bounding boxes
**Step 2:** OCR engine recognizes characters within each bounding box (CNN + CTC decoder or attention-based)
**Step 3:** Recognized text translated via NMT engine
**Step 4:** AR overlay: inpaint original text region (remove source text), render translated text with matched font/color/perspective

Deep Dive: On-Device vs Cloud Translation


| Aspect     | On-Device                | Cloud                |
|------------|--------------------------|----------------------|
| Latency    | ~50ms (no network)       | ~200ms (network RTT) |
| Quality    | Good (compressed model)  | Best (full model)    |
| Model size | ~50MB per language pair  | ~1GB+ per model      |
| Offline    | Yes                      | No                   |
| Privacy    | Data stays on device     | Data sent to server  |
| Languages  | ~59 (downloadable packs) | 130+                 |


`// On-device model optimization:
// 1. Quantization: FP32 → INT8 (4x smaller, 2-3x faster)
// 2. Distillation: large teacher → small student model
// 3. Pruning: remove low-magnitude weights (50-90% sparsity)
// 4. TFLite / ONNX Runtime for mobile inference
// 5. Shared encoder across languages, tiny per-language decoder heads

// Google Translate offline packs:
// ~50MB per language pair (download once)
// Uses Transformer-based model with 6 encoder + 2 decoder layers
// Runs on mobile CPU (no GPU required) at ~100 tokens/sec`


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 500" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="ac" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#4285F4">
</polygon>
</marker>
</defs>


<text x="550" y="25" text-anchor="middle" fill="#4285F4" font-size="16" font-weight="bold">
Google Translate Architecture
</text>


<rect x="20" y="50" width="120" height="40" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="75" text-anchor="middle" fill="white" font-size="11">
Web/Mobile/API
</text>


<rect x="200" y="50" width="130" height="40" rx="8" fill="#E65100">
</rect>
<text x="265" y="75" text-anchor="middle" fill="white" font-size="11">
API Gateway + LB
</text>


<rect x="400" y="40" width="130" height="35" rx="8" fill="#1565C0">
</rect>
<text x="465" y="63" text-anchor="middle" fill="white" font-size="11">
Lang Detection
</text>


<rect x="400" y="85" width="130" height="35" rx="8" fill="#00695C">
</rect>
<text x="465" y="108" text-anchor="middle" fill="white" font-size="11">
Translation Cache
</text>


<rect x="400" y="130" width="130" height="35" rx="8" fill="#6A1B9A">
</rect>
<text x="465" y="153" text-anchor="middle" fill="white" font-size="11">
NMT Model Serving
</text>


<rect x="400" y="175" width="130" height="35" rx="8" fill="#1565C0">
</rect>
<text x="465" y="198" text-anchor="middle" fill="white" font-size="11">
Speech-to-Text
</text>


<rect x="400" y="220" width="130" height="35" rx="8" fill="#1565C0">
</rect>
<text x="465" y="243" text-anchor="middle" fill="white" font-size="11">
Text-to-Speech
</text>


<rect x="400" y="265" width="130" height="35" rx="8" fill="#1565C0">
</rect>
<text x="465" y="288" text-anchor="middle" fill="white" font-size="11">
OCR / Vision
</text>


<rect x="600" y="50" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="670" y="75" text-anchor="middle" fill="white" font-size="11">
Model Registry
</text>


<rect x="600" y="110" width="140" height="40" rx="8" fill="#607D8B">
</rect>
<text x="670" y="135" text-anchor="middle" fill="white" font-size="11">
TPU Cluster
</text>


<rect x="600" y="170" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="670" y="195" text-anchor="middle" fill="white" font-size="11">
Training Pipeline
</text>


<rect x="600" y="230" width="140" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="670" y="255" text-anchor="middle" fill="white" font-size="11">
Feedback / RLHF
</text>


<rect x="830" y="50" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="900" y="75" text-anchor="middle" fill="white" font-size="11">
Parallel Corpora DB
</text>


<rect x="830" y="110" width="140" height="40" rx="8" fill="#4E342E">
</rect>
<text x="900" y="135" text-anchor="middle" fill="white" font-size="11">
Monolingual Data
</text>


<rect x="200" y="370" width="130" height="40" rx="8" fill="#00695C">
</rect>
<text x="265" y="395" text-anchor="middle" fill="white" font-size="11">
CDN (Static Assets)
</text>


<rect x="400" y="370" width="130" height="40" rx="8" fill="#00695C">
</rect>
<text x="465" y="395" text-anchor="middle" fill="white" font-size="11">
Redis Cluster
</text>


<rect x="600" y="370" width="140" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="670" y="395" text-anchor="middle" fill="white" font-size="11">
Kafka Event Bus
</text>


<line x1="140" y1="70" x2="195" y2="70" stroke="#4285F4" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="330" y1="70" x2="395" y2="57" stroke="#4285F4" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="330" y1="70" x2="395" y2="102" stroke="#4285F4" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="330" y1="70" x2="395" y2="147" stroke="#4285F4" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="530" y1="147" x2="595" y2="70" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="530" y1="147" x2="595" y2="130" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="740" y1="190" x2="825" y2="70" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="740" y1="190" x2="825" y2="130" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="670" y1="210" x2="670" y2="225" stroke="#FF9800" stroke-width="2" marker-end="url(#ac)">
</line>


</svg>

</details>


## 💾 Database Schema


### Translation Cache (Redis)

`// Key design for translation cache:
// Key: hash(source_lang + target_lang + normalized_text)
// Value: translated_text + metadata

SET translate:en:fr:sha256(text) {
  "translation": "Bonjour le monde",
  "model_version": "nmt-v4.2",
  "quality_score": 0.95,
  "cached_at": 1707500000
} EX 86400  // 24h TTL

// Cache segmentation:
// - Hot phrases: permanent cache (top 10M translations)
// - Session cache: 24h TTL for recent translations
// - Document cache: keyed by (doc_hash + lang_pair), 7d TTL

// Hit rate optimization:
// - Normalize whitespace, casing before hashing
// - Sentence-level caching (split paragraphs into sentences)
// - ~40% cache hit rate for text, ~60% for common phrases`


### Training Data Store

`-- Parallel Corpora (Bigtable / Spanner)
-- Billions of aligned sentence pairs
Key: (language_pair, sentence_hash)
Columns: {
  source_text: STRING,
  target_text: STRING,
  source: STRING, -- "web_crawl"|"europarl"|"user_feedback"
  quality_score: FLOAT, -- 0-1 filtering threshold
  domain: STRING, -- "medical"|"legal"|"general"
  created_at: TIMESTAMP
}

-- User Feedback (for model improvement)
Key: (feedback_id)
Columns: {
  source_text: STRING,
  machine_translation: STRING,
  user_correction: STRING,
  source_lang: STRING,
  target_lang: STRING,
  timestamp: TIMESTAMP,
  approved: BOOLEAN -- human review for training inclusion
}

-- Model metadata stored in Spanner (globally consistent)
-- Model artifacts stored in GCS (Cloud Storage)`


## ⚡ Cache & CDN Deep Dive


| Cache Layer      | What                                   | Strategy                   | TTL              |
|------------------|----------------------------------------|----------------------------|------------------|
| Browser/App      | Recent translations                    | LRU local storage          | Session / 7 days |
| CDN Edge         | Static assets, language packs          | Pull-based caching         | 30 days          |
| Redis (Regional) | Translation results                    | Write-through on translate | 24 hours         |
| Model Cache      | Loaded model weights in GPU/TPU memory | LRU across language pairs  | Permanent (hot)  |


Offline Language Packs (CDN Distribution)
`// Language packs distributed via CDN:
// - ~50MB per language pair (quantized INT8 model)
// - Delta updates: only download changed weights (~5MB)
// - Stored: GCS → CloudFront/Akamai CDN → device
// - Version pinning: app requests specific model version
// - Background download on Wi-Fi, prompt on cellular`


## 📈 Scaling Considerations


- **Model serving at scale:** TPU pods (v4: 4096 chips) for batch inference. GPU clusters (A100/H100) for real-time. Batching: group requests by language pair for efficient GPU utilization (dynamic batching with max 50ms wait).

- **Language pair routing:** not all 130×129 = 16,770 pairs have direct models. Use pivot translation through English: X → EN → Y. Trade quality for coverage.

- **Auto-scaling:** scale model servers based on QPS per language pair. Popular pairs (EN↔ES, EN↔ZH) get dedicated capacity. Rare pairs share multi-language model instances.

- **Global deployment:** model replicas in each GCP region. Route to nearest region. Language-aware routing: Asian language pairs served from asia-east, European from europe-west.


## ⚖️ Tradeoffs


> **
Multilingual Single Model
- One model handles all 130+ languages
- Zero-shot translation (unseen pairs)
- Shared representations help low-resource langs


Multilingual Single Model
- "Curse of multilinguality" — quality drops as langs increase
- Large model = higher inference cost
- High-resource languages subsidize low-resource


Pivot Through English
- Only need N models (X→EN) instead of N² pairs
- English has most training data


Pivot Through English
- Error compounds (two translations)
- English-centric bias in translations
- Double latency


## 🔄 Alternative Approaches


LLM-Based Translation (GPT-4, PaLM)

Use general-purpose LLMs for translation. Better context handling, idiom translation, and style matching. Higher latency and cost per token. Competitive quality for high-resource pairs, but specialized NMT still wins on low-resource and speed.

Retrieval-Augmented Translation

Retrieve similar translated sentences from a translation memory (TM), use as additional context for NMT model. Especially useful for domain-specific translation (medical, legal). Combines the consistency of TM with flexibility of NMT.


## 📚 Additional Information


- **BLEU score:** standard metric for translation quality (0-100). Measures n-gram overlap with reference translation. BLEU 40+ = high quality for most language pairs. Human evaluation (MQM) used for nuanced quality assessment.

- **SentencePiece tokenization:** language-agnostic subword tokenizer. Handles all scripts without language-specific preprocessing. Shared vocabulary across all languages (~64K tokens).

- **Back-translation:** data augmentation for low-resource languages. Translate target monolingual text → source using existing model. Creates synthetic parallel data. 2-3x quality improvement for low-resource pairs.

- **Adaptive translation:** user corrections feed back into model fine-tuning (with human review). Google Community contributions for quality improvement.