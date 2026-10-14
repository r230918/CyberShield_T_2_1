import java.util.Set;
public class IndependentSet {
    public static Set<Integer> findMaxIndependentSet(int n, boolean[][] adj) {
        boolean[][] complement = getComplementGraph(n, adj);
        return Clique.findMaxClique(n, complement);
    }
    public static boolean[][] getComplementGraph(int n, boolean[][] adj) {
        boolean[][] comp = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && !adj[i][j]) {
                    comp[i][j] = true;
                }
            }
        }
        return comp;
    }
}
