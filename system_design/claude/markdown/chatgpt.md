# 🤖 ChatGPT - System Design


## Functional Requirements


1. **Conversational AI:** Multi-turn chat with streaming token-by-token response, conversation history, model selection (GPT-4o, GPT-4, GPT-3.5), system prompts, temperature control

2. **Tool Use & Plugins:** Code execution (sandbox), web browsing (Bing search), image generation (DALL-E), file upload/analysis, function calling, custom GPTs (user-built assistants)

3. **Safety & Moderation:** Content filtering, RLHF alignment, refusal of harmful requests, PII detection, rate limiting, abuse prevention, usage tracking per tier (free/plus/enterprise)


## Non-Functional Requirements


- **Scale:** 200M+ weekly active users, 100B+ tokens/day inference, multi-model serving simultaneously

- **Latency:** Time to first token (TTFT) <500ms for GPT-4o, <2s for GPT-4; streaming throughput 50-80 tokens/sec

- **Availability:** 99.9% uptime; graceful degradation (fallback to smaller models during capacity constraints)

- **Cost Efficiency:** GPU compute is the primary cost — $700K+/day estimated GPU spend; optimized inference via KV cache, batching, quantization

- **Safety:** Alignment guarantees — harmful content generation rate <0.1%; multilayer safety stack


## Flow 1: Chat Completion (Streaming)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 370" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#10A37F">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Client
</text>
<text x="80" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Web / Mobile)
</text>


<rect x="190" y="50" width="130" height="50" rx="8" fill="#FB8C00">
</rect>
<text x="255" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API Gateway
</text>
<text x="255" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Auth + Rate Limit)
</text>


<rect x="370" y="50" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="435" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Chat Service
</text>
<text x="435" y="87" text-anchor="middle" fill="#fff" font-size="10">
(Orchestrator)
</text>


<rect x="550" y="20" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="615" y="42" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Safety Filter
</text>
<text x="615" y="57" text-anchor="middle" fill="#fff" font-size="10">
(Input Moderation)
</text>


<rect x="550" y="90" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="615" y="112" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Model Router
</text>
<text x="615" y="127" text-anchor="middle" fill="#fff" font-size="10">
(Model Selection)
</text>


<rect x="730" y="50" width="150" height="55" rx="8" fill="#10A37F">
</rect>
<text x="805" y="72" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
GPU Inference
</text>
<text x="805" y="90" text-anchor="middle" fill="#fff" font-size="10">
(H100 / A100 Cluster)
</text>


<rect x="370" y="180" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="435" y="202" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Output Safety
</text>
<text x="435" y="217" text-anchor="middle" fill="#fff" font-size="10">
(Response Filter)
</text>


<rect x="550" y="180" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="615" y="202" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Conversation DB
</text>
<text x="615" y="217" text-anchor="middle" fill="#fff" font-size="10">
(History Store)
</text>


<rect x="190" y="260" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="255" y="282" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
SSE Stream
</text>
<text x="255" y="297" text-anchor="middle" fill="#fff" font-size="10">
(Token-by-Token)
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#10A37F" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="320" y1="75" x2="365" y2="75" stroke="#10A37F" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="65" x2="545" y2="45" stroke="#10A37F" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="85" x2="545" y2="110" stroke="#10A37F" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="680" y1="115" x2="725" y2="80" stroke="#10A37F" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="730" y1="105" x2="500" y2="195" stroke="#10A37F" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="500" y1="205" x2="545" y2="205" stroke="#10A37F" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="370" y1="205" x2="320" y2="275" stroke="#10A37F" stroke-width="2" marker-end="url(#ah)">
</line>


<line x1="190" y1="285" x2="140" y2="100" stroke="#10A37F" stroke-width="1.5" marker-end="url(#ah)">
</line>


</svg>

</details>


### Steps


1. Client sends POST /v1/chat/completions with messages array (system + conversation history + user message)

2. API Gateway authenticates (API key or session token), checks rate limits (free: 40 msg/3hr, Plus: 80 msg/3hr for GPT-4)

3. Chat Service (orchestrator) prepares request: truncates context to model's context window, injects system prompt

4. Input Safety Filter runs content moderation classifier — blocks harmful prompts before reaching the model

5. Model Router selects inference endpoint: model version, routes to least-loaded GPU cluster (weighted round-robin)

6. GPU Inference: autoregressive token generation — model produces one token at a time; each token streamed immediately

