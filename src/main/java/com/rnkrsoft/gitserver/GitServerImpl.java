package com.rnkrsoft.gitserver;

import com.rnkrsoft.gitserver.command.GitCommandFactory;
import com.rnkrsoft.gitserver.command.SendMessageCommand;
import com.rnkrsoft.gitserver.entity.PermissionEntity;
import com.rnkrsoft.gitserver.entity.RoleEntity;
import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.gitserver.exception.RepositoryCreateFailureException;
import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;
import com.rnkrsoft.gitserver.http.mgr.FileServer;
import com.rnkrsoft.gitserver.log.Logger;
import com.rnkrsoft.gitserver.log.LoggerFactory;
import com.rnkrsoft.gitserver.service.impl.FilePermissionService;
import com.rnkrsoft.gitserver.service.PermissionService;
import com.rnkrsoft.gitserver.service.UserService;
import com.rnkrsoft.gitserver.service.impl.FileUserService;

import com.rnkrsoft.gitserver.utils.PasswordUtils;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.List;

class GitServerImpl implements GitServer {
    boolean init = false;
    GitServerSetting setting;
    Logger logger = LoggerFactory.getInstance();
    UserService userService;
    PermissionService permissionService;
    GitServerImpl() {
        this.userService = new FileUserService(this);
        this.permissionService = new FilePermissionService(this);
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
        this.init = true;
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
        if (!this.init) {
            throw new UninitializedGitServerException("Git Server is uninitialized！");
        }
        SshServer sshServer = SshServer.setUpDefaultServer();
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
                if (hasUser(username)){//如果已经注册的用户，则验证是否能通过鉴权
                    return hasAuthority(username, PasswordUtils.generateSha1(password));
                }else{//如果不存在的用户，则自动注册
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
        FileServer fileServer = new FileServer(getHttpPort(), this.setting.getFileLoader());
        try {
            fileServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Git openRepository(String name) throws RepositoryNotFoundException {
        Git git = null;
        try {
            git = Git.open(getRepositoryPath(name + Constants.DOT_GIT_EXT));
        } catch (IOException e) {
            throw new RepositoryNotFoundException("Error while opening the repository.", e);
        }
        return git;
    }

    @Override
    public Git createRepository(String name) throws RepositoryCreateFailureException {
        try {
            InitCommand initCommand = Git.init();
            initCommand.setBare(true);
            initCommand.setDirectory(getRepositoryPath(name + Constants.DOT_GIT_EXT));
            Git git = initCommand.call();
            return git;
        } catch (GitAPIException e) {
            throw new RepositoryCreateFailureException("Error while creating repository.", e);
        }
    }

    public void renameRepository(String oldName, String newName) throws RepositoryNotFoundException {
        if (oldName == null
                || newName == null || "".equals(oldName.trim())
                || "".equals(newName.trim()) || oldName.equals(newName)) {
            return;
        }

        File repoPath = new File(getRepositoriesHome() + oldName + Constants.DOT_GIT_EXT);
        if (!repoPath.exists()) {
            throw new RepositoryNotFoundException("Error while renaming repository - repository does not exists.");
        }

        boolean renameResult = repoPath.renameTo(new File(getRepositoriesHome() + newName + Constants.DOT_GIT_EXT));
        if (!renameResult) {
            throw new RepositoryNotFoundException("Renaming repository failed.");
        }
    }

    public void deleteRepository(String name) throws RepositoryNotFoundException {
        if (name == null || "".equals(name.trim())) {
            return;
        }

        File repoPath = new File(getRepositoriesHome() + name + Constants.DOT_GIT_EXT);
        if (!repoPath.exists()) {
            throw new RepositoryNotFoundException("Error while deleting repository - repository does not exists.");
        }

        if (!deleteDir(repoPath)) {
            logger.info("Failed deleting repository: " + repoPath.getAbsolutePath());
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
    public void registerUser(String username, String email, String passwordSha1) {
        this.userService.registerUser(username, email, passwordSha1);
    }

    @Override
    public void updateUser(String username, String email, String password) {
        this.userService.updateUser(username, email, password);
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
    public void grantPermission(String repositoryName, String username , String ... operates) {
        permissionService.grantPermission(repositoryName, username, operates);
    }

    @Override
    public void revokePermission(String repositoryName, String username, String operate) {
        permissionService.revokePermission(repositoryName, username, operate);
    }

    @Override
    public List<PermissionEntity> listPermissions(String repositoryName) {
        return permissionService.listPermissions(repositoryName);
    }

    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    private File getRepositoryPath(String repoMapping) {
        File basePath = new File(getRepositoriesHome());
        if (!basePath.exists()) {
            if (!basePath.mkdirs()) {
                logger.info("Repository path already exist or there is a problem creating folders.");
            }
        }

        File repoPath = new File(basePath, repoMapping);
        logger.info("Repository full path is '" + repoPath + "'");
        return repoPath;
    }
}
