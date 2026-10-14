public class MatrixChainMultiplication {
    public static class MCMResult {
        public final int minMultiplications;
        public final String optimalParentheses;
        public MCMResult(int minMultiplications, String optimalParentheses) {
            this.minMultiplications = minMultiplications;
            this.optimalParentheses = optimalParentheses;
        }
    }
    public static MCMResult solve(int[] p) {
        int n = p.length - 1;
        if (n <= 0) return new MCMResult(0, "");
        int[][] m = new int[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];
        for (int i = 1; i <= n; i++) {
            m[i][i] = 0;
        }
        for (int len = 2; len <= n; len++) {
            for (int i = 1; i <= n - len + 1; i++) {
                int j = i + len - 1;
                m[i][j] = Integer.MAX_VALUE;
                for (int k = i; k <= j - 1; k++) {
                    int cost = m[i][k] + m[k + 1][j] + p[i - 1] * p[k] * p[j];
                    if (cost < m[i][j]) {
                        m[i][j] = cost;
                        s[i][j] = k;
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        buildOptimalParens(s, 1, n, sb);
        return new MCMResult(m[1][n], sb.toString());
    }
    private static void buildOptimalParens(int[][] s, int i, int j, StringBuilder sb) {
        if (i == j) {
            sb.append("A").append(i);
        } else {
            sb.append("(");
            buildOptimalParens(s, i, s[i][j], sb);
            sb.append(" x ");
            buildOptimalParens(s, s[i][j] + 1, j, sb);
            sb.append(")");
        }
    }
}
