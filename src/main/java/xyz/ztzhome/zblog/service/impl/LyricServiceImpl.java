package xyz.ztzhome.zblog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.constant.PathCosntant;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.SongMapper;
import xyz.ztzhome.zblog.service.ILyricService;
import xyz.ztzhome.zblog.util.FileTypeUtil;

@Service
public class LyricServiceImpl implements ILyricService {
    @Autowired
    SongMapper songMapper;
    @Autowired
    MinioServiceImpl minioService;
    @Override
    public ResponseMessage getLrc(long id) {
        Song song=songMapper.selectSongById(id);
        if(song==null){
            return new ResponseMessage<>(ResponseConstant.error,"歌曲不存在");
        }
        if (song.getLyricPath()==null || song.getLyricPath().isEmpty()){
            return new ResponseMessage<>(ResponseConstant.error,"该歌曲暂无歌词");
        }
        String fullPath = PathCosntant.SONG_LYRIC_PATH + song.getLyricPath();
        String url = minioService.getFileUrl(ResponseConstant.Time_Out, fullPath);
        if (url == null) {
            return new ResponseMessage<>(ResponseConstant.error,"存储库未找到该歌词");
        }
        return new ResponseMessage<>(ResponseConstant.success,"加载成功", url);
    }

    @Override
    public ResponseMessage uploadLrc(long id, MultipartFile lrcFile) {
        Song song=songMapper.selectSongById(id);
        if(song==null){
            return new ResponseMessage<>(ResponseConstant.error,"歌曲不存在");
        }
       try {
           if (lrcFile == null || lrcFile.isEmpty()) {
               return new ResponseMessage<>(ResponseConstant.error, "歌词文件不能为空");
           }
           String fileName = lrcFile.getOriginalFilename();
           // 仅允许lrc或text类
           String fileType = FileTypeUtil.getFileType(fileName);
           if (!"text".equals(fileType) && !fileName.toLowerCase().endsWith(".lrc")) {
               return new ResponseMessage<>(ResponseConstant.error, "仅支持.lrc或文本歌词文件");
           }
           // 歌曲id+名称保存：music/lrc/{id}_{name}.lrc
           String objectName = song.getId() + "_" + song.getName() + FileTypeUtil.getFileExtension2(fileName);
           String fullPath = PathCosntant.SONG_LYRIC_PATH + objectName;
           int upload = minioService.uploadFile(lrcFile, fullPath);
           if (upload != 1) {
               return new ResponseMessage<>(ResponseConstant.error, "上传失败");
           }
           // 持久化相对路径（不含前缀），与封面一致
           Song newSong=new Song();
           newSong.setId(song.getId());
           newSong.setLyricPath(objectName);
           songMapper.updateSong(newSong);
           return new ResponseMessage<>(ResponseConstant.success,"上传成功");
       }catch (Exception e){
           return new ResponseMessage<>(ResponseConstant.error,"上传歌词异常："+ e.getMessage());
       }
    }

    @Override
    public ResponseMessage updateLrc(long id, MultipartFile file) {
        Song song = songMapper.selectSongById(id);
        if (song == null) {
            return new ResponseMessage<>(ResponseConstant.error, "歌曲不存在");
        }
        if (file == null || file.isEmpty()) {
            return new ResponseMessage<>(ResponseConstant.error, "歌词文件不能为空");
        }
        try {
            // 删除旧歌词（若存在）
            if (song.getLyricPath() != null && !song.getLyricPath().isEmpty()) {
                String oldFullPath = PathCosntant.SONG_LYRIC_PATH + song.getLyricPath();
                minioService.deleteFile(oldFullPath);
            }
            // 上传新歌词
            String fileName = file.getOriginalFilename();
            String objectName = song.getId() + "_" + song.getName() + FileTypeUtil.getFileExtension2(fileName);
            String fullPath = PathCosntant.SONG_LYRIC_PATH + objectName;
            int upload = minioService.uploadFile(file, fullPath);
            if (upload != 1) {
                return new ResponseMessage<>(ResponseConstant.error, "上传失败");
            }
            // 更新数据库
            Song update = new Song();
            update.setId(song.getId());
            update.setLyricPath(objectName);
            songMapper.updateSong(update);
            return new ResponseMessage<>(ResponseConstant.success, "更新成功");
        } catch (Exception e) {
            return new ResponseMessage<>(ResponseConstant.error, "更新歌词异常：" + e.getMessage());
        }
    }

    @Override
    public ResponseMessage deleteLrc(long id) {
        Song song=songMapper.selectSongById(id);
        if (song == null) {
            return new ResponseMessage<>(ResponseConstant.error,"歌曲不存在");
        }
        if (song.getLyricPath() == null || song.getLyricPath().isEmpty()){
            return new ResponseMessage<>(ResponseConstant.error,"歌词不存在");
        }
        String fullPath = PathCosntant.SONG_LYRIC_PATH + song.getLyricPath();
        minioService.deleteFile(fullPath);
        return new ResponseMessage<>(ResponseConstant.success,"删除成功");
    }
}
