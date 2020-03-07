package com.rnkrsoft.gitserver.entity;

import com.rnkrsoft.litebatis.annotation.*;
import com.rnkrsoft.litebatis.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Comment("仓库信息表")
@Table(name = "tb_repository_info")
public class RepositoryEntity extends BaseEntity {
    @PrimaryKey(strategy = PrimaryKeyStrategy.IDENTITY)
    @NumberColumn(name = "repository_id", nullable = false)
    @Comment("仓库编号")
    Integer repositoryId;

    @StringColumn(name = "repository_name", nullable = false)
    @Comment("仓库名称，显示为英文")
    String repositoryName;

    @StringColumn(name = "repository_desc", nullable = true)
    @Comment("仓库描述")
    String repositoryDesc;

    @StringColumn(name = "owner", nullable = true)
    @Comment("拥有者")
    String owner;
}
