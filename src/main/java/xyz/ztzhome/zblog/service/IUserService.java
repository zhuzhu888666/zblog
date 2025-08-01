package xyz.ztzhome.zblog.service;

import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.UpdateUserProfileDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateUserSecurityDTO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

public interface IUserService {
    ResponseMessage register(User user);

    ResponseMessage login(String account, String password);

    ResponseMessage loginByEmail(String email, String password);

    ResponseMessage updateUserProfile(UpdateUserProfileDTO updateUserProfileDTO);

    ResponseMessage updateUserSecurity(UpdateUserSecurityDTO securityDTO);
}
