package wooteco.subway.domain.path;

import java.util.List;

public interface PathFinder {
    GraphPathResponse find(Long source, Long target);
    void set(List<Long> vertexes, List<GraphEdgeRequest> edges);
}
