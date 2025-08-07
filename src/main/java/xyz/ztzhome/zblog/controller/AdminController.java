package xyz.ztzhome.zblog.controller;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.ztzhome.zblog.entity.Bean.Admin;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.LoginDTO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.impl.AdminServiceImpl;

import java.util.List;

@RequestMapping("/admin")
@RestController
public class AdminController {

    @Autowired
    AdminServiceImpl adminService;

    @PostMapping("/register")
    public ResponseMessage register(@RequestBody Admin admin){
        return adminService.register(admin);
    };

    @PostMapping("/login")
    public ResponseMessage adminLogin(@RequestBody LoginDTO loginDTO){
        return adminService.adminLogin(loginDTO);
    };

    @GetMapping("/user/update/status")
    public ResponseMessage updateUserStatus(@RequestParam("status")  int status,
                                            @RequestParam("account")   String account){
        return adminService.updateUserStatus(account,status);
    };
    //获取全部用户信息
    @GetMapping("/getAllUsers")
    public ResponseMessage<PageResponse<User>> selectAllUsers(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        return adminService.getAllUsers(pageNum,pageSize);
    }
    //更新用户信息
    @PostMapping("/update/updateUser")
    public ResponseMessage updateUser(@RequestBody User user){
        return adminService.updateUser(user);
    }
    //删除用户
   @PostMapping("/delete/deleteUser")
    public ResponseMessage deleteUser(@RequestParam("account") String account){
        return adminService.deleteUser(account);
    }
}
