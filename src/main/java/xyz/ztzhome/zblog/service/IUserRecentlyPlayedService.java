package xyz.ztzhome.zblog.service;

import xyz.ztzhome.zblog.entity.DTO.RecentlyPlayedDTO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import java.util.List;

public interface IUserRecentlyPlayedService {
    /**
     * 添加播放记录
     */
    ResponseMessage addRecentlyPlayed(RecentlyPlayedDTO recentlyPlayedDTO);

    /**
     * 获取用户最近播放列表
     */
    ResponseMessage<List<SongVO>> getRecentlyPlayed(long userId);

    /**
     * 同步用户最近播放歌曲（批量）
     */
    ResponseMessage syncRecentlySongs(long userId, List<Long> songIds);

    /**
     * 清空用户最近播放记录
     */
    ResponseMessage clearAll(long userId);

    /**
     * 从最近播放移除指定歌曲
     */
    ResponseMessage removeSong(long userId, long songId);
}

