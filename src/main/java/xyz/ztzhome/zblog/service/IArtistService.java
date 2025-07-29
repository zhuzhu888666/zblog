package xyz.ztzhome.zblog.service;

import xyz.ztzhome.zblog.entity.Bean.Artist;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

public interface IArtistService {
    ResponseMessage<Artist> getArtistById(Integer id);
}
