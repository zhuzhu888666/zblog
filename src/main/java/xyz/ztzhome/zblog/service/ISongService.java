package xyz.ztzhome.zblog.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.entity.DTO.AddSongDTO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

public interface ISongService {
    Song getSong(long songId);

    ResponseMessage addSong(AddSongDTO addSongDTO, MultipartFile audioFile);
}
