package app.view;

import app.controller.TG_TableGenController;
import app.model.DTO.TG_TableDefDto;

import java.util.*;

public class TG_TableView {
    private final Scanner sc = new Scanner(System.in);
    private final TG_TableGenController controller = new TG_TableGenController();
    private List<TG_TableDefDto> list = new ArrayList<>();

    public void createTableConsole() {
        System.out.println("회원 ID를 입력해주세요.");
        String memberId = sc.nextLine().trim();

        try {
            // 회원이 등록한 사용자 정의서 테이블 조회
            List<TG_TableDefDto> list = controller.usercreatedTables(memberId);
            if (list == null || list.isEmpty()) {
                System.out.println("등록된 테이블 정의서가 없습니다.");
                return;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;                                    // 실패하면 최초 화면으로
        }

        while(true) {
            System.out.println("==== 데이터 로드 여부 ====\n");
            System.out.println("데이터 적재를 실행하시겠습니까? (Y/N): ");
            String ans = sc.nextLine().trim();
            if (ans.equalsIgnoreCase("Y")) { return; } // DL 호출
            if (ans.equalsIgnoreCase("N")) { return; } // main 메뉴로 복귀
            System.out.println("Y 또는 N을 입력해주세요.");
        }
    }
    public void showuserCreatedtable(List<TG_TableDefDto> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println("==== 생성한 테이블 목록 ====");
            System.out.println((i+1)+"."+"테이블 이름: " + list.get(i).getTableName());
        }
    }

}
