package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.ztzhome.zblog.entity.Bean.Artist;

@Mapper
public interface ArtistMapper {

    int insertArtist(Artist artist);

    Artist getArtistById(long id);

    Artist selectByArtistName(String artistName);

    Artist selectByArtistId(long artistId);
} 