package com.rnkrsoft.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by woate on 2017/1/4.
 * 日期字段类型
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateColumn {
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
     * 默认值使用当前时间戳
     *
     * @return false
     */
    boolean currentTimestamp() default false;

    /**
     * 是否在更新记录的时候自动更新日期
     *
     * @return false
     */
    boolean onUpdate() default false;

    /**
     * 默认值
     *
     * @return 默认值
     * @since 1.0.0
     */
    String defaultValue() default "";

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
     * 字段数据类型
     *
     * @return 数据类型
     * @since 1.0.0
     */
    DateType type() default DateType.AUTO;
}
