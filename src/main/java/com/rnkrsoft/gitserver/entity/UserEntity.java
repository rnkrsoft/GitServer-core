package com.rnkrsoft.gitserver.entity;

import com.rnkrsoft.orm.entity.BaseEntity;
import com.rnkrsoft.orm.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Comment("用户信息表")
@Table(name = "tb_user_info")
public class UserEntity extends BaseEntity {
    @PrimaryKey(strategy = PrimaryKeyStrategy.NONE)
    @StringColumn(name = "username", nullable = false)
    @Comment("用户名")
    String username;

    @StringColumn(name = "email", nullable = false)
    @Comment("电子邮箱")
    String email;

    @StringColumn(name = "password", nullable = false)
    @Comment("密码")
    String password;

    @NumberColumn(name = "valid", defaultValue = "1", nullable = false)
    @Comment("是否有效")
    boolean valid;
}
