package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.ztzhome.zblog.entity.Bean.User;

import java.util.List;

@Mapper
public interface UserMapper {
    // 根据ID查询用户
    User selectById(long id);
    User selectByAccount(String account);
    User selectByEmail(String email);

    // 查询所有用户
    List<User> selectAll();

    // 插入用户
    int insertUser(User user);

    // 更新用户基本信息
    int updateUserProfile(User user);

    // 更新用户安全信息
    int updateUserSecurity(User user);

    // 根据ID删除用户
    int deleteById(String id);
    int deleteByAccount(String account);

    //用户是否存在
    boolean existsByAccount(String account);
    boolean existsByEmail(String email);

}