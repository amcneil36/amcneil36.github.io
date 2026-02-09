# ☁️ AWS — Cloud Infrastructure Platform


Design the core infrastructure of a cloud computing platform like AWS, focusing on EC2 (compute), S3 (storage), and the control plane that orchestrates resource provisioning, multi-tenancy isolation, and global availability.


## 📋 Functional Requirements


- **Compute provisioning (EC2)** — launch/terminate VMs with specified CPU, memory, storage, and network configs in seconds

- **Object storage (S3)** — store/retrieve objects up to 5TB with 11 nines durability, versioning, lifecycle policies

- **Networking (VPC)** — isolated virtual networks with subnets, security groups, NACLs, route tables, internet/NAT gateways

- **Identity & access (IAM)** — fine-grained permissions with policies, roles, and temporary credentials

- **Auto-scaling** — scale compute capacity based on demand metrics (CPU, network, custom)

- **Load balancing (ELB)** — distribute traffic across instances with health checks


## 📋 Non-Functional Requirements


- **Availability** — 99.99% for compute, 99.999999999% (11 nines) durability for S3

- **Multi-tenancy isolation** — strict resource, network, and data isolation between customers

- **Global scale** — 30+ regions, 100+ availability zones, millions of servers

- **Security** — encryption at rest/transit, hardware root of trust (Nitro), compliance (SOC, HIPAA, PCI)

- **Elasticity** — scale from 0 to thousands of instances in minutes

- **Cost efficiency** — high server utilization through bin-packing and spot instances


## 🔄 Flow 1: EC2 Instance Launch (Compute Provisioning)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 340" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a1" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FF9900">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="80" text-anchor="middle" fill="white" font-size="13">
Customer
</text>


<rect x="190" y="50" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="255" y="80" text-anchor="middle" fill="white" font-size="13">
API Gateway
</text>


<rect x="370" y="50" width="140" height="50" rx="8" fill="#1565C0">
</rect>
<text x="440" y="80" text-anchor="middle" fill="white" font-size="13">
EC2 Control Plane
</text>


<rect x="570" y="30" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="635" y="60" text-anchor="middle" fill="white" font-size="12">
Placement Service
</text>


<rect x="570" y="100" width="130" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="635" y="130" text-anchor="middle" fill="white" font-size="12">
Network Controller
</text>


<rect x="760" y="30" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="825" y="60" text-anchor="middle" fill="white" font-size="12">
Capacity DB
</text>


<rect x="370" y="180" width="140" height="50" rx="8" fill="#00695C">
</rect>
<text x="440" y="210" text-anchor="middle" fill="white" font-size="12">
Nitro Hypervisor
</text>


<rect x="570" y="180" width="130" height="50" rx="8" fill="#4E342E">
</rect>
<text x="635" y="210" text-anchor="middle" fill="white" font-size="12">
EBS Volume
</text>


<rect x="370" y="270" width="140" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="440" y="300" text-anchor="middle" fill="white" font-size="12">
Running VM
</text>


<line x1="140" y1="75" x2="185" y2="75" stroke="#FF9900" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="320" y1="75" x2="365" y2="75" stroke="#FF9900" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="510" y1="60" x2="565" y2="55" stroke="#FF9900" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="510" y1="90" x2="565" y2="125" stroke="#FF9900" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="700" y1="55" x2="755" y2="55" stroke="#FF9900" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="440" y1="100" x2="440" y2="175" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="510" y1="205" x2="565" y2="205" stroke="#81D4FA" stroke-width="2" marker-end="url(#a1)">
</line>


<line x1="440" y1="230" x2="440" y2="265" stroke="#4CAF50" stroke-width="2" marker-end="url(#a1)">
</line>


<text x="440" y="155" fill="#aaa" font-size="11" text-anchor="middle">
boot on host
</text>


</svg>

</details>


