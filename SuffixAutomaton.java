import java.util.HashMap;
import java.util.Map;
public class SuffixAutomaton {
    public static class State {
        public int len = 0;
        public int link = -1;
        public Map<Character, Integer> next = new HashMap<>();
    }
    private final State[] st;
    private int sz = 0;
    private int last = 0;
    public SuffixAutomaton(String s) {
        int maxLen = (s == null) ? 1 : Math.max(1, s.length() * 2 + 2);
        st = new State[maxLen];
        for (int i = 0; i < maxLen; i++) st[i] = new State();
        st[0].len = 0;
        st[0].link = -1;
        sz = 1;
        last = 0;
        if (s != null) {
            for (char c : s.toCharArray()) {
                extend(c);
            }
        }
    }
    private void extend(char c) {
        int cur = sz++;
        st[cur].len = st[last].len + 1;
        int p = last;
        while (p != -1 && !st[p].next.containsKey(c)) {
            st[p].next.put(c, cur);
            p = st[p].link;
        }
        if (p == -1) {
            st[cur].link = 0;
        } else {
            int q = st[p].next.get(c);
            if (st[p].len + 1 == st[q].len) {
                st[cur].link = q;
            } else {
                int clone = sz++;
                st[clone].len = st[p].len + 1;
                st[clone].next = new HashMap<>(st[q].next);
                st[clone].link = st[q].link;
                while (p != -1 && st[p].next.get(c) != null && st[p].next.get(c) == q) {
                    st[p].next.put(c, clone);
                    p = st[p].link;
                }
                st[q].link = clone;
                st[cur].link = clone;
            }
        }
        last = cur;
    }
    public boolean contains(String s) {
        if (s == null || s.isEmpty()) return true;
        int cur = 0;
        for (char c : s.toCharArray()) {
            if (!st[cur].next.containsKey(c)) return false;
            cur = st[cur].next.get(c);
        }
        return true;
    }
}
