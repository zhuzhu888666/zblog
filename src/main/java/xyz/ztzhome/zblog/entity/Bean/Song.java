package xyz.ztzhome.zblog.entity.Bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class Song {
    /**
     * 歌曲 id
     */
    private long id;

    /**
     * 艺术家
     */
    private long artistId;
    /**
     * 歌名
     */
    private String name;

    /**
     * 专辑
     */
    private String album;

    /**
     * 歌曲时长
     */
    private String duration;

    //歌曲风格
    private String style;

    /**
     * 歌曲封面 路径+类型后缀
     */
    private String coverPath;

    /**
     * 歌曲 路径+类型后缀
     */
    private String audioPath;

    //
    private String lyricPath;

    /**
     * 歌曲发行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date releaseTime;

    /**
     * 播放次数
     */
    private long playCount;

}
