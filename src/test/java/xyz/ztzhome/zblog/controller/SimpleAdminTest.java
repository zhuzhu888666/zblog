package xyz.ztzhome.zblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import xyz.ztzhome.zblog.entity.Bean.Admin;
import xyz.ztzhome.zblog.entity.DTO.LoginDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 简化的管理员测试类
 * 只测试基本的管理员功能，避免复杂的DTO问题
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SimpleAdminTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    /**
     * 测试管理员注册 - 成功场景
     */
    @Test
    void testAdminRegister_Success() throws Exception {
        Admin admin = new Admin();
        admin.setAccount("simpletest");
        admin.setPassword("123456");
        admin.setEmail("simpletest@example.com");

        mockMvc.perform(post("/api/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    /**
     * 测试管理员注册 - 缺少参数
     */
    @Test
    void testAdminRegister_MissingParameters() throws Exception {
        Admin admin = new Admin();
        admin.setAccount("simpletest");
        // 缺少密码

        mockMvc.perform(post("/api/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("缺少必要参数"));
    }

    /**
     * 测试管理员登录 - 成功场景
     */
    @Test
    void testAdminLogin_Success() throws Exception {
        // 先注册一个管理员
        Admin admin = new Admin();
        admin.setAccount("logintest");
        admin.setPassword("123456");
        admin.setEmail("logintest@example.com");

        mockMvc.perform(post("/api/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(admin)))
                .andExpect(status().isOk());

        // 登录
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("logintest");
        loginDTO.setPassword("123456");

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.account").value("logintest"))
                .andExpect(jsonPath("$.data.token").exists());
    }

    /**
     * 测试管理员登录 - 账号不存在
     */
    @Test
    void testAdminLogin_AccountNotExists() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("nonexistent");
        loginDTO.setPassword("123456");

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("账号不存在"));
    }

    /**
     * 测试管理员登录 - 密码错误
     */
    @Test
    void testAdminLogin_WrongPassword() throws Exception {
        // 先注册一个管理员
        Admin admin = new Admin();
        admin.setAccount("passwordtest");
        admin.setPassword("123456");
        admin.setEmail("passwordtest@example.com");

        mockMvc.perform(post("/api/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(admin)))
                .andExpect(status().isOk());

        // 使用错误密码登录
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("passwordtest");
        loginDTO.setPassword("wrongpassword");

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("密码错误"));
    }

    /**
     * 测试根据账号获取管理员信息
     */
    @Test
    void testGetAdminByAccount() throws Exception {
        // 先注册一个管理员
        Admin admin = new Admin();
        admin.setAccount("getbyaccounttest");
        admin.setPassword("123456");
        admin.setEmail("getbyaccounttest@example.com");

        mockMvc.perform(post("/api/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(admin)))
                .andExpect(status().isOk());

        // 根据账号获取管理员信息
        mockMvc.perform(get("/api/admin/info/account")
                .param("account", "getbyaccounttest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.account").value("getbyaccounttest"));
    }
}
