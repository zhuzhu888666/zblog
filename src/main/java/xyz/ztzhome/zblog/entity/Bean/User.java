package xyz.ztzhome.zblog.entity.Bean;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private long id;
    private String account;//账号
    private String password;//密码
    String email;
    String phone;
    String userAvatar;//头像
    int status=1;
    String nickname;//昵称
    int age;
    String gender;
    String address;
    String signature;//个性签名
    Date createTime=new Date();//创建的时间
}
