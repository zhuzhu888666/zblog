package xyz.ztzhome.zblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.impl.ArtistServiceImpl;

@RequestMapping("/artis")
@RestController
public class ArtistController {

    @Autowired
    ArtistServiceImpl artistService;
    @GetMapping
    public ResponseMessage getArtiById(@RequestParam("id") Integer id){
        return artistService.getArtistById(id);
    }
}
