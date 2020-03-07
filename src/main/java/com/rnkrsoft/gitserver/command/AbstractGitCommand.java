package com.rnkrsoft.gitserver.command;

import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.exception.RepositoryCreateFailureException;
import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;
import com.rnkrsoft.gitserver.internal.GitPermissionService;
import com.rnkrsoft.gitserver.internal.GitRepositoryService;
import com.rnkrsoft.gitserver.internal.GitUserService;
import com.rnkrsoft.gitserver.internal.impl.GitPermissionServiceImpl;
import com.rnkrsoft.gitserver.internal.impl.GitRepositoryServiceImpl;
import com.rnkrsoft.gitserver.internal.impl.GitUserServiceImpl;
import com.rnkrsoft.log.Logger;
import com.rnkrsoft.log.LoggerFactory;
import org.apache.sshd.server.Environment;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;

/**
 * Created by woate on 2020/02/24.
 */
public abstract class AbstractGitCommand extends BaseCommand {
    Logger logger = LoggerFactory.getInstance();
    private final static String MSG_REPOSITORY_NOT_FOUND = "Repository is not found.\r\n";
    private final static String MSG_REPOSITORY_ACCESS_PROBLEM = "Problem accessing the repository.\r\n";
    protected String name;
    protected GitServer gitServer;
    protected GitRepositoryService gitRepositoryService;
    protected GitPermissionService gitPermissionService;
    protected GitUserService gitUserService;

    protected Repository repo;

    public AbstractGitCommand(String name, GitServer gitServer) {
        this.name = name;
        this.gitServer = gitServer;
        this.gitRepositoryService = new GitRepositoryServiceImpl(gitServer);
        this.gitPermissionService = new GitPermissionServiceImpl(gitServer);
        this.gitUserService = new GitUserServiceImpl(gitServer);
    }

    public void start(final Environment env) {
        startThread(new Runnable() {
            public void run() {
                AbstractGitCommand.this.service(env);
            }
        });
    }

    private void service(final Environment env) {
        try {
            logger.info("Git Server Repositories Home is '" + gitServer.getRepositoriesHome() + "'");
            repo = this.gitRepositoryService.openRepository(getRepositoryMapping()).getRepository();
        } catch (RepositoryNotFoundException e1) {
            try {
                this.gitRepositoryService.createRepository(getRepositoryMapping(), "");
                repo = this.gitRepositoryService.openRepository(getRepositoryMapping()).getRepository();
            } catch (RepositoryCreateFailureException e) {
                logger.error("Repository not found.", e1);
                onExit(CommandConstants.CODE_ERROR, MSG_REPOSITORY_NOT_FOUND);
                return;
            } catch (RepositoryNotFoundException e) {
                logger.error("Repository not found.", e1);
                onExit(CommandConstants.CODE_ERROR, MSG_REPOSITORY_NOT_FOUND);
                return;
            } catch (UninitializedGitServerException e) {
                logger.error("GitServer is not init.", e);
                onExit(CommandConstants.CODE_ERROR, MSG_REPOSITORY_NOT_FOUND);
            }
        } catch (UninitializedGitServerException e) {
            logger.error("GitServer is not init.", e);
            onExit(CommandConstants.CODE_ERROR, MSG_REPOSITORY_NOT_FOUND);
            return;
        }

        try {
            run();

            out.flush();
            err.flush();
        } catch (IOException e) {
            logger.error("I/O repository problem.", e);
            onExit(CommandConstants.CODE_ERROR, MSG_REPOSITORY_ACCESS_PROBLEM);
            return;
        } finally {
            repo.close();
            try {
                in.close();
                out.close();
                err.close();
            } catch (IOException e) {
                logger.error("Error closing the streams.", e);
                onExit(CommandConstants.CODE_ERROR, MSG_REPOSITORY_ACCESS_PROBLEM);
                return;
            }

            onExit(CommandConstants.CODE_OK);
        }
    }

    protected String getRepositoryMapping() {
        String mapping;
        if (name.startsWith("/")) {
            mapping = name.substring(1);
        } else {
            mapping = name;
        }

        if (mapping.endsWith(Constants.DOT_GIT_EXT)) {
            mapping = mapping.substring(0, mapping.length() - Constants.DOT_GIT_EXT.length());
        }
        return mapping;
    }

    protected abstract void run() throws IOException;
}
