package xyz.ztzhome.zblog.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import xyz.ztzhome.zblog.constant.PathCosntant;
import xyz.ztzhome.zblog.entity.Bean.Singer;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.entity.DTO.AddSongDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateSongDTO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.SingerMapper;
import xyz.ztzhome.zblog.mapper.SongMapper;
import xyz.ztzhome.zblog.service.ISongService;
import xyz.ztzhome.zblog.util.FileTypeUtil;

import java.util.List;

@Service
public class SongServiceImpl implements ISongService {
    @Autowired
    private SongMapper songMapper;
    @Autowired
    private SingerMapper singerMapper;
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
            return new ResponseMessage<>(0,"歌曲不存在，未找到相关信息");
        }
        SongVO songVO=new SongVO();
        //转化为返回对象
        BeanUtils.copyProperties(song,songVO);
        //根据id查询歌手信息
        Singer singer=singerMapper.selectBySingerId(song.getSingerId());
        if(singer!=null&&singer.getSingerName()!=null){
            //添加歌手信息
            songVO.setSingerName(singer.getSingerName());
        }else {
            songVO.setSingerName("未找到对应歌手信息");
        }
        //添加返回数据
        return new ResponseMessage<>(1,"获取成功",songVO);
    }

    /**
     * 添加歌曲
     * */
    @Override
    public ResponseMessage addSong(AddSongDTO addSongDTO,MultipartFile audioFile) {

        // 1. 校验参数 //检查歌曲信息和文件信息
        if (addSongDTO == null || addSongDTO.getSong() == null || audioFile == null) {
            return new ResponseMessage<>(0,"歌曲信息和文件不能为空");
        }
        //检验文件类型
        String fileName = audioFile.getOriginalFilename();
        if (!FileTypeUtil.getFileType(fileName).equals("music")) {
            return new ResponseMessage<>(0,"暂不支持该文件类型");
        }
        // 2. 检查歌手是否存在（通过名称）
        Singer singer = singerMapper.selectBySingerName(addSongDTO.getSinger().getSingerName());

        // 3. 若不存在，则尝试插入
        if (singer == null) {
            singer = addSongDTO.getSinger();
            try {
                singerMapper.insertSinger(singer);
            } catch (DuplicateKeyException e) {
                // 并发情况下可能被其他请求先插入，重新查询
                singer = singerMapper.selectBySingerName(addSongDTO.getSinger().getSingerName());
                if (singer == null) {
                    return new ResponseMessage<>(0,"添加歌手失败");
                }
            }
        }
        //检查歌曲是否已经存在
        Song song=songMapper.selectBySingerIdAndName(singer.getId(),addSongDTO.getSong().getName());
        if (song != null) {
            return new ResponseMessage<>(0,"该歌曲已经存在");
        }
        // 5. 关联歌手并保存歌曲
        song=addSongDTO.getSong();

        song.setAudioUrl(song.getName()+FileTypeUtil.getFileExtension2(audioFile.getOriginalFilename()));
        song.setSingerId(singer.getId());
        //mybatis+mysql使用自增id,插入的 song 对象的 id 属性会被自动赋值：
        songMapper.insertSong(song);
        //6.上传歌曲文件
        ResponseMessage<SongVO> responseMessage=new ResponseMessage<>();
        try {
            //上传文件，使用歌曲id+歌名来重命名保存
            String folder=PathCosntant.SONG_SAVE_PATH+song.getId()+song.getAudioUrl();
            int isUpload=minioService.uploadFile(audioFile, folder);
            if(isUpload==1){
                responseMessage.setCode(1);
                responseMessage.setMessage("上传成功！ ");
            }
            else {
                responseMessage.setCode(0);
                responseMessage.setMessage("上传失败,上传过程发生异常");
            }
        }catch (Exception e){
            responseMessage.setCode(0);
            responseMessage.setMessage("上传过程发生异常: "+e.getMessage());
            return responseMessage;
        }
        return responseMessage;
    }
    /**
     * @param songName 根据歌曲名称模糊查询
     * @return 返回消息对象+数据
     * */
    @Override
    public ResponseMessage<List<SongVO>> getSongsByName(String songName) {
        ResponseMessage<List<SongVO>> responseMessage = new ResponseMessage<>();
        List<SongVO> songVOS=songMapper.selectSongVOsByNameLike(songName);
        if(songVOS==null|| songVOS.isEmpty()){
            responseMessage.setCode(1);
            responseMessage.setMessage("未找到相关歌曲信息");
            return responseMessage;
        }
        responseMessage.setCode(1);
        responseMessage.setMessage("查询成功");
        responseMessage.setData(songVOS);
        return responseMessage;
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
            return  new ResponseMessage<>(0,"歌曲不存在");
        }
        String path=PathCosntant.SONG_SAVE_PATH+song.getId()+song.getAudioUrl();
        String url=minioService.getFileUrl(6,path);
        if(url==null){
            return new ResponseMessage<>(0,"存储库未找到该歌曲");
        }
        return new ResponseMessage<String>(1,"加载成功",url);
    }

    /**
     * 更新
     * */
    @Override
    public ResponseMessage updateSong(UpdateSongDTO updateSongDTO) {
        if (updateSongDTO==null||updateSongDTO.getName()==null) {
            return new ResponseMessage<>(0,"缺少必要参数");
        }
        return null;
    }

    /**
     * 删除
     * */
    @Override
    public ResponseMessage deleteSong(long id) {

        Song song=songMapper.selectSongById(id);
        if(song==null){
            return new ResponseMessage<>(0,"歌曲信息不存在，删除失败");
        }
        String filePath=PathCosntant.SONG_SAVE_PATH+song.getId()+song.getAudioUrl();
        minioService.deleteFile(filePath);
        if (!minioService.fileIsExist(filePath)){
            return new ResponseMessage<>(0,"服务异常，删除失败");
        }
        //删除数据库中的歌曲信息
        songMapper.deleteSong(id);

        return new ResponseMessage<>(1,"删除成功");
    }
}
