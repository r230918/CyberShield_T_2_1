# CyberShield: Host-Based Security Log Analysis and Suspicious-Event Detection System Using DSA

CyberShield is an interactive Java command-line application that analyzes host-based security logs, identifies suspicious and malicious events, detects repeated failed-login bursts using sliding-window tracking, and searches threat patterns. The system demonstrates the practical application of Data Structures and Algorithms (DSA) across six core coursework outcomes (CO1–CO6).

---

## 1. Project Title & Objective

**Project Title:** CyberShield: Host-Based Security Log Analysis and Suspicious-Event Detection System  
**Objective:** Provide an understandable, reliable, and demonstrably correct software platform to ingest, filter, search, and analyze host security events (authentication, access control, malware indications, and system anomalies) using classic and advanced DSA algorithms.

---

## 2. Problem Statement

Modern enterprise hosts (servers, workstations, and domain controllers) generate massive streams of audit events. Security Operations Center (SOC) analysts and incident responders face significant challenges:
1. **High Log Volume:** Manual inspection of thousands of log records is slow and error-prone.
2. **Signature Detection:** Rapidly identifying known threat signatures (e.g., malware indicators, privilege escalation attempts, injection strings) across thousands of text records requires efficient pattern-matching algorithms.
3. **Brute-Force & Burst Detection:** Credential guessing attacks must be distinguished from isolated typo errors by correlating failed logins over sliding time windows rather than cumulative all-time counters.
4. **Masquerading & Typosquatting:** Attackers frequently masquerade malicious binary names (e.g., `powreshell.exe`, `svch0st.exe`) to blend into legitimate processes.

CyberShield demonstrates how fundamental data structures (tries, suffix arrays, queues, graphs) and algorithms (Aho-Corasick, KMP, Levenshtein, network flow, dynamic programming) directly solve these log analysis challenges.

---

## 3. Host-Based Security-Log Analysis Scope

CyberShield focuses on **host-based security-log analysis** of imported CSV records and simulated event streams:

- **Host-Centric Events Analyzed:**
  - Successful and failed user logins.
  - Repeated failed logins within sliding time windows (brute-force detection).
  - Unauthorized file or administrative access attempts.
  - Malware and ransomware indicators in host filesystems.
  - Privilege escalation attempts on system drivers.
  - Network indicators (port scans and suspicious outbound connections) originating from or targeting specific hosts.
- **Explicit Scope Boundaries:**
  - CyberShield processes historical log files (CSV format) and simulated real-time event queues.
  - It does **not** perform live operating system kernel hook monitoring, live packet capture (pcap), or live malware execution.
  - Matches represent **suspicious indicators** flagging events for analyst review, avoiding false claims of fully automated production intrusion prevention.

---

## 4. Implemented Features

1. **Security Log Management & Ingestion:**
   - Ingests CSV log files with automatic header validation, token extraction, and RFC-4180 quoting support.
   - Comprehensive log statistics (distribution across severity levels, event types, active host list, and timestamp range).
   - Duplicate record detection and graceful handling of malformed lines.
   - Export capability for filtered logs and incident findings.

2. **Search and Filtering Engine:**
   - Multi-algorithm text search: Knuth-Morris-Pratt (KMP), Z-Algorithm, Rabin-Karp, and Suffix Array Binary Search.
   - Filtering by Host ID (e.g., `HOST-01`), Event Type (`LOGIN`, `MALWARE`, `NETWORK`, `ACCESS`, `SYSTEM`), and Severity (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).

3. **Threat Signature Matching (IDS Engine):**
   - Implements the **Aho-Corasick** multi-pattern search automaton to match all active rules (R01–R09) simultaneously in a single linear pass $O(N + \sum M_i)$.
   - Dynamic rule management: inspect, add, or remove custom detection signatures at runtime.

