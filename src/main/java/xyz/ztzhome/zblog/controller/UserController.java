package xyz.ztzhome.zblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.AddSongDTO;
import xyz.ztzhome.zblog.entity.DTO.LoginDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateUserProfileDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateUserSecurityDTO;
import xyz.ztzhome.zblog.entity.response.PageResponse;

import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.IUserService;

@RequestMapping("/api/user")
@RestController
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("/register")
    public ResponseMessage register(@RequestBody User user){
        return userService.register(user);
    }

    @PostMapping("/login")
    public ResponseMessage login(@RequestBody LoginDTO loginDTO){
        if (loginDTO.getAccount() != null){
            return userService.login(loginDTO.getAccount(), loginDTO.getPassword());
        }
        else if (loginDTO.getEmail() != null){
            return userService.loginByEmail(loginDTO.getEmail(), loginDTO.getPassword());
        }
        else {
            return new ResponseMessage<>(ResponseConstant.error,"账号或邮箱不能为空！");
        }
    }

    @PostMapping("/update/profile")
    public ResponseMessage updateUserProfile(@RequestBody UpdateUserProfileDTO updateUserProfileDTO) {
        return userService.updateUserProfile(updateUserProfileDTO);
    }

    @PostMapping("/update/security")
    public ResponseMessage updateSecurity(@RequestBody UpdateUserSecurityDTO securityDTO){

        return userService.updateUserSecurity(securityDTO);
    }

    @PostMapping("/updateAvatar")
    public ResponseMessage updateAvatar(@RequestPart("id") String id, @RequestPart("avatar") MultipartFile avatarFile) {
        try {
            long userId = Long.parseLong(id);
            System.out.println("转换后用户id:"+userId);
            return userService.updateUserAvatar(userId, avatarFile);
        } catch (Exception e) {
            return new ResponseMessage<>(ResponseConstant.error, "接收对象构建失败:" + e.getMessage());
        }
    }
    @GetMapping("/getUserAvatar")
    public ResponseMessage getUserAvatar(@RequestParam("id")long id){
        return userService.getUserAvatar(id);
    }

    @PostMapping("/password")
    public ResponseMessage updateUserPassword(@RequestBody User user){
        return new ResponseMessage<>(ResponseConstant.success, "success");
    }
    
    // 根据账号查询用户
    @GetMapping("/by-account")
    public ResponseMessage getUserByAccount(@RequestParam("account") String account) {
        return userService.getUserByAccount(account);
    }
    
    // 根据邮箱查询用户
    @GetMapping("/by-email")
    public ResponseMessage getUserByEmail(@RequestParam("email") String email) {
        return userService.getUserByEmail(email);
    }
    
    // 搜索用户（根据账号或邮箱模糊查询）
    @GetMapping("/search")
    public ResponseMessage<PageResponse<User>> searchUsers(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return userService.searchUsers(keyword, pageNum, pageSize);
    }
}
