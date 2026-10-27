package app.repository;

import app.common.JDBCTemPlate;
import app.model.DTO.UserDto;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class UserRepository {

    private static final String QUERY_FILE =
            "src/main/java/resources/db/mapper/UserSelect.xml";

    private final Properties prop = new Properties();

    public UserRepository() {
        try (FileInputStream inputStream = new FileInputStream(QUERY_FILE)) {
            prop.loadFromXML(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("회원 SQL 파일을 읽지 못했습니다.", e);
        }
    }


    // 입력받은 아이디의 회원정보를 가져온다
    public UserDto selectMemberByMemberId(String id) {

        // 회원 정보를 담을 빈 상자를 만듦
        UserDto userDto = new UserDto();

        // JDBC 이용해서 데이터베이스에서 입력받은 아이디로 일치하는 회원정보를 가져오고

        String query = prop.getProperty("selectMemberByMemberId");                  // prop 상자에서 이름표로 SQL 문장을 꺼내 query 에 담음

        try (Connection connection = JDBCTemPlate.getConnection();                  // DB 연결 통로를 얻음 (try 가 끝나면 자동으로 닫힘)
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {   // SQL 실행 준비 (자동으로 닫힘)

            preparedStatement.setString(1, id);                                     // 첫 번째 ? 에 입력받은 아이디를 채움

            try (ResultSet resultSet = preparedStatement.executeQuery()) {          // SELECT 실행 → 결과 표를 받음 (자동으로 닫힘)
                if (resultSet.next()) {                                             // 결과 첫 줄로 이동. 줄이 있으면 (= 회원이 있으면)
                    userDto.setMemberSqno(resultSet.getInt("MEMBER_SQNO"));         // 회원 일련번호를 꺼내 상자에 넣음
                    userDto.setMemberId(resultSet.getString("MEMBER_ID"));          // 회원 ID 를 꺼내 상자에 넣음
                    userDto.setPhone(resultSet.getString("PHONE"));                 // 핸드폰번호를 꺼내 상자에 넣음
                    userDto.setPwd(resultSet.getString("PWD"));                     // 비밀번호를 꺼내 상자에 넣음
                    return userDto;   // 회원정보가 담긴 userDTO를 반환한다.                                        // 회원 정보가 담긴 상자를 바로 돌려줌
                }                                                                   // if 끝
            }                                                                       // ResultSet try 끝 → 결과 표 자동으로 닫힘
        } catch (SQLException e) {                                                  // SQL 실행 중 오류가 나면
            throw new IllegalStateException("회원 조회 중 오류가 발생했습니다.", e);       // 원인(e)을 담아 오류를 던짐
        }                                                                           // try-catch 끝 → 연결 통로 자동으로 닫힘

        return null;                                                                // 회원을 못 찾았으면 빈 상자가 아니라 null 을 돌려줌
    }


    // 회원정보가 담긴 상자를 받아 회원 테이블에 저장한다
    public boolean save(UserDto userDto) {                                          // 매개변수: 회원정보 상자 / 반환형: 성공 true, 실패 false

        String query = prop.getProperty("insertMember");                            // prop 상자에서 이름표로 INSERT 문장을 꺼내 query 에 담음

        try (Connection connection = JDBCTemPlate.getConnection();                  // DB 연결 통로를 얻음 (try 가 끝나면 자동으로 닫힘)
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {   // SQL 실행 준비 (자동으로 닫힘)

            preparedStatement.setString(1, userDto.getMemberId());                  // 첫 번째 ? 에 아이디를 채움
            preparedStatement.setString(2, userDto.getPhone());                     // 두 번째 ? 에 핸드폰번호를 채움
            preparedStatement.setString(3, userDto.getPwd());                       // 세 번째 ? 에 비밀번호를 채움

            return preparedStatement.executeUpdate() == 1;                          // INSERT 실행 → 저장된 줄 수가 1이면 true, 아니면 false
        } catch (SQLException e) {                                                  // SQL 실행 중 오류가 나면
            throw new IllegalStateException("회원 저장 중 오류가 발생했습니다.", e);       // 원인(e)을 담아 오류를 던짐
        }                                                                           // try-catch 끝 → 연결 통로 자동으로 닫힘
    }


}
