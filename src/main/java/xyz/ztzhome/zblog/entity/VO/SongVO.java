package xyz.ztzhome.zblog.entity.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SongVO {

    private long id;

    private long singerId;
    private String singerName;

    private String name;

    private String album;

    private String duration;

    private String style;

    private String coverUrl;

    private String audioUrl;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date releaseTime;

}