4. **Sliding-Window Failed-Login Burst Detection:**
   - Dedicated `FailedLoginDetector` tracking failed logins per host within a configurable time window (default: 3 failures within 300 seconds).
   - Successful logins do **not** increase the failed login counter.
   - Automatic eviction of expired entries from the sliding window.
   - Safe parsing and fallback handling for missing or malformed timestamps.
   - Duplicate alert suppression during ongoing bursts, with fresh alerting when a new burst occurs after window expiration.

5. **Text Forensics & Masquerade Analysis:**
   - Suffix Array & Kasai LCP construction for detecting recurring exploit loops and repeated payload phrases.
   - Levenshtein Distance, Damerau-Levenshtein, and Weighted Edit Distance for detecting process masquerades, phishing domains, and command-line transposition typos.

6. **Advanced DSA Coursework Demonstrations:**
   - Network flow algorithms (Ford-Fulkerson, Edmonds-Karp, Dinic).
   - Critical bottleneck partition (Minimum Cut).
   - Analyst ticket allocation (Maximum Bipartite Matching).
   - Policy consistency checking (Boolean SAT / 3-SAT Solver).
   - Collusion and cluster analysis (Maximum Clique & Maximum Independent Set).
   - Sensor placement optimization (Exact Vertex Cover vs. 2-Approximation).
   - Pipeline filter optimization (Matrix Chain Multiplication DP).
   - Auditor tour latency (Bitmask DP / TSP).
   - Randomized algorithms (Miller-Rabin, Randomized QuickSort, Reservoir Sampling).
   - Parallel prefix scan and Brent's Theorem bounds.

---

## 5. Technologies Used

- **Language:** Java (JDK 17+ compatible, standard Java SE).
- **Libraries:** Pure Java Standard Library (`java.util`, `java.io`, `java.time`, `java.math`).
- **Dependencies:** Zero external third-party dependencies or frameworks required.
- **Build Tool:** Standard `javac` and `java` commands.

---

## 6. Project Structure

```
Final_CyberSheild/
│
├── Main.java                          # System entry point and SOC banner
├── MenuManager.java                   # User-task CLI menu and workflow router
├── SecurityLog.java                   # Core security event data model
├── CSVLogManager.java                 # CSV parser, log summary, filter, and export
├── FailedLoginDetector.java           # Sliding-window failed-login burst detector
├── DetectionRule.java                 # Threat signature rule definition model
├── DetectionEngine.java               # Aho-Corasick rule evaluation engine
├── LogStreamProcessor.java            # Simulated stream processor and callback handler
├── PerformanceAnalyzer.java           # Nanosecond benchmarking for algorithms
├── InputValidator.java                # Safe terminal input scanner
├── TestRunner.java                    # Automated test suite (94/94 tests PASS)
│
├── [CO1: String Searching]
│   ├── KMP.java                       # Knuth-Morris-Pratt pattern search
│   ├── ZAlgorithm.java                # Z-Algorithm string matching
│   ├── RabinKarp.java                 # Rabin-Karp rolling hash search
│   └── AhoCorasick.java               # Aho-Corasick multi-pattern trie automaton
│
├── [CO2: Suffix Structures]
│   ├── SuffixArray.java               # Comparison-based suffix array
│   ├── SAIS.java                      # Linear time O(N) Suffix Array Induced Sorting
│   ├── KasaiLCP.java                  # Kasai linear LCP array construction
│   ├── SuffixArraySearch.java         # Suffix array binary search pattern query
│   ├── SuffixTree.java                # Suffix tree construction & substring query
│   └── SuffixAutomaton.java           # Directed Acyclic Word Graph (DAWG)
│
├── [CO3: Dynamic Programming]
│   ├── LevenshteinDistance.java       # Edit distance with backtrace transformation
│   ├── DamerauLevenshtein.java        # Edit distance with adjacent transpositions
│   ├── WeightedEditDistance.java      # Custom insertion/deletion/substitution costs
│   ├── MatrixChainMultiplication.java # DP optimal parenthesization for pipelines
│   └── BitmaskDP.java                 # Traveling Security Auditor (Held-Karp TSP)
│
├── [CO4: Network Flow]
│   ├── FlowGraph.java                 # Residual network flow graph representation
│   ├── FordFulkerson.java             # Ford-Fulkerson DFS maximum flow
│   ├── EdmondsKarp.java               # Edmonds-Karp BFS maximum flow
│   ├── Dinic.java                     # Dinic level-graph blocking flow
│   ├── MinimumCut.java                # Max-Flow Min-Cut bottleneck partition
│   └── BipartiteMatching.java         # Ticket assignment via reduction to max flow
│
├── [CO5: NP-Complete & Reductions]
│   ├── SATSolver.java                 # Boolean SAT / 3-SAT DPLL backtracking solver
│   ├── Clique.java                    # Bron-Kerbosch maximum clique search
│   ├── IndependentSet.java            # Maximum independent set via complement graph
│   ├── VertexCover.java               # Exact minimum vertex cover via Gallai reduction
│   └── VertexCoverApproximation.java  # 2-Approximation for vertex cover
│
├── [CO6: Randomized & Parallel]
│   ├── MillerRabin.java               # Miller-Rabin probabilistic primality test
│   ├── RandomizedQuickSort.java       # Randomized pivot QuickSort with controlled seed
│   ├── ReservoirSampling.java         # Reservoir Sampling (Algorithm R)
│   ├── PrefixSum.java                 # Sequential cumulative prefix sum
│   ├── ParallelReduction.java         # Parallel fork-join sum reduction
│   └── ParallelPrefixSum.java         # Parallel prefix scan & Brent's Theorem bounds
│
└── data/
    └── security_logs.csv              # Default host security event dataset
```

