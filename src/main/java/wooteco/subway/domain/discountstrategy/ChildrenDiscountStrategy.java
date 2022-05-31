package wooteco.subway.domain.discountstrategy;

public class ChildrenDiscountStrategy implements AgeDiscountStrategy {
    @Override
    public boolean available(int age) {
        return age >= 6 && age < 13;
    }

    @Override
    public double calculateFare(int fare) {
        return (fare - 350) * 0.5;
    }
}
