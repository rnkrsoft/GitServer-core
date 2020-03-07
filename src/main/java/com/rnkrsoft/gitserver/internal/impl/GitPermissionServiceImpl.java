package com.rnkrsoft.gitserver.internal.impl;

import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.entity.PermissionEntity;
import com.rnkrsoft.gitserver.enums.PermissionTypeEnum;
import com.rnkrsoft.gitserver.internal.GitPermissionService;
import com.rnkrsoft.litebatis.LiteBatis;
import com.rnkrsoft.litebatis.dao.DataAccessObject;
import com.rnkrsoft.litebatis.session.Session;
import com.rnkrsoft.log.Logger;
import com.rnkrsoft.log.LoggerFactory;

/**
 * Created by test on 2020/3/5.
 */
public class GitPermissionServiceImpl implements GitPermissionService {
    GitServer gitServer;
    Logger logger = LoggerFactory.getInstance();
    public GitPermissionServiceImpl(GitServer gitServer) {
        this.gitServer = gitServer;
    }
    @Override
    public boolean hasPermission(String repositoryName, String username, PermissionTypeEnum permissionType) {
        if (username == null || username.isEmpty()){
            throw new NullPointerException("username is null");
        }
        if (permissionType == null){
            throw new NullPointerException("permissionType is null");
        }
        Session session = LiteBatis.session();
        DataAccessObject<PermissionEntity, Integer> permissionDao = session.dao(PermissionEntity.class);
        int permissionCount = permissionDao.countSelective(PermissionEntity.builder()
                .repositoryName(repositoryName)
                .username(username)
                .permissionType(permissionType.getCode()).build());
        return permissionCount > 0;
    }

    @Override
    public void grantPermission(String repositoryName, String username, PermissionTypeEnum... permissionTypes) {
        if (repositoryName == null || repositoryName.isEmpty()){
            throw new NullPointerException("repositoryName is null");
        }
        if (username == null || username.isEmpty()){
            throw new NullPointerException("username is null");
        }
        if (permissionTypes.length == 0){
            throw new NullPointerException("permissionType is null");
        }
        Session session = LiteBatis.session();
        DataAccessObject<PermissionEntity, Integer> permissionDao = session.dao(PermissionEntity.class);
    }

    @Override
    public void revokePermission(String permissionId) {

    }
}
