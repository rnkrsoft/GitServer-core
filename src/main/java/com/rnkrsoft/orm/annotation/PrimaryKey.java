package com.rnkrsoft.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by woate on 2017/1/4.
 * 标注该注解的字段是主键，支持多个主键，但是数据访问对象只支持一个物理主键
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
    /**
     * 主键生成规则
     *
     * @return 主键生成规则
     * @since 1.0.0
     */
    PrimaryKeyStrategy strategy() default PrimaryKeyStrategy.AUTO;

    /**
     * 用于指定主键生成的特征
     * <ol>
     * <li>${yyyyMMdd}</li>
     * <li>${yyyyMMddHH}</li>
     * <li>${yyyyMMddHHmm}</li>
     * <li>${yyyyMMddHHmmss}</li>
     * <li>${yyyyMMddHHmmssSSS}</li>
     * <li>固定字符串</li>
     * <li>复杂表达式 ${yyyyMMddHHmmssSSS}_${SEQ:5}_${RANDOM:8}</li>
     * </ol>
     *
     * @return 主键特征
     * @see PrimaryKeyFeatureConstant
     * @since 1.0.0
     */
    String feature() default "";
}
