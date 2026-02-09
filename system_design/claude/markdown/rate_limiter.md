# 🚦 System Design: Rate Limiter


## 📋 Functional Requirements


  - Limit the number of requests a client can make within a time window

  - Support multiple rate limiting strategies (per-user, per-IP, per-endpoint, global)

  - Return informative headers: remaining quota, retry-after time

  - Support configurable rules per API endpoint and client tier (free vs paid)


## 🔒 Non-Functional Requirements


  - **Ultra-low latency:** <1ms overhead per request — rate limiter sits on the critical path

  - **High throughput:** Handle millions of requests/second across all services

  - **Distributed:** Consistent rate limiting across multiple API server instances

  - **Fault tolerant:** If rate limiter fails, traffic should pass through (fail-open) — never block legitimate traffic

  - **Accurate:** Minimal over/under-counting in distributed environment


## 🔄 Flow 1 — Request Rate Checking (Token Bucket)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 350" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="140" width="110" height="55" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="75" y="172" text-anchor="middle" fill="white" font-size="13">
Client
</text>

  
<line x1="130" y1="167" x2="195" y2="167" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="205" y="140" width="130" height="55" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="270" y="163" text-anchor="middle" fill="white" font-size="12">
API Gateway
</text>

  
<text x="270" y="180" text-anchor="middle" fill="white" font-size="10">
(Rate Limit Check)
</text>

  
<line x1="335" y1="155" x2="400" y2="105" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<line x1="335" y1="180" x2="400" y2="230" stroke="#aaa" marker-end="url(#ah1)">
</line>

  
<rect x="410" y="80" width="150" height="50" rx="10" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="485" y="103" text-anchor="middle" fill="white" font-size="11">
Redis Cluster
</text>

  
<text x="485" y="118" text-anchor="middle" fill="white" font-size="10">
(Token Counters)
</text>

  
<rect x="410" y="210" width="150" height="50" rx="10" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="485" y="233" text-anchor="middle" fill="white" font-size="11">
Rule Config Store
</text>

  
<text x="485" y="248" text-anchor="middle" fill="white" font-size="10">
(Rate Limit Rules)
</text>

  

  
<line x1="560" y1="105" x2="640" y2="105" stroke="#4aff4a" marker-end="url(#ah1)">
</line>

  
<text x="600" y="95" fill="#4aff4a" font-size="10">
ALLOWED
</text>

  
<rect x="650" y="80" width="140" height="50" rx="10" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="720" y="103" text-anchor="middle" fill="white" font-size="11">
Backend Service
</text>

  
<text x="720" y="118" text-anchor="middle" fill="white" font-size="10">
(Process Request)
</text>

  

  
<line x1="560" y1="105" x2="640" y2="180" stroke="#ff4a4a" marker-end="url(#ah1)">
</line>

  
<text x="620" y="155" fill="#ff4a4a" font-size="10">
BLOCKED
</text>

  
<rect x="650" y="160" width="140" height="50" rx="10" fill="#8a1a1a" stroke="#ff4a4a" stroke-width="2">
</rect>

  
<text x="720" y="183" text-anchor="middle" fill="white" font-size="11">
HTTP 429
</text>

  
<text x="720" y="198" text-anchor="middle" fill="white" font-size="10">
(Too Many Requests)
</text>


</svg>

</details>


### Step-by-Step


  1. **Request arrives:** Client sends `GET /api/v1/search?q=...` with auth token

  2. **Identify client:** Extract rate limit key: user_id (from JWT token), IP address, API key, or combination

  3. **Load rules:** Look up rate limit rules for this endpoint + client tier from config: "GET /api/search: 100 req/min for free tier, 1000 req/min for paid"

  4. **Check Redis:** Execute Lua script atomically — check current token count for key `rl:{user_id}:{endpoint}`. If tokens available → decrement and pass. If no tokens → reject

  5. **Allowed:** Request proceeds to backend service. Response headers: `X-RateLimit-Remaining: 87`, `X-RateLimit-Limit: 100`, `X-RateLimit-Reset: 1707500100`

  6. **Blocked:** Return HTTP 429 with headers: `Retry-After: 23` (seconds until window resets), `X-RateLimit-Remaining: 0`


