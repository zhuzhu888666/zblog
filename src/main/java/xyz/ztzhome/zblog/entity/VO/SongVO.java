package xyz.ztzhome.zblog.entity.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SongVO {

    private long id;

    private long artistId;

    private String artistName;

    private String name;

    private String album;

    private String duration;

    private String style;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date releaseTime;

    /**
     * 播放次数
     */
    private int playCount;

}
