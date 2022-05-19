package wooteco.subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.application.exception.NotFoundStationException;
import wooteco.subway.domain.exception.UnreachablePathException;
import wooteco.subway.domain.FareCalculator;
import wooteco.subway.domain.Graph;
import wooteco.subway.domain.PathSearcher;
import wooteco.subway.domain.PathSummary;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.PathResponse;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public PathService(StationRepository stationRepository,
                       SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public PathResponse searchPath(Long source, Long target) {
        Station sourceStation = stationRepository.findById(source)
            .orElseThrow(() -> new NotFoundStationException(source));
        Station targetStation = stationRepository.findById(target)
            .orElseThrow(() -> new NotFoundStationException(target));

        PathSearcher pathSearcher = new PathSearcher(createGraph(), new FareCalculator());
        PathSummary pathSummary = pathSearcher.search(sourceStation, targetStation);
        return new PathResponse(pathSummary);
    }

    private Graph createGraph() {
        List<Station> stations = stationRepository.findAll();
        List<Section> sections = sectionRepository.findAll();
        return new JGraphtAdapter(stations, sections);
    }
}
