package xyz.ztzhome.zblog.entity.DTO;

import lombok.Data;

@Data
public class UpdateUserSecurityDTO {
    private long id;
    private String account;
    private String password;
    private String email;
}
