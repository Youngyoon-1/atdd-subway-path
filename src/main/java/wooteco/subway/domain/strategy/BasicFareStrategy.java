package wooteco.subway.domain.strategy;

public class BasicFareStrategy implements FareStrategy {

    private static final int BASE_DISTANCE = 10;
    private static final int MIN_EXTRA_FARE = 0;
    private static final int FIRST_ROLE_STANDARD_DISTANCE = 40;
    private static final double FIRST_ROLE_EXTRA_FARE_STANDARD = 5;
    private static final double SECOND_ROLE_EXTRA_FARE_STANDARD = 8;
    private static final int FARE_UNIT = 100;

    @Override
    public int calculate(int distance) {
        int overDistance = distance - BASE_DISTANCE;
        if (overDistance >= MIN_EXTRA_FARE) {
            return calculateOverFare(overDistance);
        }
        return MIN_EXTRA_FARE;
    }

    private int calculateOverFare(int distance) {
        if (distance > FIRST_ROLE_STANDARD_DISTANCE) {
            return (int) (Math.ceil((FIRST_ROLE_STANDARD_DISTANCE) / FIRST_ROLE_EXTRA_FARE_STANDARD)) * FARE_UNIT
                    + (int) (Math.ceil((distance - FIRST_ROLE_STANDARD_DISTANCE) / SECOND_ROLE_EXTRA_FARE_STANDARD))
                    * FARE_UNIT;
        }
        return (int) (Math.ceil((distance) / FIRST_ROLE_EXTRA_FARE_STANDARD)) * FARE_UNIT;
    }
}
