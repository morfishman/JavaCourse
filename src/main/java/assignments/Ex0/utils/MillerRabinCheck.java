package assignments.Ex0.utils;


public class MillerRabinCheck {
   
    private static final long[] SMALL_PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
    private static final long[] BASES = {2, 325, 9375, 28178, 450775, 9780504, 1795265022};


    public static boolean isProbablePrime(long n) {
        if (n < 2) return false;
        if ((n & 1) == 0) return n == 2;

        for (long p : SMALL_PRIMES) {
            if (n % p == 0) return n == p;
        }
        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }
        
        
        for (long a : BASES) {
            if (a % n == 0) continue;
            long x = modPow(a, d, n);
            if (x == 1 || x == n - 1) continue;

            boolean cont = false;
            for (int r = 1; r < s; r++) {
                x = modMul(x, x, n);
                if (x == n - 1) {
                    cont = true;
                    break;
                }
            }
            if (!cont) return false;
        }
        return true;
    }

    private static long modPow(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1)
                result = modMul(result, base, mod);
            base = modMul(base, base, mod);
            exp >>= 1;    
        }
        return result;
    }

  
    private static long modMul(long a, long b, long mod) {
        long result = 0;
        a %= mod;
        b %= mod;

        while (b > 0) {
            if ((b & 1) == 1)
                result = (result + a) % mod;
            a = (a << 1) % mod;
            b >>= 1;
        }
        return result;
    }


    public static void main(String[] args) {
        long[] test = {2, 3, 4, 5, 17, 18, 19, 20, 37, 561, 1000000007L, 9999999967L};
        for (long n : test) {
            System.out.printf("%,d → %s%n", n, isProbablePrime(n));
        }
    }
}
