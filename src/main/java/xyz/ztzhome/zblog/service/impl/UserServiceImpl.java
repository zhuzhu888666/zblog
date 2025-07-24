package xyz.ztzhome.zblog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.mapper.UserMapper;
import xyz.ztzhome.zblog.service.IUserService;
import xyz.ztzhome.zblog.util.BCryptPassword;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.util.JwtToken;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public int register(User user) {
        // 检查用户是否已存在
        if (userMapper.existsByAccount(user.getAccount())) {
            return -1; // -1 表示用户已存在
        }
        if (userMapper.existsByEmail(user.getEmail())) {
            return -2; // -2 表示邮箱已存在
        }
        // 对密码进行加密
        user.setPassword(BCryptPassword.encode(user.getPassword()));
        // 插入用户
        return userMapper.insertUser(user);
    }

    @Override
    public ResponseMessage login(String account, String password) {
        User user = userMapper.selectByAccount(account);
        if (user == null) {
            return new ResponseMessage<>(ResponseConstant.error, "用户不存在");
        }
        if (BCryptPassword.matches(password, user.getPassword())) {
            String token = JwtToken.generateToken(user.getAccount());
            return new ResponseMessage<>(ResponseConstant.success, "登录成功", token);
        } else {
            return new ResponseMessage<>(ResponseConstant.error, "密码错误");
        }
    }

    @Override
    public ResponseMessage loginByEmail(String email, String password) {
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            return new ResponseMessage<>(ResponseConstant.error, "用户不存在");
        }
        if (BCryptPassword.matches(password, user.getPassword())) {
            String token = JwtToken.generateToken(user.getAccount());
            return new ResponseMessage<>(ResponseConstant.success, "登录成功", token);
        } else {
            return new ResponseMessage<>(ResponseConstant.error, "密码错误");
        }
    }
}
