package xyz.ztzhome.zblog.constant;

/**
 * 响应常量类
 * 定义统一的业务码和消息常量
 */
public class ResponseConstant {
    
    // ========== 业务状态码 ==========
    
    /**
     * 成功
     */
    public static final int success = 200;
    
    /**
     * 失败/错误
     */
    public static final int error = 400;
    
    /**
     * 无权限
     */
    public static final int no_permission = 403;
    
    /**
     * 服务器错误
     */
    public static final int server_error = 500;
    
    // ========== 消息常量 ==========
    
    /**
     * 成功消息
     */
    public static final String SUCCESS = "success";
    
    /**
     * 错误消息
     */
    public static final String ERROR = "error";
    
    /**
     * 服务器错误消息
     */
    public static final String SERVER_ERROR = "server_error";
    
    // ========== 时间常量 ==========
    
    /**
     * 默认超时时间（分钟）
     */
    public static final int Time_Out = 60;
    
    /**
     * 登录过期时间（分钟）
     */
    public static final int LoginTimOut = 60 * 24 * 7;
    
    /**
     * 头像路径过期时间（分钟）
     */
    public static final int AvatarTimOut = 60 * 24 * 7;
}
