package com.rnkrsoft.gitserver.http.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.GitServerFactory;
import com.rnkrsoft.gitserver.http.AjaxRequest;
import com.rnkrsoft.gitserver.http.AjaxResponse;
import com.rnkrsoft.gitserver.http.JSON;
import com.rnkrsoft.gitserver.http.NanoHTTPD;
import com.rnkrsoft.gitserver.service.UserService;
import com.rnkrsoft.gitserver.service.domain.QueryUsersRequest;
import com.rnkrsoft.gitserver.service.domain.QueryUsersResponse;
import com.rnkrsoft.gitserver.service.impl.sqlite.SqliteUserService;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ActionService {
    static Gson GSON = new GsonBuilder().create();
    /**
     * 查询项目列表，支持分页查询
     */
    public static final String QUERY_PROJECTS_ACTION = "QUERY_PROJECTS_ACTION";
    /**
     * 创建项目
     */
    public static final String CREATE_PROJECT_ACTION = "CREATE_PROJECT_ACTION";
    /**
     * 删除项目
     */
    public static final String DELETE_PROJECT_ACTION = "DELETE_PROJECT_ACTION";
    /**
     * 查询用户列表，支持分页查询
     */
    public static final String QUERY_USERS_ACTION = "QUERY_USERS_ACTION";
    /**
     * 创建用户
     */
    public static final String CREATE_USER_ACTION = "CREATE_USER_ACTION";
    /**
     * 修改用户信息
     */
    public static final String UPDATE_USER_ACTION = "UPDATE_USER_ACTION";
    /**
     * 删除用户
     */
    public static final String DELETE_USER_ACTION = "DELETE_USER_ACTION";
    /**
     * 重置用户密码
     */
    public static final String RESET_USER_PASSWORD_ACTION = "RESET_USER_PASSWORD_ACTION";
    /**
     * 给用户赋予权限
     */
    public static final String GRANT_USER_ACTION = "GRANT_USER_ACTION";
    /**
     * 收回给用户的权限
     */
    public static final String REVOKE_USER_ACTION = "REVOKE_USER_ACTION";


    public NanoHTTPD.Response ajax(NanoHTTPD.IHTTPSession session) {
        Map<String, String> files = new HashMap<String, String>();
        try {
            session.parseBody(files);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NanoHTTPD.ResponseException e) {
            e.printStackTrace();
        }
        String body = files.get("postData");
        Map<String, String> map = JSON.parse(body);
        String action = map.get("action");
        String data = map.get("data");

        if (action.equals(QUERY_USERS_ACTION)) {
            QueryUsersRequest request = GSON.fromJson(data, QueryUsersRequest.class);
            AjaxRequest<QueryUsersRequest> ajaxRequest = new AjaxRequest<QueryUsersRequest>(action, request);
            UserService userService = new SqliteUserService();
            AjaxResponse<QueryUsersResponse> ajaxResponse = userService.queryUsers(ajaxRequest);
            return NanoHTTPD.Response.newFixedLengthResponse(ajaxResponse.toString());
        }
        return NanoHTTPD.Response.newFixedLengthResponse("login success!");
    }

    public NanoHTTPD.Response login(String usename, String password) {
        return NanoHTTPD.Response.newFixedLengthResponse("login success!");
    }

    public NanoHTTPD.Response logout(NanoHTTPD.IHTTPSession session) {
        String token = session.getCookies().read("token");
        //fixme 解出username 并清空token
        return NanoHTTPD.Response.newFixedLengthResponse("login success!");
    }


}
