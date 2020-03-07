package com.rnkrsoft.gitserver.service.impl.sqlite;

import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.entity.RepositoryEntity;
import com.rnkrsoft.gitserver.http.AjaxRequest;
import com.rnkrsoft.gitserver.http.AjaxResponse;
import com.rnkrsoft.gitserver.internal.GitRepositoryService;
import com.rnkrsoft.gitserver.internal.impl.GitRepositoryServiceImpl;
import com.rnkrsoft.gitserver.service.RepositoryService;
import com.rnkrsoft.gitserver.service.domain.*;
import com.rnkrsoft.litebatis.LiteBatis;
import com.rnkrsoft.litebatis.dao.DataAccessObject;
import com.rnkrsoft.litebatis.entity.Pagination;
import com.rnkrsoft.litebatis.session.Session;
import com.rnkrsoft.litebatis.time.DateStyle;
import com.rnkrsoft.litebatis.util.DateUtils;
import com.rnkrsoft.log.Logger;
import com.rnkrsoft.log.LoggerFactory;

import java.util.List;

public class SqliteRepositoryService implements RepositoryService {
    GitServer gitServer;
    Logger logger = LoggerFactory.getInstance();
    GitRepositoryService gitRepositoryService;

    public SqliteRepositoryService(GitServer gitServer) {
        this.gitServer = gitServer;
        this.gitRepositoryService = new GitRepositoryServiceImpl(this.gitServer);
    }

    @Override
    public AjaxResponse<CreateRepositoryResponse> createRepository(AjaxRequest<CreateRepositoryRequest> ajaxRequest) {
        CreateRepositoryRequest request = ajaxRequest.getData();
        Session session = LiteBatis.session();
        DataAccessObject<RepositoryEntity, Integer> repositoryDao = session.dao(RepositoryEntity.class);
        RepositoryEntity repositoryEntity = RepositoryEntity.builder().repositoryName(request.getRepositoryName()).build();
        int existCnt = repositoryDao.countSelective(repositoryEntity);
        if (existCnt > 0) {
            return new AjaxResponse("001", "仓库已存在");
        }
        repositoryEntity.setOwner("woate");
        repositoryEntity.setRepositoryDesc(request.getRepositoryDesc());
        int cnt = repositoryDao.insertSelective(repositoryEntity);
        if (cnt < 1) {
            return AjaxResponse.FAILURE;
        }
        CreateRepositoryResponse response = new CreateRepositoryResponse();
        response.setRepositoryId(repositoryEntity.getRepositoryId());
        return new AjaxResponse<CreateRepositoryResponse>(response);
    }

    @Override
    public AjaxResponse<DeleteRepositoryResponse> deleteRepository(AjaxRequest<DeleteRepositoryRequest> ajaxRequest) {
        DeleteRepositoryRequest request = ajaxRequest.getData();
        Session session = LiteBatis.session();
        DataAccessObject<RepositoryEntity, Integer> repositoryDao = session.dao(RepositoryEntity.class);
        int deleteCnt = repositoryDao.deleteByPrimaryKey(request.getRepositoryId());
        if (deleteCnt < 1) {
            return AjaxResponse.FAILURE;
        } else {
            return AjaxResponse.SUCCESS;
        }
    }

    @Override
    public AjaxResponse<ListRepositoriesNameResponse> listRepositoriesName(AjaxRequest<ListRepositoriesNameRequest> ajaxRequest) {
        ListRepositoriesNameRequest request = ajaxRequest.getData();
        Session session = LiteBatis.session();
        DataAccessObject<RepositoryEntity, Integer> repositoryDao = session.dao(RepositoryEntity.class);
        List<RepositoryEntity> repositoryEntities = repositoryDao.selectSelective(RepositoryEntity.builder().build());
        ListRepositoriesNameResponse response = new ListRepositoriesNameResponse();
        for (RepositoryEntity repositoryEntity : repositoryEntities){
            response.getRepositoriesName().add(repositoryEntity.getRepositoryName());
        }
        return new AjaxResponse<ListRepositoriesNameResponse>(response);
    }

    @Override
    public AjaxResponse<QueryRepositoryResponse> queryRepository(AjaxRequest<QueryRepositoryRequest> ajaxRequest) {
        Session session = LiteBatis.session();
        DataAccessObject<RepositoryEntity, Integer> repositoryDao = session.dao(RepositoryEntity.class);
        QueryRepositoryRequest request = ajaxRequest.getData();
        Pagination<RepositoryEntity> pagination = repositoryDao.querySelective(new Pagination<RepositoryEntity>(request.getPageSize(), request.getPageNo(), RepositoryEntity.builder().repositoryName(request.getRepositoryName()).owner(request.getOwner()).build()));
        QueryRepositoryResponse response = new QueryRepositoryResponse();
        response.setTotal(pagination.getTotal());
        response.setPageNo(pagination.getCurPageNo());
        for (RepositoryEntity entity : pagination.getRecords()) {
            response.addRecord(new QueryRepositoryResponse.Record(entity.getRepositoryName(),
                            entity.getRepositoryDesc(),
                            DateUtils.toString(entity.getCreateDate(), DateStyle.SECOND_FORMAT1),
                            DateUtils.toString(entity.getLastUpdateDate(), DateStyle.SECOND_FORMAT1))
            );
        }
        return new AjaxResponse<QueryRepositoryResponse>(response);
    }
}
