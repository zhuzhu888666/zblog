package xyz.ztzhome.zblog.service;

import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

public interface IUserService {
    int register(User user);

    ResponseMessage login(String account, String password);

    ResponseMessage loginByEmail(String email, String password);
}
