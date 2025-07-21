package xyz.ztzhome.zblog.entity.Bean;

import lombok.Data;

import java.util.Date;

@Data
public class Singer {
    //id
    private long id;
    private String SingerName;
    private String gender;
    //头像URL
    private String avatar;
    //出生日期
    private Date birth;
    //国家/地区
    private String area;
    //简介
    private String introduction;
}
