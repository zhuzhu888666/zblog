package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.ztzhome.zblog.entity.Bean.SongPlayList;
import xyz.ztzhome.zblog.entity.VO.PlayListVO;
import xyz.ztzhome.zblog.entity.VO.PlayListDetailVO;

import java.util.List;

@Mapper
public interface SongPlayListMapper {
    
    /**
     * 创建歌单
     */
    int insertPlayList(SongPlayList playList);
    
    /**
     * 根据ID查询歌单
     */
    SongPlayList selectPlayListById(@Param("id") long id);
    
    /**
     * 根据ID查询歌单详情VO
     */
    PlayListDetailVO selectPlayListDetailById(@Param("id") long id);
    
    /**
     * 根据ID查询歌单VO
     */
    PlayListVO selectPlayListVOById(@Param("id") long id);
    
    /**
     * 更新歌单
     */
    int updatePlayList(SongPlayList playList);
    
    /**
     * 删除歌单
     */
    int deletePlayList(@Param("id") long id);
    
    /**
     * 根据用户ID查询创建的歌单
     */
    List<PlayListVO> selectPlayListsByUserId(@Param("userId") long userId);
    
    /**
     * 根据用户ID分页查询创建的歌单
     */
    List<PlayListVO> selectPlayListsByUserIdWithPage(@Param("userId") long userId,
                                                      @Param("offset") int offset,
                                                      @Param("limit") int limit);
    
    /**
     * 查询用户创建的歌单数量
     */
    int countPlayListsByUserId(@Param("userId") long userId);
    
    /**
     * 根据歌单名称模糊查询公开歌单
     */
    List<PlayListVO> selectPublicPlayListsByNameLike(@Param("name") String name);
    
    /**
     * 根据歌单名称模糊查询公开歌单，分页
     */
    List<PlayListVO> selectPublicPlayListsByNameLikeWithPage(@Param("name") String name,
                                                              @Param("offset") int offset,
                                                              @Param("limit") int limit);
    
    /**
     * 查询公开歌单总数
     */
    int countPublicPlayLists();
    
    /**
     * 查询热门歌单（按收藏数排序）
     */
    List<PlayListVO> selectHotPlayLists(@Param("limit") int limit);
}