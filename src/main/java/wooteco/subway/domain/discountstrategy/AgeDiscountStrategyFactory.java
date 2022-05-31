package wooteco.subway.domain.discountstrategy;

import java.util.ArrayList;
import java.util.List;

public class AgeDiscountStrategyFactory {
    static final List<AgeDiscountStrategy> strategies = new ArrayList<>();

    static {
        strategies.add(new ChildrenDiscountStrategy());
        strategies.add(new TeenagerDiscountStrategy());
    }

    private AgeDiscountStrategyFactory() {
    }

    public static AgeDiscountStrategy from(int age) {
        return strategies.stream()
                .filter(it -> it.available(age))
                .findFirst()
                .orElse(new DefaultDiscountStrategy());
    }
}
