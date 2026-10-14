import java.util.*;
public class BipartiteMatching {
    public static class MatchResult {
        public final int matchCount;
        public final Map<String, String> assignments;
        public MatchResult(int matchCount, Map<String, String> assignments) {
            this.matchCount = matchCount;
            this.assignments = assignments;
        }
    }
    public static MatchResult match(List<String> setA, List<String> setB, boolean[][] adj) {
        int m = setA.size();
        int n = setB.size();
        int total = 1 + m + n + 1;
        int s = 0;
        int t = total - 1;
        List<String> names = new ArrayList<>();
        names.add("SOURCE");
        names.addAll(setA);
        names.addAll(setB);
        names.add("SINK");
        FlowGraph net = new FlowGraph(names);
        for (int i = 0; i < m; i++) {
            net.addEdge(s, 1 + i, 1);
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (adj[i][j]) {
                    net.addEdge(1 + i, 1 + m + j, 1);
                }
            }
        }
        for (int j = 0; j < n; j++) {
            net.addEdge(1 + m + j, t, 1);
        }
        int matches = Dinic.maxFlow(net, s, t);
        Map<String, String> assign = new LinkedHashMap<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (net.flow[1 + i][1 + m + j] == 1) {
                    assign.put(setA.get(i), setB.get(j));
                }
            }
        }
        return new MatchResult(matches, assign);
    }
}
