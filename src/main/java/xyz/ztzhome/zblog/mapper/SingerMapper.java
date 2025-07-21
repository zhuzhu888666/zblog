package xyz.ztzhome.zblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.ztzhome.zblog.entity.Bean.Singer;

@Mapper
public interface SingerMapper {
    //插入歌手
    int insertSinger(Singer singer);

    //根据名字查询歌手
    Singer selectBySingerName(String singerName);

    Singer selectBySingerId(long singerId);
}
