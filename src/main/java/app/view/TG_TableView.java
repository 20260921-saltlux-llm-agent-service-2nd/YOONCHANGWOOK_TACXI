package app.view;



import java.util.*;

public class TG_TableView {
    private final Scanner sc = new Scanner(System.in);

    public boolean confirmDataLoad() {
        while (true) {
            System.out.println("\n==== 데이터 로드 여부 ====");
            System.out.print("데이터 적재를 실행하시겠습니까? (Y/N): ");

            String answer = sc.nextLine().trim();

            if (answer.equalsIgnoreCase("Y")) {return true;}
            if (answer.equalsIgnoreCase("N")) {return false;}

            System.out.println("Y 또는 N을 입력해주세요.");
        }
    }
    // 생성된 테이블 목록 출력
    public void showCreatedTables(List<String> tableNames) {
        System.out.println("==== 생성된 테이블 목록 ====");

        for (int i = 0; i < tableNames.size(); i++) {
            System.out.println((i + 1) + ". 테이블 이름: " + tableNames.get(i));
        }
    }

}
