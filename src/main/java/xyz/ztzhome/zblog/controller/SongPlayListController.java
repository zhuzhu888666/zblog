package xyz.ztzhome.zblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.DTO.AddSongToPlayListDTO;
import xyz.ztzhome.zblog.entity.DTO.CreatePlayListDTO;
import xyz.ztzhome.zblog.entity.DTO.FavoritePlayListDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdatePlayListDTO;
import xyz.ztzhome.zblog.entity.VO.PlayListDetailVO;
import xyz.ztzhome.zblog.entity.VO.PlayListVO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.ISongPlayListService;

import java.util.List;

@RestController
@RequestMapping("/api/playlist")
public class SongPlayListController {

    @Autowired
    private ISongPlayListService songPlayListService;
    // ========== 歌单基本操作 ==========

    /**
     * 创建歌单
     */
    @PostMapping
    public ResponseMessage createPlayList(@RequestPart("data") String data, 
                                         @RequestPart(value = "coverFile", required = false) MultipartFile coverFile) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            CreatePlayListDTO createPlayListDTO = mapper.readValue(data, CreatePlayListDTO.class);
            return songPlayListService.createPlayList(createPlayListDTO, coverFile);
        } catch (Exception e) {
            return new ResponseMessage<>(ResponseConstant.error, "接收对象构建失败:" + e.getMessage());
        }
    }

    /**
     * 根据ID获取歌单详情
     */
    @GetMapping("/detail")
    public ResponseMessage<PlayListDetailVO> getPlayListDetail(@RequestParam("id") long playListId) {
        return songPlayListService.getPlayListDetail(playListId);
    }

    /**
     * 根据ID获取歌单基本信息
     */
    @GetMapping("/info")
    public ResponseMessage<PlayListVO> getPlayList(@RequestParam("id") long playListId) {
        return songPlayListService.getPlayList(playListId);
    }

    /**
     * 更新歌单信息
     */
    @PostMapping("/update")
    public ResponseMessage updatePlayList(@RequestPart("data") String data,
                                         @RequestPart(value = "coverFile", required = false) MultipartFile coverFile) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            UpdatePlayListDTO updatePlayListDTO = mapper.readValue(data, UpdatePlayListDTO.class);
            return songPlayListService.updatePlayList(updatePlayListDTO, coverFile);
        } catch (Exception e) {
            return new ResponseMessage<>(ResponseConstant.error, "接收对象构建失败:" + e.getMessage());
        }
    }

    /**
     * 删除歌单
     */
    @PostMapping("/delete")
    public ResponseMessage deletePlayList(@RequestParam("playListId") long playListId,
                                         @RequestParam("userId") long userId) {
        return songPlayListService.deletePlayList(playListId, userId);
    }

    // ========== 歌单歌曲管理 ==========

    /**
     * 添加歌曲到歌单
     */
    @PostMapping("/songs")
    public ResponseMessage addSongToPlayList(@RequestBody AddSongToPlayListDTO addSongToPlayListDTO) {
        return songPlayListService.addSongToPlayList(addSongToPlayListDTO);
    }

    /**
     * 从歌单中移除歌曲
     */
    @PostMapping("/songs/remove")
    public ResponseMessage removeSongFromPlayList(@RequestParam("playListId") long playListId,
                                                 @RequestParam("songId") long songId) {
        return songPlayListService.removeSongFromPlayList(playListId, songId);
    }

    /**
     * 获取歌单中的歌曲列表
     */
    @GetMapping("/songs")
    public ResponseMessage<List<SongVO>> getPlayListSongs(@RequestParam("playListId") long playListId) {
        return songPlayListService.getPlayListSongs(playListId);
    }

    /**
     * 分页获取歌单中的歌曲列表
     */
    @GetMapping("/songs/page")
    public ResponseMessage<PageResponse<SongVO>> getPlayListSongsWithPage(
            @RequestParam("playListId") long playListId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return songPlayListService.getPlayListSongsWithPage(playListId, pageNum, pageSize);
    }

    // ========== 用户歌单查询 ==========

    /**
     * 获取用户创建的歌单列表
     */
    @GetMapping("/user")
    public ResponseMessage<List<PlayListVO>> getUserPlayLists(@RequestParam("userId") long userId) {
        return songPlayListService.getUserPlayLists(userId);
    }

    /**
     * 分页获取用户创建的歌单列表
     */
    @GetMapping("/user/page")
    public ResponseMessage<PageResponse<PlayListVO>> getUserPlayListsWithPage(
            @RequestParam("userId") long userId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return songPlayListService.getUserPlayListsWithPage(userId, pageNum, pageSize);
    }

    // ========== 公开歌单查询 ==========

    /**
     * 根据名称搜索公开歌单
     */
    @GetMapping("/search")
    public ResponseMessage<List<PlayListVO>> searchPublicPlayLists(@RequestParam("name") String name) {
        return songPlayListService.searchPublicPlayLists(name);
    }

    /**
     * 根据名称分页搜索公开歌单
     */
    @GetMapping("/search/page")
    public ResponseMessage<PageResponse<PlayListVO>> searchPublicPlayListsWithPage(
            @RequestParam("name") String name,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return songPlayListService.searchPublicPlayListsWithPage(name, pageNum, pageSize);
    }

    /**
     * 获取热门歌单
     */
    @GetMapping("/hot")
    public ResponseMessage<List<PlayListVO>> getHotPlayLists(
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return songPlayListService.getHotPlayLists(limit);
    }

    // ========== 歌单收藏相关 ==========

    /**
     * 收藏歌单
     */
    @PostMapping("/favorite")
    public ResponseMessage favoritePlayList(@RequestBody FavoritePlayListDTO favoritePlayListDTO) {
        return songPlayListService.favoritePlayList(favoritePlayListDTO.getUserId(), favoritePlayListDTO.getPlayListId());
    }

    /**
     * 取消收藏歌单
     */
    @PostMapping("/unfavorite")
    public ResponseMessage unfavoritePlayList(@RequestBody FavoritePlayListDTO favoritePlayListDTO) {
        return songPlayListService.unfavoritePlayList(favoritePlayListDTO.getUserId(), favoritePlayListDTO.getPlayListId());
    }

    /**
     * 检查用户是否已收藏该歌单
     */
    @GetMapping("/favorite/check")
    public ResponseMessage<Boolean> isUserFavoritePlayList(@RequestParam("userId") long userId,
                                                          @RequestParam("playListId") long playListId) {
        return songPlayListService.isUserFavoritePlayList(userId, playListId);
    }

    /**
     * 获取用户收藏的歌单列表
     */
    @GetMapping("/favorite/user")
    public ResponseMessage<List<PlayListVO>> getUserFavoritePlayLists(@RequestParam("userId") long userId) {
        return songPlayListService.getUserFavoritePlayLists(userId);
    }

    /**
     * 分页获取用户收藏的歌单列表
     */
    @GetMapping("/favorite/user/page")
    public ResponseMessage<PageResponse<PlayListVO>> getUserFavoritePlayListsWithPage(
            @RequestParam("userId") long userId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return songPlayListService.getUserFavoritePlayListsWithPage(userId, pageNum, pageSize);
    }

    /**
     * 获取歌单收藏数量
     */
    @GetMapping("/favorite/count")
    public ResponseMessage<Integer> getPlayListFavoriteCount(@RequestParam("playListId") long playListId) {
        return songPlayListService.getPlayListFavoriteCount(playListId);
    }
}