---

## 7. Compilation & Execution Instructions

From the root project directory:

### Compile All Source Files:
```cmd
javac *.java
```

### Run Application:
```cmd
java Main
```

### Run Automated System Test Suite:
```cmd
java TestRunner
```

---

## 8. CSV Input Format & Example

The system ingests comma-separated value (CSV) logs with a 5-column header:

```csv
timestamp,source,event_type,severity,description
```

### Header Specifications:
- `timestamp`: Date and time in `yyyy-MM-dd HH:mm:ss` format.
- `source`: Host identifier (e.g., `HOST-01`, `HOST-02`, `SRV-DB`).
- `event_type`: Categorical event (`LOGIN`, `NETWORK`, `MALWARE`, `ACCESS`, `SYSTEM`).
- `severity`: Alert criticality (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).
- `description`: Textual event message or audit description (supports commas if enclosed in quotes).

### Example Records (`data/security_logs.csv`):
```csv
timestamp,source,event_type,severity,description
2026-10-12 09:00:00,HOST-01,LOGIN,LOW,Successful login for user admin
2026-10-12 09:05:00,HOST-02,LOGIN,HIGH,Multiple failed login attempts from IP 192.168.1.50
2026-10-12 09:06:00,HOST-01,NETWORK,MEDIUM,Unusual connection attempt on port 4444
2026-10-12 09:08:00,HOST-03,MALWARE,CRITICAL,Suspicious malware signature detected in temp directory
2026-10-12 09:10:00,HOST-02,ACCESS,HIGH,Unauthorized access attempt to confidential records
2026-10-12 09:15:00,HOST-01,MALWARE,CRITICAL,Ransomware encryption activity suspected in user folder
2026-10-12 09:18:00,HOST-05,LOGIN,MEDIUM,Password brute force pattern detected
2026-10-12 09:20:00,HOST-03,SYSTEM,HIGH,Privilege escalation attempt using vulnerable driver
```

---

## 9. Operating the CLI Workflow

The main menu is structured directly around standard SOC investigative steps:

```
====================================================
                    CYBERSHIELD                     
       Host-Based Security Log Analysis System      
====================================================
  1. Load Security Logs from CSV
  2. View Log Summary
  3. Search Logs by Keyword or Threat Pattern
  4. Filter Logs by Host, Event Type, or Severity
  5. Analyze Suspicious Events (Rule Matching)
  6. Detect Repeated Failed-Login Activity
  7. Search Configured Threat Signatures (Aho-Corasick)
  8. Run Text-Analysis & Similarity Operations (DSA)
  9. Run Advanced Algorithm Demonstrations (CO4-CO6)
 10. Run Automated System Tests

  0. Exit
----------------------------------------------------
```

### Standard Demonstration Sequence (2-Minute Demo):
1. **Option 1:** Load default dataset (`data/security_logs.csv`).
2. **Option 2:** View log summary (see counts across LOW, MEDIUM, HIGH, CRITICAL, and monitored hosts).
3. **Option 4:** Filter logs by severity (`HIGH` or `CRITICAL`) or host (`HOST-01`).
4. **Option 5:** Run rule matching to flag known threat signatures (R01–R09).
5. **Option 6:** Run repeated failed-login burst detection (Option 6.1 on logs, or Option 6.2 for real-time simulation).
6. **Option 8:** Demonstrate Levenshtein edit distance for process masquerading (`svchost.exe` vs. `svch0st.exe`).
7. **Option 10:** Run automated test suite to show 94/94 tests passing.

---

## 10. Course Outcome (CO) Mapping Table

| Requirement / CO | Feature | Relevant Class | Algorithm / Data Structure | Evidence / Test |
|---|---|---|---|---|
| **CO1: String Algorithms** | Log search & multi-signature IDS matching | `KMP.java`, `ZAlgorithm.java`, `RabinKarp.java`, `AhoCorasick.java` | KMP (LPS table), Z-array, Rolling Hash, Aho-Corasick Automaton | Tests 1–8 (`TestRunner.java`) |
| **CO2: Suffix Structures** | Recurring exploit detection & fast phrase queries | `SuffixArray.java`, `SAIS.java`, `KasaiLCP.java`, `SuffixArraySearch.java`, `SuffixTree.java`, `SuffixAutomaton.java` | Suffix Array, SA-IS ($O(N)$), Kasai LCP, Binary Search, Suffix Tree/DAWG | Tests 9–20 (`TestRunner.java`) |
| **CO3: Dynamic Programming** | Phishing/masquerade detection & pipeline optimization | `LevenshteinDistance.java`, `DamerauLevenshtein.java`, `WeightedEditDistance.java`, `MatrixChainMultiplication.java`, `BitmaskDP.java` | 2D DP Edit Distance, Damerau Adjacent Transposition, MCM DP, Held-Karp Bitmask DP | Tests 21–25 (`TestRunner.java`) |
| **CO4: Network Flow** | Network throughput, bottleneck cut & analyst ticket allocation | `FlowGraph.java`, `FordFulkerson.java`, `EdmondsKarp.java`, `Dinic.java`, `MinimumCut.java`, `BipartiteMatching.java` | Residual Graph, BFS Augmenting Paths, Dinic Level Graph, Max-Flow Min-Cut Theorem, Bipartite Reduction | Tests 26–30 (`TestRunner.java`) |
| **CO5: NP-Complete & Graphs** | Security policy verification, cluster analysis & sensor placement | `SATSolver.java`, `Clique.java`, `IndependentSet.java`, `VertexCover.java`, `VertexCoverApproximation.java` | DPLL Backtracking, Bron-Kerbosch with Pivot, Complement Graph Reduction, Gallai's Theorem ($V - IS$), Maximal Matching 2-Approx | Tests 31–36 (`TestRunner.java`) |
| **CO6: Randomized & Parallel** | Cryptographic key tests, stream sampling & parallel log scans | `MillerRabin.java`, `RandomizedQuickSort.java`, `ReservoirSampling.java`, `PrefixSum.java`, `ParallelReduction.java`, `ParallelPrefixSum.java` | Miller-Rabin Modular Exponentiation, Randomized Pivot Selection, Algorithm R, Parallel Prefix Scan, Brent's Theorem | Tests 37–43 (`TestRunner.java`) |
| **Storage & Detection** | CSV ingestion, host filtering, and rule evaluation | `CSVLogManager.java`, `DetectionEngine.java`, `SecurityLog.java` | Hash indexing, ArrayList, Aho-Corasick multi-pattern evaluation | Tests 44–48, 77–88 (`TestRunner.java`) |
| **Host-Based Burst Detection** | Sliding-window failed login burst detection & stream simulation | `FailedLoginDetector.java`, `LogStreamProcessor.java` | Deque sliding window, timestamp eviction, burst threshold tracking | Tests 49–51, 52–71 (`TestRunner.java`) |