> **
**Example:** Free-tier user makes 100th request to /api/search in current minute → Redis: token count = 1 → allowed, count → 0 → Next request: count = 0 → HTTP 429 Too Many Requests, Retry-After: 23 seconds → User waits 23s → Window resets → Token bucket refilled to 100


### 🔍 Rate Limiting Algorithms Deep Dive


1. Token Bucket


Bucket holds `max_tokens`. Tokens added at rate `r` per second (refill rate). Each request consumes 1 token. If bucket empty → reject. Allows short bursts up to max_tokens.


-- Redis Lua script (atomic)
local key = KEYS[1]
local max_tokens = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])  -- tokens per second
local now = tonumber(ARGV[3])

local data = redis.call('HMGET', key, 'tokens', 'last_refill')
local tokens = tonumber(data[1]) or max_tokens
local last_refill = tonumber(data[2]) or now

-- Refill tokens based on elapsed time
local elapsed = now - last_refill
local new_tokens = math.min(max_tokens, tokens + elapsed * refill_rate)

if new_tokens >= 1 then
    redis.call('HMSET', key, 'tokens', new_tokens - 1, 'last_refill', now)
    redis.call('EXPIRE', key, math.ceil(max_tokens / refill_rate) * 2)
    return 1  -- ALLOWED
else
    redis.call('HMSET', key, 'tokens', new_tokens, 'last_refill', now)
    return 0  -- REJECTED
end


**Pros:** Allows bursts, smooth rate limiting. **Cons:** Two parameters to tune (bucket size + refill rate).


2. Sliding Window Log


Store timestamp of every request in a sorted set. Count requests in last N seconds. Most accurate but memory-intensive.


-- Redis implementation
ZADD rl:{user_id} {timestamp} {request_id}  -- add request
ZREMRANGEBYSCORE rl:{user_id} 0 {timestamp - window_size}  -- remove old
ZCARD rl:{user_id}  -- count requests in window
-- If count >= limit → reject


**Pros:** Exact counting, no boundary issues. **Cons:** O(n) memory per user (stores all timestamps), expensive for high-rate endpoints.


3. Sliding Window Counter


Hybrid of fixed window and sliding log. Use counters for current and previous window, weight by overlap percentage.


count = prev_window_count * (1 - elapsed_pct) + curr_window_count
-- Example: 60s window, 45s into current window
-- prev_window had 80 requests, current has 30
-- estimated count = 80 * 0.25 + 30 = 50


**Pros:** Memory-efficient (two counters per key), good approximation. **Cons:** Approximate — can allow slight over-limit during transition.


4. Fixed Window Counter


Simple: increment counter for current minute/second. Reset at window boundary.


key = "rl:{user_id}:{minute_timestamp}"
count = INCR key
if count == 1: EXPIRE key 60  -- auto-cleanup
if count > limit: reject


**Pros:** Simplest, lowest overhead. **Cons:** Boundary problem — user can send 2x limit by timing requests at window boundary (100 at 0:59, 100 at 1:00).


5. Leaky Bucket


Requests enter a FIFO queue (bucket). Queue processed at fixed rate. If queue full → reject. Produces perfectly smooth output rate.


**Pros:** Perfectly smooth traffic (no bursts). **Cons:** Cannot handle legitimate traffic bursts, adds latency (queuing), more complex to implement.


