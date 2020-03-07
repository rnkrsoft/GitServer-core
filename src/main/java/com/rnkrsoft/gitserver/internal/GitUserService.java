package com.rnkrsoft.gitserver.internal;

/**
 * Created by test on 2020/3/5.
 */
public interface GitUserService {
    /**
     * 是否有这个用户，并非鉴权，而只是检查是否注册
     *
     * @param username 用户名
     * @return 返回真时已注册
     */
    boolean hasUser(String username);

    /**
     * 检查指定用户和sha1处理过的密码是否有效
     *
     * @param username     用户名
     * @param passwordSha1 sha1算法处理过的密码
     * @return 返回真是注册过的用户
     */
    boolean hasAuthority(String username, String passwordSha1);
}
