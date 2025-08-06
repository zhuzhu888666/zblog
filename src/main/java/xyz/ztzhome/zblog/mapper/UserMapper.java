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

    // 查询所有用户--分页查询
    List<User> selectAll();
    
    // 分页查询所有用户
    List<User> selectAllWithPagination(@Param("offset") int offset, @Param("pageSize") int pageSize);
    
    // 查询用户总数
    long countAllUsers();
    
    // 搜索用户（根据账号或邮箱模糊查询）
    List<User> searchUsers(@Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);
    
    // 搜索用户总数
    long countSearchUsers(@Param("keyword") String keyword);

    // 插入用户
    int insertUser(User user);

    // 更新用户基本信息
    int updateUserProfile(User user);

    // 更新用户安全信息
    int updateUserSecurity(User user);

    //更新全部信息
    int updateUserAll(User user);
    //更新用户状态
    int updateUserStatus(@Param("account")  String account, @Param("status") int status);

    // 根据ID删除用户
    int deleteById(long id);
    int deleteByAccount(String account);
    //用户是否存在
    boolean existsByAccount(String account);
    boolean existsByEmail(String email);


}