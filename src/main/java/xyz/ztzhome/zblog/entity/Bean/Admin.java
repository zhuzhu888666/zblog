package xyz.ztzhome.zblog.entity.Bean;

import lombok.Data;

@Data
public class Admin {
    private long id;
    private String account;
    private String email;
    private String password;
}
