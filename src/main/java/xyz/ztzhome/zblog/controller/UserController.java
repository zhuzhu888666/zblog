package xyz.ztzhome.zblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.LoginDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateUserProfileDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateUserSecurityDTO;

import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.IUserService;

@RequestMapping("/user")
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
    public ResponseMessage updateUserAvatar(@RequestPart("id")long id,@RequestPart("avatar") MultipartFile file){
        return userService.updateUserAvatar(id,file);
    }

    @PostMapping("/updatePassword")
    public ResponseMessage updateUserPassword(@RequestBody User user){
        return new ResponseMessage(1,"success");
    }
}
