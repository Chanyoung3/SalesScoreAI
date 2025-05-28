package com.example.backend.repository;

import com.example.backend.model.Counselor;
import org.springframework.jdbc.core.JdbcTemplate; // 스프링의 JDBC 템플릿 사용
import org.springframework.jdbc.core.RowMapper; // ResultSet의 각 행을 객체로 매핑하기 위한 인터페이스
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired; // 의존성 자동 주입을 위한 어노테이션
import org.springframework.jdbc.support.GeneratedKeyHolder; // 자동 생성된 키 값을 가져오기 위한 클래스
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CounselorRepository {

    private final JdbcTemplate jdbcTemplate; // JDBC 템플릿 객체 (데이터베이스 연동에 사용)

    @Autowired // 스프링이 자동으로 JdbcTemplate 빈을 주입해줍니다.
    public CounselorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Counselor> rowMapper = (rs, rowNum) -> {
        Counselor counselor = new Counselor();
        counselor.setId(rs.getLong("id"));             // 'id' 컬럼 값을 가져와 Counselor 객체의 id 필드에 설정
        counselor.setUsername(rs.getString("username")); // 'username' 컬럼 값을 가져와 설정
        counselor.setPassword(rs.getString("password")); // 'password' 컬럼 값을 가져와 설정
        counselor.setEmail(rs.getString("email"));     // 'email' 컬럼 값을 가져와 설정
        counselor.setName(rs.getString("name"));       // 'name' 컬럼 값을 가져와 설정
        counselor.setRole(rs.getString("role"));       // 'role' 컬럼 값을 가져와 설정
        return counselor; // 매핑된 Counselor 객체 반환
    };

    // 사용자 이름으로 상담사를 찾아 반환합니다.
    public Optional<Counselor> findByUsername(String username) {
        String sql = "SELECT id, username, password, email, name, role FROM counselors WHERE username = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, username));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 새로운 상담사를 저장하거나 기존 상담사 정보를 업데이트합니다.
    public Counselor save(Counselor counselor) {
        if (counselor.getId() == null) { // ID가 없으면 새로운 상담사 등록 (INSERT)
            String sql = "INSERT INTO counselors (username, password, email, name, role) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder(); // 자동 생성된 키(ID)를 저장할 객체

            // PreparedStatementCreator를 사용하여 자동 생성된 키를 반환받습니다.
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // 자동 생성된 키를 반환하도록 설정
                ps.setString(1, counselor.getUsername());
                ps.setString(2, counselor.getPassword());
                ps.setString(3, counselor.getEmail());
                ps.setString(4, counselor.getName());
                ps.setString(5, counselor.getRole());
                return ps;
            }, keyHolder);

            // 생성된 키(ID)를 Counselor 객체에 설정합니다.
            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                counselor.setId(generatedId.longValue());
            }
            return counselor;
        } else { // ID가 있으면 기존 상담사 정보 업데이트 (UPDATE)
            String sql = "UPDATE counselors SET username = ?, password = ?, email = ?, name = ?, role = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    counselor.getUsername(),
                    counselor.getPassword(),
                    counselor.getEmail(),
                    counselor.getName(),
                    counselor.getRole(),
                    counselor.getId());
            return counselor;
        }
    }

    // ID로 상담사를 찾아 반환합니다.
    public Optional<Counselor> findById(Long id) {
        String sql = "SELECT id, username, password, email, name, role FROM counselors WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 모든 상담사를 찾아 리스트로 반환합니다.
    public List<Counselor> findAll() {
        String sql = "SELECT id, username, password, email, name, role FROM counselors";
        return jdbcTemplate.query(sql, rowMapper); // query: 여러 개의 객체를 조회할 때 사용합니다.
    }
}