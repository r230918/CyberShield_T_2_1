import java.util.Objects;
public class DetectionRule {
    private final String ruleId;
    private final String pattern;
    private final String severity; 
    private final String description;
    private boolean enabled;
    public DetectionRule(String ruleId, String pattern, String severity, String description, boolean enabled) {
        this.ruleId = ruleId;
        this.pattern = pattern;
        this.severity = severity.toUpperCase();
        this.description = description;
        this.enabled = enabled;
    }
    public String getRuleId() { return ruleId; }
    public String getPattern() { return pattern; }
    public String getSeverity() { return severity; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    @Override
    public String toString() {
        return String.format("[%s] (Active: %s) Pattern: '%s' | Severity: %s | %s",
                ruleId, enabled ? "YES" : "NO", pattern, severity, description);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetectionRule that = (DetectionRule) o;
        return Objects.equals(ruleId, that.ruleId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(ruleId);
    }
}
