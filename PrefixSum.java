public class PrefixSum {
    public static long[] compute(long[] input) {
        if (input == null || input.length == 0) return new long[0];
        long[] output = new long[input.length];
        output[0] = input[0];
        for (int i = 1; i < input.length; i++) {
            output[i] = output[i - 1] + input[i];
        }
        return output;
    }
}
