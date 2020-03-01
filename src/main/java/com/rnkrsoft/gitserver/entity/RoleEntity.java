package com.rnkrsoft.gitserver.entity;

import com.rnkrsoft.orm.entity.BaseEntity;
import com.rnkrsoft.orm.annotation.*;
import lombok.Data;

@Data
@Comment("角色信息表")
@Table(name = "tb_user_info")
public class RoleEntity extends BaseEntity {
    @PrimaryKey(strategy = PrimaryKeyStrategy.NONE)
    @StringColumn(name = "role_name", nullable = false)
    @Comment("角色名称，显示为英文")
    String roleName;

    @StringColumn(name = "role_desc", nullable = false)
    @Comment("角色描述，显示为中文")
    String roleDesc;

    @NumberColumn(name = "valid", defaultValue = "1", nullable = false)
    @Comment("当前角色是否失效")
    boolean valid;
}
