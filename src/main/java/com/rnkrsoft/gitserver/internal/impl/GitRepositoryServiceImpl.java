package com.rnkrsoft.gitserver.internal.impl;

import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.exception.RepositoryCreateFailureException;
import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;
import com.rnkrsoft.gitserver.internal.GitRepositoryService;
import com.rnkrsoft.log.Logger;
import com.rnkrsoft.log.LoggerFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;

import java.io.File;
import java.io.IOException;

/**
 * Created by test on 2020/3/5.
 */
public class GitRepositoryServiceImpl implements GitRepositoryService {
    GitServer gitServer;
    Logger logger = LoggerFactory.getInstance();

    public GitRepositoryServiceImpl(GitServer gitServer) {
        this.gitServer = gitServer;
    }

    @Override
    public void createRepository(String repositoryName, String owner) throws RepositoryCreateFailureException, UninitializedGitServerException {
        if (gitServer.getState() != GitServer.INIT) {
            throw new UninitializedGitServerException("Git Server is uninitialized！");
        }
        try {
            InitCommand initCommand = Git.init();
            initCommand.setBare(true);
            initCommand.setDirectory(getRepositoryPath(repositoryName + Constants.DOT_GIT_EXT));
            initCommand.call();
        } catch (GitAPIException e) {
            throw new RepositoryCreateFailureException("Error while creating repository.", e);
        }
    }

    @Override
    public Git openRepository(String repositoryName) throws RepositoryNotFoundException, UninitializedGitServerException {
        if (gitServer.getState() != GitServer.INIT) {
            throw new UninitializedGitServerException("Git Server is uninitialized！");
        }
        Git git = null;
        try {
            git = Git.open(getRepositoryPath(repositoryName + Constants.DOT_GIT_EXT));
        } catch (IOException e) {
            throw new RepositoryNotFoundException("Error while opening the repository.", e);
        }
        return git;
    }

    @Override
    public void renameRepository(String oldRepositoryName, String newRepositoryName) throws RepositoryNotFoundException, UninitializedGitServerException {
        if (gitServer.getState() != GitServer.INIT) {
            throw new UninitializedGitServerException("Git Server is uninitialized！");
        }
        if (oldRepositoryName == null
                || newRepositoryName == null
                || "".equals(oldRepositoryName.trim())
                || "".equals(newRepositoryName.trim())
                || oldRepositoryName.equals(newRepositoryName)) {
            return;
        }

        File repoPath = new File(gitServer.getRepositoriesHome() + oldRepositoryName + Constants.DOT_GIT_EXT);
        if (!repoPath.exists()) {
            throw new RepositoryNotFoundException("Error while renaming repository - repository does not exists.");
        }

        boolean renameResult = repoPath.renameTo(new File(gitServer.getRepositoriesHome() + newRepositoryName + Constants.DOT_GIT_EXT));
        if (!renameResult) {
            throw new RepositoryNotFoundException("Renaming repository failed.");
        }
    }

    @Override
    public void deleteRepository(String repositoryName) throws RepositoryNotFoundException, UninitializedGitServerException {
        if (gitServer.getState() != GitServer.INIT) {
            throw new UninitializedGitServerException("Git Server is uninitialized！");
        }
        if (repositoryName == null || "".equals(repositoryName.trim())) {
            return;
        }

        File repoPath = new File(gitServer.getRepositoriesHome() + repositoryName + Constants.DOT_GIT_EXT);
        if (!repoPath.exists()) {
            throw new RepositoryNotFoundException("Error while deleting repository - repository does not exists.");
        }

        if (!deleteDir(repoPath)) {
            logger.info("Failed deleting repository: " + repoPath.getAbsolutePath());
        }
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
        File basePath = new File(gitServer.getRepositoriesHome());
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
