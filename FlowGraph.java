import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class FlowGraph {
    public final int n;
    public final int[][] capacity;
    public final int[][] flow;
    public final List<String> vertexNames;
    public FlowGraph(int n) {
        this.n = n;
        this.capacity = new int[n][n];
        this.flow = new int[n][n];
        this.vertexNames = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            vertexNames.add("V" + i);
        }
    }
    public FlowGraph(List<String> names) {
        this.n = names.size();
        this.capacity = new int[n][n];
        this.flow = new int[n][n];
        this.vertexNames = new ArrayList<>(names);
    }
    public void addEdge(int u, int v, int cap) {
        if (u >= 0 && u < n && v >= 0 && v < n && cap >= 0) {
            capacity[u][v] += cap;
        }
    }
    public void resetFlow() {
        for (int i = 0; i < n; i++) {
            Arrays.fill(flow[i], 0);
        }
    }
}
