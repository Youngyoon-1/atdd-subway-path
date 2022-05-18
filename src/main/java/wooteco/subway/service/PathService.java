package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Path;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.PathResponse;

@Service
public class PathService {

    private final StationService stationService;
    private final SectionDao sectionDao;

    public PathService(StationService stationService, SectionDao sectionDao) {
        this.stationService = stationService;
        this.sectionDao = sectionDao;
    }

    public PathResponse getPath(Long sourceStationId, Long targetStationId) {
        List<Section> sections = sectionDao.findAll();

        Station departure = stationService.findStationById(sourceStationId);
        Station arrival = stationService.findStationById(targetStationId);

        return new PathResponse(new Path(sections, departure, arrival));
    }
}
