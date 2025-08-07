package xyz.ztzhome.zblog.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.UpdateUserProfileDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateUserSecurityDTO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

public interface IUserService {
    ResponseMessage register(User user);

    ResponseMessage login(String account, String password);

    ResponseMessage loginByEmail(String email, String password);

    ResponseMessage updateUserProfile(UpdateUserProfileDTO updateUserProfileDTO);

    ResponseMessage updateUserSecurity(UpdateUserSecurityDTO securityDTO);
    
    /**
     * 根据用户ID获取用户信息
     */
    ResponseMessage<User> getUserById(long userId);
    
    /**
     * 验证用户是否存在且状态正常
     */
    boolean validateUser(long userId);

    ResponseMessage updateUserAvatar(long id, MultipartFile file);

    ResponseMessage<String> getUserAvatar(long id);
    
    ResponseMessage getUserByAccount(String account);
    
    ResponseMessage getUserByEmail(String email);
    
    ResponseMessage<PageResponse<User>> searchUsers(String keyword, int pageNum, int pageSize);
}
