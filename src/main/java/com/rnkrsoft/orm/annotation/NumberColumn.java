package com.rnkrsoft.orm.annotation;

import com.rnkrsoft.interfaces.EnumIntegerCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by woate on 2017/1/4.
 * 数字类型的字段定义
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberColumn {
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
     * 默认值
     *
     * @return 默认值
     * @since 1.0.0
     */
    String defaultValue() default "";

    /**
     * 整个数字,不算点号,例如Decimal(5,2)可最大存放999.99
     *
     * @return 整数部分, 不算点号
     * @since 1.0.0
     */
    int precision() default 0;

    /**
     * 小数部分
     *
     * @return 小数部分
     * @since 1.0.0
     */
    int scale() default 0;

    /**
     * 数字类型
     *
     * @return 数字类型
     * @since 1.0.0
     */
    NumberType type() default NumberType.AUTO;

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
    Class<? extends EnumIntegerCode> enumClass() default EnumIntegerCode.class;
}
