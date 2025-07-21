package xyz.ztzhome.zblog.entity.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import xyz.ztzhome.zblog.entity.Bean.Singer;
import xyz.ztzhome.zblog.entity.Bean.Song;

@Data
//忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddSongDTO {

    private Song song;
    private Singer singer;
}
