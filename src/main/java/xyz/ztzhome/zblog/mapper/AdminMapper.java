package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.ztzhome.zblog.entity.Bean.Admin;

@Mapper
public interface AdminMapper {
    
    /**
     * 插入管理员
     */
    int insertAdmin(Admin admin);
    
    /**
     * 更新管理员信息
     */
    int updateAdmin(Admin admin);
    
    /**
     * 根据ID删除管理员
     */
    int deleteAdminById(@Param("id") long id);
    
    /**
     * 根据ID查询管理员
     */
    Admin selectAdminById(@Param("id") long id);
    
    /**
     * 根据账号查询管理员
     */
    Admin selectByAccount(@Param("account") String account);
    
    /**
     * 根据邮箱查询管理员
     */
    Admin selectByEmail(@Param("email") String email);
    
    /**
     * 检查账号是否存在
     */
    boolean existsByAccount(@Param("account") String account);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(@Param("email") String email);
}
