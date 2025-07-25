package xyz.ztzhome.zblog.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.IBlogFileService;
import javax.servlet.http.HttpServletResponse;

@Service
public class BlogFileServiceImpl implements IBlogFileService {

    @Override
    public ResponseMessage uploadFile(MultipartFile file) {
        // TODO: Implement file upload logic
        return null;
    }

    @Override
    public void downloadFile(Long id, HttpServletResponse response) {
        // TODO: Implement file download logic
    }

    @Override
    public ResponseMessage updateFile(Long id, MultipartFile file) {
        // TODO: Implement file update logic
        return null;
    }

    @Override
    public ResponseMessage deleteFile(Long id) {
        // TODO: Implement file delete logic
        return null;
    }
} 