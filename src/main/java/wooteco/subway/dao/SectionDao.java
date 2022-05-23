package wooteco.subway.dao;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.section.SectionRequest;

@Component
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapper = (rs, rowNum) -> {
        var id = rs.getLong("id");
        var upStationId = rs.getLong("up_station_id");
        var downStationId = rs.getLong("down_station_id");
        var distance = rs.getInt("distance");
        var lineId = rs.getLong("line_id");
        return new Section(id, upStationId, downStationId, distance, lineId);
    };

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(long lineId, SectionRequest sectionRequest) {
        var sql = "INSERT INTO section (up_station_id, down_station_id, distance, line_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(
                sql,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance(),
                lineId
        );
    }

    public void delete(Section section) {
        var sql = "DELETE FROM section WHERE id = ?";
        var removedRowCount = jdbcTemplate.update(sql, section.getId());

        checkValidation(removedRowCount);
    }

    private void checkValidation(int rowCount) {
        if (rowCount == 0) {
            throw new NoSuchElementException("[ERROR] 정보와 일치하는 구간이 없습니다.");
        }
    }

    public List<Section> findByLineId(Long id) {
        var sql = "SELECT * FROM section WHERE line_id = ?";

        return jdbcTemplate.query(sql, sectionRowMapper, id);
    }

    public void update(Section section) {
        var sql = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        var updatedRow = jdbcTemplate.update(sql,
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance(),
                section.getId()
        );

        checkValidation(updatedRow);
    }

    public List<Section> findAll() {
        var sql = "SELECT * FROM section";

        return jdbcTemplate.query(sql, sectionRowMapper);
    }

    public List<Section> findByStationId(Long id) {
        var sql = "SELECT * FROM section WHERE up_station_id = ? OR down_station_id = ?";

        return jdbcTemplate.query(sql, sectionRowMapper, id, id);
    }

    public List<Section> findByStationId(List<Long> ids) {
        var upStationId = ids.get(0);
        var downStationId = ids.get(1);

        var sql = "SELECT * FROM section WHERE up_station_id = ? AND down_station_id = ?";

        return jdbcTemplate.query(sql, sectionRowMapper, upStationId, downStationId);
    }
}