7. Output Safety Filter evaluates generated tokens in sliding window — catches harmful completions mid-stream

8. Tokens delivered to client via Server-Sent Events (SSE) — `data: {"choices":[{"delta":{"content":"Hello"}}]}`

9. Full conversation (messages + response) persisted to Conversation DB for history and future context


### Example


User sends "Explain quantum computing in simple terms". API Gateway validates Plus subscription. Input filter passes (benign request). Model Router selects GPT-4o cluster in us-east (lowest queue depth). Inference begins: first token "Quantum" generated in 180ms (TTFT). Subsequent tokens stream at 70 tokens/sec via SSE. Output filter scans continuously. Total response: 250 tokens in ~3.5s. Conversation saved.


### Deep Dives


GPU Inference Architecture


**Hardware:** NVIDIA H100 (80GB HBM3) / A100 clusters — tensor parallelism across 8 GPUs per node


**Framework:** Custom inference engine (similar to vLLM / TensorRT-LLM) with PagedAttention for efficient KV cache management


**Batching:** Continuous batching (in-flight batching) — new requests join batch without waiting for others to complete


**KV Cache:** Key-Value cache stores attention states for all previous tokens — avoids recomputation; managed via paged memory allocator


**Quantization:** INT8/FP8 quantization for inference (vs. FP16/BF16 for training) — 2x throughput with minimal quality loss


Server-Sent Events (SSE) Streaming


**Protocol:** HTTP/2 with SSE — unidirectional server-to-client stream over single long-lived connection


**Format:** `data: {"id":"chatcmpl-xxx","choices":[{"delta":{"content":"token"}}]}\n\n`


**Termination:** Final message: `data: [DONE]\n\n` signals stream completion


**Advantage:** User sees response forming in real-time — perceived latency dramatically reduced vs. waiting for full response


Context Window Management


**GPT-4o:** 128K token context window (~96K words)


**Truncation:** If conversation exceeds context window, oldest messages dropped (preserving system prompt and recent messages)


**Token Counting:** tiktoken library (BPE tokenizer) counts tokens before sending to model; client-side estimation for UX


**Pricing:** Input tokens ($2.50/1M for GPT-4o) cheaper than output tokens ($10/1M) — encourages long context, shorter responses


Safety / Moderation Stack


**Layer 1:** Input classifier — lightweight BERT-based model catches obviously harmful prompts in <10ms


**Layer 2:** System prompt injection — model's system prompt includes safety instructions and refusal guidelines


**Layer 3:** RLHF alignment — model trained with human feedback to refuse harmful requests naturally


**Layer 4:** Output classifier — scans generated tokens in real-time; can truncate response mid-stream


**Layer 5:** Post-hoc review — flagged conversations reviewed by human trust & safety team


## Flow 2: Tool Use (Code Interpreter / Web Browsing)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 320" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#6EE7B7">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="90" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
Client
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Chat Service
</text>
<text x="255" y="97" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
(Orchestrator)
</text>


<rect x="370" y="60" width="140" height="50" rx="8" fill="#10A37F">
</rect>
<text x="440" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
LLM Inference
</text>
<text x="440" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Tool Decision)
</text>


<rect x="560" y="20" width="130" height="45" rx="8" fill="#9C27B0">
</rect>
<text x="625" y="47" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Code Interpreter
</text>


<rect x="560" y="80" width="130" height="45" rx="8" fill="#E53935">
</rect>
<text x="625" y="107" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Web Browser
</text>


<rect x="560" y="140" width="130" height="45" rx="8" fill="#FB8C00">
</rect>
<text x="625" y="167" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
DALL-E (Images)
</text>


<rect x="740" y="60" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="805" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Sandbox (gVisor)
</text>
<text x="805" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Isolated Runtime)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#6EE7B7" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="320" y1="85" x2="365" y2="85" stroke="#6EE7B7" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="510" y1="75" x2="555" y2="42" stroke="#6EE7B7" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="510" y1="85" x2="555" y2="100" stroke="#6EE7B7" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="510" y1="95" x2="555" y2="160" stroke="#6EE7B7" stroke-width="2" marker-end="url(#ah2)">
</line>


<line x1="690" y1="42" x2="735" y2="72" stroke="#6EE7B7" stroke-width="2" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Steps


1. User prompt may require external tools: "Analyze this CSV file" or "Search the web for latest news"

2. LLM generates a structured tool call: `{"name": "python", "arguments": {"code": "import pandas as pd..."}}`

