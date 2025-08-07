package xyz.ztzhome.zblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.entity.DTO.AddSongDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateSongDTO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.impl.SongServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/song")
public class SongController {
    @Autowired
    SongServiceImpl songService;


    @PostMapping("/addSong")
    public ResponseMessage addSong(@RequestPart("data") String data, @RequestPart("audioFile") MultipartFile audioFile, @RequestPart(value = "coverFile", required = false) MultipartFile coverFile) {
        AddSongDTO addSongDTO = new AddSongDTO();
        try {
            ObjectMapper mapper = new ObjectMapper();
            addSongDTO = mapper.readValue(data, AddSongDTO.class);
            return songService.addSong(addSongDTO, audioFile, coverFile);
        } catch (Exception e) {
            return new ResponseMessage(0, "接收对象构建失败:" + e.getMessage());
        }
    }

    //根据id查找歌曲
    @GetMapping("/selectSongById")
    @PostMapping
    ResponseMessage<SongVO> getSongById(@RequestParam("id") long id){
        return songService.getSong(id);
    }

    @GetMapping("/selectSongsByName")
    //根据名称模糊查找
    ResponseMessage<List<SongVO>> getSongsByName(@RequestParam("songName") String songName){
        return songService.getSongsByName(songName);
    }

    //更新歌曲
    @PostMapping("/updateSong")
    public ResponseMessage updateSong(@RequestPart("data") String data, @RequestPart(value = "coverFile", required = false) MultipartFile coverFile) {
        UpdateSongDTO updateSongDTO = new UpdateSongDTO();
        try {
            ObjectMapper mapper = new ObjectMapper();
            updateSongDTO = mapper.readValue(data, UpdateSongDTO.class);
            return songService.updateSong(updateSongDTO, coverFile);
        } catch (Exception e) {
            return new ResponseMessage(0, "接收对象构建失败:" + e.getMessage());
        }
    }

    //删除歌曲
    @GetMapping("/deleteSong")
    @PostMapping
    ResponseMessage deleteSongById(@RequestParam("id") long id){
        return songService.deleteSong(id);
    }

    //获取歌曲临时路径
    @GetMapping("/getSongURL")
    public ResponseMessage getSongURL(@RequestParam("id") long id){
        return songService.getSongURL(id);
    }

    /**
     * 获取歌曲封面临时路径
     * @param id 歌曲id
     * @return 封面url
     */
    @GetMapping("/getCoverURL")
    public ResponseMessage getCoverURL(@RequestParam("id") long id) {
        return songService.getCoverURL(id);
    }

    /**
     * 根据歌名模糊查询，分页
     * @param songName 歌曲名称
     * @param pageNum 页码，默认1
     * @param pageSize 每页大小，默认10
     * @return 分页结果
     */
    @GetMapping("/searchByNameWithPage")
    public ResponseMessage<PageResponse<SongVO>> searchByNameWithPage(
            @RequestParam("songName") String songName,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return songService.getSongsByNameWithPage(songName, pageNum, pageSize);
    }

    /**
     * 查询全部歌曲，分页
     * @param pageNum 页码，默认1
     * @param pageSize 每页大小，默认10
     * @return 分页结果
     */
    @GetMapping("/getAllSongsWithPage")
    public ResponseMessage<PageResponse<SongVO>> getAllSongsWithPage(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return songService.getAllSongsWithPage(pageNum, pageSize);
    }

    /**
     * 随机查询歌曲
     * @param limit 查询数量，默认20
     * @return 歌曲列表
     */
    @GetMapping("/getRandomSongs")
    public ResponseMessage<List<SongVO>> getRandomSongs(
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return songService.getRandomSongs(limit);
    }

    /**
     * 根据风格分页查询
     * @param style 歌曲风格
     * @param pageNum 页码，默认1
     * @param pageSize 每页大小，默认10
     * @return 分页结果
     */
    @GetMapping("/searchByStyleWithPage")
    public ResponseMessage<PageResponse<SongVO>> searchByStyleWithPage(
            @RequestParam("style") String style,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return songService.getSongsByStyleWithPage(style, pageNum, pageSize);
    }
    //增加歌曲播放次数
    @GetMapping("/incrementPlayCount")
    public ResponseMessage addPlayCount(@RequestParam("id") long id) {
        return songService.addPlayCount(id);
    }
}