---

## 11. Algorithm Classification & Time Complexities

### Category A: Integrated Core Workflow Algorithms

| Algorithm | File | Role in CyberShield | Time Complexity | Space Complexity |
|---|---|---|---|---|
| **Aho-Corasick** | `AhoCorasick.java` | Multi-pattern threat signature scanner (rules R01–R09) | $O(N + \sum M_i + Z)$ | $O(\sum M_i)$ |
| **Knuth-Morris-Pratt (KMP)** | `KMP.java` | Keyword search in security log descriptions | $O(N + M)$ | $O(M)$ |
| **Z-Algorithm** | `ZAlgorithm.java` | Direct pattern matching across log strings | $O(N + M)$ | $O(N + M)$ |
| **Rabin-Karp** | `RabinKarp.java` | Rolling hash pattern search in descriptions | Avg: $O(N + M)$, Worst: $O(NM)$ | $O(1)$ |
| **Sliding Window Deque** | `FailedLoginDetector.java` | Time-windowed failed-login burst detection | $O(1)$ amortized per event | $O(K)$ per host |
| **Suffix Array + Kasai LCP** | `SuffixArray.java`, `KasaiLCP.java` | Unsupervised longest repeated pattern forensics | Build: $O(N^2 \log N)$, LCP: $O(N)$ | $O(N)$ |
| **Suffix Array Binary Search** | `SuffixArraySearch.java` | Binary search query over indexed log text | $O(M \log N)$ | $O(1)$ |
| **Levenshtein Distance** | `LevenshteinDistance.java` | Process masquerade detection (`svchost` vs `svch0st`) | $O(\|S_1\| \cdot \|S_2\|)$ | $O(\|S_1\| \cdot \|S_2\|)$ |
| **Damerau-Levenshtein** | `DamerauLevenshtein.java` | Detecting adjacent character transposition typos | $O(\|S_1\| \cdot \|S_2\|)$ | $O(\|S_1\| \cdot \|S_2\|)$ |
| **Weighted Edit Distance** | `WeightedEditDistance.java` | Configurable penalty cost similarity comparison | $O(\|S_1\| \cdot \|S_2\|)$ | $O(\|S_1\| \cdot \|S_2\|)$ |

### Category B: Standalone Coursework Demonstrations (CO4–CO6)

