import java.util.HashSet;
import java.util.Set;
public class VertexCover {
    public static Set<Integer> findExactVertexCover(int n, boolean[][] adj) {
        Set<Integer> maxIS = IndependentSet.findMaxIndependentSet(n, adj);
        Set<Integer> vertexCover = new HashSet<>();
        for (int i = 0; i < n; i++) {
            if (!maxIS.contains(i)) {
                vertexCover.add(i);
            }
        }
        return vertexCover;
    }
}
