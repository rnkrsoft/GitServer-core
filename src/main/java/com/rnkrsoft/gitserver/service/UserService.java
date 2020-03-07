package com.rnkrsoft.gitserver.service;

import com.rnkrsoft.gitserver.http.AjaxRequest;
import com.rnkrsoft.gitserver.http.AjaxResponse;
import com.rnkrsoft.gitserver.service.domain.*;

/**
 * 用户服务
 * Created by woate on 2020/02/24.
 */
public interface UserService {
    /**
     * 注册用户
     * @param ajaxRequest 请求
     */
    AjaxResponse<RegisterUserResponse> registerUser(AjaxRequest<RegisterUserRequest> ajaxRequest);

    /**
     * 修改指定用户名的用户信息
     * @param ajaxRequest 请求
     */
    AjaxResponse<UpdateUserResponse> updateUser(AjaxRequest<UpdateUserRequest> ajaxRequest);

    /**
     * 删除指定的用户
     * @param ajaxRequest 请求
     */
    AjaxResponse<DeleteUserResponse> deleteUser(AjaxRequest<DeleteUserRequest> ajaxRequest);

    /**
     * 列出所有的用户列表
     * @param ajaxRequest 请求
     * @return 用户列表
     */
    AjaxResponse<ListUsersResponse> listUsers(AjaxRequest<ListUsersRequest> ajaxRequest);

    /**
     * 分页查询用户列表
     *
     * @param ajaxRequest 请求
     * @return 应答
     */
    AjaxResponse<QueryUsersResponse> queryUsers(AjaxRequest<QueryUsersRequest> ajaxRequest);

}
