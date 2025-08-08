package xyz.ztzhome.zblog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")          // 所有路径
                .allowedOriginPatterns("*") // 允许所有域
                .allowedMethods("*")        // 允许所有方法
                .allowedHeaders("*")         // 允许所有头
                .allowCredentials(true)      // 允许凭证
                .maxAge(3600);               // 1小时内不需要预检
    }
}