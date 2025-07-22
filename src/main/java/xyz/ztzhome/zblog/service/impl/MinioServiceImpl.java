package xyz.ztzhome.zblog.service.impl;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.service.IMinioService;

import java.util.concurrent.TimeUnit;

@Service
public class MinioServiceImpl implements IMinioService {
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
}
