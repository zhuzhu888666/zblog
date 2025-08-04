package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.ztzhome.zblog.entity.Bean.Comment;
import xyz.ztzhome.zblog.entity.VO.CommentVO;

import java.util.List;

@Mapper
public interface CommentMapper {
    
    /**
     * 添加评论
     */
    int insertComment(Comment comment);
    
    /**
     * 删除评论（软删除，更新状态为0）
     */
    int deleteComment(@Param("id") long id, @Param("userId") long userId);
    
    /**
     * 根据ID查询评论
     */
    Comment selectCommentById(@Param("id") long id);
    
    /**
     * 根据歌曲ID查询顶级评论列表（不包含回复）
     */
    List<CommentVO> selectTopCommentsBySongId(@Param("songId") long songId, 
                                              @Param("currentUserId") long currentUserId);
    
    /**
     * 根据歌曲ID分页查询顶级评论列表
     */
    List<CommentVO> selectTopCommentsBySongIdWithPage(@Param("songId") long songId, 
                                                      @Param("currentUserId") long currentUserId,
                                                      @Param("offset") int offset, 
                                                      @Param("limit") int limit);
    
    /**
     * 根据父评论ID查询回复列表
     */
    List<CommentVO> selectRepliesByParentId(@Param("parentId") long parentId, 
                                            @Param("currentUserId") long currentUserId);
    
    /**
     * 根据根评论ID查询所有回复
     */
    List<CommentVO> selectRepliesByRootId(@Param("rootId") long rootId, 
                                          @Param("currentUserId") long currentUserId);
    
    /**
     * 分页查询某个评论的回复
     */
    List<CommentVO> selectRepliesByParentIdWithPage(@Param("parentId") long parentId, 
                                                    @Param("currentUserId") long currentUserId,
                                                    @Param("offset") int offset, 
                                                    @Param("limit") int limit);
    
    /**
     * 查询歌曲的评论总数
     */
    int countCommentsBySongId(@Param("songId") long songId);
    
    /**
     * 查询某个评论的回复总数
     */
    int countRepliesByParentId(@Param("parentId") long parentId);
    
    /**
     * 更新评论的点赞数
     */
    int updateCommentLikeCount(@Param("commentId") long commentId, @Param("increment") int increment);
    
    /**
     * 更新评论的回复数
     */
    int updateCommentReplyCount(@Param("commentId") long commentId, @Param("increment") int increment);
    
    /**
     * 根据用户ID查询用户的评论列表
     */
    List<CommentVO> selectCommentsByUserId(@Param("userId") long userId, 
                                           @Param("currentUserId") long currentUserId);
    
    /**
     * 分页查询用户的评论列表
     */
    List<CommentVO> selectCommentsByUserIdWithPage(@Param("userId") long userId, 
                                                   @Param("currentUserId") long currentUserId,
                                                   @Param("offset") int offset, 
                                                   @Param("limit") int limit);
    
    /**
     * 查询用户评论总数
     */
    int countCommentsByUserId(@Param("userId") long userId);
}