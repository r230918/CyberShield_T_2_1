import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class BitmaskDP {
    public static class TSPResult {
        public final int minCost;
        public final List<Integer> tour;
        public TSPResult(int minCost, List<Integer> tour) {
            this.minCost = minCost;
            this.tour = tour;
        }
    }
    public static TSPResult solve(int[][] dist) {
        int n = dist.length;
        if (n == 0) return new TSPResult(0, Collections.emptyList());
        if (n == 1) return new TSPResult(0, List.of(0, 0));
        int allVisited = (1 << n) - 1;
        int[][] memo = new int[1 << n][n];
        int[][] parent = new int[1 << n][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        int minCost = tspHelper(1, 0, dist, memo, parent, allVisited, n);
        List<Integer> tour = new ArrayList<>();
        int mask = 1;
        int curr = 0;
        tour.add(curr);
        for (int step = 0; step < n - 1; step++) {
            int next = parent[mask][curr];
            if (next == -1) break;
            tour.add(next);
            mask |= (1 << next);
            curr = next;
        }
        tour.add(0);
        return new TSPResult(minCost, tour);
    }
    private static int tspHelper(int mask, int u, int[][] dist, int[][] memo, int[][] parent, int allVisited, int n) {
        if (mask == allVisited) {
            return dist[u][0];
        }
        if (memo[mask][u] != -1) {
            return memo[mask][u];
        }
        int ans = 100000000;
        int bestNext = -1;
        for (int v = 0; v < n; v++) {
            if ((mask & (1 << v)) == 0) {
                int cost = dist[u][v] + tspHelper(mask | (1 << v), v, dist, memo, parent, allVisited, n);
                if (cost < ans) {
                    ans = cost;
                    bestNext = v;
                }
            }
        }
        parent[mask][u] = bestNext;
        return memo[mask][u] = ans;
    }
}
