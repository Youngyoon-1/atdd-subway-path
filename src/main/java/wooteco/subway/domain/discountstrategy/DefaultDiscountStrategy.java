package wooteco.subway.domain.discountstrategy;

public class DefaultDiscountStrategy implements AgeDiscountStrategy {
    @Override
    public boolean available(int age) {
        return true;
    }

    @Override
    public double calculateFare(int fare) {
        return fare;
    }
}
