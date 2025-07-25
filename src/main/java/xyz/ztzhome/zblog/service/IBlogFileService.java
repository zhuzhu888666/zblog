package xyz.ztzhome.zblog.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import javax.servlet.http.HttpServletResponse;

public interface IBlogFileService {
    ResponseMessage uploadFile(MultipartFile file);
    void downloadFile(Long id, HttpServletResponse response);
    ResponseMessage updateFile(Long id, MultipartFile file);
    ResponseMessage deleteFile(Long id);
} 