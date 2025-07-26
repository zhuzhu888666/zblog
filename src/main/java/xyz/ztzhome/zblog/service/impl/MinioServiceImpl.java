package xyz.ztzhome.zblog.service.impl;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.service.IMinioService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MinioServiceImpl implements IMinioService {
    private static final Logger logger = LoggerFactory.getLogger(MinioServiceImpl.class);

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public boolean isBucketExists(String bucketName) {
        return false;
    }

    @Override
    public boolean isFileExists(String bucketName, String objectName) {
        try {
            // 获取对象元数据，如果对象不存在会抛出异常
            minioClient.statObject(StatObjectArgs.builder()
                    .object(objectName)
                    .bucket(bucketName)
                    .build());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public int uploadFile(MultipartFile file, String folder) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(folder)
                    .stream(file.getInputStream(),file.getSize(),-1)
                    .build());
            return 1;
        }catch (Exception e){
            return -1;
        }
    }

    @Override
    public String getFileUrl(int timeOut, String filePath) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucket)
                    .method(Method.GET)
                    .object(filePath)
                    .expiry(timeOut, TimeUnit.MINUTES).build());
        }catch (Exception e){
            return "getURL_error:"+e.getMessage();
        }
    }

    @Override
    public String getFileUrl(int timeOut, String filePath, TimeUnit timeUnit) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucket)
                    .method(Method.GET)
                    .object(filePath)
                    .expiry(timeOut, timeUnit).build());
        } catch (Exception e) {
            return "getURL_error:" + e.getMessage();
        }
    }

    @Override
    public List<String> getFolderFileUrls(String folderPath,int timeOut) {
        List<String> urls = new ArrayList<>();
        // 确保文件夹路径以 '/' 结尾
        if (!folderPath.endsWith("/")) folderPath += "/";

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(folderPath)  // 设置文件夹前缀
                            .recursive(false)      // 不递归子目录
                            .build()
            );
            // 2. 为每个对象生成预签名 URL
            for (Result<Item> result : results) {
                Item item = result.get();
                if (!item.isDir()) { // 跳过文件夹本身
                    String url = minioClient.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .method(Method.GET)
                                    .bucket(bucket)
                                    .object(item.objectName())
                                    .expiry(timeOut, TimeUnit.MINUTES)
                                    .build()
                    );
                    urls.add(url);
                }
            }
        }catch (Exception e){
            logger.error("获取文件夹内所有文件临时路径时发生异常：{}",folderPath, e);
            return urls;
        }
        return urls;
    }

    @Override
    public int deleteFile(String filePath) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)
                    .build());
            return 1;
        }catch (Exception e){
            return -1;
        }
    }

    @Override
    public Boolean fileIsExist(String filePath) {
        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)
                    .build());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public void removeFile(String objectName) throws Exception {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build()
        );
    }
}
