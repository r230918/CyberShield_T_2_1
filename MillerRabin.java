import java.math.BigInteger;
import java.util.Random;
public class MillerRabin {
    public static boolean test(long n, int k, Random rand) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0) return false;
        long d = n - 1;
        int s = 0;
        while (d % 2 == 0) {
            d /= 2;
            s++;
        }
        BigInteger bN = BigInteger.valueOf(n);
        BigInteger bD = BigInteger.valueOf(d);
        for (int i = 0; i < k; i++) {
            long a = 2 + (Math.abs(rand.nextLong()) % (n - 3));
            BigInteger bA = BigInteger.valueOf(a);
            BigInteger x = bA.modPow(bD, bN);
            if (x.equals(BigInteger.ONE) || x.equals(BigInteger.valueOf(n - 1))) {
                continue;
            }
            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = x.modPow(BigInteger.valueOf(2), bN);
                if (x.equals(BigInteger.valueOf(n - 1))) {
                    composite = false;
                    break;
                }
            }
            if (composite) {
                return false;
            }
        }
        return true;
    }
}
