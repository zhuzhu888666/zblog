package xyz.ztzhome.zblog.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.filter.CorsFilter; // 改用Spring的CorsFilter
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import xyz.ztzhome.zblog.filter.JwtAuthenticationFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        // 1. 创建CORS配置
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");  // 允许所有域
        config.addAllowedHeader("*");         // 允许所有头
        config.addAllowedMethod("*");         // 允许所有方法

        // 2. 注册CORS配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // 3. 使用Spring的CorsFilter
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0); // 设置过滤器优先级
        return bean;
    }

    // 2. 注册 JWT 过滤器（优先级次之）
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthenticationFilter());
        registration.addUrlPatterns("/*");  // 拦截所有请求
        registration.setOrder(2);           // 优先级设置为2
        registration.setName("jwtFilter");  // 过滤器名称
        return registration;
    }
}