package xyz.ztzhome.zblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.IUserService;

@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("/register")
    public ResponseMessage register(@RequestBody User user){
        int result = userService.register(user);
        if (result == 1) {
            return new ResponseMessage<>(ResponseConstant.success, "注册成功");
        } else if (result == -1) {
            return new ResponseMessage<>(ResponseConstant.error, "用户已存在");
        } else if (result == -2) {
            return new ResponseMessage<>(ResponseConstant.error, "邮箱已注册");
        } else {
            return new ResponseMessage<>(ResponseConstant.error, "注册失败");
        }
    }

    @PostMapping("/login")
    public ResponseMessage login(@RequestParam(required = false) String account,
                                   @RequestParam(required = false) String email,
                                   @RequestParam String password){
        if (account != null && !account.isEmpty()){
            return userService.login(account,password);
        }
        else if (email != null && !email.isEmpty()){
            return userService.loginByEmail(email,password);
        }
        else {
            return new ResponseMessage<>(ResponseConstant.error,"账号或邮箱不能为空！");
        }
    }

    @PostMapping("/update")
    public ResponseMessage updateUser(@RequestBody User user){
        return new ResponseMessage(1,"success");
    }

    @PostMapping("/updateAvatar")
    public ResponseMessage updateUserAvatar(@RequestBody User user){
        return new ResponseMessage(1,"success");
    }

    @PostMapping("/updatePassword")
    public ResponseMessage updateUserPassword(@RequestBody User user){
        return new ResponseMessage(1,"success");
    }
}
