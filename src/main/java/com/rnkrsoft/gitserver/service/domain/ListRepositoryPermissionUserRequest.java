package com.rnkrsoft.gitserver.service.domain;

import lombok.Data;

@Data
public class ListRepositoryPermissionUserRequest {
    /**
     * 仓库名称
     */
    String repositoryName;
}
