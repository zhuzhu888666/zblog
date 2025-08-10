package xyz.ztzhome.zblog.entity.VO;

import lombok.Data;
import xyz.ztzhome.zblog.entity.Bean.Admin;

import java.util.Date;

@Data
public class AdminLoginVO {
    private long id;
    private String account;
    private String email;
    private String token;
    private Date createTime;
    
    public static AdminLoginVO fromAdmin(Admin admin) {
        AdminLoginVO vo = new AdminLoginVO();
        vo.setId(admin.getId());
        vo.setAccount(admin.getAccount());
        vo.setEmail(admin.getEmail());
        vo.setCreateTime(new Date());
        return vo;
    }
}
