import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
public class CSVLogManager {
    private final List<SecurityLog> logs = new ArrayList<>();
    private final List<SecurityLog> flaggedLogs = new ArrayList<>();
    private final List<String> detectionHistory = new ArrayList<>();
    public CSVLogManager() {}
    public void loadDefaultLogs(String defaultCsvPath) {
        File file = new File(defaultCsvPath);
        if (file.exists()) {
            importCsv(defaultCsvPath, false);
        }
    }
    public List<SecurityLog> getLogs() {
        return Collections.unmodifiableList(logs);
    }
    public List<SecurityLog> getFlaggedLogs() {
        return Collections.unmodifiableList(flaggedLogs);
    }
    public List<String> getDetectionHistory() {
        return Collections.unmodifiableList(detectionHistory);
    }
    public void addLog(SecurityLog log) {
        if (log != null) {
            logs.add(log);
        }
    }
    public void flagLog(SecurityLog log, String reason) {
        if (log != null) {
            if (!flaggedLogs.contains(log)) {
                flaggedLogs.add(log);
            }
            String histEntry = String.format("[%s] Flagged (%s): %s - Reason: %s",
                    log.getTimestamp(), log.getSeverity(), log.getDescription(), reason);
            detectionHistory.add(histEntry);
        }
    }
    public void addDetectionHistory(String entry) {
        detectionHistory.add(entry);
    }
    public void clearSessionHistory() {
        flaggedLogs.clear();
        detectionHistory.clear();
    }
    public ImportResult importCsv(String filePath, boolean append) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return new ImportResult(false, 0, 0, 0, "File not found: " + filePath);
        }
        List<SecurityLog> loaded = new ArrayList<>();
        int malformedCount = 0;
        int duplicateCount = 0;
        List<String> malformedReasons = new ArrayList<>();
        Set<SecurityLog> existingSet = new HashSet<>(append ? logs : Collections.emptyList());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return new ImportResult(false, 0, 0, 0, "CSV file is completely empty.", Collections.emptyList());
            }
            List<String> headers = parseCsvLine(headerLine);
            if (!validateHeaders(headers)) {
                return new ImportResult(false, 0, 0, 0,
                        "Invalid CSV header. Expected columns: timestamp,source,event_type,severity,description (Found: " + headers + ").",
                        Collections.emptyList());
            }
            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.trim().isEmpty()) continue;
                List<String> tokens = parseCsvLine(line);
                String error = validateRow(tokens, lineNum);
                if (error != null) {
                    malformedCount++;
                    malformedReasons.add(error);
                    continue;
                }
                String timestamp = tokens.get(0).trim();
                String source = tokens.get(1).trim();
                String eventType = tokens.get(2).trim();
                String severity = tokens.get(3).trim();
                String description = tokens.get(4).trim();
                SecurityLog log = new SecurityLog(timestamp, source, eventType, severity, description);
                if (existingSet.contains(log)) {
                    duplicateCount++;
                }
                existingSet.add(log);
                loaded.add(log);
            }
        } catch (IOException e) {
            return new ImportResult(false, 0, 0, 0, "IO Error reading CSV: " + e.getMessage(), Collections.emptyList());
        }
        if (!append) {
            logs.clear();
        }
        logs.addAll(loaded);
        return new ImportResult(true, loaded.size(), malformedCount, duplicateCount,
                String.format("Successfully imported %d records (%d malformed skipped, %d duplicate entries).",
                        loaded.size(), malformedCount, duplicateCount),
                malformedReasons);
    }
    public static boolean validateHeaders(List<String> headers) {
        if (headers == null || headers.size() < 5) return false;
        Set<String> lower = new HashSet<>();
        for (String h : headers) {
            lower.add(h.trim().toLowerCase().replace("-", "_").replace(" ", "_"));
        }
        return lower.contains("timestamp") &&
               lower.contains("source") &&
               (lower.contains("event_type") || lower.contains("eventtype") || lower.contains("event")) &&
               lower.contains("severity") &&
               lower.contains("description");
    }
    public static String validateRow(List<String> tokens, int lineNum) {
        if (tokens.size() < 5) {
            return "Line " + lineNum + ": Expected at least 5 fields but found " + tokens.size();
        }
        String ts = tokens.get(0).trim();
        String src = tokens.get(1).trim();
        String type = tokens.get(2).trim();
        String sev = tokens.get(3).trim();
        if (ts.isEmpty() || src.isEmpty() || type.isEmpty() || sev.isEmpty()) {
            return "Line " + lineNum + ": Missing required non-empty field (timestamp/source/event_type/severity)";
        }
        if (FailedLoginDetector.parseTimestampSafe(ts) == null) {
            return "Line " + lineNum + ": Invalid timestamp format ('" + ts + "'). Expected 'yyyy-MM-dd HH:mm:ss'";
        }
        return null; // Valid row
    }
    public static List<String> parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }
    public boolean exportLogs(String targetPath) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetPath), StandardCharsets.UTF_8))) {
            writer.println("timestamp,source,event_type,severity,description");
            for (SecurityLog log : logs) {
                writer.println(log.toCsvLine());
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public boolean exportFindings(String targetPath) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetPath), StandardCharsets.UTF_8))) {
            writer.println("# CyberShield Detection History & Findings");
            writer.println("Total Flagged Events: " + flaggedLogs.size());
            writer.println("Total Detection Log Entries: " + detectionHistory.size());
            writer.println("\n--- FLAG DETECTIONS ---");
            for (SecurityLog log : flaggedLogs) {
                writer.println(log.toString());
            }
            writer.println("\n--- HISTORY LOG ---");
            for (String hist : detectionHistory) {
                writer.println(hist);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public List<SecurityLog> filterBySeverity(String severity) {
        List<SecurityLog> res = new ArrayList<>();
        for (SecurityLog log : logs) {
            if (log.getSeverity().equalsIgnoreCase(severity)) {
                res.add(log);
            }
        }
        return res;
    }
    public List<SecurityLog> filterByEventType(String eventType) {
        List<SecurityLog> res = new ArrayList<>();
        for (SecurityLog log : logs) {
            if (log.getEventType().equalsIgnoreCase(eventType)) {
                res.add(log);
            }
        }
        return res;
    }
    public List<SecurityLog> searchByKeyword(String keyword) {
        List<SecurityLog> res = new ArrayList<>();
        String kw = keyword.toLowerCase();
        for (SecurityLog log : logs) {
            if (log.getDescription().toLowerCase().contains(kw) ||
                log.getSource().toLowerCase().contains(kw) ||
                log.getEventType().toLowerCase().contains(kw)) {
                res.add(log);
            }
        }
        return res;
    }
    public List<SecurityLog> filterByHost(String host) {
        List<SecurityLog> res = new ArrayList<>();
        if (host == null) return res;
        String h = host.trim().toLowerCase();
        for (SecurityLog log : logs) {
            if (log.getSource().toLowerCase().contains(h)) {
                res.add(log);
            }
        }
        return res;
    }
    public Map<String, Object> getLogSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalLogs", logs.size());
        Map<String, Integer> severityCounts = new LinkedHashMap<>();
        Map<String, Integer> eventTypeCounts = new LinkedHashMap<>();
        Set<String> uniqueHosts = new LinkedHashSet<>();
        for (SecurityLog log : logs) {
            severityCounts.put(log.getSeverity(), severityCounts.getOrDefault(log.getSeverity(), 0) + 1);
            eventTypeCounts.put(log.getEventType(), eventTypeCounts.getOrDefault(log.getEventType(), 0) + 1);
            uniqueHosts.add(log.getSource());
        }
        summary.put("severityBreakdown", severityCounts);
        summary.put("eventTypeBreakdown", eventTypeCounts);
        summary.put("uniqueHosts", uniqueHosts);
        summary.put("flaggedCount", flaggedLogs.size());
        String earliest = logs.isEmpty() ? "N/A" : logs.get(0).getTimestamp();
        String latest = logs.isEmpty() ? "N/A" : logs.get(logs.size() - 1).getTimestamp();
        summary.put("timeSpan", earliest + " to " + latest);
        return summary;
    }
    public List<FailedLoginDetector.BurstResult> detectFailedLoginBursts(int threshold, long windowSeconds) {
        FailedLoginDetector detector = new FailedLoginDetector(threshold, windowSeconds);
        List<FailedLoginDetector.BurstResult> bursts = new ArrayList<>();
        for (SecurityLog log : logs) {
            FailedLoginDetector.BurstResult res = detector.evaluateEvent(log);
            if (res.burstDetected) {
                bursts.add(res);
                flagLog(log, res.message);
            }
        }
        return bursts;
    }
    public void generateSyntheticDataset(int count) {
        String[] hosts = {"HOST-01", "HOST-02", "HOST-03", "HOST-04", "HOST-05", "SRV-DB", "SRV-WEB", "GW-EXT"};
        String[] types = {"LOGIN", "NETWORK", "MALWARE", "ACCESS", "SYSTEM", "FIREWALL"};
        String[] sevs = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        String[] msgs = {
            "Successful authentication session",
            "Multiple failed login attempts detected",
            "Suspicious malware signature detected in process memory",
            "Port scan detected across internal subnet",
            "Unauthorized access attempt to administrative console",
            "Outbound data exfiltration suspected to unknown host",
            "Privilege escalation attempt using kernel exploit",
            "Ransomware encryption activity suspected in shared storage",
            "Brute force credential stuffing attack observed",
            "SQL injection signature matched in web payload"
        };
        Random rand = new Random(42);
        for (int i = 1; i <= count; i++) {
            String ts = String.format("2026-10-12 %02d:%02d:%02d", rand.nextInt(24), rand.nextInt(60), rand.nextInt(60));
            String host = hosts[rand.nextInt(hosts.length)];
            String type = types[rand.nextInt(types.length)];
            String sev = sevs[rand.nextInt(sevs.length)];
            String msg = msgs[rand.nextInt(msgs.length)] + " [ID: " + i + "]";
            logs.add(new SecurityLog(ts, host, type, sev, msg));
        }
    }
    public static class ImportResult {
        public final boolean success;
        public final int loadedCount;
        public final int malformedCount;
        public final int duplicateCount;
        public final String message;
        public final List<String> malformedReasons;
        public ImportResult(boolean success, int loadedCount, int malformedCount, String message) {
            this(success, loadedCount, malformedCount, 0, message, Collections.emptyList());
        }
        public ImportResult(boolean success, int loadedCount, int malformedCount, int duplicateCount, String message) {
            this(success, loadedCount, malformedCount, duplicateCount, message, Collections.emptyList());
        }
        public ImportResult(boolean success, int loadedCount, int malformedCount, int duplicateCount, String message, List<String> malformedReasons) {
            this.success = success;
            this.loadedCount = loadedCount;
            this.malformedCount = malformedCount;
            this.duplicateCount = duplicateCount;
            this.message = message;
            this.malformedReasons = (malformedReasons != null) ? malformedReasons : Collections.emptyList();
        }
    }
}
