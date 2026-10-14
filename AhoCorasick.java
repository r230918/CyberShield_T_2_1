import java.util.*;
public class AhoCorasick {
    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        Node fail = null;
        List<String> output = new ArrayList<>();
    }
    private final Node root;
    public AhoCorasick(List<String> patterns) {
        root = new Node();
        buildTrie(patterns);
        buildFailTransitions();
    }
    private void buildTrie(List<String> patterns) {
        for (String p : patterns) {
            if (p == null || p.isEmpty()) continue;
            Node curr = root;
            for (char c : p.toCharArray()) {
                curr = curr.children.computeIfAbsent(c, k -> new Node());
            }
            curr.output.add(p);
        }
    }
    private void buildFailTransitions() {
        Queue<Node> queue = new LinkedList<>();
        for (Node child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
                char c = entry.getKey();
                Node child = entry.getValue();
                Node f = curr.fail;
                while (f != null && !f.children.containsKey(c)) {
                    f = f.fail;
                }
                child.fail = (f != null) ? f.children.get(c) : root;
                if (child.fail != null && !child.fail.output.isEmpty()) {
                    child.output.addAll(child.fail.output);
                }
                queue.add(child);
            }
        }
    }
    public static class MatchResult {
        public final int index;
        public final String pattern;
        public MatchResult(int index, String pattern) {
            this.index = index;
            this.pattern = pattern;
        }
        @Override
        public String toString() {
            return String.format("'%s' at pos %d", pattern, index);
        }
    }
    public List<MatchResult> searchAll(String text) {
        List<MatchResult> results = new ArrayList<>();
        if (text == null || text.isEmpty()) return results;
        Node curr = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            while (curr != root && !curr.children.containsKey(c)) {
                curr = curr.fail;
            }
            if (curr.children.containsKey(c)) {
                curr = curr.children.get(c);
            }
            for (String pat : curr.output) {
                results.add(new MatchResult(i - pat.length() + 1, pat));
            }
        }
        return results;
    }
}