**Step 1:** Customer calls RunInstances API with AMI, instance type, VPC/subnet, security groups, key pair, user data
**Step 2:** EC2 Control Plane authenticates via IAM, validates quotas, reserves capacity
**Step 3:** Placement Service selects physical host using bin-packing algorithm (best-fit decreasing), considering AZ spread, tenancy, and capacity
**Step 4:** Network Controller programs the virtual network (VPC ENI, security groups as iptables/eBPF rules, VXLAN overlay)
**Step 5:** Nitro hypervisor on selected host creates VM, attaches EBS volume over network, boots AMI
**Step 6:** Instance reaches "running" state; customer receives instance_id and private IP

Deep Dive: AWS Nitro System

Nitro — Custom Hardware for Cloud


Nitro offloads virtualization functions to dedicated hardware cards, giving ~100% of host resources to customer VMs:

`// Traditional hypervisor:
// Host OS + hypervisor consumes ~15% of resources
// Noisy neighbor: one VM's I/O can starve another

// Nitro architecture:
┌─────────────────────────────┐
│  Customer VM (gets ALL CPU) │
├─────────────────────────────┤
│  Nitro Hypervisor (minimal) │
├──────────┬──────────┬───────┤
│ Nitro    │ Nitro    │ Nitro │  ← Custom ASICs
│ Network  │ Storage  │ Security│
│ Card     │ Card     │ Card   │
└──────────┴──────────┴───────┘

// Nitro Network Card: handles VPC, ENA (Elastic Network Adapter)
// - SR-IOV for direct NIC access (bypasses hypervisor)
// - Hardware security group enforcement
// - 100 Gbps throughput

// Nitro Storage Card: handles EBS I/O
// - NVMe interface to VM
// - Encryption in hardware (AES-256-XTS)
// - Remote EBS volumes appear as local NVMe

// Nitro Security Chip: hardware root of trust
// - Prevents host OS from accessing customer memory
// - Cryptographic attestation of boot process`


Deep Dive: Placement & Bin-Packing

`// Placement Service must optimize multiple objectives:
// 1. Maximize server utilization (reduce costs)
// 2. Spread across failure domains (AZs, racks, hosts)
// 3. Respect placement groups (cluster, spread, partition)
// 4. Handle "stranding" (e.g., 128GB RAM left but only 2 CPUs)

// Algorithm: Multi-dimensional bin-packing (NP-hard)
// Heuristic: Best-Fit Decreasing with scoring
function placeInstance(instanceType, constraints) {
  const candidates = getHostsInAZ(constraints.az)
    .filter(h => h.hasCapacity(instanceType))
    .filter(h => meetsPlacementGroup(h, constraints))
    .filter(h => meetsSpreadConstraint(h, constraints));

  // Score each host (higher = better fit)
  return candidates.sort((a, b) => {
    const scoreA = fitScore(a, instanceType); // tighter fit = higher
    const scoreB = fitScore(b, instanceType);
    return scoreB - scoreA; // best-fit first
  })[0];
}

// Capacity reservation: pre-allocate to avoid InsufficientCapacity
// Spot instances: bid on spare capacity at 60-90% discount`


## 🔄 Flow 2: S3 Object Storage (Put/Get)


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 300" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a2" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#81D4FA">
</polygon>
</marker>
</defs>


<rect x="20" y="60" width="120" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="90" text-anchor="middle" fill="white" font-size="13">
Client
</text>


<rect x="190" y="60" width="130" height="50" rx="8" fill="#E65100">
</rect>
<text x="255" y="90" text-anchor="middle" fill="white" font-size="12">
S3 Front End
</text>


<rect x="370" y="30" width="140" height="50" rx="8" fill="#1565C0">
</rect>
<text x="440" y="60" text-anchor="middle" fill="white" font-size="12">
Index Service
</text>


<rect x="370" y="110" width="140" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="440" y="140" text-anchor="middle" fill="white" font-size="12">
Placement Service
</text>


<rect x="570" y="30" width="140" height="50" rx="8" fill="#4E342E">
</rect>
<text x="640" y="50" text-anchor="middle" fill="white" font-size="12">
Metadata Store
</text>
<text x="640" y="65" text-anchor="middle" fill="white" font-size="10">
(key→shard map)
</text>


