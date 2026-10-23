package app.repository;

import app.model.DTO.DL_SelectColnmDTO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static app.common.JDBCTemPlate.close;
import static app.common.JDBCTemPlate.getConnection;

public class DUL_Repository {
    private final Properties prop = new Properties();

    private final String DUL_SelectallFromTableQry_path = "src/main/java/resources/db/mapper/DUL_SelectAllFromTable.xml";

    public boolean DataUnload(List<DL_SelectColnmDTO> ColnmDTOList, String table_nm, String targetPath) {

        PreparedStatement psmt = null;
        Connection con = null;
        ResultSet rst = null;
        BufferedWriter writer = null;
        StringBuilder colnm = new StringBuilder();
        StringBuilder instance = new StringBuilder();
        int colCnt = ColnmDTOList.size();

        // FileInputStream은 try-with-resources로 자동 close
        try (FileInputStream fis = new FileInputStream(DUL_SelectallFromTableQry_path)) {
            prop.loadFromXML(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        con = getConnection();

        // 프로그램이 실행하는 OS에 맞춰 경로를 조립해 주는 객체
        Path outPath = Paths.get(targetPath, table_nm + ".csv");


        String qry = String.format(prop.getProperty("dul_selectallfromtable"), table_nm);

        try {
            psmt = con.prepareStatement(qry);
            rst = psmt.executeQuery();

            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outPath.toFile()), StandardCharsets.UTF_8));

            for (int i = 0; i < colCnt; i++) {
                if (!colnm.isEmpty()) colnm.append(",");
                colnm.append(ColnmDTOList.get(i).getColnm());
            }

            writer.write(colnm.toString());
            writer.newLine();

            while (rst.next()) {
                instance.setLength(0);
                for (int i = 1; i <= colCnt; i++) {

                    // 첫칼럼이 아닐경우 쉼표 APPEND
                    if (i > 1) instance.append(",");

                    String val = rst.getString(i);
                    instance.append(val == null ? "" : escapeCsv(val));
                }
                writer.write(instance.toString());
                writer.newLine();
            }
            return true;

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 자원 반납: 만든 순서의 역순으로
            if (writer != null) {
                try {
                    writer.close();   // close()가 flush도 수행
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            close(rst);
            close(psmt);
            close(con);
        }
    }
    // 칼럼안에 쉼표, 개행, /, 등 문자가 들어 갔을 경우 처리하는 메서드
    private String escapeCsv(String val) {
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }

}
