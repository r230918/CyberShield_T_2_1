import java.util.*;
public class MinimumCut {
    public static class MinCutResult {
        public final int cutCapacity;
        public final Set<Integer> sourceSet;
        public final Set<Integer> sinkSet;
        public final List<String> cutEdges;
        public MinCutResult(int cutCapacity, Set<Integer> sourceSet, Set<Integer> sinkSet, List<String> cutEdges) {
            this.cutCapacity = cutCapacity;
            this.sourceSet = sourceSet;
            this.sinkSet = sinkSet;
            this.cutEdges = cutEdges;
        }
    }
    public static MinCutResult find(FlowGraph net, int source, int sink) {
        int maxFlow = EdmondsKarp.maxFlow(net, source, sink);
        int n = net.n;
        boolean[] reachable = new boolean[n];
        Queue<Integer> q = new LinkedList<>();
        reachable[source] = true;
        q.add(source);
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v = 0; v < n; v++) {
                if (!reachable[v] && (net.capacity[u][v] - net.flow[u][v] > 0)) {
                    reachable[v] = true;
                    q.add(v);
                }
            }
        }
        Set<Integer> S = new HashSet<>();
        Set<Integer> T = new HashSet<>();
        for (int i = 0; i < n; i++) {
            if (reachable[i]) S.add(i);
            else T.add(i);
        }
        List<String> cutEdges = new ArrayList<>();
        int computedCutCapacity = 0;
        for (int u : S) {
            for (int v : T) {
                if (net.capacity[u][v] > 0) {
                    cutEdges.add(String.format("%s -> %s (Capacity: %d)",
                            net.vertexNames.get(u), net.vertexNames.get(v), net.capacity[u][v]));
                    computedCutCapacity += net.capacity[u][v];
                }
            }
        }
        return new MinCutResult(computedCutCapacity, S, T, cutEdges);
    }
}
