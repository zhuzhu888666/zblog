package xyz.ztzhome.zblog.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import jakarta.servlet.http.HttpServletResponse;

public interface IBlogFileService {
    ResponseMessage uploadFile(MultipartFile file,String Path);
    ResponseMessage downloadFile(Long id, HttpServletResponse response);
    ResponseMessage updateFile(Long id, MultipartFile file);
    ResponseMessage deleteFile(Long id);
} 