import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
public class ReservoirSampling {
    public static <T> List<T> sample(Iterator<T> stream, int k, Random rand) {
        List<T> reservoir = new ArrayList<>(k);
        int count = 0;
        while (stream.hasNext() && count < k) {
            reservoir.add(stream.next());
            count++;
        }
        while (stream.hasNext()) {
            T item = stream.next();
            count++;
            int j = rand.nextInt(count);
            if (j < k) {
                reservoir.set(j, item);
            }
        }
        return reservoir;
    }
}
