package com.rnkrsoft.gitserver.service.impl.sqlite;

import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.entity.RepositoryEntity;
import com.rnkrsoft.gitserver.exception.RepositoryCreateFailureException;
import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;
import com.rnkrsoft.gitserver.http.AjaxRequest;
import com.rnkrsoft.gitserver.http.AjaxResponse;
import com.rnkrsoft.gitserver.service.RepositoryService;
import com.rnkrsoft.gitserver.service.domain.CreateRepositoryRequest;
import com.rnkrsoft.gitserver.service.domain.CreateRepositoryResponse;
import com.rnkrsoft.gitserver.service.domain.QueryRepositoryRequest;
import com.rnkrsoft.gitserver.service.domain.QueryRepositoryResponse;
import com.rnkrsoft.log.Logger;
import com.rnkrsoft.log.LoggerFactory;
import com.rnkrsoft.orm.Orm;
import com.rnkrsoft.orm.dao.DataAccessObject;
import com.rnkrsoft.orm.entity.Pagination;
import com.rnkrsoft.orm.session.Session;
import com.rnkrsoft.time.DateStyle;
import com.rnkrsoft.util.DateUtils;
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
    public AjaxResponse<CreateRepositoryResponse> createRepository(AjaxRequest<CreateRepositoryRequest> ajaxRequest) {
        Session session = Orm.session();
        DataAccessObject<RepositoryEntity> repositoryDao = session.dao(RepositoryEntity.class);
        CreateRepositoryRequest request = ajaxRequest.getData();
        if (repositoryDao.countSelective(RepositoryEntity.builder().repositoryName(request.getRepositoryName()).build()) > 0){
            return new AjaxResponse("0001","仓库已存在");
        }
        int cnt = repositoryDao.insert(RepositoryEntity.builder().repositoryName(request.getRepositoryName()).owner(request.getOwner()).repositoryDesc(request.getOwner()).build());
        if (cnt != 1){
            return new AjaxResponse("0001","创建仓库失败");
        }
        try {
            createRepository(request.getRepositoryName(), request.getOwner());
        } catch (RepositoryCreateFailureException e) {
            return new AjaxResponse("0001","创建仓库失败");
        } catch (UninitializedGitServerException e) {
            return new AjaxResponse("0001","创建仓库失败");
        }
        return AjaxResponse.SUCCESS;
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
    public AjaxResponse<QueryRepositoryResponse> queryRepository(AjaxRequest<QueryRepositoryRequest> ajaxRequest) {
        Session session = Orm.session();
        DataAccessObject<RepositoryEntity> repositoryDao = session.dao(RepositoryEntity.class);
        QueryRepositoryRequest request = ajaxRequest.getData();
        Pagination<RepositoryEntity> pagination = repositoryDao.querySelective(new Pagination<RepositoryEntity>(request.getPageSize(), request.getPageNo(), RepositoryEntity.builder().repositoryName(request.getRepositoryName()).owner(request.getOwner()).build()));
        QueryRepositoryResponse response = new QueryRepositoryResponse();
        response.setTotal(pagination.getTotal());
        response.setPageNo(pagination.getCurPageNo());
        for (RepositoryEntity entity : pagination.getRecords()){
            response.addRecord(new QueryRepositoryResponse.Record(entity.getRepositoryName(),
                    entity.getRepositoryDesc(),
                    DateUtils.toString(entity.getCreateDate(), DateStyle.SECOND_FORMAT1),
                    DateUtils.toString(entity.getLastUpdateDate(), DateStyle.SECOND_FORMAT1))
            );
        }
        return new AjaxResponse<QueryRepositoryResponse>(response);
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
