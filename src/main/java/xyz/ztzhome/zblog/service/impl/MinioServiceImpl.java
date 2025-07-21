package xyz.ztzhome.zblog.service.impl;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.ztzhome.zblog.service.IMinioService;

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
}
