package xyz.ztzhome.zblog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import xyz.ztzhome.zblog.constant.ResponseConstant;
import xyz.ztzhome.zblog.entity.Bean.Artist;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.VO.SongVO;
import xyz.ztzhome.zblog.entity.response.PageResponse;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.mapper.ArtistMapper;
import xyz.ztzhome.zblog.mapper.SongMapper;
import xyz.ztzhome.zblog.mapper.UserMapper;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserFavoriteServiceTest {

    @Autowired
    private IUserFavoriteService userFavoriteService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private ArtistMapper artistMapper;

    private Long testUserId;
    private Long testSongId;
    private Long testArtistId;

    @BeforeEach
    public void setUp() {
        // 创建测试用户
        User testUser = new User();
        testUser.setAccount("favorite_test_user");
        testUser.setPassword("password123");
        testUser.setEmail("favorite_test@example.com");
        testUser.setNickname("收藏测试用户");
        userMapper.insertUser(testUser);
        testUserId = testUser.getId();

        // 创建测试艺术家
        Artist testArtist = new Artist();
        testArtist.setArtistName("测试艺术家");
        testArtist.setGender("male");
        testArtist.setArea("CN");
        testArtist.setIntroduction("这是一个测试艺术家");
        artistMapper.insertArtist(testArtist);
        testArtistId = testArtist.getId();

        // 创建测试歌曲
        Song testSong = new Song();
        testSong.setName("测试歌曲");
        testSong.setArtistId(testArtistId);
        testSong.setAlbum("测试专辑");
        testSong.setDuration("3:30");
        testSong.setStyle("流行");
        testSong.setReleaseTime(new Date());
        songMapper.insertSong(testSong);
        testSongId = testSong.getId();

        System.out.println("测试数据准备完成 - 用户ID: " + testUserId + ", 歌曲ID: " + testSongId + ", 艺术家ID: " + testArtistId);
    }

    // ========== 收藏歌曲测试方法 ==========

    @Test
    public void testFavoriteSong() {
        System.out.println("测试收藏歌曲功能");
        
        // 测试收藏歌曲
        ResponseMessage response = userFavoriteService.favoriteSong(testUserId, testSongId);
        assertEquals(ResponseConstant.success, response.getCode());
        assertEquals("收藏成功", response.getMessage());
        
        System.out.println("收藏歌曲测试通过: " + response.getMessage());
    }

    @Test
    public void testFavoriteSongDuplicate() {
        System.out.println("测试重复收藏歌曲功能");
        
        // 先收藏一次
        userFavoriteService.favoriteSong(testUserId, testSongId);
        
        // 再次收藏应该失败
        ResponseMessage response = userFavoriteService.favoriteSong(testUserId, testSongId);
        assertEquals(ResponseConstant.error, response.getCode());
        assertEquals("已经收藏过该歌曲", response.getMessage());
        
        System.out.println("重复收藏测试通过: " + response.getMessage());
    }

    @Test
    public void testUnfavoriteSong() {
        System.out.println("测试取消收藏歌曲功能");
        
        // 先收藏
        userFavoriteService.favoriteSong(testUserId, testSongId);
        
        // 取消收藏
        ResponseMessage response = userFavoriteService.unfavoriteSong(testUserId, testSongId);
        assertEquals(ResponseConstant.success, response.getCode());
        assertEquals("取消收藏成功", response.getMessage());
        
        System.out.println("取消收藏歌曲测试通过: " + response.getMessage());
    }

    @Test
    public void testUnfavoriteSongNotExists() {
        System.out.println("测试取消未收藏的歌曲");
        
        // 取消收藏一个未收藏的歌曲
        ResponseMessage response = userFavoriteService.unfavoriteSong(testUserId, testSongId);
        assertEquals(ResponseConstant.error, response.getCode());
        assertEquals("未收藏该歌曲", response.getMessage());
        
        System.out.println("取消未收藏歌曲测试通过: " + response.getMessage());
    }

    @Test
    public void testIsUserFavoriteSong() {
        System.out.println("测试检查收藏状态功能");
        
        // 检查未收藏状态
        ResponseMessage<Boolean> response1 = userFavoriteService.isUserFavoriteSong(testUserId, testSongId);
        assertEquals(ResponseConstant.success, response1.getCode());
        assertFalse(response1.getData());
        
        // 收藏后检查状态
        userFavoriteService.favoriteSong(testUserId, testSongId);
        ResponseMessage<Boolean> response2 = userFavoriteService.isUserFavoriteSong(testUserId, testSongId);
        assertEquals(ResponseConstant.success, response2.getCode());
        assertTrue(response2.getData());
        
        System.out.println("收藏状态检查测试通过");
    }

    @Test
    public void testGetUserFavoriteSongs() {
        System.out.println("测试获取用户收藏歌曲列表");
        
        // 收藏歌曲
        userFavoriteService.favoriteSong(testUserId, testSongId);
        
        // 获取收藏列表
        ResponseMessage<List<SongVO>> response = userFavoriteService.getUserFavoriteSongs(testUserId);
        assertEquals(ResponseConstant.success, response.getCode());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals(testSongId.longValue(), response.getData().get(0).getId());
        
        System.out.println("获取收藏歌曲列表测试通过，收藏数量: " + response.getData().size());
    }

    @Test
    public void testGetUserFavoriteSongsWithPage() {
        System.out.println("测试分页获取用户收藏歌曲列表");
        
        // 收藏歌曲
        userFavoriteService.favoriteSong(testUserId, testSongId);
        
        // 分页获取收藏列表
        ResponseMessage<PageResponse<SongVO>> response = userFavoriteService.getUserFavoriteSongsWithPage(testUserId, 1, 10);
        assertEquals(ResponseConstant.success, response.getCode());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getTotal());
        assertEquals(1, response.getData().getData().size());
        assertEquals(1, response.getData().getPageNum());
        assertEquals(10, response.getData().getPageSize());
        
        System.out.println("分页获取收藏歌曲列表测试通过，总数: " + response.getData().getTotal());
    }

    @Test
    public void testGetSongFavoriteCount() {
        System.out.println("测试获取歌曲收藏数量");
        
        // 获取初始收藏数
        ResponseMessage<Integer> response1 = userFavoriteService.getSongFavoriteCount(testSongId);
        assertEquals(ResponseConstant.success, response1.getCode());
        assertEquals(0, response1.getData().intValue());
        
        // 收藏后获取数量
        userFavoriteService.favoriteSong(testUserId, testSongId);
        ResponseMessage<Integer> response2 = userFavoriteService.getSongFavoriteCount(testSongId);
        assertEquals(ResponseConstant.success, response2.getCode());
        assertEquals(1, response2.getData().intValue());
        
        System.out.println("歌曲收藏数量测试通过，收藏数: " + response2.getData());
    }

    // ========== 关注艺术家测试方法 ==========

    @Test
    public void testFollowArtist() {
        System.out.println("测试关注艺术家功能");
        
        // 测试关注艺术家
        ResponseMessage response = userFavoriteService.followArtist(testUserId, testArtistId);
        assertEquals(ResponseConstant.success, response.getCode());
        assertEquals("关注成功", response.getMessage());
        
        System.out.println("关注艺术家测试通过: " + response.getMessage());
    }

    @Test
    public void testFollowArtistDuplicate() {
        System.out.println("测试重复关注艺术家功能");
        
        // 先关注一次
        userFavoriteService.followArtist(testUserId, testArtistId);
        
        // 再次关注应该失败
        ResponseMessage response = userFavoriteService.followArtist(testUserId, testArtistId);
        assertEquals(ResponseConstant.error, response.getCode());
        assertEquals("已经关注过该艺术家", response.getMessage());
        
        System.out.println("重复关注测试通过: " + response.getMessage());
    }

    @Test
    public void testUnfollowArtist() {
        System.out.println("测试取消关注艺术家功能");
        
        // 先关注
        userFavoriteService.followArtist(testUserId, testArtistId);
        
        // 取消关注
        ResponseMessage response = userFavoriteService.unfollowArtist(testUserId, testArtistId);
        assertEquals(ResponseConstant.success, response.getCode());
        assertEquals("取消关注成功", response.getMessage());
        
        System.out.println("取消关注艺术家测试通过: " + response.getMessage());
    }

    @Test
    public void testUnfollowArtistNotExists() {
        System.out.println("测试取消未关注的艺术家");
        
        // 取消关注一个未关注的艺术家
        ResponseMessage response = userFavoriteService.unfollowArtist(testUserId, testArtistId);
        assertEquals(ResponseConstant.error, response.getCode());
        assertEquals("未关注该艺术家", response.getMessage());
        
        System.out.println("取消未关注艺术家测试通过: " + response.getMessage());
    }

    @Test
    public void testIsUserFollowArtist() {
        System.out.println("测试检查关注状态功能");
        
        // 检查未关注状态
        ResponseMessage<Boolean> response1 = userFavoriteService.isUserFollowArtist(testUserId, testArtistId);
        assertEquals(ResponseConstant.success, response1.getCode());
        assertFalse(response1.getData());
        
        // 关注后检查状态
        userFavoriteService.followArtist(testUserId, testArtistId);
        ResponseMessage<Boolean> response2 = userFavoriteService.isUserFollowArtist(testUserId, testArtistId);
        assertEquals(ResponseConstant.success, response2.getCode());
        assertTrue(response2.getData());
        
        System.out.println("关注状态检查测试通过");
    }

    @Test
    public void testGetUserFollowArtists() {
        System.out.println("测试获取用户关注艺术家列表");
        
        // 关注艺术家
        userFavoriteService.followArtist(testUserId, testArtistId);
        
        // 获取关注列表
        ResponseMessage<List<Artist>> response = userFavoriteService.getUserFollowArtists(testUserId);
        assertEquals(ResponseConstant.success, response.getCode());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals(testArtistId.longValue(), response.getData().get(0).getId());
        
        System.out.println("获取关注艺术家列表测试通过，关注数量: " + response.getData().size());
    }

    @Test
    public void testGetUserFollowArtistsWithPage() {
        System.out.println("测试分页获取用户关注艺术家列表");
        
        // 关注艺术家
        userFavoriteService.followArtist(testUserId, testArtistId);
        
        // 分页获取关注列表
        ResponseMessage<PageResponse<Artist>> response = userFavoriteService.getUserFollowArtistsWithPage(testUserId, 1, 10);
        assertEquals(ResponseConstant.success, response.getCode());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getTotal());
        assertEquals(1, response.getData().getData().size());
        assertEquals(1, response.getData().getPageNum());
        assertEquals(10, response.getData().getPageSize());
        
        System.out.println("分页获取关注艺术家列表测试通过，总数: " + response.getData().getTotal());
    }

    @Test
    public void testGetArtistFollowerCount() {
        System.out.println("测试获取艺术家粉丝数量");
        
        // 获取初始粉丝数
        ResponseMessage<Integer> response1 = userFavoriteService.getArtistFollowerCount(testArtistId);
        assertEquals(ResponseConstant.success, response1.getCode());
        assertEquals(0, response1.getData().intValue());
        
        // 关注后获取数量
        userFavoriteService.followArtist(testUserId, testArtistId);
        ResponseMessage<Integer> response2 = userFavoriteService.getArtistFollowerCount(testArtistId);
        assertEquals(ResponseConstant.success, response2.getCode());
        assertEquals(1, response2.getData().intValue());
        
        System.out.println("艺术家粉丝数量测试通过，粉丝数: " + response2.getData());
    }

    // ========== 综合测试方法 ==========

    @Test
    public void testCompleteUserFavoriteFlow() {
        System.out.println("测试用户收藏完整流程");
        
        // 1. 收藏歌曲
        ResponseMessage favoriteResponse = userFavoriteService.favoriteSong(testUserId, testSongId);
        assertEquals(ResponseConstant.success, favoriteResponse.getCode());
        
        // 2. 关注艺术家
        ResponseMessage followResponse = userFavoriteService.followArtist(testUserId, testArtistId);
        assertEquals(ResponseConstant.success, followResponse.getCode());
        
        // 3. 验证状态
        ResponseMessage<Boolean> songStatus = userFavoriteService.isUserFavoriteSong(testUserId, testSongId);
        assertTrue(songStatus.getData());
        
        ResponseMessage<Boolean> artistStatus = userFavoriteService.isUserFollowArtist(testUserId, testArtistId);
        assertTrue(artistStatus.getData());
        
        // 4. 获取列表
        ResponseMessage<List<SongVO>> favoriteSongs = userFavoriteService.getUserFavoriteSongs(testUserId);
        assertEquals(1, favoriteSongs.getData().size());
        
        ResponseMessage<List<Artist>> followArtists = userFavoriteService.getUserFollowArtists(testUserId);
        assertEquals(1, followArtists.getData().size());
        
        // 5. 统计数量
        ResponseMessage<Integer> songCount = userFavoriteService.getSongFavoriteCount(testSongId);
        assertEquals(1, songCount.getData().intValue());
        
        ResponseMessage<Integer> artistCount = userFavoriteService.getArtistFollowerCount(testArtistId);
        assertEquals(1, artistCount.getData().intValue());
        
        // 6. 取消收藏和关注
        ResponseMessage unfavoriteResponse = userFavoriteService.unfavoriteSong(testUserId, testSongId);
        assertEquals(ResponseConstant.success, unfavoriteResponse.getCode());
        
        ResponseMessage unfollowResponse = userFavoriteService.unfollowArtist(testUserId, testArtistId);
        assertEquals(ResponseConstant.success, unfollowResponse.getCode());
        
        // 7. 验证最终状态
        ResponseMessage<Boolean> finalSongStatus = userFavoriteService.isUserFavoriteSong(testUserId, testSongId);
        assertFalse(finalSongStatus.getData());
        
        ResponseMessage<Boolean> finalArtistStatus = userFavoriteService.isUserFollowArtist(testUserId, testArtistId);
        assertFalse(finalArtistStatus.getData());
        
        System.out.println("用户收藏完整流程测试通过！");
    }
}