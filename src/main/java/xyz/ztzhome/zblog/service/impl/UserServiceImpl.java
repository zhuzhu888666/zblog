package xyz.ztzhome.zblog.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.constant.PathCosntant;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.UpdateUserProfileDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateUserSecurityDTO;
import xyz.ztzhome.zblog.entity.VO.UserLoginVO;
import xyz.ztzhome.zblog.mapper.UserMapper;
import xyz.ztzhome.zblog.service.IUserService;
import xyz.ztzhome.zblog.util.BCryptPassword;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.util.FileTypeUtil;
import xyz.ztzhome.zblog.util.JwtToken;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    MinioServiceImpl minioService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public ResponseMessage register(User user) {
        // 检查用户是否已存在
        if (userMapper.existsByAccount(user.getAccount())) {
            return new ResponseMessage<>(ResponseConstant.error,"用户已存在");
        }
        if (userMapper.existsByEmail(user.getEmail())) {
            return new ResponseMessage<>(ResponseConstant.error,"该邮箱已被其他账号绑定"); // -2 表示邮箱已存在
        }
        // 对密码进行加密
        user.setPassword(BCryptPassword.encode(user.getPassword()));
        // 插入用户，并判断是否注册成功
       if ( userMapper.insertUser(user)<=0){
           return new ResponseMessage<>(ResponseConstant.error,"服务异常");
       }
        return new ResponseMessage<>(ResponseConstant.success,"注册成功");
    }

    @Override
    public ResponseMessage login(String account, String password) {
        User user = userMapper.selectByAccount(account);
        if (user == null) {
            logger.warn("登录尝试失败：用户账号不存在 {}", account);
            return new ResponseMessage<>(ResponseConstant.error, "用户不存在");
        }
        if (BCryptPassword.matches(password, user.getPassword())) {
            String token = JwtToken.generateToken(user.getAccount());
            logger.info("用户登录成功：{}", account);
            // Store token and user info in Redis for 7 days
            redisTemplate.opsForValue().set("user:token:" + token, user, 7, TimeUnit.DAYS);
            UserLoginVO userLoginVO = new UserLoginVO();
            BeanUtils.copyProperties(user, userLoginVO);
            userLoginVO.setToken(token);
            return new ResponseMessage<>(ResponseConstant.success, "登录成功", userLoginVO);
        } else {
            logger.warn("登录尝试失败：用户密码错误 {}", account);
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
            logger.info("用户通过邮箱登录成功：{}", email);
            // Store token and user info in Redis for 7 days
            redisTemplate.opsForValue().set("user:token:" + token, user, 7, TimeUnit.DAYS);
            UserLoginVO userLoginVO = new UserLoginVO();
            BeanUtils.copyProperties(user, userLoginVO);
            userLoginVO.setToken(token);
            return new ResponseMessage<>(ResponseConstant.success, "登录成功", userLoginVO);
        } else {
            return new ResponseMessage<>(ResponseConstant.error, "密码错误");
        }
    }

    @Override
    public ResponseMessage updateUserProfile(UpdateUserProfileDTO updateUserProfileDTO) {
        if(updateUserProfileDTO==null||updateUserProfileDTO.getAccount()==null){
            return new ResponseMessage<>(ResponseConstant.error,"缺少必要字段");
        }
        User user = new User();
        user.setAccount(updateUserProfileDTO.getAccount());
        user.setNickname(updateUserProfileDTO.getNickname());
        user.setAge(updateUserProfileDTO.getAge());
        user.setGender(updateUserProfileDTO.getGender());
        user.setAddress(updateUserProfileDTO.getAddress());
        user.setSignature(updateUserProfileDTO.getSignature());
        user.setPhone(updateUserProfileDTO.getPhone());
        int result=userMapper.updateUserProfile(user);
        if (result>0){
            return new ResponseMessage<>(ResponseConstant.success,"更新成功");
        }
        return new ResponseMessage<>(ResponseConstant.error,"服务异常");
    }

    @Override
    public ResponseMessage updateUserSecurity(UpdateUserSecurityDTO securityDTO) {
        if(securityDTO==null||securityDTO.getAccount()==null){
            return new ResponseMessage<>(ResponseConstant.error,"缺少必要字段");
        }

        logger.info("开始更新用户安全信息，账号：{}", securityDTO.getAccount());
        try {
            if (userMapper.existsByEmail(securityDTO.getEmail())) {
                logger.warn("更新邮箱失败：邮箱 {} 已被绑定，操作账号：{}", securityDTO.getEmail(), securityDTO.getAccount());
                return new ResponseMessage<>(ResponseConstant.error,"该邮箱已被其他账号绑定");
            }

            User user = new User();
            user.setAccount(securityDTO.getAccount());

            if(securityDTO.getEmail()!=null&& !securityDTO.getEmail().isEmpty()){
                user.setEmail(securityDTO.getEmail());
            }
            if (securityDTO.getPassword()!=null&& !securityDTO.getPassword().isEmpty()) {
                user.setPassword(BCryptPassword.encode(securityDTO.getPassword()));
            }

            int result=userMapper.updateUserSecurity(user);
            if(result>0){
                logger.info("用户安全信息更新成功，账号：{}", securityDTO.getAccount());
                return new ResponseMessage<>(ResponseConstant.success,"更新成功");
            }
            return new ResponseMessage<>(ResponseConstant.error,"服务异常");
        } catch (Exception e) {
            logger.error("更新用户安全信息时发生未知异常，账号：{}", securityDTO.getAccount(), e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常，请联系管理员");
        }
    }

    @Override
    public ResponseMessage<User> getUserById(long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user != null && user.getStatus() == 1) {
                return new ResponseMessage<>(ResponseConstant.success, "查询成功", user);
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在或已被禁用");
            }
        } catch (Exception e) {
            logger.error("查询用户信息失败，用户ID：{}", userId, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public boolean validateUser(long userId) {
        try {
            User user = userMapper.selectById(userId);
            return user != null && user.getStatus() == 1;
        } catch (Exception e) {
            logger.error("验证用户失败，用户ID：{}", userId, e);
            return false;
        }
    }

    @Override
    public ResponseMessage updateUserAvatar(long id, MultipartFile file) {
        if (file == null) {
            return new ResponseMessage<>(ResponseConstant.error,"上传文件不能为空");
        }
        User user= userMapper.selectById(id);
        if (user == null) {
            return new ResponseMessage<>(ResponseConstant.error,"用户不存在！");
        }
        String fileName = file.getOriginalFilename();
        if(!FileTypeUtil.getFileType(fileName).equals("image")){
            return new ResponseMessage<>(ResponseConstant.error,"暂不支持该文件类型");
        }
        try {
            if(user.getUserAvatar()!=null){
                minioService.deleteFile(PathCosntant.USER_Avatar+user.getAccount());
            }
            String path= PathCosntant.USER_Avatar+user.getId()+ FileTypeUtil.getFileExtension2(fileName);
            minioService.uploadFile(file,path);
            return new ResponseMessage<>(ResponseConstant.success,"更新成功！");
        }catch (Exception e){
            logger.error("更新用户头像失败{}",user.getId(),e);
            return new ResponseMessage<>(ResponseConstant.error,e.getMessage());
        }
    }
}
