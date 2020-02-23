package com.rnkrsoft.gitserver;

import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;

import com.rnkrsoft.gitserver.service.PermissionService;
import com.rnkrsoft.gitserver.service.RepositoryService;
import com.rnkrsoft.gitserver.service.RoleService;
import com.rnkrsoft.gitserver.service.UserService;

/**
 * Git服务
 */
public interface GitServer extends UserService, PermissionService, RoleService, RepositoryService {
    /**
     * 获取仓库根目录
     * @return 仓库根目录
     */
    String getRepositoriesHome();

    /**
     * SSH监听端口， 用于GIT客户端进行通信
     * @return SSH监听端口
     */
    int getSshPort();

    /**
     * HTTP监听端口，用于给浏览器提供管理界面
     * @return HTTP监听端口
     */
    int getHttpPort();
    /**
     * 初始化GitServer，将进行一些初始化工作，例如建立仓库根目录
     * @param setting 配置对象
     * @return Git服务本身
     */
    GitServer init(GitServerSetting setting);
    /**
     * 启动Git服务
     */
    GitServer startup() throws UninitializedGitServerException;
}
