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
}