| Algorithm | File | Demonstration Use Case | Time Complexity |
|---|---|---|---|
| **SA-IS** | `SAIS.java` | Linear-time Suffix Array Induced Sorting | $O(N)$ |
| **Suffix Tree** | `SuffixTree.java` | Substring query and suffix indexing | $O(N^2)$ simple / $O(N)$ Ukkonen |
| **Suffix Automaton** | `SuffixAutomaton.java` | Substring acceptance verification (DAWG) | $O(N)$ |
| **Edmonds-Karp** | `EdmondsKarp.java` | Network throughput via BFS shortest paths | $O(V \cdot E^2)$ |
| **Ford-Fulkerson** | `FordFulkerson.java` | Network throughput via DFS paths | $O(E \cdot \text{max\_flow})$ |
| **Dinic's Algorithm** | `Dinic.java` | Level graph blocking flow network throughput | $O(V^2 \cdot E)$ |
| **Minimum Cut** | `MinimumCut.java` | Partition isolating source from sink | $O(V \cdot E^2)$ |
| **Bipartite Matching** | `BipartiteMatching.java` | Security analyst incident ticket assignment | $O(V^2 \cdot E)$ |
| **SAT Solver (DPLL)** | `SATSolver.java` | Boolean security policy contradiction check | $O(2^V)$ worst-case |
| **Bron-Kerbosch (Clique)** | `Clique.java` | Host collusion cluster detection | $O(3^{V/3})$ worst-case |
| **Independent Set** | `IndependentSet.java` | Non-interfering host subset via complement graph | $O(3^{V/3})$ |
| **Exact Vertex Cover** | `VertexCover.java` | Sensor placement optimization via Gallai reduction | $O(3^{V/3})$ |
| **2-Approx Vertex Cover** | `VertexCoverApproximation.java` | Linear-time approximation for sensor placement | $O(V + E)$ |
| **Matrix Chain Mult.** | `MatrixChainMultiplication.java` | Optimal log pipeline filter order parenthesization | $O(K^3)$ |
| **Bitmask DP (TSP)** | `BitmaskDP.java` | Minimum-cost traveling security auditor tour | $O(2^V \cdot V^2)$ |
| **Miller-Rabin** | `MillerRabin.java` | Probabilistic primality test for crypto keys | $O(k \log^3 N)$ |
| **Randomized QuickSort** | `RandomizedQuickSort.java` | Adversary-resistant sorting with random pivot | Expected: $O(N \log N)$ |
| **Reservoir Sampling** | `ReservoirSampling.java` | Uniform stream sampling (Algorithm R) | $O(N)$ |
| **Parallel Prefix Sum** | `ParallelPrefixSum.java` | Parallel prefix scan with Brent's bound analysis | Work: $O(N)$, Span: $O(\log N)$ |

---

## 12. Verified Test Results

Executing `java TestRunner` produces:

