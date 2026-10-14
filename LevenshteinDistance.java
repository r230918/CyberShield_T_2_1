import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class LevenshteinDistance {
    public static class EditDetail {
        public final int distance;
        public final List<String> operations;
        public EditDetail(int distance, List<String> operations) {
            this.distance = distance;
            this.operations = operations;
        }
    }
    public static EditDetail compute(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], 
                            Math.min(dp[i - 1][j],           
                                     dp[i][j - 1]));         
                }
            }
        }
        List<String> ops = new ArrayList<>();
        int i = m, j = n;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && s1.charAt(i - 1) == s2.charAt(j - 1)) {
                ops.add(String.format("Keep '%c'", s1.charAt(i - 1)));
                i--; j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                ops.add(String.format("Substitute '%c' with '%c'", s1.charAt(i - 1), s2.charAt(j - 1)));
                i--; j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                ops.add(String.format("Delete '%c' from first message", s1.charAt(i - 1)));
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                ops.add(String.format("Insert '%c' into first message", s2.charAt(j - 1)));
                j--;
            } else {
                break;
            }
        }
        Collections.reverse(ops);
        return new EditDetail(dp[m][n], ops);
    }
}
