package com.rnkrsoft.gitserver.service;

import com.rnkrsoft.gitserver.exception.RepositoryCreateFailureException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

public interface RepositoryService {

    /**
     * 创建一个git仓库
     *
     * @param name 仓库名称
     * @throws RepositoryCreateFailureException 创建失败时抛出异常
     */
    Git createRepository(String name) throws RepositoryCreateFailureException;

    /**
     * 打开一个已经存在的git仓库
     *
     * @param name 仓库名称
     * @return git操作对象
     * @throws RepositoryNotFoundException 打开时如果仓库不存在则抛出异常
     */
    Git openRepository(String name) throws RepositoryNotFoundException;

    /**
     * 重命名一个存在的git仓库
     *
     * @param oldName 旧的仓库名称
     * @param newName 新的仓库名称
     * @throws RepositoryNotFoundException 如果仓库不存在则抛出异常
     */
    void renameRepository(String oldName, String newName) throws RepositoryNotFoundException;

    /**
     * 删除一个存在的git仓库
     *
     * @param name 仓库名称
     * @throws RepositoryNotFoundException 如果仓库不存在则抛出异常
     */
    void deleteRepository(String name) throws RepositoryNotFoundException;
}
