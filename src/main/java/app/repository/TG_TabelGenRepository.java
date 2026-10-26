package app.repository;

import app.model.DTO.TG_TableDefDto;
import app.model.DTO.UserDto;

import java.io.*;
import java.sql.*;
import java.util.*;

import static app.common.JDBCTemPlate.getConnection;

public class TG_TabelGenRepository {

    public TG_TabelGenRepository() {
        try (InputStream in = new FileInputStream("src/main/java/resources/db/mapper/TG_TableCreate.xml")) {
            P.loadFromXML(in);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private  final Properties P = new Properties();

    // [사용자 정의 테이블 조회]
    public List<TG_TableDefDto> findDefinitions(String userid,String table_nm) throws SQLException {
        List<TG_TableDefDto> list = new ArrayList<>();
        Connection conn = getConnection();

        String defineQry = P.getProperty("tg_selectDefinitions");

        try (PreparedStatement ps = conn.prepareStatement(defineQry) ) {
            ps.setString(1, userid);
            ps.setString(2, table_nm);
//            ps.setString(2, table_name);
            try (ResultSet rs = ps.executeQuery()) {           // SELECT 실행
                while (rs.next()) {                            // 한 행씩 이동
                    TG_TableDefDto d = new TG_TableDefDto();
                    d.setGenMntrsqno(rs.getInt("GEN_MNTR_SQNO"));
                    d.setMemberSqno(rs.getInt("MEMBER_SQNO"));
                    d.setTableName(rs.getString("TABLE_NAME"));
                    d.setColDataType(rs.getString("COL_DATA_TYPE"));
                    d.setColumnNm(rs.getString("COLUMN_NM"));
                    d.setColumSize(Objects.toString(rs.getString("COLUMN_SIZE"), ""));
                    d.setNullYn(Objects.toString(rs.getString("NULL_YN"), ""));
                    d.setPkYn(Objects.toString(rs.getString("PK_YN"), ""));
                    d.setFkYn(Objects.toString(rs.getString("FK_YN"), ""));
                    d.setRefTableNm(Objects.toString(rs.getString("REF_TABLE_NM"), ""));
                    d.setRefColumn(Objects.toString(rs.getString("REF_COLMN"), ""));
                    d.setDefaultStr(Objects.toString(rs.getString("DEFAULT_STR"), ""));
                    d.setComment(Objects.toString(rs.getString("COMMENT"), ""));
                    d.setLschgDtm(rs.getString("LSCHG_DTM"));
                    list.add(d);
                }
            }
        }
        return list;
    }
    public List<TG_TableDefDto> userCreatedtables(String userid) throws SQLException {

        List<TG_TableDefDto> list = new ArrayList<>();
        Connection conn = getConnection();

        String defineQry = P.getProperty("tg_seletUserdefinedTables");
        try (PreparedStatement ps = conn.prepareStatement(defineQry)) {
            ps.setString(1, userid);
            try (ResultSet rs = ps.executeQuery()) {           // SELECT 실행
                while (rs.next()) {                            // 한 행씩 이동
                    TG_TableDefDto d = new TG_TableDefDto();
                    d.setGenMntrsqno(rs.getInt("gen_mntr_sqno"));
                    d.setTableName(rs.getString("TABLE_NAME"));
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
