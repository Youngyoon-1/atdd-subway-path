package wooteco.subway.domain.discountstrategy;

public interface AgeDiscountStrategy {

    boolean available(int age);

    double calculateFare(int fare);
}
