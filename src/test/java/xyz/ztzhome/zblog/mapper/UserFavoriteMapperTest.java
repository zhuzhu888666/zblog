package xyz.ztzhome.zblog.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import xyz.ztzhome.zblog.entity.Bean.Artist;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.Bean.UserFavoriteSong;
import xyz.ztzhome.zblog.entity.Bean.UserFollowArtist;
import xyz.ztzhome.zblog.entity.VO.SongVO;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserFavoriteMapperTest {

    @Autowired
    private UserFavoriteSongMapper userFavoriteSongMapper;

    @Autowired
    private UserFollowArtistMapper userFollowArtistMapper;

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
        testUser.setAccount("mapper_test_user");
        testUser.setPassword("password123");
        testUser.setEmail("mapper_test@example.com");
        testUser.setNickname("Mapper测试用户");
        userMapper.insertUser(testUser);
        testUserId = testUser.getId();

        // 创建测试艺术家
        Artist testArtist = new Artist();
        testArtist.setArtistName("Mapper测试艺术家");
        testArtist.setGender("male");
        testArtist.setArea("CN");
        testArtist.setIntroduction("这是一个Mapper测试艺术家");
        artistMapper.insertArtist(testArtist);
        testArtistId = testArtist.getId();

        // 创建测试歌曲
        Song testSong = new Song();
        testSong.setName("Mapper测试歌曲");
        testSong.setArtistId(testArtistId);
        testSong.setAlbum("Mapper测试专辑");
        testSong.setDuration("3:45");
        testSong.setStyle("电子");
        testSong.setReleaseTime(new Date());
        songMapper.insertSong(testSong);
        testSongId = testSong.getId();

        System.out.println("Mapper测试数据准备完成 - 用户ID: " + testUserId + ", 歌曲ID: " + testSongId + ", 艺术家ID: " + testArtistId);
    }

    // ========== UserFavoriteSongMapper 测试 ==========

    @Test
    public void testInsertUserFavoriteSong() {
        System.out.println("测试插入用户收藏歌曲");

        UserFavoriteSong userFavoriteSong = new UserFavoriteSong();
        userFavoriteSong.setUserId(testUserId);
        userFavoriteSong.setSongId(testSongId);

        int result = userFavoriteSongMapper.insertUserFavoriteSong(userFavoriteSong);
        
        assertEquals(1, result);
        assertNotNull(userFavoriteSong.getId());
        assertTrue(userFavoriteSong.getId() > 0);

        System.out.println("插入用户收藏歌曲测试通过，生成ID: " + userFavoriteSong.getId());
    }

    @Test
    public void testDeleteUserFavoriteSong() {
        System.out.println("测试删除用户收藏歌曲");

        // 先插入
        UserFavoriteSong userFavoriteSong = new UserFavoriteSong();
        userFavoriteSong.setUserId(testUserId);
        userFavoriteSong.setSongId(testSongId);
        userFavoriteSongMapper.insertUserFavoriteSong(userFavoriteSong);

        // 再删除
        int result = userFavoriteSongMapper.deleteUserFavoriteSong(testUserId, testSongId);
        assertEquals(1, result);

        // 验证已删除
        boolean exists = userFavoriteSongMapper.isUserFavoriteSong(testUserId, testSongId);
        assertFalse(exists);

        System.out.println("删除用户收藏歌曲测试通过");
    }

    @Test
    public void testIsUserFavoriteSong() {
        System.out.println("测试检查用户是否收藏歌曲");

        // 未收藏时应为false
        boolean beforeFavorite = userFavoriteSongMapper.isUserFavoriteSong(testUserId, testSongId);
        assertFalse(beforeFavorite);

        // 收藏后应为true
        UserFavoriteSong userFavoriteSong = new UserFavoriteSong();
        userFavoriteSong.setUserId(testUserId);
        userFavoriteSong.setSongId(testSongId);
        userFavoriteSongMapper.insertUserFavoriteSong(userFavoriteSong);

        boolean afterFavorite = userFavoriteSongMapper.isUserFavoriteSong(testUserId, testSongId);
        assertTrue(afterFavorite);

        System.out.println("检查用户收藏状态测试通过");
    }

    @Test
    public void testSelectFavoriteSongsByUserId() {
        System.out.println("测试根据用户ID查询收藏歌曲列表");

        // 收藏歌曲
        UserFavoriteSong userFavoriteSong = new UserFavoriteSong();
        userFavoriteSong.setUserId(testUserId);
        userFavoriteSong.setSongId(testSongId);
        userFavoriteSongMapper.insertUserFavoriteSong(userFavoriteSong);

        // 查询收藏列表
        List<SongVO> favoriteSongs = userFavoriteSongMapper.selectFavoriteSongsByUserId(testUserId);
        
        assertNotNull(favoriteSongs);
        assertEquals(1, favoriteSongs.size());
        assertEquals(testSongId.longValue(), favoriteSongs.get(0).getId());
        assertEquals("Mapper测试歌曲", favoriteSongs.get(0).getName());
        assertEquals("Mapper测试艺术家", favoriteSongs.get(0).getArtistName());

        System.out.println("查询收藏歌曲列表测试通过，收藏数量: " + favoriteSongs.size());
    }

    @Test
    public void testSelectFavoriteSongsByUserIdWithPage() {
        System.out.println("测试分页查询用户收藏歌曲");

        // 收藏歌曲
        UserFavoriteSong userFavoriteSong = new UserFavoriteSong();
        userFavoriteSong.setUserId(testUserId);
        userFavoriteSong.setSongId(testSongId);
        userFavoriteSongMapper.insertUserFavoriteSong(userFavoriteSong);

        // 分页查询
        List<SongVO> favoriteSongs = userFavoriteSongMapper.selectFavoriteSongsByUserIdWithPage(testUserId, 0, 10);
        
        assertNotNull(favoriteSongs);
        assertEquals(1, favoriteSongs.size());
        assertEquals(testSongId.longValue(), favoriteSongs.get(0).getId());

        System.out.println("分页查询收藏歌曲测试通过");
    }

    @Test
    public void testCountFavoriteSongsByUserId() {
        System.out.println("测试统计用户收藏歌曲数量");

        // 初始应为0
        int initialCount = userFavoriteSongMapper.countFavoriteSongsByUserId(testUserId);
        assertEquals(0, initialCount);

        // 收藏后应为1
        UserFavoriteSong userFavoriteSong = new UserFavoriteSong();
        userFavoriteSong.setUserId(testUserId);
        userFavoriteSong.setSongId(testSongId);
        userFavoriteSongMapper.insertUserFavoriteSong(userFavoriteSong);

        int afterCount = userFavoriteSongMapper.countFavoriteSongsByUserId(testUserId);
        assertEquals(1, afterCount);

        System.out.println("统计用户收藏歌曲数量测试通过");
    }

    @Test
    public void testCountUsersBySongId() {
        System.out.println("测试统计歌曲被收藏次数");

        // 初始应为0
        int initialCount = userFavoriteSongMapper.countUsersBySongId(testSongId);
        assertEquals(0, initialCount);

        // 收藏后应为1
        UserFavoriteSong userFavoriteSong = new UserFavoriteSong();
        userFavoriteSong.setUserId(testUserId);
        userFavoriteSong.setSongId(testSongId);
        userFavoriteSongMapper.insertUserFavoriteSong(userFavoriteSong);

        int afterCount = userFavoriteSongMapper.countUsersBySongId(testSongId);
        assertEquals(1, afterCount);

        System.out.println("统计歌曲被收藏次数测试通过");
    }

    // ========== UserFollowArtistMapper 测试 ==========

    @Test
    public void testInsertUserFollowArtist() {
        System.out.println("测试插入用户关注艺术家");

        UserFollowArtist userFollowArtist = new UserFollowArtist();
        userFollowArtist.setUserId(testUserId);
        userFollowArtist.setArtistId(testArtistId);

        int result = userFollowArtistMapper.insertUserFollowArtist(userFollowArtist);
        
        assertEquals(1, result);
        assertNotNull(userFollowArtist.getId());
        assertTrue(userFollowArtist.getId() > 0);

        System.out.println("插入用户关注艺术家测试通过，生成ID: " + userFollowArtist.getId());
    }

    @Test
    public void testDeleteUserFollowArtist() {
        System.out.println("测试删除用户关注艺术家");

        // 先插入
        UserFollowArtist userFollowArtist = new UserFollowArtist();
        userFollowArtist.setUserId(testUserId);
        userFollowArtist.setArtistId(testArtistId);
        userFollowArtistMapper.insertUserFollowArtist(userFollowArtist);

        // 再删除
        int result = userFollowArtistMapper.deleteUserFollowArtist(testUserId, testArtistId);
        assertEquals(1, result);

        // 验证已删除
        boolean exists = userFollowArtistMapper.isUserFollowArtist(testUserId, testArtistId);
        assertFalse(exists);

        System.out.println("删除用户关注艺术家测试通过");
    }

    @Test
    public void testIsUserFollowArtist() {
        System.out.println("测试检查用户是否关注艺术家");

        // 未关注时应为false
        boolean beforeFollow = userFollowArtistMapper.isUserFollowArtist(testUserId, testArtistId);
        assertFalse(beforeFollow);

        // 关注后应为true
        UserFollowArtist userFollowArtist = new UserFollowArtist();
        userFollowArtist.setUserId(testUserId);
        userFollowArtist.setArtistId(testArtistId);
        userFollowArtistMapper.insertUserFollowArtist(userFollowArtist);

        boolean afterFollow = userFollowArtistMapper.isUserFollowArtist(testUserId, testArtistId);
        assertTrue(afterFollow);

        System.out.println("检查用户关注状态测试通过");
    }

    @Test
    public void testSelectFollowArtistsByUserId() {
        System.out.println("测试根据用户ID查询关注艺术家列表");

        // 关注艺术家
        UserFollowArtist userFollowArtist = new UserFollowArtist();
        userFollowArtist.setUserId(testUserId);
        userFollowArtist.setArtistId(testArtistId);
        userFollowArtistMapper.insertUserFollowArtist(userFollowArtist);

        // 查询关注列表
        List<Artist> followArtists = userFollowArtistMapper.selectFollowArtistsByUserId(testUserId);
        
        assertNotNull(followArtists);
        assertEquals(1, followArtists.size());
        assertEquals(testArtistId.longValue(), followArtists.get(0).getId());
        assertEquals("Mapper测试艺术家", followArtists.get(0).getArtistName());

        System.out.println("查询关注艺术家列表测试通过，关注数量: " + followArtists.size());
    }

    @Test
    public void testSelectFollowArtistsByUserIdWithPage() {
        System.out.println("测试分页查询用户关注艺术家");

        // 关注艺术家
        UserFollowArtist userFollowArtist = new UserFollowArtist();
        userFollowArtist.setUserId(testUserId);
        userFollowArtist.setArtistId(testArtistId);
        userFollowArtistMapper.insertUserFollowArtist(userFollowArtist);

        // 分页查询
        List<Artist> followArtists = userFollowArtistMapper.selectFollowArtistsByUserIdWithPage(testUserId, 0, 10);
        
        assertNotNull(followArtists);
        assertEquals(1, followArtists.size());
        assertEquals(testArtistId.longValue(), followArtists.get(0).getId());

        System.out.println("分页查询关注艺术家测试通过");
    }

    @Test
    public void testCountFollowArtistsByUserId() {
        System.out.println("测试统计用户关注艺术家数量");

        // 初始应为0
        int initialCount = userFollowArtistMapper.countFollowArtistsByUserId(testUserId);
        assertEquals(0, initialCount);

        // 关注后应为1
        UserFollowArtist userFollowArtist = new UserFollowArtist();
        userFollowArtist.setUserId(testUserId);
        userFollowArtist.setArtistId(testArtistId);
        userFollowArtistMapper.insertUserFollowArtist(userFollowArtist);

        int afterCount = userFollowArtistMapper.countFollowArtistsByUserId(testUserId);
        assertEquals(1, afterCount);

        System.out.println("统计用户关注艺术家数量测试通过");
    }

    @Test
    public void testCountUsersByArtistId() {
        System.out.println("测试统计艺术家粉丝数量");

        // 初始应为0
        int initialCount = userFollowArtistMapper.countUsersByArtistId(testArtistId);
        assertEquals(0, initialCount);

        // 关注后应为1
        UserFollowArtist userFollowArtist = new UserFollowArtist();
        userFollowArtist.setUserId(testUserId);
        userFollowArtist.setArtistId(testArtistId);
        userFollowArtistMapper.insertUserFollowArtist(userFollowArtist);

        int afterCount = userFollowArtistMapper.countUsersByArtistId(testArtistId);
        assertEquals(1, afterCount);

        System.out.println("统计艺术家粉丝数量测试通过");
    }

    // ========== 综合测试 ==========

    @Test
    public void testCompleteMapperFlow() {
        System.out.println("测试Mapper层完整流程");

        // 1. 收藏歌曲
        UserFavoriteSong userFavoriteSong = new UserFavoriteSong();
        userFavoriteSong.setUserId(testUserId);
        userFavoriteSong.setSongId(testSongId);
        int favoriteResult = userFavoriteSongMapper.insertUserFavoriteSong(userFavoriteSong);
        assertEquals(1, favoriteResult);

        // 2. 关注艺术家
        UserFollowArtist userFollowArtist = new UserFollowArtist();
        userFollowArtist.setUserId(testUserId);
        userFollowArtist.setArtistId(testArtistId);
        int followResult = userFollowArtistMapper.insertUserFollowArtist(userFollowArtist);
        assertEquals(1, followResult);

        // 3. 验证状态
        assertTrue(userFavoriteSongMapper.isUserFavoriteSong(testUserId, testSongId));
        assertTrue(userFollowArtistMapper.isUserFollowArtist(testUserId, testArtistId));

        // 4. 查询列表
        List<SongVO> favoriteSongs = userFavoriteSongMapper.selectFavoriteSongsByUserId(testUserId);
        assertEquals(1, favoriteSongs.size());

        List<Artist> followArtists = userFollowArtistMapper.selectFollowArtistsByUserId(testUserId);
        assertEquals(1, followArtists.size());

        // 5. 统计数量
        assertEquals(1, userFavoriteSongMapper.countFavoriteSongsByUserId(testUserId));
        assertEquals(1, userFavoriteSongMapper.countUsersBySongId(testSongId));
        assertEquals(1, userFollowArtistMapper.countFollowArtistsByUserId(testUserId));
        assertEquals(1, userFollowArtistMapper.countUsersByArtistId(testArtistId));

        // 6. 删除记录
        int deleteFavoriteResult = userFavoriteSongMapper.deleteUserFavoriteSong(testUserId, testSongId);
        assertEquals(1, deleteFavoriteResult);

        int deleteFollowResult = userFollowArtistMapper.deleteUserFollowArtist(testUserId, testArtistId);
        assertEquals(1, deleteFollowResult);

        // 7. 验证删除
        assertFalse(userFavoriteSongMapper.isUserFavoriteSong(testUserId, testSongId));
        assertFalse(userFollowArtistMapper.isUserFollowArtist(testUserId, testArtistId));

        System.out.println("Mapper层完整流程测试通过！");
    }
}