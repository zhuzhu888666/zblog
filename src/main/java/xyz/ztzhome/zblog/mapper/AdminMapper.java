package xyz.ztzhome.zblog.mapper;

import xyz.ztzhome.zblog.entity.Bean.Admin;

public interface AdminMapper {
    int insertAdmin(Admin admin);
    int updateAdmin(Admin admin);
    int deleteAdminById(int id);
    Admin selectAdminById(int id);
    Admin selectByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByAccount(String account);
}
