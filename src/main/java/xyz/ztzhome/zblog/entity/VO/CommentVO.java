package xyz.ztzhome.zblog.entity.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentVO {
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
     * 评论用户昵称
     */
    private String userNickname;
    
    /**
     * 评论用户头像
     */
    private String userAvatar;
    
    /**
     * 评论内容
     */
    private String content;
    
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
     * 回复的目标用户昵称
     */
    private String replyToUserNickname;
    
    /**
     * 点赞数
     */
    private int likeCount;
    
    /**
     * 回复数
     */
    private int replyCount;
    
    /**
     * 评论状态
     */
    private int status;
    
    /**
     * 当前用户是否已点赞该评论
     */
    private boolean isLiked = false;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    /**
     * 子评论列表（回复）
     */
    private List<CommentVO> replies;
}