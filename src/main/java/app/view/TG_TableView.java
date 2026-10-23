package app.view;

import app.controller.TG_TableGenController;

import java.util.*;

public class TG_TableView {
    private final Scanner sc = new Scanner(System.in);
    private final TG_TableGenController controller = new TG_TableGenController();

    public void createTableConsole() {
        System.out.println("회원 일련번호를 입력해주세요.");
        int memberSqno = Integer.parseInt(sc.nextLine().trim());

        try {
            System.out.println("테이블 생성 완료: " + controller.createTables(memberSqno));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;                                    // 실패하면 최초 화면으로
        }

        while(true) {
            System.out.println("데이터 적재를 실행하시겠습니까? (Y/N): ");
            String ans = sc.nextLine().trim();
            if (ans.equalsIgnoreCase("Y")) { return; } // DL 호출
            if (ans.equalsIgnoreCase("N")) { return; } // main 메뉴로 복귀
            System.out.println("Y 또는 N을 입력해주세요.");
        }
    }

}
