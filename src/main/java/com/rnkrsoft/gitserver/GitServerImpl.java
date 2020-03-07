package com.rnkrsoft.gitserver;

import com.rnkrsoft.gitserver.command.GitCommandFactory;
import com.rnkrsoft.gitserver.command.SendMessageCommand;
import com.rnkrsoft.gitserver.entity.PermissionEntity;
import com.rnkrsoft.gitserver.entity.RepositoryEntity;
import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;
import com.rnkrsoft.gitserver.http.HttpServer;
import com.rnkrsoft.gitserver.internal.GitRepositoryService;
import com.rnkrsoft.gitserver.internal.GitUserService;
import com.rnkrsoft.gitserver.internal.impl.GitRepositoryServiceImpl;
import com.rnkrsoft.gitserver.internal.impl.GitUserServiceImpl;
import com.rnkrsoft.litebatis.LiteBatis;
import com.rnkrsoft.litebatis.LiteBatisSetting;
import com.rnkrsoft.litebatis.dao.DataAccessObject;
import com.rnkrsoft.log.Logger;
import com.rnkrsoft.log.LoggerFactory;
import com.rnkrsoft.util.PasswordUtils;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.sql.SQLException;
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

    final GitUserService gitUserService = new GitUserServiceImpl(this);
    final GitRepositoryService gitRepositoryService = new GitRepositoryServiceImpl(this);

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

        try {
            LiteBatis.init(LiteBatisSetting.builder()
                    .entityClass(UserEntity.class, RepositoryEntity.class, PermissionEntity.class)
                    .jdbcDriverClassName("org.sqlite.JDBC")
                    .jdbcUrl("jdbc:sqlite:./target/sample.db")
                    .build());
            DataAccessObject<UserEntity, String> userDao = LiteBatis.session().dao(UserEntity.class);
            DataAccessObject<RepositoryEntity, Integer> repositoryDao = LiteBatis.session().dao(RepositoryEntity.class);
            DataAccessObject<PermissionEntity, Integer> permissionDao = LiteBatis.session().dao(PermissionEntity.class);
            userDao.createTable();
            repositoryDao.createTable();
            permissionDao.createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                    if (gitUserService.hasUser(username)) {//如果已经注册的用户，则验证是否能通过鉴权
                        return gitUserService.hasAuthority(username, PasswordUtils.generateSha1(password));
                    } else {//如果不存在的用户，则自动注册

                        return false;
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
}
