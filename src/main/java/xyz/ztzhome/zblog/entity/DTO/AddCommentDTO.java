package xyz.ztzhome.zblog.entity.DTO;

import lombok.Data;

@Data
public class AddCommentDTO {
    /**
     * 歌曲ID
     */
    private long songId;
    
    /**
     * 评论用户ID
     */
    private long userId;
    
    /**
     * 评论内容
     */
    private String content;
}