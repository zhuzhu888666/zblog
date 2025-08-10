package xyz.ztzhome.zblog.entity.response;

import lombok.Data;
import xyz.ztzhome.zblog.constant.ResponseConstant;

/**
 * 统一响应消息类
 * @param <T> 响应数据类型
 */
@Data
public class ResponseMessage<T> {
    private int code;   // 业务状态码
    private String message; // 提示信息
    private T data;

    public ResponseMessage() {}

    public ResponseMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseMessage(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ========== 便利的静态方法 ==========

    /**
     * 创建成功响应（无数据）
     */
    public static <T> ResponseMessage<T> success(String message) {
        return new ResponseMessage<>(ResponseConstant.success, message);
    }

    /**
     * 创建成功响应（有数据）
     */
    public static <T> ResponseMessage<T> success(String message, T data) {
        return new ResponseMessage<>(ResponseConstant.success, message, data);
    }

    /**
     * 创建成功响应（使用默认成功消息）
     */
    public static <T> ResponseMessage<T> success() {
        return new ResponseMessage<>(ResponseConstant.success, ResponseConstant.SUCCESS);
    }

    /**
     * 创建错误响应
     */
    public static <T> ResponseMessage<T> error(String message) {
        return new ResponseMessage<>(ResponseConstant.error, message);
    }

    /**
     * 创建错误响应（有数据）
     */
    public static <T> ResponseMessage<T> error(String message, T data) {
        return new ResponseMessage<>(ResponseConstant.error, message, data);
    }

    /**
     * 创建服务器错误响应
     */
    public static <T> ResponseMessage<T> serverError(String message) {
        return new ResponseMessage<>(ResponseConstant.server_error, message);
    }

    /**
     * 创建无权限响应
     */
    public static <T> ResponseMessage<T> noPermission(String message) {
        return new ResponseMessage<>(ResponseConstant.no_permission, message);
    }

    /**
     * 创建无权限响应（默认消息）
     */
    public static <T> ResponseMessage<T> noPermission() {
        return new ResponseMessage<>(ResponseConstant.no_permission, "无权限访问");
    }
}
