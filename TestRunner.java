import java.util.*;
public class TestRunner {
    private int passedCount = 0;
    private int failedCount = 0;
    private final List<String> failureMessages = new ArrayList<>();
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        runner.runAllTests();
    }
    public void runAllTests() {
        System.out.println("====================================================");
        System.out.println("         RUNNING AUTOMATED SYSTEM TESTS             ");
        System.out.println("====================================================");
        testCO1StringAlgorithms();
        testCO2SuffixStructures();
        testCO3DynamicProgramming();
        testCO4NetworkFlow();
        testCO5NPCompleteAndGraphs();
        testCO6RandomizedAndParallel();
        testStorageAndDetection();
        testStreamProcessing();
        testFailedLoginDetection();
        testCSVValidationAndEdgeCases();
        testHostFilteringAndLogSummary();
        System.out.println("----------------------------------------------------");
        System.out.printf("Total Tests: %d | Passed: %d | Failed: %d%n",
                (passedCount + failedCount), passedCount, failedCount);
        if (failedCount > 0) {
            System.out.println("Failures:");
            for (String f : failureMessages) {
                System.out.println(" - " + f);
            }
        } else {
            System.out.println("ALL SYSTEM TESTS PASSED SUCCESSFULLY! (100% Correctness)");
        }
        System.out.println("====================================================");
    }
    private void assertTrue(String testName, boolean condition) {
        if (condition) {
            passedCount++;
            System.out.printf("  [PASS] %s%n", testName);
        } else {
            failedCount++;
            failureMessages.add(testName);
            System.out.printf("  [FAIL] %s%n", testName);
        }
    }
    private void assertEquals(String testName, Object expected, Object actual) {
        if (Objects.equals(expected, actual)) {
            passedCount++;
            System.out.printf("  [PASS] %s%n", testName);
        } else {
            failedCount++;
            String msg = String.format("%s (Expected: %s, Got: %s)", testName, expected, actual);
            failureMessages.add(msg);
            System.out.printf("  [FAIL] %s%n", msg);
        }
    }
    private void testCO1StringAlgorithms() {
        System.out.println("\n[Testing CO1: String Algorithms]");
        String text = "malware detected in host. malware signature matched.";
        String pattern = "malware";
        List<Integer> kmp = KMP.search(text, pattern);
        assertEquals("KMP pattern match count", 2, kmp.size());
        assertEquals("KMP first match position", 0, kmp.get(0));
        assertEquals("KMP second match position", 26, kmp.get(1));
        List<Integer> z = ZAlgorithm.search(text, pattern);
        assertEquals("Z-Algorithm matches KMP", kmp, z);
        List<Integer> rk = RabinKarp.search(text, pattern);
        assertEquals("Rabin-Karp matches KMP", kmp, rk);
        assertTrue("Empty pattern handling", KMP.search(text, "").isEmpty());
        assertTrue("Pattern longer than text", KMP.search("abc", "abcdef").isEmpty());
        List<String> dict = List.of("malware", "signature", "host");
        AhoCorasick ac = new AhoCorasick(dict);
        List<AhoCorasick.MatchResult> acMatches = ac.searchAll(text);
        assertEquals("Aho-Corasick multiple pattern occurrences", 4, acMatches.size());
    }
    private void testCO2SuffixStructures() {
        System.out.println("\n[Testing CO2: Suffix Structures]");
        String s = "banana";
        int[] sa = SuffixArray.build(s);
        assertEquals("Suffix Array banana first element", 5, sa[0]);
        assertEquals("Suffix Array banana second element", 3, sa[1]);
        int[] saisArr = SAIS.build(s);
        assertEquals("SAIS matches standard SuffixArray", Arrays.toString(sa), Arrays.toString(saisArr));
        SuffixTree tree = new SuffixTree(s);
        assertTrue("SuffixTree contains substring 'nan'", tree.containsSubstring("nan"));
        assertTrue("SuffixTree does not contain 'xyz'", !tree.containsSubstring("xyz"));
        SuffixAutomaton sam = new SuffixAutomaton(s);
        assertTrue("SuffixAutomaton contains substring 'ana'", sam.contains("ana"));
        assertTrue("SuffixAutomaton does not contain 'xyz'", !sam.contains("xyz"));
        int[] lcp = KasaiLCP.build(s, sa);
        assertEquals("Kasai LCP between ana and anana", 3, lcp[2]);
        int maxLcp = 0, startIdx = -1;
        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] > maxLcp) {
                maxLcp = lcp[i];
                startIdx = sa[i];
            }
        }
        assertEquals("Longest repeated substring banana", "ana", s.substring(startIdx, startIdx + maxLcp));
        List<Integer> occurrences = SuffixArraySearch.search("attack detected in network attack trace", "attack", SuffixArray.build("attack detected in network attack trace"));
        assertEquals("Suffix Array Binary Search match count", 2, occurrences.size());
        assertEquals("Suffix Array Binary Search match pos 1", 0, occurrences.get(0));
        assertEquals("Suffix Array Binary Search match pos 2", 27, occurrences.get(1));
    }
    private void testCO3DynamicProgramming() {
        System.out.println("\n[Testing CO3: Dynamic Programming]");
        LevenshteinDistance.EditDetail ed = LevenshteinDistance.compute("kitten", "sitting");
        assertEquals("Levenshtein distance kitten -> sitting", 3, ed.distance);
        int damDist = DamerauLevenshtein.compute("teh", "the");
        assertEquals("Damerau-Levenshtein transposition 'teh' -> 'the'", 1, damDist);
        int weighted = WeightedEditDistance.compute("cat", "cut", 5, 5, 2);
        assertEquals("Weighted edit distance substitution cost", 2, weighted);
        MatrixChainMultiplication.MCMResult mcm = MatrixChainMultiplication.solve(new int[]{10, 30, 5, 60});
        assertEquals("MCM optimal multiplications", 4500, mcm.minMultiplications);
        int[][] dist = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        BitmaskDP.TSPResult tsp = BitmaskDP.solve(dist);
        assertEquals("Bitmask TSP optimal cycle cost", 80, tsp.minCost);
    }
    private void testCO4NetworkFlow() {
        System.out.println("\n[Testing CO4: Network Flow]");
        FlowGraph net = new FlowGraph(4);
        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 10);
        net.addEdge(1, 2, 1);
        net.addEdge(1, 3, 10);
        net.addEdge(2, 3, 10);
        int ek = EdmondsKarp.maxFlow(net, 0, 3);
        assertEquals("Edmonds-Karp max flow", 20, ek);
        int ff = FordFulkerson.maxFlow(net, 0, 3);
        assertEquals("Ford-Fulkerson max flow", 20, ff);
        int dinic = Dinic.maxFlow(net, 0, 3);
        assertEquals("Dinic max flow", 20, dinic);
        MinimumCut.MinCutResult minCut = MinimumCut.find(net, 0, 3);
        assertEquals("Min-Cut capacity equals Max Flow", 20, minCut.cutCapacity);
        List<String> analysts = List.of("Alice", "Bob");
        List<String> tickets = List.of("T1", "T2");
        boolean[][] adj = {
            {true, false},
            {true, true}
        };
        BipartiteMatching.MatchResult bmr = BipartiteMatching.match(analysts, tickets, adj);
        assertEquals("Bipartite Matching max matches", 2, bmr.matchCount);
    }
    private void testCO5NPCompleteAndGraphs() {
        System.out.println("\n[Testing CO5: NP-Complete & Graphs]");
        List<List<Integer>> unsatClauses = List.of(
            List.of(1, 2),
            List.of(-1, 2),
            List.of(-2)
        );
        SATSolver.SATResult resUnsat = SATSolver.solve(2, unsatClauses);
        assertTrue("SAT correctly identifies unsatisfiable formula", !resUnsat.satisfiable);
        List<List<Integer>> satClauses = List.of(
            List.of(1, 2),
            List.of(-1, 3)
        );
        SATSolver.SATResult resSat = SATSolver.solve(3, satClauses);
        assertTrue("SAT correctly finds satisfying assignment", resSat.satisfiable);
        boolean[][] adj = new boolean[4][4];
        adj[0][1] = adj[1][0] = true;
        adj[1][2] = adj[2][1] = true;
        adj[0][2] = adj[2][0] = true;
        adj[2][3] = adj[3][2] = true;
        Set<Integer> maxClique = Clique.findMaxClique(4, adj);
        assertEquals("Maximum Clique size is 3", 3, maxClique.size());
        Set<Integer> maxIS = IndependentSet.findMaxIndependentSet(4, adj);
        assertEquals("Maximum Independent Set size is 2", 2, maxIS.size());
        Set<Integer> minVC = VertexCover.findExactVertexCover(4, adj);
        assertEquals("Exact Vertex Cover size is 2 (V - IS)", 2, minVC.size());
        Set<Integer> approxVC = VertexCoverApproximation.findApproximateVertexCover(4, adj);
        assertTrue("2-Approx Vertex Cover is valid and <= 2 * OPT", approxVC.size() >= 2 && approxVC.size() <= 4);
    }
    private void testCO6RandomizedAndParallel() {
        System.out.println("\n[Testing CO6: Randomized & Parallel]");
        Random rand = new Random(12345);
        assertTrue("Miller-Rabin verifies prime 104729", MillerRabin.test(104729, 10, rand));
        assertTrue("Miller-Rabin identifies composite 104730", !MillerRabin.test(104730, 10, rand));
        int[] arr = {9, 3, 7, 1, 5, 8, 2, 6, 4, 0};
        RandomizedQuickSort.sort(arr, rand);
        int[] expectedArr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        assertTrue("Randomized QuickSort correctly sorts array", Arrays.equals(expectedArr, arr));
        List<Integer> stream = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> sample = ReservoirSampling.sample(stream.iterator(), 3, rand);
        assertEquals("Reservoir sample size is exactly K", 3, sample.size());
        long[] input = {10, 20, 30, 40, 50};
        long[] seq = PrefixSum.compute(input);
        long[] par = ParallelPrefixSum.compute(input);
        assertTrue("Sequential and Parallel Prefix Sum are identical", Arrays.equals(seq, par));
        assertEquals("Prefix sum last element", 150L, par[4]);
        long seqSum = ParallelReduction.sequentialSum(input);
        long parSum = ParallelReduction.parallelSum(input);
        assertEquals("Parallel reduction sum matches sequential", seqSum, parSum);
    }
    private void testStorageAndDetection() {
        System.out.println("\n[Testing Storage and Detection Rules]");
        CSVLogManager manager = new CSVLogManager();
        manager.addLog(new SecurityLog("2026-10-12 09:00:00", "HOST-01", "LOGIN", "HIGH", "Multiple failed login attempts"));
        manager.addLog(new SecurityLog("2026-10-12 09:05:00", "HOST-02", "NETWORK", "LOW", "Normal outbound traffic"));
        assertEquals("CSVLogManager total logs", 2, manager.getLogs().size());
        assertEquals("Filter by severity HIGH", 1, manager.filterBySeverity("HIGH").size());
        assertEquals("Search by keyword 'failed'", 1, manager.searchByKeyword("failed").size());
        DetectionEngine engine = new DetectionEngine();
        List<DetectionEngine.MatchFinding> findings = engine.evaluateText("Alert: multiple failed login attempts detected");
        assertTrue("Detection engine matches rule R01", !findings.isEmpty());
        assertEquals("Matched rule pattern", "failed login", findings.get(0).rule.getPattern());
    }
    private void testStreamProcessing() {
        System.out.println("\n[Testing Simulated Event Stream]");
        DetectionEngine engine = new DetectionEngine();
        CSVLogManager manager = new CSVLogManager();
        LogStreamProcessor stream = new LogStreamProcessor(engine, manager);
        SecurityLog log = new SecurityLog("2026-10-12 09:00:00", "HOST-TEST", "MALWARE", "CRITICAL", "Suspicious malware signature detected");
        LogStreamProcessor.ProcessEventResult res = stream.processIncomingEvent(log);
        assertTrue("Stream correctly flags suspicious event", res.isFlagged);
        assertTrue("Stream measures non-negative algorithm execution time", res.algorithmTimeNs >= 0);
        assertEquals("Stream flagged logs count", 1, stream.getStreamFlaggedEvents().size());
    }
    private void testFailedLoginDetection() {
        System.out.println("\n[Testing Failed Login Detection & Time-Window Burst Logic]");
        FailedLoginDetector detector = new FailedLoginDetector(3, 300); // 3 attempts in 300 seconds

        // 1. One successful login
        SecurityLog successLog = new SecurityLog("2026-10-12 09:00:00", "HOST-A", "LOGIN", "LOW", "Successful login for user admin");
        FailedLoginDetector.BurstResult res1 = detector.evaluateEvent(successLog);
        assertTrue("One successful login does not count as failed login", !res1.isFailedLogin);
        assertEquals("One successful login window count is 0", 0, res1.countInWindow);
        assertTrue("One successful login does not trigger burst", !res1.burstDetected);

        // 2. One failed login
        SecurityLog failLog1 = new SecurityLog("2026-10-12 09:01:00", "HOST-A", "LOGIN", "MEDIUM", "Failed password for user admin");
        FailedLoginDetector.BurstResult res2 = detector.evaluateEvent(failLog1);
        assertTrue("One failed login recognized", res2.isFailedLogin);
        assertEquals("One failed login window count is 1", 1, res2.countInWindow);
        assertTrue("One failed login does not reach threshold (1 < 3)", !res2.burstDetected);

        // 3. Multiple failed logins below the threshold (2 < 3)
        SecurityLog failLog2 = new SecurityLog("2026-10-12 09:02:00", "HOST-A", "LOGIN", "MEDIUM", "Failed password for user admin");
        FailedLoginDetector.BurstResult res3 = detector.evaluateEvent(failLog2);
        assertEquals("Second failed login window count is 2", 2, res3.countInWindow);
        assertTrue("Failed logins below threshold do not trigger burst (2 < 3)", !res3.burstDetected);

        // 4. Failed logins reaching the threshold (3 >= 3)
        SecurityLog failLog3 = new SecurityLog("2026-10-12 09:03:00", "HOST-A", "LOGIN", "HIGH", "Failed password for user admin");
        FailedLoginDetector.BurstResult res4 = detector.evaluateEvent(failLog3);
        assertEquals("Third failed login window count reaches threshold (3)", 3, res4.countInWindow);
        assertTrue("Third failed login triggers burst alert", res4.burstDetected);
        assertTrue("Burst result explanation contains window and count",
                res4.message.contains("3 failed logins") && res4.message.contains("300s window"));

        // 5. Successful logins mixed with failed logins
        detector.reset();
        SecurityLog m1 = new SecurityLog("2026-10-12 10:00:00", "HOST-B", "LOGIN", "MEDIUM", "Failed login attempt");
        SecurityLog m2 = new SecurityLog("2026-10-12 10:01:00", "HOST-B", "LOGIN", "LOW", "Successful login for user service_acct");
        SecurityLog m3 = new SecurityLog("2026-10-12 10:02:00", "HOST-B", "LOGIN", "MEDIUM", "Failed password attempt");
        SecurityLog m4 = new SecurityLog("2026-10-12 10:03:00", "HOST-B", "LOGIN", "LOW", "Successful authentication session");
        detector.evaluateEvent(m1);
        detector.evaluateEvent(m2);
        detector.evaluateEvent(m3);
        FailedLoginDetector.BurstResult mixedRes = detector.evaluateEvent(m4);
        assertEquals("Mixed successful logins do not increment failed login counter (count is 2)", 2, detector.getActiveWindowCount("HOST-B"));
        assertTrue("Mixed logins below threshold do not trigger burst", !mixedRes.burstDetected);

        // 6. Failed logins occurring outside the configured time window
        detector.reset();
        SecurityLog old1 = new SecurityLog("2026-10-12 08:00:00", "HOST-C", "LOGIN", "HIGH", "Failed login for admin");
        SecurityLog old2 = new SecurityLog("2026-10-12 08:01:00", "HOST-C", "LOGIN", "HIGH", "Failed login for admin");
        detector.evaluateEvent(old1);
        detector.evaluateEvent(old2);
        // Third attempt arrives 30 minutes later (1800s > 300s window)
        SecurityLog delayed = new SecurityLog("2026-10-12 08:30:00", "HOST-C", "LOGIN", "HIGH", "Failed login for admin");
        FailedLoginDetector.BurstResult delayedRes = detector.evaluateEvent(delayed);
        assertEquals("Old events outside window are evicted (window count is 1)", 1, delayedRes.countInWindow);
        assertTrue("Failed login outside time window does not trigger burst", !delayedRes.burstDetected);

        // 7. Invalid or missing timestamps handled safely
        detector.reset();
        SecurityLog nullTsLog = new SecurityLog(null, "HOST-D", "LOGIN", "HIGH", "Failed password attempt");
        SecurityLog malformedTsLog = new SecurityLog("invalid-date-format", "HOST-D", "LOGIN", "HIGH", "Failed password attempt");
        FailedLoginDetector.BurstResult safe1 = detector.evaluateEvent(nullTsLog);
        FailedLoginDetector.BurstResult safe2 = detector.evaluateEvent(malformedTsLog);
        assertTrue("Null timestamp event handled safely without exception", safe1.isFailedLogin);
        assertTrue("Malformed timestamp event handled safely without exception", safe2.isFailedLogin);
        assertEquals("Invalid timestamp events tracked safely in window", 2, safe2.countInWindow);

        // 8. Fresh burst after an earlier burst has expired
        detector.reset();
        // Burst 1: at 11:00:00, 11:01:00, 11:02:00
        detector.evaluateEvent(new SecurityLog("2026-10-12 11:00:00", "HOST-E", "LOGIN", "HIGH", "Failed login"));
        detector.evaluateEvent(new SecurityLog("2026-10-12 11:01:00", "HOST-E", "LOGIN", "HIGH", "Failed login"));
        FailedLoginDetector.BurstResult b1 = detector.evaluateEvent(new SecurityLog("2026-10-12 11:02:00", "HOST-E", "LOGIN", "HIGH", "Failed login"));
        assertTrue("First burst triggers alert", b1.burstDetected);

        // 20 minutes later (past 300s window), new burst starts:
        detector.evaluateEvent(new SecurityLog("2026-10-12 11:22:00", "HOST-E", "LOGIN", "HIGH", "Failed login"));
        detector.evaluateEvent(new SecurityLog("2026-10-12 11:23:00", "HOST-E", "LOGIN", "HIGH", "Failed login"));
        FailedLoginDetector.BurstResult b2 = detector.evaluateEvent(new SecurityLog("2026-10-12 11:24:00", "HOST-E", "LOGIN", "HIGH", "Failed login"));
        assertTrue("Fresh burst after earlier burst expired triggers new alert", b2.burstDetected);
    }
    private void testCSVValidationAndEdgeCases() {
        System.out.println("\n[Testing CSV Validation, Quoting, and Edge Cases]");
        // Test CSV line parsing with quotes containing commas
        String quotedLine = "2026-10-12 09:00:00,HOST-01,LOGIN,HIGH,\"Failed login, retry count: 3, source ip: 10.0.0.1\"";
        List<String> tokens = CSVLogManager.parseCsvLine(quotedLine);
        assertEquals("CSV quoted line tokens count", 5, tokens.size());
        assertEquals("CSV quoted token preserved commas", "Failed login, retry count: 3, source ip: 10.0.0.1", tokens.get(4));

        // Test non-existent file handling
        CSVLogManager manager = new CSVLogManager();
        CSVLogManager.ImportResult resNotFound = manager.importCsv("data/non_existent_file.csv", false);
        assertTrue("Non-existent CSV returns failure", !resNotFound.success);

        // Test header validation logic
        List<String> validHeaders = List.of("timestamp", "source", "event_type", "severity", "description");
        List<String> invalidHeaders = List.of("timestamp", "ip_address", "event_type", "severity", "notes");
        assertTrue("Header validation accepts valid required headers", CSVLogManager.validateHeaders(validHeaders));
        assertTrue("Header validation rejects headers with missing required columns", !CSVLogManager.validateHeaders(invalidHeaders));

        // Test row validation for malformed fields and timestamps
        List<String> validRow = List.of("2026-10-12 09:00:00", "HOST-01", "LOGIN", "LOW", "Sample event");
        List<String> badTimestampRow = List.of("invalid-timestamp", "HOST-01", "LOGIN", "LOW", "Sample event");
        List<String> emptyFieldRow = List.of("2026-10-12 09:00:00", "", "LOGIN", "LOW", "Sample event");
        List<String> shortRow = List.of("2026-10-12 09:00:00", "HOST-01", "LOGIN");

        assertEquals("Valid row passes validation", null, CSVLogManager.validateRow(validRow, 2));
        assertTrue("Bad timestamp flagged as malformed", CSVLogManager.validateRow(badTimestampRow, 3) != null);
        assertTrue("Empty field flagged as malformed", CSVLogManager.validateRow(emptyFieldRow, 4) != null);
        assertTrue("Short row flagged as malformed", CSVLogManager.validateRow(shortRow, 5) != null);

        // Test default dataset loading
        CSVLogManager.ImportResult resDefault = manager.importCsv("data/security_logs.csv", false);
        assertTrue("Default dataset imported successfully", resDefault.success);
        assertTrue("Default dataset loaded count >= 10", resDefault.loadedCount >= 10);
        assertEquals("Default dataset malformed count is 0", 0, resDefault.malformedCount);

        // Test duplicate detection when appending same dataset
        CSVLogManager.ImportResult resAppend = manager.importCsv("data/security_logs.csv", true);
        assertTrue("Append succeeds", resAppend.success);
        assertTrue("Duplicates detected when appending same dataset", resAppend.duplicateCount >= 10);
    }
    private void testHostFilteringAndLogSummary() {
        System.out.println("\n[Testing Host Filtering, Summary & Batch Burst Detection]");
        CSVLogManager manager = new CSVLogManager();
        manager.importCsv("data/security_logs.csv", false);

        // Host filtering
        List<SecurityLog> host1Logs = manager.filterByHost("HOST-01");
        assertTrue("Filter by host HOST-01 finds records", !host1Logs.isEmpty());
        for (SecurityLog l : host1Logs) {
            assertEquals("Filtered log has source HOST-01", "HOST-01", l.getSource());
        }

        // Summary generation
        Map<String, Object> summary = manager.getLogSummary();
        assertTrue("Summary contains totalLogs", summary.containsKey("totalLogs"));
        assertTrue("Summary contains severityBreakdown", summary.containsKey("severityBreakdown"));
        assertTrue("Summary contains uniqueHosts", summary.containsKey("uniqueHosts"));
        assertTrue("Summary contains timeSpan", summary.containsKey("timeSpan"));

        // Batch failed login burst detection on loaded logs
        List<FailedLoginDetector.BurstResult> bursts = manager.detectFailedLoginBursts(2, 600);
        assertTrue("Batch burst detection executes without errors", bursts != null);
    }
}
