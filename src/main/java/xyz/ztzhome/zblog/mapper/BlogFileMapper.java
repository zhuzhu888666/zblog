package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.ztzhome.zblog.entity.Bean.BlogFile;
import java.util.List;

@Mapper
public interface BlogFileMapper {
    int insertFile(BlogFile file);
    BlogFile selectFileById(Long id);
    List<BlogFile> selectAllFiles();
    int updateFile(BlogFile file);
    int deleteFileById(Long id);
}
