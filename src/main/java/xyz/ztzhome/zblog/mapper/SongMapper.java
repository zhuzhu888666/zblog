package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.entity.VO.SongVO;

import java.util.List;


@Mapper
public interface SongMapper {
    //插入添加歌曲
    int insertSong(Song song);

    //根据id查找歌曲
    Song selectSongById(long songId);

    //根据艺术家id和歌曲名称查询歌曲
    Song selectByArtistIdAndName(@Param("artistId") long artistId,
                                 @Param("songName") String songName);

    //根据歌曲名称模糊查询
    List<Song> selectSongsByNameLike(String songName);
    List<SongVO> selectSongVOsByNameLike(String songName);

    int deleteSong(long id);

    int updateSong(Song song);

    // 新增方法：根据歌名模糊查询，分页
    List<SongVO> selectSongVOsByNameLikeWithPage(@Param("songName") String songName, 
                                                  @Param("offset") int offset, 
                                                  @Param("limit") int limit);

    // 新增方法：查询全部歌曲，分页
    List<SongVO> selectAllSongVOsWithPage(@Param("offset") int offset, 
                                          @Param("limit") int limit);

    // 新增方法：获取歌曲总数
    int countAllSongs();

    // 新增方法：根据歌名模糊查询总数
    int countSongsByNameLike(@Param("songName") String songName);

    // 新增方法：根据风格查询总数
    int countSongsByStyle(@Param("style") String style);

    // 新增方法：随机查询20条歌曲
    List<Song> selectRandomSongs(@Param("limit") int limit);

    //直接返回VO对象
    List<SongVO> selectRandomSongsWithArtist(@Param("limit")int limit);

    // 新增方法：根据风格分页查询
    List<SongVO> selectSongVOsByStyleWithPage(@Param("style") String style, 
                                              @Param("offset") int offset, 
                                              @Param("limit") int limit);

   int updatePlayCount(@Param("count")long count,@Param("id")long id);
}
