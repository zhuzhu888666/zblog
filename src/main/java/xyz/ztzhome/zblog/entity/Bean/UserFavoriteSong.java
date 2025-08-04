package xyz.ztzhome.zblog.entity.Bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserFavoriteSong {
    /**
     * 主键id
     */
    private long id;
    
    /**
     * 用户id
     */
    private long userId;
    
    /**
     * 歌曲id
     */
    private long songId;
    
    /**
     * 收藏时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date favoriteTime = new Date();
}