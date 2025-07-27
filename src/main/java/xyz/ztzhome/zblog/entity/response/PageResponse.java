package xyz.ztzhome.zblog.entity.response;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    /**
     * 数据列表
     */
    private List<T> data;
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 当前页码
     */
    private int pageNum;
    
    /**
     * 每页大小
     */
    private int pageSize;
    
    /**
     * 总页数
     */
    private long totalPages;
    
    public PageResponse() {}
    
    public PageResponse(List<T> data, long total, int pageNum, int pageSize) {
        this.data = data;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (total + pageSize - 1) / pageSize;
    }
} 