import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
public class Dinic {
    public static int maxFlow(FlowGraph net, int source, int sink) {
        net.resetFlow();
        int maxFlow = 0;
        int n = net.n;
        int[] level = new int[n];
        int[] ptr = new int[n];
        while (bfsLevels(net, source, sink, level)) {
            Arrays.fill(ptr, 0);
            while (true) {
                int pushed = dinicDFS(net, source, sink, Integer.MAX_VALUE, level, ptr);
                if (pushed == 0) break;
                maxFlow += pushed;
            }
        }
        return maxFlow;
    }
    private static boolean bfsLevels(FlowGraph net, int source, int sink, int[] level) {
        Arrays.fill(level, -1);
        level[source] = 0;
        Queue<Integer> q = new LinkedList<>();
        q.add(source);
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v = 0; v < net.n; v++) {
                int residual = net.capacity[u][v] - net.flow[u][v];
                if (residual > 0 && level[v] == -1) {
                    level[v] = level[u] + 1;
                    q.add(v);
                }
            }
        }
        return level[sink] != -1;
    }
    private static int dinicDFS(FlowGraph net, int u, int sink, int flowIn, int[] level, int[] ptr) {
        if (u == sink || flowIn == 0) return flowIn;
        for (int v = ptr[u]; v < net.n; v = ++ptr[u]) {
            int residual = net.capacity[u][v] - net.flow[u][v];
            if (level[v] == level[u] + 1 && residual > 0) {
                int tr = dinicDFS(net, v, sink, Math.min(flowIn, residual), level, ptr);
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
