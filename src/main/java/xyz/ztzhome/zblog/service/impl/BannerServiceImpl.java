package xyz.ztzhome.zblog.service.impl;

import org.springframework.boot.Banner;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.IBannerService;

import java.util.List;

public class BannerServiceImpl implements IBannerService {
    @Override
    public ResponseMessage<Banner> getBanner(String path) {
        return null;
    }

    //
    @Override
    public ResponseMessage<List<Banner>> getBanners(Banner banner) {
        return null;
    }

    @Override
    public ResponseMessage<List<Banner>> getBanners(Integer page, Integer rows) {
        return null;
    }
}