<rect x="570" y="110" width="140" height="50" rx="8" fill="#00695C">
</rect>
<text x="640" y="130" text-anchor="middle" fill="white" font-size="12">
Storage Nodes
</text>
<text x="640" y="145" text-anchor="middle" fill="white" font-size="10">
(replicated shards)
</text>


<rect x="770" y="110" width="140" height="50" rx="8" fill="#4E342E">
</rect>
<text x="840" y="130" text-anchor="middle" fill="white" font-size="12">
Erasure-Coded
</text>
<text x="840" y="145" text-anchor="middle" fill="white" font-size="10">
Storage (cold)
</text>


<line x1="140" y1="85" x2="185" y2="85" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="320" y1="75" x2="365" y2="55" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="320" y1="95" x2="365" y2="135" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="510" y1="55" x2="565" y2="55" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="510" y1="135" x2="565" y2="135" stroke="#81D4FA" stroke-width="2" marker-end="url(#a2)">
</line>


<line x1="710" y1="135" x2="765" y2="135" stroke="#FF9900" stroke-width="2" marker-end="url(#a2)">
</line>


<text x="740" y="120" fill="#aaa" font-size="10">
lifecycle tiering
</text>


</svg>

</details>


**Step 1:** Client sends PUT /bucket/key with object data. S3 Front End authenticates (SigV4), validates request
**Step 2:** Index Service maps key to storage partition using consistent hashing on (bucket + key)
**Step 3:** Placement Service determines which storage nodes hold the partition replicas (3 AZs minimum)
**Step 4:** Object data written to all replicas; return 200 OK only after quorum writes succeed (strong consistency since Dec 2020)
**Step 5:** For GET, Index Service locates replicas; return data from nearest/fastest replica

Deep Dive: S3 Durability — 11 Nines

`// S3 achieves 99.999999999% durability through:

// 1. Replication across 3+ AZs (within a region)
//    Each AZ is an independent data center (separate power, cooling, network)
//    P(all 3 AZs fail simultaneously) ≈ 10^-11

// 2. Integrity verification
//    - MD5 checksums on upload (end-to-end verification)
//    - Background scrubbing: read all data periodically, verify checksums
//    - If corruption detected → auto-repair from replica

// 3. Erasure coding (for infrequent access tiers)
//    Split object into k data chunks + m parity chunks
//    Can reconstruct from any k of (k+m) chunks
//    Example: RS(10,4) → 14 chunks across 14 nodes
//    Tolerates 4 simultaneous node failures
//    40% storage overhead vs 200% for 3x replication

// 4. Versioning & MFA Delete
//    Accidental deletion protection
//    Object Lock for WORM compliance (SEC Rule 17a-4)

// Math: P(data loss) per object per year
// With 3 replicas, each on drives with 1% annual failure rate:
// P(all 3 fail) = (0.01)^3 = 10^-6 per year
// Plus repair time: replace failed replica in ~hours
// P(2nd fails during repair) = 0.01 × (repair_hours / 8760)
// Result: ~10^-11 annual durability per object`


Deep Dive: S3 Strong Consistency


Since December 2020, S3 provides strong read-after-write consistency at no extra cost:

`// Before 2020: eventual consistency for overwrite PUTs and DELETEs
// PUT new key → immediate consistency
// PUT overwrite → might read stale (seconds)
// DELETE → might still read deleted object

// After 2020: strong consistency for all operations
// Achieved via "witness" protocol:
// 1. Write goes to all replicas
// 2. Metadata index updated atomically
// 3. Subsequent reads always see latest write

// Implementation: "replication under consensus"
// - Index layer uses consensus protocol (similar to Paxos)
// - Read checks index version before returning data
// - No cache staleness: index is source of truth

// Performance: same latency as before!
// AWS achieved this by optimizing the consensus path
// to be as fast as the previous replication path`


## 🔄 Flow 3: VPC Networking & Security Groups


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1050 320" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="a3" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#4CAF50">
</polygon>
</marker>
</defs>


<rect x="20" y="50" width="120" height="50" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="80" text-anchor="middle" fill="white" font-size="12">
Customer VPC
</text>


