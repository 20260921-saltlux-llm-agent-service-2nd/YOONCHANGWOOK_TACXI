package app.repository;

import app.common.TG_SqlStore;
import app.model.DTO.TG_TableDefDto;
import java.io.*;
import java.sql.*;
import java.util.*;

public class TG_TabelGenRepository {
    private static final String QUERY_FILE = "src/main/java/resources/db/mapper/TG_TableCreate.xml";
    private final Properties prop = new Properties();

    public TG_TabelGenRepository() {
        try (FileInputStream inputStream = new FileInputStream(QUERY_FILE)) {
            prop.loadFromXML(inputStream);
        } catch(IOException e) {
            throw new IllegalStateException("테이블 생성 SQL 파일을 읽지 못했습니다.", e);
        }
    }

    // PreparedStatemment : SQL의 구조를 먼저 DB에 보내고 파싱해놓고 나중에 ? 값을 채우는 건데, 그래서 값(리터럴)만 가능함
    String sql = prop.getProperty("createTable");

    public List<TG_TableDefDto> findDefinitions(Connection conn, int memberSqno) throws SQLException {
        List<TG_TableDefDto> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(TG_SqlStore.get("selectDefinitions"))) {
            ps.setInt(1, memberSqno);
            try (ResultSet rs = ps.executeQuery()) {           // SELECT 실행
                while (rs.next()) {                            // 한 행씩 이동
                    TG_TableDefDto d = new TG_TableDefDto();
                    d.setTableName(rs.getString("TABLE_NAME"));
                    d.setColDataType(rs.getString("COL_DATA_TYPE"));
                    d.setColumnNm(rs.getString("COLUMN_NM"));
                    d.setColumnSize(rs.getString("COLUMN_SIZE"));
                    d.setNullYn(rs.getString("NULL_YN"));
                    d.setPkYn(rs.getString("PK_YN"));
                    d.setDefaultStr(rs.getString("DEFAULT_STR"));
                    d.setComment(rs.getString("COL_COMMENT"));
                    list.add(d);
                }
            }
        }
        return list;
    }
}
