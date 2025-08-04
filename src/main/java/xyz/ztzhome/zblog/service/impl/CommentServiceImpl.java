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

import java.util.List;

@Service
public class CommentServiceImpl implements ICommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Override
    @Transactional
    public ResponseMessage addComment(AddCommentDTO addCommentDTO) {
        try {
            if (addCommentDTO.getContent() == null || addCommentDTO.getContent().trim().isEmpty()) {
                return new ResponseMessage<>(ResponseConstant.error, "评论内容不能为空");
            }

            Comment comment = new Comment();
            comment.setSongId(addCommentDTO.getSongId());
            comment.setUserId(addCommentDTO.getUserId());
            comment.setContent(addCommentDTO.getContent().trim());

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

            Comment reply = new Comment();
            reply.setSongId(replyCommentDTO.getSongId());
            reply.setUserId(replyCommentDTO.getUserId());
            reply.setContent(replyCommentDTO.getContent().trim());
            reply.setParentId(replyCommentDTO.getParentId());
            reply.setRootId(replyCommentDTO.getRootId());
            reply.setReplyToUserId(replyCommentDTO.getReplyToUserId());

            int result = commentMapper.insertComment(reply);
            if (result > 0) {
                commentMapper.updateCommentReplyCount(replyCommentDTO.getParentId(), 1);
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
            int result = commentMapper.deleteComment(commentId, userId);
            if (result > 0) {
                return new ResponseMessage<>(ResponseConstant.success, "评论删除成功");
            } else {
                return new ResponseMessage<>(ResponseConstant.error, "评论删除失败或无权限");
            }
        } catch (Exception e) {
            logger.error("删除评论失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<CommentVO> getCommentById(long commentId, long currentUserId) {
        // TODO: 实现获取单个评论
        return new ResponseMessage<>(ResponseConstant.error, "功能暂未实现");
    }

    @Override
    public ResponseMessage<List<CommentVO>> getCommentsBySongId(long songId, long currentUserId) {
        try {
            List<CommentVO> comments = commentMapper.selectTopCommentsBySongId(songId, currentUserId);
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
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", replies);
        } catch (Exception e) {
            logger.error("查询回复失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常");
        }
    }

    @Override
    public ResponseMessage<PageResponse<CommentVO>> getRepliesByCommentIdWithPage(long commentId, long currentUserId, int pageNum, int pageSize) {
        // TODO: 实现分页查询回复
        return new ResponseMessage<>(ResponseConstant.error, "功能暂未实现");
    }

    @Override
    public ResponseMessage<List<CommentVO>> getCommentsByUserId(long userId, long currentUserId) {
        // TODO: 实现用户评论查询
        return new ResponseMessage<>(ResponseConstant.error, "功能暂未实现");
    }

    @Override
    public ResponseMessage<PageResponse<CommentVO>> getCommentsByUserIdWithPage(long userId, long currentUserId, int pageNum, int pageSize) {
        // TODO: 实现分页查询用户评论
        return new ResponseMessage<>(ResponseConstant.error, "功能暂未实现");
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