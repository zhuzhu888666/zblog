package xyz.ztzhome.zblog.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.Artist;
import xyz.ztzhome.zblog.entity.DTO.AddArtistDTO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.ArtistMapper;
import xyz.ztzhome.zblog.service.IArtistService;

@Service
public class ArtistServiceImpl implements IArtistService {
    private static final Logger logger = LoggerFactory.getLogger(ArtistServiceImpl.class);


    @Autowired
    ArtistMapper artistMapper;

    @Override
    public ResponseMessage<Artist> getArtistById(Integer id) {
        try {
            Artist artist = artistMapper.getArtistById(id);
            if(artist==null){
                return new ResponseMessage<>(ResponseConstant.error,"歌手不存在");
            }
            return new ResponseMessage<>(ResponseConstant.success,ResponseConstant.SUCCESS,artist);

        }catch (Exception e){
            logger.error("获取Artist时发生异常：{}",id,e);
            return null;
        }

    }

    @Override
    public ResponseMessage addArtist(AddArtistDTO addArtistDTO) {
        if(addArtistDTO==null|| addArtistDTO.getArtistName().isEmpty()){
            return new ResponseMessage<>(ResponseConstant.error,"缺少必要参数",addArtistDTO);

        }
        Artist artist = new Artist();
        BeanUtils.copyProperties(addArtistDTO,artist);
        artistMapper.insertArtist(artist);
        return new ResponseMessage<>(ResponseConstant.success,ResponseConstant.SUCCESS);
    }
}
