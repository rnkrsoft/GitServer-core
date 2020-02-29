package com.rnkrsoft.gitserver.service.domain;

import lombok.Data;

@Data
public class UpdateUserRequest {
    /**
     * username 用户名
     */
    String username;
    /**
     * email 邮箱地址
     */
    String email;
    /**
     *  password 用户输入的密码
     */
    String password;
    /**
     * valid 是否有效 如果为null，则不更新是否有效
     */
    Boolean valid;
}