3. Chat Service (orchestrator) detects tool call in model output → routes to appropriate tool executor

4. **Code Interpreter:** Python code executed in gVisor sandbox (isolated container, no network, 120s timeout, 512MB memory)

5. **Web Browser:** Bing Search API → fetches top results → scrapes page content → injects into context for model to summarize

6. **DALL-E:** Image generation request sent to DALL-E 3 pipeline → returns image URL

7. Tool output (execution result, search results, image URL) appended to conversation as tool_result message

8. Model processes tool output and generates final natural language response to user

9. Multi-step tool use: model can chain multiple tool calls (search → code analysis → generate chart)


### Example


User uploads sales.csv and asks "Create a chart of monthly revenue trends." LLM generates Python code: `pd.read_csv('sales.csv').groupby('month')['revenue'].sum().plot()`. Code Interpreter executes in sandbox, produces matplotlib chart image. Image returned to model. Model responds: "Here's your monthly revenue chart. Revenue peaked in December at $1.2M..." with chart displayed inline.


### Deep Dives


Code Interpreter Sandbox


**Runtime:** Python 3.11 with pre-installed packages (pandas, numpy, matplotlib, scipy, sympy, PIL)


**Isolation:** gVisor (user-space kernel) — syscall-level sandboxing; no network access, no filesystem persistence


**Limits:** 120 second execution timeout, 512MB memory, 10MB output file size


**State:** Sandbox persists within a conversation (uploaded files accessible across multiple code runs)


Function Calling (API)


**Schema:** Developer defines functions as JSON Schema → model decides when to call them and with what arguments


**Parallel:** Model can invoke multiple functions in single turn (parallel function calling)


**Forced:** `tool_choice: "required"` forces model to use a tool; `"auto"` lets model decide


**Use Cases:** API integration, database queries, IoT control, structured data extraction


## Flow 3: Custom GPTs & Assistants API


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 900 280" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#D946EF">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#34A853">
</rect>
<text x="80" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
GPT Builder
</text>
<text x="80" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Creator UI)
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="255" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
GPT Config
</text>
<text x="255" y="97" text-anchor="middle" fill="#fff" font-size="10">
(System Prompt)
</text>


<rect x="370" y="30" width="130" height="50" rx="8" fill="#795548">
</rect>
<text x="435" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Knowledge Files
</text>
<text x="435" y="67" text-anchor="middle" fill="#fff" font-size="10">
(RAG Retrieval)
</text>


<rect x="370" y="100" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="435" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Custom Actions
</text>
<text x="435" y="137" text-anchor="middle" fill="#fff" font-size="10">
(OpenAPI Spec)
</text>


<rect x="560" y="60" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="625" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Vector Store
</text>
<text x="625" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Embeddings)
</text>


<rect x="740" y="60" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="805" y="82" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
GPT Store
</text>
<text x="805" y="97" text-anchor="middle" fill="#fff" font-size="10">
(Marketplace)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#D946EF" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#D946EF" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="320" y1="95" x2="365" y2="120" stroke="#D946EF" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="500" y1="55" x2="555" y2="75" stroke="#D946EF" stroke-width="2" marker-end="url(#ah3)">
</line>


<line x1="690" y1="85" x2="735" y2="85" stroke="#D946EF" stroke-width="2" marker-end="url(#ah3)">
</line>


</svg>

</details>


### Steps


1. Creator builds Custom GPT: defines system prompt (instructions), uploads knowledge files, configures tools (code interpreter, web browsing, DALL-E)

2. Knowledge files (PDFs, docs, CSVs) chunked, embedded via text-embedding-3-small, stored in vector store

3. Custom Actions: creator provides OpenAPI spec → GPT can call external APIs during conversation

4. When user chats with Custom GPT: system prompt injected, RAG retrieves relevant chunks from knowledge files, tools enabled per config

5. RAG pipeline: user query embedded → cosine similarity search against vector store → top-K chunks injected into context

6. Published GPTs listed in GPT Store marketplace; revenue sharing with creators based on usage


### Example


Creator builds "Legal Research Assistant" GPT: system prompt with legal terminology guidelines, uploads 500 pages of case law PDFs (chunked into 3,000 embeddings), enables web browsing for recent rulings. User asks: "What precedents exist for data privacy in employment?" RAG retrieves 5 relevant chunks from uploaded case law, web browser fetches 2 recent rulings, model synthesizes comprehensive legal analysis.


### Deep Dives


