package assignments.Ex0.context;

import assignments.Ex0.interfaces.PrimeCheckStrategy;

public class PrimeChecker {

    private PrimeCheckStrategy strategy;

    public PrimeChecker(PrimeCheckStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(PrimeCheckStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean isPrime(long n) {
        if (strategy == null)
            throw new IllegalStateException("Prime checking strategy not set.");
        return strategy.isPrime(n);
    }
}
