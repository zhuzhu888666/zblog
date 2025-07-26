package xyz.ztzhome.zblog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.Banner;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.BannerMapper;
import xyz.ztzhome.zblog.service.IBannerService;
import xyz.ztzhome.zblog.service.IMinioService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BannerServiceImpl implements IBannerService {

    private static final Logger logger = LoggerFactory.getLogger(BannerServiceImpl.class);

    @Autowired
    private BannerMapper bannerMapper;

    @Autowired
    private IMinioService minioService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public ResponseMessage<Banner> createBanner(MultipartFile file, String title, String linkUrl, Integer sortOrder, Boolean isActive) {
        if (file == null || file.isEmpty()) {
            return new ResponseMessage<>(ResponseConstant.error, "文件不能为空");
        }
        try {
            String objectName = "banners/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            minioService.uploadFile(file, objectName);

            Banner banner = new Banner();
            banner.setTitle(title);
            banner.setObjectName(objectName);
            banner.setLinkUrl(linkUrl);
            banner.setSortOrder(sortOrder);
            banner.setIsActive(isActive);
            bannerMapper.insert(banner);
            
            logger.info("创建新的轮播图成功: {}", title);
            return new ResponseMessage<>(ResponseConstant.success, "创建成功", banner);
        } catch (Exception e) {
            logger.error("创建轮播图失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常，创建失败");
        }
    }

    @Override
    @Transactional
    public ResponseMessage<Banner> updateBanner(Long id, MultipartFile file, String title, String linkUrl, Integer sortOrder, Boolean isActive) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            return new ResponseMessage<>(ResponseConstant.error, "轮播图不存在");
        }

        try {
            if (file != null && !file.isEmpty()) {
                minioService.removeFile(banner.getObjectName());
                String objectName = "banners/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
                minioService.uploadFile(file, objectName);
                banner.setObjectName(objectName);
            }

            banner.setTitle(title != null ? title : banner.getTitle());
            banner.setLinkUrl(linkUrl != null ? linkUrl : banner.getLinkUrl());
            banner.setSortOrder(sortOrder != null ? sortOrder : banner.getSortOrder());
            banner.setIsActive(isActive != null ? isActive : banner.getIsActive());
            bannerMapper.update(banner);
            
            logger.info("更新轮播图成功, ID: {}", id);
            return new ResponseMessage<>(ResponseConstant.success, "更新成功", banner);
        } catch (Exception e) {
            logger.error("更新轮播图失败, ID: " + id, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常，更新失败");
        }
    }

    @Override
    @Transactional
    public ResponseMessage<Void> deleteBanner(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            return new ResponseMessage<>(ResponseConstant.error, "轮播图不存在");
        }
        try {
            minioService.removeFile(banner.getObjectName());
            bannerMapper.deleteById(id);
            logger.info("删除轮播图成功, ID: {}", id);
            return new ResponseMessage<>(ResponseConstant.success, "删除成功");
        } catch (Exception e) {
            logger.error("删除轮播图失败, ID: " + id, e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常，删除失败");
        }
    }

    @Override
    public ResponseMessage<PageInfo<Banner>> getAllBanners(int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<Banner> banners = bannerMapper.selectAll();
            PageInfo<Banner> pageInfo = new PageInfo<>(banners);
            
            pageInfo.getList().forEach(banner -> {
                String bannerUrl = getBannerUrlWithCache(banner.getObjectName(), 7, TimeUnit.DAYS);
                banner.setImageUrl(bannerUrl);
            });

            return new ResponseMessage<>(ResponseConstant.success, "查询成功", pageInfo);
        } catch (Exception e) {
            logger.error("分页查询轮播图失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常，查询失败");
        }
    }

    @Override
    public ResponseMessage<List<String>> getRandomBanners() {
        try {
            List<Banner> activeBanners = bannerMapper.findActiveBanners();
            Collections.shuffle(activeBanners);
            
            List<String> randomUrls = activeBanners.stream()
                .limit(10)
                .map(banner -> getBannerUrlWithCache(banner.getObjectName(), 1, TimeUnit.HOURS))
                .filter(url -> url != null && !url.startsWith("getURL_error:"))
                .collect(Collectors.toList());
            
            return new ResponseMessage<>(ResponseConstant.success, "查询成功", randomUrls);
        } catch (Exception e) {
            logger.error("获取随机轮播图失败", e);
            return new ResponseMessage<>(ResponseConstant.error, "服务异常，查询失败");
        }
    }

    private String getBannerUrlWithCache(String objectName, int duration, TimeUnit timeUnit) {
        String redisKey = "banner:url:" + objectName;
        String cachedUrl = redisTemplate.opsForValue().get(redisKey);

        if (cachedUrl != null) {
            logger.debug("缓存命中，直接返回Banner URL: {}", objectName);
            return cachedUrl;
        }

        logger.debug("缓存未命中，从MinIO生成新的Banner URL: {}", objectName);
        try {
            String newUrl = minioService.getFileUrl(duration, objectName, timeUnit);
            if (newUrl != null && !newUrl.startsWith("getURL_error:")) {
                // Set cache expiration slightly shorter than the URL's expiration
                long cacheExpiration = timeUnit.toSeconds(duration) - 60; // 缓存提前60秒过期
                if (cacheExpiration > 0) {
                    redisTemplate.opsForValue().set(redisKey, newUrl, cacheExpiration, TimeUnit.SECONDS);
                }
            }
            return newUrl;
        } catch (Exception e) {
            logger.error("从MinIO获取Banner URL时发生异常: " + objectName, e);
            return null;
        }
    }
}
