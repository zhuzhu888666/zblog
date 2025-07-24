package xyz.ztzhome.zblog.entity.DTO;

import lombok.Data;


@Data
public class updateUserDTO {
    private long id;
    private String account;//账号

    String email;
    String phone;
    String userAvatar;//头像
    int status=1;
    String nickname;//昵称
    int age;
    String gender;
    String address;
    String signature;//个性签名
}
