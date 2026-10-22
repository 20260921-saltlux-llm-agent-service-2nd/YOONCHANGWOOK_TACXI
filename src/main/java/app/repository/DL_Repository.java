package app.repository;

import app.model.DTO.DL_SelectColnmDTO;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static app.common.JDBCTemPlate.close;
import static app.common.JDBCTemPlate.getConnection;

public class DL_Repository {

    private final Properties prop = new Properties();

    private final String DL_SelectcolnmQry_path = "src/main/java/resources/db/mapper/DL_SelectColnm.xml";


    public DL_Repository () {

    }

    public  List<DL_SelectColnmDTO> selectColnmDTOList (String table_nm) {

        //1. 객체 선언 및 초기화
        List<DL_SelectColnmDTO> selectColnmDTOList = new ArrayList<>();
        ResultSet rst = null;
        PreparedStatement psmt = null;


        //2.상수로 지정해둔 경로를 따라 XMl로드
        try {
            FileInputStream fileInputStream = new FileInputStream(DL_SelectcolnmQry_path);
            prop.loadFromXML(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 연결 누수 가능성 염두(close(con))에 대비
        Connection con = getConnection();

        //3.쿼리실행 및 DTO에 값 넘김
        try {
            String qry = prop.getProperty("dl_selectcolnm");
            psmt = con.prepareStatement(qry);
            psmt.setString(1,table_nm);
            rst = psmt.executeQuery();

            while(rst.next()) {

                DL_SelectColnmDTO dlSelectColnmDTO = new DL_SelectColnmDTO();
                dlSelectColnmDTO.setColnm(rst.getString("COLUMN_NAME"));
                dlSelectColnmDTO.setTable_nm(rst.getString("TABLE_NAME"));
                selectColnmDTOList.add(dlSelectColnmDTO);

            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            //4. 자원 반납
            close(rst);
            close(psmt);
            close(con);
        }

        return selectColnmDTOList;
    }

    public boolean Loadcsv(List<DL_SelectColnmDTO> ColnmDTOList, String table_nm, String targetPath) {

        //1. 객체 선언 및 초기화
        PreparedStatement psmt = null;
        Connection conn = null;
        int cnt = 0;
        int batchCnt = 0;
        StringBuilder colList = new StringBuilder();
        StringBuilder marks = new StringBuilder();

        //2. XML 로드 (try-with-resources로 자동 close)
        try (FileInputStream fis = new FileInputStream("src/main/java/resources/db/mapper/DL_InsertTable.xml")) {
            prop.loadFromXML(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //3. 컬럼 목록 / 위치마크 / 쿼리 조립 (한 번만)
        for (int i = 0; i < ColnmDTOList.size(); i++) {
            colList.append("`");
            colList.append(ColnmDTOList.get(i).getColnm());
            colList.append("`");
            marks.append("?");

            if (i < ColnmDTOList.size() - 1) {
                colList.append(", ");
                marks.append(", ");
            }
        }
        String qry = String.format(prop.getProperty("dl_inserttoTable"), table_nm, colList, marks);
        System.out.println(qry);

        //4. 연결 / PreparedStatement 생성 (한 번만) 후 CSV 읽어서 적재
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(targetPath), Charset.forName("MS949")))) {

            conn = getConnection();
            conn.setAutoCommit(false);
            psmt = conn.prepareStatement(qry);

            String readLine;

            while ((readLine = br.readLine()) != null) {
                // 첫 줄(헤더)과 빈 줄은 건너뜀
                if (cnt != 0 && readLine.length() != 0) {

                    String[] temp_arr = readLine.split(",", -1);

                    // 컬럼 수와 값 개수가 다른 줄은 건너뜀
                    if (temp_arr.length != ColnmDTOList.size()) {
                        System.out.println("컬럼 수 불일치로 건너뜀(" + (cnt + 1) + "번째 줄)");
                    } else {

                        //System.out.println("temp_arr = " + Arrays.toString(temp_arr));
                        for (int i = 0; i < ColnmDTOList.size(); i++) {
                            // 빈 값은 NULL로 저장
                            psmt.setString(i + 1, temp_arr[i].isEmpty() ? null : temp_arr[i]);
                        }
                        psmt.addBatch();
                        batchCnt++;

                        if (batchCnt % 1000 == 0) {
                            psmt.executeBatch();
                        }
                    }
                }
                cnt++;
            }

            psmt.executeBatch();   // 남은 건 실행
            conn.commit();
            return true;

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            //5. 자원 반납
            close(psmt);
            close(conn);
        }
    }
}
