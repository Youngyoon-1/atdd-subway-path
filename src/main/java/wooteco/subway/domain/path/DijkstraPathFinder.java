package wooteco.subway.domain.path;

import java.util.List;
import java.util.stream.Collectors;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

@Component
public class DijkstraPathFinder implements PathFinder {

    private DijkstraShortestPath<Long, DefaultWeightedEdge> graph;

    @Override
    public void set(List<Long> vertexes, List<GraphEdgeRequest> edges) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        setVertex(vertexes, graph);
        setEdgeWeight(edges, graph);
        this.graph = new DijkstraShortestPath(graph);
    }

    private void setVertex(List<Long> vertexes, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        for (Long vertex : vertexes) {
            graph.addVertex(vertex);
        }
    }

    private void setEdgeWeight(List<GraphEdgeRequest> edges, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        for (GraphEdgeRequest edge : edges) {
            var sourceVertex = edge.getSourceVertex();
            var targetVertex = edge.getTargetVertex();
            var weight = edge.getWeight();

            graph.setEdgeWeight(graph.addEdge(sourceVertex, targetVertex), weight);
        }
    }

    @Override
    public GraphPathResponse find(Long source, Long target) {
        var shortestPath = graph.getPath(source, target);

        return new GraphPathResponse(shortestPath.getVertexList(),
                parseEdgeList(shortestPath.getEdgeList()),
                shortestPath.getWeight()
        );
    }


    private List<Edge> parseEdgeList(List<DefaultWeightedEdge> edgeList) {
        return edgeList.stream()
                .map(it -> StationIdParser.parse(it.toString()))
                .map(Edge::new)
                .collect(Collectors.toList());
    }
}
