import java.util.Arrays;
public class SAIS {
    public static int[] build(String text) {
        if (text == null || text.isEmpty()) return new int[0];
        int n = text.length();
        int[] s = new int[n + 1];
        for (int i = 0; i < n; i++) {
            s[i] = (int) text.charAt(i) + 1;
        }
        s[n] = 0; 
        int maxChar = 256;
        for (int c : s) if (c > maxChar) maxChar = c;
        int[] sa = new int[n + 1];
        sais(s, sa, n + 1, maxChar + 1);
        int[] result = new int[n];
        System.arraycopy(sa, 1, result, 0, n);
        return result;
    }
    private static void sais(int[] s, int[] sa, int n, int k) {
        boolean[] isS = new boolean[n];
        isS[n - 1] = true;
        for (int i = n - 2; i >= 0; i--) {
            isS[i] = (s[i] < s[i + 1]) || (s[i] == s[i + 1] && isS[i + 1]);
        }
        int[] count = new int[k];
        for (int i = 0; i < n; i++) count[s[i]]++;
        int[] lms = new int[n];
        int m = 0;
        for (int i = 1; i < n; i++) {
            if (isS[i] && !isS[i - 1]) {
                lms[m++] = i;
            }
        }
        induceSort(s, sa, n, k, isS, count, lms, m);
        int[] lmsOrder = new int[m];
        int lmsIdx = 0;
        for (int i = 0; i < n; i++) {
            int pos = sa[i];
            if (pos > 0 && isS[pos] && !isS[pos - 1]) {
                lmsOrder[lmsIdx++] = pos;
            }
        }
        int[] name = new int[n];
        Arrays.fill(name, -1);
        int curName = 0;
        if (m > 0) {
            name[lmsOrder[0]] = curName;
        }
        for (int i = 1; i < m; i++) {
            int p1 = lmsOrder[i - 1];
            int p2 = lmsOrder[i];
            boolean diff = false;
            for (int d = 0; ; d++) {
                if (s[p1 + d] != s[p2 + d] || isS[p1 + d] != isS[p2 + d]) {
                    diff = true;
                    break;
                }
                if (d > 0 && ((isS[p1 + d] && !isS[p1 + d - 1]) || (isS[p2 + d] && !isS[p2 + d - 1]))) {
                    break;
                }
            }
            if (diff) curName++;
            name[p2] = curName;
        }
        int[] s1 = new int[m];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            if (name[i] >= 0) s1[idx++] = name[i];
        }
        int[] sa1 = new int[m];
        if (curName + 1 < m) {
            sais(s1, sa1, m, curName + 1);
        } else {
            for (int i = 0; i < m; i++) {
                sa1[s1[i]] = i;
            }
        }
        int[] sortedLms = new int[m];
        for (int i = 0; i < m; i++) {
            sortedLms[i] = lms[sa1[i]];
        }
        induceSort(s, sa, n, k, isS, count, sortedLms, m);
    }
    private static void induceSort(int[] s, int[] sa, int n, int k, boolean[] isS, int[] count, int[] lmsList, int m) {
        Arrays.fill(sa, -1);
        int[] bucket = new int[k];
        getBuckets(count, bucket, k, true);
        for (int i = m - 1; i >= 0; i--) {
            int c = s[lmsList[i]];
            sa[--bucket[c]] = lmsList[i];
        }
        getBuckets(count, bucket, k, false);
        for (int i = 0; i < n; i++) {
            if (sa[i] > 0) {
                int j = sa[i] - 1;
                if (!isS[j]) {
                    int c = s[j];
                    sa[bucket[c]++] = j;
                }
            }
        }
        getBuckets(count, bucket, k, true);
        for (int i = n - 1; i >= 0; i--) {
            if (sa[i] > 0) {
                int j = sa[i] - 1;
                if (isS[j]) {
                    int c = s[j];
                    sa[--bucket[c]] = j;
                }
            }
        }
    }
    private static void getBuckets(int[] count, int[] bucket, int k, boolean end) {
        int sum = 0;
        for (int i = 0; i < k; i++) {
            sum += count[i];
            bucket[i] = end ? sum : (sum - count[i]);
        }
    }
}
