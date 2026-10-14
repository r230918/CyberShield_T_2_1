import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
public class LogStreamProcessor {
    private final DetectionEngine detectionEngine;
    private final CSVLogManager logManager;
    private final List<SecurityLog> recentStreamEvents = new ArrayList<>();
    private final List<SecurityLog> streamFlaggedEvents = new ArrayList<>();
    private final FailedLoginDetector failedLoginDetector;
    public LogStreamProcessor(DetectionEngine detectionEngine, CSVLogManager logManager) {
        this(detectionEngine, logManager, new FailedLoginDetector(3, 300));
    }
    public LogStreamProcessor(DetectionEngine detectionEngine, CSVLogManager logManager, FailedLoginDetector detector) {
        this.detectionEngine = detectionEngine;
        this.logManager = logManager;
        this.failedLoginDetector = (detector != null) ? detector : new FailedLoginDetector(3, 300);
    }
    public FailedLoginDetector getFailedLoginDetector() {
        return failedLoginDetector;
    }
    public List<SecurityLog> getRecentStreamEvents() {
        return Collections.unmodifiableList(recentStreamEvents);
    }
    public List<SecurityLog> getStreamFlaggedEvents() {
        return Collections.unmodifiableList(streamFlaggedEvents);
    }
    public ProcessEventResult processIncomingEvent(SecurityLog log) {
        if (log == null) return null;
        recentStreamEvents.add(log);
        if (recentStreamEvents.size() > 50) {
            recentStreamEvents.remove(0);
        }
        long startNs = System.nanoTime();
        List<DetectionEngine.MatchFinding> findings = detectionEngine.evaluateLog(log);
        FailedLoginDetector.BurstResult burstRes = failedLoginDetector.evaluateEvent(log);
        long durationNs = System.nanoTime() - startNs;
        boolean flagged = false;
        StringBuilder reason = new StringBuilder();
        if (!findings.isEmpty()) {
            flagged = true;
            reason.append("Pattern matches: ");
            for (DetectionEngine.MatchFinding f : findings) {
                reason.append(String.format("[%s: '%s'] ", f.rule.getRuleId(), f.rule.getPattern()));
            }
        }
        if (burstRes.burstDetected) {
            flagged = true;
            reason.append(String.format("[BURST WARNING: %s has %d failed logins in %ds window (threshold: %d)] ",
                    log.getSource(), burstRes.countInWindow, burstRes.windowSeconds, burstRes.threshold));
        }
        if (flagged) {
            streamFlaggedEvents.add(log);
            logManager.flagLog(log, reason.toString().trim());
        }
        return new ProcessEventResult(log, flagged, reason.toString().trim(), durationNs);
    }
    public void runSyntheticStream(int count, int delayMs, StreamCallback callback) {
        String[] hosts = {"HOST-01", "HOST-02", "HOST-03", "SRV-WEB", "GW-VPN"};
        String[] events = {
            "Successful login for user admin",
            "Multiple failed login attempts from IP 10.0.0.45",
            "Port scan detected across internal subnet",
            "Suspicious malware signature detected in win32 system",
            "Ransomware encryption activity suspected in shared storage",
            "Normal outbound HTTPS traffic to internal API",
            "Unauthorized access attempt to confidential records",
            "Privilege escalation attempt using vulnerable driver"
        };
        String[] types = {"LOGIN", "NETWORK", "MALWARE", "SYSTEM", "ACCESS"};
        String[] sevs = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        Random rand = new Random();
        for (int i = 1; i <= count; i++) {
            if (callback != null && callback.shouldStop()) {
                break;
            }
            String host = hosts[rand.nextInt(hosts.length)];
            int idx = rand.nextInt(events.length);
            String desc = events[idx];
            String type = types[rand.nextInt(types.length)];
            String sev = sevs[rand.nextInt(sevs.length)];
            SecurityLog log = SecurityLog.createNow(host, type, sev, desc);
            ProcessEventResult res = processIncomingEvent(log);
            if (callback != null) {
                callback.onEventProcessed(i, res);
            }
            if (delayMs > 0) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    public void streamExternalCsv(String filePath, int delayMs, StreamCallback callback) {
        File file = new File(filePath);
        if (!file.exists()) {
            if (callback != null) callback.onError("File does not exist: " + filePath);
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null) {
                if (callback != null) callback.onError("CSV is empty.");
                return;
            }
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (callback != null && callback.shouldStop()) {
                    break;
                }
                if (line.trim().isEmpty()) continue;
                List<String> tokens = CSVLogManager.parseCsvLine(line);
                if (tokens.size() < 5) {
                    continue;
                }
                SecurityLog log = new SecurityLog(tokens.get(0), tokens.get(1), tokens.get(2), tokens.get(3), tokens.get(4));
                count++;
                ProcessEventResult res = processIncomingEvent(log);
                if (callback != null) {
                    callback.onEventProcessed(count, res);
                }
                if (delayMs > 0) {
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            if (callback != null) callback.onError("IO error during stream: " + e.getMessage());
        }
    }
    public static class ProcessEventResult {
        public final SecurityLog log;
        public final boolean isFlagged;
        public final String flagReason;
        public final long algorithmTimeNs;
        public ProcessEventResult(SecurityLog log, boolean isFlagged, String flagReason, long algorithmTimeNs) {
            this.log = log;
            this.isFlagged = isFlagged;
            this.flagReason = flagReason;
            this.algorithmTimeNs = algorithmTimeNs;
        }
    }
    public interface StreamCallback {
        void onEventProcessed(int eventNumber, ProcessEventResult result);
        void onError(String message);
        boolean shouldStop();
    }
}
