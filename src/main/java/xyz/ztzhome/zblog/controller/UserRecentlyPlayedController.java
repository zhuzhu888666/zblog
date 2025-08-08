package xyz.ztzhome.zblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.ztzhome.zblog.entity.DTO.RecentlyPlayedDTO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.IUserRecentlyPlayedService;

import java.util.List;

@RestController
@RequestMapping("/user/recently-played")
public class UserRecentlyPlayedController {

    @Autowired
    private IUserRecentlyPlayedService recentlyPlayedService;

    /**
     * 添加最近播放记录
     */
    @PostMapping("/add")
    public ResponseMessage addRecentlyPlayed(@RequestBody RecentlyPlayedDTO recentlyPlayedDTO) {
        return recentlyPlayedService.addRecentlyPlayed(recentlyPlayedDTO);
    }
    
    /**
     * 获取最近播放列表
     */
    @GetMapping("/getRecentlyPlayed")
    public ResponseMessage<List<SongVO>> getRecentlyPlayed(@RequestParam("userId") long userId) {
        return recentlyPlayedService.getRecentlyPlayed(userId);
    }
    // 同步最近播放列表（前端已去重并仅传未在数据库的歌曲ID数组）
    @PostMapping("/sync")
    public ResponseMessage syncRecentlyPlayed(@RequestParam("userId") long userId,
                                              @RequestBody List<Long> songIds) {
        return recentlyPlayedService.syncRecentlySongs(userId, songIds);
    }
    /**
     * 清空用户最近播放记录
     */
    @PostMapping("/clear")
    public ResponseMessage clearAll(@RequestParam("userId") long userId) {
        return recentlyPlayedService.clearAll(userId);
    }

    /**
     * 从最近播放移除指定歌曲
     */
    @PostMapping("/remove")
    public ResponseMessage removeSong(@RequestParam("userId") long userId,
                                      @RequestParam("songId") long songId) {
        return recentlyPlayedService.removeSong(userId, songId);
    }
}


