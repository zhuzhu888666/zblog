package xyz.ztzhome.zblog.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.Comment;
import xyz.ztzhome.zblog.entity.Bean.CommentLike;
import xyz.ztzhome.zblog.entity.DTO.AddCommentDTO;
import xyz.ztzhome.zblog.entity.DTO.CommentLikeDTO;
import xyz.ztzhome.zblog.entity.DTO.ReplyCommentDTO;
import xyz.ztzhome.zblog.entity.VO.CommentVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.CommentLikeMapper;
import xyz.ztzhome.zblog.mapper.CommentMapper;
import xyz.ztzhome.zblog.service.ICommentService;
import xyz.ztzhome.zblog.service.IUserService;
import xyz.ztzhome.zblog.service.IMinioService;

import java.util.List;

@Service
public class CommentServiceImpl implements ICommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Autowired
    private IUserService userService;

    @Autowired
    private IMinioService minioService;

    private String buildAvatarUrl(String avatarPath) {
        if (avatarPath == null || avatarPath.isEmpty()) {
            return "/images/default.jpg";
        }
        String url = minioService.getFileUrl(ResponseConstant.AvatarTimOut, avatarPath);
        if (url == null || url.startsWith("getURL_error")) {
            return "/images/default.jpg";
        }
        return url;
    }

    private void applyAvatarUrl(CommentVO commentVO) {
        if (commentVO == null) return;
        commentVO.setUserAvatar(buildAvatarUrl(commentVO.getUserAvatar()));
        if (commentVO.getReplies() != null) {
            for (CommentVO reply : commentVO.getReplies()) {
                applyAvatarUrl(reply);
            }
        }
    }

    private void applyAvatarUrl(List<CommentVO> commentVOList) {
        if (commentVOList == null) return;
        for (CommentVO vo : commentVOList) {
            applyAvatarUrl(vo);
        }
    }

    @Override
    @Transactional
    public ResponseMessage addComment(AddCommentDTO addCommentDTO) {
        try {
            if (addCommentDTO.getContent() == null || addCommentDTO.getContent().trim().isEmpty()) {
                return new ResponseMessage<>(ResponseConstant.error, "评论内容不能为空");
            }

            // 验证用户是否存在且状态正常
            if (!userService.validateUser(addCommentDTO.getUserId())) {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在或已被禁用");
            }

            Comment comment = new Comment();
            comment.setSongId(addCommentDTO.getSongId());
            comment.setUserId(addCommentDTO.getUserId());
            comment.setContent(addCommentDTO.getContent().trim());
            comment.setParentId(0L); // 顶级评论
            comment.setRootId(0L); // 顶级评论的rootId为0
            comment.setReplyToUserId(0L); // 顶级评论没有回复目标
            comment.setLikeCount(0);
            comment.setReplyCount(0);
            comment.setStatus(1);

            int result = commentMapper.insertComment(comment);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "评论发表成功", comment.getId());
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "评论发表失败");
            }
        } catch (Exception e) {
            logger.error("添加评论失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    @Transactional
    public ResponseMessage replyComment(ReplyCommentDTO replyCommentDTO) {
        try {
            if (replyCommentDTO.getContent() == null || replyCommentDTO.getContent().trim().isEmpty()) {
                return new ResponseMessage<>(ResponseConstant.error, "回复内容不能为空");
            }

            // 验证用户是否存在且状态正常
            if (!userService.validateUser(replyCommentDTO.getUserId())) {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在或已被禁用");
            }

            // 验证回复目标用户是否存在
            if (replyCommentDTO.getReplyToUserId() > 0 && !userService.validateUser(replyCommentDTO.getReplyToUserId())) {
                return new ResponseMessage<>(ResponseConstant.error, "回复目标用户不存在或已被禁用");
            }

            // 获取父评论信息
            Comment parentComment = commentMapper.selectCommentById(replyCommentDTO.getCommentId());
            if (parentComment == null || parentComment.getStatus() != 1) {
                return new ResponseMessage<>(ResponseConstant.error, "要回复的评论不存在或已删除");
            }

            Comment reply = new Comment();
            reply.setSongId(parentComment.getSongId()); // 回复的歌曲ID与父评论相同
            reply.setUserId(replyCommentDTO.getUserId());
            reply.setContent(replyCommentDTO.getContent().trim());
            reply.setParentId(replyCommentDTO.getCommentId()); // 父评论ID
            reply.setReplyToUserId(replyCommentDTO.getReplyToUserId());
            
            // 设置rootId
            if (parentComment.getParentId() == 0) {
                // 如果父评论是顶级评论，rootId就是父评论的ID
                reply.setRootId(parentComment.getId());
            } else {
                // 如果父评论是回复，rootId就是父评论的rootId
                reply.setRootId(parentComment.getRootId());
            }
            
            reply.setLikeCount(0);
            reply.setReplyCount(0);
            reply.setStatus(1);

            int result = commentMapper.insertComment(reply);
            if (result > 0) {
                // 更新父评论的回复数
                commentMapper.updateCommentReplyCount(replyCommentDTO.getCommentId(), 1);
                return new ResponseMessage<>(ResponseConstant.success, "回复发表成功", reply.getId());
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "回复发表失败");
            }
        } catch (Exception e) {
            logger.error("回复评论失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    @Transactional
    public ResponseMessage deleteComment(long commentId, long userId) {
        try {
            // 验证用户是否存在且状态正常
            if (!userService.validateUser(userId)) {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在或已被禁用");
            }

            // 检查评论是否存在且属于该用户
            Comment comment = commentMapper.selectCommentById(commentId);
            if (comment == null) {
                return new ResponseMessage<>(ResponseConstant.error, "评论不存在");
            }
            
            if (comment.getUserId() != userId) {
                return new ResponseMessage<>(ResponseConstant.error, "无权限删除此评论");
            }

            int result = commentMapper.deleteComment(commentId, userId);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "评论删除成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "评论删除失败");
            }
        } catch (Exception e) {
            logger.error("删除评论失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<CommentVO> getCommentById(long commentId, long currentUserId) {
        try {
            Comment comment = commentMapper.selectCommentById(commentId);
            if (comment == null || comment.getStatus() != 1) {
                return new ResponseMessage<>(ResponseConstant.error, "评论不存在或已删除");
            }

            // 获取评论详情（包含用户信息和点赞状态）
            List<CommentVO> comments = commentMapper.selectTopCommentsBySongId(comment.getSongId(), currentUserId);
            CommentVO targetComment = null;
            
            // 在顶级评论中查找
            for (CommentVO vo : comments) {
                if (vo.getId() == commentId) {
                    targetComment = vo;
                    break;
                }
            }
            
            // 如果没找到，可能在回复中
            if (targetComment == null) {
                List<CommentVO> replies = commentMapper.selectRepliesByParentId(comment.getParentId(), currentUserId);
                for (CommentVO vo : replies) {
                    if (vo.getId() == commentId) {
                        targetComment = vo;
                        break;
                    }
                }
            }

            if (targetComment != null) {
                applyAvatarUrl(targetComment);
                return new ResponseMessage<>(ResponseConstant.success, "查询成功", targetComment);
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "评论不存在");
            }
        } catch (Exception e) {
            logger.error("获取评论详情失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<List<CommentVO>> getCommentsBySongId(long songId, long currentUserId) {
        try {
            List<CommentVO> comments = commentMapper.selectTopCommentsBySongId(songId, currentUserId);
            applyAvatarUrl(comments);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", comments);
        } catch (Exception e) {
            logger.error("查询评论失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<PageResponse<CommentVO>> getCommentsBySongIdWithPage(long songId, long currentUserId, int pageNum, int pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            List<CommentVO> comments = commentMapper.selectTopCommentsBySongIdWithPage(songId, currentUserId, offset, pageSize);
            applyAvatarUrl(comments);
            int totalCount = commentMapper.countCommentsBySongId(songId);

            PageResponse<CommentVO> pageResponse = new PageResponse<>();
            pageResponse.setData(comments);
            pageResponse.setTotal(totalCount);
            pageResponse.setPageNum(pageNum);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotalPages((long) Math.ceil((double) totalCount / pageSize));

            return new ResponseMessage<>(ResponseConstant.success, "查询成功", pageResponse);
        } catch (Exception e) {
            logger.error("分页查询评论失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<List<CommentVO>> getRepliesByCommentId(long commentId, long currentUserId) {
        try {
            List<CommentVO> replies = commentMapper.selectRepliesByParentId(commentId, currentUserId);
            applyAvatarUrl(replies);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", replies);
        } catch (Exception e) {
            logger.error("查询回复失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<PageResponse<CommentVO>> getRepliesByCommentIdWithPage(long commentId, long currentUserId, int pageNum, int pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            List<CommentVO> replies = commentMapper.selectRepliesByParentIdWithPage(commentId, currentUserId, offset, pageSize);
            applyAvatarUrl(replies);
            int totalCount = commentMapper.countRepliesByParentId(commentId);

            PageResponse<CommentVO> pageResponse = new PageResponse<>();
            pageResponse.setData(replies);
            pageResponse.setTotal(totalCount);
            pageResponse.setPageNum(pageNum);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotalPages((long) Math.ceil((double) totalCount / pageSize));

            return new ResponseMessage<>(ResponseConstant.success, "查询成功", pageResponse);
        } catch (Exception e) {
            logger.error("分页查询回复失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<List<CommentVO>> getCommentsByUserId(long userId, long currentUserId) {
        try {
            // 验证用户是否存在
            if (!userService.validateUser(userId)) {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在或已被禁用");
            }

            List<CommentVO> comments = commentMapper.selectCommentsByUserId(userId, currentUserId);
            applyAvatarUrl(comments);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", comments);
        } catch (Exception e) {
            logger.error("查询用户评论失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<PageResponse<CommentVO>> getCommentsByUserIdWithPage(long userId, long currentUserId, int pageNum, int pageSize) {
        try {
            // 验证用户是否存在
            if (!userService.validateUser(userId)) {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在或已被禁用");
            }

            int offset = (pageNum - 1) * pageSize;
            List<CommentVO> comments = commentMapper.selectCommentsByUserIdWithPage(userId, currentUserId, offset, pageSize);
            applyAvatarUrl(comments);
            int totalCount = commentMapper.countCommentsByUserId(userId);

            PageResponse<CommentVO> pageResponse = new PageResponse<>();
            pageResponse.setData(comments);
            pageResponse.setTotal(totalCount);
            pageResponse.setPageNum(pageNum);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotalPages((long) Math.ceil((double) totalCount / pageSize));

            return new ResponseMessage<>(ResponseConstant.success, "查询成功", pageResponse);
        } catch (Exception e) {
            logger.error("分页查询用户评论失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<Integer> getCommentCountBySongId(long songId) {
        try {
            int count = commentMapper.countCommentsBySongId(songId);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", count);
        } catch (Exception e) {
            logger.error("查询评论数失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    @Transactional
    public ResponseMessage likeComment(CommentLikeDTO commentLikeDTO) {
        try {
            // 验证用户是否存在且状态正常
            if (!userService.validateUser(commentLikeDTO.getUserId())) {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在或已被禁用");
            }

            if (commentLikeMapper.isUserLikedComment(commentLikeDTO.getCommentId(), commentLikeDTO.getUserId())) {
                return new ResponseMessage<>(ResponseConstant.error, "已经点赞过该评论");
            }

            CommentLike commentLike = new CommentLike();
            commentLike.setCommentId(commentLikeDTO.getCommentId());
            commentLike.setUserId(commentLikeDTO.getUserId());

            int result = commentLikeMapper.insertCommentLike(commentLike);
            if (result > 0) {
                commentMapper.updateCommentLikeCount(commentLikeDTO.getCommentId(), 1);
                return new ResponseMessage<>(ResponseConstant.success, "点赞成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "点赞失败");
            }
        } catch (Exception e) {
            logger.error("点赞失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    @Transactional
    public ResponseMessage unlikeComment(CommentLikeDTO commentLikeDTO) {
        try {
            // 验证用户是否存在且状态正常
            if (!userService.validateUser(commentLikeDTO.getUserId())) {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在或已被禁用");
            }

            int result = commentLikeMapper.deleteCommentLike(commentLikeDTO.getCommentId(), commentLikeDTO.getUserId());
            if (result > 0) {
                commentMapper.updateCommentLikeCount(commentLikeDTO.getCommentId(), -1);
                return new ResponseMessage<>(ResponseConstant.success, "取消点赞成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "取消点赞失败");
            }
        } catch (Exception e) {
            logger.error("取消点赞失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<Boolean> isUserLikedComment(long commentId, long userId) {
        try {
            // 验证用户是否存在且状态正常
            if (!userService.validateUser(userId)) {
                return new ResponseMessage<>(ResponseConstant.error, "用户不存在或已被禁用");
            }

            boolean isLiked = commentLikeMapper.isUserLikedComment(commentId, userId);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", isLiked);
        } catch (Exception e) {
            logger.error("查询点赞状态失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<Integer> getCommentLikeCount(long commentId) {
        try {
            int count = commentLikeMapper.countLikesByCommentId(commentId);
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", count);
        } catch (Exception e) {
            logger.error("查询点赞数失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }
}