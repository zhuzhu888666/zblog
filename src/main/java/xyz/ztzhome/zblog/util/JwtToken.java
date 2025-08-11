package xyz.ztzhome.zblog.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import xyz.ztzhome.zblog.service.impl.MinioServiceImpl;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;

public class JwtToken {
    private static final Logger logger = LoggerFactory.getLogger(JwtToken.class);

    // 使用更安全的密钥生成方式
    private static final Key USER_SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final Key ADMIN_SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private static final long EXPIRATION = 24 * 60 * 60 * 1000; // 24小时

    // 生成普通用户 Token
    public static String generateToken(String userId) {
        return buildToken(userId, USER_SECRET_KEY);
    }

    // 生成管理员 Token
    public static String generateAdminToken(String adminName) {
        return buildToken(adminName, ADMIN_SECRET_KEY);
    }

    private static String buildToken(String subject, Key key) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // 验证普通用户 Token
    public static int validateToken(String token) {
        return validateToken(token, USER_SECRET_KEY);
    }

    // 验证管理员 Token
    public static int validateAdminToken(String token) {
        return validateToken(token, ADMIN_SECRET_KEY);
    }

    private static int validateToken(String token, Key key) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return 1; // 验证成功
        } catch (ExpiredJwtException e) {
            return 2; // Token 已过期
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            return 0; // Token 无效
        } catch (IllegalArgumentException e) {
            return 0; // Token 为空或格式错误
        }
    }

    // 从普通用户 Token 获取账户信息
    public static String getAccountFromToken(String token) {
        return extractSubject(token, USER_SECRET_KEY);
    }

    // 从管理员 Token 获取账户信息
    public static String getAdminAccountFromToken(String token) {
        return extractSubject(token, ADMIN_SECRET_KEY);
    }

    private static String extractSubject(String token, Key key) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            // 即使过期也返回账户信息
            return e.getClaims().getSubject();
        } catch (JwtException e) {
            logger.error("提取token异常：->无效的token：{}",e.getMessage());
            return  null;
        }
    }

    // 从请求头提取Token（可直接在Controller中使用）
    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}