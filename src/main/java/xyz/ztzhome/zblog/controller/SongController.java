package xyz.ztzhome.zblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.entity.DTO.AddSongDTO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.impl.SongServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/song")
public class SongController {
    @Autowired
    SongServiceImpl songService;


    @PostMapping("addSong")
    public ResponseMessage addSong(@RequestPart("data") String data, @RequestPart("audioFile") MultipartFile audioFile){

        AddSongDTO addSongDTO = new AddSongDTO();
        try {
            ObjectMapper mapper = new ObjectMapper();
            addSongDTO = mapper.readValue(data, AddSongDTO.class);

            return songService.addSong(addSongDTO,audioFile);
        }catch (Exception e){
            return new ResponseMessage(0,"接收对象构建失败:"+e.getMessage());
        }
    }

    //根据id查找歌曲
    @GetMapping("/selectSongById")
    @PostMapping
    ResponseMessage<SongVO> getSongById(@RequestParam("id") long id){
        return songService.getSong(id);
    }

    @GetMapping("/selectSongsByName")
    @PostMapping
    //根据名称模糊查找
    ResponseMessage<List<SongVO>> getSongsByName(@RequestParam("songName") String songName){
        return songService.getSongsByName(songName);
    }

    //更新歌曲

    @PostMapping("/updateSong")
    ResponseMessage updateSong(){
        return null;
    }

    //删除歌曲
    @GetMapping("/deleteSong")
    @PostMapping
    ResponseMessage deleteSongById(@RequestParam("id") long id){
        return songService.deleteSong(id);
    }

    //获取歌曲临时路径
    ResponseMessage getSongURL(@RequestParam("id") long id){
        return songService.getSongURL(id);
    }
}
