package xyz.ztzhome.zblog.service.impl;

import org.springframework.stereotype.Service;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.Admin;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.LoginDTO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.UserMapper;
import xyz.ztzhome.zblog.service.IAdminService;

import java.util.List;

@Service
public class AdminServiceImpl implements IAdminService {

    private final UserMapper userMapper;

    public AdminServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public ResponseMessage register(Admin admin) {
        return null;
    }

    @Override
    public ResponseMessage adminLogin(LoginDTO loginDTO) {
        return null;
    }

    @Override
    public ResponseMessage updateUserStatus(String account, int status) {

        if (account==null|| account.isEmpty()){
            return new ResponseMessage<>(ResponseConstant.error,"缺少必要参数");
        }
        User user=userMapper.selectByAccount(account);
        if (user==null){
            return new ResponseMessage<>(ResponseConstant.error,"用户不存在");
        }
        userMapper.updateUserStatus(user.getAccount(),status);
        return new ResponseMessage<>(ResponseConstant.success,"更新成功",status);
    }

    @Override
    public ResponseMessage<PageResponse<User>> getAllUsers(int pageNum, int pageSize) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;

        try {
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
            return new ResponseMessage<>(ResponseConstant.error, "查询失败: " + e.getMessage());
        }
    }
    
    @Override
    public ResponseMessage updateUser(User user) {
        if (user == null || user.getAccount() == null || user.getAccount().isEmpty()) {
            return new ResponseMessage<>(ResponseConstant.error, "用户信息不能为空");
        }
        
        try {
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
            return new ResponseMessage<>(ResponseConstant.error, "更新失败: " + e.getMessage());
        }
    }
    
    @Override
    public ResponseMessage deleteUser(String account) {
        if (account == null || account.isEmpty()) {
            return new ResponseMessage<>(ResponseConstant.error, "账号不能为空");
        }
        
        try {
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
            return new ResponseMessage<>(ResponseConstant.error, "删除失败: " + e.getMessage());
        }
    }
}
