package com.rnkrsoft.gitserver.service;

import com.rnkrsoft.gitserver.entity.PermissionEntity;

import java.util.List;

public interface PermissionService {
    /**
     * 检查当前用户是否拥有指定仓库对应操作是否拥有权限
     * @param repositoryName 仓库名称
     * @param username 用户名
     * @param operate 操作类型
     * @return 返回真拥有权限
     */
    boolean hasPermission(String repositoryName, String username, String operate);

    /**
     * 赋予指定仓库指定用户权限
     * @param repositoryName 仓库名称
     * @param username 用户名
     * @param operates 操作类型
     */
    void grantPermission(String repositoryName, String username, String... operates);

    /**
     * 撤销指定仓库指定用户权限
     * @param repositoryName 仓库名称
     * @param username 用户名
     * @param operate 操作类型
     */
    void revokePermission(String repositoryName, String username, String operate);

    /**
     * 列出指定仓库的所有权限
     * @param repositoryName 仓库名称
     * @return 权限列表
     */
    List<PermissionEntity> listPermissions(String repositoryName);
}
