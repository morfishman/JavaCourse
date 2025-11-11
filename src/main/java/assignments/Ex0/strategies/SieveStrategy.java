package assignments.Ex0.strategies;

import assignments.Ex0.interfaces.PrimeCheckStrategy;
import assignments.Ex0.utils.PrimeGenerator;

import java.util.BitSet;
import java.util.concurrent.ExecutorService;

public class SieveStrategy implements PrimeCheckStrategy {

    private final long limit;
    private final BitSet sieve;
    private final long[] words;

    public SieveStrategy(long limit, ExecutorService sharedPool) throws InterruptedException {
        this.limit = limit;
        PrimeGenerator.initSieveParallel(limit, sharedPool);
        this.sieve = PrimeGenerator.getSieve();
        this.words = sieve.toLongArray();
    }
   
    

    @Override
    public boolean isPrime(long n) {
        return PrimeGenerator.isPrime(n);
    }

    public BitSet getBitSet() {
        return sieve;
    }

    public long getLimit() {
        return limit;
    }

    public long findPrimePairBitwise(long start, long n) {
        if (n < 2 || (n & 1) != 0) return -1;
        if (sieve == null) return -1;

        long p1 = Math.max(2, start);
        if (p1 == 2 && isPrime(p1 + n)) return 2;
        if ((p1 & 1) == 0) p1++;
        long[] words = this.words;
        int shift = (int) (n >> 1);  
        int wordShift = shift >>> 6; 
        int bitShift = shift & 63; 

        long startIndex = p1 >> 1;  
        int startWord = (int) (startIndex >>> 6);
        int startBit  = (int) (startIndex & 63);

        long mask = ~((1L << startBit) - 1);
        for (int w = startWord; w < words.length - wordShift - 1; w++) {
            long low = words[w] & mask;
            long high = words[w + wordShift];
            long next = (w + wordShift + 1 < words.length) ? words[w + wordShift + 1] : 0;
            long shiftedHigh = (bitShift == 0) ? high : (high >>> bitShift) | (next << (64 - bitShift));
            long combined = low & shiftedHigh;

            while (combined != 0) {
                int offset = Long.numberOfTrailingZeros(combined);
                long prime = (((long) w << 6) + offset) * 2 + 1;
                if (prime + n <= limit) return prime;
                combined &= combined - 1; 
            }

            mask = -1L; 
        }

        return -1;
    }

    public long findClosestPrimePairBitwise(long start, long n, int m) {
        if (n < 2 || (n & 1) != 0 || m < 0) return -1;
        if (sieve == null) return -1;

        int fromIndex = Math.max(3, (int) (start | 1)) >> 1;
        int prevBit = sieve.nextSetBit(fromIndex);

        while (prevBit >= 0) {
            int nextBit = sieve.nextSetBit(prevBit + 1);
            if (nextBit < 0) break;

            long p1 = ((long) prevBit << 1) + 1;
            long p2 = ((long) nextBit << 1) + 1;
            if (p2 > limit) break;

            if (p1 >= start && p2 - p1 == n) {
                if (m == 0) return p1;  
                m--;
            }

            prevBit = nextBit;
        }

        return -1;
    }
}
