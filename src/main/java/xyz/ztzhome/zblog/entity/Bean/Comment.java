package xyz.ztzhome.zblog.entity.Bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    /**
     * 评论ID
     */
    private long id;
    
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
    
    /**
     * 父评论ID（用于回复功能，0表示顶级评论）
     */
    private long parentId = 0;
    
    /**
     * 根评论ID（用于标识回复链的根评论）
     */
    private long rootId = 0;
    
    /**
     * 回复的目标用户ID（回复评论时使用）
     */
    private long replyToUserId = 0;
    
    /**
     * 点赞数
     */
    private int likeCount = 0;
    
    /**
     * 回复数
     */
    private int replyCount = 0;
    
    /**
     * 评论状态：0-已删除 1-正常 2-审核中
     */
    private int status = 1;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime = new Date();
}