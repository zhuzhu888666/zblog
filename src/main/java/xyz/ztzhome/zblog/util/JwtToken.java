package xyz.ztzhome.zblog.util;

import io.jsonwebtoken.*;

import java.util.Date;

public class JwtToken {

    // 密钥（实际应从配置中读取）
    private static final String SECRET_KEY = "YourSuperLongSecretKey1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz12345678901";
    private static final String Admin_KEY = "admin" + SECRET_KEY + "123";
    private static final long EXPIRATION = 24 * 60 * 60 * 1000;

    // 生成普通用户 Token
    public static String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // 生成管理员 Token
    public static String generateAdminToken(String adminName) {
        return Jwts.builder()
                .setSubject(adminName)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, Admin_KEY)
                .compact();
    }

    // 验证普通用户 Token
    public static int validateToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return 1; // 验证成功
        } catch (ExpiredJwtException e) {
            return 2; // Token 已过期
        } catch (JwtException e) {
            return 0; // 其他验证失败
        } catch (Exception e) {
            return 0; // 未知异常
        }
    }

    // 验证管理员 Token
    public static int validateAdminToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(Admin_KEY)
                    .build()
                    .parseClaimsJws(token);
            return 1; // 验证成功
        } catch (ExpiredJwtException e) {
            return 2; // Token 已过期
        } catch (JwtException e) {
            return 0; // 其他验证失败
        } catch (Exception e) {
            return 0; // 未知异常
        }
    }

    // 新增方法：从普通用户 Token 获取账户信息
    public static String getAccountFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject(); // 返回账户名
        } catch (ExpiredJwtException e) {
            // 即使过期也返回账户信息
            return e.getClaims().getSubject();
        } catch (JwtException e) {
            throw new RuntimeException("无效的 Token: " + e.getMessage());
        }
    }

    // 新增方法：从管理员 Token 获取账户信息
    public static String getAdminAccountFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Admin_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject(); // 返回管理员账户名
        } catch (ExpiredJwtException e) {
            // 即使过期也返回账户信息
            return e.getClaims().getSubject();
        } catch (JwtException e) {
            throw new RuntimeException("无效的管理员 Token: " + e.getMessage());
        }
    }

    // 测试主方法
    public static void main(String[] args) {
        String userId = "testUser123";
        String adminName = "adminUser";

        // 测试普通用户 Token
        String userToken = generateToken(userId);
        System.out.println("普通用户 Token 验证结果: " + validateToken(userToken));
        System.out.println("从普通 Token 获取账户: " + getAccountFromToken(userToken));

        // 测试管理员 Token
        String adminToken = generateAdminToken(adminName);
        System.out.println("管理员 Token 验证结果: " + validateAdminToken(adminToken));
        System.out.println("从管理员 Token 获取账户: " + getAdminAccountFromToken(adminToken));

        // 测试过期 Token
        String expiredToken = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        System.out.println("过期 Token 获取账户: " + getAccountFromToken(expiredToken));
    }
}