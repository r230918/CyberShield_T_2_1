import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class SuffixArraySearch {
    public static List<Integer> search(String text, String pattern, int[] sa) {
        List<Integer> occurrences = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || sa == null || sa.length == 0 || text.length() < pattern.length()) {
            return occurrences;
        }
        int n = text.length();
        int low = 0, high = n - 1;
        int leftMatch = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = sa[mid];
            int cmp = comparePrefix(text, suffixStart, pattern);
            if (cmp >= 0) {
                if (cmp == 0) leftMatch = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        if (leftMatch == -1) return occurrences;
        low = 0; high = n - 1;
        int rightMatch = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = sa[mid];
            int cmp = comparePrefix(text, suffixStart, pattern);
            if (cmp <= 0) {
                if (cmp == 0) rightMatch = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        if (rightMatch != -1) {
            for (int k = leftMatch; k <= rightMatch; k++) {
                occurrences.add(sa[k]);
            }
            Collections.sort(occurrences);
        }
        return occurrences;
    }
    private static int comparePrefix(String text, int textStart, String pattern) {
        int m = pattern.length();
        int n = text.length();
        for (int i = 0; i < m; i++) {
            if (textStart + i >= n) {
                return -1;
            }
            char c1 = text.charAt(textStart + i);
            char c2 = pattern.charAt(i);
            if (c1 != c2) {
                return Character.compare(c1, c2);
            }
        }
        return 0;
    }
}
