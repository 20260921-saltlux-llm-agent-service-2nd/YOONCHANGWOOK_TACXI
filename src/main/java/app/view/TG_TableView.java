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
            // list에 그러면 Table name에 해당하는 거 가져오기
            System.out.println("테이블 생성 완료: " + controller.createTables(list));
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
