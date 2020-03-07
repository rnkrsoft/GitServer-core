package com.rnkrsoft.gitserver.internal.impl;

import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.gitserver.internal.GitUserService;
import com.rnkrsoft.litebatis.LiteBatis;
import com.rnkrsoft.litebatis.dao.DataAccessObject;
import com.rnkrsoft.litebatis.session.Session;
import com.rnkrsoft.log.Logger;
import com.rnkrsoft.log.LoggerFactory;

/**
 * Created by test on 2020/3/5.
 */
public class GitUserServiceImpl implements GitUserService{
    GitServer gitServer;
    Logger logger = LoggerFactory.getInstance();
    public GitUserServiceImpl(GitServer gitServer) {
        this.gitServer = gitServer;
    }
    @Override
    public boolean hasUser(String username) {
        Session session = LiteBatis.session();
        DataAccessObject<UserEntity, String> userDao = session.dao(UserEntity.class);
        return userDao.countSelective(UserEntity.builder().username(username).build()) > 0;
    }

    @Override
    public boolean hasAuthority(String username, String passwordSha1) {
        Session session = LiteBatis.session();
        DataAccessObject<UserEntity, String> userDao = session.dao(UserEntity.class);
        UserEntity userEntity = userDao.selectByPrimaryKey(username);
        if (userEntity == null) {
            return false;
        }
        return passwordSha1.equals(userEntity.getPassword());
    }
}
