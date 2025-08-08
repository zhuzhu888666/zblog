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

    /**
     * 前端直接使用的封面路径
     * - 若为默认封面，返回静态资源路径：/files/image/default_cover.jpg
     * - 若为自定义封面，返回 MinIO 预签名 URL
     */
    private String coverPath;
}
