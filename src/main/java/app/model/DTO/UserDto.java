package app.model.DTO;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDto {

    private int memberSqno;
    private String memberId;
    private String phone;
    private String pwd;

}
