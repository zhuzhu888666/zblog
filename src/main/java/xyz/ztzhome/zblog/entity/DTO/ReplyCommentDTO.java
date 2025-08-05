package xyz.ztzhome.zblog.entity.DTO;

import lombok.Data;

@Data
public class ReplyCommentDTO {
    /**
     * 要回复的评论ID
     */
    private long commentId;
    
    /**
     * 回复用户ID
     */
    private long userId;
    
    /**
     * 回复的目标用户ID
     */
    private long replyToUserId;
    
    /**
     * 回复内容
     */
    private String content;
}