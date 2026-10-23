package app.repository;

import app.common.TG_SqlStore;
import app.model.DTO.TG_TableDefDto;
import java.io.*;
import java.sql.*;
import java.util.*;

public class TG_TabelGenRepository {

    // [사용저 정의 테이블 조회]
    public List<TG_TableDefDto> findDefinitions(Connection conn, int memberSqno) throws SQLException {
        List<TG_TableDefDto> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(TG_SqlStore.get("tg_selectDefinitions"))) {
            ps.setInt(1, memberSqno);
            try (ResultSet rs = ps.executeQuery()) {           // SELECT 실행
                while (rs.next()) {                            // 한 행씩 이동
                    TG_TableDefDto d = new TG_TableDefDto();
                    d.setTableName(rs.getString("TABLE_NAME"));
                    d.setColDataType(rs.getString("COL_DATA_TYPE"));
                    d.setColumnNm(rs.getString("COLUMN_NM"));
                    d.setColum(Objects.toString(rs.getString("COLUM"), ""));
                    d.setNullYn(Objects.toString(rs.getString("NULL_YN"), ""));
                    d.setPkYn(Objects.toString(rs.getString("PK_YN"), ""));
                    d.setDefaultStr(Objects.toString(rs.getString("DEFAULT_STR"), ""));
                    d.setComment(Objects.toString(rs.getString("COMMENT"), ""));
                    d.setLschgDtm(rs.getString("LSCHG_DTM"));
                    list.add(d);
                }
            }
        }
        return list;
    }

    // [조각문을 조립한 DDL 실행]
    public void execute(Connection conn, String ddl) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute(ddl);
        }
    }
}
