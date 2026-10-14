import java.util.Arrays;
public class SuffixArray {
    public static int[] build(String text) {
        if (text == null || text.isEmpty()) {
            return new int[0];
        }
        int n = text.length();
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        Arrays.sort(order, (a, b) -> {
            int lenA = n - a;
            int lenB = n - b;
            int minLen = Math.min(lenA, lenB);
            for (int k = 0; k < minLen; k++) {
                char c1 = text.charAt(a + k);
                char c2 = text.charAt(b + k);
                if (c1 != c2) return Character.compare(c1, c2);
            }
            return Integer.compare(lenA, lenB);
        });
        int[] sa = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = order[i];
        }
        return sa;
    }
}
