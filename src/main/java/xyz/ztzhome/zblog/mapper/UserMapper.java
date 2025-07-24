package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
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

    // 更新用户信息（动态更新）
    int updateUser(User user);

    // 根据ID删除用户
    int deleteById(String id);
    int deleteByAccount(String account);

    //用户是否存在
    boolean existsByAccount(String account);
    boolean existsByEmail(String email);

}