package xyz.ztzhome.zblog.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.Bean.Banner;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.IBannerService;
import java.util.List;

@RestController
@RequestMapping("/banners")
public class BannerController {

    @Autowired
    private IBannerService bannerService;

    @PostMapping
    public ResponseMessage<Banner> createBanner(@RequestPart("file") MultipartFile file,
                                                @RequestParam("title") String title,
                                                @RequestParam(value = "linkUrl", required = false) String linkUrl,
                                                @RequestParam(value = "sortOrder", defaultValue = "0") Integer sortOrder,
                                                @RequestParam(value = "isActive", defaultValue = "true") Boolean isActive) {
        return bannerService.createBanner(file, title, linkUrl, sortOrder, isActive);
    }

    @PutMapping("/{id}")
    public ResponseMessage<Banner> updateBanner(@PathVariable Long id,
                                                @RequestPart(value = "file", required = false) MultipartFile file,
                                                @RequestParam(value = "title", required = false) String title,
                                                @RequestParam(value = "linkUrl", required = false) String linkUrl,
                                                @RequestParam(value = "sortOrder", required = false) Integer sortOrder,
                                                @RequestParam(value = "isActive", required = false) Boolean isActive) {
        return bannerService.updateBanner(id, file, title, linkUrl, sortOrder, isActive);
    }

    @DeleteMapping("/{id}")
    public ResponseMessage<Void> deleteBanner(@PathVariable Long id) {
        return bannerService.deleteBanner(id);
    }

    @GetMapping
    public ResponseMessage<PageInfo<Banner>> getAllBanners(@RequestParam(defaultValue = "1") int pageNum,
                                                             @RequestParam(defaultValue = "10") int pageSize) {
        return bannerService.getAllBanners(pageNum, pageSize);
    }

    @GetMapping("/random")
    public ResponseMessage<List<String>> getRandomBanners() {
        return bannerService.getRandomBanners();
    }
} 