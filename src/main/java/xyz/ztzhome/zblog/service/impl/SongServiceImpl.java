package xyz.ztzhome.zblog.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import xyz.ztzhome.zblog.constant.PathCosntant;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.Artist;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.entity.DTO.AddSongDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateSongDTO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.ArtistMapper;
import xyz.ztzhome.zblog.mapper.SongMapper;
import xyz.ztzhome.zblog.service.ISongService;
import xyz.ztzhome.zblog.util.FileTypeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class SongServiceImpl implements ISongService {
    private static final Logger log = LoggerFactory.getLogger(SongServiceImpl.class);
    @Autowired
    private SongMapper songMapper;
    @Autowired
    private ArtistMapper artistMapper;
    @Autowired
    MinioServiceImpl minioService;

    /**
     * 获取歌曲信息
     * */
    @Override
    public ResponseMessage<SongVO> getSong(long songId) {
        //查询歌曲信息
        Song song= songMapper.selectSongById(songId);
        if(song==null){
            return new ResponseMessage<>(ResponseConstant.error,"歌曲不存在，未找到相关信息");
        }
        SongVO songVO=new SongVO();
        //转化为返回对象
        BeanUtils.copyProperties(song,songVO);
        //根据id查询艺术家信息
        Artist artist=artistMapper.selectByArtistId(song.getArtistId());
        if(artist!=null&&artist.getArtistName()!=null){
            //添加艺术家信息
            songVO.setArtistName(artist.getArtistName());
        }else {
            songVO.setArtistName("未找到对应艺术家信息");
        }
        // 封面URL
        if (song.getCoverPath() == null || song.getCoverPath().isEmpty() || "default.jpg".equals(song.getCoverPath())) {
            songVO.setCoverPath("/files/image/default_cover.jpg");
        } else {
            String minioPath = PathCosntant.SONG_COVER_PATH + song.getCoverPath();
            String url = minioService.getFileUrl(60*24, minioPath);
            songVO.setCoverPath(url);
        }
        //添加返回数据
        return new ResponseMessage<>(ResponseConstant.success,"获取成功",songVO);
    }

    /**
     * 添加歌曲
     * */
    @Override
    public ResponseMessage addSong(AddSongDTO addSongDTO, MultipartFile audioFile, MultipartFile coverFile) {
        // 1. 校验参数 //检查歌曲信息和文件信息
        if (addSongDTO == null || addSongDTO.getSong() == null || audioFile == null) {
            return new ResponseMessage<>(ResponseConstant.error, "歌曲信息和文件不能为空");
        }
        //检验文件类型
        String fileName = audioFile.getOriginalFilename();
        if (!FileTypeUtil.getFileType(fileName).equals("music")) {
            return new ResponseMessage<>(ResponseConstant.error, "暂不支持该文件类型");
        }
        // 2. 检查艺术家是否存在（通过名称）
        Artist artist = artistMapper.selectByArtistName(addSongDTO.getArtist().getArtistName());
        // 3. 若不存在，则尝试插入
        if (artist == null) {
            artist = addSongDTO.getArtist();
            try {
                artistMapper.insertArtist(artist);
            } catch (DuplicateKeyException e) {
                // 并发情况下可能被其他请求先插入，重新查询
                artist = artistMapper.selectByArtistName(addSongDTO.getArtist().getArtistName());
                if (artist == null) {
                    return new ResponseMessage<>(ResponseConstant.error, "添加艺术家失败");
                }
            }
        }
        //检查歌曲是否已经存在
        Song song = songMapper.selectByArtistIdAndName(artist.getId(), addSongDTO.getSong().getName());
        if (song != null) {
            return new ResponseMessage<>(ResponseConstant.error, "该歌曲已经存在");
        }
        // 4. 先插入数据库（不带audioPath/coverPath），获取自增ID
        song = addSongDTO.getSong();
        song.setArtistId(artist.getId());
        song.setAudioPath(null); // 先不设置音频路径
        song.setCoverPath(null); // 先不设置封面路径
        songMapper.insertSong(song); // 此时song的id已被赋值
        // 5. 用ID生成音频文件名，上传到minio
        String audioFileName = song.getId() + song.getName() + FileTypeUtil.getFileExtension2(audioFile.getOriginalFilename());
        String audioSavePath = PathCosntant.SONG_SAVE_PATH + audioFileName;
        int isUpload = 0;
        try {
            isUpload = minioService.uploadFile(audioFile, audioSavePath);
        } catch (Exception e) {
            log.error("音频文件上传异常，歌曲ID:{}，文件名:{}，异常:{}", song.getId(), audioFileName, e.getMessage(), e);
            songMapper.deleteSong(song.getId());
            return new ResponseMessage<>(ResponseConstant.error, "上传过程发生异常: " + e.getMessage());
        }
        // 6. 处理封面文件
        String coverPath = "default_cover.jpg";
        if (coverFile != null && !coverFile.isEmpty()) {
            String coverFileName = song.getId() + "_cover" + FileTypeUtil.getFileExtension2(coverFile.getOriginalFilename());
            String coverSavePath = PathCosntant.SONG_COVER_PATH + coverFileName;
            int coverUpload = 0;
            try {
                coverUpload = minioService.uploadFile(coverFile, coverSavePath);
                if (coverUpload == 1) {
                    coverPath = coverFileName;
                }
            } catch (Exception e) {
                log.warn("封面文件上传失败，歌曲ID:{}，文件名:{}，异常:{}", song.getId(), coverFileName, e.getMessage(), e);
                // 封面上传失败不影响主流程，仍用默认封面
            }
        }
        if (isUpload == 1) {
            // 上传成功，更新audioPath和coverPath字段
            song.setAudioPath(audioFileName);
            song.setCoverPath(coverPath);
            songMapper.updateSong(song);
            return new ResponseMessage<>(ResponseConstant.success, "上传成功");
        } else {
            // 上传失败，删除数据库记录
            songMapper.deleteSong(song.getId());
            return new ResponseMessage<>(ResponseConstant.error, "上传失败,上传过程发生异常");
        }
    }
    /**
     * @param songName 根据歌曲名称模糊查询
     * @return 返回消息对象+数据
     * */
    @Override
    public ResponseMessage<List<SongVO>> getSongsByName(String songName) {
        List<SongVO> songVOS=songMapper.selectSongVOsByNameLike(songName);
        if(songVOS==null|| songVOS.isEmpty()){
            return new ResponseMessage<>(ResponseConstant.error,"未找到相关歌曲信息");
        }
        // 填充封面URL
        fillCoverUrls(songVOS);
        return new ResponseMessage<>(ResponseConstant.success,"success",songVOS);
    }

    /**
     * 获取歌曲临时路径
     * @param id 歌曲id
     * @return 返回消息对象
     * */
    @Override
    public ResponseMessage<String> getSongURL(long id) {

        Song song=songMapper.selectSongById(id);
        if(song==null){
            return  new ResponseMessage<>(ResponseConstant.error,"歌曲不存在");
        }
        String path=PathCosntant.SONG_SAVE_PATH+song.getAudioPath();
        String url=minioService.getFileUrl(60*24,path);
        if(url==null){
            return new ResponseMessage<>(ResponseConstant.error,"存储库未找到该歌曲");
        }
        return new ResponseMessage<String>(ResponseConstant.success,"加载成功",url);
    }

    /**
     * 更新
     * */
    @Override
    public ResponseMessage updateSong(UpdateSongDTO updateSongDTO, MultipartFile coverFile) {
        if (updateSongDTO == null || updateSongDTO.getName() == null) {
            return new ResponseMessage<>(ResponseConstant.error, "缺少必要参数");
        }
        // 2. 获取原始歌曲数据
        Song song=songMapper.selectSongById(updateSongDTO.getId());
        if (song == null) {
            return new ResponseMessage<>(ResponseConstant.error, "该歌曲不存在，更新失败");
        }
        //复制更改元素
        BeanUtils.copyProperties(updateSongDTO,song);
        //如果传入的艺术家不为空，更新艺术家
        //优先使用艺术家id更新艺术家
        if (updateSongDTO.getArtistId()!=0){
            Artist artist=artistMapper.selectByArtistId(updateSongDTO.getArtistId());
            if (artist == null) {
                return new ResponseMessage<>(ResponseConstant.error,"新艺术家不存在");
            }
            //更新艺术家id
            song.setArtistId(artist.getId());
        }else if(updateSongDTO.getArtistName()!=null&& !updateSongDTO.getArtistName().isEmpty()){

            Artist artist=artistMapper.selectByArtistName(updateSongDTO.getArtistName());
            if (artist == null) {
                return new ResponseMessage<>(ResponseConstant.error,"新艺术家不存在");
            }
            //更新艺术家id
            song.setArtistId(artist.getId());
        }
        // 处理封面文件
        if (coverFile != null && !coverFile.isEmpty()) {
            String coverFileName = song.getId() + "_cover" + FileTypeUtil.getFileExtension2(coverFile.getOriginalFilename());
            String coverSavePath = PathCosntant.SONG_COVER_PATH + coverFileName;
            int coverUpload = 0;
            try {
                //删除旧的封面
                minioService.deleteFile(PathCosntant.SONG_COVER_PATH+song.getCoverPath());
                //上传新封面
                coverUpload = minioService.uploadFile(coverFile, coverSavePath);
                if (coverUpload == 1) {
                    song.setCoverPath(coverFileName);
                }
            } catch (Exception e) {
                log.warn("封面文件上传失败（更新），歌曲ID:{}，文件名:{}，异常:{}", song.getId(), coverFileName, e.getMessage(), e);
                // 封面上传失败，保留原封面
            }
        } else if (song.getCoverPath() == null || song.getCoverPath().isEmpty()) {
            // 如果原本没有封面，设为默认
            song.setCoverPath("default.jpg");
        }
        //更新歌曲
        songMapper.updateSong(song);
        return new ResponseMessage<>(ResponseConstant.success, "更新成功");
    }

    /**
     * 删除
     * */
    @Override
    public ResponseMessage deleteSong(long id) {

        Song song=songMapper.selectSongById(id);
        if(song==null){
            return new ResponseMessage<>(ResponseConstant.error,"歌曲信息不存在，删除失败");
        }
        String filePath=PathCosntant.SONG_SAVE_PATH+song.getAudioPath();
        minioService.deleteFile(filePath);
        if (!minioService.fileIsExist(filePath)){
            //删除数据库中的歌曲信息--既然文件不存在那么把数据库中的数据也清除了
            songMapper.deleteSong(id);
            return new ResponseMessage<>(ResponseConstant.success,"未找到对应歌曲文件，已清空歌曲信息");
        }
        //删除数据库中的歌曲信息
        songMapper.deleteSong(id);
        return new ResponseMessage<>(ResponseConstant.success,"删除成功");
    }

    /**
     * 根据歌名模糊查询，分页
     * @param songName 歌曲名称
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Override
    public ResponseMessage<PageResponse<SongVO>> getSongsByNameWithPage(String songName, int pageNum, int pageSize) {
        // 参数校验
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;
        
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        int total = songMapper.countSongsByNameLike(songName);
        
        // 查询数据
        List<SongVO> songs = songMapper.selectSongVOsByNameLikeWithPage(songName, offset, pageSize);
        fillCoverUrls(songs);
        
        PageResponse<SongVO> pageResponse = new PageResponse<>(songs, total, pageNum, pageSize);
        
        if (songs.isEmpty()) {
            return new ResponseMessage<>(ResponseConstant.error, "未找到相关歌曲信息", pageResponse);
        }
        
        return new ResponseMessage<>(ResponseConstant.success, "查询成功", pageResponse);
    }

    /**
     * 查询全部歌曲，分页
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Override
    public ResponseMessage<PageResponse<SongVO>> getAllSongsWithPage(int pageNum, int pageSize) {
        // 参数校验
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;
        
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        int total = songMapper.countAllSongs();
        
        // 查询数据
        List<SongVO> songs = songMapper.selectAllSongVOsWithPage(offset, pageSize);
        fillCoverUrls(songs);
        
        PageResponse<SongVO> pageResponse = new PageResponse<>(songs, total, pageNum, pageSize);
        
        if (songs.isEmpty()) {
            return new ResponseMessage<>(ResponseConstant.error, "暂无歌曲数据", pageResponse);
        }
        
        return new ResponseMessage<>(ResponseConstant.success, "查询成功", pageResponse);
    }

    /**
     * 随机查询歌曲
     * @param limit 查询数量
     * @return 歌曲列表
     */
    @Override
    public ResponseMessage<List<SongVO>> getRandomSongs(int limit) {
        // 参数校验
        if (limit < 1) limit = 20;
        if (limit > 100) limit = 100; // 限制最大查询数量

        List<SongVO> songVOs = songMapper.selectRandomSongsWithArtist(limit);
        fillCoverUrls(songVOs);

        if (songVOs.isEmpty()) {
            return new ResponseMessage<>(ResponseConstant.error, "暂无歌曲数据");
        }

        return new ResponseMessage<>(ResponseConstant.success, "查询成功", songVOs);
    }

    /**
     * 根据风格分页查询
     * @param style 歌曲风格
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Override
    public ResponseMessage<PageResponse<SongVO>> getSongsByStyleWithPage(String style, int pageNum, int pageSize) {
        // 参数校验
        if (style == null || style.trim().isEmpty()) {
            return new ResponseMessage<>(ResponseConstant.error, "风格参数不能为空");
        }
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;
        
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        int total = songMapper.countSongsByStyle(style);
        
        // 查询数据
        List<SongVO> songs = songMapper.selectSongVOsByStyleWithPage(style, offset, pageSize);
        fillCoverUrls(songs);
        
        PageResponse<SongVO> pageResponse = new PageResponse<>(songs, total, pageNum, pageSize);
        
        if (songs.isEmpty()) {
            return new ResponseMessage<>(ResponseConstant.error, "未找到该风格的歌曲", pageResponse);
        }
        
        return new ResponseMessage<>(ResponseConstant.success, "查询成功", pageResponse);
    }

    @Override
    public ResponseMessage<String> getCoverURL(long id) {
        Song song = songMapper.selectSongById(id);
        if (song == null) {
            return new ResponseMessage<>(ResponseConstant.error, "歌曲不存在");
        }
        String coverPath = song.getCoverPath();
        if (coverPath == null || coverPath.isEmpty() || "default.jpg".equals(coverPath)) {
            // 返回静态资源路径
            return new ResponseMessage<>(ResponseConstant.success, "加载成功", "/files/image/default_cover.jpg");
        }
        String minioPath = PathCosntant.SONG_COVER_PATH + coverPath;
        String url = minioService.getFileUrl(60*24, minioPath);
        if (url == null) {
            return new ResponseMessage<>(ResponseConstant.error, "存储库未找到该封面");
        }
        return new ResponseMessage<>(ResponseConstant.success, "加载成功", url);
    }

    @Override
    public ResponseMessage addPlayCount(long id) {
        Song song=songMapper.selectSongById(id);
        if (song==null) {
            return new ResponseMessage<>(ResponseConstant.error,"歌曲不存在");
        }
        long count=song.getPlayCount()+1;
        songMapper.updatePlayCount(count,song.getId());
        return new ResponseMessage<>(ResponseConstant.success,"1");
    }

    private void fillCoverUrls(List<SongVO> songVOs) {
        if (songVOs == null || songVOs.isEmpty()) return;
        for (SongVO vo : songVOs) {
            String coverPath = vo.getCoverPath();
            if (coverPath == null || coverPath.isEmpty() || "default.jpg".equals(coverPath)) {
                vo.setCoverPath("/files/image/default_cover.jpg");
            } else {
                String minioPath = PathCosntant.SONG_COVER_PATH + coverPath;
                String url = minioService.getFileUrl(60*24, minioPath);
                vo.setCoverPath(url);
            }
        }
    }
}
