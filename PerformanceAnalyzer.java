import java.util.*;
public class PerformanceAnalyzer {
    public static class BenchmarkResult {
        public final String algorithmName;
        public final long elapsedNano;
        public final double elapsedMilli;
        public final String correctnessSummary;
        public final String complexity;
        public BenchmarkResult(String algorithmName, long elapsedNano, String correctnessSummary, String complexity) {
            this.algorithmName = algorithmName;
            this.elapsedNano = elapsedNano;
            this.elapsedMilli = elapsedNano / 1_000_000.0;
            this.correctnessSummary = correctnessSummary;
            this.complexity = complexity;
        }
        @Override
        public String toString() {
            return String.format("%-22s | Time: %8.3f ms (%10d ns) | %-12s | Complexity: %s",
                    algorithmName, elapsedMilli, elapsedNano, correctnessSummary, complexity);
        }
    }
    public static List<BenchmarkResult> compareStringAlgorithms(String text, String pattern, int iterations) {
        List<BenchmarkResult> results = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            KMP.search(text, pattern);
            RabinKarp.search(text, pattern);
            ZAlgorithm.search(text, pattern);
        }
        List<Integer> kmpMatches = null;
        long startKMP = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            kmpMatches = KMP.search(text, pattern);
        }
        long timeKMP = (System.nanoTime() - startKMP) / iterations;
        results.add(new BenchmarkResult("KMP Algorithm", timeKMP,
                kmpMatches.size() + " matches", "O(N + M)"));
        List<Integer> rkMatches = null;
        long startRK = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            rkMatches = RabinKarp.search(text, pattern);
        }
        long timeRK = (System.nanoTime() - startRK) / iterations;
        boolean rkCorrect = rkMatches.equals(kmpMatches);
        results.add(new BenchmarkResult("Rabin-Karp Algorithm", timeRK,
                (rkCorrect ? "Matches KMP (" + rkMatches.size() + ")" : "Mismatch!"), "Avg O(N+M), Worst O(NM)"));
        List<Integer> zMatches = null;
        long startZ = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            zMatches = ZAlgorithm.search(text, pattern);
        }
        long timeZ = (System.nanoTime() - startZ) / iterations;
        boolean zCorrect = zMatches.equals(kmpMatches);
        results.add(new BenchmarkResult("Z-Algorithm", timeZ,
                (zCorrect ? "Matches KMP (" + zMatches.size() + ")" : "Mismatch!"), "O(N + M)"));
        return results;
    }
    public static List<BenchmarkResult> compareDistanceAlgorithms(String s1, String s2, int iterations) {
        List<BenchmarkResult> results = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            LevenshteinDistance.compute(s1, s2);
            DamerauLevenshtein.compute(s1, s2);
        }
        int levDist = 0;
        long startLev = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            levDist = LevenshteinDistance.compute(s1, s2).distance;
        }
        long timeLev = (System.nanoTime() - startLev) / iterations;
        results.add(new BenchmarkResult("Levenshtein (DP)", timeLev, "Dist: " + levDist, "O(|S1| * |S2|)"));
        int damDist = 0;
        long startDam = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            damDist = DamerauLevenshtein.compute(s1, s2);
        }
        long timeDam = (System.nanoTime() - startDam) / iterations;
        results.add(new BenchmarkResult("Damerau-Levenshtein", timeDam, "Dist: " + damDist, "O(|S1| * |S2|)"));
        return results;
    }
    public static List<BenchmarkResult> compareFlowAlgorithms(FlowGraph templateNet, int source, int sink, int iterations) {
        List<BenchmarkResult> results = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            EdmondsKarp.maxFlow(templateNet, source, sink);
            FordFulkerson.maxFlow(templateNet, source, sink);
            Dinic.maxFlow(templateNet, source, sink);
        }
        int ekFlow = 0;
        long startEK = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ekFlow = EdmondsKarp.maxFlow(templateNet, source, sink);
        }
        long timeEK = (System.nanoTime() - startEK) / iterations;
        results.add(new BenchmarkResult("Edmonds-Karp (BFS)", timeEK, "Flow: " + ekFlow, "O(V * E^2)"));
        int ffFlow = 0;
        long startFF = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ffFlow = FordFulkerson.maxFlow(templateNet, source, sink);
        }
        long timeFF = (System.nanoTime() - startFF) / iterations;
        results.add(new BenchmarkResult("Ford-Fulkerson (DFS)", timeFF, "Flow: " + ffFlow, "O(E * MaxFlow)"));
        int dinicFlow = 0;
        long startDinic = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            dinicFlow = Dinic.maxFlow(templateNet, source, sink);
        }
        long timeDinic = (System.nanoTime() - startDinic) / iterations;
        results.add(new BenchmarkResult("Dinic's Algorithm", timeDinic, "Flow: " + dinicFlow, "O(V^2 * E)"));
        return results;
    }
    public static List<BenchmarkResult> compareQuickSort(int[] original, int iterations) {
        List<BenchmarkResult> results = new ArrayList<>();
        long startStd = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            int[] copy = Arrays.copyOf(original, original.length);
            RandomizedQuickSort.deterministicSort(copy);
        }
        long timeStd = (System.nanoTime() - startStd) / iterations;
        results.add(new BenchmarkResult("Standard QuickSort", timeStd, "Sorted " + original.length + " items", "Avg O(NlogN), Worst O(N^2)"));
        Random rand = new Random(42);
        long startRand = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            int[] copy = Arrays.copyOf(original, original.length);
            RandomizedQuickSort.sort(copy, rand);
        }
        long timeRand = (System.nanoTime() - startRand) / iterations;
        results.add(new BenchmarkResult("Randomized QuickSort", timeRand, "Sorted " + original.length + " items", "High Prob O(NlogN)"));
        return results;
    }
    public static List<BenchmarkResult> comparePrefixSum(long[] input, int iterations) {
        List<BenchmarkResult> results = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PrefixSum.compute(input);
            ParallelPrefixSum.compute(input);
        }
        long[] seqOut = null;
        long startSeq = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            seqOut = PrefixSum.compute(input);
        }
        long timeSeq = (System.nanoTime() - startSeq) / iterations;
        results.add(new BenchmarkResult("Sequential Prefix Sum", timeSeq, "Size: " + input.length, "Work O(N), Depth O(N)"));
        long[] parOut = null;
        long startPar = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            parOut = ParallelPrefixSum.compute(input);
        }
        long timePar = (System.nanoTime() - startPar) / iterations;
        boolean matches = Arrays.equals(seqOut, parOut);
        results.add(new BenchmarkResult("Parallel Prefix Sum", timePar,
                (matches ? "Equivalent Result" : "MISMATCH!"), "Work O(N), Depth O(log N)"));
        return results;
    }
}
