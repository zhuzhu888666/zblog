package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.ztzhome.zblog.entity.Bean.CommentLike;

@Mapper
public interface CommentLikeMapper {
    
    /**
     * 添加评论点赞
     */
    int insertCommentLike(CommentLike commentLike);
    
    /**
     * 取消评论点赞
     */
    int deleteCommentLike(@Param("commentId") long commentId, @Param("userId") long userId);
    
    /**
     * 检查用户是否已点赞该评论
     */
    boolean isUserLikedComment(@Param("commentId") long commentId, @Param("userId") long userId);
    
    /**
     * 统计评论的点赞数
     */
    int countLikesByCommentId(@Param("commentId") long commentId);
    
    /**
     * 批量检查用户对多个评论的点赞状态
     */
    @SuppressWarnings("rawtypes")
    java.util.List<Long> selectLikedCommentIds(@Param("commentIds") java.util.List commentIds, @Param("userId") long userId);
}