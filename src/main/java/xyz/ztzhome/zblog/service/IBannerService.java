package xyz.ztzhome.zblog.service;

import org.springframework.boot.Banner;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

import java.util.List;

public interface IBannerService {
    ResponseMessage<Banner> getBanner(Banner banner);

    ResponseMessage<List<Banner>> getBanners(Banner banner);

    //指定页数
    ResponseMessage<List<Banner>> getBanners(Integer page, Integer rows);
}
