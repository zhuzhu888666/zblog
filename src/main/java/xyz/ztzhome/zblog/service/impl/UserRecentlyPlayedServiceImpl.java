package xyz.ztzhome.zblog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.ztzhome.zblog.constant.PathCosntant;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.UserRecentlyPlayed;
import xyz.ztzhome.zblog.entity.DTO.RecentlyPlayedDTO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.SongMapper;
import xyz.ztzhome.zblog.mapper.UserRecentlyPlayedMapper;
import xyz.ztzhome.zblog.service.IUserRecentlyPlayedService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserRecentlyPlayedServiceImpl implements IUserRecentlyPlayedService {
    
    private static final int MAX_RECENTLY_PLAYED = 200;

    @Autowired
    private UserRecentlyPlayedMapper recentlyPlayedMapper;
    
    @Autowired
    private SongMapper songMapper;

    @Autowired
    private MinioServiceImpl minioService;

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
        
        executeWithDeadlockRetry(() -> recentlyPlayedMapper.upsertRecentlyPlayed(played));
        
        // 检查并清理超出200条的旧记录
        int count = recentlyPlayedMapper.countRecentlyPlayed(recentlyPlayedDTO.getUserId());
        if (count > MAX_RECENTLY_PLAYED) {
            recentlyPlayedMapper.trimKeepNewest(recentlyPlayedDTO.getUserId(), MAX_RECENTLY_PLAYED);
        }
        
        return new ResponseMessage<>(ResponseConstant.success, "添加成功");
    }

    @Override
    public ResponseMessage<List<SongVO>> getRecentlyPlayed(long userId) {
        List<SongVO> songs = recentlyPlayedMapper.selectRecentlyPlayedSongs(userId, MAX_RECENTLY_PLAYED);
        // 填充封面URL（含MinIO存在性校验）
        if (songs != null && !songs.isEmpty()) {
            for (SongVO vo : songs) {
                String coverPath = vo.getCoverPath();
                if (coverPath == null || coverPath.isEmpty() || "default.jpg".equals(coverPath)) {
                    vo.setCoverPath("/files/image/default_cover.jpg");
                } else {
                    String minioPath = PathCosntant.SONG_COVER_PATH + coverPath;
                    String url = minioService.getFileUrl(60 * 24, minioPath);
                    if (url == null || (url.startsWith("getURL_error:"))) {
                        // 文件不存在或生成URL异常，则回退默认封面
                        vo.setCoverPath("/files/image/default_cover.jpg");
                    } else {
                        vo.setCoverPath(url);
                    }
                }
            }
        }
        return new ResponseMessage<>(ResponseConstant.success, "查询成功", songs);
    }

    @Override
    public ResponseMessage syncRecentlySongs(long userId, List<Long> songIds) {
        if (songIds == null || songIds.isEmpty()) {
            return new ResponseMessage<>(ResponseConstant.success, "无需同步");
        }

        // 去重并保序
        LinkedHashSet<Long> distinctIds = new LinkedHashSet<>(songIds);
        // 记录原始顺序下标，用于正确设置 play_time
        Map<Long, Integer> originalIndexBySongId = new HashMap<>();
        int tmpIdx = 0;
        for (Long id : distinctIds) {
            originalIndexBySongId.put(id, tmpIdx++);
        }

        Date now = new Date();
        int index = 0;
        // 固定顺序执行，避免不同事务不同顺序导致意外锁顺序冲突
        List<Long> sortedIds = new ArrayList<>(distinctIds);
        Collections.sort(sortedIds);
        for (Long songId : sortedIds) {
            if (songId == null) continue;
            // 歌曲存在校验
            if (songMapper.selectSongById(songId) == null) {
                continue; // 不存在则跳过
            }
            UserRecentlyPlayed played = new UserRecentlyPlayed();
            played.setUserId(userId);
            played.setSongId(songId);
            // 保持数组后面的时间更新为更近，使用 now 逐步递增毫秒确保顺序
            Integer orig = originalIndexBySongId.get(songId);
            int ordinal = (orig != null ? orig : index);
            played.setPlayTime(new Date(now.getTime() + ordinal));
            executeWithDeadlockRetry(() -> recentlyPlayedMapper.upsertRecentlyPlayed(played));
            index++;
        }

        // 限制上限
        int count = recentlyPlayedMapper.countRecentlyPlayed(userId);
        if (count > MAX_RECENTLY_PLAYED) {
            recentlyPlayedMapper.trimKeepNewest(userId, MAX_RECENTLY_PLAYED);
        }

        return new ResponseMessage<>(ResponseConstant.success, "同步成功");
    }

    @Override
    public ResponseMessage clearAll(long userId) {
        recentlyPlayedMapper.deleteAllByUserId(userId);
        return new ResponseMessage<>(ResponseConstant.success, "清空成功");
    }

    @Override
    public ResponseMessage removeSong(long userId, long songId) {
        int affected = recentlyPlayedMapper.deleteByUserAndSong(userId, songId);
        if (affected > 0) {
            return new ResponseMessage<>(ResponseConstant.success, "移除成功");
        } else {
            return new ResponseMessage<>(ResponseConstant.error, "记录不存在或已移除");
        }
    }
    private static final int DEADLOCK_MAX_RETRY = 3;
    private static final long DEADLOCK_BACKOFF_MS = 30L;

    private void executeWithDeadlockRetry(Runnable action) {
        int attempt = 0;
        while (true) {
            try {
                action.run();
                return;
            } catch (org.springframework.dao.DeadlockLoserDataAccessException e) {
                attempt++;
                if (attempt >= DEADLOCK_MAX_RETRY) {
                    throw e;
                }
                try {
                    Thread.sleep(DEADLOCK_BACKOFF_MS * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
    }
}

