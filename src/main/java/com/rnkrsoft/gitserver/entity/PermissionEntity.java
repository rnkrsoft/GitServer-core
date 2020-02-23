package com.rnkrsoft.gitserver.entity;

import java.util.ArrayList;
import java.util.List;

public class PermissionEntity {
    String repositoryName;
    String username;
    final List<String> operates = new ArrayList();

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getOperates() {
        return operates;
    }
}
