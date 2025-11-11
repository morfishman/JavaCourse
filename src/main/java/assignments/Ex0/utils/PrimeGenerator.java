package assignments.Ex0.utils;

import java.util.BitSet;
import java.util.concurrent.*;

public class PrimeGenerator {

    private static BitSet sieve;
    private static long sieveLimit = 0;
    public static synchronized BitSet getSieve() { return sieve; }

    public static void initSieveParallel(final long limit, ExecutorService pool) throws InterruptedException {
        if (limit <= 2) return;
        sieveLimit = limit;

        int half = (int)(limit / 2);
        sieve = new BitSet(half);
        sieve.set(0, half, true);
        sieve.clear(0); 

        int sqrt = (int)Math.sqrt(limit);
        int threads = Math.max(1, Runtime.getRuntime().availableProcessors());
        ExecutorService exec = pool != null ? pool : Executors.newFixedThreadPool(threads);

        final int step = (sqrt + threads - 1) / threads;
        CountDownLatch latch = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            final int start = 3 + 2 * (t * step);
            final int end   = Math.min(sqrt, 3 + 2 * ((t + 1) * step));
            exec.submit(() -> {
                for (int i = start; i < end; i += 2) {
                    if (sieve.get(i >> 1)) {
                        for (long j = (long)i * i; j <= limit; j += 2L * i)
                            sieve.clear((int)(j >> 1));
                    }
                }
                latch.countDown();
            });
        }

        latch.await(); 
    }


    public static boolean isPrime(long n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if ((n & 1) == 0) return false;
        if (n <= sieveLimit) return sieve.get((int)(n >> 1));
        int sqrt = (int)Math.sqrt(n);
        for (int i = 3; i <= sqrt; i += 2) {
            if (i <= sieveLimit && !sieve.get(i >> 1)) continue;
            if (n % i == 0) return false;
        }
        return true;
    }
}
