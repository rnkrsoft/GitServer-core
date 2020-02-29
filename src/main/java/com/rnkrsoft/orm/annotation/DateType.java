package com.rnkrsoft.orm.annotation;

/**
 * Created by rnkrsoft.com on 2017/1/4.
 * 日期类型定义
 */
public enum DateType {
    /**
     * 自动选择
     */
    AUTO,
    /**
     * 只保存日期
     */
    DATE,
    /**
     * 只保存时间
     */
    TIME,
    /**
     * 保存时间戳
     */
    TIMESTAMP,
    /**
     * 无时区年月日时分秒
     */
    DATETIME
}