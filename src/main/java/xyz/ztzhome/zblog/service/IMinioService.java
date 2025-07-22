package xyz.ztzhome.zblog.service;

import org.springframework.web.multipart.MultipartFile;

public interface IMinioService {
    //检查桶是否存在
    boolean isBucketExists(String bucketName);
    //检查文件是否存在
    boolean isFileExists(String bucketName, String objectName);

    //上传文件
    /**
     * 上传文件到 MinIO
     * @param file   要上传的文件
     * @param folder 存储文件的目录
     * @return *
     */
    int uploadFile (MultipartFile file, String folder);

    //获取文件临时路径
    String getFileUrl(int timeOut, String filePath);

    int deleteFile(String filePath);

    Boolean fileIsExist(String filePath);
}