RAG (Retrieval-Augmented Generation)


**Chunking:** Files split into 800-token chunks with 200-token overlap (sliding window)


**Embeddings:** text-embedding-3-small (1536 dimensions) — encodes semantic meaning of each chunk


**Retrieval:** Cosine similarity search; top-20 chunks retrieved, re-ranked by cross-encoder, top-5 injected into context


**Vector DB:** Qdrant / Pinecone — approximate nearest neighbor (ANN) search with HNSW index


Assistants API (Developer)


**Threads:** Conversation state managed server-side (no need to send full history each call)


**Runs:** Each user message creates a "run" — async execution with polling or streaming for status


**File Search:** Automatic RAG — upload files to assistant, auto-chunked and indexed


**Code Interpreter:** Same sandboxed Python environment as ChatGPT web interface


## Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 500" xmlns="http://www.w3.org/2000/svg">


<defs>
<marker id="ah4" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#10A37F">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="130" height="50" rx="8" fill="#34A853">
</rect>
<text x="85" y="72" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Web / Mobile
</text>
<text x="85" y="87" text-anchor="middle" fill="#fff" font-size="10">
(chat.openai.com)
</text>


<rect x="20" y="120" width="130" height="50" rx="8" fill="#34A853">
</rect>
<text x="85" y="142" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API Clients
</text>
<text x="85" y="157" text-anchor="middle" fill="#fff" font-size="10">
(Developers)
</text>


<rect x="210" y="80" width="130" height="60" rx="8" fill="#FB8C00">
</rect>
<text x="275" y="105" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
API Gateway
</text>
<text x="275" y="122" text-anchor="middle" fill="#fff" font-size="10">
(CloudFlare + Auth)
</text>


<rect x="400" y="30" width="130" height="50" rx="8" fill="#4285F4">
</rect>
<text x="465" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Chat Service
</text>
<text x="465" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Orchestrator)
</text>


<rect x="400" y="100" width="130" height="50" rx="8" fill="#E53935">
</rect>
<text x="465" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Safety Service
</text>
<text x="465" y="137" text-anchor="middle" fill="#fff" font-size="10">
(Moderation)
</text>


<rect x="400" y="170" width="130" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="465" y="192" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Tool Router
</text>
<text x="465" y="207" text-anchor="middle" fill="#fff" font-size="10">
(Code/Browse/DALL-E)
</text>


<rect x="600" y="30" width="140" height="55" rx="8" fill="#10A37F">
</rect>
<text x="670" y="52" text-anchor="middle" fill="#fff" font-size="12" font-weight="bold">
GPU Cluster
</text>
<text x="670" y="70" text-anchor="middle" fill="#fff" font-size="10">
(H100 × 10,000+)
</text>


<rect x="600" y="105" width="140" height="50" rx="8" fill="#9C27B0">
</rect>
<text x="670" y="125" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Model Registry
</text>
<text x="670" y="140" text-anchor="middle" fill="#fff" font-size="10">
(GPT-4o/4/3.5)
</text>


<rect x="800" y="30" width="130" height="50" rx="8" fill="#607D8B">
</rect>
<text x="865" y="52" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
KV Cache Pool
</text>
<text x="865" y="67" text-anchor="middle" fill="#fff" font-size="10">
(Paged Attention)
</text>


<rect x="800" y="100" width="130" height="50" rx="8" fill="#00897B">
</rect>
<text x="865" y="122" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Vector Store
</text>
<text x="865" y="137" text-anchor="middle" fill="#fff" font-size="10">
(RAG / Embeddings)
</text>


<rect x="400" y="320" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="465" y="342" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
PostgreSQL
</text>
<text x="465" y="357" text-anchor="middle" fill="#fff" font-size="10">
(Users + Conversations)
</text>


<rect x="600" y="320" width="140" height="55" rx="8" fill="#795548">
</rect>
<text x="670" y="342" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Redis Cluster
</text>
<text x="670" y="357" text-anchor="middle" fill="#fff" font-size="10">
(Sessions + Rate Limits)
</text>


<rect x="800" y="320" width="130" height="55" rx="8" fill="#795548">
</rect>
<text x="865" y="342" text-anchor="middle" fill="#fff" font-size="11" font-weight="bold">
Blob Storage
</text>
<text x="865" y="357" text-anchor="middle" fill="#fff" font-size="10">
(Files + Images)
</text>