## 🔄 Flow 2 — Distributed Rate Limiting


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 400" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ah2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="60" width="100" height="45" rx="8" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="70" y="87" text-anchor="middle" fill="white" font-size="11">
Client A
</text>

  
<rect x="20" y="120" width="100" height="45" rx="8" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="70" y="147" text-anchor="middle" fill="white" font-size="11">
Client B
</text>

  
<rect x="20" y="180" width="100" height="45" rx="8" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="70" y="207" text-anchor="middle" fill="white" font-size="11">
Client C
</text>

  

  
<line x1="120" y1="83" x2="190" y2="130" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="120" y1="142" x2="190" y2="135" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="120" y1="202" x2="190" y2="140" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="200" y="110" width="110" height="50" rx="8" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="255" y="140" text-anchor="middle" fill="white" font-size="11">
Load Balancer
</text>

  

  
<line x1="310" y1="125" x2="380" y2="75" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="310" y1="135" x2="380" y2="135" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="310" y1="145" x2="380" y2="195" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="390" y="55" width="130" height="40" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="455" y="80" text-anchor="middle" fill="white" font-size="10">
API Server 1
</text>

  
<rect x="390" y="115" width="130" height="40" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="455" y="140" text-anchor="middle" fill="white" font-size="10">
API Server 2
</text>

  
<rect x="390" y="175" width="130" height="40" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="455" y="200" text-anchor="middle" fill="white" font-size="10">
API Server 3
</text>

  

  
<line x1="520" y1="75" x2="600" y2="130" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="520" y1="135" x2="600" y2="135" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<line x1="520" y1="195" x2="600" y2="140" stroke="#aaa" marker-end="url(#ah2)">
</line>

  
<rect x="610" y="80" width="130" height="40" rx="8" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="675" y="105" text-anchor="middle" fill="white" font-size="10">
Redis Primary
</text>

  
<rect x="610" y="130" width="130" height="40" rx="8" fill="#1a8a7a" stroke="#4afff0" stroke-width="1.5">
</rect>

  
<text x="675" y="155" text-anchor="middle" fill="white" font-size="10">
Redis Replica 1
</text>

  
<rect x="610" y="180" width="130" height="40" rx="8" fill="#1a8a7a" stroke="#4afff0" stroke-width="1.5">
</rect>

  
<text x="675" y="205" text-anchor="middle" fill="white" font-size="10">
Redis Replica 2
</text>

  

  
<line x1="675" y1="120" x2="675" y2="128" stroke="#4afff0" marker-end="url(#ah2)">
</line>

  
<line x1="675" y1="170" x2="675" y2="178" stroke="#4afff0" marker-end="url(#ah2)">
</line>


</svg>

</details>


### Challenge: Consistency Across Multiple Servers


Without centralized state, each API server would track its own counters — user could send N requests to each of K servers, effectively getting K×N requests through.

Solution: Centralized Redis


All API servers check/update the same Redis key atomically (Lua scripts). Single source of truth. Adds network hop (~0.5ms) but ensures global consistency.


Alternative: Local Rate Limiter + Sync


Each server maintains local counter. Periodically syncs to central store. Allows slight over-limit (N × K instead of N). Good enough for most use cases. Used when Redis latency is unacceptable.


Sticky Sessions Alternative


