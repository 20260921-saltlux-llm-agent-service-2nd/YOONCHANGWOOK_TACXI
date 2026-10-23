package app.controller;
import app.common.TG_SqlStore;
import app.model.DTO.TG_TableDefDto;
import app.repository.TG_TabelGenRepository;
import app.common.JDBCTemPlate;

import java.sql.*;
import java.util.*;


public class TG_TableGenController {
    // ===================== 1. 의존 객체 =====================
    private final TG_TabelGenRepository tgRepo = new TG_TabelGenRepository();

    // ===================== 2. 공개 메서드 (View에서 호출) =====================
    /** 사용자 테이블 정의서 기준으로 DB 생성. 생성된 테이블명 목록 반환, 실패 시 예외 -> 콘솔 관련 기능들 **/
    public List<String> createTables(int memberSqno) {
        List<String> created = new ArrayList<>();

        try (Connection conn = JDBCTemPlate.getConnection()) {
            List<TG_TableDefDto> defs = tgRepo.findDefinitions(conn, memberSqno);   // Member 번호에 해당하는 테이블 읽기
            Map<String, List<TG_TableDefDto>> byTable = groupByTable(defs);         // 그룹핑

            for (Map.Entry<String, List<TG_TableDefDto>> e : byTable.entrySet()) {
                String ddl = buildCreateTable(e.getKey(), e.getValue());            // DDL 조립
                System.out.println(ddl);                                            // 테스트용: 만들어진 SQL 확인
                tgRepo.execute(conn, ddl);                                          // 실행
                created.add(e.getKey());
            }
        } catch (SQLException ex) {
            throw new RuntimeException("테이블 생성 실패 (생성 완료: " + created + ")", ex);
            // 원인 테스트용
//            System.out.println(ex.getMessage());
//            if (ex.getCause() != null) System.out.println("원인: " + ex.getCause().getMessage());
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
            String type  = c.getColDataType() + (c.getColum().isEmpty() ?   "" : "(" + c.getColum() + ")");

            // NULL 여부 조각
            String nullYn = "Y".equalsIgnoreCase(c.getNullYn()) ? "NULL" : "NOT NULL";

            // DEFAULT 여부 조각
            String dflt  = c.getDefaultStr().isEmpty() ? "" : TG_SqlStore.get("tg_defaultDef", c.getDefaultStr());

            // COMMENT 여부 조각
            String cmt = c.getComment().isEmpty() ? "" : TG_SqlStore.get("tg_commentDef", c.getComment());

            // 조각문들 조립
            defs.add(TG_SqlStore.get("tg_columnDef", c.getColumnNm(), type, nullYn, dflt, cmt));

            // PK 여부 조각
            if ("Y".equals(c.getPkYn()))
                pks.add("`" + c.getColumnNm() + "`");
        }
        /** 모든 컬럼을 완성하고 한번만 실행 **/
        // DATETIME 조각
        defs.add(TG_SqlStore.get("tg_lschgDtm"));

        // PK 조각 최종 조각 완성 후 조립
        if (!pks.isEmpty())
            defs.add(TG_SqlStore.get("tg_primaryKey", String.join(", ", pks)));

        return TG_SqlStore.get("tg_createTable", tableName, String.join(",\n  ", defs));
    }
}

/*
Application (메인 메뉴 루프)
   ↓ "테이블 생성" 선택
TG_TableGenView        회원 일련번호 입력 → controller 호출 → 결과 출력 → Y/N 입력
   ↓
TG_TableGenController
   1. Connection 획득           (JDBCTemplate)
   2. 정의서 조회               (Repository → PreparedStatement + ResultSet)
   3. 테이블명별 그룹핑          (Map)
   4. CREATE TABLE 문자열 조립   (XML 템플릿 + String.format)
   5. DDL 실행                  (Repository → Statement.execute)
   ↓
TableGenView        Y → Data Load 호출 / N → 최초 화면*/
