package com.rnkrsoft.gitserver.service;

import com.rnkrsoft.gitserver.entity.UserEntity;

import java.util.List;

public interface UserService {
    /**
     * 注册用户
     * @param username 用户名
     * @param email 邮箱地址
     * @param passwordSha1 sha1算法处理过的密码
     */
    void registerUser(String username, String email, String passwordSha1);

    /**
     * 修改指定用户名的用户信息
     * @param username 用户名
     * @param email 邮箱地址
     * @param password 用户输入的密码
     */
    void updateUser(String username, String email, String password);

    /**
     * 删除指定的用户
     * @param username 用户名
     */
    void deleteUser(String username);

    /**
     * 列出所有的用户列表
     * @return 用户列表
     */
    List<UserEntity> listUsers();

    /**
     * 是否有这个用户，并非鉴权，而只是检查是否注册
     * @param username 用户名
     * @return 返回真时已注册
     */
    boolean hasUser(String username);
    /**
     * 检查指定用户和sha1处理过的密码是否有效
     * @param username 用户名
     * @param passwordSha1 sha1算法处理过的密码
     * @return 返回真是注册过的用户
     */
    boolean hasAuthority(String username, String passwordSha1);
}
