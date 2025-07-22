package xyz.ztzhome.zblog.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.DTO.AddSongDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateSongDTO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

import java.util.List;

public interface ISongService {

    //添加歌曲
    ResponseMessage addSong(AddSongDTO addSongDTO, MultipartFile audioFile);

    //根据歌曲id查询歌曲
   ResponseMessage<SongVO> getSong(long songId);

   //根据名称模糊查询，获取歌曲集合
    ResponseMessage<List<SongVO>> getSongsByName(String songName);

    //获取歌曲临时路径
    ResponseMessage<String> getSongURL(long id);

    //更新歌曲
    ResponseMessage updateSong(UpdateSongDTO updateSongDTO);


    ResponseMessage deleteSong(long id);
}