<rect x="190" y="30" width="130" height="45" rx="8" fill="#1565C0">
</rect>
<text x="255" y="57" text-anchor="middle" fill="white" font-size="12">
Public Subnet
</text>


<rect x="190" y="90" width="130" height="45" rx="8" fill="#6A1B9A">
</rect>
<text x="255" y="117" text-anchor="middle" fill="white" font-size="12">
Private Subnet
</text>


<rect x="380" y="30" width="130" height="45" rx="8" fill="#E65100">
</rect>
<text x="445" y="57" text-anchor="middle" fill="white" font-size="12">
Internet GW
</text>


<rect x="380" y="90" width="130" height="45" rx="8" fill="#E65100">
</rect>
<text x="445" y="117" text-anchor="middle" fill="white" font-size="12">
NAT Gateway
</text>


<rect x="570" y="50" width="140" height="50" rx="8" fill="#00695C">
</rect>
<text x="640" y="70" text-anchor="middle" fill="white" font-size="12">
VXLAN Overlay
</text>
<text x="640" y="85" text-anchor="middle" fill="white" font-size="10">
Network
</text>


<rect x="770" y="50" width="140" height="50" rx="8" fill="#4E342E">
</rect>
<text x="840" y="70" text-anchor="middle" fill="white" font-size="12">
Blackfoot
</text>
<text x="840" y="85" text-anchor="middle" fill="white" font-size="10">
(Network Controller)
</text>


<rect x="380" y="200" width="130" height="50" rx="8" fill="#00695C">
</rect>
<text x="445" y="220" text-anchor="middle" fill="white" font-size="12">
Security Groups
</text>
<text x="445" y="235" text-anchor="middle" fill="white" font-size="10">
(eBPF/Nitro NIC)
</text>


<rect x="570" y="200" width="140" height="50" rx="8" fill="#6A1B9A">
</rect>
<text x="640" y="225" text-anchor="middle" fill="white" font-size="12">
Flow Logs → S3
</text>


<line x1="140" y1="65" x2="185" y2="52" stroke="#4CAF50" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="140" y1="85" x2="185" y2="112" stroke="#4CAF50" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="320" y1="52" x2="375" y2="52" stroke="#4CAF50" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="320" y1="112" x2="375" y2="112" stroke="#4CAF50" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="510" y1="75" x2="565" y2="75" stroke="#4CAF50" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="710" y1="75" x2="765" y2="75" stroke="#81D4FA" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="445" y1="135" x2="445" y2="195" stroke="#FF9900" stroke-width="2" marker-end="url(#a3)">
</line>


<line x1="510" y1="225" x2="565" y2="225" stroke="#FF9900" stroke-width="2" marker-end="url(#a3)">
</line>


</svg>

</details>


**Step 1:** Customer creates VPC with CIDR block (e.g., 10.0.0.0/16), subnets in each AZ
**Step 2:** VXLAN overlay encapsulates tenant traffic with VNI (Virtual Network Identifier) for isolation
**Step 3:** Blackfoot network controller programs SDN rules on physical switches/Nitro cards for routing
**Step 4:** Security groups enforced at Nitro NIC level (hardware firewall); NACLs at subnet level
**Step 5:** VPC Flow Logs capture network metadata to S3/CloudWatch for audit

Deep Dive: Multi-Tenancy Network Isolation

`// Challenge: millions of customers sharing same physical network
// Must ensure Customer A can NEVER access Customer B's traffic

// Solution: VXLAN overlay network
// Each VPC gets a unique VNI (24-bit → 16M possible VPCs)
// All tenant packets encapsulated:
// [Outer IP][Outer UDP:4789][VXLAN Header (VNI)][Inner Ethernet][Inner IP][Payload]

// Physical network sees only outer headers (customer IPs invisible)
// Nitro NIC does encap/decap in hardware (line rate)

// Security Group enforcement:
// - Stateful firewall at Nitro NIC level
// - Rules compiled to eBPF programs loaded onto NIC
// - Connection tracking in hardware
// - No performance penalty vs no security groups

// Zero Trust within VPC:
// - Instance metadata service (IMDS v2) uses session tokens
// - IAM roles via instance profile (no embedded credentials)
// - KMS for envelope encryption of EBS volumes`


