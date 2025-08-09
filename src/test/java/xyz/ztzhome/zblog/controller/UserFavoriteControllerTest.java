package xyz.ztzhome.zblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import xyz.ztzhome.zblog.entity.Bean.Artist;
import xyz.ztzhome.zblog.entity.Bean.Song;
import xyz.ztzhome.zblog.entity.Bean.User;
import xyz.ztzhome.zblog.entity.DTO.FavoriteDTO;
import xyz.ztzhome.zblog.entity.DTO.FollowDTO;
import xyz.ztzhome.zblog.mapper.ArtistMapper;
import xyz.ztzhome.zblog.mapper.SongMapper;
import xyz.ztzhome.zblog.mapper.UserMapper;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserFavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        testUser.setAccount("api_test_user");
        testUser.setPassword("password123");
        testUser.setEmail("api_test@example.com");
        testUser.setNickname("API测试用户");
        userMapper.insertUser(testUser);
        testUserId = testUser.getId();

        // 创建测试艺术家
        Artist testArtist = new Artist();
        testArtist.setArtistName("API测试艺术家");
        testArtist.setGender("female");
        testArtist.setArea("US");
        testArtist.setIntroduction("这是一个API测试艺术家");
        artistMapper.insertArtist(testArtist);
        testArtistId = testArtist.getId();

        // 创建测试歌曲
        Song testSong = new Song();
        testSong.setName("API测试歌曲");
        testSong.setArtistId(testArtistId);
        testSong.setAlbum("API测试专辑");
        testSong.setDuration("4:15");
        testSong.setStyle("摇滚");
        testSong.setReleaseTime(new Date());
        songMapper.insertSong(testSong);
        testSongId = testSong.getId();

        System.out.println("API测试数据准备完成 - 用户ID: " + testUserId + ", 歌曲ID: " + testSongId + ", 艺术家ID: " + testArtistId);
    }

    // ========== 收藏歌曲API测试 ==========

    @Test
    public void testFavoriteSongAPI() throws Exception {
        System.out.println("测试收藏歌曲API");

        FavoriteDTO favoriteDTO = new FavoriteDTO();
        favoriteDTO.setUserId(testUserId);
        favoriteDTO.setSongId(testSongId);

        mockMvc.perform(post("/api/users/favorites/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(favoriteDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("收藏成功"));

        System.out.println("收藏歌曲API测试通过");
    }

    @Test
    public void testUnfavoriteSongAPI() throws Exception {
        System.out.println("测试取消收藏歌曲API");

        FavoriteDTO favoriteDTO = new FavoriteDTO();
        favoriteDTO.setUserId(testUserId);
        favoriteDTO.setSongId(testSongId);

        // 先收藏
        mockMvc.perform(post("/api/users/favorites/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(favoriteDTO)))
                .andExpect(status().isOk());

        // 再取消收藏
        mockMvc.perform(post("/api/users/favorites/songs/unfavorite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(favoriteDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("取消收藏成功"));

        System.out.println("取消收藏歌曲API测试通过");
    }

    @Test
    public void testCheckFavoriteSongAPI() throws Exception {
        System.out.println("测试检查收藏状态API");

        // 检查未收藏状态
        mockMvc.perform(get("/api/users/favorites/songs/check")
                .param("userId", testUserId.toString())
                .param("songId", testSongId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(false));

        // 收藏后检查
        FavoriteDTO favoriteDTO = new FavoriteDTO();
        favoriteDTO.setUserId(testUserId);
        favoriteDTO.setSongId(testSongId);

        mockMvc.perform(post("/api/users/favorites/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(favoriteDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/favorites/songs/check")
                .param("userId", testUserId.toString())
                .param("songId", testSongId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(true));

        System.out.println("检查收藏状态API测试通过");
    }

    @Test
    public void testGetUserFavoriteSongsAPI() throws Exception {
        System.out.println("测试获取用户收藏歌曲列表API");

        // 先收藏一首歌
        FavoriteDTO favoriteDTO = new FavoriteDTO();
        favoriteDTO.setUserId(testUserId);
        favoriteDTO.setSongId(testSongId);

        mockMvc.perform(post("/api/users/favorites/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(favoriteDTO)))
                .andExpect(status().isOk());

        // 获取收藏列表
        mockMvc.perform(get("/api/users/favorites/songs/list")
                .param("userId", testUserId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(testSongId));

        System.out.println("获取用户收藏歌曲列表API测试通过");
    }

    @Test
    public void testGetUserFavoriteSongsWithPageAPI() throws Exception {
        System.out.println("测试分页获取用户收藏歌曲列表API");

        // 先收藏一首歌
        FavoriteDTO favoriteDTO = new FavoriteDTO();
        favoriteDTO.setUserId(testUserId);
        favoriteDTO.setSongId(testSongId);

        mockMvc.perform(post("/api/users/favorites/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(favoriteDTO)))
                .andExpect(status().isOk());

        // 分页获取收藏列表
        mockMvc.perform(get("/api/users/favorites/songs/page")
                .param("userId", testUserId.toString())
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.pageNum").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.data.length()").value(1));

        System.out.println("分页获取用户收藏歌曲列表API测试通过");
    }

    @Test
    public void testGetSongFavoriteCountAPI() throws Exception {
        System.out.println("测试获取歌曲收藏数量API");

        // 获取初始收藏数
        mockMvc.perform(get("/api/users/favorites/songs/count")
                .param("songId", testSongId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(0));

        // 收藏后获取数量
        FavoriteDTO favoriteDTO = new FavoriteDTO();
        favoriteDTO.setUserId(testUserId);
        favoriteDTO.setSongId(testSongId);

        mockMvc.perform(post("/api/users/favorites/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(favoriteDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/favorites/songs/count")
                .param("songId", testSongId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(1));

        System.out.println("获取歌曲收藏数量API测试通过");
    }

    // ========== 关注艺术家API测试 ==========

    @Test
    public void testFollowArtistAPI() throws Exception {
        System.out.println("测试关注艺术家API");

        FollowDTO followDTO = new FollowDTO();
        followDTO.setUserId(testUserId);
        followDTO.setArtistId(testArtistId);

        mockMvc.perform(post("/api/users/favorites/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("关注成功"));

        System.out.println("关注艺术家API测试通过");
    }

    @Test
    public void testUnfollowArtistAPI() throws Exception {
        System.out.println("测试取消关注艺术家API");

        FollowDTO followDTO = new FollowDTO();
        followDTO.setUserId(testUserId);
        followDTO.setArtistId(testArtistId);

        // 先关注
        mockMvc.perform(post("/api/users/favorites/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDTO)))
                .andExpect(status().isOk());

        // 再取消关注
        mockMvc.perform(post("/api/users/favorites/artists/unfollow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("取消关注成功"));

        System.out.println("取消关注艺术家API测试通过");
    }

    @Test
    public void testCheckFollowArtistAPI() throws Exception {
        System.out.println("测试检查关注状态API");

        // 检查未关注状态
        mockMvc.perform(get("/api/users/favorites/artists/check")
                .param("userId", testUserId.toString())
                .param("artistId", testArtistId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(false));

        // 关注后检查
        FollowDTO followDTO = new FollowDTO();
        followDTO.setUserId(testUserId);
        followDTO.setArtistId(testArtistId);

        mockMvc.perform(post("/api/users/favorites/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/favorites/artists/check")
                .param("userId", testUserId.toString())
                .param("artistId", testArtistId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(true));

        System.out.println("检查关注状态API测试通过");
    }

    @Test
    public void testGetUserFollowArtistsAPI() throws Exception {
        System.out.println("测试获取用户关注艺术家列表API");

        // 先关注一个艺术家
        FollowDTO followDTO = new FollowDTO();
        followDTO.setUserId(testUserId);
        followDTO.setArtistId(testArtistId);

        mockMvc.perform(post("/api/users/favorites/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDTO)))
                .andExpect(status().isOk());

        // 获取关注列表
        mockMvc.perform(get("/api/users/favorites/artists/list")
                .param("userId", testUserId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(testArtistId));

        System.out.println("获取用户关注艺术家列表API测试通过");
    }

    @Test
    public void testGetArtistFollowerCountAPI() throws Exception {
        System.out.println("测试获取艺术家粉丝数量API");

        // 获取初始粉丝数
        mockMvc.perform(get("/api/users/favorites/artists/followers")
                .param("artistId", testArtistId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(0));

        // 关注后获取数量
        FollowDTO followDTO = new FollowDTO();
        followDTO.setUserId(testUserId);
        followDTO.setArtistId(testArtistId);

        mockMvc.perform(post("/api/users/favorites/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/favorites/artists/followers")
                .param("artistId", testArtistId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(1));

        System.out.println("获取艺术家粉丝数量API测试通过");
    }

    // ========== 综合API测试 ==========

    @Test
    public void testCompleteAPIFlow() throws Exception {
        System.out.println("测试完整API流程");

        // 1. 收藏歌曲
        FavoriteDTO favoriteDTO = new FavoriteDTO();
        favoriteDTO.setUserId(testUserId);
        favoriteDTO.setSongId(testSongId);

        mockMvc.perform(post("/api/users/favorites/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(favoriteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        // 2. 关注艺术家
        FollowDTO followDTO = new FollowDTO();
        followDTO.setUserId(testUserId);
        followDTO.setArtistId(testArtistId);

        mockMvc.perform(post("/api/users/favorites/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        // 3. 检查状态
        mockMvc.perform(get("/api/users/favorites/songs/check")
                .param("userId", testUserId.toString())
                .param("songId", testSongId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));

        mockMvc.perform(get("/api/users/favorites/artists/check")
                .param("userId", testUserId.toString())
                .param("artistId", testArtistId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));

        // 4. 获取列表
        mockMvc.perform(get("/api/users/favorites/songs/list")
                .param("userId", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));

        mockMvc.perform(get("/api/users/favorites/artists/list")
                .param("userId", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));

        // 5. 取消收藏和关注
        mockMvc.perform(post("/api/users/favorites/songs/unfavorite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(favoriteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        mockMvc.perform(post("/api/users/favorites/artists/unfollow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        System.out.println("完整API流程测试通过！");
    }
}