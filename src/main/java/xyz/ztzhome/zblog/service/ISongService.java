package xyz.ztzhome.zblog.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.entity.DTO.AddSongDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateSongDTO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

import java.util.List;

public interface ISongService {

    //添加歌曲
    ResponseMessage addSong(AddSongDTO addSongDTO, MultipartFile audioFile, MultipartFile coverFile);

    //根据歌曲id查询歌曲
   ResponseMessage<SongVO> getSong(long songId);

   //根据名称模糊查询，获取歌曲集合
    ResponseMessage<List<SongVO>> getSongsByName(String songName);

    //获取歌曲临时路径
    ResponseMessage<String> getSongURL(long id);

    //更新歌曲
    ResponseMessage updateSong(UpdateSongDTO updateSongDTO, MultipartFile coverFile);

    ResponseMessage deleteSong(long id);

    // 新增方法：根据歌名模糊查询，分页
    ResponseMessage<PageResponse<SongVO>> getSongsByNameWithPage(String songName, int pageNum, int pageSize);

    // 新增方法：查询全部歌曲，分页
    ResponseMessage<PageResponse<SongVO>> getAllSongsWithPage(int pageNum, int pageSize);

    // 新增方法：随机查询20条歌曲
    ResponseMessage<List<SongVO>> getRandomSongs(int limit);

    // 新增方法：根据风格分页查询
    ResponseMessage<PageResponse<SongVO>> getSongsByStyleWithPage(String style, int pageNum, int pageSize);

    ResponseMessage getCoverURL(long id);

    ResponseMessage addPlayCount(long id);
}
