package xyz.ztzhome.zblog.entity.DTO;

import lombok.Data;

@Data
public class CommentLikeDTO {
    /**
     * 评论ID
     */
    private long commentId;
    
    /**
     * 点赞用户ID
     */
    private long userId;
}