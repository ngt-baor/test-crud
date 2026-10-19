package com.example.testcrud.repository;

import com.example.testcrud.dto.HocSinhRequest;
import com.example.testcrud.dto.HocSinhResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public class HocSinhRepository {
    private static final String SELECT_WITH_CLASS = """
            SELECT hs.MaHS, hs.HoTen, hs.NgaySinh, hs.GioiTinh, hs.DiaChi, hs.MaLop, lh.TenLop
            FROM dbo.HocSinh hs
            JOIN dbo.LopHoc lh ON lh.MaLop = hs.MaLop
            """;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/uuuu", Locale.US);

    private final NamedParameterJdbcTemplate jdbc;

    public HocSinhRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<HocSinhResponse> findAll() {
        return jdbc.query(SELECT_WITH_CLASS + " ORDER BY hs.MaHS", new MapSqlParameterSource(), this::mapRow);
    }

    public Optional<HocSinhResponse> findById(int maHS) {
        List<HocSinhResponse> result = jdbc.query(
                SELECT_WITH_CLASS + " WHERE hs.MaHS = :maHS",
                new MapSqlParameterSource("maHS", maHS),
                this::mapRow
        );
        return result.stream().findFirst();
    }

    public boolean classExists(int maLop) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM dbo.LopHoc WHERE MaLop = :maLop",
                new MapSqlParameterSource("maLop", maLop), Integer.class
        );
        return count != null && count > 0;
    }

    public HocSinhResponse insert(HocSinhRequest request, LocalDate ngaySinh) {
        String sql = """
                INSERT INTO dbo.HocSinh (HoTen, NgaySinh, GioiTinh, DiaChi, MaLop)
                VALUES (:hoTen, :ngaySinh, :gioiTinh, :diaChi, :maLop)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, parameters(request, ngaySinh), keyHolder, new String[]{"MaHS"});
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("SQL Server không trả về MaHS mới");
        }
        return findById(key.intValue()).orElseThrow();
    }

    public Optional<HocSinhResponse> update(int maHS, HocSinhRequest request, LocalDate ngaySinh) {
        String sql = """
                UPDATE dbo.HocSinh
                SET HoTen = :hoTen, NgaySinh = :ngaySinh, GioiTinh = :gioiTinh,
                    DiaChi = :diaChi, MaLop = :maLop
                WHERE MaHS = :maHS
                """;
        MapSqlParameterSource params = parameters(request, ngaySinh).addValue("maHS", maHS);
        if (jdbc.update(sql, params) == 0) {
            return Optional.empty();
        }
        return findById(maHS);
    }

    public boolean delete(int maHS) {
        return jdbc.update("DELETE FROM dbo.HocSinh WHERE MaHS = :maHS",
                new MapSqlParameterSource("maHS", maHS)) > 0;
    }

    private MapSqlParameterSource parameters(HocSinhRequest request, LocalDate ngaySinh) {
        return new MapSqlParameterSource()
                .addValue("hoTen", request.hoTen())
                .addValue("ngaySinh", ngaySinh == null ? null : Date.valueOf(ngaySinh), Types.DATE)
                .addValue("gioiTinh", request.gioiTinh())
                .addValue("diaChi", request.diaChi())
                .addValue("maLop", request.maLop());
    }

    private HocSinhResponse mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Date date = rs.getDate("NgaySinh");
        return new HocSinhResponse(
                rs.getInt("MaHS"),
                rs.getString("HoTen"),
                date == null ? null : DATE_FORMAT.format(date.toLocalDate()),
                rs.getString("GioiTinh"),
                rs.getString("DiaChi"),
                rs.getInt("MaLop"),
                rs.getString("TenLop")
        );
    }
}