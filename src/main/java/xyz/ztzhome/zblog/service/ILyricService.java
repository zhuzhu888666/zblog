package xyz.ztzhome.zblog.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;

public interface ILyricService {
    ResponseMessage getLrc(long id);

    ResponseMessage uploadLrc(long id, MultipartFile lrcFile);

    ResponseMessage updateLrc(long id, MultipartFile file);

    ResponseMessage deleteLrc(long id);
}
