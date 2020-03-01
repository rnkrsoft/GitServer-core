package com.rnkrsoft.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rnkrsoft.com on 2018/1/4.
 * 该注解标注在实体类上，用于声明对应表名等信息
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /**
     * 表名
     * @return 表名, 当不设置时使用类名作为表名
     * @since 1.0.0
     */
    String name() default "";

    /**
     * 实体所属模式
     * @return 模式
     * @since 1.0.0
     */
    String schema() default "";

    /**
     * 表前缀
     * @return 表前缀
     * @since 1.0.0
     */
    String prefix() default "";

    /**
     * 表后缀
     * @return 表后缀
     * @since 1.0.0
     */
    String suffix() default "";
    /**
     * 关键字的单词模式
     * 例如select drop delete update where 等
     * @return 单词模式
     * @since 1.0.0
     */
    WordMode keywordMode() default WordMode.lowerCase;

    /**
     * SQL语句使用的单词模式
     * 例如 select col1 from table1, col1和table1就是SQL语句
     * @return 单词模式
     * @since 1.0.0
     */
    WordMode sqlMode() default WordMode.upperCase;

    /**
     * 数据库类型
     * @return 数据库类型
     */
    DatabaseType type() default DatabaseType.MySQL;
}
