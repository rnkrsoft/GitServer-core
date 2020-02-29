package com.rnkrsoft.gitserver.service.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryUsersRequest {
    String userName;
    boolean userNameLike;
    int pageSize;
    int pageNo;
}
