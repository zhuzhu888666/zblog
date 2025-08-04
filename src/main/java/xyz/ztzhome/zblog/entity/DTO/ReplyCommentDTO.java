package xyz.ztzhome.zblog.entity.DTO;

import lombok.Data;

@Data
public class ReplyCommentDTO {
    /**
     * 歌曲ID
     */
    private long songId;
    
    /**
     * 回复用户ID
     */
    private long userId;
    
    /**
     * 父评论ID
     */
    private long parentId;
    
    /**
     * 根评论ID
     */
    private long rootId;
    
    /**
     * 回复的目标用户ID
     */
    private long replyToUserId;
    
    /**
     * 回复内容
     */
    private String content;
}