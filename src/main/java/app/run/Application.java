package app.run;

import app.controller.DL_Controller;
import app.controller.DUL_Controller;
import app.controller.UserController;
import app.view.TG_TableView;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {

        try (Scanner sc = new Scanner(System.in)) {
            //UserController controller = new UserController();
            startProgram(sc/*, controller*/);
        } catch (IllegalStateException e) {
            System.out.println("데이터베이스 처리 중 오류가 발생했습니다.");
            System.out.println("db.properties 설정과 schema.sql 실행 상태를 확인하세요.");
        } catch (NoSuchElementException e) {
            System.out.println("입력이 종료되어 프로그램을 종료합니다.");
        }
    }

    private static void startProgram(Scanner sc/*, UserController userController*/) {
        while (true) {
            printStartMenu();
            int choice = readInt(sc);

            switch (choice) {
                case 1:
                    //signup(sc, userController);
                    break;
                case 2:
                    if (/*login(sc, userController)*/true) {
                        midCatMenu(sc);
                    } else {
                        System.out.println("로그인에 실패하였습니다. 다시 입력해주세요.");
                    }
                    break;
                case 9:
                    System.out.println("프로그램을 종료합니다.");
                    return;
                default:
                    System.out.println("올바른 메뉴 번호를 입력하세요.");
            }
        }
    }

    private static void midCatMenu(Scanner sc) {
        while (true) {
            printMidCatMenu();
            int choice = readInt(sc);

            switch (choice) {
                case 1:
                    // 테이블 생성
                    TG_TableView tgView = new TG_TableView();
                    tgView.createTableConsole();
                    break;
                case 2:
                    tableMenu(sc);
                    break;
                case 9:
                    return;
                default:
                    System.out.println("올바른 메뉴 번호를 입력하세요.");
            }
        }
    }

    private static void tableMenu(Scanner sc) {
        while (true) {
            printTableMenu();
            int choice = readInt(sc);

            switch (choice) {
                case 1:
                    // 생성한 테이블 조회
                    break;
                case 2:
                    if(dataload(sc)) {
                        return;
                    } else {
                        break;
                    }
                case 3:
                    // 데이터 UNLOAD dataunload
                    if(dataunload(sc)) {
                        return;
                    } else {
                        break;
                    }
                case 9:
                    return;
                default:
                    System.out.println("올바른 메뉴 번호를 입력하세요.");
            }
        }
    }

    private static void printStartMenu() {
        System.out.println();
        System.out.println("=====테이블 자동 생성 및 데이터 적재·추출 프로그램=====\n");
        System.out.println("1. 회원가입");
        System.out.println("2. 로그인");
        System.out.println("9. 프로그램 종료");
        System.out.print("선택: ");
    }

    private static void printMidCatMenu() {
        System.out.println();
        System.out.println("=====환영합니다. 원하시는 메뉴를 골라주세요=====\n");
        System.out.println("1. 테이블 생성");
        System.out.println("2. 테이블 조회");
        System.out.println("9. 이전 메뉴로");
        System.out.print("선택: ");
    }

    private static void printTableMenu() {
        System.out.println();
        System.out.println("===== 테이블 조회 =====\n");
        System.out.println("1. 생성한 테이블 조회");
        System.out.println("2. 데이터 LOAD");
        System.out.println("3. 데이터 UNLOAD");
        System.out.println("9. 이전 메뉴로");
        System.out.print("선택: ");
    }
    private static boolean dataload(Scanner sc) {
        System.out.println();
        System.out.println("===== DataLoad =====\n");
        System.out.println("Load를 원하시는 테이블 명(물리명)을 입력해주세요 :");
        String table_nm = sc.nextLine().trim();
        System.out.println("Load를 원하시는 csv파일의 경로를 입력해주세요(/로 디렉토리 구분):");
        String targetPath = sc.nextLine().trim();
        System.out.println("Load프로그램을 호출중입니다, 기다려 주세요");
        DL_Controller dlController = new DL_Controller();

        boolean result = dlController.StartDL_Controller(table_nm,targetPath);

        return result;

    }
    private static boolean dataunload(Scanner sc) {
        System.out.println();
        System.out.println("===== dataunload =====\n");
        System.out.println("UnLoad를 원하시는 테이블 명(물리명)을 입력해주세요 :");
        String table_nm = sc.nextLine().trim();
        System.out.println("UnLoad를 원하시는 csv파일의 경로를 입력해주세요(/로 디렉토리 구분):");
        String targetPath = sc.nextLine().trim();
        System.out.println("UnLoad프로그램을 호출중입니다, 기다려 주세요");
        DUL_Controller dulController = new DUL_Controller();

        boolean result = dulController.StartDUL_Controller(table_nm,targetPath);

        return result;

    }


    // 숫자 입력 전용: 줄 단위로 읽어서 버퍼 문제를 막고, 숫자가 아니면 -1 반환
    private static int readInt(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void signup(Scanner sc, UserController userController) {
        System.out.println();
        System.out.println("===== 회원가입 =====");
        System.out.print("이름: ");
        String name = sc.nextLine().trim();
        System.out.print("나이: ");
        int age = readInt(sc);
        System.out.print("휴대폰 번호 (예: 010-1234-5678): ");
        String phone = sc.nextLine().trim();

        //userController.signUp(name, age, phone);
    }

    public static boolean login(Scanner sc, UserController userController) {
        System.out.println();
        System.out.println("===== 로그인 =====");
        System.out.print("ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Password: ");
        String pwd = sc.nextLine().trim();

        //return userController.login(id, pwd);
        return true;
    }
}