## 🏗️ Combined Architecture


<details>
<summary>📊 Click to expand diagram</summary>

<svg viewbox="0 0 1100 550" style="width:100%;background:#111;border-radius:12px;margin:1rem 0">


<defs>
<marker id="ac" markerwidth="10" markerheight="7" refx="10" refy="3.5" orient="auto">
<polygon points="0 0,10 3.5,0 7" fill="#FF9900">
</polygon>
</marker>
</defs>


<text x="550" y="25" text-anchor="middle" fill="#FF9900" font-size="16" font-weight="bold">
AWS Core Infrastructure Architecture
</text>


<rect x="20" y="50" width="120" height="40" rx="8" fill="#2E7D32">
</rect>
<text x="80" y="75" text-anchor="middle" fill="white" font-size="11">
Customers
</text>


<rect x="200" y="50" width="140" height="40" rx="8" fill="#E65100">
</rect>
<text x="270" y="75" text-anchor="middle" fill="white" font-size="11">
API Gateway / SDKs
</text>


<rect x="200" y="110" width="140" height="40" rx="8" fill="#1565C0">
</rect>
<text x="270" y="135" text-anchor="middle" fill="white" font-size="11">
IAM (AuthN/AuthZ)
</text>


<rect x="400" y="50" width="130" height="40" rx="8" fill="#1565C0">
</rect>
<text x="465" y="75" text-anchor="middle" fill="white" font-size="11">
EC2 Control Plane
</text>


<rect x="400" y="110" width="130" height="40" rx="8" fill="#1565C0">
</rect>
<text x="465" y="135" text-anchor="middle" fill="white" font-size="11">
S3 Control Plane
</text>


<rect x="400" y="170" width="130" height="40" rx="8" fill="#1565C0">
</rect>
<text x="465" y="195" text-anchor="middle" fill="white" font-size="11">
VPC Control Plane
</text>


<rect x="400" y="230" width="130" height="40" rx="8" fill="#1565C0">
</rect>
<text x="465" y="255" text-anchor="middle" fill="white" font-size="11">
EBS Control Plane
</text>


<rect x="600" y="50" width="130" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="665" y="75" text-anchor="middle" fill="white" font-size="11">
Placement / Scheduler
</text>


<rect x="600" y="110" width="130" height="40" rx="8" fill="#00695C">
</rect>
<text x="665" y="135" text-anchor="middle" fill="white" font-size="11">
Nitro Hypervisors
</text>


<rect x="600" y="170" width="130" height="40" rx="8" fill="#4E342E">
</rect>
<text x="665" y="195" text-anchor="middle" fill="white" font-size="11">
S3 Storage Nodes
</text>


<rect x="600" y="230" width="130" height="40" rx="8" fill="#00695C">
</rect>
<text x="665" y="255" text-anchor="middle" fill="white" font-size="11">
EBS Storage Nodes
</text>


<rect x="800" y="50" width="140" height="40" rx="8" fill="#607D8B">
</rect>
<text x="870" y="75" text-anchor="middle" fill="white" font-size="11">
Physical Servers
</text>


<rect x="800" y="110" width="140" height="40" rx="8" fill="#607D8B">
</rect>
<text x="870" y="135" text-anchor="middle" fill="white" font-size="11">
Physical Network
</text>


<rect x="800" y="170" width="140" height="40" rx="8" fill="#607D8B">
</rect>
<text x="870" y="195" text-anchor="middle" fill="white" font-size="11">
Physical Storage
</text>


<rect x="200" y="350" width="140" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="270" y="375" text-anchor="middle" fill="white" font-size="11">
CloudWatch
</text>


<rect x="400" y="350" width="130" height="40" rx="8" fill="#6A1B9A">
</rect>
<text x="465" y="375" text-anchor="middle" fill="white" font-size="11">
Auto Scaling
</text>


<rect x="600" y="350" width="130" height="40" rx="8" fill="#E65100">
</rect>
<text x="665" y="375" text-anchor="middle" fill="white" font-size="11">
ELB
</text>


