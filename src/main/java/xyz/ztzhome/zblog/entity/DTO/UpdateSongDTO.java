package xyz.ztzhome.zblog.entity.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateSongDTO {
    /**
     * 歌曲 id
     */
    private long id;

    /**
     * 艺术家名称
     * */
    private String artistName;

    /**
     * 艺术家id
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

    //风格
    private String style;

    /**
     * 歌曲封面 url
     */
    private String coverPath;

    /**
     * 歌曲 url
     */
    private String audioPath;

    /**
     * 歌曲发行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date releaseTime;

    /**
     * 播放次数
     */
    private int playCount;
}
