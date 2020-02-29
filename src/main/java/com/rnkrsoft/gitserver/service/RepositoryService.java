package com.rnkrsoft.gitserver.service;

import com.rnkrsoft.gitserver.entity.RepositoryEntity;
import com.rnkrsoft.gitserver.exception.RepositoryCreateFailureException;
import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;
import com.rnkrsoft.gitserver.service.domain.QueryRepositoryRequest;
import com.rnkrsoft.gitserver.service.domain.QueryRepositoryResponse;
import com.rnkrsoft.orm.Pagination;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.util.List;
/**
 * 仓库服务
 * Created by woate on 2020/02/24.
 */
public interface RepositoryService {

    /**
     * 创建一个git仓库
     *
     * @param repositoryName 仓库名称
     * @param owner 拥有者
     * @throws RepositoryCreateFailureException 创建失败时抛出异常
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

    /**
     * 列出所有仓库的名字
     * @return 仓库名称列表
     */
    List<String> listRepositoriesName();
    /**
     * 分页查询仓库
     * @return 分页结果对象
     */
    QueryRepositoryResponse queryRepository(QueryRepositoryRequest request);
}
