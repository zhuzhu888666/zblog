package xyz.ztzhome.zblog.controller;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.ztzhome.zblog.entity.Bean.Admin;
import xyz.ztzhome.zblog.entity.DTO.LoginDTO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.impl.AdminServiceImpl;

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

    @GetMapping("/update/user/status")
    public ResponseMessage updateUserStatus(@RequestParam("status")  int status,
                                            @RequestParam("account")   String account){
        return adminService.updateUserStatus(account,status);
    };
}
