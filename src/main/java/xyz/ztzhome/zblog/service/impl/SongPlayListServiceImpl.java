package xyz.ztzhome.zblog.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.constant.PathCosntant;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.SongPlayList;
import xyz.ztzhome.zblog.entity.Bean.SongPlayListSong;
import xyz.ztzhome.zblog.entity.Bean.UserFavoritePlayList;
import xyz.ztzhome.zblog.entity.DTO.AddSongToPlayListDTO;
import xyz.ztzhome.zblog.entity.DTO.CreatePlayListDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdatePlayListDTO;
import xyz.ztzhome.zblog.entity.VO.PlayListDetailVO;
import xyz.ztzhome.zblog.entity.VO.PlayListVO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.SongPlayListMapper;
import xyz.ztzhome.zblog.mapper.SongPlayListSongMapper;
import xyz.ztzhome.zblog.mapper.UserFavoritePlayListMapper;
import xyz.ztzhome.zblog.service.ISongPlayListService;
import xyz.ztzhome.zblog.util.FileTypeUtil;

import java.util.Date;
import java.util.List;

@Service
public class SongPlayListServiceImpl implements ISongPlayListService {

    private static final Logger logger = LoggerFactory.getLogger(SongPlayListServiceImpl.class);

    @Autowired
    private SongPlayListMapper songPlayListMapper;

    @Autowired
    private SongPlayListSongMapper songPlayListSongMapper;

    @Autowired
    private UserFavoritePlayListMapper userFavoritePlayListMapper;

    @Autowired
    private MinioServiceImpl minioService;

    // ========== 歌单基本操作 ==========

