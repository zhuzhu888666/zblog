package xyz.ztzhome.zblog.entity.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class AddArtistDTO {
    private String artistName;
    private String gender;
    //头像
    private String avatar;
    //出生日期
    private Date birth;
    //国家/地区
    private String area;
    //简介
    private String introduction;
}
