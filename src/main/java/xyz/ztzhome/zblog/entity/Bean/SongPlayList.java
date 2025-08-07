package xyz.ztzhome.zblog.entity.Bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SongPlayList {
    /**
     * 歌单id
     */
    private long id;
    
    /**
     * 创建者用户id
     */
    private long userId;
    
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
     * 是否公开
     */
    private Boolean isPublic = true;
    
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
