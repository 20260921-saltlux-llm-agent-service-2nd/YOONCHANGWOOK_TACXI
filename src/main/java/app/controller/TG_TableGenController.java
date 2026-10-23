package app.controller;
import app.common.TG_SqlStore;
import app.model.DTO.TG_TableDefDto;
import app.repository.TG_TabelGenRepository;
import app.common.JDBCTemPlate;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jdk.xml.internal.Utils.isEmpty;

// 테이블 조회,
// GEN_MNTR_SQNO     MEMBER_SQNO        TABLE_NAME                  COL_DATA_TYPE       COLUMN_NM       COLUM(크기)
// 1,                1,                 TB_SUBWAY_CONGESTION,       VARCHAR,            구분,            6,           N,Y,N,,,,"평일, 토요일, 일요일",2026-10-22 11:56:45
//Application (메인 메뉴 루프)
//   ↓ "테이블 생성" 선택
//TableGenView        회원 일련번호 입력 → controller 호출 → 결과 출력 → Y/N 입력
//   ↓
//TG_TableGenController
//   ① Connection 획득           (JDBCTemplate)
//   ② 정의서 조회               (Repository → PreparedStatement + ResultSet)
//   ③ 테이블명별 그룹핑          (Map)
//   ④ CREATE TABLE 문자열 조립   (XML 템플릿 + String.format)
//   ⑤ DDL 실행                  (Repository → Statement.execute)
//   ↓
//TableGenView        Y → controller.loadData() / N → 최초 화면

public class TG_TableGenController {
    //
    private final TG_TabelGenRepository tgRepo = new TG_TabelGenRepository();

    // ===================== 2. 공개 메서드 (View에서 호출) =====================
    /**
     * 정의서 기준으로 테이블 생성. 생성된 테이블명 목록 반환, 실패 시 예외 -> 콘솔 관련 기능들
     **/
    public List<String> createTables(int memberSqno) {
        List<String> created = new ArrayList<>();

        try (Connection conn = JDBCTemPlate.getConnection()) {
            List<TG_TableDefDto> defs = tgRepo.findDefinitions(conn, memberSqno);   // ID에
            Map<String, List<TG_TableDefDto>> byTable = groupByTable(defs);       // 그룹핑

            for (Map.Entry<String, List<TG_TableDefDto>> e : byTable.entrySet()) {
                String ddl = buildCreateTable(e.getKey(), e.getValue());       // 조립
                System.out.println(ddl);                                       // 학습용: 만들어진 SQL 확인
                tgRepo.execute(conn, ddl);                                       // 실행
                created.add(e.getKey());
            }
        } catch (SQLException ex) {
            throw new RuntimeException("테이블 생성 실패 (생성 완료: " + created + ")", ex);
        }
        return created;
    }

    // ===================== 3. 그룹핑 =====================
    private Map<String, List<TG_TableDefDto>> groupByTable(List<TG_TableDefDto> defs) {
        Map<String, List<TG_TableDefDto>> map = new LinkedHashMap<>();    // 입력 순서 유지
        for (TG_TableDefDto d : defs) {
            map.computeIfAbsent(d.getTableName(), k -> new ArrayList<>()).add(d);
        }
        return map;
    }

    // ===================== 4. DDL 조립 =====================
    private String buildCreateTable(String tableName, List<TG_TableDefDto> cols) {
        List<String> defs = new ArrayList<>();
        List<String> pks  = new ArrayList<>();

        for (TG_TableDefDto c : cols) {
            // 자료형(자료형크기) 조각
            String type  = c.getColDataType() + (isEmpty(c.getColumnSize()) ? "" : "(" + c.getColumnSize() + ")");

            // NULL 여부 조각
            String nullYn = "Y".equals(c.getNullYn()) ? "NULL" : "NOT NULL";

            // DEFAULT 여부 조각
            String dflt  = isEmpty(c.getDefaultStr()) ? "" : TG_SqlStore.get("defaultClause", c.getDefaultStr());

            // COMMENT 여부 조각
            String cmt = isEmpty(c.getComment())    ? "" : TG_SqlStore.get("commentClause", c.getComment());

            // 조각문들 조립
            defs.add(TG_SqlStore.get("columnDef", c.getColumnNm(), type, nullYn, dflt, cmt));
            if ("Y".equals(c.getPkYn())) pks.add("`" + c.getColumnNm() + "`");
        }

        if (!pks.isEmpty()) defs.add(TG_SqlStore.get("primaryKey", String.join(", ", pks)));

        return TG_SqlStore.get("createTable", tableName, String.join(",\n  ", defs));
    }


    // 조각1. 테이블 이름 -> 테이블

    // 반복문으로 테이블 조각 조립

    //NULL_YN
    //PK_YN
    //FK_YN
    //REF_TABLE_NM
    //REF_COLNM
    //DEFAULT_STR
    //COMMENT
    //LSCHG_DTM
}
