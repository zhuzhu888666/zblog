package xyz.ztzhome.zblog.entity.DTO;

import lombok.Data;

@Data
public class FavoritePlayListDTO {
    /**
     * 用户id
     */
    private long userId;
    
    /**
     * 歌单id
     */
    private long playListId;
}