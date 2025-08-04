package xyz.ztzhome.zblog.entity.Bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserFollowArtist {
    /**
     * 主键id
     */
    private long id;
    
    /**
     * 用户id
     */
    private long userId;
    
    /**
     * 艺术家id
     */
    private long artistId;
    
    /**
     * 关注时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date followTime = new Date();
}