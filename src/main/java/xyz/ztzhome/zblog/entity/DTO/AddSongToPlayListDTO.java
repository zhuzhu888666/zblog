package xyz.ztzhome.zblog.entity.DTO;

import lombok.Data;

@Data
public class AddSongToPlayListDTO {
    /**
     * 歌单id
     */
    private long playListId;
    
    /**
     * 歌曲id
     */
    private long songId;
}