package xyz.ztzhome.zblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.impl.LyricServiceImpl;

@RequestMapping("/api/lyrics")
@RestController
public class SongLyricController {
    @Autowired
    LyricServiceImpl lrcService;

    @GetMapping
    public ResponseMessage getSongLrc(@RequestParam("id")long id) {
        return lrcService.getLrc(id);
    }
    //上传歌曲歌词
    @PostMapping
    public ResponseMessage uploadSongLRC(@RequestParam("id") String id,@RequestParam("lrcFile") MultipartFile lrcFile){
        long songId = Long.parseLong(id);
        return lrcService.uploadLrc(songId,lrcFile);
    }
    //更新歌词
    @PostMapping("/update")
    public ResponseMessage updateSongLRC(@RequestParam("id") String id,@RequestParam("file") MultipartFile file){
        long songId = Long.parseLong(id);
        return lrcService.updateLrc(songId,file);
    }
    @PostMapping("/delete")
    public ResponseMessage deleteSongLRC(@RequestParam("id") long id){
        return lrcService.deleteLrc(id);
    }
}