```
====================================================
         RUNNING AUTOMATED SYSTEM TESTS             
====================================================

[Testing CO1: String Algorithms]
  [PASS] KMP pattern match count
  [PASS] KMP first match position
  [PASS] KMP second match position
  [PASS] Z-Algorithm matches KMP
  [PASS] Rabin-Karp matches KMP
  [PASS] Empty pattern handling
  [PASS] Pattern longer than text
  [PASS] Aho-Corasick multiple pattern occurrences

[Testing CO2: Suffix Structures]
  [PASS] Suffix Array banana first element
  [PASS] Suffix Array banana second element
  [PASS] SAIS matches standard SuffixArray
  [PASS] SuffixTree contains substring 'nan'
  [PASS] SuffixTree does not contain 'xyz'
  [PASS] SuffixAutomaton contains substring 'ana'
  [PASS] SuffixAutomaton does not contain 'xyz'
  [PASS] Kasai LCP between ana and anana
  [PASS] Longest repeated substring banana
  [PASS] Suffix Array Binary Search match count
  [PASS] Suffix Array Binary Search match pos 1
  [PASS] Suffix Array Binary Search match pos 2

[Testing CO3: Dynamic Programming]
  [PASS] Levenshtein distance kitten -> sitting
  [PASS] Damerau-Levenshtein transposition 'teh' -> 'the'
  [PASS] Weighted edit distance substitution cost
  [PASS] MCM optimal multiplications
  [PASS] Bitmask TSP optimal cycle cost

[Testing CO4: Network Flow]
  [PASS] Edmonds-Karp max flow
  [PASS] Ford-Fulkerson max flow
  [PASS] Dinic max flow
  [PASS] Min-Cut capacity equals Max Flow
  [PASS] Bipartite Matching max matches

[Testing CO5: NP-Complete & Graphs]
  [PASS] SAT correctly identifies unsatisfiable formula
  [PASS] SAT correctly finds satisfying assignment
  [PASS] Maximum Clique size is 3
  [PASS] Maximum Independent Set size is 2
  [PASS] Exact Vertex Cover size is 2 (V - IS)
  [PASS] 2-Approx Vertex Cover is valid and <= 2 * OPT

[Testing CO6: Randomized & Parallel]
  [PASS] Miller-Rabin verifies prime 104729
  [PASS] Miller-Rabin identifies composite 104730
  [PASS] Randomized QuickSort correctly sorts array
  [PASS] Reservoir sample size is exactly K
  [PASS] Sequential and Parallel Prefix Sum are identical
  [PASS] Prefix sum last element
  [PASS] Parallel reduction sum matches sequential

[Testing Storage and Detection Rules]
  [PASS] CSVLogManager total logs
  [PASS] Filter by severity HIGH
  [PASS] Search by keyword 'failed'
  [PASS] Detection engine matches rule R01
  [PASS] Matched rule pattern

[Testing Simulated Event Stream]
  [PASS] Stream correctly flags suspicious event
  [PASS] Stream measures non-negative algorithm execution time
  [PASS] Stream flagged logs count

[Testing Failed Login Detection & Time-Window Burst Logic]
  [PASS] One successful login does not count as failed login
  [PASS] One successful login window count is 0
  [PASS] One successful login does not trigger burst
  [PASS] One failed login recognized
  [PASS] One failed login window count is 1
  [PASS] One failed login does not reach threshold (1 < 3)
  [PASS] Second failed login window count is 2
  [PASS] Failed logins below threshold do not trigger burst (2 < 3)
  [PASS] Third failed login window count reaches threshold (3)
  [PASS] Third failed login triggers burst alert
  [PASS] Burst result explanation contains window and count
  [PASS] Mixed successful logins do not increment failed login counter (count is 2)
  [PASS] Mixed logins below threshold do not trigger burst
  [PASS] Old events outside window are evicted (window count is 1)
  [PASS] Failed login outside time window does not trigger burst
  [PASS] Null timestamp event handled safely without exception
  [PASS] Malformed timestamp event handled safely without exception
  [PASS] Invalid timestamp events tracked safely in window
  [PASS] First burst triggers alert
  [PASS] Fresh burst after earlier burst expired triggers new alert

[Testing CSV Validation, Quoting, and Edge Cases]
  [PASS] CSV quoted line tokens count
  [PASS] CSV quoted token preserved commas
  [PASS] Non-existent CSV returns failure
  [PASS] Header validation accepts valid required headers
  [PASS] Header validation rejects headers with missing required columns
  [PASS] Valid row passes validation
  [PASS] Bad timestamp flagged as malformed
  [PASS] Empty field flagged as malformed
  [PASS] Short row flagged as malformed
  [PASS] Default dataset imported successfully
  [PASS] Default dataset loaded count >= 10
  [PASS] Default dataset malformed count is 0
  [PASS] Append succeeds
  [PASS] Duplicates detected when appending same dataset

[Testing Host Filtering, Summary & Batch Burst Detection]
  [PASS] Filter by host HOST-01 finds records
  [PASS] Filtered log has source HOST-01
  [PASS] Filtered log has source HOST-01
  [PASS] Filtered log has source HOST-01
  [PASS] Summary contains totalLogs
  [PASS] Summary contains severityBreakdown
  [PASS] Summary contains uniqueHosts
  [PASS] Summary contains timeSpan
  [PASS] Batch burst detection executes without errors
----------------------------------------------------
Total Tests: 94 | Passed: 94 | Failed: 0
ALL SYSTEM TESTS PASSED SUCCESSFULLY! (100% Correctness)
====================================================
```

