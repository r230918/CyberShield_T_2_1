import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
public class EdmondsKarp {
    public static int maxFlow(FlowGraph net, int source, int sink) {
        net.resetFlow();
        int maxFlow = 0;
        int n = net.n;
        int[] parent = new int[n];
        while (true) {
            Arrays.fill(parent, -1);
            parent[source] = source;
            Queue<Integer> q = new LinkedList<>();
            q.add(source);
            while (!q.isEmpty()) {
                int u = q.poll();
                if (u == sink) break;
                for (int v = 0; v < n; v++) {
                    int residual = net.capacity[u][v] - net.flow[u][v];
                    if (parent[v] == -1 && residual > 0) {
                        parent[v] = u;
                        q.add(v);
                    }
                }
            }
            if (parent[sink] == -1) {
                break;
            }
            int push = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                push = Math.min(push, net.capacity[u][v] - net.flow[u][v]);
            }
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                net.flow[u][v] += push;
                net.flow[v][u] -= push;
            }
            maxFlow += push;
        }
        return maxFlow;
    }
}
