package xyz.ztzhome.zblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.DTO.AddSongDTO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.impl.SongServiceImpl;

@RestController
@RequestMapping("/song")
public class SongController {
    @Autowired
    SongServiceImpl songService;


    @PostMapping("addSong")
    public ResponseMessage addSong(@RequestPart("data") String data, @RequestPart("audioFile") MultipartFile audioFile){

        System.out.println(data);

        AddSongDTO addSongDTO = new AddSongDTO();
        try {
            ObjectMapper mapper = new ObjectMapper();
            addSongDTO = mapper.readValue(data, AddSongDTO.class);

            return songService.addSong(addSongDTO,audioFile);
        }catch (Exception e){
            return new ResponseMessage(0,"接收对象构建失败:"+e.getMessage());
        }
    }
}