---

## 13. Viva Questions & Beginner-Friendly Answers

### Q1: What is the primary purpose of CyberShield?
**Answer:** CyberShield is a host-based security log analysis system. It ingests computer and server audit logs (such as login records, file access, and malware notices), identifies suspicious threat patterns using string and graph algorithms, and detects brute-force credential attacks within defined time windows.

### Q2: Why is Aho-Corasick preferred over multiple independent KMP searches for threat signatures?
**Answer:** If we have $k$ signature patterns of length $M$ and a log description of length $N$, running KMP $k$ times takes $O(k \cdot (N + M))$ time. Aho-Corasick builds a trie with failure transitions in $O(\sum M)$ preprocessing time and then scans the log text in a single pass of $O(N + \sum M + Z)$ time, where $Z$ is the number of matches. This makes signature scanning independent of the number of active rules.

### Q3: How did you resolve the failed-login detection bug?
**Answer:** Previously, any log with event type `LOGIN` (including successful logins) incremented an all-time counter without time limits. We resolved this by:
1. Validating that only authentication failure events increment the counter, while successful logins are explicitly ignored.
2. Implementing a sliding-window queue (`Deque<Long>`) per host that tracks timestamps in epoch seconds.
3. Evicting timestamps older than the configured window (e.g., 300 seconds) so that isolated login attempts hours apart do not trigger false alerts.
4. Suppressing duplicate alerts within the same ongoing burst and cleanly resetting when a fresh burst occurs after window expiration.

### Q4: How is Levenshtein edit distance used in host security?
**Answer:** Attackers frequently deploy malware masquerading under legitimate system process names or register deceptive domain names (e.g., `svch0st.exe` instead of `svchost.exe`, or `bankc0rp.com` instead of `bankcorp.com`). Levenshtein Distance calculates the minimum single-character insertions, deletions, and substitutions needed to transform one string into another. An edit distance of 1 or 2 flags high-risk typosquatting or process masquerading.

### Q5: What is the relationship between Maximum Clique, Independent Set, and Vertex Cover?
**Answer:** These are Karp's classic polynomial-time reductions in NP-completeness:
- A clique of size $k$ in graph $G$ corresponds exactly to an Independent Set of size $k$ in the complement graph $\bar{G}$.
- Gallai's Theorem states that for any graph $G = (V, E)$, a subset $S \subseteq V$ is an Independent Set if and only if $V \setminus S$ is a Vertex Cover. Therefore, $|IS| + |VC| = |V|$.
- Thus, solving Maximum Clique immediately solves Maximum Independent Set and Minimum Vertex Cover.

### Q6: Why does 2-Approximation for Vertex Cover run in polynomial time while exact Vertex Cover is NP-Hard?
**Answer:** Finding the exact minimum vertex cover is NP-Hard ($O(2^V)$). The 2-approximation algorithm greedily selects maximal matching edges: it picks an arbitrary edge $(u, v)$, adds both endpoints $u$ and $v$ to the cover, and deletes all incident edges. This runs in linear time $O(V + E)$ and is mathematically guaranteed to select at most $2 \times$ the optimal minimum cover size.

---

## 14. Current Limitations & Future Improvements

1. **In-Memory Storage:** The current implementation stores imported records in JVM memory. For datasets exceeding millions of records, off-heap storage or streaming disk buffers would be beneficial.
2. **Pre-defined Signatures:** Signatures are currently string patterns evaluated via Aho-Corasick. Regular expression support (Regex NFA/DFA) could be added for variable threat patterns.
3. **Simulated Stream:** Streaming events are generated programmatically or read sequentially from CSV files. Integration with live OS syslog sockets or Windows Event Log collectors could be added in future work.
4. **Time Zone Alignment:** Timestamps are currently evaluated using UTC epoch seconds. Cross-timezone normalization can be expanded for multi-region server clusters.
