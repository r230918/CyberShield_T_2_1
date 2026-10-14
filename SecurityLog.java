import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
public class SecurityLog {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String timestamp;
    private String source;
    private String eventType;
    private String severity; 
    private String description;
    public SecurityLog(String timestamp, String source, String eventType, String severity, String description) {
        this.timestamp = (timestamp == null || timestamp.isBlank()) ? LocalDateTime.now().format(FORMATTER) : timestamp.trim();
        this.source = (source == null || source.isBlank()) ? "UNKNOWN_SRC" : source.trim();
        this.eventType = (eventType == null || eventType.isBlank()) ? "INFO" : eventType.trim().toUpperCase();
        this.severity = (severity == null || severity.isBlank()) ? "LOW" : severity.trim().toUpperCase();
        this.description = (description == null) ? "" : description.trim();
    }
    public static SecurityLog createNow(String source, String eventType, String severity, String description) {
        return new SecurityLog(LocalDateTime.now().format(FORMATTER), source, eventType, severity, description);
    }
    public String getTimestamp() { return timestamp; }
    public String getSource() { return source; }
    public String getEventType() { return eventType; }
    public String getSeverity() { return severity; }
    public String getDescription() { return description; }
    public void setSeverity(String severity) { this.severity = severity; }
    public void setDescription(String description) { this.description = description; }
    public boolean hasValidTimestamp() {
        return FailedLoginDetector.parseTimestampSafe(this.timestamp) != null;
    }
    public String toCsvLine() {
        return String.format("%s,%s,%s,%s,%s",
                escapeCsv(timestamp),
                escapeCsv(source),
                escapeCsv(eventType),
                escapeCsv(severity),
                escapeCsv(description));
    }
    private String escapeCsv(String val) {
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
    @Override
    public String toString() {
        return String.format("[%s] [%-8s] [%-8s] [%-8s] %s",
                timestamp, source, eventType, severity, description);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityLog that = (SecurityLog) o;
        return Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(source, that.source) &&
               Objects.equals(eventType, that.eventType) &&
               Objects.equals(severity, that.severity) &&
               Objects.equals(description, that.description);
    }
    @Override
    public int hashCode() {
        return Objects.hash(timestamp, source, eventType, severity, description);
    }
}
