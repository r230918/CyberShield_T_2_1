import java.util.Arrays;
public class ParallelReduction {
    public static long parallelSum(long[] input) {
        if (input == null || input.length == 0) return 0;
        return Arrays.stream(input).parallel().sum();
    }
    public static long sequentialSum(long[] input) {
        if (input == null || input.length == 0) return 0;
        long sum = 0;
        for (long v : input) sum += v;
        return sum;
    }
}
