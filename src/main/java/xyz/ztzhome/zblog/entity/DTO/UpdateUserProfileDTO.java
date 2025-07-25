package xyz.ztzhome.zblog.entity.DTO;

import lombok.Data;


@Data
public class UpdateUserProfileDTO {
    private long id;
    private String account;//账号
    String phone;
    String nickname;//昵称
    int age;
    String gender;
    String address;
    String signature;//个性签名
}
