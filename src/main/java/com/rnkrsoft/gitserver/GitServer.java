package com.rnkrsoft.gitserver;

import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;

import com.rnkrsoft.gitserver.service.PermissionService;
import com.rnkrsoft.gitserver.service.RepositoryService;
import com.rnkrsoft.gitserver.service.RoleService;
import com.rnkrsoft.gitserver.service.UserService;

/**
 * Git服务
 * Created by woate on 2020/02/24.
 */
public interface GitServer extends UserService, PermissionService, RoleService, RepositoryService {
    int STOP = 0;
    int INIT = 1;
    int RUNNING = 2;
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

    /**
     * 检查服务器状态
     * @return
     */
    int getState();

    /**
     * 更新状态
     * @param expected
     * @param newState
     * @return
     */
    GitServer updateState(int expected, int newState);
    /**
     * 启动服务后主线程进入等待
     * @return Git服务
     * @throws InterruptedException
     */
    GitServer await() throws InterruptedException;

    /**
     * 关闭服务
     * @throws InterruptedException
     */
    void shutdown() throws InterruptedException;
}
