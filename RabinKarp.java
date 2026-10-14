import java.util.ArrayList;
import java.util.List;
public class RabinKarp {
    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }
        int n = text.length();
        int m = pattern.length();
        int d = 256; 
        long q = 1000000007L; 
        long pHash = 0;
        long tHash = 0;
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * d) % q;
        }
        for (int i = 0; i < m; i++) {
            pHash = (d * pHash + pattern.charAt(i)) % q;
            tHash = (d * tHash + text.charAt(i)) % q;
        }
        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches.add(i);
                }
            }
            if (i < n - m) {
                tHash = (d * (tHash - text.charAt(i) * h) + text.charAt(i + m)) % q;
                if (tHash < 0) {
                    tHash = (tHash + q);
                }
            }
        }
        return matches;
    }
}
