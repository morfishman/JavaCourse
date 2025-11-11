package assignments.Ex0.strategies;

import assignments.Ex0.interfaces.PrimeCheckStrategy;
import assignments.Ex0.utils.MillerRabinCheck;

public class MillerRabinStrategy implements PrimeCheckStrategy {

    @Override
    public boolean isPrime(long n) {
        return MillerRabinCheck.isProbablePrime(n);
    }
}
