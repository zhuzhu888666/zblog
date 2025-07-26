package xyz.ztzhome.zblog.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

    String getFileUrl(int timeOut, String filePath, TimeUnit timeUnit);

    //获取指定文件夹的所有文件临时路径，不包含子文件夹
    public List<String> getFolderFileUrls(String folderPath, int timeOut);

    // 删除文件
    void removeFile(String objectName) throws Exception;

    int deleteFile(String filePath);

    Boolean fileIsExist(String filePath);
}
