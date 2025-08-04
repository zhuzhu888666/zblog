package xyz.ztzhome.zblog;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import xyz.ztzhome.zblog.constant.PathCosntant;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.Artist;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.AddArtistDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateUserProfileDTO;
import xyz.ztzhome.zblog.entity.DTO.UpdateUserSecurityDTO;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.ArtistMapper;
import xyz.ztzhome.zblog.mapper.SongMapper;
import xyz.ztzhome.zblog.service.IUserService;
import xyz.ztzhome.zblog.service.impl.ArtistServiceImpl;
import xyz.ztzhome.zblog.service.impl.MinioServiceImpl;
import xyz.ztzhome.zblog.util.BCryptPassword;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ZblogApplicationTests {

    @Autowired
    SongMapper songMapper;
    @Test
    void contextLoads() {
        Song song = new Song();
        song.setName("test09");
        song.setAlbum("test");
        song.setDuration("123");
        song.setArtistId(11);
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
//        String url= minioService.getFileUrl(6,PathCosntant.SONG_SAVE_PATH+"下载.webp");
//        System.out.println(url);
        Song song =  songMapper.selectSongById(563);
        minioService.deleteFile(PathCosntant.SONG_COVER_PATH+song.getCoverPath());
    }

    @Autowired
    private IUserService userService;

    @Test
    void testLogin() {
        // 先注册一个用户
        User user = new User();
        user.setAccount("loginuser");
        user.setPassword("password123");
        user.setEmail("loginuser@example.com");
        userService.register(user);

        // 测试登录
        ResponseMessage response = userService.login("loginuser", "password123");
        assertEquals(ResponseConstant.success, response.getCode());
    }



    @Test
    void testUpdateUserProfile() {
        // 先注册一个用户
        User user = new User();
        user.setAccount("updateuser");
        user.setPassword("password123");
        user.setEmail("updateuser@example.com");
        userService.register(user);

        // 更新用户信息
        UpdateUserProfileDTO profileDTO = new UpdateUserProfileDTO();
        profileDTO.setAccount("updateuser");
        profileDTO.setNickname("New Nick");
        profileDTO.setNickname("New Nick");
        profileDTO.setAge(25);

        ResponseMessage response = userService.updateUserProfile(profileDTO);
        assertEquals(ResponseConstant.success, response.getCode());
    }

    @Test
    void testUpdateUserSecurity() {
        // 先注册一个用户
        User user = new User();
        user.setAccount("securityuser");
        user.setPassword("password123");
        user.setEmail("securityuser@example.com");
        userService.register(user);

        // 更新安全信息
        UpdateUserSecurityDTO securityDTO = new UpdateUserSecurityDTO();
        securityDTO.setAccount("securityuser");
        securityDTO.setPassword(BCryptPassword.encode("newpassword456"));
        securityDTO.setEmail("newsecurity@example.com");

        ResponseMessage response = userService.updateUserSecurity(securityDTO);
        assertEquals(ResponseConstant.success, response.getCode());
    }

    @Autowired
    ArtistServiceImpl artistService;
    @Test
    void testGetArtist(){
        List<SongVO> songVOS=songMapper.selectRandomSongsWithArtist(10);
       for(SongVO songVO:songVOS){
           System.out.println(songVO.toString());
       }
    }
    @Autowired
    ArtistMapper artistMapper;
    @Test
    void testAdd(){
        Artist artist = new Artist();
        artist.setArtistName("test01");
        artist.setGender("male");
        artist.setIntroduction("test01");
        artist.setArea("CN");
        artist.setAvatar("test01");
        AddArtistDTO addArtistDTO = new AddArtistDTO();
        BeanUtils.copyProperties(artist,addArtistDTO);
       ResponseMessage responseMessage= artistService.addArtist(addArtistDTO);
        System.out.println(responseMessage.toString());
    }
}
