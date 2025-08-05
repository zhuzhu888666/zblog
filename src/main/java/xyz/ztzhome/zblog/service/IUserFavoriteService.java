package xyz.ztzhome.zblog.service;

import xyz.ztzhome.zblog.entity.Bean.Artist;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

import java.util.List;

public interface IUserFavoriteService {
    
    // ========== 收藏歌曲相关方法 ==========
    
    /**
     * 收藏歌曲
     */
    ResponseMessage favoriteSong(long userId, long songId);
    
    /**
     * 取消收藏歌曲
     */
    ResponseMessage unfavoriteSong(long userId, long songId);
    
    /**
     * 检查用户是否已收藏该歌曲
     */
    ResponseMessage<Boolean> isUserFavoriteSong(long userId, long songId);
    
    /**
     * 获取用户收藏的歌曲列表
     */
    ResponseMessage<List<SongVO>> getUserFavoriteSongs(long userId);
    
    /**
     * 分页获取用户收藏的歌曲列表
     */
    ResponseMessage<PageResponse<SongVO>> getUserFavoriteSongsWithPage(long userId, int pageNum, int pageSize);
    
    /**
     * 获取歌曲被收藏的次数
     */
    ResponseMessage<Integer> getSongFavoriteCount(long songId);
    
    // ========== 关注艺术家相关方法 ==========
    
    /**
     * 关注艺术家
     */
    ResponseMessage followArtist(long userId, long artistId);
    
    /**
     * 取消关注艺术家
     */
    ResponseMessage unfollowArtist(long userId, long artistId);
    
    /**
     * 检查用户是否已关注该艺术家
     */
    ResponseMessage<Boolean> isUserFollowArtist(long userId, long artistId);
    
    /**
     * 获取用户关注的艺术家列表
     */
    ResponseMessage<List<Artist>> getUserFollowArtists(long userId);
    
    /**
     * 分页获取用户关注的艺术家列表
     */
    ResponseMessage<PageResponse<Artist>> getUserFollowArtistsWithPage(long userId, int pageNum, int pageSize);
    
    /**
     * 获取艺术家的粉丝数量
     */
    ResponseMessage<Integer> getArtistFollowerCount(long artistId);
    
    // ========== 收藏歌单相关方法 ==========
    
    /**
     * 收藏歌单
     */
    ResponseMessage favoritePlayList(long userId, long playListId);
    
    /**
     * 取消收藏歌单
     */
    ResponseMessage unfavoritePlayList(long userId, long playListId);
    
    /**
     * 检查用户是否已收藏该歌单
     */
    ResponseMessage<Boolean> isUserFavoritePlayList(long userId, long playListId);
    
    /**
     * 获取用户收藏的歌单列表
     */
    ResponseMessage<List<xyz.ztzhome.zblog.entity.VO.PlayListVO>> getUserFavoritePlayLists(long userId);
    
    /**
     * 分页获取用户收藏的歌单列表
     */
    ResponseMessage<PageResponse<xyz.ztzhome.zblog.entity.VO.PlayListVO>> getUserFavoritePlayListsWithPage(long userId, int pageNum, int pageSize);
    
    /**
     * 获取歌单收藏数量
     */
    ResponseMessage<Integer> getPlayListFavoriteCount(long playListId);
}