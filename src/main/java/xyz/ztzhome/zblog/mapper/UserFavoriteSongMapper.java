package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.ztzhome.zblog.entity.Bean.UserFavoriteSong;
import xyz.ztzhome.zblog.entity.VO.SongVO;

import java.util.List;

@Mapper
public interface UserFavoriteSongMapper {
    
    /**
     * 添加收藏歌曲
     */
    int insertUserFavoriteSong(UserFavoriteSong userFavoriteSong);
    
    /**
     * 取消收藏歌曲
     */
    int deleteUserFavoriteSong(@Param("userId") long userId, @Param("songId") long songId);
    
    /**
     * 检查用户是否已收藏该歌曲
     */
    boolean isUserFavoriteSong(@Param("userId") long userId, @Param("songId") long songId);
    
    /**
     * 根据用户ID查询收藏的歌曲列表
     */
    List<SongVO> selectFavoriteSongsByUserId(@Param("userId") long userId);
    
    /**
     * 根据用户ID分页查询收藏的歌曲列表
     */
    List<SongVO> selectFavoriteSongsByUserIdWithPage(@Param("userId") long userId, 
                                                     @Param("offset") int offset, 
                                                     @Param("limit") int limit);
    
    /**
     * 查询用户收藏歌曲总数
     */
    int countFavoriteSongsByUserId(@Param("userId") long userId);
    
    /**
     * 根据歌曲ID查询收藏该歌曲的用户数量
     */
    int countUsersBySongId(@Param("songId") long songId);
}