package xyz.ztzhome.zblog.entity.Bean;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class Banner implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 轮播图标题
     */
    private String title;

    /**
     * 存储在MinIO中的对象名称
     */
    private String objectName;

    /**
     * 点击轮播图后跳转的链接
     */
    private String linkUrl;

    /**
     * 排序值，越小越靠前
     */
    private Integer sortOrder;

    /**
     * 是否激活显示 (1-是, 0-否)
     */
    private Boolean isActive;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 图片临时URL (非数据库字段)
     * 用于临时存放从MinIO获取的带签名的图片URL。
     */
    private transient String imageUrl;
} 