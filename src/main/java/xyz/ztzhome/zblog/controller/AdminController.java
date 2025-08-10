package xyz.ztzhome.zblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.ztzhome.zblog.entity.Bean.Admin;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.LoginDTO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.IAdminService;

import java.util.List;

/**
 * 管理员控制器
 * 提供管理员注册、登录、用户管理等功能
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    // ========== 管理员账户管理 ==========

    /**
     * 管理员注册
     * @param admin 管理员信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseMessage register(@RequestBody Admin admin) {
        return adminService.register(admin);
    }

    /**
     * 管理员登录
     * @param loginDTO 登录信息
     * @return 登录结果，包含JWT token
     */
    @PostMapping("/login")
    public ResponseMessage adminLogin(@RequestBody LoginDTO loginDTO) {
        return adminService.adminLogin(loginDTO);
    }

    /**
     * 更新管理员信息
     * @param admin 管理员信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public ResponseMessage updateAdmin(@RequestBody Admin admin) {
        return adminService.updateAdmin(admin);
    }

    /**
     * 删除管理员
     * @param id 管理员ID
     * @return 删除结果
     */
    @PostMapping("/delete")
    public ResponseMessage deleteAdmin(@RequestParam("id") long id) {
        return adminService.deleteAdmin(id);
    }

    /**
     * 根据ID获取管理员信息
     * @param id 管理员ID
     * @return 管理员信息
     */
    @GetMapping("/info")
    public ResponseMessage<Admin> getAdminById(@RequestParam("id") long id) {
        return adminService.getAdminById(id);
    }

    /**
     * 根据账号获取管理员信息
     * @param account 管理员账号
     * @return 管理员信息
     */
    @GetMapping("/info/account")
    public ResponseMessage<Admin> getAdminByAccount(@RequestParam("account") String account) {
        return adminService.getAdminByAccount(account);
    }

    // ========== 用户管理 ==========

    /**
     * 获取所有用户信息（分页）
     * @param pageNum 页码，默认1
     * @param pageSize 每页大小，默认10
     * @return 用户列表
     */
    @GetMapping("/users")
    public ResponseMessage<PageResponse<User>> getAllUsers(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return adminService.getAllUsers(pageNum, pageSize);
    }

    /**
     * 更新用户状态
     * @param account 用户账号
     * @param status 用户状态（0-禁用，1-正常）
     * @return 更新结果
     */
    @PostMapping("/users/status")
    public ResponseMessage updateUserStatus(
            @RequestParam("account") String account,
            @RequestParam("status") int status) {
        return adminService.updateUserStatus(account, status);
    }

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    @PostMapping("/users/update")
    public ResponseMessage updateUser(@RequestBody User user) {
        return adminService.updateUser(user);
    }

    /**
     * 删除用户
     * @param account 用户账号
     * @return 删除结果
     */
    @PostMapping("/users/delete")
    public ResponseMessage deleteUser(@RequestParam("account") String account) {
        return adminService.deleteUser(account);
    }

    /**
     * 根据账号查询用户
     * @param account 用户账号
     * @return 用户信息
     */
    @GetMapping("/users/search")
    public ResponseMessage<User> getUserByAccount(@RequestParam("account") String account) {
        return adminService.getUserByAccount(account);
    }

    /**
     * 搜索用户（根据账号或邮箱模糊查询）
     * @param keyword 搜索关键词
     * @param pageNum 页码，默认1
     * @param pageSize 每页大小，默认10
     * @return 用户列表
     */
    @GetMapping("/users/search/keyword")
    public ResponseMessage<PageResponse<User>> searchUsers(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return adminService.searchUsers(keyword, pageNum, pageSize);
    }
}