    @Override
    @Transactional
    public ResponseMessage createPlayList(CreatePlayListDTO createPlayListDTO, MultipartFile coverFile) {
        try {
            SongPlayList playList = new SongPlayList();
            playList.setUserId(createPlayListDTO.getUserId());
            playList.setName(createPlayListDTO.getName());
            playList.setDescription(createPlayListDTO.getDescription());
            playList.setIsPublic(createPlayListDTO.getIsPublic());

            // 先插入歌单获取ID
            int result = songPlayListMapper.insertPlayList(playList);
            if (result <= 0) {
                return new ResponseMessage<>(ResponseConstant.error, "歌单创建失败");
            }

            // 处理封面文件
            if (coverFile != null && !coverFile.isEmpty()) {
                try {
                    String coverFileName = "playlist_" + playList.getId() + FileTypeUtil.getFileExtension2(coverFile.getOriginalFilename());
                    String coverPath = PathCosntant.PLAYLIST_COVER_PATH + coverFileName;
                    int uploadResult = minioService.uploadFile(coverFile, coverPath);
                    if (uploadResult == 1) {
                        playList.setCoverPath(coverFileName);
                        songPlayListMapper.updatePlayList(playList);
                    }
                } catch (Exception e) {
                    logger.warn("上传歌单封面失败: {}", e.getMessage());
                    // 封面上传失败不影响歌单创建
                }
            }

            return new ResponseMessage<>(ResponseConstant.success, "歌单创建成功", playList.getId());
        } catch (Exception e) {
            logger.error("创建歌单失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "创建歌单失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<PlayListDetailVO> getPlayListDetail(long playListId) {
        try {
            PlayListDetailVO playListDetail = songPlayListMapper.selectPlayListDetailById(playListId);
            if (playListDetail == null) {
                return new ResponseMessage<>(ResponseConstant.error, "歌单不存在");
            }

            // 获取歌单中的歌曲列表
            List<SongVO> songs = songPlayListSongMapper.selectSongsByPlayListId(playListId);
            playListDetail.setSongs(songs);

            return new ResponseMessage<>(ResponseConstant.success, "获取歌单详情成功", playListDetail);
        } catch (Exception e) {
            logger.error("获取歌单详情失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "获取歌单详情失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<PlayListVO> getPlayList(long playListId) {
        try {
            PlayListVO playListVO = songPlayListMapper.selectPlayListVOById(playListId);
            if (playListVO == null) {
                return new ResponseMessage<>(ResponseConstant.error, "歌单不存在");
            }
            return new ResponseMessage<>(ResponseConstant.success, "获取歌单信息成功", playListVO);
        } catch (Exception e) {
            logger.error("获取歌单信息失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "获取歌单信息失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseMessage updatePlayList(UpdatePlayListDTO updatePlayListDTO, MultipartFile coverFile) {
        try {
            SongPlayList playList = songPlayListMapper.selectPlayListById(updatePlayListDTO.getId());
            if (playList == null) {
                return new ResponseMessage<>(ResponseConstant.error, "歌单不存在");
            }

            // 更新基本信息
            playList.setName(updatePlayListDTO.getName());
            playList.setDescription(updatePlayListDTO.getDescription());
            playList.setIsPublic(updatePlayListDTO.getIsPublic());
            playList.setUpdateTime(new Date());

            // 处理封面文件
            if (coverFile != null && !coverFile.isEmpty()) {
                try {
                    String coverFileName = "playlist_" + playList.getId() + FileTypeUtil.getFileExtension2(coverFile.getOriginalFilename());
                    String coverPath = PathCosntant.PLAYLIST_COVER_PATH + coverFileName;
                    int uploadResult = minioService.uploadFile(coverFile, coverPath);
                    if (uploadResult == 1) {
                        playList.setCoverPath(coverFileName);
                    }
                } catch (Exception e) {
                    logger.warn("更新歌单封面失败: {}", e.getMessage());
                }
            }

            int result = songPlayListMapper.updatePlayList(playList);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "歌单更新成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "歌单更新失败");
            }
        } catch (Exception e) {
            logger.error("更新歌单失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "更新歌单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseMessage deletePlayList(long playListId, long userId) {
        try {
            SongPlayList playList = songPlayListMapper.selectPlayListById(playListId);
            if (playList == null) {
                return new ResponseMessage<>(ResponseConstant.error, "歌单不存在");
            }

            if (playList.getUserId() != userId) {
                return new ResponseMessage<>(ResponseConstant.error, "只能删除自己创建的歌单");
            }

            // 删除歌单中的所有歌曲关联
            songPlayListSongMapper.deleteAllSongsByPlayListId(playListId);

            // 删除歌单
            int result = songPlayListMapper.deletePlayList(playListId);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "歌单删除成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "歌单删除失败");
            }
        } catch (Exception e) {
            logger.error("删除歌单失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "删除歌单失败: " + e.getMessage());
        }
    }

    // ========== 歌单歌曲管理 ==========

    @Override
    @Transactional
    public ResponseMessage addSongToPlayList(AddSongToPlayListDTO addSongToPlayListDTO) {
        try {
            long playListId = addSongToPlayListDTO.getPlayListId();
            long songId = addSongToPlayListDTO.getSongId();

            // 检查歌单是否存在
            SongPlayList playList = songPlayListMapper.selectPlayListById(playListId);
            if (playList == null) {
                return new ResponseMessage<>(ResponseConstant.error, "歌单不存在");
            }

            // 检查歌曲是否已在歌单中
            if (songPlayListSongMapper.isSongInPlayList(playListId, songId)) {
                return new ResponseMessage<>(ResponseConstant.error, "歌曲已在歌单中");
            }

            // 获取下一个排序号
            int nextOrder = songPlayListSongMapper.getMaxSortOrder(playListId) + 1;

            // 添加歌曲到歌单
            SongPlayListSong playListSong = new SongPlayListSong();
            playListSong.setPlayListId(playListId);
            playListSong.setSongId(songId);
            playListSong.setSortOrder(nextOrder);

            int result = songPlayListSongMapper.insertPlayListSong(playListSong);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "歌曲添加成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "歌曲添加失败");
            }
        } catch (Exception e) {
            logger.error("添加歌曲到歌单失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "添加歌曲失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseMessage removeSongFromPlayList(long playListId, long songId) {
        try {
            // 检查歌曲是否在歌单中
            if (!songPlayListSongMapper.isSongInPlayList(playListId, songId)) {
                return new ResponseMessage<>(ResponseConstant.error, "歌曲不在歌单中");
            }

            int result = songPlayListSongMapper.deletePlayListSong(playListId, songId);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "歌曲移除成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "歌曲移除失败");
            }
        } catch (Exception e) {
            logger.error("从歌单移除歌曲失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "移除歌曲失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<List<SongVO>> getPlayListSongs(long playListId) {
        try {
            List<SongVO> songs = songPlayListSongMapper.selectSongsByPlayListId(playListId);
            return new ResponseMessage<>(ResponseConstant.success, "获取歌单歌曲列表成功", songs);
        } catch (Exception e) {
            logger.error("获取歌单歌曲列表失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "获取歌单歌曲列表失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<PageResponse<SongVO>> getPlayListSongsWithPage(long playListId, int pageNum, int pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            List<SongVO> songs = songPlayListSongMapper.selectSongsByPlayListIdWithPage(playListId, offset, pageSize);
            int totalCount = songPlayListSongMapper.countSongsByPlayListId(playListId);

            PageResponse<SongVO> pageResponse = new PageResponse<>();
            pageResponse.setData(songs);
            pageResponse.setPageNum(pageNum);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotal(totalCount);
            pageResponse.setTotalPages((long) Math.ceil((double) totalCount / pageSize));

            return new ResponseMessage<>(ResponseConstant.success, "获取歌单歌曲列表成功", pageResponse);
        } catch (Exception e) {
            logger.error("分页获取歌单歌曲列表失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "获取歌单歌曲列表失败: " + e.getMessage());
        }
    }

    // ========== 用户歌单查询 ==========

    @Override
    public ResponseMessage<List<PlayListVO>> getUserPlayLists(long userId) {
        try {
            List<PlayListVO> playLists = songPlayListMapper.selectPlayListsByUserId(userId);
            return new ResponseMessage<>(ResponseConstant.success, "获取用户歌单列表成功", playLists);
        } catch (Exception e) {
            logger.error("获取用户歌单列表失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "获取用户歌单列表失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<PageResponse<PlayListVO>> getUserPlayListsWithPage(long userId, int pageNum, int pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            List<PlayListVO> playLists = songPlayListMapper.selectPlayListsByUserIdWithPage(userId, offset, pageSize);
            int totalCount = songPlayListMapper.countPlayListsByUserId(userId);

            PageResponse<PlayListVO> pageResponse = new PageResponse<>();
            pageResponse.setData(playLists);
            pageResponse.setPageNum(pageNum);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotal(totalCount);
            pageResponse.setTotalPages((long) Math.ceil((double) totalCount / pageSize));

            return new ResponseMessage<>(ResponseConstant.success, "获取用户歌单列表成功", pageResponse);
        } catch (Exception e) {
            logger.error("分页获取用户歌单列表失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "获取用户歌单列表失败: " + e.getMessage());
        }
    }

    // ========== 公开歌单查询 ==========

    @Override
    public ResponseMessage<List<PlayListVO>> searchPublicPlayLists(String name) {
        try {
            List<PlayListVO> playLists = songPlayListMapper.selectPublicPlayListsByNameLike(name);
            return new ResponseMessage<>(ResponseConstant.success, "搜索歌单成功", playLists);
        } catch (Exception e) {
            logger.error("搜索歌单失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "搜索歌单失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<PageResponse<PlayListVO>> searchPublicPlayListsWithPage(String name, int pageNum, int pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            List<PlayListVO> playLists = songPlayListMapper.selectPublicPlayListsByNameLikeWithPage(name, offset, pageSize);
            int totalCount = songPlayListMapper.countPublicPlayLists();

            PageResponse<PlayListVO> pageResponse = new PageResponse<>();
            pageResponse.setData(playLists);
            pageResponse.setPageNum(pageNum);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotal(totalCount);
            pageResponse.setTotalPages((long) Math.ceil((double) totalCount / pageSize));

            return new ResponseMessage<>(ResponseConstant.success, "搜索歌单成功", pageResponse);
        } catch (Exception e) {
            logger.error("分页搜索歌单失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "搜索歌单失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<List<PlayListVO>> getHotPlayLists(int limit) {
        try {
            List<PlayListVO> playLists = songPlayListMapper.selectHotPlayLists(limit);
            return new ResponseMessage<>(ResponseConstant.success, "获取热门歌单成功", playLists);
        } catch (Exception e) {
            logger.error("获取热门歌单失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "获取热门歌单失败: " + e.getMessage());
        }
    }

    // ========== 歌单收藏相关 ==========

    @Override
    @Transactional
    public ResponseMessage favoritePlayList(long userId, long playListId) {
        try {
            // 检查歌单是否存在
            SongPlayList playList = songPlayListMapper.selectPlayListById(playListId);
            if (playList == null) {
                return new ResponseMessage<>(ResponseConstant.error, "歌单不存在");
            }

            // 检查是否已经收藏
            if (userFavoritePlayListMapper.isUserFavoritePlayList(userId, playListId)) {
                return new ResponseMessage<>(ResponseConstant.error, "已经收藏过该歌单");
            }

            // 创建收藏记录
            UserFavoritePlayList userFavoritePlayList = new UserFavoritePlayList();
            userFavoritePlayList.setUserId(userId);
            userFavoritePlayList.setPlayListId(playListId);

            int result = userFavoritePlayListMapper.insertUserFavoritePlayList(userFavoritePlayList);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "收藏成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "收藏失败");
            }
        } catch (Exception e) {
            logger.error("收藏歌单失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "收藏歌单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseMessage unfavoritePlayList(long userId, long playListId) {
        try {
            // 检查是否已收藏
            if (!userFavoritePlayListMapper.isUserFavoritePlayList(userId, playListId)) {
                return new ResponseMessage<>(ResponseConstant.error, "尚未收藏该歌单");
            }

            int result = userFavoritePlayListMapper.deleteUserFavoritePlayList(userId, playListId);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "取消收藏成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "取消收藏失败");
            }
        } catch (Exception e) {
            logger.error("取消收藏歌单失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "取消收藏歌单失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<Boolean> isUserFavoritePlayList(long userId, long playListId) {
        try {
            boolean isFavorite = userFavoritePlayListMapper.isUserFavoritePlayList(userId, playListId);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", isFavorite);
        } catch (Exception e) {
            logger.error("查询收藏状态失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "查询收藏状态失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<List<PlayListVO>> getUserFavoritePlayLists(long userId) {
        try {
            List<PlayListVO> playLists = userFavoritePlayListMapper.selectFavoritePlayListsByUserId(userId);
            return new ResponseMessage<>(ResponseConstant.success, "获取用户收藏歌单列表成功", playLists);
        } catch (Exception e) {
            logger.error("获取用户收藏歌单列表失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "获取用户收藏歌单列表失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<PageResponse<PlayListVO>> getUserFavoritePlayListsWithPage(long userId, int pageNum, int pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            List<PlayListVO> playLists = userFavoritePlayListMapper.selectFavoritePlayListsByUserIdWithPage(userId, offset, pageSize);
            int totalCount = userFavoritePlayListMapper.countFavoritePlayListsByUserId(userId);

            PageResponse<PlayListVO> pageResponse = new PageResponse<>();
            pageResponse.setData(playLists);
            pageResponse.setPageNum(pageNum);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotal(totalCount);
            pageResponse.setTotalPages((long) Math.ceil((double) totalCount / pageSize));

            return new ResponseMessage<>(ResponseConstant.success, "获取用户收藏歌单列表成功", pageResponse);
        } catch (Exception e) {
            logger.error("分页获取用户收藏歌单列表失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "获取用户收藏歌单列表失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<Integer> getPlayListFavoriteCount(long playListId) {
        try {
            int count = userFavoritePlayListMapper.countUsersByPlayListId(playListId);
            return new ResponseMessage<>(ResponseConstant.success, "获取歌单收藏数成功", count);
        } catch (Exception e) {
            logger.error("获取歌单收藏数失败: {}", e.getMessage(), e);
            return new ResponseMessage<>(ResponseConstant.error, "获取歌单收藏数失败: " + e.getMessage());
        }
    }
}