<line x1="150" y1="75" x2="205" y2="100" stroke="#10A37F" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="150" y1="145" x2="205" y2="120" stroke="#10A37F" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="340" y1="100" x2="395" y2="55" stroke="#10A37F" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="340" y1="110" x2="395" y2="125" stroke="#10A37F" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="340" y1="125" x2="395" y2="195" stroke="#10A37F" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="530" y1="55" x2="595" y2="55" stroke="#10A37F" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="530" y1="125" x2="595" y2="125" stroke="#10A37F" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="740" y1="55" x2="795" y2="55" stroke="#10A37F" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="740" y1="130" x2="795" y2="120" stroke="#10A37F" stroke-width="2" marker-end="url(#ah4)">
</line>


<line x1="465" y1="220" x2="465" y2="315" stroke="#10A37F" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="530" y1="55" x2="600" y2="335" stroke="#10A37F" stroke-width="1.5" marker-end="url(#ah4)">
</line>


<line x1="530" y1="195" x2="800" y2="340" stroke="#10A37F" stroke-width="1.5" marker-end="url(#ah4)">
</line>


</svg>

</details>


## Database Schema


### PostgreSQL — Users & Conversations

`CREATE TABLE users (
  user_id        UUID PRIMARY KEY,        `PK`
  email          VARCHAR(255) UNIQUE,     `IDX`
  name           VARCHAR(100),
  tier           VARCHAR(20),  -- free, plus, team, enterprise
  org_id         UUID,                    `FK`
  api_key_hash   CHAR(64),
  token_usage    JSONB,  -- {input_tokens, output_tokens, by_model}
  rate_limit     JSONB,  -- per-model, per-tier limits
  created_at     TIMESTAMPTZ
);

CREATE TABLE conversations (
  conversation_id UUID PRIMARY KEY,       `PK`
  user_id         UUID REFERENCES users,  `FK`
  title           VARCHAR(255),
  model           VARCHAR(30),
  gpt_id          UUID,  -- NULL for default ChatGPT
  message_count   INT DEFAULT 0,
  total_tokens    INT DEFAULT 0,
  created_at      TIMESTAMPTZ,
  updated_at      TIMESTAMPTZ             `IDX`
);
CREATE INDEX idx_conv_user ON conversations(user_id, updated_at DESC);

CREATE TABLE messages (
  message_id      UUID PRIMARY KEY,       `PK`
  conversation_id UUID,                   `FK`
  role            VARCHAR(20),  -- system, user, assistant, tool
  content         TEXT,
  tool_calls      JSONB,  -- [{name, arguments}]
  tool_call_id    VARCHAR(64),
  tokens          INT,
  model           VARCHAR(30),
  finish_reason   VARCHAR(20),  -- stop, length, tool_calls
  created_at      TIMESTAMPTZ
);
CREATE INDEX idx_msg_conv ON messages(conversation_id, created_at);`


 `SHARD` Sharded by user_id — conversations and messages co-located per user


### Custom GPTs & Vector Store

`CREATE TABLE custom_gpts (
  gpt_id          UUID PRIMARY KEY,       `PK`
  creator_id      UUID REFERENCES users,  `FK`
  name            VARCHAR(100),
  description     TEXT,
  system_prompt   TEXT,
  tools_enabled   VARCHAR(50)[],  -- ['code_interpreter','web_browsing','dalle']
  actions_schema  JSONB,  -- OpenAPI spec for custom actions
  knowledge_files UUID[],
  vector_store_id UUID,
  is_public       BOOLEAN DEFAULT FALSE,
  usage_count     BIGINT DEFAULT 0,
  rating          DECIMAL(2,1),
  created_at      TIMESTAMPTZ
);

-- Vector Store (Qdrant / Pinecone)
Collection: gpt_{gpt_id}_vectors
{
  "id": "chunk_uuid",
  "vector": [0.023, -0.041, ..., 0.017],  // 1536 dims
  "payload": {
    "file_id": "uuid",
    "chunk_index": 42,
    "text": "The court ruled that...",
    "metadata": {"page": 15, "filename": "case_law.pdf"}
  }
}`


## Cache & CDN Deep Dive


KV Cache (GPU Memory)


**Purpose:** Stores key-value attention states for all previous tokens — eliminates recomputation during autoregressive generation


**PagedAttention:** Virtual memory-like paging system for KV cache — non-contiguous memory allocation reduces fragmentation by 60-80%


**Size:** GPT-4 KV cache: ~2GB per active conversation at full context length


