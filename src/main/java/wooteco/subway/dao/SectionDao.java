package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(List<Section> sections, Long lineId) {
        batchInsert(sections, lineId);
    }

    public List<Section> findAll() {
        final String sql =
                "select s.id sid, s.distance sdistance, us.id usid, us.name usname, ds.id dsid, ds.name dsname " +
                        "from sections s " +
                        "join station us on s.up_station_id = us.id " +
                        "join station ds on s.down_station_id = ds.id";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> {
            return Section.createWithId(rs.getLong("sid"), new Station(rs.getLong("usid"), rs.getString("usname")),
                    new Station(rs.getLong("dsid"), rs.getString("dsname")), rs.getInt("sdistance"));
        }));
    }

    private int[] batchInsert(List<Section> sections, Long lineId) {
        return this.jdbcTemplate.batchUpdate(
                "insert into sections (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, lineId);
                        ps.setLong(2, sections.get(i).getUpStation().getId());
                        ps.setLong(3, sections.get(i).getDownStation().getId());
                        ps.setInt(4, sections.get(i).getDistance().getValue());
                    }

                    @Override
                    public int getBatchSize() {
                        return sections.size();
                    }
                }
        );
    }

    public void deleteByLineId(Long lineId) {
        final String sql = "delete from sections where line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
