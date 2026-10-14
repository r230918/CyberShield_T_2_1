import java.util.Arrays;
public class ParallelPrefixSum {
    public static long[] compute(long[] input) {
        if (input == null || input.length == 0) return new long[0];
        int n = input.length;
        long[] output = new long[n];
        System.arraycopy(input, 0, output, 0, n);
        Arrays.parallelPrefix(output, Long::sum);
        return output;
    }
    public static class BrentAnalysis {
        public final long work;
        public final long depth;
        public final double speedup;
        public final double brentBoundTime;
        public BrentAnalysis(long work, long depth, int processors) {
            this.work = work;
            this.depth = depth;
            this.brentBoundTime = ((double) work / processors) + depth;
            this.speedup = (double) work / Math.max(1.0, brentBoundTime);
        }
    }
    public static BrentAnalysis analyzeBrent(int n, int processors) {
        long work = n - 1;
        long depth = (long) Math.ceil(Math.log(n) / Math.log(2));
        return new BrentAnalysis(work, depth, processors);
    }
}
