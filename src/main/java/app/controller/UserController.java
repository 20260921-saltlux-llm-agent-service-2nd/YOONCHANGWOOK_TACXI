package app.controller;

import app.model.DTO.UserDto;
import app.repository.UserRepository;
import app.view.UserView;

public class UserController {

    UserRepository user;
    UserView userView;
    UserDto loginUser;


    public UserController() {
        this.user = new UserRepository();
        this.userView = new UserView();
    }


    // 접근제어자 반환형 메서드명
    public boolean login(String id, String pw) {

        //2.데이터베이스에서 사용자 정보를 가져온다
        UserDto userDto = user.selectMemberByMemberId(id);

        //3.입력한 아이디 패스워드와 데이터베이스의 아이디 패스워드가 일치하는지 확인한다
        //맞은면 true/ 틀리면 false
        if (userDto == null) {
            userView.displayMessage("등록되지 않은 아이디입니다.");
            return false;
        }

        if (!userDto.getPwd().equals(pw)) {
            userView.displayMessage("비밀번호가 일치하지 않습니다.");
            return false;
        }

        //4.맞으면 Controller의 LoginUser필드에 사용자 정보를 담는다
        loginUser = userDto;
        return true;
    }


    // 회원가입 : 아이디, 비밀번호, 핸드폰번호를 받아 중복확인 후 저장한다
    public boolean signUp(String id, String pw, String phone) {

        //빈칸으로 입력했는지 확인한다
        if (id.isBlank() || pw.isBlank() || phone.isBlank()) {
            userView.displayMessage("아이디, 비밀번호, 핸드폰번호를 모두 입력해 주세요.");
            return false;
        }

        //같은 아이디가 이미 있는지 데이터베이스에서 확인한다 (로그인 때 만든 메서드 재사용)
        if (user.selectMemberByMemberId(id) != null) {
            userView.displayMessage("이 아이디는 사용할 수 없습니다.");
            return false;
        }

        //입력한 정보를 상자(UserDto)에 담는다
        UserDto userDto = new UserDto(0, id, phone, pw);

        //데이터베이스에 저장한다
        boolean saved = user.save(userDto);

        if (saved) {
            userView.displayMessage("회원가입이 완료되었습니다.");
            return true;
        } else {
            userView.displayMessage("회원가입에 실패했습니다.");
            return false;
        }
    }
}
