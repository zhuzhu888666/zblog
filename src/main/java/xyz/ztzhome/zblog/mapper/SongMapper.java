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
    //根据名称查询歌手
    Song selectBySingerName(String songName);

    Song selectBySingerIdAndName(@Param("singerId") long singerId,
                                 @Param("songName") String songName);

}
