package assignments.Ex0;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import assignments.Ex0.context.PrimeChecker;
import assignments.Ex0.interfaces.PrimeCheckStrategy;
import assignments.Ex0.strategies.MillerRabinStrategy;
import assignments.Ex0.strategies.SieveStrategy;
/**
 * This class is a basis for Ex0 (your first assigment),
 * The definition of the Ex0 can be found here: https://docs.google.com/document/d/1UtngN203ttQKf5ackCnXs4UnbAROZWHr/edit?usp=sharing&ouid=113711744349547563645&rtpof=true&sd=true
 * You are asked to complete the functions below and may add additional functions if needed.

 */
public class Ex0 {
    public final static long ID = 1;  // Do update your ID here

    private static final Map<String, PrimeCheckStrategy> STRATEGY_CACHE = new ConcurrentHashMap<>();
    private static final long SIEVE_LIMIT = 100_000_000L;
    private static final ExecutorService GLOBAL_POOL =Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    );

    private static PrimeChecker checker;
  
    static {
        PrimeCheckStrategy miller = new MillerRabinStrategy();
        STRATEGY_CACHE.put("miller", miller);
        checker = new PrimeChecker(miller);

        CompletableFuture.runAsync(() -> {
            try {
                PrimeCheckStrategy sieve = new SieveStrategy(SIEVE_LIMIT, GLOBAL_POOL);
                STRATEGY_CACHE.put("sieve", sieve);
            } catch (Exception e) {
                System.err.println("Failed to initialize sieve: " + e.getMessage());
            }
        }, GLOBAL_POOL);
    }


    /**
     * This function checks if n is a prime number.
     * Notes:
     * i) This code is very slow - make sure you improved it!
     * ii) Make sure to document your code
     *
     * @param n (Integer) - represented as long
     * @return true if and only if there is no integer (p) within the range of [2,n) which divides n.
     *
     */
    public static boolean isPrime(long n) {
        PrimeCheckStrategy chosen;
        if (n <= SIEVE_LIMIT && STRATEGY_CACHE.containsKey("sieve")) {
            chosen = STRATEGY_CACHE.get("sieve");
        } else {
            chosen = STRATEGY_CACHE.get("miller");
        }

        checker.setStrategy(chosen);
        return checker.isPrime(n);
    }





    private static long getPrimePairFallback(long start, long n) {
        if (n < 2 || (n & 1L) != 0L) return -1;
        long p1 = Math.max(2, start);
        PrimeCheckStrategy strategy = STRATEGY_CACHE.get("miller");

        if (p1 == 2 && isPrime(p1 + n)) return 2;
        if ((p1 & 1) == 0) p1++; 

        while (p1 < Long.MAX_VALUE - n) {
            if (strategy.isPrime(p1) && strategy.isPrime(p1 + n))
                return p1;
            p1 += 2; 
        }
        return -1;
    }

    private static long getClosestPrimePairFallback(long start, long n) {
        if (n < 2 || (n & 1L) != 0L) return -1;

        long prev = Math.max(2, start);
        while (!isPrime(prev)) prev++;

        long curr = prev + 1;
        while (true) {
            while (!isPrime(curr)) curr++;
            if (curr - prev == n) {
                return prev;
            }
            prev = curr;
            curr++;
            if (curr > 10_000_000_000L) break; 
        }
        return -1;
    }



    /// ////////////////////
    ///
    /**
     * This function finds the first prime integer (p1) >= start, for which p2=p1+n is also a prime number.
     * @param start - a starting value from which p1 should be searched for.
     * @param n - a positive (even) integer value.
     * @return the first prime number p1 such that: i) p1>=start, ii) p1+n is a prime number.
     * in case a wrong value is given to the function
     * (n<0 or n is an odd number) the function should return -1.
     *
     */
    public static long getPrimePair(long start, long n) {
        if (n < 2 || (n & 1) != 0) return -1; 

        long p1 = Math.max(3, start | 1);
        if (p1 == 2 && isPrime(p1 + n)) return 2;
        if ((p1 & 1) == 0) p1++;

        PrimeCheckStrategy strategy = STRATEGY_CACHE.get("sieve");
        if (strategy instanceof SieveStrategy sieve && p1 <= SIEVE_LIMIT) {
            return sieve.findPrimePairBitwise(p1, n);
        }

        return getPrimePairFallback(start, n);
    }

    /**
     * This function compute the first prime number p1 for which:
     * i) p1 >= start (p1 is a prime number)
     * ii) p1+n==p2 ia a prime number.
     * iii) there are no prime numbers in the (p1,p2) range.
     *
     * @param start a positive integer which is the lower bound of p1.
     * @param n - a positive even integer.
     * @return a prime number p1>=start that the following prime number is p1+n.
     */
    public static long getClosestPrimePair(long start, long n) {
        /// Add your code below ///
        /// ////////////////// ///
        if (n < 2 || (n & 1) != 0) return -1; 

        long p1 = Math.max(3, start | 1);
        if (p1 == 2 && isPrime(p1 + n)) return 2;
        if ((p1 & 1) == 0) p1++;

        PrimeCheckStrategy strategy = STRATEGY_CACHE.get("sieve");
        if (strategy instanceof SieveStrategy sieve && p1 <= SIEVE_LIMIT) {
            return sieve.findClosestPrimePairBitwise(p1, n,0);
        }
        return getClosestPrimePairFallback(start, n);

    }

    /**
     * This function compute the m'th positive integer p1 for which:
     * i) p1 is a prime number.
     * ii) p1+n==p2 ia a prime number.
     * iii) there are no prime numbers in the (p1,p2) range.
     *
     * @param m a none negative integer.
     * @param n - a positive even integer.
     * @return a prime number (p1) such that (p1,p1+n) are the m"th the closest prime pair (starting from 0).
     *
     */
    /**/
    public static long getMthClosestPrimePair(int m, long n) {
        if (m < 0 || n < 2 || (n & 1L) != 0L) {
            System.err.println("Invalid input: m=" + m + ", n=" + n);
            return -1;
        }

        SieveStrategy sieve = (SieveStrategy) STRATEGY_CACHE.get("sieve");
        MillerRabinStrategy miller = (MillerRabinStrategy) STRATEGY_CACHE.get("miller");

        long limit = sieve != null ? sieve.getLimit() : 0;
        long count = 0;

        long currPrime = 2;
        long next = 3;
        if (n <= limit) {
            long p1 = sieve.findClosestPrimePairBitwise(2, n, m);
            if (p1 != -1) return p1;
        }

        if (next < limit) next = limit + 1;
        while (!miller.isPrime(next)) next += 2;

        while (true) {
            long gap = next - currPrime;
            if (gap == n) {
                if (count == m) return currPrime;
                count++;
            }

            currPrime = next;
            next += 2;
            boolean nextPrime = (next <= limit)
                    ? sieve.isPrime(next)
                    : miller.isPrime(next);

            while (!nextPrime) {
                next += 2;
                nextPrime = (next <= limit)
                        ? sieve.isPrime(next)
                        : miller.isPrime(next);
            }

            if (next > 20_000_000_000L) {
                System.err.println("Search exceeded safe bound; stopping.");
                return -1;
            }
        }
    }

}