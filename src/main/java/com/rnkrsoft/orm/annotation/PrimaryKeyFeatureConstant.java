package com.rnkrsoft.orm.annotation;

/**
 * Created by rnkrsoft.com on 2017/1/4.
 * 主键生成特征字符串常量
 */
public interface PrimaryKeyFeatureConstant {
    /**
     * 生成一个日期为前缀，序列号为后缀的日期串
     */
    String YYYY_MM_DD_HH_MM_SS_SSS_SEQUEUE5 = "${yyyyMMddHHmmssSSS}${seqNo:5}";
}
