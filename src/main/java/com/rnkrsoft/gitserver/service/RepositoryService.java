package com.rnkrsoft.gitserver.service;

import com.rnkrsoft.gitserver.http.AjaxRequest;
import com.rnkrsoft.gitserver.http.AjaxResponse;
import com.rnkrsoft.gitserver.service.domain.*;

/**
 * Created by woate on 2020/02/24.
 * 仓库服务
 */
public interface RepositoryService {

    /**
     * 创建一个git仓库
     *
     * @param ajaxRequest 请求
     * @return 应答
     */
    AjaxResponse<CreateRepositoryResponse> createRepository(AjaxRequest<CreateRepositoryRequest> ajaxRequest);

    /**
     * 删除仓库
     * @param ajaxRequest 请求
     * @return 应答
     */
    AjaxResponse<DeleteRepositoryResponse> deleteRepository(AjaxRequest<DeleteRepositoryRequest> ajaxRequest);
    /**
     * 列出所有仓库的名字
     *
     * @return 仓库名称列表
     */
    AjaxResponse<ListRepositoriesNameResponse> listRepositoriesName(AjaxRequest<ListRepositoriesNameRequest> ajaxRequest);

    /**
     * 分页查询仓库
     *
     * @return 分页结果对象
     */
    AjaxResponse<QueryRepositoryResponse> queryRepository(AjaxRequest<QueryRepositoryRequest> ajaxRequest);
}
