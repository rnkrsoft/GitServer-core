package com.rnkrsoft.orm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页对象
 * Created by woate on 2020/02/28.
 */
public class Pagination<T> {
    /**
     * 总条数
     */
    int total = 0;
    /**
     * 分页大小
     */
    int pageSize = 15;
    /**
     * 当前第几页
     */
    int curPageNo = 1;
    /**
     * 分页数
     */
    int pageNum = 0;
    /**
     * 当前分页的数据
     */
    final List<T> records = new ArrayList();
    /**
     * 是否已准备好数据
     */
    boolean ready = false;
    /**
     * 是否统计总条数
     */
    boolean statsTotal = true;
    private T entity;
    private Map<String, Object> params = new HashMap<String, Object>();

    public Pagination(int pageSize, int curPageNo, T entity) {
        validate(pageSize, curPageNo);
        this.pageSize = pageSize;
        this.curPageNo = curPageNo;
        this.entity = entity;
    }

    public Pagination(int pageSize, int curPageNo) {
        this(pageSize, curPageNo, (T)null);
    }

    public Pagination(int pageSize, int curPageNo, int total) {
        this(pageSize, curPageNo, total, null);
    }

    public Pagination(int pageSize, int curPageNo, int total, T entity) {
        this(pageSize, curPageNo, entity);
        setTotal(total);

    }

    /**
     * 进行逻辑分页
     * @param pageSize 分页大小
     * @param curPageNo 当前页
     * @param data 数据
     */
    public Pagination(int pageSize, int curPageNo, List<T> data) {
        this(pageSize, curPageNo, (T)null);
        setTotal(data.size());
        List<T> result = new ArrayList();
        int offset = (curPageNo - 1) * pageSize;
        for (int i = offset; i < offset + pageSize && i < data.size(); i++) {
            result.add(data.get(i));
        }
        this.ready = true;
        this.records.clear();
        this.records.addAll(result);
    }

    /**
     * 如果超过最大页数，则重设总条数
     * @param total
     */
    public void ifOverPageNumResetCurPageNoAndTotal(int total) {
        this.total = total;
        //如果超过最大页数，则重设总条数
        if(total == 0){
            curPageNo = 1;
        }else if (((total - 1) / pageSize) + 1 < curPageNo) {
            curPageNo = ((total - 1) / pageSize) + 1 ;
        }
        this.pageNum = (total + pageSize -1 ) / pageSize;
    }

    public void setTotal(int total) {
        this.total = total;
        //如果设置了总条数，不执行统计
        if(total > 0){
            //如果传入的总条数超过了分页大小乘以总页数，则使用统计
            if (((total - 1) / pageSize) + 1 < curPageNo) {
                statsTotal = true;
            }else{
                this.pageNum = (total + pageSize -1 ) / pageSize;
                statsTotal = false;
            }
        }else{
            statsTotal = true;
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCurPageNo() {
        return curPageNo;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getSkipRecordNum(){
        return this.pageSize * (this.getCurPageNo() - 1);
    }

    public boolean isReady() {
        return ready;
    }

    void validate(int pageSize, int pageNo) {
        if (pageSize < 1 || pageSize > 1000) {
            throw new IllegalArgumentException("无效分页大小");
        }
        if (pageNo < 1) {
            throw new IllegalArgumentException("无效页数");
        }
        if (pageNum < 0) {
            throw new IllegalArgumentException("无效页数");
        }
        if (total < 0) {
            throw new IllegalArgumentException("无效总条数");
        }
    }

    /**
     * 满足条件的记录总条数
     *
     * @return
     */
    public int getTotal() {
        if (!this.ready) {
            throw new IllegalArgumentException("分页数据未准备就绪");
        }
        return total;
    }

    /**
     * @return
     */
    public List<T> getRecords() {
        if (!this.ready) {
            throw new IllegalArgumentException("分页数据未准备就绪");
        }
        return records;
    }

    /**
     * 当前页是否为最后一页
     *
     * @return
     */
    public boolean isLastPage() {
        if (!this.ready) {
            throw new IllegalArgumentException("分页数据未准备就绪");
        }
        return ((total - 1) / pageSize) + 1 == curPageNo;
    }

    /**
     * 当前页是否为第一页
     *
     * @return
     */
    public boolean isFirstPage() {
        if (!this.ready) {
            throw new IllegalArgumentException("分页数据未准备就绪");
        }
        return curPageNo == 1;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setRecords(List records) {
        if (this.ready) {
            throw new IllegalArgumentException("已设置过数据");
        }
        if (records.size() != pageSize && records.size() != total % pageSize) {
            throw new IllegalArgumentException("无效的数据条数");
        }
        this.ready = true;
        this.records.clear();
        this.records.addAll(records);
    }

    public boolean isStatsTotal() {
        return statsTotal;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
