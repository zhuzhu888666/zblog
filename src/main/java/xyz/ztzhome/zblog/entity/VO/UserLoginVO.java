package xyz.ztzhome.zblog.entity.VO;

import lombok.Data;
import xyz.ztzhome.zblog.entity.Bean.User;

import java.util.Date;

@Data
public class UserLoginVO {
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
    Date createTime;//创建的时间
    //添加token字段
    private String token;
}
