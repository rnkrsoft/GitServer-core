package com.rnkrsoft.gitserver.internal;

import com.rnkrsoft.gitserver.exception.RepositoryCreateFailureException;
import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

/**
 * Created by woate on 2020/3/5.
 * Git仓库服务
 */
public interface GitRepositoryService {
    /**
     * 创建一个git仓库 仓库名称
     * @param repositoryName
     * @param owner
     * @throws RepositoryCreateFailureException
     * @throws UninitializedGitServerException
     */
    void createRepository(String repositoryName, String owner) throws RepositoryCreateFailureException, UninitializedGitServerException;
    /**
     * 打开一个已经存在的git仓库
     *
     * @param repositoryName 仓库名称
     * @return git操作对象
     * @throws RepositoryNotFoundException 打开时如果仓库不存在则抛出异常
     */
    Git openRepository(String repositoryName) throws RepositoryNotFoundException, UninitializedGitServerException;

    /**
     * 重命名一个存在的git仓库
     *
     * @param oldRepositoryName 旧的仓库名称
     * @param newRepositoryName 新的仓库名称
     * @throws RepositoryNotFoundException 如果仓库不存在则抛出异常
     */
    void renameRepository(String oldRepositoryName, String newRepositoryName) throws RepositoryNotFoundException, UninitializedGitServerException;

    /**
     * 删除一个存在的git仓库
     *
     * @param repositoryName 仓库名称
     * @throws RepositoryNotFoundException 如果仓库不存在则抛出异常
     */
    void deleteRepository(String repositoryName) throws RepositoryNotFoundException, UninitializedGitServerException;
}
