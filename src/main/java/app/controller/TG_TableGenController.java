package app.controller;
import app.model.DTO.TG_TableDefDto;
import app.repository.TG_TabelGenRepository;
import app.common.JDBCTemPlate;
import app.view.TG_TableView;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.*;


public class TG_TableGenController {

    // ===================== 1. 의존 객체 =====================
    private final TG_TabelGenRepository tgRepo = new TG_TabelGenRepository();


    //1-1.유저가 생성한 테이블 리스트 조회
    public void usercreatedTables(String userId) {
        List<TG_TableDefDto> usercreatedtablelist = new ArrayList<>();
        TG_TableView tgTableView = new TG_TableView();
        try {
           usercreatedtablelist = tgRepo.userCreatedtables(userId);
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
        tgTableView.showuserCreatedtable(usercreatedtablelist);
   }

    // ===================== 2. 공개 메서드 (View에서 호출) =====================
    /** 사용자 테이블 정의서 기준으로 DB 생성. 생성된 테이블명 목록 반환, 실패 시 예외 -> 콘솔 관련 기능들 **/
    public List<String> createTables(List<TG_TableDefDto> defs) {
        List<String> created = new ArrayList<>();
        StringBuilder createqry =  new StringBuilder();

        // 로그인 상태임

        // Table명을 받아서 그 이름에 해당하는 테이블 조회하기
        try (Connection conn = JDBCTemPlate.getConnection()) {
            // dfs dto 리스트에 담긴 테이블 내용 읽기
            Map<String, List<TG_TableDefDto>> byTable = groupByTable(defs);

            // 그룹핑
            for (Map.Entry<String, List<TG_TableDefDto>> e : byTable.entrySet()) {
                String ddl = buildCreateTable(e.getKey(), e.getValue());            // DDL 조립
                System.out.println(ddl);                                            // 테스트용: 만들어진 SQL 확인
                tgRepo.execute(conn, ddl);                                          // 실행
                created.add(e.getKey());
            }
        } catch (SQLException ex) {
//            throw new RuntimeException("테이블 생성 실패 (생성 완료: " + created + ")", ex);
            // 원인 테스트용
            System.out.println(ex.getMessage());
            if (ex.getCause() != null) System.out.println("원인: " + ex.getCause().getMessage());
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
    private String buildCreateTable(String tableName, List<TG_TableDefDto> defs) {
        StringBuilder createqry = new StringBuilder();
        List<String> pks = new ArrayList<>();                    // PK 컬럼 모음

        createqry.append("CREATE TABLE `").append(tableName).append("` (");

        for (int i = 0; i < defs.size(); i++) {
            TG_TableDefDto d = defs.get(i);
            String type = d.getColDataType().trim().toUpperCase();     // switch용 (기본값은 대문자로 바꾸지 않음)

            // 1. 컬럼 앞 쉼표 (첫 컬럼은 제외) + 컬럼명 + 자료형
            createqry.append(i > 0 ? ",\n  " : "\n  ");
            createqry.append('`').append(d.getColumnNm()).append("` ").append(type);

            // 2. 크기: CHAR, VARCHAR, DECIMAL, NUMERIC만
            switch (type) {
                case "CHAR": case "VARCHAR": case "DECIMAL": case "NUMERIC":
                    if (!isEmpty(d.getColum())) createqry.append('(').append(d.getColum().trim()).append(')');
                    break;
                default:
                    break;
            }

            // 3. NULL 여부 (Y = 허용)
            createqry.append("Y".equalsIgnoreCase(d.getNullYn()) ? " NULL" : " NOT NULL");

            // 4. DEFAULT / AUTO_INCREMENT
            if (!isEmpty(d.getDefaultStr())) {
                String v = d.getDefaultStr().trim();                   // 원본 유지

                switch (type) {
                    // ----- 숫자 -----
                    case "TINYINT": case "SMALLINT": case "MEDIUMINT": case "INT": case "INTEGER":
                    case "BIGINT": case "DECIMAL": case "NUMERIC": case "FLOAT": case "DOUBLE":
                        if ("AUTO_INCREMENT".equalsIgnoreCase(v)) createqry.append(" AUTO_INCREMENT");
                        else                                      createqry.append(" DEFAULT ").append(v);
                        break;

                    // ----- 문자 -----
                    case "CHAR": case "VARCHAR":
                        createqry.append(" DEFAULT '").append(v).append('\'');
                        break;

                    // ----- 날짜/시간 -----
                    case "DATETIME": case "TIMESTAMP":
                        if ("CURRENT_TIMESTAMP".equalsIgnoreCase(v)) createqry.append(" DEFAULT CURRENT_TIMESTAMP");
                        else                                         createqry.append(" DEFAULT '").append(v).append('\'');
                        break;

                    case "DATE":
                        if ("CURDATE()".equalsIgnoreCase(v)) createqry.append(" DEFAULT (CURDATE())");
                        else                                 createqry.append(" DEFAULT '").append(v).append('\'');
                        break;

                    case "TIME": case "YEAR":
                        createqry.append(" DEFAULT '").append(v).append('\'');
                        break;

                    // ----- 그 외 (TEXT, BLOB, JSON ...): DEFAULT 사용 불가 → 생략 -----
                    default:
                        break;
                }
            }   // ← if 닫는 중괄호

            // 5. COMMENT
            if (!isEmpty(d.getComment()))
                createqry.append(" COMMENT '").append(d.getComment().trim()).append('\'');

            // 6. PK는 모아만 둠
            if ("Y".equalsIgnoreCase(d.getPkYn())) pks.add("`" + d.getColumnNm() + "`");
        }   // ← for 닫는 중괄호

        // 테이블당 한 번
        createqry.append(",\n  `LSCHG_DTM` DATETIME DEFAULT CURRENT_TIMESTAMP");
        if (!pks.isEmpty())
            createqry.append(",\n  PRIMARY KEY (").append(String.join(", ", pks)).append(")");

        createqry.append("\n)");
        return createqry.toString();
    }

    private boolean isEmpty(String s) {
        return s == null || s.isBlank() || "null".equalsIgnoreCase(s.trim());
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
