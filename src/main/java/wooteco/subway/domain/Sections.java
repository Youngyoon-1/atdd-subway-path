package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.dao.EmptyResultDataAccessException;

public class Sections {

    private static final int UP_STATION_SIZE = 1;
    private static final int UP_STATION_INDEX = 0;
    private static final int MINIMUM_SECTION_SIZE = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public List<Long> getSortedStationIdsInSingleLine() {
        List<Long> stationIds = new ArrayList<>();

        Long upStationId = getUpStationId(sections);
        stationIds.add(upStationId);

        while (stationIds.size() != sections.size() + UP_STATION_SIZE) {
            Long downStationId = getDownStationId(sections, upStationId);
            stationIds.add(downStationId);
            upStationId = downStationId;
        }

        return stationIds;
    }

    private Long getUpStationId(List<Section> sections) {
        List<Long> upStationIds = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());

        List<Long> downStationIds = sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());

        upStationIds.removeAll(downStationIds);
        return upStationIds.get(UP_STATION_INDEX);
    }

    private Long getDownStationId(List<Section> sections, Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .getDownStationId();
    }

    public Sections getSectionsToDelete(Long stationId) {
        validateSectionSize(sections);
        List<Section> sectionsToDelete = sections.stream()
                .filter(section -> section.contains(stationId))
                .collect(Collectors.toList());
        validateDeletion(sectionsToDelete);
        return new Sections(sectionsToDelete);
    }

    private void validateSectionSize(List<Section> sections) {
        if (sections.size() == MINIMUM_SECTION_SIZE) {
            throw new IllegalArgumentException("구간이 하나뿐이라 삭제할 수 없습니다.");
        }
    }

    private void validateDeletion(List<Section> sectionsToDelete) {
        if (sectionsToDelete.isEmpty()) {
            throw new EmptyResultDataAccessException(0);
        }
    }

    public List<Long> getSectionIds() {
        return sections.stream()
                .map(Section::getId)
                .collect(Collectors.toList());
    }

    public Section merge() {
        return sections.get(0).merge(sections.get(1));
    }

    public int size() {
        return sections.size();
    }

    public List<Long> getShortestPathStationIds(Long departureId, Long arrivalId) {
        List<String> stationIds = findShortestPath(departureId, arrivalId).getVertexList();
        return stationIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    private GraphPath findShortestPath(Long departureId, Long arrivalId) {
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        addStationVertex(graph);
        addSectionEdge(graph);
        GraphPath path = getGraphPath(departureId, arrivalId, graph);
        validateConnection(path);
        return path;
    }

    private void validateConnection(GraphPath path) {
        if (path == null) {
            throw new IllegalArgumentException("연결되지 않은 구간입니다.");
        }
    }

    private GraphPath getGraphPath(Long departureId, Long arrivalId,
                                   WeightedMultigraph<String, DefaultWeightedEdge> graph) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        try {
            return dijkstraShortestPath.getPath(String.valueOf(departureId), String.valueOf(arrivalId));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("구간에 등록 되지 않은 역입니다.");
        }
    }

    private void addStationVertex(WeightedMultigraph<String, DefaultWeightedEdge> graph) {
        for (Long stationId : getAllStationIds()) {
            graph.addVertex(String.valueOf(stationId));
        }
    }

    private List<Long> getAllStationIds() {
        Set<Long> stationIds = new HashSet<>();

        for (Section section : sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }

        return new ArrayList<>(stationIds);
    }

    private void addSectionEdge(WeightedMultigraph<String, DefaultWeightedEdge> graph) {
        for (Section section : sections) {
            String upStationId = String.valueOf(section.getUpStationId());
            String downStationId = String.valueOf(section.getDownStationId());
            int distance = section.getDistance();
            graph.setEdgeWeight(graph.addEdge(upStationId, downStationId), distance);
        }
    }

    public int getShortestPathDistance(Long departureId, Long arrivalId) {
        return (int) findShortestPath(departureId, arrivalId).getWeight();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
