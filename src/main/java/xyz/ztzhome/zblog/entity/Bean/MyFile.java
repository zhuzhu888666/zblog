package xyz.ztzhome.zblog.entity.Bean;

import lombok.Data;

import java.util.Date;

@Data
public class MyFile {
    private long id;
    private String fileName;
    private String originalName;
    private String filePath;
    private String fileType;
    private String fileSize;
    private Date createTime=new Date();
}
