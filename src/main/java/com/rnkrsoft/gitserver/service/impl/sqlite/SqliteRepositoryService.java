package com.rnkrsoft.gitserver.service.impl.sqlite;

import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.entity.RepositoryEntity;
import com.rnkrsoft.gitserver.exception.RepositoryCreateFailureException;
import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;
import com.rnkrsoft.gitserver.service.domain.QueryRepositoryRequest;
import com.rnkrsoft.gitserver.service.domain.QueryRepositoryResponse;
import com.rnkrsoft.log.Logger;
import com.rnkrsoft.log.LoggerFactory;
import com.rnkrsoft.orm.Pagination;
import com.rnkrsoft.gitserver.service.RepositoryService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SqliteRepositoryService implements RepositoryService {
    GitServer gitServer;
    Logger logger = LoggerFactory.getInstance();

    @Override
    public Git openRepository(String name) throws RepositoryNotFoundException, UninitializedGitServerException {
        if (gitServer.getState() != GitServer.INIT) {
            throw new UninitializedGitServerException("Git Server is uninitialized！");
        }
        Git git = null;
        try {
            git = Git.open(getRepositoryPath(name + Constants.DOT_GIT_EXT));
        } catch (IOException e) {
            throw new RepositoryNotFoundException("Error while opening the repository.", e);
        }
        return git;
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

    public void renameRepository(String oldName, String newName) throws RepositoryNotFoundException, UninitializedGitServerException {
        if (gitServer.getState() != GitServer.INIT) {
            throw new UninitializedGitServerException("Git Server is uninitialized！");
        }
        if (oldName == null
                || newName == null || "".equals(oldName.trim())
                || "".equals(newName.trim()) || oldName.equals(newName)) {
            return;
        }

        File repoPath = new File(gitServer.getRepositoriesHome() + oldName + Constants.DOT_GIT_EXT);
        if (!repoPath.exists()) {
            throw new RepositoryNotFoundException("Error while renaming repository - repository does not exists.");
        }

        boolean renameResult = repoPath.renameTo(new File(gitServer.getRepositoriesHome() + newName + Constants.DOT_GIT_EXT));
        if (!renameResult) {
            throw new RepositoryNotFoundException("Renaming repository failed.");
        }
    }

    public void deleteRepository(String name) throws RepositoryNotFoundException, UninitializedGitServerException {
        if (gitServer.getState() != GitServer.INIT) {
            throw new UninitializedGitServerException("Git Server is uninitialized！");
        }
        if (name == null || "".equals(name.trim())) {
            return;
        }

        File repoPath = new File(gitServer.getRepositoriesHome() + name + Constants.DOT_GIT_EXT);
        if (!repoPath.exists()) {
            throw new RepositoryNotFoundException("Error while deleting repository - repository does not exists.");
        }

        if (!deleteDir(repoPath)) {
            logger.info("Failed deleting repository: " + repoPath.getAbsolutePath());
        }
    }

    @Override
    public List<String> listRepositoriesName() {
        return null;
    }

    @Override
    public QueryRepositoryResponse queryRepository(QueryRepositoryRequest request) {
        return null;
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
