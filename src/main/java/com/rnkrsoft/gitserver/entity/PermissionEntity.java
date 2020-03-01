package com.rnkrsoft.gitserver.entity;

import com.rnkrsoft.gitserver.enums.PermissionEnum;
import com.rnkrsoft.orm.entity.BaseEntity;
import com.rnkrsoft.orm.annotation.*;
import lombok.Data;

@Data
@Comment("仓库信息表")
@Table(name = "tb_repository_info")
public class PermissionEntity extends BaseEntity {
    @PrimaryKey(strategy = PrimaryKeyStrategy.IDENTITY)
    @NumberColumn(name = "permission_id", nullable = false)
    @Comment("权限编号")
    Integer permissionId;

    @StringColumn(name = "repository_name", nullable = true)
    @Comment("仓库名称，显示为英文")
    String repositoryName;

    @StringColumn(name = "user_name", nullable = false)
    @Comment("用户名")
    String username;

    @NumberColumn(name = "operate", nullable = false, enumClass = PermissionEnum.class)
    @Comment("操作代码")
    Integer operate;
}