<rect x="800" y="350" width="140" height="40" rx="8" fill="#1565C0">
</rect>
<text x="870" y="375" text-anchor="middle" fill="white" font-size="11">
Route 53 (DNS)
</text>


<line x1="140" y1="70" x2="195" y2="70" stroke="#FF9900" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="340" y1="70" x2="395" y2="70" stroke="#FF9900" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="340" y1="130" x2="395" y2="130" stroke="#FF9900" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="530" y1="70" x2="595" y2="70" stroke="#FF9900" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="530" y1="130" x2="595" y2="130" stroke="#FF9900" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="530" y1="190" x2="595" y2="190" stroke="#FF9900" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="530" y1="250" x2="595" y2="250" stroke="#FF9900" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="70" x2="795" y2="70" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="130" x2="795" y2="130" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="190" x2="795" y2="190" stroke="#81D4FA" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="340" y1="370" x2="395" y2="370" stroke="#4CAF50" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="530" y1="370" x2="595" y2="370" stroke="#4CAF50" stroke-width="2" marker-end="url(#ac)">
</line>


<line x1="730" y1="370" x2="795" y2="370" stroke="#4CAF50" stroke-width="2" marker-end="url(#ac)">
</line>


</svg>

</details>


## 💾 Database Schema


### Control Plane — PostgreSQL / DynamoDB

`-- EC2 Instances (Control Plane State)
CREATE TABLE instances (
  instance_id VARCHAR(20) PRIMARY KEY, --  `PK` i-0abc123
  account_id VARCHAR(12) NOT NULL, --  `IDX`  `SHARD`
  region VARCHAR(20) NOT NULL,
  az VARCHAR(25) NOT NULL,
  instance_type VARCHAR(30) NOT NULL,
  state VARCHAR(20) NOT NULL, -- pending|running|stopping|terminated
  ami_id VARCHAR(25) NOT NULL,
  vpc_id VARCHAR(25), --  `FK`
  subnet_id VARCHAR(30), --  `FK`
  private_ip INET,
  public_ip INET,
  host_id VARCHAR(30) NOT NULL, -- physical server
  launch_time TIMESTAMPTZ NOT NULL,
  tags JSONB DEFAULT '{}'
);
--  `IDX`: (account_id, state) for listing
--  `IDX`: (host_id) for placement tracking
-- Sharded by account_id across regions

-- S3 Metadata (DynamoDB-style)
-- Key: (bucket_name, object_key)
-- Attributes: version_id, etag, size, storage_class,
--   content_type, encryption_key_id, acl, last_modified
-- GSI: (bucket_name, last_modified) for listing
-- Partition: hash(bucket_name) for even distribution`


### Capacity & Placement

`-- Host Capacity Tracking
CREATE TABLE hosts (
  host_id VARCHAR(30) PRIMARY KEY, --  `PK`
  region VARCHAR(20) NOT NULL,
  az VARCHAR(25) NOT NULL, --  `IDX`
  rack_id VARCHAR(20) NOT NULL,
  total_vcpus INT NOT NULL,
  total_memory_gb INT NOT NULL,
  available_vcpus INT NOT NULL, --  `IDX`
  available_memory_gb INT NOT NULL,
  instance_types_supported TEXT[], -- array of types
  nitro_version VARCHAR(10),
  status VARCHAR(15) DEFAULT 'active'
);
--  `IDX`: (az, available_vcpus, available_memory_gb)
-- for placement queries

-- Network State
-- VPC configs, ENIs, route tables, security group rules
-- stored in distributed KV store (similar to etcd)
-- replicated within region for consistency
-- Blackfoot controller pushes state to physical switches`


## ⚡ Cache & CDN Deep Dive


### Multi-Layer Caching in AWS


