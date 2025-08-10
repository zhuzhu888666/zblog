package xyz.ztzhome.zblog.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.Admin;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.LoginDTO;
import xyz.ztzhome.zblog.entity.VO.AdminLoginVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.AdminMapper;
import xyz.ztzhome.zblog.mapper.UserMapper;
import xyz.ztzhome.zblog.service.IAdminService;
import xyz.ztzhome.zblog.util.BCryptPassword;
import xyz.ztzhome.zblog.util.JwtToken;

import java.util.List;

@Service
public class AdminServiceImpl implements IAdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    public AdminServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // ========== 管理员账户管理 ==========

    @Override
    public ResponseMessage register(Admin admin) {
        try {
            if (admin == null || admin.getAccount() == null || admin.getAccount().isEmpty() || 
                admin.getPassword() == null || admin.getPassword().isEmpty()) {
                return ResponseMessage.error("缺少必要参数");
            }

            // 检查账号是否已存在
            boolean exists = adminMapper.existsByAccount(admin.getAccount());
            if (exists) {
                return ResponseMessage.error("账号已存在");
            }

            // 检查邮箱是否已存在（如果提供了邮箱）
            if (admin.getEmail() != null && !admin.getEmail().isEmpty()) {
                boolean emailExists = adminMapper.existsByEmail(admin.getEmail());
                if (emailExists) {
                    return ResponseMessage.error("邮箱已存在");
                }
            }

            // 加密密码
            admin.setPassword(BCryptPassword.encode(admin.getPassword()));
            
            int result = adminMapper.insertAdmin(admin);
            if (result > 0) {
                return ResponseMessage.success("注册成功");
            } else {
                return ResponseMessage.error("注册失败");
            }
        } catch (Exception e) {
            logger.error("管理员注册失败", e);
            return ResponseMessage.error("服务异常");
        }
    }

    @Override
    public ResponseMessage adminLogin(LoginDTO loginDTO) {
        try {
            if (loginDTO == null || loginDTO.getAccount() == null || loginDTO.getAccount().isEmpty() ||
                loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
                return new ResponseMessage<>(ResponseConstant.error, "账号和密码不能为空");
            }

            // 根据账号查询管理员
            Admin admin = adminMapper.selectByAccount(loginDTO.getAccount());
            if (admin == null) {
                return new ResponseMessage<>(ResponseConstant.error, "账号不存在");
            }

            // 验证密码
            if (!BCryptPassword.matches(loginDTO.getPassword(), admin.getPassword())) {
                return new ResponseMessage<>(ResponseConstant.error, "密码错误");
            }

            // 生成JWT token
            String token = JwtToken.generateAdminToken(admin.getAccount());
            
            // 创建登录响应对象
            AdminLoginVO adminLoginVO = AdminLoginVO.fromAdmin(admin);
            adminLoginVO.setToken(token);
            
            return new ResponseMessage<AdminLoginVO>(ResponseConstant.success, "登录成功", adminLoginVO);
        } catch (Exception e) {
            logger.error("管理员登录失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage updateAdmin(Admin admin) {
        try {
            if (admin == null) {
                return new ResponseMessage<>(ResponseConstant.error, "管理员信息不能为空");
            }

            // 检查管理员是否存在
            Admin existingAdmin = null;
            if (admin.getId() > 0) {
                existingAdmin = adminMapper.selectAdminById(admin.getId());
            } else if (admin.getAccount() != null && !admin.getAccount().isEmpty()) {
                existingAdmin = adminMapper.selectByAccount(admin.getAccount());
            }

            if (existingAdmin == null) {
                return new ResponseMessage<>(ResponseConstant.error, "管理员不存在");
            }

            // 如果更新密码，需要加密
            if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
                admin.setPassword(BCryptPassword.encode(admin.getPassword()));
            }

            // 检查邮箱是否被其他管理员使用
            if (admin.getEmail() != null && !admin.getEmail().isEmpty() && 
                !admin.getEmail().equals(existingAdmin.getEmail())) {
                boolean emailExists = adminMapper.existsByEmail(admin.getEmail());
                if (emailExists) {
                    return new ResponseMessage<>(ResponseConstant.error, "邮箱已被其他管理员使用");
                }
            }

            int result = adminMapper.updateAdmin(admin);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "更新成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "更新失败");
            }
        } catch (Exception e) {
            logger.error("更新管理员信息失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage deleteAdmin(long id) {
        try {
            if (id <= 0) {
                return new ResponseMessage<>(ResponseConstant.error, "管理员ID无效");
            }

            // 检查管理员是否存在
            Admin admin = adminMapper.selectAdminById(id);
            if (admin == null) {
                return new ResponseMessage<>(ResponseConstant.error, "管理员不存在");
            }

            int result = adminMapper.deleteAdminById(id);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "删除成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "删除失败");
            }
        } catch (Exception e) {
            logger.error("删除管理员失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<Admin> getAdminById(long id) {
        try {
            if (id <= 0) {
                return new ResponseMessage<>(ResponseConstant.error, "管理员ID无效");
            }

            Admin admin = adminMapper.selectAdminById(id);
            if (admin == null) {
                return new ResponseMessage<>(ResponseConstant.error, "管理员不存在");
            }

            // 不返回密码
            admin.setPassword(null);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", admin);
        } catch (Exception e) {
            logger.error("查询管理员信息失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<Admin> getAdminByAccount(String account) {
        try {
            if (account == null || account.isEmpty()) {
                return new ResponseMessage<>(ResponseConstant.error, "账号不能为空");
            }

            Admin admin = adminMapper.selectByAccount(account);
            if (admin == null) {
                return new ResponseMessage<>(ResponseConstant.error, "管理员不存在");
            }

            // 不返回密码
            admin.setPassword(null);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", admin);
        } catch (Exception e) {
            logger.error("查询管理员信息失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    // ========== 用户管理 ==========

    @Override
    public ResponseMessage updateUserStatus(String account, int status) {
        try {
            if (account == null || account.isEmpty()) {
                return new ResponseMessage<>(ResponseConstant.error, "账号不能为空");
            }

            if (status != 0 && status != 1) {
                return new ResponseMessage<>(ResponseConstant.error, "状态值无效（0-禁用，1-正常）");
            }

            User user = userMapper.selectByAccount(account);
            if (user == null) {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在");
            }

            int result = userMapper.updateUserStatus(account, status);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "状态更新成功", status);
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "状态更新失败");
            }
        } catch (Exception e) {
            logger.error("更新用户状态失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<PageResponse<User>> getAllUsers(int pageNum, int pageSize) {
        try {
            if (pageNum < 1) pageNum = 1;
            if (pageSize < 1) pageSize = 10;

            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;
            
            // 查询用户总数
            long total = userMapper.countAllUsers();
            
            // 分页查询用户列表
            List<User> users = userMapper.selectAllWithPagination(offset, pageSize);
            
            // 创建分页响应对象
            PageResponse<User> pageResponse = new PageResponse<>(users, total, pageNum, pageSize);
            
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", pageResponse);
        } catch (Exception e) {
            logger.error("查询用户列表失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }
    
    @Override
    public ResponseMessage updateUser(User user) {
        try {
            if (user == null || user.getAccount() == null || user.getAccount().isEmpty()) {
                return new ResponseMessage<>(ResponseConstant.error, "用户信息不能为空");
            }
            
            // 检查用户是否存在
            User existingUser = userMapper.selectByAccount(user.getAccount());
            if (existingUser == null) {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在");
            }
            
            // 更新用户信息
            int result = userMapper.updateUserAll(user);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "用户信息更新成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "用户信息更新失败");
            }
        } catch (Exception e) {
            logger.error("更新用户信息失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }
    
    @Override
    public ResponseMessage deleteUser(String account) {
        try {
            if (account == null || account.isEmpty()) {
                return new ResponseMessage<>(ResponseConstant.error, "账号不能为空");
            }
            
            // 检查用户是否存在
            User existingUser = userMapper.selectByAccount(account);
            if (existingUser == null) {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在");
            }
            
            // 删除用户
            int result = userMapper.deleteByAccount(account);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "用户删除成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "用户删除失败");
            }
        } catch (Exception e) {
            logger.error("删除用户失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<User> getUserByAccount(String account) {
        try {
            if (account == null || account.isEmpty()) {
                return new ResponseMessage<>(ResponseConstant.error, "账号不能为空");
            }

            User user = userMapper.selectByAccount(account);
            if (user == null) {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在");
            }

            // 不返回密码
            user.setPassword(null);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", user);
        } catch (Exception e) {
            logger.error("查询用户信息失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<PageResponse<User>> searchUsers(String keyword, int pageNum, int pageSize) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return new ResponseMessage<>(ResponseConstant.error, "搜索关键词不能为空");
            }

            if (pageNum < 1) pageNum = 1;
            if (pageSize < 1) pageSize = 10;

            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;
            
            // 搜索用户
            List<User> users = userMapper.searchUsers(keyword.trim(), offset, pageSize);
            
            // 查询总数（这里需要添加一个count方法到UserMapper）
            long total = userMapper.countSearchUsers(keyword.trim());
            
            // 创建分页响应对象
            PageResponse<User> pageResponse = new PageResponse<>(users, total, pageNum, pageSize);
            
            return new ResponseMessage<>(ResponseConstant.success, "搜索成功", pageResponse);
        } catch (Exception e) {
            logger.error("搜索用户失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }
}
