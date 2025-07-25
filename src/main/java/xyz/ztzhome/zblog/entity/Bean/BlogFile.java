package xyz.ztzhome.zblog.entity.Bean;

import lombok.Data;
import java.util.Date;

@Data
public class BlogFile {
    private long id;
    private String fileName;
    private String originalName;
    private String filePath;
    private String fileType;
    private long fileSize;
    private Date createTime=new Date();
} 