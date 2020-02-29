package com.rnkrsoft.gitserver.service.domain;

import lombok.Data;

@Data
public class GetOwnPermissionUserDetailRequest {
    String repositoryName;
    String userName;
}
