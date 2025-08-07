package xyz.ztzhome.zblog.entity.DTO;

import lombok.Data;

@Data
public class CreatePlayListDTO {
    /**
     * 创建者用户id
     */
    private long userId;
    
    /**
     * 歌单名称
     */
    private String name;
    
    /**
     * 歌单描述
     */
    private String description;
    
    /**
     * 是否公开
     */
    private Boolean isPublic = true;
}