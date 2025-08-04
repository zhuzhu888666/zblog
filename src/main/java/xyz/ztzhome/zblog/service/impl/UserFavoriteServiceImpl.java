package xyz.ztzhome.zblog.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.Artist;
import xyz.ztzhome.zblog.entity.Bean.UserFavoriteSong;
import xyz.ztzhome.zblog.entity.Bean.UserFollowArtist;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.UserFavoriteSongMapper;
import xyz.ztzhome.zblog.mapper.UserFollowArtistMapper;
import xyz.ztzhome.zblog.service.IUserFavoriteService;

import java.util.List;

@Service
public class UserFavoriteServiceImpl implements IUserFavoriteService {

    private static final Logger logger = LoggerFactory.getLogger(UserFavoriteServiceImpl.class);

    @Autowired
    private UserFavoriteSongMapper userFavoriteSongMapper;

    @Autowired
    private UserFollowArtistMapper userFollowArtistMapper;

    // ========== 收藏歌曲相关方法 ==========

    @Override
    @Transactional
    public ResponseMessage favoriteSong(long userId, long songId) {
        try {
            // 检查是否已经收藏
            if (userFavoriteSongMapper.isUserFavoriteSong(userId, songId)) {
                return new ResponseMessage<>(ResponseConstant.error, "已经收藏过该歌曲");
            }

            // 创建收藏记录
            UserFavoriteSong userFavoriteSong = new UserFavoriteSong();
            userFavoriteSong.setUserId(userId);
            userFavoriteSong.setSongId(songId);

            int result = userFavoriteSongMapper.insertUserFavoriteSong(userFavoriteSong);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "收藏成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "收藏失败");
            }
        } catch (Exception e) {
            logger.error("收藏歌曲失败：用户ID={}, 歌曲ID={}", userId, songId, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    @Transactional
    public ResponseMessage unfavoriteSong(long userId, long songId) {
        try {
            // 检查是否已经收藏
            if (!userFavoriteSongMapper.isUserFavoriteSong(userId, songId)) {
                return new ResponseMessage<>(ResponseConstant.error, "未收藏该歌曲");
            }

            int result = userFavoriteSongMapper.deleteUserFavoriteSong(userId, songId);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "取消收藏成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "取消收藏失败");
            }
        } catch (Exception e) {
            logger.error("取消收藏歌曲失败：用户ID={}, 歌曲ID={}", userId, songId, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<Boolean> isUserFavoriteSong(long userId, long songId) {
        try {
            boolean isFavorite = userFavoriteSongMapper.isUserFavoriteSong(userId, songId);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", isFavorite);
        } catch (Exception e) {
            logger.error("查询收藏状态失败：用户ID={}, 歌曲ID={}", userId, songId, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<List<SongVO>> getUserFavoriteSongs(long userId) {
        try {
            List<SongVO> favoriteSongs = userFavoriteSongMapper.selectFavoriteSongsByUserId(userId);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", favoriteSongs);
        } catch (Exception e) {
            logger.error("查询用户收藏歌曲失败：用户ID={}", userId, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<PageResponse<SongVO>> getUserFavoriteSongsWithPage(long userId, int pageNum, int pageSize) {
        try {
            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;
            
            // 查询分页数据
            List<SongVO> favoriteSongs = userFavoriteSongMapper.selectFavoriteSongsByUserIdWithPage(userId, offset, pageSize);
            
            // 查询总数
            int totalCount = userFavoriteSongMapper.countFavoriteSongsByUserId(userId);
            
            // 构建分页响应
            PageResponse<SongVO> pageResponse = new PageResponse<>();
            pageResponse.setData(favoriteSongs);
            pageResponse.setTotal(totalCount);
            pageResponse.setPageNum(pageNum);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotalPages((long) Math.ceil((double) totalCount / pageSize));
            
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", pageResponse);
        } catch (Exception e) {
            logger.error("分页查询用户收藏歌曲失败：用户ID={}, 页码={}, 页大小={}", userId, pageNum, pageSize, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<Integer> getSongFavoriteCount(long songId) {
        try {
            int count = userFavoriteSongMapper.countUsersBySongId(songId);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", count);
        } catch (Exception e) {
            logger.error("查询歌曲收藏数失败：歌曲ID={}", songId, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    // ========== 关注艺术家相关方法 ==========

    @Override
    @Transactional
    public ResponseMessage followArtist(long userId, long artistId) {
        try {
            // 检查是否已经关注
            if (userFollowArtistMapper.isUserFollowArtist(userId, artistId)) {
                return new ResponseMessage<>(ResponseConstant.error, "已经关注过该艺术家");
            }

            // 创建关注记录
            UserFollowArtist userFollowArtist = new UserFollowArtist();
            userFollowArtist.setUserId(userId);
            userFollowArtist.setArtistId(artistId);

            int result = userFollowArtistMapper.insertUserFollowArtist(userFollowArtist);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "关注成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "关注失败");
            }
        } catch (Exception e) {
            logger.error("关注艺术家失败：用户ID={}, 艺术家ID={}", userId, artistId, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    @Transactional
    public ResponseMessage unfollowArtist(long userId, long artistId) {
        try {
            // 检查是否已经关注
            if (!userFollowArtistMapper.isUserFollowArtist(userId, artistId)) {
                return new ResponseMessage<>(ResponseConstant.error, "未关注该艺术家");
            }

            int result = userFollowArtistMapper.deleteUserFollowArtist(userId, artistId);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "取消关注成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "取消关注失败");
            }
        } catch (Exception e) {
            logger.error("取消关注艺术家失败：用户ID={}, 艺术家ID={}", userId, artistId, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<Boolean> isUserFollowArtist(long userId, long artistId) {
        try {
            boolean isFollow = userFollowArtistMapper.isUserFollowArtist(userId, artistId);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", isFollow);
        } catch (Exception e) {
            logger.error("查询关注状态失败：用户ID={}, 艺术家ID={}", userId, artistId, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<List<Artist>> getUserFollowArtists(long userId) {
        try {
            List<Artist> followArtists = userFollowArtistMapper.selectFollowArtistsByUserId(userId);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", followArtists);
        } catch (Exception e) {
            logger.error("查询用户关注艺术家失败：用户ID={}", userId, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<PageResponse<Artist>> getUserFollowArtistsWithPage(long userId, int pageNum, int pageSize) {
        try {
            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;
            
            // 查询分页数据
            List<Artist> followArtists = userFollowArtistMapper.selectFollowArtistsByUserIdWithPage(userId, offset, pageSize);
            
            // 查询总数
            int totalCount = userFollowArtistMapper.countFollowArtistsByUserId(userId);
            
            // 构建分页响应
            PageResponse<Artist> pageResponse = new PageResponse<>();
            pageResponse.setData(followArtists);
            pageResponse.setTotal(totalCount);
            pageResponse.setPageNum(pageNum);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotalPages((long) Math.ceil((double) totalCount / pageSize));
            
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", pageResponse);
        } catch (Exception e) {
            logger.error("分页查询用户关注艺术家失败：用户ID={}, 页码={}, 页大小={}", userId, pageNum, pageSize, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<Integer> getArtistFollowerCount(long artistId) {
        try {
            int count = userFollowArtistMapper.countUsersByArtistId(artistId);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", count);
        } catch (Exception e) {
            logger.error("查询艺术家粉丝数失败：艺术家ID={}", artistId, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }
}