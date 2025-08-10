package xyz.ztzhome.zblog.service;

import xyz.ztzhome.zblog.entity.Bean.Admin;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.LoginDTO;
import xyz.ztzhome.zblog.entity.VO.AdminLoginVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

public interface IAdminService {

    // ========== 管理员账户管理 ==========
    
    /**
     * 管理员注册
     */
    ResponseMessage register(Admin admin);

    /**
     * 管理员登录
     */
    ResponseMessage<AdminLoginVO> adminLogin(LoginDTO loginDTO);
    
    /**
     * 更新管理员信息
     */
    ResponseMessage updateAdmin(Admin admin);
    
    /**
     * 删除管理员
     */
    ResponseMessage deleteAdmin(long id);
    
    /**
     * 根据ID获取管理员信息
     */
    ResponseMessage<Admin> getAdminById(long id);
    
    /**
     * 根据账号获取管理员信息
     */
    ResponseMessage<Admin> getAdminByAccount(String account);

    // ========== 用户管理 ==========
    
    /**
     * 更新用户状态
     */
    ResponseMessage updateUserStatus(String account, int status);

    /**
     * 获取所有用户（分页）
     */
    ResponseMessage<PageResponse<User>> getAllUsers(int pageNum, int pageSize);
    
    /**
     * 更新用户信息
     */
    ResponseMessage updateUser(User user);
    
    /**
     * 删除用户
     */
    ResponseMessage deleteUser(String account);
    
    /**
     * 根据账号查询用户
     */
    ResponseMessage<User> getUserByAccount(String account);
    
    /**
     * 搜索用户（根据账号或邮箱模糊查询）
     */
    ResponseMessage<PageResponse<User>> searchUsers(String keyword, int pageNum, int pageSize);
}
