package wooteco.subway.domain.discountstrategy;

public class TeenagerDiscountStrategy implements AgeDiscountStrategy {
    @Override
    public boolean available(int age) {
        return age >= 13 && age < 19;
    }

    @Override
    public double calculateFare(int fare) {
        return (fare - 350) * 0.2;
    }
}
