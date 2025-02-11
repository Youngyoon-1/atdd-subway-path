package wooteco.subway.domain;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections createByStationId(List<Section> sections, Long stationId) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("[ERROR] 구간이 하나인 노선에서 마지막 구간을 제거할 수 없습니다.");
        }

        var parsedSections = sections.stream()
                .filter(it -> it.isSameStationId(stationId))
                .collect(Collectors.toList());

        return new Sections(parsedSections);
    }

    public Optional<Section> createSection(Section section) {
        checkAnyMatch(section);

        var sameUpStationSection = sections.stream().filter(it -> it.isSameUpStationId(section)).findAny();
        var downUpStationSection = sections.stream().filter(it -> it.isSameDownStationId(section)).findAny();

        if (sameUpStationSection.isPresent() && downUpStationSection.isPresent()) {
            throw new IllegalArgumentException("[ERROR] 상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }

        return sameUpStationSection.map(it -> Section.createWhenSameUpStation(it, section))
                .or(() -> downUpStationSection.map(it -> Section.createWhenSameDownStation(it, section)));
    }

    private void checkAnyMatch(Section section) {
        sections.stream()
                .filter(it -> it.isSameStationId(section.getUpStationId()) ||
                        it.isSameStationId(section.getDownStationId()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 상행역과 하행역 둘 중 하나도 포함되어있지 않습니다."));
    }

    public boolean hasOnlyOneSection() {
        return sections.size() == 1;
    }

    public Section createSectionByStationId(Long stationId) {
        var firstSection = sections.get(0);
        var secondSection = sections.get(1);

        var parsedIds = sections.stream()
                .map(it -> List.of(it.getUpStationId(), it.getDownStationId()))
                .flatMap(Collection::stream)
                .filter(it -> !it.equals(stationId))
                .collect(Collectors.toList());

        return new Section(
                firstSection.getId(),
                parsedIds.get(0),
                parsedIds.get(1),
                firstSection.plusDistance(secondSection)
        );
    }

    public Section getSecondSection() {
        return sections.get(1);
    }

    public List<Section> get() {
        return sections;
    }

    public Section findByStationIds(List<Long> ids) {
        return sections.stream()
                .filter(it -> it.getUpStationId().equals(ids.get(0)))
                .filter(it -> it.getDownStationId().equals(ids.get(1)))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 구간에 등록되지 않은 역 아이디입니다."));
    }

    public Section findByStationId(Long stationId) {
        return sections.stream()
                .filter(it -> it.isSameStationId(stationId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 구간에 등록되지 않은 역 아이디입니다."));
    }
}
