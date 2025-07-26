package xyz.ztzhome.zblog.service.impl;

import org.springframework.stereotype.Service;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.Admin;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.LoginDTO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.UserMapper;
import xyz.ztzhome.zblog.service.IAdminService;

@Service
public class AdminServiceImpl implements IAdminService {

    private final UserMapper userMapper;

    public AdminServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public ResponseMessage register(Admin admin) {
        return null;
    }

    @Override
    public ResponseMessage adminLogin(LoginDTO loginDTO) {
        return null;
    }

    @Override
    public ResponseMessage updateUserStatus(String account, int status) {

        if (account==null|| account.isEmpty()){
            return new ResponseMessage<>(ResponseConstant.error,"缺少必要参数");
        }
        User user=userMapper.selectByAccount(account);
        if (user==null){
            return new ResponseMessage<>(ResponseConstant.error,"用户不存在");
        }
        userMapper.updateUserStatus(user.getAccount(),status);
        return new ResponseMessage<>(ResponseConstant.success,"更新成功",status);
    }
}
