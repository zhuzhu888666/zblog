package xyz.ztzhome.zblog.service;

import xyz.ztzhome.zblog.entity.Bean.Admin;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.LoginDTO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

public interface IAdminService {

    ResponseMessage register(Admin admin);

    ResponseMessage adminLogin(LoginDTO loginDTO);

    ResponseMessage updateUserStatus(String account, int status);

    ResponseMessage<PageResponse<User>> getAllUsers(int pageNum, int pageSize);
    
    ResponseMessage updateUser(User user);
    
    ResponseMessage deleteUser(String account);
}
