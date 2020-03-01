package com.rnkrsoft.orm.annotation;

import com.rnkrsoft.interfaces.EnumStringCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by woate on 2017/1/4.
 * 字符串类型的数据定义
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringColumn {
    /**
     * 字段名称
     *
     * @return 字段名称
     * @since 1.0.0
     */
    String name() default "";

    /**
     * 是否允许为空
     *
     * @return 是否允许为空
     * @since 1.0.0
     */
    boolean nullable() default true;

    /**
     * 字段长度
     * 超过255的VARCHAR，在MySQL数据库自动使用TEXT
     *
     * @return 字段长度
     * @since 1.0.0
     */
    int length() default 255;

    /**
     * 默认值
     *
     * @return 默认值
     * @since 1.0.0
     */
    String defaultValue() default "";

    /**
     * 字段数据类型
     *
     * @return 数据类型
     * @since 1.0.0
     */
    StringType type() default StringType.AUTO;

    /**
     * 当前字段作为条件，如果不配置条件对象时提供的默认逻辑模式
     *
     * @return 逻辑模式
     */
    LogicMode logicMode() default LogicMode.AND;

    /**
     * 当前字段作为条件，如果不配置条件对象时提供的默认值模式
     *
     * @return 值模式
     */
    ValueMode valueMode() default ValueMode.EQ;

    /**
     * 字段枚举值
     *
     * @return
     */
    Class<? extends EnumStringCode> enumClass() default EnumStringCode.class;
}

