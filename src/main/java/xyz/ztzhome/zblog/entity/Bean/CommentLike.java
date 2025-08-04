package xyz.ztzhome.zblog.entity.Bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class CommentLike {
    /**
     * 主键ID
     */
    private long id;
    
    /**
     * 评论ID
     */
    private long commentId;
    
    /**
     * 点赞用户ID
     */
    private long userId;
    
    /**
     * 点赞时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date likeTime = new Date();
}