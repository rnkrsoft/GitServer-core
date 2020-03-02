package com.rnkrsoft.orm.session;

import com.rnkrsoft.orm.dao.DataAccessObject;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 抽象数据库事务为会话
 * 会话管理的是数据库连接资源，基于线程绑定机制，也就是在同一线程中获取的会话是同一个会话，也是同一个连接。
 * 不能实现同一个线程访问多个数据库连接
 */
public interface Session extends Closeable {
    /**
     * 获取链接
     * @return
     * @throws SQLException
     */
    Connection getConnection()throws SQLException;
    /**
     * 开始事务
     *
     * @return 会话对象
     */
    Session begin() throws SQLException;

    /**
     * 提交事务
     *
     * @return 会话对象
     */
    Session commit() throws SQLException;

    /**
     * 回滚事务
     *
     * @return 会话对象
     */
    Session rollback() throws SQLException;
    /**
     * 获取指定实体类的数据访问对象
     *
     * @param entityClass 实体类
     * @return 数据访问对象
     */
    <T> DataAccessObject<T>dao(Class<T> entityClass);
}
