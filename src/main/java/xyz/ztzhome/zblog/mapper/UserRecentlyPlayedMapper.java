package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.ztzhome.zblog.entity.Bean.UserRecentlyPlayed;
import xyz.ztzhome.zblog.entity.VO.SongVO;

import java.util.List;

@Mapper
public interface UserRecentlyPlayedMapper {
    /**
     * 插入或更新播放记录
     */
    void upsertRecentlyPlayed(UserRecentlyPlayed recentlyPlayed);
    
    /**
     * 查询用户最近播放的歌曲
     */
    List<SongVO> selectRecentlyPlayedSongs(@Param("userId") long userId, @Param("limit") int limit);
    
    /**
     * 查询用户播放记录总数
     */
    int countRecentlyPlayed(@Param("userId") long userId);
    
    /**
     * 查询用户最早的播放记录
     */
    List<UserRecentlyPlayed> selectOldestPlayed(@Param("userId") long userId, @Param("limit") int limit);
    
    /**
     * 删除指定的播放记录
     */
    void deleteRecentlyPlayed(@Param("ids") List<Long> ids);

    /**
     * 删除多余的播放记录，仅保留该用户最近的 keep 条（按 play_time DESC）
     */
    void trimKeepNewest(@Param("userId") long userId, @Param("keep") int keep);

    /**
     * 清空用户的最近播放记录
     */
    void deleteAllByUserId(@Param("userId") long userId);

    /**
     * 从用户最近播放中移除指定歌曲
     * @return 受影响行数
     */
    int deleteByUserAndSong(@Param("userId") long userId, @Param("songId") long songId);
}

