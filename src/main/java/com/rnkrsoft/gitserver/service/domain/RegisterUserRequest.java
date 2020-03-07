package com.rnkrsoft.gitserver.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by test on 2020/3/5.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserRequest {
    /**
     * 用户名
     */
    String username;
    /**
     * 邮箱地址
     */
    String email;
    /**
     * sha1算法处理过的密码
     */
    String passwordSha1;
}
