package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.ztzhome.zblog.entity.Bean.Banner;
import java.util.List;

@Mapper
public interface BannerMapper {
    int insert(Banner banner);
    Banner selectById(Long id);
    List<Banner> selectAll();
    int update(Banner banner);
    int deleteById(Long id);
    List<Banner> findActiveBanners();
} 