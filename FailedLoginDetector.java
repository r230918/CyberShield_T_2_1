import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Sliding-window failed login burst detector for host-based security analysis.
 * Tracks authentication failures per host within a configurable time window
 * to detect brute-force credential stuffing or password spray attacks.
 */
public class FailedLoginDetector {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int threshold;
    private final long windowSeconds;

    // Per-host sliding window deque of failed login epoch timestamps
    private final Map<String, Deque<Long>> hostTimestamps = new HashMap<>();

    // Per-host flag to prevent duplicate alert spam during an ongoing burst
    private final Map<String, Boolean> hostBurstAlerted = new HashMap<>();

    public FailedLoginDetector() {
        this(3, 300); // Default: 3 failed attempts in 300 seconds (5 minutes)
    }

    public FailedLoginDetector(int threshold, long windowSeconds) {
        this.threshold = Math.max(1, threshold);
        this.windowSeconds = Math.max(1, windowSeconds);
    }

    public int getThreshold() {
        return threshold;
    }

    public long getWindowSeconds() {
        return windowSeconds;
    }

    public synchronized void reset() {
        hostTimestamps.clear();
        hostBurstAlerted.clear();
    }

    public synchronized int getActiveWindowCount(String host) {
        if (host == null) return 0;
        Deque<Long> deque = hostTimestamps.get(host);
        return (deque == null) ? 0 : deque.size();
    }

    /**
     * Determines whether a log record represents a failed authentication event.
     * Explicit success messages are NEVER counted as failed logins.
     */
    public static boolean isFailedLoginEvent(SecurityLog log) {
        if (log == null) return false;
        String desc = log.getDescription().toLowerCase();

        // 1. Explicit success indicators must NEVER count as failed login
        if (desc.contains("success") || desc.contains("successful") ||
            desc.contains("accepted") || desc.contains("authenticated") ||
            desc.contains("granted") || desc.contains("normal outbound")) {
            return false;
        }

        // 2. Failure indicators
        boolean hasFailureWord = desc.contains("failed") || desc.contains("failure") ||
                                 desc.contains("bad password") || desc.contains("invalid") ||
                                 desc.contains("brute force") || desc.contains("denied") ||
                                 desc.contains("rejected") || desc.contains("unauthorized");

        // 3. Login or authentication context
        boolean isLoginContext = log.getEventType().equalsIgnoreCase("LOGIN") ||
                                 log.getEventType().equalsIgnoreCase("AUTH") ||
                                 desc.contains("login") || desc.contains("auth") ||
                                 desc.contains("password") || desc.contains("credential");

        return isLoginContext && hasFailureWord;
    }

    /**
     * Safely parses a timestamp string into epoch seconds.
     * Returns null if timestamp is null, blank, or malformed.
     */
    public static Long parseTimestampSafe(String timestampStr) {
        if (timestampStr == null || timestampStr.isBlank()) return null;
        try {
            return LocalDateTime.parse(timestampStr.trim(), FORMATTER).toEpochSecond(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static class BurstResult {
        public final boolean isFailedLogin;
        public final boolean burstDetected;
        public final int countInWindow;
        public final int threshold;
        public final long windowSeconds;
        public final String host;
        public final String message;

        public BurstResult(boolean isFailedLogin, boolean burstDetected, int countInWindow,
                           int threshold, long windowSeconds, String host, String message) {
            this.isFailedLogin = isFailedLogin;
            this.burstDetected = burstDetected;
            this.countInWindow = countInWindow;
            this.threshold = threshold;
            this.windowSeconds = windowSeconds;
            this.host = host;
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    /**
     * Evaluates an incoming log event against the sliding time window.
     */
    public synchronized BurstResult evaluateEvent(SecurityLog log) {
        if (log == null) {
            return new BurstResult(false, false, 0, threshold, windowSeconds, "UNKNOWN", "Null log event.");
        }

        String host = log.getSource();

        // If not a failed login, it does NOT increase the failed login counter
        if (!isFailedLoginEvent(log)) {
            int current = getActiveWindowCount(host);
            return new BurstResult(false, false, current, threshold, windowSeconds, host,
                    "Event is not a failed login (successful or non-authentication event).");
        }

        Long parsedEpoch = parseTimestampSafe(log.getTimestamp());
        long currentEpoch;
        if (parsedEpoch != null) {
            currentEpoch = parsedEpoch;
        } else {
            // Safe fallback for invalid or missing timestamps: use current system time
            currentEpoch = System.currentTimeMillis() / 1000L;
        }

        Deque<Long> deque = hostTimestamps.computeIfAbsent(host, k -> new ArrayDeque<>());

        // Evict expired entries outside the sliding window
        while (!deque.isEmpty() && (currentEpoch - deque.peekFirst()) > windowSeconds) {
            deque.pollFirst();
        }

        // If window shrank below threshold, reset the alerted flag for a fresh burst
        if (deque.size() < threshold) {
            hostBurstAlerted.put(host, false);
        }

        // Add the new failed login timestamp
        deque.addLast(currentEpoch);
        int count = deque.size();

        boolean burst = false;
        String msg;

        if (count >= threshold) {
            boolean alreadyAlerted = hostBurstAlerted.getOrDefault(host, false);
            if (!alreadyAlerted) {
                burst = true;
                hostBurstAlerted.put(host, true);
                msg = String.format("BURST DETECTED: Host %s reached %d failed logins in %ds window (threshold: %d).",
                        host, count, windowSeconds, threshold);
            } else {
                msg = String.format("Host %s ongoing burst (%d failed logins in window, already alerted).",
                        host, count);
            }
        } else {
            msg = String.format("Host %s failed login recorded (%d/%d in %ds window).",
                    host, count, threshold, windowSeconds);
        }

        return new BurstResult(true, burst, count, threshold, windowSeconds, host, msg);
    }
}
