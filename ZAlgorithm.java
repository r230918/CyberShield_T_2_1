import java.util.ArrayList;
import java.util.List;
public class ZAlgorithm {
    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }
        String concat = pattern + "$" + text;
        int l = concat.length();
        int[] z = new int[l];
        int left = 0, right = 0;
        for (int i = 1; i < l; i++) {
            if (i <= right) {
                z[i] = Math.min(right - i + 1, z[i - left]);
            }
            while (i + z[i] < l && concat.charAt(z[i]) == concat.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }
        }
        int m = pattern.length();
        for (int i = m + 1; i < l; i++) {
            if (z[i] >= m) {
                matches.add(i - m - 1);
            }
        }
        return matches;
    }
}
