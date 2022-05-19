package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @DisplayName("구간 목록에 포함된 모든 역 id를 반환한다.")
    @Test
    void getStationIds() {
        Section section1 = new Section(1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 2L, 3L, 10);
        Section section3 = new Section(3L, 3L, 4L, 10);

        Sections sections = new Sections(List.of(section1, section2, section3));

        assertThat(sections.getSortedStationIdsInSingleLine()).containsExactly(1L, 2L, 3L, 4L);
    }

    @DisplayName("삭제 가능한 구간을 반환한다.")
    @Test
    void getSectionsToDelete() {
        Section section1 = new Section(1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 2L, 3L, 10);
        Section section3 = new Section(3L, 3L, 4L, 10);

        Sections sections = new Sections(List.of(section1, section2, section3));

        assertAll(
                () -> assertThat(sections.getSectionsToDelete(1L).size()).isEqualTo(1),
                () -> assertThat(sections.getSectionsToDelete(2L).size()).isEqualTo(2)
        );
    }

    @Test
    void getSectionIds() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 2L, 3L, 10);
        Section section3 = new Section(3L, 1L, 3L, 4L, 10);

        Sections sections = new Sections(List.of(section1, section2, section3));

        List<Long> sectionIds = sections.getSectionIds();

        assertThat(sectionIds).containsExactly(1L, 2L, 3L);
    }

    @Test
    void merge() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 2L, 3L, 10);

        Sections sections = new Sections(List.of(section1, section2));

        Section mergedSection = sections.merge();

        assertThat(mergedSection.getDistance()).isEqualTo(20);
    }

    @DisplayName("출발역id와 도착역id를 받아, 최단 경로에 해당하는 지하철역 id들을 반환한다.")
    @Test
    void getShortestPathStationIds() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 2);
        Section section2 = new Section(2L, 1L, 2L, 3L, 4);
        Section section3 = new Section(3L, 1L, 3L, 4L, 6);
        Section section4 = new Section(4L, 2L, 3L, 5L, 8);

        Sections sections = new Sections(List.of(section1, section2, section3, section4));

        List<Long> expected = List.of(1L, 2L, 3L, 5L);

        assertThat(sections.getShortestPathStationIds(1L, 5L)).isEqualTo(expected);
    }

    @DisplayName("연결되지 않은 구간의 최단 경로를 조회할 경우 예외가 발생한다.")
    @Test
    void getShortestPathStationIds_NotConnected() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 2);
        Section section2 = new Section(2L, 1L, 2L, 3L, 4);
        Section section3 = new Section(3L, 1L, 3L, 4L, 6);
        Section section4 = new Section(4L, 2L, 5L, 6L, 8);

        Sections sections = new Sections(List.of(section1, section2, section3, section4));

        assertThatThrownBy(() -> sections.getShortestPathStationIds(1L, 5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("연결되지 않은 구간입니다.");
    }

    @DisplayName("출발역id와 도착역id를 받아, 최단 거리를 반환한다.")
    @Test
    void getShortestPathDistance() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 2);
        Section section2 = new Section(2L, 1L, 2L, 3L, 4);
        Section section3 = new Section(3L, 1L, 3L, 4L, 6);
        Section section4 = new Section(4L, 2L, 3L, 5L, 8);

        Sections sections = new Sections(List.of(section1, section2, section3, section4));

        assertThat(sections.getShortestPathDistance(1L, 5L)).isEqualTo(14);
    }

    @DisplayName("연결되지 않은 구간의 최단 경로의 거리를 조회할 경우 예외가 발생한다.")
    @Test
    void getShortestPathDistance_NotConnected() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 2);
        Section section2 = new Section(2L, 1L, 2L, 3L, 4);
        Section section3 = new Section(3L, 1L, 3L, 4L, 6);
        Section section4 = new Section(4L, 2L, 5L, 6L, 8);

        Sections sections = new Sections(List.of(section1, section2, section3, section4));

        assertThatThrownBy(() -> sections.getShortestPathDistance(1L, 5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("연결되지 않은 구간입니다.");
    }
}
