import java.util.HashMap;
import java.util.Map;
public class SuffixTree {
    public static class Node {
        public int start;
        public int end;
        public Map<Character, Node> children = new HashMap<>();
        public int suffixIndex = -1;
        public Node(int start, int end) {
            this.start = start;
            this.end = end;
        }
        public int edgeLength() {
            return end - start;
        }
    }
    private final String text;
    private final Node root;
    public SuffixTree(String text) {
        this.text = (text == null) ? "$" : (text.endsWith("$") ? text : text + "$");
        this.root = new Node(-1, -1);
        build();
    }
    private void build() {
        int n = text.length();
        for (int i = 0; i < n; i++) {
            insertSuffix(i);
        }
    }
    private void insertSuffix(int suffixStart) {
        Node curr = root;
        int i = suffixStart;
        int n = text.length();
        while (i < n) {
            char ch = text.charAt(i);
            if (!curr.children.containsKey(ch)) {
                Node leaf = new Node(i, n);
                leaf.suffixIndex = suffixStart;
                curr.children.put(ch, leaf);
                return;
            }
            Node child = curr.children.get(ch);
            int edgeLen = child.edgeLength();
            int matchLen = 0;
            while (matchLen < edgeLen && i + matchLen < n &&
                   text.charAt(child.start + matchLen) == text.charAt(i + matchLen)) {
                matchLen++;
            }
            if (matchLen == edgeLen) {
                curr = child;
                i += edgeLen;
            } else {
                Node split = new Node(child.start, child.start + matchLen);
                curr.children.put(ch, split);
                child.start += matchLen;
                split.children.put(text.charAt(child.start), child);
                if (i + matchLen < n) {
                    Node leaf = new Node(i + matchLen, n);
                    leaf.suffixIndex = suffixStart;
                    split.children.put(text.charAt(leaf.start), leaf);
                } else {
                    split.suffixIndex = suffixStart;
                }
                return;
            }
        }
    }
    public boolean containsSubstring(String pattern) {
        if (pattern == null || pattern.isEmpty()) return true;
        Node curr = root;
        int i = 0;
        int m = pattern.length();
        while (i < m) {
            char ch = pattern.charAt(i);
            if (!curr.children.containsKey(ch)) return false;
            Node child = curr.children.get(ch);
            int edgeLen = child.edgeLength();
            int matchLen = 0;
            while (matchLen < edgeLen && i + matchLen < m) {
                if (text.charAt(child.start + matchLen) != pattern.charAt(i + matchLen)) {
                    return false;
                }
                matchLen++;
            }
            i += matchLen;
            curr = child;
        }
        return true;
    }
}
