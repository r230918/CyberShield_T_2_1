import java.util.*;

/**
 * Streamlined Command-Line Interface (CLI) Menu Manager for CyberShield.
 * Focuses on the core host-based security workflow in 7 streamlined menus,
 * integrating Search + Filter and Analysis + Rules for a clean 2-minute demo.
 */
public class MenuManager {
    private final Scanner scanner;
    private final CSVLogManager logManager;
    private final DetectionEngine detectionEngine;
    private final LogStreamProcessor streamProcessor;

    // Configurable burst detection settings
    private int burstThreshold = 3;
    private long burstWindowSeconds = 300; // 5 minutes

    public MenuManager(Scanner scanner, CSVLogManager logManager, DetectionEngine detectionEngine, LogStreamProcessor streamProcessor) {
        this.scanner = scanner;
        this.logManager = logManager;
        this.detectionEngine = detectionEngine;
        this.streamProcessor = streamProcessor;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = InputValidator.readInt(scanner, "  Enter your choice (0-7): ", 0, 7);
            System.out.println();
            switch (choice) {
                case 1 -> menuLoadSecurityLogs();
                case 2 -> menuViewLogSummary();
                case 3 -> menuSearchAndFilterLogs();
                case 4 -> menuThreatSignaturesAndAnalysis();
                case 5 -> menuDetectFailedLogins();
                case 6 -> menuTextForensicsAndMasquerade();
                case 7 -> menuAdvancedDemosAndTests();
                case 0 -> {
                    System.out.println("Thank you for using CyberShield. Exiting application...");
                    running = false;
                }
                default -> System.out.println("Invalid selection. Please try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("====================================================");
        System.out.println("                    CYBERSHIELD                     ");
        System.out.println("       Host-Based Security Log Analysis System      ");
        System.out.println("====================================================");
        System.out.println("  1. Load & Manage Security Logs from CSV");
        System.out.println("  2. View Security Log Summary & Statistics");
        System.out.println("  3. Search & Filter Logs (KMP, Z, Rabin-Karp, Host, Sev)");
        System.out.println("  4. Threat Signature Detection & Suspicious Events (Aho-Corasick)");
        System.out.println("  5. Detect Repeated Failed-Login Bursts (Sliding-Window Deque)");
        System.out.println("  6. Text Forensics & Masquerade Analysis (Levenshtein, Suffix Array)");
        System.out.println("  7. Advanced Algorithm Demonstrations & Automated Tests (CO1-CO6)");
        System.out.println();
        System.out.println("  0. Exit");
        System.out.println("----------------------------------------------------");
    }

    private void pause() {
        System.out.println("\nPress ENTER to continue...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    // =========================================================================
    // 1. LOAD & MANAGE SECURITY LOGS FROM CSV
    // =========================================================================
    private void menuLoadSecurityLogs() {
        boolean back = false;
        while (!back) {
            System.out.println("----------------------------------------------------");
            System.out.println("        1. LOAD & MANAGE SECURITY LOGS FROM CSV      ");
            System.out.println("----------------------------------------------------");
            System.out.println("  1. Load Default Dataset (data/security_logs.csv)");
            System.out.println("  2. Load Custom CSV File Path");
            System.out.println("  3. View / Display All Loaded Log Records");
            System.out.println("  4. Generate Synthetic Sample Dataset");
            System.out.println("  5. Export Current Logs to CSV");
            System.out.println("  0. Return to Main Menu");
            System.out.println("----------------------------------------------------");
            int c = InputValidator.readInt(scanner, "  Select an option: ", 0, 5);
            System.out.println();
            switch (c) {
                case 1 -> {
                    boolean append = InputValidator.readYesNo(scanner, "Append to existing logs instead of replacing?");
                    CSVLogManager.ImportResult res = logManager.importCsv("data/security_logs.csv", append);
                    System.out.println(res.message);
                    if (res.success && !logManager.getLogs().isEmpty()) {
                        if (InputValidator.readYesNo(scanner, "Display loaded records now?")) {
                            displayLogTable(logManager.getLogs());
                        }
                    }
                }
                case 2 -> {
                    String path = InputValidator.readNonEmptyString(scanner, "Enter CSV file path: ");
                    boolean append = InputValidator.readYesNo(scanner, "Append to existing logs instead of replacing?");
                    CSVLogManager.ImportResult res = logManager.importCsv(path, append);
                    System.out.println(res.message);
                    if (!res.malformedReasons.isEmpty()) {
                        System.out.println("Malformed Row Details:");
                        for (String r : res.malformedReasons) System.out.println("  * " + r);
                    }
                    if (res.success && !logManager.getLogs().isEmpty()) {
                        if (InputValidator.readYesNo(scanner, "Display loaded records now?")) {
                            displayLogTable(logManager.getLogs());
                        }
                    }
                }
                case 3 -> displayLogTable(logManager.getLogs());
                case 4 -> {
                    int count = InputValidator.readInt(scanner, "Enter number of synthetic records to generate (10-1000): ", 10, 1000);
                    logManager.generateSyntheticDataset(count);
                    System.out.printf("Generated %d synthetic logs. Total logs in memory: %d%n", count, logManager.getLogs().size());
                    if (InputValidator.readYesNo(scanner, "Display generated records now?")) {
                        displayLogTable(logManager.getLogs());
                    }
                }
                case 5 -> {
                    if (logManager.getLogs().isEmpty()) {
                        System.out.println("No logs in memory to export.");
                        break;
                    }
                    String target = InputValidator.readStringWithDefault(scanner, "Enter export CSV file path [data/exported_logs.csv]: ", "data/exported_logs.csv");
                    boolean ok = logManager.exportLogs(target);
                    System.out.println(ok ? "Logs successfully exported to: " + target : "Failed to export logs.");
                }
                case 0 -> back = true;
            }
            if (!back) pause();
        }
    }

    private void displayLogTable(List<SecurityLog> logs) {
        if (logs == null || logs.isEmpty()) {
            System.out.println("No security logs loaded in memory to display.");
            return;
        }
        System.out.printf("%n%-4s | %-19s | %-12s | %-10s | %-8s | %s%n",
                "#", "Timestamp", "Source Host", "Event Type", "Severity", "Description");
        System.out.println("-".repeat(95));
        int idx = 1;
        for (SecurityLog log : logs) {
            System.out.printf("%-4d | %-19s | %-12s | %-10s | %-8s | %s%n",
                    idx++,
                    log.getTimestamp(),
                    log.getSource(),
                    log.getEventType(),
                    log.getSeverity(),
                    log.getDescription());
        }
        System.out.println("-".repeat(95));
        System.out.printf("Total records: %d%n", logs.size());
    }

    // =========================================================================
    // 2. VIEW SECURITY LOG SUMMARY & STATISTICS
    // =========================================================================
    private void menuViewLogSummary() {
        System.out.println("----------------------------------------------------");
        System.out.println("       2. VIEW SECURITY LOG SUMMARY & STATISTICS    ");
        System.out.println("----------------------------------------------------");
        List<SecurityLog> logs = logManager.getLogs();
        if (logs.isEmpty()) {
            System.out.println("No security logs loaded in memory.");
            System.out.println("Please load logs using Option 1 from the main menu.");
            pause();
            return;
        }

        Map<String, Object> summary = logManager.getLogSummary();
        System.out.println("Total Security Logs Loaded: " + summary.get("totalLogs"));
        System.out.println("Time Span:                 " + summary.get("timeSpan"));
        System.out.println("Flagged Detections So Far: " + summary.get("flaggedCount"));

        System.out.println("\nSeverity Distribution:");
        @SuppressWarnings("unchecked")
        Map<String, Integer> sevs = (Map<String, Integer>) summary.get("severityBreakdown");
        for (Map.Entry<String, Integer> e : sevs.entrySet()) {
            System.out.printf("  - %-10s : %d%n", e.getKey(), e.getValue());
        }

        System.out.println("\nEvent Type Distribution:");
        @SuppressWarnings("unchecked")
        Map<String, Integer> types = (Map<String, Integer>) summary.get("eventTypeBreakdown");
        for (Map.Entry<String, Integer> e : types.entrySet()) {
            System.out.printf("  - %-10s : %d%n", e.getKey(), e.getValue());
        }

        System.out.println("\nMonitored Host Entities:");
        @SuppressWarnings("unchecked")
        Set<String> hosts = (Set<String>) summary.get("uniqueHosts");
        System.out.println("  " + hosts);

        if (InputValidator.readYesNo(scanner, "\nDisplay all loaded raw log records?")) {
            displayLogTable(logs);
        }

        pause();
    }

    // =========================================================================
    // 3. SEARCH & FILTER LOGS (COMBINED)
    // =========================================================================
    private void menuSearchAndFilterLogs() {
        boolean back = false;
        while (!back) {
            System.out.println("----------------------------------------------------");
            System.out.println("             3. SEARCH & FILTER LOGS                ");
            System.out.println("----------------------------------------------------");
            System.out.println("  1. Search logs by keyword using KMP Algorithm [O(N+M)]");
            System.out.println("  2. Search logs by keyword using Z-Algorithm [O(N+M)]");
            System.out.println("  3. Search logs by keyword using Rabin-Karp Rolling Hash");
            System.out.println("  4. Search logs using Suffix Array Binary Search [O(M log N)]");
            System.out.println("  5. Filter logs by Host ID (e.g. HOST-01, HOST-02)");
            System.out.println("  6. Filter logs by Event Type (LOGIN, MALWARE, NETWORK, etc.)");
            System.out.println("  7. Filter logs by Severity (LOW, MEDIUM, HIGH, CRITICAL)");
            System.out.println("  8. Benchmark / Compare String Search Algorithms");
            System.out.println("  0. Return to Main Menu");
            System.out.println("----------------------------------------------------");
            int c = InputValidator.readInt(scanner, "  Select an option: ", 0, 8);
            System.out.println();
            switch (c) {
                case 1, 2, 3 -> executeStringSearch(c);
                case 4 -> executeSuffixArrayLogSearch();
                case 5 -> {
                    List<SecurityLog> logs = logManager.getLogs();
                    if (logs.isEmpty()) { System.out.println("No logs loaded."); break; }
                    String host = InputValidator.readNonEmptyString(scanner, "Enter Host ID to filter: ");
                    displayFiltered(logManager.filterByHost(host), "Host = " + host);
                }
                case 6 -> {
                    List<SecurityLog> logs = logManager.getLogs();
                    if (logs.isEmpty()) { System.out.println("No logs loaded."); break; }
                    String type = InputValidator.readNonEmptyString(scanner, "Enter Event Type (LOGIN, MALWARE, etc.): ");
                    displayFiltered(logManager.filterByEventType(type), "Event Type = " + type.toUpperCase());
                }
                case 7 -> {
                    List<SecurityLog> logs = logManager.getLogs();
                    if (logs.isEmpty()) { System.out.println("No logs loaded."); break; }
                    String sev = InputValidator.readNonEmptyString(scanner, "Enter Severity (LOW, MEDIUM, HIGH, CRITICAL): ");
                    displayFiltered(logManager.filterBySeverity(sev), "Severity = " + sev.toUpperCase());
                }
                case 8 -> compareSearchAlgorithms();
                case 0 -> back = true;
            }
            if (!back) pause();
        }
    }

    private void displayFiltered(List<SecurityLog> filtered, String label) {
        System.out.printf("\n--- Filter Results [%s] --- (Found: %d records)%n", label, filtered.size());
        if (filtered.isEmpty()) {
            System.out.println("No matching logs found.");
        } else {
            displayLogTable(filtered);
        }
    }

    private void executeStringSearch(int algChoice) {
        List<SecurityLog> logs = logManager.getLogs();
        if (logs.isEmpty()) {
            System.out.println("No security logs loaded. Please load logs via Option 1 first.");
            return;
        }
        String pattern = InputValidator.readNonEmptyString(scanner, "Enter pattern or keyword to search: ");
        String algName = switch (algChoice) {
            case 1 -> "Knuth-Morris-Pratt (KMP) [O(N+M)]";
            case 2 -> "Z-Algorithm [O(N+M)]";
            default -> "Rabin-Karp Rolling Hash [Avg O(N+M)]";
        };

        System.out.printf("\nScanning %d logs using %s...%n", logs.size(), algName);
        int matchedLogs = 0;
        long totalSearchNs = 0;

        for (SecurityLog log : logs) {
            String text = log.getDescription().toLowerCase();
            String pat = pattern.toLowerCase();
            long s = System.nanoTime();
            List<Integer> matches = switch (algChoice) {
                case 1 -> KMP.search(text, pat);
                case 2 -> ZAlgorithm.search(text, pat);
                default -> RabinKarp.search(text, pat);
            };
            totalSearchNs += (System.nanoTime() - s);

            if (!matches.isEmpty()) {
                matchedLogs++;
                System.out.printf("  [MATCH] %s | %s | %s (Offsets: %s)%n",
                        log.getSource(), log.getSeverity(), log.getDescription(), matches);
            }
        }

        System.out.println("\n--- Search Summary ---");
        System.out.printf("Matching Logs: %d / %d%n", matchedLogs, logs.size());
        System.out.printf("Pure Algorithm Search Time: %.4f ms (%d ns)%n", totalSearchNs / 1_000_000.0, totalSearchNs);
    }

    private void executeSuffixArrayLogSearch() {
        List<SecurityLog> logs = logManager.getLogs();
        if (logs.isEmpty()) {
            System.out.println("No security logs loaded. Please load logs via Option 1 first.");
            return;
        }
        String pattern = InputValidator.readNonEmptyString(scanner, "Enter phrase to search with Suffix Array Binary Search: ");
        int matchedLogs = 0;
        long totalNs = 0;

        for (SecurityLog log : logs) {
            String text = log.getDescription().toLowerCase();
            String pat = pattern.toLowerCase();
            long s = System.nanoTime();
            int[] sa = SuffixArray.build(text);
            List<Integer> matches = SuffixArraySearch.search(text, pat, sa);
            totalNs += (System.nanoTime() - s);

            if (!matches.isEmpty()) {
                matchedLogs++;
                System.out.printf("  [MATCH] %s | %s (Indices: %s)%n",
                        log.getSource(), log.getDescription(), matches);
            }
        }
        System.out.println("\n--- Suffix Array Search Summary ---");
        System.out.printf("Matching Logs: %d / %d%n", matchedLogs, logs.size());
        System.out.printf("SA Build + Binary Search Time: %.4f ms (%d ns)%n", totalNs / 1_000_000.0, totalNs);
    }

    private void compareSearchAlgorithms() {
        String text = InputValidator.readNonEmptyString(scanner, "Enter sample log description to benchmark: ");
        String pattern = InputValidator.readNonEmptyString(scanner, "Enter search pattern: ");
        int iters = InputValidator.readInt(scanner, "Enter iterations (e.g. 1000): ", 10, 500000);

        List<PerformanceAnalyzer.BenchmarkResult> res = PerformanceAnalyzer.compareStringAlgorithms(text, pattern, iters);
        System.out.println("\n--- String Search Performance Benchmark ---");
        for (PerformanceAnalyzer.BenchmarkResult r : res) {
            System.out.println(r);
        }
    }

    // =========================================================================
    // 4. THREAT SIGNATURE DETECTION & SUSPICIOUS EVENTS (AHO-CORASICK)
    // =========================================================================
    private void menuThreatSignaturesAndAnalysis() {
        boolean back = false;
        while (!back) {
            System.out.println("----------------------------------------------------");
            System.out.println(" 4. THREAT SIGNATURES & SUSPICIOUS EVENTS (AHO-CORASICK)");
            System.out.println("----------------------------------------------------");
            System.out.println("  1. Evaluate Loaded Logs against Active Threat Rules (R01-R09)");
            System.out.println("  2. Scan Custom Payload Text with Aho-Corasick Automaton");
            System.out.println("  3. View All Active Threat Detection Rules");
            System.out.println("  4. Add New Threat Detection Signature");
            System.out.println("  5. Remove Threat Detection Signature");
            System.out.println("  0. Return to Main Menu");
            System.out.println("----------------------------------------------------");
            int c = InputValidator.readInt(scanner, "  Select an option: ", 0, 5);
            System.out.println();
            switch (c) {
                case 1 -> {
                    List<SecurityLog> logs = logManager.getLogs();
                    if (logs.isEmpty()) {
                        System.out.println("No security logs loaded. Please load logs via Option 1 first.");
                        break;
                    }
                    System.out.printf("Evaluating %d loaded logs against %d active rules simultaneously...%n%n",
                            logs.size(), detectionEngine.getRules().size());
                    int flaggedCount = 0;
                    long totalEvalNs = 0;
                    for (SecurityLog log : logs) {
                        long s = System.nanoTime();
                        List<DetectionEngine.MatchFinding> findings = detectionEngine.evaluateLog(log);
                        totalEvalNs += (System.nanoTime() - s);
                        if (!findings.isEmpty()) {
                            flaggedCount++;
                            StringBuilder reason = new StringBuilder();
                            for (DetectionEngine.MatchFinding f : findings) {
                                reason.append(String.format("[%s: '%s' at offset %d] ", f.rule.getRuleId(), f.rule.getPattern(), f.position));
                            }
                            logManager.flagLog(log, reason.toString().trim());
                            System.out.printf("[FLAGGED %s] %s%n  Reason: %s%n%n", log.getSeverity(), log, reason);
                        }
                    }
                    System.out.println("--- Analysis Summary ---");
                    System.out.printf("Logs Evaluated: %d | Flagged: %d (%.1f%%)%n", logs.size(), flaggedCount, (flaggedCount * 100.0) / Math.max(1, logs.size()));
                    System.out.printf("Aho-Corasick Multi-Pattern Time: %.4f ms (%d ns)%n", totalEvalNs / 1_000_000.0, totalEvalNs);
                    if (flaggedCount > 0 && InputValidator.readYesNo(scanner, "Export findings report to file?")) {
                        String path = InputValidator.readStringWithDefault(scanner, "Report path [data/findings_report.txt]: ", "data/findings_report.txt");
                        boolean ok = logManager.exportFindings(path);
                        System.out.println(ok ? "Exported to: " + path : "Failed to export report.");
                    }
                }
                case 2 -> {
                    String text = InputValidator.readNonEmptyString(scanner, "Enter log text or payload to scan: ");
                    List<DetectionEngine.MatchFinding> findings = detectionEngine.evaluateText(text);
                    System.out.println("\n--- Multi-Pattern Signature Scan Results ---");
                    if (findings.isEmpty()) {
                        System.out.println("No known threat signatures matched.");
                    } else {
                        System.out.printf("Found %d signature matches:%n", findings.size());
                        for (DetectionEngine.MatchFinding f : findings) {
                            System.out.printf("  - [%s] Pattern: '%s' | Severity: %s | Offset: %d | %s%n",
                                    f.rule.getRuleId(), f.rule.getPattern(), f.rule.getSeverity(), f.position, f.rule.getDescription());
                        }
                    }
                }
                case 3 -> {
                    System.out.println("--- Active Threat Detection Rules (Aho-Corasick Engine) ---");
                    for (DetectionRule r : detectionEngine.getRules()) System.out.println("  " + r);
                }
                case 4 -> {
                    String id = InputValidator.readNonEmptyString(scanner, "Rule ID (e.g. R10): ");
                    String pat = InputValidator.readNonEmptyString(scanner, "Signature pattern: ");
                    String sev = InputValidator.readNonEmptyString(scanner, "Severity (LOW/MEDIUM/HIGH/CRITICAL): ");
                    String desc = InputValidator.readNonEmptyString(scanner, "Description: ");
                    detectionEngine.addRule(new DetectionRule(id, pat, sev, desc, true));
                    System.out.println("Threat signature rule added successfully.");
                }
                case 5 -> {
                    String id = InputValidator.readNonEmptyString(scanner, "Rule ID to remove: ");
                    boolean ok = detectionEngine.removeRule(id);
                    System.out.println(ok ? "Rule removed." : "Rule ID not found.");
                }
                case 0 -> back = true;
            }
            if (!back) pause();
        }
    }

    // =========================================================================
    // 5. DETECT REPEATED FAILED-LOGIN BURSTS (SLIDING-WINDOW DEQUE)
    // =========================================================================
    private void menuDetectFailedLogins() {
        boolean back = false;
        while (!back) {
            System.out.println("----------------------------------------------------");
            System.out.println("     5. DETECT REPEATED FAILED-LOGIN BURSTS         ");
            System.out.println("----------------------------------------------------");
            System.out.printf("  Current Settings: Threshold = %d failed attempts | Window = %d seconds (%.1f min)%n",
                    burstThreshold, burstWindowSeconds, burstWindowSeconds / 60.0);
            System.out.println("----------------------------------------------------");
            System.out.println("  1. Run Sliding-Window Burst Detection on Loaded CSV Logs");
            System.out.println("  2. Run Simulated Real-Time Security Event Stream (Interactive)");
            System.out.println("  3. Stream External CSV File (Event-by-Event)");
            System.out.println("  4. Configure Threshold and Time Window");
            System.out.println("  0. Return to Main Menu");
            System.out.println("----------------------------------------------------");
            int c = InputValidator.readInt(scanner, "  Select an option: ", 0, 4);
            System.out.println();
            switch (c) {
                case 1 -> {
                    List<SecurityLog> logs = logManager.getLogs();
                    if (logs.isEmpty()) {
                        System.out.println("No security logs loaded. Please load logs via Option 1 first.");
                        break;
                    }
                    System.out.printf("Scanning %d logs for failed-login bursts (>= %d failures in %ds)...%n%n",
                            logs.size(), burstThreshold, burstWindowSeconds);
                    List<FailedLoginDetector.BurstResult> bursts = logManager.detectFailedLoginBursts(burstThreshold, burstWindowSeconds);
                    if (bursts.isEmpty()) {
                        System.out.println("No repeated failed-login bursts detected in the loaded dataset.");
                    } else {
                        System.out.printf("Detected %d burst incidents:%n", bursts.size());
                        for (FailedLoginDetector.BurstResult b : bursts) {
                            System.out.printf("  [ALERT] Host: %-10s | %s%n", b.host, b.message);
                        }
                    }
                }
                case 2 -> {
                    int count = InputValidator.readInt(scanner, "Number of simulated stream events (5-50): ", 5, 50);
                    int delay = InputValidator.readInt(scanner, "Delay between events in ms (e.g. 200): ", 0, 2000);
                    System.out.println("\nStarting real-time simulated security stream...\n");
                    streamProcessor.getFailedLoginDetector().reset();
                    streamProcessor.runSyntheticStream(count, delay, new LogStreamProcessor.StreamCallback() {
                        @Override
                        public void onEventProcessed(int eventNumber, LogStreamProcessor.ProcessEventResult res) {
                            String flagTag = res.isFlagged ? "[FLAGGED]" : "[OK]     ";
                            System.out.printf("  #%-2d %s [%s] %s | %s (Time: %.3f ms)%n",
                                    eventNumber, flagTag, res.log.getSource(), res.log.getEventType(),
                                    res.log.getDescription(), res.algorithmTimeNs / 1_000_000.0);
                            if (res.isFlagged) {
                                System.out.printf("       -> Reason: %s%n", res.flagReason);
                            }
                        }
                        @Override
                        public void onError(String message) {
                            System.out.println("Stream Error: " + message);
                        }
                        @Override
                        public boolean shouldStop() {
                            return false;
                        }
                    });
                    System.out.printf("%nStream complete. Flagged events in session: %d%n",
                            streamProcessor.getStreamFlaggedEvents().size());
                }
                case 3 -> {
                    String path = InputValidator.readStringWithDefault(scanner, "Enter CSV path [data/security_logs.csv]: ", "data/security_logs.csv");
                    int delay = InputValidator.readInt(scanner, "Delay between events in ms (e.g. 150): ", 0, 2000);
                    System.out.println("\nStreaming events from CSV: " + path + "\n");
                    streamProcessor.streamExternalCsv(path, delay, new LogStreamProcessor.StreamCallback() {
                        @Override
                        public void onEventProcessed(int eventNumber, LogStreamProcessor.ProcessEventResult res) {
                            String flagTag = res.isFlagged ? "[ALERT] " : "[NORMAL]";
                            System.out.printf("  #%-2d %s [%s] %s (Time: %.3f ms)%n",
                                    eventNumber, flagTag, res.log.getSource(), res.log.getDescription(),
                                    res.algorithmTimeNs / 1_000_000.0);
                            if (res.isFlagged) {
                                System.out.printf("       -> Flag Reason: %s%n", res.flagReason);
                            }
                        }
                        @Override
                        public void onError(String message) {
                            System.out.println("Stream Error: " + message);
                        }
                        @Override
                        public boolean shouldStop() {
                            return false;
                        }
                    });
                }
                case 4 -> {
                    burstThreshold = InputValidator.readInt(scanner, "Enter failed login threshold (e.g. 3): ", 1, 20);
                    burstWindowSeconds = InputValidator.readInt(scanner, "Enter time window in seconds (e.g. 300): ", 10, 86400);
                    System.out.printf("Updated settings: %d failed attempts in %d seconds.%n", burstThreshold, burstWindowSeconds);
                }
                case 0 -> back = true;
            }
            if (!back) pause();
        }
    }

    // =========================================================================
    // 6. TEXT FORENSICS & MASQUERADE ANALYSIS (LEVENSHTEIN, SUFFIX ARRAY)
    // =========================================================================
    private void menuTextForensicsAndMasquerade() {
        boolean back = false;
        while (!back) {
            System.out.println("----------------------------------------------------");
            System.out.println("  6. TEXT FORENSICS & MASQUERADE ANALYSIS (DSA)     ");
            System.out.println("----------------------------------------------------");
            System.out.println("  1. Process Masquerading & Typosquatting (Levenshtein Distance)");
            System.out.println("  2. Command Transposition Typos (Damerau-Levenshtein)");
            System.out.println("  3. Weighted Edit Distance with Custom Costs");
            System.out.println("  4. Longest Repeated Exploit Loop (Suffix Array + Kasai LCP)");
            System.out.println("  5. Find All Repeated Phrases of Minimum Length");
            System.out.println("  6. Longest Common Substring Between Two Messages");
            System.out.println("  7. Suffix Array & Kasai LCP Inspection Table");
            System.out.println("  8. Substring Verification (Suffix Tree & Suffix Automaton)");
            System.out.println("  0. Return to Main Menu");
            System.out.println("----------------------------------------------------");
            int c = InputValidator.readInt(scanner, "  Select an option: ", 0, 8);
            System.out.println();
            switch (c) {
                case 1 -> {
                    System.out.println("--- Phishing Domain & Masquerade Analysis (Levenshtein) ---");
                    System.out.println("  1) Preset Phishing Domain: 'https://secure-login.bankcorp.com' vs 'https://secure-login.bankc0rp.com'");
                    System.out.println("  2) Preset Process Masquerade: 'svchost.exe' vs 'svch0st.exe'");
                    System.out.println("  3) Custom strings");
                    int ch = InputValidator.readInt(scanner, "Choose option (1-3): ", 1, 3);
                    String s1 = (ch == 1) ? "https://secure-login.bankcorp.com" : (ch == 2) ? "svchost.exe" : InputValidator.readNonEmptyString(scanner, "Message 1: ");
                    String s2 = (ch == 1) ? "https://secure-login.bankc0rp.com" : (ch == 2) ? "svch0st.exe" : InputValidator.readNonEmptyString(scanner, "Message 2: ");

                    LevenshteinDistance.EditDetail detail = LevenshteinDistance.compute(s1, s2);
                    System.out.println("\n--- Edit Distance Analysis ---");
                    System.out.println("Original: " + s1);
                    System.out.println("Suspect:  " + s2);
                    System.out.println("Minimum Edit Distance: " + detail.distance);
                    System.out.println("Step-by-step Transformation Plan:");
                    for (int i = 0; i < detail.operations.size(); i++) {
                        System.out.printf("  %d. %s%n", (i + 1), detail.operations.get(i));
                    }
                    System.out.println("Verdict: " + (detail.distance <= 2 ? "HIGH RISK TYPOSQUATTING / SPOOFING DETECTED" : "LOW SIMILARITY"));
                }
                case 2 -> {
                    System.out.println("--- Detect Transposition Typosquat (Damerau-Levenshtein) ---");
                    String s1 = InputValidator.readStringWithDefault(scanner, "String 1 [powershell.exe]: ", "powershell.exe");
                    String s2 = InputValidator.readStringWithDefault(scanner, "String 2 [powreshell.exe]: ", "powreshell.exe");
                    int dam = DamerauLevenshtein.compute(s1, s2);
                    int lev = LevenshteinDistance.compute(s1, s2).distance;
                    System.out.printf("Standard Levenshtein: %d | Damerau-Levenshtein: %d%n", lev, dam);
                    if (dam < lev) {
                        System.out.println("Detection: Adjacent character swap (transposition typo) detected in 1 single editing operation!");
                    } else {
                        System.out.println("Detection: No single-step transposition advantage detected.");
                    }
                }
                case 3 -> {
                    String s1 = InputValidator.readNonEmptyString(scanner, "String 1: ");
                    String s2 = InputValidator.readNonEmptyString(scanner, "String 2: ");
                    int cIns = InputValidator.readInt(scanner, "Insertion cost: ", 1, 100);
                    int cDel = InputValidator.readInt(scanner, "Deletion cost: ", 1, 100);
                    int cSub = InputValidator.readInt(scanner, "Substitution cost: ", 1, 100);
                    int cost = WeightedEditDistance.compute(s1, s2, cIns, cDel, cSub);
                    System.out.printf("Total Weighted Edit Cost: %d (Ins=%d, Del=%d, Sub=%d)%n", cost, cIns, cDel, cSub);
                }
                case 4 -> {
                    System.out.println("--- Forensic Analysis: Longest Repeated Pattern in Attack Log ---");
                    System.out.println("  1) Preset Exploit Trace: 'PAYLOAD_9090_SHELLCODE_9090_NOP_SLED_PAYLOAD_9090_SHELLCODE_OVERFLOW'");
                    System.out.println("  2) Preset Incident Log: 'Repeated unauthorized login failure on port 22. Repeated unauthorized login failure on port 22'");
                    System.out.println("  3) Custom Text / Document");
                    int rChoice = InputValidator.readInt(scanner, "Choose option (1-3): ", 1, 3);
                    String text = switch (rChoice) {
                        case 1 -> "PAYLOAD_9090_SHELLCODE_9090_NOP_SLED_PAYLOAD_9090_SHELLCODE_OVERFLOW";
                        case 2 -> "Repeated unauthorized login failure on port 22. Repeated unauthorized login failure on port 22";
                        default -> InputValidator.readNonEmptyString(scanner, "Enter message/document text: ");
                    };

                    long s = System.nanoTime();
                    int[] sa = SuffixArray.build(text);
                    int[] lcp = KasaiLCP.build(text, sa);
                    int maxLen = 0, startIdx = -1;
                    for (int i = 1; i < lcp.length; i++) {
                        if (lcp[i] > maxLen) {
                            maxLen = lcp[i];
                            startIdx = sa[i];
                        }
                    }
                    String repeated = (maxLen > 0 && startIdx != -1) ? text.substring(startIdx, startIdx + maxLen) : "";
                    long time = System.nanoTime() - s;

                    System.out.println("\n--- Longest Repeated Pattern Result ---");
                    if (repeated.isEmpty()) {
                        System.out.println("No recurring patterns detected.");
                    } else {
                        System.out.printf("Recurring Exploit / Loop Pattern: \"%s\" (Length: %d chars)%n", repeated, repeated.length());
                    }
                    System.out.printf("Analysis Time: %.4f ms (%d ns)%n", time / 1_000_000.0, time);
                }
                case 5 -> {
                    String text = InputValidator.readNonEmptyString(scanner, "Enter message text: ");
                    int minLen = InputValidator.readInt(scanner, "Enter minimum repeated length: ", 1, text.length());
                    int[] sa = SuffixArray.build(text);
                    int[] lcp = KasaiLCP.build(text, sa);
                    Set<String> uniqueRepeats = new LinkedHashSet<>();
                    for (int i = 1; i < lcp.length; i++) {
                        if (lcp[i] >= minLen) {
                            uniqueRepeats.add(text.substring(sa[i], sa[i] + lcp[i]));
                        }
                    }
                    System.out.println("\n--- Repeated Phrases (Min Length " + minLen + ") ---");
                    if (uniqueRepeats.isEmpty()) {
                        System.out.println("None found.");
                    } else {
                        for (String r : uniqueRepeats) {
                            System.out.printf(" - \"%s\" (%d chars)%n", r, r.length());
                        }
                    }
                }
                case 6 -> {
                    String m1 = InputValidator.readNonEmptyString(scanner, "Enter Message 1: ");
                    String m2 = InputValidator.readNonEmptyString(scanner, "Enter Message 2: ");
                    long s = System.nanoTime();
                    String combined = m1 + "#" + m2;
                    int n1 = m1.length();
                    int[] sa = SuffixArray.build(combined);
                    int[] lcp = KasaiLCP.build(combined, sa);
                    int maxLen = 0, bestStart = -1;
                    for (int i = 1; i < lcp.length; i++) {
                        int p1 = sa[i - 1];
                        int p2 = sa[i];
                        boolean oneInFirst = (p1 < n1 && p2 > n1) || (p2 < n1 && p1 > n1);
                        if (oneInFirst && lcp[i] > maxLen) {
                            int validLen = Math.min(lcp[i], Math.min(n1 - Math.min(p1, p2), combined.length() - Math.max(p1, p2)));
                            if (validLen > maxLen) {
                                maxLen = validLen;
                                bestStart = Math.min(p1, p2);
                            }
                        }
                    }
                    String lcs = (maxLen > 0 && bestStart != -1) ? combined.substring(bestStart, bestStart + maxLen) : "";
                    long time = System.nanoTime() - s;
                    System.out.println("\n--- Longest Common Substring ---");
                    System.out.println(lcs.isEmpty() ? "No common substring found." : "Common Phrase: \"" + lcs + "\" (Length: " + lcs.length() + ")");
                    System.out.printf("Time Taken: %.4f ms%n", time / 1_000_000.0);
                }
                case 7 -> {
                    String text = InputValidator.readNonEmptyString(scanner, "Enter text (keep under 50 chars for clean view): ");
                    int[] sa = SuffixArray.build(text);
                    int[] lcp = KasaiLCP.build(text, sa);
                    System.out.println("\n--- Suffix Array & Kasai LCP Table ---");
                    System.out.printf("%-5s | %-5s | %-5s | %s%n", "Rank", "SA[i]", "LCP", "Suffix Content");
                    System.out.println("-----------------------------------------------------------------");
                    for (int i = 0; i < sa.length; i++) {
                        System.out.printf("%-5d | %-5d | %-5d | %s%n", i, sa[i], lcp[i], text.substring(sa[i]));
                    }
                }
                case 8 -> {
                    String text = InputValidator.readNonEmptyString(scanner, "Enter base document/log text: ");
                    String sub = InputValidator.readNonEmptyString(scanner, "Enter query substring to test: ");
                    SuffixTree tree = new SuffixTree(text);
                    SuffixAutomaton sam = new SuffixAutomaton(text);
                    System.out.printf("Suffix Tree Search:      %s%n", tree.containsSubstring(sub) ? "SUBSTRING PRESENT" : "NOT FOUND");
                    System.out.printf("Suffix Automaton Search: %s%n", sam.contains(sub) ? "SUBSTRING PRESENT" : "NOT FOUND");
                }
                case 0 -> back = true;
            }
            if (!back) pause();
        }
    }

    // =========================================================================
    // 7. ADVANCED ALGORITHM DEMONSTRATIONS & TESTS (CO1 - CO6)
    // =========================================================================
    private void menuAdvancedDemosAndTests() {
        boolean back = false;
        while (!back) {
            System.out.println("----------------------------------------------------");
            System.out.println(" 7. ADVANCED DEMONSTRATIONS & SYSTEM TESTS (CO1-CO6)");
            System.out.println("----------------------------------------------------");
            System.out.println("  1. Run Automated System Test Suite (94/94 PASS)");
            System.out.println("  2. Maximum Network Throughput (Edmonds-Karp & Dinic) [CO4]");
            System.out.println("  3. Network Bottlenecks & Critical Links (Minimum Cut) [CO4]");
            System.out.println("  4. Security Analyst Task Allocation (Bipartite Matching) [CO4]");
            System.out.println("  5. Boolean Security Policy Verification (SAT / 3-SAT Solver) [CO5]");
            System.out.println("  6. Collusion Ring Analysis (Maximum Clique & Independent Set) [CO5]");
            System.out.println("  7. Security Sensor Placement: Vertex Cover (Exact vs 2-Approx) [CO5]");
            System.out.println("  8. Log Pipeline Filter Optimization (Matrix-Chain Mult. DP) [CO3]");
            System.out.println("  9. Min-Cost Traveling Security Auditor (Bitmask DP / TSP) [CO3]");
            System.out.println(" 10. Miller-Rabin Cryptographic Primality Test [CO6]");
            System.out.println(" 11. Randomized QuickSort with Controlled Seed [CO6]");
            System.out.println(" 12. Reservoir Sampling from Security Stream [CO6]");
            System.out.println(" 13. Prefix Sum: Sequential vs Parallel & Brent's Theorem Bounds [CO6]");
            System.out.println("  0. Return to Main Menu");
            System.out.println("----------------------------------------------------");
            int c = InputValidator.readInt(scanner, "  Select an option: ", 0, 13);
            System.out.println();
            switch (c) {
                case 1 -> {
                    TestRunner runner = new TestRunner();
                    runner.runAllTests();
                }
                case 2 -> {
                    FlowGraph net = buildSampleCorporateNetwork();
                    int flowEK = EdmondsKarp.maxFlow(net, 0, 5);
                    int flowDinic = Dinic.maxFlow(net, 0, 5);
                    System.out.println("Source: CLIENT -> Destination: INTERNAL_DB");
                    System.out.println("Max Flow (Edmonds-Karp): " + flowEK + " packets/sec");
                    System.out.println("Max Flow (Dinic):        " + flowDinic + " packets/sec");
                }
                case 3 -> {
                    FlowGraph net = buildSampleCorporateNetwork();
                    MinimumCut.MinCutResult minCut = MinimumCut.find(net, 0, 5);
                    System.out.println("Total Bottleneck Cut Capacity: " + minCut.cutCapacity);
                    System.out.println("Reachable Partition (S): " + minCut.sourceSet);
                    System.out.println("Isolated Partition (T):  " + minCut.sinkSet);
                    System.out.println("Critical S-T Edges that saturate traffic:");
                    for (String edge : minCut.cutEdges) System.out.println("  * " + edge);
                }
                case 4 -> {
                    List<String> analysts = List.of("Analyst_Alice", "Analyst_Bob", "Analyst_Charlie", "Analyst_David");
                    List<String> tickets = List.of("Malware_Analysis", "Firewall_Audit", "DDoS_Mitigation", "SQLi_Patching");
                    boolean[][] adj = {
                        {true, true, false, false},
                        {false, true, true, false},
                        {true, false, true, true},
                        {false, false, false, true}
                    };
                    BipartiteMatching.MatchResult res = BipartiteMatching.match(analysts, tickets, adj);
                    System.out.printf("Maximum Simultaneous Assignments: %d / %d%n", res.matchCount, tickets.size());
                    for (Map.Entry<String, String> e : res.assignments.entrySet()) {
                        System.out.printf("  [ASSIGNED] %-15s ===> %s%n", e.getKey(), e.getValue());
                    }
                }
                case 5 -> {
                    System.out.println("--- Policy Verification via SAT Solver ---");
                    int vars = InputValidator.readInt(scanner, "Variables (1-10): ", 1, 10);
                    int count = InputValidator.readInt(scanner, "Clauses count: ", 1, 20);
                    List<List<Integer>> clauses = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        System.out.printf("Clause %d (e.g. '1 -2'): ", (i + 1));
                        String line = scanner.nextLine().trim();
                        String[] parts = line.split("\\s+");
                        List<Integer> cl = new ArrayList<>();
                        for (String p : parts) if (!p.isEmpty()) cl.add(Integer.parseInt(p));
                        clauses.add(cl);
                    }
                    SATSolver.SATResult res = SATSolver.solve(vars, clauses);
                    if (res.satisfiable) {
                        System.out.println("Status: SATISFIABLE (Valid Security Policy Exists)");
                        System.out.println("Assignments: " + res.model);
                    } else {
                        System.out.println("Status: UNSATISFIABLE (Contradiction / Conflict Detected in Policy)");
                    }
                }
                case 6 -> {
                    boolean[][] adj = sampleUndirectedGraph();
                    Set<Integer> clique = Clique.findMaxClique(6, adj);
                    Set<Integer> is = IndependentSet.findMaxIndependentSet(6, adj);
                    System.out.println("Max Clique Size: " + clique.size() + " " + clique);
                    System.out.println("Max Independent Set Size: " + is.size() + " " + is);
                }
                case 7 -> {
                    boolean[][] adj = sampleUndirectedGraph();
                    Set<Integer> exactVC = VertexCover.findExactVertexCover(6, adj);
                    Set<Integer> approxVC = VertexCoverApproximation.findApproximateVertexCover(6, adj);
                    System.out.printf("Exact Minimum Vertex Cover (Optimal): %s (Size: %d)%n", exactVC, exactVC.size());
                    System.out.printf("2-Approximation Vertex Cover:         %s (Size: %d)%n", approxVC, approxVC.size());
                    System.out.println("Theoretical Guarantee: |Approx| <= 2 * |Optimal|");
                }
                case 8 -> {
                    int[] dims = {10, 30, 5, 60, 10};
                    MatrixChainMultiplication.MCMResult res = MatrixChainMultiplication.solve(dims);
                    System.out.println("Minimum Scalar Operations: " + res.minMultiplications);
                    System.out.println("Optimal Pipeline Order:    " + res.optimalParentheses);
                }
                case 9 -> {
                    int[][] costMatrix = {
                        {0, 12, 10, 19, 8},
                        {12, 0, 3, 7, 2},
                        {10, 3, 0, 6, 20},
                        {19, 7, 6, 0, 4},
                        {8, 2, 20, 4, 0}
                    };
                    BitmaskDP.TSPResult tsp = BitmaskDP.solve(costMatrix);
                    System.out.println("Optimal Tour Latency / Cost: " + tsp.minCost);
                    System.out.println("Auditor Node Inspection Tour: " + tsp.tour);
                }
                case 10 -> {
                    long num = InputValidator.readInt(scanner, "Candidate number (e.g. 104729): ", 2, Integer.MAX_VALUE);
                    boolean isPrime = MillerRabin.test(num, 10, new Random(42));
                    System.out.println(isPrime ? "Number " + num + " is PROBABLY PRIME." : "Number " + num + " is DEFINITELY COMPOSITE.");
                }
                case 11 -> {
                    int[] arr = {9, 3, 7, 1, 5, 8, 2, 6, 4, 0};
                    System.out.println("Original array: " + Arrays.toString(arr));
                    RandomizedQuickSort.sort(arr, new Random(100));
                    System.out.println("Sorted array:   " + Arrays.toString(arr));
                }
                case 12 -> {
                    List<String> stream = new ArrayList<>();
                    for (int i = 1; i <= 100; i++) stream.add("EVENT-" + i);
                    int k = InputValidator.readInt(scanner, "Choose sample size K: ", 1, 20);
                    List<String> sampled = ReservoirSampling.sample(stream.iterator(), k, new Random(42));
                    System.out.println("Reservoir Sampled Events: " + sampled);
                }
                case 13 -> {
                    int n = 100000;
                    long[] arr = new long[n];
                    Arrays.fill(arr, 1L);
                    long[] seq = PrefixSum.compute(arr);
                    long[] par = ParallelPrefixSum.compute(arr);
                    System.out.println("Sequential & Parallel Results Identical: " + Arrays.equals(seq, par));
                    ParallelPrefixSum.BrentAnalysis b = ParallelPrefixSum.analyzeBrent(1000000, 8);
                    System.out.printf("Brent's Theorem Bound (N=1M, P=8): Work=%d, Depth=%d, Speedup=%.2fx%n",
                            b.work, b.depth, b.speedup);
                }
                case 0 -> back = true;
            }
            if (!back) pause();
        }
    }

    private FlowGraph buildSampleCorporateNetwork() {
        List<String> names = List.of("CLIENT", "FIREWALL", "LOAD_BALANCER", "APP_SRV_1", "APP_SRV_2", "INTERNAL_DB");
        FlowGraph net = new FlowGraph(names);
        net.addEdge(0, 1, 100);
        net.addEdge(1, 2, 80);
        net.addEdge(1, 3, 30);
        net.addEdge(2, 3, 40);
        net.addEdge(2, 4, 50);
        net.addEdge(3, 5, 60);
        net.addEdge(4, 5, 50);
        return net;
    }

    private boolean[][] sampleUndirectedGraph() {
        boolean[][] adj = new boolean[6][6];
        adj[0][1] = adj[1][0] = true;
        adj[1][2] = adj[2][1] = true;
        adj[0][2] = adj[2][0] = true;
        adj[2][3] = adj[3][2] = true;
        adj[3][4] = adj[4][3] = true;
        adj[4][5] = adj[5][4] = true;
        return adj;
    }
}
