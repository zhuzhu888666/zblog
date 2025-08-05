package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.ztzhome.zblog.entity.Bean.SongPlayListSong;
import xyz.ztzhome.zblog.entity.VO.SongVO;

import java.util.List;

@Mapper
public interface SongPlayListSongMapper {
    
    /**
     * 添加歌曲到歌单
     */
    int insertPlayListSong(SongPlayListSong playListSong);
    
    /**
     * 从歌单中移除歌曲
     */
    int deletePlayListSong(@Param("playListId") long playListId, @Param("songId") long songId);
    
    /**
     * 检查歌曲是否在歌单中
     */
    boolean isSongInPlayList(@Param("playListId") long playListId, @Param("songId") long songId);
    
    /**
     * 根据歌单ID查询歌曲列表
     */
    List<SongVO> selectSongsByPlayListId(@Param("playListId") long playListId);
    
    /**
     * 根据歌单ID分页查询歌曲列表
     */
    List<SongVO> selectSongsByPlayListIdWithPage(@Param("playListId") long playListId,
                                                  @Param("offset") int offset,
                                                  @Param("limit") int limit);
    
    /**
     * 查询歌单中歌曲数量
     */
    int countSongsByPlayListId(@Param("playListId") long playListId);
    
    /**
     * 根据歌单ID删除所有歌曲关联
     */
    int deleteAllSongsByPlayListId(@Param("playListId") long playListId);
    
    /**
     * 更新歌曲在歌单中的排序
     */
    int updateSongOrder(@Param("playListId") long playListId, 
                        @Param("songId") long songId, 
                        @Param("sortOrder") int sortOrder);
    
    /**
     * 获取歌单中歌曲的最大排序号
     */
    int getMaxSortOrder(@Param("playListId") long playListId);
}