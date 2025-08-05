package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.ztzhome.zblog.entity.Bean.UserFavoritePlayList;
import xyz.ztzhome.zblog.entity.VO.PlayListVO;

import java.util.List;

@Mapper
public interface UserFavoritePlayListMapper {
    
    /**
     * 收藏歌单
     */
    int insertUserFavoritePlayList(UserFavoritePlayList userFavoritePlayList);
    
    /**
     * 取消收藏歌单
     */
    int deleteUserFavoritePlayList(@Param("userId") long userId, @Param("playListId") long playListId);
    
    /**
     * 检查用户是否已收藏该歌单
     */
    boolean isUserFavoritePlayList(@Param("userId") long userId, @Param("playListId") long playListId);
    
    /**
     * 根据用户ID查询收藏的歌单列表
     */
    List<PlayListVO> selectFavoritePlayListsByUserId(@Param("userId") long userId);
    
    /**
     * 根据用户ID分页查询收藏的歌单列表
     */
    List<PlayListVO> selectFavoritePlayListsByUserIdWithPage(@Param("userId") long userId,
                                                              @Param("offset") int offset,
                                                              @Param("limit") int limit);
    
    /**
     * 查询用户收藏歌单总数
     */
    int countFavoritePlayListsByUserId(@Param("userId") long userId);
    
    /**
     * 根据歌单ID查询收藏该歌单的用户数量
     */
    int countUsersByPlayListId(@Param("playListId") long playListId);
}