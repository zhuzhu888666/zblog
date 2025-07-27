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
     * 歌手
     */
    private long singerId;

    /**
     * 歌手名称
     * */
    private String singerName;
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
}
