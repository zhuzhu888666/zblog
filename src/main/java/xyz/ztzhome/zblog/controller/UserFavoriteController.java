package xyz.ztzhome.zblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.ztzhome.zblog.entity.Bean.Artist;
import xyz.ztzhome.zblog.entity.DTO.FavoriteDTO;
import xyz.ztzhome.zblog.entity.DTO.FavoritePlayListDTO;
import xyz.ztzhome.zblog.entity.DTO.FollowDTO;
import xyz.ztzhome.zblog.entity.VO.PlayListVO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.IUserFavoriteService;

import java.util.List;

@RestController
@RequestMapping("/api/users/favorites")
public class UserFavoriteController {

    @Autowired
    private IUserFavoriteService userFavoriteService;

    // ========== 收藏歌曲相关接口 ==========

    /**
     * 收藏歌曲
     */
    @PostMapping("/songs")
    public ResponseMessage favoriteSong(@RequestBody FavoriteDTO favoriteDTO) {
        return userFavoriteService.favoriteSong(favoriteDTO.getUserId(), favoriteDTO.getSongId());
    }

    /**
     * 取消收藏歌曲
     */
    @PostMapping("/songs/unfavorite")
    public ResponseMessage unfavoriteSong(@RequestBody FavoriteDTO favoriteDTO) {
        return userFavoriteService.unfavoriteSong(favoriteDTO.getUserId(), favoriteDTO.getSongId());
    }

    /**
     * 检查用户是否已收藏该歌曲
     */
    @GetMapping("/songs/check")
    public ResponseMessage<Boolean> isUserFavoriteSong(@RequestParam("userId") long userId, 
                                                      @RequestParam("songId") long songId) {
        return userFavoriteService.isUserFavoriteSong(userId, songId);
    }

    /**
     * 获取用户收藏的歌曲列表
     */
    @GetMapping("/songs/list")
    public ResponseMessage<List<SongVO>> getUserFavoriteSongs(@RequestParam("userId") long userId) {
        return userFavoriteService.getUserFavoriteSongs(userId);
    }

    /**
     * 分页获取用户收藏的歌曲列表
     */
    @GetMapping("/songs/page")
    public ResponseMessage<PageResponse<SongVO>> getUserFavoriteSongsWithPage(
            @RequestParam("userId") long userId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return userFavoriteService.getUserFavoriteSongsWithPage(userId, pageNum, pageSize);
    }

    /**
     * 获取歌曲被收藏的次数
     */
    @GetMapping("/songs/count")
    public ResponseMessage<Integer> getSongFavoriteCount(@RequestParam("songId") long songId) {
        return userFavoriteService.getSongFavoriteCount(songId);
    }

    // ========== 关注艺术家相关接口 ==========

    /**
     * 关注艺术家
     */
    @PostMapping("/artists")
    public ResponseMessage followArtist(@RequestBody FollowDTO followDTO) {
        return userFavoriteService.followArtist(followDTO.getUserId(), followDTO.getArtistId());
    }

    /**
     * 取消关注艺术家
     */
    @PostMapping("/artists/unfollow")
    public ResponseMessage unfollowArtist(@RequestBody FollowDTO followDTO) {
        return userFavoriteService.unfollowArtist(followDTO.getUserId(), followDTO.getArtistId());
    }

    /**
     * 检查用户是否已关注该艺术家
     */
    @GetMapping("/artists/check")
    public ResponseMessage<Boolean> isUserFollowArtist(@RequestParam("userId") long userId, 
                                                      @RequestParam("artistId") long artistId) {
        return userFavoriteService.isUserFollowArtist(userId, artistId);
    }

    /**
     * 获取用户关注的艺术家列表
     */
    @GetMapping("/artists/list")
    public ResponseMessage<List<Artist>> getUserFollowArtists(@RequestParam("userId") long userId) {
        return userFavoriteService.getUserFollowArtists(userId);
    }

    /**
     * 分页获取用户关注的艺术家列表
     */
    @GetMapping("/artists/page")
    public ResponseMessage<PageResponse<Artist>> getUserFollowArtistsWithPage(
            @RequestParam("userId") long userId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return userFavoriteService.getUserFollowArtistsWithPage(userId, pageNum, pageSize);
    }

    /**
     * 获取艺术家的粉丝数量
     */
    @GetMapping("/artists/followers")
    public ResponseMessage<Integer> getArtistFollowerCount(@RequestParam("artistId") long artistId) {
        return userFavoriteService.getArtistFollowerCount(artistId);
    }

    // ========== 收藏歌单相关接口 ==========

    /**
     * 收藏歌单
     */
    @PostMapping("/playlists")
    public ResponseMessage favoritePlayList(@RequestBody FavoritePlayListDTO favoritePlayListDTO) {
        return userFavoriteService.favoritePlayList(favoritePlayListDTO.getUserId(), favoritePlayListDTO.getPlayListId());
    }

    /**
     * 取消收藏歌单
     */
    @PostMapping("/playlists/unfavorite")
    public ResponseMessage unfavoritePlayList(@RequestBody FavoritePlayListDTO favoritePlayListDTO) {
        return userFavoriteService.unfavoritePlayList(favoritePlayListDTO.getUserId(), favoritePlayListDTO.getPlayListId());
    }

    /**
     * 检查用户是否已收藏该歌单
     */
    @GetMapping("/playlists/check")
    public ResponseMessage<Boolean> isUserFavoritePlayList(@RequestParam("userId") long userId,
                                                          @RequestParam("playListId") long playListId) {
        return userFavoriteService.isUserFavoritePlayList(userId, playListId);
    }

    /**
     * 获取用户收藏的歌单列表
     */
    @GetMapping("/playlists/list")
    public ResponseMessage<List<PlayListVO>> getUserFavoritePlayLists(@RequestParam("userId") long userId) {
        return userFavoriteService.getUserFavoritePlayLists(userId);
    }

    /**
     * 分页获取用户收藏的歌单列表
     */
    @GetMapping("/playlists/page")
    public ResponseMessage<PageResponse<PlayListVO>> getUserFavoritePlayListsWithPage(
            @RequestParam("userId") long userId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return userFavoriteService.getUserFavoritePlayListsWithPage(userId, pageNum, pageSize);
    }

    /**
     * 获取歌单的收藏数量
     */
    @GetMapping("/playlists/count")
    public ResponseMessage<Integer> getPlayListFavoriteCount(@RequestParam("playListId") long playListId) {
        return userFavoriteService.getPlayListFavoriteCount(playListId);
    }
}