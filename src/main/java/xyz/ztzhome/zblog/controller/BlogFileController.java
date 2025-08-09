package xyz.ztzhome.zblog.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/file")
public class BlogFileController {

    @PostMapping("/upload")
    public ResponseMessage uploadFile(@RequestParam("file") MultipartFile file) {
        // TODO: Call service to upload file
        return null;
    }

    @GetMapping("/download/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) {
        // TODO: Call service to download file
    }

    @PutMapping("/update/{id}")
    public ResponseMessage updateFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        // TODO: Call service to update file
        return null;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteFile(@PathVariable Long id) {
        // TODO: Call service to delete file
        return null;
    }
}