**Eviction:** LRU eviction of completed conversations; prefix caching for common system prompts


Redis (Rate Limiting + Sessions)


**Rate Limits:** Token bucket per user per model: `INCR user:{id}:gpt4o:tokens` with TTL window


**Sessions:** Auth session cache (JWT validation cache) — TTL 15 minutes


**Conversation Cache:** Recent conversations cached for fast sidebar loading — TTL 1 hour


CDN (Cloudflare)


**Static:** Web app assets (JS, CSS, fonts) cached at edge with immutable hashed filenames


**DALL-E Images:** Generated images cached at CDN edge — content-addressed URLs (hash-based, immutable)


**API:** No CDN caching for API responses — all dynamic, personalized content


Prompt Caching


**Prefix Cache:** Common system prompts (same first N tokens) share KV cache across requests — 50% cost reduction for identical prefixes


**API:** `cached_tokens` field in response shows how many input tokens were cache hits


**Scope:** Within same model, same org — not shared across organizations for privacy


## Scaling & Load Balancing


- **GPU Cluster:** 10,000+ H100 GPUs across multiple datacenters; tensor parallelism (8 GPUs per model instance for GPT-4), pipeline parallelism across nodes

- **Model Router:** Weighted round-robin with queue depth awareness — routes to least-loaded inference server; per-model pools (GPT-4o, GPT-4, GPT-3.5 on separate clusters)

- **Continuous Batching:** In-flight batching allows new requests to join batch at any iteration step — maximizes GPU utilization from 30% (naive) to 80%+

- **Graceful Degradation:** During peak load, free-tier users may be queued or offered GPT-3.5 fallback; Plus subscribers get priority queue

- **Cloudflare:** Global edge network handles DDoS protection, TLS termination, and geographic routing to nearest API datacenter

- **Multi-Region:** US, EU, Asia inference clusters — data residency options for enterprise customers (EU data stays in EU)


## Tradeoffs


> **

✅ Streaming (SSE)


- Dramatically reduces perceived latency (first token in <500ms vs. waiting 10s+)

- Users can start reading while generation continues

- Can cancel/abort mid-stream saving compute


❌ Streaming (SSE)


- Cannot evaluate full response safety before showing to user

- More complex client implementation (partial JSON parsing)

- Long-lived connections strain load balancers


✅ Large Context Windows (128K)


- Entire documents analyzed in single prompt

- Multi-turn conversations without losing context

- Reduces need for external retrieval systems


❌ Large Context Windows


- Quadratic attention cost — 128K tokens requires massive GPU memory

- "Lost in the middle" — models attend poorly to middle of long context

- Expensive — 128K tokens input costs $0.32 per request


## Alternative Approaches


Open-Source Self-Hosted (Llama 3)


Deploy Llama 3 70B on own infrastructure (vLLM on H100s). Full control, no per-token cost, data stays private. But requires ML ops expertise, no built-in safety stack, significant upfront GPU investment, and quality gap vs. GPT-4.


Mixture of Experts (MoE)


Route tokens to specialized sub-models (e.g., Mixtral architecture). Only activates subset of parameters per token — 4x efficiency. Used by GPT-4 (rumored 8×220B MoE). Tradeoff: more total parameters (memory), but less compute per token.


Speculative Decoding


Small draft model generates N candidate tokens, large model verifies in parallel. If draft model is accurate (70-80%), get near-draft-model speed with large-model quality. Requires maintaining two models and careful calibration.


## Additional Information


- **RLHF Training Pipeline:** Human labelers rank model outputs → reward model trained on preferences → PPO (Proximal Policy Optimization) fine-tunes base model to maximize reward. ~10M human preference comparisons for GPT-4.

- **Training Cost:** GPT-4 estimated at $100M+ in compute (25,000 A100s for ~100 days). Inference costs dominate long-term (training is one-time, inference is ongoing).

- **Tokenizer (tiktoken):** Byte Pair Encoding (BPE) with 100K vocabulary. Average English word = 1.3 tokens. Code-heavy text: ~2 tokens per keyword. Efficient for 100+ languages.

- **Memory / Personalization:** "Memory" feature stores user preferences across conversations (stored in user profile, injected as system prompt context). User can view/delete memories. Enterprise: disabled by default for data privacy.

- **Enterprise (ChatGPT Enterprise):** SOC 2 Type II compliant, data not used for training, SSO/SCIM, admin console, unlimited GPT-4 access, 128K context, advanced analytics dashboard.