package xyz.ztzhome.zblog.entity.Bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SongPlayListSong {
    /**
     * 主键id
     */
    private long id;
    
    /**
     * 歌单id
     */
    private long playListId;
    
    /**
     * 歌曲id
     */
    private long songId;
    
    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addTime = new Date();
    
    /**
     * 歌曲在歌单中的排序
     */
    private int sortOrder = 0;
}