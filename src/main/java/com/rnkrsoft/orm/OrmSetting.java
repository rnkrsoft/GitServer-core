package com.rnkrsoft.orm;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
/**
 * Created by woate on 2020/03/01.
 * ORM 配置类
 */
@Getter
public final class OrmSetting {
    /**
     * 实体类列表
     */
    final Set<Class> entityClasses = new HashSet<Class>();
    /**
     * 数据库驱动类
     */
    String jdbcDriverClassName;
    /**
     * 数据库连接串
     */
    String jdbcUrl;
    /**
     * 用户名
     */
    String username;
    /**
     * 密码
     */
    String password;

    private OrmSetting() {
    }

    public static OrmSettingBuilder builder() {
        return new OrmSettingBuilder();
    }

    public static class OrmSettingBuilder {
        final Set<Class> entityClasses = new HashSet<Class>();
        String jdbcDriverClassName;
        String jdbcUrl;
        String username;
        String password;

        public OrmSettingBuilder entityClass(Class... entityClasses) {
            for (Class clazz : entityClasses) {
                this.entityClasses.add(clazz);
            }
            return this;
        }

        public OrmSettingBuilder jdbcDriverClassName(String jdbcDriverClassName) {
            this.jdbcDriverClassName = jdbcDriverClassName;
            return this;
        }

        public OrmSettingBuilder jdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
            return this;
        }

        public OrmSettingBuilder username(String username) {
            this.username = username;
            return this;
        }

        public OrmSettingBuilder password(String password) {
            this.password = password;
            return this;
        }

        public OrmSetting build() {
            OrmSetting setting = new OrmSetting();
            setting.entityClasses.addAll(this.entityClasses);
            setting.jdbcDriverClassName = this.jdbcDriverClassName;
            setting.jdbcUrl = this.jdbcUrl;
            setting.username = this.username;
            setting.password = this.password;
            return setting;
        }
    }
}
