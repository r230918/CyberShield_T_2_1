import java.util.HashSet;
import java.util.Set;
public class VertexCoverApproximation {
    public static Set<Integer> findApproximateVertexCover(int n, boolean[][] adj) {
        Set<Integer> cover = new HashSet<>();
        boolean[] visited = new boolean[n];
        for (int u = 0; u < n; u++) {
            if (!visited[u]) {
                for (int v = u + 1; v < n; v++) {
                    if (adj[u][v] && !visited[v]) {
                        visited[u] = true;
                        visited[v] = true;
                        cover.add(u);
                        cover.add(v);
                        break;
                    }
                }
            }
        }
        return cover;
    }
}
