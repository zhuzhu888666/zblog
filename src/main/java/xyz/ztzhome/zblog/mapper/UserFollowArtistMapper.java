package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.ztzhome.zblog.entity.Bean.Artist;
import xyz.ztzhome.zblog.entity.Bean.UserFollowArtist;

import java.util.List;

@Mapper
public interface UserFollowArtistMapper {
    
    /**
     * 关注艺术家
     */
    int insertUserFollowArtist(UserFollowArtist userFollowArtist);
    
    /**
     * 取消关注艺术家
     */
    int deleteUserFollowArtist(@Param("userId") long userId, @Param("artistId") long artistId);
    
    /**
     * 检查用户是否已关注该艺术家
     */
    boolean isUserFollowArtist(@Param("userId") long userId, @Param("artistId") long artistId);
    
    /**
     * 根据用户ID查询关注的艺术家列表
     */
    List<Artist> selectFollowArtistsByUserId(@Param("userId") long userId);
    
    /**
     * 根据用户ID分页查询关注的艺术家列表
     */
    List<Artist> selectFollowArtistsByUserIdWithPage(@Param("userId") long userId, 
                                                     @Param("offset") int offset, 
                                                     @Param("limit") int limit);
    
    /**
     * 查询用户关注艺术家总数
     */
    int countFollowArtistsByUserId(@Param("userId") long userId);
    
    /**
     * 根据艺术家ID查询关注该艺术家的用户数量（粉丝数）
     */
    int countUsersByArtistId(@Param("artistId") long artistId);
}