| Layer                    | What                               | Strategy                        | TTL                      |
|--------------------------|------------------------------------|---------------------------------|--------------------------|
| CloudFront (CDN)         | Static content, API responses      | Pull-based, 400+ edge locations | Per-object Cache-Control |
| ElastiCache              | Application data (Redis/Memcached) | Customer-managed                | Customer-defined         |
| DAX (DynamoDB)           | DynamoDB read cache                | Write-through                   | 5 min default            |
| S3 Transfer Acceleration | Upload optimization                | Edge → backbone → S3            | N/A                      |
| EC2 Instance Store       | Ephemeral local NVMe               | Local to host                   | Until termination        |


EBS I/O Path (Caching)
`// EBS volumes are network-attached storage
// Latency: ~0.5ms for io2 Block Express (vs ~0.1ms local SSD)

// Caching layers for EBS:
// 1. Guest OS page cache (customer's responsibility)
// 2. Nitro card NVMe cache (transparent, SSD buffer)
// 3. EBS node SSD cache (hot blocks)
// 4. EBS node HDD (cold data, for gp3/st1)

// Multi-attach: up to 16 instances can share one io2 volume
// Throughput: up to 4,000 MB/s (io2 Block Express)`


## 📈 Scaling Considerations


### Global Infrastructure Scaling


- **Regions:** each region is fully independent (control planes, data planes, IAM). Region failure doesn't affect others. Cross-region via explicit replication (S3 CRR, DynamoDB Global Tables).

- **Availability Zones:** each AZ is 1+ data centers with independent power/cooling/network, connected via low-latency links (<2ms). Default: spread resources across 3 AZs.

- **Cell-based architecture:** AWS services use cellular architecture. Each "cell" handles a subset of customers. Blast radius limited to one cell on failure. Example: each S3 partition is a cell.

- **Shuffle sharding:** each customer assigned to random subset of cells. With 8 cells and 2-cell assignment, P(two customers sharing both cells) = 1/28. Minimizes correlated failures.


### Auto Scaling

`// Auto Scaling Group (ASG) config:
{
  "min": 2, "max": 100, "desired": 4,
  "scaling_policies": [{
    "type": "TargetTrackingScaling",
    "target_metric": "CPUUtilization",
    "target_value": 70.0,
    "scale_in_cooldown": 300,
    "scale_out_cooldown": 60
  }],
  "predictive_scaling": true // ML-based, pre-provisions before demand spikes
}`


## ⚖️ Tradeoffs


> **
Multi-AZ Replication
- High availability (survive AZ failure)
- Low-latency cross-AZ (<2ms)
- 11 nines durability


Multi-AZ Replication
- 3x storage cost (3 replicas)
- Cross-AZ data transfer charges
- Write latency increased by replication


Nitro Hardware Offload
- 100% host resources to customer
- Hardware security isolation
- Line-rate network processing


Nitro Hardware Offload
- Custom silicon development cost
- Hardware iteration slower than software
- Debugging complexity


## 🔄 Alternative Approaches


Containers (ECS/EKS/Fargate)

Instead of VMs, use containers for faster start (seconds vs minutes), higher density, and microservice orchestration. Fargate provides serverless containers (no server management). Trade isolation guarantee for efficiency.

Serverless (Lambda)

Event-driven compute with no provisioning. Pay per invocation (100ms granularity). Cold start challenge (~100-500ms). Best for intermittent workloads. Uses Firecracker microVMs for isolation.

Bare Metal (Outposts)

AWS infrastructure on-premises. For latency-sensitive, data-sovereignty, or hybrid workloads. Same APIs as cloud. Higher cost but full control over physical hardware.


## 📚 Additional Information


- **Firecracker:** open-source microVM manager (powers Lambda & Fargate). Boots in <125ms, ~5MB memory overhead per VM. Written in Rust for safety.

- **Graviton:** custom ARM processors. 40% better price-performance vs x86. Nitro + Graviton = fully custom stack.

- **Availability math:** 99.99% = 52 min downtime/year. Multi-AZ: if each AZ is 99.9%, two AZs give 1 - (0.001)² = 99.9999%.

- **Shared responsibility model:** AWS secures infrastructure (OF the cloud), customer secures their configs/data (IN the cloud).

- **Cost optimization:** Reserved Instances (1-3yr commit, ~60% savings), Spot (up to 90% savings, can be interrupted), Savings Plans (flexible commitment).