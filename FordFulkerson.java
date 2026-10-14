import java.util.Arrays;
public class FordFulkerson {
    public static int maxFlow(FlowGraph net, int source, int sink) {
        net.resetFlow();
        int maxFlow = 0;
        int n = net.n;
        boolean[] visited = new boolean[n];
        while (true) {
            Arrays.fill(visited, false);
            int pushed = dfsPath(net, source, sink, Integer.MAX_VALUE, visited);
            if (pushed == 0) break;
            maxFlow += pushed;
        }
        return maxFlow;
    }
    private static int dfsPath(FlowGraph net, int u, int sink, int flowIn, boolean[] visited) {
        if (u == sink) return flowIn;
        visited[u] = true;
        for (int v = 0; v < net.n; v++) {
            int residual = net.capacity[u][v] - net.flow[u][v];
            if (!visited[v] && residual > 0) {
                int tr = dfsPath(net, v, sink, Math.min(flowIn, residual), visited);
                if (tr > 0) {
                    net.flow[u][v] += tr;
                    net.flow[v][u] -= tr;
                    return tr;
                }
            }
        }
        return 0;
    }
}
