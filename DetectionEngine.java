import java.util.*;
public final class DetectionEngine {
    private final List<DetectionRule> rules = new ArrayList<>();
    public DetectionEngine() {
        addRule(new DetectionRule("R01", "failed login", "HIGH", "Authentication anomaly / Brute force indication", true));
        addRule(new DetectionRule("R02", "malware signature", "CRITICAL", "Host compromise signature match", true));
        addRule(new DetectionRule("R03", "unauthorized access", "HIGH", "Access control breach attempt", true));
        addRule(new DetectionRule("R04", "port scan", "MEDIUM", "Network reconnaissance indicator", true));
        addRule(new DetectionRule("R05", "ransomware", "CRITICAL", "Ransomware encryption indicator", true));
        addRule(new DetectionRule("R06", "privilege escalation", "HIGH", "Privilege boundary violation", true));
        addRule(new DetectionRule("R07", "brute force", "HIGH", "Repeated credential guessing attempt", true));
        addRule(new DetectionRule("R08", "exfiltration", "CRITICAL", "Data exfiltration indicator", true));
        addRule(new DetectionRule("R09", "sql injection", "HIGH", "Web application injection attack", true));
    }
    public List<DetectionRule> getRules() {
        return Collections.unmodifiableList(rules);
    }
    public void addRule(DetectionRule rule) {
        rules.removeIf(r -> r.getRuleId().equalsIgnoreCase(rule.getRuleId()));
        rules.add(rule);
    }
    public boolean removeRule(String ruleId) {
        return rules.removeIf(r -> r.getRuleId().equalsIgnoreCase(ruleId));
    }
    public static class MatchFinding {
        public final DetectionRule rule;
        public final int position;
        public MatchFinding(DetectionRule rule, int position) {
            this.rule = rule;
            this.position = position;
        }
    }
    public List<MatchFinding> evaluateText(String text) {
        List<MatchFinding> findings = new ArrayList<>();
        if (text == null || text.isBlank()) return findings;
        List<DetectionRule> activeRules = new ArrayList<>();
        List<String> patterns = new ArrayList<>();
        for (DetectionRule r : rules) {
            if (r.isEnabled()) {
                activeRules.add(r);
                patterns.add(r.getPattern().toLowerCase());
            }
        }
        if (patterns.isEmpty()) return findings;
        String lowerText = text.toLowerCase();
        AhoCorasick ac = new AhoCorasick(patterns);
        List<AhoCorasick.MatchResult> acMatches = ac.searchAll(lowerText);
        for (AhoCorasick.MatchResult m : acMatches) {
            for (DetectionRule r : activeRules) {
                if (r.getPattern().equalsIgnoreCase(m.pattern)) {
                    findings.add(new MatchFinding(r, m.index));
                    break;
                }
            }
        }
        return findings;
    }
    public List<MatchFinding> evaluateLog(SecurityLog log) {
        if (log == null) return Collections.emptyList();
        String fullContent = log.getSource() + " " + log.getEventType() + " " + log.getDescription();
        return evaluateText(fullContent);
    }
}
