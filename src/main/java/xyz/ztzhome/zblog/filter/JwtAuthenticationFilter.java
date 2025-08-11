package xyz.ztzhome.zblog.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import xyz.ztzhome.zblog.util.JwtToken;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class JwtAuthenticationFilter extends GenericFilterBean {

    // 公开路径（无需认证，任意方法）
    private static final Set<String> PUBLIC_ANY_METHOD_PATHS = Collections.unmodifiableSet(
            Set.of(
                    //test
                    "/test/**",
                    //
                    "/api/user/register",
                    "/api/user/login",
                    "/api/user/getUserAvatar",
                    "/api/admin/login",
                    "/api/admin/register",
                    "/recently-played-test.html",
                    "/api/song/cover-url",
                    "/api/song/search",
                    // lyric
                    "/api/lyric/getLyric"
            )
    );

    // 公开的只读路径（仅GET方法放行）
    private static final Set<String> PUBLIC_GET_PATHS = Collections.unmodifiableSet(
            Set.of(
                    // user
                    "/api/user/avatar",
                    // banner
                    "/api/banner",
                    "/api/banner/random",
                    // song
                    "/api/song/*",
                    "/api/song/random",
                    "/api/song/search",
                    "/api/song/search/page",
                    "/api/song/searchByStyle/page",
                    "/api/song/page",
                    "/api/song/url",
                    "/api/song/cover-url",
                    "/api/song/incrementPlayCount",
                    //lyric
                    "/api/lyric/getLyric",
                    // comment
                    "/api/comment/song",
                    "/api/comment/song/page",
                    "/api/comment/replies",
                    "/api/comment/replies/page",
                    "/api/comment/user",
                    "/api/comment/user/page",
                    "/api/comment/count",
                    "/api/comment/like/check",
                    "/api/comment/like/count",
                    // playlist
                    "/api/playlist/user",
                    "/api/playlist/detail",
                    "/api/playlist/info",
                    "/api/playlist/songs",
                    "/api/playlist/songs/page",
                    "/api/playlist/search",
                    "/api/playlist/search/page",
                    "/api/playlist/hot",
                    "/api/playlist/favorite/check",
                    "/api/playlist/favorite/count",
                    // user favorite (counts and check)
                    "/api/user/favorite/songs/check",
                    "/api/user/favorite/songs/count",
                    "/api/user/favorite/artists/check",
                    "/api/user/favorite/artists/followers",
                    "/api/user/favorite/playlists/check",
                    "/api/user/favorite/playlists/count",

                    // files
                    "/api/files/download/*"
            )
    );

    // 普通用户可以访问的路径
    private static final Set<String> USER_PATHS = Collections.unmodifiableSet(
            Set.of(
                    "/api/user/**",
                    "/api/user/favorites/**",
                    "/api/user/recently-played/**",
                    "/api/comment/**",
                    "/api/playlist/**",
                    "/api/song/**"
            )
    );

    // 管理员可以访问的路径
    private static final Set<String> ADMIN_PATHS = Collections.unmodifiableSet(
            Set.of(
                    "/**",
                    "/api/**",
                    "/api/user/**",
                    "/api/song/**"
                    )
    );

    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        final String path = request.getRequestURI();
        final String contextPath = request.getContextPath();
        final String requestPath = path.substring(contextPath.length());

        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // 1. 检查是否为公开路径
        if (isPathAllowed(requestPath, PUBLIC_ANY_METHOD_PATHS) ||
                ("GET".equalsIgnoreCase(request.getMethod()) && isPathAllowed(requestPath, PUBLIC_GET_PATHS))) {
            chain.doFilter(request, response);
            return;
        }

        // 2. 获取Token
        String headerToken = request.getHeader("Authorization");
        if (!StringUtils.hasText(headerToken)) {
            sendError(response, "Missing authorization token", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 3. 提取Token（去掉Bearer前缀）
        String token = extractPureToken(headerToken);
        if (token == null) {
            sendError(response, "Invalid token format", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 4. 验证Token
        try {
            // 先验证管理员Token
            if (isAdminTokenValid(token)) {
                handleAdmin(request, response, chain, token);
                return;
            }

            // 再验证普通用户Token
            if (isUserTokenValid(token)) {
                handleUser(request, response, chain, token, requestPath);
                return;
            }

            // 如果两种Token都无效，检查具体原因
            checkTokenExpiration(token, response);

        } catch (Exception e) {
            sendError(response, "Token verification failed: " + e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    //去掉前缀
    private String extractPureToken(String headerToken) {
        if (headerToken.startsWith("Bearer ")) {
            return headerToken.substring(7);
        }
        return headerToken;
    }

    private void handleAdmin(HttpServletRequest request, HttpServletResponse response,
                             FilterChain chain, String token) throws IOException, ServletException {

        // 管理员拥有所有权限，直接放行
        String adminAccount = JwtToken.getAdminAccountFromToken(token);
        request.setAttribute("adminAccount", adminAccount);
        chain.doFilter(request, response);
    }

    private void handleUser(HttpServletRequest request, HttpServletResponse response,
                            FilterChain chain, String token, String requestPath) throws IOException, ServletException {

        // 检查用户是否有权限访问该路径
        if (isPathAllowed(requestPath, USER_PATHS)
                || isPathAllowed(requestPath, PUBLIC_ANY_METHOD_PATHS)
                || ("GET".equalsIgnoreCase(request.getMethod()) && isPathAllowed(requestPath, PUBLIC_GET_PATHS))) {
            String userId = JwtToken.getAccountFromToken(token);
            request.setAttribute("userId", userId);
            chain.doFilter(request, response);
        } else {
            sendError(response, "Access denied. Insufficient permissions", HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private void checkTokenExpiration(String token, HttpServletResponse response) throws IOException {
        // 检查普通用户Token是否过期
        int userStatus = JwtToken.validateToken(token);
        if (userStatus == 2) {
            sendError(response, "User token expired", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 检查管理员Token是否过期
        int adminStatus = JwtToken.validateAdminToken(token);
        if (adminStatus == 2) {
            sendError(response, "Admin token expired", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 都不是过期，则是无效Token
        sendError(response, "Invalid token", HttpServletResponse.SC_UNAUTHORIZED);
    }

    private boolean isAdminTokenValid(String token) {
        return JwtToken.validateAdminToken(token) == 1;
    }

    private boolean isUserTokenValid(String token) {
        return JwtToken.validateToken(token) == 1;
    }

    // 使用AntPathMatcher检查路径是否匹配
    private boolean isPathAllowed(String requestPath, Set<String> allowedPaths) {
        for (String pattern : allowedPaths) {
            if (pathMatcher.match(pattern, requestPath)) {
                return true;
            }
        }
        return false;
    }

    private void sendError(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"code\": " + statusCode + ", \"message\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}