Route same client to same server (consistent hashing on client ID). Local counter is sufficient. Drawback: uneven load distribution, failure of one server drops all its clients' state.


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1000 400" xmlns="http://www.w3.org/2000/svg">

  
<defs>
<marker id="ahc" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0, 10 3.5, 0 7" fill="#aaa">
</polygon>
</marker>
</defs>

  
<rect x="20" y="80" width="100" height="40" rx="8" fill="#2d7a3a" stroke="#4aff4a" stroke-width="2">
</rect>

  
<text x="70" y="105" text-anchor="middle" fill="white" font-size="11">
Clients
</text>

  
<line x1="120" y1="100" x2="185" y2="100" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="195" y="75" width="120" height="50" rx="10" fill="#c97a1a" stroke="#ffa54a" stroke-width="2">
</rect>

  
<text x="255" y="98" text-anchor="middle" fill="white" font-size="11">
API Gateway
</text>

  
<text x="255" y="113" text-anchor="middle" fill="white" font-size="9">
(Rate Limit Middleware)
</text>

  
<line x1="315" y1="100" x2="385" y2="60" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="315" y1="100" x2="385" y2="100" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<line x1="315" y1="100" x2="385" y2="140" stroke="#aaa" marker-end="url(#ahc)">
</line>

  
<rect x="395" y="40" width="130" height="40" rx="8" fill="#1a8a7a" stroke="#4afff0" stroke-width="2">
</rect>

  
<text x="460" y="65" text-anchor="middle" fill="white" font-size="10">
Redis (Counters)
</text>

  
<rect x="395" y="85" width="130" height="40" rx="8" fill="#5a3a1a" stroke="#c97a1a" stroke-width="2">
</rect>

  
<text x="460" y="110" text-anchor="middle" fill="white" font-size="10">
Config Store (Rules)
</text>

  
<rect x="395" y="130" width="130" height="40" rx="8" fill="#6a1a8a" stroke="#d94aff" stroke-width="2">
</rect>

  
<text x="460" y="155" text-anchor="middle" fill="white" font-size="10">
Kafka (Analytics)
</text>

  
<line x1="525" y1="60" x2="600" y2="100" stroke="#4aff4a" marker-end="url(#ahc)">
</line>

  
<text x="575" y="72" fill="#4aff4a" font-size="9">
PASS
</text>

  
<rect x="610" y="80" width="130" height="40" rx="8" fill="#1a5a8a" stroke="#4a9eff" stroke-width="2">
</rect>

  
<text x="675" y="105" text-anchor="middle" fill="white" font-size="10">
Backend Services
</text>


</svg>

</details>


> **
**End-to-End:** Request hits API Gateway → Middleware extracts user_id from JWT → Loads rules from config cache: "100 req/min for /search, free tier" → Redis Lua script: atomic token bucket check (0.3ms) → Tokens available → Decrement → Set response headers (X-RateLimit-*) → Forward to backend → Kafka event for analytics ("user X consumed 1 token for /search"). If rate exceeded → 429 with Retry-After header → Client implements exponential backoff


## 🗄️ Database Schema


### Redis — Rate Limit State


# Token Bucket state (per user per endpoint)
HSET rl:token:{user_id}:{endpoint_hash}
  tokens   {remaining_float}
  last_ts  {last_refill_timestamp}
EXPIRE rl:token:{user_id}:{endpoint_hash} 120  # auto-cleanup

# Fixed Window counter
SET rl:fixed:{user_id}:{endpoint}:{minute} {count}
EXPIRE rl:fixed:{user_id}:{endpoint}:{minute} 60

# Sliding Window Log (sorted set)
ZADD rl:log:{user_id}:{endpoint} {timestamp} {request_uuid}


### Config Store — Rate Limit Rules


{
  "rules": [
    {
      "endpoint": "/api/v1/search",
      "method": "GET",
      "limits": {
        "free": { "requests": 100, "window_seconds": 60, "algorithm": "token_bucket", "burst": 20 },
        "paid": { "requests": 1000, "window_seconds": 60, "algorithm": "token_bucket", "burst": 100 },
        "enterprise": { "requests": 10000, "window_seconds": 60, "algorithm": "sliding_window" }
      }
    },
    {
      "endpoint": "/api/v1/data",
      "method": "POST",
      "limits": {
        "free": { "requests": 10, "window_seconds": 60 },
        "global": { "requests": 100000, "window_seconds": 1, "note": "global rate limit across all users" }
      }
    }
  ]
}


Rules stored in a config service (e.g., etcd, Consul, or simple DB). Cached in-memory at API Gateway with 1-min refresh. Changes propagated via push notification or polling.


## 💾 Cache Deep Dive


