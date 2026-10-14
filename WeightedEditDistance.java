public class WeightedEditDistance {
    public static int compute(String s1, String s2, int insertCost, int deleteCost, int subCost) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i * deleteCost;
        for (int j = 0; j <= n; j++) dp[0][j] = j * insertCost;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j] + deleteCost,
                               Math.min(dp[i][j - 1] + insertCost,
                                        dp[i - 1][j - 1] + subCost));
                }
            }
        }
        return dp[m][n];
    }
}
