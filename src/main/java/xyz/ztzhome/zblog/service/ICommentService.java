package xyz.ztzhome.zblog.service;

import xyz.ztzhome.zblog.entity.DTO.AddCommentDTO;
import xyz.ztzhome.zblog.entity.DTO.CommentLikeDTO;
import xyz.ztzhome.zblog.entity.DTO.ReplyCommentDTO;
import xyz.ztzhome.zblog.entity.VO.CommentVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

import java.util.List;

public interface ICommentService {
    
    // ========== 评论管理相关方法 ==========
    
    /**
     * 添加评论
     */
    ResponseMessage addComment(AddCommentDTO addCommentDTO);
    
    /**
     * 回复评论
     */
    ResponseMessage replyComment(ReplyCommentDTO replyCommentDTO);
    
    /**
     * 删除评论
     */
    ResponseMessage deleteComment(long commentId, long userId);
    
    /**
     * 根据ID获取评论详情
     */
    ResponseMessage<CommentVO> getCommentById(long commentId, long currentUserId);
    
    // ========== 评论查询相关方法 ==========
    
    /**
     * 根据歌曲ID获取评论列表（包含回复）
     */
    ResponseMessage<List<CommentVO>> getCommentsBySongId(long songId, long currentUserId);
    
    /**
     * 根据歌曲ID分页获取评论列表
     */
    ResponseMessage<PageResponse<CommentVO>> getCommentsBySongIdWithPage(long songId, long currentUserId, 
                                                                         int pageNum, int pageSize);
    
    /**
     * 获取评论的回复列表
     */
    ResponseMessage<List<CommentVO>> getRepliesByCommentId(long commentId, long currentUserId);
    
    /**
     * 分页获取评论的回复列表
     */
    ResponseMessage<PageResponse<CommentVO>> getRepliesByCommentIdWithPage(long commentId, long currentUserId, 
                                                                           int pageNum, int pageSize);
    
    /**
     * 获取用户的评论列表
     */
    ResponseMessage<List<CommentVO>> getCommentsByUserId(long userId, long currentUserId);
    
    /**
     * 分页获取用户的评论列表
     */
    ResponseMessage<PageResponse<CommentVO>> getCommentsByUserIdWithPage(long userId, long currentUserId, 
                                                                         int pageNum, int pageSize);
    
    /**
     * 获取歌曲的评论总数
     */
    ResponseMessage<Integer> getCommentCountBySongId(long songId);
    
    // ========== 评论点赞相关方法 ==========
    
    /**
     * 点赞评论
     */
    ResponseMessage likeComment(CommentLikeDTO commentLikeDTO);
    
    /**
     * 取消点赞评论
     */
    ResponseMessage unlikeComment(CommentLikeDTO commentLikeDTO);
    
    /**
     * 检查用户是否已点赞该评论
     */
    ResponseMessage<Boolean> isUserLikedComment(long commentId, long userId);
    
    /**
     * 获取评论的点赞数
     */
    ResponseMessage<Integer> getCommentLikeCount(long commentId);
}