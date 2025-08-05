package xyz.ztzhome.zblog.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.DTO.AddSongToPlayListDTO;
import xyz.ztzhome.zblog.entity.DTO.CreatePlayListDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdatePlayListDTO;
import xyz.ztzhome.zblog.entity.VO.PlayListDetailVO;
import xyz.ztzhome.zblog.entity.VO.PlayListVO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

import java.util.List;

public interface ISongPlayListService {
    
    // ========== 歌单基本操作 ==========
    
    /**
     * 创建歌单
     */
    ResponseMessage createPlayList(CreatePlayListDTO createPlayListDTO, MultipartFile coverFile);
    
    /**
     * 根据ID获取歌单详情
     */
    ResponseMessage<PlayListDetailVO> getPlayListDetail(long playListId);
    
    /**
     * 根据ID获取歌单基本信息
     */
    ResponseMessage<PlayListVO> getPlayList(long playListId);
    
    /**
     * 更新歌单信息
     */
    ResponseMessage updatePlayList(UpdatePlayListDTO updatePlayListDTO, MultipartFile coverFile);
    
    /**
     * 删除歌单
     */
    ResponseMessage deletePlayList(long playListId, long userId);
    
    // ========== 歌单歌曲管理 ==========
    
    /**
     * 添加歌曲到歌单
     */
    ResponseMessage addSongToPlayList(AddSongToPlayListDTO addSongToPlayListDTO);
    
    /**
     * 从歌单中移除歌曲
     */
    ResponseMessage removeSongFromPlayList(long playListId, long songId);
    
    /**
     * 获取歌单中的歌曲列表
     */
    ResponseMessage<List<SongVO>> getPlayListSongs(long playListId);
    
    /**
     * 分页获取歌单中的歌曲列表
     */
    ResponseMessage<PageResponse<SongVO>> getPlayListSongsWithPage(long playListId, int pageNum, int pageSize);
    
    // ========== 用户歌单查询 ==========
    
    /**
     * 获取用户创建的歌单列表
     */
    ResponseMessage<List<PlayListVO>> getUserPlayLists(long userId);
    
    /**
     * 分页获取用户创建的歌单列表
     */
    ResponseMessage<PageResponse<PlayListVO>> getUserPlayListsWithPage(long userId, int pageNum, int pageSize);
    
    // ========== 公开歌单查询 ==========
    
    /**
     * 根据名称搜索公开歌单
     */
    ResponseMessage<List<PlayListVO>> searchPublicPlayLists(String name);
    
    /**
     * 根据名称分页搜索公开歌单
     */
    ResponseMessage<PageResponse<PlayListVO>> searchPublicPlayListsWithPage(String name, int pageNum, int pageSize);
    
    /**
     * 获取热门歌单
     */
    ResponseMessage<List<PlayListVO>> getHotPlayLists(int limit);
    
    // ========== 歌单收藏相关 ==========
    
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
    ResponseMessage<List<PlayListVO>> getUserFavoritePlayLists(long userId);
    
    /**
     * 分页获取用户收藏的歌单列表
     */
    ResponseMessage<PageResponse<PlayListVO>> getUserFavoritePlayListsWithPage(long userId, int pageNum, int pageSize);
    
    /**
     * 获取歌单收藏数量
     */
    ResponseMessage<Integer> getPlayListFavoriteCount(long playListId);
}