**Redis IS the cache** — rate limit state lives entirely in Redis. No separate caching layer needed. If Redis goes down, rate limiter fails open (allows all traffic) to prevent blocking legitimate users.


**Redis Cluster:** Shard by rate limit key (user_id hash) across cluster nodes. Each key always lands on same node → no cross-node coordination needed for single-user rate limits.


**Global rate limits:** Single key (e.g., `rl:global:/api/data`) — hot key problem. Solution: shard into N sub-counters (`rl:global:/api/data:{0..9}`), each server increments random sub-counter, sum for total.


## ⚖️ Tradeoffs


> **
  
> **
    ✅ Token Bucket
    

Allows controlled bursts, smooth limiting, widely understood, efficient (2 values per key in Redis)

  
  
> **
    ❌ Token Bucket
    

Two parameters to tune (rate + burst). Burst can surprise — user may send 100 requests instantly then wait. Hard to explain to API consumers vs simple "100 per minute"

  
  
> **
    ✅ Centralized Redis
    

Globally consistent counters, atomic operations, sub-millisecond latency, battle-tested

  
  
> **
    ❌ Centralized Redis
    

SPOF (mitigated by fail-open), added network hop, Redis cluster management complexity, costs scale with traffic volume

  
  
> **
    ✅ Fail-Open
    

Never blocks legitimate traffic due to rate limiter infrastructure failure

  
  
> **
    ❌ Fail-Open
    

During Redis outage, NO rate limiting — vulnerable to DDoS. Must have secondary defense (WAF, IP blocking at network level)

  


## 🔄 Alternative Approaches


Local In-Memory Rate Limiter


No Redis — each server maintains its own counters in memory (e.g., Go's `rate.Limiter`, Java Guava RateLimiter). Pro: zero network overhead, ultra-fast. Con: not globally consistent — user routed to different servers effectively gets N×limit. Works with sticky sessions.


Service Mesh Rate Limiting (Envoy/Istio)


Rate limiting at the sidecar proxy level. Envoy has built-in rate limit service integration. Advantages: language-agnostic, consistent across microservices, centralized policy. Disadvantage: added sidecar overhead, Envoy rate limit service is another dependency.


API Gateway Rate Limiting (Kong/AWS API Gateway)


Managed rate limiting at the gateway layer. AWS API Gateway: configurable per-method, per-API key. Kong: plugin-based with Redis backend. Pro: no custom code. Con: less flexibility, vendor lock-in, may not support custom algorithms.


## 📚 Additional Information


### HTTP Response Headers (RFC 6585 / RFC 7231)


HTTP/1.1 429 Too Many Requests
Content-Type: application/json
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1707500100    # Unix timestamp when window resets
Retry-After: 23                   # Seconds until client should retry

{ "error": "rate_limit_exceeded", "message": "Rate limit of 100 requests per minute exceeded" }


### Client-Side Best Practices


  - **Exponential backoff:** On 429, wait 1s → 2s → 4s → 8s before retrying (with jitter)

  - **Respect Retry-After:** Don't retry before the indicated time

  - **Track remaining quota:** Use X-RateLimit-Remaining to proactively slow down before hitting limit

  - **Circuit breaker:** After N consecutive 429s, stop requesting for longer period


### Multi-Tier Rate Limiting


Production systems apply rate limits at multiple levels: (1) Per-IP at network edge (DDoS protection — millions/sec), (2) Per-API-key at API Gateway (business limits — thousands/min), (3) Per-endpoint per-user (fine-grained — tens/sec), (4) Global per-service (backend protection — total capacity). Each tier uses appropriate algorithm and storage.


### Race Condition: Check-Then-Act


Naive implementation: `GET count` → `if count < limit` → `INCR count`. Race condition: two concurrent requests both read count=99 (limit 100), both proceed. Solution: Redis Lua scripts execute atomically — read + check + increment in single operation. Or use `INCR` first, then check: `INCR key` returns new value — if > limit, the request that pushed it over gets rejected.