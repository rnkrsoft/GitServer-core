package com.rnkrsoft.gitserver.entity;

import com.rnkrsoft.orm.entity.BaseEntity;
import com.rnkrsoft.orm.annotation.*;
import lombok.Data;

@Data
@Comment("仓库信息表")
@Table(name = "tb_repository_info")
public class RepositoryEntity extends BaseEntity {
    @PrimaryKey(strategy = PrimaryKeyStrategy.NONE)
    @StringColumn(name = "repository_name", nullable = false)
    @Comment("仓库名称，显示为英文")
    String repositoryName;

    @StringColumn(name = "repository_desc", nullable = true)
    @Comment("仓库描述")
    String repositoryDesc;
}
