package xyz.ztzhome.zblog.service;

import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.Bean.Banner;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import java.util.List;

public interface IBannerService {
    ResponseMessage<Banner> createBanner(MultipartFile file, String title, String linkUrl, Integer sortOrder, Boolean isActive);
    ResponseMessage<Banner> updateBanner(Long id, MultipartFile file, String title, String linkUrl, Integer sortOrder, Boolean isActive);
    ResponseMessage<Void> deleteBanner(Long id);
    ResponseMessage<PageInfo<Banner>> getAllBanners(int pageNum, int pageSize);
    ResponseMessage<List<String>> getRandomBanners();
}
