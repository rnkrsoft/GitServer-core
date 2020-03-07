package com.rnkrsoft.gitserver.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueryRepositoryRequest implements Serializable {
    /**
     * 仓库名字
     */
    String repositoryName;
    /**
     * 仓库名字是否模糊查询
     */
    boolean repositoryNameLike;
    /**
     * 创建日期
     */
    String createDate;
    /**
     * 最后更新日期
     */
    String lastUpdateDate;
    /**
     * 拥有者
     */
    String owner;
    /**
     * 分页大小
     */
    int pageSize;
    /**
     * 当前分页
     */
    int pageNo;
}
