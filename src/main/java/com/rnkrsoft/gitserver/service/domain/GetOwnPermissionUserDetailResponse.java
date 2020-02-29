package com.rnkrsoft.gitserver.service.domain;

import com.rnkrsoft.gitserver.enums.PermissionEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个仓库拥有权限的详细情况，精确到每一个权限操作
 */
@Data
public class GetOwnPermissionUserDetailResponse {
    /**
     * 用户名
     */
    String username;
    /**
     * 与仓库无关的用户权限
     */
    final List<NoneRepositoryPermissionRecord> permissions1 = new ArrayList<NoneRepositoryPermissionRecord>();
    /**
     * 与仓库关联的用户权限列表
     */
    final List<RepositoryPermissionRecord> permissions2 = new ArrayList<RepositoryPermissionRecord>();

    @Data
    static class NoneRepositoryPermissionRecord {
        final List<PermissionEnum> operates = new ArrayList();
    }

    @Data
    static class RepositoryPermissionRecord {
        String repositoryName;
        String username;
        final List<PermissionEnum> operates = new ArrayList();
    }
}
