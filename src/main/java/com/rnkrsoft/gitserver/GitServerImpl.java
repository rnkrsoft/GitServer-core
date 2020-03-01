package com.rnkrsoft.gitserver;

import com.rnkrsoft.gitserver.command.GitCommandFactory;
import com.rnkrsoft.gitserver.command.SendMessageCommand;
import com.rnkrsoft.gitserver.entity.RoleEntity;
import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.gitserver.enums.PermissionEnum;
import com.rnkrsoft.gitserver.exception.RepositoryCreateFailureException;
import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;
import com.rnkrsoft.gitserver.http.HttpServer;
import com.rnkrsoft.gitserver.service.PermissionService;
import com.rnkrsoft.gitserver.service.UserService;
import com.rnkrsoft.gitserver.service.domain.*;
import com.rnkrsoft.log.Logger;
import com.rnkrsoft.log.LoggerFactory;
import com.rnkrsoft.orm.entity.Pagination;
import com.rnkrsoft.util.PasswordUtils;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by woate on 2020/02/24.
 */
class GitServerImpl implements GitServer {


    final AtomicInteger state = new AtomicInteger(STOP);

    GitServerSetting setting;
    Logger logger = LoggerFactory.getInstance();
    SshServer sshServer;
    HttpServer httpServer;
    UserService userService;
    PermissionService permissionService;

    GitServerImpl() {

    }

    @Override
    public String getRepositoriesHome() {
        return setting.getRepositoriesHome();
    }

    @Override
    public int getSshPort() {
        return setting.getSshPort();
    }

    @Override
    public int getHttpPort() {
        return setting.getHttpPort();
    }

    public GitServer init(GitServerSetting setting) {
        File basePath = new File(setting.getRepositoriesHome());
        if (!basePath.exists()) {
            if (!basePath.mkdirs()) {
                logger.info("Create Repository happens error!");
            }
        }
        this.setting = setting;
        this.state.compareAndSet(STOP, INIT);
        return this;
    }

    public GitServer startup() throws UninitializedGitServerException {
        class NoShell implements Factory<Command> {

            public NoShell() {
            }

            public Command create() {
                return new SendMessageCommand();
            }

        }
        if (this.state.get() != INIT) {
            throw new UninitializedGitServerException("Git Server is uninitialized！");
        }
        synchronized (this) {
            sshServer = SshServer.setUpDefaultServer();
            sshServer.setPort(this.setting.getSshPort());
            SimpleGeneratorHostKeyProvider hostKeyProvider = new SimpleGeneratorHostKeyProvider("hostkey.ser", "RSA", 2048);
            sshServer.setKeyPairProvider(hostKeyProvider);
            sshServer.setShellFactory(new NoShell());
            sshServer.setCommandFactory(new GitCommandFactory(this));
            sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {

                @Override
                public boolean authenticate(String username, String password, ServerSession session) {
                    if (password == null || "".equals(password.trim())) {
                        return false;
                    }
                    if (hasUser(username)) {//如果已经注册的用户，则验证是否能通过鉴权
                        return hasAuthority(username, PasswordUtils.generateSha1(password));
                    } else {//如果不存在的用户，则自动注册
                        registerUser(username, username, PasswordUtils.generateSha1(password));
                        return true;
                    }
                }
            });
            sshServer.setPublickeyAuthenticator(new PublickeyAuthenticator() {

                @Override
                public boolean authenticate(String username, PublicKey key, ServerSession session) {
                    return true;
                }
            });
            try {
                sshServer.start();
                logger.info("GitServer has already successful startup!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            httpServer = new HttpServer(getHttpPort(), this.setting.getFileLoader());
            try {
                httpServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.state.compareAndSet(1, 2);
        return this;
    }

    @Override
    public int getState() {
        return this.state.get();
    }

    @Override
    public GitServer updateState(int expected, int newState) {
        this.state.compareAndSet(expected, newState);
        return this;
    }

    @Override
    public GitServer await() throws InterruptedException {
        while (this.state.get() == RUNNING) {
            Thread.sleep(1000);
        }
        return this;
    }

    @Override
    public void shutdown() throws InterruptedException {
        this.state.set(STOP);
        synchronized (this) {
            httpServer.stop();
            sshServer.stop();
        }
    }


    @Override
    public void addRole(String roleName, String roleDesc) {

    }

    @Override
    public void deleteRole(String roleName) {

    }

    @Override
    public void updateRole(String roleName, String roleDesc, boolean valid) {

    }

    @Override
    public List<RoleEntity> listRoles() {
        return null;
    }

    @Override
    public Pagination<RoleEntity> queryRoles(String roleName, boolean roleNameLike, int pageSize, int pageNo) {
        return null;
    }


    @Override
    public void registerUser(String username, String email, String passwordSha1) {
        this.userService.registerUser(username, email, passwordSha1);
    }

    @Override
    public void updateUser(UpdateUserRequest request) {

    }


    @Override
    public void deleteUser(String username) {
        this.userService.deleteUser(username);
    }

    @Override
    public List<UserEntity> listUsers() {
        return this.userService.listUsers();
    }

    @Override
    public QueryUsersResponse queryUsers(QueryUsersRequest request) {
        return null;
    }


    @Override
    public boolean hasUser(String username) {
        return this.userService.hasUser(username);
    }

    @Override
    public boolean hasAuthority(String username, String passwordSha1) {
        return this.userService.hasAuthority(username, passwordSha1);
    }

    @Override
    public boolean hasPermission(String repositoryName, String username, String operate) {
        return permissionService.hasPermission(repositoryName, username, operate);
    }

    @Override
    public void grantPermission(String repositoryName, String username, PermissionEnum... operates) {

    }

    @Override
    public void revokePermission(String permissionId) {

    }


    @Override
    public ListRepositoryPermissionUserResponse listRepositoryPermissionUser(ListRepositoryPermissionUserRequest request) {
        return null;
    }

    @Override
    public GetOwnPermissionUserDetailResponse getOwnPermissionUserDetail(GetOwnPermissionUserDetailRequest request) {
        return null;
    }


    @Override
    public void createRepository(String repositoryName, String owner) throws RepositoryCreateFailureException, UninitializedGitServerException {

    }

    @Override
    public Git openRepository(String repositoryName) throws RepositoryNotFoundException, UninitializedGitServerException {
        return null;
    }

    @Override
    public void renameRepository(String oldRepositoryName, String newRepositoryName) throws RepositoryNotFoundException, UninitializedGitServerException {

    }

    @Override
    public void deleteRepository(String repositoryName) throws RepositoryNotFoundException, UninitializedGitServerException {

    }

    @Override
    public List<String> listRepositoriesName() {
        return null;
    }

    @Override
    public QueryRepositoryResponse queryRepository(QueryRepositoryRequest request) {
        return null;
    }

}
