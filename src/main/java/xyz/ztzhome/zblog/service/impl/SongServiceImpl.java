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
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.SingerMapper;
import xyz.ztzhome.zblog.mapper.SongMapper;
import xyz.ztzhome.zblog.service.ISongService;

import java.util.List;

@Service
public class SongServiceImpl implements ISongService {
    @Autowired
    private SongMapper songMapper;
    @Autowired
    private SingerMapper singerMapper;
    @Autowired
    MinioServiceImpl minioService;

    @Override
    public ResponseMessage<SongVO> getSong(long songId) {
        //查询歌曲信息
        Song song= songMapper.selectSongById(songId);

        ResponseMessage<SongVO> responseMessage = new ResponseMessage<>();
        if(song==null){
            responseMessage.setCode(0);
            responseMessage.setMessage("歌曲不存在，未找到相关信息");
            return responseMessage;
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
        responseMessage.setData(songVO);
        return responseMessage;
    }

    @Override
    public ResponseMessage addSong(AddSongDTO addSongDTO,MultipartFile audioFile) {
        //1.检查歌曲信息和文件信息
        ResponseMessage responseMessage = new ResponseMessage();

        // 1. 校验参数
        if (addSongDTO == null || addSongDTO.getSong() == null || audioFile == null) {
            responseMessage.setCode(0);
            responseMessage.setMessage("歌曲信息和文件不能为空");
            return responseMessage;
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
                    responseMessage.setCode(0);
                    responseMessage.setMessage("添加歌手失败");
                    return responseMessage;
                }
            }
        }
        //检查歌曲是否已经存在
        Song song=songMapper.selectBySingerIdAndName(singer.getId(),addSongDTO.getSong().getName());
        if (song != null) {
            responseMessage.setCode(0);
            responseMessage.setMessage("该歌曲已经存在");
            return responseMessage;
        }
        // 5. 关联歌手并保存歌曲
        song=addSongDTO.getSong();
        song.setSingerId(singer.getId());
        songMapper.insertSong(song);
        //6.上传歌曲文件
        String fileName = audioFile.getOriginalFilename();
        try {
            //上传文件
            int isUpload=minioService.uploadFile(audioFile, PathCosntant.SONG_SAVE_PATH +fileName);
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
}
