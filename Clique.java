import java.util.HashSet;
import java.util.Set;
public class Clique {
    public static Set<Integer> findMaxClique(int n, boolean[][] adj) {
        Set<Integer> bestClique = new HashSet<>();
        bronKerbosch(new HashSet<>(), getFullSet(n), new HashSet<>(), adj, bestClique);
        return bestClique;
    }
    private static void bronKerbosch(Set<Integer> R, Set<Integer> P, Set<Integer> X, boolean[][] adj, Set<Integer> best) {
        if (P.isEmpty() && X.isEmpty()) {
            if (R.size() > best.size()) {
                best.clear();
                best.addAll(R);
            }
            return;
        }
        int u = -1;
        if (!P.isEmpty()) u = P.iterator().next();
        else if (!X.isEmpty()) u = X.iterator().next();
        Set<Integer> candidates = new HashSet<>(P);
        if (u != -1) {
            for (int v : P) {
                if (adj[u][v]) candidates.remove(v);
            }
        }
        for (int v : candidates) {
            Set<Integer> newR = new HashSet<>(R);
            newR.add(v);
            Set<Integer> newP = new HashSet<>();
            for (int p : P) if (adj[v][p]) newP.add(p);
            Set<Integer> newX = new HashSet<>();
            for (int x : X) if (adj[v][x]) newX.add(x);
            bronKerbosch(newR, newP, newX, adj, best);
            P.remove(v);
            X.add(v);
        }
    }
    private static Set<Integer> getFullSet(int n) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < n; i++) set.add(i);
        return set;
    }
}
