package wooteco.subway.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PathServiceTest {

    /**
     *     - 10km 까지는 1250원이다.
     *
     *     - 10~50km 까지는 5km마다 100원씩 증액한다.
     *
     *     - 50km 이상부터는 8km마다 100원씩 증액한다.
     */
    private final PathService pathService = new PathService();

    @DisplayName("자연수 이외의 입력은 예외를 반환한다")
    @ParameterizedTest(name = "허용되지 않는 거리 : {0}")
    @ValueSource(ints = {0, -1})
    void throwExceptionWhenNotNaturalNumberInput(int distance) {
        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> pathService.calculateFare(distance)
        ).isInstanceOf(Exception.class);
    }

    @DisplayName("10km 까지는 기본적으로 1250원이다.")
    @ParameterizedTest(name = "거리가 {0} km 일 때는 1250원이다")
    @ValueSource(ints = {8, 10})
    void calculateFareUntil_10km(int distance) {
        assertThat(pathService.calculateFare(distance)).isEqualTo(1_250);

    }

    @DisplayName("10~50km 까지는 5km 마다 100원씩 증액한다")
    @ParameterizedTest(name = "거리가 {0} km 일 때는 {1} 원이다")
    @CsvSource(value = {"11 - 1350", "14 - 1350", "50 - 2050"}, delimiterString = " - ")
    void calculateFareBetween_10km_and_50km(int distance, int fare) {
        assertThat(pathService.calculateFare(distance)).isEqualTo(fare);
    }

    @DisplayName("50km 초과시 8km 마다 100원씩 증액한다")
    @ParameterizedTest(name = "거리가 {0} km 일 때는 {1} 원이다")
    @CsvSource(value = {"51 - 2150", "58 - 2150", "59 - 2250"}, delimiterString = " - ")
    void calculateFareOver_50km(int distance, int fare) {
        assertThat(pathService.calculateFare(distance)).isEqualTo(fare);
    }
}
