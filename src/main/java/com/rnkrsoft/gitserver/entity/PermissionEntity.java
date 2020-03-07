package com.rnkrsoft.gitserver.entity;

import com.rnkrsoft.gitserver.enums.PermissionTypeEnum;
import com.rnkrsoft.litebatis.annotation.*;
import com.rnkrsoft.litebatis.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Comment("权限信息表")
@Table(name = "tb_permission_info")
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

    @NumberColumn(name = "PERMISSION_TYPE", nullable = false, enumClass = PermissionTypeEnum.class)
    @Comment("操作代码")
    Integer permissionType;
}
