package xyz.ztzhome.zblog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

@RequestMapping("/banner")
@RestController
public class BannerCntroller {

    @GetMapping("/getBanners")
    ResponseMessage getBanners(){

        return new ResponseMessage<>();
    }
}
