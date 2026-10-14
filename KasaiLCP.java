public class KasaiLCP {
    public static int[] build(String text, int[] sa) {
        int n = text.length();
        if (n == 0 || sa == null || sa.length == 0) return new int[0];
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }
        int[] lcp = new int[n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] == 0) {
                lcp[0] = 0;
                continue;
            }
            int j = sa[rank[i] - 1]; 
            while (i + k < n && j + k < n && text.charAt(i + k) == text.charAt(j + k)) {
                k++;
            }
            lcp[rank[i]] = k;
            if (k > 0) {
                k--;
            }
        }
        return lcp;
    }
}
