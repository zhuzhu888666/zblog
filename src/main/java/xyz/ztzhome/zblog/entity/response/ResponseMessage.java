package xyz.ztzhome.zblog.entity.response;

import lombok.Data;

//@Data自动生成模板get和set
@Data
public class ResponseMessage<T> {
    private int code;   // 业务状态码
    private String message; // 提示信息
    private T data;

    public ResponseMessage() {};

    public ResponseMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseMessage(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
