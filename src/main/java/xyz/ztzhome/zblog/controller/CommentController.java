package xyz.ztzhome.zblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.ztzhome.zblog.entity.DTO.AddCommentDTO;
import xyz.ztzhome.zblog.entity.DTO.CommentLikeDTO;
import xyz.ztzhome.zblog.entity.DTO.ReplyCommentDTO;
import xyz.ztzhome.zblog.entity.VO.CommentVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.ICommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    // ========== 评论管理相关接口 ==========

    /**
     * 添加评论
     */
    @PostMapping("/add")
    public ResponseMessage addComment(@RequestBody AddCommentDTO addCommentDTO) {
        return commentService.addComment(addCommentDTO);
    }

    /**
     * 回复评论
     */
    @PostMapping("/reply")
    public ResponseMessage replyComment(@RequestBody ReplyCommentDTO replyCommentDTO) {
        return commentService.replyComment(replyCommentDTO);
    }

    /**
     * 删除评论
     */
    @PostMapping("/delete")
    public ResponseMessage deleteComment(@RequestParam("commentId") long commentId, 
                                       @RequestParam("userId") long userId) {
        return commentService.deleteComment(commentId, userId);
    }

    /**
     * 根据ID获取评论详情
     */
    @GetMapping("/detail")
    public ResponseMessage<CommentVO> getCommentById(@RequestParam("commentId") long commentId, 
                                                    @RequestParam(value = "currentUserId", defaultValue = "0") long currentUserId) {
        return commentService.getCommentById(commentId, currentUserId);
    }

    // ========== 评论查询相关接口 ==========

    /**
     * 根据歌曲ID获取评论列表
     */
    @GetMapping("/song")
    public ResponseMessage<List<CommentVO>> getCommentsBySongId(@RequestParam("songId") long songId, 
                                                               @RequestParam(value = "currentUserId", defaultValue = "0") long currentUserId) {
        return commentService.getCommentsBySongId(songId, currentUserId);
    }

    /**
     * 根据歌曲ID分页获取评论列表
     */
    @GetMapping("/song/page")
    public ResponseMessage<PageResponse<CommentVO>> getCommentsBySongIdWithPage(
            @RequestParam("songId") long songId,
            @RequestParam(value = "currentUserId", defaultValue = "0") long currentUserId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return commentService.getCommentsBySongIdWithPage(songId, currentUserId, pageNum, pageSize);
    }

    /**
     * 获取评论的回复列表
     */
    @GetMapping("/replies")
    public ResponseMessage<List<CommentVO>> getRepliesByCommentId(@RequestParam("commentId") long commentId, 
                                                                 @RequestParam(value = "currentUserId", defaultValue = "0") long currentUserId) {
        return commentService.getRepliesByCommentId(commentId, currentUserId);
    }

    /**
     * 分页获取评论的回复列表
     */
    @GetMapping("/replies/page")
    public ResponseMessage<PageResponse<CommentVO>> getRepliesByCommentIdWithPage(
            @RequestParam("commentId") long commentId,
            @RequestParam(value = "currentUserId", defaultValue = "0") long currentUserId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return commentService.getRepliesByCommentIdWithPage(commentId, currentUserId, pageNum, pageSize);
    }

    /**
     * 获取用户的评论列表
     */
    @GetMapping("/user")
    public ResponseMessage<List<CommentVO>> getCommentsByUserId(@RequestParam("userId") long userId, 
                                                               @RequestParam(value = "currentUserId", defaultValue = "0") long currentUserId) {
        return commentService.getCommentsByUserId(userId, currentUserId);
    }

    /**
     * 分页获取用户的评论列表
     */
    @GetMapping("/user/page")
    public ResponseMessage<PageResponse<CommentVO>> getCommentsByUserIdWithPage(
            @RequestParam("userId") long userId,
            @RequestParam(value = "currentUserId", defaultValue = "0") long currentUserId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return commentService.getCommentsByUserIdWithPage(userId, currentUserId, pageNum, pageSize);
    }

    /**
     * 获取歌曲的评论总数
     */
    @GetMapping("/count")
    public ResponseMessage<Integer> getCommentCountBySongId(@RequestParam("songId") long songId) {
        return commentService.getCommentCountBySongId(songId);
    }

    // ========== 评论点赞相关接口 ==========

    /**
     * 点赞评论
     */
    @PostMapping("/like")
    public ResponseMessage likeComment(@RequestBody CommentLikeDTO commentLikeDTO) {
        return commentService.likeComment(commentLikeDTO);
    }

    /**
     * 取消点赞评论
     */
    @PostMapping("/unlike")
    public ResponseMessage unlikeComment(@RequestBody CommentLikeDTO commentLikeDTO) {
        return commentService.unlikeComment(commentLikeDTO);
    }

    /**
     * 检查用户是否已点赞该评论
     */
    @GetMapping("/like/check")
    public ResponseMessage<Boolean> isUserLikedComment(@RequestParam("commentId") long commentId, 
                                                      @RequestParam("userId") long userId) {
        return commentService.isUserLikedComment(commentId, userId);
    }

    /**
     * 获取评论的点赞数
     */
    @GetMapping("/like/count")
    public ResponseMessage<Integer> getCommentLikeCount(@RequestParam("commentId") long commentId) {
        return commentService.getCommentLikeCount(commentId);
    }
}