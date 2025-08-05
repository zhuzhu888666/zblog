package xyz.ztzhome.zblog.entity.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PlayListDetailVO {
    /**
     * 歌单id
     */
    private long id;
    
    /**
     * 创建者用户id
     */
    private long userId;
    
    /**
     * 创建者用户名
     */
    private String userName;
    
    /**
     * 歌单名称
     */
    private String name;
    
    /**
     * 歌单描述
     */
    private String description;
    
    /**
     * 歌单封面路径
     */
    private String coverPath;
    
    /**
     * 是否公开（0-私有，1-公开）
     */
    private int isPublic;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
    /**
     * 歌曲数量
     */
    private int songCount;
    
    /**
     * 收藏数量
     */
    private int favoriteCount;
    
    /**
     * 歌单中的歌曲列表
     */
    private List<SongVO> songs;
}