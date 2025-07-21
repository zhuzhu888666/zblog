package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.ztzhome.zblog.entity.Bean.Song;


@Mapper
public interface SongMapper {
    //插入添加歌曲
    int insertSong(Song song);

    //根据id查找歌曲
    Song selectSongById(long songId);

    //根据歌手id和歌曲名称查询歌曲
    Song selectBySingerIdAndName(@Param("singerId") long singerId,
                                 @Param("songName") String songName);

}
