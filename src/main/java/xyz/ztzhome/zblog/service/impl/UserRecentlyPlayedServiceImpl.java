package xyz.ztzhome.zblog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.UserRecentlyPlayed;
import xyz.ztzhome.zblog.entity.DTO.RecentlyPlayedDTO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.SongMapper;
import xyz.ztzhome.zblog.mapper.UserRecentlyPlayedMapper;
import xyz.ztzhome.zblog.service.IUserRecentlyPlayedService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRecentlyPlayedServiceImpl implements IUserRecentlyPlayedService {
    
    private static final int MAX_RECENTLY_PLAYED = 200;

    @Autowired
    private UserRecentlyPlayedMapper recentlyPlayedMapper;
    
    @Autowired
    private SongMapper songMapper;

    @Override
    @Transactional
    public ResponseMessage addRecentlyPlayed(RecentlyPlayedDTO recentlyPlayedDTO) {
        if (songMapper.selectSongById(recentlyPlayedDTO.getSongId()) == null) {
            return new ResponseMessage<>(ResponseConstant.error, "歌曲不存在");
        }
        
        UserRecentlyPlayed played = new UserRecentlyPlayed();
        played.setUserId(recentlyPlayedDTO.getUserId());
        played.setSongId(recentlyPlayedDTO.getSongId());
        played.setPlayTime(new Date());
        
        recentlyPlayedMapper.upsertRecentlyPlayed(played);
        
        // 检查并清理超出200条的旧记录
        int count = recentlyPlayedMapper.countRecentlyPlayed(recentlyPlayedDTO.getUserId());
        if (count > MAX_RECENTLY_PLAYED) {
            int toDeleteCount = count - MAX_RECENTLY_PLAYED;
            List<UserRecentlyPlayed> oldestPlayed = recentlyPlayedMapper.selectOldestPlayed(recentlyPlayedDTO.getUserId(), toDeleteCount);
            List<Long> idsToDelete = oldestPlayed.stream().map(UserRecentlyPlayed::getId).collect(Collectors.toList());
            if (!idsToDelete.isEmpty()) {
                recentlyPlayedMapper.deleteRecentlyPlayed(idsToDelete);
            }
        }
        
        return new ResponseMessage<>(ResponseConstant.success, "添加成功");
    }

    @Override
    public ResponseMessage<List<SongVO>> getRecentlyPlayed(long userId) {
        List<SongVO> songs = recentlyPlayedMapper.selectRecentlyPlayedSongs(userId, MAX_RECENTLY_PLAYED);
        return new ResponseMessage<>(ResponseConstant.success, "查询成功", songs);
    }
}

