package xyz.ztzhome.zblog.entity.response;

import lombok.Data;

//@Data自动生成模板get和set
@Data
public class ResponseMessage<T> {
    private Integer code;   // 业务状态码
    private String message; // 提示信息
    private T data;
}
