package xyz.ztzhome.zblog;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.ztzhome.zblog.constant.PathCosntant;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.mapper.SongMapper;
import xyz.ztzhome.zblog.service.impl.MinioServiceImpl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ZblogApplicationTests {

    @Autowired
    SongMapper songMapper;
    @Test
    void contextLoads() {
        Song song = new Song();
        song.setName("test09");
        song.setAlbum("test");
        song.setDuration("123");
        song.setSingerId(11);
        song.setReleaseTime(new Date());
        songMapper.insertSong(song);
        System.out.println(song.toString());
    }

    @Autowired
    MinioClient minioClient;
    @Autowired
    MinioServiceImpl minioService;
    @Value("${minio.bucket}")
    private String bucket;
    @Test
    void test(){
        try {
            String presignedObjectUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucket)
                    .method(Method.GET)
                    .expiry(6, TimeUnit.MINUTES)
                    .object(PathCosntant.SONG_SAVE_PATH + "下载.webp").build());
            System.out.println(presignedObjectUrl);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void test1(){
        String url= minioService.getFileUrl(6,PathCosntant.SONG_SAVE_PATH+"下载.webp");
        System.out.println(url);
    }

    @Test
    void test2(){
        String url= PathCosntant.SONG_SAVE_PATH+"下载.webp";
        System.out.println(minioService.deleteFile(url));
    }

    @Test
    void test3(){
        String url= PathCosntant.SONG_SAVE_PATH+"下载.webp";
        try {
          minioService.fileIsExist(url);
          System.out.println(minioService.fileIsExist(url));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
